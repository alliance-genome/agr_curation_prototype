import React, { useState, useEffect } from 'react';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { SystemService } from '../service/SystemService';
import { CLASSES } from '../constants/Classes';
import { NumberTemplate } from './Templates/NumberTemplate';

export const EntityCountsComponent = () => {
	const [tableData, setTableData] = useState({});

	useEffect(() => {
		let isMounted = true;
		const systemService = new SystemService();

		let _tableData = {};

		systemService.getSiteSummary().then((res) => {
			if (isMounted) {
				for (const key in res.entity) {
					if (!CLASSES[key]) continue;

					const { type } = CLASSES[key];

					if (!_tableData[type]) {
						_tableData[type] = [];
					}

					_tableData[type].push({
						name: CLASSES[key].name,
						link: CLASSES[key].link,
						type: CLASSES[key].type,
						isIndexed: CLASSES[key].isIndexed,
						dbCount: res.entity[key]['dbCount'],
						esCount: res.entity[key]['esCount'],
					});
				}
				setTableData(_tableData);
			}
		});
		return () => {
			isMounted = false;
		};
		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, []);

	const nameHyperlinkTemplate = (rowData) => {
		if (!rowData.link) {
			return rowData.name;
		}
		return <a href={rowData.link}>{rowData.name}</a>;
	};

	const getRowClass = (rowData) => {
		console.log(rowData);
		if (!rowData.isIndexed) return;

		if (rowData?.dbCount !== rowData.esCount) {
			return 'bg-gray-500 text-white';
		}
	};

	return (
		<>
			<div className="grid nested dashboard">
				<div className="col-3">
					<DataTable
						header="Entities"
						value={tableData.entity}
						sortField="name"
						sortOrder={1}
						rowClassName={(rowData) => getRowClass(rowData)}
					>
						<Column field="name" header="Entity Name" body={nameHyperlinkTemplate} />
						<Column
							field="dbCount"
							header="Database Entity Count"
							body={(rowData) => <NumberTemplate number={rowData.dbCount} />}
						/>
						<Column
							field="esCount"
							header="Search index Entity Count"
							body={(rowData) => <NumberTemplate number={rowData.esCount} />}
						/>
					</DataTable>
				</div>
				<div className="col-3">
					<DataTable
						header="Ontologies"
						value={tableData.ontology}
						sortField="name"
						sortOrder={1}
						rowClassName={(rowData) => getRowClass(rowData)}
					>
						<Column field="name" header="Ontology Name" body={nameHyperlinkTemplate} />
						<Column
							field="dbCount"
							header="Database Term Count"
							body={(rowData) => <NumberTemplate number={rowData.dbCount} />}
						/>
						<Column
							field="esCount"
							header="Search index Term Count"
							body={(rowData) => <NumberTemplate number={rowData.esCount} />}
						/>
					</DataTable>
				</div>
				<div className="col-3">
					<DataTable
						header="Associations"
						value={tableData.association}
						sortField="name"
						sortOrder={1}
						rowClassName={(rowData) => getRowClass(rowData)}
					>
						<Column field="name" header="Association Type" body={nameHyperlinkTemplate} />
						<Column
							field="dbCount"
							header="Database Association Count"
							body={(rowData) => <NumberTemplate number={rowData.dbCount} />}
						/>
					</DataTable>
				</div>
				<div className="col-3">
					<DataTable
						header="System"
						value={tableData.system}
						sortField="name"
						sortOrder={1}
						rowClassName={(rowData) => getRowClass(rowData)}
					>
						<Column field="name" header="System Name" body={nameHyperlinkTemplate} />
						<Column
							field="dbCount"
							header="Database Object Count"
							body={(rowData) => <NumberTemplate number={rowData.dbCount} />}
						/>
						<Column
							field="esCount"
							header="Search index Object Count"
							body={(rowData) => <NumberTemplate number={rowData.esCount} />}
						/>
					</DataTable>
				</div>
			</div>
		</>
	);
};
