# AGR curation release notes
 
https://agr-jira.atlassian.net/wiki/spaces/ATEAM/overview

## v0.35.0
* New features
   * Load FBcv into the persistent store (SCRUM-2190)
   * Implement Gene Type field/column for Genes data table (SCRUM-3983)
   * Turn off auxiliary clean up for associated entities (e.g. clean up of disease annotations based on a gene load) - public site Indexer work	(SCRUM-4150)
   * SQTR Gene Associations import into Persistent store (SCRUM-4185, SCRUM-4187, SCRUM-4188)
   * HTPdataset loaded into persistent store (SCRUM-4192,SCRUM-4303, SCRUM-4341)
   * Make obsolete disease annotations hidden by default for all MOD default settings	(SCRUM-4277)
   * HTPdataset ZFIN Fms files having wrong naming of property	(SCRUM-4328)
   * Clean up erroneous DOTerms (SCRUM-4337)
   * Importing expression annotations into the persistent store - Load ExpressionPattern, TemporalContext, and AnatomicalSite (SCRUM-3952)
   * Implement programmatic, regular purging of exception message history for all exceptions older than 2 weeks (SCRUM-4377)
   * HTPdatasetSample import P1 - Creating entities and migration file (SCRUM-4426)
   * Reenable downloading of all exception messages for a given data load (SCRUM-4452)
   * P1: Expression Atlas Import: Load accession data files from Ensembl, etc... (SCRUM-4118)
   * GFF Loading (Load assembly, transcripts, exons, and CDSs without positional information; Load positional information for Transcripts, Exons, and CDSs; Load associations between Genes, Transcripts, Exons, and CDSs) (SCRUM-4174, SCRUM-4175, SCRUM-4176)
   * Associate Expression Annotations with Go Slim Terms	(SCRUM-4300)
   * Implement caching of references for DQM loads	(SCRUM-4336)
   * Accommodate BIOGRID-ORCS loader data type: to establish 3rd part link out to BIOGRID from gene page phenotypes section P1 - create DTO, load via migration, and loading infrastructure (SCRUM-4440)
   * Investigate: Accommodate BIOGRID-ORCS loader data type: to establish 3rd part link out to BIOGRID from gene page phenotypes section (SCRUM-4471)
   * Load VariantGenomicLocationAssociation from FMS submissions and generate HGVSg identifiers	(SCRUM-4499)
   * Test effect of removing AuditedObject fields on disease annotation indexing speed	(SCRUM-4520)
   * Java model changes for disease genetic modifier changes to split based on modifier type	(SCRUM-4523)
   * Curation UI changes to Disease Annotations table to split out Genetic Modifiers into 3 separate fields for each of Gene, Allele and AGM modifier types	(SCRUM-4524)
* Fixes and maintenance
   * Edit cancelation button getting squeezed out of frame on alpha data tables (SCRUM-4236)
   * Constructs data table on alpha-curation throwing 500 error and not loading	(SCRUM-4259)
   * Fix SQTR Gene Association Integration Tests (SCRUM-4299)
   * Curl timing out when uploading to persistent store (SCRUM-3683)
   * Annotations and interactions not indexed on alpha-curation (despite attempts to reindex) (SCRUM-4254)
   * Loads not showing in the Data Loads page "Data Processing Info Table" widget (SCRUM-4428)
   * New "Running Time" showing negative time counting down	(SCRUM-4429)
   * Molecule table columns expand automatically when sorting	(SCRUM-4531)


## v0.34.0
* New features
  * Paralogy annotations loaded into persistent store (SCRUM-4086, SCRUM-4088, SCRUM-4089, SCRUM-4090)
  * Expression annotations loaded into persistent store (SCRUM-3708)
  * HTPTags loaded into persistent store (SCRUM-4221)
  * Data load file history sorted by Load Started timestamp (SCRUM-4204)
  * Data loads stopped if error rate exceeds cutoff threshold (SCRUM-3871)
  * SGD strain background added to defining fields of disease/phenotype annotations (SCRUM-4137)
  * Turned off cleanup of associated annotations/associations (SCRUM-4107)
* Fixes and maintenance
  * Fixed reporting of out-of-date errors in data loads tooltip (SCRUM-4210)
  * Fixed loading of genes with duplicate cross-references (SCRUM-4160)
  * Fixed lading of interactions via NCBI_Gene cross-references (SCRUM-4159)
  * Fixed orthology load cleanup (SCRUM-4146)
  * Fixed duplicate references on interactions (SCRUM-4114)
  * Fixed subject filter on disease annotations table (SCRUM-4113)
  * Fixed association of secondary phenotype annotations with primary annotations that have condition relations (SCRUM-4062)
  * Fixed downloads of large exception lists (SCRUM-3871)
  * Fixed missing exception messages in downloads (SCRUM-4162)

## v0.33.0
* New features 
  * Importing gene interactions into the persistent store (phase 2) (SCRUM-3809)
  * Load SARS-CoV-2 genes into the persistent store (SCRUM-3929)
  * Remove apparently redundant subclass (e.g. Gene Disease Annotations) links from curation Dashboard (SCRUM-3954)
  * Hide obsolete rows (filter obsolete = false) when resetting the table to default settings (SCRUM-3960)
  * Reduce empty space in all remaining tables to create a more compact representation (SCRUM-3968)
* Fixes and maintenance
  * Refactor Constructs Templates Table into Components (SCRUM-3755)
  * Refactor Genes Table Templates into Components (SCRUM-3759)
  * Refactor Literature Reference Table Templates into Components (SCRUM-3762)
  * Refactor useGenericDataTable's useQuery into a seperate useGetTableData hook (SCRUM-3803)
  * Upgrade to React 18 (SCRUM-3894)
  * Obsolete GO & CHEBI terms cluttering up root level Tree View (SCRUM-3918)
  * Fix interaction bulk load cleanup failures (SCRUM-3944)

## v0.32.0
* New features
  * Loading Gene phenotype annotations from the FMS (SCRUM-3686)
  * Loading Gene molecular interactions from the FMS - phase 1 (SCRUM-3806)
  * Lading Allele phenotype annotations from the FMS (SCRUM-3711)
  * Enable MOD-specific required fields for new disease annotations and editing existing annotations (SCRUM-2755)
  * GraphQL-like mechanism to trim unnecessary fields from API responses (SCRUM-3800)
  * Added functionality to OR filters in search
* Fixes and maintenance
  * In search documentation
  * In display after clicking on save and add on the New annotation form
  * Change default number of rows rendered for each curation data table to 10 (SCRUM-3857)
  * Refactor Species Table Templates into Components (SCRUM-3765)
  * Refactor Allele Table Templates into Components (SCRUM-3753)￼
  * Vocabulary term lookup when creating experiments
  * Changed the mod related setting specifically to ZFIN
  * Changed the data tables items into alphabetical order
  * Increase reporting interval for bulk loads
  * Hardcode the RGD publication ID for OMIM and ORPHA ID cross references for Human gene phenotype annotations from the FMS(SCRUM-3883)
  * ATP:0000035 not showing up in ATP ontology Tree View (SCRUM-3884)
 
## v0.31.0
* New features
  * Replaced Negated column/field with NOT in disease annotation table and new annotation form (SCRUM-3794)
  * Enabled loading of AGM Phenotype Annotations from FMS (SCRUM-3712)
  * Added restrictions to Allele Gene Associations to prevent duplicate associations or multiple is_allele_of associations (SCRUM-3625 & SCRUM-3626)
  * Added GENO Ontology load (SCRUM-3694)
  * Establised persistence of new annotation pop-up field visibility settings for disease annotations (SCRUM-3661)
* Fixes and maintenance
  * Aligned codebase with LinkML v2.2.0 (SCRUM-3515)
  * Fixed WB Molecular bulk load error (SCRUM-3823)
  * Added warning and provided openAPI definition download link on Swagger page (SCRUM-3779)
  * Automated cleanup of exception messages from old submissions (SCRUM-3795)
  * Removed unnecessary glyphs from ontology tree where no child terms (SCRUM-3249)
  * Improved speed of closure queries to address failing ontology loads (SCRUM-3780)
  * Improved speed of ID retrieval for data cleanup (SCRUM-3770)

## v0.30.0
* New features
   * Enable "New Annotation" pop-up field-visibility custom settings for disease annotations (SCRUM-2680)
   * Add and enable "Show All Fields" button for "New Annotation" pop-up for disease annotations (SCRUM-2756)
* Fixes and maintenance
   * Address deletion of allele-gene associations on the Allele detail page Allele-Gene Associations table	(SCRUM-3637)
   * ZFIN Allele load mostly failing with apparent unrecognized PMID not present in most cases	(SCRUM-3640)
   * Duplicate disease annotations (same Unique ID) on production curation (SCRUM-3641)
  
## v0.29.0
* New features
   * Allele detail page: Visually emphasize field names/sections and visually demphasize 'Add' buttons (SCRUM-3343)	
   * Report Failed Loads table in Data Loads page UI (SCRUM-3440)	
   * Notify developers via Slack of failed curation system loads (SCRUM-3470)	
   * Provide "Saving in progress" indicator and inactivate interaction with page on allele detail pages (SCRUM-3536)	
   * Move "Add" buttons in Allele detail page to right of field names (in-line)(SCRUM-3551)	
   * Reduce empty space in disease annotations table to create a more compact representation (SCRUM-3564)	
   * Enable downloading of all exception messages for a given data load (SCRUM-2639)	

* Fixes and maintenance
   * Upgrade python version used in agr_db_backups (SCRUM-3094)	
   * FB constructs failing to load (SCRUM-3613)	
   * Fix failing Construct Association loads (SCRUM-3620)	
   * SGD Allele load failing with uninterpretable exception messages (SCRUM-3630)	
   * Attempting to display FB constructs with secondary IDs in Constructs table (on alpha) throws error (SCRUM-3631)	
   * RGD Allele load entries failing incorrectly (SCRUM-3633)
   * Create Json View to Limit data from Disease Annotations (SCRUM-3588)	

## v0.28.0-rc2 
* Updates quarkus to version 3
* Fixes and maintenance 

## v0.27.2 & v0.27.3
 * Fixes and maintenance
   * Fixes for permissions and dependent workflow triggering on automated release creation (SCRUM-3444)

## v0.27.1
 * Fixes and maintenance
   * Automated release creation (SCRUM-3444)
   * Added branch-tagging to latest beta and production container images (SCRUM-3415)
   * Updated SSL Cert to Auto-renewed Amazon-issued Certificate

## v0.27.0
 * New features
   * Allele details page changes:
      * Enabled display and editing of Nomenclature Events of alleles on allele details page (SCRUM-3345)
      * Enabled load and display of Allele-Gene associations on allele details page (SCRUM-2248)
      * Moved allele detail page references into table (SCRUM-3353)
   * Enabled load and display of variant table (SCRUM-3321)
   * Enabled load and display of ConstructGenomicEntityAssociation (construct table) (SCRUM-3325)
 * Fixes and maintenance
   * Clean up and consolidate vocabularies into sets (SCRUM-3322)
   * Fixed dropdown selection registration bug (SCRUM-3357)
   * Added association classes to linkML Schema Version Table reporting compatible LinkML versions
   * Prevent duplicates in multi-select autocomplete (SCRUM-3387)

## v0.26.0
 * New features
   * Improved healthcheck endpoints and added health page (#1228 and #1230)
   * Added FMS Data Files page (#1247)
   * Enabled display and editing of Name property of alleles on Allele Detail pages (SCRUM-3124)
   * Expanded allele nomenclature events pop-up display to include created/updated AuditedObject properties (SCRUM-3220)
   * Enabled display and editing of Mutation Type property of alleles on Allele Detail pages (SCRUM-3128)
   * Enabled load and display of constructs (SCRUM-199)
   * Enabled display and editing of ... of alleles on allele details page:
      * Inheritance Modes (SCRUM-3132)
      * Secondary IDs (SCRUM-3127)
      * Database Status (SCRUM-3131)
      * Related Notes (SCRUM-3133)
      * Functional Impacts (SCRUM-3129)
      * Sumbol (SCRUM-3125)
      * GermlineTransmissionStatus (SCRUM-3130)
   * Display crossReferences in table on allele detail page (SCRUM-3200)
   * Display MOD release version on DataLoads page (SCRUM-3336)
 * Fixes and maintenance
   * Fixed drop down selection bug on Allele Detail page (SCRUM-3316)
   * Increase readibility of experimental conditions in editing interfaces (SCRUM-3027)
   * Fixed error on saving row in Experiments table (SCRUM-3290)
   * Fix failing WB Molecule loads (SCRUM-3337)
   * ConditionRelation and ExperimentalCondition cleanup (SCRUM-3306)
   * Display next future (a.k.a current stage) release as default by @markquintontulloch (SCRUM-3268)
   * Update to align datamodels with LinkML v1.9.0 (SCRUM-3278)
   * Quarkus update for security fixes (SCRUM-3303)

## v0.25.0
 * New features
   * Display and editing of allele synonyms on allele detail page (SCRUM-3126)
   * Keep the ontology browser term information panel in view while browsing the (SCRUM-3250)
   * Display uniqueId error message in row edit mode of Disease Annotations table (SCRUM-3248)
   * Display validation errors of hidden fields in row edit mode (SCRUM-3255)
   * Enable access to curation API using admin tokens without Okta user (SCRUM-3078)
   * Enable sorting of references by primary xref (SCRUM-2725)
 * Fixes and maintenance
   * Update curation system to align with LinkML v1.8.0 (SCRUM-3156)
   * Fix short citation display, sorting, and filtering in Literature References table (SCRUM-3247)

## v0.24.0
 * New features
   * Load and display Allele 'allele_nomenclature_events' attribute (SCRUM-2340)
   * Restore gene page disease table Source column linkouts based on cross references and persistent store data (SCRUM-2372)
   * Show reference short citation in Disease Annotations table "New Annotation" pop-up human-readable values (SCRUM-2784)
   * Disallow entry of duplicate notes	(SCRUM-3080) 
   * Update Alliance gene page disease table download file to include primary annotation disease relation (SCRUM-3091)
   * Add primary disease annotation disease relation to the gene page disease table Annotation Details pop-up	(SCRUM-3092)
   * Update species order of disease annotation display on gene pages (SCRUM-3093)
   * Enable display of read-only allele properties on Allele Detail page (SCRUM-3120)
   * Display allele symbol and curie at top of Allele Detail Page	(SCRUM-3198)
   * Display species/taxon name next to Taxon data entry field on Allele Detail Page (SCRUM-3199)
   * Collapse redundant Based On entries on gene page disease tables	(SCRUM-3201)

 * Fixes and maintenance
   * agr_mati GH-actions authentication credentials cleanup (SCRUM-3211) 
   * Update agr_mati GH-actions authentication to AWS (SCRUM-3210) 
   * Fix display of strain disease genetic modifier to display strain name instead of curie	(SCRUM-3203) 
   * Need to regenerate disease annotation Unique IDs on production Postgres according to new consensus set of defining properties	(SCRUM-3190) 
   * Release and deploy SCRUM-3143 fix to beta (as v0.22.0-rc2) (SCRUM-3144)
   * New Taxons not showing up after they are loaded	(SCRUM-3169)
   * Ensure submitted data meets expectations for cleanup code parameters	(SCRUM-3172)
   * Update agr_curation GH-actions authentication to AWS to prevent usage of long-lived secrets (SCRUM-3068)
   * cln-3.1 WB gene page disease annotations should be consolidated	(SCRUM-2933)
## v0.23.0
 * New features
   * "Vocabulary Label" field added to vocabularies and vocabulary term sets (SCRUM-3167)
   * Created Allele Detail page (SCRUM-3009)
   * Enabled display and editing of simple properties on Allele Detail page (SCRUM-3121)
   * Display short citation in human-readable values of reference field (SCRUM-2784)
 
 * Fixes and maintenance
   * Fixed display of load status in summary row (SCRUM-3117)
   * Fixed reporting of load failures due to invalid URL (SCRUM-2248) 

## v0.22.0
 * New features
   * Loaded orthology data (SCRUM-2710)
   * Loaded Xenbase genes and orthology data (SCRUM-3056)
   * Loaded Biological Spatial Ontology (BSPO) (SCRUM-3062)
   * Loaded Cell Ontology(CL) (SCRUM-3059)
   * Loaded Clinical Measurement Ontology (SCRUM-3060)
   * Loaded Pathway Ontology (PW) (SCRUM-3057)
   * Loaded Uberson Ontology (SCRUM-2454)
   * Loaded Rat Strain Ontology (RS) (SCRUM-3050)
   * Loaded Molecular Interactions Ontology (MI) (SCRUM-2451)
   * Loaded Mouse Pathology Ontology (MPATH) (SCRUM-2452)
   * Loaded Rat Strain Ontology (RS) (SCRUM-3036)
 
 * Fixes and maintenance
   * Bulk load cleanup procedure fixed (SCRUM-3143 & SCRUM-3073)
   * Removed DataProvider reset endpoint (SCRUM-3037)
   * Fixed age error when cancelling edit mode after having duplicated annotation (SCRUM-3030)
   * Fixed disappearing loads in Data Loads page when multiple loads running (SCRUM-3051)
   * Disabled reset buttons during row editing (SCRUM-3030)

## v0.21.0
 * New features
   * Duplicate Disease Annotation popup (SCRUM-2911)
   * Updated display of ontology terms in Tree View (SCRUM-3021)
   * Improved display of notes popups (SCRUM-3005)
   * Switch to single consensus set of defining properties for disease annotations (SCRUM-3008)
   * Loaded Vertebrate Trait (VT) Ontology (SCRUM-2453)
   * Loaded Ascomycete Phenotype Ontology (APO) (SCRUM-2447)
   * Loaded Measurement Method Ontology (MMO) (SCRUM-1206)
   * Added Data Provider column to Alleles table (SCRUM-3006)
   * Added Data Provider column to Genes and AGMs tables (SCRUM-3007)
   * Prevented loading of duplicate notes (SCRUM-2828)
 * Fixes and maintenance   
   * Allele indexing optimizations (SCRUM-2982) 

## v0.20.1
 * Fixes
 	* Temporary endpoint to reset data provider on all disease annotations (SCRUM-3037)

## v0.20.0
 * New features
   * Modify curation data submission API endpoint to include a "no clean up" option boolean (SCRUM-2932)
   * Load and display Allele 'allele_germline_transmission_status' attribute (SCRUM-2333)
   * Load and display Allele 'allele_functional_impacts' attribute (SCRUM-2334)
   * Load and display Allele 'related_notes' attribute (SCRUM-2341)
   * Expand gene page disease table "Annotation Details" pop-up to include all persistent store disease annotation information	(SCRUM-2394)
  
 * Fixes & maintenance
   * Enable filtering on non-PMID references of disease annotations on gene pages (SCRUM-2727)	
	* Upgrade machine size for the FMS to allow Paralogy work to continue (SCRUM-2848)
	* Add mock data for all data table tests (SCRUM-2900)
   * Upgrade quarkus to 3.0.3.Final (SCRUM-2908S)
	* Address outstanding slow Cacher query after Neo4J upgrade to version 5.5 (SCRUM-2909)
   * Fix broken public site API as a result of the Neo4J upgrade (SCRUM-2913)
   * Audit allele indexes on the database	(SCRUM-2923)
   * UI miss-handling of cross references is breaking the disease annotations section in the main site (SCRUM-2925)
  
## v0.19.1
 * Fixes
 	* Fix cleanup of biological entities associated with disease annotation genetic modifiers (SCRUM-2918)
 	* Fix duplication of slot annotations on reload of bulk file (SCRUM-2949)

## v0.19.0
 * New features
    * Synchronizes to LinkML v1.7.1 release (SCRUM-2836)
    * Enable specification of object type in pending edit tooltip (SCRUM-2891)
    * Loading of gene secondary IDs and display of gene slot annotation fields (SCRUM-2800)
    * Sort out display/format text in UI code (SCRUM-2890)
    * Display and enable editing of allele synonyms (SCRUM-2342)
    * added HP Ontology (SCRUM-2449)
 * Fixes & maintenance
    * Remove deprecated secondaryId index references (SCRUM-2841)
    * Added basic component tests to most data tables (SCRUM-2877)
    * Fix table name (SCRUM-2342)
    * Remove legacy secondary IDs (SCRUM-2841)
    * Fix geneticModifiers autocomplete (SCRUM-28236)
    * Fix popup dialog autocomplete selection bug (SCRUM-2885)
    * updated row index for Autocomplete editors (SCRUM-2838)
    * Fix gene table tooltips (SCRUM-2870)
    * Fixed row cancel error (SCRUM-2838)
    * Fixed tooltip superscripts on GenesTable (SCRUM-2773)
    * Migration to populate null data providers (SCRUM-2830)

## v0.18.0
 * New features
    * Rearrange "New Annotation" pop-up fields for disease annotations (SCRUM-2754)
    * Re-enable editing of allele symbol and name (SCRUM-2383)
    * Load Phenotype and Trait Ontology (PATO) into the persistent store (SCRUM-2450)
    * Load Human Phenotype Ontology (HP) into the persistent store (SCRUM-2449)
 * Fixes & maintenance
    * Fix some table state persistence bugs (SCRUM-2754)
    * Fix column reordering persistence (SCRUM-2457)
    * Fix Gene names being displayed in symbol field (SCRUM-2840)

## v0.17.0
 * New features
    * Improved reporting of bulk load deprecation events (SCRUM-2761)
    * Implemented error boundaries for better error handling in UI (SCRUM-2587)
    * Prevented loading of annotations referencing ECOTerms not in AGR subset (SCRUM-2748)
    * Enabled loading of biological entities with non-canonical species (SCRUM-2679)
    * Enabled filtering of experimental conditions by relation type in disease annotation table (SCRUM-2718)
    * Improved filtering of evidence codes (SCRUM-189)
    * Implemented autopopulated dataProvider field for biological entities (SCRUM-2711)
    * Improved error reporting for post-load processing of data load files (SCRUM-2455)
    * Implemented multi-select picklist for data provider in diseaes annotations table (SCRUM-2509)
    * Enabled users to delete local storage entries in their profile page (SCRUM-2654)
    * Implemented first non-trivial tests in UI testing framework (SCRUM-2282)
    * Loaded WBPhenotype ontology (SCRUM-2446)
    * Prevented loading of data files formatted to unsupported LinkML schema versions (SCRUM-2627)
    * Implemented consistent ordering of data loads (SCRUM-2593)
    * Enabled deletion of disease annotations from persistent store with appropriate constraints (SCRUM-1907)
 * Fixes & maintenance
    * Rendering of boolean filter selections after table reset bug fixed (SCRUM-2757)
    * Rendering of gene name and symbol superscripts fixed (SCRUM-2678)
    * Forced selection from dropdown for biological entity fields (SCRUM-2698)
    * Implemented cleanup of non-submitted ingest sets in data loads (SCRUM-2696)
    * Created centralised store for sort and filter fields for each table (SCRUM-2650)
    * Fixed indexing of slot annotation fields upon insert (SCRUM-2658)
    * Added check for valid fields in users' local storage (SCRUM-2526)
    * Added integration tests for creation of objects with only required fields (SCRUM-2601)
    * Fixed data loads exception popup refresh when no exceptions present (SCRUM-2594) 

## v0.16.0
 * New features
    * Prevent loading of biological entries from non-canonical species (SCRUM-2477)
    * Added Load Resource Descriptors file as load (SCRUM-2459)
    * Updated subject search ordering (#879)
 * Data updates
    * Updated FlyBase DiseaseAnnotation uniqueId fields to better capture unique entities (SCRUM-2503)
 * Maintenance
    * Tackle gh-actions deprecation warnings #881
    * enabled scheduled reindexing (SCRUM-2466)
 * Bugfixes:
    * SCRUM-2493 Filters issue on click og Rest to MOD button solved by @kthorat-prog in #883

## v0.14.0
 * New features
    * Added confirmation popup to data loads widget upload file UI functionality
    * Exclude obsolete results from AutoComplete suggestions (except reference field) (SCRUM-2327)
    * Enabled deprecation (instead of deletion) of public disease annotations (1st iteration, SCRUM-2296 & SCRUM-2298)
    * Added alliance member affiliation (#808)
    * Display human-readable subject name underneath subject field (#816)
 * Data changes
    * Synchronised codebase with LinkML v1.5.0 (NameSlotAnnotations) (SCRUM-2311, SCRUM-2345)
 * Bug fixes
    * Fixed disabled filter after validation fail in Allele table (SCRUM-2326)
 * Maintenance
    * Upgrade quarkus to 2.14.2 (SCRUM-2330)

## v0.13.0
 * New Features
    * Added 'Mutation types' to Allele table (SCRUM-2267 & SCRUM-2268)
    * Added 'Asserted Genes' and 'Asserted allele' fields to form for new disease annotations (SCRUM-2167)
    * Added Upload button for UI submission of bulk upload files (SCRUM-2092)
    * Added initial UI automated tests (SCRUM-1737)
    * Included invalid entity in bulk upload error message (SCRUM-2314)
    * Replaced Allele 'In Collection' dropdown menus with text search / Autocomplete editor (SCRUM-2293 & SCRUM-2295)
    * Synchronised codebase with LinkML v1.4.0 (SCRUM-2266)
    * Populated new Vocabulary form with default value for 'obsolete' field (SCRUM-2301
    * Sorted Vocabularies by name in editor dropdown (SCRUM-2302)
 * Fixes & maintenance
    * Autocomplete code refactored (SCRUM-2059)
    * Updated Github actions (SCRUM-2269)
    * Added check for Autocomplete selection on biological entity fields (SCRUM-2252)
    * Implemented handling of unexpected API errors upon saving entries in UI (SCRUM-2251)

## v0.12.0
 * New Features
    * New service to call MaTI (SCRUM-2011)
    * Removed extra SiteLayout from routes (SCRUM-2160)
    * Renaming of Experiments table and column (SCRUM-2069, SCRUM-2070)
    * Refactor DTO validation code (SCRUM-2145)
    * Return exact matches only from dropdown filters (SCRUM-2139)
    * Make allele attributes editable (SCRUM-2161)
    * Add LinkML version submission documentation 
    * Experiments dropdown to NewAnnotationForm (SCRUM-1917)
    * Enable entering identifiers of experimental condition components in autocomplete (SCRUM-2099)
 * Fixes
    * Fix for OR on multiselect fields 
    * Fix synonym and reference persistence
    * Bug fixes and integration tests for updates with null value ( SCRUM-2174)
    * Fix reference autosuggest (SCRUM-2161)
    * Fix blank screen on ref deletion (SCRUM-2161)
    * Delete existing notes when doing DA bulk upload
    * Added indexes to allele touching tables 

## v0.11.0
 * New Features
    * Enable creation of disease annotation; 1st & 2nd iteration pop-up (SCRUM-1709, SCRUM-1903)
    * Enabled simple deletion of disease annotations (SCRUM-1890)
    * Condition Summaries & Unique IDs for Experimental Conditions (plan to retire Condition Statements)
       * Generate consistent condition_summary entries for experimental conditions (SCRUM-2036)
       * Generate consistent unique_id values for experimental conditions (SCRUM-2037)
       * Clean up experimental conditions (redundant entries + usage of condition_summary in place of condition_statement) (SCRUM-2038)
       * Add tooltip to condition autosuggest options to display unique_id values for each experimental condition displayed (SCRUM-2040)
       * Replace experimental condition autosuggest display with condition_summary instead of condition_statement (SCRUM-2039)
    * Add display of compatible LinkML schema versions (SCRUM-2091)
 * Fixes
    * Fix broken autocomplete for experimental conditions (SCRUM-2089)
    * Fix sort order of data loads by reverse chronological order (SCRUM-2010)
    * Fix missing ontologies from dashboard (SCRUM-2076)
    * Fix blank screen bug in Data Loads widget (SCRUM-2042)
    * Clean up and address stale file loads in Data Loads widget (SCRUM-2004)

## v0.10.0

 * New features
    * Enabled disease annotation deletion (through UI) (SCRUM-1890)
    * Enabled disease annotation creation (through UI) (SCRUM-1709)
 * Data and schema changes
    * Synchronised curation application data model with LinkML schema v1.2.4 (SCRUM-1926)
 * Bugfixes, minor enhancements & maintenance
    * Enabled preventing disease-annotation save without reference/subject/object (SCRUM-1916)
    * Added additional integration tests for required fields (SCRUM-1939)
    * Minor indexing improvements (for the index everything endpoint)
    * Minor deployment notification fixes
    * Updated data validation to treat empty strings as empty values (SCRUM-1942)
    * Updated bulk load to correctly new null values on update (SCRUM-1975)
    * Fixed bulk load last loaded date (SCRUM-2000)
    * Cleanup of inconsistent records in DB (SCRUM-2005)
    * Fixed experimentalConditions UI filter (SCRUM-2003)
    * Fixed file sorting in data loads widget (SCRUM-2010)
    * Added additional note reference validation (SCRUM-2028)

## v0.9.0

 * New features
    * Enabled deletion of relations in Condition Relation table (SCRUM-1605)
    * Enabled creation of Experimental Conditions (SCRUM-1848, SCRUM-1849, SCRUM-1850)
    * Added ATP ontology (SCRUM-1840)
    * Automatic population of createdBy field when creating new entries (SCRUM-1868)
    * Repurposed Condition Relation Handles table for all Condition Relations, with or without handle (SCRUM-1867)
    * Enabled deletion of controlled vocabulary terms (SCRUM-1889)
    * Added parents, ancestors, and closure to ontologies
    * Enabled per-class reporting of LinkML schema version compatibility

 * Bugfixes, minor enhancements & maintenance
    * API updates to enable deletion of disease annotations (SCRUM-1711)
    * Improved filter functionality for Experiments and Experimental Conditions columns in Disease Annotation table (SCRUM-1816)

## v0.8.0
 * New features
    * Enabled ConditionRelation creation for disease annotation (SCRUM-1405)
    * Enabled dissociation of ConditionRelation from DiseaseAnnotation (SCRUM-1563)
    * Added inferred/asserted genes/aleles (SCRUM-1795, SCRUM-1796, SCRUM-1797)
    * Enabled AGM creation (for integration into ZFIN workflow) SCRUM-1724
    * Enabled deletion of experimental conditions (SCRUM-1799)
 * Bugfixes, minor enhancements & maintenance
    * Fixed argument display single type indexing endpoints (#571)
    * Fix activation of Keep Edits button on adding a note to disease annotation (SCRUM-1808)
    * Fixed experiment dropdown update reset on cancel (SCRUM-1806)
    * Append newly created condition relation handles to the top of the table (SCRUM-1801)
    * Switched search indexing from ElasticSearch to OpenSearch (SCRUM-1646, SCRUM-1647, SCRUM-1648)

## v0.7.0
 * New Features
    * SCRUM-1743 new Vocabularies Table by @adamgibs in #566
    * SCRUM-1555 Make existing disease annotation ‘condition relations’ editable (no handle in use) by @markquintontulloch in #554
    * SCRUM-1556 Enable display and editing of existing disease annotation experiment handles by @markquintontulloch in #563
    * SCRUM-1765 Bring functionality of editing of 'related notes' in Disease Annotation table in line with that of 'condition relations' by @markquintontulloch in #559
 * Maintenance
    * SCRUM-1712 Synchronisation of References with Literature System by @markquintontulloch in #552, #557, #561, #564
    * SCRUM-1732 Fixed validation of references in conditionRelations #560
    * SCRUM-1710 Fixed multiple tooltips bug by @markquintontulloch in #565
    * SCRUM-1722 Fixed multi-line cell warnings bug by @adamgibs in #562
    * Added indexing params to reindexing endpoints by @mluypaert in #555
    * SCRUM-1676 Refactor of AutocompleteEditor template by @adamgibs in #547
    SCRUM-1756 Fixed saving of notes attached to disease annotations bug by @markquintontulloch in #553

## v0.6.0
 * New Features
    * SCRUM-1537 make condition relation handle table editable by @cmpich in #491
    * SCRUM-1537 validate null handle, disallow same handle/pub combination by @cmpich in #511
    * SCRUM-1537 add auto-complete on reference column on condition-relation-handle-table by @cmpich in #518
    * Filters icon added to Genric Data Table Component by @kthorat-prog in #505
    * Filters Label added on Roweditor frozen column by @kthorat-prog in #513
    * Obsolete Column added to disease annotations table by @kthorat-prog in #514
 * Data Load
    * SCRUM-1609, SCRUM-1611, SCRUM-1612 Loading and testing of obsolete field by @markquintontulloch in #503
 * Maintenance
    * Misc refactors by @adamgibs in #504
    * Column header names changed for 2 columns by @kthorat-prog in #508
    * SCRUM-1603 added VocabularyTermUpdate view to return vocabulary field by @adamgibs in #510
    * SCRUM-1598 Added DiseaseAnnotationUpdate view to conditions by @adamgibs in #512
    * null check added to col variable in useEffect by @kthorat-prog in #517
    * fixed autocomplete hover bug by @adamgibs in #520
    * SCRUM-516 SCRUM 1537 create getEntityType() method to select and display the auto-complete list, auto-complete for DA table by @cmpich in #519
    * SCRUM-1675 added check for conditions with no handle or reference by @adamgibs in #521

## v0.4.0
 * Data loading
    * SCRUM-1205 Anatomy stage ontologies by @markquintontulloch in #3117
    * SCRUM-1319 Add AuditedObject fields to disease annotations by @markquintontulloch in #314 
    * SCRUM-1205 MmusDv load update by @markquintontulloch in #324
    * SCRUM-737 update - SGD strain background filter by @markquintontulloch in #327
    * SCRUM-1298 Validate uniqueId doesn't exist after editing experimentalCondition by @markquintontulloch in #344
    * SCRUM-731 disease qualifiers by @markquintontulloch in #338
    * SCRUM-1326 Add condition summary to experimental conditions by @markquintontulloch in #352
    * SCRUM-1155 Limit ZECO terms for conditionClass to AGR SLIM by @markquintontulloch in #355
    * SCRUM-1369 Hide Parental Populations column in AGM table by default by @markquintontulloch in #353
    * Make ExperimentalCondition uniqueId generation consistent by @markquintontulloch in #360
    * SCRUM-1427 Submission of notes with disease annotations by @markquintontulloch in #392
    * SCRUM-1435 Backend code for conditionRelation editing via diseaseAnnotation edits by @markquintontulloch in #401
    * fix AGM creation logic by @cmpich in #370
    * SCRUM-1232 - Add Xenopus ontologies by @markquintontulloch in #424
    * SCRUM-1232 Enable multiple ontology loads from single OWL file by @markquintontulloch in #425
    * SCRUM-1232 Add additional namespace for XBA load by @markquintontulloch in #428 
 * UI Features
    * UI reskin by @adamgibs in #305, #321, #334
    * Editor Refactor by @adamgibs in #309
    * Header freeze added by @kthorat-prog in #340
    * Added log server to list of other links by @oblodgett in #317
    * Changed button to use primreact styles by @oblodgett in #339
    * Changed links to open in a new tab by @oblodgett in #341
    * Template made for all datatables across website by @kthorat-prog in #357
    * SCRUM-1318 added filter field for condition relations handle by @adamgibs in #380
    * Added ellipsis & Tooltips for title,abstract & citation columns in Literature Ref Table by @kthorat-prog in #384
    * SCRUM-1403 Autopopulate 'Modified By' field on editing by @markquintontulloch in #381
    * Date modified and Modified fields are updated immediately on success by @kthorat-prog in #399
    * Generic data assignment done for immediate row update & Frozen Column removed by @kthorat-prog in #400
    * Edit Column is frozen and positioned to extreme left side of table by @kthorat-prog in #419
    * Dashboard list of table names sorted alphabetically for both tables by @kthorat-prog in #369
    * Literature Reference added to Dashboard & its Layout is changed to ma… by @kthorat-prog in #374
    * SCRUM-1426 display handle instead of conditions if available by @cmpich in #391
 * UI Maintenance
    * Remove columns with work still to do on UI by @markquintontulloch in #332
    * The ID column made non-sortable by @kthorat-prog in #331
    * SCRUM-737 Disable sgdStrainBackground editing for non-gene annotations by @markquintontulloch in #337
    * reduced DATable width by @adamgibs in #342
    * SCRUM-1368 turned filtering for AGM table name column back on by @adamgibs in #358
    * SCRUM-734 Fix spelling mistake by @markquintontulloch in #366
    * Misc refactor items by @adamgibs in #368
    * Header backgroundcolor added to make it opaque by @kthorat-prog in #376
    * SCRUM-1393 added default column order state to DA table by @adamgibs in #372
    * Changed dialog width, refactored list and changed dialog title by @adamgibs in #375
    * To make the error message float above the cell width for visibility by @kthorat-prog in #383
    * Taxon_ID column display changed and sort order is based on name now by @kthorat-prog in #394
    * Taxon display changed by @kthorat-prog in #398
    * SCRUM-1423 Changes done by Olin only by @kthorat-prog in #405
 * API Feature
    * SCRUM-1435 Add validation endpoint by @markquintontulloch in #402
    * SCRUM-1391 non-loaded allele removal by @mluypaert in #411
    * Added code for integration with Literature Reference ES Server by @oblodgett in #359
    * SCRUM-1371 + SCRUM-1396: non-loaded gene and agm removal by @mluypaert in #436   
 * API Maintenance
    * SCRUM-1364 Add field name to response by @markquintontulloch in #350
    * SCRUM-1412 Fix inconsistency in inchiKey field naming by @markquintontulloch in #377
    * SCRUM-734 property bridge fix by @markquintontulloch in #363
    * Fixed params for tracking number of results by @oblodgett in #365
    * Use .keyword field for searching in the ABC SCRUM-1400 by @oblodgett in #367
    * Removed logging verbosity for loads by @oblodgett in #395
    * Updated ordering by @oblodgett in #397
    * Added comment for upcoming sprint (MERGE AFTER REVIEW) by @oblodgett in #416
    * Added stopped loads to be started (MERGE AFTER REVIEW) by @oblodgett in #417
    * paper handle fixes: index definition by @cmpich in #373
    * SCRUM-1318 handle condition updates, include handle info in unique ID by @cmpich in #385
    * SCRUM-1415 Set UniqueID for new ExperimentalCondition entities by @markquintontulloch in #386
    * Changed exception so they will print in the logs by @oblodgett in #430
    * SCRUM-1499 Improve error reporting for ConditionRelations and RelatedNotes attached to DAs by @markquintontulloch in #433 
 * Other
    * Add MIT LICENSE file by @chris-grove in #421 
    * Removed duplicate code PR into PR by @oblodgett in #426
    * SCRUM-1432 VocabularyTerm validation and integration tests by @markquintontulloch in #396 
    * SCRUM-1279 by @markquintontulloch in #307
    * Scrum 1020 by @kthorat-prog in #319
    * SCRUM-1320 Switching of disease annotation enums to VocabularyTerms by @markquintontulloch in #318
    * Scrum 1296 by @adamgibs in #326
    * Switch vocabularyTerm lookup from search to find by @markquintontulloch in #328
    * SCRUM-1317 by @markquintontulloch in #346
    * Scrum 1341 by @adamgibs in #361
    * SCRUM-1318 by @adamgibs in #362
    * SCRUM-1393 by @adamgibs in #378
    * SCRUM-1394 by @adamgibs in #371
    * Scrum 1377a by @cmpich in #364
    * SCRUM-1315 by @adamgibs in #410
    * SCRUM-1401 by @adamgibs in #412
    * SCRUM-1315 by @adamgibs in #413
    * SCRUM-1401 by @adamgibs in #390
 * DevOps
    * SCRUM-1366 merge from beta to alpha by @markquintontulloch in #351
    * Merge v0.3.0-rc3 changes back into alpha by @mluypaert in #356
    * Merge-back production to beta after v0.3.0 release creation by @mluypaert in #404
    * SCRUM-1417: Release/v0.4.0-rc1 by @mluypaert in #407   
    * Alpha deployment trigger update proposal by @mluypaert in #418
    * Release/v0.4.0 rc2 by @oblodgett in #423
    * PR merge/beta by @oblodgett in #432
    * Release/v0.4.0 rc4 by @kthorat-prog in #442
    * Release/v0.4.0 by @kthorat-prog in #446
    * SCRUM-1424 Renamed migration files to clear patch release version number by @mluypaert in #389
 * Bugs
    * Fixed API Token Login by @oblodgett in #313
    * Fixed status' on startup by @oblodgett in #315
    * Fixed obsolete on vocabulary pages by @oblodgett in #316
    * Fixed disease relation validation by @oblodgett in #320
    * Vocab UI fix by @markquintontulloch in #323
    * The changes to reflect in table as new term/vocab added is added. by @kthorat-prog in #330
    * Fix 'Cannot convert undefined or null to object' error by @markquintontulloch in #333
    * SCRUM-1205 fix for missing term by @markquintontulloch in #335
    * ControlledVocabularyDropdown fixes by @markquintontulloch in #336
    * SCRUM-1306 fixed synonyms bug for subject and with fields by @adamgibs in #343
    * SCRUM-1360 fixed dropdown refocus bug by @adamgibs in #345
    * SCRUM-1364 Experimental condition bug by @markquintontulloch in #347
    * ErrorMessage location is changed to under the editor for Editable tables by @kthorat-prog in #382
    * Table Header bleed through content fixed for other tables by @kthorat-prog in #379
    * Autocomplete search result for synonym fixed for Genetic Modifier field by @kthorat-prog in #408
    * SCRUM-1315 added code to address column reording issue by @adamgibs in #414
    * Bleed through in frozen edit column fixed by @kthorat-prog in #420
    * Fixed Frozen header and frozen column issue by @kthorat-prog in #422
    * Error message box alignment fixed by @kthorat-prog in #434
    * ZIndex made highest for errormessages component by @kthorat-prog in #435
    * Minor fixes by @markquintontulloch in #427
    * Quick fix for annotation returned object by @oblodgett in #415
    * SCRUM-1416 added null check to ListTableCell by @adamgibs in #388
  
## v0.3.0
 * Data loading
    * Implemented NCBITaxonTerm class (SCRUM-338)
    * Enabled linkML format disease annotation submission (SCRUM-1203, SCRUM-122)
    * Loaded and added UI table for read and edit of Experminetal conditions data (SCRUM-1083, SCRUM-1082, SCRUM-725, SCRUM-1153, SCRUM-1154,  SCRUM-1194)
    * Added better load reporting (counts and errors) (#289)
    * Fixed loading consistency and performance (SCRUM-1280, SCRUM-1281)
    * Added type suffix on data load download files (SCRUM-1067)
 * UI improvements
    * Added additional developer links in UI (#270, #271)
    * Enabled data customization session persistence (SCRUM-1036)
    * Enabled checkbox selection filter option for limited-option fields (SCRUM-1022)
 * Other
    * Added API authentication (SCRUM-1045)
    * Implemented flyway as DB schema managment solution (SCRUM-1054)
    * Serveral other fixes and improvements (#243, #245, SCRUM-853, #257, #263, #266, #274, #276, #293)

## v0.2.0
 * Added UI login (authentication) (SCRUM-1034)
 * Enabled filter for Name in Genes Table (SCRUM-1058)
 * Added NCBI Taxonomy ontologies, including Id validation and autopopulation (SCRUM-338, SCRUM-1065)
 * Improved data load error handling display (SCRUM-972)
 * Enabled bulk submission of LinkML JSON files for disease annotations (SCRUM-984)
 * Fixed allele description sort & filter bug (SCRUM-1048)
 * Several UI fixes and improvements (SCRUM-852)

## v0.1.0
 * Added and enabled update/edit on additional disease annotation properties
    * disease field (SCRUM-492, #110, #111, #112, #113, #114)
    * disease relation field (SCRUM-486, SCRUM-962, #119, #120, #132, #134, #135, #136, #137, #152, #153)
    * with field (SCRUM-706, #139, #142, #144, #145, #148)
    * evidence code (SCRUM-881, SCRUM-879)
    * negated field (SCRUM-498, SCRUM-498)
 * Changed curie to unique id for disease annotations #224
 * Loaded various ontologies:
    * MA (SCRUM-857, #141)
    * DAO (SCRUM-887)
    * EMAPA (SCRUM-892)
    * WBbt (SCRUM-873)
    * MP (SCRUM-948, #150)
    * ZFA (SCRUM-868)
    * XCO (SCRUM-102)
    * ZECO (SCRUM-102)
    * GO (SCRUM-897, #160)
    * CHEBI (SCRUM-902)
    * DO (SCRUM-277)
    * SO (SCRUM-1004, SCRUM-1006, SCRUM-1003)
    * Added obsolete colunm (SCRUM-1021)
 * Added Molecule class - loading and read-only diplay (SCRUM-335, #118, #122, #123, #124, #128, #143, #207, #213)
 * Added controlled vocabulary (SCRUM-570, #140)
 * Improved integration testing and automated run at PR validation (SCRUM-280, SCRUM-257, SCRUM-274)
 * UI improvements
    * Various search improvements (SCRUM-600, SCRUM-817, SCRUM-818, #131)
    * prevent updates adding obsolete DO terms (SCRUM-594)
    * Enabled sort and autosuggest on name/symbol fields for object/with columns (SCRUM-970, SCRUM-955)
    * Other autosuggest improvements (SCRUM-715)
    * Enabled user modification of UI data table display (SCRUM-1028)
    * various other fixes and improvements (SCRUM-851, SCRUM-971, SCRUM-499, SCRUM-969, SCRUM-710, #159, ...)
 * LinkML model updates (SCRUM-510)
 * Implemented automatic update loading of input files (#201)
 * Enabled central application logging (SCRUM-547)

## v0.0.31
 * Setup additional application environments
    * Alpha environment (SCRUM-451, SCRUM-459, #83)
    * Beta environment (SCRUM-461, #102)
 * Cosmetic UI changes (SCRUM-568, #104, #100, #98, #97, #94)
 * Enabled in-table editing in disease annotation table
    * disease field (SCRUM-492, #90, #93, #101, #89)
    * subject field (SCRUM-473, #87, #86, #85, #84, #82, #75)
 * Added API endpoints to load disease annotations with evidence codes (SCRUM-322, #99)
 * Reduced reindexing failure rate (#95)
 * Integrated application compilation into Docker image building (#91)
 * Added integration testing for basic gene loading (SCRUM-248, #81, #80, #77, #74, #73, #72)
 * Added several testfile for later inclusion in integration testing (#71)
 * Temporarily disabled auto scaling (commit b54ebb8 in #76)
 * Other minor changes (#96, #92, #78, #69)
