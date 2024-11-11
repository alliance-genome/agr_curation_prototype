import React, { useRef, useState } from 'react';
import { useMutation } from '@tanstack/react-query';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { ErrorMessageComponent } from '../../components/Error/ErrorMessageComponent';
import { VariantService } from '../../service/VariantService';
import { RelatedNotesDialog } from '../../components/RelatedNotesDialog';
import { TaxonTableEditor } from '../../components/Editors/taxon/TaxonTableEditor';
import { VariantTypeTableEditor } from '../../components/Editors/variantType/VariantTypeTableEditor';
import { SourceGeneralConsequenceTableEditor } from '../../components/Editors/sourceGeneralConsequence/SourceGeneralConsequenceTableEditor';
import { BooleanTableEditor } from '../../components/Editors/boolean/BooleanTableEditor';

import { Toast } from 'primereact/toast';
import { Button } from 'primereact/button';
import { EditMessageTooltip } from '../../components/EditMessageTooltip';
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_CONFIGS } from '../../constants/FilterFields';
import { useControlledVocabularyService } from '../../service/useControlledVocabularyService';
import { ControlledVocabularyDropdown } from '../../components/ControlledVocabularySelector';

import { ObjectListTemplate } from '../../components/Templates/ObjectListTemplate';
import { StringTemplate } from '../../components/Templates/StringTemplate';
import { OntologyTermTemplate } from '../../components/Templates/OntologyTermTemplate';
import { CountDialogTemplate } from '../../components/Templates/dialog/CountDialogTemplate';
import { BooleanTemplate } from '../../components/Templates/BooleanTemplate';
import { IdTemplate } from '../../components/Templates/IdTemplate';

import { useGetTableData } from '../../service/useGetTableData';
import { useGetUserSettings } from '../../service/useGetUserSettings';
import { crossReferencesSort } from '../../components/Templates/utils/sortMethods';

import { SearchService } from '../../service/SearchService';

export const VariantsTable = () => {
	const [isInEditMode, setIsInEditMode] = useState(false);
	const [errorMessages, setErrorMessages] = useState({});
	const errorMessagesRef = useRef();
	errorMessagesRef.current = errorMessages;

	const [totalRecords, setTotalRecords] = useState(0);
	const [variants, setVariants] = useState([]);

	const searchService = new SearchService();

	const [relatedNotesData, setRelatedNotesData] = useState({
		relatedNotes: [],
		isInEdit: false,
		dialog: false,
		rowIndex: null,
		mainRowProps: {},
	});

	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);

	let variantService = new VariantService();

	const mutation = useMutation((updatedVariant) => {
		if (!variantService) {
			variantService = new VariantService();
		}
		return variantService.saveVariant(updatedVariant);
	});

	const variantStatusTerms = useControlledVocabularyService('variant_status');

	const onVariantStatusEditorValueChange = (props, event) => {
		let updatedVariants = [...props.props.value];
		updatedVariants[props.rowIndex].variantStatus = event.value;
	};

	const variantStatusEditor = (props) => {
		return (
			<>
				<ControlledVocabularyDropdown
					field="variantStatus"
					options={variantStatusTerms}
					editorChange={onVariantStatusEditorValueChange}
					props={props}
					showClear={true}
				/>
				<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField={'geneticSex'} />
			</>
		);
	};

	const handleRelatedNotesOpen = (relatedNotes) => {
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
							<EditMessageTooltip object="variant" />
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
			sortable: { isInEditMode },
			body: (rowData) => <IdTemplate id={rowData.curie} />,
			filterConfig: FILTER_CONFIGS.curieFilterConfig,
		},
		{
			field: 'modEntityId',
			header: 'MOD Entity ID',
			sortable: true,
			body: (rowData) => <IdTemplate id={rowData.modEntityId} />,
			filterConfig: FILTER_CONFIGS.modentityidFilterConfig,
		},
		{
			field: 'modInternalId',
			header: 'MOD Internal ID',
			sortable: true,
			body: (rowData) => <IdTemplate id={rowData.modInternalId} />,
			filterConfig: FILTER_CONFIGS.modinternalidFilterConfig,
		},
		{
			field: 'taxon.name',
			header: 'Taxon',
			sortable: true,
			body: (rowData) => <OntologyTermTemplate term={rowData.taxon} />,
			filterConfig: FILTER_CONFIGS.taxonFilterConfig,
			editor: (props) => <TaxonTableEditor rowProps={props} errorMessagesRef={errorMessagesRef} />,
		},
		{
			field: 'variantType.name',
			header: 'Variant Type',
			sortable: true,
			body: (rowData) => <OntologyTermTemplate term={rowData.variantType} />,
			filterConfig: FILTER_CONFIGS.variantTypeFilterConfig,
			editor: (props) => <VariantTypeTableEditor rowProps={props} errorMessagesRef={errorMessagesRef} />,
		},
		{
			field: 'variantStatus.name',
			header: 'Variant Status',
			sortable: true,
			body: (rowData) => <StringTemplate string={rowData.variantStatus?.name} />,
			filterConfig: FILTER_CONFIGS.variantStatusFilterConfig,
			editor: (props) => variantStatusEditor(props),
		},
		{
			field: 'relatedNotes.freeText',
			header: 'Related Notes',
			sortable: true,
			body: (rowData) => (
				<CountDialogTemplate entities={rowData.relatedNotes} handleOpen={handleRelatedNotesOpen} text={'Notes'} />
			),
			filterConfig: FILTER_CONFIGS.relatedNotesFilterConfig,
			editor: relatedNotesEditor,
		},
		{
			field: 'sourceGeneralConsequence.name',
			header: 'Source General Consequence',
			sortable: true,
			body: (rowData) => <OntologyTermTemplate term={rowData.sourceGeneralConsequence} />,
			filterConfig: FILTER_CONFIGS.sourceGeneralConsequenceFilterConfig,
			editor: (props) => <SourceGeneralConsequenceTableEditor rowProps={props} errorMessagesRef={errorMessagesRef} />,
		},
		{
			field: 'dataProvider.sourceOrganization.abbreviation',
			header: 'Data Provider',
			sortable: true,
			body: (rowData) => <StringTemplate string={rowData.dataProvider?.sourceOrganization?.abbreviation} />,
			filterConfig: FILTER_CONFIGS.variantDataProviderFilterConfig,
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
			filter: true,
			body: (rowData) => <BooleanTemplate value={rowData.internal} />,
			filterConfig: FILTER_CONFIGS.internalFilterConfig,
			sortable: true,
			editor: (props) => (
				<BooleanTableEditor rowProps={props} errorMessagesRef={errorMessagesRef} field={'internal'} showClear={false} />
			),
		},
		{
			field: 'obsolete',
			header: 'Obsolete',
			filter: true,
			body: (rowData) => <BooleanTemplate value={rowData.obsolete} />,
			filterConfig: FILTER_CONFIGS.obsoleteFilterConfig,
			sortable: true,
			editor: (props) => (
				<BooleanTableEditor rowProps={props} errorMessagesRef={errorMessagesRef} field={'obsolete'} showClear={false} />
			),
		},
	];

	const DEFAULT_COLUMN_WIDTH = 10;
	const SEARCH_ENDPOINT = 'variant';

	const initialTableState = getDefaultTableState('Variants', columns, DEFAULT_COLUMN_WIDTH);

	const { settings: tableState, mutate: setTableState } = useGetUserSettings(
		initialTableState.tableSettingsKeyName,
		initialTableState
	);

	const { isFetching, isLoading } = useGetTableData({
		tableState,
		endpoint: SEARCH_ENDPOINT,
		setIsInEditMode,
		setEntities: setVariants,
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
					tableName="Variants"
					entities={variants}
					setEntities={setVariants}
					totalRecords={totalRecords}
					setTotalRecords={setTotalRecords}
					tableState={tableState}
					setTableState={setTableState}
					columns={columns}
					isEditable={true}
					hasDetails={false}
					mutation={mutation}
					isInEditMode={isInEditMode}
					setIsInEditMode={setIsInEditMode}
					toasts={{ toast_topleft, toast_topright }}
					errorObject={{ errorMessages, setErrorMessages }}
					defaultColumnWidth={DEFAULT_COLUMN_WIDTH}
					fetching={isFetching || isLoading}
				/>
			</div>
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
