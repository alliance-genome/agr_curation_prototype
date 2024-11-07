package org.alliancegenome.curation_api.services.validation.dto.fms;

import java.util.HashMap;

import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.ingest.dto.fms.BiogridOrcFmsDTO;
import org.alliancegenome.curation_api.response.SearchResponse;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class BioGridOrcsFmsDTOValidator {

    //Todo: rename?
    public CrossReference validateBioGridOrcsFmsDTO(String referencedCurie){
        			// 		HashMap<String, Object> crossRefParams = new HashMap<>();
					// crossRefParams.put("referencedCurie", referencedCurie);
					// crossRefParams.put("displayName", referencedCurie);
					// crossRefParams.put("resourceDescriptorPage.id", resourceDescriptorPage.getId());

					// // Log.debug("--------------crossRefDupSearch----------------");
					// SearchResponse<CrossReference> crossRefDupSearch =
					// crossRefDAO.findByParams(crossRefParams);
					// Log.debug(crossRefDupSearch.getResults().isEmpty());

					// if(!crossRefDupSearch.getResults().isEmpty()) continue;

        return new CrossReference();
    }
    
}
