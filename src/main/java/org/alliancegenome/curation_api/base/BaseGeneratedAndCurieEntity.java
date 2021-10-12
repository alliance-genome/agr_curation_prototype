package org.alliancegenome.curation_api.base;

import java.time.LocalDateTime;

import javax.persistence.*;

import org.alliancegenome.curation_api.view.View;
import org.hibernate.annotations.*;
import org.hibernate.search.engine.backend.types.*;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.*;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.*;

@Data @EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@MappedSuperclass
public class BaseGeneratedAndCurieEntity extends BaseEntity {

    @Id @DocumentId
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @JsonView({View.FieldsOnly.class})
    @EqualsAndHashCode.Include
    protected Long id;
    
    @KeywordField(aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES)
    @Column(unique = true)
    @JsonView({View.FieldsOnly.class})
    @EqualsAndHashCode.Include
    private String curie;
    
    @GenericField(aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES)
    @CreationTimestamp
    @JsonView({View.FieldsOnly.class})
    private LocalDateTime created;
    
    @GenericField(aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES)
    @UpdateTimestamp
    @JsonView({View.FieldsOnly.class})
    private LocalDateTime lastUpdated;

}
