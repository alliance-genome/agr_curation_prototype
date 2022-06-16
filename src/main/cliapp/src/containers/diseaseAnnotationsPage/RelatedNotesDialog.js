import React, { useRef, useState } from 'react';
import { Dialog } from 'primereact/dialog';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Button } from 'primereact/button';
import { Toast } from 'primereact/toast';
import { ConfirmDialog } from 'primereact/confirmdialog';
import { ColumnGroup } from 'primereact/columngroup';
import { Row } from 'primereact/row';
import { InputTextAreaEditor } from '../../components/InputTextAreaEditor';
import { ErrorMessageComponent } from '../../components/ErrorMessageComponent';
import { EllipsisTableCell } from '../../components/EllipsisTableCell';
import { TrueFalseDropdown } from '../../components/TrueFalseDropDownSelector';
import { useControlledVocabularyService } from '../../service/useControlledVocabularyService';
import { ValidationService } from '../../service/ValidationService';
import { ControlledVocabularyDropdown } from '../../components/ControlledVocabularySelector';

export const RelatedNotesDialog = ({
													originalRelatedNotesData,
													setOriginalRelatedNotesData,
													errorMessagesMainRow,
													setErrorMessagesMainRow
												}) => {
	const { originalRelatedNotes, isInEdit, dialog, rowIndex, mainRowProps } = originalRelatedNotesData;
	const [localRelateNotes, setLocalRelateNotes] = useState(null) ;
	const [editingRows, setEditingRows] = useState({});
	const [errorMessages, setErrorMessages] = useState([]);
	const [deleteRelatedNoteDialog, setDeleteRelatedNoteDialog] = useState(false);
	const booleanTerms = useControlledVocabularyService('generic_boolean_terms');
	const noteTypeTerms = useControlledVocabularyService('Disease annotation note types');
	const validationService = new ValidationService();
	const tableRef = useRef(null);
	const rowsInEdit = useRef(0);
	const hasEdited = useRef(false);
	const toast_topright = useRef(null);

	const showDialogHandler = () => {
		let _localRelatedNotes = cloneNotes(originalRelatedNotes);
		setLocalRelateNotes(_localRelatedNotes);

		if(isInEdit){
			let rowsObject = {};
			if(_localRelatedNotes) {
				_localRelatedNotes.forEach((note) => {
					rowsObject[`${note.dataKey}`] = true;
				});
			}
			setEditingRows(rowsObject);
			rowsInEdit.current++;
		}else{
			setEditingRows({});
		}
		hasEdited.current = false;
	};

	const onRowEditChange = (e) => {
		setEditingRows(e.data);
	}

	const onRowEditCancel = (event) => {
		console.log(editingRows);
		rowsInEdit.current--;
		let _editingRows = { ...editingRows };
		delete _editingRows[event.index];
		setEditingRows(_editingRows);
		let _localRelateNotes = [...localRelateNotes];//add new note support
		if(originalRelatedNotes && originalRelatedNotes[event.index]){
			let dataKey = _localRelateNotes[event.index].dataKey;
			_localRelateNotes[event.index] = global.structuredClone(originalRelatedNotes[event.index]);
			_localRelateNotes[event.index].dataKey = dataKey;
			setLocalRelateNotes(_localRelateNotes);
		}else{

		}
		const errorMessagesCopy = errorMessages;
		errorMessagesCopy[event.index] = {};
		setErrorMessages(errorMessagesCopy);
		if(hasEdited && hasEdited.current === false)
			compareChangesInNotes(event.data,event.index);
	};

	const compareChangesInNotes = (data,index) => {
		if(originalRelatedNotes && originalRelatedNotes[index]) {
			if (data.noteType.name !== originalRelatedNotes[index].noteType.name) {
				hasEdited.current = true;
			}
			if (data.internal !== originalRelatedNotes[index].internal) {
				hasEdited.current = true;
			}
			if (data.freeText !== originalRelatedNotes[index].freeText) {
				hasEdited.current = true;
			}
		}
	};

	const onRowEditSave = (event) => {
		rowsInEdit.current--;
		let _localRelateNotes = [...localRelateNotes];
		_localRelateNotes[event.index] = event.data;
		setLocalRelateNotes(_localRelateNotes);
		compareChangesInNotes(event.data,event.index);
	};

	const hideDialog = () => {
		setErrorMessages([]);
		setOriginalRelatedNotesData((originalRelatedNotesData) => {
			return {
				...originalRelatedNotesData,
				dialog: false,
			};
		});
		let _localRelatedNotes = [];
		setLocalRelateNotes(_localRelatedNotes);
	};

	const validateNotes = async (notes) => {
		const validationResultsArray = [];
		let _notes = global.structuredClone(notes);
		for (const note of _notes) {
			delete note.dataKey;
			const result = await validationService.validate('note', note);
			validationResultsArray.push(result);
		}
		return validationResultsArray;
	};

	const cloneNotes = (clonableNotes) => {
		let _clonableNotes = global.structuredClone(clonableNotes);
		if(_clonableNotes) {
			let counter = 0 ;
			_clonableNotes.forEach((note) => {
				note.dataKey = counter++;
			});
		} else {
			_clonableNotes = [];
		};
		return _clonableNotes;
	};

	const createNewNoteHandler = (event) => {
		let cnt = localRelateNotes ? localRelateNotes.length : 0;
		localRelateNotes.push({
			dataKey : cnt,
			noteType: {
				name : ""
			}
		});
		let _editingRows = { ...editingRows, ...{ [`${cnt}`]: true } };
		setEditingRows(_editingRows);
		rowsInEdit.current++;
		hasEdited.current = true;
	};

	const saveDataHandler = async () => {
		const resultsArray = await validateNotes(localRelateNotes);
		const errorMessagesCopy = [...errorMessages];
		let keepDialogOpen = false;
		let anyErrors = false;

		resultsArray.forEach((result, index) => {
			const { isError, data } = result;
			if (isError) {
				errorMessagesCopy[index] = {};
				Object.keys(data).forEach((field) => {
					let messageObject = {
						severity: "error",
						message: data[field]
					};
					errorMessagesCopy[index][field] = messageObject;
					setErrorMessages(errorMessagesCopy);
					keepDialogOpen = true;
					anyErrors = true;
				});
			}
		});

		if (!anyErrors) {
			setErrorMessages([]);
			for (const note of localRelateNotes) {
				delete note.dataKey;
			}
			mainRowProps.rowData.relatedNotes = localRelateNotes;
			let updatedAnnotations = [...mainRowProps.props.value];
			updatedAnnotations[rowIndex].relatedNotes = localRelateNotes;
			keepDialogOpen = false;

			if(hasEdited.current){
				const errorMessagesCopy = errorMessagesMainRow;
				let messageObject = {
					severity: "warn",
					message: "Pending Edits!"
				};
				errorMessagesCopy[mainRowProps.rowIndex] = {};
				errorMessagesCopy[mainRowProps.rowIndex]["relatedNotes.freeText"] = messageObject;
				setErrorMessagesMainRow({...errorMessagesCopy});
			}
		};

		setOriginalRelatedNotesData((originalRelatedNotesData) => {
				return {
					...originalRelatedNotesData,
					dialog: keepDialogOpen,
				}
			}
		);
	};

	const noteTypeTemplate = (rowData) => {
		return <EllipsisTableCell>{rowData.noteType.name}</EllipsisTableCell>;
	};

	const internalTemplate = (rowData) => {
		return <EllipsisTableCell>{JSON.stringify(rowData.internal)}</EllipsisTableCell>;
	};

	const textTemplate = (rowData) => {
		return <EllipsisTableCell>{rowData.freeText}</EllipsisTableCell>;
	};

	const onInternalEditorValueChange = (props, event) => {
		let _localRelateNotes = [...localRelateNotes];
		_localRelateNotes[props.rowIndex].internal = event.value.name;
	}

	const internalEditor = (props) => {
		return (
			<>
				<TrueFalseDropdown
					options={booleanTerms}
					editorChange={onInternalEditorValueChange}
					props={props}
					field={"internal"}
				/>
				<ErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={"internal"} />
			</>
		);
	};

	const onNoteTypeEditorValueChange = (props, event) => {
		let _localRelateNotes = [...localRelateNotes];
		_localRelateNotes[props.rowIndex].noteType = event.value;
	};

	const noteTypeEditor = (props) => {
		return (
			<>
				<ControlledVocabularyDropdown
					field="noteType"
					options={noteTypeTerms}
					editorChange={onNoteTypeEditorValueChange}
					props={props}
					showClear={false}
					dataKey='id'
				/>
				<ErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={"noteType"} />
			</>
		);
	};

	const onFreeTextEditorValueChange = (event, props) => {
		let _localRelateNotes = [...localRelateNotes];
		_localRelateNotes[props.rowIndex].freeText = event.target.value;
	};

	const freeTextEditor = (props, fieldName, errorMessages) => {
		if (errorMessages) {
			errorMessages.severity = "error";
		}
		return (
			<>
				<InputTextAreaEditor
					initalValue={props.value}
					editorChange={(e) => onFreeTextEditorValueChange(e, props)}
					rows={5}
					columns={30}
				/>
				<ErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={fieldName} />
			</>
		);
	};

	const footerTemplate = () => {
		if (!isInEdit) {
			return null;
		};
		return (
			<div>
				<Button label="Cancel" icon="pi pi-times" onClick={hideDialog} className="p-button-text" />
				<Button label="New Note" icon="pi pi-plus" onClick={createNewNoteHandler}/>
				<Button label="Keep Edits" icon="pi pi-check" onClick={saveDataHandler} disabled={!hasEdited.current}/>
			</div>
		);
	}

	const confirmDeleteRelatedNote = () => {
		/*let _products = products.filter(val => val.id !== product.id);
		setProducts(_products);
		setDeleteProductDialog(false);
		setProduct(emptyProduct);*/
		setDeleteRelatedNoteDialog(false);
		toast_topright.current.show([
			{ life: 3000, severity: 'success', summary: 'Successful ', detail: 'Related Note Deleted' , sticky: false }
		]);
	}

	const actionBodyTemplate = (rowData) => {
		return (
			<Button icon="pi pi-trash" className="p-button-text" onClick={() => setDeleteRelatedNoteDialog(true)} />
		);
	}

	let headerGroup = 	<ColumnGroup>
						<Row>
							<Column header="Actions" colSpan={2} style={{display: isInEdit ? 'visible' : 'none'}}/>
							<Column header="Note Type" />
							<Column header="Internal" />
							<Column header="Text" />
						</Row>
						</ColumnGroup>;

	return (
		<div>
			<Toast ref={toast_topright} position="top-right" />
			<Dialog visible={dialog} className='w-6' modal onHide={hideDialog} closable={!isInEdit} onShow={showDialogHandler} footer={footerTemplate} resizable>
				<h3>Related Notes</h3>
				<DataTable value={localRelateNotes} dataKey="dataKey" showGridlines editMode='row' headerColumnGroup={headerGroup}
								editingRows={editingRows} onRowEditChange={onRowEditChange} ref={tableRef} onRowEditCancel={onRowEditCancel} onRowEditSave={(props) => onRowEditSave(props)}>
					<Column rowEditor={isInEdit} style={{maxWidth: '7rem', display: isInEdit ? 'visible' : 'none'}} headerStyle={{width: '7rem', position: 'sticky'}}
								bodyStyle={{textAlign: 'center'}} frozen headerClassName='surface-0'/>
					<Column editor={actionBodyTemplate} style={{ maxWidth: '4rem' , display: isInEdit ? 'visible' : 'none'}} frozen headerClassName='surface-0' bodyStyle={{textAlign: 'center'}}/>
					<Column editor={noteTypeEditor} field="noteType.name" header="Note Type" headerClassName='surface-0' body={noteTypeTemplate}/>
					<Column editor={internalEditor} field="internal" header="Internal" body={internalTemplate} headerClassName='surface-0'/>
					<Column
						editor={(props) => freeTextEditor(props, "freeText", errorMessages)}
						field="freeText"
						header="Text"
						body={textTemplate}
						headerClassName='surface-0'
					/>
				</DataTable>
			</Dialog>

			<ConfirmDialog visible={deleteRelatedNoteDialog} onHide={() => setDeleteRelatedNoteDialog(false)} message="Are you sure you want to delete?"
				header="Confirmation" icon="pi pi-exclamation-triangle" accept={confirmDeleteRelatedNote} /*reject={cancelDeleteRelatedNote}*/ />
			<Button onClick={() => setDeleteRelatedNoteDialog(true)} icon="pi pi-check" label="Confirm" />
		</div>
	);
};
