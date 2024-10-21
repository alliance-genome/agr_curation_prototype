import { BaseAuthService } from './BaseAuthService';
import { saveAs } from 'file-saver';

export class DataLoadService extends BaseAuthService {
	createGroup(newGroup) {
		return this.api.post(`/bulkloadgroup`, newGroup);
	}

	deleteGroup(id) {
		return this.api.delete(`/bulkloadgroup/${id}`);
	}

	createLoad(newLoad) {
		let endpoint = newLoad.type.toLowerCase();

		newLoad.group = { id: newLoad.group };
		for (const objectKey in newLoad) {
			if (!newLoad[objectKey]) {
				delete newLoad[objectKey];
			}
		}
		return this.api.post(`/${endpoint}`, newLoad);
	}

	updateLoad(newLoad) {
		let endpoint = newLoad.type.toLowerCase();

		newLoad.group = { id: newLoad.group };
		for (const objectKey in newLoad) {
			if (!newLoad[objectKey]) {
				delete newLoad[objectKey];
			}
		}
		delete newLoad['loadFiles'];
		console.log('Saving: ');
		console.log(newLoad);
		return this.api.put(`/${endpoint}`, newLoad);
	}

	deleteLoad(loadType, id) {
		let endpoint = loadType.toLowerCase();
		return this.api.delete(`/${endpoint}/${id}`);
	}

	restartLoad(id) {
		return this.api.get(`/bulkloadfilehistory/restartload/${id}`);
	}

	restartHistoryLoad(id) {
		return this.api.get(`/bulkloadfilehistory/restartloadhistory/${id}`);
	}

	getFileHistoryFile(id) {
		return this.api.get(`/bulkloadfilehistory/${id}`);
	}

	getHistoryExceptions(historyId, rows, page) {
		var searchObject = {
			'bulkLoadFileHistory.id': historyId,
		};
		return this.api.post(`/bulkloadfileexception/find?limit=${rows}&page=${page}`, searchObject);
	}

	downloadExceptions(id, setIsLoading) {
		setIsLoading(true);
		this.api.get(`/bulkloadfilehistory/${id}/download`, { responseType: 'blob' }).then((response) => {
			const match = response.headers['content-disposition'].match(/filename="([^"]+)"/);
			saveAs(response.data, match[1]);
			setIsLoading(false);
		});
	}

	deleteLoadFile(id) {
		return this.api.delete(`/bulkloadfile/${id}`);
	}

	deleteLoadFileHistory(id) {
		return this.api.delete(`/bulkloadfilehistory/${id}`);
	}

	getBackendBulkLoadTypes(loadType) {
		const bulkLoadTypes = {
			BulkFMSLoad: [
				'GFF', // This needs to be removed at some point

				'GFF_EXON',
				'GFF_CDS',
				'GFF_TRANSCRIPT',
				'HTPDATASET',
				'HTPDATASAMPLE',
				'INTERACTION-GEN',
				'INTERACTION-MOL',
				'MOLECULE',
				'ORTHOLOGY',
				'PHENOTYPE',
				'PARALOGY',
				'SEQUENCE_TARGETING_REAGENT',
			],
			BulkURLLoad: [
				'ONTOLOGY',
				'GENE',
				'ALLELE',
				'AGM',
				'DISEASE_ANNOTATION',
				'RESOURCE_DESCRIPTOR',
				'EXPRESSION_ATLAS',
			],
			BulkManualLoad: [
				'FULL_INGEST',
				'DISEASE_ANNOTATION',
				'GENE_DISEASE_ANNOTATION',
				'ALLELE_DISEASE_ANNOTATION',
				'AGM_DISEASE_ANNOTATION',
				'GENE',
				'ALLELE',
				'AGM',
				'VARIANT',
				'CONSTRUCT',
				'ALLELE_ASSOCIATION',
				'CONSTRUCT_ASSOCIATION',
			],
		};
		return bulkLoadTypes[loadType];
	}

	getLoadTypes() {
		return ['BulkFMSLoad', 'BulkURLLoad', 'BulkManualLoad'];
	}

	getOntologyTypes() {
		return [
			'APO',
			'ATP',
			'BSPO',
			'CHEBI',
			'CL',
			'CMO',
			'DAO',
			'DO',
			'ECO',
			'EMAPA',
			'FBCV',
			'FBDV',
			'GENO',
			'GO',
			'HP',
			'MA',
			'MI',
			'MMO',
			'MPATH',
			'MOD',
			'MMUSDV',
			'MP',
			'OBI',
			'PATO',
			'PW',
			'RO',
			'RS',
			'SO',
			'UBERON',
			'VT',
			'WBBT',
			'WBLS',
			'WBPheno',
			'XBA_XBS',
			'XBED',
			'XCO',
			'XPO',
			'XSMO',
			'ZECO',
			'ZFA',
			'ZFS',
		];
	}

	getDataProviders() {
		return ['FB', 'MGI', 'HUMAN', 'RGD', 'SGD', 'WB', 'XB', 'ZFIN', 'SARSCoV2'];
	}
}
