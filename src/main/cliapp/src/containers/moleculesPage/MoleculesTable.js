import React, { useRef, useState } from 'react';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { Toast } from 'primereact/toast';
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_CONFIGS } from '../../constants/FilterFields';
import { useGetTableData } from '../../service/useGetTableData';
import { useGetUserSettings } from '../../service/useGetUserSettings';

import { IdTemplate } from '../../components/Templates/IdTemplate';
import { StringTemplate } from '../../components/Templates/StringTemplate';

import { SearchService } from '../../service/SearchService';

export const MoleculesTable = () => {
	const [isInEditMode, setIsInEditMode] = useState(false);
	const [errorMessages, setErrorMessages] = useState({});

	const [totalRecords, setTotalRecords] = useState(0);
	const [molecules, setMolecules] = useState([]);
	const searchService = new SearchService();

	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);

	const columns = [
		{
			field: 'curie',
			header: 'Curie',
			sortable: true,
			filter: true,
			body: (rowData) => <IdTemplate id={rowData.curie} />,
			filterConfig: FILTER_CONFIGS.curieFilterConfig,
		},
		{
			field: 'name',
			header: 'Name',
			sortable: true,
			filter: true,
			body: (rowData) => <StringTemplate string={rowData.name} />,
			filterConfig: FILTER_CONFIGS.nameFilterConfig,
		},
		{
			field: 'inchi',
			header: 'InChi',
			sortable: true,
			filter: true,
			body: (rowData) => <StringTemplate string={rowData.inchi} />,
			filterConfig: FILTER_CONFIGS.inchiFilterConfig,
		},
		{
			field: 'inchiKey',
			header: 'InChiKey',
			sortable: true,
			filter: true,
			body: (rowData) => <StringTemplate string={rowData.inchiKey} />,
			filterConfig: FILTER_CONFIGS.inchiKeyFilterConfig,
		},
		{
			field: 'iupac',
			header: 'IUPAC',
			sortable: true,
			filter: true,
			body: (rowData) => <StringTemplate string={rowData.iupac} />,
			filterConfig: FILTER_CONFIGS.iupacFilterConfig,
		},
		{
			field: 'formula',
			header: 'Formula',
			sortable: true,
			filter: true,
			body: (rowData) => <StringTemplate string={rowData.formula} />,
			filterConfig: FILTER_CONFIGS.formulaFilterConfig,
		},
		{
			field: 'smiles',
			header: 'SMILES',
			sortable: true,
			filter: true,
			body: (rowData) => <StringTemplate string={rowData.smiles} />,
			filterConfig: FILTER_CONFIGS.smilesFilterConfig,
		},
	];

	const DEFAULT_COLUMN_WIDTH = 13;
	const SEARCH_ENDPOINT = 'molecule';

	const initialTableState = getDefaultTableState('Molecule', columns, DEFAULT_COLUMN_WIDTH);

	const { settings: tableState, mutate: setTableState } = useGetUserSettings(
		initialTableState.tableSettingsKeyName,
		initialTableState
	);

	const { isLoading, isFetching } = useGetTableData({
		tableState,
		endpoint: SEARCH_ENDPOINT,
		setIsInEditMode,
		setEntities: setMolecules,
		setTotalRecords,
		toast_topleft,
		searchService,
	});

	return (
		<div className="card">
			<Toast ref={toast_topleft} position="top-left" />
			<Toast ref={toast_topright} position="top-right" />
			<GenericDataTable
				endpoint={SEARCH_ENDPOINT}
				tableName="Molecule"
				entities={molecules}
				setEntities={setMolecules}
				totalRecords={totalRecords}
				setTotalRecords={setTotalRecords}
				tableState={tableState}
				setTableState={setTableState}
				columns={columns}
				defaultColumnWidth={DEFAULT_COLUMN_WIDTH}
				dataKey="curie"
				isEditable={false}
				isInEditMode={isInEditMode}
				setIsInEditMode={setIsInEditMode}
				toasts={{ toast_topleft, toast_topright }}
				errorObject={{ errorMessages, setErrorMessages }}
				fetching={isFetching || isLoading}
			/>
		</div>
	);
};
