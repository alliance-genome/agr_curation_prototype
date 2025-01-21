import React from 'react';
import { BrowserRouter } from 'react-router-dom/cjs/react-router-dom';
import { waitFor } from '@testing-library/react';
import { renderWithClient } from '../../../tools/jest/utils';
import { ConstructsTable } from '../ConstructsTable';
import { setLocalStorage } from '../../../tools/jest/setupTests';
import {
	setupSettingsHandler,
	setupFindHandler,
	setupSearchHandler,
	setupSaveSettingsHandler,
} from '../../../tools/jest/commonMswhandlers';
import { data } from '../mockData/mockData.js';
import 'core-js/features/structured-clone';

describe('<ConstructsTable />', () => {
	beforeEach(() => {
		setupFindHandler();
		setupSettingsHandler();
		setupSaveSettingsHandler();
		setupSearchHandler(data);
	});

	it('Renders without crashing', async () => {
		let result = await renderWithClient(
			<BrowserRouter>
				<ConstructsTable />
			</BrowserRouter>
		);

		await waitFor(() => {
			expect(result);
		});
	});

	it('Contains Correct Table Name', async () => {
		let result = await renderWithClient(
			<BrowserRouter>
				<ConstructsTable />
			</BrowserRouter>
		);

		const tableTitle = await result.findByText(/Constructs Table/i);
		expect(tableTitle).toBeInTheDocument();
	});

	it('Contains Correct Table Data', async () => {
		let result = await renderWithClient(
			<BrowserRouter>
				<ConstructsTable />
			</BrowserRouter>
		);

		const primaryExternalIdTd = await result.findByText(/WB:WBCnstr00000001/i);
		const referencesTd = await result.findByText(/PMID:17486083/i);
		const updatedByCreatedByArray = await result.findAllByText('WB:curator');
		const dateCreatedTd = await result.findByText(/2010-01-02T00:00:00Z/i);
		const dateUpdatedTd = await result.findByText(/2012-08-03T01:00:00\+01:00/i);
		const constructComponentTd = await result.findByText(/egl19/i);
		const symbolTd = await result.findByText(/KP273/);
		const nameTd = await result.findByText(/King Potato 273/);
		const synonymTd = await result.findByText(/KPot273/);
		const constructGenomicComponentTd = await result.findByText(/lin-17/i);

		await waitFor(() => {
			expect(primaryExternalIdTd).toBeInTheDocument();
			expect(nameTd).toBeInTheDocument();
			expect(symbolTd).toBeInTheDocument();
			expect(synonymTd).toBeInTheDocument();
			expect(referencesTd).toBeInTheDocument();
			expect(updatedByCreatedByArray.length).toEqual(2);
			expect(dateUpdatedTd).toBeInTheDocument();
			expect(dateCreatedTd).toBeInTheDocument();
			expect(constructComponentTd).toBeInTheDocument();
			expect(constructGenomicComponentTd).toBeInTheDocument();
		});
	});
});
