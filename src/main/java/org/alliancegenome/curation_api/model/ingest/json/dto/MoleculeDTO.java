package org.alliancegenome.curation_api.model.ingest.json.dto;

import java.util.List;

import org.alliancegenome.curation_api.base.BaseDTO;

import lombok.Data;

@Data
public class MoleculeDTO extends BaseDTO {
    private BasicGeneticEntityDTO basicGeneticEntity;
    private String name;
    private String inchi;
    private String inchiKey;
    private String iupac;
    private String formula;
    private String smiles;
    private List<String> synonyms;
    private List<CrossReferenceDTO> crossReferences;
}
