package org.alliancegenome.curation_api.services;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import lombok.extern.jbosslog.JBossLog;

import org.alliancegenome.curation_api.base.BaseService;
import org.alliancegenome.curation_api.base.SearchResults;

import org.alliancegenome.curation_api.dao.CrossReferenceDAO;

import org.alliancegenome.curation_api.model.entities.CrossReference;

import org.alliancegenome.curation_api.model.ingest.json.dto.CrossReferenceDTO;
import org.alliancegenome.curation_api.model.input.Pagination;

import javax.annotation.PostConstruct;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.Session;
import javax.transaction.Transactional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@JBossLog
public class CrossReferenceService extends BaseService<CrossReference, CrossReferenceDAO> implements Runnable{

    @Inject CrossReferenceDAO crossReferenceDAO;


    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(crossReferenceDAO);
    }

    public SearchResults<CrossReference> getAllCrossReferences(Pagination pagination) {
        return getAll(pagination);
    }

    @Transactional
    public CrossReference processUpdate(CrossReferenceDTO crossReferenceDTO) {

        CrossReference crossReference = crossReferenceDAO.find(crossReferenceDTO.getId());
        if(crossReference == null) {
            crossReference = new CrossReference();
            crossReference.setCurie(crossReferenceDTO.getId());
            crossReference.setPageAreas(crossReferenceDTO.getPages());
            create(crossReference);
        } else {
            if(crossReference.getCurie().equals(crossReferenceDTO.getId())) {
                crossReference.setPageAreas(crossReferenceDTO.getPages());
                update(crossReference);
            }
        }

        return crossReference;

    }


    @Inject
    ConnectionFactory connectionFactory;

    private int threadCount = 3;

    private final ExecutorService scheduler = Executors.newFixedThreadPool(threadCount);

    void onStart(@Observes StartupEvent ev) {
        log.info("CrossReferenceService Queue Starting:");
        for(int i = 0; i < threadCount; i++) {
            scheduler.submit(new Thread(this));
        }
    }

    void onStop(@Observes ShutdownEvent ev) {
        scheduler.shutdown();
    }

    @Override
    public void run() {
        try (JMSContext context = connectionFactory.createContext(Session.AUTO_ACKNOWLEDGE)) {
            JMSConsumer consumer = context.createConsumer(context.createQueue("crossReferenceQueue"));
            while (true) {
                processUpdate(consumer.receiveBody(CrossReferenceDTO.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
