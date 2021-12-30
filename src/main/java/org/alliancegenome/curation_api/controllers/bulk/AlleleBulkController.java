package org.alliancegenome.curation_api.controllers.bulk;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.consumers.AlleleDTOConsumer;
import org.alliancegenome.curation_api.interfaces.bulk.AlleleBulkInterface;
import org.alliancegenome.curation_api.jobs.BulkLoadFileProcessor;
import org.alliancegenome.curation_api.model.entities.bulkloads.*;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoad.*;
import org.alliancegenome.curation_api.model.ingest.json.dto.*;
import org.alliancegenome.curation_api.services.AlleleService;
import org.alliancegenome.curation_api.util.*;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import io.quarkus.vertx.ConsumeEvent;
import io.vertx.core.eventbus.Message;

@RequestScoped
public class AlleleBulkController implements AlleleBulkInterface {

    @Inject AlleleDTOConsumer alleleDTOConsumer;
    
    @Inject AlleleService alleleService;
    
    @Inject BulkLoadFileProcessor bulkLoadFileProcessor;

    //@Override
    public String updateAlleles(AlleleMetaDataDTO alleleData, boolean async) {

        ProcessDisplayHelper ph = new ProcessDisplayHelper(10000);
        ph.startProcess("Allele Update", alleleData.getData().size());
        for(AlleleDTO allele: alleleData.getData()) {
            if(async) {
                alleleDTOConsumer.send(allele);
            } else {
                alleleService.processUpdate(allele);
            }

            ph.progressProcess();
        }
        ph.finishProcess();

        return "OK";
    }

    @Override
    public String updateAlleles(MultipartFormDataInput input) {
        bulkLoadFileProcessor.process(input, BackendBulkLoadType.ALLELE);   
        return "OK";
    }
    
}
