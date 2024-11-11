import React, { useRef, useState } from 'react';
import { useMutation } from '@tanstack/react-query';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { ErrorMessageComponent } from '../../components/Error/ErrorMessageComponent';
import { AlleleService } from '../../service/AlleleService';
import { SearchService } from '../../service/SearchService';
import { useGetTableData } from '../../service/useGetTableData';
import { useGetUserSettings } from '../../service/useGetUserSettings';
import { MutationTypesDialog } from './mutationTypes/MutationTypesDialog';
import { FunctionalImpactsDialog } from './functionalImpacts/FunctionalImpactsDialog';
import { InheritanceModesDialog } from './inheritanceModes/InheritanceModesDialog';
import { NomenclatureEventsDialog } from './nomenclatureEvents/NomenclatureEventsDialog';
import { GermlineTransmissionStatusDialog } from './germlineTransmissionStatus/GermlineTransmissionStatusDialog';
import { DatabaseStatusDialog } from './databaseStatus/DatabaseStatusDialog';
import { SymbolDialog } from '../nameSlotAnnotations/dialogs/SymbolDialog';
import { FullNameDialog } from '../nameSlotAnnotations/dialogs/FullNameDialog';
import { SecondaryIdsDialog } from './secondaryIds/SecondaryIdsDialog';
import { SynonymsDialog } from '../nameSlotAnnotations/dialogs/SynonymsDialog';
import { RelatedNotesDialog } from '../../components/RelatedNotesDialog';
import { TaxonTableEditor } from '../../components/Editors/taxon/TaxonTableEditor';
import { InCollectionTableEditor } from '../../components/Editors/inCollection/InCollectionTableEditor';
import { ReferencesTableEditor } from '../../components/Editors/references/ReferencesTableEditor';
import { BooleanTableEditor } from '../../components/Editors/boolean/BooleanTableEditor';

import { TruncatedReferencesTemplate } from '../../components/Templates/reference/TruncatedReferencesTemplate';
import { IdTemplate } from '../../components/Templates/IdTemplate';
import { BooleanTemplate } from '../../components/Templates/BooleanTemplate';
import { TextDialogTemplate } from '../../components/Templates/dialog/TextDialogTemplate';
import { ListDialogTemplate } from '../../components/Templates/dialog/ListDialogTemplate';
import { NestedListDialogTemplate } from '../../components/Templates/dialog/NestedListDialogTemplate';
import { CountDialogTemplate } from '../../components/Templates/dialog/CountDialogTemplate';
import { ObjectListTemplate } from '../../components/Templates/ObjectListTemplate';

import { Tooltip } from 'primereact/tooltip';
import { Toast } from 'primereact/toast';
import { Button } from 'primereact/button';
import { EditMessageTooltip } from '../../components/EditMessageTooltip';
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_CONFIGS } from '../../constants/FilterFields';
import { crossReferencesSort } from '../../components/Templates/utils/sortMethods';
import { StringTemplate } from '../../components/Templates/StringTemplate';
import { OntologyTermTemplate } from '../../components/Templates/OntologyTermTemplate';

export const AllelesTable = () => {
	const [isInEditMode, setIsInEditMode] = useState(false);
	const [errorMessages, setErrorMessages] = useState({});
	const errorMessagesRef = useRef();
	errorMessagesRef.current = errorMessages;

	const [totalRecords, setTotalRecords] = useState(0);
	const [alleles, setAlleles] = useState([]);

	const searchService = new SearchService();

	const [relatedNotesData, setRelatedNotesData] = useState({
		relatedNotes: [],
		isInEdit: false,
		dialog: false,
		rowIndex: null,
		mainRowProps: {},
	});

	const [symbolData, setSymbolData] = useState({
		isInEdit: false,
		dialog: false,
		rowIndex: null,
		mainRowProps: {},
	});

	const [fullNameData, setFullNameData] = useState({
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

	const [functionalImpactsData, setFunctionalImpactsData] = useState({
		isInEdit: false,
		dialog: false,
		rowIndex: null,
		mainRowProps: {},
	});

	const [germlineTransmissionStatusData, setGermlineTransmissionStatusData] = useState({
		isInEdit: false,
		dialog: false,
		rowIndex: null,
		mainRowProps: {},
	});

	const [databaseStatusData, setDatabaseStatusData] = useState({
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

	const [nomenclatureEventsData, setNomenclatureEventsData] = useState({
		nomenclatureEvents: {},
		isInEdit: false,
		dialog: false,
		rowIndex: null,
		mainRowProps: {},
	});

	const [secondaryIdsData, setSecondaryIdsData] = useState({
		isInEdit: false,
		dialog: false,
		rowIndex: null,
		mainRowProps: {},
	});

	const [synonymsData, setSynonymsData] = useState({
		isInEdit: false,
		dialog: false,
		rowIndex: null,
		mainRowProps: {},
	});

	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);

	let alleleService = new AlleleService();

	const mutation = useMutation((updatedAllele) => {
		if (!alleleService) {
			alleleService = new AlleleService();
		}
		return alleleService.saveAllele(updatedAllele);
	});

	const handleRelatedNotesOpen = (relatedNotes) => {
		console.log('relatedNotes', relatedNotes);
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
							<EditMessageTooltip object="allele" />
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

	const symbolEditor = (props) => {
		return (
			<>
				<div>
					<Button
						className="p-button-text"
						onClick={(event) => {
							handleSymbolOpenInEdit(event, props, true);
						}}
					>
						<span style={{ textDecoration: 'underline' }}>
							{
								<div
									className="overflow-hidden text-overflow-ellipsis"
									dangerouslySetInnerHTML={{ __html: props.rowData.alleleSymbol.displayText }}
								/>
							}
							<i className="pi pi-user-edit" style={{ fontSize: '1em' }}></i>
						</span>
						&nbsp;&nbsp;&nbsp;&nbsp;
						<EditMessageTooltip object="allele" />
					</Button>
				</div>
				<ErrorMessageComponent
					errorMessages={errorMessagesRef.current[props.rowIndex]}
					errorField={'alleleSymbol'}
					style={{ fontSize: '1em' }}
				/>
			</>
		);
	};

	const handleSymbolOpen = (alleleSymbol) => {
		let _symbolData = {};
		_symbolData['originalSymbols'] = [alleleSymbol];
		_symbolData['dialog'] = true;
		_symbolData['isInEdit'] = false;
		setSymbolData(() => ({
			..._symbolData,
		}));
	};

	const handleSymbolOpenInEdit = (event, rowProps, isInEdit) => {
		const { rows } = rowProps.props;
		const { rowIndex } = rowProps;
		const index = rowIndex % rows;
		let _symbolData = {};
		_symbolData['originalSymbols'] = [rowProps.rowData.alleleSymbol];
		_symbolData['dialog'] = true;
		_symbolData['isInEdit'] = isInEdit;
		_symbolData['rowIndex'] = index;
		_symbolData['mainRowProps'] = rowProps;
		setSymbolData(() => ({
			..._symbolData,
		}));
	};

	const fullNameEditor = (props) => {
		if (props?.rowData?.alleleFullName) {
			return (
				<>
					<div>
						<Button
							className="p-button-text"
							onClick={(event) => {
								handleFullNameOpenInEdit(event, props, true);
							}}
						>
							<span style={{ textDecoration: 'underline' }}>
								{
									<div
										className="overflow-hidden text-overflow-ellipsis"
										dangerouslySetInnerHTML={{ __html: props.rowData.alleleFullName.displayText }}
									/>
								}
								<i className="pi pi-user-edit" style={{ fontSize: '1em' }}></i>
							</span>
							&nbsp;&nbsp;&nbsp;&nbsp;
							<EditMessageTooltip object="allele" />
						</Button>
					</div>
					<ErrorMessageComponent
						errorMessages={errorMessagesRef.current[props.rowIndex]}
						errorField={'alleleFullName'}
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
								handleFullNameOpenInEdit(event, props, true);
							}}
						>
							<span style={{ textDecoration: 'underline' }}>
								Add Full Name
								<i className="pi pi-user-edit" style={{ fontSize: '1em' }}></i>
							</span>
							&nbsp;&nbsp;&nbsp;&nbsp;
							<Tooltip target=".exclamation-icon" style={{ width: '250px', maxWidth: '250px' }} />
							<EditMessageTooltip object="allele" />
						</Button>
					</div>
					<ErrorMessageComponent
						errorMessages={errorMessagesRef.current[props.rowIndex]}
						errorField={'alleleFullName'}
						style={{ fontSize: '1em' }}
					/>
				</>
			);
		}
	};

	const handleFullNameOpen = (alleleFullName) => {
		let _fullNameData = {};
		_fullNameData['originalFullNames'] = [alleleFullName];
		_fullNameData['dialog'] = true;
		_fullNameData['isInEdit'] = false;
		setFullNameData(() => ({
			..._fullNameData,
		}));
	};

	const handleFullNameOpenInEdit = (event, rowProps, isInEdit) => {
		const { rows } = rowProps.props;
		const { rowIndex } = rowProps;
		const index = rowIndex % rows;
		let _fullNameData = {};
		_fullNameData['originalFullNames'] = [rowProps.rowData.alleleFullName];
		_fullNameData['dialog'] = true;
		_fullNameData['isInEdit'] = isInEdit;
		_fullNameData['rowIndex'] = index;
		_fullNameData['mainRowProps'] = rowProps;
		setFullNameData(() => ({
			..._fullNameData,
		}));
	};

	const synonymsEditor = (props) => {
		if (props?.rowData?.alleleSynonyms) {
			return (
				<>
					<div>
						<Button
							className="p-button-text"
							onClick={(event) => {
								handleSynonymsOpenInEdit(event, props, true);
							}}
						>
							<span style={{ textDecoration: 'underline' }}>
								{`Synonyms(${props.rowData.alleleSynonyms.length}) `}
								<i className="pi pi-user-edit" style={{ fontSize: '1em' }}></i>
							</span>
							&nbsp;&nbsp;&nbsp;&nbsp;
							<EditMessageTooltip object="allele" />
						</Button>
					</div>
					<ErrorMessageComponent
						errorMessages={errorMessagesRef.current[props.rowIndex]}
						errorField={'alleleSynonyms'}
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
								handleSynonymsOpenInEdit(event, props, true);
							}}
						>
							<span style={{ textDecoration: 'underline' }}>
								Add Synonym
								<i className="pi pi-user-edit" style={{ fontSize: '1em' }}></i>
							</span>
							&nbsp;&nbsp;&nbsp;&nbsp;
							<Tooltip target=".exclamation-icon" style={{ width: '250px', maxWidth: '250px' }} />
							<EditMessageTooltip object="allele" />
						</Button>
					</div>
					<ErrorMessageComponent
						errorMessages={errorMessagesRef.current[props.rowIndex]}
						errorField={'alleleSynonyms'}
						style={{ fontSize: '1em' }}
					/>
				</>
			);
		}
	};

	const handleSynonymsOpen = (alleleSynonyms) => {
		let _synonymsData = {};
		_synonymsData['originalSynonyms'] = alleleSynonyms;
		_synonymsData['dialog'] = true;
		_synonymsData['isInEdit'] = false;
		setSynonymsData(() => ({
			..._synonymsData,
		}));
	};

	const handleSynonymsOpenInEdit = (event, rowProps, isInEdit) => {
		const { rows } = rowProps.props;
		const { rowIndex } = rowProps;
		const index = rowIndex % rows;
		let _synonymsData = {};
		_synonymsData['originalSynonyms'] = rowProps.rowData.alleleSynonyms;
		_synonymsData['dialog'] = true;
		_synonymsData['isInEdit'] = isInEdit;
		_synonymsData['rowIndex'] = index;
		_synonymsData['mainRowProps'] = rowProps;
		setSynonymsData(() => ({
			..._synonymsData,
		}));
	};

	const inheritanceModesEditor = (props) => {
		if (props?.rowData?.alleleInheritanceModes) {
			return (
				<>
					<div>
						<Button
							className="p-button-text"
							onClick={(event) => {
								handleInheritanceModesOpenInEdit(event, props, true);
							}}
						>
							<span style={{ textDecoration: 'underline' }}>
								{`Inheritance Modes(${props.rowData.alleleInheritanceModes.length}) `}
								<i className="pi pi-user-edit" style={{ fontSize: '1em' }}></i>
							</span>
							&nbsp;&nbsp;&nbsp;&nbsp;
							<EditMessageTooltip object="allele" />
						</Button>
					</div>
					<ErrorMessageComponent
						errorMessages={errorMessagesRef.current[props.rowIndex]}
						errorField={'alleleInheritanceModes'}
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
								handleInheritanceModesOpenInEdit(event, props, true);
							}}
						>
							<span style={{ textDecoration: 'underline' }}>
								Add Inheritance Mode
								<i className="pi pi-user-edit" style={{ fontSize: '1em' }}></i>
							</span>
							&nbsp;&nbsp;&nbsp;&nbsp;
							<Tooltip target=".exclamation-icon" style={{ width: '250px', maxWidth: '250px' }} />
							<EditMessageTooltip object="allele" />
						</Button>
					</div>
					<ErrorMessageComponent
						errorMessages={errorMessagesRef.current[props.rowIndex]}
						errorField={'alleleInheritanceModes'}
						style={{ fontSize: '1em' }}
					/>
				</>
			);
		}
	};

	const handleInheritanceModesOpen = (alleleInheritanceModes) => {
		let _inheritanceModesData = {};
		_inheritanceModesData['originalInheritanceModes'] = alleleInheritanceModes;
		_inheritanceModesData['dialog'] = true;
		_inheritanceModesData['isInEdit'] = false;
		setInheritanceModesData(() => ({
			..._inheritanceModesData,
		}));
	};

	const handleInheritanceModesOpenInEdit = (event, rowProps, isInEdit) => {
		const { rows } = rowProps.props;
		const { rowIndex } = rowProps;
		const index = rowIndex % rows;
		let _inheritanceModesData = {};
		_inheritanceModesData['originalInheritanceModes'] = rowProps.rowData.alleleInheritanceModes;
		_inheritanceModesData['dialog'] = true;
		_inheritanceModesData['isInEdit'] = isInEdit;
		_inheritanceModesData['rowIndex'] = index;
		_inheritanceModesData['mainRowProps'] = rowProps;
		setInheritanceModesData(() => ({
			..._inheritanceModesData,
		}));
	};

	const germlineTransmissionStatusEditor = (props) => {
		if (props?.rowData?.alleleGermlineTransmissionStatus) {
			return (
				<>
					<div>
						<Button
							className="p-button-text"
							onClick={(event) => {
								handleGermlineTransmissionStatusOpenInEdit(event, props, true);
							}}
						>
							<span style={{ textDecoration: 'underline' }}>
								{`${props.rowData.alleleGermlineTransmissionStatus.germlineTransmissionStatus.name}`}
								<i className="pi pi-user-edit" style={{ fontSize: '1em' }}></i>
							</span>
							&nbsp;&nbsp;&nbsp;&nbsp;
							<EditMessageTooltip object="allele" />
						</Button>
					</div>
					<ErrorMessageComponent
						errorMessages={errorMessagesRef.current[props.rowIndex]}
						errorField={'alleleGermlineTransmissionStatus'}
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
								handleGermlineTransmissionStatusOpenInEdit(event, props, true);
							}}
						>
							<span style={{ textDecoration: 'underline' }}>
								Add Germline Transmission Status
								<i className="pi pi-user-edit" style={{ fontSize: '1em' }}></i>
							</span>
							&nbsp;&nbsp;&nbsp;&nbsp;
							<Tooltip target=".exclamation-icon" style={{ width: '250px', maxWidth: '250px' }} />
							<EditMessageTooltip object="allele" />
						</Button>
					</div>
					<ErrorMessageComponent
						errorMessages={errorMessagesRef.current[props.rowIndex]}
						errorField={'alleleGermlineTransmissionStatus'}
						style={{ fontSize: '1em' }}
					/>
				</>
			);
		}
	};

	const handleGermlineTransmissionStatusOpen = (alleleGermlineTransmissionStatus) => {
		let _germlineTransmissionStatusData = {};
		_germlineTransmissionStatusData['originalGermlineTransmissionStatuses'] = [alleleGermlineTransmissionStatus];
		_germlineTransmissionStatusData['dialog'] = true;
		_germlineTransmissionStatusData['isInEdit'] = false;
		setGermlineTransmissionStatusData(() => ({
			..._germlineTransmissionStatusData,
		}));
	};

	const handleGermlineTransmissionStatusOpenInEdit = (event, rowProps, isInEdit) => {
		const { rows } = rowProps.props;
		const { rowIndex } = rowProps;
		const index = rowIndex % rows;
		let _germlineTransmissionStatusData = {};
		_germlineTransmissionStatusData['originalGermlineTransmissionStatuses'] = [
			rowProps.rowData.alleleGermlineTransmissionStatus,
		];
		_germlineTransmissionStatusData['dialog'] = true;
		_germlineTransmissionStatusData['isInEdit'] = isInEdit;
		_germlineTransmissionStatusData['rowIndex'] = index;
		_germlineTransmissionStatusData['mainRowProps'] = rowProps;
		setGermlineTransmissionStatusData(() => ({
			..._germlineTransmissionStatusData,
		}));
	};

	const nomenclatureEventsEditor = (props) => {
		if (props?.rowData?.alleleNomenclatureEvents) {
			return (
				<>
					<div>
						<Button
							className="p-button-text"
							onClick={(event) => {
								handleNomenclatureEventsOpenInEdit(event, props, true);
							}}
						>
							<span style={{ textDecoration: 'underline' }}>
								{`Nomenclature Events(${props.rowData.alleleNomenclatureEvents.length}) `}
								<i className="pi pi-user-edit" style={{ fontSize: '1em' }}></i>
							</span>
							&nbsp;&nbsp;&nbsp;&nbsp;
							<EditMessageTooltip object="allele" />
						</Button>
					</div>
					<ErrorMessageComponent
						errorMessages={errorMessagesRef.current[props.rowIndex]}
						errorField={'alleleNomenclatureEvents'}
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
								handleNomenclatureEventsOpenInEdit(event, props, true);
							}}
						>
							<span style={{ textDecoration: 'underline' }}>
								Add Nomenclature Event
								<i className="pi pi-user-edit" style={{ fontSize: '1em' }}></i>
							</span>
							&nbsp;&nbsp;&nbsp;&nbsp;
							<Tooltip target=".exclamation-icon" style={{ width: '250px', maxWidth: '250px' }} />
							<EditMessageTooltip object="allele" />
						</Button>
					</div>
					<ErrorMessageComponent
						errorMessages={errorMessagesRef.current[props.rowIndex]}
						errorField={'alleleNomenclatureEvents'}
						style={{ fontSize: '1em' }}
					/>
				</>
			);
		}
	};

	const handleNomenclatureEventsOpen = (alleleNomenclatureEvents) => {
		let _nomenclatureEventsData = {};
		_nomenclatureEventsData['originalNomenclatureEvents'] = alleleNomenclatureEvents;
		_nomenclatureEventsData['dialog'] = true;
		_nomenclatureEventsData['isInEdit'] = false;
		setNomenclatureEventsData(() => ({
			..._nomenclatureEventsData,
		}));
	};

	const handleNomenclatureEventsOpenInEdit = (event, rowProps, isInEdit) => {
		const { rows } = rowProps.props;
		const { rowIndex } = rowProps;
		const index = rowIndex % rows;
		let _nomenclatureEventsData = {};
		_nomenclatureEventsData['originalNomenclatureEvents'] = rowProps.rowData.alleleNomenclatureEvents;
		_nomenclatureEventsData['dialog'] = true;
		_nomenclatureEventsData['isInEdit'] = isInEdit;
		_nomenclatureEventsData['rowIndex'] = index;
		_nomenclatureEventsData['mainRowProps'] = rowProps;
		setNomenclatureEventsData(() => ({
			..._nomenclatureEventsData,
		}));
	};

	const databaseStatusEditor = (props) => {
		if (props?.rowData?.alleleDatabaseStatus?.databaseStatus) {
			return (
				<>
					<div>
						<Button
							className="p-button-text"
							onClick={(event) => {
								handleDatabaseStatusOpenInEdit(event, props, true);
							}}
						>
							<span style={{ textDecoration: 'underline' }}>
								{`${props.rowData.alleleDatabaseStatus.databaseStatus.name}`}
								<i className="pi pi-user-edit" style={{ fontSize: '1em' }}></i>
							</span>
							&nbsp;&nbsp;&nbsp;&nbsp;
							<EditMessageTooltip object="allele" />
						</Button>
					</div>
					<ErrorMessageComponent
						errorMessages={errorMessagesRef.current[props.rowIndex]}
						errorField={'alleleDatabaseStatus'}
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
								handleDatabaseStatusOpenInEdit(event, props, true);
							}}
						>
							<span style={{ textDecoration: 'underline' }}>
								Add Database Status
								<i className="pi pi-user-edit" style={{ fontSize: '1em' }}></i>
							</span>
							&nbsp;&nbsp;&nbsp;&nbsp;
							<Tooltip target=".exclamation-icon" style={{ width: '250px', maxWidth: '250px' }} />
							<EditMessageTooltip object="allele" />
						</Button>
					</div>
					<ErrorMessageComponent
						errorMessages={errorMessagesRef.current[props.rowIndex]}
						errorField={'alleleDatabaseStatus'}
						style={{ fontSize: '1em' }}
					/>
				</>
			);
		}
	};

	const handleDatabaseStatusOpen = (alleleDatabaseStatus) => {
		let _databaseStatusData = {};
		_databaseStatusData['originalDatabaseStatuses'] = [alleleDatabaseStatus];
		_databaseStatusData['dialog'] = true;
		_databaseStatusData['isInEdit'] = false;
		setDatabaseStatusData(() => ({
			..._databaseStatusData,
		}));
	};

	const handleDatabaseStatusOpenInEdit = (event, rowProps, isInEdit) => {
		const { rows } = rowProps.props;
		const { rowIndex } = rowProps;
		const index = rowIndex % rows;
		let _databaseStatusData = {};
		_databaseStatusData['originalDatabaseStatuses'] = [rowProps.rowData.alleleDatabaseStatus];
		_databaseStatusData['dialog'] = true;
		_databaseStatusData['isInEdit'] = isInEdit;
		_databaseStatusData['rowIndex'] = index;
		_databaseStatusData['mainRowProps'] = rowProps;
		setDatabaseStatusData(() => ({
			..._databaseStatusData,
		}));
	};

	const mutationTypesEditor = (props) => {
		if (props?.rowData?.alleleMutationTypes) {
			return (
				<>
					<div>
						<Button
							className="p-button-text"
							onClick={(event) => {
								handleMutationTypesOpenInEdit(event, props, true);
							}}
						>
							<span style={{ textDecoration: 'underline' }}>
								{`Mutation Types(${props.rowData.alleleMutationTypes.length}) `}
								<i className="pi pi-user-edit" style={{ fontSize: '1em' }}></i>
							</span>
							&nbsp;&nbsp;&nbsp;&nbsp;
							<EditMessageTooltip object="allele" />
						</Button>
					</div>
					<ErrorMessageComponent
						errorMessages={errorMessagesRef.current[props.rowIndex]}
						errorField={'alleleMutationTypes'}
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
								handleMutationTypesOpenInEdit(event, props, true);
							}}
						>
							<span style={{ textDecoration: 'underline' }}>
								Add Mutation Type
								<i className="pi pi-user-edit" style={{ fontSize: '1em' }}></i>
							</span>
							&nbsp;&nbsp;&nbsp;&nbsp;
							<Tooltip target=".exclamation-icon" style={{ width: '250px', maxWidth: '250px' }} />
							<EditMessageTooltip object="allele" />
						</Button>
					</div>
					<ErrorMessageComponent
						errorMessages={errorMessagesRef.current[props.rowIndex]}
						errorField={'alleleMutationTypes'}
						style={{ fontSize: '1em' }}
					/>
				</>
			);
		}
	};

	const handleMutationTypesOpen = (alleleMutationTypes) => {
		let _mutationTypesData = {};
		_mutationTypesData['originalMutationTypes'] = alleleMutationTypes;
		_mutationTypesData['dialog'] = true;
		_mutationTypesData['isInEdit'] = false;
		setMutationTypesData(() => ({
			..._mutationTypesData,
		}));
	};

	const handleMutationTypesOpenInEdit = (event, rowProps, isInEdit) => {
		const { rows } = rowProps.props;
		const { rowIndex } = rowProps;
		const index = rowIndex % rows;
		let _mutationTypesData = {};
		_mutationTypesData['originalMutationTypes'] = rowProps.rowData.alleleMutationTypes;
		_mutationTypesData['dialog'] = true;
		_mutationTypesData['isInEdit'] = isInEdit;
		_mutationTypesData['rowIndex'] = index;
		_mutationTypesData['mainRowProps'] = rowProps;
		setMutationTypesData(() => ({
			..._mutationTypesData,
		}));
	};

	const functionalImpactsEditor = (props) => {
		if (props?.rowData?.alleleFunctionalImpacts) {
			return (
				<>
					<div>
						<Button
							className="p-button-text"
							onClick={(event) => {
								handleFunctionalImpactsOpenInEdit(event, props, true);
							}}
						>
							<span style={{ textDecoration: 'underline' }}>
								{`Functional Impacts(${props.rowData.alleleFunctionalImpacts.length}) `}
								<i className="pi pi-user-edit" style={{ fontSize: '1em' }}></i>
							</span>
							&nbsp;&nbsp;&nbsp;&nbsp;
							<EditMessageTooltip />
						</Button>
					</div>
					<ErrorMessageComponent
						errorMessages={errorMessagesRef.current[props.rowIndex]}
						errorField={'alleleFunctionalImpacts'}
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
								handleFunctionalImpactsOpenInEdit(event, props, true);
							}}
						>
							<span style={{ textDecoration: 'underline' }}>
								Add Functional Impact
								<i className="pi pi-user-edit" style={{ fontSize: '1em' }}></i>
							</span>
							&nbsp;&nbsp;&nbsp;&nbsp;
							<Tooltip target=".exclamation-icon" style={{ width: '250px', maxWidth: '250px' }} />
							<EditMessageTooltip />
						</Button>
					</div>
					<ErrorMessageComponent
						errorMessages={errorMessagesRef.current[props.rowIndex]}
						errorField={'alleleFunctionalImpacts'}
						style={{ fontSize: '1em' }}
					/>
				</>
			);
		}
	};

	const handleFunctionalImpactsOpen = (alleleFunctionalImpacts) => {
		let _functionalImpactsData = {};
		_functionalImpactsData['originalFunctionalImpacts'] = alleleFunctionalImpacts;
		_functionalImpactsData['dialog'] = true;
		_functionalImpactsData['isInEdit'] = false;
		setFunctionalImpactsData(() => ({
			..._functionalImpactsData,
		}));
	};

	const handleFunctionalImpactsOpenInEdit = (event, rowProps, isInEdit) => {
		const { rows } = rowProps.props;
		const { rowIndex } = rowProps;
		const index = rowIndex % rows;
		let _functionalImpactsData = {};
		_functionalImpactsData['originalFunctionalImpacts'] = rowProps.rowData.alleleFunctionalImpacts;
		_functionalImpactsData['dialog'] = true;
		_functionalImpactsData['isInEdit'] = isInEdit;
		_functionalImpactsData['rowIndex'] = index;
		_functionalImpactsData['mainRowProps'] = rowProps;
		setFunctionalImpactsData(() => ({
			..._functionalImpactsData,
		}));
	};

	const secondaryIdsEditor = (props) => {
		if (props?.rowData?.alleleSecondaryIds) {
			return (
				<>
					<div>
						<Button
							className="p-button-text"
							onClick={(event) => {
								handleSecondaryIdsOpenInEdit(event, props, true);
							}}
						>
							<span style={{ textDecoration: 'underline' }}>
								{`Secondary IDs(${props.rowData.alleleSecondaryIds.length}) `}
								<i className="pi pi-user-edit" style={{ fontSize: '1em' }}></i>
							</span>
							&nbsp;&nbsp;&nbsp;&nbsp;
							<EditMessageTooltip object="allele" />
						</Button>
					</div>
					<ErrorMessageComponent
						errorMessages={errorMessagesRef.current[props.rowIndex]}
						errorField={'alleleSecondaryIds'}
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
								handleSecondaryIdsOpenInEdit(event, props, true);
							}}
						>
							<span style={{ textDecoration: 'underline' }}>
								Add Secondary ID
								<i className="pi pi-user-edit" style={{ fontSize: '1em' }}></i>
							</span>
							&nbsp;&nbsp;&nbsp;&nbsp;
							<EditMessageTooltip object="allele" />
						</Button>
					</div>
					<ErrorMessageComponent
						errorMessages={errorMessagesRef.current[props.rowIndex]}
						errorField={'alleleSecondaryIds'}
						style={{ fontSize: '1em' }}
					/>
				</>
			);
		}
	};

	const handleSecondaryIdsOpen = (alleleSecondaryIds) => {
		let _secondaryIdsData = {};
		_secondaryIdsData['originalSecondaryIds'] = alleleSecondaryIds;
		_secondaryIdsData['dialog'] = true;
		_secondaryIdsData['isInEdit'] = false;
		setSecondaryIdsData(() => ({
			..._secondaryIdsData,
		}));
	};

	const handleSecondaryIdsOpenInEdit = (event, rowProps, isInEdit) => {
		const { rows } = rowProps.props;
		const { rowIndex } = rowProps;
		const index = rowIndex % rows;
		let _secondaryIdsData = {};
		_secondaryIdsData['originalSecondaryIds'] = rowProps.rowData.alleleSecondaryIds;
		_secondaryIdsData['dialog'] = true;
		_secondaryIdsData['isInEdit'] = isInEdit;
		_secondaryIdsData['rowIndex'] = index;
		_secondaryIdsData['mainRowProps'] = rowProps;
		setSecondaryIdsData(() => ({
			..._secondaryIdsData,
		}));
	};

	const columns = [
		{
			field: 'curie',
			header: 'Curie',
			body: (rowData) => <IdTemplate id={rowData.curie} />,
			sortable: true,
			filterConfig: FILTER_CONFIGS.curieFilterConfig,
		},
		{
			field: 'modEntityId',
			header: 'MOD Entity ID',
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
			field: 'alleleFullName.displayText',
			header: 'Name',
			body: (rowData) => (
				<TextDialogTemplate
					entity={rowData.alleleFullName}
					handleOpen={handleFullNameOpen}
					text={rowData.alleleFullName?.displayText}
					underline={false}
				/>
			),
			editor: (props) => fullNameEditor(props),
			sortable: true,
			filterConfig: FILTER_CONFIGS.alleleNameFilterConfig,
		},
		{
			field: 'alleleSymbol.displayText',
			header: 'Symbol',
			body: (rowData) => (
				<TextDialogTemplate
					entity={rowData.alleleSymbol}
					handleOpen={handleSymbolOpen}
					text={rowData.alleleSymbol?.displayText}
					underline={false}
				/>
			),
			editor: (props) => symbolEditor(props),
			sortable: true,
			filterConfig: FILTER_CONFIGS.alleleSymbolFilterConfig,
		},
		{
			field: 'alleleSynonyms.displayText',
			header: 'Synonyms',
			body: (rowData) => (
				<ListDialogTemplate
					entities={rowData.alleleSynonyms}
					handleOpen={handleSynonymsOpen}
					getTextField={(entity) => entity?.displayText}
					underline={false}
				/>
			),
			editor: (props) => synonymsEditor(props),
			sortable: true,
			filterConfig: FILTER_CONFIGS.alleleSynonymsFilterConfig,
		},
		{
			field: 'alleleSecondaryIds.secondaryId',
			header: 'Secondary IDs',
			body: (rowData) => (
				<ListDialogTemplate
					entities={rowData.alleleSecondaryIds}
					handleOpen={handleSecondaryIdsOpen}
					getTextField={(entity) => entity?.secondaryId}
				/>
			),
			editor: (props) => secondaryIdsEditor(props),
			sortable: true,
			filterConfig: FILTER_CONFIGS.alleleSecondaryIdsFilterConfig,
		},
		{
			field: 'alleleNomenclatureEvents.nomenclatureEvent.name',
			header: 'Nomenclature Events',
			body: (rowData) => (
				<ListDialogTemplate
					entities={rowData.alleleNomenclatureEvents}
					handleOpen={handleNomenclatureEventsOpen}
					getTextField={(entity) => entity?.nomenclatureEvent?.name}
				/>
			),
			sortable: true,
			filterConfig: FILTER_CONFIGS.alleleNomenclatureEventsFilterConfig,
			editor: (props) => nomenclatureEventsEditor(props),
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
			field: 'alleleMutationTypes.mutationTypes.name',
			header: 'Mutation Types',
			body: (rowData) => (
				<NestedListDialogTemplate
					entities={rowData.alleleMutationTypes}
					subType={'mutationTypes'}
					handleOpen={handleMutationTypesOpen}
					getTextString={(item) => `${item.name} (${item.curie})`}
				/>
			),
			editor: (props) => mutationTypesEditor(props),
			sortable: true,
			filterConfig: FILTER_CONFIGS.alleleMutationFilterConfig,
		},
		{
			field: 'alleleFunctionalImpacts.functionalImpacts.name',
			header: 'Functional Impacts',
			body: (rowData) => (
				<NestedListDialogTemplate
					entities={rowData.alleleFunctionalImpacts}
					subType={'functionalImpacts'}
					handleOpen={handleFunctionalImpactsOpen}
					getTextString={(item) => item.name}
				/>
			),
			editor: (props) => functionalImpactsEditor(props),
			sortable: true,
			filterConfig: FILTER_CONFIGS.alleleFunctionalImpactsFilterConfig,
		},
		{
			field: 'alleleGermlineTransmissionStatus.germlineTransmissionStatus.name',
			header: 'Germline Transmission Status',
			body: (rowData) => (
				<TextDialogTemplate
					entity={rowData.alleleGermlineTransmissionStatus}
					handleOpen={handleGermlineTransmissionStatusOpen}
					text={rowData.alleleGermlineTransmissionStatus?.germlineTransmissionStatus?.name}
				/>
			),
			editor: (props) => germlineTransmissionStatusEditor(props),
			sortable: true,
			filterConfig: FILTER_CONFIGS.alleleGermlineTransmissionStatusFilterConfig,
		},
		{
			field: 'alleleDatabaseStatus.databaseStatus.name',
			header: 'Database Status',
			body: (rowData) => (
				<TextDialogTemplate
					entity={rowData.alleleDatabaseStatus}
					handleOpen={handleDatabaseStatusOpen}
					text={rowData.alleleDatabaseStatus?.databaseStatus?.name}
				/>
			),
			editor: (props) => databaseStatusEditor(props),
			sortable: true,
			filterConfig: FILTER_CONFIGS.alleleDatabaseStatusFilterConfig,
		},
		{
			field: 'references.primaryCrossReferenceCurie',
			header: 'References',
			body: (rowData) => (
				<TruncatedReferencesTemplate
					references={rowData.references}
					identifier={rowData.modEntityId}
					detailPage="Allele"
				/>
			),
			sortable: true,
			filterConfig: FILTER_CONFIGS.referencesFilterConfig,
			editor: (props) => <ReferencesTableEditor rowProps={props} errorMessagesRef={errorMessagesRef} />,
		},
		{
			field: 'alleleInheritanceModes.inheritanceMode.name',
			header: 'Inheritance Modes',
			body: (rowData) => (
				<ListDialogTemplate
					entities={rowData.alleleInheritanceModes}
					handleOpen={handleInheritanceModesOpen}
					getTextField={(entity) => entity?.inheritanceMode?.name}
				/>
			),
			sortable: true,
			filterConfig: FILTER_CONFIGS.alleleInheritanceModesFilterConfig,
			editor: (props) => inheritanceModesEditor(props),
		},
		{
			field: 'inCollection.name',
			header: 'In Collection',
			sortable: true,
			body: (rowData) => <StringTemplate string={rowData.inCollection?.name} />,
			filterConfig: FILTER_CONFIGS.inCollectionFilterConfig,
			editor: (props) => <InCollectionTableEditor rowProps={props} errorMessagesRef={errorMessagesRef} />,
		},
		{
			field: 'isExtinct',
			header: 'Is Extinct',
			body: (rowData) => <BooleanTemplate value={rowData.isExtinct} />,
			filterConfig: FILTER_CONFIGS.isExtinctFilterConfig,
			sortable: true,
			editor: (props) => (
				<BooleanTableEditor rowProps={props} errorMessagesRef={errorMessagesRef} field={'isExtinct'} />
			),
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
			field: 'dataProvider.sourceOrganization.abbreviation',
			header: 'Data Provider',
			sortable: true,
			filterConfig: FILTER_CONFIGS.alleleDataProviderFilterConfig,
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
			body: (rowData) => <BooleanTemplate value={rowData.internal} />,
			filter: true,
			filterConfig: FILTER_CONFIGS.internalFilterConfig,
			sortable: true,
			editor: (props) => <BooleanTableEditor rowProps={props} errorMessagesRef={errorMessagesRef} field={'internal'} />,
		},
		{
			field: 'obsolete',
			header: 'Obsolete',
			body: (rowData) => <BooleanTemplate value={rowData.obsolete} />,
			filter: true,
			filterConfig: FILTER_CONFIGS.obsoleteFilterConfig,
			sortable: true,
			editor: (props) => <BooleanTableEditor rowProps={props} errorMessagesRef={errorMessagesRef} field={'obsolete'} />,
		},
	];

	const DEFAULT_COLUMN_WIDTH = 10;
	const SEARCH_ENDPOINT = 'allele';

	const initialTableState = getDefaultTableState('Alleles', columns, DEFAULT_COLUMN_WIDTH);

	const { settings: tableState, mutate: setTableState } = useGetUserSettings(
		initialTableState.tableSettingsKeyName,
		initialTableState
	);

	const { isFetching, isLoading } = useGetTableData({
		tableState,
		endpoint: SEARCH_ENDPOINT,
		setIsInEditMode,
		setEntities: setAlleles,
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
					endpoint={SEARCH_ENDPOINT}
					tableName="Alleles"
					entities={alleles}
					setEntities={setAlleles}
					totalRecords={totalRecords}
					setTotalRecords={setTotalRecords}
					tableState={tableState}
					setTableState={setTableState}
					columns={columns}
					isEditable={true}
					hasDetails={true}
					mutation={mutation}
					isInEditMode={isInEditMode}
					setIsInEditMode={setIsInEditMode}
					toasts={{ toast_topleft, toast_topright }}
					errorObject={{ errorMessages, setErrorMessages }}
					defaultColumnWidth={DEFAULT_COLUMN_WIDTH}
					fetching={isFetching || isLoading}
				/>
			</div>
			<SymbolDialog
				name="Allele Symbol"
				field="alleleSymbol"
				endpoint="allelesymbolslotannotation"
				originalSymbolData={symbolData}
				setOriginalSymbolData={setSymbolData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
			/>
			<FullNameDialog
				name="Allele Name"
				field="alleleFullName"
				endpoint="allelefullnameslotannotation"
				originalFullNameData={fullNameData}
				setOriginalFullNameData={setFullNameData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
			/>
			<SynonymsDialog
				name="Allele Synonym"
				field="alleleSynonyms"
				endpoint="allelesynonymslotannotation"
				originalSynonymsData={synonymsData}
				setOriginalSynonymsData={setSynonymsData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
			/>
			<NomenclatureEventsDialog
				originalNomenclatureEventsData={nomenclatureEventsData}
				setOriginalNomenclatureEventsData={setNomenclatureEventsData}
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
			<FunctionalImpactsDialog
				originalFunctionalImpactsData={functionalImpactsData}
				setOriginalFunctionalImpactsData={setFunctionalImpactsData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
			/>
			<GermlineTransmissionStatusDialog
				originalGermlineTransmissionStatusData={germlineTransmissionStatusData}
				setOriginalGermlineTransmissionStatusData={setGermlineTransmissionStatusData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
			/>
			<DatabaseStatusDialog
				originalDatabaseStatusData={databaseStatusData}
				setOriginalDatabaseStatusData={setDatabaseStatusData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
			/>
			<RelatedNotesDialog
				originalRelatedNotesData={relatedNotesData}
				setOriginalRelatedNotesData={setRelatedNotesData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
				noteTypeVocabularyTermSet="allele_note_type"
			/>
		</>
	);
};
