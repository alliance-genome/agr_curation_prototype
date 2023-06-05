package org.alliancegenome.curation_api.services.helpers.slotAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.model.entities.InformationContentEntity;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.ontology.SOTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.NameSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.SecondaryIdSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.SlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleInheritanceModeSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleMutationTypeSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.AlleleInheritanceModeSlotAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.AlleleMutationTypeSlotAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.NameSlotAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.SecondaryIdSlotAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.SlotAnnotationDTO;
import org.alliancegenome.curation_api.services.ReferenceService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

@RequestScoped
public class SlotAnnotationIdentityHelper {
	
	@Inject ReferenceService refService;
	
	public static String alleleMutationTypesIdentity(AlleleMutationTypeSlotAnnotation annotation) {
		String identity = "";
		if (CollectionUtils.isNotEmpty(annotation.getMutationTypes())) {
			List<String> mutationTypeCuries = annotation.getMutationTypes().stream().map(SOTerm::getCurie).collect(Collectors.toList());
			Collections.sort(mutationTypeCuries);
			identity = StringUtils.join(mutationTypeCuries, ":");
		}
		return identity + "|" + slotAnnotationIdentity(annotation);
	}
	
	public String alleleMutationTypesDtoIdentity(AlleleMutationTypeSlotAnnotationDTO dto) {
		String identity = "";
		List<String> mutationTypeCuries = dto.getMutationTypeCuries();
		if (CollectionUtils.isNotEmpty(mutationTypeCuries)) {
			Collections.sort(mutationTypeCuries);
			identity = StringUtils.join(mutationTypeCuries, ":");
		}
		
		return identity + "|" + slotAnnotationDtoIdentity(dto);
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
		String identity = "";
		if (CollectionUtils.isNotEmpty(annotation.getEvidence())) {
			List<String> evidenceCuries = annotation.getEvidence().stream().map(InformationContentEntity::getCurie).collect(Collectors.toList());
			Collections.sort(evidenceCuries);
			identity = StringUtils.join(evidenceCuries, ":");
		}
		
		return identity;
	}
	
	private String slotAnnotationDtoIdentity(SlotAnnotationDTO dto) {
		List<String> evidenceCuries = dto.getEvidenceCuries();
		if (CollectionUtils.isEmpty(evidenceCuries))
			return "";
		
		List<String> agrEvidenceCuries = new ArrayList<>();
		for (String curie : evidenceCuries) {
			Reference ref = refService.retrieveFromDbOrLiteratureService(curie);
			if (ref != null)
				agrEvidenceCuries.add(ref.getCurie());
		}
		
		if (CollectionUtils.isEmpty(agrEvidenceCuries))
			return "";
		Collections.sort(agrEvidenceCuries);
		return StringUtils.join(agrEvidenceCuries, ":");
	}

}
