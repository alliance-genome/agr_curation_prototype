import React, { useState, useEffect } from 'react';

import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { CLASSES } from '../../constants/Classes';

export const Dashboard = () => {
	const [tableData, setTableData] = useState({});

	useEffect(() => {
		let _tableData = {};
		for (const key in CLASSES) {
			const { type } = CLASSES[key];

			if (!_tableData[type]) {
				_tableData[type] = [];
			}
			if (CLASSES[key].hasTable) {
				_tableData[type].push({
					name: CLASSES[key].name,
					link: CLASSES[key].link,
				});
			}
		}
		setTableData(_tableData);
	}, []);

	const nameHyperlinkTemplate = (rowData) => {
		return <a href={rowData.link}>{rowData.name}</a>;
	};

	return (
		<>
			<div className="grid nested dashboard">
				<div className="col-4">
					<DataTable header="Entities" value={tableData.entity} sortField="name" sortOrder={1} showHeaders={false}>
						<Column field="name" body={nameHyperlinkTemplate} />
					</DataTable>
				</div>
				<div className="col-4">
					<DataTable header="Ontologies" value={tableData.ontology} sortField="name" sortOrder={1} showHeaders={false}>
						<Column field="name" body={nameHyperlinkTemplate} />
					</DataTable>
				</div>
				<div className="col-4">
					<DataTable header="System" value={tableData.system} sortField="name" sortOrder={1} showHeaders={false}>
						<Column field="name" body={nameHyperlinkTemplate} />
					</DataTable>
				</div>
			</div>
		</>
	);
};
