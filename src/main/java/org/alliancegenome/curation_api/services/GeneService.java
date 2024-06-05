package org.alliancegenome.curation_api.services;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.model.entities.associations.alleleAssociations.AlleleGeneAssociation;
import org.alliancegenome.curation_api.model.entities.associations.constructAssociations.ConstructGenomicEntityAssociation;
import org.alliancegenome.curation_api.model.entities.orthology.GeneToGeneOrthology;
import org.alliancegenome.curation_api.model.ingest.dto.GeneDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.associations.alleleAssociations.AlleleGeneAssociationService;
import org.alliancegenome.curation_api.services.associations.constructAssociations.ConstructGenomicEntityAssociationService;
import org.alliancegenome.curation_api.services.base.SubmittedObjectCrudService;
import org.alliancegenome.curation_api.services.orthology.GeneToGeneOrthologyService;
import org.alliancegenome.curation_api.services.validation.GeneValidator;
import org.alliancegenome.curation_api.services.validation.dto.GeneDTOValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class GeneService extends SubmittedObjectCrudService<Gene, GeneDTO, GeneDAO> {

	@Inject GeneDAO geneDAO;
	@Inject GeneValidator geneValidator;
	@Inject GeneDTOValidator geneDtoValidator;
	@Inject DiseaseAnnotationService diseaseAnnotationService;
	@Inject PersonService personService;
	@Inject GeneToGeneOrthologyService orthologyService;
	@Inject AlleleGeneAssociationService alleleGeneAssociationService;
	@Inject ConstructGenomicEntityAssociationService constructGenomicEntityAssociationService;
	@Inject GeneInteractionService geneInteractionService;
	@Inject PhenotypeAnnotationService phenotypeAnnotationService;
	@Inject	GeneExpressionAnnotationService geneExpressionAnnotationService;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(geneDAO);
	}

	@Override
	@Transactional
	public ObjectResponse<Gene> update(Gene uiEntity) {
		Gene dbEntity = geneValidator.validateGeneUpdate(uiEntity);
		return new ObjectResponse<Gene>(dbEntity);
	}

	@Override
	@Transactional
	public ObjectResponse<Gene> create(Gene uiEntity) {
		Gene dbEntity = geneValidator.validateGeneCreate(uiEntity);
		return new ObjectResponse<Gene>(dbEntity);
	}

	@Override
	public Gene upsert(GeneDTO dto) throws ObjectUpdateException {
		return upsert(dto, null);
	}

	@Override
	public Gene upsert(GeneDTO dto, BackendBulkDataProvider dataProvider) throws ObjectUpdateException {
		return geneDtoValidator.validateGeneDTO(dto, dataProvider);
	}

	@Override
	@Transactional
	public ObjectResponse<Gene> deleteById(Long id) {
		removeOrDeprecateNonUpdated(id, "Gene DELETE API call");
		ObjectResponse<Gene> ret = new ObjectResponse<>();
		return ret;
	}

	@Override
	@Transactional
	public void removeOrDeprecateNonUpdated(Long id, String loadDescription) {
		Gene gene = geneDAO.find(id);
		if (gene != null) {
			List<Long> referencingDAIds = geneDAO.findReferencingDiseaseAnnotations(id);
			Boolean anyReferencingEntities = false;
			for (Long daId : referencingDAIds) {
				DiseaseAnnotation referencingDA = diseaseAnnotationService.deprecateOrDeleteAnnotationAndNotes(daId, false, loadDescription, true);
				if (referencingDA != null) {
					anyReferencingEntities = true;
				}
			}

			List<Long> referencingPAIds = geneDAO.findReferencingPhenotypeAnnotations(id);
			for (Long paId : referencingPAIds) {
				PhenotypeAnnotation referencingPA = phenotypeAnnotationService.deprecateOrDeleteAnnotationAndNotes(paId, false, loadDescription, true);
				if (referencingPA != null) {
					anyReferencingEntities = true;
				}
			}

			List<Long> referencingOrthologyPairs = geneDAO.findReferencingOrthologyPairs(id);
			for (Long orthId : referencingOrthologyPairs) {
				GeneToGeneOrthology referencingOrthoPair = orthologyService.deprecateOrthologyPair(orthId, loadDescription);
				if (referencingOrthoPair != null) {
					anyReferencingEntities = true;
				}
			}
			List<Long> referencingInteractions = geneDAO.findReferencingInteractions(id);
			for (Long interactionId : referencingInteractions) {
				GeneInteraction referencingInteraction = geneInteractionService.deprecateOrDeleteInteraction(interactionId, false, loadDescription, true);
				if (referencingInteraction != null) {
					anyReferencingEntities = true;
				}
			}
			List<Long> referencingGeneExpressionAnnotations = geneDAO.findReferencingGeneExpressionAnnotations(id);
			if (referencingGeneExpressionAnnotations != null) {
				if (referencingGeneExpressionAnnotations.size() > 0) {
					anyReferencingEntities = true;
				}
			}
			if (CollectionUtils.isNotEmpty(gene.getAlleleGeneAssociations())) {
				for (AlleleGeneAssociation association : gene.getAlleleGeneAssociations()) {
					association = alleleGeneAssociationService.deprecateOrDeleteAssociation(association.getId(), false, loadDescription, true);
					if (association != null) {
						anyReferencingEntities = true;
					}
				}
			}
			if (CollectionUtils.isNotEmpty(gene.getConstructGenomicEntityAssociations())) {
				for (ConstructGenomicEntityAssociation association : gene.getConstructGenomicEntityAssociations()) {
					association = constructGenomicEntityAssociationService.deprecateOrDeleteAssociation(association.getId(), false, loadDescription, true);
					if (association != null) {
						anyReferencingEntities = true;
					}
				}
			}

			if (anyReferencingEntities) {
				gene.setUpdatedBy(personService.fetchByUniqueIdOrCreate(loadDescription));
				gene.setDateUpdated(OffsetDateTime.now());
				gene.setObsolete(true);
				geneDAO.persist(gene);
			} else {
				geneDAO.remove(id);
			}
		} else {
			log.error("Failed getting gene: " + id);
		}
	}

	public List<Long> getIdsByDataProvider(BackendBulkDataProvider dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.DATA_PROVIDER, dataProvider.sourceOrganization);
		if (StringUtils.equals(dataProvider.sourceOrganization, "RGD")) {
			params.put(EntityFieldConstants.TAXON, dataProvider.canonicalTaxonCurie);
		}
		List<Long> ids = geneDAO.findIdsByParams(params);
		ids.removeIf(Objects::isNull);

		return ids;
	}

}
