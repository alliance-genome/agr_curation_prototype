import axios from 'axios';

export class DataLoadService {

    createGroup(newGroup) {
        return axios.post(`api/bulkloadgroup`, newGroup);
    }

    createLoad(newLoad) {
        let endpoint = newLoad.type.toLowerCase();
        newLoad.group = { id: newLoad.group.id };
        return axios.post(`api/${endpoint}`, newLoad);
    }


    getBackendBulkLoadTypes() {
        return [
            "ONTOLOGY_DTO", "GENE_DTO", "ALLELE_DTO", "AGM_DTO", "DISEASE_ANNOTATION_DTO",
            "ONTOLOGY", "GENE", "ALLELE", "AGM", "DISEASE_ANNOTATION"
        ];
    }


    getLoadTypes() {
        return [
            "BulkFMSLoad", "BulkURLLoad", "BulkManualLoad"
        ];
    }

    restartLoad(loadType, id) {
        let endpoint = loadType.toLowerCase();
        return axios.get(`api/${endpoint}/restart/${id}`);
    }
}
