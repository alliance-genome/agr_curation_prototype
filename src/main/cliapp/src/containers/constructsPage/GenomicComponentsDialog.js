import React, { useRef, useState } from 'react';
import { Dialog } from 'primereact/dialog';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Tooltip } from 'primereact/tooltip';
import { ColumnGroup } from 'primereact/columngroup';
import { Row } from 'primereact/row';
import { EllipsisTableCell } from '../../components/EllipsisTableCell';
import { evidenceTemplate } from '../../components/EvidenceComponent';
import { Button } from 'primereact/button';
import { RelatedNotesDialog } from '../../components/RelatedNotesDialog';
import { getIdentifier } from '../../utils/utils';

export const GenomicComponentsDialog = ({
	originalComponentsData,
	setOriginalComponentsData,
	errorMessagesMainRow,
	setErrorMessagesMainRow,
}) => {
	const { originalComponents, dialog } = originalComponentsData;
	const [localComponents, setLocalComponents] = useState(null);
	const tableRef = useRef(null);

	const [relatedNotesData, setRelatedNotesData] = useState({
		relatedNotes: [],
		isInEdit: false,
		dialog: false,
		rowIndex: null,
		mainRowProps: {},
	});

	const showDialogHandler = () => {
		let _localComponents = cloneComponents(originalComponents);
		setLocalComponents(_localComponents);
	};

	const hideDialog = () => {
		setOriginalComponentsData((originalComponentsData) => {
			return {
				...originalComponentsData,
				dialog: false,
			};
		});
		let _localComponents = [];
		setLocalComponents(_localComponents);
	};

	const cloneComponents = (clonableComponents) => {
		let _clonableComponents = global.structuredClone(clonableComponents);
		if (_clonableComponents) {
			let counter = 0;
			_clonableComponents.forEach((note) => {
				note.dataKey = counter++;
			});
		} else {
			_clonableComponents = [];
		}
		return _clonableComponents;
	};

	const internalTemplate = (rowData) => {
		return <EllipsisTableCell>{JSON.stringify(rowData.internal)}</EllipsisTableCell>;
	};

	const obsoleteTemplate = (rowData) => {
		return <EllipsisTableCell>{JSON.stringify(rowData.obsolete)}</EllipsisTableCell>;
	};

	const handleRelatedNotesOpen = (event, rowData, isInEdit) => {
		let _relatedNotesData = {};
		_relatedNotesData['originalRelatedNotes'] = rowData.relatedNotes;
		_relatedNotesData['dialog'] = true;
		_relatedNotesData['isInEdit'] = isInEdit;
		setRelatedNotesData(() => ({
			..._relatedNotesData,
		}));
	};

	const relatedNotesTemplate = (rowData) => {
		if (rowData?.relatedNotes) {
			return (
				<Button
					className="p-button-text"
					onClick={(event) => {
						handleRelatedNotesOpen(event, rowData, false);
					}}
				>
					<span style={{ textDecoration: 'underline' }}>{`Notes(${rowData.relatedNotes.length})`}</span>
				</Button>
			);
		}
	};

	const componentTemplate = (rowData) => {
		let componentDisplayValue = '';
		if (
			rowData.constructGenomicEntityAssociationObject.geneSymbol ||
			rowData.constructGenomicEntityAssociationObject.alleleSymbol
		) {
			let symbolValue = rowData.constructGenomicEntityAssociationObject.geneSymbol
				? rowData.constructGenomicEntityAssociationObject.geneSymbol.displayText
				: rowData.constructGenomicEntityAssociationObject.alleleSymbol.displayText;
			componentDisplayValue = symbolValue + ' (' + getIdentifier(rowData.constructGenomicEntityAssociationObject) + ')';
		} else if (rowData.constructGenomicEntityAssociationObject.name) {
			componentDisplayValue =
				rowData.constructGenomicEntityAssociationObject.name +
				' (' +
				getIdentifier(rowData.constructGenomicEntityAssociationObject) +
				')';
		} else {
			componentDisplayValue = getIdentifier(rowData.constructGenomicEntityAssociationObject);
		}
		return (
			<>
				<div
					className={`overflow-hidden text-overflow-ellipsis component_${rowData.id}`}
					dangerouslySetInnerHTML={{ __html: componentDisplayValue }}
				/>
				<Tooltip target={`.component_${rowData.id}`}>
					<div dangerouslySetInnerHTML={{ __html: componentDisplayValue }} />
				</Tooltip>
			</>
		);
	};

	let headerGroup = (
		<ColumnGroup>
			<Row>
				<Column header="Relation" />
				<Column header="Component" />
				<Column header="RelatedNotes" />
				<Column header="Evidence" />
				<Column header="Updated By" />
				<Column header="Date Updated" />
				<Column header="Created By" />
				<Column header="Date Created" />
				<Column header="Internal" />
				<Column header="Obsolete" />
			</Row>
		</ColumnGroup>
	);

	return (
		<>
			<div>
				<Dialog visible={dialog} className="w-8" modal onHide={hideDialog} closable={true} onShow={showDialogHandler}>
					<h3>Component Associations</h3>
					<DataTable
						value={localComponents}
						dataKey="dataKey"
						showGridlines
						editMode="row"
						headerColumnGroup={headerGroup}
						ref={tableRef}
					>
						<Column field="relation.name" header="Relation" headerClassName="surface-0" />
						<Column
							field="constructGenomicEntityAssociationObject.primaryExternalId"
							header="Component"
							headerClassName="surface-0"
							body={componentTemplate}
						/>
						<Column
							field="relatedNotes.freeText"
							header="Related Notes"
							headerClassName="surface-0"
							body={relatedNotesTemplate}
						/>
						<Column
							field="evidence.curie"
							header="Evidence"
							headerClassName="surface-0"
							body={(rowData) => evidenceTemplate(rowData)}
						/>
						<Column field="updatedBy.uniqueId" header="Updated By" headerClassName="surface-0" />
						<Column field="dateUpdated" header="Date Updated" headerClassName="surface-0" />
						<Column field="createdBy.uniqueId" header="Created By" headerClassName="surface-0" />
						<Column field="dateCreated" header="Date Created" headerClassName="surface-0" />
						<Column field="internal" header="Internal" body={internalTemplate} headerClassName="surface-0" />
						<Column field="obsolete" header="Obsolete" body={obsoleteTemplate} headerClassName="surface-0" />
					</DataTable>
				</Dialog>
			</div>
			<RelatedNotesDialog
				originalRelatedNotesData={relatedNotesData}
				setOriginalRelatedNotesData={setRelatedNotesData}
				errorMessagesMainRow={errorMessagesMainRow}
				setErrorMessagesMainRow={setErrorMessagesMainRow}
				noteTypeVocabularyTermSet="construct_component_note_type"
			/>
		</>
	);
};
