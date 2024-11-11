const modTableSettings = {
	RGDStaff: {
		DiseaseAnnotations: {
			page: 0,
			first: 0,
			rows: 50,
			multiSortMeta: [],
			selectedColumnNames: [
				'Subject',
				'Disease Relation',
				'NOT',
				'Disease',
				'Reference',
				'Evidence Code',
				'Related Notes',
				'Experimental Conditions',
				'Disease Qualifiers',
				'Inferred Gene',
				'Asserted Genes',
				'Inferred Allele',
				'Asserted Allele',
				'Data Provider',
				'Secondary Data Provider',
				'Updated By',
				'Date Updated',
				'Created By',
				'Date Created',
				'Internal',
				'Obsolete',
			],
			selectedFormFields: [
				'Subject',
				'Disease Relation',
				'NOT',
				'Disease',
				'Reference',
				'Evidence Code',
				'Related Notes',
				'Experimental Conditions',
				'Disease Qualifiers',
				'Asserted Genes',
				'Asserted Allele',
			],
			filters: {
				dataProviderFilter: {
					'dataProvider.sourceOrganization.abbreviation': { queryString: 'RGD', tokenOperator: 'AND' },
					'dataProvider.sourceOrganization.fullName': { queryString: 'RGD', tokenOperator: 'AND' },
					'dataProvider.sourceOrganization.shortName': { queryString: 'RGD', tokenOperator: 'AND' },
				},
				obsoleteFilter: {
					obsolete: { queryString: 'false' },
				},
			},
			tableKeyName: 'DiseaseAnnotations',
			tableSettingsKeyName: 'DiseaseAnnotationsTableSettings',
		},
	},
	SGDStaff: {
		DiseaseAnnotations: {
			page: 0,
			first: 0,
			rows: 50,
			multiSortMeta: [],
			selectedColumnNames: [
				'Subject',
				'Disease Relation',
				'NOT',
				'Disease',
				'Reference',
				'Evidence Code',
				'With',
				'SGD Strain Background',
				'Annotation Type',
				'Data Provider',
				'Updated By',
				'Date Updated',
				'Created By',
				'Date Created',
				'Internal',
				'Obsolete',
			],
			selectedFormFields: [
				'Subject',
				'Disease Relation',
				'NOT',
				'Disease',
				'Reference',
				'Evidence Code',
				'With',
				'SGD Strain Background',
				'Annotation Type',
			],
			filters: {
				dataProviderFilter: {
					'dataProvider.sourceOrganization.abbreviation': { queryString: 'SGD', tokenOperator: 'AND' },
					'dataProvider.sourceOrganization.fullName': { queryString: 'SGD', tokenOperator: 'AND' },
					'dataProvider.sourceOrganization.shortName': { queryString: 'SGD', tokenOperator: 'AND' },
				},
				obsoleteFilter: {
					obsolete: { queryString: 'false' },
				},
			},
			tableKeyName: 'DiseaseAnnotations',
			tableSettingsKeyName: 'DiseaseAnnotationsTableSettings',
		},
	},
	WBStaff: {
		DiseaseAnnotations: {
			page: 0,
			rows: 50,
			first: 0,
			multiSortMeta: [{ field: 'dateCreated', order: -1 }],
			selectedColumnNames: [
				'MOD Annotation ID',
				'Subject',
				'Asserted Genes',
				'Disease Relation',
				'NOT',
				'Disease',
				'Reference',
				'Evidence Code',
				'Related Notes',
				'Experimental Conditions',
				'Genetic Sex',
				'Genetic Modifier Relation',
				'Genetic Modifier AGMs',
				'Genetic Modifier Alleles',
				'Genetic Modifier Genes',
				'Data Provider',
				'Updated By',
				'Date Updated',
				'Created By',
				'Date Created',
				'Internal',
				'Obsolete',
			],
			selectedFormFields: [
				'Subject',
				'Disease Relation',
				'NOT',
				'Disease',
				'Reference',
				'Evidence Code',
				'Related Notes',
				'Experimental Conditions',
				'Genetic Sex',
				'Genetic Modifier Relation',
				'Genetic Modifier AGMs',
				'Genetic Modifier Alleles',
				'Genetic Modifier Genes',
				'Asserted Genes',
			],
			filters: {
				dataProviderFilter: {
					'dataProvider.sourceOrganization.abbreviation': { queryString: 'WB', tokenOperator: 'AND' },
					'dataProvider.sourceOrganization.fullName': { queryString: 'WB', tokenOperator: 'AND' },
					'dataProvider.sourceOrganization.shortName': { queryString: 'WB', tokenOperator: 'AND' },
				},
				obsoleteFilter: {
					obsolete: { queryString: 'false' },
				},
			},
			tableKeyName: 'DiseaseAnnotations',
			tableSettingsKeyName: 'DiseaseAnnotationsTableSettings',
		},
	},
	FBStaff: {
		DiseaseAnnotations: {
			page: 0,
			first: 0,
			rows: 50,
			multiSortMeta: [],
			selectedColumnNames: [
				'Obsolete',
				'Reference',
				'Subject',
				'NOT',
				'Disease Relation',
				'Disease',
				'Evidence Code',
				'Inferred Gene',
				'Genetic Modifier Relation',
				'Genetic Modifier AGMs',
				'Genetic Modifier Alleles',
				'Genetic Modifier Genes',
				'Related Notes',
				'Data Provider',
				'Updated By',
				'Date Updated',
				'Created By',
				'Date Created',
			],
			selectedFormFields: [
				'Subject',
				'Disease Relation',
				'NOT',
				'Disease',
				'Reference',
				'Evidence Code',
				'Related Notes',
				'Genetic Modifier Relation',
				'Genetic Modifier AGMs',
				'Genetic Modifier Alleles',
				'Genetic Modifier Genes',
			],
			filters: {
				dataProviderFilter: {
					'dataProvider.sourceOrganization.abbreviation': { queryString: 'FB', tokenOperator: 'AND' },
					'dataProvider.sourceOrganization.fullName': { queryString: 'FB', tokenOperator: 'AND' },
					'dataProvider.sourceOrganization.shortName': { queryString: 'FB', tokenOperator: 'AND' },
				},
				obsoleteFilter: {
					obsolete: { queryString: 'false' },
				},
			},
			tableKeyName: 'DiseaseAnnotations',
			tableSettingsKeyName: 'DiseaseAnnotationsTableSettings',
		},
	},
	ZFINStaff: {
		DiseaseAnnotations: {
			page: 0,
			first: 0,
			rows: 50,
			multiSortMeta: [],
			selectedColumnNames: [
				'Subject',
				'Experiments',
				'Disease Relation',
				'Disease',
				'Evidence Code',
				'Reference',
				'Inferred Gene',
				'Inferred Allele',
				'Data Provider',
				'Updated By',
				'Date Updated',
				'Created By',
				'Date Created',
				'Obsolete',
			],
			selectedFormFields: ['Subject', 'Disease Relation', 'Disease', 'Reference', 'Evidence Code', 'Experiments'],
			filters: {
				dataProviderFilter: {
					'dataProvider.sourceOrganization.abbreviation': { queryString: 'ZFIN', tokenOperator: 'AND' },
					'dataProvider.sourceOrganization.fullName': { queryString: 'ZFIN', tokenOperator: 'AND' },
					'dataProvider.sourceOrganization.shortName': { queryString: 'ZFIN', tokenOperator: 'AND' },
				},
				obsoleteFilter: {
					obsolete: { queryString: 'false' },
				},
			},
			tableKeyName: 'DiseaseAnnotations',
			tableSettingsKeyName: 'DiseaseAnnotationsTableSettings',
		},
	},
	XBStaff: {
		DiseaseAnnotations: {
			page: 0,
			first: 0,
			rows: 50,
			multiSortMeta: [],
			selectedColumnNames: [
				'Subject',
				'Disease Relation',
				'Disease',
				'Experimental Conditions',
				'Evidence Code',
				'Reference',
				'Asserted Genes',
				'Data Provider',
				'Updated By',
				'Date Updated',
				'Created By',
				'Date Created',
				'Obsolete',
			],
			selectedFormFields: [
				'Subject',
				'Disease Relation',
				'Disease',
				'Reference',
				'Evidence Code',
				'Experimental Conditions',
			],
			filters: {
				dataProviderFilter: {
					'dataProvider.sourceOrganization.abbreviation': { queryString: 'ZFIN', tokenOperator: 'AND' },
					'dataProvider.sourceOrganization.fullName': { queryString: 'ZFIN', tokenOperator: 'AND' },
					'dataProvider.sourceOrganization.shortName': { queryString: 'ZFIN', tokenOperator: 'AND' },
				},
				obsoleteFilter: {
					obsolete: { queryString: 'false' },
				},
			},
			tableKeyName: 'DiseaseAnnotations',
			tableSettingsKeyName: 'DiseaseAnnotationsTableSettings',
		},
	},
	MGIStaff: {
		DiseaseAnnotations: {
			page: 0,
			first: 0,
			rows: 50,
			multiSortMeta: [],
			selectedColumnNames: [
				'Internal',
				'Obsolete',
				'Subject',
				'Disease Relation',
				'NOT',
				'Disease',
				'Reference',
				'Evidence Code',
				'Related Notes',
				'Inferred Gene',
				'Inferred Allele',
				'Data Provider',
				'Updated By',
				'Date Updated',
				'Created By',
				'Date Created',
			],
			selectedFormFields: [
				'Subject',
				'Disease Relation',
				'NOT',
				'Disease',
				'Reference',
				'Evidence Code',
				'Related Notes',
			],
			filters: {
				dataProviderFilter: {
					'dataProvider.sourceOrganization.abbreviation': { queryString: 'MGI', tokenOperator: 'AND' },
					'dataProvider.sourceOrganization.fullName': { queryString: 'MGI', tokenOperator: 'AND' },
					'dataProvider.sourceOrganization.shortName': { queryString: 'MGI', tokenOperator: 'AND' },
				},
				obsoleteFilter: {
					obsolete: { queryString: 'false' },
				},
			},
			tableKeyName: 'DiseaseAnnotations',
			tableSettingsKeyName: 'DiseaseAnnotationsTableSettings',
		},
	},
	Default: {
		DiseaseAnnotations: {
			page: 0,
			first: 0,
			rows: 50,
			multiSortMeta: [],
			selectedColumnNames: [
				'Unique ID',
				'MOD Annotation ID',
				'Subject',
				'Disease Relation',
				'NOT',
				'Disease',
				'Reference',
				'Evidence Code',
				'With',
				'Related Notes',
				'Experiments',
				'Experimental Conditions',
				'Genetic Sex',
				'Disease Qualifiers',
				'SGD Strain Background',
				'Annotation Type',
				'Genetic Modifier Relation',
				'Genetic Modifier AGMs',
				'Genetic Modifier Alleles',
				'Genetic Modifier Genes',
				'Inferred Gene',
				'Asserted Genes',
				'Inferred Allele',
				'Asserted Allele',
				'Data Provider',
				'Secondary Data Provider',
				'Updated By',
				'Date Updated',
				'Created By',
				'Date Created',
				'Internal',
				'Obsolete',
			],
			selectedFormFields: [
				'Asserted Genes',
				'Asserted Allele',
				'NOT',
				'With',
				'Related Notes',
				'Experimental Conditions',
				'Experiments',
				'Genetic Sex',
				'Disease Qualifiers',
				'SGD Strain Background',
				'Annotation Type',
				'Genetic Modifier Relation',
				'Genetic Modifier AGMs',
				'Genetic Modifier Alleles',
				'Genetic Modifier Genes',
				'Internal',
			],
			filters: {
				obsoleteFilter: {
					obsolete: { queryString: 'false' },
				},
			},
			tableKeyName: 'DiseaseAnnotations',
			tableSettingsKeyName: 'DiseaseAnnotationsTableSettings',
		},
	},
};

export function getModTableState(table, defaultColumnWidths, defaultColumnNames) {
	const oktaToken = JSON.parse(localStorage.getItem('okta-token-storage'));
	const mod = oktaToken?.accessToken?.claims?.Groups?.filter((group) => group.includes('Staff'));
	const modTableState = global.structuredClone(
		modTableSettings[mod] ? modTableSettings[mod][table] : modTableSettings['Default'][table]
	);
	modTableState.columnWidths = defaultColumnWidths;
	modTableState.defaultColumnWidths = defaultColumnWidths;
	modTableState.orderedColumnNames = defaultColumnNames;
	modTableState.defaultColumnNames = defaultColumnNames;
	return modTableState;
}

export function getModFormFields(table) {
	const oktaToken = JSON.parse(localStorage.getItem('okta-token-storage'));
	const mod = oktaToken?.accessToken?.claims?.Groups?.filter((group) => group.includes('Staff'));
	const modFormFields = modTableSettings[mod]
		? modTableSettings[mod][table]['selectedFormFields']
		: modTableSettings['Default'][table]['selectedFormFields'];
	return modFormFields;
}

export function getDefaultTableState(
	tableName,
	columns,
	defaultColumnWidth,
	defaultFilters = {},
	defaultVisibleColumns
) {
	const { defaultColumnNames, defaultColumnWidths } = columns.reduce(
		(acc, col) => {
			acc.defaultColumnNames.push(col.header);
			acc.defaultColumnWidths[col.field] = defaultColumnWidth;
			return acc;
		},
		{ defaultColumnNames: [], defaultColumnWidths: {} }
	);

	return {
		page: 0,
		first: 0,
		rows: 10,
		multiSortMeta: [],
		selectedColumnNames: defaultVisibleColumns || defaultColumnNames,
		orderedColumnNames: defaultColumnNames,
		defaultColumnNames,
		columnWidths: defaultColumnWidths,
		defaultColumnWidths,
		filters: defaultFilters,
		tableKeyName: tableName,
		tableSettingsKeyName: `${tableName}TableSettings`,
	};
}

export function getDefaultFormState(form, defaultFieldNames, defaultVisibleFields) {
	return {
		selectedFormFields: defaultVisibleFields ? defaultVisibleFields : defaultFieldNames,
		orderedFormFields: defaultFieldNames,
		formSettingsKeyName: `${form}FormSettings`,
	};
}
