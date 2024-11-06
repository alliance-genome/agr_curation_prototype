import React, { useRef, useState } from 'react';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { Toast } from 'primereact/toast';
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_CONFIGS } from '../../constants/FilterFields';
import { useGetTableData } from '../../service/useGetTableData';
import { useGetUserSettings } from '../../service/useGetUserSettings';
import { SearchService } from '../../service/SearchService';

import { StringTemplate } from '../../components/Templates/StringTemplate';

export const ResourceDescriptorPagesTable = () => {
	const [isInEditMode, setIsInEditMode] = useState(false);
	const [errorMessages, setErrorMessages] = useState({});
	const [totalRecords, setTotalRecords] = useState(0);

	const [resourceDescriptorPages, setResourceDescriptorPages] = useState();

	const searchService = new SearchService();

	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);

	const columns = [
		{
			field: 'resourceDescriptor.prefix',
			header: 'Resource Descriptor',
			sortable: true,
			body: (rowData) => (
				<StringTemplate string={`${rowData.resourceDescriptor?.prefix} (${rowData.resourceDescriptor.name})`} />
			),
			filterConfig: FILTER_CONFIGS.resourceDescriptorFilterConfig,
		},
		{
			field: 'name',
			header: 'Name',
			sortable: true,
			body: (rowData) => <StringTemplate string={rowData.name} />,
			filterConfig: FILTER_CONFIGS.nameFilterConfig,
		},
		{
			field: 'urlTemplate',
			header: 'URL Template',
			sortable: true,
			body: (rowData) => <StringTemplate string={rowData.urlTemplate} />,
			filterConfig: FILTER_CONFIGS.urlTemplateFilterConfig,
		},
		{
			field: 'pageDescription',
			header: 'Page Description',
			sortable: true,
			body: (rowData) => <StringTemplate string={rowData.pageDescription} />,
			filterConfig: FILTER_CONFIGS.pageDescriptionFilterConfig,
		},
	];

	const DEFAULT_COLUMN_WIDTH = 20;
	const SEARCH_ENDPOINT = 'resourcedescriptorpage';

	const initialTableState = getDefaultTableState('ResourceDescriptorPages', columns, DEFAULT_COLUMN_WIDTH);

	const { settings: tableState, mutate: setTableState } = useGetUserSettings(
		initialTableState.tableSettingsKeyName,
		initialTableState
	);

	const { isLoading, isFetching } = useGetTableData({
		tableState,
		endpoint: SEARCH_ENDPOINT,
		setIsInEditMode,
		setEntities: setResourceDescriptorPages,
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
				entities={resourceDescriptorPages}
				setEntities={setResourceDescriptorPages}
				totalRecords={totalRecords}
				setTotalRecords={setTotalRecords}
				tableState={tableState}
				setTableState={setTableState}
				tableName="Resource Descriptor Pages"
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
