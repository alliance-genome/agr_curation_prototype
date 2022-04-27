package org.alliancegenome.curation_api.services;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.base.services.BaseCrudService;
import org.alliancegenome.curation_api.dao.*;
import org.alliancegenome.curation_api.dao.ontology.NcbiTaxonTermDAO;
import org.alliancegenome.curation_api.exceptions.*;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.ingest.fms.dto.*;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.helpers.DtoConverterHelper;
import org.alliancegenome.curation_api.services.helpers.validators.AlleleValidator;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.HashedMap;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class AlleleService extends BaseCrudService<Allele, AlleleDAO> {

    @Inject AlleleDAO alleleDAO;
    @Inject AlleleValidator alleleValidator;
    @Inject GeneDAO geneDAO;
    @Inject CrossReferenceService crossReferenceService;
    @Inject SynonymService synonymService;
    @Inject NcbiTaxonTermService ncbiTaxonTermService;
    @Inject NcbiTaxonTermDAO ncbiTaxonTermDAO;

    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(alleleDAO);
    }

    @Override
    @Transactional
    public ObjectResponse<Allele> update(Allele uiEntity) {
        Allele dbEntity = alleleValidator.validateAnnotation(uiEntity);
        return new ObjectResponse<Allele>(alleleDAO.persist(dbEntity));
    }

    @Transactional
    public Allele processUpdate(AlleleFmsDTO allele) throws ObjectUpdateException {
        validateAlleleDTO(allele);

        Allele dbAllele = alleleDAO.find(allele.getPrimaryId());
        //log.info("Allele: " + allele + " : " + alleleDTO.getPrimaryId());
        if (dbAllele == null) {
            dbAllele = new Allele();
            dbAllele.setCurie(allele.getPrimaryId());

            handleNewSynonyms(allele, dbAllele);
        } else {
            handleUpdateSynonyms(allele, dbAllele);
        }

        dbAllele.setSymbol(allele.getSymbol());
        dbAllele.setDescription(allele.getDescription());
        dbAllele.setTaxon(ncbiTaxonTermDAO.find(allele.getTaxonId()));
        dbAllele.setInternal(false);

        handleCrossReferences(allele, dbAllele);
        handleSecondaryIds(allele, dbAllele);

        alleleDAO.persist(dbAllele);

        return dbAllele;
    }

    private void handleCrossReferences(AlleleFmsDTO allele, Allele dbAllele) {
        Map<String, CrossReference> currentIds;
        if (dbAllele.getCrossReferences() == null) {
            currentIds = new HashedMap<>();
            dbAllele.setCrossReferences(new ArrayList<>());
        } else {
            currentIds = dbAllele.getCrossReferences().stream().collect(Collectors.toMap(CrossReference::getCurie, Function.identity()));
        }

        Map<String, CrossReferenceFmsDTO> newIds;
        if (allele.getCrossReferences() == null) {
            newIds = new HashedMap<>();
        } else {
            newIds = allele.getCrossReferences().stream().collect(Collectors.toMap(CrossReferenceFmsDTO::getId, Function.identity(),
                    (cr1, cr2) -> {
                        HashSet<String> pageAreas = new HashSet<>();
                        if (cr1.getPages() != null) pageAreas.addAll(cr1.getPages());
                        if (cr2.getPages() != null) pageAreas.addAll(cr2.getPages());
                        CrossReferenceFmsDTO newCr = new CrossReferenceFmsDTO();
                        newCr.setId(cr2.getId());
                        newCr.setPages(new ArrayList<>(pageAreas));
                        return newCr;
                    }
            ));
        }

        newIds.forEach((k, v) -> {
            if (!currentIds.containsKey(k)) {
                dbAllele.getCrossReferences().add(crossReferenceService.processUpdate(v));
            }
        });

        currentIds.forEach((k, v) -> {
            if (!newIds.containsKey(k)) {
                dbAllele.getCrossReferences().remove(v);
            }
        });

    }

    private void handleNewSynonyms(AlleleFmsDTO allele, Allele dbAllele) {
        if (CollectionUtils.isNotEmpty(allele.getSynonyms())) {
            List<Synonym> synonyms = DtoConverterHelper.getSynonyms(allele);
            synonyms.forEach(synonym -> synonymService.create(synonym));
            dbAllele.setSynonyms(synonyms);
        }
    }

    private void handleUpdateSynonyms(AlleleFmsDTO allele, Allele dbAllele) {
        if (CollectionUtils.isNotEmpty(allele.getSynonyms())) {
            List<Synonym> newSynonyms = DtoConverterHelper.getSynonyms(allele);

            List<Synonym> existingSynonyms = dbAllele.getSynonyms();

            // remove synonyms that are not found in the new synonym list
            if (CollectionUtils.isNotEmpty(existingSynonyms)) {
                List<String> existingSynonymStrings = existingSynonyms.stream().map(Synonym::getName).collect(Collectors.toList());
                List<Long> removeSynIDs = existingSynonyms.stream()
                        .filter(synonym -> !existingSynonymStrings.contains(synonym.getName()))
                        .map(Synonym::getId)
                        .collect(Collectors.toList());
                removeSynIDs.forEach(id -> synonymService.delete(id));
                existingSynonyms.removeIf(synonym -> newSynonyms.stream().noneMatch(synonym1 -> synonym1.getName().equals(synonym.getName())));
            }
            // add new synonyms that are not found in the existing synonym list
            if (existingSynonyms != null) {
                List<String> existingSynonymStrings = existingSynonyms.stream().map(Synonym::getName).collect(Collectors.toList());
                final List<Synonym> newCollect = newSynonyms.stream().filter(synonym -> !existingSynonymStrings.contains(synonym.getName())).collect(Collectors.toList());
                newCollect.forEach(synonym -> {
                    synonym.setGenomicEntities(List.of(dbAllele));
                    synonymService.create(synonym);
                });
                existingSynonyms.addAll(newCollect);
            }
        } else {
            // remove all existing synonyms if there are no incoming synonyms
            dbAllele.getSynonyms().forEach(synonym -> synonymService.delete(synonym.getId()));
            dbAllele.setSynonyms(new ArrayList<>());
        }
    }

    private void handleSecondaryIds(AlleleFmsDTO allele, Allele dbAllele) {
        Set<String> currentIds;
        if (dbAllele.getSecondaryIdentifiers() == null) {
            currentIds = new HashSet<>();
            dbAllele.setSecondaryIdentifiers(new ArrayList<>());
        } else {
            currentIds = dbAllele.getSecondaryIdentifiers().stream().collect(Collectors.toSet());
        }

        Set<String> newIds;
        if (allele.getSecondaryIds() == null) {
            newIds = new HashSet<>();
        } else {
            newIds = allele.getSecondaryIds().stream().collect(Collectors.toSet());
        }

        newIds.forEach(id -> {
            if (!currentIds.contains(id)) {
                dbAllele.getSecondaryIdentifiers().add(id);
            }
        });

        currentIds.forEach(id -> {
            if (!newIds.contains(id)) {
                dbAllele.getSecondaryIdentifiers().remove(id);
            }
        });

    }

    private void validateAlleleDTO(AlleleFmsDTO allele) throws ObjectValidationException {
        // Check for required fields
        if (allele.getPrimaryId() == null || allele.getSymbol() == null
                || allele.getSymbolText() == null || allele.getTaxonId() == null) {
            throw new ObjectValidationException(allele, "Entry for allele " + allele.getPrimaryId() + " missing required fields - skipping");
        }

        // Validate taxon ID
        ObjectResponse<NCBITaxonTerm> taxon = ncbiTaxonTermService.get(allele.getTaxonId());
        if (taxon.getEntity() == null) {
            throw new ObjectValidationException(allele, "Invalid taxon ID for allele " + allele.getPrimaryId() + " - skipping");
        }

        // Validate xrefs
        if (allele.getCrossReferences() != null) {
            for (CrossReferenceFmsDTO xrefDTO : allele.getCrossReferences()) {
                if (xrefDTO.getId() == null) {
                    throw new ObjectValidationException(allele, "Missing xref ID for allele " + allele.getPrimaryId() + " - skipping");
                }
            }
        }

        // Validate allele object relations
        // TODO: validate construct relation once constructs are loaded
        if (CollectionUtils.isNotEmpty(allele.getAlleleObjectRelations())) {
            for (AlleleObjectRelationFmsDTO alleleObjectRelation : allele.getAlleleObjectRelations()) {
                if (alleleObjectRelation.getObjectRelation().getAssociationType() == null) {
                    throw new ObjectValidationException(allele, "No association type found for related gene/construct of " + allele.getPrimaryId() + " - skipping");
                }
                if (alleleObjectRelation.getObjectRelation().getGene() != null) {
                    Gene gene = geneDAO.find(alleleObjectRelation.getObjectRelation().getGene());
                    if (gene == null) {
                        throw new ObjectValidationException(allele, "Related gene not found in database for " + allele.getPrimaryId() + " - skipping");
                    }
                    if (!alleleObjectRelation.getObjectRelation().getAssociationType().equals("allele_of")) {
                        throw new ObjectValidationException(allele, "Invalid association type for related gene of " + allele.getPrimaryId() + " - skipping");
                    }
                } else if (alleleObjectRelation.getObjectRelation().getConstruct() != null) {
                    // TODO: add validation of construct once constructs are loaded
                    if (!alleleObjectRelation.getObjectRelation().getAssociationType().equals("contains")) {
                        throw new ObjectValidationException(allele, "Invalid association type for related gene of " + allele.getPrimaryId() + " - skipping");
                    }
                } else {
                    throw new ObjectValidationException(allele, "Nog gene or construct specified in related object of " + allele.getPrimaryId() + " - skipping");
                }
            }
        }
    }

}
