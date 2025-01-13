import { confirmDialog } from 'primereact/confirmdialog';
import { SORT_FIELDS } from '../constants/SortFields';

import { FIELD_SETS } from '../constants/FilterFields';
import { ValidationService } from '../service/ValidationService';

export function returnSorted(event, originalSort) {
	let found = false;
	let replace = false;
	let newSort = [...originalSort];

	if (event.multiSortMeta.length > 0) {
		newSort.forEach((o) => {
			if (o.field === event.multiSortMeta[0].field) {
				if (o.order === event.multiSortMeta[0].order) {
					replace = true;
					found = true;
				} else {
					o.order = event.multiSortMeta[0].order;
					found = true;
				}
			}
		});
	} else {
		newSort = [];
	}

	if (!found) {
		return newSort.concat(event.multiSortMeta);
	} else {
		if (replace) {
			return event.multiSortMeta;
		} else {
			return newSort;
		}
	}
}

export function trimWhitespace(value) {
	return value?.replace(/\s{2,}/g, ' ').trim();
}

export function filterColumns(columns, selectedColumnNames) {
	const filteredColumns = columns.filter((col) => {
		return selectedColumnNames?.includes(col.header);
	});
	return filteredColumns;
}

export function orderColumns(columns, orderedColumnNames) {
	if (!orderedColumnNames) return columns;
	let orderedColumns = [];
	orderedColumnNames.forEach((columnName) => {
		var column = columns.find((col) => col.header === columnName); // Once column names change it can't be assumed that everyones settings are changed.
		if (column) {
			orderedColumns.push(column);
		}
	});
	return orderedColumns;
}
export function orderColumnFields(
	columns,
	orderedColumnNames,
	isEditable,
	deletionEnabled,
	duplicationEnabled,
	hasDetails
) {
	if (!orderedColumnNames) return columns;
	let orderedColumnFields = [];
	orderedColumnNames.forEach((columnName) => {
		let column = columns.find((col) => col.header === columnName);
		if (column) {
			orderedColumnFields.push(column.field);
		}
	});

	if (hasDetails) orderedColumnFields.unshift('details');
	if (duplicationEnabled) orderedColumnFields.unshift('duplicate');
	if (deletionEnabled) orderedColumnFields.unshift('delete');
	if (isEditable) orderedColumnFields.unshift('rowEditor');

	return orderedColumnFields;
}

export function reorderArray(array, from, to) {
	const item = array.splice(from, 1);
	array.splice(to, 0, item[0]);
	return array;
}

export function restoreTableState(
	columns,
	dataTable,
	orderedColumnNames,
	isEditable,
	deletionEnabled,
	duplicationEnabled,
	hasDetails,
	tableState
) {
	let initalColumnOrderFields = orderColumnFields(
		columns,
		orderedColumnNames,
		isEditable,
		deletionEnabled,
		duplicationEnabled,
		hasDetails
	);

	const newState = {
		first: tableState.first,
		rows: tableState.rows,
		multisortmeta: tableState.multisortmeta,
		filters: tableState.filters,
		columnWidths: '',
		tableWidth: '',
		columnOrder: initalColumnOrderFields,
	};

	dataTable.current.restoreTableState(newState);
}

// ToDo: Create enumeration
export function getEntityType(entity) {
	if (entity.curie && entity.curie.startsWith('AGR:AGR-Reference-')) {
		return 'Literature';
	}
	if (entity.conditionSummary) {
		return 'Experiment Condition';
	}
	return 'Unknown Entity';
}

export function getIdentifier(data) {
	if (!data) {
		return null;
	}
	if (data.primaryExternalId) {
		return data.primaryExternalId;
	}
	if (data.modInternalId) {
		return data.modInternalId;
	}
	if (data.curie) {
		return data.curie;
	}
	return '';
}

export const getGenomicEntityText = (genomicEntity) => {
	if (genomicEntity.geneSymbol) return genomicEntity.geneSymbol.displayText;
	if (genomicEntity.alleleSymbol) return genomicEntity.alleleSymbol.displayText;
	if (genomicEntity.geneFullName) return genomicEntity.geneFullName.displayText;
	if (genomicEntity.alleleFullName) return genomicEntity.alleleFullName.displayText;
	if (genomicEntity.name) return genomicEntity.name;
	return null;
};

export function getRefStrings(referenceItems) {
	if (!referenceItems) return;

	let refStrings = [];
	referenceItems.forEach((referenceItem) => {
		refStrings.push(getRefString(referenceItem));
	});

	return refStrings.sort();
}

export function getRefString(referenceItem) {
	if (!referenceItem) return;

	if (!referenceItem.cross_references && !referenceItem.crossReferences) return referenceItem.curie;

	let xrefCuries = [];
	if (referenceItem.cross_references) {
		referenceItem.cross_references.forEach((x, i) => xrefCuries.push(x.curie));
	} else {
		referenceItem.crossReferences.forEach((x, i) => xrefCuries.push(x.referencedCurie));
	}

	if (xrefCuries.length === 0) return referenceItem.curie;

	if (xrefCuries.length === 1) return xrefCuries[0] + ' (' + referenceItem.curie + ')';

	let sortedCuries = [];
	if (indexWithPrefix(xrefCuries, 'PMID:') > -1) {
		sortedCuries.push(xrefCuries.splice(indexWithPrefix(xrefCuries, 'PMID:'), 1));
	}
	if (indexWithPrefix(xrefCuries, 'FB:') > -1) {
		sortedCuries.push(xrefCuries.splice(indexWithPrefix(xrefCuries, 'FB:'), 1));
	}
	if (indexWithPrefix(xrefCuries, 'MGI:') > -1) {
		sortedCuries.push(xrefCuries.splice(indexWithPrefix(xrefCuries, 'MGI:'), 1));
	}
	if (indexWithPrefix(xrefCuries, 'RGD:') > -1) {
		sortedCuries.push(xrefCuries.splice(indexWithPrefix(xrefCuries, 'RGD:'), 1));
	}
	if (indexWithPrefix(xrefCuries, 'SGD:') > -1) {
		sortedCuries.push(xrefCuries.splice(indexWithPrefix(xrefCuries, 'SGD:'), 1));
	}
	if (indexWithPrefix(xrefCuries, 'WB:') > -1) {
		sortedCuries.push(xrefCuries.splice(indexWithPrefix(xrefCuries, 'WB:'), 1));
	}
	if (indexWithPrefix(xrefCuries, 'ZFIN:') > -1) {
		sortedCuries.push(xrefCuries.splice(indexWithPrefix(xrefCuries, 'ZFIN:'), 1));
	}
	if (xrefCuries.length > 0) {
		sortedCuries = sortedCuries.concat(xrefCuries.sort());
	}
	sortedCuries.push(referenceItem.curie);

	let primaryXrefCurie = sortedCuries.splice(0, 1);

	return primaryXrefCurie + ' (' + sortedCuries.join('|') + ')';
}

function indexWithPrefix(array, prefix) {
	for (var i = 0; i < array.length; i++) {
		if (array[i].startsWith(prefix)) {
			return i;
		}
	}
	return -1;
}

export function genericConfirmDialog({ header, message, accept, reject }) {
	confirmDialog({
		message,
		header,
		acceptClassName: 'p-button-danger',
		icon: 'pi pi-info-circle',
		accept,
		reject,
	});
}

function containsMatch(inputValue, selectedItem) {
	for (const part of inputValue.split(/[^a-z0-9]/i)) {
		if (part.length > 0 && selectedItem.indexOf(part) !== -1) return 1;
	}
	return 0;
}

export function filterDropDownObject(inputValue, object) {
	const trimmedValue = trimWhitespace(inputValue.toLowerCase());
	let _object = global.structuredClone(object);

	if (_object.geneSystematicName) {
		if (containsMatch(trimmedValue, _object.geneSystematicName.displayText.toString().toLowerCase()) === 0)
			_object = { ..._object, geneSystematicName: {} };
	}

	const listFields = new Map([
		['synonyms', 'name'],
		['crossReferences', 'displayName'],
		['cross_references', 'curie'],
		['secondaryIdentifiers', ''],
		['geneSynonyms', 'displayText'],
		['geneSecondaryIds', 'secondaryId'],
		['alleleSynonyms', 'displayText'],
		['alleleSecondaryIds', 'secondaryId'],
	]);

	listFields.forEach(function (subField, field) {
		if (_object[field] && _object[field]?.length > 0) {
			const filteredItems = [];
			_object[field].forEach((item) => {
				let selectedItemValue = '';
				if ((field === 'synonyms' && !item.name) || subField === '') {
					selectedItemValue = item;
				} else {
					selectedItemValue = item[subField];
				}

				if (containsMatch(trimmedValue, selectedItemValue.toString().toLowerCase())) filteredItems.push(item);
			});
			_object[field] = filteredItems;
		}
	});

	return _object;
}

export function onSelectionOver(event, item, query, op, setAutocompleteHoverItem) {
	const _item = filterDropDownObject(query, item);
	setAutocompleteHoverItem(_item);
	op.current.show(event);
}

export function autocompleteSearch(
	searchService,
	endpoint,
	filterName,
	filter,
	setSuggestions,
	otherFilters = {},
	applyObsoleteFilter = true
) {
	const obsoleteFilter =
		applyObsoleteFilter && endpoint !== 'literature-reference'
			? {
					obsoleteFilter: {
						obsolete: {
							queryString: false,
						},
					},
				}
			: {};
	searchService
		.search(endpoint, 15, 0, [], { [filterName]: filter, ...otherFilters, ...obsoleteFilter })
		.then((data) => {
			if (data.results?.length > 0) {
				setSuggestions(data.results);
			} else {
				setSuggestions([]);
			}
		});
}

export function buildAutocompleteFilter(event, autocompleteFields) {
	let filter = {};
	autocompleteFields.forEach((field) => {
		filter[field] = {
			queryString: event.query,
			tokenOperator: 'AND',
			useKeywordFields: true,
		};
	});

	return filter;
}

export function defaultAutocompleteOnChange(rowProps, event, fieldName, setFieldValue, subField = 'curie') {
	const index = rowProps.props.rows ? rowProps.rowIndex % rowProps.props.rows : rowProps.rowIndex;

	let updatedRows = [...rowProps.props.value];

	if (!event.target.value) {
		updatedRows[index][fieldName] = null;
		setFieldValue('');
		return;
	}

	if (typeof event.target.value === 'object') {
		updatedRows[index][fieldName] = event.target.value;
		setFieldValue(updatedRows[rowProps.rowIndex][fieldName]?.[subField]);
	} else {
		updatedRows[index][fieldName] = {};
		updatedRows[index][fieldName][subField] = event.target.value;
		setFieldValue(updatedRows[index][fieldName]?.[subField]);
	}
}

export function multipleAutocompleteOnChange(rowProps, event, fieldName, setFieldValue) {
	let updatedRows = [...rowProps.props.value];
	const index = rowProps.props.rows ? rowProps.rowIndex % rowProps.props.rows : rowProps.rowIndex;
	if (!event.target.value) {
		updatedRows[index][fieldName] = null;
		setFieldValue('');
		return;
	}
	let nonDuplicateRows = [];
	if (event.target.value.length > 0) {
		nonDuplicateRows = getUniqueItemsByProperty(event.target.value, 'id');
	}
	updatedRows[index][fieldName] = nonDuplicateRows;
	setFieldValue(updatedRows[index][fieldName]);
}

const isPropValuesEqual = (subject, target, propName) => {
	return subject[propName] === target[propName];
};

export function getUniqueItemsByProperty(items, propName) {
	return items.filter(
		(item, index, array) => index === array.findIndex((foundItem) => isPropValuesEqual(foundItem, item, propName))
	);
}

export function validateBioEntityFields(
	updatedRow,
	setUiErrorMessages,
	event,
	setIsInEditMode,
	closeRowRef,
	areUiErrors
) {
	const bioEntityFieldNames = ['diseaseAnnotationSubject', 'sgdStrainBackground', 'assertedAllele'];

	bioEntityFieldNames.forEach((field) => {
		if (updatedRow[field] && Object.keys(updatedRow[field]).length === 1) {
			const errorObject = {
				severity: 'error',
				message: 'Must select from autosuggest',
			};
			setUiErrorMessages((uiErrorMessages) => {
				const _uiErrorMessages = global.structuredClone(uiErrorMessages);
				if (!_uiErrorMessages[event.index]) _uiErrorMessages[event.index] = {};
				_uiErrorMessages[event.index][field] = errorObject;
				return _uiErrorMessages;
			});

			setIsInEditMode(false);
			areUiErrors.current = true;
		} else {
			setUiErrorMessages((uiErrorMessages) => {
				const _uiErrorMessages = global.structuredClone(uiErrorMessages);
				if (_uiErrorMessages[event.index]) _uiErrorMessages[event.index][field] = null;
				return _uiErrorMessages;
			});
		}
	});
}

export function validateRequiredFields(newAnnotationForm, uiErrorMessages, setUiErrorMessages, areUiErrors, mod) {
	const fields = ['relation', 'diseaseAnnotationObject', 'singleReference', 'evidenceCodes'];

	const isEmptyObject = (field) => {
		if (Object.keys(newAnnotationForm[field]).length !== 0) {
			for (let key in newAnnotationForm[field]) {
				if (newAnnotationForm[field][key] === '') {
					return true;
				} else {
					return false;
				}
			}
		}
	};

	let _uiErrorMessages = {};
	fields.forEach((field) => {
		if (newAnnotationForm[field] === null || newAnnotationForm[field].length === 0 || isEmptyObject(field)) {
			_uiErrorMessages[field] = 'This is a required field';
			setUiErrorMessages({ ..._uiErrorMessages });
			areUiErrors.current = true;
		}
	});
	if (mod.includes('ZFINStaff')) {
		if (newAnnotationForm.conditionRelations.length === 0 || !newAnnotationForm.conditionRelations[0]['handle']) {
			_uiErrorMessages['conditionRelations'] = 'This is a required field';
			setUiErrorMessages({ ..._uiErrorMessages });
			areUiErrors.current = true;
		}
	}
}

export function validateFormBioEntityFields(newAnnotationForm, uiErrorMessages, setUiErrorMessages, areUiErrors) {
	const bioEntityFieldNames = ['diseaseAnnotationSubject', 'sgdStrainBackground', 'assertedAllele'];

	bioEntityFieldNames.forEach((field) => {
		if (
			newAnnotationForm[field] &&
			!Object.keys(newAnnotationForm['diseaseAnnotationSubject']).includes('curie') &&
			!Object.keys(newAnnotationForm['diseaseAnnotationSubject']).includes('primaryExternalId') &&
			!Object.keys(newAnnotationForm['diseaseAnnotationSubject']).includes('modInternalId')
		) {
			const _uiErrorMessages = {};
			_uiErrorMessages[field] = 'Must select from autosuggest';
			setUiErrorMessages({ ..._uiErrorMessages });
			areUiErrors.current = true;
		}
	});
}

export const removeInvalidFilters = (currentFilters) => {
	const currentFiltersCopy = global.structuredClone(currentFilters);

	if (currentFiltersCopy && Object.keys(currentFiltersCopy).length > 0) {
		const invalidFilters = [];

		let validFilters = {};
		Object.entries(FIELD_SETS).forEach(([key, value]) => {
			validFilters[[FIELD_SETS[key].filterName]] = value;
		});

		for (let filterName in currentFiltersCopy) {
			if (validFilters[filterName]) {
				let validFields = validFilters[filterName].fields;
				const invalidFields = [];
				for (let fieldName in currentFiltersCopy[filterName]) {
					if (!validFields.includes(fieldName)) {
						invalidFields.push(fieldName);
					}
				}
				invalidFields.forEach((fieldName) => {
					delete currentFiltersCopy[filterName][fieldName];
				});
				if (Object.keys(currentFiltersCopy[filterName]).length === 0) {
					delete currentFiltersCopy[filterName];
				}
			} else {
				invalidFilters.push(filterName);
			}
		}
		invalidFilters.forEach((filterName) => {
			delete currentFiltersCopy[filterName];
		});
	}

	return currentFiltersCopy;
};

export const removeInvalidSorts = (currentSorts) => {
	const currentSortsCopy = global.structuredClone(currentSorts);

	let invalidSorts = [];
	if (!currentSortsCopy || currentSortsCopy.length === 0) {
		return currentSortsCopy;
	} else {
		currentSortsCopy.forEach((sort) => {
			if (!SORT_FIELDS.includes(sort.field)) invalidSorts.push(sort.field);
		});
		invalidSorts.forEach((field) => {
			currentSortsCopy.splice(
				currentSortsCopy.findIndex((sort) => sort.field === field),
				1
			);
		});
	}

	return currentSortsCopy;
};

export const validate = async (entities, endpoint, validationService) => {
	const validationResultsArray = [];
	for (const entity of entities) {
		const result = await validationService.validate(endpoint, entity);
		validationResultsArray.push(result);
	}
	return validationResultsArray;
};

export const validateTable = async (endpoint, errorType, table, dispatch) => {
	if (!table) return false;
	const validationService = new ValidationService();
	const results = await validate(table, endpoint, validationService);
	const errors = [];
	let anyErrors = false;
	results.forEach((result, index) => {
		const { isError, data } = result;
		if (isError) {
			errors[index] = {};
			if (!data) return;
			Object.keys(data).forEach((field) => {
				errors[index][field] = {
					severity: 'error',
					message: data[field],
				};
			});
			anyErrors = true;
		}
	});
	dispatch({ type: 'UPDATE_ERROR_MESSAGES', errorType: errorType, errorMessages: errors });
	return anyErrors;
};

//handles optional autocomplete fields so that a string isn't sent to the backend
//when a value is removed or not selected from the dropdown
export const processOptionalField = (eventValue) => {
	if (!eventValue || eventValue === '') return null;
	if (!eventValue.curie) return { curie: eventValue };
	return eventValue;
};

export const setNewEntity = (tableState, setEntities, newEntity, queryClient) => {
	if (!tableState || !setEntities || !newEntity) return;

	if (
		(tableState.filters && Object.keys(tableState.filters).length > 0) ||
		(tableState.multiSortMeta && tableState.multiSortMeta.length > 0) ||
		tableState.page > 0
	) {
		//Invalidating the query immediately after success leads to api results that don't always include the new entity
		setTimeout(() => {
			queryClient.invalidateQueries([tableState.tableKeyName]);
		}, 1000);
		return;
	}

	// Adds new entity to the top of the entities array when there are no filters, sorting, or pagination applied.
	setEntities((previousEntities) => {
		return [newEntity, ...previousEntities];
	});
};
