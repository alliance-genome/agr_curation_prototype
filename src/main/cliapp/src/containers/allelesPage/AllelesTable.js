import React, { useRef, useState } from 'react';
import { useMutation } from 'react-query';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { EllipsisTableCell } from '../../components/EllipsisTableCell';
import { ListTableCell } from '../../components/ListTableCell';
import { internalTemplate, obsoleteTemplate } from '../../components/AuditedObjectComponent';
import { TrueFalseDropdown } from '../../components/TrueFalseDropDownSelector';
import { ErrorMessageComponent } from '../../components/ErrorMessageComponent';
import { useControlledVocabularyService } from '../../service/useControlledVocabularyService';
import { AlleleService } from '../../service/AlleleService';
import { SearchService } from '../../service/SearchService';
import { MutationTypesDialog } from './MutationTypesDialog';
import { InheritanceModesDialog } from './InheritanceModesDialog';
import { SymbolDialog } from './SymbolDialog';
import { FullNameDialog } from './FullNameDialog';
import { SecondaryIdsDialog } from './SecondaryIdsDialog';
import { AutocompleteEditor } from '../../components/Autocomplete/AutocompleteEditor';
import { LiteratureAutocompleteTemplate } from '../../components/Autocomplete/LiteratureAutocompleteTemplate';
import { VocabTermAutocompleteTemplate } from '../../components/Autocomplete/VocabTermAutocompleteTemplate';

import { Tooltip } from 'primereact/tooltip';
import { Toast } from 'primereact/toast';
import { Button } from 'primereact/button';
import { EditMessageTooltip } from '../../components/EditMessageTooltip';
import { defaultAutocompleteOnChange, autocompleteSearch, buildAutocompleteFilter, getRefStrings, multipleAutocompleteOnChange } from '../../utils/utils';
import { AutocompleteMultiEditor } from "../../components/Autocomplete/AutocompleteMultiEditor";
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_CONFIGS } from '../../constants/FilterFields';

export const AllelesTable = () => {

	const [isEnabled, setIsEnabled] = useState(true);
	const [errorMessages, setErrorMessages] = useState({});
	const errorMessagesRef = useRef();
	errorMessagesRef.current = errorMessages;

	const [symbolData, setSymbolData] = useState({
		displayText: "",
		isInEdit: false,
		dialog: false,
		rowIndex: null,
		mainRowProps: {},
	});
	
	const [fullNameData, setFullNameData] = useState({
		displayText: "",
		isInEdit: false,
		dialog: false,
		rowIndex: null,
		mainRowProps: {},
	});

	const [mutationTypesData, setMutationTypesData] = useState({
		mutationTypes: [],
		isInEdit: false,
		dialog: false,
		rowIndex: null,
		mainRowProps: {},
	});

	const [inheritanceModesData, setInheritanceModesData] = useState({
		inheritanceModes: {},
		isInEdit: false,
		dialog: false,
		rowIndex: null,
		mainRowProps: {},
	});

	const [secondaryIdsData, setSecondaryIdsData] = useState({
		secondaryId: "",
		isInEdit: false,
		dialog: false,
		rowIndex: null,
		mainRowProps: {},
	});

	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);

	const booleanTerms = useControlledVocabularyService('generic_boolean_terms');
	const searchService = new SearchService();
	let alleleService = new AlleleService();

	const mutation = useMutation(updatedAllele => {
		if (!alleleService) {
			alleleService = new AlleleService();
		}
		return alleleService.saveAllele(updatedAllele);
	});

	const taxonTemplate = (rowData) => {
		if (rowData?.taxon) {
			return (
				<>
					<EllipsisTableCell otherClasses={`${"TAXON_NAME_"}${rowData.curie.replace(':', '')}${rowData.taxon.curie.replace(':', '')}`}>
						{rowData.taxon.name} ({rowData.taxon.curie})
					</EllipsisTableCell>
					<Tooltip target={`.${"TAXON_NAME_"}${rowData.curie.replace(':', '')}${rowData.taxon.curie.replace(':', '')}`} content= {`${rowData.taxon.name} (${rowData.taxon.curie})`} style={{ width: '250px', maxWidth: '450px' }}/>
				</>
			);
		}
	}

	const onInCollectionValueChange = (event, setFieldValue, props) => {
		defaultAutocompleteOnChange(props, event, "inCollection", setFieldValue, "name");
	};

	const inCollectionEditor = (props) => {
		return (
			<>
				<AutocompleteEditor
					search={inCollectionSearch}
					initialValue={props.rowData.inCollection?.name}
					rowProps={props}
					fieldName='inCollection'
					onValueChangeHandler={onInCollectionValueChange}
					valueDisplay={(item, setAutocompleteSelectedItem, op, query) =>
						<VocabTermAutocompleteTemplate item={item} op={op} query={query} setAutocompleteSelectedItem={setAutocompleteSelectedItem}/>}
				/>
				<ErrorMessageComponent
					errorMessages={errorMessagesRef.current[props.rowIndex]}
					errorField='inCollection'
				/>
			</>
		);
	};

	const isExtinctTemplate = (rowData) => {
		if (rowData && rowData.isExtinct !== null && rowData.isExtinct !== undefined) {
			return <EllipsisTableCell>{JSON.stringify(rowData.isExtinct)}</EllipsisTableCell>;
		}
	};

	const referencesTemplate = (rowData) => {
		if (rowData && rowData.references && rowData.references.length > 0) {
			const refStrings = getRefStrings(rowData.references);
			const listTemplate = (item) => {
				return (
					<EllipsisTableCell>
						{item}
					</EllipsisTableCell>
				);
			};
			return (
				<>
					<div className={`${rowData.curie.replace(':','')}${rowData.references[0].curie.replace(':', '')}`}>
						<ListTableCell template={listTemplate} listData={refStrings}/>
					</div>
					<Tooltip target={`.${rowData.curie.replace(':','')}${rowData.references[0].curie.replace(':', '')}`} style={{ width: '450px', maxWidth: '450px' }} position='left'>
						<ListTableCell template={listTemplate} listData={refStrings}/>
					</Tooltip>
				</>
			);

		}
	};

	const inCollectionSearch = (event, setFiltered, setQuery) => {
		const autocompleteFields = ["name"];
		const endpoint = "vocabularyterm";
		const filterName = "taxonFilter";
		const otherFilters = {
			vocabularyFilter: {
				"vocabulary.name": {
					queryString: "Allele collection vocabulary"
				}
			}
		}
		setQuery(event.query);
		const filter = buildAutocompleteFilter(event, autocompleteFields);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered, otherFilters);
	}

	const onReferenceValueChange = (event, setFieldValue, props) => {
		multipleAutocompleteOnChange(props, event, "references", setFieldValue);
	};

	const referenceSearch = (event, setFiltered, setInputValue) => {
		const autocompleteFields = ["curie", "cross_references.curie"];
		const endpoint = "literature-reference";
		const filterName = "curieFilter";
		const filter = buildAutocompleteFilter(event, autocompleteFields);

		setInputValue(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
	}

	const referencesEditor = (props) => {
		return (
			<>
				<AutocompleteMultiEditor
					search={referenceSearch}
					initialValue={props.rowData.references}
					rowProps={props}
					fieldName='references'
					valueDisplay={(item, setAutocompleteHoverItem, op, query) =>
						<LiteratureAutocompleteTemplate item={item} setAutocompleteHoverItem={setAutocompleteHoverItem} op={op} query={query}/>}
					onValueChangeHandler={onReferenceValueChange}
				/>
				<ErrorMessageComponent
					errorMessages={errorMessagesRef.current[props.rowIndex]}
					errorField={"references"}
				/>
			</>
		);
	};

	const onTaxonValueChange = (event, setFieldValue, props) => {
		defaultAutocompleteOnChange(props, event, "taxon", setFieldValue);
	};

	const taxonSearch = (event, setFiltered, setQuery) => {
		const autocompleteFields = ["curie", "name", "crossReferences.referencedCurie", "secondaryIdentifiers", "synonyms.name"];
		const endpoint = "ncbitaxonterm";
		const filterName = "taxonFilter";
		setQuery(event.query);
		const filter = buildAutocompleteFilter(event, autocompleteFields);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
	}

	const taxonEditor = (props) => {
		return (
			<>
				<AutocompleteEditor
					search={taxonSearch}
					initialValue={props.rowData.taxon?.curie}
					rowProps={props}
					fieldName='taxon'
					onValueChangeHandler={onTaxonValueChange}
				/>
				<ErrorMessageComponent
					errorMessages={errorMessagesRef.current[props.rowIndex]}
					errorField='taxon'
				/>
			</>
		);
	};

	const onInternalEditorValueChange = (props, event) => {
		let updatedAlleles = [...props.props.value];
		if (event.value || event.value === '') {
			updatedAlleles[props.rowIndex].internal = JSON.parse(event.value.name);
		}
	};

	const internalEditor = (props) => {
		return (
			<>
				<TrueFalseDropdown
					options={booleanTerms}
					editorChange={onInternalEditorValueChange}
					props={props}
					field={"internal"}
				/>
				<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField={"internal"} />
			</>
		);
	};

	const onObsoleteEditorValueChange = (props, event) => {
		let updatedAlleles = [...props.props.value];
		if (event.value || event.value === '') {
			updatedAlleles[props.rowIndex].obsolete = JSON.parse(event.value.name);
		}
	};

	const obsoleteEditor = (props) => {
		return (
			<>
				<TrueFalseDropdown
					options={booleanTerms}
					editorChange={onObsoleteEditorValueChange}
					props={props}
					field={"obsolete"}
				/>
				<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField={"obsolete"} />
			</>
		);
	};

	const onIsExtinctEditorValueChange = (props, event) => {
		let updatedAlleles = [...props.props.value];

		if (event.value && event.value !== '') {
			updatedAlleles[props.rowIndex].isExtinct = JSON.parse(event.value.name);
		} else {
			updatedAlleles[props.rowIndex].isExtinct = null;
		}
	};

	const isExtinctEditor = (props) => {
		return (
			<>
				<TrueFalseDropdown
					options={booleanTerms}
					editorChange={onIsExtinctEditorValueChange}
					props={props}
					field={"isExtinct"}
					showClear={true}
				/>
				<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField={"isExtinct"} />
			</>
		);
	};

	const symbolTemplate = (rowData) => {
		return (
			<>
				<Button className="p-button-text"
					onClick={(event) => { handleSymbolOpen(event, rowData, false) }} >						<div className='overflow-hidden text-overflow-ellipsis' dangerouslySetInnerHTML={{ __html: rowData.alleleSymbol.displayText }} />								
				</Button>
			</>
		);
	};

	const symbolEditor = (props) => {
		return (
			<>
			<div>
				<Button className="p-button-text"
					onClick={(event) => { handleSymbolOpenInEdit(event, props, true) }} >
					<span style={{ textDecoration: 'underline' }}>
						{<div className='overflow-hidden text-overflow-ellipsis' dangerouslySetInnerHTML={{ __html: props.rowData.alleleSymbol.displayText }} />}
						<i className="pi pi-user-edit" style={{ 'fontSize': '1em' }}></i>
					</span>&nbsp;&nbsp;&nbsp;&nbsp;
					<EditMessageTooltip/>
				</Button>
			</div>
			<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField={"alleleSymbol"} style={{ 'fontSize': '1em' }}/>
			</>
		)
	};

	const handleSymbolOpen = (event, rowData, isInEdit) => {
		let _symbolData = {};
		_symbolData["originalSymbols"] = [rowData.alleleSymbol];
		_symbolData["dialog"] = true;
		_symbolData["isInEdit"] = isInEdit;
		setSymbolData(() => ({
			..._symbolData
		}));
	};

	const handleSymbolOpenInEdit = (event, rowProps, isInEdit) => {
		const { rows } = rowProps.props;
		const { rowIndex } = rowProps;
		const index = rowIndex % rows;
		let _symbolData = {};
		_symbolData["originalSymbols"] = [rowProps.rowData.alleleSymbol];
		_symbolData["dialog"] = true;
		_symbolData["isInEdit"] = isInEdit;
		_symbolData["rowIndex"] = index;
		_symbolData["mainRowProps"] = rowProps;
		setSymbolData(() => ({
			..._symbolData
		}));
	};
	
	const fullNameTemplate = (rowData) => {
		if (rowData?.alleleFullName) {
			return (
				<>
					<Button className="p-button-text"
						onClick={(event) => { handleFullNameOpen(event, rowData, false) }} >
						<div className='overflow-hidden text-overflow-ellipsis' dangerouslySetInnerHTML={{ __html: rowData.alleleFullName.displayText }} />								
					</Button>
				</>
			);
		}
	};

	const fullNameEditor = (props) => {
		if (props?.rowData?.alleleFullName) {
			return (
				<>
				<div>
					<Button className="p-button-text"
						onClick={(event) => { handleFullNameOpenInEdit(event, props, true) }} >
						<span style={{ textDecoration: 'underline' }}>
							{<div className='overflow-hidden text-overflow-ellipsis' dangerouslySetInnerHTML={{ __html: props.rowData.alleleFullName.displayText }} />}
							<i className="pi pi-user-edit" style={{ 'fontSize': '1em' }}></i>
						</span>&nbsp;&nbsp;&nbsp;&nbsp;
						<EditMessageTooltip/>
					</Button>
				</div>
				<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField={"alleleFullName"} style={{ 'fontSize': '1em' }}/>
				</>
			)
		} else {
			return (
				<>
					<div>
						<Button className="p-button-text"
							onClick={(event) => { handleFullNameOpenInEdit(event, props, true) }} >
							<span style={{ textDecoration: 'underline' }}>
								Add Full Name
								<i className="pi pi-user-edit" style={{ 'fontSize': '1em' }}></i>
							</span>&nbsp;&nbsp;&nbsp;&nbsp;
							<Tooltip target=".exclamation-icon" style={{ width: '250px', maxWidth: '250px',	 }}/>
							<EditMessageTooltip/>
						</Button>
					</div>
					<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField={"alleleFullName"} style={{ 'fontSize': '1em' }}/>
				</>
			)
		}
	};

	const handleFullNameOpen = (event, rowData, isInEdit) => {
		let _fullNameData = {};
		_fullNameData["originalFullNames"] = [rowData.alleleFullName];
		_fullNameData["dialog"] = true;
		_fullNameData["isInEdit"] = isInEdit;
		setFullNameData(() => ({
			..._fullNameData
		}));
	};

	const handleFullNameOpenInEdit = (event, rowProps, isInEdit) => {
		const { rows } = rowProps.props;
		const { rowIndex } = rowProps;
		const index = rowIndex % rows;
		let _fullNameData = {};
		_fullNameData["originalFullNames"] = [rowProps.rowData.alleleFullName];
		_fullNameData["dialog"] = true;
		_fullNameData["isInEdit"] = isInEdit;
		_fullNameData["rowIndex"] = index;
		_fullNameData["mainRowProps"] = rowProps;
		setFullNameData(() => ({
			..._fullNameData
		}));
	};

	const inheritanceModesTemplate = (rowData) => {
		if (rowData?.alleleInheritanceModes) {
			const inheritanceModeSet = new Set();
			for(var i = 0; i < rowData.alleleInheritanceModes.length; i++){
				if (rowData.alleleInheritanceModes[i].inheritanceMode) {
					inheritanceModeSet.add(rowData.alleleInheritanceModes[i].inheritanceMode.name);
				}
			}
			if (inheritanceModeSet.size > 0) {
				const sortedInheritanceModes = Array.from(inheritanceModeSet).sort();
				const listTemplate = (item) => {
					return (
						<span style={{ textDecoration: 'underline' }}>
							{item && item}
						</span>
					);
				};
				return (
					<>
						<Button className="p-button-text"
							onClick={(event) => { handleInheritanceModesOpen(event, rowData, false) }} >
							<ListTableCell template={listTemplate} listData={sortedInheritanceModes}/>
						</Button>
					</>
				);
			}
		}
	};

	const inheritanceModesEditor = (props) => {
		if (props?.rowData?.alleleInheritanceModes) {
			return (
				<>
				<div>
					<Button className="p-button-text"
						onClick={(event) => { handleInheritanceModesOpenInEdit(event, props, true) }} >
						<span style={{ textDecoration: 'underline' }}>
							{`Inheritance Modes(${props.rowData.alleleInheritanceModes.length}) `}
							<i className="pi pi-user-edit" style={{ 'fontSize': '1em' }}></i>
						</span>&nbsp;&nbsp;&nbsp;&nbsp;
						<EditMessageTooltip/>
					</Button>
				</div>
				<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField={"alleleInheritanceModes"} style={{ 'fontSize': '1em' }}/>
				</>
			)
		} else {
			return (
				<>
					<div>
						<Button className="p-button-text"
							onClick={(event) => { handleInheritanceModesOpenInEdit(event, props, true) }} >
							<span style={{ textDecoration: 'underline' }}>
								Add Inheritance Mode
								<i className="pi pi-user-edit" style={{ 'fontSize': '1em' }}></i>
							</span>&nbsp;&nbsp;&nbsp;&nbsp;
							<Tooltip target=".exclamation-icon" style={{ width: '250px', maxWidth: '250px',	 }}/>
							<EditMessageTooltip/>
						</Button>
					</div>
					<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField={"alleleInheritanceModes"} style={{ 'fontSize': '1em' }}/>
				</>
			)
		}
	};

	const handleInheritanceModesOpen = (event, rowData, isInEdit) => {
		let _inheritanceModesData = {};
		_inheritanceModesData["originalInheritanceModes"] = rowData.alleleInheritanceModes;
		_inheritanceModesData["dialog"] = true;
		_inheritanceModesData["isInEdit"] = isInEdit;
		setInheritanceModesData(() => ({
			..._inheritanceModesData
		}));
	};

	const handleInheritanceModesOpenInEdit = (event, rowProps, isInEdit) => {
		const { rows } = rowProps.props;
		const { rowIndex } = rowProps;
		const index = rowIndex % rows;
		let _inheritanceModesData = {};
		_inheritanceModesData["originalInheritanceModes"] = rowProps.rowData.alleleInheritanceModes;
		_inheritanceModesData["dialog"] = true;
		_inheritanceModesData["isInEdit"] = isInEdit;
		_inheritanceModesData["rowIndex"] = index;
		_inheritanceModesData["mainRowProps"] = rowProps;
		setInheritanceModesData(() => ({
			..._inheritanceModesData
		}));
	};

	const mutationTypesTemplate = (rowData) => {
		if (rowData?.alleleMutationTypes) {
			const mutationTypeSet = new Set();
			for(var i = 0; i < rowData.alleleMutationTypes.length; i++){
				if (rowData.alleleMutationTypes[i].mutationTypes) {
					for(var j = 0; j < rowData.alleleMutationTypes[i].mutationTypes.length; j++) {
						let mtString = rowData.alleleMutationTypes[i].mutationTypes[j].name + ' (' +
							rowData.alleleMutationTypes[i].mutationTypes[j].curie + ')';
						mutationTypeSet.add(mtString);
					}
				}
			}
			if (mutationTypeSet.size > 0) {
				const sortedMutationTypes = Array.from(mutationTypeSet).sort();
				const listTemplate = (item) => {
					return (
						<span style={{ textDecoration: 'underline' }}>
							{item && item}
						</span>
					);
				};
				return (
					<>
						<Button className="p-button-text"
							onClick={(event) => { handleMutationTypesOpen(event, rowData, false) }} >
							<ListTableCell template={listTemplate} listData={sortedMutationTypes}/>
						</Button>
					</>
				);
			}
		}
	};

	const mutationTypesEditor = (props) => {
		if (props?.rowData?.alleleMutationTypes) {
			return (
				<>
				<div>
					<Button className="p-button-text"
						onClick={(event) => { handleMutationTypesOpenInEdit(event, props, true) }} >
						<span style={{ textDecoration: 'underline' }}>
							{`Mutation Types(${props.rowData.alleleMutationTypes.length}) `}
							<i className="pi pi-user-edit" style={{ 'fontSize': '1em' }}></i>
						</span>&nbsp;&nbsp;&nbsp;&nbsp;
						<EditMessageTooltip/>
					</Button>
				</div>
				<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField={"alleleMutationTypes"} style={{ 'fontSize': '1em' }}/>
				</>
			)
		} else {
			return (
				<>
					<div>
						<Button className="p-button-text"
							onClick={(event) => { handleMutationTypesOpenInEdit(event, props, true) }} >
							<span style={{ textDecoration: 'underline' }}>
								Add Mutation Type
								<i className="pi pi-user-edit" style={{ 'fontSize': '1em' }}></i>
							</span>&nbsp;&nbsp;&nbsp;&nbsp;
							<Tooltip target=".exclamation-icon" style={{ width: '250px', maxWidth: '250px',	 }}/>
							<EditMessageTooltip/>
						</Button>
					</div>
					<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField={"alleleMutationTypes"} style={{ 'fontSize': '1em' }}/>
				</>
			)
		}
	};

	const handleMutationTypesOpen = (event, rowData, isInEdit) => {
		let _mutationTypesData = {};
		_mutationTypesData["originalMutationTypes"] = rowData.alleleMutationTypes;
		_mutationTypesData["dialog"] = true;
		_mutationTypesData["isInEdit"] = isInEdit;
		setMutationTypesData(() => ({
			..._mutationTypesData
		}));
	};

	const handleMutationTypesOpenInEdit = (event, rowProps, isInEdit) => {
		const { rows } = rowProps.props;
		const { rowIndex } = rowProps;
		const index = rowIndex % rows;
		let _mutationTypesData = {};
		_mutationTypesData["originalMutationTypes"] = rowProps.rowData.alleleMutationTypes;
		_mutationTypesData["dialog"] = true;
		_mutationTypesData["isInEdit"] = isInEdit;
		_mutationTypesData["rowIndex"] = index;
		_mutationTypesData["mainRowProps"] = rowProps;
		setMutationTypesData(() => ({
			..._mutationTypesData
		}));
	};

	const secondaryIdsTemplate = (rowData) => {
		if (rowData?.alleleSecondaryIds) {
			const listTemplate = (item) => {
				return (
					<span style={{ textDecoration: 'underline' }}>
						{item && item}
					</span>
				);
			};
			return (
				<>
					<Button className="p-button-text"
							onClick={(event) => { handleSecondaryIdsOpen(event, rowData, false) }} >
						<ListTableCell template={listTemplate} listData={rowData.alleleSecondaryIds.map(a => a.secondaryId).sort()}/>
					</Button>
				</>
			);
		}
	};

	const secondaryIdsEditor = (props) => {
		if (props?.rowData?.alleleSecondaryIds) {
			return (
				<>
				<div>
					<Button className="p-button-text"
						onClick={(event) => { handleSecondaryIdsOpenInEdit(event, props, true) }} >
						<span style={{ textDecoration: 'underline' }}>
							{`Secondary IDs(${props.rowData.alleleSecondaryIds.length}) `}
							<i className="pi pi-user-edit" style={{ 'fontSize': '1em' }}></i>
						</span>&nbsp;&nbsp;&nbsp;&nbsp;
						<EditMessageTooltip/>
					</Button>
				</div>
					<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField={"alleleSecondaryIds"} style={{ 'fontSize': '1em' }}/>
				</>
			)
		} else {
			return (
				<>
					<div>
						<Button className="p-button-text"
							onClick={(event) => { handleSecondaryIdsOpenInEdit(event, props, true) }} >
							<span style={{ textDecoration: 'underline' }}>
								Add Secondary ID
								<i className="pi pi-user-edit" style={{ 'fontSize': '1em' }}></i>
							</span>&nbsp;&nbsp;&nbsp;&nbsp;
							<EditMessageTooltip/>
						</Button>
					</div>
					<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField={"alleleSecondaryIds"} style={{ 'fontSize': '1em' }}/>
				</>
			)
		}
	};



	const handleSecondaryIdsOpen = (event, rowData, isInEdit) => {
		let _secondaryIdsData = {};
		_secondaryIdsData["originalSecondaryIds"] = rowData.alleleSecondaryIds;
		_secondaryIdsData["dialog"] = true;
		_secondaryIdsData["isInEdit"] = isInEdit;
		setSecondaryIdsData(() => ({
			..._secondaryIdsData
		}));
	};

	const handleSecondaryIdsOpenInEdit = (event, rowProps, isInEdit) => {
		const { rows } = rowProps.props;
		const { rowIndex } = rowProps;
		const index = rowIndex % rows;
		let _secondaryIdsData = {};
		_secondaryIdsData["originalSecondaryIds"] = rowProps.rowData.alleleSecondaryIds;
		_secondaryIdsData["dialog"] = true;
		_secondaryIdsData["isInEdit"] = isInEdit;
		_secondaryIdsData["rowIndex"] = index;
		_secondaryIdsData["mainRowProps"] = rowProps;
		setSecondaryIdsData(() => ({
			..._secondaryIdsData
		}));
	};

	const columns = [
		{
			field: "curie",
			header: "Curie",
			sortable: { isEnabled },
			filterConfig: FILTER_CONFIGS.curieFilterConfig,
		},
		{
			field: "alleleFullName.displayText",
			header: "Name",
			body: fullNameTemplate,
			editor: (props) => fullNameEditor(props),
			sortable: isEnabled,
			filterConfig: FILTER_CONFIGS.alleleNameFilterConfig,
		},
		{
			field: "alleleSymbol.displayText",
			header: "Symbol",
			body: symbolTemplate,
			editor: (props) => symbolEditor(props),
			sortable: isEnabled,
			filterConfig: FILTER_CONFIGS.alleleSymbolFilterConfig,
		},
		{
			field: "alleleSecondaryIds.secondaryId",
			header: "Secondary IDs",
			body: secondaryIdsTemplate,
			editor: (props) => secondaryIdsEditor(props),
			sortable: isEnabled,
			filterConfig: FILTER_CONFIGS.secondaryIdsFilterConfig,
		},
		{
			field: "taxon.name",
			header: "Taxon",
			body: taxonTemplate,
			sortable: isEnabled,
			filterConfig: FILTER_CONFIGS.taxonFilterConfig,
			editor: (props) => taxonEditor(props)
		},
		{
			field: "alleleMutationTypes.mutationTypes.name",
			header: "Mutation Types",
			body: mutationTypesTemplate,
			editor: (props) => mutationTypesEditor(props),
			sortable: isEnabled,
			filterConfig: FILTER_CONFIGS.alleleMutationFilterConfig,
		},
		{
			field: "references.curie",
			header: "References",
			body: referencesTemplate,
			sortable: isEnabled,
			filterConfig: FILTER_CONFIGS.referencesFilterConfig,
			editor: (props) => referencesEditor(props)
		},
		{
			field: "alleleInheritanceModes.inheritanceMode.name",
			header: "Inheritance Modes",
			body: inheritanceModesTemplate,
			sortable: isEnabled,
			filterConfig: FILTER_CONFIGS.alleleInheritanceModesFilterConfig,
			editor: (props) => inheritanceModesEditor(props),
		},
		{
			field: "inCollection.name",
			header: "In Collection",
			sortable: isEnabled,
			filterConfig: FILTER_CONFIGS.inCollectionFilterConfig,
			editor: (props) => inCollectionEditor(props)
		},
		{
			field: "isExtinct",
			header: "Is Extinct",
			body: isExtinctTemplate,
			filterConfig: FILTER_CONFIGS.isExtinctFilterConfig,
			sortable: isEnabled,
			editor: (props) => isExtinctEditor(props)
		},{
			field: "updatedBy.uniqueId",
			header: "Updated By",
			sortable: isEnabled,
			filterConfig: FILTER_CONFIGS.updatedByFilterConfig,
		},
		{
			field: "dateUpdated",
			header: "Date Updated",
			sortable: isEnabled,
			filter: true,
			filterConfig: FILTER_CONFIGS.dateUpdatedFilterConfig
		},
		{
			field: "createdBy.uniqueId",
			header: "Created By",
			sortable: isEnabled,
			filter: true,
			filterConfig: FILTER_CONFIGS.createdByFilterConfig
		},
		{
			field: "dateCreated",
			header: "Date Created",
			sortable: isEnabled,
			filter: true,
			filterConfig: FILTER_CONFIGS.dataCreatedFilterConfig
		},
		{
			field: "internal",
			header: "Internal",
			body: internalTemplate,
			filter: true,
			filterConfig: FILTER_CONFIGS.internalFilterConfig,
			sortable: isEnabled,
			editor: (props) => internalEditor(props)
		},
		{
			field: "obsolete",
			header: "Obsolete",
			body: obsoleteTemplate,
			filter: true,
			filterConfig: FILTER_CONFIGS.obsoleteFilterConfig,
			sortable: isEnabled,
			editor: (props) => obsoleteEditor(props)
		}
	];

	const defaultColumnNames = columns.map((col) => {
		return col.header;
	});


	const initialTableState = getDefaultTableState("Alleles", defaultColumnNames);

	return (
		<>
			<div className="card">
				<Toast ref={toast_topleft} position="top-left" />
				<Toast ref={toast_topright} position="top-right" />
				<GenericDataTable
					dataKey="curie"
					endpoint="allele"
					tableName="Alleles"
					columns={columns}
					defaultColumnNames={defaultColumnNames}
					initialTableState={initialTableState}
					isEditable={true}
					mutation={mutation}
					isEnabled={isEnabled}
					setIsEnabled={setIsEnabled}
					toasts={{toast_topleft, toast_topright }}
					initialColumnWidth={10}
					errorObject = {{errorMessages, setErrorMessages}}
				/>
			</div>
			<SymbolDialog
				originalSymbolData={symbolData}
				setOriginalSymbolData={setSymbolData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
			/>
			<FullNameDialog
				originalFullNameData={fullNameData}
				setOriginalFullNameData={setFullNameData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
			/>
			<MutationTypesDialog
				originalMutationTypesData={mutationTypesData}
				setOriginalMutationTypesData={setMutationTypesData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
			/>
			<InheritanceModesDialog
				originalInheritanceModesData={inheritanceModesData}
				setOriginalInheritanceModesData={setInheritanceModesData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
			/>
			<SecondaryIdsDialog
				originalSecondaryIdsData={secondaryIdsData}
				setOriginalSecondaryIdsData={setSecondaryIdsData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
			/>
		</>
	);
};
