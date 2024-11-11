export const conditionsSort = (conditions) => {
	return conditions.sort((a, b) => (a.conditionSummary > b.conditionSummary ? 1 : -1));
};

export const crossReferencesSort = (crossReferences) => {
	return crossReferences.sort((a, b) =>
		a.displayName > b.displayName ? 1 : a.resourceDescriptorPage.name === b.resourceDescriptorPage.name ? 1 : -1
	);
};

export const diseaseQualifiersSort = (qualifiers) => qualifiers.sort((a, b) => (a.name > b.name ? 1 : -1));

export const evidenceCodesSort = (evidenceCodes) => {
	return evidenceCodes.sort((a, b) => (a.abbreviation > b.abbreviation ? 1 : a.curie === b.curie ? 1 : -1));
};
