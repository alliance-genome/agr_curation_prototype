import { useImmerReducer } from "use-immer";

const initialAlleleState = {
	allele: {
		taxon: {
			curie: "",
		},
		references: [],
		inCollection: {
			name: "",
		},
		isExtinct: false,
	},
	errorMessages: {},
	submitted: false,
};

const alleleReducer = (draft, action) => {
	switch (action.type) {
		case 'SET':
			draft.allele = action.value;
			break;
		case 'RESET':
			draft.allele = initialAlleleState.allele;
			draft.errorMessages = {};
			draft.submitted = false;
			break;
		case 'EDIT':
			draft.allele[action.field] = action.value;
			break;
		case 'UPDATE_ERROR_MESSAGES':
			draft.errorMessages = action.errorMessages;
			break;
		case 'SUBMIT':
			draft.submitted = true;
			break;
		default:
      throw Error('Unknown action: ' + action.type);
	}
};

export const useAlleleReducer = () => {
	const [alleleState, alleleDispatch] = useImmerReducer(alleleReducer, initialAlleleState);
	return {alleleState, alleleDispatch};
}
