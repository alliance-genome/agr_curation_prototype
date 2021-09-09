package org.alliancegenome.curation_api.interfaces.rest;

import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

@Data
public class APIVersionInfo {
    
    @JsonView(View.FieldsOnly.class)
    private String name;
    @JsonView(View.FieldsOnly.class)
    private String version;
}
