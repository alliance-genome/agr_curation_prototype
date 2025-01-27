import { BaseAuthService } from './BaseAuthService';

export class SearchService extends BaseAuthService {
	search(endpoint, rows, page, sorts, filters, sortMapping, aggregationFields, nonNullFieldsTable) {
		const searchOptions = {};
		if (!sorts) {
			sorts = [];
		}

		if (!aggregationFields) {
			aggregationFields = [];
		}

		if (!nonNullFieldsTable) {
			nonNullFieldsTable = [];
		}

		searchOptions['searchFilters'] = filters;
		searchOptions['sortOrders'] = includeSecondarySorts(sorts, sortMapping);
		searchOptions['aggregations'] = aggregationFields;
		searchOptions['nonNullFieldsTable'] = nonNullFieldsTable;

		const siteSettings = JSON.parse(localStorage.getItem('siteSettings'));
		searchOptions['debug'] = siteSettings?.debug === 'true' ? 'true' : 'false';
		return this.api.post(`/${endpoint}/search?limit=${rows}&page=${page}`, searchOptions).then((res) => res.data);
	}

	find(endpoint, rows, page, findOptions) {
		//console.log(findOptions);
		return this.api.post(`/${endpoint}/find?limit=${rows}&page=${page}`, findOptions).then((res) => res.data);
	}
}

function includeSecondarySorts(sorts, sortMapping) {
	const newSorts = [];

	sorts.forEach((sort) => {
		newSorts.push(sort);
		if (sortMapping && sort.field in sortMapping) {
			sortMapping[sort.field].forEach((field) => {
				let newSort = {};
				newSort['field'] = field;
				newSort['order'] = sort.order;
				newSorts.push(newSort);
			});
		}
	});

	return newSorts;
}
