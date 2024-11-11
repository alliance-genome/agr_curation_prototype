import { render, fireEvent } from '@testing-library/react';
import '../../../tools/jest/setupTests';
import { DataTableFooter } from '../../GenericDataTable/DataTableFooter';

describe('DataTableFooter', () => {
	it('should render Paginator component when isInEditMode is false', () => {
		const result = render(
			<DataTableFooter first={0} rows={10} totalRecords={100} onLazyLoad={() => {}} isInEditMode={false} />
		);
		const paginator = result.queryByTestId('datatable-footer');

		expect(paginator).toBeInTheDocument();
	});

	it('should handle totalRecords, first, and last being zero', () => {
		const { getByText } = render(
			<DataTableFooter first={0} rows={10} totalRecords={0} onLazyLoad={() => {}} isInEditMode={false} />
		);
		expect(getByText('Showing 0 to 0 of 0')).toBeInTheDocument();
	});

	it('should display formatted totalRecords, first, and last numbers', () => {
		const first = 1000;
		const rows = 10;
		const totalRecords = 2000;
		const onLazyLoad = jest.fn();
		const isInEditMode = false;

		const { getByText } = render(
			<DataTableFooter
				first={first}
				rows={rows}
				totalRecords={totalRecords}
				onLazyLoad={onLazyLoad}
				isInEditMode={isInEditMode}
			/>
		);

		const firstDisplay = getByText('Showing 1,001 to 1,010 of 2,000');
		expect(firstDisplay).toBeInTheDocument();
	});

	it('should show correct rows per page options in dropdown', () => {
		const first = 0;
		const rows = 10;
		const totalRecords = 100;
		const onLazyLoad = jest.fn();
		const isInEditMode = false;

		const result = render(
			<DataTableFooter
				first={first}
				rows={rows}
				totalRecords={totalRecords}
				onLazyLoad={onLazyLoad}
				isInEditMode={isInEditMode}
			/>
		);

		const dropdownButton = result.getAllByText('10')[1];
		fireEvent.click(dropdownButton);

		expect(result.getByText('20')).toBeInTheDocument();
		expect(result.getByText('50')).toBeInTheDocument();
		expect(result.getByText('100')).toBeInTheDocument();
		expect(result.getByText('250')).toBeInTheDocument();
		expect(result.getByText('1000')).toBeInTheDocument();
	});

	it('should handle null or undefined totalRecords, first, and last', () => {
		const onLazyLoad = jest.fn();

		const result = render(
			<DataTableFooter first={null} rows={10} totalRecords={null} onLazyLoad={onLazyLoad} isInEditMode={false} />
		);

		expect(result.getByText(/Showing 0 to 0 of/i)).toBeInTheDocument();
	});

	it('should not render Paginator when isInEditMode is true', () => {
		const onLazyLoad = jest.fn();
		const result = render(
			<DataTableFooter first={0} rows={10} totalRecords={100} onLazyLoad={onLazyLoad} isInEditMode={true} />
		);

		const paginator = result.queryByTestId('datatable-footer');
		expect(paginator).toBeNull();
	});
});
