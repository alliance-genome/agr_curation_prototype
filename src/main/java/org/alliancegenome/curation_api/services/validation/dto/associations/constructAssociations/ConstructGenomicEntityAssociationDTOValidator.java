package org.alliancegenome.curation_api.services.validation.dto.associations.constructAssociations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.associations.constructAssociations.ConstructGenomicEntityAssociationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.Construct;
import org.alliancegenome.curation_api.model.entities.GenomicEntity;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.associations.constructAssociations.ConstructGenomicEntityAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.associations.constructAssociations.ConstructGenomicEntityAssociationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.ConstructService;
import org.alliancegenome.curation_api.services.GenomicEntityService;
import org.alliancegenome.curation_api.services.validation.dto.associations.EvidenceAssociationDTOValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class ConstructGenomicEntityAssociationDTOValidator extends EvidenceAssociationDTOValidator<ConstructGenomicEntityAssociation, ConstructGenomicEntityAssociationDTO> {

	@Inject ConstructService constructService;
	@Inject GenomicEntityService genomicEntityService;
	@Inject ConstructGenomicEntityAssociationDAO constructGenomicEntityAssociationDAO;

	public ConstructGenomicEntityAssociation validateConstructGenomicEntityAssociationDTO(ConstructGenomicEntityAssociationDTO dto, BackendBulkDataProvider beDataProvider) throws ValidationException {
		response = new ObjectResponse<ConstructGenomicEntityAssociation>();
		
		Construct construct = null;
		if (StringUtils.isNotBlank(dto.getConstructIdentifier())) {
			construct = constructService.getShallowEntity(constructService.getIdByModID(dto.getConstructIdentifier()));
			if (construct == null) {
				response.addErrorMessage("construct_identifier", ValidationConstants.INVALID_MESSAGE);
			} else {
				if (beDataProvider != null && !construct.getDataProvider().getAbbreviation().equals(beDataProvider.sourceOrganization)) {
					response.addErrorMessage("construct_identifier", ValidationConstants.INVALID_MESSAGE + " for " + beDataProvider.name() + " load");
					return null;
				}
			}
		} else {
			response.addErrorMessage("construct_identifier", ValidationConstants.REQUIRED_MESSAGE);
		}

		GenomicEntity genomicEntity = null;
		if (StringUtils.isBlank(dto.getGenomicEntityIdentifier())) {
			response.addErrorMessage("genomic_entity_identifier", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			genomicEntity = genomicEntityService.getShallowEntity(genomicEntityService.getIdByModID(dto.getGenomicEntityIdentifier()));
			if (genomicEntity == null) {
				response.addErrorMessage("genomic_entity_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getGenomicEntityIdentifier() + ")");
			}
		}
		ConstructGenomicEntityAssociation association = null;
		if (construct != null && StringUtils.isNotBlank(dto.getGenomicEntityRelationName()) && genomicEntity != null) {
			HashMap<String, Object> params = new HashMap<>();
			params.put("constructAssociationSubject.id", construct.getId());
			params.put("relation.name", dto.getGenomicEntityRelationName());
			params.put("constructGenomicEntityAssociationObject.id", genomicEntity.getId());

			SearchResponse<ConstructGenomicEntityAssociation> searchResponse = constructGenomicEntityAssociationDAO.findByParams(params);
			if (searchResponse != null && searchResponse.getResults().size() == 1) {
				association = searchResponse.getSingleResult();
			}
		}
		if (association == null) {
			association = new ConstructGenomicEntityAssociation();
		}

		association.setConstructAssociationSubject(construct);
		association.setConstructGenomicEntityAssociationObject(genomicEntity);

		association = validateEvidenceAssociationDTO(association, dto);

		VocabularyTerm relation = validateRequiredTermInVocabularyTermSet("genomic_entity_relation_name", dto.getGenomicEntityRelationName(), VocabularyConstants.CONSTRUCT_GENOMIC_ENTITY_RELATION_VOCABULARY_TERM_SET);
		association.setRelation(relation);

		if (association.getRelatedNotes() != null) {
			association.getRelatedNotes().clear();
		}

		List<Note> validatedNotes = validateNotes(dto.getNoteDtos(), VocabularyConstants.CONSTRUCT_COMPONENT_NOTE_TYPES_VOCABULARY_TERM_SET);
		if (CollectionUtils.isNotEmpty(validatedNotes)) {
			if (association.getRelatedNotes() == null) {
				association.setRelatedNotes(new ArrayList<>());
			}
			association.getRelatedNotes().addAll(validatedNotes);
		}

		if (response.hasErrors()) {
			throw new ObjectValidationException(dto, response.errorMessagesString());
		}

		return constructGenomicEntityAssociationDAO.persist(association);
	}
}
