import React, {useRef, useState} from 'react';
import {DataTable} from 'primereact/datatable';
import {Column} from 'primereact/column';
import {Button} from 'primereact/button';
import {DiseaseAnnotationService} from '../service/DiseaseAnnotationService'
import {useMutation, useQuery} from 'react-query';
import {Message} from "primereact/message";
import {AutoComplete} from "primereact/autocomplete";
import {BiologicalEntityService} from "../service/BiologicalEntityService";
import { Toast } from 'primereact/toast';
import {OntologyService} from "../service/OntologyService";


export const DiseaseAnnotationsComponent = () => {

    let [diseaseAnnotations, setDiseaseAnnotations] = useState(null);

    const [page, setPage] = useState(0);
    const [multiSortMeta, setMultiSortMeta] = useState([]);
    const [filters, setFilters] = useState({});
    const [rows, setRows] = useState(50);
    const [totalRecords, setTotalRecords] = useState(0);
    const [first, setFirst] = useState(0);
    const [originalRows, setOriginalRows] = useState([]);
    const [filteredSubjects, setFilteredSubjects] = useState([]);
    const [filteredDiseases, setFilteredDiseases] = useState([]);
    const [editingRows, setEditingRows] = useState({});

    const diseaseAnnotationService = new DiseaseAnnotationService();
    const biologicalEntityService = new BiologicalEntityService();
    const ontologyService = new OntologyService();

    const toast_topleft = useRef(null);
    const toast_topright = useRef(null);

    useQuery(['diseaseAnnotations', rows, page, multiSortMeta, filters],
        () => diseaseAnnotationService.getDiseaseAnnotations(rows, page, multiSortMeta, filters), {
            onSuccess: (data) => {

                setDiseaseAnnotations(data.results);
                setTotalRecords(data.totalResults);
            },
            onError: (error) => {
                toast_topleft.current.show([
                    {life: 7000, severity: 'error', summary: 'Page error: ', detail: error.message, sticky: false }
                ])
            },
            onSettled:() => {
                setOriginalRows([]);
            },
            keepPreviousData: true,
            refetchOnWindowFocus: false

        }
    );

    const mutation = useMutation(updatedAnnotation => {
        return diseaseAnnotationService.saveDiseaseAnnotation(updatedAnnotation);
    });

    const onLazyLoad = (event) => {
        setRows(event.rows);
        setPage(event.page);
        setFirst(event.first);
    };


    const onFilter = (event) => {
        setFilters(event.filters);
    };

    const onSort = (event) => {
        let found = false;
        const newSort = [...multiSortMeta];

        newSort.forEach((o) => {
            if (o.field === event.multiSortMeta[0].field) {
                o.order = event.multiSortMeta[0].order;
                found = true;
            }
        });

        if (!found) {
            setMultiSortMeta(newSort.concat(event.multiSortMeta));
        } else {
            setMultiSortMeta(newSort);
        }
    };

    const publicationTemplate = (rowData) => {
        if (rowData && rowData.referenceList) {
            return <div>{rowData.referenceList.map(a => a.curie)}</div>
        }
    };

    const negatedTemplate = (rowData) => {
        if(rowData && rowData.negated !== null && rowData.negated !== undefined){
            return <div>{JSON.stringify(rowData.negated)}</div>
        }
    };

    const searchSubject = (event) => {
        biologicalEntityService.getBiologicalEntities(15, 0, null, {"curie":{"value": event.query}})
            .then((data) => {
                setFilteredSubjects(data.results);
            });
    };

    const searchDisease = (event) => {
        ontologyService.getTerms('doterm', 15, 0, null, {"curie":{"value": event.query}})
            .then((data) => {
                setFilteredDiseases(data.results);
            });
    };

    const onSubjectEditorValueChange = (props, event) => {
        let updatedAnnotations = [...props.value];
        if(event.target.value || event.target.value === '') {
            updatedAnnotations[props.rowIndex].subject = {};//this needs to be fixed. Otherwise, we won't have access to the other subject fields
            if(typeof event.target.value === "object"){
                updatedAnnotations[props.rowIndex].subject.curie = event.target.value.curie;
            } else {
                updatedAnnotations[props.rowIndex].subject.curie = event.target.value;
            }
            setDiseaseAnnotations(updatedAnnotations);
        }
    };


    const subjectEditor = (props) => {
        return (<div><AutoComplete
            field="curie"
            value={props.rowData.subject.curie}
            suggestions={filteredSubjects}
            itemTemplate={subjectItemTemplate}
            completeMethod={searchSubject}
            onChange={(e) => onSubjectEditorValueChange(props, e)}
            forceSelection
        /><Message severity={props.rowData.subject.errorSeverity ? props.rowData.subject.errorSeverity : ""} text={props.rowData.subject.errorMessage} /></div>)
    };

    const onDiseaseEditorValueChange = (props, event) => {
        let updatedAnnotations = [...props.value];
        if(event.target.value || event.target.value === '') {
            updatedAnnotations[props.rowIndex].object = {};//this needs to be fixed. Otherwise, we won't have access to the other subject fields
            if(typeof event.target.value === "object"){
                updatedAnnotations[props.rowIndex].object.curie = event.target.value.curie;
            } else {
                updatedAnnotations[props.rowIndex].object.curie = event.target.value;
            }
            setDiseaseAnnotations(updatedAnnotations);
        }
    };

    const diseaseEditor = (props) => {
        return (<div><AutoComplete
            field="curie"
            value={props.rowData.object.curie}
            suggestions={filteredDiseases}
            itemTemplate={diseaseItemTemplate}
            completeMethod={searchDisease}
            onChange={(e) => onDiseaseEditorValueChange(props, e)}
            forceSelection
        /><Message severity={props.rowData.object.errorSeverity ? props.rowData.object.errorSeverity : ""} text={props.rowData.object.errorMessage} /></div>)
    };

    const onRowEditInit = (event) => {
        originalRows[event.index] = { ...diseaseAnnotations[event.index] };
        setOriginalRows(originalRows);
        console.log("in onRowEditInit")
    };

    const onRowEditCancel = (event) => {
        let annotations = [...diseaseAnnotations];
        annotations[event.index] = originalRows[event.index];
        delete originalRows[event.index];
        setOriginalRows(originalRows);
        setDiseaseAnnotations(annotations);
    };

    const onRowEditSave = (event) =>{
        let updatedRow = JSON.parse(JSON.stringify(event.data));//deep copy
        if(Object.keys(event.data.subject).length > 1){
            updatedRow.subject = {};
            updatedRow.subject.curie = event.data.subject.curie;
        }
        if(Object.keys(event.data.object).length > 1){
            updatedRow.object = {};
            updatedRow.object.curie = event.data.object.curie;
        }

        mutation.mutate(updatedRow, {
            onSuccess: (data, variables, context) => {
                console.log(data);
                toast_topright.current.show({ severity: 'success', summary: 'Successful', detail: 'Row Updated' });

                let annotations = [...diseaseAnnotations];
                annotations[event.index].subject = data.data.entity.subject;
                annotations[event.index].object = data.data.entity.object;
                setDiseaseAnnotations(annotations);
            },
            onError: (error, variables, context) => {
                toast_topright.current.show([
                    {life: 7000, severity: 'error', summary: 'Update error: ', detail: error.response.data.errorMessage, sticky: false}
                ]);

                let annotations = [...diseaseAnnotations];
                if(error.response.data.errorMessages.subject) {
                    annotations[event.index].subject.errorSeverity = "error";
                    annotations[event.index].subject.errorMessage = error.response.data.errorMessages.subject;
                }
                if(error.response.data.errorMessages.object){
                    annotations[event.index].object.errorSeverity = "error";
                    annotations[event.index].object.errorMessage = error.response.data.errorMessages.object;
                }
                setDiseaseAnnotations(annotations);
                let _editingRows = { ...editingRows, ...{ [`${annotations[event.index].id}`]: true } };
                setEditingRows(_editingRows);
            },
            onSettled: (data, error, variables, context) => {

            },
        })
    };

    const onRowEditChange = (event) => {
        setEditingRows(event.data);
    }

    const subjectItemTemplate = (item) => {
        if(item.symbol){
            return <div dangerouslySetInnerHTML={{__html: item.curie + ' (' + item.symbol + ')'}}/>;
        } else if(item.name){
            return <div dangerouslySetInnerHTML={{__html: item.curie + ' (' + item.name + ')'}}/>;
        }else {
            return <div>{item.curie}</div>;
        }
    };

    const subjectBodyTemplate = (rowData) => {
        if(rowData.subject.symbol){
            return <div dangerouslySetInnerHTML={{__html: rowData.subject.curie + ' (' + rowData.subject.symbol + ')'}}/>;
        } else if(rowData.subject.name) {
            return <div dangerouslySetInnerHTML={{__html: rowData.subject.curie + ' (' + rowData.subject.name + ')'}}/>;
        }else {
            return <div>{rowData.subject.curie}</div>;
        }
    };

    const diseaseItemTemplate = (item) => {
        return <div>{item.curie} ({item.name})</div>;
    };

    const diseaseBodyTemplate = (rowData) => {
        return <div>{rowData.object.curie} ({rowData.object.name})</div>;
    };


    return (
        <div>
            <div className="card">
                <Toast ref={toast_topleft} position="top-left" />
                <Toast ref={toast_topright} position="top-right" />
                <h3>Disease Annotations Table</h3>
                <DataTable value={diseaseAnnotations} className="p-datatable-md"
                           editMode="row" onRowEditInit={onRowEditInit} onRowEditCancel={onRowEditCancel} onRowEditSave={(props) => onRowEditSave(props)}
                           editingRows={editingRows} onRowEditChange={onRowEditChange}
                           sortMode="multiple" removableSort onSort={onSort} multiSortMeta={multiSortMeta}
                           first={first} onFilter={onFilter} filters={filters}
                           dataKey="id"
                           paginator totalRecords={totalRecords} onPage={onLazyLoad} lazy
                           paginatorTemplate="CurrentPageReport FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown"
                           currentPageReportTemplate="Showing {first} to {last} of {totalRecords}" rows={rows} rowsPerPageOptions={[1, 10, 20, 50, 100, 250, 1000]}
                >
                    <Column field="curie" header="Curie" style={{whiteSpace: 'pr.e-wrap', overflowWrap: 'break-word'}} sortable filter></Column>
                    <Column field="subject.curie" header="Subject" sortable filter editor={(props) => subjectEditor(props)} body={subjectBodyTemplate}  style={{whiteSpace: 'pr.e-wrap', overflowWrap: 'break-word'}}></Column>
                    <Column field="diseaseRelation" header="Disease Relation" sortable filter></Column>
                    <Column field="negated" header="Negated" body={negatedTemplate} sortable ></Column>
                    <Column field="object.curie" header="Disease" sortable filter editor={(props) => diseaseEditor(props)} body={diseaseBodyTemplate}></Column>
                    <Column field="referenceList.curie" header="Reference" body={publicationTemplate} sortable filter></Column>
                    <Column field="created" header="Creation Date" sortable ></Column>
                    <Column rowEditor headerStyle={{ width: '7rem' }} bodyStyle={{ textAlign: 'center' }}></Column>
                </DataTable>
            </div>
        </div>
    )
};
