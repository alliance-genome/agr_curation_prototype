package org.alliancegenome.curation_api.services;

import io.quarkus.logging.Log;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.alliancegenome.curation_api.auth.AuthenticatedUser;
import org.alliancegenome.curation_api.dao.GafDAO;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.dao.SpeciesDAO;
import org.alliancegenome.curation_api.dao.ontology.GoTermDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.model.entities.ontology.GOTerm;
import org.alliancegenome.curation_api.model.ingest.dto.GafDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.DataProviderValidator;

import java.util.HashMap;
import java.util.Map;

@RequestScoped
public class GafService extends BaseEntityCrudService<Gaf, GafDAO> {

	private Species species;
	public static final String RESOURCE_DESCRIPTOR_PREFIX = "ENSEMBL";
	public static final String RESOURCE_DESCRIPTOR_PAGE_NAME = "default";
	// <crossReference.referencedCurie, DataProvider>
	Map<String, Long> accessionGeneMap = new HashMap<>();
	Map<String, Long> goTermMap = new HashMap<>();
	HashMap<String, DataProvider> dataProviderMap = new HashMap<>();


	@Inject
	@AuthenticatedUser
	protected Person authenticatedPerson;
	@Inject
	GafDAO gafDAO;
	@Inject
	GeneDAO geneDAO;
	@Inject
	SpeciesDAO speciesDAO;
	@Inject
	GoTermDAO goTermDAO;
	@Inject
	DataProviderValidator dataProviderValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(gafDAO);
	}

	@Transactional
	public ObjectResponse<Gaf> insert(GafDTO uiEntity, String orgAbbreviation) {
		// if record exists skip over it
		if (gafMap.values().stream().anyMatch(gafDTO -> gafDTO.equals(uiEntity))) {
			for (Map.Entry<Long, GafDTO> entry : gafMap.entrySet()) {
				if (entry.getValue().equals(uiEntity)) {
					Gaf gaf = new Gaf();
					gaf.setId(entry.getKey());
					ObjectResponse<Gaf> objectObjectResponse = new ObjectResponse<>();
					objectObjectResponse.setEntity(gaf);
					return objectObjectResponse;
				}
			}
		}
		// convert curies into IDs

		Long geneID = getGeneID(uiEntity, orgAbbreviation);
		Gaf gaf = new Gaf();
		Gene gene = new Gene();
		gene.setId(geneID);
		gaf.setGene(gene);
		Long goID = getGOID(uiEntity);
		GOTerm term = new GOTerm();
		term.setId(goID);
		gaf.setGoTerm(term);
		Gaf gafNew = gafDAO.persistGeneGoAssociation(gaf);
		addNewRecordToMap(gafNew, uiEntity);
		return new ObjectResponse<>(gafNew);
	}

	private void addNewRecordToMap(Gaf gafNew, GafDTO uiEntity) {
		GafDTO dto = new GafDTO();
		dto.setGeneID(uiEntity.getGeneID());
		dto.setGoID(uiEntity.getGoID());
		gafMap.put(gafNew.getId(), dto);
	}

	public Long getGeneID(GafDTO uiEntity, String orgAbbreviation) {
		if (accessionGeneMap.isEmpty()) {
			accessionGeneMap = geneDAO.getAllGeneIdsPerSpecies(getSpecies(orgAbbreviation));
		}
		Long geneID = accessionGeneMap.get(uiEntity.getGeneID());
		return geneID;
	}

	private Long getGOID(GafDTO uiEntity) {
		if (goTermMap.isEmpty()) {
			goTermMap = goTermDAO.getAllGOIds();
		}
		Long goID = goTermMap.get(uiEntity.getGoID());
		return goID;
	}

	private Species getSpecies(String orgAbbreviation) {
		if (species != null) {
			return species;
		}
		Map<String, Object> map = new HashMap<>();
		map.put("displayName", orgAbbreviation);
		species = speciesDAO.findByParams(map).getSingleResult();
		return species;
	}

	public ObjectResponse<DataProvider> validate(DataProvider uiEntity) {
		return dataProviderValidator.validateDataProvider(uiEntity, null, true);
	}


	private Map<Long, GafDTO> gafMap = new HashMap<>();

	public Map<Long, GafDTO> getGafMap(Organization organization) {
		if (gafMap.size() > 0) {
			return gafMap;
		}
		gafMap = gafDAO.getAllGafIdsPerProvider(organization);
		return gafMap;
	}

	@Transactional
	public Gaf deprecateOrDelete(Long id, Boolean throwApiError, String requestSource, Boolean deprecate) {
		gafDAO.delete(id);
		return null;
	}

}
