package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.view.View;
import org.hibernate.search.engine.backend.types.Aggregable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@AGRCurationSchemaVersion(min = "1.3.2", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { Agent.class })
@Table(indexes = {
		@Index(name = "person_createdby_index", columnList = "createdBy_id"),
		@Index(name = "person_updatedby_index", columnList = "updatedBy_id"),
		@Index(name = "person_uniqueid_index", columnList = "uniqueid"),
		@Index(name = "person_allianceMember_index", columnList = "allianceMember_id")
})
public class Person extends Agent {

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "uniqueId_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@Column(unique = true, length = 2000)
	@JsonView({ View.FieldsOnly.class })
	@EqualsAndHashCode.Include
	protected String uniqueId;
	
	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "firstName_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({ View.FieldsOnly.class, View.PersonSettingView.class })
	private String firstName;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "middleName_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({ View.FieldsOnly.class, View.PersonSettingView.class })
	private String middleName;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "lastName_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({ View.FieldsOnly.class, View.PersonSettingView.class })
	private String lastName;

	@KeywordField(aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES)
	@ElementCollection
	@JsonView({ View.FieldsAndLists.class, View.PersonSettingView.class })
	@JoinTable(indexes = @Index(columnList = "person_id"))
	private List<String> emails;

	@KeywordField(aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES)
	@ElementCollection
	@JsonView({ View.FieldsAndLists.class, View.PersonSettingView.class })
	@JoinTable(indexes = @Index(columnList = "person_id"))
	private List<String> oldEmails;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "orcid_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({ View.FieldsOnly.class, View.PersonSettingView.class })
	@Column(unique = true)
	private String orcid;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "primaryExternalId_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({ View.FieldsOnly.class, View.PersonSettingView.class })
	@Column(unique = true)
	private String primaryExternalId;

	@OneToMany(mappedBy = "person")
	@JsonView({ View.PersonSettingView.class })
	private List<PersonSetting> settings;

	@ManyToOne
	@JsonView({ View.FieldsOnly.class, View.PersonSettingView.class })
	private AllianceMember allianceMember;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "oktaId_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({ View.PrivateOnlyView.class, View.PersonSettingView.class })
	@Column(unique = true)
	private String oktaId;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "oktaEmail_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({ View.PrivateOnlyView.class, View.PersonSettingView.class })
	@Column(unique = true)
	private String oktaEmail;

	@JsonView({ View.PrivateOnlyView.class, View.PersonSettingView.class })
	private String apiToken;
	
}
