import React, { useRef, useState } from 'react';
import { Toast } from 'primereact/toast';
import { TabView, TabPanel } from 'primereact/tabview';
import { BooleanTemplate } from '../../components/Templates/BooleanTemplate';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { GenericDataTree } from '../../components/GenericDataTree';
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_CONFIGS } from '../../constants/FilterFields';
import { useGetTableData } from '../../service/useGetTableData';
import { useGetUserSettings } from '../../service/useGetUserSettings';

import { IdTemplate } from '../../components/Templates/IdTemplate';
import { StringTemplate } from '../../components/Templates/StringTemplate';

import { SearchService } from '../../service/SearchService';
import { StringListTemplate } from '../../components/Templates/StringListTemplate';

export const GeneralOntologyComponent = ({ name, endpoint, showNamespace, showAbbreviation, hideDefinition }) => {
	const [isInEditMode, setIsInEditMode] = useState(false);
	const [errorMessages, setErrorMessages] = useState({});
	const [totalRecords, setTotalRecords] = useState(0);
	const [ontologies, setOntologies] = useState([]);

	const [activeTabIndex, setActiveTabIndex] = useState(0);

	const searchService = new SearchService();

	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);

	const columns = [];

	columns.push({
		field: 'curie',
		header: 'Curie',
		sortable: true,
		body: (rowData) => <IdTemplate id={rowData.curie} />,
		filterConfig: FILTER_CONFIGS.curieFilterConfig,
	});
	columns.push({
		field: 'name',
		header: 'Name',
		sortable: true,
		body: (rowData) => <StringTemplate string={rowData.name} />,
		filterConfig: FILTER_CONFIGS.nameFilterConfig,
	});
	if (!hideDefinition) {
		columns.push({
			field: 'definition',
			header: 'Definition',
			sortable: true,
			body: (rowData) => <StringTemplate string={rowData.definition} />,
			filterConfig: FILTER_CONFIGS.definitionFilterConfig,
		});
	}
	if (showAbbreviation) {
		columns.push({
			field: 'abbreviation',
			header: 'Abbreviation',
			sortable: true,
			body: (rowData) => <StringTemplate string={rowData.abbreviation} />,
			filterConfig: FILTER_CONFIGS.abbreviationFilterConfig,
		});
	}
	if (showNamespace) {
		columns.push({
			field: 'namespace',
			header: 'Name Space',
			sortable: true,
			body: (rowData) => <StringTemplate string={rowData.namespace} />,
			filterConfig: FILTER_CONFIGS.namespaceFilterConfig,
		});
	}
	columns.push({
		field: 'synonyms.name',
		header: 'Synonyms',
		body: (rowData) => <StringListTemplate list={rowData.synonyms?.map((synonym) => synonym?.name)} />,
		sortable: true,
		filterConfig: FILTER_CONFIGS.ontologySynonymsFilterConfig,
	});
	columns.push({
		field: 'secondaryIdentifiers',
		header: 'Secondary IDs',
		sortable: true,
		body: (rowData) => <StringListTemplate list={rowData.secondaryIdentifiers} />,
		filterConfig: FILTER_CONFIGS.secondaryIdsFilterConfig,
	});
	columns.push({
		field: 'obsolete',
		header: 'Obsolete',
		sortable: true,
		body: (rowData) => <BooleanTemplate value={rowData.obsolete} />,
		filterConfig: FILTER_CONFIGS.obsoleteFilterConfig,
	});

	const DEFAULT_COLUMN_WIDTH = 17;
	const defaultFilters = { obsoleteFilter: { obsolete: { queryString: 'false' } } };

	const initialTableState = getDefaultTableState(name, columns, DEFAULT_COLUMN_WIDTH, defaultFilters);

	const { settings: tableState, mutate: setTableState } = useGetUserSettings(
		initialTableState.tableSettingsKeyName,
		initialTableState
	);

	const { isFetching, isLoading } = useGetTableData({
		tableState,
		endpoint: endpoint,
		setIsInEditMode,
		setEntities: setOntologies,
		setTotalRecords,
		toast_topleft,
		searchService,
	});

	return (
		<>
			<Toast ref={toast_topleft} position="top-left" />
			<Toast ref={toast_topright} position="top-right" />
			<TabView activeIndex={activeTabIndex} onTabChange={(e) => setActiveTabIndex(e.index)}>
				<TabPanel header="Table View">
					<GenericDataTable
						endpoint={endpoint}
						tableName={name}
						entities={ontologies}
						setEntities={setOntologies}
						totalRecords={totalRecords}
						setTotalRecords={setTotalRecords}
						tableState={tableState}
						setTableState={setTableState}
						columns={columns}
						dataKey="curie"
						isEditable={false}
						isInEditMode={isInEditMode}
						setIsInEditMode={setIsInEditMode}
						toasts={{ toast_topleft, toast_topright }}
						errorObject={{ errorMessages, setErrorMessages }}
						defaultColumnWidth={DEFAULT_COLUMN_WIDTH}
						fetching={isFetching || isLoading}
						defaultFilters={defaultFilters}
					/>
				</TabPanel>
				<TabPanel header="Tree View">
					<GenericDataTree
						endpoint={endpoint}
						treeName={name}
						toasts={{ toast_topleft, toast_topright }}
						errorObject={{ errorMessages, setErrorMessages }}
					/>
				</TabPanel>
			</TabView>
		</>
	);
};
