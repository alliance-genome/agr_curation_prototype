import { useImmerReducer } from 'use-immer';
import 'core-js/features/structured-clone';

const DEFAULT_ANNOTATION = {
	diseaseAnnotationSubject: {
		modEntityId: '',
	},
	assertedGenes: [],
	assertedAllele: null,
	relation: {
		name: '',
	},
	negated: false,
	diseaseAnnotationObject: {
		curie: '',
	},
	singleReference: {
		curie: '',
	},
	evidenceCodes: [],
	with: [],
	relatedNotes: [],
	conditionRelations: [],
	geneticSex: null,
	diseaseQualifiers: null,
	sgdStrainBackground: null,
	annotationType: null,
	diseaseGeneticModifierRelation: null,
	diseaseGeneticModifierAgm: [],
	diseaseGeneticModifierAlleles: [],
	diseaseGeneticModifierGenes: [],
	internal: false,
};
const initialNewAnnotationState = {
	newAnnotation: global.structuredClone(DEFAULT_ANNOTATION),
	errorMessages: {},
	relatedNotesErrorMessages: [],
	exConErrorMessages: [],
	submitted: false,
	newAnnotationDialog: false,
	showRelatedNotes: false,
	relatedNotesEditingRows: {},
	showConditionRelations: false,
	conditionRelationsEditingRows: {},
	isValid: false,
	isAssertedGeneEnabled: false,
	isAssertedAlleleEnabled: false,
};

const buildAnnotation = (rowData) => {
	return {
		diseaseAnnotationSubject:
			global.structuredClone(rowData.diseaseAnnotationSubject) || DEFAULT_ANNOTATION.diseaseAnnotationSubject,
		assertedGenes: global.structuredClone(rowData.assertedGenes) || DEFAULT_ANNOTATION.assertedGenes,
		assertedAllele: global.structuredClone(rowData.assertedAllele) || DEFAULT_ANNOTATION.assertedAllele,
		relation: global.structuredClone(rowData.relation) || DEFAULT_ANNOTATION.relation,
		negated: rowData.negated || DEFAULT_ANNOTATION.negated,
		diseaseAnnotationObject:
			global.structuredClone(rowData.diseaseAnnotationObject) || DEFAULT_ANNOTATION.diseaseAnnotationObject,
		singleReference: global.structuredClone(rowData.singleReference) || DEFAULT_ANNOTATION.singleReference,
		evidenceCodes: global.structuredClone(rowData.evidenceCodes) || DEFAULT_ANNOTATION.subject,
		with: global.structuredClone(rowData.with) || DEFAULT_ANNOTATION.with,
		relatedNotes:
			processDupRelatedNotes(global.structuredClone(rowData.relatedNotes)) || DEFAULT_ANNOTATION.relatedNotes,
		conditionRelations: global.structuredClone(rowData.conditionRelations) || DEFAULT_ANNOTATION.conditionRelations,
		geneticSex: global.structuredClone(rowData.geneticSex) || DEFAULT_ANNOTATION.geneticSex,
		diseaseQualifiers: global.structuredClone(rowData.diseaseQualifiers) || DEFAULT_ANNOTATION.diseaseQualifiers,
		sgdStrainBackground: global.structuredClone(rowData.sgdStrainBackground) || DEFAULT_ANNOTATION.sgdStrainBackground,
		annotationType: global.structuredClone(rowData.annotationType) || DEFAULT_ANNOTATION.annotationType,
		diseaseGeneticModifierRelation:
			global.structuredClone(rowData.diseaseGeneticModifierRelation) ||
			DEFAULT_ANNOTATION.diseaseGeneticModifierRelation,
		diseaseGeneticModifierAgms:
			global.structuredClone(rowData.diseaseGeneticModifierAgms) || DEFAULT_ANNOTATION.diseaseGeneticModifierAgms,
		diseaseGeneticModifierAlleles:
			global.structuredClone(rowData.diseaseGeneticModifierAlleles) || DEFAULT_ANNOTATION.diseaseGeneticModifierAlleles,
		diseaseGeneticModifierGenes:
			global.structuredClone(rowData.diseaseGeneticModifierGenes) || DEFAULT_ANNOTATION.diseaseGeneticModifierGenes,
		internal: rowData.internal || DEFAULT_ANNOTATION.internal,
	};
};

const processDupRelatedNotes = (notes) => {
	if (!notes) return;
	notes.forEach((note) => {
		if (note.id) {
			delete note.id;
		}
	});
	return notes;
};

const newAnnotationReducer = (draft, action) => {
	switch (action.type) {
		case 'RESET':
			return initialNewAnnotationState;
		case 'EDIT':
			draft.newAnnotation[action.field] = action.value;
			break;
		case 'EDIT_EXPERIMENT':
			if (typeof action.value === 'object') {
				draft.newAnnotation.conditionRelations[0] = action.value;
			} else {
				if (draft.newAnnotation.conditionRelations && draft.newAnnotation.conditionRelations[0])
					draft.newAnnotation.conditionRelations[0].handle = action.value;
			}
			break;
		case 'UPDATE_ERROR_MESSAGES':
			draft[action.errorType] = action.errorMessages;
			draft.isValid = false;
			break;
		case 'SUBMIT':
			draft.submitted = true;
			draft.errorMessages = {};
			draft.relatedNotesErrorMessages = [];
			draft.isValid = false;
			break;
		case 'OPEN_DIALOG':
			draft.newAnnotationDialog = true;
			break;
		case 'DUPLICATE_ROW':
			draft.newAnnotation = buildAnnotation(action.rowData);
			break;
		case 'CLEAR':
			return { ...initialNewAnnotationState, newAnnotationDialog: true };
		case 'ADD_NEW_NOTE':
			draft.newAnnotation.relatedNotes.push({
				dataKey: action.count,
				noteType: {
					name: '',
				},
				freeText: '',
			});
			draft.relatedNotesEditingRows[`${action.count}`] = true;
			draft.showRelatedNotes = true;
			break;
		case 'ADD_NEW_RELATION':
			draft.newAnnotation.conditionRelations.push({
				dataKey: action.count,
				conditions: [],
			});
			draft.conditionRelationsEditingRows[`${action.count}`] = true;
			draft.showConditionRelations = true;
			break;
		case 'DELETE_ROW':
			draft.newAnnotation[action.tableType].splice(action.index, 1);
			if (draft.newAnnotation[action.tableType].length === 0) {
				draft[action.showType] = false;
			}
			break;
		case 'EDIT_ROW':
			draft.newAnnotation[action.tableType][action.index][action.field] = action.value;
			break;
		case 'SET_IS_ENABLED':
			draft.isEnabled = action.value;
			break;
		case 'SET_IS_ASSERTED_GENE_ENABLED':
			draft.isAssertedGeneEnabled = action.value;
			break;
		case 'SET_IS_ASSERTED_ALLELE_ENABLED':
			draft.isAssertedAlleleEnabled = action.value;
			break;
		case 'SET_RELATED_NOTES_EDITING_ROWS':
			action.relatedNotes.forEach((note) => {
				draft.relatedNotesEditingRows[`${note.dataKey}`] = true;
			});
			draft.showRelatedNotes = true;
			break;
		case 'SET_CONDITION_RELATIONS_EDITING_ROWS':
			action.conditionRelations.forEach((relation) => {
				draft.conditionRelationsEditingRows[`${relation.dataKey}`] = true;
			});
			draft.showConditionRelations = true;
			break;
		default:
			throw Error('Unknown action: ' + action.type);
	}
};

export const useNewAnnotationReducer = () => {
	const [newAnnotationState, newAnnotationDispatch] = useImmerReducer(newAnnotationReducer, initialNewAnnotationState);
	return { newAnnotationState, newAnnotationDispatch };
};
