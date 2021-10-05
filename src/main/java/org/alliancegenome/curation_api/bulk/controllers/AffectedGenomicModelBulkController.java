package org.alliancegenome.curation_api.bulk.controllers;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.consumers.*;
import org.alliancegenome.curation_api.interfaces.bulk.AffectedGenomicModelBulkRESTInterface;
import org.alliancegenome.curation_api.model.ingest.json.dto.*;
import org.alliancegenome.curation_api.services.*;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class AffectedGenomicModelBulkController implements AffectedGenomicModelBulkRESTInterface {

    @Inject AffectedGenomicModelDTOConsumer affectedGenomicModelDTOConsumer;
    
    @Inject AffectedGenomicModelService affectedGenomicModelService;

    @Override
    public String updateAGMs(AffectedGenomicModelMetaDataDTO agmData, boolean async) {

        ProcessDisplayHelper ph = new ProcessDisplayHelper(10000);
        ph.startProcess("AGM Update", agmData.getData().size());
        for(AffectedGenomicModelDTO agm: agmData.getData()) {
            if(async) {
                affectedGenomicModelDTOConsumer.send(agm);
            } else {
                affectedGenomicModelService.processUpdate(agm);
            }

            ph.progressProcess();
        }
        ph.finishProcess();

        return "OK";
    }
    
    
}
