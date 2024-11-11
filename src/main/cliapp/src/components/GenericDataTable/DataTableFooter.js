import React from 'react';
import { Paginator } from 'primereact/paginator';
import { NumberTemplate } from '../Templates/NumberTemplate';

export const DataTableFooter = ({ first, rows, totalRecords, onLazyLoad, isInEditMode }) => {
	return (
		!isInEditMode && (
			<Paginator
				data-testid="datatable-footer"
				first={first}
				rows={rows}
				totalRecords={totalRecords}
				rowsPerPageOptions={[10, 20, 50, 100, 250, 1000]}
				onPageChange={onLazyLoad}
				template={{
					layout:
						'CurrentPageReport FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown',
					CurrentPageReport: (options) => {
						const totalRecordsDisplay = <NumberTemplate number={options.totalRecords} />;
						const firstDisplay = <NumberTemplate number={options.first} />;
						const lastDisplay = <NumberTemplate number={options.last} />;
						return (
							<>
								Showing {firstDisplay} to {lastDisplay} of {totalRecordsDisplay}
							</>
						);
					},
					PageLinks: (options) => {
						const pageDisplay = <NumberTemplate number={options.page + 1} />;
						return (
							<button type="button" className={options.className} onClick={options.onClick}>
								{pageDisplay}
							</button>
						);
					},
				}}
			/>
		)
	);
};
