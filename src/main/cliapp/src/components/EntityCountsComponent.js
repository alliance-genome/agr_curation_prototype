import React, { useState, useEffect } from 'react';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { SystemService } from '../service/SystemService';
import { CLASSES } from '../constants/Classes';

export const EntityCountsComponent = () => {

	const [tableData, setTableData] = useState({});

	useEffect(() => {
		let isMounted = true;
		const systemService = new SystemService();

		let _tableData = {};

		systemService.getSiteSummary().then((res) => {
			if (isMounted) {
				for (const key in res.entity){

					if(!CLASSES[key]) continue;

					const { type } = CLASSES[key];

					if(!_tableData[type]){
						_tableData[type] = [];
					}

					_tableData[type].push({
						name: CLASSES[key].name,
						link: CLASSES[key].link,
						dbCount: res.entity[key]["dbCount"],
						esCount: res.entity[key]["esCount"]
					});
				}
				setTableData(_tableData);
			}
		});
		return () => { isMounted = false };
		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, []);

	const nameHyperlinkTemplate = (rowData) => {
		return <a href={rowData.link}>{rowData.name}</a>
	}

	return (
		<>
			<div className="grid nested dashboard">
				<div className="col-4">
					<DataTable header="Entities" value={tableData.entity} sortField="name" sortOrder={1}>
						<Column field="name" header="Entity Name" body={nameHyperlinkTemplate}/>
						<Column field="dbCount" header="Database Entity Count" />
						<Column field="esCount" header="Search index Entity Count" />
					</DataTable>
				</div>
				<div className="col-4">
					<DataTable header="Ontologies" value={tableData.ontology} sortField="name" sortOrder={1}>
						<Column field="name" header="Ontology Name" body={nameHyperlinkTemplate} />
						<Column field="dbCount" header="Database Term Count" />
						<Column field="esCount" header="Search index Term Count" />
					</DataTable>
				</div>
				<div className="col-4">
					<DataTable header="System" value={tableData.system} sortField="name" sortOrder={1}>
						<Column field="name" header="System Name" body={nameHyperlinkTemplate} />
						<Column field="dbCount" header="Database Object Count" />
						<Column field="esCount" header="Search index Object Count" />
					</DataTable>
				</div>
			</div>
		</>
	);
};
