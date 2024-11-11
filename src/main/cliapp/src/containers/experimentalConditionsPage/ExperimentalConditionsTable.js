import React, { useRef, useState } from 'react';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { InputTextEditor } from '../../components/InputTextEditor';
import { AutocompleteEditor } from '../../components/Autocomplete/AutocompleteEditor';
import { useMutation } from '@tanstack/react-query';
import { Toast } from 'primereact/toast';
import { SearchService } from '../../service/SearchService';
import { ErrorMessageComponent } from '../../components/Error/ErrorMessageComponent';
import { ExperimentalConditionService } from '../../service/ExperimentalConditionService';
import { Button } from 'primereact/button';
import { TrueFalseDropdown } from '../../components/TrueFalseDropDownSelector';
import { NewConditionForm } from './NewConditionForm';
import { useControlledVocabularyService } from '../../service/useControlledVocabularyService';
import { useNewConditionReducer } from './useNewConditionReducer';
import {
	defaultAutocompleteOnChange,
	autocompleteSearch,
	buildAutocompleteFilter,
	setNewEntity,
} from '../../utils/utils';
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_CONFIGS } from '../../constants/FilterFields';
import { useGetTableData } from '../../service/useGetTableData';
import { useGetUserSettings } from '../../service/useGetUserSettings';
import { IdTemplate } from '../../components/Templates/IdTemplate';
import { StringTemplate } from '../../components/Templates/StringTemplate';
import { BooleanTemplate } from '../../components/Templates/BooleanTemplate';
import { NumberTemplate } from '../../components/Templates/NumberTemplate';
import { OntologyTermTemplate } from '../../components/Templates/OntologyTermTemplate';

export const ExperimentalConditionsTable = () => {
	const [errorMessages, setErrorMessages] = useState({});
	const [isInEditMode, setIsInEditMode] = useState(false);
	const [totalRecords, setTotalRecords] = useState(0);
	const { newConditionState, newConditionDispatch } = useNewConditionReducer();
	const [experimentalConditions, setExperimentalConditions] = useState([]);

	const booleanTerms = useControlledVocabularyService('generic_boolean_terms');
	const searchService = new SearchService();
	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);
	const errorMessagesRef = useRef();
	errorMessagesRef.current = errorMessages;

	let experimentalConditionService = new ExperimentalConditionService();

	const curieAutocompleteFields = [
		'curie',
		'name',
		'crossReferences.referencedCurie',
		'secondaryIdentifiers',
		'synonyms.name',
	];

	const sortMapping = {
		'conditionGeneOntology.name': ['conditionGeneOntology.curie', 'conditionGeneOntology.namespace'],
	};

	const mutation = useMutation((updatedCondition) => {
		if (!experimentalConditionService) {
			experimentalConditionService = new ExperimentalConditionService();
		}
		return experimentalConditionService.saveExperimentalCondition(updatedCondition);
	});

	const handleNewConditionOpen = () => {
		newConditionDispatch({ type: 'OPEN_DIALOG' });
	};

	const freeTextEditor = (props, fieldname) => {
		return (
			<>
				<InputTextEditor rowProps={props} fieldName={fieldname} />
				<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField={fieldname} />
			</>
		);
	};

	const onInternalEditorValueChange = (props, event) => {
		let updatedAnnotations = [...props.props.value];
		if (event.value || event.value === '') {
			updatedAnnotations[props.rowIndex].internal = JSON.parse(event.value.name);
		}
	};

	const internalEditor = (props) => {
		return (
			<>
				<TrueFalseDropdown
					options={booleanTerms}
					editorChange={onInternalEditorValueChange}
					props={props}
					field={'internal'}
				/>
				<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField={'internal'} />
			</>
		);
	};

	const onConditionClassValueChange = (event, setFieldValue, props) => {
		defaultAutocompleteOnChange(props, event, 'conditionClass', setFieldValue);
	};

	const conditionClassSearch = (event, setFiltered, setQuery) => {
		const endpoint = 'zecoterm';
		const filterName = 'conditionClassEditorFilter';
		const filter = buildAutocompleteFilter(event, curieAutocompleteFields);
		const otherFilters = {
			subsetFilter: {
				subsets: {
					queryString: 'ZECO_0000267',
				},
			},
		};

		setQuery(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered, otherFilters);
	};

	const conditionClassEditorTemplate = (props) => {
		return (
			<>
				<AutocompleteEditor
					search={conditionClassSearch}
					initialValue={props.rowData.conditionClass?.curie}
					rowProps={props}
					fieldName="conditionClass"
					onValueChangeHandler={onConditionClassValueChange}
				/>
				<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField="conditionClass" />
			</>
		);
	};

	const onSingleOntologyValueChange = (event, setFieldValue, props, fieldName) => {
		defaultAutocompleteOnChange(props, event, fieldName, setFieldValue);
	};

	const singleOntologySearch = (event, setFiltered, endpoint, autocomplete, setQuery) => {
		const filterName = 'singleOntologyFilter';
		const filter = buildAutocompleteFilter(event, autocomplete);

		setQuery(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
	};

	const singleOntologyEditorTemplate = (props, fieldname, endpoint, autocomplete) => {
		return (
			<>
				<AutocompleteEditor
					search={(event, setFiltered, setQuery) =>
						singleOntologySearch(event, setFiltered, endpoint, autocomplete, setQuery)
					}
					initialValue={props.rowData[fieldname]?.curie}
					rowProps={props}
					filterName="singleOntologyFilter"
					fieldName={fieldname}
					onValueChangeHandler={onSingleOntologyValueChange}
				/>
				<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField={fieldname} />
			</>
		);
	};

	const columns = [
		{
			field: 'uniqueId',
			header: 'Unique ID',
			sortable: true,
			body: (rowData) => <IdTemplate id={rowData.uniqueId} />,
			filterConfig: FILTER_CONFIGS.uniqueidFilterConfig,
		},
		{
			field: 'conditionSummary',
			header: 'Summary',
			sortable: true,
			body: (rowData) => <StringTemplate string={rowData.conditionSummary} />,
			filterConfig: FILTER_CONFIGS.conditionRelationSummaryFilterConfig,
		},
		{
			field: 'conditionClass.name',
			header: 'Class',
			sortable: true,
			body: (rowData) => <OntologyTermTemplate term={rowData.conditionClass} />,
			filterConfig: FILTER_CONFIGS.conditionClassFilterConfig,
			editor: (props) => conditionClassEditorTemplate(props, curieAutocompleteFields),
		},
		{
			field: 'conditionId.name',
			header: 'Condition Term',
			sortable: true,
			body: (rowData) => <OntologyTermTemplate term={rowData.conditionId} />,
			filterConfig: FILTER_CONFIGS.conditionIdFilterConfig,
			editor: (props) =>
				singleOntologyEditorTemplate(
					props,
					'conditionId',
					'experimentalconditionontologyterm',
					curieAutocompleteFields
				),
		},
		{
			field: 'conditionGeneOntology.name',
			header: 'Gene Ontology',
			sortable: true,
			body: (rowData) => <OntologyTermTemplate term={rowData.conditionGeneOntology} />,
			filterConfig: FILTER_CONFIGS.conditionGeneOntologyFilterConfig,
			editor: (props) =>
				singleOntologyEditorTemplate(props, 'conditionGeneOntology', 'goterm', curieAutocompleteFields),
		},
		{
			field: 'conditionChemical.name',
			header: 'Chemical',
			sortable: true,
			body: (rowData) => <OntologyTermTemplate term={rowData.conditionChemical} />,
			filterConfig: FILTER_CONFIGS.conditionChemicalFilterConfig,
			editor: (props) =>
				singleOntologyEditorTemplate(props, 'conditionChemical', 'chemicalterm', curieAutocompleteFields),
		},
		{
			field: 'conditionAnatomy.name',
			header: 'Anatomy',
			sortable: true,
			body: (rowData) => <OntologyTermTemplate term={rowData.conditionAnatomy} />,
			filterConfig: FILTER_CONFIGS.conditionAnatomyFilterConfig,
			editor: (props) =>
				singleOntologyEditorTemplate(props, 'conditionAnatomy', 'anatomicalterm', curieAutocompleteFields),
		},
		{
			field: 'conditionTaxon.name',
			header: 'Condition Taxon',
			sortable: true,
			body: (rowData) => <OntologyTermTemplate term={rowData.conditionTaxon} />,
			filterConfig: FILTER_CONFIGS.conditionTaxonFilterConfig,
			editor: (props) =>
				singleOntologyEditorTemplate(props, 'conditionTaxon', 'ncbitaxonterm', curieAutocompleteFields),
		},
		{
			field: 'conditionQuantity',
			header: 'Quantity',
			sortable: true,
			body: (rowData) => <NumberTemplate number={rowData.conditionQuantity} />,
			filterConfig: FILTER_CONFIGS.conditionQuantityFilterConfig,
			editor: (props) => freeTextEditor(props, 'conditionQuantity'),
		},
		{
			field: 'conditionFreeText',
			header: 'Free Text',
			sortable: true,
			body: (rowData) => <StringTemplate string={rowData.conditionFreeText} />,
			filterConfig: FILTER_CONFIGS.conditionFreeTextFilterConfig,
			editor: (props) => freeTextEditor(props, 'conditionFreeText'),
		},
		{
			field: 'internal',
			header: 'Internal',
			body: (rowData) => <BooleanTemplate value={rowData.internal} />,
			filterConfig: FILTER_CONFIGS.internalFilterConfig,
			sortable: true,
			editor: (props) => internalEditor(props),
		},
	];

	const DEFAULT_COLUMN_WIDTH = 10;
	const SEARCH_ENDPOINT = 'experimental-condition';

	const initialTableState = getDefaultTableState('ExperimentalConditions', columns, DEFAULT_COLUMN_WIDTH);

	const { settings: tableState, mutate: setTableState } = useGetUserSettings(
		initialTableState.tableSettingsKeyName,
		initialTableState
	);

	const { isLoading, isFetching } = useGetTableData({
		tableState,
		endpoint: SEARCH_ENDPOINT,
		sortMapping,
		setIsInEditMode,
		setEntities: setExperimentalConditions,
		setTotalRecords,
		toast_topleft,
		searchService,
	});

	const headerButtons = (disabled = false) => {
		return (
			<>
				<Button label="New Condition" icon="pi pi-plus" onClick={handleNewConditionOpen} disabled={disabled} />
				&nbsp;&nbsp;
			</>
		);
	};

	return (
		<div className="card">
			<Toast ref={toast_topleft} position="top-left" />
			<Toast ref={toast_topright} position="top-right" />
			<GenericDataTable
				endpoint={SEARCH_ENDPOINT}
				tableName="Experimental Conditions"
				entities={experimentalConditions}
				setEntities={setExperimentalConditions}
				totalRecords={totalRecords}
				setTotalRecords={setTotalRecords}
				tableState={tableState}
				setTableState={setTableState}
				columns={columns}
				isEditable={true}
				curieFields={[
					'conditionClass',
					'conditionId',
					'conditionAnatomy',
					'conditionTaxon',
					'conditionGeneOntology',
					'conditionChemical',
				]}
				sortMapping={sortMapping}
				mutation={mutation}
				isInEditMode={isInEditMode}
				setIsInEditMode={setIsInEditMode}
				headerButtons={headerButtons}
				toasts={{ toast_topleft, toast_topright }}
				errorObject={{ errorMessages, setErrorMessages }}
				deletionEnabled={true}
				deletionMethod={experimentalConditionService.deleteExperimentalCondition}
				defaultColumnWidth={DEFAULT_COLUMN_WIDTH}
				fetching={isFetching || isLoading}
			/>
			<NewConditionForm
				newConditionState={newConditionState}
				newConditionDispatch={newConditionDispatch}
				searchService={searchService}
				mutation={mutation}
				setNewExperimentalCondition={(newExCon, queryClient) =>
					setNewEntity(tableState, setExperimentalConditions, newExCon, queryClient)
				}
				curieAutocompleteFields={curieAutocompleteFields}
			/>
		</div>
	);
};
