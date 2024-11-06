import React, { useState, useRef } from 'react';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { Card } from 'primereact/card';
import { Toast } from 'primereact/toast';
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_CONFIGS } from '../../constants/FilterFields';
import { useGetTableData } from '../../service/useGetTableData';
import { useGetUserSettings } from '../../service/useGetUserSettings';
import { StringListTemplate } from '../../components/Templates/StringListTemplate';
import { StringTemplate } from '../../components/Templates/StringTemplate';
import { SearchService } from '../../service/SearchService';

export const LiteratureReferenceTable = () => {
	const [isInEditMode, setIsInEditMode] = useState(false);
	const [errorMessages, setErrorMessages] = useState({});

	const [totalRecords, setTotalRecords] = useState(0);
	const [literatureReferences, setLiteratureReferences] = useState([]);
	const searchService = new SearchService();

	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);

	const columns = [
		{
			field: 'curie',
			header: 'Curie',
			sortable: { isInEditMode },
			filter: true,
			filterConfig: FILTER_CONFIGS.curieFilterConfig,
		},
		{
			field: 'cross_references.curie',
			header: 'Cross References',
			sortable: true,
			body: (rowData) => <StringListTemplate list={rowData?.cross_references?.map((reference) => reference.curie)} />,
			filter: true,
			filterConfig: FILTER_CONFIGS.literatureCrossReferenceFilterConfig,
		},
		{
			field: 'title',
			header: 'Title',
			sortable: true,
			filter: true,
			body: (rowData) => <StringTemplate string={rowData.title} />,
			filterConfig: FILTER_CONFIGS.titleFilterConfig,
		},
		{
			field: 'abstract',
			header: 'Abstract',
			sortable: true,
			filter: true,
			body: (rowData) => <StringTemplate string={rowData.abstract} />,
			filterConfig: FILTER_CONFIGS.abstractFilterConfig,
		},
		{
			field: 'citation',
			header: 'Citation',
			sortable: true,
			filter: true,
			body: (rowData) => <StringTemplate string={rowData.citation} />,
			filterConfig: FILTER_CONFIGS.citationFilterConfig,
		},
		{
			field: 'short_citation',
			header: 'Short Citation',
			sortable: { isInEditMode },
			filter: true,
			filterConfig: FILTER_CONFIGS.literatureShortCitationFilterConfig,
		},
	];
	const DEFAULT_COLUMN_WIDTH = 20;
	const SEARCH_ENDPOINT = 'literature-reference';

	const initialTableState = getDefaultTableState('LiteratureReferences', columns, DEFAULT_COLUMN_WIDTH);

	const { settings: tableState, mutate: setTableState } = useGetUserSettings(
		initialTableState.tableSettingsKeyName,
		initialTableState
	);

	const { isLoading, isFetching } = useGetTableData({
		tableState,
		endpoint: SEARCH_ENDPOINT,
		setIsInEditMode,
		setEntities: setLiteratureReferences,
		setTotalRecords,
		toast_topleft,
		searchService,
	});

	return (
		<Card>
			<Toast ref={toast_topleft} position="top-left" />
			<Toast ref={toast_topright} position="top-right" />
			<GenericDataTable
				endpoint={SEARCH_ENDPOINT}
				tableName="Literature References"
				entities={literatureReferences}
				setEntities={setLiteratureReferences}
				totalRecords={totalRecords}
				setTotalRecords={setTotalRecords}
				tableState={tableState}
				setTableState={setTableState}
				columns={columns}
				dataKey="curie"
				isEditable={false}
				isInEditMode={isInEditMode}
				setIsInEditMode={setIsInEditMode}
				defaultColumnWidth={DEFAULT_COLUMN_WIDTH}
				toasts={{ toast_topleft, toast_topright }}
				errorObject={{ errorMessages, setErrorMessages }}
				fetching={isFetching || isLoading}
			/>
		</Card>
	);
};
