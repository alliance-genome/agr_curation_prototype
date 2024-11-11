import React from 'react';

import { SecureRoute } from '@okta/okta-react';

import { DashboardPage } from './containers/dashboardPage';

import { DataLoadsPage } from './containers/dataLoadsPage/';
import { ReportsPage } from './containers/reportsPage/';
import { DiseaseAnnotationsPage } from './containers/diseaseAnnotationsPage';
import { PhenotypeAnnotationsPage } from './containers/phenotypeAnnotationsPage';
import { GeneGeneticInteractionsPage } from './containers/geneGeneticInteractionsPage/GeneGeneticInteractionsPage';
import { GeneMolecularInteractionsPage } from './containers/geneMolecularInteractionsPage/GeneMolecularInteractionsPage';
import { ExperimentalConditionsPage } from './containers/experimentalConditionsPage';
import { ConditionRelationPage } from './containers/conditionRelationPage';
import { AffectedGenomicModelPage } from './containers/affectedGenomicModelPage';
import { AllelesPage, AlleleDetailPage } from './containers/allelesPage';
import { GenesPage } from './containers/genesPage';
import { VariantsPage } from './containers/variantsPage';
import { ConstructsPage } from './containers/constructsPage';
import { ProfilePage } from './containers/profilePage';
import { MoleculesPage } from './containers/moleculesPage';
import { SpeciesPage } from './containers/speciesPage';
import { ReferencePage } from './containers/referencePage';
import { ControlledVocabularyPage } from './containers/controlledVocabularyPage';
import { VocabulariesPage } from './containers/vocabularyPage';
import { VocabularyTermSetPage } from './containers/vocabularyTermSetPage';

import { FMSComponent } from './components/FMSComponent';
import { EntityCountsComponent } from './components/EntityCountsComponent';
import { FMSDataTypesComponent } from './components/FMSDataTypesComponent';
import { MetricsComponent } from './components/MetricsComponent';
import { HealthComponent } from './components/HealthComponent';

import { SiteLayout } from './containers/layout/SiteLayout';
//import { Login } from './Login';

import { GeneralOntologyComponent } from './containers/ontologies/GeneralOntologyComponent';
import { ResourceDescriptorsPage } from './containers/resourceDescriptorPage';
import { ResourceDescriptorPagesPage } from './containers/resourceDescriptorPagePage';
import ErrorBoundary from './components/Error/ErrorBoundary';

export default (
	<SiteLayout>
		<SecureRoute
			exact
			path="/"
			render={() => (
				<ErrorBoundary>
					<DashboardPage />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/profile"
			render={() => (
				<ErrorBoundary>
					<ProfilePage />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/dataloads"
			render={() => (
				<ErrorBoundary>
					<DataLoadsPage />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/reports"
			render={() => (
				<ErrorBoundary>
					<ReportsPage />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/diseaseAnnotations"
			render={() => (
				<ErrorBoundary>
					<DiseaseAnnotationsPage />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/phenotypeAnnotations"
			render={() => (
				<ErrorBoundary>
					<PhenotypeAnnotationsPage />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/geneGeneticInteractions"
			render={() => (
				<ErrorBoundary>
					<GeneGeneticInteractionsPage />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/geneMolecularInteractions"
			render={() => (
				<ErrorBoundary>
					<GeneMolecularInteractionsPage />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/experimentalConditions"
			render={() => (
				<ErrorBoundary>
					<ExperimentalConditionsPage />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/conditionRelations"
			render={() => (
				<ErrorBoundary>
					<ConditionRelationPage />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/genes"
			render={() => (
				<ErrorBoundary>
					<GenesPage />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/alleles"
			render={() => (
				<ErrorBoundary>
					<AllelesPage />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/allele/:identifier"
			render={() => (
				<ErrorBoundary>
					<AlleleDetailPage />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/variants"
			render={() => (
				<ErrorBoundary>
					<VariantsPage />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/constructs"
			render={() => (
				<ErrorBoundary>
					<ConstructsPage />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/molecules"
			render={() => (
				<ErrorBoundary>
					<MoleculesPage />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/species"
			render={() => (
				<ErrorBoundary>
					<SpeciesPage />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/references"
			render={() => (
				<ErrorBoundary>
					<ReferencePage />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/vocabterms"
			render={() => (
				<ErrorBoundary>
					<ControlledVocabularyPage />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/vocabularies"
			render={() => (
				<ErrorBoundary>
					<VocabulariesPage />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/vocabularytermsets"
			render={() => (
				<ErrorBoundary>
					<VocabularyTermSetPage />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/resourcedescriptors"
			render={() => (
				<ErrorBoundary>
					<ResourceDescriptorsPage />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/resourcedescriptorpages"
			render={() => (
				<ErrorBoundary>
					<ResourceDescriptorPagesPage />
				</ErrorBoundary>
			)}
		/>

		<SecureRoute
			path="/ontology/eco"
			render={() => (
				<ErrorBoundary>
					<GeneralOntologyComponent showAbbreviation={true} name="ECO" endpoint="ecoterm" />
				</ErrorBoundary>
			)}
		/>

		<SecureRoute
			path="/ontology/go"
			render={() => (
				<ErrorBoundary>
					<GeneralOntologyComponent showNamespace={true} name="GO" endpoint="goterm" />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/ontology/so"
			render={() => (
				<ErrorBoundary>
					<GeneralOntologyComponent showNamespace={true} name="SO" endpoint="soterm" />
				</ErrorBoundary>
			)}
		/>

		<SecureRoute
			path="/ontology/ncbitaxon"
			render={() => (
				<ErrorBoundary>
					<GeneralOntologyComponent hideDefinition={true} name="NCBITaxon" endpoint="ncbitaxonterm" />
				</ErrorBoundary>
			)}
		/>

		<SecureRoute
			path="/ontology/chebi"
			render={() => (
				<ErrorBoundary>
					<GeneralOntologyComponent name="ChEBI" endpoint="chebiterm" />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/ontology/do"
			render={() => (
				<ErrorBoundary>
					<GeneralOntologyComponent name="Diseases" endpoint="doterm" />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/ontology/ma"
			render={() => (
				<ErrorBoundary>
					<GeneralOntologyComponent name="MA" endpoint="materm" />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/ontology/zfa"
			render={() => (
				<ErrorBoundary>
					<GeneralOntologyComponent name="ZFA" endpoint="zfaterm" />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/ontology/mp"
			render={() => (
				<ErrorBoundary>
					{' '}
					<GeneralOntologyComponent name="MP" endpoint="mpterm" />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/ontology/dao"
			render={() => (
				<ErrorBoundary>
					<GeneralOntologyComponent name="DAO" endpoint="daoterm" />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/ontology/emapa"
			render={() => (
				<ErrorBoundary>
					<GeneralOntologyComponent name="EMAPA" endpoint="emapaterm" />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/ontology/wbbt"
			render={() => (
				<ErrorBoundary>
					<GeneralOntologyComponent name="WBbt" endpoint="wbbtterm" />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/ontology/xco"
			render={() => (
				<ErrorBoundary>
					<GeneralOntologyComponent name="XCO" endpoint="xcoterm" />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/ontology/ro"
			render={() => (
				<ErrorBoundary>
					<GeneralOntologyComponent name="RO" endpoint="roterm" />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/ontology/zeco"
			render={() => (
				<ErrorBoundary>
					<GeneralOntologyComponent name="ZECO" endpoint="zecoterm" />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/ontology/wbls"
			render={() => (
				<ErrorBoundary>
					<GeneralOntologyComponent name="WBls" endpoint="wblsterm" />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/ontology/fbdv"
			render={() => (
				<ErrorBoundary>
					<GeneralOntologyComponent name="FBdv" endpoint="fbdvterm" />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/ontology/mmusdv"
			render={() => (
				<ErrorBoundary>
					<GeneralOntologyComponent name="MmusDv" endpoint="mmusdvterm" />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/ontology/zfs"
			render={() => (
				<ErrorBoundary>
					<GeneralOntologyComponent name="ZFS" endpoint="zfsterm" />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/ontology/xba"
			render={() => (
				<ErrorBoundary>
					<GeneralOntologyComponent name="XBA" endpoint="xbaterm" />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/ontology/xbs"
			render={() => (
				<ErrorBoundary>
					<GeneralOntologyComponent name="XBS" endpoint="xbsterm" />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/ontology/xpo"
			render={() => (
				<ErrorBoundary>
					<GeneralOntologyComponent name="XPO" endpoint="xpoterm" />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/ontology/atp"
			render={() => (
				<ErrorBoundary>
					<GeneralOntologyComponent name="ATP" endpoint="atpterm" />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/ontology/xbed"
			render={() => (
				<ErrorBoundary>
					<GeneralOntologyComponent name="XBED" endpoint="xbedterm" />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/ontology/xsmo"
			render={() => (
				<ErrorBoundary>
					<GeneralOntologyComponent name="XSMO" endpoint="xsmoterm" />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/ontology/vt"
			render={() => (
				<ErrorBoundary>
					<GeneralOntologyComponent name="VT" endpoint="vtterm" />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/ontology/obi"
			render={() => (
				<ErrorBoundary>
					<GeneralOntologyComponent name="OBI" endpoint="obiterm" />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/ontology/wbpheno"
			render={() => (
				<ErrorBoundary>
					<GeneralOntologyComponent name="WBPhenotype" endpoint="wbphenotypeterm" />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/ontology/pato"
			render={() => (
				<ErrorBoundary>
					<GeneralOntologyComponent name="PATO" endpoint="patoterm" />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/ontology/hp"
			render={() => (
				<ErrorBoundary>
					<GeneralOntologyComponent name="HP" endpoint="hpterm" />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/ontology/fbcv"
			render={() => (
				<ErrorBoundary>
					<GeneralOntologyComponent showNamespace={true} name="FBcv" endpoint="fbcvterm" />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/ontology/mmo"
			render={() => (
				<ErrorBoundary>
					<GeneralOntologyComponent name="MMO" endpoint="mmoterm" />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/ontology/apo"
			render={() => (
				<ErrorBoundary>
					<GeneralOntologyComponent showNamespace={true} name="APO" endpoint="apoterm" />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/ontology/mi"
			render={() => (
				<ErrorBoundary>
					<GeneralOntologyComponent name="MI" endpoint="miterm" />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/ontology/mpath"
			render={() => (
				<ErrorBoundary>
					<GeneralOntologyComponent name="MPATH" endpoint="mpathterm" />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/ontology/mod"
			render={() => (
				<ErrorBoundary>
					<GeneralOntologyComponent name="MOD" endpoint="modterm" />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/ontology/uberon"
			render={() => (
				<ErrorBoundary>
					<GeneralOntologyComponent name="UBERON" endpoint="uberonterm" />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/ontology/rs"
			render={() => (
				<ErrorBoundary>
					<GeneralOntologyComponent name="RS" endpoint="rsterm" />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/ontology/pw"
			render={() => (
				<ErrorBoundary>
					<GeneralOntologyComponent name="PW" endpoint="pwterm" />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/ontology/cl"
			render={() => (
				<ErrorBoundary>
					<GeneralOntologyComponent name="CL" endpoint="clterm" />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/ontology/cmo"
			render={() => (
				<ErrorBoundary>
					<GeneralOntologyComponent name="CMO" endpoint="cmoterm" />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/ontology/bspo"
			render={() => (
				<ErrorBoundary>
					<GeneralOntologyComponent name="BSPO" endpoint="bspoterm" />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/ontology/geno"
			render={() => (
				<ErrorBoundary>
					<GeneralOntologyComponent name="GENO" endpoint="genoterm" />
				</ErrorBoundary>
			)}
		/>

		<SecureRoute
			path="/fmsdatafiles"
			render={() => (
				<ErrorBoundary>
					<FMSComponent />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/fmsdatatypes/:dataType"
			render={() => (
				<ErrorBoundary>
					<FMSDataTypesComponent />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			exact
			path="/fmsdatatypes"
			render={() => (
				<ErrorBoundary>
					<FMSDataTypesComponent />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/entitycounts"
			render={() => (
				<ErrorBoundary>
					<EntityCountsComponent />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/metricspage"
			render={() => (
				<ErrorBoundary>
					<MetricsComponent />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/healthpage"
			render={() => (
				<ErrorBoundary>
					<HealthComponent />
				</ErrorBoundary>
			)}
		/>
		<SecureRoute
			path="/agms"
			render={() => (
				<ErrorBoundary>
					<AffectedGenomicModelPage />
				</ErrorBoundary>
			)}
		/>
	</SiteLayout>
);
