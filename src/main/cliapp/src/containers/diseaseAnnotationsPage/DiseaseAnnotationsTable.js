import React, { useRef, useState } from 'react';
import { useMutation } from '@tanstack/react-query';
import { Toast } from 'primereact/toast';

import { AutocompleteEditor } from '../../components/Autocomplete/AutocompleteEditor';
import { SubjectAutocompleteTemplate } from '../../components/Autocomplete/SubjectAutocompleteTemplate';
import { EvidenceAutocompleteTemplate } from '../../components/Autocomplete/EvidenceAutocompleteTemplate';
import { LiteratureAutocompleteTemplate } from '../../components/Autocomplete/LiteratureAutocompleteTemplate';
import { EditMessageTooltip } from '../../components/EditMessageTooltip';
import { EllipsisTableCell } from '../../components/EllipsisTableCell';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { SearchService } from '../../service/SearchService';
import { DiseaseAnnotationService } from '../../service/DiseaseAnnotationService';
import { RelatedNotesDialog } from '../../components/RelatedNotesDialog';
import { ConditionRelationsDialog } from '../../components/ConditionRelationsDialog';

import { SingleReferenceTemplate } from '../../components/Templates/reference/SingleReferenceTemplate';
import { ObjectListTemplate } from '../../components/Templates/ObjectListTemplate';
import { IdTemplate } from '../../components/Templates/IdTemplate';
import { OntologyTermTemplate } from '../../components/Templates/OntologyTermTemplate';
import { GenomicEntityTemplate } from '../../components/Templates/genomicEntity/GenomicEntityTemplate';
import { GenomicEntityListTemplate } from '../../components/Templates/genomicEntity/GenomicEntityListTemplate';
import { BooleanTemplate } from '../../components/Templates/BooleanTemplate';
import { NotTemplate } from '../../components/Templates/NotTemplate';
import { CountDialogTemplate } from '../../components/Templates/dialog/CountDialogTemplate';
import { TextDialogTemplate } from '../../components/Templates/dialog/TextDialogTemplate';
import { StringTemplate } from '../../components/Templates/StringTemplate';

import { NotEditor } from '../../components/Editors/NotEditor';

import { ControlledVocabularyDropdown } from '../../components/ControlledVocabularySelector';
import { ConditionRelationHandleDropdown } from '../../components/ConditionRelationHandleSelector';
import { ControlledVocabularyMultiSelectDropdown } from '../../components/ControlledVocabularyMultiSelector';
import { useControlledVocabularyService } from '../../service/useControlledVocabularyService';
import { ErrorMessageComponent } from '../../components/Error/ErrorMessageComponent';
import { TrueFalseDropdown } from '../../components/TrueFalseDropDownSelector';
import { Button } from 'primereact/button';
import {
	getRefString,
	autocompleteSearch,
	buildAutocompleteFilter,
	defaultAutocompleteOnChange,
	multipleAutocompleteOnChange,
	getIdentifier,
	setNewEntity,
} from '../../utils/utils';
import { diseaseQualifiersSort, evidenceCodesSort } from '../../components/Templates/utils/sortMethods';
import { useNewAnnotationReducer } from './useNewAnnotationReducer';
import { NewAnnotationForm } from './NewAnnotationForm';
import { AutocompleteMultiEditor } from '../../components/Autocomplete/AutocompleteMultiEditor';
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_CONFIGS } from '../../constants/FilterFields';
import { useGetTableData } from '../../service/useGetTableData';
import { useGetUserSettings } from '../../service/useGetUserSettings';

export const DiseaseAnnotationsTable = () => {
	const [isInEditMode, setIsInEditMode] = useState(false); //needs better name
	const [totalRecords, setTotalRecords] = useState(0);
	const [conditionRelationsData, setConditionRelationsData] = useState({
		conditionRelations: [],
		isInEdit: false,
		dialog: false,
		rowIndex: null,
		mailRowProps: {},
	});
	const [relatedNotesData, setRelatedNotesData] = useState({
		relatedNotes: [],
		isInEdit: false,
		dialog: false,
		rowIndex: null,
		mainRowProps: {},
	});
	const { newAnnotationState, newAnnotationDispatch } = useNewAnnotationReducer();

	const relationsTerms = useControlledVocabularyService('disease_relation');
	const geneticSexTerms = useControlledVocabularyService('genetic_sex');
	const annotationTypeTerms = useControlledVocabularyService('annotation_type');
	const booleanTerms = useControlledVocabularyService('generic_boolean_terms');
	const geneticModifierRelationTerms = useControlledVocabularyService('disease_genetic_modifier_relation');
	const diseaseQualifiersTerms = useControlledVocabularyService('disease_qualifier');

	const [errorMessages, setErrorMessages] = useState({});
	const errorMessagesRef = useRef();
	errorMessagesRef.current = errorMessages;

	const [uiErrorMessages, setUiErrorMessages] = useState([]);
	const uiErrorMessagesRef = useRef();
	uiErrorMessagesRef.current = uiErrorMessages;

	const searchService = new SearchService();

	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);

	const [diseaseAnnotations, setDiseaseAnnotations] = useState([]);

	let diseaseAnnotationService = new DiseaseAnnotationService();

	const sortMapping = {
		'diseaseAnnotationObject.name': ['diseaseAnnotationObject.curie', 'diseaseAnnotationObject.namespace'],
		'diseaseAnnotationSubject.symbol': ['diseaseAnnotationSubject.name', 'diseaseAnnotationSubject.modEntityId'],
		'with.geneSymbol.displayText': ['with.geneFullName.displayText', 'with.modEntityId'],
		'sgdStrainBackground.name': ['sgdStrainBackground.modEntityId'],
		'diseaseGeneticModifier.symbol': ['diseaseGeneticModifier.name', 'diseaseGeneticModifier.modEntityId'],
	};

	const mutation = useMutation((updatedAnnotation) => {
		return diseaseAnnotationService.saveDiseaseAnnotation(updatedAnnotation);
	});

	const handleNewAnnotationOpen = () => {
		newAnnotationDispatch({ type: 'OPEN_DIALOG' });
	};

	const handleDuplication = (rowData) => {
		newAnnotationDispatch({ type: 'DUPLICATE_ROW', rowData });
		newAnnotationDispatch({ type: 'SET_IS_ENABLED', value: true });
		if (rowData.type === 'AGMDiseaseAnnotation') {
			newAnnotationDispatch({ type: 'SET_IS_ASSERTED_GENE_ENABLED', value: true });
			newAnnotationDispatch({ type: 'SET_IS_ASSERTED_ALLELE_ENABLED', value: true });
		}

		if (rowData.type === 'AlleleDiseaseAnnotation') {
			newAnnotationDispatch({ type: 'SET_IS_ASSERTED_GENE_ENABLED', value: true });
		}

		if (rowData.relatedNotes && rowData.relatedNotes.length > 0) {
			newAnnotationDispatch({ type: 'SET_RELATED_NOTES_EDITING_ROWS', relatedNotes: rowData.relatedNotes });
		}

		if (rowData.conditionRelations && rowData.conditionRelations.length > 0) {
			newAnnotationDispatch({
				type: 'SET_CONDITION_RELATIONS_EDITING_ROWS',
				conditionRelations: rowData.conditionRelations,
			});
		}

		handleNewAnnotationOpen();
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

	const handleConditionRelationsOpen = (conditionRelations) => {
		let _conditionRelationsData = {};
		_conditionRelationsData['originalConditionRelations'] = conditionRelations;
		_conditionRelationsData['dialog'] = true;
		_conditionRelationsData['isInEdit'] = false;
		setConditionRelationsData(() => ({
			..._conditionRelationsData,
		}));
	};

	const handleConditionRelationsOpenInEdit = (event, rowProps, isInEdit) => {
		const { rows } = rowProps.props;
		const { rowIndex } = rowProps;
		const index = rowIndex % rows;
		let _conditionRelationsData = {};
		_conditionRelationsData['originalConditionRelations'] = rowProps.rowData.conditionRelations;
		_conditionRelationsData['dialog'] = true;
		_conditionRelationsData['isInEdit'] = isInEdit;
		_conditionRelationsData['rowIndex'] = index;
		_conditionRelationsData['mainRowProps'] = rowProps;
		setConditionRelationsData(() => ({
			..._conditionRelationsData,
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

	const conditionRelationsEditor = (props) => {
		if (props.rowData?.conditionRelations) {
			const handle = props.rowData.conditionRelations[0]?.handle;

			if (handle) return null;

			return (
				<>
					<div>
						<Button
							className="p-button-text"
							onClick={(event) => {
								handleConditionRelationsOpenInEdit(event, props, true);
							}}
						>
							<span style={{ textDecoration: 'underline' }}>
								{!handle && `Conditions (${props.rowData.conditionRelations.length})`}
								{handle && handle}
								<i className="pi pi-user-edit" style={{ fontSize: '1em' }}></i>
							</span>
							&nbsp;&nbsp;&nbsp;&nbsp;
							<EditMessageTooltip />
						</Button>
					</div>
					<ErrorMessageComponent
						errorMessages={errorMessagesRef.current[props.rowIndex]}
						errorField={'conditionRelations'}
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
								handleConditionRelationsOpenInEdit(event, props, true);
							}}
						>
							<span style={{ textDecoration: 'underline' }}>
								Add Condition
								<i className="pi pi-user-edit" style={{ fontSize: '1em' }}></i>
							</span>
							&nbsp;&nbsp;&nbsp;&nbsp;
							<EditMessageTooltip />
						</Button>
					</div>
				</>
			);
		}
	};

	const conditionRelationHandleEditor = (props) => {
		if (props.rowData?.conditionRelations && props.rowData.conditionRelations[0]?.handle) {
			return (
				<>
					<ConditionRelationHandleDropdown
						field="conditionRelationHandle"
						editorChange={onConditionRelationHandleEditorValueChange}
						props={props}
						showClear={false}
						placeholderText={props.rowData.conditionRelations[0].handle}
					/>
					<ErrorMessageComponent
						errorMessages={errorMessagesRef.current[props.rowIndex]}
						errorField={'conditionRelationHandle'}
					/>
				</>
			);
		}
	};

	const onConditionRelationHandleEditorValueChange = (props, event) => {
		let updatedAnnotations = [...props.props.value];
		if (typeof event.value === 'object') {
			updatedAnnotations[props.rowIndex].conditionRelations[0] = event.value;
		} else {
			updatedAnnotations[props.rowIndex].conditionRelations[0].handle = event.value;
		}
	};

	const onRelationEditorValueChange = (props, event) => {
		let updatedAnnotations = [...props.props.value];
		if (event.value || event.value === '') {
			updatedAnnotations[props.rowIndex].relation = event.value;
		}
	};

	const relationEditor = (props) => {
		return (
			<>
				<ControlledVocabularyDropdown
					field="relation"
					options={relationsTerms}
					editorChange={onRelationEditorValueChange}
					props={props}
					showClear={false}
					placeholderText={props.rowData.relation.name}
				/>
				<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField={'relation'} />
			</>
		);
	};

	const onGeneticSexEditorValueChange = (props, event) => {
		let updatedAnnotations = [...props.props.value];
		updatedAnnotations[props.rowIndex].geneticSex = event.value;
	};

	const geneticSexEditor = (props) => {
		return (
			<>
				<ControlledVocabularyDropdown
					field="geneticSex"
					options={geneticSexTerms}
					editorChange={onGeneticSexEditorValueChange}
					props={props}
					showClear={true}
				/>
				<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField={'geneticSex'} />
			</>
		);
	};

	const onAnnotationTypeEditorValueChange = (props, event) => {
		let updatedAnnotations = [...props.props.value];
		updatedAnnotations[props.rowIndex].annotationType = event.value;
	};

	const annotationTypeEditor = (props) => {
		return (
			<>
				<ControlledVocabularyDropdown
					field="annotationType"
					options={annotationTypeTerms}
					editorChange={onAnnotationTypeEditorValueChange}
					props={props}
					showClear={true}
				/>
				<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField={'annotationType'} />
			</>
		);
	};

	const onGeneticModifierRelationEditorValueChange = (props, event) => {
		let updatedAnnotations = [...props.props.value];
		updatedAnnotations[props.rowIndex].diseaseGeneticModifierRelation = event.value;
	};

	const geneticModifierRelationEditor = (props) => {
		return (
			<>
				<ControlledVocabularyDropdown
					field="diseaseGeneticModifierRelation"
					options={geneticModifierRelationTerms}
					editorChange={onGeneticModifierRelationEditorValueChange}
					props={props}
					showClear={true}
				/>
				<ErrorMessageComponent
					errorMessages={errorMessagesRef.current[props.rowIndex]}
					errorField={'diseaseGeneticModifierRelation'}
				/>
			</>
		);
	};

	const onDiseaseQualifiersEditorValueChange = (props, event) => {
		let updatedAnnotations = [...props.props.value];
		if (event.value || event.value === '') {
			updatedAnnotations[props.rowIndex].diseaseQualifiers = event.value;
		}
	};

	const diseaseQualifiersEditor = (props) => {
		let placeholderText = '';
		if (props.rowData.diseaseQualifiers) {
			let placeholderTextElements = [];
			props.rowData.diseaseQualifiers.forEach((x, i) => placeholderTextElements.push(x.name));
			placeholderText = placeholderTextElements.join();
		}
		return (
			<>
				<ControlledVocabularyMultiSelectDropdown
					options={diseaseQualifiersTerms}
					editorChange={onDiseaseQualifiersEditorValueChange}
					props={props}
					placeholderText={placeholderText}
				/>
				<ErrorMessageComponent
					errorMessages={errorMessagesRef.current[props.rowIndex]}
					errorField={'diseaseQualifiers'}
				/>
			</>
		);
	};

	const onNegatedEditorValueChange = (event, props) => {
		if (event.target.value === undefined || event.target.value === null) return;

		props.editorCallback(event.target.value);

		setDiseaseAnnotations((prevDiseaseAnnotations) => {
			prevDiseaseAnnotations[props.rowIndex].negated = event.target.value;
			return prevDiseaseAnnotations;
		});
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

	const onObsoleteEditorValueChange = (props, event) => {
		let updatedAnnotations = [...props.props.value];
		if (event.value || event.value === '') {
			updatedAnnotations[props.rowIndex].obsolete = JSON.parse(event.value.name);
		}
	};

	const obsoleteEditor = (props) => {
		return (
			<>
				<TrueFalseDropdown
					options={booleanTerms}
					editorChange={onObsoleteEditorValueChange}
					props={props}
					field={'obsolete'}
				/>
				<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField={'obsolete'} />
			</>
		);
	};

	const onSubjectValueChange = (event, setFieldValue, props) => {
		defaultAutocompleteOnChange(props, event, 'diseaseAnnotationSubject', setFieldValue, 'modEntityId');
	};

	const subjectSearch = (event, setFiltered, setQuery, props) => {
		const autocompleteFields = getSubjectAutocompleteFields(props);
		const endpoint = getSubjectEndpoint(props);
		const filterName = 'diseaseAnnotationSubjectFilter';
		const filter = buildAutocompleteFilter(event, autocompleteFields);
		setQuery(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
	};

	const subjectEditorTemplate = (props) => {
		return (
			<>
				<AutocompleteEditor
					initialValue={getIdentifier(props.rowData.diseaseAnnotationSubject)}
					search={subjectSearch}
					rowProps={props}
					searchService={searchService}
					subField="modEntityId"
					fieldName="diseaseAnnotationSubject"
					valueDisplay={(item, setAutocompleteHoverItem, op, query) => (
						<SubjectAutocompleteTemplate
							item={item}
							setAutocompleteHoverItem={setAutocompleteHoverItem}
							op={op}
							query={query}
						/>
					)}
					onValueChangeHandler={onSubjectValueChange}
				/>
				<ErrorMessageComponent
					errorMessages={errorMessagesRef.current[props.rowIndex]}
					errorField={'diseaseAnnotationSubject'}
				/>
				<ErrorMessageComponent
					errorMessages={uiErrorMessagesRef.current[props.rowIndex]}
					errorField={'diseaseAnnotationSubject'}
				/>
			</>
		);
	};

	const getSubjectEndpoint = (props) => {
		if (props.rowData?.type === 'GeneDiseaseAnnotation') return 'gene';
		if (props.rowData?.type === 'AlleleDiseaseAnnotation') return 'allele';
		if (props.rowData?.type === 'AGMDiseaseAnnotation') return 'agm';
		return 'biologicalentity';
	};

	const getSubjectAutocompleteFields = (props) => {
		let subjectFields = ['curie', 'modEntityId', 'modInternalId', 'crossReferences.referencedCurie'];
		if (props.rowData.type === 'AGMDiseaseAnnotation') {
			subjectFields.push('name');
		} else if (props.rowData.type === 'AlleleDiseaseAnnotation') {
			subjectFields.push(
				'alleleFullName.formatText',
				'alleleFullName.displayText',
				'alleleSymbol.formatText',
				'alleleSymbol.displayText',
				'alleleSynonyms.formatText',
				'alleleSynonyms.displayText',
				'alleleSecondaryIds.secondaryId'
			);
		} else if (props.rowData.type === 'GeneDiseaseAnnotation') {
			subjectFields.push(
				'geneFullName.formatText',
				'geneFullName.displayText',
				'geneSymbol.formatText',
				'geneSymbol.displayText',
				'geneSynonyms.formatText',
				'geneSynonyms.displayText',
				'geneSystematicName.formatText',
				'geneSystematicName.displayText',
				'geneSecondaryIds.secondaryId'
			);
		}
		return subjectFields;
	};
	const onSgdStrainBackgroundValueChange = (event, setFieldValue, props) => {
		defaultAutocompleteOnChange(props, event, 'sgdStrainBackground', setFieldValue, 'modEntityId');
	};

	const sgdStrainBackgroundSearch = (event, setFiltered, setQuery) => {
		const autocompleteFields = ['name', 'curie', 'modEntityId', 'modInternalId', 'crossReferences.referencedCurie'];
		const endpoint = 'agm';
		const filterName = 'sgdStrainBackgroundFilter';
		const filter = buildAutocompleteFilter(event, autocompleteFields);
		const otherFilters = {
			taxonFilter: {
				'taxon.name': {
					queryString: 'Saccharomyces cerevisiae',
				},
			},
		};
		setQuery(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered, otherFilters);
	};

	const sgdStrainBackgroundEditorTemplate = (props) => {
		return (
			<>
				<AutocompleteEditor
					rowProps={props}
					initialValue={getIdentifier(props.rowData.sgdStrainBackground)}
					search={sgdStrainBackgroundSearch}
					searchService={searchService}
					fieldName="sgdStrainBackground"
					subField="modEntityId"
					valueDisplay={(item, setAutocompleteHoverItem, op, query) => (
						<SubjectAutocompleteTemplate
							item={item}
							setAutocompleteHoverItem={setAutocompleteHoverItem}
							op={op}
							query={query}
						/>
					)}
					onValueChangeHandler={onSgdStrainBackgroundValueChange}
				/>
				<ErrorMessageComponent
					errorMessages={errorMessagesRef.current[props.rowIndex]}
					errorField={'sgdStrainBackground'}
				/>
				<ErrorMessageComponent
					errorMessages={uiErrorMessagesRef.current[props.rowIndex]}
					errorField={'sgdStrainBackground'}
				/>
			</>
		);
	};

	const onGeneticModifierAgmsValueChange = (event, setFieldValue, props) => {
		multipleAutocompleteOnChange(props, event, 'diseaseGeneticModifierAgms', setFieldValue);
	};

	const geneticModifierAgmsSearch = (event, setFiltered, setInputValue) => {
		const autocompleteFields = ['name', 'curie', 'modEntityId', 'modInternalId', 'crossReferences.referencedCurie'];
		const endpoint = 'agm';
		const filterName = 'geneticModifierAgmsFilter';
		const filter = buildAutocompleteFilter(event, autocompleteFields);
		setInputValue(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
	};

	const geneticModifierAgmsEditorTemplate = (props) => {
		return (
			<>
				<AutocompleteMultiEditor
					search={geneticModifierAgmsSearch}
					initialValue={props.rowData.diseaseGeneticModifierAgms}
					rowProps={props}
					fieldName="diseaseGeneticModifierAgms"
					subField="modEntityId"
					valueDisplay={(item, setAutocompleteHoverItem, op, query) => (
						<SubjectAutocompleteTemplate
							item={item}
							setAutocompleteHoverItem={setAutocompleteHoverItem}
							op={op}
							query={query}
						/>
					)}
					onValueChangeHandler={onGeneticModifierAgmsValueChange}
				/>
				<ErrorMessageComponent
					errorMessages={errorMessagesRef.current[props.rowIndex]}
					errorField={'diseaseGeneticModifierAgms'}
				/>
				<ErrorMessageComponent
					errorMessages={uiErrorMessagesRef.current[props.rowIndex]}
					errorField={'diseaseGeneticModifierAgms'}
				/>
			</>
		);
	};

	const onGeneticModifierAllelesValueChange = (event, setFieldValue, props) => {
		multipleAutocompleteOnChange(props, event, 'diseaseGeneticModifierAlleles', setFieldValue);
	};

	const geneticModifierAllelesSearch = (event, setFiltered, setInputValue) => {
		const autocompleteFields = [
			'alleleSymbol.formatText',
			'alleleFullName.formatText',
			'alleleFullName.displayText',
			'alleleSynonyms.formatText',
			'alleleSynonyms.displayText',
			'curie',
			'modEntityId',
			'modInternalId',
			'crossReferences.referencedCurie',
			'alleleSecondaryIds.secondaryId',
		];
		const endpoint = 'allele';
		const filterName = 'geneticModifierAllelesFilter';
		const filter = buildAutocompleteFilter(event, autocompleteFields);
		setInputValue(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
	};

	const geneticModifierAllelesEditorTemplate = (props) => {
		return (
			<>
				<AutocompleteMultiEditor
					search={geneticModifierAllelesSearch}
					initialValue={props.rowData.diseaseGeneticModifierAlleles}
					rowProps={props}
					fieldName="diseaseGeneticModifierAlleles"
					subField="modEntityId"
					valueDisplay={(item, setAutocompleteHoverItem, op, query) => (
						<SubjectAutocompleteTemplate
							item={item}
							setAutocompleteHoverItem={setAutocompleteHoverItem}
							op={op}
							query={query}
						/>
					)}
					onValueChangeHandler={onGeneticModifierAllelesValueChange}
				/>
				<ErrorMessageComponent
					errorMessages={errorMessagesRef.current[props.rowIndex]}
					errorField={'diseaseGeneticModifierAlleles'}
				/>
				<ErrorMessageComponent
					errorMessages={uiErrorMessagesRef.current[props.rowIndex]}
					errorField={'diseaseGeneticModifierAlleles'}
				/>
			</>
		);
	};

	const onGeneticModifierGenesValueChange = (event, setFieldValue, props) => {
		multipleAutocompleteOnChange(props, event, 'diseaseGeneticModifierGenes', setFieldValue);
	};

	const geneticModifierGenesSearch = (event, setFiltered, setInputValue) => {
		const autocompleteFields = [
			'geneSymbol.formatText',
			'geneSymbol.displayText',
			'geneFullName.formatText',
			'geneFullName.displayText',
			'geneSynonyms.formatText',
			'geneSynonyms.displayText',
			'geneSystematicName.formatText',
			'geneSystematicName.displayText',
			'geneSecondaryIds.secondaryId',
			'curie',
			'modEntityId',
			'modInternalId',
			'crossReferences.referencedCurie',
		];
		const endpoint = 'gene';
		const filterName = 'geneticModifierGenesFilter';
		const filter = buildAutocompleteFilter(event, autocompleteFields);
		setInputValue(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
	};

	const geneticModifierGenesEditorTemplate = (props) => {
		return (
			<>
				<AutocompleteMultiEditor
					search={geneticModifierGenesSearch}
					initialValue={props.rowData.diseaseGeneticModifierGenes}
					rowProps={props}
					fieldName="diseaseGeneticModifierGenes"
					subField="modEntityId"
					valueDisplay={(item, setAutocompleteHoverItem, op, query) => (
						<SubjectAutocompleteTemplate
							item={item}
							setAutocompleteHoverItem={setAutocompleteHoverItem}
							op={op}
							query={query}
						/>
					)}
					onValueChangeHandler={onGeneticModifierGenesValueChange}
				/>
				<ErrorMessageComponent
					errorMessages={errorMessagesRef.current[props.rowIndex]}
					errorField={'diseaseGeneticModifierGenes'}
				/>
				<ErrorMessageComponent
					errorMessages={uiErrorMessagesRef.current[props.rowIndex]}
					errorField={'diseaseGeneticModifierGenes'}
				/>
			</>
		);
	};

	const onAssertedAlleleValueChange = (event, setFieldValue, props) => {
		defaultAutocompleteOnChange(props, event, 'assertedAllele', setFieldValue, 'modEntityId');
	};

	const assertedAlleleSearch = (event, setFiltered, setQuery) => {
		const autocompleteFields = [
			'alleleSymbol.formatText',
			'alleleSymbol.displayText',
			'alleleFullName.formatText',
			'alleleFullName.displayText',
			'curie',
			'modEntityId',
			'modInternalId',
			'crossReferences.referencedCurie',
			'alleleSecondaryIds.secondaryId',
			'alleleSynonyms.formatText',
			'alleleSynonyms.displayText',
		];
		const endpoint = 'allele';
		const filterName = 'assertedAlleleFilter';
		const filter = buildAutocompleteFilter(event, autocompleteFields);
		setQuery(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
	};

	const assertedAlleleEditorTemplate = (props) => {
		if (props.rowData.type === 'AGMDiseaseAnnotation') {
			return (
				<>
					<AutocompleteEditor
						search={assertedAlleleSearch}
						initialValue={getIdentifier(props.rowData.assertedAllele)}
						rowProps={props}
						fieldName="assertedAllele"
						subField="modEntityId"
						valueDisplay={(item, setAutocompleteHoverItem, op, query) => (
							<SubjectAutocompleteTemplate
								item={item}
								setAutocompleteHoverItem={setAutocompleteHoverItem}
								op={op}
								query={query}
							/>
						)}
						onValueChangeHandler={onAssertedAlleleValueChange}
					/>
					<ErrorMessageComponent
						errorMessages={errorMessagesRef.current[props.rowIndex]}
						errorField={'assertedAllele'}
					/>
					<ErrorMessageComponent
						errorMessages={uiErrorMessagesRef.current[props.rowIndex]}
						errorField={'assertedAllele'}
					/>
				</>
			);
		} else {
			return null;
		}
	};

	const onDiseaseValueChange = (event, setFieldValue, props) => {
		defaultAutocompleteOnChange(props, event, 'diseaseAnnotationObject', setFieldValue);
	};

	const diseaseSearch = (event, setFiltered, setQuery) => {
		const autocompleteFields = [
			'curie',
			'name',
			'crossReferences.referencedCurie',
			'secondaryIdentifiers',
			'synonyms.name',
		];
		const endpoint = 'doterm';
		const filterName = 'diseaseFilter';
		const filter = buildAutocompleteFilter(event, autocompleteFields);
		const otherFilters = {
			obsoleteFilter: {
				obsolete: {
					queryString: false,
				},
			},
		};
		setQuery(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered, otherFilters);
	};

	const diseaseEditorTemplate = (props) => {
		return (
			<>
				<AutocompleteEditor
					search={diseaseSearch}
					initialValue={props.rowData.diseaseAnnotationObject?.curie}
					rowProps={props}
					fieldName="diseaseAnnotationObject"
					onValueChangeHandler={onDiseaseValueChange}
				/>
				<ErrorMessageComponent
					errorMessages={errorMessagesRef.current[props.rowIndex]}
					errorField={'diseaseAnnotationObject'}
				/>
			</>
		);
	};

	const onAssertedGeneValueChange = (event, setFieldValue, props) => {
		multipleAutocompleteOnChange(props, event, 'assertedGenes', setFieldValue);
	};

	const assertedGenesSearch = (event, setFiltered, setInputValue) => {
		const autocompleteFields = [
			'geneSymbol.formatText',
			'geneSymbol.displayText',
			'geneFullName.formatText',
			'geneFullName.displayText',
			'curie',
			'modEntityId',
			'modInternalId',
			'crossReferences.referencedCurie',
			'geneSynonyms.formatText',
			'geneSynonyms.displayText',
			'geneSystematicName.formatText',
			'geneSystematicName.displayText',
		];
		const endpoint = 'gene';
		const filterName = 'assertedGenesFilter';
		const filter = buildAutocompleteFilter(event, autocompleteFields);

		setInputValue(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
	};

	const assertedGenesEditorTemplate = (props) => {
		if (props.rowData.type === 'GeneDiseaseAnnotation') {
			return null;
		} else {
			return (
				<>
					<AutocompleteMultiEditor
						search={assertedGenesSearch}
						initialValue={props.rowData.assertedGenes}
						rowProps={props}
						fieldName="assertedGenes"
						subField="modEntityId"
						valueDisplay={(item, setAutocompleteHoverItem, op, query) => (
							<SubjectAutocompleteTemplate
								item={item}
								setAutocompleteHoverItem={setAutocompleteHoverItem}
								op={op}
								query={query}
							/>
						)}
						onValueChangeHandler={onAssertedGeneValueChange}
					/>
					<ErrorMessageComponent
						errorMessages={errorMessagesRef.current[props.rowIndex]}
						errorField={'assertedGenes'}
					/>
				</>
			);
		}
	};

	const onWithValueChange = (event, setFieldValue, props) => {
		multipleAutocompleteOnChange(props, event, 'with', setFieldValue);
	};

	const withSearch = (event, setFiltered, setInputValue) => {
		const autocompleteFields = [
			'geneSymbol.formatText',
			'geneSymbol.displayText',
			'geneFullName.formatText',
			'geneFullName.displayText',
			'modEntityId',
			'modInternalId',
			'curie',
			'crossReferences.referencedCurie',
			'geneSynonyms.formatText',
			'geneSynonyms.displayText',
			'geneSystematicName.formatText',
			'geneSystematicName.displayText',
			'geneSecondaryIds.secondaryId',
		];
		const endpoint = 'gene';
		const filterName = 'withFilter';
		const filter = buildAutocompleteFilter(event, autocompleteFields);
		const otherFilters = {
			taxonFilter: {
				'taxon.curie': {
					queryString: 'NCBITaxon:9606',
				},
			},
		};

		setInputValue(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered, otherFilters);
	};
	const withEditorTemplate = (props) => {
		return (
			<>
				<AutocompleteMultiEditor
					search={withSearch}
					initialValue={props.rowData.with}
					rowProps={props}
					fieldName="with"
					subField="modEntityId"
					valueDisplay={(item, setAutocompleteHoverItem, op, query) => (
						<SubjectAutocompleteTemplate
							item={item}
							setAutocompleteHoverItem={setAutocompleteHoverItem}
							op={op}
							query={query}
						/>
					)}
					onValueChangeHandler={onWithValueChange}
				/>
				<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField="with" />
			</>
		);
	};

	const onEvidenceValueChange = (event, setFieldValue, props) => {
		multipleAutocompleteOnChange(props, event, 'evidenceCodes', setFieldValue);
	};

	const evidenceSearch = (event, setFiltered, setInputValue) => {
		const autocompleteFields = ['curie', 'name', 'abbreviation'];
		const endpoint = 'ecoterm';
		const filterName = 'evidenceFilter';
		const filter = buildAutocompleteFilter(event, autocompleteFields);
		const otherFilters = {
			obsoleteFilter: {
				obsolete: {
					queryString: false,
				},
			},
			subsetFilter: {
				subsets: {
					queryString: 'agr_eco_terms',
				},
			},
		};

		setInputValue(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered, otherFilters);
	};

	const evidenceEditorTemplate = (props) => {
		return (
			<>
				<AutocompleteMultiEditor
					search={evidenceSearch}
					initialValue={props.rowData.evidenceCodes}
					rowProps={props}
					fieldName="evidenceCodes"
					valueDisplay={(item, setAutocompleteHoverItem, op, query) => (
						<EvidenceAutocompleteTemplate
							item={item}
							setAutocompleteHoverItem={setAutocompleteHoverItem}
							op={op}
							query={query}
						/>
					)}
					onValueChangeHandler={onEvidenceValueChange}
				/>
				<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField="evidenceCodes" />
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

	const uniqueIdEditorTemplate = (props) => {
		return (
			<>
				<EllipsisTableCell otherClasses={`c${props.rowData.id}`}>{props.rowData.uniqueId}</EllipsisTableCell>
				<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField={'uniqueId'} />
			</>
		);
	};

	const sgdStrainBackgroundEditorSelector = (props) => {
		if (props.rowData.type === 'GeneDiseaseAnnotation') {
			return sgdStrainBackgroundEditorTemplate(props);
		} else {
			return null;
		}
	};

	const columns = [
		{
			field: 'uniqueId',
			header: 'Unique ID',
			body: (rowData) => <IdTemplate id={rowData.uniqueId} />,
			sortable: true,
			filterConfig: FILTER_CONFIGS.uniqueidFilterConfig,
			editor: (props) => uniqueIdEditorTemplate(props),
		},
		{
			field: 'modEntityId',
			header: 'MOD Annotation ID',
			body: (rowData) => <IdTemplate id={rowData.modEntityId} />,
			sortable: true,
			filterConfig: FILTER_CONFIGS.modentityidFilterConfig,
		},
		{
			field: 'modInternalId',
			header: 'MOD Internal ID',
			body: (rowData) => <IdTemplate id={rowData.modInternalId} />,
			sortable: true,
			filterConfig: FILTER_CONFIGS.modinternalidFilterConfig,
		},
		{
			field: 'diseaseAnnotationSubject.symbol',
			header: 'Subject',
			body: (rowData) => <GenomicEntityTemplate genomicEntity={rowData.diseaseAnnotationSubject} />,
			sortable: true,
			filterConfig: FILTER_CONFIGS.diseaseAnnotationSubjectFieldConfig,
			editor: (props) => subjectEditorTemplate(props),
		},
		{
			field: 'relation.name',
			header: 'Disease Relation',
			sortable: true,
			filterConfig: FILTER_CONFIGS.relationFilterConfig,
			editor: (props) => relationEditor(props),
		},
		{
			field: 'negated',
			header: 'NOT',
			body: (rowData) => <NotTemplate value={rowData.negated} />,
			sortable: true,
			filterConfig: FILTER_CONFIGS.negatedFilterConfig,
			editor: (props) => <NotEditor props={props} value={props.value} editorChange={onNegatedEditorValueChange} />,
		},
		{
			field: 'diseaseAnnotationObject.name',
			header: 'Disease',
			body: (rowData) => <OntologyTermTemplate term={rowData.diseaseAnnotationObject} />,
			sortable: true,
			filterConfig: FILTER_CONFIGS.diseaseAnnotationObjectFilterConfig,
			editor: (props) => diseaseEditorTemplate(props),
		},
		{
			field: 'singleReference.primaryCrossReferenceCurie',
			header: 'Reference',
			body: (rowData) => <SingleReferenceTemplate singleReference={rowData.singleReference} />,
			sortable: true,
			filterConfig: FILTER_CONFIGS.singleReferenceFilterConfig,
			editor: (props) => referenceEditorTemplate(props),
		},
		{
			field: 'evidenceCodes.abbreviation',
			header: 'Evidence Code',
			body: (rowData) => (
				<ObjectListTemplate
					list={rowData.evidenceCodes}
					sortMethod={evidenceCodesSort}
					stringTemplate={(item) => `${item.abbreviation} - ${item.name} (${item.curie})`}
				/>
			),
			sortable: true,
			filterConfig: FILTER_CONFIGS.evidenceCodesFilterConfig,
			editor: (props) => evidenceEditorTemplate(props),
		},
		{
			field: 'with.geneSymbol.displayText',
			header: 'With',
			body: (rowData) => <GenomicEntityListTemplate genomicEntities={rowData.with} />,
			sortable: true,
			filterConfig: FILTER_CONFIGS.withFilterConfig,
			editor: (props) => withEditorTemplate(props),
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
			field: 'conditionRelations.handle',
			header: 'Experiments',
			body: (rowData) => {
				if (!rowData.conditionRelations?.[0]?.handle) return null;
				return (
					<TextDialogTemplate
						entity={rowData.conditionRelations}
						handleOpen={handleConditionRelationsOpen}
						text={rowData.conditionRelations[0].handle}
						underline={false}
					/>
				);
			},
			sortable: true,
			filterConfig: FILTER_CONFIGS.daConditionRelationsHandleFilterConfig,
			editor: (props) => conditionRelationHandleEditor(props),
		},
		{
			field: 'conditionRelations.uniqueId',
			header: 'Experimental Conditions',
			body: (rowData) => {
				if (rowData.conditionRelations?.[0]?.handle) return null;
				return (
					<CountDialogTemplate
						entities={rowData.conditionRelations}
						handleOpen={handleConditionRelationsOpen}
						text={'Conditions'}
					/>
				);
			},
			sortable: true,
			filterConfig: FILTER_CONFIGS.daConditionRelationsSummaryFilterConfig,
			editor: (props) => conditionRelationsEditor(props),
		},
		{
			field: 'geneticSex.name',
			header: 'Genetic Sex',
			sortable: true,
			filterConfig: FILTER_CONFIGS.geneticSexFilterConfig,
			editor: (props) => geneticSexEditor(props),
		},
		{
			field: 'diseaseQualifiers.name',
			header: 'Disease Qualifiers',
			body: (rowData) => (
				<ObjectListTemplate
					list={rowData.diseaseQualifiers}
					sortMethod={diseaseQualifiersSort}
					stringTemplate={(item) => item.name}
				/>
			),
			sortable: true,
			filterConfig: FILTER_CONFIGS.diseaseQualifiersFilterConfig,
			editor: (props) => diseaseQualifiersEditor(props),
		},
		{
			field: 'sgdStrainBackground.name',
			header: 'SGD Strain Background',
			body: (rowData) => <GenomicEntityTemplate genomicEntity={rowData.sgdStrainBackground} />,
			sortable: true,
			filterConfig: FILTER_CONFIGS.sgdStrainBackgroundFilterConfig,
			editor: (props) => sgdStrainBackgroundEditorSelector(props),
		},
		{
			field: 'annotationType.name',
			header: 'Annotation Type',
			sortable: true,
			filterConfig: FILTER_CONFIGS.annotationTypeFilterConfig,
			editor: (props) => annotationTypeEditor(props),
		},
		{
			field: 'diseaseGeneticModifierRelation.name',
			header: 'Genetic Modifier Relation',
			sortable: true,
			filterConfig: FILTER_CONFIGS.geneticModifierRelationFilterConfig,
			editor: (props) => geneticModifierRelationEditor(props),
		},
		{
			field: 'diseaseGeneticModifierAgms.name',
			header: 'Genetic Modifier AGMs',
			body: (rowData) => <GenomicEntityListTemplate genomicEntities={rowData.diseaseGeneticModifierAgms} />,
			sortable: true,
			filterConfig: FILTER_CONFIGS.geneticModifierAgmsFilterConfig,
			editor: (props) => geneticModifierAgmsEditorTemplate(props),
		},
		{
			field: 'diseaseGeneticModifierAlleles.alleleSymbol.displayText',
			header: 'Genetic Modifier Alleles',
			body: (rowData) => <GenomicEntityListTemplate genomicEntities={rowData.diseaseGeneticModifierAlleles} />,
			sortable: true,
			filterConfig: FILTER_CONFIGS.geneticModifierAllelesFilterConfig,
			editor: (props) => geneticModifierAllelesEditorTemplate(props),
		},
		{
			field: 'diseaseGeneticModifierGenes.geneSymbol.displayText',
			header: 'Genetic Modifier Genes',
			body: (rowData) => <GenomicEntityListTemplate genomicEntities={rowData.diseaseGeneticModifierGenes} />,
			sortable: true,
			filterConfig: FILTER_CONFIGS.geneticModifierGenesFilterConfig,
			editor: (props) => geneticModifierGenesEditorTemplate(props),
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
			editor: (props) => assertedGenesEditorTemplate(props),
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
			editor: (props) => assertedAlleleEditorTemplate(props),
		},
		{
			field: 'dataProvider.sourceOrganization.abbreviation',
			header: 'Data Provider',
			sortable: true,
			filterConfig: FILTER_CONFIGS.diseaseDataProviderFilterConfig,
		},
		{
			field: 'secondaryDataProvider.sourceOrganization.abbreviation',
			header: 'Secondary Data Provider',
			sortable: true,
			filterConfig: FILTER_CONFIGS.secondaryDataProviderFilterConfig,
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
			body: (rowData) => <StringTemplate string={rowData.dateUpdated} />,
			filterConfig: FILTER_CONFIGS.dateUpdatedFilterConfig,
		},
		{
			field: 'createdBy.uniqueId',
			header: 'Created By',
			sortable: true,
			body: (rowData) => <StringTemplate string={rowData.createdBy?.uniqueId} />,
			filterConfig: FILTER_CONFIGS.createdByFilterConfig,
		},
		{
			field: 'dateCreated',
			header: 'Date Created',
			sortable: true,
			body: (rowData) => <StringTemplate string={rowData.dateCreated} />,
			filterConfig: FILTER_CONFIGS.dataCreatedFilterConfig,
		},
		{
			field: 'internal',
			header: 'Internal',
			body: (rowData) => <BooleanTemplate value={rowData.internal} />,
			sortable: true,
			filterConfig: FILTER_CONFIGS.internalFilterConfig,
			editor: (props) => internalEditor(props),
		},
		{
			field: 'obsolete',
			header: 'Obsolete',
			body: (rowData) => <BooleanTemplate value={rowData.obsolete} />,
			sortable: true,
			filterConfig: FILTER_CONFIGS.obsoleteFilterConfig,
			editor: (props) => obsoleteEditor(props),
		},
	];

	const DEFAULT_COLUMN_WIDTH = 10;
	const SEARCH_ENDPOINT = 'disease-annotation';
	const defaultFilters = { obsoleteFilter: { obsolete: { queryString: 'false' } } };

	const initialTableState = getDefaultTableState('DiseaseAnnotations', columns, DEFAULT_COLUMN_WIDTH, defaultFilters);

	const { settings: tableState, mutate: setTableState } = useGetUserSettings(
		initialTableState.tableSettingsKeyName,
		initialTableState
	);

	const { isLoading, isFetching } = useGetTableData({
		tableState,
		endpoint: SEARCH_ENDPOINT,
		sortMapping,
		setIsInEditMode,
		setEntities: setDiseaseAnnotations,
		setTotalRecords,
		toast_topleft,
		searchService,
	});

	const headerButtons = (disabled = false) => {
		return (
			<>
				<Button label="New Annotation" icon="pi pi-plus" onClick={handleNewAnnotationOpen} disabled={disabled} />
				&nbsp;&nbsp;
			</>
		);
	};

	return (
		<>
			<div className="card">
				<Toast ref={toast_topleft} position="top-left" />
				<Toast ref={toast_topright} position="top-right" />
				<GenericDataTable
					endpoint={SEARCH_ENDPOINT}
					tableName="Disease Annotations"
					entities={diseaseAnnotations}
					setEntities={setDiseaseAnnotations}
					totalRecords={totalRecords}
					setTotalRecords={setTotalRecords}
					tableState={tableState}
					setTableState={setTableState}
					columns={columns}
					isEditable={true}
					mutation={mutation}
					isInEditMode={isInEditMode}
					setIsInEditMode={setIsInEditMode}
					toasts={{ toast_topleft, toast_topright }}
					errorObject={{ errorMessages, setErrorMessages, uiErrorMessages, setUiErrorMessages }}
					headerButtons={headerButtons}
					deletionEnabled={true}
					deletionMethod={diseaseAnnotationService.deleteDiseaseAnnotation}
					deprecationMethod={diseaseAnnotationService.deprecateDiseaseAnnotation}
					deprecateOption={true}
					modReset={true}
					handleDuplication={handleDuplication}
					duplicationEnabled={true}
					defaultColumnWidth={DEFAULT_COLUMN_WIDTH}
					fetching={isFetching || isLoading}
					defaultFilters={defaultFilters}
				/>
			</div>
			<NewAnnotationForm
				newAnnotationState={newAnnotationState}
				newAnnotationDispatch={newAnnotationDispatch}
				searchService={searchService}
				relationsTerms={relationsTerms}
				negatedTerms={booleanTerms}
				setNewDiseaseAnnotation={(newAnnotation, queryClient) =>
					setNewEntity(tableState, setDiseaseAnnotations, newAnnotation, queryClient)
				}
			/>
			<RelatedNotesDialog
				originalRelatedNotesData={relatedNotesData}
				setOriginalRelatedNotesData={setRelatedNotesData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
				noteTypeVocabularyTermSet="da_note_type"
				showReferences={false}
			/>
			<ConditionRelationsDialog
				originalConditionRelationsData={conditionRelationsData}
				setOriginalConditionRelationsData={setConditionRelationsData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
			/>
		</>
	);
};
