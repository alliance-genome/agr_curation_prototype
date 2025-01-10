import React, { useRef, useState } from 'react';

import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { ConditionRelationsDialog } from '../../components/ConditionRelationsDialog';
import { SingleReferenceTemplate } from '../../components/Templates/reference/SingleReferenceTemplate';
import { IdTemplate } from '../../components/Templates/IdTemplate';
import { GenomicEntityTemplate } from '../../components/Templates/genomicEntity/GenomicEntityTemplate';
import { GenomicEntityListTemplate } from '../../components/Templates/genomicEntity/GenomicEntityListTemplate';
import { BooleanTemplate } from '../../components/Templates/BooleanTemplate';
import { Button } from 'primereact/button';
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_CONFIGS } from '../../constants/FilterFields';
import { useGetTableData } from '../../service/useGetTableData';
import { useGetUserSettings } from '../../service/useGetUserSettings';

import { SearchService } from '../../service/SearchService';

export const PhenotypeAnnotationsTable = () => {
	const [isInEditMode, setIsInEditMode] = useState(false); //needs better name
	const [totalRecords, setTotalRecords] = useState(0);
	const [phenotypeAnnotations, setPhenotypeAnnotations] = useState([]);
	const [conditionRelationsData, setConditionRelationsData] = useState({
		conditionRelations: [],
		isInEdit: false,
		dialog: false,
		rowIndex: null,
		mailRowProps: {},
	});
	const [errorMessages, setErrorMessages] = useState({});
	const errorMessagesRef = useRef();
	errorMessagesRef.current = errorMessages;

	const [uiErrorMessages, setUiErrorMessages] = useState([]);
	const uiErrorMessagesRef = useRef();
	uiErrorMessagesRef.current = uiErrorMessages;

	const searchService = new SearchService();

	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);

	const sortMapping = {
		'phenotypeAnnotationObject.name': ['phenotypeAnnotationObject.curie', 'phenotypeAnnotationObject.namespace'],
		'phenotypeAnnotationSubject.symbol': [
			'phenotypeAnnotationSubject.name',
			'phenotypeAnnotationSubject.primaryExternalId',
		],
		'sgdStrainBackground.name': ['sgdStrainBackground.primaryExternalId'],
	};

	const handleConditionRelationsOpen = (event, rowData, isInEdit) => {
		let _conditionRelationsData = {};
		_conditionRelationsData['originalConditionRelations'] = rowData.conditionRelations;
		_conditionRelationsData['dialog'] = true;
		_conditionRelationsData['isInEdit'] = isInEdit;
		setConditionRelationsData(() => ({
			..._conditionRelationsData,
		}));
	};

	const conditionRelationsTemplate = (rowData) => {
		if (rowData?.conditionRelations && !rowData.conditionRelations[0]?.handle) {
			return (
				<Button
					className="p-button-text"
					onClick={(event) => {
						handleConditionRelationsOpen(event, rowData);
					}}
				>
					<span className="-my-4 p-1 underline">{`Conditions (${rowData.conditionRelations.length})`}</span>
				</Button>
			);
		}
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
			field: 'phenotypeAnnotationSubject.symbol',
			header: 'Subject',
			body: (rowData) => <GenomicEntityTemplate genomicEntity={rowData.phenotypeAnnotationSubject} />,
			sortable: true,
			filterConfig: FILTER_CONFIGS.phenotypeAnnotationSubjectFilterConfig,
		},
		{
			field: 'relation.name',
			header: 'Phenotype Relation',
			sortable: true,
			filterConfig: FILTER_CONFIGS.paRelationFilterConfig,
		},
		{
			field: 'phenotypeAnnotationObject',
			header: 'Phenotype',
			body: (rowData) => <IdTemplate id={rowData.phenotypeAnnotationObject} />,
			sortable: true,
			filterConfig: FILTER_CONFIGS.phenotypeAnnotationObjectFilterConfig,
		},
		{
			field: 'singleReference.primaryCrossReferenceCurie',
			header: 'Reference',
			body: (rowData) => <SingleReferenceTemplate singleReference={rowData.singleReference} />,
			sortable: true,
			filterConfig: FILTER_CONFIGS.singleReferenceFilterConfig,
		},
		{
			field: 'crossReference.displayName',
			header: 'Cross Reference',
			sortable: true,
			filterConfig: FILTER_CONFIGS.crossReferenceFilterConfig,
		},
		{
			field: 'conditionRelations.uniqueId',
			header: 'Experimental Conditions',
			body: conditionRelationsTemplate,
			sortable: true,
			filterConfig: FILTER_CONFIGS.paConditionRelationsSummaryFilterConfig,
		},
		{
			field: 'sgdStrainBackground.name',
			header: 'SGD Strain Background',
			body: (rowData) => <GenomicEntityTemplate genomicEntity={rowData.sgdStrainBackground} />,
			sortable: true,
			filterConfig: FILTER_CONFIGS.sgdStrainBackgroundFilterConfig,
		},
		{
			field: 'inferredGene.geneSymbol.displayText',
			header: 'Inferred Gene',
			body: (rowData) => <GenomicEntityTemplate genomicEntity={rowData.inferredGene} />,
			sortable: true,
			filterConfig: FILTER_CONFIGS.inferredGeneFilterConfig,
		},
		{
			field: 'assertedGenes.geneSymbol.displayText',
			header: 'Asserted Genes',
			body: (rowData) => <GenomicEntityListTemplate genomicEntities={rowData.assertedGenes} />,
			sortable: true,
			filterConfig: FILTER_CONFIGS.assertedGenesFilterConfig,
		},
		{
			field: 'inferredAllele.alleleSymbol.displayText',
			header: 'Inferred Allele',
			body: (rowData) => <GenomicEntityTemplate genomicEntity={rowData.inferredAllele} />,
			sortable: true,
			filterConfig: FILTER_CONFIGS.inferredAlleleFilterConfig,
		},
		{
			field: 'assertedAllele.alleleSymbol.displayText',
			header: 'Asserted Allele',
			body: (rowData) => <GenomicEntityTemplate genomicEntity={rowData.assertedAllele} />,
			sortable: true,
			filterConfig: FILTER_CONFIGS.assertedAlleleFilterConfig,
		},
		{
			field: 'dataProvider.abbreviation',
			header: 'Data Provider',
			sortable: true,
			filterConfig: FILTER_CONFIGS.phenotypeDataProviderFilterConfig,
		},
		{
			field: 'dateCreated',
			header: 'Date Created',
			sortable: true,
			filterConfig: FILTER_CONFIGS.dataCreatedFilterConfig,
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
	const SEARCH_ENDPOINT = 'phenotype-annotation';
	const defaultFilters = { obsoleteFilter: { obsolete: { queryString: 'false' } } };

	const initialTableState = getDefaultTableState('PhenotypeAnnotations', columns, DEFAULT_COLUMN_WIDTH, defaultFilters);

	const { settings: tableState, mutate: setTableState } = useGetUserSettings(
		initialTableState.tableSettingsKeyName,
		initialTableState
	);

	const { isFetching, isLoading } = useGetTableData({
		tableState,
		endpoint: SEARCH_ENDPOINT,
		setIsInEditMode,
		setEntities: setPhenotypeAnnotations,
		setTotalRecords,
		toast_topleft,
		searchService,
	});

	return (
		<>
			<div className="card">
				<GenericDataTable
					endpoint={SEARCH_ENDPOINT}
					tableName="Phenotype Annotations"
					entities={phenotypeAnnotations}
					setEntities={setPhenotypeAnnotations}
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
			<ConditionRelationsDialog
				originalConditionRelationsData={conditionRelationsData}
				setOriginalConditionRelationsData={setConditionRelationsData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
			/>
		</>
	);
};
