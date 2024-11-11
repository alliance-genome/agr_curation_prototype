import React, { useRef, useState } from 'react';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { ComponentsDialog } from './ComponentsDialog';
import { GenomicComponentsDialog } from './GenomicComponentsDialog';
import { SymbolDialog } from '../nameSlotAnnotations/dialogs/SymbolDialog';
import { FullNameDialog } from '../nameSlotAnnotations/dialogs/FullNameDialog';
import { SynonymsDialog } from '../nameSlotAnnotations/dialogs/SynonymsDialog';
import { Toast } from 'primereact/toast';
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_CONFIGS } from '../../constants/FilterFields';
import { useGetTableData } from '../../service/useGetTableData';
import { useGetUserSettings } from '../../service/useGetUserSettings';
import { IdTemplate } from '../../components/Templates/IdTemplate';
import { TextDialogTemplate } from '../../components/Templates/dialog/TextDialogTemplate';
import { ListDialogTemplate } from '../../components/Templates/dialog/ListDialogTemplate';
import { StringListTemplate } from '../../components/Templates/StringListTemplate';
import { BooleanTemplate } from '../../components/Templates/BooleanTemplate';
import { TruncatedReferencesTemplate } from '../../components/Templates/reference/TruncatedReferencesTemplate';
import { StringTemplate } from '../../components/Templates/StringTemplate';

import { SearchService } from '../../service/SearchService';

export const ConstructsTable = () => {
	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);

	const [synonymsData, setSynonymsData] = useState({
		dialog: false,
	});

	const [symbolData, setSymbolData] = useState({
		dialog: false,
	});

	const [fullNameData, setFullNameData] = useState({
		dialog: false,
	});

	const [isInEditMode, setIsInEditMode] = useState(false);
	const [errorMessages, setErrorMessages] = useState({});
	const [totalRecords, setTotalRecords] = useState(0);
	const [constructs, setConstructs] = useState([]);

	const searchService = new SearchService();

	const errorMessagesRef = useRef();
	errorMessagesRef.current = errorMessages;

	const [componentsData, setComponentsData] = useState({
		isInEdit: false,
		dialog: false,
		rowIndex: null,
		mainRowProps: {},
	});

	const [genomicComponentsData, setGenomicComponentsData] = useState({
		isInEdit: false,
		dialog: false,
		rowIndex: null,
		mainRowProps: {},
	});

	const handleFullNameOpen = (constructFullName) => {
		let _fullNameData = {};
		_fullNameData['originalFullNames'] = [constructFullName];
		_fullNameData['dialog'] = true;
		setFullNameData(() => ({
			..._fullNameData,
		}));
	};

	const handleSynonymsOpen = (constructSynonyms) => {
		let _synonymsData = {};
		_synonymsData['originalSynonyms'] = constructSynonyms;
		_synonymsData['dialog'] = true;
		setSynonymsData(() => ({
			..._synonymsData,
		}));
	};

	const handleSymbolOpen = (constructSymbol) => {
		let _symbolData = {};
		_symbolData['originalSymbols'] = [constructSymbol];
		_symbolData['dialog'] = true;
		setSymbolData(() => ({
			..._symbolData,
		}));
	};

	const handleComponentsOpen = (constructComponents) => {
		let _componentsData = {};
		_componentsData['originalComponents'] = constructComponents;
		_componentsData['dialog'] = true;
		setComponentsData(() => ({
			..._componentsData,
		}));
	};

	const handleGenomicComponentsOpen = (constructGenomicEntityAssociations) => {
		let _componentsData = {};
		_componentsData['originalComponents'] = constructGenomicEntityAssociations;
		_componentsData['dialog'] = true;
		setGenomicComponentsData(() => ({
			..._componentsData,
		}));
	};

	const getComponentsTextString = (item) => {
		let relationName = '';
		if (item?.relation?.name) {
			relationName = item.relation.name;
			if (relationName.indexOf(' (RO:') !== -1) {
				relationName = relationName.substring(0, relationName.indexOf(' (RO:'));
			}
		}
		return relationName + ': ' + item.componentSymbol;
	};

	const getComponentsAssociationTextString = (item) => {
		let symbolValue = '';
		if (
			item?.constructGenomicEntityAssociationObject?.geneSymbol ||
			item?.constructGenomicEntityAssociationObject?.alleleSymbol
		) {
			symbolValue = item.constructGenomicEntityAssociationObject.geneSymbol
				? item.constructGenomicEntityAssociationObject.geneSymbol.displayText
				: item.constructGenomicEntityAssociationObject.alleleSymbol.displayText;
		} else if (item?.constructGenomicEntityAssociationObject?.name) {
			symbolValue = item.constructGenomicEntityAssociationObject.name;
		} else {
			symbolValue = item.constructGenomicEntityAssociationObject.curie;
		}
		let relationName = '';
		if (item?.relation?.name) {
			relationName = item.relation.name;
			if (relationName.indexOf(' (RO:') !== -1) {
				relationName = relationName.substring(0, relationName.indexOf(' (RO:'));
			}
		}
		return relationName + ': ' + symbolValue;
	};

	const columns = [
		{
			field: 'uniqueId',
			header: 'Unique ID',
			sortable: { isInEditMode },
			body: (rowData) => <IdTemplate id={rowData.uniqueId} />,
			filterConfig: FILTER_CONFIGS.uniqueidFilterConfig,
		},
		{
			field: 'modEntityId',
			header: 'MOD Entity ID',
			sortable: { isInEditMode },
			body: (rowData) => <IdTemplate id={rowData.modEntityId} />,
			filterConfig: FILTER_CONFIGS.modentityidFilterConfig,
		},
		{
			field: 'modInternalId',
			header: 'MOD Internal ID',
			sortable: { isInEditMode },
			body: (rowData) => <IdTemplate id={rowData.modInternalId} />,
			filterConfig: FILTER_CONFIGS.modinternalidFilterConfig,
		},
		{
			field: 'constructSymbol.displayText',
			header: 'Symbol',
			sortable: true,
			body: (rowData) => (
				<TextDialogTemplate
					entity={rowData.constructSymbol}
					handleOpen={handleSymbolOpen}
					text={rowData.constructSymbol?.displayText}
					underline={false}
				/>
			),
			filter: true,
			filterConfig: FILTER_CONFIGS.constructSymbolFilterConfig,
		},
		{
			field: 'constructFullName.displayText',
			header: 'Name',
			sortable: true,
			filter: true,
			body: (rowData) => (
				<TextDialogTemplate
					entity={rowData.constructFullName}
					handleOpen={handleFullNameOpen}
					text={rowData.constructFullName?.displayText}
					underline={false}
				/>
			),
			filterConfig: FILTER_CONFIGS.constructNameFilterConfig,
		},
		{
			field: 'constructSynonyms.displayText',
			header: 'Synonyms',
			body: (rowData) => (
				<ListDialogTemplate
					entities={rowData.constructSynonyms}
					handleOpen={handleSynonymsOpen}
					getTextField={(entity) => entity?.displayText}
					underline={false}
				/>
			),
			sortable: true,
			filterConfig: FILTER_CONFIGS.constructSynonymsFilterConfig,
		},
		{
			field: 'secondaryIdentifiers',
			header: 'Secondary IDs',
			sortable: true,
			filterConfig: FILTER_CONFIGS.secondaryIdsFilterConfig,
			body: (rowData) => <StringListTemplate list={rowData.secondaryIdentifiers} />,
		},
		{
			field: 'constructComponents.componentSymbol',
			header: 'Free Text Components',
			body: (rowData) => (
				<ListDialogTemplate
					entities={rowData.constructComponents}
					handleOpen={handleComponentsOpen}
					getTextField={getComponentsTextString}
					underline={true}
				/>
			),
			sortable: { isInEditMode },
			filterConfig: FILTER_CONFIGS.constructComponentsFilterConfig,
		},
		{
			field: 'constructGenomicEntityAssociations.constructGenomicEntityAssociationObject.symbol',
			header: 'Component Associations',
			body: (rowData) => (
				<ListDialogTemplate
					entities={rowData.constructGenomicEntityAssociations}
					handleOpen={handleGenomicComponentsOpen}
					getTextField={getComponentsAssociationTextString}
					underline={true}
				/>
			),
			sortable: { isInEditMode },
			filterConfig: FILTER_CONFIGS.constructGenomicComponentsFilterConfig,
		},
		{
			field: 'references.primaryCrossReferenceCurie',
			header: 'References',
			body: (rowData) => <TruncatedReferencesTemplate references={rowData.references} />,
			sortable: { isInEditMode },
			filterConfig: FILTER_CONFIGS.referencesFilterConfig,
		},
		{
			field: 'dataProvider.sourceOrganization.abbreviation',
			header: 'Data Provider',
			sortable: { isInEditMode },
			filterConfig: FILTER_CONFIGS.constructDataProviderFilterConfig,
		},
		{
			field: 'updatedBy.uniqueId',
			header: 'Updated By',
			sortable: { isInEditMode },
			body: (rowData) => <StringTemplate string={rowData.updatedBy?.uniqueId} />,
			filterConfig: FILTER_CONFIGS.updatedByFilterConfig,
		},
		{
			field: 'dateUpdated',
			header: 'Date Updated',
			sortable: { isInEditMode },
			filter: true,
			body: (rowData) => <StringTemplate string={rowData.dateUpdated} />,
			filterConfig: FILTER_CONFIGS.dateUpdatedFilterConfig,
		},
		{
			field: 'createdBy.uniqueId',
			header: 'Created By',
			sortable: { isInEditMode },
			filter: true,
			body: (rowData) => <StringTemplate string={rowData.createdBy?.uniqueId} />,
			filterConfig: FILTER_CONFIGS.createdByFilterConfig,
		},
		{
			field: 'dateCreated',
			header: 'Date Created',
			sortable: { isInEditMode },
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
			sortable: { isInEditMode },
		},
		{
			field: 'obsolete',
			header: 'Obsolete',
			body: (rowData) => <BooleanTemplate value={rowData.obsolete} />,
			filter: true,
			filterConfig: FILTER_CONFIGS.obsoleteFilterConfig,
			sortable: { isInEditMode },
		},
	];

	const DEFAULT_COLUMN_WIDTH = 10;
	const SEARCH_ENDPOINT = 'construct';

	const initialTableState = getDefaultTableState('Constructs', columns, DEFAULT_COLUMN_WIDTH);

	const { settings: tableState, mutate: setTableState } = useGetUserSettings(
		initialTableState.tableSettingsKeyName,
		initialTableState
	);

	const { isFetching, isLoading } = useGetTableData({
		tableState,
		endpoint: SEARCH_ENDPOINT,
		setIsInEditMode,
		setEntities: setConstructs,
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
					dataKey="id"
					endpoint={SEARCH_ENDPOINT}
					tableName="Constructs"
					entities={constructs}
					setEntities={setConstructs}
					totalRecords={totalRecords}
					setTotalRecords={setTotalRecords}
					tableState={tableState}
					setTableState={setTableState}
					columns={columns}
					isEditable={false}
					hasDetails={false}
					isInEditMode={isInEditMode}
					setIsInEditMode={setIsInEditMode}
					toasts={{ toast_topleft, toast_topright }}
					errorObject={{ errorMessages, setErrorMessages }}
					defaultColumnWidth={DEFAULT_COLUMN_WIDTH}
					fetching={isFetching || isLoading}
				/>
			</div>
			<FullNameDialog
				name="Construct Name"
				field="constructFullName"
				endpoint="constructfullnameslotannotation"
				originalFullNameData={fullNameData}
				setOriginalFullNameData={setFullNameData}
			/>
			<SymbolDialog
				name="Construct Symbol"
				field="constructSymbol"
				endpoint="constructsymbolslotannotation"
				originalSymbolData={symbolData}
				setOriginalSymbolData={setSymbolData}
			/>
			<SynonymsDialog
				name="Construct Synonym"
				field="constructSynonyms"
				endpoint="constructsynonymslotannotation"
				originalSynonymsData={synonymsData}
				setOriginalSynonymsData={setSynonymsData}
			/>
			<ComponentsDialog
				originalComponentsData={componentsData}
				setOriginalComponentsData={setComponentsData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
			/>
			<GenomicComponentsDialog
				originalComponentsData={genomicComponentsData}
				setOriginalComponentsData={setGenomicComponentsData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
			/>
		</>
	);
};
