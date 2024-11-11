package org.alliancegenome.curation_api.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.dao.CrossReferenceDAO;
import org.alliancegenome.curation_api.dao.MoleculeDAO;
import org.alliancegenome.curation_api.dao.ResourceDescriptorPageDAO;
import org.alliancegenome.curation_api.dao.SynonymDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.KnownIssueValidationException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.interfaces.crud.BaseUpsertServiceInterface;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Molecule;
import org.alliancegenome.curation_api.model.entities.Synonym;
import org.alliancegenome.curation_api.model.ingest.dto.fms.CrossReferenceFmsDTO;
import org.alliancegenome.curation_api.model.ingest.dto.fms.MoleculeFmsDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.MoleculeValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class MoleculeService extends BaseEntityCrudService<Molecule, MoleculeDAO> implements BaseUpsertServiceInterface<Molecule, MoleculeFmsDTO> {

	@Inject MoleculeDAO moleculeDAO;
	@Inject SynonymDAO synonymDAO;
	@Inject ResourceDescriptorPageDAO resourceDescriptorPageDAO;
	@Inject MoleculeValidator moleculeValidator;
	@Inject CrossReferenceService crossReferenceService;
	@Inject CrossReferenceDAO crossReferenceDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(moleculeDAO);
	}

	@Override
	@Transactional
	public ObjectResponse<Molecule> create(Molecule uiEntity) {
		Molecule dbEntity = moleculeValidator.validateMoleculeCreate(uiEntity);
		return new ObjectResponse<Molecule>(dbEntity);
	}

	@Override
	@Transactional
	public ObjectResponse<Molecule> update(Molecule uiEntity) {
		Molecule dbEntity = moleculeValidator.validateMoleculeUpdate(uiEntity);
		return new ObjectResponse<>(moleculeDAO.persist(dbEntity));
	}

	@Transactional
	public Molecule upsert(MoleculeFmsDTO dto, BackendBulkDataProvider backendBulkDataProvider) throws ValidationException {
		log.debug("processUpdate Molecule: ");

		if (StringUtils.isBlank(dto.getId())) {
			log.debug(dto.getName() + " has no ID - skipping");
			throw new ObjectUpdateException(dto, dto.getId() + " has no ID - skipping");
		}

		if (dto.getId().startsWith("CHEBI:")) {
			log.debug("Skipping processing of " + dto.getId());
			throw new KnownIssueValidationException("Skipping processing of " + dto.getId());
		}

		if (StringUtils.isBlank(dto.getName())) {
			log.debug(dto.getId() + " has no name - skipping");
			throw new ObjectUpdateException(dto, dto.getId() + " has no name - skipping");
		}

		if (dto.getCrossReferences() != null) {
			for (CrossReferenceFmsDTO xrefDTO : dto.getCrossReferences()) {
				if (StringUtils.isBlank(xrefDTO.getId())) {
					log.debug("Missing xref ID for molecule " + dto.getId() + " - skipping");
					throw new ObjectUpdateException(dto, "Missing xref ID for molecule " + dto.getId() + " - skipping");
				}
			}
		}

		try {
			Molecule molecule = findByCurie(dto.getId());

			if (molecule == null) {
				molecule = new Molecule();
				molecule.setCurie(dto.getId());
			}

			molecule.setName(dto.getName());

			String inchi = null;
			if (StringUtils.isNotBlank(dto.getInchi())) {
				inchi = dto.getInchi();
			}
			molecule.setInchi(inchi);

			String inchikey = null;
			if (StringUtils.isNotBlank(dto.getInchikey())) {
				inchikey = dto.getInchikey();
			}
			molecule.setInchiKey(inchikey);

			String iupac = null;
			if (StringUtils.isNotBlank(dto.getIupac())) {
				iupac = dto.getIupac();
			}
			molecule.setIupac(iupac);

			String formula = null;
			if (StringUtils.isNotBlank(dto.getFormula())) {
				formula = dto.getFormula();
			}
			molecule.setFormula(formula);

			String smiles = null;
			if (StringUtils.isNotBlank(dto.getSmiles())) {
				smiles = dto.getSmiles();
			}
			molecule.setSmiles(smiles);

			if (CollectionUtils.isNotEmpty(dto.getSynonyms())) {
				List<Synonym> synonyms = new ArrayList<Synonym>();
				for (String synonymName : dto.getSynonyms()) {
					SearchResponse<Synonym> response = synonymDAO.findByField("name", synonymName);
					Synonym synonym;
					if (response == null || response.getSingleResult() == null) {
						synonym = new Synonym();
						synonym.setName(synonymName);
						synonym = synonymDAO.persist(synonym);
					} else {
						synonym = response.getSingleResult();
					}
					synonyms.add(synonym);
				}
				molecule.setSynonyms(synonyms);
			} else {
				molecule.setSynonyms(null);
			}

			molecule.setNamespace("molecule");

			List<Long> currentXrefIds;
			if (molecule.getCrossReferences() == null) {
				currentXrefIds = new ArrayList<>();
			} else {
				currentXrefIds = molecule.getCrossReferences().stream().map(CrossReference::getId).collect(Collectors.toList());
			}

			List<Long> mergedXrefIds;
			if (dto.getCrossReferences() == null) {
				mergedXrefIds = new ArrayList<>();
				molecule.setCrossReferences(null);
			} else {
				List<CrossReference> mergedCrossReferences = crossReferenceService.getMergedFmsXrefList(dto.getCrossReferences(), molecule.getCrossReferences());
				mergedXrefIds = mergedCrossReferences.stream().map(CrossReference::getId).collect(Collectors.toList());
				molecule.setCrossReferences(mergedCrossReferences);
			}

			moleculeDAO.persist(molecule);

			for (Long currentId : currentXrefIds) {
				if (!mergedXrefIds.contains(currentId)) {
					crossReferenceDAO.remove(currentId);
				}
			}

			return molecule;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ObjectUpdateException(dto, e.getMessage(), e.getStackTrace());
		}

	}

}
