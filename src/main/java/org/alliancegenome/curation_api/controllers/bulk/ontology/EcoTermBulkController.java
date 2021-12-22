package org.alliancegenome.curation_api.controllers.bulk.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.BaseOntologyTermBulkController;
import org.alliancegenome.curation_api.dao.ontology.EcoTermDAO;
import org.alliancegenome.curation_api.interfaces.bulk.ontology.EcoTermBulkInterface;
import org.alliancegenome.curation_api.model.entities.ontology.EcoTerm;
import org.alliancegenome.curation_api.services.ontology.EcoTermService;

@RequestScoped
public class EcoTermBulkController extends BaseOntologyTermBulkController<EcoTermService, EcoTerm, EcoTermDAO> implements EcoTermBulkInterface {

    @Inject EcoTermService ecoTermService;

    @Override
    @PostConstruct
    public void init() {
        setService(ecoTermService, EcoTerm.class);
    }
    
    @Override
    public String updateTerms(boolean async, String fullText) {
        String status = super.updateTerms(async, fullText);
        if (status.equals("OK")) {
            ecoTermService.updateAbbreviations();
        }
        return status;
    }

}
