import { Button } from "primereact/button";

export const DeleteAction = ({ disabled, deletionHandler, index}) => {
  return (
    <Button icon="pi pi-trash" className="p-button-text" disabled={disabled}
      onClick={(e) => deletionHandler(e, index)} />
  );
};