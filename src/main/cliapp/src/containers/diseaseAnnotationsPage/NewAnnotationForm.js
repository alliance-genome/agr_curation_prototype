import React, { useRef, useState } from 'react';
import { Dialog } from 'primereact/dialog';
import { Button } from 'primereact/button';
import { Dropdown } from 'primereact/dropdown';
import { Toast } from 'primereact/toast';
import { MultiSelect } from 'primereact/multiselect';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { FormErrorMessageComponent } from '../../components/Error/FormErrorMessageComponent';
import { NotEditor } from '../../components/Editors/NotEditor';
import { classNames } from 'primereact/utils';
import { DiseaseAnnotationService } from '../../service/DiseaseAnnotationService';
import { Splitter, SplitterPanel } from 'primereact/splitter';
import { LiteratureAutocompleteTemplate } from '../../components/Autocomplete/LiteratureAutocompleteTemplate';
import { SubjectAutocompleteTemplate } from '../../components/Autocomplete/SubjectAutocompleteTemplate';
import { EvidenceAutocompleteTemplate } from '../../components/Autocomplete/EvidenceAutocompleteTemplate';
import { RelatedNotesForm } from './RelatedNotesForm';
import { ConditionRelationsForm } from './ConditionRelationsForm';
import { ConditionRelationHandleFormDropdown } from '../../components/ConditionRelationHandleFormSelector';
import { ControlledVocabularyFormDropdown } from '../../components/ControlledVocabularyFormSelector';
import { useControlledVocabularyService } from '../../service/useControlledVocabularyService';
import { ControlledVocabularyFormMultiSelectDropdown } from '../../components/ControlledVocabularyFormMultiSelector';
import { AutocompleteFormEditor } from '../../components/Autocomplete/AutocompleteFormEditor';
import {
	autocompleteSearch,
	buildAutocompleteFilter,
	validateRequiredFields,
	validateFormBioEntityFields,
	validateTable,
} from '../../utils/utils';
import { AutocompleteFormMultiEditor } from '../../components/Autocomplete/AutocompleteFormMultiEditor';
import { SubjectAdditionalFieldData } from '../../components/FieldData/SubjectAdditionalFieldData';
import { AssertedAlleleAdditionalFieldData } from '../../components/FieldData/AssertedAlleleAdditionalFieldData';
import { DiseaseAdditionalFieldData } from '../../components/FieldData/DiseaseAdditionalFieldData';
import { SingleReferenceAdditionalFieldData } from '../../components/FieldData/SingleReferenceAdditionalFieldData';
import { SGDStrainBackgroundAdditionalFieldData } from '../../components/FieldData/SGDStrainBackgroundAdditionalFieldData';
import { AssertedGenesAdditionalFieldData } from '../../components/FieldData/AssertedGenesAdditionalFieldData';
import { EvidenceCodesAdditionalFieldData } from '../../components/FieldData/EvidenceCodesAdditionalFieldData';
import { WithAdditionalFieldData } from '../../components/FieldData/WithAdditionalFieldData';
import { GeneticModifierAgmsAdditionalFieldData } from '../../components/FieldData/GeneticModifierAgmsAdditionalFieldData';
import { GeneticModifierAllelesAdditionalFieldData } from '../../components/FieldData/GeneticModifierAllelesAdditionalFieldData';
import { GeneticModifierGenesAdditionalFieldData } from '../../components/FieldData/GeneticModifierGenesAdditionalFieldData';
import ErrorBoundary from '../../components/Error/ErrorBoundary';
import { ConfirmButton } from '../../components/ConfirmButton';
import { getDefaultFormState, getModFormFields } from '../../service/TableStateService';
import { useGetUserSettings } from '../../service/useGetUserSettings';

export const NewAnnotationForm = ({
	newAnnotationState,
	newAnnotationDispatch,
	searchService,
	diseaseAnnotationService,
	relationsTerms,
	negatedTerms,
	setNewDiseaseAnnotation,
}) => {
	const queryClient = useQueryClient();
	const toast_success = useRef(null);
	const toast_error = useRef(null);
	const withRef = useRef(null);
	const assertedGenesRef = useRef(null);
	const assertedAlleleRef = useRef(null);
	const evidenceCodesRef = useRef(null);
	const experimentsRef = useRef(null);
	const {
		newAnnotation,
		errorMessages,
		relatedNotesErrorMessages,
		relatedNotesEditingRows,
		exConErrorMessages,
		conditionRelationsEditingRows,
		submitted,
		newAnnotationDialog,
		showRelatedNotes,
		showConditionRelations,
		isEnabled,
		isAssertedGeneEnabled,
		isAssertedAlleleEnabled,
	} = newAnnotationState;
	const geneticSexTerms = useControlledVocabularyService('genetic_sex');
	const diseaseQualifiersTerms = useControlledVocabularyService('disease_qualifier');
	const annotationTypeTerms = useControlledVocabularyService('annotation_type');
	const booleanTerms = useControlledVocabularyService('generic_boolean_terms');
	const geneticModifierRelationTerms = useControlledVocabularyService('disease_genetic_modifier_relation');
	const [uiErrorMessages, setUiErrorMessages] = useState({});
	const areUiErrors = useRef(false);
	let newAnnotationOptionalFields = [
		'Asserted Genes',
		'Asserted Allele',
		'NOT',
		'With',
		'Related Notes',
		'Experimental Conditions',
		'Experiments',
		'Genetic Sex',
		'Disease Qualifiers',
		'SGD Strain Background',
		'Annotation Type',
		'Genetic Modifier Relation',
		'Genetic Modifier AGMs',
		'Genetic Modifier Alleles',
		'Genetic Modifier Genes',
		'Internal',
	];
	const oktaToken = JSON.parse(localStorage.getItem('okta-token-storage'));
	const mod = oktaToken?.accessToken?.claims?.Groups?.filter((group) => group.includes('Staff'));
	let defaultUserSettings = getDefaultFormState('DiseaseAnnotations', newAnnotationOptionalFields, undefined);
	const { settings: settingsKey, mutate: setSettingsKey } = useGetUserSettings(
		'DiseaseAnnotationsFormSettings',
		defaultUserSettings,
		false
	);
	const { selectedFormFields } = settingsKey;
	const mutation = useMutation((newAnnotation) => {
		if (!diseaseAnnotationService) {
			diseaseAnnotationService = new DiseaseAnnotationService();
		}
		return diseaseAnnotationService.createDiseaseAnnotation(newAnnotation);
	});

	const hideDialog = () => {
		newAnnotationDispatch({ type: 'RESET' });
		newAnnotationDispatch({ type: 'SET_IS_ENABLED', value: false });
		newAnnotationDispatch({ type: 'SET_IS_ASSERTED_ALLELE_ENABLED', value: false });
		newAnnotationDispatch({ type: 'SET_IS_ASSERTED_GENE_ENABLED', value: false });
		setUiErrorMessages({});
	};

	const handleSubmit = async (event, closeAfterSubmit = true) => {
		event.preventDefault();
		newAnnotationDispatch({ type: 'SUBMIT' });
		const isRelatedNotesErrors = await validateTable(
			'note',
			'relatedNotesErrorMessages',
			newAnnotation.relatedNotes,
			newAnnotationDispatch
		);
		const isExConErrors = await validateTable(
			'condition-relation',
			'exConErrorMessages',
			newAnnotation.conditionRelations,
			newAnnotationDispatch
		);

		areUiErrors.current = false;
		validateRequiredFields(newAnnotation, uiErrorMessages, setUiErrorMessages, areUiErrors, mod);
		validateFormBioEntityFields(newAnnotation, uiErrorMessages, setUiErrorMessages, areUiErrors);
		if (areUiErrors.current) {
			newAnnotationDispatch({ type: 'SET_IS_ENABLED', value: true });

			return;
		}

		mutation.mutate(newAnnotation, {
			onSuccess: (data) => {
				if (!(isRelatedNotesErrors || isExConErrors)) {
					queryClient.invalidateQueries(['DiseaseAnnotationsHandles']);
					toast_success.current.show({ severity: 'success', summary: 'Successful', detail: 'New Annotation Added' });
					if (closeAfterSubmit) {
						newAnnotationDispatch({ type: 'RESET' });
					} else {
						setUiErrorMessages({});
					}

					setNewDiseaseAnnotation(data.data.entity, queryClient);
				}
			},
			onError: (error) => {
				let message;
				if (error?.response?.data?.errorMessages?.uniqueId) {
					message = 'Page Error: New annotation is a duplicate of an existing annotation';
				} else if (error?.response?.data?.errorMessage) {
					message = error.response.data.errorMessage;
				} else {
					//toast will still display even if 500 error and no errorMessages
					message = `${error.response.status} ${error.response.statusText}`;
				}

				toast_error.current.show({ severity: 'error', summary: 'Page error: ', detail: message });

				newAnnotationDispatch({
					type: 'UPDATE_ERROR_MESSAGES',
					errorType: 'errorMessages',
					errorMessages: error.response?.data?.errorMessages || {},
				});
			},
		});
	};

	const handleClear = () => {
		//this manually resets the value of the input text in autocomplete fields with multiple values and the experiments dropdown
		if (withRef.current?.getInput()) withRef.current.getInput().value = '';
		if (evidenceCodesRef.current?.getInput().value) evidenceCodesRef.current.getInput().value = '';
		newAnnotationDispatch({ type: 'CLEAR' });
		newAnnotationDispatch({ type: 'SET_IS_ENABLED', value: false });
		newAnnotationDispatch({ type: 'SET_IS_ASSERTED_GENE_ENABLED', value: false });
		newAnnotationDispatch({ type: 'SET_IS_ASSERTED_ALLELE_ENABLED', value: false });

		setUiErrorMessages({});
	};

	const handleSubmitAndAdd = (event) => {
		handleSubmit(event, false);
	};

	const onSingleReferenceChange = (event) => {
		setUiErrorMessages({});
		newAnnotationDispatch({
			type: 'EDIT',
			field: event.target.name,
			value: event.target.value,
		});
	};

	const referenceSearch = (event, setFiltered, setQuery) => {
		const autocompleteFields = ['curie', 'cross_references.curie'];
		const endpoint = 'literature-reference';
		const filterName = 'curieFilter';
		const filter = buildAutocompleteFilter(event, autocompleteFields);
		setQuery(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
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

	const geneticModifierAgmsSearch = (event, setFiltered, setQuery) => {
		const autocompleteFields = ['modEntityId', 'modInternalId', 'name', 'curie', 'crossReferences.referencedCurie'];
		const endpoint = 'agm';
		const filterName = 'geneticModifierAgmsFilter';
		const filter = buildAutocompleteFilter(event, autocompleteFields);
		setQuery(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
	};

	const geneticModifierAllelesSearch = (event, setFiltered, setQuery) => {
		const autocompleteFields = [
			'alleleSymbol.displayText',
			'alleleFullName.displayText',
			'modEntityId',
			'modInternalId',
			'curie',
			'crossReferences.referencedCurie',
			'alleleSecondaryIds.secondaryId',
			'alleleSynonyms.displayText',
		];
		const endpoint = 'allele';
		const filterName = 'geneticModifierAllelesFilter';
		const filter = buildAutocompleteFilter(event, autocompleteFields);
		setQuery(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
	};

	const geneticModifierGenesSearch = (event, setFiltered, setQuery) => {
		const autocompleteFields = [
			'geneSymbol.displayText',
			'geneFullName.displayText',
			'modEntityId',
			'modInternalId',
			'curie',
			'crossReferences.referencedCurie',
			'geneSynonyms.displayText',
		];
		const endpoint = 'gene';
		const filterName = 'geneticModifierGenesFilter';
		const filter = buildAutocompleteFilter(event, autocompleteFields);
		setQuery(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
	};

	const onSubjectChange = (event) => {
		setUiErrorMessages({});
		if (event.target && event.target.value !== '' && event.target.value != null) {
			newAnnotationDispatch({ type: 'SET_IS_ENABLED', value: true });
		} else {
			newAnnotationDispatch({ type: 'SET_IS_ENABLED', value: false });
		}
		if (
			event.target.value &&
			event.target.value.type &&
			(event.target.value.type === 'Allele' || event.target.value.type === 'AffectedGenomicModel')
		) {
			newAnnotationDispatch({ type: 'SET_IS_ASSERTED_GENE_ENABLED', value: true });
		} else {
			newAnnotationDispatch({ type: 'SET_IS_ASSERTED_GENE_ENABLED', value: false });
		}
		if (event.target.value && event.target.value.type && event.target.value.type === 'AffectedGenomicModel') {
			newAnnotationDispatch({ type: 'SET_IS_ASSERTED_ALLELE_ENABLED', value: true });
		} else {
			newAnnotationDispatch({ type: 'SET_IS_ASSERTED_ALLELE_ENABLED', value: false });
		}
		newAnnotationDispatch({
			type: 'EDIT',
			field: event.target.name,
			value: event.target.value,
		});
	};

	const subjectSearch = (event, setFiltered, setQuery) => {
		//The order of the below fields are as per the Autocomplete search result
		const autocompleteFields = [
			'geneSymbol.displayText',
			'alleleSymbol.displayText',
			'name',
			'geneFullName.displayText',
			'alleleFullName.displayText',
			'alleleSynonyms.displayText',
			'geneSynonyms.displayText',
			'modEntityId',
			'modInternalId',
			'curie',
			'crossReferences.referencedCurie',
			'alleleSecondaryIds.secondaryId',
		];
		const endpoint = 'biologicalentity';
		const filterName = 'diseaseAnnotationSubjectFilter';
		const filter = buildAutocompleteFilter(event, autocompleteFields);
		setQuery(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
	};

	const onDiseaseChange = (event) => {
		/*const curie = event.value.curie;
		const stringValue = event.value;
		const value = typeof event.value === "string" ? {curie: stringValue} : {curie};*/

		newAnnotationDispatch({
			type: 'EDIT',
			field: event.target.name,
			value: event.value,
		});
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
		setQuery(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
	};

	const onDropdownFieldChange = (event) => {
		newAnnotationDispatch({
			type: 'EDIT',
			field: event.target.name,
			value: event.target.value,
		});
	};

	const onDropdownExperimentsFieldChange = (event) => {
		newAnnotationDispatch({
			type: 'EDIT_EXPERIMENT',
			field: event.target.name,
			value: event.target.value,
		});
	};

	const onArrayFieldChange = (event) => {
		newAnnotationDispatch({
			type: 'EDIT',
			field: event.target.name,
			value: event.target.value,
		});
	};

	const onControlledVocabChange = (event) => {
		newAnnotationDispatch({
			type: 'EDIT',
			field: event.target.name,
			value: event.target.value,
		});
	};
	const isExperimentEnabled = () => {
		return (
			//only enabled if a reference is selected from suggestions and condition relation table isn't visible
			typeof newAnnotation.singleReference === 'object' &&
			newAnnotation.singleReference.curie !== '' &&
			!showConditionRelations
		);
	};

	const isConditionRelationButtonEnabled = () => {
		return newAnnotation.conditionRelations?.[0] && newAnnotation.conditionRelations?.[0].handle;
	};
	const evidenceSearch = (event, setFiltered, setInputValue) => {
		const autocompleteFields = ['curie', 'name', 'abbreviation'];
		const endpoint = 'ecoterm';
		const filterName = 'evidenceFilter';
		const filter = buildAutocompleteFilter(event, autocompleteFields);
		const otherFilters = {
			subsetFilter: {
				subsets: {
					queryString: 'agr_eco_terms',
				},
			},
		};

		setInputValue(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered, otherFilters);
	};

	const withSearch = (event, setFiltered, setInputValue) => {
		const autocompleteFields = [
			'geneSymbol.displayText',
			'geneFullName.displayText',
			'modEntityId',
			'modInternalId',
			'curie',
			'crossReferences.referencedCurie',
			'geneSynonyms.displayText',
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

	const dialogFooter = (
		<>
			<Splitter style={{ border: 'none', paddingTop: '12px', height: '10%' }} gutterSize="0">
				<SplitterPanel style={{ textAlign: 'left', flexGrow: 0 }} size={10}>
					<Button label="Clear" icon="pi pi-check" className="p-button-text" onClick={handleClear} />
					<Button label="Cancel" icon="pi pi-times" className="p-button-text" onClick={hideDialog} />
				</SplitterPanel>
				<SplitterPanel style={{ flexGrow: 0 }} size={60}></SplitterPanel>
				<SplitterPanel style={{ textAlign: 'right', flexGrow: 0 }} size={30}>
					<Button
						label="Save & Close"
						icon="pi pi-check"
						className="p-button-text"
						disabled={!isEnabled}
						onClick={handleSubmit}
					/>
					<Button
						label="Save & Add Another"
						icon="pi pi-check"
						className="p-button-text"
						disabled={!isEnabled}
						onClick={handleSubmitAndAdd}
					/>
				</SplitterPanel>
			</Splitter>
		</>
	);

	const assertedGenesSearch = (event, setFiltered, setInputValue) => {
		const autocompleteFields = [
			'geneSymbol.displayText',
			'geneFullName.displayText',
			'modEntityId',
			'modInternalId',
			'curie',
			'crossReferences.referencedCurie',
			'geneSynonyms.displayText',
		];
		const endpoint = 'gene';
		const filterName = 'assertedGenesFilter';
		const filter = buildAutocompleteFilter(event, autocompleteFields);
		setInputValue(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
	};

	const assertedAlleleSearch = (event, setFiltered, setInputValue) => {
		const autocompleteFields = [
			'alleleSymbol.displayText',
			'alleleFullName.displayText',
			'modEntityId',
			'modInternalId',
			'curie',
			'crossReferences.referencedCurie',
			'alleleSecondaryIds.secondaryId',
			'alleleSynonyms.displayText',
		];
		const endpoint = 'allele';
		const filterName = 'assertedAlleleFilter';
		const filter = buildAutocompleteFilter(event, autocompleteFields);
		setInputValue(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
	};

	const updateFormFields = (updatedFields) => {
		let updatedSettings = { ...defaultUserSettings, selectedFormFields: updatedFields };
		setSettingsKey(updatedSettings);
	};

	const handleShowAllFields = () => {
		updateFormFields(newAnnotationOptionalFields);
	};

	const setToModDefault = () => {
		const modFormFields = getModFormFields('DiseaseAnnotations');
		updateFormFields(modFormFields);
	};

	const dialogHeader = (
		<>
			<Splitter style={{ border: 'none', height: '5%' }} gutterSize="0">
				<SplitterPanel size={25} style={{ textAlign: 'left' }}>
					<h4>Add Disease Annotation</h4>
				</SplitterPanel>
				<SplitterPanel size={10} style={{ textAlign: 'right', padding: '5px' }}>
					<MultiSelect
						aria-label="columnToggle"
						options={newAnnotationOptionalFields}
						value={selectedFormFields}
						filter
						resetFilterOnHide
						onChange={(e) => updateFormFields(e.value)}
						className="w-20rem text-center"
						maxSelectedLabels={4}
					/>
				</SplitterPanel>
				<SplitterPanel size={10} style={{ textAlign: 'right', padding: '5px' }}>
					<Button label="Show all fields" onClick={handleShowAllFields} />
				</SplitterPanel>
				<SplitterPanel size={10} style={{ textAlign: 'right', padding: '5px' }}>
					<ConfirmButton
						buttonText="Set to MOD Defaults"
						messageText={`Are you sure? This will reset to MOD default settings.`}
						acceptHandler={setToModDefault}
					/>
				</SplitterPanel>
			</Splitter>
		</>
	);

	const labelColumnSize = 'col-2';
	const widgetColumnSize = 'col-4';
	const fieldDetailsColumnSize = 'col-5';
	const requiredfield = <font color={'red'}>*</font>;
	let required = '';

	return (
		<div>
			<Toast ref={toast_error} position="top-left" />
			<Toast ref={toast_success} position="top-right" />
			<Dialog
				visible={newAnnotationDialog}
				header={dialogHeader}
				position={'top'}
				modal
				className="p-fluid w-9"
				footer={dialogFooter}
				onHide={hideDialog}
				maximizable
			>
				<ErrorBoundary>
					<form>
						<div className="grid">
							<div className={labelColumnSize}>
								<label htmlFor="diseaseAnnotationSubject">
									<font color={'red'}>*</font>Subject
								</label>
							</div>
							<div className={widgetColumnSize}>
								<AutocompleteFormEditor
									initialValue={newAnnotation.diseaseAnnotationSubject}
									search={subjectSearch}
									fieldName="diseaseAnnotationSubject"
									subField="modEntityId"
									name="diseaseAnnotationSubject"
									searchService={searchService}
									onValueChangeHandler={onSubjectChange}
									valueDisplay={(item, setAutocompleteHoverItem, op, query) => (
										<SubjectAutocompleteTemplate
											item={item}
											setAutocompleteHoverItem={setAutocompleteHoverItem}
											op={op}
											query={query}
										/>
									)}
									classNames={classNames({ 'p-invalid': submitted && errorMessages.diseaseAnnotationSubject })}
								/>
							</div>
							<div className={fieldDetailsColumnSize}>
								<FormErrorMessageComponent errorMessages={errorMessages} errorField={'diseaseAnnotationSubject'} />
								<FormErrorMessageComponent errorMessages={uiErrorMessages} errorField={'diseaseAnnotationSubject'} />
								<SubjectAdditionalFieldData fieldData={newAnnotation.diseaseAnnotationSubject} />
							</div>
						</div>

						{selectedFormFields?.includes('Asserted Genes') && (
							<>
								<div className="grid">
									<div className={labelColumnSize}>
										<label htmlFor="assertedGenes">Asserted Genes</label>
									</div>
									<div className={widgetColumnSize}>
										<AutocompleteFormMultiEditor
											customRef={assertedGenesRef}
											search={assertedGenesSearch}
											name="assertedGenes"
											subField="modEntityId"
											label="Asserted Genes"
											fieldName="assertedGenes"
											disabled={!isAssertedGeneEnabled}
											initialValue={newAnnotation.assertedGenes}
											onValueChangeHandler={onArrayFieldChange}
											valueDisplay={(item, setAutocompleteHoverItem, op, query) => (
												<SubjectAutocompleteTemplate
													item={item}
													setAutocompleteHoverItem={setAutocompleteHoverItem}
													op={op}
													query={query}
												/>
											)}
											classNames={classNames({ 'p-invalid': submitted && errorMessages.assertedGenes })}
										/>
									</div>
									<div className={fieldDetailsColumnSize}>
										<FormErrorMessageComponent errorMessages={errorMessages} errorField={'assertedGenes'} />
										<AssertedGenesAdditionalFieldData fieldData={newAnnotation.assertedGenes} />
									</div>
								</div>
							</>
						)}

						{selectedFormFields?.includes('Asserted Allele') && (
							<>
								<div className="grid">
									<div className={labelColumnSize}>
										<label htmlFor="assertedAllele">Asserted Allele</label>
									</div>
									<div className={widgetColumnSize}>
										<AutocompleteFormEditor
											customRef={assertedAlleleRef}
											search={assertedAlleleSearch}
											name="assertedAllele"
											label="Asserted Allele"
											fieldName="assertedAllele"
											subField="modEntityId"
											disabled={!isAssertedAlleleEnabled}
											initialValue={newAnnotation.assertedAllele}
											onValueChangeHandler={onSingleReferenceChange}
											valueDisplay={(item, setAutocompleteHoverItem, op, query) => (
												<SubjectAutocompleteTemplate
													item={item}
													setAutocompleteHoverItem={setAutocompleteHoverItem}
													op={op}
													query={query}
												/>
											)}
											classNames={classNames({ 'p-invalid': submitted && errorMessages.assertedAllele })}
										/>
									</div>
									<div className={fieldDetailsColumnSize}>
										<FormErrorMessageComponent errorMessages={errorMessages} errorField={'assertedAllele'} />
										<FormErrorMessageComponent errorMessages={uiErrorMessages} errorField={'assertedAllele'} />
										<AssertedAlleleAdditionalFieldData fieldData={newAnnotation.assertedAllele} />
									</div>
								</div>
							</>
						)}

						<div className="grid">
							<div className={labelColumnSize}>
								<label htmlFor="relation">
									<font color={'red'}>*</font>Disease Relation
								</label>
							</div>
							<div className={widgetColumnSize}>
								<Dropdown
									options={relationsTerms}
									value={newAnnotation.relation}
									name="relation"
									optionLabel="name"
									onChange={onDropdownFieldChange}
									className={classNames({ 'p-invalid': submitted && errorMessages.relation })}
								/>
							</div>
							<div className={fieldDetailsColumnSize}>
								<FormErrorMessageComponent errorMessages={errorMessages} errorField={'relation'} />
								<FormErrorMessageComponent errorMessages={uiErrorMessages} errorField={'relation'} />
							</div>
						</div>

						{selectedFormFields?.includes('NOT') && (
							<>
								<div className="grid">
									<div className={labelColumnSize}>
										<label htmlFor="negated">NOT</label>
									</div>
									<div className={widgetColumnSize}>
										<NotEditor value={newAnnotation.negated} editorChange={onDropdownFieldChange} />
									</div>
									<div className={fieldDetailsColumnSize}>
										<FormErrorMessageComponent errorMessages={errorMessages} errorField={'negated'} />
									</div>
								</div>
							</>
						)}

						<div className="grid">
							<div className={labelColumnSize}>
								<label htmlFor="diseaseAnnotationObject">
									<font color={'red'}>*</font>Disease
								</label>
							</div>
							<div className={widgetColumnSize}>
								<AutocompleteFormEditor
									name="diseaseAnnotationObject"
									search={diseaseSearch}
									label="Disease"
									fieldName="diseaseAnnotationObject"
									initialValue={newAnnotation.diseaseAnnotationObject}
									onValueChangeHandler={onDiseaseChange}
									classNames={classNames({ 'p-invalid': submitted && errorMessages.diseaseAnnotationObject })}
								/>
							</div>
							<div className={fieldDetailsColumnSize}>
								<FormErrorMessageComponent errorMessages={errorMessages} errorField={'diseaseAnnotationObject'} />
								<FormErrorMessageComponent errorMessages={uiErrorMessages} errorField={'diseaseAnnotationObject'} />
								<DiseaseAdditionalFieldData fieldData={newAnnotation.diseaseAnnotationObject} />
							</div>
						</div>

						<div className="grid">
							<div className={labelColumnSize}>
								<label htmlFor="singleReference">
									<font color={'red'}>*</font>Reference
								</label>
							</div>
							<div className={widgetColumnSize}>
								<AutocompleteFormEditor
									search={referenceSearch}
									name="singleReference"
									label="Reference"
									fieldName="singleReference"
									initialValue={newAnnotation.singleReference}
									onValueChangeHandler={onSingleReferenceChange}
									valueDisplay={(item, setAutocompleteHoverItem, op, query) => (
										<LiteratureAutocompleteTemplate
											item={item}
											setAutocompleteHoverItem={setAutocompleteHoverItem}
											op={op}
											query={query}
										/>
									)}
									classNames={classNames({ 'p-invalid': submitted && errorMessages.singleReference })}
								/>
							</div>
							<div className={fieldDetailsColumnSize}>
								<FormErrorMessageComponent errorMessages={errorMessages} errorField={'singleReference'} />
								<FormErrorMessageComponent errorMessages={uiErrorMessages} errorField={'singleReference'} />
								<SingleReferenceAdditionalFieldData fieldData={newAnnotation.singleReference} />
							</div>
						</div>

						<div className="grid">
							<div className={labelColumnSize}>
								<label htmlFor="evidenceCodes">
									<font color={'red'}>*</font>Evidence Code
								</label>
							</div>
							<div className={widgetColumnSize}>
								<AutocompleteFormMultiEditor
									customRef={evidenceCodesRef}
									search={evidenceSearch}
									initialValue={newAnnotation.evidenceCodes}
									name="evidenceCodes"
									label="Evidence Code"
									fieldName="evidenceCodes"
									onValueChangeHandler={onArrayFieldChange}
									valueDisplay={(item, setAutocompleteHoverItem, op, query) => (
										<EvidenceAutocompleteTemplate
											item={item}
											setAutocompleteHoverItem={setAutocompleteHoverItem}
											op={op}
											query={query}
										/>
									)}
									classNames={classNames({ 'p-invalid': submitted && errorMessages.evidenceCodes })}
								/>
							</div>
							<div className={fieldDetailsColumnSize}>
								<FormErrorMessageComponent errorMessages={errorMessages} errorField={'evidenceCodes'} />
								<FormErrorMessageComponent errorMessages={uiErrorMessages} errorField={'evidenceCodes'} />
								<EvidenceCodesAdditionalFieldData fieldData={newAnnotation.evidenceCodes} />
							</div>
						</div>

						{selectedFormFields?.includes('With') && (
							<>
								<div className="grid">
									<div className={labelColumnSize}>
										<label htmlFor="with">With</label>
									</div>
									<div className={widgetColumnSize}>
										<AutocompleteFormMultiEditor
											customRef={withRef}
											search={withSearch}
											name="with"
											label="With"
											fieldName="with"
											subField="modEntityId"
											initialValue={newAnnotation.with}
											onValueChangeHandler={onArrayFieldChange}
											valueDisplay={(item, setAutocompleteHoverItem, op, query) => (
												<SubjectAutocompleteTemplate
													item={item}
													setAutocompleteHoverItem={setAutocompleteHoverItem}
													op={op}
													query={query}
												/>
											)}
											classNames={classNames({ 'p-invalid': submitted && errorMessages.with })}
										/>
									</div>
									<div className={fieldDetailsColumnSize}>
										<FormErrorMessageComponent errorMessages={errorMessages} errorField={'with'} />
										<WithAdditionalFieldData fieldData={newAnnotation.with} />
									</div>
								</div>
							</>
						)}

						{selectedFormFields?.includes('Related Notes') && (
							<>
								<div className="grid">
									<div className={labelColumnSize}>
										<label>Related Notes</label>
									</div>
									<div
										className={classNames('col-9', {
											'border-2 border-red-300':
												submitted && errorMessages.relatedNotes && Object.keys(relatedNotesErrorMessages).length === 0,
										})}
									>
										<RelatedNotesForm
											dispatch={newAnnotationDispatch}
											relatedNotes={newAnnotation.relatedNotes}
											showRelatedNotes={showRelatedNotes}
											errorMessages={relatedNotesErrorMessages}
											editingRows={relatedNotesEditingRows}
										/>
										<FormErrorMessageComponent errorMessages={errorMessages} errorField={'relatedNotes'} />
										<FormErrorMessageComponent errorMessages={uiErrorMessages} errorField={'relatedNotes'} />
									</div>
								</div>
							</>
						)}

						{selectedFormFields?.includes('Experimental Conditions') && (
							<>
								<div className="grid">
									<div className={labelColumnSize}>
										<label>Experimental Conditions</label>
									</div>
									<div className={classNames('col-9')}>
										<ConditionRelationsForm
											dispatch={newAnnotationDispatch}
											conditionRelations={newAnnotation.conditionRelations}
											showConditionRelations={showConditionRelations}
											errorMessages={exConErrorMessages}
											searchService={searchService}
											buttonIsDisabled={isConditionRelationButtonEnabled()}
											editingRows={conditionRelationsEditingRows}
										/>
									</div>
								</div>
							</>
						)}

						{(mod?.includes('ZFINStaff')
							? (required = requiredfield)
							: selectedFormFields?.includes('Experiments')) && (
							<>
								<div className="grid">
									<div className={labelColumnSize}>
										<label htmlFor="experiments">{required}Experiments</label>
									</div>
									<div className={widgetColumnSize}>
										<ConditionRelationHandleFormDropdown
											name="experiments"
											customRef={experimentsRef}
											editorChange={onDropdownExperimentsFieldChange}
											referenceCurie={newAnnotation.singleReference?.curie}
											value={newAnnotation.conditionRelations?.[0]?.handle}
											showClear={true}
											placeholderText={newAnnotation.conditionRelations?.[0]?.handle}
											isEnabled={isExperimentEnabled()}
										/>
									</div>
									<div className={fieldDetailsColumnSize}>
										<FormErrorMessageComponent errorMessages={errorMessages} errorField={'conditionRelations'} />
										<FormErrorMessageComponent errorMessages={uiErrorMessages} errorField={'conditionRelations'} />
									</div>
								</div>
							</>
						)}

						{selectedFormFields?.includes('Genetic Sex') && (
							<>
								<div className="grid">
									<div className={labelColumnSize}>
										<label htmlFor="geneticSex">Genetic Sex</label>
									</div>
									<div className={widgetColumnSize}>
										<ControlledVocabularyFormDropdown
											name="geneticSex"
											options={geneticSexTerms}
											editorChange={onDropdownFieldChange}
											value={newAnnotation.geneticSex}
											showClear={true}
											className={classNames({ 'p-invalid': submitted && errorMessages.geneticSex })}
										/>
									</div>
									<div className={fieldDetailsColumnSize}>
										<FormErrorMessageComponent errorMessages={errorMessages} errorField={'geneticSex'} />
									</div>
								</div>
							</>
						)}

						{selectedFormFields?.includes('Disease Qualifiers') && (
							<>
								<div className="grid">
									<div className={labelColumnSize}>
										<label htmlFor="diseaseQualifiers">Disease Qualifiers</label>
									</div>
									<div className={widgetColumnSize}>
										<ControlledVocabularyFormMultiSelectDropdown
											name="diseaseQualifiers"
											options={diseaseQualifiersTerms}
											editorChange={onControlledVocabChange}
											value={newAnnotation.diseaseQualifiers}
											className={classNames({ 'p-invalid': submitted && errorMessages.diseaseQualifiers })}
										/>
									</div>
									<div className={fieldDetailsColumnSize}>
										<FormErrorMessageComponent errorMessages={errorMessages} errorField={'diseaseQualifiers'} />
									</div>
								</div>
							</>
						)}

						{selectedFormFields?.includes('SGD Strain Background') && (
							<>
								<div className="grid">
									<div className={labelColumnSize}>
										<label htmlFor="sgdStrainBackground">SGD Strain Background</label>
									</div>
									<div className={widgetColumnSize}>
										<AutocompleteFormEditor
											initialValue={newAnnotation.sgdStrainBackground}
											search={sgdStrainBackgroundSearch}
											searchService={searchService}
											fieldName="sgdStrainBackground"
											name="sgdStrainBackground"
											label="SGD Strain Background"
											subField="modEntityId"
											valueDisplay={(item, setAutocompleteHoverItem, op, query) => (
												<SubjectAutocompleteTemplate
													item={item}
													setAutocompleteHoverItem={setAutocompleteHoverItem}
													op={op}
													query={query}
												/>
											)}
											onValueChangeHandler={onSingleReferenceChange}
											classNames={classNames({ 'p-invalid': submitted && errorMessages.sgdStrainBackground })}
										/>
									</div>
									<div className={fieldDetailsColumnSize}>
										<FormErrorMessageComponent errorMessages={errorMessages} errorField={'sgdStrainBackground'} />
										<FormErrorMessageComponent errorMessages={uiErrorMessages} errorField={'sgdStrainBackground'} />
										<SGDStrainBackgroundAdditionalFieldData fieldData={newAnnotation.sgdStrainBackground} />
									</div>
								</div>
							</>
						)}

						{selectedFormFields?.includes('Annotation Type') && (
							<>
								<div className="grid">
									<div className={labelColumnSize}>
										<label htmlFor="annotationType">Annotation Type</label>
									</div>
									<div className={widgetColumnSize}>
										<ControlledVocabularyFormDropdown
											name="annotationType"
											field="annotationType"
											options={annotationTypeTerms}
											editorChange={onDropdownFieldChange}
											showClear={true}
											value={newAnnotation.annotationType}
											className={classNames({ 'p-invalid': submitted && errorMessages.annotationType })}
										/>
									</div>
									<div className={fieldDetailsColumnSize}>
										<FormErrorMessageComponent errorMessages={errorMessages} errorField={'annotationType'} />
									</div>
								</div>
							</>
						)}

						{selectedFormFields?.includes('Genetic Modifier Relation') && (
							<>
								<div className="grid">
									<div className={labelColumnSize}>
										<label htmlFor="diseaseGeneticModifierRelation">Genetic Modifier Relation</label>
									</div>
									<div className={widgetColumnSize}>
										<ControlledVocabularyFormDropdown
											name="diseaseGeneticModifierRelation"
											field="diseaseGeneticModifierRelation"
											options={geneticModifierRelationTerms}
											editorChange={onDropdownFieldChange}
											showClear={true}
											value={newAnnotation.diseaseGeneticModifierRelation}
											className={classNames({ 'p-invalid': submitted && errorMessages.diseaseGeneticModifierRelation })}
										/>
									</div>
									<div className={fieldDetailsColumnSize}>
										<FormErrorMessageComponent
											errorMessages={errorMessages}
											errorField={'diseaseGeneticModifierRelation'}
										/>
										<FormErrorMessageComponent
											errorMessages={uiErrorMessages}
											errorField={'diseaseGeneticModifierRelation'}
										/>
									</div>
								</div>
							</>
						)}

						{selectedFormFields?.includes('Genetic Modifier AGMs') && (
							<>
								<div className="grid">
									<div className={labelColumnSize}>
										<label htmlFor="diseaseGeneticModifierAgm">Genetic Modifier AGMs</label>
									</div>
									<div className={widgetColumnSize}>
										<AutocompleteFormMultiEditor
											search={geneticModifierAgmsSearch}
											initialValue={newAnnotation.diseaseGeneticModifierAgms}
											fieldName="diseaseGeneticModifierAgms"
											name="diseaseGeneticModifierAgms"
											subField="modEntityId"
											valueDisplay={(item, setAutocompleteHoverItem, op, query) => (
												<SubjectAutocompleteTemplate
													item={item}
													setAutocompleteHoverItem={setAutocompleteHoverItem}
													op={op}
													query={query}
												/>
											)}
											onValueChangeHandler={onArrayFieldChange}
											classNames={classNames({ 'p-invalid': submitted && errorMessages.diseaseGeneticModifierAgms })}
										/>
									</div>
									<div className={fieldDetailsColumnSize}>
										<FormErrorMessageComponent
											errorMessages={errorMessages}
											errorField={'diseaseGeneticModifierAgms'}
										/>
										<GeneticModifierAgmsAdditionalFieldData fieldData={newAnnotation.diseaseGeneticModifierAgms} />
									</div>
								</div>
							</>
						)}

						{selectedFormFields?.includes('Genetic Modifier Alleles') && (
							<>
								<div className="grid">
									<div className={labelColumnSize}>
										<label htmlFor="diseaseGeneticModifierAllele">Genetic Modifier Alleles</label>
									</div>
									<div className={widgetColumnSize}>
										<AutocompleteFormMultiEditor
											search={geneticModifierAllelesSearch}
											initialValue={newAnnotation.diseaseGeneticModifierAlleles}
											fieldName="diseaseGeneticModifierAlleles"
											name="diseaseGeneticModifierAlleles"
											subField="modEntityId"
											valueDisplay={(item, setAutocompleteHoverItem, op, query) => (
												<SubjectAutocompleteTemplate
													item={item}
													setAutocompleteHoverItem={setAutocompleteHoverItem}
													op={op}
													query={query}
												/>
											)}
											onValueChangeHandler={onArrayFieldChange}
											classNames={classNames({ 'p-invalid': submitted && errorMessages.diseaseGeneticModifierAlleles })}
										/>
									</div>
									<div className={fieldDetailsColumnSize}>
										<FormErrorMessageComponent
											errorMessages={errorMessages}
											errorField={'diseaseGeneticModifierAlleles'}
										/>
										<GeneticModifierAllelesAdditionalFieldData
											fieldData={newAnnotation.diseaseGeneticModifierAlleles}
										/>
									</div>
								</div>
							</>
						)}

						{selectedFormFields?.includes('Genetic Modifier Genes') && (
							<>
								<div className="grid">
									<div className={labelColumnSize}>
										<label htmlFor="diseaseGeneticModifierGene">Genetic Modifier Genes</label>
									</div>
									<div className={widgetColumnSize}>
										<AutocompleteFormMultiEditor
											search={geneticModifierGenesSearch}
											initialValue={newAnnotation.diseaseGeneticModifierGenes}
											fieldName="diseaseGeneticModifierGenes"
											name="diseaseGeneticModifierGenes"
											subField="modEntityId"
											valueDisplay={(item, setAutocompleteHoverItem, op, query) => (
												<SubjectAutocompleteTemplate
													item={item}
													setAutocompleteHoverItem={setAutocompleteHoverItem}
													op={op}
													query={query}
												/>
											)}
											onValueChangeHandler={onArrayFieldChange}
											classNames={classNames({ 'p-invalid': submitted && errorMessages.diseaseGeneticModifierGenes })}
										/>
									</div>
									<div className={fieldDetailsColumnSize}>
										<FormErrorMessageComponent
											errorMessages={errorMessages}
											errorField={'diseaseGeneticModifierGenes'}
										/>
										<GeneticModifierGenesAdditionalFieldData fieldData={newAnnotation.diseaseGeneticModifierGenes} />
									</div>
								</div>
							</>
						)}

						{selectedFormFields?.includes('Internal') && (
							<>
								<div className="grid">
									<div className={labelColumnSize}>
										<label htmlFor="internal">
											<font color={'red'}>*</font>Internal
										</label>
									</div>
									<div className={widgetColumnSize}>
										<Dropdown
											name="internal"
											value={newAnnotation.internal}
											options={booleanTerms}
											optionLabel="text"
											optionValue="name"
											onChange={onDropdownFieldChange}
											className={classNames({ 'p-invalid': submitted && errorMessages.internal })}
										/>
									</div>
									<div className={fieldDetailsColumnSize}>
										<FormErrorMessageComponent errorMessages={errorMessages} errorField={'internal'} />
									</div>
								</div>
							</>
						)}
					</form>
				</ErrorBoundary>
			</Dialog>
		</div>
	);
};
