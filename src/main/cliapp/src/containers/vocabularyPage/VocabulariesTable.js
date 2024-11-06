import React, { useRef, useState } from 'react';
import { useMutation } from '@tanstack/react-query';
import { Toast } from 'primereact/toast';
import { Button } from 'primereact/button';
import { VocabularyService } from '../../service/VocabularyService';
import { useControlledVocabularyService } from '../../service/useControlledVocabularyService';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { NewVocabularyForm } from '../../containers/controlledVocabularyPage/NewVocabularyForm';
import { ErrorMessageComponent } from '../../components/Error/ErrorMessageComponent';
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_CONFIGS } from '../../constants/FilterFields';
import { InputTextEditor } from '../../components/InputTextEditor';
import { TrueFalseDropdown } from '../../components/TrueFalseDropDownSelector';
import { useGetTableData } from '../../service/useGetTableData';
import { useGetUserSettings } from '../../service/useGetUserSettings';
import { SearchService } from '../../service/SearchService';
import { setNewEntity } from '../../utils/utils';

import { StringTemplate } from '../../components/Templates/StringTemplate';
import { BooleanTemplate } from '../../components/Templates/BooleanTemplate';

export const VocabulariesTable = () => {
	const [isInEditMode, setIsInEditMode] = useState(false);
	const [totalRecords, setTotalRecords] = useState(0);
	const [errorMessages, setErrorMessages] = useState({});

	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);
	const errorMessagesRef = useRef();
	errorMessagesRef.current = errorMessages;

	const [vocabularies, setVocabularies] = useState([]);
	const [newVocabularyDialog, setNewVocabularyDialog] = useState(false);

	const searchService = new SearchService();

	const obsoleteTerms = useControlledVocabularyService('generic_boolean_terms');

	let vocabularyService = new VocabularyService();

	const mutation = useMutation((updatedVocabulary) => {
		if (!vocabularyService) {
			vocabularyService = new VocabularyService();
		}
		return vocabularyService.saveVocabulary(updatedVocabulary);
	});

	const stringEditor = (props, field) => {
		return (
			<>
				<InputTextEditor rowProps={props} fieldName={field} />
				<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField={field} />
			</>
		);
	};

	const obsoleteEditorTemplate = (props) => {
		return (
			<>
				<TrueFalseDropdown
					options={obsoleteTerms}
					editorChange={onObsoleteEditorValueChange}
					props={props}
					field={'obsolete'}
				/>
				<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField={'obsolete'} />
			</>
		);
	};

	const onObsoleteEditorValueChange = (props, event) => {
		let updatedTerms = [...props.props.value];
		if (event.value || event.value === '') {
			updatedTerms[props.rowIndex].obsolete = JSON.parse(event.value.name);
		}
	};

	const columns = [
		{
			field: 'name',
			header: 'Name',
			sortable: true,
			body: (rowData) => <StringTemplate string={rowData.name} />,
			filterConfig: FILTER_CONFIGS.nameFilterConfig,
			editor: (props) => stringEditor(props, 'name'),
		},
		{
			field: 'vocabularyDescription',
			header: 'Description',
			sortable: true,
			body: (rowData) => <StringTemplate string={rowData.vocabularyDescription} />,
			filterConfig: FILTER_CONFIGS.vocabularyDescriptionFilterConfig,
			editor: (props) => stringEditor(props, 'vocabularyDescription'),
		},
		{
			field: 'obsolete',
			header: 'Obsolete',
			sortable: true,
			body: (rowData) => <BooleanTemplate value={rowData.obsolete} />,
			filterConfig: FILTER_CONFIGS.obsoleteFilterConfig,
			editor: (props) => obsoleteEditorTemplate(props),
		},
		{
			field: 'vocabularyLabel',
			header: 'Label',
			sortable: true,
			body: (rowData) => <StringTemplate string={rowData.vocabularyLabel} />,
			filterConfig: FILTER_CONFIGS.vocabularyLabelFilterConfig,
		},
	];

	const DEFAULT_COLUMN_WIDTH = 20;
	const SEARCH_ENDPOINT = 'vocabulary';

	const initialTableState = getDefaultTableState('Vocabularies', columns, DEFAULT_COLUMN_WIDTH);

	const { settings: tableState, mutate: setTableState } = useGetUserSettings(
		initialTableState.tableSettingsKeyName,
		initialTableState
	);

	const { isLoading, isFetching } = useGetTableData({
		tableState,
		endpoint: SEARCH_ENDPOINT,
		setIsInEditMode,
		setEntities: setVocabularies,
		setTotalRecords,
		toast_topleft,
		searchService,
	});

	const handleOpenNewVocabulary = () => {
		setNewVocabularyDialog(true);
	};

	const headerButtons = (disabled = false) => {
		return (
			<>
				<Button label="New Vocabulary" icon="pi pi-plus" onClick={handleOpenNewVocabulary} disabled={disabled} />
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
				tableName="Vocabularies"
				entities={vocabularies}
				setEntities={setVocabularies}
				totalRecords={totalRecords}
				setTotalRecords={setTotalRecords}
				tableState={tableState}
				setTableState={setTableState}
				columns={columns}
				isEditable={true}
				mutation={mutation}
				isInEditMode={isInEditMode}
				setIsInEditMode={setIsInEditMode}
				headerButtons={headerButtons}
				toasts={{ toast_topleft, toast_topright }}
				errorObject={{ errorMessages, setErrorMessages }}
				deletionEnabled={true}
				deletionMethod={vocabularyService.deleteVocabulary}
				defaultColumnWidth={DEFAULT_COLUMN_WIDTH}
				fetching={isFetching || isLoading}
			/>
			<NewVocabularyForm
				newVocabularyDialog={newVocabularyDialog}
				setNewVocabularyDialog={setNewVocabularyDialog}
				setNewVocabulary={(newVocabulary, queryClient) =>
					setNewEntity(tableState, setVocabularies, newVocabulary, queryClient)
				}
			/>
		</div>
	);
};
