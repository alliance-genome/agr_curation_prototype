import React, { useRef, useState } from 'react';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { Toast } from 'primereact/toast';
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_CONFIGS } from '../../constants/FilterFields';
import { SecondaryIdsDialog } from './SecondaryIdsDialog';
import { SynonymsDialog } from '../nameSlotAnnotations/dialogs/SynonymsDialog';
import { SymbolDialog } from '../nameSlotAnnotations/dialogs/SymbolDialog';
import { FullNameDialog } from '../nameSlotAnnotations/dialogs/FullNameDialog';
import { SystematicNameDialog } from './SystematicNameDialog';
import { ObjectListTemplate } from '../../components/Templates/ObjectListTemplate';
import { useGetTableData } from '../../service/useGetTableData';
import { useGetUserSettings } from '../../service/useGetUserSettings';
import { IdTemplate } from '../../components/Templates/IdTemplate';
import { TextDialogTemplate } from '../../components/Templates/dialog/TextDialogTemplate';
import { ListDialogTemplate } from '../../components/Templates/dialog/ListDialogTemplate';
import { RelatedNotesDialog } from '../../components/RelatedNotesDialog';
import { BooleanTemplate } from '../../components/Templates/BooleanTemplate';
import { OntologyTermTemplate } from '../../components/Templates/OntologyTermTemplate';
import { StringTemplate } from '../../components/Templates/StringTemplate';
import { CountDialogTemplate } from '../../components/Templates/dialog/CountDialogTemplate';
import { ErrorMessageComponent } from '../../components/Error/ErrorMessageComponent';

import { crossReferencesSort } from '../../components/Templates/utils/sortMethods';

import { SearchService } from '../../service/SearchService';

import { Button } from 'primereact/button';
import { EditMessageTooltip } from '../../components/EditMessageTooltip';

export const GenesTable = () => {
	const [isInEditMode, setIsInEditMode] = useState(false);
	const [errorMessages, setErrorMessages] = useState({});
	const [totalRecords, setTotalRecords] = useState(0);
	const [genes, setGenes] = useState([]);
	const errorMessagesRef = useRef();
	errorMessagesRef.current = errorMessages;

	const searchService = new SearchService();

	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);

	const [relatedNotesData, setRelatedNotesData] = useState({
		relatedNotes: [],
		isInEdit: false,
		dialog: false,
		rowIndex: null,
		mainRowProps: {},
	});

	const [synonymsData, setSynonymsData] = useState({
		dialog: false,
	});

	const [secondaryIdsData, setSecondaryIdsData] = useState({
		dialog: false,
	});

	const [symbolData, setSymbolData] = useState({
		dialog: false,
	});

	const [fullNameData, setFullNameData] = useState({
		dialog: false,
	});

	const [systematicNameData, setSystematicNameData] = useState({
		dialog: false,
	});

	const handleFullNameOpen = (geneFullName) => {
		let _fullNameData = {};
		_fullNameData['originalFullNames'] = [geneFullName];
		_fullNameData['dialog'] = true;
		setFullNameData(() => ({
			..._fullNameData,
		}));
	};

	const handleSynonymsOpen = (geneSynonyms) => {
		let _synonymsData = {};
		_synonymsData['originalSynonyms'] = geneSynonyms;
		_synonymsData['dialog'] = true;
		setSynonymsData(() => ({
			..._synonymsData,
		}));
	};

	const handleSymbolOpen = (geneSymbol) => {
		let _symbolData = {};
		_symbolData['originalSymbols'] = [geneSymbol];
		_symbolData['dialog'] = true;
		setSymbolData(() => ({
			..._symbolData,
		}));
	};

	const handleSecondaryIdsOpen = (geneSecondaryIds) => {
		let _secondaryIdsData = {};
		_secondaryIdsData['originalSecondaryIds'] = geneSecondaryIds;
		_secondaryIdsData['dialog'] = true;
		setSecondaryIdsData(() => ({
			..._secondaryIdsData,
		}));
	};

	const handleSystematicNameOpen = (geneSystematicName) => {
		let _systematicNameData = {};
		_systematicNameData['originalSystematicNames'] = [geneSystematicName];
		_systematicNameData['dialog'] = true;
		setSystematicNameData(() => ({
			..._systematicNameData,
		}));
	};

	const handleRelatedNotesOpen = (relatedNotes) => {
		console.log('relatedNotes', relatedNotes);
		let _relatedNotesData = {};
		_relatedNotesData['originalRelatedNotes'] = relatedNotes;
		_relatedNotesData['dialog'] = true;
		_relatedNotesData['isInEdit'] = false;
		setRelatedNotesData(() => ({
			..._relatedNotesData,
		}));
	};

	const handleRelatedNotesOpenInEdit = (event, rowProps, isInEdit) => {
		const { rows } = rowProps.props;
		const { rowIndex } = rowProps;
		const index = rowIndex % rows;
		let _relatedNotesData = {};
		_relatedNotesData['originalRelatedNotes'] = rowProps.rowData.relatedNotes;
		_relatedNotesData['dialog'] = true;
		_relatedNotesData['isInEdit'] = isInEdit;
		_relatedNotesData['rowIndex'] = index;
		_relatedNotesData['mainRowProps'] = rowProps;
		setRelatedNotesData(() => ({
			..._relatedNotesData,
		}));
	};

	const relatedNotesEditor = (props) => {
		if (props?.rowData?.relatedNotes) {
			return (
				<>
					<div>
						<Button
							className="p-button-text"
							onClick={(event) => {
								handleRelatedNotesOpenInEdit(event, props, true);
							}}
						>
							<span style={{ textDecoration: 'underline' }}>
								{`Notes(${props.rowData.relatedNotes.length}) `}
								<i className="pi pi-user-edit" style={{ fontSize: '1em' }}></i>
							</span>
							&nbsp;&nbsp;&nbsp;&nbsp;
							<EditMessageTooltip object="allele" />
						</Button>
					</div>
					<ErrorMessageComponent
						errorMessages={errorMessagesRef.current[props.rowIndex]}
						errorField={'relatedNotes'}
						style={{ fontSize: '1em' }}
					/>
				</>
			);
		} else {
			return (
				<>
					<div>
						<Button
							className="p-button-text"
							onClick={(event) => {
								handleRelatedNotesOpenInEdit(event, props, true);
							}}
						>
							<span style={{ textDecoration: 'underline' }}>
								Add Note
								<i className="pi pi-user-edit" style={{ fontSize: '1em' }}></i>
							</span>
							&nbsp;&nbsp;&nbsp;&nbsp;
							<EditMessageTooltip />
						</Button>
					</div>
					<ErrorMessageComponent
						errorMessages={errorMessagesRef.current[props.rowIndex]}
						errorField={'relatedNotes'}
						style={{ fontSize: '1em' }}
					/>
				</>
			);
		}
	};

	const columns = [
		{
			field: 'curie',
			header: 'Curie',
			sortable: true,
			filter: true,
			body: (rowData) => <IdTemplate id={rowData.curie} />,
			filterConfig: FILTER_CONFIGS.curieFilterConfig,
		},
		{
			field: 'primaryExternalId',
			header: 'Primary External ID',
			sortable: true,
			body: (rowData) => <IdTemplate id={rowData.primaryExternalId} />,
			filterConfig: FILTER_CONFIGS.primaryexternalidFilterConfig,
		},
		{
			field: 'modInternalId',
			header: 'MOD Internal ID',
			sortable: true,
			body: (rowData) => <IdTemplate id={rowData.modInternalId} />,
			filterConfig: FILTER_CONFIGS.modinternalidFilterConfig,
		},
		{
			field: 'geneFullName.displayText',
			header: 'Name',
			sortable: true,
			filter: true,
			body: (rowData) => (
				<TextDialogTemplate
					entity={rowData.geneFullName}
					handleOpen={handleFullNameOpen}
					text={rowData.geneFullName?.displayText}
					underline={false}
				/>
			),
			filterConfig: FILTER_CONFIGS.geneNameFilterConfig,
		},
		{
			field: 'geneSymbol.displayText',
			header: 'Symbol',
			sortable: true,
			body: (rowData) => (
				<TextDialogTemplate
					entity={rowData.geneSymbol}
					handleOpen={handleSymbolOpen}
					text={rowData.geneSymbol?.displayText}
					underline={false}
				/>
			),
			filter: true,
			filterConfig: FILTER_CONFIGS.geneSymbolFilterConfig,
		},
		{
			field: 'geneSynonyms.displayText',
			header: 'Synonyms',
			sortable: true,
			body: (rowData) => (
				<ListDialogTemplate
					entities={rowData.geneSynonyms}
					handleOpen={handleSynonymsOpen}
					getTextField={(entity) => entity?.displayText}
					underline={false}
				/>
			),
			filterConfig: FILTER_CONFIGS.geneSynonymsFilterConfig,
		},
		{
			field: 'geneSecondaryIds.secondaryId',
			header: 'Secondary IDs',
			body: (rowData) => (
				<ListDialogTemplate
					entities={rowData.geneSecondaryIds}
					handleOpen={handleSecondaryIdsOpen}
					getTextField={(entity) => entity?.secondaryId}
					underline={false}
				/>
			),
			sortable: true,
			filterConfig: FILTER_CONFIGS.geneSecondaryIdsFilterConfig,
		},
		{
			field: 'geneSystematicName.displayText',
			header: 'Systematic Name',
			sortable: true,
			body: (rowData) => (
				<TextDialogTemplate
					entity={rowData.geneSystematicName}
					handleOpen={handleSystematicNameOpen}
					text={rowData.geneSystematicName?.displayText}
					underline={false}
				/>
			),
			filter: true,
			filterConfig: FILTER_CONFIGS.geneSystematicNameFilterConfig,
		},
		{
			field: 'geneType.name',
			header: 'Gene Type',
			body: (rowData) => <OntologyTermTemplate term={rowData.geneType} />,
			sortable: true,
			filterConfig: FILTER_CONFIGS.geneTypeFilterConfig,
		},
		{
			field: 'taxon.name',
			header: 'Taxon',
			sortable: true,
			body: (rowData) => <OntologyTermTemplate term={rowData.taxon} />,
			filter: true,
			filterConfig: FILTER_CONFIGS.taxonFilterConfig,
		},
		{
			field: 'relatedNotes.freeText',
			header: 'Related Notes',
			body: (rowData) => (
				<CountDialogTemplate entities={rowData.relatedNotes} handleOpen={handleRelatedNotesOpen} text={'Notes'} />
			),
			sortable: true,
			filterConfig: FILTER_CONFIGS.relatedNotesFilterConfig,
			editor: relatedNotesEditor,
		},
		{
			field: 'dataProvider.abbreviation',
			header: 'Data Provider',
			sortable: true,
			filterConfig: FILTER_CONFIGS.geneDataProviderFilterConfig,
		},
		{
			field: 'crossReferences.displayName',
			header: 'Cross References',
			sortable: true,
			filterConfig: FILTER_CONFIGS.crossReferencesFilterConfig,
			body: (rowData) => (
				<ObjectListTemplate
					list={rowData.crossReferences}
					sortMethod={crossReferencesSort}
					stringTemplate={(item) => `${item.displayName} (${item.resourceDescriptorPage.name})`}
				/>
			),
		},
		{
			field: 'updatedBy.uniqueId',
			header: 'Updated By',
			sortable: true,
			body: (rowData) => <StringTemplate string={rowData.updatedBy?.uniqueId} />,
			filterConfig: FILTER_CONFIGS.updatedByFilterConfig,
		},
		{
			field: 'dateUpdated',
			header: 'Date Updated',
			sortable: true,
			filter: true,
			body: (rowData) => <StringTemplate string={rowData.dateUpdated} />,
			filterConfig: FILTER_CONFIGS.dateUpdatedFilterConfig,
		},
		{
			field: 'createdBy.uniqueId',
			header: 'Created By',
			sortable: true,
			filter: true,
			body: (rowData) => <StringTemplate string={rowData.createdBy?.uniqueId} />,
			filterConfig: FILTER_CONFIGS.createdByFilterConfig,
		},
		{
			field: 'dateCreated',
			header: 'Date Created',
			sortable: true,
			filter: true,
			body: (rowData) => <StringTemplate string={rowData.dateCreated} />,
			filterConfig: FILTER_CONFIGS.dataCreatedFilterConfig,
		},
		{
			field: 'internal',
			header: 'Internal',
			body: (rowData) => <BooleanTemplate value={rowData.internal} />,
			filter: true,
			filterConfig: FILTER_CONFIGS.internalFilterConfig,
			sortable: true,
		},
		{
			field: 'obsolete',
			header: 'Obsolete',
			body: (rowData) => <BooleanTemplate value={rowData.obsolete} />,
			filter: true,
			filterConfig: FILTER_CONFIGS.obsoleteFilterConfig,
			sortable: true,
		},
	];

	const DEFAULT_COLUMN_WIDTH = 10;
	const SEARCH_ENDPOINT = 'gene';

	const initialTableState = getDefaultTableState('Genes', columns, DEFAULT_COLUMN_WIDTH);

	const { settings: tableState, mutate: setTableState } = useGetUserSettings(
		initialTableState.tableSettingsKeyName,
		initialTableState
	);

	const { isFetching, isLoading } = useGetTableData({
		tableState,
		endpoint: SEARCH_ENDPOINT,
		setIsInEditMode,
		setEntities: setGenes,
		setTotalRecords,
		toast_topleft,
		searchService,
	});

	return (
		<>
			<div className="card">
				<Toast ref={toast_topleft} position="top-left" />
				<Toast ref={toast_topright} position="top-right" />
				<GenericDataTable
					endpoint={SEARCH_ENDPOINT}
					tableName="Genes"
					entities={genes}
					setEntities={setGenes}
					totalRecords={totalRecords}
					setTotalRecords={setTotalRecords}
					tableState={tableState}
					setTableState={setTableState}
					columns={columns}
					isEditable={false}
					isInEditMode={isInEditMode}
					setIsInEditMode={setIsInEditMode}
					toasts={{ toast_topleft, toast_topright }}
					errorObject={{ errorMessages, setErrorMessages }}
					defaultColumnWidth={DEFAULT_COLUMN_WIDTH}
					fetching={isFetching || isLoading}
				/>
			</div>
			<FullNameDialog
				name="Gene Name"
				field="geneFullName"
				endpoint="genefullnameslotannotation"
				originalFullNameData={fullNameData}
				setOriginalFullNameData={setFullNameData}
			/>
			<SymbolDialog
				name="Gene Symbol"
				field="geneSymbol"
				endpoint="genesymbolslotannotation"
				originalSymbolData={symbolData}
				setOriginalSymbolData={setSymbolData}
			/>
			<SynonymsDialog
				name="Gene Synonym"
				field="geneSynonyms"
				endpoint="genesynonymslotannotation"
				originalSynonymsData={synonymsData}
				setOriginalSynonymsData={setSynonymsData}
			/>
			<SecondaryIdsDialog
				originalSecondaryIdsData={secondaryIdsData}
				setOriginalSecondaryIdsData={setSecondaryIdsData}
			/>
			<SystematicNameDialog
				originalSystematicNameData={systematicNameData}
				setOriginalSystematicNameData={setSystematicNameData}
			/>
			<RelatedNotesDialog
				originalRelatedNotesData={relatedNotesData}
				setOriginalRelatedNotesData={setRelatedNotesData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
				noteTypeVocabularyTermSet="variant_note_type"
			/>
		</>
	);
};
