package org.alliancegenome.curation_api.jobs.executors;

import java.io.FileInputStream;
import java.util.*;
import java.util.zip.GZIPInputStream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.GeneDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException.ObjectUpdateExceptionData;
import org.alliancegenome.curation_api.model.entities.GeneDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.bulkloads.*;
import org.alliancegenome.curation_api.model.ingest.dto.*;
import org.alliancegenome.curation_api.response.*;
import org.alliancegenome.curation_api.services.*;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
public class GeneDiseaseAnnotationExecutor extends LoadFileExecutor {

    @Inject GeneDiseaseAnnotationDAO geneDiseaseAnnotationDAO;
    @Inject GeneDiseaseAnnotationService geneDiseaseAnnotationService;
    @Inject DiseaseAnnotationService diseaseAnnotationService;

    public void runLoad(BulkLoadFile bulkLoadFile) {
        
        try {
            BulkManualLoad manual = (BulkManualLoad)bulkLoadFile.getBulkLoad();
            log.info("Running with: " + manual.getDataType().name() + " " + manual.getDataType().getTaxonId());

            IngestDTO ingestDto = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())), IngestDTO.class);
            List<GeneDiseaseAnnotationDTO> annotations = ingestDto.getDiseaseGeneIngestSet();
            String taxonId = manual.getDataType().getTaxonId();

            if (annotations != null) {
                bulkLoadFile.setRecordCount(annotations.size() + bulkLoadFile.getRecordCount());
                bulkLoadFileDAO.merge(bulkLoadFile);
                
                trackHistory(runLoad(taxonId, annotations), bulkLoadFile);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    // Gets called from the API directly
    public APIResponse runLoad(String taxonId, List<GeneDiseaseAnnotationDTO> annotations) {
        
        List<String> annotationIdsBefore = new ArrayList<>();
        annotationIdsBefore.addAll(geneDiseaseAnnotationDAO.findAllAnnotationIds(taxonId));
        annotationIdsBefore.removeIf(Objects::isNull);
        
        log.debug("runLoad: Before: " + taxonId + " " + annotationIdsBefore.size());
        List<String> annotationIdsAfter = new ArrayList<>();
        BulkLoadFileHistory history = new BulkLoadFileHistory(annotations.size());
        ProcessDisplayHelper ph = new ProcessDisplayHelper(10000);
        ph.startProcess("Gene Disease Annotation Update " + taxonId, annotations.size());
        annotations.forEach(annotationDTO -> {
            
            try {
                GeneDiseaseAnnotation annotation = geneDiseaseAnnotationService.upsert(annotationDTO);
                history.incrementCompleted();
                annotationIdsAfter.add(annotation.getUniqueId());
            } catch (ObjectUpdateException e) {
                addException(history, e.getData());
            } catch (Exception e) {
                addException(history, new ObjectUpdateExceptionData(annotationDTO, e.getMessage()));
            }

            ph.progressProcess();
        });
        ph.finishProcess();
        
        diseaseAnnotationService.removeNonUpdatedAnnotations(taxonId, annotationIdsBefore, annotationIdsAfter);
        
        return new LoadHistoryResponce(history);
    }
    
    
    
}
