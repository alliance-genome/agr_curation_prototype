import React, { useRef, useState } from 'react';

import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { IdTemplate } from '../../components/Templates/IdTemplate';
import { GenomicEntityTemplate } from '../../components/Templates/genomicEntity/GenomicEntityTemplate';
import { BooleanTemplate } from '../../components/Templates/BooleanTemplate';
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_CONFIGS } from '../../constants/FilterFields';
import { OntologyTermTemplate } from '../../components/Templates/OntologyTermTemplate';
import { useGetTableData } from '../../service/useGetTableData';
import { useGetUserSettings } from '../../service/useGetUserSettings';
import { TruncatedReferencesTemplate } from '../../components/Templates/reference/TruncatedReferencesTemplate';
import { StringTemplate } from '../../components/Templates/StringTemplate';

import { SearchService } from '../../service/SearchService';

export const GeneMolecularInteractionsTable = () => {
	const [isInEditMode, setIsInEditMode] = useState(false); //needs better name
	const [errorMessages, setErrorMessages] = useState({});
	const [totalRecords, setTotalRecords] = useState(0);
	const [geneMolecularInteractions, setGeneMolecularInteractions] = useState([]);
	const errorMessagesRef = useRef();
	errorMessagesRef.current = errorMessages;

	const searchService = new SearchService();

	const [uiErrorMessages, setUiErrorMessages] = useState([]);
	const uiErrorMessagesRef = useRef();
	uiErrorMessagesRef.current = uiErrorMessages;

	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);

	const sortMapping = {
		'geneAssociationSubject.geneSymbol.displayText': [
			'geneAssociationSubject.geneFullName.displayText',
			'geneAssociationSubject.modEntityId',
		],
		'geneGeneAssociationObject.geneSymbol.displayText': [
			'geneGeneAssociationObject.geneFullName.displayText',
			'geneGeneAssociationObject.modEntityId',
		],
		'phenotypeAnnotationObject.name': ['phenotypeAnnotationObject.curie', 'phenotypeAnnotationObject.namespace'],
		'interactionType.name': ['interactionType.curie'],
		'interactorARole.name': ['interactorARole.curie'],
		'interactorBRole.name': ['interactorBRole.curie'],
		'interactorAType.name': ['interactorAType.curie'],
		'interactorBType.name': ['interactorBType.curie'],
		'interactionSource.name': ['interactionSource.curie'],
		'aggregationDatabase.name': ['aggregationDatabase.curie'],
		'detectionMethod.name': ['detectionMethod.curie'],
	};

	const columns = [
		{
			field: 'uniqueId',
			header: 'Unique ID',
			body: (rowData) => <IdTemplate id={rowData.uniqueId} />,
			sortable: true,
			filterConfig: FILTER_CONFIGS.uniqueidFilterConfig,
		},
		{
			field: 'interactionId',
			header: 'Interaction ID',
			body: (rowData) => <IdTemplate id={rowData.interactionId} />,
			sortable: true,
			filterConfig: FILTER_CONFIGS.interactionIdFilterConfig,
		},
		{
			field: 'geneAssociationSubject.geneSymbol.displayText',
			header: 'Interactor A',
			body: (rowData) => <GenomicEntityTemplate genomicEntity={rowData.geneAssociationSubject} />,
			sortable: true,
			filterConfig: FILTER_CONFIGS.geneAssociationSubjectFilterConfig,
		},
		{
			field: 'relation.name',
			header: 'Interaction Relation',
			sortable: true,
			body: (rowData) => <StringTemplate string={rowData.relation?.name} />,
			filterConfig: FILTER_CONFIGS.gmiRelationFilterConfig,
		},
		{
			field: 'geneGeneAssociationObject.geneSymbol.displayText',
			header: 'Interactor B',
			body: (rowData) => <GenomicEntityTemplate genomicEntity={rowData.geneGeneAssociationObject} />,
			sortable: true,
			filterConfig: FILTER_CONFIGS.geneGeneAssociationObjectFilterConfig,
		},
		{
			field: 'interactionType.name',
			header: 'Interaction Type',
			body: (rowData) => <OntologyTermTemplate term={rowData.interactionType} />,
			sortable: true,
			filterConfig: FILTER_CONFIGS.interactionTypeFilterConfig,
		},
		{
			field: 'interactorARole.name',
			header: 'Interactor A Role',
			body: (rowData) => <OntologyTermTemplate term={rowData.interactorARole} />,
			sortable: true,
			filterConfig: FILTER_CONFIGS.interactorARoleFilterConfig,
		},
		{
			field: 'interactorBRole.name',
			header: 'Interactor B Role',
			body: (rowData) => <OntologyTermTemplate term={rowData.interactorBRole} />,
			sortable: true,
			filterConfig: FILTER_CONFIGS.interactorBRoleFilterConfig,
		},
		{
			field: 'interactorAType.name',
			header: 'Interactor A Type',
			body: (rowData) => <OntologyTermTemplate term={rowData.interactorAType} />,
			sortable: true,
			filterConfig: FILTER_CONFIGS.interactorATypeFilterConfig,
		},
		{
			field: 'interactorBType.name',
			header: 'Interactor B Type',
			body: (rowData) => <OntologyTermTemplate term={rowData.interactorBType} />,
			sortable: true,
			filterConfig: FILTER_CONFIGS.interactorBTypeFilterConfig,
		},
		{
			field: 'interactionSource.name',
			header: 'Interaction Source',
			body: (rowData) => <OntologyTermTemplate term={rowData.interactionSource} />,
			sortable: true,
			filterConfig: FILTER_CONFIGS.interactionSourceFilterConfig,
		},
		{
			field: 'aggregationDatabase.name',
			header: 'Aggregation DB',
			body: (rowData) => <OntologyTermTemplate term={rowData.aggregationDatabase} />,
			sortable: true,
			filterConfig: FILTER_CONFIGS.aggregationDatabaseFilterConfig,
		},
		{
			field: 'detectionMethod.name',
			header: 'Detection Method',
			body: (rowData) => <OntologyTermTemplate term={rowData.detectionMethod} />,
			sortable: true,
			filterConfig: FILTER_CONFIGS.detectionMethodFilterConfig,
		},
		{
			field: 'evidence.curie',
			header: 'Evidence',
			body: (rowData) => <TruncatedReferencesTemplate references={rowData.evidence} />,
			sortable: true,
			filterConfig: FILTER_CONFIGS.evidenceFilterConfig,
		},
		{
			field: 'internal',
			header: 'Internal',
			body: (rowData) => <BooleanTemplate value={rowData.internal} />,
			sortable: true,
			filterConfig: FILTER_CONFIGS.internalFilterConfig,
		},
		{
			field: 'obsolete',
			header: 'Obsolete',
			body: (rowData) => <BooleanTemplate value={rowData.obsolete} />,
			sortable: true,
			filterConfig: FILTER_CONFIGS.obsoleteFilterConfig,
		},
	];

	const DEFAULT_COLUMN_WIDTH = 10;
	const SEARCH_ENDPOINT = 'gene-molecular-interaction';
	const defaultFilters = { obsoleteFilter: { obsolete: { queryString: 'false' } } };

	const initialTableState = getDefaultTableState(
		'GeneMolecularInteractions',
		columns,
		DEFAULT_COLUMN_WIDTH,
		defaultFilters
	);

	const { settings: tableState, mutate: setTableState } = useGetUserSettings(
		initialTableState.tableSettingsKeyName,
		initialTableState
	);

	const { isFetching, isLoading } = useGetTableData({
		tableState,
		endpoint: SEARCH_ENDPOINT,
		setIsInEditMode,
		setEntities: setGeneMolecularInteractions,
		setTotalRecords,
		toast_topleft,
		searchService,
	});

	return (
		<>
			<div className="card">
				<GenericDataTable
					endpoint={SEARCH_ENDPOINT}
					tableName="Gene Molecular Interactions"
					entities={geneMolecularInteractions}
					setEntities={setGeneMolecularInteractions}
					totalRecords={totalRecords}
					setTotalRecords={setTotalRecords}
					tableState={tableState}
					setTableState={setTableState}
					columns={columns}
					toasts={{ toast_topleft, toast_topright }}
					isEditable={false}
					isInEditMode={isInEditMode}
					setIsInEditMode={setIsInEditMode}
					sortMapping={sortMapping}
					errorObject={{ errorMessages, setErrorMessages, uiErrorMessages, setUiErrorMessages }}
					deletionEnabled={false}
					deprecateOption={false}
					modReset={false}
					duplicationEnabled={false}
					defaultColumnWidth={DEFAULT_COLUMN_WIDTH}
					fetching={isFetching || isLoading}
					defaultFilters={defaultFilters}
				/>
			</div>
		</>
	);
};
