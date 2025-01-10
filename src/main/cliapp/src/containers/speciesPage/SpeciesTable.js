import React, { useRef, useState } from 'react';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { Toast } from 'primereact/toast';
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_CONFIGS } from '../../constants/FilterFields';
import { CommaSeparatedArrayTemplate } from '../../components/Templates/CommaSeparatedArrayTemplate';
import { useGetTableData } from '../../service/useGetTableData';
import { useGetUserSettings } from '../../service/useGetUserSettings';

import { SearchService } from '../../service/SearchService';

export const SpeciesTable = () => {
	const [isInEditMode, setIsInEditMode] = useState(false);
	const [errorMessages, setErrorMessages] = useState({});
	const [totalRecords, setTotalRecords] = useState(0);
	const [species, setSpecies] = useState([]);

	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);

	const searchService = new SearchService();

	const columns = [
		{
			field: 'taxon.curie',
			header: 'Taxon',
			sortable: true,
			filter: true,
			filterConfig: FILTER_CONFIGS.speciesTaxonCurieFilterConfig,
		},
		{
			field: 'fullName',
			header: 'Full Name',
			sortable: true,
			filter: true,
			filterConfig: FILTER_CONFIGS.speciesFullNameFilterConfig,
		},
		{
			field: 'displayName',
			header: 'Display Name',
			sortable: true,
			filter: true,
			filterConfig: FILTER_CONFIGS.speciesDisplayNameFilterConfig,
		},
		{
			field: 'abbreviation',
			header: 'Abbreviation',
			sortable: true,
			filter: true,
			filterConfig: FILTER_CONFIGS.speciesAbbreviationFilterConfig,
		},
		{
			field: 'commonNames',
			header: 'Common Names',
			sortable: false,
			filter: true,
			body: (rowData) => <CommaSeparatedArrayTemplate array={rowData.commonNames} />,
			filterConfig: FILTER_CONFIGS.speciesCommonNameFilterConfig,
		},
		{
			field: 'dataProvider.abbreviation',
			header: 'Data Provider',
			sortable: true,
			filter: true,
			filterConfig: FILTER_CONFIGS.speciesDataProviderFilterConfig,
		},
		{
			field: 'phylogeneticOrder',
			header: 'Phylogenetic Order',
			sortable: true,
		},
		{
			field: 'assembly_curie',
			header: 'Assembly',
			sortable: false,
			//filterConfig: FILTER_CONFIGS.speciesAssemblyFilterConfig
		},
	];

	const DEFAULT_COLUMN_WIDTH = 10;
	const SEARCH_ENDPOINT = 'species';

	const initialTableState = getDefaultTableState('Species', columns, DEFAULT_COLUMN_WIDTH);

	const { settings: tableState, mutate: setTableState } = useGetUserSettings(
		initialTableState.tableSettingsKeyName,
		initialTableState
	);

	const { isFetching, isLoading } = useGetTableData({
		tableState,
		endpoint: SEARCH_ENDPOINT,
		setIsInEditMode,
		setEntities: setSpecies,
		setTotalRecords,
		toast_topleft,
		searchService,
	});

	return (
		<>
			<div className="card">
				<Toast ref={toast_topleft} position="top-left" />
				<Toast ref={toast_topright} position="top-right" />
				<GenericDataTable
					endpoint={SEARCH_ENDPOINT}
					tableName="Species"
					entities={species}
					setEntities={setSpecies}
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
		</>
	);
};
