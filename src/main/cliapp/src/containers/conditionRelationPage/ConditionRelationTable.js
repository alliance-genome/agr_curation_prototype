import React, { useRef, useState } from 'react';
import { useMutation } from '@tanstack/react-query';
import { Toast } from 'primereact/toast';
import { SearchService } from '../../service/SearchService';
import { Messages } from 'primereact/messages';
import { ControlledVocabularyDropdown } from '../../components/ControlledVocabularySelector';
import { ErrorMessageComponent } from '../../components/Error/ErrorMessageComponent';
import { useControlledVocabularyService } from '../../service/useControlledVocabularyService';
import { Button } from 'primereact/button';
import { ConditionRelationService } from '../../service/ConditionRelationService';
import { AutocompleteEditor } from '../../components/Autocomplete/AutocompleteEditor';
import { ExConAutocompleteTemplate } from '../../components/Autocomplete/ExConAutocompleteTemplate';
import { LiteratureAutocompleteTemplate } from '../../components/Autocomplete/LiteratureAutocompleteTemplate';
import { NewRelationForm } from './NewRelationForm';
import { useNewRelationReducer } from './useNewRelationReducer';
import { InputTextEditor } from '../../components/InputTextEditor';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import {
	defaultAutocompleteOnChange,
	autocompleteSearch,
	buildAutocompleteFilter,
	getRefString,
	multipleAutocompleteOnChange,
	setNewEntity,
} from '../../utils/utils';
import { AutocompleteMultiEditor } from '../../components/Autocomplete/AutocompleteMultiEditor';
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_CONFIGS } from '../../constants/FilterFields';
import { useGetTableData } from '../../service/useGetTableData';
import { useGetUserSettings } from '../../service/useGetUserSettings';
import { ObjectListTemplate } from '../../components/Templates/ObjectListTemplate';
import { SingleReferenceTemplate } from '../../components/Templates/reference/SingleReferenceTemplate';
import { conditionsSort } from '../../components/Templates/utils/sortMethods';

export const ConditionRelationTable = () => {
	const [isInEditMode, setIsInEditMode] = useState(false);
	const [totalRecords, setTotalRecords] = useState(0);
	const { newRelationState, newRelationDispatch } = useNewRelationReducer();
	const [conditionRelations, setConditionRelations] = useState([]);

	const searchService = new SearchService();
	const errorMessage = useRef(null);
	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);
	const [errorMessages, setErrorMessages] = useState({});
	const errorMessagesRef = useRef();
	errorMessagesRef.current = errorMessages;

	let conditionRelationService = new ConditionRelationService();

	const conditionRelationTypeTerms = useControlledVocabularyService('condition_relation');

	const mutation = useMutation((updatedRelation) => {
		if (!conditionRelationService) {
			conditionRelationService = new ConditionRelationService();
		}
		return conditionRelationService.saveConditionRelation(updatedRelation);
	});

	const handleNewRelationOpen = () => {
		newRelationDispatch({ type: 'OPEN_DIALOG' });
	};

	const onConditionRelationTypeValueChange = (props, event) => {
		let updatedConditions = [...props.props.value];
		if (event.value || event.value === '') {
			updatedConditions[props.rowIndex].conditionRelationType = event.value;
		}
	};

	const conditionRelationTypeEditor = (props) => {
		return (
			<>
				<ControlledVocabularyDropdown
					field="conditionRelationType"
					options={conditionRelationTypeTerms}
					editorChange={onConditionRelationTypeValueChange}
					props={props}
					showClear={false}
					placeholderText={props.rowData.conditionRelationType.name}
				/>
				<ErrorMessageComponent
					errorMessages={errorMessagesRef.current[props.rowIndex]}
					errorField={'conditionRelationType'}
				/>
			</>
		);
	};

	const onReferenceValueChange = (event, setFieldValue, props) => {
		defaultAutocompleteOnChange(props, event, 'singleReference', setFieldValue);
	};

	const referenceSearch = (event, setFiltered, setQuery) => {
		const autocompleteFields = ['curie', 'cross_references.curie'];
		const endpoint = 'literature-reference';
		const filterName = 'curieFilter';
		const filter = buildAutocompleteFilter(event, autocompleteFields);
		setQuery(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
	};

	const referenceEditorTemplate = (props) => {
		return (
			<>
				<AutocompleteEditor
					search={referenceSearch}
					initialValue={() => getRefString(props.rowData.singleReference)}
					rowProps={props}
					fieldName="singleReference"
					valueDisplay={(item, setAutocompleteHoverItem, op, query) => (
						<LiteratureAutocompleteTemplate
							item={item}
							setAutocompleteHoverItem={setAutocompleteHoverItem}
							op={op}
							query={query}
						/>
					)}
					onValueChangeHandler={onReferenceValueChange}
				/>
				<ErrorMessageComponent
					errorMessages={errorMessagesRef.current[props.rowIndex]}
					errorField={'singleReference'}
				/>
			</>
		);
	};

	const onConditionRelationValueChange = (event, setFieldValue, props) => {
		multipleAutocompleteOnChange(props, event, 'conditions', setFieldValue);
	};

	const conditionRelationSearch = (event, setFiltered, setInputValue) => {
		const autocompleteFields = ['conditionSummary'];
		const endpoint = 'experimental-condition';
		const filterName = 'experimentalConditionFilter';
		const filter = buildAutocompleteFilter(event, autocompleteFields);

		setInputValue(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
	};

	const conditionRelationTemplate = (props) => {
		return (
			<>
				<AutocompleteMultiEditor
					search={conditionRelationSearch}
					initialValue={props.rowData.conditions}
					rowProps={props}
					fieldName="conditions"
					subField="conditionSummary"
					valueDisplay={(item, setAutocompleteHoverItem, op, query) => (
						<ExConAutocompleteTemplate
							item={item}
							setAutocompleteHoverItem={setAutocompleteHoverItem}
							op={op}
							query={query}
						/>
					)}
					onValueChangeHandler={onConditionRelationValueChange}
				/>
				<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField="conditions" />
			</>
		);
	};

	const handleEditor = (props) => {
		return (
			<>
				<InputTextEditor rowProps={props} fieldName={'handle'} />
				<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField={'handle'} />
			</>
		);
	};

	const columns = [
		{
			field: 'handle',
			header: 'Handle',
			sortable: true,
			body: (rowData) => rowData.handle,
			filterConfig: FILTER_CONFIGS.conditionRelationHandleFilterConfig,
			editor: (props) => handleEditor(props),
		},
		{
			field: 'singleReference.primaryCrossReferenceCurie',
			header: 'Reference',
			sortable: true,
			filterConfig: FILTER_CONFIGS.singleReferenceFilterConfig,
			editor: (props) => referenceEditorTemplate(props),
			body: (rowData) => <SingleReferenceTemplate singleReference={rowData.singleReference} />,
		},
		{
			field: 'conditionRelationType.name',
			header: 'Relation',
			sortable: true,
			filterConfig: FILTER_CONFIGS.conditionRelationTypeFilterConfig,
			editor: (props) => conditionRelationTypeEditor(props),
		},
		{
			field: 'conditions.conditionSummary',
			header: 'Experimental Conditions',
			sortable: true,
			body: (rowData) => (
				<ObjectListTemplate
					list={rowData.conditions}
					sortMethod={conditionsSort}
					stringTemplate={(item) => item.conditionSummary}
					showBullets={true}
				/>
			),
			filterConfig: FILTER_CONFIGS.experimentalConditionFilterConfig,
			editor: (props) => conditionRelationTemplate(props),
		},
	];

	const DEFAULT_COLUMN_WIDTH = 10;
	const SEARCH_ENDPOINT = 'condition-relation';

	const initialTableState = getDefaultTableState('Experiments', columns, DEFAULT_COLUMN_WIDTH);

	const { settings: tableState, mutate: setTableState } = useGetUserSettings(
		initialTableState.tableSettingsKeyName,
		initialTableState
	);

	const { isLoading, isFetching } = useGetTableData({
		tableState,
		endpoint: SEARCH_ENDPOINT,
		setIsInEditMode,
		setEntities: setConditionRelations,
		setTotalRecords,
		toast_topleft,
		searchService,
	});

	const headerButtons = (disabled = false) => {
		return (
			<>
				<Button label="New Condition Relation" icon="pi pi-plus" onClick={handleNewRelationOpen} disabled={disabled} />
				&nbsp;&nbsp;
			</>
		);
	};

	return (
		<div className="card">
			<Toast ref={toast_topleft} position="top-left" />
			<Toast ref={toast_topright} position="top-right" />
			<Messages ref={errorMessage} />
			<GenericDataTable
				endpoint={SEARCH_ENDPOINT}
				tableName="Experiments"
				entities={conditionRelations}
				setEntities={setConditionRelations}
				totalRecords={totalRecords}
				setTotalRecords={setTotalRecords}
				tableState={tableState}
				setTableState={setTableState}
				columns={columns}
				isEditable={true}
				curieFields={['singleReference']}
				mutation={mutation}
				isInEditMode={isInEditMode}
				setIsInEditMode={setIsInEditMode}
				toasts={{ toast_topleft, toast_topright }}
				errorObject={{ errorMessages, setErrorMessages }}
				headerButtons={headerButtons}
				deletionEnabled={true}
				deletionMethod={conditionRelationService.deleteConditionRelation}
				defaultColumnWidth={DEFAULT_COLUMN_WIDTH}
				fetching={isFetching || isLoading}
			/>
			<NewRelationForm
				newRelationState={newRelationState}
				newRelationDispatch={newRelationDispatch}
				searchService={searchService}
				conditionRelationService={conditionRelationService}
				conditionRelationTypeTerms={conditionRelationTypeTerms}
				setNewConditionRelation={(newConditionRelation, queryClient) =>
					setNewEntity(tableState, setConditionRelations, newConditionRelation, queryClient)
				}
			/>
		</div>
	);
};
