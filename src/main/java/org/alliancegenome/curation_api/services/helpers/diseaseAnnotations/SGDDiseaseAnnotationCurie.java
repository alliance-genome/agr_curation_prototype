package org.alliancegenome.curation_api.services.helpers.diseaseAnnotations;

import org.alliancegenome.curation_api.model.ingest.json.dto.DiseaseModelAnnotationDTO;
import org.alliancegenome.curation_api.services.helpers.CurieGeneratorHelper;
import org.apache.commons.collections4.CollectionUtils;

public class SGDDiseaseAnnotationCurie extends DiseaseAnnotationCurie {

    /**
     * gene ID + DOID + PubID
     *
     * @param annotationDTO DiseaseModelAnnotationDTO
     * @return curie string
     */
    @Override
    public String getCurieID(DiseaseModelAnnotationDTO annotationDTO) {
        CurieGeneratorHelper curie = new CurieGeneratorHelper();
        curie.add(annotationDTO.getObjectId());
        curie.add(annotationDTO.getDoId());
        curie.add(getEvidenceCurie(annotationDTO.getEvidence()));
        curie.add(getWithCuries(annotationDTO));
        return curie.getCurie();
    }

    public String getWithCuries(DiseaseModelAnnotationDTO annotationDTO) {
        if (CollectionUtils.isEmpty(annotationDTO.getWith()))
            return null;
        CurieGeneratorHelper generator = new CurieGeneratorHelper();
        generator.addAll(annotationDTO.getWith());
        return generator.getCurie();
    }
}

