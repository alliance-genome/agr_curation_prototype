import React, { useRef, useState } from 'react';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { Toast } from 'primereact/toast';
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_CONFIGS } from '../../constants/FilterFields';
import { useGetTableData } from '../../service/useGetTableData';
import { useGetUserSettings } from '../../service/useGetUserSettings';
import { SearchService } from '../../service/SearchService';

import { StringTemplate } from '../../components/Templates/StringTemplate';
import { StringListTemplate } from '../../components/Templates/StringListTemplate';

export const ResourceDescriptorsTable = () => {
	const [isInEditMode, setIsInEditMode] = useState(false);
	const [errorMessages, setErrorMessages] = useState({});
	const [totalRecords, setTotalRecords] = useState(0);

	const [resourceDescriptors, setResourceDescriptors] = useState();

	const searchService = new SearchService();

	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);

	const columns = [
		{
			field: 'prefix',
			header: 'Prefix',
			sortable: true,
			body: (rowData) => <StringTemplate string={rowData.prefix} />,
			filterConfig: FILTER_CONFIGS.prefixFilterConfig,
		},
		{
			field: 'name',
			header: 'Name',
			sortable: true,
			body: (rowData) => <StringTemplate string={rowData.name} />,
			filterConfig: FILTER_CONFIGS.nameFilterConfig,
		},
		{
			field: 'synonyms',
			header: 'Synonyms',
			body: (rowData) => <StringListTemplate list={rowData.synonyms} />,
			filterConfig: FILTER_CONFIGS.synonymsFilterConfig,
		},
		{
			field: 'idPattern',
			header: 'ID Pattern',
			sortable: true,
			body: (rowData) => <StringTemplate string={rowData.idPattern} />,
			filterConfig: FILTER_CONFIGS.idPatternFilterConfig,
		},
		{
			field: 'idExample',
			header: 'ID Example',
			sortable: true,
			body: (rowData) => <StringTemplate string={rowData.idExample} />,
			filterConfig: FILTER_CONFIGS.idExampleFilterConfig,
		},
		{
			field: 'defaultUrlTemplate',
			header: 'Default URL Template',
			sortable: true,
			body: (rowData) => <StringTemplate string={rowData.defaultUrlTemplate} />,
			filterConfig: FILTER_CONFIGS.defaultUrlTemplateFilterConfig,
		},
	];

	const DEFAULT_COLUMN_WIDTH = 20;
	const SEARCH_ENDPOINT = 'resourcedescriptor';

	const initialTableState = getDefaultTableState('ResourceDescriptors', columns, DEFAULT_COLUMN_WIDTH);

	const { settings: tableState, mutate: setTableState } = useGetUserSettings(
		initialTableState.tableSettingsKeyName,
		initialTableState
	);

	const { isLoading, isFetching } = useGetTableData({
		tableState,
		endpoint: SEARCH_ENDPOINT,
		setIsInEditMode,
		setEntities: setResourceDescriptors,
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
				tableName="Resource Descriptors"
				entities={resourceDescriptors}
				setEntities={setResourceDescriptors}
				totalRecords={totalRecords}
				setTotalRecords={setTotalRecords}
				tableState={tableState}
				setTableState={setTableState}
				columns={columns}
				isEditable={false}
				isInEditMode={isInEditMode}
				setIsInEditMode={setIsInEditMode}
				toasts={{ toast_topleft, toast_topright }}
				errorObject={{ errorMessages, setErrorMessages }}
				defaultColumnWidth={DEFAULT_COLUMN_WIDTH}
				fetching={isFetching || isLoading}
			/>
		</div>
	);
};
