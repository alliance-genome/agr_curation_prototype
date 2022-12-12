import React, { useRef, useState } from 'react';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { EllipsisTableCell } from '../../components/EllipsisTableCell';
import { Toast } from 'primereact/toast';
import { Tooltip } from 'primereact/tooltip';


export const GenesTable = () => {

	const [isEnabled, setIsEnabled] = useState(true);
	const [errorMessages, setErrorMessages] = useState({});

	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);

	const nameBodyTemplate = (rowData) => {
		if (rowData?.geneFullName) {
			return (
				<>
					<EllipsisTableCell otherClasses={`a${rowData.curie.replace(':', '')}`}>
						{rowData.geneFullName.displayText}
					</EllipsisTableCell>
					<Tooltip target={`.a${rowData.curie.replace(':', '')}`} content={rowData.geneFullName.displayText} />
				</>
			)
		}
	};

	const taxonBodyTemplate = (rowData) => {
			if (rowData.taxon) {
					return (
							<>
									<EllipsisTableCell otherClasses={`${"TAXON_NAME_"}${rowData.curie.replace(':', '')}${rowData.taxon.curie.replace(':', '')}`}>
											{rowData.taxon.name} ({rowData.taxon.curie})
									</EllipsisTableCell>
									<Tooltip target={`.${"TAXON_NAME_"}${rowData.curie.replace(':', '')}${rowData.taxon.curie.replace(':', '')}`} content= {`${rowData.taxon.name} (${rowData.taxon.curie})`} style={{ width: '250px', maxWidth: '450px' }}/>
							</>
					);
			}
	};

	const columns = [
		{
			field: "curie",
			header: "Curie",
			sortable: isEnabled,
			filter: true,
			filterElement: {type: "input", filterName: "curieFilter", fields: ["curie"]},
		},
		{
			field: "geneFullName.displayText",
			header: "Name",
			sortable: isEnabled,
			filter: true,
			body: nameBodyTemplate,
			filterElement: {type: "input", filterName: "nameFilter", fields: ["geneFullName.displayText", "geneFullName.formatText"]},
		},
		{
			field: "geneSymbol.displayText",
			header: "Symbol",
			sortable: isEnabled,
			filter: true,
			filterElement: {type: "input", filterName: "symbolFilter", fields: ["geneSymbol.displayText", "geneSymbol.formatText"]},
		},
		{
			field: "taxon.name",
			header: "Taxon",
			sortable: isEnabled,
			body: taxonBodyTemplate,
			filter: true,
			filterElement: {type: "input", filterName: "taxonFilter", fields: ["taxon.curie","taxon.name"]},
		}
	];

	return (
			<div className="card">
				<Toast ref={toast_topleft} position="top-left" />
				<Toast ref={toast_topright} position="top-right" />
				<GenericDataTable
					endpoint="gene"
					tableName="Genes"
					columns={columns}
					isEditable={false}
					isEnabled={isEnabled}
					setIsEnabled={setIsEnabled}
					toasts={{toast_topleft, toast_topright }}
					initialColumnWidth={20}
					errorObject = {{errorMessages, setErrorMessages}}
				/>
			</div>
	)
}
