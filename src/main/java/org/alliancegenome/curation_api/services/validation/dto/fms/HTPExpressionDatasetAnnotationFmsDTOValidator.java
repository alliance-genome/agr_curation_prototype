package org.alliancegenome.curation_api.services.validation.dto.fms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.HTPExpressionDatasetAnnotationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.ExternalDataBaseEntity;
import org.alliancegenome.curation_api.model.entities.HTPExpressionDatasetAnnotation;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.input.Pagination;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.ingest.dto.fms.HTPExpressionDatasetAnnotationFmsDTO;
import org.alliancegenome.curation_api.model.ingest.dto.fms.PublicationFmsDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.DataProviderService;
import org.alliancegenome.curation_api.services.ExternalDataBaseEntityService;
import org.alliancegenome.curation_api.services.ReferenceService;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class HTPExpressionDatasetAnnotationFmsDTOValidator {

	@Inject HTPExpressionDatasetAnnotationDAO htpExpressionDatasetAnnotationDAO;
	@Inject ExternalDataBaseEntityService externalDataBaseService;
	@Inject ReferenceService referenceService;
	@Inject VocabularyTermService vocabularyTermService;
	@Inject ExternalDataBaseEntityFmsDTOValidator externalDataBaseEntityFmsDtoValidator;
	@Inject DataProviderService dataProviderService;
	
	@Transactional
	public HTPExpressionDatasetAnnotation validateHTPExpressionDatasetAnnotationFmsDTO(HTPExpressionDatasetAnnotationFmsDTO dto, BackendBulkDataProvider backendBulkDataProvider) throws ValidationException {
		ObjectResponse<HTPExpressionDatasetAnnotation> htpAnnotationResponse = new ObjectResponse<>();

		HTPExpressionDatasetAnnotation htpannotation;

		if (dto.getDatasetId() != null) {
			String curie = dto.getDatasetId().getPrimaryId();
			ExternalDataBaseEntity externalDbEntity = externalDataBaseEntityFmsDtoValidator.validateExternalDataBaseEntityFmsDTO(dto.getDatasetId());
			if (externalDbEntity != null) {
				Long htpId = externalDbEntity.getId();
				Map<String, Object> params = new HashMap<>();
				params.put("htpExpressionDataset.id", htpId);
				SearchResponse<HTPExpressionDatasetAnnotation> searchResponse = htpExpressionDatasetAnnotationDAO.findByParams(params);
				if (searchResponse == null || searchResponse.getSingleResult() == null) {
					htpannotation = new HTPExpressionDatasetAnnotation();
					htpannotation.setHtpExpressionDataset(externalDbEntity);
				} else {
					htpannotation = searchResponse.getSingleResult();
				}
			} else {
				htpAnnotationResponse.addErrorMessage("datasetId", ValidationConstants.INVALID_MESSAGE + " (" + curie + ")");
				htpannotation = new HTPExpressionDatasetAnnotation();
			}
		} else {
			htpAnnotationResponse.addErrorMessage("datasetId", ValidationConstants.REQUIRED_MESSAGE);
			htpannotation = new HTPExpressionDatasetAnnotation();
		}

		if (CollectionUtils.isNotEmpty(dto.getPublications())) {
			List<Reference> references = new ArrayList<>();
			for (PublicationFmsDTO publication : dto.getPublications()) {
				if (StringUtils.isEmpty(publication.getPublicationId())) {
					htpAnnotationResponse.addErrorMessage("Publication - publicationId", ValidationConstants.REQUIRED_MESSAGE + " (" + publication.getPublicationId() + ")");
				} else {
					Reference reference = referenceService.retrieveFromDbOrLiteratureService(publication.getPublicationId());
					if (reference == null) {
						htpAnnotationResponse.addErrorMessage("Publication - publicationId", ValidationConstants.INVALID_MESSAGE + " (" + publication.getPublicationId() + ")");
					} else {
						references.add(reference);
					}
				}
			}
			htpannotation.setReferences(references);
		} else {
			htpannotation.setReferences(null);
		}

		if (dto.getNumChannels() != null) {
			if (dto.getNumChannels() == 1 || dto.getNumChannels() == 2) {
				htpannotation.setNumberOfChannels(dto.getNumChannels());
			} else {
				htpAnnotationResponse.addErrorMessage("numChannels", ValidationConstants.INVALID_MESSAGE);
			}
		} else {
			htpannotation.setNumberOfChannels(null);
		}

		if (CollectionUtils.isNotEmpty(dto.getSubSeries())) {
			List<ExternalDataBaseEntity> subSeries = new ArrayList<>();
			for (String subSeriesString : dto.getSubSeries()) {
				if (StringUtils.isNotEmpty(subSeriesString)) {
					ExternalDataBaseEntity externalDbEntity = externalDataBaseService.findByCurie(subSeriesString);
					if (externalDbEntity != null) {
						subSeries.add(externalDbEntity);
					} else {
						htpAnnotationResponse.addErrorMessage("subSeries", ValidationConstants.INVALID_MESSAGE + " (" + subSeries + ")");
					}
				}
			}
			htpannotation.setSubSeries(subSeries);
		} else {
			htpannotation.setSubSeries(null);
		}

		if (CollectionUtils.isNotEmpty(dto.getCategoryTags())) {
			Map<String, VocabularyTerm> categoryTags = new HashMap<>();
			for (String categoryTag : dto.getCategoryTags()) {
				if (StringUtils.isNotEmpty(categoryTag)) {
					Map<String, Object> params = new HashMap<>();
					params.put("name", categoryTag);
					params.put("query_operator", "or");
					params.put("synonyms", categoryTag);
					SearchResponse<VocabularyTerm> searchResponse = vocabularyTermService.findByParams(new Pagination(), params);
					boolean added = false;
					if (searchResponse.getTotalResults() > 0) {
						for (VocabularyTerm tag : searchResponse.getResults()) {
							if (tag.getVocabulary().getName().equals("Data Set Category Tags") && (tag.getName().equals(categoryTag) || tag.getSynonyms().contains(categoryTag))) {
								if (categoryTags.containsKey(categoryTag)) {
									htpAnnotationResponse.addErrorMessage("categoryTags", ValidationConstants.INVALID_MESSAGE + " Multiple Tags found in the Vocabulary " + " (" + categoryTag + ")");
								} else {
									categoryTags.put(categoryTag, tag);
									added = true;
								}
							}
						}
					}
					if (!added) {
						htpAnnotationResponse.addErrorMessage("categoryTags", ValidationConstants.INVALID_MESSAGE + " (" + categoryTag + ")");
					}
				}
			}
			htpannotation.setCategoryTags(new ArrayList<>(categoryTags.values()));
		} else {
			htpAnnotationResponse.addErrorMessage("categoryTags", ValidationConstants.REQUIRED_MESSAGE);
		}

		if (StringUtils.isEmpty(dto.getTitle())) {
			htpAnnotationResponse.addErrorMessage("title", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			htpannotation.setName(dto.getTitle());
		}

		if (StringUtils.isNotEmpty(dto.getSummary())) {
			Note relatedNote = new Note();
			relatedNote.setFreeText(dto.getSummary());
			relatedNote.setNoteType(vocabularyTermService.getTermInVocabularyTermSet(VocabularyConstants.HTP_DATASET_NOTE_TYPE_VOCABULARY_TERM_SET, "htp_expression_dataset_summary").getEntity());
			htpannotation.setRelatedNote(relatedNote);
		} else {
			htpannotation.setRelatedNote(null);
		}

		htpannotation.setDataProvider(dataProviderService.getDefaultDataProvider(backendBulkDataProvider.sourceOrganization));

		if (htpAnnotationResponse.hasErrors()) {
			throw new ObjectValidationException(dto, htpAnnotationResponse.errorMessagesString());
		}

		return htpExpressionDatasetAnnotationDAO.persist(htpannotation);
	}
}