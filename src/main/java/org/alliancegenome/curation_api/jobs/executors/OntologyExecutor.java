package org.alliancegenome.curation_api.jobs.executors;

import java.io.FileInputStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

import org.alliancegenome.curation_api.dao.loads.BulkLoadFileDAO;
import org.alliancegenome.curation_api.enums.OntologyBulkLoadType;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.entities.ontology.OntologyTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;
import org.alliancegenome.curation_api.services.helpers.GenericOntologyLoadConfig;
import org.alliancegenome.curation_api.services.helpers.GenericOntologyLoadHelper;
import org.alliancegenome.curation_api.services.ontology.ApoTermService;
import org.alliancegenome.curation_api.services.ontology.AtpTermService;
import org.alliancegenome.curation_api.services.ontology.BspoTermService;
import org.alliancegenome.curation_api.services.ontology.CHEBITermService;
import org.alliancegenome.curation_api.services.ontology.ClTermService;
import org.alliancegenome.curation_api.services.ontology.CmoTermService;
import org.alliancegenome.curation_api.services.ontology.DaoTermService;
import org.alliancegenome.curation_api.services.ontology.DoTermService;
import org.alliancegenome.curation_api.services.ontology.EcoTermService;
import org.alliancegenome.curation_api.services.ontology.EmapaTermService;
import org.alliancegenome.curation_api.services.ontology.FbcvTermService;
import org.alliancegenome.curation_api.services.ontology.FbdvTermService;
import org.alliancegenome.curation_api.services.ontology.GenoTermService;
import org.alliancegenome.curation_api.services.ontology.GoTermService;
import org.alliancegenome.curation_api.services.ontology.HpTermService;
import org.alliancegenome.curation_api.services.ontology.MaTermService;
import org.alliancegenome.curation_api.services.ontology.MiTermService;
import org.alliancegenome.curation_api.services.ontology.MmoTermService;
import org.alliancegenome.curation_api.services.ontology.MmusdvTermService;
import org.alliancegenome.curation_api.services.ontology.ModTermService;
import org.alliancegenome.curation_api.services.ontology.MpTermService;
import org.alliancegenome.curation_api.services.ontology.MpathTermService;
import org.alliancegenome.curation_api.services.ontology.ObiTermService;
import org.alliancegenome.curation_api.services.ontology.PatoTermService;
import org.alliancegenome.curation_api.services.ontology.PwTermService;
import org.alliancegenome.curation_api.services.ontology.RoTermService;
import org.alliancegenome.curation_api.services.ontology.RsTermService;
import org.alliancegenome.curation_api.services.ontology.SoTermService;
import org.alliancegenome.curation_api.services.ontology.UberonTermService;
import org.alliancegenome.curation_api.services.ontology.VtTermService;
import org.alliancegenome.curation_api.services.ontology.WbPhenotypeTermService;
import org.alliancegenome.curation_api.services.ontology.WbbtTermService;
import org.alliancegenome.curation_api.services.ontology.WblsTermService;
import org.alliancegenome.curation_api.services.ontology.XbaTermService;
import org.alliancegenome.curation_api.services.ontology.XbedTermService;
import org.alliancegenome.curation_api.services.ontology.XbsTermService;
import org.alliancegenome.curation_api.services.ontology.XcoTermService;
import org.alliancegenome.curation_api.services.ontology.XpoTermService;
import org.alliancegenome.curation_api.services.ontology.XsmoTermService;
import org.alliancegenome.curation_api.services.ontology.ZecoTermService;
import org.alliancegenome.curation_api.services.ontology.ZfaTermService;
import org.alliancegenome.curation_api.services.ontology.ZfsTermService;
import org.alliancegenome.curation_api.services.processing.LoadProcessDisplayService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
public class OntologyExecutor {

	@Inject XcoTermService xcoTermService;
	@Inject GoTermService goTermService;
	@Inject SoTermService soTermService;
	@Inject EcoTermService ecoTermService;
	@Inject ZecoTermService zecoTermService;
	@Inject EmapaTermService emapaTermService;
	@Inject DaoTermService daoTermService;
	@Inject CHEBITermService chebiTermService;
	@Inject ZfaTermService zfaTermService;
	@Inject DoTermService doTermService;
	@Inject HpTermService hpTermService;
	@Inject WbbtTermService wbbtTermService;
	@Inject MpTermService mpTermService;
	@Inject MaTermService maTermService;
	@Inject VtTermService vtTermService;
	@Inject WblsTermService wblsTermService;
	@Inject FbdvTermService fbdvTermService;
	@Inject MmusdvTermService mmusdvTermService;
	@Inject ZfsTermService zfsTermService;
	@Inject XbaTermService xbaTermService;
	@Inject XbsTermService xbsTermService;
	@Inject XpoTermService xpoTermService;
	@Inject AtpTermService atpTermService;
	@Inject XbedTermService xbedTermService;
	@Inject XsmoTermService xsmoTermService;
	@Inject RoTermService roTermService;
	@Inject ObiTermService obiTermService;
	@Inject PatoTermService patoTermService;
	@Inject WbPhenotypeTermService wbPhenotypeTermService;
	@Inject FbcvTermService fbcvTermService;
	@Inject MmoTermService mmoTermService;
	@Inject ApoTermService apoTermService;
	@Inject MiTermService miTermService;
	@Inject MpathTermService mpathTermService;
	@Inject ModTermService modTermService;
	@Inject UberonTermService uberonTermService;
	@Inject RsTermService rsTermService;
	@Inject PwTermService pwTermService;
	@Inject ClTermService clTermService;
	@Inject CmoTermService cmoTermService;
	@Inject BspoTermService bspoTermService;
	@Inject GenoTermService genoTermService;

	@Inject BulkLoadFileDAO bulkLoadFileDAO;
	@Inject LoadProcessDisplayService loadProcessDisplayService;

	public void execLoad(BulkLoadFileHistory bulkLoadFileHistory) throws Exception {

		GenericOntologyLoadConfig config = new GenericOntologyLoadConfig();
		OntologyBulkLoadType ontologyType = bulkLoadFileHistory.getBulkLoad().getOntologyType();

		switch (ontologyType) {
			case ZECO -> {
				config.setLoadOnlyIRIPrefix("ZECO");
				processTerms(bulkLoadFileHistory, zecoTermService, config);
			}
			case EMAPA -> {
				config.getAltNameSpaces().add("anatomical_structure");
				processTerms(bulkLoadFileHistory, emapaTermService, config);
			}
			case GO -> {
				config.setLoadOnlyIRIPrefix("GO"); // GO has to have both prefix and namespaces as obsolete terms do not show up in
													// the namespace's
				config.getAltNameSpaces().add("biological_process");
				config.getAltNameSpaces().add("molecular_function");
				config.getAltNameSpaces().add("cellular_component");
				processTerms(bulkLoadFileHistory, goTermService, config);
			}
			case SO -> processTerms(bulkLoadFileHistory, soTermService, config);
			case XCO -> processTerms(bulkLoadFileHistory, xcoTermService, config);
			case ECO -> {
				processTerms(bulkLoadFileHistory, ecoTermService, config);
				ecoTermService.updateAbbreviations();
			}
			case CHEBI -> {
				config.setLoadOnlyIRIPrefix("CHEBI");
				processTerms(bulkLoadFileHistory, chebiTermService, config);
			}
			case ZFA -> {
				config.getAltNameSpaces().add("zebrafish_anatomy");
				processTerms(bulkLoadFileHistory, zfaTermService, config);
			}
			case DO -> processTerms(bulkLoadFileHistory, doTermService, config);
			case MP -> {
				config.setLoadOnlyIRIPrefix("MP");
				processTerms(bulkLoadFileHistory, mpTermService, config);
			}
			case RO -> {
				config.setLoadObjectProperties(true);
				config.setLoadOnlyIRIPrefix("RO");
				processTerms(bulkLoadFileHistory, roTermService, config);
			}
			case MA -> processTerms(bulkLoadFileHistory, maTermService, config);
			case WBBT -> processTerms(bulkLoadFileHistory, wbbtTermService, config);
			case DAO -> {
				config.setLoadOnlyIRIPrefix("FBbt");
				processTerms(bulkLoadFileHistory, daoTermService, config);
			}
			case WBLS -> processTerms(bulkLoadFileHistory, wblsTermService, config);
			case FBDV -> processTerms(bulkLoadFileHistory, fbdvTermService, config);
			case MMUSDV -> {
				config.getAltNameSpaces().add("mouse_developmental_stage");
				config.getAltNameSpaces().add("mouse_stages_ontology");
				processTerms(bulkLoadFileHistory, mmusdvTermService, config);
			}
			case ZFS -> processTerms(bulkLoadFileHistory, zfsTermService, config);
			case XBA_XBS -> {
				config.getAltNameSpaces().add("xenopus_anatomy");
				config.getAltNameSpaces().add("xenopus_anatomy_in_vitro");
				processTerms(bulkLoadFileHistory, OntologyBulkLoadType.XBA, xbaTermService, config);
				GenericOntologyLoadConfig config2 = new GenericOntologyLoadConfig();
				config2.getAltNameSpaces().add("xenopus_developmental_stage");
				processTerms(bulkLoadFileHistory, OntologyBulkLoadType.XBS, xbsTermService, config2);
			}
			case XPO -> {
				config.setLoadOnlyIRIPrefix("XPO");
				processTerms(bulkLoadFileHistory, xpoTermService, config);
			}
			case ATP -> {
				config.setLoadOnlyIRIPrefix("ATP");
				processTerms(bulkLoadFileHistory, atpTermService, config);
			}
			case XBED -> processTerms(bulkLoadFileHistory, xbedTermService, config);
			case VT -> processTerms(bulkLoadFileHistory, vtTermService, config);
			case XSMO -> processTerms(bulkLoadFileHistory, xsmoTermService, config);
			case OBI -> {
				config.setLoadOnlyIRIPrefix("OBI");
				processTerms(bulkLoadFileHistory, obiTermService, config);
			}
			case WBPheno -> processTerms(bulkLoadFileHistory, wbPhenotypeTermService, config);
			case PATO -> processTerms(bulkLoadFileHistory, patoTermService, config);
			case HP -> {
				config.setLoadOnlyIRIPrefix("HP");
				processTerms(bulkLoadFileHistory, hpTermService, config);
			}
			case FBCV -> {
				config.setLoadOnlyIRIPrefix("FBcv");
				processTerms(bulkLoadFileHistory, fbcvTermService, config);
			}
			case MMO -> processTerms(bulkLoadFileHistory, mmoTermService, config);
			case APO -> {
				config.getAltNameSpaces().add("experiment_type");
				config.getAltNameSpaces().add("mutant_type");
				config.getAltNameSpaces().add("observable");
				config.getAltNameSpaces().add("qualifier");
				processTerms(bulkLoadFileHistory, apoTermService, config);
			}
			case MI -> processTerms(bulkLoadFileHistory, miTermService, config);
			case MPATH -> processTerms(bulkLoadFileHistory, mpathTermService, config);
			case MOD -> processTerms(bulkLoadFileHistory, modTermService, config);
			case UBERON -> {
				config.setLoadOnlyIRIPrefix("UBERON");
				processTerms(bulkLoadFileHistory, uberonTermService, config);
			}
			case RS -> processTerms(bulkLoadFileHistory, rsTermService, config);
			case PW -> processTerms(bulkLoadFileHistory, pwTermService, config);
			case CL -> {
				config.setLoadOnlyIRIPrefix("CL");
				processTerms(bulkLoadFileHistory, clTermService, config);
			}
			case CMO -> {
				config.setLoadOnlyIRIPrefix("CMO");
				processTerms(bulkLoadFileHistory, cmoTermService, config);
			}
			case BSPO -> {
				config.setLoadOnlyIRIPrefix("BSPO");
				processTerms(bulkLoadFileHistory, bspoTermService, config);
			}
			case GENO -> {
				config.setLoadOnlyIRIPrefix("GENO");
				processTerms(bulkLoadFileHistory, genoTermService, config);
			}
			default -> {
				log.info("Ontology Load: " + bulkLoadFileHistory.getBulkLoad().getName() + " for OT: " + ontologyType + " not implemented");
				throw new Exception("Ontology Load: " + bulkLoadFileHistory.getBulkLoad().getName() + " for OT: " + ontologyType + " not implemented");
			}
		}

	}

	private void processTerms(BulkLoadFileHistory bulkLoadFileHistory, BaseOntologyTermService service, GenericOntologyLoadConfig config) throws Exception {
		processTerms(bulkLoadFileHistory, bulkLoadFileHistory.getBulkLoad().getOntologyType(), service, config);
	}

	private void processTerms(BulkLoadFileHistory bulkLoadFileHistory, OntologyBulkLoadType ontologyType, BaseOntologyTermService service, GenericOntologyLoadConfig config) throws Exception {

		GenericOntologyLoadHelper<? extends OntologyTerm> loader = new GenericOntologyLoadHelper<>(ontologyType.getClazz(), config);

		Map<String, ? extends OntologyTerm> termMap = loader.load(new GZIPInputStream(new FileInputStream(bulkLoadFileHistory.getBulkLoadFile().getLocalFilePath())));

		bulkLoadFileHistory.getBulkLoadFile().setRecordCount(bulkLoadFileHistory.getBulkLoadFile().getRecordCount() + termMap.size());

		bulkLoadFileDAO.merge(bulkLoadFileHistory.getBulkLoadFile());
		
		ProcessDisplayHelper ph = new ProcessDisplayHelper();
		ph.addDisplayHandler(loadProcessDisplayService);
		ph.startProcess(bulkLoadFileHistory.getBulkLoad().getName() + ": " + ontologyType.getClazz().getSimpleName() + " Terms", termMap.size());
		for (Entry<String, ? extends OntologyTerm> entry : termMap.entrySet()) {
			service.processUpdate(entry.getValue());
			ph.progressProcess();
		}
		ph.finishProcess();

		ProcessDisplayHelper ph1 = new ProcessDisplayHelper();
		ph.addDisplayHandler(loadProcessDisplayService);
		ph1.startProcess(bulkLoadFileHistory.getBulkLoad().getName() + ": " + ontologyType.getClazz().getSimpleName() + " Closure", termMap.size());
		for (Entry<String, ? extends OntologyTerm> entry : termMap.entrySet()) {
			service.processUpdateRelationships(entry.getValue());
			// Thread.sleep(5000);
			ph1.progressProcess();
		}
		ph1.finishProcess();

		ProcessDisplayHelper ph2 = new ProcessDisplayHelper();
		ph.addDisplayHandler(loadProcessDisplayService);
		ph2.startProcess(bulkLoadFileHistory.getBulkLoad().getName() + ": " + ontologyType.getClazz().getSimpleName() + " Counts", termMap.size());
		for (Entry<String, ? extends OntologyTerm> entry : termMap.entrySet()) {
			service.processCounts(entry.getValue());
			// Thread.sleep(5000);
			ph2.progressProcess();
		}
		ph2.finishProcess();
	}
}
