package org.alliancegenome.curation_api.services.validation.dto.fms;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.GeneExpressionAnnotationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.model.entities.ontology.*;
import org.alliancegenome.curation_api.model.ingest.dto.fms.GeneExpressionFmsDTO;
import org.alliancegenome.curation_api.model.ingest.dto.fms.UberonSlimTermDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.DataProviderService;
import org.alliancegenome.curation_api.services.GeneService;
import org.alliancegenome.curation_api.services.ReferenceService;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.alliancegenome.curation_api.services.helpers.annotations.GeneExpressionAnnotationUniqueIdHelper;
import org.alliancegenome.curation_api.services.ontology.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@RequestScoped
public class GeneExpressionAnnotationFmsDTOValidator {

	@Inject GeneService geneService;
	@Inject MmoTermService mmoTermService;
	@Inject ReferenceService referenceService;
	@Inject GeneExpressionAnnotationUniqueIdHelper geneExpressionAnnotationUniqueIdHelper;
	@Inject GeneExpressionAnnotationDAO geneExpressionAnnotationDAO;
	@Inject DataProviderService dataProviderService;
	@Inject VocabularyTermService vocabularyTermService;
	@Inject AnatomicalTermService anatomicalTermService;
	@Inject GoTermService goTermService;
	@Inject UberonTermService uberonTermService;
	@Inject StageTermService stageTermService;
	@Inject OntologyTermService ontologyTermService;

	public GeneExpressionAnnotation validateAnnotation(GeneExpressionFmsDTO geneExpressionFmsDTO, BackendBulkDataProvider dataProvider) throws ValidationException {
		ObjectResponse<GeneExpressionAnnotation> response = new ObjectResponse<>();
		GeneExpressionAnnotation geneExpressionAnnotation;

		ObjectResponse<Reference> singleReferenceResponse = validateEvidence(geneExpressionFmsDTO);
		if (singleReferenceResponse.hasErrors()) {
			response.addErrorMessage("singleReference", singleReferenceResponse.errorMessagesString());
			throw new ObjectValidationException(geneExpressionFmsDTO, response.errorMessagesString());
		} else {
			String referenceCurie = singleReferenceResponse.getEntity().getCurie();
			String uniqueId = geneExpressionAnnotationUniqueIdHelper.generateUniqueId(geneExpressionFmsDTO, referenceCurie);
			SearchResponse<GeneExpressionAnnotation> annotationDB = geneExpressionAnnotationDAO.findByField("uniqueId", uniqueId);
			if (annotationDB != null && annotationDB.getSingleResult() != null) {
				geneExpressionAnnotation = annotationDB.getSingleResult();
			} else {
				geneExpressionAnnotation = new GeneExpressionAnnotation();
				geneExpressionAnnotation.setUniqueId(uniqueId);
			}
			if (geneExpressionAnnotation.getExpressionPattern() == null) {
				geneExpressionAnnotation.setExpressionPattern(new ExpressionPattern());
			}
			geneExpressionAnnotation.setSingleReference(singleReferenceResponse.getEntity());
		}

		if (ObjectUtils.isEmpty(geneExpressionFmsDTO.getGeneId())) {
			response.addErrorMessage("geneId - ", ValidationConstants.REQUIRED_MESSAGE + " (" + geneExpressionFmsDTO.getGeneId() + ")");
		} else {
			Gene expressionAnnotationSubject = geneService.findByIdentifierString(geneExpressionFmsDTO.getGeneId());
			if (expressionAnnotationSubject == null) {
				response.addErrorMessage("geneId - ", ValidationConstants.INVALID_MESSAGE + " (" + geneExpressionFmsDTO.getGeneId() + ")");
			} else {
				geneExpressionAnnotation.setExpressionAnnotationSubject(expressionAnnotationSubject);
			}
		}

		if (ObjectUtils.isEmpty(geneExpressionFmsDTO.getDateAssigned())) {
			response.addErrorMessage("dateAssigned - ", ValidationConstants.REQUIRED_MESSAGE + " (" + geneExpressionFmsDTO.getDateAssigned() + ")");
		} else {
			OffsetDateTime creationDate = null;
			try {
				creationDate = OffsetDateTime.parse(geneExpressionFmsDTO.getDateAssigned());
			} catch (DateTimeParseException e) {
				response.addErrorMessage("dateAssigned", ValidationConstants.INVALID_MESSAGE + " (" + geneExpressionFmsDTO.getDateAssigned() + ")");
			}
			geneExpressionAnnotation.setDateCreated(creationDate);
		}

		if (ObjectUtils.isEmpty(geneExpressionFmsDTO.getAssay())) {
			response.addErrorMessage("assay - ", ValidationConstants.REQUIRED_MESSAGE + " (" + geneExpressionFmsDTO.getAssay() + ")");
		} else {
			MMOTerm expressionAssayUsed = mmoTermService.findByCurie(geneExpressionFmsDTO.getAssay());
			if (expressionAssayUsed == null) {
				response.addErrorMessage("assay - ", ValidationConstants.INVALID_MESSAGE + " (" + geneExpressionFmsDTO.getAssay() + ")");
			} else {
				geneExpressionAnnotation.setExpressionAssayUsed(expressionAssayUsed);
			}
		}

		ObjectResponse<AnatomicalSite> anatomicalSiteObjectResponse = validateAnatomicalSite(geneExpressionFmsDTO);
		if (anatomicalSiteObjectResponse.hasErrors()) {
			response.addErrorMessage("expressionPattern", anatomicalSiteObjectResponse.errorMessagesString());
		} else {
			geneExpressionAnnotation.setWhereExpressedStatement(geneExpressionFmsDTO.getWhereExpressed().getWhereExpressedStatement());
			AnatomicalSite anatomicalSite = updateAnatomicalSite(anatomicalSiteObjectResponse, geneExpressionAnnotation);
			geneExpressionAnnotation.getExpressionPattern().setWhereExpressed(anatomicalSite);
		}

		ObjectResponse<TemporalContext> temporalContextObjectResponse = validateTemporalContext(geneExpressionFmsDTO);
		if (temporalContextObjectResponse.hasErrors()) {
			response.addErrorMessage("expressionPattern", temporalContextObjectResponse.errorMessagesString());
		} else {
			geneExpressionAnnotation.setWhenExpressedStageName(geneExpressionFmsDTO.getWhenExpressed().getStageName());
			TemporalContext temporalContext = updateTemporalContext(temporalContextObjectResponse, geneExpressionAnnotation);
			geneExpressionAnnotation.getExpressionPattern().setWhenExpressed(temporalContext);
		}

		geneExpressionAnnotation.setDataProvider(dataProviderService.getDefaultDataProvider(dataProvider.sourceOrganization));
		geneExpressionAnnotation.setRelation(vocabularyTermService.getTermInVocabulary(VocabularyConstants.GENE_EXPRESSION_VOCABULARY, VocabularyConstants.GENE_EXPRESSION_RELATION_TERM).getEntity());
		geneExpressionAnnotation.setObsolete(false);
		geneExpressionAnnotation.setInternal(false);

		if (response.hasErrors()) {
			throw new ObjectValidationException(geneExpressionFmsDTO, response.errorMessagesString());
		}
		return geneExpressionAnnotation;
	}

	private ObjectResponse<TemporalContext> validateTemporalContext(GeneExpressionFmsDTO geneExpressionFmsDTO) {
		ObjectResponse<TemporalContext> response = new ObjectResponse<>();
		TemporalContext temporalContext = new TemporalContext();
		if (ObjectUtils.isEmpty(geneExpressionFmsDTO.getWhenExpressed())) {
			response.addErrorMessage("whenExpressed - ", ValidationConstants.REQUIRED_MESSAGE + " (" + geneExpressionFmsDTO.getWhenExpressed() + ")");
			return response;
		} else {
			String stageName = geneExpressionFmsDTO.getWhenExpressed().getStageName();
			if (ObjectUtils.isEmpty(stageName)) {
				response.addErrorMessage("whenExpressed - whenExpressedStageName", ValidationConstants.REQUIRED_MESSAGE + " (" + stageName + ")");
			}
			String stageTermId = geneExpressionFmsDTO.getWhenExpressed().getStageTermId();
			if (!ObjectUtils.isEmpty(stageTermId)) {
				StageTerm stageTerm = stageTermService.findByCurie(stageTermId);
				if (stageTerm == null) {
					response.addErrorMessage("whenExpressed - stageTermId", ValidationConstants.INVALID_MESSAGE + " (" + stageTermId + ")");
				} else {
					temporalContext.setDevelopmentalStageStart(stageTerm);
				}
			}
			if (!ObjectUtils.isEmpty(geneExpressionFmsDTO.getWhenExpressed().getStageUberonSlimTerm())) {
				if (!ObjectUtils.isEmpty(geneExpressionFmsDTO.getWhenExpressed().getStageUberonSlimTerm().getUberonTerm())) {
					String stageUberonSlimTermId = geneExpressionFmsDTO.getWhenExpressed().getStageUberonSlimTerm().getUberonTerm();
					if (!ObjectUtils.isEmpty(stageUberonSlimTermId)) {
						VocabularyTerm stageUberonSlimTerm = vocabularyTermService.getTermInVocabulary(VocabularyConstants.STAGE_UBERON_SLIM_TERMS, stageUberonSlimTermId).getEntity();
						if (stageUberonSlimTerm == null) {
							response.addErrorMessage("whenExpressed - stageUberonSlimTermId", ValidationConstants.INVALID_MESSAGE + " (" + stageUberonSlimTermId + ")");
						} else {
							temporalContext.setStageUberonSlimTerms(List.of(stageUberonSlimTerm));
						}
					}
				}
			}
			response.setEntity(temporalContext);
		}
		return response;
	}

	private ObjectResponse<AnatomicalSite> validateAnatomicalSite(GeneExpressionFmsDTO geneExpressionFmsDTO) {
		ObjectResponse<AnatomicalSite> response = new ObjectResponse<>();
		AnatomicalSite anatomicalSite = new AnatomicalSite();
		if (ObjectUtils.isEmpty(geneExpressionFmsDTO.getWhereExpressed())) {
			response.addErrorMessage("whereExpressed - ", ValidationConstants.REQUIRED_MESSAGE + " (" + geneExpressionFmsDTO.getWhereExpressed() + ")");
		} else {
			if (ObjectUtils.isEmpty(geneExpressionFmsDTO.getWhereExpressed().getWhereExpressedStatement())) {
				response.addErrorMessage("whereExpressed - whereExpressedStatement", ValidationConstants.REQUIRED_MESSAGE + " (" + geneExpressionFmsDTO.getWhereExpressed().getWhereExpressedStatement() + ")");
				return response;
			}

			boolean lackAnatomicalStructureTermId = ObjectUtils.isEmpty(geneExpressionFmsDTO.getWhereExpressed().getAnatomicalStructureTermId());
			boolean lackStructureUberonSlimTermIds = ObjectUtils.isEmpty(geneExpressionFmsDTO.getWhereExpressed().getAnatomicalStructureUberonSlimTermIds());
			boolean lackCellularComponentId = ObjectUtils.isEmpty(geneExpressionFmsDTO.getWhereExpressed().getCellularComponentTermId());

			if ((lackAnatomicalStructureTermId || lackStructureUberonSlimTermIds) && lackCellularComponentId) {
				response.addErrorMessage("whereExpressed - MUST HAVe (anatomicalStructureTermId and anatomicalStructureUberonSlimTermIds) or cellularComponentTermId", ValidationConstants.REQUIRED_MESSAGE + " (" + geneExpressionFmsDTO.getWhereExpressed() + ")");
			}

			if (!lackAnatomicalStructureTermId) {
				AnatomicalTerm anatomicalStructureTerm = anatomicalTermService.findByCurie(geneExpressionFmsDTO.getWhereExpressed().getAnatomicalStructureTermId());
				if (anatomicalStructureTerm == null) {
					response.addErrorMessage("whereExpressed - anatomicalStructureTermId", ValidationConstants.INVALID_MESSAGE + " (" + geneExpressionFmsDTO.getWhereExpressed().getAnatomicalStructureTermId() + ")");
				} else {
					anatomicalSite.setAnatomicalStructure(anatomicalStructureTerm);
				}
			}

			if (!ObjectUtils.isEmpty(geneExpressionFmsDTO.getWhereExpressed().getAnatomicalSubStructureTermId())) {
				AnatomicalTerm anatomicalSubStructureTerm = anatomicalTermService.findByCurie(geneExpressionFmsDTO.getWhereExpressed().getAnatomicalSubStructureTermId());
				if (anatomicalSubStructureTerm == null) {
					response.addErrorMessage("whereExpressed - anatomicalSubStructureTermId", ValidationConstants.INVALID_MESSAGE + " (" + geneExpressionFmsDTO.getWhereExpressed().getAnatomicalSubStructureTermId() + ")");
				} else {
					anatomicalSite.setAnatomicalSubstructure(anatomicalSubStructureTerm);
				}
			}

			if (!lackCellularComponentId) {
				GOTerm cellularComponent = goTermService.findByCurie(geneExpressionFmsDTO.getWhereExpressed().getCellularComponentTermId());
				if (cellularComponent == null) {
					response.addErrorMessage("whereExpressed - cellularComponentTermId", ValidationConstants.INVALID_MESSAGE + " (" + geneExpressionFmsDTO.getWhereExpressed().getCellularComponentTermId() + ")");
				} else {
					GOTerm cellularComponentRibbon = goTermService.findSubsetTerm(cellularComponent, "goslim_agr");
					if (cellularComponentRibbon == null) {
						anatomicalSite.setCellularComponentOther(true);
					} else {
						anatomicalSite.setCellularComponentOther(false);
						anatomicalSite.setCellularComponentRibbonTerm(cellularComponentRibbon);
					}
					anatomicalSite.setCellularComponentTerm(cellularComponent);
				}
			}

			if (!ObjectUtils.isEmpty(geneExpressionFmsDTO.getWhereExpressed().getAnatomicalStructureQualifierTermId())) {
				String anatomicalstructurequalifiertermId = geneExpressionFmsDTO.getWhereExpressed().getAnatomicalStructureQualifierTermId();
				if (vocabularyTermService.getTermInVocabularyTermSet(VocabularyConstants.ANATOMICAL_STRUCTURE_QUALIFIER, anatomicalstructurequalifiertermId) != null) {
					OntologyTerm anatomicalStructureQualifierTerm = ontologyTermService.findByCurieOrSecondaryId(anatomicalstructurequalifiertermId);
					if (anatomicalStructureQualifierTerm == null) {
						response.addErrorMessage("whereExpressed - anatomicalStructureQualifierTermId", ValidationConstants.INVALID_MESSAGE + " (" + anatomicalstructurequalifiertermId + ")");
					} else {
						anatomicalSite.setAnatomicalStructureQualifiers(List.of(anatomicalStructureQualifierTerm));
					}
				}
			}

			if (!ObjectUtils.isEmpty(geneExpressionFmsDTO.getWhereExpressed().getAnatomicalSubStructureQualifierTermId())) {
				String anatomicalsubstructurequalifierId = geneExpressionFmsDTO.getWhereExpressed().getAnatomicalSubStructureQualifierTermId();
				if (vocabularyTermService.getTermInVocabularyTermSet(VocabularyConstants.ANATOMICAL_SUBSTRUCTURE_QUALIFIER, anatomicalsubstructurequalifierId) != null) {
					OntologyTerm anatomicalSubStructureQualifierTerm = ontologyTermService.findByCurieOrSecondaryId(anatomicalsubstructurequalifierId);
					if (anatomicalSubStructureQualifierTerm == null) {
						response.addErrorMessage("whereExpressed - anatomicalSubStructureQualifierTermId", ValidationConstants.INVALID_MESSAGE + " (" + anatomicalsubstructurequalifierId + ")");
					} else {
						anatomicalSite.setAnatomicalSubstructureQualifiers(List.of(anatomicalSubStructureQualifierTerm));
					}
				}
			}

			if (!ObjectUtils.isEmpty(geneExpressionFmsDTO.getWhereExpressed().getCellularComponentQualifierTermId())) {
				String cellularComponentQualifierTermId = geneExpressionFmsDTO.getWhereExpressed().getCellularComponentQualifierTermId();
				if (vocabularyTermService.getTermInVocabularyTermSet(VocabularyConstants.CELLULAR_COMPONENT_QUALIFIER, cellularComponentQualifierTermId) != null) {
					OntologyTerm cellularComponentQualifierTerm = ontologyTermService.findByCurieOrSecondaryId(cellularComponentQualifierTermId);
					if (cellularComponentQualifierTerm == null) {
						response.addErrorMessage("whereExpressed - cellularComponentQualifierTermId", ValidationConstants.INVALID_MESSAGE + " (" + cellularComponentQualifierTermId + ")");
					} else {
						anatomicalSite.setCellularComponentQualifiers(List.of(cellularComponentQualifierTerm));
					}
				}
			}

			if (!ObjectUtils.isEmpty(geneExpressionFmsDTO.getWhereExpressed().getAnatomicalStructureUberonSlimTermIds())) {
				List<UberonSlimTermDTO> anatomicalStructureUberonSlimTermIds = geneExpressionFmsDTO.getWhereExpressed().getAnatomicalStructureUberonSlimTermIds();
				List<UBERONTerm> uberonTerms = new ArrayList<>();
				for (UberonSlimTermDTO uberonSlimTermDTO: anatomicalStructureUberonSlimTermIds) {
					if (!uberonSlimTermDTO.getUberonTerm().equals("Other")) {
						UBERONTerm uberonTerm = uberonTermService.getByCurie(uberonSlimTermDTO.getUberonTerm()).getEntity();
						if (uberonTerm == null) {
							response.addErrorMessage("whereExpressed - anatomicalStructureUberonSlimTermId", ValidationConstants.INVALID_MESSAGE + " (" + uberonSlimTermDTO.getUberonTerm() + ")");
						} else {
							uberonTerms.add(uberonTerm);
						}
					}
				}
				anatomicalSite.setAnatomicalStructureUberonTerms(uberonTerms);
			}

			if (!ObjectUtils.isEmpty(geneExpressionFmsDTO.getWhereExpressed().getAnatomicalSubStructureUberonSlimTermIds())) {
				List<UberonSlimTermDTO> anatomicalSubStructureUberonSlimTermIds = geneExpressionFmsDTO.getWhereExpressed().getAnatomicalSubStructureUberonSlimTermIds();
				List<UBERONTerm> uberonTerms = new ArrayList<>();
				for (UberonSlimTermDTO uberonSlimTermDTO : anatomicalSubStructureUberonSlimTermIds) {
					if (!uberonSlimTermDTO.getUberonTerm().equals("Other")) {
						UBERONTerm uberonTerm = uberonTermService.getByCurie(uberonSlimTermDTO.getUberonTerm()).getEntity();
						if (uberonTerm == null) {
							response.addErrorMessage("whereExpressed - anatomicalStructureUberonSlimTermId", ValidationConstants.INVALID_MESSAGE + " (" + uberonSlimTermDTO.getUberonTerm() + ")");
						} else {
							uberonTerms.add(uberonTerm);
						}
					}
				}
				anatomicalSite.setAnatomicalSubstructureUberonTerms(uberonTerms);
			}
			response.setEntity(anatomicalSite);
		}
		return response;
	}

	private ObjectResponse<Reference> validateEvidence(GeneExpressionFmsDTO geneExpressionFmsDTO) {
		ObjectResponse<Reference> response = new ObjectResponse<>();

		if (ObjectUtils.isEmpty(geneExpressionFmsDTO.getEvidence())) {
			response.addErrorMessage("evidence - ", ValidationConstants.REQUIRED_MESSAGE + " (" + geneExpressionFmsDTO.getEvidence() + ")");
		} else {
			if (StringUtils.isEmpty(geneExpressionFmsDTO.getEvidence().getPublicationId())) {
				response.addErrorMessage("evidence - publicationId", ValidationConstants.REQUIRED_MESSAGE + " (" + geneExpressionFmsDTO.getEvidence().getPublicationId() + ")");
			} else {
				Reference reference = referenceService.retrieveFromDbOrLiteratureService(geneExpressionFmsDTO.getEvidence().getPublicationId());
				if (reference == null) {
					response.addErrorMessage("evidence - publicationId", ValidationConstants.INVALID_MESSAGE + " (" + geneExpressionFmsDTO.getEvidence().getPublicationId() + ")");
				} else {
					response.setEntity(reference);
				}
			}
		}
		return response;
	}

	private TemporalContext updateTemporalContext(ObjectResponse<TemporalContext> temporalContextObjectResponse, GeneExpressionAnnotation geneExpressionAnnotation) {
		TemporalContext temporalContext = temporalContextObjectResponse.getEntity();
		TemporalContext temporalContextDB = geneExpressionAnnotation.getExpressionPattern().getWhenExpressed();
		if (temporalContextDB == null) {
			temporalContextDB = new TemporalContext();
		}
		temporalContextDB.setDevelopmentalStageStart(temporalContext.getDevelopmentalStageStart());
		temporalContextDB.setStageUberonSlimTerms(temporalContext.getStageUberonSlimTerms());
		return temporalContextDB;
	}

	private AnatomicalSite updateAnatomicalSite(ObjectResponse<AnatomicalSite> anatomicalSiteObjectResponse, GeneExpressionAnnotation geneExpressionAnnotation) {
		AnatomicalSite anatomicalSite = anatomicalSiteObjectResponse.getEntity();
		AnatomicalSite anatomicalSiteDB = geneExpressionAnnotation.getExpressionPattern().getWhereExpressed();
		if (anatomicalSiteDB == null) {
			anatomicalSiteDB = new AnatomicalSite();
		}
		anatomicalSiteDB.setCellularComponentTerm(anatomicalSite.getCellularComponentTerm());
		anatomicalSiteDB.setCellularComponentRibbonTerm(anatomicalSite.getCellularComponentRibbonTerm());
		anatomicalSiteDB.setCellularComponentOther(anatomicalSite.getCellularComponentOther());
		anatomicalSiteDB.setAnatomicalStructure(anatomicalSite.getAnatomicalStructure());
		anatomicalSiteDB.setAnatomicalSubstructure(anatomicalSite.getAnatomicalSubstructure());
		anatomicalSiteDB.setAnatomicalStructureQualifiers(anatomicalSite.getAnatomicalStructureQualifiers());
		anatomicalSiteDB.setAnatomicalSubstructureQualifiers(anatomicalSite.getAnatomicalSubstructureQualifiers());
		anatomicalSiteDB.setCellularComponentQualifiers(anatomicalSite.getCellularComponentQualifiers());
		anatomicalSiteDB.setAnatomicalStructureUberonTerms(anatomicalSite.getAnatomicalStructureUberonTerms());
		anatomicalSiteDB.setAnatomicalSubstructureUberonTerms(anatomicalSite.getAnatomicalSubstructureUberonTerms());
		return anatomicalSiteDB;
	}
}
