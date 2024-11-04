package org.alliancegenome.curation_api.services.validation.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.constants.Gff3Constants;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.CodingSequenceDAO;
import org.alliancegenome.curation_api.dao.ExonDAO;
import org.alliancegenome.curation_api.dao.TranscriptDAO;
import org.alliancegenome.curation_api.dao.associations.codingSequenceAssociations.CodingSequenceGenomicLocationAssociationDAO;
import org.alliancegenome.curation_api.dao.associations.exonAssociations.ExonGenomicLocationAssociationDAO;
import org.alliancegenome.curation_api.dao.associations.transcriptAssociations.TranscriptCodingSequenceAssociationDAO;
import org.alliancegenome.curation_api.dao.associations.transcriptAssociations.TranscriptExonAssociationDAO;
import org.alliancegenome.curation_api.dao.associations.transcriptAssociations.TranscriptGeneAssociationDAO;
import org.alliancegenome.curation_api.dao.associations.transcriptAssociations.TranscriptGenomicLocationAssociationDAO;
import org.alliancegenome.curation_api.dao.ontology.SoTermDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.AssemblyComponent;
import org.alliancegenome.curation_api.model.entities.CodingSequence;
import org.alliancegenome.curation_api.model.entities.Exon;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.GenomicEntity;
import org.alliancegenome.curation_api.model.entities.LocationAssociation;
import org.alliancegenome.curation_api.model.entities.Transcript;
import org.alliancegenome.curation_api.model.entities.associations.codingSequenceAssociations.CodingSequenceGenomicLocationAssociation;
import org.alliancegenome.curation_api.model.entities.associations.exonAssociations.ExonGenomicLocationAssociation;
import org.alliancegenome.curation_api.model.entities.associations.transcriptAssociations.TranscriptCodingSequenceAssociation;
import org.alliancegenome.curation_api.model.entities.associations.transcriptAssociations.TranscriptExonAssociation;
import org.alliancegenome.curation_api.model.entities.associations.transcriptAssociations.TranscriptGeneAssociation;
import org.alliancegenome.curation_api.model.entities.associations.transcriptAssociations.TranscriptGenomicLocationAssociation;
import org.alliancegenome.curation_api.model.entities.ontology.SOTerm;
import org.alliancegenome.curation_api.model.ingest.dto.fms.Gff3DTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.AssemblyComponentService;
import org.alliancegenome.curation_api.services.DataProviderService;
import org.alliancegenome.curation_api.services.GeneService;
import org.alliancegenome.curation_api.services.Gff3Service;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.alliancegenome.curation_api.services.helpers.gff3.Gff3UniqueIdHelper;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class Gff3DtoValidator {

	@Inject ExonDAO exonDAO;
	@Inject TranscriptDAO transcriptDAO;
	@Inject CodingSequenceDAO codingSequenceDAO;
	@Inject GeneService geneService;
	@Inject ExonGenomicLocationAssociationDAO exonLocationDAO;
	@Inject TranscriptGenomicLocationAssociationDAO transcriptLocationDAO;
	@Inject TranscriptGeneAssociationDAO transcriptGeneDAO;
	@Inject TranscriptExonAssociationDAO transcriptExonDAO;
	@Inject TranscriptCodingSequenceAssociationDAO transcriptCdsDAO;
	@Inject CodingSequenceGenomicLocationAssociationDAO cdsLocationDAO;
	@Inject AssemblyComponentService assemblyComponentService;
	@Inject DataProviderService dataProviderService;
	@Inject NcbiTaxonTermService ncbiTaxonTermService;
	@Inject SoTermDAO soTermDAO;
	@Inject Gff3Service gff3Service;
	@Inject VocabularyTermService vocabularyTermService;
	
	@Transactional
	public void validateExonEntry(Gff3DTO dto, Map<String, String> attributes, List<Long> idsAdded, BackendBulkDataProvider dataProvider) throws ValidationException {

		Exon exon = null;

		if (!StringUtils.equals(dto.getType(), "exon") && !StringUtils.equals(dto.getType(), "noncoding_exon")) {
			throw new ObjectValidationException(dto, "Invalid Type: " + dto.getType() + " for Exon Entity");
		}
		
		String uniqueId = Gff3UniqueIdHelper.getExonOrCodingSequenceUniqueId(dto, attributes, dataProvider);
		SearchResponse<Exon> searchResponse = exonDAO.findByField("uniqueId", uniqueId);
		if (searchResponse != null && searchResponse.getSingleResult() != null) {
			exon = searchResponse.getSingleResult();
		} else {
			exon = new Exon();
			exon.setUniqueId(uniqueId);
		}
		
		if (attributes.containsKey("Name")) {
			exon.setName(attributes.get("Name"));
		}
		
		ObjectResponse<Exon> exonResponse = validateGenomicEntity(exon, dto, attributes, dataProvider);
	
		if (exonResponse.hasErrors()) {
			throw new ObjectValidationException(dto, exonResponse.errorMessagesString());
		}
		
		exon = exonDAO.persist(exonResponse.getEntity());
		if (exon != null) {
			idsAdded.add(exon.getId());
		}
	}
	
	@Transactional
	public void validateCdsEntry(Gff3DTO dto, Map<String, String> attributes, List<Long> idsAdded, BackendBulkDataProvider dataProvider) throws ValidationException {

		CodingSequence cds = null;
		
		if (!StringUtils.equals(dto.getType(), "CDS")) {
			throw new ObjectValidationException(dto, "Invalid Type: " + dto.getType() + " for CDS Entity");
		}
		
		String uniqueId = Gff3UniqueIdHelper.getExonOrCodingSequenceUniqueId(dto, attributes, dataProvider);
		SearchResponse<CodingSequence> searchResponse = codingSequenceDAO.findByField("uniqueId", uniqueId);
		if (searchResponse != null && searchResponse.getSingleResult() != null) {
			cds = searchResponse.getSingleResult();
		} else {
			cds = new CodingSequence();
			cds.setUniqueId(uniqueId);
		}
		
		if (attributes.containsKey("Name")) {
			cds.setName(attributes.get("Name"));
		}
		
		ObjectResponse<CodingSequence> cdsResponse = validateGenomicEntity(cds, dto, attributes, dataProvider);
	
		if (cdsResponse.hasErrors()) {
			throw new ObjectValidationException(dto, cdsResponse.errorMessagesString());
		}
		
		cds = codingSequenceDAO.persist(cdsResponse.getEntity());
		if (cds != null) {
			idsAdded.add(cds.getId());
		}
	}
	
	@Transactional
	public void validateTranscriptEntry(Gff3DTO dto, Map<String, String> attributes, List<Long> idsAdded, BackendBulkDataProvider dataProvider) throws ValidationException {

		if (!Gff3Constants.TRANSCRIPT_TYPES.contains(dto.getType())) {
			throw new ObjectValidationException(dto, "Invalid Type: " + dto.getType() + " for Transcript Entity");
		}

		Transcript transcript = null;
		if (attributes.containsKey("ID")) {
			SearchResponse<Transcript> searchResponse = transcriptDAO.findByField("modInternalId", attributes.get("ID"));
			if (searchResponse != null && searchResponse.getSingleResult() != null) {
				transcript = searchResponse.getSingleResult();
			} else {
				transcript = new Transcript();
				transcript.setModInternalId(attributes.get("ID"));
			}
		}
		
		SearchResponse<SOTerm> soResponse = soTermDAO.findByField("name", dto.getType());
		if (soResponse != null) {
			transcript.setTranscriptType(soResponse.getSingleResult());
		}
		
		if (attributes.containsKey("Name")) {
			transcript.setName(attributes.get("Name"));
		}
		
		ObjectResponse<Transcript> transcriptResponse = validateGenomicEntity(transcript, dto, attributes, dataProvider);
		if (!attributes.containsKey("ID")) {
			transcriptResponse.addErrorMessage("attributes - ID", ValidationConstants.REQUIRED_MESSAGE);
		}
		
		if (attributes.containsKey("transcript_id")) {
			transcript.setTranscriptId(attributes.get("transcript_id"));
		} else {
			transcriptResponse.addErrorMessage("attributes - transcript_id", ValidationConstants.REQUIRED_MESSAGE);
		}
		
		if (transcriptResponse.hasErrors()) {
			throw new ObjectValidationException(dto, transcriptResponse.errorMessagesString());
		}
		
		transcript = transcriptDAO.persist(transcriptResponse.getEntity());
		if (transcript != null) {
			idsAdded.add(transcript.getId());
		}
	}
	
	private <E extends GenomicEntity> ObjectResponse<E> validateGenomicEntity(E entity, Gff3DTO dto, Map<String, String> attributes, BackendBulkDataProvider dataProvider) {
		ObjectResponse<E> geResponse = new ObjectResponse<E>();
		
		entity.setDataProvider(dataProviderService.getDefaultDataProvider(dataProvider.sourceOrganization));
		entity.setTaxon(ncbiTaxonTermService.getByCurie(dataProvider.canonicalTaxonCurie).getEntity());
		
		geResponse.setEntity(entity);
		
		return geResponse;
	}

	@Transactional
	public CodingSequenceGenomicLocationAssociation validateCdsLocation(Gff3DTO gffEntry, CodingSequence cds, String assemblyId, BackendBulkDataProvider dataProvider) throws ObjectValidationException {
		AssemblyComponent assemblyComponent = null;
		CodingSequenceGenomicLocationAssociation locationAssociation = new CodingSequenceGenomicLocationAssociation();
		if (StringUtils.isNotBlank(gffEntry.getSeqId())) {
			assemblyComponent = assemblyComponentService.fetchOrCreate(gffEntry.getSeqId(), assemblyId, dataProvider.canonicalTaxonCurie, dataProvider);
			Map<String, Object> params = new HashMap<>();
			params.put(EntityFieldConstants.CODING_SEQUENCE_ASSOCIATION_SUBJECT + ".id", cds.getId());
			params.put(EntityFieldConstants.CODING_SEQUENCE_GENOMIC_LOCATION_ASSOCIATION_OBJECT + ".name", assemblyComponent.getName());
			params.put(EntityFieldConstants.CODING_SEQUENCE_GENOMIC_LOCATION_ASSOCIATION_OBJECT_ASSEMBLY, assemblyId);
			SearchResponse<CodingSequenceGenomicLocationAssociation> locationSearchResponse = cdsLocationDAO.findByParams(params);
			if (locationSearchResponse != null && locationSearchResponse.getSingleResult() != null) {
				locationAssociation = locationSearchResponse.getSingleResult();
			}
			locationAssociation.setCodingSequenceGenomicLocationAssociationObject(assemblyComponent);
		}
		locationAssociation.setCodingSequenceAssociationSubject(cds);
		locationAssociation.setStrand(gffEntry.getStrand());
		locationAssociation.setPhase(gffEntry.getPhase());
		
		ObjectResponse<CodingSequenceGenomicLocationAssociation> locationResponse = validateLocationAssociation(locationAssociation, gffEntry, assemblyComponent);
		if (locationResponse.hasErrors()) {
			throw new ObjectValidationException(gffEntry, locationResponse.errorMessagesString());
		}
		
		return cdsLocationDAO.persist(locationResponse.getEntity());
	}

	@Transactional
	public ExonGenomicLocationAssociation validateExonLocation(Gff3DTO gffEntry, Exon exon, String assemblyId, BackendBulkDataProvider dataProvider) throws ObjectValidationException {
		AssemblyComponent assemblyComponent = null;
		ExonGenomicLocationAssociation locationAssociation = new ExonGenomicLocationAssociation();
		if (StringUtils.isNotBlank(gffEntry.getSeqId())) {
			assemblyComponent = assemblyComponentService.fetchOrCreate(gffEntry.getSeqId(), assemblyId, dataProvider.canonicalTaxonCurie, dataProvider);
			Map<String, Object> params = new HashMap<>();
			params.put(EntityFieldConstants.EXON_ASSOCIATION_SUBJECT + ".id", exon.getId());
			params.put(EntityFieldConstants.EXON_GENOMIC_LOCATION_ASSOCIATION_OBJECT + ".name", assemblyComponent.getName());
			params.put(EntityFieldConstants.EXON_GENOMIC_LOCATION_ASSOCIATION_OBJECT_ASSEMBLY, assemblyId);
			SearchResponse<ExonGenomicLocationAssociation> locationSearchResponse = exonLocationDAO.findByParams(params);
			if (locationSearchResponse != null && locationSearchResponse.getSingleResult() != null) {
				locationAssociation = locationSearchResponse.getSingleResult();
			}
			locationAssociation.setExonGenomicLocationAssociationObject(assemblyComponent);
		}
		locationAssociation.setExonAssociationSubject(exon);
		locationAssociation.setStrand(gffEntry.getStrand());
		
		ObjectResponse<ExonGenomicLocationAssociation> locationResponse = validateLocationAssociation(locationAssociation, gffEntry, assemblyComponent);
		if (locationResponse.hasErrors()) {
			throw new ObjectValidationException(gffEntry, locationResponse.errorMessagesString());
		}
		
		return exonLocationDAO.persist(locationResponse.getEntity());
	}

	@Transactional
	public TranscriptGenomicLocationAssociation validateTranscriptLocation(Gff3DTO gffEntry, Transcript transcript, String assemblyId, BackendBulkDataProvider dataProvider) throws ObjectValidationException {
		AssemblyComponent assemblyComponent = null;
		TranscriptGenomicLocationAssociation locationAssociation = new TranscriptGenomicLocationAssociation();
		if (StringUtils.isNotBlank(gffEntry.getSeqId())) {
			assemblyComponent = assemblyComponentService.fetchOrCreate(gffEntry.getSeqId(), assemblyId, dataProvider.canonicalTaxonCurie, dataProvider);
			Map<String, Object> params = new HashMap<>();
			params.put(EntityFieldConstants.TRANSCRIPT_ASSOCIATION_SUBJECT + ".id", transcript.getId());
			params.put(EntityFieldConstants.TRANSCRIPT_GENOMIC_LOCATION_ASSOCIATION_OBJECT_ASSEMBLY, assemblyId);
			SearchResponse<TranscriptGenomicLocationAssociation> locationSearchResponse = transcriptLocationDAO.findByParams(params);
			if (locationSearchResponse != null && locationSearchResponse.getSingleResult() != null) {
				locationAssociation = locationSearchResponse.getSingleResult();
			}
			locationAssociation.setTranscriptGenomicLocationAssociationObject(assemblyComponent);
		}
		locationAssociation.setTranscriptAssociationSubject(transcript);
		locationAssociation.setStrand(gffEntry.getStrand());
		locationAssociation.setPhase(gffEntry.getPhase());
		
		ObjectResponse<TranscriptGenomicLocationAssociation> locationResponse = validateLocationAssociation(locationAssociation, gffEntry, assemblyComponent);
		if (locationResponse.hasErrors()) {
			throw new ObjectValidationException(gffEntry, locationResponse.errorMessagesString());
		}
		
		return transcriptLocationDAO.persist(locationResponse.getEntity());
	}
	
	@Transactional
	public TranscriptGeneAssociation validateTranscriptGeneAssociation(Gff3DTO gffEntry, Transcript transcript, Map<String, String> attributes, Map<String, String> geneIdCurieMap) throws ObjectValidationException {
		TranscriptGeneAssociation association = new TranscriptGeneAssociation();
		boolean newAssociation = true;
		ObjectResponse<TranscriptGeneAssociation> associationResponse = new ObjectResponse<TranscriptGeneAssociation>();
		if (!attributes.containsKey("Parent")) {
			associationResponse.addErrorMessage("Attributes - Parent", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			String geneCurie = geneIdCurieMap.containsKey(attributes.get("Parent")) ? geneIdCurieMap.get(attributes.get("Parent")) : attributes.get("Parent");
			Gene parentGene = geneService.findByIdentifierString(geneCurie);
			if (parentGene == null) {
				associationResponse.addErrorMessage("Attributes - Parent", ValidationConstants.INVALID_MESSAGE + " (" + attributes.get("Parent") + ")");
			} else {
				Map<String, Object> params = new HashMap<>();
				params.put(EntityFieldConstants.TRANSCRIPT_ASSOCIATION_SUBJECT + ".id", transcript.getId());
				params.put("transcriptGeneAssociationObject.id", parentGene.getId());
				SearchResponse<TranscriptGeneAssociation> searchResponse = transcriptGeneDAO.findByParams(params);
				if (searchResponse != null && searchResponse.getSingleResult() != null) {
					association = searchResponse.getSingleResult();
					newAssociation = false;
				}
				association.setTranscriptGeneAssociationObject(parentGene);
			}
		}
		association.setTranscriptAssociationSubject(transcript);
		association.setRelation(vocabularyTermService.getTermInVocabulary(VocabularyConstants.TRANSCRIPT_RELATION_VOCABULARY, VocabularyConstants.TRANSCRIPT_CHILD_TERM).getEntity());
		
		if (associationResponse.hasErrors()) {
			throw new ObjectValidationException(gffEntry, associationResponse.errorMessagesString());
		}

		if (newAssociation) {
			return transcriptGeneDAO.persist(association);
		} else {
			return association;
		}
	}
	
	@Transactional
	public TranscriptCodingSequenceAssociation validateTranscriptCodingSequenceAssociation(Gff3DTO gffEntry, CodingSequence cds, Map<String, String> attributes) throws ObjectValidationException {
		TranscriptCodingSequenceAssociation association = new TranscriptCodingSequenceAssociation();
		
		ObjectResponse<TranscriptCodingSequenceAssociation> associationResponse = new ObjectResponse<TranscriptCodingSequenceAssociation>();
		if (!attributes.containsKey("Parent")) {
			associationResponse.addErrorMessage("Attributes - Parent", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			SearchResponse<Transcript> parentTranscriptSearch = transcriptDAO.findByField("modInternalId", attributes.get("Parent"));
			if (parentTranscriptSearch == null || parentTranscriptSearch.getSingleResult() == null) {
				associationResponse.addErrorMessage("Attributes - Parent", ValidationConstants.INVALID_MESSAGE + " (" + attributes.get("Parent") + ")");
			} else {
				Transcript parentTranscript = parentTranscriptSearch.getSingleResult();
				Map<String, Object> params = new HashMap<>();
				params.put(EntityFieldConstants.TRANSCRIPT_ASSOCIATION_SUBJECT + ".id", parentTranscript.getId());
				params.put("transcriptCodingSequenceAssociationObject.id", cds.getId());
				SearchResponse<TranscriptCodingSequenceAssociation> searchResponse = transcriptCdsDAO.findByParams(params);
				if (searchResponse != null && searchResponse.getSingleResult() != null) {
					association = searchResponse.getSingleResult();
				}
				association.setTranscriptAssociationSubject(parentTranscript);
			}
		}
		association.setTranscriptCodingSequenceAssociationObject(cds);
		association.setRelation(vocabularyTermService.getTermInVocabulary(VocabularyConstants.TRANSCRIPT_RELATION_VOCABULARY, VocabularyConstants.TRANSCRIPT_PARENT_TERM).getEntity());
		
		if (associationResponse.hasErrors()) {
			throw new ObjectValidationException(gffEntry, associationResponse.errorMessagesString());
		}

		return transcriptCdsDAO.persist(association);
	}
	
	@Transactional
	public TranscriptExonAssociation validateTranscriptExonAssociation(Gff3DTO gffEntry, Exon exon, Map<String, String> attributes) throws ObjectValidationException {
		TranscriptExonAssociation association = new TranscriptExonAssociation();
		
		ObjectResponse<TranscriptExonAssociation> associationResponse = new ObjectResponse<TranscriptExonAssociation>();
		if (!attributes.containsKey("Parent")) {
			associationResponse.addErrorMessage("Attributes - Parent", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			SearchResponse<Transcript> parentTranscriptSearch = transcriptDAO.findByField("modInternalId", attributes.get("Parent"));
			if (parentTranscriptSearch == null || parentTranscriptSearch.getSingleResult() == null) {
				associationResponse.addErrorMessage("Attributes - Parent", ValidationConstants.INVALID_MESSAGE + " (" + attributes.get("Parent") + ")");
			} else {
				Transcript parentTranscript = parentTranscriptSearch.getSingleResult();
				Map<String, Object> params = new HashMap<>();
				params.put(EntityFieldConstants.TRANSCRIPT_ASSOCIATION_SUBJECT + ".id", parentTranscript.getId());
				params.put("transcriptExonAssociationObject.id", exon.getId());
				SearchResponse<TranscriptExonAssociation> searchResponse = transcriptExonDAO.findByParams(params);
				if (searchResponse != null && searchResponse.getSingleResult() != null) {
					association = searchResponse.getSingleResult();
				}
				association.setTranscriptAssociationSubject(parentTranscript);
			}
		}
		association.setTranscriptExonAssociationObject(exon);
		association.setRelation(vocabularyTermService.getTermInVocabulary(VocabularyConstants.TRANSCRIPT_RELATION_VOCABULARY, VocabularyConstants.TRANSCRIPT_PARENT_TERM).getEntity());
		
		if (associationResponse.hasErrors()) {
			throw new ObjectValidationException(gffEntry, associationResponse.errorMessagesString());
		}

		return transcriptExonDAO.persist(association);
	}
	
	private <E extends LocationAssociation> ObjectResponse<E> validateLocationAssociation(E association, Gff3DTO dto, AssemblyComponent assemblyComponent) {
		ObjectResponse<E> associationResponse = new ObjectResponse<E>();
		
		association.setRelation(vocabularyTermService.getTermInVocabulary(VocabularyConstants.LOCATION_ASSOCIATION_RELATION_VOCABULARY, "located_on").getEntity());
		
		if (assemblyComponent == null) {
			associationResponse.addErrorMessage("SeqId", ValidationConstants.REQUIRED_MESSAGE);
		}
		
		if (dto.getStart() == null) {
			associationResponse.addErrorMessage("Start", ValidationConstants.REQUIRED_MESSAGE);
		}
		association.setStart(dto.getStart());
		
		if (dto.getEnd() == null) {
			associationResponse.addErrorMessage("End", ValidationConstants.REQUIRED_MESSAGE);
		}
		association.setEnd(dto.getEnd());
		
		if (StringUtils.isBlank(dto.getStrand())) {
			associationResponse.addErrorMessage("Strand", ValidationConstants.REQUIRED_MESSAGE);
		} else if (!Gff3Constants.STRANDS.contains(dto.getStrand())) {
			associationResponse.addErrorMessage("Strand", ValidationConstants.INVALID_MESSAGE + " (" + dto.getStrand() + ")");
		}
		
		if (dto.getPhase() != null && (dto.getPhase() > 2 || dto.getPhase() < 0)) {
			associationResponse.addErrorMessage("Phase", ValidationConstants.INVALID_MESSAGE + " (" + dto.getPhase() + ")");
		}
		
		associationResponse.setEntity(association);
		
		return associationResponse;
	}

}
