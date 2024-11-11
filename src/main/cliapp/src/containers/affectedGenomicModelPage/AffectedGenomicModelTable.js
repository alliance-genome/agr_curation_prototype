import React, { useState, useRef } from 'react';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { Toast } from 'primereact/toast';
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_CONFIGS } from '../../constants/FilterFields';
import { StringTemplate } from '../../components/Templates/StringTemplate';
import { IdTemplate } from '../../components/Templates/IdTemplate';
import { BooleanTemplate } from '../../components/Templates/BooleanTemplate';
import { ObjectListTemplate } from '../../components/Templates/ObjectListTemplate';
import { useGetTableData } from '../../service/useGetTableData';
import { useGetUserSettings } from '../../service/useGetUserSettings';

import { SearchService } from '../../service/SearchService';
import { crossReferencesSort } from '../../components/Templates/utils/sortMethods';
import { OntologyTermTemplate } from '../../components/Templates/OntologyTermTemplate';

export const AffectedGenomicModelTable = () => {
	const [isInEditMode, setIsInEditMode] = useState(false);
	const [errorMessages, setErrorMessages] = useState({});
	const [totalRecords, setTotalRecords] = useState(0);
	const [agms, setAgms] = useState([]);

	const searchService = new SearchService();

	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);

	const columns = [
		{
			field: 'curie',
			header: 'Curie',
			sortable: true,
			filterConfig: FILTER_CONFIGS.curieFilterConfig,
		},
		{
			field: 'modEntityId',
			header: 'MOD Entity ID',
			body: (rowData) => <IdTemplate id={rowData.modEntityId} />,
			sortable: true,
			filterConfig: FILTER_CONFIGS.modentityidFilterConfig,
		},
		{
			field: 'modInternalId',
			header: 'MOD Internal ID',
			body: (rowData) => <IdTemplate id={rowData.modInternalId} />,
			sortable: true,
			filterConfig: FILTER_CONFIGS.modinternalidFilterConfig,
		},
		{
			field: 'name',
			header: 'Name',
			body: (rowData) => <StringTemplate string={rowData.name} />,
			sortable: true,
			filterConfig: FILTER_CONFIGS.nameFilterConfig,
		},
		{
			field: 'subtype.name',
			header: 'Sub Type',
			body: (rowData) => <StringTemplate string={rowData.subtype?.name} />,
			sortable: true,
			filterConfig: FILTER_CONFIGS.subtypeFilterConfig,
		},
		{
			field: 'taxon.name',
			header: 'Taxon',
			sortable: true,
			body: (rowData) => <OntologyTermTemplate term={rowData.taxon} />,
			filterConfig: FILTER_CONFIGS.taxonFilterConfig,
		},
		{
			field: 'dataProvider.sourceOrganization.abbreviation',
			header: 'Data Provider',
			sortable: true,
			filterConfig: FILTER_CONFIGS.agmDataProviderFilterConfig,
		},
		{
			field: 'crossReferences.displayName',
			header: 'Cross References',
			sortable: true,
			filterConfig: FILTER_CONFIGS.crossReferencesFilterConfig,
			body: (rowData) => (
				<ObjectListTemplate
					list={rowData.crossReferences}
					sortMethod={crossReferencesSort}
					stringTemplate={(item) => `${item.displayName} (${item.resourceDescriptorPage.name})`}
				/>
			),
		},
		{
			field: 'updatedBy.uniqueId',
			header: 'Updated By',
			sortable: true,
			body: (rowData) => <StringTemplate string={rowData.updatedBy?.uniqueId} />,
			filterConfig: FILTER_CONFIGS.updatedByFilterConfig,
		},
		{
			field: 'dateUpdated',
			header: 'Date Updated',
			sortable: true,
			filter: true,
			body: (rowData) => <StringTemplate string={rowData.dateUpdated} />,
			filterConfig: FILTER_CONFIGS.dateUpdatedFilterConfig,
		},
		{
			field: 'createdBy.uniqueId',
			header: 'Created By',
			sortable: true,
			filter: true,
			body: (rowData) => <StringTemplate string={rowData.createdBy?.uniqueId} />,
			filterConfig: FILTER_CONFIGS.createdByFilterConfig,
		},
		{
			field: 'dateCreated',
			header: 'Date Created',
			sortable: true,
			filter: true,
			body: (rowData) => <StringTemplate string={rowData.dateCreated} />,
			filterConfig: FILTER_CONFIGS.dataCreatedFilterConfig,
		},
		{
			field: 'internal',
			header: 'Internal',
			body: (rowData) => <BooleanTemplate value={rowData.internal} />,
			filter: true,
			filterConfig: FILTER_CONFIGS.internalFilterConfig,
			sortable: true,
		},
		{
			field: 'obsolete',
			header: 'Obsolete',
			body: (rowData) => <BooleanTemplate value={rowData.obsolete} />,
			filter: true,
			filterConfig: FILTER_CONFIGS.obsoleteFilterConfig,
			sortable: true,
		},
	];

	const DEFAULT_COLUMN_WIDTH = 100 / columns.length;
	const SEARCH_ENDPOINT = 'agm';

	const initialTableState = getDefaultTableState('AffectedGenomicModels', columns, DEFAULT_COLUMN_WIDTH);

	const { settings: tableState, mutate: setTableState } = useGetUserSettings(
		initialTableState.tableSettingsKeyName,
		initialTableState
	);

	const { isFetching, isLoading } = useGetTableData({
		tableState,
		endpoint: SEARCH_ENDPOINT,
		setIsInEditMode,
		setEntities: setAgms,
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
				tableName="Affected Genomic Models"
				entities={agms}
				setEntities={setAgms}
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
