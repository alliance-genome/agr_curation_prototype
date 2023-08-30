import { Button } from "primereact/button";
import { FormTableWrapper } from "../../components/FormTableWrapper";
import { SynonymsFormTable } from "./synonyms/SynonymsFormTable";
import { useRef } from "react";

export const SynonymsForm = ({ labelColumnSize, state, dispatch, editingRows }) => {
  const tableRef = useRef(null);
  const createNewSynonymHandler = (e) => {
    e.preventDefault();
    dispatch({type: "ADD_ROW", showType: "showSynonyms", row: {}})
    return null;
  };

  const onRowEditChange = (e) => {
    return null;
  };

  const nameTypeOnChangeHandler = (event, editorCallback, rowIndex, field) => {
    //updates value in table input box
    editorCallback(event.target.value);
    dispatch({ 
      type: 'EDIT_ROW', 
      tableType: 'alleleSynonyms', 
      index: rowIndex, 
      field: field, 
      value: event.target.value
    });
  };

  const internalOnChangeHandler = (event, editorCallback, rowIndex, field) => {
    //updates value in table input box
    editorCallback(event.target.value);
    dispatch({ 
      type: 'EDIT_ROW', 
      tableType: 'alleleSynonyms', 
      index: rowIndex, 
      field: field, 
      value: event.target.value
    });
  };

  const synonymScopeOnChangeHandler = (event, editorCallback, rowIndex, field) => {
    //updates value in table input box
    editorCallback(event.target.value);
    dispatch({ 
      type: 'EDIT_ROW', 
      tableType: 'alleleSynonyms', 
      index: rowIndex, 
      field: field, 
      value: event.target.value
    });
  };

  const textOnChangeHandler = (rowIndex, event, editorCallback, field) => {
    //updates value in table input box
    editorCallback(event.target.value);
    dispatch({ 
      type: 'EDIT_ROW', 
      tableType: 'alleleSynonyms', 
      index: rowIndex, 
      field: field, 
      value: event.target.value
    });
  }

  const evidenceOnChangeHandler = (event, setFieldValue, props) => {
    //updates value in table input box
    setFieldValue(event.target.value);
    dispatch({ 
      type: 'EDIT_ROW', 
      tableType: 'alleleSynonyms', 
      index: props.rowIndex, 
      field: "evidence", 
      value: event.target.value
    });
  }

  const nameTypeEditor = (e) => {
    return null;
  };

  const internalTemplate = (e) => {
    return null;
  };

  const onRowEditCancel = (e) => {
    return null;
  };

  const onRowEditSave = (e) => {
    return null;
  };

  const deleteAction = (e) => {
    return null;
  };

  return (
    <FormTableWrapper
      labelColumnSize={labelColumnSize}
      table={
        <SynonymsFormTable
          synonyms={state.allele?.alleleSynonyms}
          editingRows={editingRows}
          onRowEditChange={onRowEditChange}
          tableRef={tableRef}
          onRowEditCancel={onRowEditCancel}
          onRowEditSave={onRowEditSave}
          deleteAction={deleteAction}
          errorMessages={state.errorMessages}
          nameTypeEditor={nameTypeEditor}
          textOnChangeHandler={textOnChangeHandler}
          synonymScopeOnChangeHandler={synonymScopeOnChangeHandler}
          nameTypeOnChangeHandler={nameTypeOnChangeHandler}
          internalOnChangeHandler={internalOnChangeHandler}
          evidenceOnChangeHandler={evidenceOnChangeHandler}
        />
      }
      tableName="Synonyms"
      showTable={state.showSynonyms}
      button={<Button label="Add Synonym" onClick={createNewSynonymHandler} style={{ width: "50%" }} />}
    />
  );

};