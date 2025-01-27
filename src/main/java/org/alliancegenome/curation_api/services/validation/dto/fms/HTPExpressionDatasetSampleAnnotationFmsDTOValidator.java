package org.alliancegenome.curation_api.services.validation.dto.fms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.AnatomicalSiteDAO;
import org.alliancegenome.curation_api.dao.HTPExpressionDatasetSampleAnnotationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.AnatomicalSite;
import org.alliancegenome.curation_api.model.entities.BioSampleAge;
import org.alliancegenome.curation_api.model.entities.BioSampleGenomicInformation;
import org.alliancegenome.curation_api.model.entities.ExternalDataBaseEntity;
import org.alliancegenome.curation_api.model.entities.HTPExpressionDatasetSampleAnnotation;
import org.alliancegenome.curation_api.model.entities.MicroarraySampleDetails;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.TemporalContext;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.MMOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.entities.ontology.OBITerm;
import org.alliancegenome.curation_api.model.ingest.dto.fms.BioSampleGenomicInformationFmsDTO;
import org.alliancegenome.curation_api.model.ingest.dto.fms.HTPExpressionDatasetSampleAnnotationFmsDTO;
import org.alliancegenome.curation_api.model.ingest.dto.fms.WhereExpressedFmsDTO;
import org.alliancegenome.curation_api.model.input.Pagination;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.AffectedGenomicModelService;
import org.alliancegenome.curation_api.services.AlleleService;
import org.alliancegenome.curation_api.services.ExternalDataBaseEntityService;
import org.alliancegenome.curation_api.services.OrganizationService;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.alliancegenome.curation_api.services.ontology.MmoTermService;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.alliancegenome.curation_api.services.ontology.ObiTermService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class HTPExpressionDatasetSampleAnnotationFmsDTOValidator {

	@Inject ExternalDataBaseEntityFmsDTOValidator externalDataBaseEntityFmsDtoValidator;
	@Inject GeneExpressionAnnotationFmsDTOValidator geneExpressionAnnotationFmsDTOValidator;
	@Inject HTPExpressionDatasetSampleAnnotationDAO htpExpressionDatasetSampleAnnotationDAO;
	@Inject VocabularyTermService vocabularyTermService;
	@Inject ExternalDataBaseEntityService externalDataBaseEntityService;
	@Inject OrganizationService organizationService;
	@Inject ObiTermService obiTermService;
	@Inject MmoTermService mmoTermService;
	@Inject AlleleService alleleService;
	@Inject AffectedGenomicModelService affectedGenomicModelService;
	@Inject NcbiTaxonTermService ncbiTaxonTermService;
	@Inject AnatomicalSiteDAO anatomicalSiteDAO;

	@Transactional
	public HTPExpressionDatasetSampleAnnotation validateHTPExpressionDatasetSampleAnnotationFmsDTO(HTPExpressionDatasetSampleAnnotationFmsDTO dto, BackendBulkDataProvider backendBulkDataProvider) throws ValidationException {
		ObjectResponse<HTPExpressionDatasetSampleAnnotation> htpSampleAnnotationResponse = new ObjectResponse<>();
		HTPExpressionDatasetSampleAnnotation htpSampleAnnotation;

		Boolean sampleExists = dto.getSampleId() != null && StringUtils.isNotEmpty(dto.getSampleId().getPrimaryId()) || StringUtils.isNotEmpty(dto.getSampleTitle());

		if (!sampleExists) {
			htpSampleAnnotationResponse.addErrorMessage("SampleId or Sample Title", ValidationConstants.REQUIRED_MESSAGE);
		}
		
		if (dto.getSampleId() != null && StringUtils.isNotBlank(dto.getSampleId().getPrimaryId())) {
			ExternalDataBaseEntity externalDbEntity = externalDataBaseEntityFmsDtoValidator.validateExternalDataBaseEntityFmsDTO(dto.getSampleId());
			if (externalDbEntity != null) {
				Long htpSampleId = externalDbEntity.getId();
				Map<String, Object> params = new HashMap<>();
				params.put("htpExpressionSample.id", htpSampleId);
				SearchResponse<HTPExpressionDatasetSampleAnnotation> searchResponse = htpExpressionDatasetSampleAnnotationDAO.findByParams(params);
				if (searchResponse == null || searchResponse.getSingleResult() == null) {
					htpSampleAnnotation = new HTPExpressionDatasetSampleAnnotation();
					htpSampleAnnotation.setHtpExpressionSample(externalDbEntity);
				} else {
					htpSampleAnnotation = searchResponse.getSingleResult();
				}
			} else {
				htpSampleAnnotation = new HTPExpressionDatasetSampleAnnotation();
			}
		} else {
			htpSampleAnnotation = new HTPExpressionDatasetSampleAnnotation();
		}

		if (StringUtils.isNotEmpty(dto.getSampleTitle())) {
			htpSampleAnnotation.setHtpExpressionSampleTitle(dto.getSampleTitle());
		}

		if (StringUtils.isNotEmpty(dto.getAbundance())) {
			htpSampleAnnotation.setAbundance(dto.getAbundance());
		}
		
		if (StringUtils.isNotEmpty(dto.getSampleType())) {
			String curie = dto.getSampleType();
			OBITerm obiTerm = obiTermService.findByCurie(curie);
			if (obiTerm != null) {
				htpSampleAnnotation.setHtpExpressionSampleType(obiTerm);
			} else {
				htpSampleAnnotationResponse.addErrorMessage("SampleType", ValidationConstants.INVALID_MESSAGE + " (" + curie + ")");
			}
		} else {
			htpSampleAnnotationResponse.addErrorMessage("SampleType", ValidationConstants.REQUIRED_MESSAGE);
		}

		if (dto.getSampleAge() != null) {
			if (htpSampleAnnotation.getHtpExpressionSampleAge() == null) {
				htpSampleAnnotation.setHtpExpressionSampleAge(new BioSampleAge());
			}
			ObjectResponse<TemporalContext> temporalContextObjectResponse = geneExpressionAnnotationFmsDTOValidator.validateTemporalContext(dto.getSampleAge().getStage());
			if (temporalContextObjectResponse.hasErrors()) {
				htpSampleAnnotationResponse.addErrorMessage("Sample Age - Stage", temporalContextObjectResponse.errorMessagesString());
			} else {
				TemporalContext temporalContext = geneExpressionAnnotationFmsDTOValidator.updateTemporalContext(temporalContextObjectResponse, htpSampleAnnotation.getHtpExpressionSampleAge().getStage());
				htpSampleAnnotation.getHtpExpressionSampleAge().setAge(dto.getSampleAge().getAge());
				htpSampleAnnotation.getHtpExpressionSampleAge().setWhenExpressedStageName(dto.getSampleAge().getStage().getStageName());
				htpSampleAnnotation.getHtpExpressionSampleAge().setStage(temporalContext);
			}
		}

		List<Long> idsToRemove = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(dto.getSampleLocations())) {
			if (CollectionUtils.isNotEmpty(htpSampleAnnotation.getHtpExpressionSampleLocations())) {
				for (AnatomicalSite anatomicalSite : htpSampleAnnotation.getHtpExpressionSampleLocations()) {
					idsToRemove.add(anatomicalSite.getId());
				}
			}
			List<AnatomicalSite> htpSampleLocations = new ArrayList<>();
			for (WhereExpressedFmsDTO whereExpressedDTO : dto.getSampleLocations()) {
				ObjectResponse<AnatomicalSite> anatomicalSiteObjectResponse = geneExpressionAnnotationFmsDTOValidator.validateAnatomicalSite(whereExpressedDTO);
				if (anatomicalSiteObjectResponse.hasErrors()) {
					htpSampleAnnotationResponse.addErrorMessage("SampleLocations", anatomicalSiteObjectResponse.errorMessagesString());
				} else {
					htpSampleLocations.add(anatomicalSiteObjectResponse.getEntity());
				}
			}
			htpSampleAnnotation.setHtpExpressionSampleLocations(htpSampleLocations);
		}

		if (dto.getGenomicInformation() != null) {
			Boolean genomicInformationExists = StringUtils.isNotEmpty(dto.getGenomicInformation().getBioSampleText()) || StringUtils.isNotEmpty(dto.getGenomicInformation().getBiosampleId());
			if (genomicInformationExists) {
				if (htpSampleAnnotation.getGenomicInformation() == null) {
					htpSampleAnnotation.setGenomicInformation(new BioSampleGenomicInformation());
					if (StringUtils.isNotEmpty(dto.getGenomicInformation().getBiosampleId())) {
						validateGenomicInformation(dto.getGenomicInformation(), htpSampleAnnotation, htpSampleAnnotationResponse);
					}
					if (StringUtils.isNotEmpty(dto.getGenomicInformation().getBioSampleText())) {
						htpSampleAnnotation.getGenomicInformation().setBioSampleText(dto.getGenomicInformation().getBioSampleText());
					}
				} else {
					if (StringUtils.isNotEmpty(dto.getGenomicInformation().getBiosampleId())) {
						String identifierString = null;
						if (htpSampleAnnotation.getGenomicInformation().getBioSampleAgm() != null) {
							identifierString = htpSampleAnnotation.getGenomicInformation().getBioSampleAgm().getIdentifier();
						} else if (htpSampleAnnotation.getGenomicInformation().getBioSampleAllele() != null) {
							identifierString = htpSampleAnnotation.getGenomicInformation().getBioSampleAllele().getIdentifier();
						}
						if (!identifierString.equals(dto.getGenomicInformation().getBiosampleId()) || htpSampleAnnotation.getGenomicInformation().getBioSampleAgmType() == null && StringUtils.isNotEmpty(dto.getGenomicInformation().getIdType())) {
							validateGenomicInformation(dto.getGenomicInformation(), htpSampleAnnotation, htpSampleAnnotationResponse);
						}
						if (StringUtils.isNotEmpty(dto.getGenomicInformation().getBioSampleText())) {
							htpSampleAnnotation.getGenomicInformation().setBioSampleText(dto.getGenomicInformation().getBioSampleText());
						}
					}
				}
			} else {
				htpSampleAnnotationResponse.addErrorMessage("GenomicInformation - BioSampleId or BioSampleText", ValidationConstants.REQUIRED_MESSAGE);
			}
		}

		if (StringUtils.isNotEmpty(dto.getSex())) {
			Map<String, Object> params = new HashMap<>();
			params.put("name", dto.getSex());
			params.put("query_operator", "or");
			params.put("synonyms", dto.getSex());
			SearchResponse<VocabularyTerm> searchResponse = vocabularyTermService.findByParams(new Pagination(), params);
			boolean added = false;
			if (searchResponse.getTotalResults() > 0) {
				for (VocabularyTerm tag : searchResponse.getResults()) {
					if (tag.getVocabulary().getVocabularyLabel().equals("genetic_sex") && (tag.getName().equals(dto.getSex()) || tag.getSynonyms().contains(dto.getSex()))) {
						htpSampleAnnotation.setGeneticSex(tag);
						added = true;
					}
				}
			}
			if (!added) {
				htpSampleAnnotationResponse.addErrorMessage("Sex", ValidationConstants.INVALID_MESSAGE + " (" + dto.getSex() + ")");
			}
		}

		if (StringUtils.isNotEmpty(dto.getAssayType())) {
			String curie = dto.getAssayType();
			MMOTerm mmoTerm = mmoTermService.findByCurie(curie);
			if (mmoTerm != null) {
				htpSampleAnnotation.setExpressionAssayUsed(mmoTerm);
			} else {
				htpSampleAnnotationResponse.addErrorMessage("AssayType", ValidationConstants.INVALID_MESSAGE + " (" + curie + ")");
			}
		} else {
			htpSampleAnnotationResponse.addErrorMessage("AssayType", ValidationConstants.REQUIRED_MESSAGE);
		}

		if (CollectionUtils.isNotEmpty(dto.getAssemblyVersions())) {
			List<String> assemblyVersions = new ArrayList<>();
			for (String assemblyVersion : dto.getAssemblyVersions()) {
				assemblyVersions.add(assemblyVersion);
			}
			htpSampleAnnotation.setAssemblyVersions(assemblyVersions);
		}

		if (CollectionUtils.isNotEmpty(dto.getDatasetIds())) {
			List<ExternalDataBaseEntity> datasetIds = new ArrayList<>();
			for (String datasetId : dto.getDatasetIds()) {
				ExternalDataBaseEntity externalDbEntity = externalDataBaseEntityService.findByCurie(datasetId);
				if (externalDbEntity != null) {
					datasetIds.add(externalDbEntity);
				}
			}
			htpSampleAnnotation.setDatasetIds(datasetIds);
		} else {
			htpSampleAnnotationResponse.addErrorMessage("DatasetIds", ValidationConstants.REQUIRED_MESSAGE);
		}

		if (StringUtils.isNotEmpty(dto.getSequencingFormat())) {
			VocabularyTerm sequencingFormat = vocabularyTermService.getTermInVocabulary(VocabularyConstants.HTP_DATASET_SAMPLE_SEQUENCE_FORMAT_VOCABULARY, dto.getSequencingFormat()).getEntity();
			if (sequencingFormat != null) {
				htpSampleAnnotation.setSequencingFormat(sequencingFormat);
			}
		}

		if (StringUtils.isNotEmpty(dto.getTaxonId())) {
			ObjectResponse<NCBITaxonTerm> taxonResponse = ncbiTaxonTermService.getByCurie(dto.getTaxonId());
			if (taxonResponse.getEntity() == null || backendBulkDataProvider != null && (backendBulkDataProvider.name().equals("RGD") || backendBulkDataProvider.name().equals("HUMAN")) && !taxonResponse.getEntity().getCurie().equals(backendBulkDataProvider.canonicalTaxonCurie)) {
				htpSampleAnnotationResponse.addErrorMessage("taxonId", ValidationConstants.INVALID_MESSAGE + " (" + dto.getTaxonId() + ") for " + backendBulkDataProvider.name() + " load");
			}
			htpSampleAnnotation.setTaxon(taxonResponse.getEntity());
		}

		if (dto.getMicroarraySampleDetails() != null) {
			if (htpSampleAnnotation.getMicroarraySampleDetails() == null && (dto.getMicroarraySampleDetails().getChannelId() != null || dto.getMicroarraySampleDetails().getChannelNum() != null)) {
				htpSampleAnnotation.setMicroarraySampleDetails(new MicroarraySampleDetails());
			}
			if (StringUtils.isNotEmpty(dto.getMicroarraySampleDetails().getChannelId())) {
				htpSampleAnnotation.getMicroarraySampleDetails().setChannelId(dto.getMicroarraySampleDetails().getChannelId());
			}
			if (dto.getMicroarraySampleDetails().getChannelNum() != null) {
				htpSampleAnnotation.getMicroarraySampleDetails().setChannelNumber(dto.getMicroarraySampleDetails().getChannelNum());
			}
		}

		if (StringUtils.isNotEmpty(dto.getNotes())) {
			List<Note> relatedNotes = new ArrayList<>();
			Note relatedNote = new Note();
			relatedNote.setFreeText(dto.getNotes());
			relatedNote.setNoteType(vocabularyTermService.getTermInVocabularyTermSet(VocabularyConstants.HTP_DATASET_SAMPLE_NOTE_TYPE_VOCABULARY_TERM_SET, "htp_expression_dataset_sample_note_type").getEntity());
			relatedNotes.add(relatedNote);
			htpSampleAnnotation.setRelatedNotes(relatedNotes);
		}
		
		htpSampleAnnotation.setDataProvider(organizationService.getByAbbr(backendBulkDataProvider.sourceOrganization).getEntity());

		if (htpSampleAnnotationResponse.hasErrors()) {
			throw new ObjectValidationException(dto, htpSampleAnnotationResponse.errorMessagesString());
		}

		HTPExpressionDatasetSampleAnnotation htp = htpExpressionDatasetSampleAnnotationDAO.persist(htpSampleAnnotation);
		for (Long id : idsToRemove) {
			anatomicalSiteDAO.remove(id);
		}
		return htp;
	}

	protected void validateGenomicInformation(BioSampleGenomicInformationFmsDTO dto, HTPExpressionDatasetSampleAnnotation htpSampleAnnotation, ObjectResponse<HTPExpressionDatasetSampleAnnotation> htpSampleAnnotationResponse) {
		if (StringUtils.isNotEmpty(dto.getBiosampleId())) {
			String identifierString = dto.getBiosampleId();
			Allele allele = alleleService.findByIdentifierString(identifierString);
			if (allele != null) {
				htpSampleAnnotation.getGenomicInformation().setBioSampleAllele(allele);
			} else {
				AffectedGenomicModel agm = affectedGenomicModelService.findByIdentifierString(identifierString);
				if (agm == null) {
					htpSampleAnnotationResponse.addErrorMessage("GenomicInformation - BioSampleId", ValidationConstants.INVALID_MESSAGE + " (" + identifierString + ")");
				} else {
					htpSampleAnnotation.getGenomicInformation().setBioSampleAgm(agm);
					VocabularyTerm agmType = vocabularyTermService.getTermInVocabulary(VocabularyConstants.AGM_SUBTYPE_VOCABULARY, dto.getIdType()).getEntity();
					if (agmType != null) {
						htpSampleAnnotation.getGenomicInformation().setBioSampleAgmType(agmType);
					} else {
						htpSampleAnnotationResponse.addErrorMessage("GenomicInformation - IdType", ValidationConstants.INVALID_MESSAGE + " (" + dto.getIdType() + ")");
					}
				}
			}
		}
	}
}
