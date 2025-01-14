import React from 'react';
import { BrowserRouter } from 'react-router-dom/cjs/react-router-dom';
import { waitFor } from '@testing-library/react';
import { renderWithClient } from '../../../tools/jest/utils';
import { AllelesTable } from '../AllelesTable';
import '../../../tools/jest/setupTests';
import {
	setupSettingsHandler,
	setupFindHandler,
	setupSearchHandler,
	setupSaveSettingsHandler,
} from '../../../tools/jest/commonMswhandlers';
import { data } from '../mockData/mockData.js';
import 'core-js/features/structured-clone';

describe('<AllelesTable />', () => {
	beforeEach(() => {
		setupFindHandler();
		setupSettingsHandler();
		setupSaveSettingsHandler();
		setupSearchHandler(data);
	});

	it('Renders without crashing', async () => {
		let result = await renderWithClient(
			<BrowserRouter>
				<AllelesTable />
			</BrowserRouter>
		);

		await waitFor(() => {
			expect(result);
		});
	});

	it('Contains Correct Table Name', async () => {
		let result = await renderWithClient(
			<BrowserRouter>
				<AllelesTable />
			</BrowserRouter>
		);

		const tableTitle = await result.findByText(/Alleles Table/i);
		expect(tableTitle).toBeInTheDocument();
	});

	it('Contains Correct Table Data', async () => {
		let result = await renderWithClient(
			<BrowserRouter>
				<AllelesTable />
			</BrowserRouter>
		);

		const primaryExternalIdTd = await result.findByText(/FB:FBal0196303/i);
		const nameTd = await result.findByText(/Saccharomyces cerevisiae UAS construct a of Stefancsik/i);
		const symbolTd = await result.findByText(/symbol display text/i);
		const secondaryIdsTd = await result.findByText(/FB:FBal0123136/i);
		const taxonTd = await result.findByText(/Drosophila melanogaster/i);
		const referencesTd = await result.findByText(/AGRKB:101000000033001/i);
		const updatedByCreatedByArray = await result.findAllByText('FB:FB_curator');
		const dateUpdatedTd = await result.findByText(/date updated text/i);
		const dateCreatedTd = await result.findByText(/date created text/i);
		const alleleDatabaseStatusTd = await result.findByText(/approved/i);

		await waitFor(() => {
			expect(primaryExternalIdTd).toBeInTheDocument();
			expect(nameTd).toBeInTheDocument();
			expect(symbolTd).toBeInTheDocument();
			expect(secondaryIdsTd).toBeInTheDocument();
			expect(taxonTd).toBeInTheDocument();
			expect(referencesTd).toBeInTheDocument();
			expect(updatedByCreatedByArray.length).toEqual(2);
			expect(dateUpdatedTd).toBeInTheDocument();
			expect(dateCreatedTd).toBeInTheDocument();
			expect(alleleDatabaseStatusTd).toBeInTheDocument();
		});
	});
});
