import React, { useRef, useState } from 'react'
import { Toast } from 'primereact/toast';
import { NameTemplate } from './NameTemplate';
import { TabView, TabPanel } from 'primereact/tabview';
import { DefinitionTemplate } from './DefinitionTemplate';
import { BooleanTemplate } from '../../components/BooleanTemplate';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { GenericDataTree } from '../../components/GenericDataTree';
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_FIELDS } from '../../constants/FilterFields';

export const GeneralOntologyComponent = ({name, endpoint, showNamespace, showAbbreviation, hideDefinition}) => {
	const [isEnabled, setIsEnabled] = useState(true);
	const [errorMessages, setErrorMessages] = useState({});

	const [activeTabIndex, setActiveTabIndex] = useState(0);

	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);

	const columns = [];

	columns.push(
		{
			field: "curie",
			header: "Curie",
			filterElement: {type: "input", fieldSet: "curieFieldSet"}
		}
	);
	columns.push(
		{
			field: "name",
			header: "Name",
			body: (rowData) => <NameTemplate rowData={rowData}/>,
			filterElement: {type: "input", fieldSet: "nameFieldSet"}
		},
	);
	if(!hideDefinition) {
		columns.push(
			{
				field: "definition",
				header: "Definition",
				body: (rowData) => <DefinitionTemplate rowData={rowData} />,
				filterElement: {type: "input", fieldSet: "definitionFieldSet"}
			},
		);
	}
	if(showAbbreviation) {
		columns.push(
			{
				field: "abbreviation",
				header: "Abbreviation",
				filterElement: {type: "input", fieldSet: "abbreviationFieldSet"}
			}
		);
	}
	if(showNamespace) {
		columns.push(
			{
				field: "namespace",
				header: "Name Space",
				filterElement: {type: "input", fieldSet: "abbreviationFieldSet"}
			}
		);
	}
	columns.push(
		{
			field: "obsolete",
			header: "Obsolete",
			body: (rowData) => <BooleanTemplate value={rowData.obsolete}/>,
			filterElement: {type: "input", fieldSet: "obsoleteFieldSet"}
		}
	);

	const defaultColumnNames = columns.map((col) => {
		return col.header;
	});


	const initialTableState = getDefaultTableState(name, defaultColumnNames);

	return (
		<>
			<Toast ref={toast_topleft} position="top-left" />
			<Toast ref={toast_topright} position="top-right" />
			<TabView activeIndex={activeTabIndex} onTabChange={(e) => setActiveTabIndex(e.index)}>
				<TabPanel header="Table View">
					<GenericDataTable
						endpoint={endpoint}
						tableName={name}
						columns={columns}
						defaultColumnNames={defaultColumnNames}
						initialTableState={initialTableState}
						isEditable={false}
						isEnabled={isEnabled}
						setIsEnabled={setIsEnabled}
						toasts={{toast_topleft, toast_topright }}
						initialColumnWidth={17}
						errorObject = {{errorMessages, setErrorMessages}}
					/>
			    </TabPanel>
			    <TabPanel header="Tree View">
			        <GenericDataTree
						endpoint={endpoint}
						treeName={name}
						toasts={{toast_topleft, toast_topright }}
						errorObject = {{errorMessages, setErrorMessages}}
					/>
			    </TabPanel>
			</TabView>
		</>
	)
}
