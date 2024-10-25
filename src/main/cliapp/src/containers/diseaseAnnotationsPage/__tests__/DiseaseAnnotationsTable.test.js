import React from 'react';
import { waitFor } from '@testing-library/react';
import { renderWithClient } from '../../../tools/jest/utils';
import { DiseaseAnnotationsPage } from '../index';
import '../../../tools/jest/setupTests';
import {
	setupSettingsHandler,
	setupFindHandler,
	setupSearchHandler,
	setupSaveSettingsHandler,
} from '../../../tools/jest/commonMswhandlers';
import { data } from '../mockData/mockData';
import 'core-js/features/structured-clone';

describe('<DiseaseAnnotationsPage />', () => {
	beforeEach(() => {
		setupFindHandler();
		setupSettingsHandler();
		setupSaveSettingsHandler();
		setupSearchHandler(data);
	});

	it('Renders without crashing', async () => {
		let result = await renderWithClient(<DiseaseAnnotationsPage />);

		await waitFor(() => {
			expect(result);
		});
	}, 10000);

	it('Contains Correct Table Name', async () => {
		let result = await renderWithClient(<DiseaseAnnotationsPage />);

		const tableTitle = await result.findByText(/Disease Annotations Table/i);
		expect(tableTitle).toBeInTheDocument();
	}, 10000);

	it('The table contains correct data', async () => {
		let result = await renderWithClient(<DiseaseAnnotationsPage />);

		const uniqueIdTd = await result.findByText('MGI:5560505|DOID:0050545|AGRKB:101000000827851');
		const modInternalIdTd = await result.findByText('mockModInternalId');
		const subjectTd = await result.findByText(/C57BL\/6J-Rfx3/i);
		const relationTd = await result.findByText('is_model_of');
		const internalObsoleteArray = await result.findAllByText('false');
		const NOTArray = await result.findAllByText('NOT');
		const diseaseTd = await result.findByText(/visceral heterotaxy/i);
		const referenceTd = await result.findByText(/MGI:5284969/i);
		const evidenceCodeTd = await result.findByText(/TAS/i);
		const withTd = await result.findByText('with_test_symbol (with_test_curie)');

		const annotationTypeTd = await result.findByText(/manually_curated/i);
		const inferredGeneTd = await result.findByText('Rfx3 (MGI:106582)');
		const inferredAlleleTd = await result.findByText(/MGI:5560494/i);
		const dataProviderTd = await result.findByText('MGI');
		const relatedNotesTd = await result.findByText('Notes (1)');
		const conditionRelationHandleTd = await result.findByText('condition relations handle test');
		const diseaseQualifiersTd = await result.findByText('disease qualifiers test');
		const geneticSexTd = await result.findByText('genetic sex test');
		const sgdStrainBackgroundTd = await result.findByText('SGD Strain Background test (sgd test curie)');
		const diseaseGeneticModifierRelationTd = await result.findByText('disease genetic modifier relation test');
		const diseaseGeneticModifierAgmsTd = await result.findByText('DGM AGM test (WB:DGM1)');
		const diseaseGeneticModifierAllelesTd = await result.findByText('DGM Allele test (WB:DGM2)');
		const diseaseGeneticModifierGenesTd = await result.findByText('DGM Gene test (WB:DGM3)');
		const secondaryDataProviderTd = await result.findByText('test provider');

		const updatedByCreatedByArray = await result.findAllByText('MGI:curation_staff');
		const dateUpdatedDateCreatedArray = await result.findAllByText('2017-06-08T14:15:35Z');

		await waitFor(() => {
			expect(uniqueIdTd).toBeInTheDocument();
			expect(modInternalIdTd).toBeInTheDocument();
			expect(subjectTd).toBeInTheDocument();
			expect(relationTd).toBeInTheDocument();
			expect(internalObsoleteArray.length).toEqual(4);
			expect(NOTArray.length).toEqual(2);
			expect(diseaseTd).toBeInTheDocument();
			expect(referenceTd).toBeInTheDocument();
			expect(evidenceCodeTd).toBeInTheDocument();
			expect(annotationTypeTd).toBeInTheDocument();
			expect(inferredGeneTd).toBeInTheDocument();
			expect(inferredAlleleTd).toBeInTheDocument();
			expect(dataProviderTd).toBeInTheDocument();
			expect(withTd).toBeInTheDocument();
			expect(relatedNotesTd).toBeInTheDocument();
			expect(conditionRelationHandleTd).toBeInTheDocument();
			expect(geneticSexTd).toBeInTheDocument();
			expect(sgdStrainBackgroundTd).toBeInTheDocument();
			expect(diseaseQualifiersTd).toBeInTheDocument();
			expect(diseaseGeneticModifierRelationTd).toBeInTheDocument();
			expect(diseaseGeneticModifierAgmsTd).toBeInTheDocument();
			expect(diseaseGeneticModifierAllelesTd).toBeInTheDocument();
			expect(diseaseGeneticModifierGenesTd).toBeInTheDocument();
			expect(secondaryDataProviderTd).toBeInTheDocument();
			expect(updatedByCreatedByArray.length).toEqual(2);
			expect(dateUpdatedDateCreatedArray.length).toEqual(2);
		});
	}, 10000);
});
