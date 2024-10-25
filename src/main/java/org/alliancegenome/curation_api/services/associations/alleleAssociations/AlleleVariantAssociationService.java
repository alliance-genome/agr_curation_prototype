package org.alliancegenome.curation_api.services.associations.alleleAssociations;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.dao.NoteDAO;
import org.alliancegenome.curation_api.dao.PersonDAO;
import org.alliancegenome.curation_api.dao.VariantDAO;
import org.alliancegenome.curation_api.dao.associations.alleleAssociations.AlleleVariantAssociationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Variant;
import org.alliancegenome.curation_api.model.entities.associations.alleleAssociations.AlleleVariantAssociation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.PersonService;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.associations.alleleAssociations.AlleleVariantAssociationValidator;
import org.alliancegenome.curation_api.services.validation.dto.associations.alleleAssociations.AlleleVariantAssociationDTOValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class AlleleVariantAssociationService extends BaseEntityCrudService<AlleleVariantAssociation, AlleleVariantAssociationDAO> {

	@Inject AlleleVariantAssociationDAO alleleVariantAssociationDAO;
	@Inject AlleleVariantAssociationValidator alleleVariantAssociationValidator;
	@Inject AlleleVariantAssociationDTOValidator alleleVariantAssociationDtoValidator;
	@Inject AlleleDAO alleleDAO;
	@Inject VariantDAO variantDAO;
	@Inject NoteDAO noteDAO;
	@Inject GeneDAO geneDAO;
	@Inject PersonService personService;
	@Inject PersonDAO personDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(alleleVariantAssociationDAO);
	}

	@Transactional
	public ObjectResponse<AlleleVariantAssociation> upsert(AlleleVariantAssociation uiEntity) {
		AlleleVariantAssociation dbEntity = alleleVariantAssociationValidator.validateAlleleVariantAssociation(uiEntity, true, true);
		if (dbEntity == null) {
			return null;
		}
		dbEntity = alleleVariantAssociationDAO.persist(dbEntity);
		addAssociationToAllele(dbEntity);
		addAssociationToVariant(dbEntity);
		return new ObjectResponse<AlleleVariantAssociation>(dbEntity);
	}

	public ObjectResponse<AlleleVariantAssociation> validate(AlleleVariantAssociation uiEntity) {
		AlleleVariantAssociation aga = alleleVariantAssociationValidator.validateAlleleVariantAssociation(uiEntity, true, false);
		return new ObjectResponse<AlleleVariantAssociation>(aga);
	}

	public List<Long> getAssociationsByDataProvider(BackendBulkDataProvider dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.ALLELE_ASSOCIATION_SUBJECT_DATA_PROVIDER, dataProvider.sourceOrganization);
		List<Long> associationIds = alleleVariantAssociationDAO.findIdsByParams(params);
		associationIds.removeIf(Objects::isNull);

		return associationIds;
	}

	@Override
	@Transactional
	public AlleleVariantAssociation deprecateOrDelete(Long id, Boolean throwApiError, String loadDescription, Boolean deprecate) {
		AlleleVariantAssociation association = alleleVariantAssociationDAO.find(id);

		if (association == null) {
			String errorMessage = "Could not find AlleleVariantAssociation with id: " + id;
			if (throwApiError) {
				ObjectResponse<AlleleVariantAssociation> response = new ObjectResponse<>();
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
				return alleleVariantAssociationDAO.persist(association);
			}
			return association;
		}

		Long noteId = null;
		if (association.getRelatedNote() != null) {
			noteId = association.getRelatedNote().getId();
		}
		alleleVariantAssociationDAO.remove(association.getId());
		if (noteId != null) {
			noteDAO.remove(noteId);
		}

		return null;
	}

	public ObjectResponse<AlleleVariantAssociation> getAssociation(Long alleleId, String relationName, Long geneId) {
		AlleleVariantAssociation association = null;

		Map<String, Object> params = new HashMap<>();
		params.put("alleleAssociationSubject.id", alleleId);
		params.put("relation.name", relationName);
		params.put("alleleVariantAssociationObject.id", geneId);

		SearchResponse<AlleleVariantAssociation> resp = alleleVariantAssociationDAO.findByParams(params);
		if (resp != null && resp.getSingleResult() != null) {
			association = resp.getSingleResult();
		}

		ObjectResponse<AlleleVariantAssociation> response = new ObjectResponse<>();
		response.setEntity(association);

		return response;
	}

	public void addAssociationToAllele(AlleleVariantAssociation association) {
		Allele allele = association.getAlleleAssociationSubject();
		List<AlleleVariantAssociation> currentAssociations = allele.getAlleleVariantAssociations();
		if (currentAssociations == null) {
			currentAssociations = new ArrayList<>();
			allele.setAlleleVariantAssociations(currentAssociations);
		}

		List<Long> currentAssociationIds = new ArrayList<>();
		for (AlleleVariantAssociation aga : currentAssociations) {
			currentAssociationIds.add(aga.getId());
		}

		if (!currentAssociationIds.contains(association.getId())) {
			currentAssociations.add(association);
		}
	}

	public void addAssociationToVariant(AlleleVariantAssociation association) {
		Variant variant = variantDAO.find(association.getAlleleVariantAssociationObject().getId());
		List<AlleleVariantAssociation> currentAssociations = variant.getAlleleVariantAssociations();
		if (currentAssociations == null) {
			currentAssociations = new ArrayList<>();
			variant.setAlleleVariantAssociations(currentAssociations);
		}

		List<Long> currentAssociationIds = new ArrayList<>();
		for (AlleleVariantAssociation ava : currentAssociations) {
			currentAssociationIds.add(ava.getId());
		}

		if (!currentAssociationIds.contains(association.getId())) {
			currentAssociations.add(association);
		}
		
	}
}
