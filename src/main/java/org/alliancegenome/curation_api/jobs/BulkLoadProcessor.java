package org.alliancegenome.curation_api.jobs;

import java.io.FileInputStream;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.BaseOntologyTermService;
import org.alliancegenome.curation_api.dao.loads.BulkLoadFileDAO;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoad.BackendBulkLoadType;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;
import org.alliancegenome.curation_api.model.entities.ontology.*;
import org.alliancegenome.curation_api.model.ingest.json.dto.*;
import org.alliancegenome.curation_api.services.*;
import org.alliancegenome.curation_api.services.helpers.*;
import org.alliancegenome.curation_api.services.ontology.*;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.logging.Log;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
public class BulkLoadProcessor {

    @Inject ObjectMapper mapper;
    
    @Inject AlleleService alleleService;
    @Inject GeneService geneService;
    @Inject AffectedGenomicModelService agmService;
    @Inject DiseaseAnnotationService diseaseService;
    
    
    @Inject EcoTermService ecoTermService;
    @Inject ZecoTermService zecoTermService;

    @Inject BulkLoadFileDAO bulkLoadFileDAO;
    
    public void process(BulkLoadFile bulkLoadFile) throws Exception {
        //log.info("Process Starting for: " + bulkLoadFile);
        if(bulkLoadFile.getBulkLoad().getBackendBulkLoadType() == BackendBulkLoadType.GENE) {
            GeneMetaDataDTO geneData = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())), GeneMetaDataDTO.class);
            ProcessDisplayHelper ph = new ProcessDisplayHelper(10000);
            ph.startProcess("Gene Update", geneData.getData().size());
            bulkLoadFile.setRecordCount(geneData.getData().size());
            bulkLoadFileDAO.merge(bulkLoadFile);
            for(GeneDTO gene: geneData.getData()) {
                geneService.processUpdate(gene);
                ph.progressProcess();
            }
            ph.finishProcess();
        } else if(bulkLoadFile.getBulkLoad().getBackendBulkLoadType() == BackendBulkLoadType.ALLELE) {
            AlleleMetaDataDTO alleleData = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())), AlleleMetaDataDTO.class);
            ProcessDisplayHelper ph = new ProcessDisplayHelper(10000);
            bulkLoadFile.setRecordCount(alleleData.getData().size());
            bulkLoadFileDAO.merge(bulkLoadFile);
            ph.startProcess("Allele Update", alleleData.getData().size());
            for(AlleleDTO allele: alleleData.getData()) {
                alleleService.processUpdate(allele);
                ph.progressProcess();
            }
            ph.finishProcess();
        } else if(bulkLoadFile.getBulkLoad().getBackendBulkLoadType() == BackendBulkLoadType.AGM) {
            AffectedGenomicModelMetaDataDTO agmData = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())), AffectedGenomicModelMetaDataDTO.class);
            ProcessDisplayHelper ph = new ProcessDisplayHelper(10000);
            bulkLoadFile.setRecordCount(agmData.getData().size());
            bulkLoadFileDAO.merge(bulkLoadFile);
            ph.startProcess("AGM Update", agmData.getData().size());
            for(AffectedGenomicModelDTO agm: agmData.getData()) {
                agmService.processUpdate(agm);
                ph.progressProcess();
            }
            ph.finishProcess();
        } else if(bulkLoadFile.getBulkLoad().getBackendBulkLoadType() == BackendBulkLoadType.DISEASE_ANNOTATION) {
            DiseaseAnnotationMetaDataDTO diseaseData = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())), DiseaseAnnotationMetaDataDTO.class);
            // TODO find taxon ID and send it with this load
            bulkLoadFile.setRecordCount(diseaseData.getData().size());
            bulkLoadFileDAO.merge(bulkLoadFile);
            diseaseService.runLoad(null, diseaseData);
        } else if(bulkLoadFile.getBulkLoad().getBackendBulkLoadType() == BackendBulkLoadType.ONTOLOGY) {
            if(bulkLoadFile.getBulkLoad().getName().equals("ZECO Ontology Load")) {
                GenericOntologyLoadConfig config = new GenericOntologyLoadConfig();
                config.setLoadOnlyIRIPrefix("ZECO");
                processTerms(bulkLoadFile, ZecoTerm.class, bulkLoadFile.getLocalFilePath(), zecoTermService, config);
            } else if(bulkLoadFile.getBulkLoad().getName().equals("ECO Ontology Load")) {
                processTerms(bulkLoadFile, EcoTerm.class, bulkLoadFile.getLocalFilePath(), ecoTermService);
            } else {
            }
        } else if(bulkLoadFile.getBulkLoad().getGroup().getName().equals("Reindex Tasks")) {
            if(bulkLoadFile.getBulkLoad().getName().equals("Gene Reindex")) { geneService.reindex(); }
            if(bulkLoadFile.getBulkLoad().getName().equals("Allele Reindex")) { alleleService.reindex(); }
            if(bulkLoadFile.getBulkLoad().getName().equals("AGM Reindex")) { agmService.reindex(); }
        } else {
            Log.info("Load: " + bulkLoadFile.getBulkLoad().getName() + " not implemented");
        }

        //log.info("Process Finished for: " + bulkLoadFile);
    }
    
    private <T extends OntologyTerm> void processTerms(BulkLoadFile bulkLoadFile, Class<T> clazz, String filePath, BaseOntologyTermService service) throws Exception {
        processTerms(bulkLoadFile, clazz, filePath, null);
    }
    
    private <T extends OntologyTerm> void processTerms(BulkLoadFile bulkLoadFile, Class<T> clazz, String filePath, BaseOntologyTermService service, GenericOntologyLoadConfig config) throws Exception {
        GenericOntologyLoadHelper<T> loader;
        if(config != null) {
            loader = new GenericOntologyLoadHelper<T>(clazz, config);
        } else {
            loader = new GenericOntologyLoadHelper<T>(clazz);
        }

        Map<String, T> termMap = loader.load(new GZIPInputStream(new FileInputStream(filePath)));

        bulkLoadFile.setRecordCount(termMap.size());
        bulkLoadFileDAO.merge(bulkLoadFile);
        ProcessDisplayHelper ph = new ProcessDisplayHelper(10000);
        ph.startProcess(clazz.getSimpleName() + " Database Persistance", termMap.size());
        for(String termKey: termMap.keySet()) {
            service.processUpdate(termMap.get(termKey));
            ph.progressProcess();
        }
        ph.finishProcess();
    }


}
