import React, {useRef, useState} from 'react';
import {useMutation} from 'react-query';
import {Toast} from 'primereact/toast';
import {SearchService} from '../../service/SearchService';
import {Messages} from 'primereact/messages';
import {getRefID} from '../../utils/utils';
import {ControlledVocabularyDropdown} from "../../components/ControlledVocabularySelector";
import {ErrorMessageComponent} from "../../components/ErrorMessageComponent";
import {useControlledVocabularyService} from "../../service/useControlledVocabularyService";
import {EllipsisTableCell} from "../../components/EllipsisTableCell";
import {ListTableCell} from "../../components/ListTableCell";
import {ConditionRelationService} from "../../service/ConditionRelationService";
import {AutocompleteEditor} from "../../components/AutocompleteEditor";
import {InputTextEditor} from "../../components/InputTextEditor";
import {GenericDataTable} from '../../components/GenericDataTable/GenericDataTable';


export const ConditionRelationTable = () => {

	const [isEnabled, setIsEnabled] = useState(true);

	const searchService = new SearchService();
	const errorMessage = useRef(null);
	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);
	const [errorMessages, setErrorMessages] = useState({});


	let conditionRelationService = null;

	const conditionRelationTypeTerms = useControlledVocabularyService('Condition relation types');

	const mutation = useMutation(updatedRelation => {
		if (!conditionRelationService) {
			conditionRelationService = new ConditionRelationService();
		}
		return conditionRelationService.saveConditionRelation(updatedRelation);
	});


	const aggregationFields = [
		'conditionRelationType.name'
	];

	const onConditionRelationValueChange = (props, event) => {
		let updatedConditions = [...props.props.value];
		if (event.value || event.value === '') {
			updatedConditions[props.rowIndex].conditionRelationType = event.value;
		}
	};


	const conditionRelationTypeEditor = (props) => {
		return (
			<>
				<ControlledVocabularyDropdown
					field="conditionRelationType.name"
					options={conditionRelationTypeTerms}
					editorChange={onConditionRelationValueChange}
					props={props}
					showClear={false}
					placeholderText={props.rowData.conditionRelationType.name}
				/>
				<ErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={"conditionRelationType.name"}/>
			</>
		);
	};

	const singleValueReferenceSelector = (referenceItem) => {
		return getRefID(referenceItem)
	}

	const referenceEditorTemplate = (props) => {
		return (
			<>
				<AutocompleteEditor
					autocompleteFields={["curie", "cross_references.curie"]}
					rowProps={props}
					searchService={searchService}
					endpoint='literature-reference'
					filterName='curieFilter'
					isSubject={true}
					fieldName='singleReference'
					valueSelector={singleValueReferenceSelector}
				/>
				<ErrorMessageComponent
					errorMessages={errorMessages[props.rowIndex]}
					errorField={"reference"}
				/>
			</>
		);
	};


	const conditionTemplate = (rowData) => {
		if (rowData.conditions) {
			const listTemplate = (condition) => {
				return (
					<EllipsisTableCell>
						{condition.conditionSummary}
					</EllipsisTableCell>
				);
			};
			return (
				<>
					<ListTableCell template={listTemplate} listData={rowData.conditions} showBullets={true}/>
				</>
			);
		}
	};

	const conditionRelationTemplate = (props) => {
		return (
			<>
				<AutocompleteEditor
					autocompleteFields={["conditionSummary"]}
					rowProps={props}
					searchService={searchService}
					endpoint='experimental-condition'
					filterName='experimentalConditionFilter'
					fieldName='conditions'
					subField='conditionSummary'
					isMultiple={true}
				/>
				<ErrorMessageComponent
					errorMessages={errorMessages[props.rowIndex]}
					errorField="conditions"
				/>
			</>
		);
	};

	const handleEditor = (props) => {
		return (
			<>
				<InputTextEditor
					rowProps={props}
					fieldName={'handle'}
				/>
				<ErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={"handle"}/>
			</>
		);
	};

	const columns = [
		{
			field: "handle",
			header: "Handle",
			sortable: isEnabled,
			filter: true,
			body: (rowData) => rowData.handle,
			filterElement: {type: "input", filterName: "uniqueIdFilter", fields: ["handle"]},
			editor: (props) => handleEditor(props)
		},
		{
			field: "singleReference.curie",
			header: "Reference",
			sortable: isEnabled,
			filter: true,
			filterElement: {type: "input", filterName: "singleReferenceFilter", fields: ["singleReference.curie"]},
			editor: (props) => referenceEditorTemplate(props)
		},
		{
			field: "conditionRelationType.name",
			header: "Relation",
			sortable: isEnabled,
			filter: true,
			filterElement: {type: "multiselect", filterName: "conditionRelationFilter", fields: ["conditionRelationType.name"]},
			editor: (props) => conditionRelationTypeEditor(props)
		},
		{
			field: "conditions.conditionSummary",
			header: "Conditions",
			sortable: isEnabled,
			filter: true,
			body: conditionTemplate,
			filterElement: {type: "input", filterName: "experimentalConditionFilter", fields: ["conditions.conditionSummary"]},
			editor: (props) => conditionRelationTemplate(props)
		},

	];

	return (
		<div className="card">
			<Toast ref={toast_topleft} position="top-left"/>
			<Toast ref={toast_topright} position="top-right"/>
			<Messages ref={errorMessage}/>
			<GenericDataTable
				endpoint="condition-relation"
				tableName="Condition Relations Handles"
				columns={columns}
				aggregationFields={aggregationFields}
				nonNullFields={['handle', 'singleReference.curie']}
				isEditable={true}
				curieFields={["singleReference"]}
				idFields={["conditionRelationType"]}
				mutation={mutation}
				isEnabled={isEnabled}
				setIsEnabled={setIsEnabled}
				toasts={{toast_topleft, toast_topright }}
				initialColumnWidth={10}
				errorObject = {{errorMessages, setErrorMessages}}
			/>
		</div>
	)
}
