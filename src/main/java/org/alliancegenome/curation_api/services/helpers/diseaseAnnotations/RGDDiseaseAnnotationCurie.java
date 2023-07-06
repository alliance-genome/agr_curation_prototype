package org.alliancegenome.curation_api.services.helpers.diseaseAnnotations;

import java.util.List;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.model.entities.ConditionRelation;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.ontology.ECOTerm;
import org.alliancegenome.curation_api.model.ingest.dto.DiseaseAnnotationDTO;
import org.alliancegenome.curation_api.services.helpers.CurieGeneratorHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

public class RGDDiseaseAnnotationCurie extends DiseaseAnnotationCurie {

	/**
	 * genotype ID + DOID + PubID
	 *
	 * @param annotationDTO DiseaseModelAnnotationFmsDTO
	 * @return curie string
	 */

	@Override
	public String getCurieID(DiseaseAnnotationDTO annotationDTO, String subjectCurie, String refCurie) {
		CurieGeneratorHelper curie = new CurieGeneratorHelper();
		curie.add(subjectCurie);
		curie.add(annotationDTO.getDoTermCurie());
		curie.add(refCurie);
		curie.add(StringUtils.join(annotationDTO.getEvidenceCodeCuries(), "::"));
		return curie.getCurie();
	}

	@Override
	public String getCurieID(DiseaseAnnotation annotation) {
		CurieGeneratorHelper curie = new CurieGeneratorHelper();
		curie.add(annotation.getSubjectCurie());
		if (annotation.getObject() != null)
			curie.add(annotation.getObject().getCurie());
		if (annotation.getSingleReference() != null)
			curie.add(annotation.getSingleReference().getCurie());
		if (CollectionUtils.isNotEmpty(annotation.getEvidenceCodes()))
			curie.add(StringUtils.join(annotation.getEvidenceCodes().stream().map(ECOTerm::getCurie).collect(Collectors.toList()), "::"));
		return curie.getCurie();
	}

	@Override
	public String getCurieID(String subject, String object, String reference, List<String> evidenceCodes, List<ConditionRelation> relations, String associationType, String negated, String diseaseGeneticModifierRelation, String diseaseGeneticModifier) {
		return super.getCurieID(subject, object, reference, null, null, null, null, null, null);
	}

}