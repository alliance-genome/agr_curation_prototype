import { describe, expect, it } from '@jest/globals';
import { getRefString, removeInvalidFilters, removeInvalidSorts } from '../utils';
import 'core-js/features/structured-clone';

describe('removeInvalidFilters', () => {
	it('All Valid fields', () => {
		const localStorageFilters = {
			objectFilter: {
				'diseaseAnnotationObject.name': {
					queryString: 'j',
					tokenOperator: 'AND',
				},
				'diseaseAnnotationObject.curie': {
					queryString: 'j',
					tokenOperator: 'AND',
				},
			},
			negatedFilter: {
				negated: {
					queryString: 'true',
					tokenOperator: 'OR',
				},
			},
			primaryexternalidFilter: {
				primaryExternalId: {
					queryString: 's',
					tokenOperator: 'AND',
				},
			},
		};
		const newFilters = removeInvalidFilters(localStorageFilters);
		expect(newFilters).toEqual(localStorageFilters);
	});

	it('One invalid field', () => {
		const localStorageFilters = {
			objectFilter: {
				'diseaseAnnotationObject.invalidName': {
					queryString: 'j',
					tokenOperator: 'AND',
				},
				'diseaseAnnotationObject.curie': {
					queryString: 'j',
					tokenOperator: 'AND',
				},
			},
			negatedFilter: {
				negated: {
					queryString: 'true',
					tokenOperator: 'OR',
				},
			},
			primaryexternalidFilter: {
				primaryExternalId: {
					queryString: 's',
					tokenOperator: 'AND',
				},
			},
		};

		const newFilters = removeInvalidFilters(localStorageFilters);

		expect(newFilters).toEqual({
			objectFilter: {
				'diseaseAnnotationObject.curie': {
					queryString: 'j',
					tokenOperator: 'AND',
				},
			},
			negatedFilter: {
				negated: {
					queryString: 'true',
					tokenOperator: 'OR',
				},
			},
			primaryexternalidFilter: {
				primaryExternalId: {
					queryString: 's',
					tokenOperator: 'AND',
				},
			},
		});
	});

	it('All invalid fields', () => {
		const localStorageFilters = {
			objectFilter: {
				'diseaseAnnotationObject.invalidName': {
					queryString: 'j',
					' tokenOperator': 'AND',
				},
				'diseaseAnnotationObject.invalidCurie': {
					queryString: 'j',
					tokenOperator: 'AND',
				},
			},
			negatedFilter: {
				invalidNegated: {
					queryString: 'true',
					tokenOperator: 'OR',
				},
			},
			primaryexternalidFilter: {
				invalidPrimaryExternalId: {
					queryString: 's',
					tokenOperator: 'AND',
				},
			},
		};

		const newFilters = removeInvalidFilters(localStorageFilters);
		expect(newFilters).toEqual({});
	});
});

describe('removeInvalidSorts', () => {
	it('All Valid fields', () => {
		const localMultiSortMeta = [
			{
				field: 'negated',
				order: 1,
			},
			{
				field: 'diseaseAnnotationObject.name',
				order: 1,
			},
			{
				field: 'diseaseAnnotationSubject.symbol',
				order: 1,
			},
		];
		const newSorts = removeInvalidSorts(localMultiSortMeta);
		expect(newSorts).toEqual(localMultiSortMeta);
	});

	it('One invalid field', () => {
		const localMultiSortMeta = [
			{
				field: 'invalidNegated',
				order: 1,
			},
			{
				field: 'diseaseAnnotationObject.name',
				order: 1,
			},
			{
				field: 'diseaseAnnotationSubject.symbol',
				order: 1,
			},
		];

		const newSorts = removeInvalidSorts(localMultiSortMeta);
		expect(newSorts).toEqual([
			{
				field: 'diseaseAnnotationObject.name',
				order: 1,
			},
			{
				field: 'diseaseAnnotationSubject.symbol',
				order: 1,
			},
		]);
	});

	it('All invalid fields', () => {
		const localMultiSortMeta = [
			{
				field: 'invalidNegated',
				order: 1,
			},
			{
				field: 'diseaseAnnotationObject.invalidName',
				order: 1,
			},
			{
				field: 'diseaseAnnotationSubject.invalidSymbol',
				order: 1,
			},
		];

		const newSorts = removeInvalidSorts(localMultiSortMeta);
		expect(newSorts).toEqual([]);
	});
});

describe('getRefString', () => {
	it('orders curies by prefix then alphabetically ', () => {
		const referenceItem = {
			curie: 'CURIE',
			cross_references: [
				{ curie: 'PMID:123' },
				{ curie: 'FB:456' },
				{ curie: 'MGI:789' },
				{ curie: 'RGD:012' },
				{ curie: 'SGD:345' },
				{ curie: 'WB:678' },
				{ curie: 'ZFIN:901' },
				{ curie: 'XYZ:234' },
				{ curie: 'ABC:567' },
			],
		};

		const result = getRefString(referenceItem);

		expect(result).toBe('PMID:123 (FB:456|MGI:789|RGD:012|SGD:345|WB:678|ZFIN:901|ABC:567|XYZ:234|CURIE)');
	});

	it('should return without any errors when referenceItem is null', () => {
		const referenceItem = null;
		expect(getRefString(referenceItem)).toBeUndefined();
	});

	it('should return referenceItem.curie when referenceItem has no cross_references or crossReferences', () => {
		const referenceItem = {
			curie: 'CURIE',
		};
		expect(getRefString(referenceItem)).toBe(referenceItem.curie);
	});

	it('should return referenceItem.curie when referenceItem has cross_references and crossReferences with empty arrays', () => {
		const referenceItem = {
			curie: 'CURIE',
			cross_references: [],
			crossReferences: [],
		};
		expect(getRefString(referenceItem)).toBe(referenceItem.curie);
	});
});
