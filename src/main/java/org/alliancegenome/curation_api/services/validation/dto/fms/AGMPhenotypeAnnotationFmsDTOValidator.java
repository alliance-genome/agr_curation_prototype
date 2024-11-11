package org.alliancegenome.curation_api.services.validation.dto.fms;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.AGMPhenotypeAnnotationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.AGMPhenotypeAnnotation;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.GenomicEntity;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.ingest.dto.fms.PhenotypeFmsDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.GenomicEntityService;
import org.alliancegenome.curation_api.services.PhenotypeAnnotationService;
import org.alliancegenome.curation_api.services.helpers.annotations.AnnotationUniqueIdHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AGMPhenotypeAnnotationFmsDTOValidator extends PhenotypeAnnotationFmsDTOValidator {

	@Inject AGMPhenotypeAnnotationDAO agmPhenotypeAnnotationDAO;
	@Inject GenomicEntityService genomicEntityService;
	@Inject PhenotypeAnnotationService phenotypeAnnotationService;

	public AGMPhenotypeAnnotation validatePrimaryAnnotation(AffectedGenomicModel subject, PhenotypeFmsDTO dto, BackendBulkDataProvider dataProvider) throws ObjectValidationException {

		ObjectResponse<AGMPhenotypeAnnotation> apaResponse = new ObjectResponse<AGMPhenotypeAnnotation>();
		AGMPhenotypeAnnotation annotation = new AGMPhenotypeAnnotation();

		ObjectResponse<Reference> refResponse = validateReference(dto);
		apaResponse.addErrorMessages(refResponse.getErrorMessages());

		Reference reference = refResponse.getEntity();
		String refString = reference == null ? null : reference.getCurie();

		String uniqueId = AnnotationUniqueIdHelper.getPhenotypeAnnotationUniqueId(dto, subject.getIdentifier(), refString);
		SearchResponse<AGMPhenotypeAnnotation> annotationSearch = agmPhenotypeAnnotationDAO.findByField("uniqueId", uniqueId);
		if (annotationSearch != null && annotationSearch.getSingleResult() != null) {
			annotation = annotationSearch.getSingleResult();
		}

		annotation.setUniqueId(uniqueId);
		annotation.setSingleReference(reference);
		annotation.setPhenotypeAnnotationSubject(subject);

		// Reset implied/asserted fields as secondary annotations loaded separately
		annotation.setAssertedAllele(null);
		annotation.setAssertedGenes(null);
		annotation.setInferredAllele(null);
		annotation.setInferredGene(null);

		ObjectResponse<AGMPhenotypeAnnotation> paResponse = validatePhenotypeAnnotation(annotation, dto, dataProvider);
		apaResponse.addErrorMessages(paResponse.getErrorMessages());
		annotation = paResponse.getEntity();

		if (apaResponse.hasErrors()) {
			throw new ObjectValidationException(dto, apaResponse.errorMessagesString());
		}

		return annotation;

	}

	public List<AGMPhenotypeAnnotation> validateInferredOrAssertedEntities(AffectedGenomicModel primaryAnnotationSubject, PhenotypeFmsDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {
		ObjectResponse<AGMPhenotypeAnnotation> apaResponse = new ObjectResponse<AGMPhenotypeAnnotation>();

		ObjectResponse<Reference> refResponse = validateReference(dto);
		apaResponse.addErrorMessages(refResponse.getErrorMessages());

		Reference reference = refResponse.getEntity();
		String refString = reference == null ? null : reference.getCurie();
		
		List<AGMPhenotypeAnnotation> primaryAnnotations = findPrimaryAnnotations(agmPhenotypeAnnotationDAO, dto, primaryAnnotationSubject.getModEntityId(), refString);
		
		if (CollectionUtils.isEmpty(primaryAnnotations)) {
			PhenotypeFmsDTO inferredPrimaryDTO = createPrimaryAnnotationDTO(dto, primaryAnnotationSubject.getIdentifier());
			try {
				Long primaryAnnotationId = phenotypeAnnotationService.upsertPrimaryAnnotation(inferredPrimaryDTO, dataProvider);
				AGMPhenotypeAnnotation primaryAnnotation = agmPhenotypeAnnotationDAO.find(primaryAnnotationId);
				primaryAnnotations = List.of(primaryAnnotation);
			} catch (ObjectUpdateException e) {
				throw new ObjectValidationException(dto, "Could not construct primary annotation for " + inferredPrimaryDTO.getObjectId() + ": " + e.getData().getMessage());
			} catch (Exception e) {
				throw new ObjectValidationException(dto, e.getMessage());
			}
		}

		if (StringUtils.isBlank(dto.getObjectId())) {
			apaResponse.addErrorMessage("objectId", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			GenomicEntity inferredOrAssertedEntity = genomicEntityService.findByIdentifierString(dto.getObjectId());
			if (inferredOrAssertedEntity == null) {
				apaResponse.addErrorMessage("objectId", ValidationConstants.INVALID_MESSAGE + " (" + dto.getObjectId() + ")");
			} else if (inferredOrAssertedEntity instanceof Gene) {
				for (AGMPhenotypeAnnotation primaryAnnotation : primaryAnnotations) {
					if (dataProvider.hasInferredGenePhenotypeAnnotations) {
						primaryAnnotation.setInferredGene((Gene) inferredOrAssertedEntity);
					} else if (dataProvider.hasAssertedGenePhenotypeAnnotations) {
						List<Gene> assertedGenes = primaryAnnotation.getAssertedGenes();
						if (assertedGenes == null) {
							assertedGenes = new ArrayList<>();
						}
						assertedGenes.add((Gene) inferredOrAssertedEntity);
						primaryAnnotation.setAssertedGenes(assertedGenes);
					} else {
						apaResponse.addErrorMessage("objectId", ValidationConstants.INVALID_MESSAGE + " (" + dto.getObjectId() + ")");
					}
				}
			} else if (inferredOrAssertedEntity instanceof Allele) {
				for (AGMPhenotypeAnnotation primaryAnnotation : primaryAnnotations) {
					if (dataProvider.hasInferredAllelePhenotypeAnnotations) {
						primaryAnnotation.setInferredAllele((Allele) inferredOrAssertedEntity);
					} else if (dataProvider.hasAssertedAllelePhenotypeAnnotations) {
						primaryAnnotation.setAssertedAllele((Allele) inferredOrAssertedEntity);
					} else {
						apaResponse.addErrorMessage("objectId", ValidationConstants.INVALID_MESSAGE + " (" + dto.getObjectId() + ")");
					}
				}
			} else {
				apaResponse.addErrorMessage("objectId", ValidationConstants.INVALID_MESSAGE + " (" + dto.getObjectId() + ")");
			}
		}

		if (apaResponse.hasErrors()) {
			throw new ObjectValidationException(dto, apaResponse.errorMessagesString());
		}
		
		return primaryAnnotations;
	}

	private PhenotypeFmsDTO createPrimaryAnnotationDTO(PhenotypeFmsDTO dto, String primarySubjectId) {
		PhenotypeFmsDTO primaryAnnotationDTO = new PhenotypeFmsDTO();

		primaryAnnotationDTO.setObjectId(primarySubjectId);
		primaryAnnotationDTO.setPhenotypeStatement(dto.getPhenotypeStatement());
		primaryAnnotationDTO.setPhenotypeTermIdentifiers(dto.getPhenotypeTermIdentifiers());
		primaryAnnotationDTO.setEvidence(dto.getEvidence());
		primaryAnnotationDTO.setDateAssigned(dto.getDateAssigned());
		primaryAnnotationDTO.setConditionRelations(dto.getConditionRelations());

		return primaryAnnotationDTO;
	}
}
