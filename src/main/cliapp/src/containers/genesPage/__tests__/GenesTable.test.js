import React from 'react';
import { waitFor } from '@testing-library/react';
import { renderWithClient } from '../../../tools/jest/utils';
import { GenesPage } from '../index';
import { setLocalStorage } from '../../../tools/jest/setupTests';
import {
	setupSettingsHandler,
	setupFindHandler,
	setupSearchHandler,
	setupSaveSettingsHandler,
} from '../../../tools/jest/commonMswhandlers';
import { data } from '../mockData/mockData';
import 'core-js/features/structured-clone';

describe('<GenesPage />', () => {
	beforeEach(() => {
		setupFindHandler();
		setupSettingsHandler();
		setupSaveSettingsHandler();
		setupSearchHandler(data);
	});

	it('Renders without crashing', async () => {
		let result = await renderWithClient(<GenesPage />);

		await waitFor(() => {
			expect(result);
		});
	});

	it('Contains Correct Table Name', async () => {
		let result = await renderWithClient(<GenesPage />);

		const tableTitle = await result.findByText(/Genes Table/i);
		expect(tableTitle).toBeInTheDocument();
	});

	it('The table contains correct data', async () => {
		let result = await renderWithClient(<GenesPage />);

		const modEntityIdTd = await result.findByText(/WB:WBGene00003771/i);
		const nameTd = await result.findByText(/LEVamisole resistant 8/i);
		const symbolTd = await result.findByText(/lev-8/i);
		const synonymsTd = await result.findByText(/acr-13/i);
		const secondaryIdsTd = await result.findByText(/WB:WBGene00000052/i);
		const systematicNameTd = await result.findByText('C35C5.5');
		const taxonTd = await result.findByText(/Caenorhabditis elegans/i);
		const xrefsTd = await result.findByText(/WBGene00003771Xref \(gene\)/);
		const geneTypeTd = await result.findByText(/protein_coding_gene/i);

		await waitFor(() => {
			expect(modEntityIdTd).toBeInTheDocument();
			expect(nameTd).toBeInTheDocument();
			expect(symbolTd).toBeInTheDocument();
			expect(synonymsTd).toBeInTheDocument();
			expect(secondaryIdsTd).toBeInTheDocument();
			expect(systematicNameTd).toBeInTheDocument();
			expect(taxonTd).toBeInTheDocument();
			expect(xrefsTd).toBeInTheDocument();
			expect(geneTypeTd).toBeInTheDocument();
		});
	});
});
