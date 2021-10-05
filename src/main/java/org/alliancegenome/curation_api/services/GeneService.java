package org.alliancegenome.curation_api.services;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.base.*;
import org.alliancegenome.curation_api.dao.*;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.model.ingest.json.dto.*;
import org.alliancegenome.curation_api.model.input.Pagination;
import org.alliancegenome.curation_api.services.helpers.DtoConverterHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.HashedMap;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class GeneService extends BaseService<Gene, GeneDAO> {

    @Inject
    GeneDAO geneDAO;
    @Inject
    CrossReferenceDAO crossReferenceDAO;
    @Inject
    CrossReferenceService crossReferenceService;
    @Inject
    SynonymService synonymService;

    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(geneDAO);
    }

    @Transactional
    public Gene getByIdOrCurie(String id) {
        Gene gene = geneDAO.getByIdOrCurie(id);
        if (gene != null) {
            gene.getSynonyms().size();
            gene.getSecondaryIdentifiers().size();
        }
        return gene;
    }

    @Transactional
    public void processUpdate(GeneDTO gene) {
        //log.info("processUpdate Gene: ");

        Gene g = geneDAO.find(gene.getBasicGeneticEntity().getPrimaryId());
        boolean newGene = false;

        if (g == null) {
            g = new Gene();
            g.setCurie(gene.getBasicGeneticEntity().getPrimaryId());
            newGene = true;
            handleNewSynonyms(gene, g);
        } else {
            handleUpdateSynonyms(gene, g);
        }

        g.setGeneSynopsis(gene.getGeneSynopsis());
        g.setGeneSynopsisURL(gene.getGeneSynopsisUrl());

        g.setSymbol(gene.getSymbol());
        g.setName(gene.getName());
        g.setTaxon(gene.getBasicGeneticEntity().getTaxonId());
        g.setType(gene.getSoTermId());

        handleCrossReferences(gene, g);
        handleSecondaryIds(gene, g);

        if (newGene) {
            geneDAO.persist(g);
        }

    }
    
    private void handleSecondaryIds(GeneDTO geneDTO, Gene gene) {
        Set<String> currentIds;
        if(gene.getSecondaryIdentifiers() == null) {
            currentIds = new HashSet<>();
            gene.setSecondaryIdentifiers(new ArrayList<>());
        } else {
            currentIds = gene.getSecondaryIdentifiers().stream().collect(Collectors.toSet());
        }
        
        Set<String> newIds;
        if(geneDTO.getBasicGeneticEntity().getSecondaryIds() == null) {
            newIds = new HashSet<>();
        } else {
            newIds = geneDTO.getBasicGeneticEntity().getSecondaryIds().stream().collect(Collectors.toSet());
        }
        
        newIds.forEach(id -> {
            if(!currentIds.contains(id)) {
                gene.getSecondaryIdentifiers().add(id);
            }
        });
        
        currentIds.forEach(id -> {
            if(!newIds.contains(id)) {
                gene.getSecondaryIdentifiers().remove(id);
            }
        });

    }
    
    private void handleCrossReferences(GeneDTO geneDTO, Gene gene) {
        Map<String, CrossReference> currentIds;
        if(gene.getCrossReferences() == null) {
            currentIds = new HashedMap<>();
            gene.setCrossReferences(new ArrayList<>());
        } else {
            currentIds = gene.getCrossReferences().stream().collect(Collectors.toMap(CrossReference::getCurie, Function.identity()));
        }
        Map<String, CrossReferenceDTO> newIds;
        if(geneDTO.getBasicGeneticEntity().getCrossReferences() == null) {
            newIds = new HashedMap<>();
        } else {
            newIds = geneDTO.getBasicGeneticEntity().getCrossReferences().stream().collect(Collectors.toMap(CrossReferenceDTO::getId, Function.identity(),
                    (cr1, cr2) -> {
                        HashSet<String> pageAreas = new HashSet<>();
                        if(cr1.getPages() != null) pageAreas.addAll(cr1.getPages());
                        if(cr2.getPages() != null) pageAreas.addAll(cr2.getPages());
                        CrossReferenceDTO newCr = new CrossReferenceDTO();
                        newCr.setId(cr2.getId());
                        newCr.setPages(new ArrayList<>(pageAreas));
                        return newCr;
                    }
            ));
        }
        
        newIds.forEach((k, v) -> {
            if(!currentIds.containsKey(k)) {
                gene.getCrossReferences().add(crossReferenceService.processUpdate(v));
            }
        });
        
        currentIds.forEach((k, v) -> {
            if(!newIds.containsKey(k)) {
                gene.getCrossReferences().remove(v);
            }
        });

    }
    
    private void handleNewSynonyms(GeneDTO gene, Gene g) {
        if (CollectionUtils.isNotEmpty(gene.getBasicGeneticEntity().getSynonyms())) {
            List<Synonym> synonyms = DtoConverterHelper.getSynonyms(gene);
            synonyms.forEach(synonym -> synonymService.create(synonym));
            g.setSynonyms(synonyms);
        }
    }

    private void handleUpdateSynonyms(GeneDTO geneDTO, Gene gene) {
        if (CollectionUtils.isNotEmpty(geneDTO.getBasicGeneticEntity().getSynonyms())) {
            List<Synonym> newSynonyms = DtoConverterHelper.getSynonyms(geneDTO);

            List<Synonym> existingSynonyms = gene.getSynonyms();

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
                    synonym.setGenomicEntities(List.of(gene));
                    synonymService.create(synonym);
                });
                existingSynonyms.addAll(newCollect);
            }
        } else {
            // remove all existing synonyms if there are no incoming synonyms
            gene.getSynonyms().forEach(synonym -> synonymService.delete(synonym.getId()));
            gene.setSynonyms(new ArrayList<>());
        }
    }


}
