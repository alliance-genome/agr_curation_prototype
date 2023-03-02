import React, { useState, useRef } from 'react';

import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { EllipsisTableCell } from '../../components/EllipsisTableCell';

import { Card } from 'primereact/card';
import { Tooltip } from "primereact/tooltip";
import { Toast } from 'primereact/toast';
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_FIELDS } from '../../constants/FilterFields';

export const LiteratureReferenceTable = () => {

		const [isEnabled, setIsEnabled] = useState(true);
		const [errorMessages, setErrorMessages] = useState({});

		const toast_topleft = useRef(null);
		const toast_topright = useRef(null);

		const crossReferenceTemplate = (rowData) => {
				if (rowData && rowData.cross_references) {
						const sortedCross_References= rowData.cross_references.sort((a, b) => (a.curie > b.curie) ? 1 : -1);
						return (<div>
								<ul stype={{ listStypeType: 'none' }}>
										{sortedCross_References.map((a, index) =>
												<li key={index}>
														<EllipsisTableCell>
																{a.curie}
														</EllipsisTableCell>
												</li>
										)}
								</ul>
						</div>);
				}
		};

		const titleTemplate = (rowData) => {
				return (
						<>
								<EllipsisTableCell otherClasses={`${"TITLE_"}${rowData.curie.replace(':', '')}`}>
										{rowData.title}
								</EllipsisTableCell>
								<Tooltip target={`.${"TITLE_"}${rowData.curie.replace(':', '')}`} content={rowData.title} style={{ width: '450px', maxWidth: '450px' }}/>
						</>
				);
		};

		const abstractTemplate = (rowData) => {
				return (
						<>
								<EllipsisTableCell otherClasses={`${"ABSTRACT_"}${rowData.curie.replace(':', '')}`}>
										{rowData.abstract}
								</EllipsisTableCell>
								<Tooltip target={`.${"ABSTRACT_"}${rowData.curie.replace(':', '')}`} content={rowData.abstract} style={{ width: '450px', maxWidth: '450px' }}/>
						</>
				);
		};

		const citationTemplate = (rowData) => {
				return (
						<>
								<EllipsisTableCell otherClasses={`${"CITATION_"}${rowData.curie.replace(':', '')}`}>
										{rowData.citation}
								</EllipsisTableCell>
								<Tooltip target={`.${"CITATION_"}${rowData.curie.replace(':', '')}`} content={rowData.citation} style={{ width: '450px', maxWidth: '450px' }}/>
						</>
				);
		};

		const columns = [{
						field: "curie",
						header: "Curie",
						sortable: { isEnabled },
						filter: true,
						filterElement: {type: "input", fieldSet: "curieFieldSet"}, 
				}, {
						field: "cross_references.curie",
						header: "Cross References",
						sortable: isEnabled,
						body: crossReferenceTemplate,
						filter: true,
						filterElement: {type: "input", fieldSet: "cross_referenceFieldSet"}, 
				}, {
						field: "title",
						header: "Title",
						sortable: isEnabled,
						filter: true,
						body : titleTemplate,
						filterElement: {type: "input", fieldSet: "titleFieldSet"}, 
				}, {
						field: "abstract",
						header: "Abstract",
						sortable: isEnabled,
						filter: true,
						body : abstractTemplate,
						filterElement: {type: "input", fieldSet: "abstractFieldSet"}, 
				}, {
						field: "citation",
						header: "Citation",
						sortable: isEnabled,
						filter: true,
						body : citationTemplate,
						filterElement: {type: "input", fieldSet: "citationFieldSet"}, 
				}
		];
		const defaultColumnNames = columns.map((col) => {
			return col.header;
		});


		const initialTableState = getDefaultTableState("LiteratureReferences", defaultColumnNames);

		return (
						<Card>
								<Toast ref={toast_topleft} position="top-left" />
								<Toast ref={toast_topright} position="top-right" />
								<GenericDataTable 
									endpoint="literature-reference" 
									tableName="Literature References" 
									columns={columns}	 
									defaultColumnNames={defaultColumnNames}
									initialTableState={initialTableState}
									isEditable={false}
									isEnabled={isEnabled}
									setIsEnabled={setIsEnabled}
									toasts={{toast_topleft, toast_topright }}
									initialColumnWidth={20}
									errorObject = {{errorMessages, setErrorMessages}}
								/>
						</Card>
		);

}
