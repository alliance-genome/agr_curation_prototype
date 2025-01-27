import React from 'react';
import { waitFor } from '@testing-library/react';
import { renderWithClient } from '../../../tools/jest/utils';
import { ResourceDescriptorsPage } from '../index';
import { setLocalStorage } from '../../../tools/jest/setupTests';
import {
	setupSettingsHandler,
	setupFindHandler,
	setupSearchHandler,
	setupSaveSettingsHandler,
} from '../../../tools/jest/commonMswhandlers';
import { data } from '../mockData/mockData';
import 'core-js/features/structured-clone';

describe('<ResourceDescriptorsPage />', () => {
	beforeEach(() => {
		setupFindHandler();
		setupSettingsHandler();
		setupSaveSettingsHandler();
		setupSearchHandler(data);
	});

	it('Renders without crashing', async () => {
		let result = await renderWithClient(<ResourceDescriptorsPage />);

		await waitFor(() => {
			expect(result);
		});
	});

	it('Contains Correct Table Name', async () => {
		let result = await renderWithClient(<ResourceDescriptorsPage />);

		const tableTitle = await result.findByText(/Resource Descriptors Table/i);
		expect(tableTitle).toBeInTheDocument();
	});

	it('The table contains correct data', async () => {
		let result = await renderWithClient(<ResourceDescriptorsPage />);

		const prefixTd = await result.findByText('Orphanet');
		const nameTd = await result.findByText(/Orphanet_name/i);
		const idPatternTd = await result.findByText('^ORPHA:\\\\d+$');
		const idExampleTd = await result.findByText(/ORPHA:600483/i);
		const defaultUrlTemplateTd = await result.findByText(
			'https://www.orpha.net/consor/cgi-bin/OC_Exp.php?lng=EN&Expert=[%s]'
		);
		await waitFor(() => {
			expect(prefixTd).toBeInTheDocument();
			expect(nameTd).toBeInTheDocument();
			expect(idPatternTd).toBeInTheDocument();
			expect(idExampleTd).toBeInTheDocument();
			expect(defaultUrlTemplateTd).toBeInTheDocument();
		});
	});
});
