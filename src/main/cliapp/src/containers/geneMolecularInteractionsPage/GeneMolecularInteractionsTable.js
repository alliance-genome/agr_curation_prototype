import React, { useRef, useState } from 'react';

import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { IdTemplate } from '../../components/Templates/IdTemplate'; 
import { GenomicEntityTemplate } from '../../components/Templates/genomicEntity/GenomicEntityTemplate';
import { BooleanTemplate } from '../../components/Templates/BooleanTemplate';
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_CONFIGS } from '../../constants/FilterFields';
import { evidenceTemplate } from '../../components/EvidenceComponent';
import { OntologyTermTemplate } from '../../components/Templates/OntologyTermTemplate';

export const GeneMolecularInteractionsTable = () => {

	const [isInEditMode, setIsInEditMode] = useState(false); //needs better name
	const [errorMessages, setErrorMessages] = useState({});
	const errorMessagesRef = useRef();
	errorMessagesRef.current = errorMessages;

	const [uiErrorMessages, setUiErrorMessages] = useState([]);
	const uiErrorMessagesRef = useRef();
	uiErrorMessagesRef.current = uiErrorMessages;

	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);

	const sortMapping = {
		'geneAssociationSubject.geneSymbol.displayText': ['geneAssociationSubject.geneFullName.displayText', 'geneAssociationSubject.modEntityId'],
		'geneGeneAssociationObject.geneSymbol.displayText': ['geneGeneAssociationObject.geneFullName.displayText', 'geneGeneAssociationObject.modEntityId'],
		'phenotypeAnnotationObject.name': ['phenotypeAnnotationObject.curie', 'phenotypeAnnotationObject.namespace'],
		'interactionType.name': ['interactionType.curie'],
		'interactorARole.name': ['interactorARole.curie'],
		'interactorBRole.name': ['interactorBRole.curie'],
		'interactorAType.name': ['interactorAType.curie'],
		'interactorBType.name': ['interactorBType.curie'],
		'interactionSource.name': ['interactionSource.curie'],
		'aggregationDatabase.name': ['aggregationDatabase.curie'],
		'detectionMethod.name': ['detectionMethod.curie'],
	};

	const columns = [{
		field: "uniqueId",
		header: "Unique ID",
		body: (rowData) => <IdTemplate id={rowData.uniqueId}/>,
		sortable: true,
		filterConfig: FILTER_CONFIGS.uniqueidFilterConfig
	},
	{
		field: "interactionId",
		header: "Interaction ID",
		body: (rowData) => <IdTemplate id={rowData.interactionId}/>,
		sortable: true,
		filterConfig: FILTER_CONFIGS.interactionIdFilterConfig
	},
	{
		field: "geneAssociationSubject.geneSymbol.displayText",
		header: "Interactor A",
		body: (rowData) => <GenomicEntityTemplate genomicEntity={rowData.geneAssociationSubject}/>,
		sortable: true,
		filterConfig: FILTER_CONFIGS.geneAssociationSubjectFilterConfig
	},
	{
		field: "relation.name",
		header: "Interaction Relation",
		sortable: true,
		filterConfig: FILTER_CONFIGS.gmiRelationFilterConfig
	},
	{
		field: "geneGeneAssociationObject.geneSymbol.displayText",
		header: "Interactor B",
		body: (rowData) => <GenomicEntityTemplate genomicEntity={rowData.geneGeneAssociationObject}/>,
		sortable: true,
		filterConfig: FILTER_CONFIGS.geneGeneAssociationObjectFilterConfig
	},
	{
		field: "interactionType.name",
		header: "Interaction Type",
		body: (rowData) => <OntologyTermTemplate object={rowData.interactionType}/>,
		sortable: true,
		filterConfig: FILTER_CONFIGS.interactionTypeFilterConfig
	},
	{
		field: "interactorARole.name",
		header: "Interactor A Role",
		body: (rowData) => <OntologyTermTemplate object={rowData.interactorARole}/>,
		sortable: true,
		filterConfig: FILTER_CONFIGS.interactorARoleFilterConfig
	},
	{
		field: "interactorBRole.name",
		header: "Interactor B Role",
		body: (rowData) => <OntologyTermTemplate object={rowData.interactorBRole}/>,
		sortable: true,
		filterConfig: FILTER_CONFIGS.interactorBRoleFilterConfig
	},
	{
		field: "interactorAType.name",
		header: "Interactor A Type",
		body: (rowData) => <OntologyTermTemplate object={rowData.interactorAType}/>,
		sortable: true,
		filterConfig: FILTER_CONFIGS.interactorATypeFilterConfig
	},
	{
		field: "interactorBType.name",
		header: "Interactor B Type",
		body: (rowData) => <OntologyTermTemplate object={rowData.interactorBType}/>,
		sortable: true,
		filterConfig: FILTER_CONFIGS.interactorBTypeFilterConfig
	},
	{
		field: "interactionSource.name",
		header: "Interaction Source",
		body: (rowData) => <OntologyTermTemplate object={rowData.interactionSource}/>,
		sortable: true,
		filterConfig: FILTER_CONFIGS.interactionSourceFilterConfig
	},
	{
		field: "aggregationDatabase.name",
		header: "Aggregation DB",
		body: (rowData) => <OntologyTermTemplate object={rowData.aggregationDatabase}/>,
		sortable: true,
		filterConfig: FILTER_CONFIGS.aggregationDatabaseFilterConfig
	},
	{
		field: "detectionMethod.name",
		header: "Detection Method",
		body: (rowData) => <OntologyTermTemplate object={rowData.detectionMethod}/>,
		sortable: true,
		filterConfig: FILTER_CONFIGS.detectionMethodFilterConfig
	},
	{
		field: "evidence.curie",
		header: "Evidence",
		body: (rowData) => evidenceTemplate(rowData),
		sortable: true,
		filterConfig: FILTER_CONFIGS.evidenceFilterConfig
	},
	{
		field: "internal",
		header: "Internal",
		body: (rowData) => <BooleanTemplate value={rowData.internal}/>,
		sortable: true,
		filterConfig: FILTER_CONFIGS.internalFilterConfig
	},
	{
		field: "obsolete",
		header: "Obsolete",
		body: (rowData) => <BooleanTemplate value={rowData.obsolete}/>,
		sortable: true,
		filterConfig: FILTER_CONFIGS.obsoleteFilterConfig
	}
	];

	const defaultColumnNames = columns.map((col) => {
		return col.header;
	});

	const widthsObject = {};

	columns.forEach((col) => {
		widthsObject[col.field] = 10;
	});

	const initialTableState = getDefaultTableState("PhenotypeAnnotations", defaultColumnNames, undefined, widthsObject);

	return (
		<>
			<div className="card">
				<GenericDataTable
					endpoint="gene-molecular-interaction"
					tableName="Gene Molecular Interactions"
					columns={columns}
					defaultColumnNames={defaultColumnNames}
					initialTableState={initialTableState}
					toasts={{toast_topleft, toast_topright }}
					isEditable={false}
					isInEditMode={isInEditMode}
					setIsInEditMode={setIsInEditMode}
					sortMapping={sortMapping}
					errorObject={{errorMessages, setErrorMessages, uiErrorMessages, setUiErrorMessages}}
					deletionEnabled={false}
					deprecateOption={false}
					modReset={false}
					widthsObject={widthsObject}
					duplicationEnabled={false}
				/>
			</div>
		</>
	);
};
