package org.alliancegenome.curation_api.services.helpers.slotAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.model.entities.InformationContentEntity;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.SOTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.NameSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.SecondaryIdSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.SlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleFunctionalImpactSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleGermlineTransmissionStatusSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleInheritanceModeSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleMutationTypeSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleNomenclatureEventSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.constructSlotAnnotations.ConstructComponentSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.NoteDTO;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.NameSlotAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.SecondaryIdSlotAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.SlotAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.alleleSlotAnnotations.AlleleFunctionalImpactSlotAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.alleleSlotAnnotations.AlleleGermlineTransmissionStatusSlotAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.alleleSlotAnnotations.AlleleInheritanceModeSlotAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.alleleSlotAnnotations.AlleleMutationTypeSlotAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.alleleSlotAnnotations.AlleleNomenclatureEventSlotAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.constructSlotAnnotations.ConstructComponentSlotAnnotationDTO;
import org.alliancegenome.curation_api.services.ReferenceService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class SlotAnnotationIdentityHelper {

	@Inject ReferenceService refService;

	public static String alleleMutationTypeIdentity(AlleleMutationTypeSlotAnnotation annotation) {
		String identity = "";
		if (CollectionUtils.isNotEmpty(annotation.getMutationTypes())) {
			List<String> mutationTypeCuries = annotation.getMutationTypes().stream().map(SOTerm::getCurie).collect(Collectors.toList());
			Collections.sort(mutationTypeCuries);
			identity = StringUtils.join(mutationTypeCuries, ":");
		}
		return identity + "|" + slotAnnotationIdentity(annotation);
	}

	public String alleleMutationTypeDtoIdentity(AlleleMutationTypeSlotAnnotationDTO dto) {
		String identity = "";
		List<String> mutationTypeCuries = dto.getMutationTypeCuries();
		if (CollectionUtils.isNotEmpty(mutationTypeCuries)) {
			Collections.sort(mutationTypeCuries);
			identity = StringUtils.join(mutationTypeCuries, ":");
		}

		return identity + "|" + slotAnnotationDtoIdentity(dto);
	}

	public static String alleleGermlineTransmissionStatusIdentity(AlleleGermlineTransmissionStatusSlotAnnotation annotation) {
		String gts = annotation.getGermlineTransmissionStatus() == null ? "" : annotation.getGermlineTransmissionStatus().getName();

		return StringUtils.join(List.of(gts, slotAnnotationIdentity(annotation)), "|");
	}

	public String alleleGermlineTransmissionStatusDtoIdentity(AlleleGermlineTransmissionStatusSlotAnnotationDTO dto) {
		String gts = StringUtils.isBlank(dto.getGermlineTransmissionStatusName()) ? "" : dto.getGermlineTransmissionStatusName();

		return StringUtils.join(List.of(gts, slotAnnotationDtoIdentity(dto)), "|");
	}

	public static String alleleNomenclatureEventIdentity(AlleleNomenclatureEventSlotAnnotation annotation) {
		String ne = annotation.getNomenclatureEvent() == null ? "" : annotation.getNomenclatureEvent().getName();

		return StringUtils.join(List.of(ne, slotAnnotationIdentity(annotation)), "|");
	}

	public String alleleNomenclatureEventDtoIdentity(AlleleNomenclatureEventSlotAnnotationDTO dto) {
		String ne = StringUtils.isBlank(dto.getNomenclatureEventName()) ? "" : dto.getNomenclatureEventName();

		return StringUtils.join(List.of(ne, slotAnnotationDtoIdentity(dto)), "|");
	}

	public static String alleleFunctionalImpactIdentity(AlleleFunctionalImpactSlotAnnotation annotation) {
		String functionalImpactNameString = "";
		if (CollectionUtils.isNotEmpty(annotation.getFunctionalImpacts())) {
			List<String> functionalImpactNames = annotation.getFunctionalImpacts().stream().map(VocabularyTerm::getName).collect(Collectors.toList());
			Collections.sort(functionalImpactNames);
			functionalImpactNameString = StringUtils.join(functionalImpactNames, ":");
		}
		String phenotypeTerm = annotation.getPhenotypeTerm() == null ? "" : annotation.getPhenotypeTerm().getCurie();
		String phenotypeStatement = StringUtils.isBlank(annotation.getPhenotypeStatement()) ? "" : annotation.getPhenotypeStatement();

		return StringUtils.join(List.of(functionalImpactNameString, phenotypeTerm, phenotypeStatement, slotAnnotationIdentity(annotation)), "|");
	}

	public String alleleFunctionalImpactDtoIdentity(AlleleFunctionalImpactSlotAnnotationDTO dto) {
		String functionalImpactNameString = "";
		List<String> functionalImpactNames = dto.getFunctionalImpactNames();
		if (CollectionUtils.isNotEmpty(functionalImpactNames)) {
			Collections.sort(functionalImpactNames);
			functionalImpactNameString = StringUtils.join(functionalImpactNames, ":");
		}
		String phenotypeTerm = StringUtils.isBlank(dto.getPhenotypeTermCurie()) ? "" : dto.getPhenotypeTermCurie();
		String phenotypeStatement = StringUtils.isBlank(dto.getPhenotypeStatement()) ? "" : dto.getPhenotypeStatement();

		return StringUtils.join(List.of(functionalImpactNameString, phenotypeTerm, phenotypeStatement, slotAnnotationDtoIdentity(dto)), "|");
	}

	public static String alleleInheritanceModeIdentity(AlleleInheritanceModeSlotAnnotation annotation) {
		String inheritanceMode = annotation.getInheritanceMode() == null ? "" : annotation.getInheritanceMode().getName();
		String phenotypeTerm = annotation.getPhenotypeTerm() == null ? "" : annotation.getPhenotypeTerm().getCurie();
		String phenotypeStatement = StringUtils.isBlank(annotation.getPhenotypeStatement()) ? "" : annotation.getPhenotypeStatement();

		return StringUtils.join(List.of(inheritanceMode, phenotypeTerm, phenotypeStatement, slotAnnotationIdentity(annotation)), "|");
	}

	public String alleleInheritanceModeDtoIdentity(AlleleInheritanceModeSlotAnnotationDTO dto) {
		String inheritanceMode = StringUtils.isBlank(dto.getInheritanceModeName()) ? "" : dto.getInheritanceModeName();
		String phenotypeTerm = StringUtils.isBlank(dto.getPhenotypeTermCurie()) ? "" : dto.getPhenotypeTermCurie();
		String phenotypeStatement = StringUtils.isBlank(dto.getPhenotypeStatement()) ? "" : dto.getPhenotypeStatement();

		return StringUtils.join(List.of(inheritanceMode, phenotypeTerm, phenotypeStatement, slotAnnotationDtoIdentity(dto)), "|");
	}

	public static String constructComponentIdentity(ConstructComponentSlotAnnotation annotation) {
		String componentSymbol = StringUtils.isBlank(annotation.getComponentSymbol()) ? "" : annotation.getComponentSymbol();
		String taxon = annotation.getTaxon() == null ? "" : annotation.getTaxon().getCurie();
		String taxonText = StringUtils.isBlank(annotation.getTaxonText()) ? "" : annotation.getTaxonText();
		String notesIdentity = notesIdentity(annotation.getRelatedNotes());

		return StringUtils.join(List.of(componentSymbol, taxon, taxonText, notesIdentity), "|");
	}

	public String constructComponentDtoIdentity(ConstructComponentSlotAnnotationDTO dto) {
		String componentSymbol = StringUtils.isBlank(dto.getComponentSymbol()) ? "" : dto.getComponentSymbol();
		String taxon = StringUtils.isBlank(dto.getTaxonCurie()) ? "" : dto.getTaxonCurie();
		String taxonText = StringUtils.isBlank(dto.getTaxonText()) ? "" : dto.getTaxonText();
		String notesIdentity = noteDtosIdentity(dto.getNoteDtos());

		return StringUtils.join(List.of(componentSymbol, taxon, taxonText, notesIdentity), "|");
	}

	public static String nameSlotAnnotationIdentity(NameSlotAnnotation annotation) {
		String displayText = StringUtils.isBlank(annotation.getDisplayText()) ? "" : annotation.getDisplayText();
		String formatText = StringUtils.isBlank(annotation.getFormatText()) ? "" : annotation.getFormatText();

		return StringUtils.join(List.of(displayText, formatText, slotAnnotationIdentity(annotation)), "|");
	}

	public String nameSlotAnnotationDtoIdentity(NameSlotAnnotationDTO dto) {
		String displayText = StringUtils.isBlank(dto.getDisplayText()) ? "" : dto.getDisplayText();
		String formatText = StringUtils.isBlank(dto.getFormatText()) ? "" : dto.getFormatText();

		return StringUtils.join(List.of(displayText, formatText, slotAnnotationDtoIdentity(dto)), "|");
	}

	public static String secondaryIdIdentity(SecondaryIdSlotAnnotation annotation) {
		String secondaryId = StringUtils.isBlank(annotation.getSecondaryId()) ? "" : annotation.getSecondaryId();

		return StringUtils.join(List.of(secondaryId, slotAnnotationIdentity(annotation)), "|");
	}

	public String secondaryIdDtoIdentity(SecondaryIdSlotAnnotationDTO dto) {
		String secondaryId = StringUtils.isBlank(dto.getSecondaryId()) ? "" : dto.getSecondaryId();

		return StringUtils.join(List.of(secondaryId, slotAnnotationDtoIdentity(dto)), "|");
	}

	private static String slotAnnotationIdentity(SlotAnnotation annotation) {
		return evidenceIdentity(annotation.getEvidence());
	}

	private String slotAnnotationDtoIdentity(SlotAnnotationDTO dto) {
		List<String> evidenceCuries = dto.getEvidenceCuries();
		return evidenceIngestIdentity(evidenceCuries);
	}

	private static String evidenceIdentity(List<InformationContentEntity> evidence) {
		String identity = "";
		if (CollectionUtils.isEmpty(evidence)) {
			return identity;
		}

		List<String> evidenceCuries = evidence.stream().map(InformationContentEntity::getCurie).collect(Collectors.toList());
		Collections.sort(evidenceCuries);
		identity = StringUtils.join(evidenceCuries, ":");

		return identity;
	}

	public static String notesIdentity(List<Note> notes) {
		if (CollectionUtils.isEmpty(notes)) {
			return "";
		}

		List<String> noteIdentities = new ArrayList<>();
		for (Note note : notes) {
			String freeText = StringUtils.isBlank(note.getFreeText()) ? "" : note.getFreeText();
			String noteType = note.getNoteType() == null ? "" : note.getNoteType().getName();
			String references = referencesIdentity(note.getReferences());
			String noteIdentity = StringUtils.join(List.of(freeText, noteType, references), "|");
			noteIdentities.add(noteIdentity);
		}
		Collections.sort(noteIdentities);

		return StringUtils.join(noteIdentities, "|");
	}

	public String noteDtosIdentity(List<NoteDTO> dtos) {
		if (CollectionUtils.isEmpty(dtos)) {
			return "";
		}

		List<String> noteIdentities = new ArrayList<>();
		for (NoteDTO dto : dtos) {
			String freeText = StringUtils.isBlank(dto.getFreeText()) ? "" : dto.getFreeText();
			String noteType = StringUtils.isBlank(dto.getNoteTypeName()) ? "" : dto.getNoteTypeName();
			String references = evidenceIngestIdentity(dto.getEvidenceCuries());
			String noteIdentity = StringUtils.join(List.of(freeText, noteType, references), "|");
			noteIdentities.add(noteIdentity);
		}
		Collections.sort(noteIdentities);

		return StringUtils.join(noteIdentities, "|");
	}

	private static String referencesIdentity(List<Reference> references) {
		String identity = "";
		if (CollectionUtils.isEmpty(references)) {
			return identity;
		}

		List<String> referenceCuries = references.stream().map(InformationContentEntity::getCurie).collect(Collectors.toList());
		Collections.sort(referenceCuries);
		identity = StringUtils.join(referenceCuries, ":");

		return identity;
	}

	private String evidenceIngestIdentity(List<String> evidenceCuries) {
		if (CollectionUtils.isEmpty(evidenceCuries)) {
			return "";
		}

		List<String> agrEvidenceCuries = new ArrayList<>();
		for (String curie : evidenceCuries) {
			Reference ref = refService.retrieveFromDbOrLiteratureService(curie);
			if (ref != null) {
				agrEvidenceCuries.add(ref.getCurie());
			}
		}

		if (CollectionUtils.isEmpty(agrEvidenceCuries)) {
			return "";
		}
		Collections.sort(agrEvidenceCuries);
		return StringUtils.join(agrEvidenceCuries, ":");
	}

}
