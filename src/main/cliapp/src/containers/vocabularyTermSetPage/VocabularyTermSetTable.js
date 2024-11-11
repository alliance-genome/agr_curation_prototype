import React, { useRef, useState } from 'react';
import { useMutation } from '@tanstack/react-query';
import { Toast } from 'primereact/toast';
import { SearchService } from '../../service/SearchService';
import { Messages } from 'primereact/messages';
import { ErrorMessageComponent } from '../../components/Error/ErrorMessageComponent';
import { Button } from 'primereact/button';
import { VocabularyTermSetService } from '../../service/VocabularyTermSetService';
import { VocabTermAutocompleteTemplate } from '../../components/Autocomplete/VocabTermAutocompleteTemplate';
import { NewVocabularyTermSetForm } from './NewVocabularyTermSetForm';
import { useNewVocabularyTermSetReducer } from './useNewVocabularyTermSetReducer';
import { InputTextEditor } from '../../components/InputTextEditor';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { AutocompleteEditor } from '../../components/Autocomplete/AutocompleteEditor';
import {
	autocompleteSearch,
	buildAutocompleteFilter,
	defaultAutocompleteOnChange,
	multipleAutocompleteOnChange,
	setNewEntity,
} from '../../utils/utils';
import { AutocompleteMultiEditor } from '../../components/Autocomplete/AutocompleteMultiEditor';
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_CONFIGS } from '../../constants/FilterFields';
import { useGetTableData } from '../../service/useGetTableData';
import { useGetUserSettings } from '../../service/useGetUserSettings';

import { StringTemplate } from '../../components/Templates/StringTemplate';
import { StringListTemplate } from '../../components/Templates/StringListTemplate';

export const VocabularyTermSetTable = () => {
	const [isInEditMode, setIsInEditMode] = useState(false);
	const [totalRecords, setTotalRecords] = useState(0);
	const { newVocabularyTermSetState, newVocabularyTermSetDispatch } = useNewVocabularyTermSetReducer();

	const [termSets, setTermSets] = useState();

	const searchService = new SearchService();
	const errorMessage = useRef(null);
	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);
	const [errorMessages, setErrorMessages] = useState({});
	const errorMessagesRef = useRef();
	errorMessagesRef.current = errorMessages;

	let vocabularyTermSetService = new VocabularyTermSetService();

	const mutation = useMutation((updatedVocabularyTermSet) => {
		if (!vocabularyTermSetService) {
			vocabularyTermSetService = new VocabularyTermSetService();
		}
		return vocabularyTermSetService.saveVocabularyTermSet(updatedVocabularyTermSet);
	});

	const handleNewVocabularyTermSetOpen = () => {
		newVocabularyTermSetDispatch({ type: 'OPEN_DIALOG' });
	};

	const onVocabularyChange = (event, setFieldValue, props) => {
		defaultAutocompleteOnChange(props, event, 'vocabularyTermSetVocabulary', setFieldValue, 'name');
	};
	const vocabularySearch = (event, setFiltered, setQuery) => {
		const autocompleteFields = ['name'];
		const endpoint = 'vocabulary';
		const filterName = 'vocabularyFilter';
		const filter = buildAutocompleteFilter(event, autocompleteFields);

		setQuery(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
	};

	const vocabularyEditorTemplate = (props) => {
		return (
			<>
				<AutocompleteEditor
					search={vocabularySearch}
					initialValue={props.rowData.vocabularyTermSetVocabulary?.name}
					rowProps={props}
					fieldName="vocabularyTermSetVocabulary"
					subField={'name'}
					valueDisplay={(item, setAutocompleteSelectedItem, op, query) => (
						<VocabTermAutocompleteTemplate
							item={item}
							setAutocompleteSelectedItem={setAutocompleteSelectedItem}
							op={op}
							query={query}
						/>
					)}
					onValueChangeHandler={onVocabularyChange}
				/>
				<ErrorMessageComponent
					errorMessages={errorMessagesRef.current[props.rowIndex]}
					errorField={'vocabularyTermSetVocabulary'}
				/>
			</>
		);
	};

	const onMemberTermsChange = (event, setFieldValue, props) => {
		multipleAutocompleteOnChange(props, event, 'memberTerms', setFieldValue);
	};

	const memberTermSearch = (event, setFiltered, setInputValue, props) => {
		const autocompleteFields = ['name'];
		const endpoint = 'vocabularyterm';
		const filterName = 'memberTermsFilter';
		const filter = buildAutocompleteFilter(event, autocompleteFields);
		const otherFilters = {
			vocabularyFilter: {
				'vocabulary.name': {
					queryString: props.props.value[props.rowIndex].vocabularyTermSetVocabulary.name,
				},
			},
		};

		setInputValue(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered, otherFilters);
	};

	const memberTermsEditorTemplate = (props) => {
		return (
			<>
				<AutocompleteMultiEditor
					name="memberTerms"
					fieldName="memberTerms"
					subField="name"
					initialValue={props.rowData.memberTerms}
					rowProps={props}
					search={memberTermSearch}
					onValueChangeHandler={onMemberTermsChange}
					valueDisplay={(item, setAutocompleteSelectedItem, op, query) => (
						<VocabTermAutocompleteTemplate
							item={item}
							setAutocompleteSelectedItem={setAutocompleteSelectedItem}
							op={op}
							query={query}
						/>
					)}
				/>
				<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField="memberTerms" />
			</>
		);
	};

	const nameEditor = (props) => {
		return (
			<>
				<InputTextEditor rowProps={props} fieldName={'name'} />
				<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField={'name'} />
			</>
		);
	};

	const descriptionEditor = (props) => {
		return (
			<>
				<InputTextEditor rowProps={props} fieldName={'vocabularyTermSetDescription'} />
				<ErrorMessageComponent
					errorMessages={errorMessagesRef.current[props.rowIndex]}
					errorField={'vocabularyTermSetDescription'}
				/>
			</>
		);
	};

	const columns = [
		{
			field: 'name',
			header: 'Name',
			body: (rowData) => <StringTemplate string={rowData.name} />,
			sortable: true,
			filterConfig: FILTER_CONFIGS.nameFilterConfig,
			editor: (props) => nameEditor(props),
		},
		{
			field: 'vocabularyTermSetVocabulary.name',
			header: 'Vocabulary',
			sortable: true,
			body: (rowData) => <StringTemplate string={rowData.vocabularyTermSetVocabulary?.name} />,
			filterConfig: FILTER_CONFIGS.vocabularyFieldSetFilterConfig,
			editor: (props) => vocabularyEditorTemplate(props),
		},
		{
			field: 'memberTerms.name',
			header: 'Member Terms',
			sortable: true,
			body: (rowData) => <StringListTemplate list={rowData.memberTerms?.map((memberTerm) => memberTerm?.name)} />,
			filterConfig: FILTER_CONFIGS.vocabularyMemberTermsFilterConfig,
			editor: (props) => memberTermsEditorTemplate(props),
		},
		{
			field: 'vocabularyTermSetDescription',
			header: 'Description',
			body: (rowData) => <StringTemplate string={rowData.vocabularyTermSetDescription} />,
			sortable: true,
			filterConfig: FILTER_CONFIGS.vocabularyTermSetDescriptionFilterConfig,
			editor: (props) => descriptionEditor(props),
		},
		{
			field: 'vocabularyLabel',
			header: 'Label',
			body: (rowData) => <StringTemplate string={rowData.vocabularyLabel} />,
			filterConfig: FILTER_CONFIGS.vocabularyLabelFilterConfig,
		},
	];

	const DEFAULT_COLUMN_WIDTH = 15;
	const SEARCH_ENDPOINT = 'vocabularytermset';

	const initialTableState = getDefaultTableState('VocabularyTermSets', columns, DEFAULT_COLUMN_WIDTH);

	const { settings: tableState, mutate: setTableState } = useGetUserSettings(
		initialTableState.tableSettingsKeyName,
		initialTableState
	);

	const { isLoading, isFetching } = useGetTableData({
		tableState,
		endpoint: SEARCH_ENDPOINT,
		setIsInEditMode,
		setEntities: setTermSets,
		setTotalRecords,
		toast_topleft,
		searchService,
	});

	const headerButtons = (disabled = false) => {
		return (
			<>
				<Button
					label="New Vocabulary Term Set"
					icon="pi pi-plus"
					onClick={handleNewVocabularyTermSetOpen}
					disabled={disabled}
				/>
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
				tableName="Vocabulary Term Sets"
				entities={termSets}
				setEntities={setTermSets}
				totalRecords={totalRecords}
				setTotalRecords={setTotalRecords}
				tableState={tableState}
				setTableState={setTableState}
				columns={columns}
				isEditable={true}
				idFields={['vocabularyTermSetVocabulary, memberTerms']}
				mutation={mutation}
				isInEditMode={isInEditMode}
				setIsInEditMode={setIsInEditMode}
				toasts={{ toast_topleft, toast_topright }}
				errorObject={{ errorMessages, setErrorMessages }}
				headerButtons={headerButtons}
				deletionEnabled={true}
				deletionMethod={vocabularyTermSetService.deleteVocabularyTermSet}
				defaultColumnWidth={DEFAULT_COLUMN_WIDTH}
				fetching={isFetching || isLoading}
			/>
			<NewVocabularyTermSetForm
				newVocabularyTermSetState={newVocabularyTermSetState}
				newVocabularyTermSetDispatch={newVocabularyTermSetDispatch}
				searchService={searchService}
				vocabularyTermSetService={vocabularyTermSetService}
				setNewVocabularyTermSet={(newTermSet, queryClient) =>
					setNewEntity(tableState, setTermSets, newTermSet, queryClient)
				}
			/>
		</div>
	);
};
