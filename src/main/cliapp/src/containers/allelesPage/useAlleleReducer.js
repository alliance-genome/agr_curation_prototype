import { useImmerReducer } from "use-immer";

const initialAlleleState = {
	allele: {
		taxon: {
			curie: "",
		},
		alleleSynonyms: [],
		alleleFullName: null,
		alleleSecondaryIds: [],
		alleleMutationTypes: [],
		alleleInheritanceModes: [],
		alleleFunctionalImpacts: [],
		references: [],
		inCollection: {
			name: "",
		},
		isExtinct: false,
		internal: false,
		obsolete: false,
	},
	entityStates: {
		alleleSynonyms: {
			field: 'alleleSynonyms',
			show: false,
			errorMessages: [],
			editingRows: {},
			type: "table",
		},
		alleleFullName: {
			field: 'alleleFullName',
			show: false,
			errorMessages: [],
			editingRows: {},
			type: "object",
		},
		alleleSecondaryIds: {
			field: 'alleleSecondaryIds',
			show: false,
			errorMessages: [],
			editingRows: {},
			type: "table",
		},
		alleleMutationTypes: {
			field: 'alleleMutationTypes',
			show: false,
			errorMessages: [],
			editingRows: {},
			type: "table",
		},
		alleleInheritanceModes: {
			field: 'alleleInheritanceModes',
			show: false,
			errorMessages: [],
			editingRows: {},
			type: "table",
		},
		alleleFunctionalImpacts: {
			field: 'alleleFunctionalImpacts',
			show: false,
			errorMessages: [],
			editingRows: {},
			type: "table",
		},
		alleleDatabaseStatus: {
			field: 'alleleDatabaseStatus',
			show: false,
			errorMessages: [],
			editingRows: {},
			type: "object",
		},
	},
	errorMessages: {},
	submitted: false,
};

const processTable = (field, allele, draft) => {
	if(!allele) return;

	if(!allele[field]) {
		allele[field] = [];
		return; 
	}

	let clonableEntities = global.structuredClone(allele[field]);
	clonableEntities.forEach((entity, index) => {
		entity.dataKey = index;
		draft.entityStates[field].editingRows[`${entity.dataKey}`] = true;
	});

	allele[field] = clonableEntities;
	draft.entityStates[field].show = true;
}
const processObject = (field, allele, draft) => {
	if(!allele) return;

	if(!allele[field]) return; 

	allele[field].dataKey = 0;
	draft.entityStates[field].editingRows[0] = true;
	draft.entityStates[field].show = true;
}

const alleleReducer = (draft, action) => {
	switch (action.type) {
		case 'SET':
			const allele = action.value;

			let states = Object.values(draft.entityStates);

			states.forEach((state) => {
				if(state.type === "table") processTable(state.field, allele, draft); 
				if(state.type === "object") processObject(state.field, allele, draft); 
			})

			draft.allele = allele;
			break;
		case 'RESET':
			draft.allele = initialAlleleState.allele;
			draft.errorMessages = {};
			draft.submitted = false;
			break;
		case 'EDIT':
			draft.allele[action.field] = action.value;
			break;
		case 'EDIT_ROW': 
			draft.allele[action.entityType][action.index][action.field] = action.value;
			break;
		case 'EDIT_OBJECT': 
			draft.allele[action.entityType][action.field] = action.value;
			break;
		case 'ADD_ROW': 
			draft.allele[action.entityType].push(action.row);
			draft.entityStates[action.entityType].editingRows[`${action.row.dataKey}`] = true;
			draft.entityStates[action.entityType].show = true;
			break;
		case 'ADD_OBJECT': 
			draft.allele[action.entityType] = action.value
			draft.entityStates[action.entityType].editingRows[`${action.value.dataKey}`] = true;
			draft.entityStates[action.entityType].show = true;
			break;
		case 'DELETE_ROW':
			draft.allele[action.entityType].splice(action.index, 1);
			if(draft.allele[action.entityType].length === 0){
				draft.entityStates[action.entityType].show = false;
			}
			break;
		case 'DELETE_OBJECT': 
			draft.allele[action.entityType] = null;
			draft.entityStates[action.entityType].show = false;
			break;
		case 'UPDATE_ERROR_MESSAGES': 
			draft.errorMessages = action.errorMessages;
			break;
		case 'UPDATE_TABLE_ERROR_MESSAGES': 
			draft.entityStates[action.entityType].errorMessages = action.errorMessages;
			break;
		case 'SUBMIT':
			draft.submitted = true;
			draft.errorMessages = {};

			states = Object.values(draft.entityStates);

			states.forEach((state) => {
				state.errorMessages = [];
			})

			break;
		default:
      throw Error('Unknown action: ' + action.type);
	}
};

export const useAlleleReducer = () => {
	const [alleleState, alleleDispatch] = useImmerReducer(alleleReducer, initialAlleleState);
	return {alleleState, alleleDispatch};
}
