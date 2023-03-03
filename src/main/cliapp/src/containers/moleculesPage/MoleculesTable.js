import React, { useRef, useState } from 'react';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { EllipsisTableCell } from '../../components/EllipsisTableCell';
import { Tooltip } from 'primereact/tooltip';
import { Toast } from 'primereact/toast';
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_FIELDS } from '../../constants/FilterFields';

export const MoleculesTable = () => {

	const [isEnabled, setIsEnabled] = useState(true);
	const [errorMessages, setErrorMessages] = useState({});

	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);

	const inChiBodyTemplate = (rowData) => {
		return (
			<>
				<EllipsisTableCell otherClasses={`a${rowData.curie.replaceAll(':', '')}`}>{rowData.inchi}</EllipsisTableCell>
				<Tooltip target={`.a${rowData.curie.replaceAll(':', '')}`} content={rowData.inchi} />
			</>
		)
	};

	const iupacBodyTemplate = (rowData) => {
		return (
			<>
				<EllipsisTableCell otherClasses={`b${rowData.curie.replaceAll(':', '')}`}>{rowData.iupac}</EllipsisTableCell>
				<Tooltip target={`.b${rowData.curie.replaceAll(':', '')}`} content={rowData.iupac} />
			</>
		)
	};

	const smilesBodyTemplate = (rowData) => {
		return (
			<>
				<EllipsisTableCell otherClasses={`c${rowData.curie.replaceAll(':', '')}`}>{rowData.smiles}</EllipsisTableCell>
				<Tooltip target={`.c${rowData.curie.replaceAll(':', '')}`} content={rowData.smiles} style={{ width: '450px', maxWidth: '450px' }} />
			</>
		)
	};

	const columns = [
		{
			field: "curie",
			header: "Curie",
			sortable: isEnabled,
			filter: true,
			filterElement: {type: "input", fieldSet: "curieFieldSet"}, 
		},
		{
			field: "name",
			header: "Name",
			sortable: isEnabled,
			filter: true,
			filterElement: {type: "input", fieldSet: "nameFieldSet"}, 
		},
		{
			field: "inchi",
			header: "InChi",
			sortable: isEnabled,
			filter: true,
			body: inChiBodyTemplate,
			filterElement: {type: "input", fieldSet: "inchiFieldSet"}, 
		},
		{
			field: "inchiKey",
			header: "InChiKey",
			sortable: isEnabled,
			filter: true,
			filterElement: {type: "input", fieldSet: "inchiKeyFieldSet"}, 
		},
		{
			field: "iupac",
			header: "IUPAC",
			sortable: isEnabled,
			filter: true,
			body: iupacBodyTemplate,
			filterElement: {type: "input", fieldSet: "iupacFieldSet"}, 
		},
		{
			field: "formula",
			header: "Formula",
			sortable: isEnabled,
			filter: true,
			filterElement: {type: "input", fieldSet: "formulaFieldSet"}, 
		},
		{
			field: "smiles",
			header: "SMILES",
			sortable: isEnabled,
			filter: true,
			body: smilesBodyTemplate,
			filterElement: {type: "input", fieldSet: "smilesFieldSet"}, 
		}
	];

	const defaultColumnNames = columns.map((col) => {
		return col.header;
	});


	const initialTableState = getDefaultTableState("Molecule", defaultColumnNames);


	return (
			<div className="card">
				<Toast ref={toast_topleft} position="top-left" />
				<Toast ref={toast_topright} position="top-right" />
				<GenericDataTable 
					endpoint="molecule" 
					tableName="Molecule" 
					columns={columns}	 
					defaultColumnNames={defaultColumnNames}
					initialTableState={initialTableState}
					isEditable={false}
					isEnabled={isEnabled}
					setIsEnabled={setIsEnabled}
					toasts={{toast_topleft, toast_topright }}
					initialColumnWidth={13}
					errorObject = {{errorMessages, setErrorMessages}}
				/>
			</div>
	)
}
