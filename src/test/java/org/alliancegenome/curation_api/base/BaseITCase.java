package org.alliancegenome.curation_api.base;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.alliancegenome.curation_api.constants.OntologyConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.AGMDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.AGMPhenotypeAnnotation;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.AlleleDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.AllelePhenotypeAnnotation;
import org.alliancegenome.curation_api.model.entities.AssemblyComponent;
import org.alliancegenome.curation_api.model.entities.BiologicalEntity;
import org.alliancegenome.curation_api.model.entities.ConditionRelation;
import org.alliancegenome.curation_api.model.entities.Construct;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.ExperimentalCondition;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.GeneDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.GenomeAssembly;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.Organization;
import org.alliancegenome.curation_api.model.entities.Person;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptor;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptorPage;
import org.alliancegenome.curation_api.model.entities.SequenceTargetingReagent;
import org.alliancegenome.curation_api.model.entities.Variant;
import org.alliancegenome.curation_api.model.entities.Vocabulary;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.VocabularyTermSet;
import org.alliancegenome.curation_api.model.entities.associations.alleleAssociations.AlleleGeneAssociation;
import org.alliancegenome.curation_api.model.entities.associations.constructAssociations.ConstructGenomicEntityAssociation;
import org.alliancegenome.curation_api.model.entities.ontology.AnatomicalTerm;
import org.alliancegenome.curation_api.model.entities.ontology.CHEBITerm;
import org.alliancegenome.curation_api.model.entities.ontology.ChemicalTerm;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ECOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ExperimentalConditionOntologyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.GOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.MITerm;
import org.alliancegenome.curation_api.model.entities.ontology.MMOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.OBITerm;
import org.alliancegenome.curation_api.model.entities.ontology.MPTerm;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.entities.ontology.OntologyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.SOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.StageTerm;
import org.alliancegenome.curation_api.model.entities.ontology.UBERONTerm;
import org.alliancegenome.curation_api.model.entities.ontology.WBPhenotypeTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ZECOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ZFATerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleSymbolSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.constructSlotAnnotations.ConstructSymbolSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneSymbolSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectListResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.apache.commons.lang3.StringUtils;

import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ValidatableResponse;

public class BaseITCase {

	public VocabularyTerm addVocabularyTermToSet(String setName, String termName, Vocabulary vocabulary, Boolean obsolete) {
		VocabularyTermSet set = getVocabularyTermSet(setName);
		VocabularyTerm term = createVocabularyTerm(vocabulary, termName, false);

		List<VocabularyTerm> setTerms = set.getMemberTerms();
		setTerms.add(term);
		set.setMemberTerms(setTerms);

		RestAssured.given().
			contentType("application/json").
			body(set).
			when().
			put("/api/vocabularytermset").
			then().
			statusCode(200);

		term.setObsolete(obsolete);

		ObjectResponse<VocabularyTerm> response = RestAssured.given().
			contentType("application/json").
			body(term).
			when().
			put("/api/vocabularyterm").
			then().
			statusCode(200).
			extract().body().as(getObjectResponseTypeRefVocabularyTerm());

		return response.getEntity();
	}

	public void checkBulkLoadRecordCounts(String endpoint, String filePath, String countType, int expectedTotalRecords, int expectedFailedRecords, int expectedCompletedRecords, int expectedSkippedRecords) throws Exception {
		String content = Files.readString(Path.of(filePath));

		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post(endpoint).
			then().
			statusCode(200).
			body("history.counts." + countType + ".total", is(expectedTotalRecords)).
			body("history.counts." + countType + ".failed", is(expectedFailedRecords)).
			body("history.counts." + countType + ".completed", is(expectedCompletedRecords)).
			body("history.counts." + countType + ".skipped", is(expectedSkippedRecords));
	}

	public void checkBulkLoadRecordCounts(String endpoint, String filePath, HashMap<String, HashMap<String, Integer>> params) throws Exception {
		String content = Files.readString(Path.of(filePath));

		ValidatableResponse resp = RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post(endpoint).
			then().
			statusCode(200);

		for (Entry<String, HashMap<String, Integer>> entry: params.entrySet()) {
			resp.body("history.counts." + entry.getKey() + ".total", is(entry.getValue().get("total")));
			resp.body("history.counts." + entry.getKey() + ".failed", is(entry.getValue().get("failed")));
			resp.body("history.counts." + entry.getKey() + ".completed", is(entry.getValue().get("completed")));
			resp.body("history.counts." + entry.getKey() + ".skipped", is(entry.getValue().get("skipped")));
		}
	}

	public void checkFailedBulkLoad(String endpoint, String filePath) throws Exception {
		checkBulkLoadRecordCounts(endpoint, filePath, "Records", 1, 1, 0, 0);
	}

	public void checkSkippedBulkLoad(String endpoint, String filePath) throws Exception {
		checkBulkLoadRecordCounts(endpoint, filePath, "Records", 1, 0, 0, 1);
	}

	public void checkSkippedBulkLoad(String endpoint, String filePath, int nrRecords) throws Exception {
		checkBulkLoadRecordCounts(endpoint, filePath, "Records", nrRecords, 0, 0, nrRecords);
	}

	public void checkSuccessfulBulkLoad(String endpoint, String filePath) throws Exception {
		checkSuccessfulBulkLoad(endpoint, filePath, 1);
	}

	public void checkSuccessfulBulkLoad(String endpoint, String filePath, int nrRecords) throws Exception {
		checkBulkLoadRecordCounts(endpoint, filePath, "Records", nrRecords, 0, nrRecords, 0);
	}

	public HashMap<String, Integer> createCountParams(int total, int failed, int completed, int skipped) {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put("total", total);
		map.put("failed", failed);
		map.put("completed", completed);
		map.put("skipped", skipped);
		return map;
	}

	public AffectedGenomicModel createAffectedGenomicModel(String primaryExternalId, String name, String taxonCurie, String subtypeName, Boolean obsolete) throws Exception {
		return createAffectedGenomicModel(primaryExternalId, name, taxonCurie, subtypeName, obsolete, null);
	}

	public AffectedGenomicModel createAffectedGenomicModel(String primaryExternalId, String name, String taxonCurie, String subtypeName, Boolean obsolete, Organization dataProvider) {
		Vocabulary subtypeVocabulary = getVocabulary(VocabularyConstants.AGM_SUBTYPE_VOCABULARY);
		VocabularyTerm subtype = getVocabularyTerm(subtypeVocabulary, subtypeName);

		AffectedGenomicModel model = new AffectedGenomicModel();
		model.setPrimaryExternalId(primaryExternalId);
		model.setTaxon(getNCBITaxonTerm(taxonCurie));
		model.setSubtype(subtype);
		model.setName(name);
		model.setObsolete(obsolete);
		model.setDataProvider(dataProvider);

		ObjectResponse<AffectedGenomicModel> response = given().
				contentType("application/json").
				body(model).
				when().
				post("/api/agm").
				then().
				statusCode(200).
				extract().body().as(getObjectResponseTypeRefAffectedGenomicModel());
		return response.getEntity();
	}

	public Allele createAllele(String primaryExternalId, String taxonCurie, VocabularyTerm symbolNameTerm, Boolean obsolete) {
		return createAllele(primaryExternalId, primaryExternalId, taxonCurie, symbolNameTerm, obsolete, null);
	}

	public Allele createAllele(String primaryExternalId, String symbol, String taxonCurie, VocabularyTerm symbolNameTerm, Boolean obsolete, Organization dataProvider) {
		Allele allele = new Allele();
		allele.setPrimaryExternalId(primaryExternalId);
		allele.setTaxon(getNCBITaxonTerm(taxonCurie));
		allele.setObsolete(obsolete);
		allele.setInternal(false);
		allele.setDataProvider(dataProvider);

		AlleleSymbolSlotAnnotation alleleSymbol = new AlleleSymbolSlotAnnotation();
		alleleSymbol.setNameType(symbolNameTerm);
		alleleSymbol.setDisplayText(symbol);
		alleleSymbol.setFormatText(symbol);

		allele.setAlleleSymbol(alleleSymbol);

		ObjectResponse<Allele> response = given().
				contentType("application/json").
				body(allele).
				when().
				post("/api/allele").
				then().
				statusCode(200).
				extract().body().as(getObjectResponseTypeRefAllele());
		return response.getEntity();
	}

	public AnatomicalTerm createAnatomicalTerm(String curie, String name) throws Exception {
		AnatomicalTerm anatomicalTerm = new AnatomicalTerm();
		anatomicalTerm.setCurie(curie);
		anatomicalTerm.setName(name);
		anatomicalTerm.setObsolete(false);
		anatomicalTerm.setSecondaryIdentifiers(List.of(curie + "secondary"));

		ObjectResponse<AnatomicalTerm> response = RestAssured.given().
			contentType("application/json").
			body(anatomicalTerm).
			when().
			put("/api/anatomicalterm").
			then().
			statusCode(200).
			extract().body().as(getObjectResponseTypeRefAnatomicalTerm());

		return response.getEntity();
	}
	
	public AssemblyComponent createAssemblyComponent(String primaryExternalId, String name, GenomeAssembly assembly, Organization dataProvider) throws Exception {
		AssemblyComponent assemblyComponent = new AssemblyComponent();
		assemblyComponent.setPrimaryExternalId(primaryExternalId);
		assemblyComponent.setName(name);
		assemblyComponent.setDataProvider(dataProvider);
		assemblyComponent.setGenomeAssembly(assembly);
		
		ObjectResponse<AssemblyComponent> response = RestAssured.given().
				contentType("application/json").
				body(assemblyComponent).
				when().
				put("/api/assemblycomponent").
				then().
				statusCode(200).
				extract().body().as(getObjectResponseTypeRefAssemblyComponent());

			return response.getEntity();
	}

	public BiologicalEntity createBiologicalEntity(String primaryExternalId, String taxonCurie) {
		BiologicalEntity bioEntity = new BiologicalEntity();
		bioEntity.setPrimaryExternalId(primaryExternalId);
		bioEntity.setTaxon(getNCBITaxonTerm(taxonCurie));

		ObjectResponse<BiologicalEntity> response = given().
				contentType("application/json").
				body(bioEntity).
				when().
				post("/api/biologicalentity").
				then().
				statusCode(200).
				extract().body().as(getObjectResponseTypeRefBiologicalEntity());

		return response.getEntity();
	}

	public CHEBITerm createChebiTerm(String curie, String name, Boolean obsolete) {
		CHEBITerm chebiTerm = new CHEBITerm();
		chebiTerm.setCurie(curie);
		chebiTerm.setObsolete(obsolete);
		chebiTerm.setName(name);
		chebiTerm.setSecondaryIdentifiers(List.of(curie + "secondary"));

		ObjectResponse<CHEBITerm> response = given().
				contentType("application/json").
				body(chebiTerm).
				when().
				post("/api/chebiterm").
				then().
				statusCode(200).
				extract().body().as(getObjectResponseTypeRefCHEBITerm());

		return response.getEntity();
	}

	public ChemicalTerm createChemicalTerm(String curie, String name) throws Exception {
		ChemicalTerm chemicalTerm = new ChemicalTerm();
		chemicalTerm.setCurie(curie);
		chemicalTerm.setName(name);
		chemicalTerm.setObsolete(false);
		chemicalTerm.setSecondaryIdentifiers(List.of(curie + "secondary"));

		ObjectResponse<ChemicalTerm> response = RestAssured.given().
			contentType("application/json").
			body(chemicalTerm).
			when().
			put("/api/chemicalterm").
			then().
			statusCode(200).
			extract().body().as(getObjectResponseTypeRefChemicalTerm());

		return response.getEntity();
	}

	public ConditionRelation createConditionRelation(String handle, Reference reference, VocabularyTerm relationType, List<ExperimentalCondition> conditions) {
		ConditionRelation conditionRelation = new ConditionRelation();
		conditionRelation.setHandle(handle);
		conditionRelation.setSingleReference(reference);
		conditionRelation.setConditionRelationType(relationType);
		conditionRelation.setConditions(conditions);

		ObjectResponse<ConditionRelation> response = RestAssured.given().
			contentType("application/json").
			body(conditionRelation).
			when().
			post("/api/condition-relation").
			then().
			statusCode(200).
			extract().body().as(getObjectResponseTypeRefConditionRelation());


		return response.getEntity();
	}

	public Construct createConstruct(String primaryExternalId, Boolean obsolete, VocabularyTerm symbolNameTerm) {
		Construct construct = new Construct();
		construct.setPrimaryExternalId(primaryExternalId);
		construct.setObsolete(obsolete);

		ConstructSymbolSlotAnnotation symbol = new ConstructSymbolSlotAnnotation();
		symbol.setNameType(symbolNameTerm);
		symbol.setDisplayText(primaryExternalId);
		symbol.setFormatText(primaryExternalId);

		construct.setConstructSymbol(symbol);

		ObjectResponse<Construct> response = RestAssured.given().
			contentType("application/json").
			body(construct).
			when().
			post("/api/construct").
			then().
			statusCode(200).
			extract().body().as(getObjectResponseTypeRefConstruct());

		return response.getEntity();
	}

	public DOTerm createDoTerm(String curie, String name) {
		return createDoTerm(curie, name, false);
	}

	public DOTerm createDoTerm(String curie, Boolean obsolete) {
		return createDoTerm(curie, curie, obsolete);
	}

	public DOTerm createDoTerm(String curie, String name, Boolean obsolete) {
		DOTerm doTerm = new DOTerm();
		doTerm.setCurie(curie);
		doTerm.setName(name);
		doTerm.setObsolete(obsolete);
		doTerm.setSecondaryIdentifiers(List.of(curie + "secondary"));

		ObjectResponse<DOTerm> response = given().
				contentType("application/json").
				body(doTerm).
				when().
				post("/api/doterm").
				then().
				statusCode(200).
				extract().body().as(getObjectResponseTypeRefDOTerm());

		return response.getEntity();
	}

	public ECOTerm createEcoTerm(String curie, String name, Boolean obsolete, Boolean inAgrSubset) {
		ECOTerm ecoTerm = new ECOTerm();
		ecoTerm.setCurie(curie);
		ecoTerm.setName(name);
		ecoTerm.setObsolete(obsolete);
		ecoTerm.setSecondaryIdentifiers(List.of(curie + "secondary"));
		if (inAgrSubset) {
			ecoTerm.setSubsets(List.of(OntologyConstants.AGR_ECO_TERM_SUBSET));
		}

		ObjectResponse<ECOTerm> response = given().
				contentType("application/json").
				body(ecoTerm).
				when().
				post("/api/ecoterm").
				then().
				statusCode(200).
				extract().body().as(getObjectResponseTypeRefECOTerm());
		return response.getEntity();
	}

	public ExperimentalCondition createExperimentalCondition(String uniqueId, String conditionClassCurie, String conditionClassName) {
		ExperimentalCondition condition = new ExperimentalCondition();
		condition.setConditionClass(createZecoTerm(conditionClassCurie, conditionClassName, false, OntologyConstants.ZECO_AGR_SLIM_SUBSET));
		condition.setUniqueId(uniqueId);

		ObjectResponse<ExperimentalCondition> response = given().
			contentType("application/json").
			body(condition).
			when().
			post("/api/experimental-condition").
			then().
			statusCode(200).
			extract().body().as(getObjectResponseTypeRefExperimentalCondition());

		return response.getEntity();
	}

	public ExperimentalConditionOntologyTerm createExperimentalConditionOntologyTerm(String curie, String name) throws Exception {
		ExperimentalConditionOntologyTerm ecTerm = new ExperimentalConditionOntologyTerm();
		ecTerm.setCurie(curie);
		ecTerm.setName(name);
		ecTerm.setObsolete(false);
		ecTerm.setSecondaryIdentifiers(List.of(curie + "secondary"));

		ObjectResponse<ExperimentalConditionOntologyTerm> response = RestAssured.given().
			contentType("application/json").
			body(ecTerm).
			when().
			post("/api/experimentalconditionontologyterm").
			then().
			statusCode(200).
			extract().body().as(getObjectResponseTypeRefExperimentalConditionOntologyTerm());

		return response.getEntity();
	}

	public Gene createGene(String primaryExternalId, String taxonCurie, VocabularyTerm symbolNameTerm, Boolean obsolete) {
		return createGene(primaryExternalId, taxonCurie, symbolNameTerm, obsolete, null);
	}

	public Gene createGene(String primaryExternalId, String taxonCurie, VocabularyTerm symbolNameTerm, Boolean obsolete, Organization dataProvider) {
		return createGeneWithXref(primaryExternalId, taxonCurie, symbolNameTerm, obsolete, dataProvider, null);
	}

	public List<Gene> createGenes(List<String> primaryExternalIds, String taxonCurie, VocabularyTerm symbolNameTerm, Boolean obsolete, Organization dataProvider) {
		List<Gene> geneList = new ArrayList<>();
		for (String primaryExternalId : primaryExternalIds) {
			geneList.add(createGene(primaryExternalId, taxonCurie, symbolNameTerm, obsolete, dataProvider));
		}

		return geneList;
	}

	public Gene createGeneWithXref(String primaryExternalId, String taxonCurie, VocabularyTerm symbolNameTerm, Boolean obsolete, Organization dataProvider, String xrefCurie) {
		Gene gene = new Gene();
		gene.setPrimaryExternalId(primaryExternalId);
		gene.setTaxon(getNCBITaxonTerm(taxonCurie));
		gene.setDataProvider(dataProvider);
		gene.setObsolete(obsolete);

		GeneSymbolSlotAnnotation symbol = new GeneSymbolSlotAnnotation();
		symbol.setNameType(symbolNameTerm);
		symbol.setDisplayText(primaryExternalId);
		symbol.setFormatText(primaryExternalId);

		gene.setGeneSymbol(symbol);

		SOTerm geneType = getSoTerm("SO:0001217");
		gene.setGeneType(geneType);

		if (StringUtils.isNotBlank(xrefCurie)) {
			CrossReference xref = new CrossReference();
			xref.setReferencedCurie(xrefCurie);
			xref.setDisplayName(xrefCurie);
			gene.setCrossReferences(List.of(xref));
		}

		ObjectResponse<Gene> response = given().
				contentType("application/json").
				body(gene).
				when().
				post("/api/gene").
				then().
				statusCode(200).
				extract().body().as(getObjectResponseTypeRefGene());
		return response.getEntity();
	}

	public OntologyTerm createOntologyTerm(String curie, String name, Boolean obsolete) {
		OntologyTerm ontologyTerm = new OntologyTerm();
		ontologyTerm.setCurie(curie);
		ontologyTerm.setName(name);
		ontologyTerm.setObsolete(obsolete);
		ObjectResponse<OntologyTerm> response = given().
			contentType("application/json").
			body(ontologyTerm).
			when().
			post("/api/ontologyterm").
			then().
			statusCode(200).
			extract().body().as(getObjectResponseTypeRefOntologyTerm());
		return response.getEntity();
	}

	public GOTerm createGoTerm(String curie, String name, Boolean obsolete) {
		return createGoTerm(curie, name, obsolete, null, null);
	}

	public GOTerm createGoTerm(String curie, String name, Boolean obsolete, List<String> subsets) {
		return createGoTerm(curie, name, obsolete, null, subsets);
	}

	public GOTerm createGoTerm(String curie, String name, Boolean obsolete, GOTerm ancestor) {
		return createGoTerm(curie, name, obsolete, ancestor, null);
	}

	public GOTerm createGoTerm(String curie, String name, Boolean obsolete, GOTerm ancestor, List<String> subsets) {
		GOTerm goTerm = new GOTerm();
		goTerm.setCurie(curie);
		goTerm.setObsolete(obsolete);
		goTerm.setName(name);
		goTerm.setSecondaryIdentifiers(List.of(curie + "secondary"));
		goTerm.setSubsets(subsets);
		goTerm.addIsaAncestor(ancestor);

		ObjectResponse<GOTerm> response = given().
				contentType("application/json").
				body(goTerm).
				when().
				post("/api/goterm").
				then().
				statusCode(200).
				extract().body().as(getObjectResponseTypeRefGOTerm());
		return response.getEntity();
	}

	public MITerm createMiTerm(String curie, String name) throws Exception {
		MITerm miTerm = new MITerm();
		miTerm.setCurie(curie);
		miTerm.setName(name);
		miTerm.setObsolete(false);
		miTerm.setSecondaryIdentifiers(List.of(curie + "secondary"));

		ObjectResponse<MITerm> response = RestAssured.given().
			contentType("application/json").
			body(miTerm).
			when().
			put("/api/miterm").
			then().
			statusCode(200).
			extract().body().as(getObjectResponseTypeRefMITerm());

		return response.getEntity();
	}

	public MMOTerm createMmoTerm(String curie, String name) throws Exception {
		MMOTerm mmoTerm = new MMOTerm();
		mmoTerm.setCurie(curie);
		mmoTerm.setName(name);
		mmoTerm.setObsolete(false);
		mmoTerm.setSecondaryIdentifiers(List.of(curie + "secondary"));

		ObjectResponse<MMOTerm> response = RestAssured.given().
			contentType("application/json").
			body(mmoTerm).
			when().
			put("/api/mmoterm").
			then().
			statusCode(200).
			extract().body().as(getObjectResponseTypeRefMMOTerm());

		return response.getEntity();
	}

	public OBITerm createObiTerm(String curie, String name) throws Exception {
		OBITerm obiTerm = new OBITerm();
		obiTerm.setCurie(curie);
		obiTerm.setName(name);
		obiTerm.setObsolete(false);
		obiTerm.setSecondaryIdentifiers(List.of(curie + "secondary"));

		ObjectResponse<OBITerm> response = RestAssured.given().
			contentType("application/json").
			body(obiTerm).
			when().
			put("/api/obiterm").
			then().
			statusCode(200).
			extract().body().as(getObjectResponseTypeRefOBITerm());

		return response.getEntity();
	}

	public MPTerm createMpTerm(String curie, String name) {
		return createMpTerm(curie, name, false);
	}

	public MPTerm createMpTerm(String curie, Boolean obsolete) {
		return createMpTerm(curie, "Test MPTerm", obsolete);
	}

	public MPTerm createMpTerm(String curie, String name, Boolean obsolete) {
		MPTerm mpTerm = new MPTerm();
		mpTerm.setCurie(curie);
		mpTerm.setObsolete(obsolete);
		mpTerm.setName(name);
		mpTerm.setSecondaryIdentifiers(List.of(curie + "secondary"));

		ObjectResponse<MPTerm> response = given().
				contentType("application/json").
				body(mpTerm).
				when().
				post("/api/mpterm").
				then().
				statusCode(200).
				extract().body().as(getObjectResponseTypeRefMPTerm());

		return response.getEntity();
	}

	public NCBITaxonTerm createNCBITaxonTerm(String curie, String name, Boolean obsolete) {
		NCBITaxonTerm term = new NCBITaxonTerm();
		term.setCurie(curie);
		term.setName(name);
		term.setObsolete(obsolete);

		ObjectResponse<NCBITaxonTerm> response = RestAssured.given().
				contentType("application/json").
				body(term).
				when().
				post("/api/ncbitaxonterm").
				then().
				statusCode(200).extract().
				body().as(getObjectResponseTypeRefNCBITaxonTerm());

		return response.getEntity();
	}

	public Note createNote(VocabularyTerm vocabularyTerm, String text, Boolean internal, Reference reference) {
		Note note = new Note();
		note.setNoteType(vocabularyTerm);
		note.setFreeText(text);
		note.setInternal(internal);
		if (reference != null) {
			note.setReferences(List.of(reference));
		}

		return note;
	}

	public Organization createOrganization(String abbreviation, Boolean obsolete) {
		Organization organization = new Organization();
		organization.setAbbreviation(abbreviation);
		organization.setObsolete(obsolete);

		ObjectResponse<Organization> response = RestAssured.given().
				contentType("application/json").
				body(organization).
				when().
				post("/api/organization").
				then().
				statusCode(200).
				extract().body().as(getObjectResponseTypeRefOrganization());

		return response.getEntity();
	}

	public Person createPerson(String uniqueId) {
		Person person = new Person();
		person.setUniqueId(uniqueId);

		ObjectResponse<Person> response = RestAssured.given().
				contentType("application/json").
				body(person).
				when().
				post("/api/person").
				then().
				statusCode(200).extract().
				body().as(getObjectResponseTypeRefLoggedInPerson());

		person = response.getEntity();
		return person;
	}

	public Reference createReference(String curie, String xrefCurie) {
		return createReference(curie, xrefCurie, false);
	}

	public Reference createReference(String curie, Boolean obsolete) {
		return createReference(curie, "PMID:TestXref", obsolete);
	}

	public Reference createReference(String curie, String xrefCurie, Boolean obsolete) {
		Reference reference = new Reference();
		reference.setCurie(curie);
		reference.setObsolete(obsolete);

		CrossReference xref = new CrossReference();
		xref.setReferencedCurie(xrefCurie);
		xref.setDisplayName(xrefCurie);
		ObjectResponse<CrossReference> xrefResponse = RestAssured.given().
			contentType("application/json").
			body(xref).
			when().
			post("/api/cross-reference").
			then().
			statusCode(200).
			extract().body().as(getObjectResponseTypeRefCrossReference());

		reference.setCrossReferences(List.of(xrefResponse.getEntity()));

		ObjectResponse<Reference> response = RestAssured.given().
			contentType("application/json").
			body(reference).
			when().
			post("/api/reference").
			then().
			statusCode(200).
			extract().body().as(getObjectResponseTypeRefReference());

		return response.getEntity();
	}

	public ResourceDescriptor createResourceDescriptor(String prefix) {
		ResourceDescriptor rd = new ResourceDescriptor();
		rd.setPrefix(prefix);

		ObjectResponse<ResourceDescriptor> response = RestAssured.given().
			contentType("application/json").
			body(rd).
			when().
			post("/api/resourcedescriptor").
			then().
			statusCode(200).
			extract().body().as(getObjectResponseTypeRefResourceDescriptor());

		return response.getEntity();
	}

	public ResourceDescriptorPage createResourceDescriptorPage(String name, String urlTemplate, ResourceDescriptor rd) {
		ResourceDescriptorPage rdPage = new ResourceDescriptorPage();
		rdPage.setResourceDescriptor(rd);
		rdPage.setUrlTemplate(urlTemplate);
		rdPage.setName(name);

		ObjectResponse<ResourceDescriptorPage> response = RestAssured.given().
			contentType("application/json").
			body(rdPage).
			when().
			post("/api/resourcedescriptorpage").
			then().
			statusCode(200).
			extract().body().as(getObjectResponseTypeRefResourceDescriptorPage());

		return response.getEntity();
	}

	public SequenceTargetingReagent createSequenceTargetingReagent(String primaryExternalId, Boolean obsolete, String name) {
		SequenceTargetingReagent sqtr = new SequenceTargetingReagent();
		sqtr.setPrimaryExternalId(primaryExternalId);
		sqtr.setObsolete(obsolete);
		sqtr.setName(name);

		ObjectResponse<SequenceTargetingReagent> response = given().
				contentType("application/json").
				body(sqtr).
				when().
				post("/api/sqtr").
				then().
				statusCode(200).
				extract().body().as(getObjectResponseTypeRefSequenceTargetingReagent());
		return response.getEntity();
	}

	public SOTerm createSoTerm(String curie, String name, Boolean obsolete) {
		SOTerm term = new SOTerm();
		term.setCurie(curie);
		term.setName(name);
		term.setObsolete(obsolete);
		term.setSecondaryIdentifiers(List.of(curie + "secondary"));

		ObjectResponse<SOTerm> response = RestAssured.given().
				contentType("application/json").
				body(term).
				when().
				post("/api/soterm").
				then().
				statusCode(200).extract().
				body().as(getObjectResponseTypeRefSOTerm());

		return response.getEntity();
	}

	public StageTerm createStageTerm(String curie, String name) throws Exception {
		StageTerm stageTerm = new StageTerm();
		stageTerm.setCurie(curie);
		stageTerm.setName(name);
		stageTerm.setObsolete(false);
		stageTerm.setSecondaryIdentifiers(List.of(curie + "secondary"));

		ObjectResponse<StageTerm> response = RestAssured.given().
			contentType("application/json").
			body(stageTerm).
			when().
			put("/api/stageterm").
			then().
			statusCode(200).
			extract().body().as(getObjectResponseTypeRefStageTerm());

		return response.getEntity();
	}

	public UBERONTerm createUberonTerm(String curie, String name) throws Exception {
		UBERONTerm uberonTerm = new UBERONTerm();
		uberonTerm.setCurie(curie);
		uberonTerm.setName(name);
		uberonTerm.setObsolete(false);
		uberonTerm.setSecondaryIdentifiers(List.of(curie + "secondary"));

		ObjectResponse<UBERONTerm> response = RestAssured.given().
			contentType("application/json").
			body(uberonTerm).
			when().
			put("/api/uberonterm").
			then().
			statusCode(200).
			extract().body().as(getObjectResponseTypeRefUBERONTerm());

		return response.getEntity();
	}

	public Vocabulary createVocabulary(String name, Boolean obsolete) {
		Vocabulary vocabulary = new Vocabulary();
		vocabulary.setName(name);
		vocabulary.setVocabularyLabel(name);
		vocabulary.setInternal(false);
		vocabulary.setObsolete(obsolete);

		ObjectResponse<Vocabulary> response =
			RestAssured.given().
				contentType("application/json").
				body(vocabulary).
				when().
				post("/api/vocabulary").
				then().
				statusCode(200).
				extract().body().as(getObjectResponseTypeRefVocabulary());

		vocabulary = response.getEntity();

		return vocabulary;
	}

	public VocabularyTerm createVocabularyTerm(Vocabulary vocabulary, String name, Boolean obsolete) {
		VocabularyTerm vocabularyTerm = new VocabularyTerm();
		vocabularyTerm.setName(name);
		vocabularyTerm.setVocabulary(vocabulary);
		vocabularyTerm.setObsolete(obsolete);
		vocabularyTerm.setInternal(false);

		ObjectResponse<VocabularyTerm> response =
			RestAssured.given().
				contentType("application/json").
				body(vocabularyTerm).
				when().
				post("/api/vocabularyterm").
				then().
				statusCode(200).
				extract().body().as(getObjectResponseTypeRefVocabularyTerm());

		return response.getEntity();
	}

	public VocabularyTerm createVocabularyTerm(VocabularyTermSet vocabularyTermSet, String name, Boolean obsolete) {
		VocabularyTerm vocabularyTerm = new VocabularyTerm();
		vocabularyTerm.setName(name);
		vocabularyTerm.setVocabulary(vocabularyTermSet.getVocabularyTermSetVocabulary());
		vocabularyTerm.setVocabularyTermSets(List.of(vocabularyTermSet));
		vocabularyTerm.setObsolete(obsolete);
		vocabularyTerm.setInternal(false);

		ObjectResponse<VocabularyTerm> response =
			RestAssured.given().
				contentType("application/json").
				body(vocabularyTerm).
				when().
				post("/api/vocabularyterm").
				then().
				statusCode(200).
				extract().body().as(getObjectResponseTypeRefVocabularyTerm());

		return response.getEntity();
	}

	public void createVocabularyTermSet(String name, Vocabulary vocabulary, List<VocabularyTerm> terms) {
		VocabularyTermSet vocabularyTermSet = new VocabularyTermSet();
		vocabularyTermSet.setName(name);
		vocabularyTermSet.setVocabularyLabel(name);
		vocabularyTermSet.setVocabularyTermSetVocabulary(vocabulary);
		vocabularyTermSet.setInternal(false);
		vocabularyTermSet.setMemberTerms(terms);

		RestAssured.given().
				contentType("application/json").
				body(vocabularyTermSet).
				when().
				post("/api/vocabularytermset").
				then().
				statusCode(200);
	}

	public WBPhenotypeTerm createWbPhenotypeTerm(String curie, String name) throws Exception {
		WBPhenotypeTerm wbTerm = new WBPhenotypeTerm();
		wbTerm.setCurie(curie);
		wbTerm.setName(name);
		wbTerm.setObsolete(false);
		wbTerm.setSecondaryIdentifiers(List.of(curie + "secondary"));

		ObjectResponse<WBPhenotypeTerm> response = RestAssured.given().
			contentType("application/json").
			body(wbTerm).
			when().
			put("/api/wbphenotypeterm").
			then().
			statusCode(200).
			extract().body().as(getObjectResponseTypeRefWBPhenotypeTerm());

		return response.getEntity();
	}

	public ZECOTerm createZecoTerm(String curie, String name, Boolean obsolete) {
		return createZecoTerm(curie, name, obsolete, null);
	}

	public ZECOTerm createZecoTerm(String curie, String name, Boolean obsolete, String subset) {
		ZECOTerm zecoTerm = new ZECOTerm();
		zecoTerm.setCurie(curie);
		zecoTerm.setName(name);
		zecoTerm.setObsolete(obsolete);
		List<String> subsets = new ArrayList<String>();
		if (subset != null) {
			subsets.add(subset);
			zecoTerm.setSubsets(subsets);
		}
		zecoTerm.setObsolete(obsolete);
		zecoTerm.setSecondaryIdentifiers(List.of(curie + "secondary"));

		ObjectResponse<ZECOTerm> response = given().
			contentType("application/json").
			body(zecoTerm).
			when().
			post("/api/zecoterm").
			then().
			statusCode(200).
			extract().body().as(getObjectResponseTypeRefZecoTerm());
		return response.getEntity();
	}

	public ZFATerm createZfaTerm(String curie, Boolean obsolete) {
		ZFATerm zfaTerm = new ZFATerm();
		zfaTerm.setCurie(curie);
		zfaTerm.setObsolete(obsolete);
		zfaTerm.setName("Test ZFATerm");
		zfaTerm.setSecondaryIdentifiers(List.of(curie + "secondary"));

		ObjectResponse<ZFATerm> response = given().
				contentType("application/json").
				body(zfaTerm).
				when().
				post("/api/zfaterm").
				then().
				statusCode(200).
				extract().body().as(getObjectResponseTypeRefZFATerm());
		return response.getEntity();
	}

	public AffectedGenomicModel getAffectedGenomicModel(String identifier) {
		ObjectResponse<AffectedGenomicModel> res = RestAssured.given().
				when().
				get("/api/agm/" + identifier).
				then().
				statusCode(200).
				extract().body().as(getObjectResponseTypeRefAffectedGenomicModel());

		return res.getEntity();
	}

	public AGMDiseaseAnnotation getAgmDiseaseAnnotation(String uniqueId) {
		ObjectResponse<AGMDiseaseAnnotation> res = RestAssured.given().
				when().
				get("/api/agm-disease-annotation/findBy/" + uniqueId).
				then().
				statusCode(200).
				extract().body().as(getObjectResponseTypeRefAGMDiseaseAnnotation());

		return res.getEntity();
	}

	public Allele getAllele(String identifier) {
		ObjectResponse<Allele> res = RestAssured.given().
				when().
				get("/api/allele/" + identifier).
				then().
				statusCode(200).
				extract().body().as(getObjectResponseTypeRefAllele());

		return res.getEntity();
	}

	public AlleleDiseaseAnnotation getAlleleDiseaseAnnotation(String uniqueId) {
		ObjectResponse<AlleleDiseaseAnnotation> res = RestAssured.given().
				when().
				get("/api/allele-disease-annotation/findBy/" + uniqueId).
				then().
				statusCode(200).
				extract().body().as(getObjectResponseTypeRefAlleleDiseaseAnnotation());

		return res.getEntity();
	}

	public AlleleGeneAssociation getAlleleGeneAssociation(Long alleleId, String relationName, Long geneId) {
		ObjectResponse<AlleleGeneAssociation> res = RestAssured.given().
			when().
			get("/api/allelegeneassociation/findBy" + "?alleleId=" + alleleId + "&relationName=" + relationName + "&geneId=" + geneId).
			then().
			statusCode(200).
			extract().body().as(getObjectResponseTypeRefAlleleGeneAssociation());

		return res.getEntity();
	}

	public ConditionRelation getConditionRelation(Long id) {
		ObjectResponse<ConditionRelation> response =
				given().
					when().
					get("/api/condition-relation/" + id).
					then().
					statusCode(200).
					extract().body().as(getObjectResponseTypeRefConditionRelation());

			return response.getEntity();
	}

	public Construct getConstruct(String identifier) {
		ObjectResponse<Construct> res = RestAssured.given().
				when().
				get("/api/construct/" + identifier).
				then().
				statusCode(200).
				extract().body().as(getObjectResponseTypeRefConstruct());

		return res.getEntity();
	}

	public ConstructGenomicEntityAssociation getConstructGenomicEntityAssociation(Long constructId, String relationName, Long genomicEntityId) {
		ObjectResponse<ConstructGenomicEntityAssociation> res = RestAssured.given().
			when().
			get("/api/constructgenomicentityassociation/findBy" + "?constructId=" + constructId + "&relationName=" + relationName + "&genomicEntityId=" + genomicEntityId).
			then().
			statusCode(200).
			extract().body().as(getObjectResponseTypeRefConstructGenomicEntityAssociation());

		return res.getEntity();
	}

	public ExperimentalCondition getExperimentalCondition(String conditionSummary) {
		ObjectResponse<ExperimentalCondition> res = RestAssured.given().
				when().
				get("/api/experimental-condition/findBy/" + conditionSummary).
				then().
				statusCode(200).
				extract().body().as(getObjectResponseTypeRefExperimentalCondition());

		return res.getEntity();
	}

	public Gene getGene(String identifier) {
		ObjectResponse<Gene> res = RestAssured.given().
				when().
				get("/api/gene/" + identifier).
				then().
				statusCode(200).
				extract().body().as(getObjectResponseTypeRefGene());

		return res.getEntity();
	}

	public SequenceTargetingReagent getSequenceTargetingReagent(String identifier) {
		ObjectResponse<SequenceTargetingReagent> res = RestAssured.given().
				when().
				get("/api/sqtr/" + identifier).
				then().
				statusCode(200).
				extract().body().as(getObjectResponseTypeRefSequenceTargetingReagent());

		return res.getEntity();
	}

	public GeneDiseaseAnnotation getGeneDiseaseAnnotation(String uniqueId) {
		ObjectResponse<GeneDiseaseAnnotation> res = RestAssured.given().
				when().
				get("/api/gene-disease-annotation/findBy/" + uniqueId).
				then().
				statusCode(200).
				extract().body().as(getObjectResponseTypeRefGeneDiseaseAnnotation());

		return res.getEntity();
	}
	
	public GenomeAssembly getGenomeAssembly(String primaryExternalId) throws Exception {
		ObjectResponse<GenomeAssembly> response = RestAssured.given().
				when().
				get("/api/genomeassembly/" + primaryExternalId).
				then().
				statusCode(200).
				extract().body().as(getObjectResponseTypeRefGenomeAssembly());

			return response.getEntity();
	}

	public MPTerm getMpTerm(String curie) {
		ObjectResponse<MPTerm> response = RestAssured.given().
			when().
			get("/api/mpterm/" + curie).
			then().
			statusCode(200).
			extract().body().as(getObjectResponseTypeRefMPTerm());

		return response.getEntity();
	}

	public Note getNote(Long id) {

		ObjectResponse<Note> response =
				RestAssured.given().
					when().
					get("/api/note/" + id).
					then().
					statusCode(200).
					extract().body().as(getObjectResponseTypeRefNote());

		Note note = response.getEntity();

		return note;
	}

	public Variant getVariant(String curie) {
		ObjectResponse<Variant> res = RestAssured.given().
				when().
				get("/api/variant/" + curie).
				then().
				statusCode(200).
				extract().body().as(getObjectResponseTypeRefVariant());

		return res.getEntity();
	}

	private TypeRef<ObjectListResponse<VocabularyTerm>> getObjectListResponseTypeRefVocabularyTerm() {
		return new TypeRef<ObjectListResponse<VocabularyTerm>>() {
		};
	}

	private TypeRef<ObjectResponse<AffectedGenomicModel>> getObjectResponseTypeRefAffectedGenomicModel() {
		return new TypeRef<ObjectResponse<AffectedGenomicModel>>() {
		};
	}

	private TypeRef<ObjectResponse<AGMDiseaseAnnotation>> getObjectResponseTypeRefAGMDiseaseAnnotation() {
		return new TypeRef<ObjectResponse<AGMDiseaseAnnotation>>() {
		};
	}

	private TypeRef<ObjectResponse<Allele>> getObjectResponseTypeRefAllele() {
		return new TypeRef<ObjectResponse<Allele>>() {
		};
	}

	private TypeRef<ObjectResponse<AlleleDiseaseAnnotation>> getObjectResponseTypeRefAlleleDiseaseAnnotation() {
		return new TypeRef<ObjectResponse<AlleleDiseaseAnnotation>>() {
		};
	}

	private TypeRef<ObjectResponse<AlleleGeneAssociation>> getObjectResponseTypeRefAlleleGeneAssociation() {
		return new TypeRef<ObjectResponse<AlleleGeneAssociation>>() {
		};
	}

	private TypeRef<ObjectResponse<AnatomicalTerm>> getObjectResponseTypeRefAnatomicalTerm() {
		return new TypeRef<ObjectResponse<AnatomicalTerm>>() {
		};
	}

	private TypeRef<ObjectResponse<AssemblyComponent>> getObjectResponseTypeRefAssemblyComponent() {
		return new TypeRef<ObjectResponse<AssemblyComponent>>() {
		};
	}

	private TypeRef<ObjectResponse<BiologicalEntity>> getObjectResponseTypeRefBiologicalEntity() {
		return new TypeRef<ObjectResponse<BiologicalEntity>>() {
		};
	}

	private TypeRef<ObjectResponse<CHEBITerm>> getObjectResponseTypeRefCHEBITerm() {
		return new TypeRef<ObjectResponse<CHEBITerm>>() {
		};
	}

	private TypeRef<ObjectResponse<ChemicalTerm>> getObjectResponseTypeRefChemicalTerm() {
		return new TypeRef<ObjectResponse<ChemicalTerm>>() {
		};
	}

	public TypeRef<ObjectResponse<ConditionRelation>> getObjectResponseTypeRefConditionRelation() {
		return new TypeRef<ObjectResponse<ConditionRelation>>() {
		};
	}

	public TypeRef<ObjectResponse<Construct>> getObjectResponseTypeRefConstruct() {
		return new TypeRef<ObjectResponse<Construct>>() {
		};
	}

	private TypeRef<ObjectResponse<ConstructGenomicEntityAssociation>> getObjectResponseTypeRefConstructGenomicEntityAssociation() {
		return new TypeRef<ObjectResponse<ConstructGenomicEntityAssociation>>() {
		};
	}

	private TypeRef<ObjectResponse<CrossReference>> getObjectResponseTypeRefCrossReference() {
		return new TypeRef<ObjectResponse<CrossReference>>() {
		};
	}

	private TypeRef<ObjectResponse<DOTerm>> getObjectResponseTypeRefDOTerm() {
		return new TypeRef<ObjectResponse<DOTerm>>() {
		};
	}

	private TypeRef<ObjectResponse<ECOTerm>> getObjectResponseTypeRefECOTerm() {
		return new TypeRef<ObjectResponse<ECOTerm>>() {
		};
	}

	private TypeRef<ObjectResponse<ExperimentalCondition>> getObjectResponseTypeRefExperimentalCondition() {
		return new TypeRef<ObjectResponse<ExperimentalCondition>>() {
		};
	}

	private TypeRef<ObjectResponse<ExperimentalConditionOntologyTerm>> getObjectResponseTypeRefExperimentalConditionOntologyTerm() {
		return new TypeRef<ObjectResponse<ExperimentalConditionOntologyTerm>>() {
		};
	}

	private TypeRef<ObjectResponse<Gene>> getObjectResponseTypeRefGene() {
		return new TypeRef<ObjectResponse<Gene>>() {
		};
	}

	private TypeRef<ObjectResponse<GenomeAssembly>> getObjectResponseTypeRefGenomeAssembly() {
		return new TypeRef<ObjectResponse<GenomeAssembly>>() {
		};
	}

	private TypeRef<ObjectResponse<SequenceTargetingReagent>> getObjectResponseTypeRefSequenceTargetingReagent() {
		return new TypeRef<ObjectResponse<SequenceTargetingReagent>>() {
		};
	}

	private TypeRef<ObjectResponse<GeneDiseaseAnnotation>> getObjectResponseTypeRefGeneDiseaseAnnotation() {
		return new TypeRef<ObjectResponse<GeneDiseaseAnnotation>>() {
		};
	}

	private TypeRef<ObjectResponse<GOTerm>> getObjectResponseTypeRefGOTerm() {
		return new TypeRef<ObjectResponse<GOTerm>>() {
		};
	}

	private TypeRef<ObjectResponse<Person>> getObjectResponseTypeRefLoggedInPerson() {
		return new TypeRef<ObjectResponse<Person>>() {
		};
	}

	private TypeRef<ObjectResponse<MITerm>> getObjectResponseTypeRefMITerm() {
		return new TypeRef<ObjectResponse<MITerm>>() {
		};
	}

	private TypeRef<ObjectResponse<MMOTerm>> getObjectResponseTypeRefMMOTerm() {
		return new TypeRef<ObjectResponse<MMOTerm>>() {
		};
	}

	private TypeRef<ObjectResponse<OBITerm>> getObjectResponseTypeRefOBITerm() {
		return new TypeRef<ObjectResponse<OBITerm>>() {
		};
	}

	private TypeRef<ObjectResponse<MPTerm>> getObjectResponseTypeRefMPTerm() {
		return new TypeRef<ObjectResponse<MPTerm>>() {
		};
	}

	private TypeRef<ObjectResponse<NCBITaxonTerm>> getObjectResponseTypeRefNCBITaxonTerm() {
		return new TypeRef<ObjectResponse<NCBITaxonTerm>>() {
		};
	}

	public TypeRef<ObjectResponse<Note>> getObjectResponseTypeRefNote() {
		return new TypeRef<ObjectResponse<Note>>() {
		};
	}

	private TypeRef<ObjectResponse<OntologyTerm>> getObjectResponseTypeRefOntologyTerm() {
		return new TypeRef<ObjectResponse<OntologyTerm>>() {
		};
	}

	private TypeRef<ObjectResponse<Organization>> getObjectResponseTypeRefOrganization() {
		return new TypeRef<ObjectResponse<Organization>>() {
		};
	}

	private TypeRef<ObjectResponse<Reference>> getObjectResponseTypeRefReference() {
		return new TypeRef<ObjectResponse<Reference>>() {
		};
	}

	private TypeRef<ObjectResponse<ResourceDescriptor>> getObjectResponseTypeRefResourceDescriptor() {
		return new TypeRef<ObjectResponse<ResourceDescriptor>>() {
		};
	}

	private TypeRef<ObjectResponse<ResourceDescriptorPage>> getObjectResponseTypeRefResourceDescriptorPage() {
		return new TypeRef<ObjectResponse<ResourceDescriptorPage>>() {
		};
	}

	private TypeRef<ObjectResponse<SOTerm>> getObjectResponseTypeRefSOTerm() {
		return new TypeRef<ObjectResponse<SOTerm>>() {
		};
	}

	private TypeRef<ObjectResponse<StageTerm>> getObjectResponseTypeRefStageTerm() {
		return new TypeRef<ObjectResponse<StageTerm>>() {
		};
	}

	private TypeRef<ObjectResponse<UBERONTerm>> getObjectResponseTypeRefUBERONTerm() {
		return new TypeRef<ObjectResponse<UBERONTerm>>() {
		};
	}

	private TypeRef<ObjectResponse<Variant>> getObjectResponseTypeRefVariant() {
		return new TypeRef<ObjectResponse<Variant>>() {
		};
	}

	private TypeRef<ObjectResponse<Vocabulary>> getObjectResponseTypeRefVocabulary() {
		return new TypeRef<ObjectResponse<Vocabulary>>() {
		};
	}

	private TypeRef<ObjectResponse<VocabularyTerm>> getObjectResponseTypeRefVocabularyTerm() {
		return new TypeRef<ObjectResponse<VocabularyTerm>>() {
		};
	}

	private TypeRef<ObjectResponse<WBPhenotypeTerm>> getObjectResponseTypeRefWBPhenotypeTerm() {
		return new TypeRef<ObjectResponse<WBPhenotypeTerm>>() {
		};
	}

	private TypeRef<ObjectResponse<ZECOTerm>> getObjectResponseTypeRefZecoTerm() {
		return new TypeRef<ObjectResponse<ZECOTerm>>() {
		};
	}

	private TypeRef<ObjectResponse<ZFATerm>> getObjectResponseTypeRefZFATerm() {
		return new TypeRef<ObjectResponse<ZFATerm>>() {
		};
	}

	public NCBITaxonTerm getNCBITaxonTerm(String curie) {
		ObjectResponse<NCBITaxonTerm> response = RestAssured.given().
			when().
			get("/api/ncbitaxonterm/" + curie).
			then().
			statusCode(200).
			extract().body().as(getObjectResponseTypeRefNCBITaxonTerm());

		return response.getEntity();
	}

	public Organization getOrganization(String abbreviation) {

		SearchResponse<Organization> response =
				RestAssured.given().
					contentType("application/json").
					body("{\"abbreviation\": \"" + abbreviation + "\" }").
					when().
					post("/api/organization/find").
					then().
					statusCode(200).
					extract().body().as(getSearchResponseTypeRefOrganization());

		return response.getSingleResult();
	}

	protected TypeRef<SearchResponse<AGMPhenotypeAnnotation>> getSearchResponseTypeRefAGMPhenotypeAnnotation() {
		return new TypeRef<SearchResponse<AGMPhenotypeAnnotation>>() {
		};
	}

	protected TypeRef<SearchResponse<AllelePhenotypeAnnotation>> getSearchResponseTypeRefAllelePhenotypeAnnotation() {
		return new TypeRef<SearchResponse<AllelePhenotypeAnnotation>>() {
		};
	}

	private TypeRef<SearchResponse<Organization>> getSearchResponseTypeRefOrganization() {
		return new TypeRef<SearchResponse<Organization>>() {
		};
	}

	private TypeRef<SearchResponse<Vocabulary>> getSearchResponseTypeRefVocabulary() {
		return new TypeRef<SearchResponse<Vocabulary>>() {
		};
	}

	private TypeRef<SearchResponse<VocabularyTermSet>> getSearchResponseTypeRefVocabularyTermSet() {
		return new TypeRef<SearchResponse<VocabularyTermSet>>() {
		};
	}

	public SOTerm getSoTerm(String curie) {
		ObjectResponse<SOTerm> response = RestAssured.given().
			when().
			get("/api/soterm/" + curie).
			then().
			statusCode(200).
			extract().body().as(getObjectResponseTypeRefSOTerm());

		return response.getEntity();
	}

	public Vocabulary getVocabulary(String label) {
		SearchResponse<Vocabulary> response =
			RestAssured.given().
				contentType("application/json").
				body("{\"vocabularyLabel\": \"" + label + "\" }").
				when().
				post("/api/vocabulary/find").then().
				statusCode(200).
				extract().body().as(getSearchResponseTypeRefVocabulary());

		Vocabulary vocabulary = response.getSingleResult();

		return vocabulary;
	}

	public VocabularyTerm getVocabularyTerm(Vocabulary vocabulary, String name) {
		ObjectListResponse<VocabularyTerm> response =
			RestAssured.given().
				when().
				get("/api/vocabulary/" + vocabulary.getId() + "/terms").
				then().
				statusCode(200).
				extract().body().as(getObjectListResponseTypeRefVocabularyTerm());

		List<VocabularyTerm> vocabularyTerms = response.getEntities();
		for (VocabularyTerm vocabularyTerm : vocabularyTerms) {
			if (vocabularyTerm.getName().equals(name)) {
				return vocabularyTerm;
			}
		}

		return null;
	}

	public VocabularyTermSet getVocabularyTermSet(String label) {

		SearchResponse<VocabularyTermSet> response =
				RestAssured.given().
					contentType("application/json").
					body("{\"vocabularyLabel\": \"" + label + "\" }").
					when().
					post("/api/vocabularytermset/find").
					then().
					statusCode(200).
					extract().body().as(getSearchResponseTypeRefVocabularyTermSet());

		return response.getSingleResult();
	}

	public ZECOTerm getZecoTerm(String curie) {
		ObjectResponse<ZECOTerm> response =
			given().
				when().
				get("/api/zecoterm/" + curie).
				then().
				statusCode(200).
				extract().body().as(getObjectResponseTypeRefZecoTerm());

		return response.getEntity();
	}
}
