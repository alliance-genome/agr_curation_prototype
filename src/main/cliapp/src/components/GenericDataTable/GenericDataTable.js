import React, { useRef, useState, useEffect } from 'react';

import { DataTable } from 'primereact/datatable';
import { Dialog } from 'primereact/dialog';
import { Column } from 'primereact/column';
import { Button } from 'primereact/button';
import { Toast } from 'primereact/toast';
import { MultiSelect } from 'primereact/multiselect';
import { Checkbox } from 'primereact/checkbox';

import { FilterComponent } from '../Filters/FilterComponent';
import { DataTableHeaderFooterTemplate } from '../DataTableHeaderFooterTemplate';
import { DuplicationAction } from '../Actions/DuplicationAction';
import { EntityDetailsAction } from '../Actions/EntityDetailsAction';

import { filterColumns, orderColumns, getIdentifier } from '../../utils/utils';
import { useGenericDataTable } from './useGenericDataTable';

import './styles.scss';
import { DataTableFooter } from './DataTableFooter';
export const GenericDataTable = (props) => {
	const {
		tableName,
		isInEditMode,
		aggregationFields,
		endpoint,
		columns,
		headerButtons,
		deletionEnabled,
		handleDuplication,
		duplicationEnabled,
		hasDetails = false,
		dataKey = 'id',
		deprecateOption = false,
		modReset = false,
		highlightObsolete = true,
		fetching,
		isEditable,
	} = props;

	const {
		setSelectedColumnNames,
		tableStateConfirm,
		onFilter,
		setColumnList,
		entities,
		dataTable,
		editingRows,
		onRowEditInit,
		onRowEditCancel,
		onRowEditSave,
		onRowEditChange,
		onSort,
		colReorderHandler,
		handleColumnResizeEnd,
		totalRecords,
		onLazyLoad,
		columnList,
		handleDeletion,
		handleDeprecation,
		tableState,
		exceptionDialog,
		setExceptionDialog,
		setToModDefault,
		resetTableState,
		exceptionMessage,
	} = useGenericDataTable(props);

	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);
	const [deleteDialog, setDeleteDialog] = useState(false);
	const [deprecateDialog, setDeprecateDialog] = useState(false);
	const [errorDialog, setErrorDialog] = useState(false);
	const [idToDelete, setIdToDelete] = useState(null);
	const [entityToDelete, setEntityToDelete] = useState(null);
	const [deletionErrorMessage, setDeletionErrorMessage] = useState(null);
	const [allowDelete, setAllowDelete] = useState(false);

	const createMultiselectComponent = () => {
		return (
			<MultiSelect
				aria-label="columnToggle"
				value={tableState.selectedColumnNames}
				options={tableState.defaultColumnNames}
				filter
				resetFilterOnHide
				onChange={(e) => {
					let orderedSelectedColumnNames = tableState.orderedColumnNames.filter((columnName) => {
						return e.value.some((selectedColumn) => selectedColumn === columnName);
					});

					setSelectedColumnNames(orderedSelectedColumnNames);
				}}
				className="w-20rem text-center"
				disabled={isInEditMode}
				maxSelectedLabels={4}
			/>
		);
	};

	const header = (
		<DataTableHeaderFooterTemplate
			title={tableName + ' Table'}
			tableState={tableState}
			multiselectComponent={createMultiselectComponent()}
			buttons={headerButtons ? headerButtons(isInEditMode) : undefined}
			tableStateConfirm={tableStateConfirm}
			setToModDefault={setToModDefault}
			resetTableState={resetTableState}
			isInEditMode={isInEditMode}
			modReset={modReset}
		/>
	);

	const filterComponentTemplate = (config) => {
		return (
			<FilterComponent
				filterConfig={config}
				isInEditMode={isInEditMode}
				onFilter={onFilter}
				aggregationFields={aggregationFields}
				tableState={tableState}
				endpoint={endpoint}
			/>
		);
	};

	useEffect(() => {
		const orderedColumns = orderColumns(columns, tableState.orderedColumnNames);
		const filteredColumns = filterColumns(orderedColumns, tableState.selectedColumnNames);
		setColumnList(() => {
			return filteredColumns.map((col) => {
				if (col) {
					return (
						<Column
							style={{
								minWidth: `${tableState.columnWidths[col.field]}vw`,
								maxWidth: `${tableState.columnWidths[col.field]}vw`,
								padding: '4px 10px 4px',
							}}
							headerClassName="surface-0"
							columnKey={col.field}
							key={col.field}
							field={col.field}
							header={col.header}
							body={col.body}
							sortable={col.sortable && !isInEditMode}
							filter
							editor={col.editor}
							showFilterMenu={false}
							filterElement={() => filterComponentTemplate(col.filterConfig)}
							headerStyle={{ padding: '1rem' }}
						/>
					);
				} else {
					return null;
				}
			});
		});
		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [tableState, isInEditMode]);

	const rowEditorFilterNameHeader = (options) => {
		return (
			<div>
				<span className="p-column-title text-center">Filters</span>
			</div>
		);
	};

	const showDeleteOrDeprecateDialog = (props) => {
		let _idToDelete = props.rowData ? props.rowData[dataKey] : props[dataKey];
		let isPublic = true; // TODO: check field in props when populated
		setIdToDelete(_idToDelete);
		setEntityToDelete(props);
		if (deprecateOption && isPublic) {
			setDeprecateDialog(true);
		} else {
			setDeleteDialog(true);
		}
	};

	const deleteOrDeprecateRow = async (idToDelete, entityToDelete, deprecateOnly) => {
		setDeleteDialog(false);
		setDeprecateDialog(false);
		if (deprecateOnly) {
			handleDeprecation(entityToDelete);
		} else {
			let _deletionErrorMessage = await handleDeletion(idToDelete, entityToDelete);
			setDeletionErrorMessage(_deletionErrorMessage);
			if (_deletionErrorMessage !== null) {
				setErrorDialog(true);
			}
		}
		setAllowDelete(false);
	};

	const deleteAction = (props, disabled) => {
		return (
			<Button
				icon="pi pi-trash"
				className="p-button-text p-0 text-base"
				disabled={disabled}
				onClick={() => showDeleteOrDeprecateDialog(props)}
			/>
		);
	};

	const hideDeprecateDialog = () => {
		setDeprecateDialog(false);
		setAllowDelete(false);
	};

	const hideDeleteDialog = () => {
		setDeleteDialog(false);
	};

	const hideErrorDialog = () => {
		setErrorDialog(false);
	};

	const hideExceptionDialog = () => {
		setExceptionDialog(false);
	};

	const deleteDialogFooter = () => {
		return (
			<React.Fragment>
				<Button label="Cancel" icon="pi pi-times" className="p-button-text" onClick={hideDeleteDialog} />
				<Button
					label="Confirm"
					icon="pi pi-check"
					className="p-button-text"
					onClick={() => deleteOrDeprecateRow(idToDelete, entityToDelete, false)}
				/>
			</React.Fragment>
		);
	};

	const deprecateDialogFooter = () => {
		return (
			<React.Fragment>
				<Button label="Cancel" icon="pi pi-times" className="p-button-text" onClick={hideDeprecateDialog} />
				<Button
					label="Deprecate"
					icon="pi pi-check"
					className="p-button-text"
					onClick={() => deleteOrDeprecateRow(idToDelete, entityToDelete, true)}
				/>
				<Button
					label="Delete"
					icon="pi pi-check"
					className="p-button-text"
					onClick={() => deleteOrDeprecateRow(idToDelete, entityToDelete, false)}
					disabled={!allowDelete}
				/>
			</React.Fragment>
		);
	};

	const errorDialogFooter = () => {
		return (
			<React.Fragment>
				<Button label="OK" icon="pi pi-times" className="p-button-text" onClick={hideErrorDialog} />
			</React.Fragment>
		);
	};

	const exceptionDialogFooter = () => {
		return (
			<React.Fragment>
				<Button label="OK" icon="pi pi-times" className="p-button-text" onClick={hideExceptionDialog} />
			</React.Fragment>
		);
	};

	const getRowClass = (props) => {
		if (props?.obsolete && highlightObsolete) {
			return 'bg-gray-500 text-white';
		}
		return null;
	};

	return (
		<div className="card">
			<Toast ref={toast_topleft} position="top-left" />
			<Toast ref={toast_topright} position="top-right" />
			<DataTable
				dataKey={dataKey}
				value={entities}
				header={header}
				ref={dataTable}
				filterDisplay="row"
				scrollHeight="62vh"
				scrollable={true}
				tableClassName="p-datatable-md"
				editMode="row"
				onRowEditInit={onRowEditInit}
				onRowEditCancel={onRowEditCancel}
				onRowEditSave={onRowEditSave}
				editingRows={editingRows}
				onRowEditChange={onRowEditChange}
				sortMode="multiple"
				removableSort={true}
				onSort={onSort}
				multiSortMeta={tableState.multiSortMeta}
				onColReorder={colReorderHandler}
				reorderableColumns={true}
				resizableColumns={true}
				columnResizeMode="expand"
				showGridlines={true}
				onColumnResizeEnd={handleColumnResizeEnd}
				totalRecords={totalRecords}
				lazy={true}
				rowClassName={(props) => getRowClass(props)}
				loading={fetching}
				loadingIcon="pi pi-spin pi-spinner"
				footer={
					<DataTableFooter
						first={tableState.first}
						rows={tableState.rows}
						totalRecords={totalRecords}
						onLazyLoad={onLazyLoad}
						isInEditMode={isInEditMode}
					/>
				}
			>
				{isEditable && (
					<Column
						field="rowEditor"
						rowEditor
						className={`text-center row-editor-column p-0 text-base`}
						filter
						filterElement={rowEditorFilterNameHeader}
						showFilterMenu={false}
						bodyStyle={{ textAlign: 'center' }}
						frozen
						headerClassName={`surface-0 row-editor-column sticky`}
					/>
				)}
				{deletionEnabled && (
					<Column
						field="delete"
						editor={(props) => deleteAction(props, true)}
						body={(props) => deleteAction(props, isInEditMode)}
						filterElement={rowEditorFilterNameHeader}
						showFilterMenu={false}
						className={`text-center p-0 action-column ${isEditable ? 'visible' : 'hidden'}`}
						bodyStyle={{ textAlign: 'center' }}
						frozen
						headerClassName="surface-0 action-column sticky"
					/>
				)}
				{duplicationEnabled && (
					<Column
						field="duplicate"
						editor={(props) => (
							<DuplicationAction props={props} handleDuplication={handleDuplication} disabled={true} />
						)}
						body={(props) => (
							<DuplicationAction props={props} handleDuplication={handleDuplication} disabled={isInEditMode} />
						)}
						showFilterMenu={false}
						className={`text-center p-0 action-column ${isEditable ? 'visible' : 'hidden'}`}
						bodyStyle={{ textAlign: 'center' }}
						frozen
						headerClassName="surface-0 action-column sticky"
					/>
				)}
				{hasDetails && (
					<Column
						field="details"
						editor={(props) => <EntityDetailsAction identifier={getIdentifier(props.rowData)} disabled={true} />}
						body={(props) => <EntityDetailsAction identifier={getIdentifier(props)} disabled={isInEditMode} />}
						showFilterMenu={false}
						className={`text-center p-0 action-column ${isEditable ? 'visible' : 'hidden'}`}
						bodyStyle={{ textAlign: 'center' }}
						frozen
						headerClassName="surface-0 action-column sticky"
					/>
				)}
				{columnList}
			</DataTable>

			<Dialog
				visible={deleteDialog}
				className="w-30rem"
				header="Confirm Deletion"
				modal
				footer={deleteDialogFooter}
				onHide={hideDeleteDialog}
			>
				<div className="confirmation-content">
					<i className="pi pi-exclamation-triangle mr-3 text-4xl" />
					{
						<span>
							Warning: You are about to delete this data object from the database. This cannot be undone. Please confirm
							deletion or cancel.
						</span>
					}
				</div>
			</Dialog>

			<Dialog
				visible={deprecateDialog}
				className="w-30rem"
				header="Confirm Deletion"
				modal
				footer={deprecateDialogFooter}
				onHide={hideDeprecateDialog}
			>
				<div className="confirmation-content">
					<p>
						<i className="pi pi-exclamation-triangle mr-3 text-4xl" />
						Warning: You are about to delete this data object from the database. This cannot be undone. Please confirm
						the following information or deprecate instead:
					</p>
					<br />
				</div>
				<div>
					<Checkbox onChange={(e) => setAllowDelete(!allowDelete)} checked={allowDelete}></Checkbox>
					<label>
						{' '}
						This data object has not been made public OR this data object has been made public but fits criteria for
						deletion from the database
					</label>
				</div>
			</Dialog>

			<Dialog
				visible={errorDialog}
				className="w-30rem"
				header="Deletion Error"
				modal
				footer={errorDialogFooter}
				onHide={hideErrorDialog}
			>
				<div className="error-message-dialog">
					<i className="pi pi-ban mr-3 text-4xl" />
					{
						<span>
							ERROR: The data object you are trying to delete is in use by other data objects. Remove data connections
							to all other data objects and try to delete again.
						</span>
					}
				</div>
				<hr />
				<div className="error-message-detail">{<span className="text-sm">{deletionErrorMessage}</span>}</div>
			</Dialog>

			<Dialog
				visible={exceptionDialog}
				className="w-34rem"
				header="Exception"
				modal
				footer={exceptionDialogFooter}
				onHide={hideExceptionDialog}
			>
				<div className="error-message-dialog">
					<i className="pi pi-ban mr-3 text-4xl" />
					{<span>Exception Occurred!!</span>}
				</div>
				<hr />
				<div className="error-message-detail">{<span className="text-sm">{exceptionMessage}</span>}</div>
			</Dialog>
		</div>
	);
};
