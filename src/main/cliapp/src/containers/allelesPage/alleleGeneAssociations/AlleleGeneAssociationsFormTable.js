import { useState } from 'react';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { DeleteAction } from '../../../components/Actions/DeletionAction';
import { EvidenceEditor } from '../../../components/Editors/EvidenceEditor';
import { GeneEditor } from '../../../components/Editors/GeneEditor';
import { RelatedNoteEditor } from '../../../components/Editors/RelatedNoteEditor';
import { EvidenceCodeEditor } from '../../../components/Editors/EvidenceCodeEditor';
import { RelatedNotesDialogEditOnly } from '../../../components/RelatedNotesDialogEditOnly';
import { VocabularyTermSetEditor } from '../../../components/Editors/VocabularyTermSetEditor';
import { RelationshipFilterTemplate } from './RelationshipFilterTemplate';

export const AlleleGeneAssociationsFormTable = ({
	alleleGeneAssociations,
	editingRows,
	onRowEditChange,
	tableRef,
	deletionHandler,
	errorMessages,
	evidenceOnChangeHandler,
	alleleGeneRelationOnChangeHandler,
	geneOnChangeHandler,
	evidenceCodeOnChangeHandler,
	relatedNoteOnChangeHandler,
	dispatch,
}) => {
	const [rows, setRows] = useState(5);
	const [first, setFirst] = useState(0);
	const [filters, setFilters] = useState({
		'relation.name': { value: null, matchMode: 'in' },
		'alleleGeneAssociationObject.primaryExternalId': { value: null, matchMode: 'contains' },
		'relatedNote.freeText': { value: null, matchMode: 'contains' },
		'evidenceCode.curie': { value: null, matchMode: 'contains' },
		evidenceCurieSearchFilter: { value: null, matchMode: 'contains' },
		'updatedBy.uniqueId': { value: null, matchMode: 'startsWith' },
		dateUpdated: { value: null, matchMode: 'startsWith' },
	});

	const [relatedNotesData, setRelatedNotesData] = useState({
		relatedNotes: [],
		dialogIsVisible: false,
		rowIndex: null,
		mainRowProps: {},
		errorMessages,
	});
	const onPage = (options) => {
		setRows(options.rows);
		setFirst(options.first);
	};

	const onFilter = (options) => {
		setFilters(options.filters);
	};

	return (
		<>
			<DataTable
				value={alleleGeneAssociations}
				dataKey="dataKey"
				showGridlines
				editMode="row"
				removableSort
				filterDisplay="row"
				onPage={onPage}
				onFilter={onFilter}
				filters={filters}
				size="small"
				editingRows={editingRows}
				resizableColumns
				columnResizeMode="expand"
				onRowEditChange={onRowEditChange}
				ref={tableRef}
				paginator
				paginatorTemplate="CurrentPageReport FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown"
				currentPageReportTemplate="Showing {first} to {last} of {totalRecords}"
				rows={rows}
				first={first}
				rowsPerPageOptions={[5, 10, 20, 50]}
			>
				<Column
					editor={(props) => <DeleteAction deletionHandler={deletionHandler} id={props.rowData?.dataKey} />}
					className="max-w-4rem"
					bodyClassName="text-center"
					headerClassName="surface-0"
					frozen
				/>
				<Column
					editor={(props) => {
						return (
							<VocabularyTermSetEditor
								props={props}
								onChangeHandler={alleleGeneRelationOnChangeHandler}
								errorMessages={errorMessages}
								dataKey={props?.rowData?.dataKey}
								rowIndex={props.rowIndex}
								vocabType="allele_gene_relation"
								field="relation"
								showClear={false}
							/>
						);
					}}
					field="relation"
					header="Relationship"
					headerClassName="surface-0"
					filter
					sortable
					filterField="relation.name"
					sortField="relation.name"
					showFilterMenu={false}
					filterElement={(props) => {
						return <RelationshipFilterTemplate options={props} alleleGeneAssociations={alleleGeneAssociations} />;
					}}
				/>
				<Column
					editor={(props) => {
						return (
							<GeneEditor
								fieldName="alleleGeneAssociationObject"
								props={props}
								errorMessages={errorMessages}
								onChange={geneOnChangeHandler}
								dataKey={props?.rowData?.dataKey}
							/>
						);
					}}
					field="alleleGeneAssociationObject"
					header="Gene"
					headerClassName="surface-0"
					filter
					sortable
					filterField="alleleGeneAssociationObject.primaryExternalId"
					sortField="alleleGeneAssociationObject.primaryExternalId"
					showFilterMenu={false}
				/>
				<Column
					editor={(props) => {
						return (
							<RelatedNoteEditor
								rowProps={props}
								relatedNote={props.rowData.relatedNote}
								errorMessages={errorMessages}
								dataKey={props?.rowData?.dataKey}
								rows={props.rows}
								setRelatedNotesData={setRelatedNotesData}
							/>
						);
					}}
					field="relatedNote"
					header="Note"
					headerClassName="surface-0"
					filter
					filterField="relatedNote.freeText"
					showFilterMenu={false}
					sortable
					sortField="relatedNote.freeText"
				/>
				<Column
					editor={(props) => {
						return (
							<EvidenceCodeEditor
								props={props}
								onChangeHandler={evidenceCodeOnChangeHandler}
								errorMessages={errorMessages}
								dataKey={props.rowData?.dataKey}
							/>
						);
					}}
					filter
					sortable
					filterField="evidenceCode.curie"
					sortField="evidenceCode.curie"
					showFilterMenu={false}
					field="evidenceCode"
					header="Evidence Code"
					headerClassName="surface-0"
				/>
				<Column
					editor={(props) => {
						return <EvidenceEditor props={props} errorMessages={errorMessages} onChange={evidenceOnChangeHandler} />;
					}}
					filter
					filterField="evidenceCurieSearchFilter"
					showFilterMenu={false}
					field="evidence.curie"
					sortable
					sortField="evidenceCurieSearchFilter"
					header="Evidence"
					headerClassName="surface-0"
				/>
				<Column field="updatedBy.uniqueId" header="Updated By" />
				<Column field="dateUpdated" header="Date Updated" />
			</DataTable>
			<RelatedNotesDialogEditOnly
				relatedNotesData={relatedNotesData}
				setRelatedNotesData={setRelatedNotesData}
				tableErrorMessages={errorMessages}
				dispatch={dispatch}
				singleValue={true}
				onChange={relatedNoteOnChangeHandler}
				defaultValues={{ noteType: 'comment' }}
				errorField="relatedNote"
				entityType="alleleGeneAssociations"
				noteTypeVocabType="allele_genomic_entity_association_note_type"
			/>
		</>
	);
};
