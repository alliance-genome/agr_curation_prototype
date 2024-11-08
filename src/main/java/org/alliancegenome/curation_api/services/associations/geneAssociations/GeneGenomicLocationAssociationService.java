package org.alliancegenome.curation_api.services.associations.geneAssociations;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.PersonDAO;
import org.alliancegenome.curation_api.dao.associations.geneAssociations.GeneGenomicLocationAssociationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.associations.geneAssociations.GeneGenomicLocationAssociation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.PersonService;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.apache.commons.lang.StringUtils;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class GeneGenomicLocationAssociationService extends BaseEntityCrudService<GeneGenomicLocationAssociation, GeneGenomicLocationAssociationDAO> {

	@Inject GeneGenomicLocationAssociationDAO geneGenomicLocationAssociationDAO;
	@Inject PersonDAO personDAO;
	@Inject PersonService personService;
	
	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(geneGenomicLocationAssociationDAO);
	}


	public List<Long> getIdsByDataProvider(BackendBulkDataProvider dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.GENE_ASSOCIATION_SUBJECT_DATA_PROVIDER, dataProvider.sourceOrganization);
		if (StringUtils.equals(dataProvider.sourceOrganization, "RGD") || StringUtils.equals(dataProvider.sourceOrganization, "XB")) {
			params.put(EntityFieldConstants.GENE_ASSOCIATION_SUBJECT_TAXON, dataProvider.canonicalTaxonCurie);
		}
		List<Long> associationIds = geneGenomicLocationAssociationDAO.findIdsByParams(params);
		associationIds.removeIf(Objects::isNull);

		return associationIds;
	}

	@Override
	@Transactional
	public GeneGenomicLocationAssociation deprecateOrDelete(Long id, Boolean throwApiError, String loadDescription, Boolean deprecate) {
		GeneGenomicLocationAssociation association = geneGenomicLocationAssociationDAO.find(id);

		if (association == null) {
			String errorMessage = "Could not find GeneGenomicLocationAssociation with id: " + id;
			if (throwApiError) {
				ObjectResponse<GeneGenomicLocationAssociation> response = new ObjectResponse<>();
				response.addErrorMessage("id", errorMessage);
				throw new ApiErrorException(response);
			}
			log.error(errorMessage);
			return null;
		}
		if (deprecate) {
			if (!association.getObsolete()) {
				association.setObsolete(true);
				if (authenticatedPerson.getId() != null) {
					association.setUpdatedBy(personDAO.find(authenticatedPerson.getId()));
				} else {
					association.setUpdatedBy(personService.fetchByUniqueIdOrCreate(loadDescription));
				}
				association.setDateUpdated(OffsetDateTime.now());
				return geneGenomicLocationAssociationDAO.persist(association);
			}
			return association;
		}
		
		geneGenomicLocationAssociationDAO.remove(association.getId());
		
		return null;
	}

	public ObjectResponse<GeneGenomicLocationAssociation> getLocationAssociation(Long geneId, Long assemblyComponentId) {
		GeneGenomicLocationAssociation association = null;

		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.GENE_ASSOCIATION_SUBJECT + ".id", geneId);
		params.put("geneGenomicLocationAssociationObject.id", assemblyComponentId);

		SearchResponse<GeneGenomicLocationAssociation> resp = geneGenomicLocationAssociationDAO.findByParams(params);
		if (resp != null && resp.getSingleResult() != null) {
			association = resp.getSingleResult();
		}

		ObjectResponse<GeneGenomicLocationAssociation> response = new ObjectResponse<>();
		response.setEntity(association);

		return response;
	}
	
	public void addAssociationToSubject(GeneGenomicLocationAssociation association) {
		Gene gene = association.getGeneAssociationSubject();
		
		List<GeneGenomicLocationAssociation> currentSubjectAssociations = gene.getGeneGenomicLocationAssociations();
		if (currentSubjectAssociations == null) {
			currentSubjectAssociations = new ArrayList<>();
		}
		
		List<Long> currentSubjectAssociationIds = currentSubjectAssociations.stream()
				.map(GeneGenomicLocationAssociation::getId).collect(Collectors.toList());
		
		if (!currentSubjectAssociationIds.contains(association.getId())) {
			currentSubjectAssociations.add(association);
		}
	}
}
