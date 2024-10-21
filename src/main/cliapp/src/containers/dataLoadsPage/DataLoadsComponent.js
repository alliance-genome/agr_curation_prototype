import React, { useReducer, useRef, useState, useContext } from 'react';
import { useQuery } from '@tanstack/react-query';
import { DataTable } from 'primereact/datatable';
import Moment from 'react-moment';
import { Column } from 'primereact/column';
import { FileUpload } from 'primereact/fileupload';
import { Toast } from 'primereact/toast';
import { Dialog } from 'primereact/dialog';
import { ProgressBar } from 'primereact/progressbar';

import { useOktaAuth } from '@okta/okta-react';
import { SearchService } from '../../service/SearchService';
import { DataLoadService } from '../../service/DataLoadService';
import { DataSubmissionService } from '../../service/DataSubmissionService';
import { Messages } from 'primereact/messages';
import { Button } from 'primereact/button';
import { NewBulkLoadForm } from './NewBulkLoadForm';
import { NewBulkLoadGroupForm } from './NewBulkLoadGroupForm';
import { HistoryDialog } from './HistoryDialog';
import { useQueryClient } from '@tanstack/react-query';
import { SiteContext } from '../layout/SiteContext';
import { LoadingOverlay } from '../../components/LoadingOverlay';
import moment from 'moment-timezone';
import { NumberTemplate } from '../../components/Templates/NumberTemplate';

export const DataLoadsComponent = () => {
	const { authState } = useOktaAuth();

	const bulkLoadReducer = (state, action) => {
		switch (action.type) {
			case 'EDIT':
				return { ...action.editBulkLoad };
			case 'RESET':
				return { name: '' };
			default:
				return { ...state, [action.field]: action.value };
		}
	};

	const { apiVersion } = useContext(SiteContext);
	const [isLoading, setIsLoading] = useState(false);
	const [groups, setGroups] = useState({});
	const [errorLoads, setErrorLoads] = useState([]);
	const [runningLoads, setRunningLoads] = useState({});
	const [history, setHistory] = useState({ id: 0 });
	const [bulkLoadGroupDialog, setBulkLoadGroupDialog] = useState(false);
	const [historyDialog, setHistoryDialog] = useState(false);
	const [bulkLoadDialog, setBulkLoadDialog] = useState(false);
	const [expandedGroupRows, setExpandedGroupRows] = useState(null);
	const [expandedLoadRows, setExpandedLoadRows] = useState(null);
	//const [expandedFileRows, setExpandedFileRows] = useState(null);
	const [expandedErrorLoadRows, setExpandedErrorLoadRows] = useState(null);
	const [disableFormFields, setDisableFormFields] = useState(false);
	const errorMessage = useRef(null);
	const searchService = new SearchService();
	const dataSubmissionService = new DataSubmissionService();
	const [uploadLoadType, setUploadLoadType] = useState(null);
	const [uploadSubType, setUploadSubType] = useState(null);
	const [uploadConfirmDialog, setUploadConfirmDialog] = useState(false);

	const [newBulkLoad, bulkLoadDispatch] = useReducer(bulkLoadReducer, {});

	const queryClient = useQueryClient();

	let dataLoadService = null;
	const toast = useRef(null);

	const handleNewBulkLoadGroupOpen = (event) => {
		setBulkLoadGroupDialog(true);
	};

	const handleNewBulkLoadOpen = (event) => {
		bulkLoadDispatch({ type: 'RESET' });
		setBulkLoadDialog(true);
	};

	const loadTypeClasses = new Map([
		[
			'FULL_INGEST',
			[
				'GeneDiseaseAnnotationDTO',
				'AlleleDiseaseAnnotationDTO',
				'AGMDiseaseAnnotationDTO',
				'GeneDTO',
				'AlleleDTO',
				'AffectedGenomicModelDTO',
				'ConstructDTO',
			],
		],
		['DISEASE_ANNOTATION', ['GeneDiseaseAnnotationDTO', 'AlleleDiseaseAnnotationDTO', 'AGMDiseaseAnnotationDTO']],
		['GENE_DISEASE_ANNOTATION', ['GeneDiseaseAnnotationDTO']],
		['ALLELE_DISEASE_ANNOTATION', ['AlleleDiseaseAnnotationDTO']],
		['AGM_DISEASE_ANNOTATION', ['AGMDiseaseAnnotationDTO']],
		['GENE', ['GeneDTO']],
		['ALLELE', ['AlleleDTO']],
		['AGM', ['AffectedGenomicModelDTO']],
		['VARIANT', ['VariantDTO']],
		['CONSTRUCT', ['ConstructDTO']],
		['ALLELE_ASSOCIATION', ['AlleleGeneAssociationDTO']],
		['CONSTRUCT_ASSOCIATION', ['ConstructGenomicEntityAssociationDTO']],
	]);

	useQuery(['bulkloadtable'], () => searchService.find('bulkloadgroup', 100, 0, {}), {
		onSuccess: (data) => {
			if (data.results) {
				let _errorLoads = [];
				for (let group of data.results) {
					if (group.loads) {
						for (let load of group.loads) {
							load.group = group.id;
							if (load.history) {
								let sortedFiles = sortFilesByDate(load.history);
								if (sortedFiles[0].bulkloadStatus === 'FAILED') {
									_errorLoads.push(load);
								}
							}
						}
					}
				}
				setGroups(data.results.sort((a, b) => (a.name > b.name ? 1 : -1)));
				setErrorLoads(_errorLoads.sort((a, b) => (a.name > b.name ? 1 : -1)));
			}

			var loc = window.location,
				new_uri;
			if (loc.protocol === 'https:') {
				new_uri = 'wss:';
			} else {
				new_uri = 'ws:';
			}
			if (process.env.NODE_ENV === 'production') {
				new_uri += '//' + loc.host;
			} else {
				new_uri += '//localhost:8080';
			}

			new_uri += loc.pathname + 'load_processing_events';
			//console.log(new_uri);
			let ws = new WebSocket(new_uri);

			ws.onopen = () => console.log('ws opened');
			ws.onclose = () => console.log('ws closed');

			ws.onmessage = (e, runningLoads) => {
				let processingMessage = JSON.parse(e.data);
				setRunningLoads((prevState) => {
					//console.log(prevState);
					const newState = { ...prevState };
					newState[processingMessage.message] = processingMessage;
					//console.log(newState);
					return newState;
				});
			};
		},
		keepPreviousData: true,
		refetchOnWindowFocus: false,
	});

	const getService = () => {
		if (!dataLoadService) {
			dataLoadService = new DataLoadService(authState);
		}
		return dataLoadService;
	};

	const urlTemplate = (rowData) => {
		return <a href={rowData.bulkLoadFile.s3Url}>Download</a>;
	};

	const refresh = () => {
		queryClient.invalidateQueries(['bulkloadtable']);
	};

	const runLoad = (rowData) => {
		getService()
			.restartLoad(rowData.id)
			.then((response) => {
				queryClient.invalidateQueries(['bulkloadtable']);
			});
	};

	const runHistoryLoad = (rowData) => {
		getService()
			.restartHistoryLoad(rowData.id)
			.then((response) => {
				queryClient.invalidateQueries(['bulkloadtable']);
			});
	};

	const editLoad = (rowData) => {
		bulkLoadDispatch({ type: 'EDIT', editBulkLoad: rowData });
		setBulkLoadDialog(true);
		setDisableFormFields(true);
	};

	const deleteLoadFileHistory = (rowData) => {
		getService()
			.deleteLoadFileHistory(rowData.id)
			.then((response) => {
				queryClient.invalidateQueries(['bulkloadtable']);
			});
	};

	const deleteLoad = (rowData) => {
		getService()
			.deleteLoad(rowData.type, rowData.id)
			.then((response) => {
				queryClient.invalidateQueries(['bulkloadtable']);
			});
	};

	const deleteGroup = (rowData) => {
		getService()
			.deleteGroup(rowData.id)
			.then((response) => {
				queryClient.invalidateQueries(['bulkloadtable']);
			});
	};

	const showHistory = (rowData) => {
		setHistory(rowData);
		setHistoryDialog(true);
	};

	const historyActionBodyTemplate = (rowData) => {
		return (
			<nobr>
				<Button
					icon="pi pi-search-plus"
					className="p-button-rounded p-button-info mr-2"
					onClick={() => showHistory(rowData)}
				/>

				{/*{ rowData.failedRecords > 0 &&
				Have to resort to this code if file is too large (SCRUM-2639)
					<a href={`/api/bulkloadfilehistory/${rowData.id}/download`} className="p-button p-button-warning">
						<i className="pi pi-exclamation-triangle"></i>
						<i className="pi pi-download"></i>
					</a>
				}*/}

				{rowData.counts &&
					Object.values(rowData.counts).some((field) => field.failed !== undefined && field.failed > 0) && (
						<Button className="p-button-rounded p-button-warning" onClick={() => downloadFileExceptions(rowData.id)}>
							<i className="pi pi-exclamation-triangle"></i>
							<i className="pi pi-download ml-1"></i>
						</Button>
					)}
			</nobr>
		);
	};

	const downloadFileExceptions = (id) => {
		dataLoadService.downloadExceptions(id, setIsLoading);
	};

	const showUploadConfirmDialog = (rowData) => {
		setUploadLoadType(rowData.backendBulkLoadType);
		setUploadSubType(rowData.dataProvider);
		setUploadConfirmDialog(true);
		//setUploadFile(event.files[0]);
	};

	const hideUploadConfirmDialog = () => {
		setUploadLoadType(null);
		setUploadSubType(null);
		setUploadConfirmDialog(false);
	};

	const uploadLoadFile = (event) => {
		let type = uploadLoadType + '_' + uploadSubType;
		let formData = new FormData();
		if (event.files.length > 0) {
			formData.append(type, event.files[0]);
		}
		dataSubmissionService.sendFile(formData);
		toast.current.show({ severity: 'info', summary: 'Success', detail: 'File Uploaded' });
		setUploadLoadType(null);
		setUploadSubType(null);
		setUploadConfirmDialog(false);
	};

	const loadFileActionBodyTemplate = (rowData, bulkload) => {
		let ret = [];
		// console.log(rowData);
		if (
			!rowData.bulkloadStatus ||
			rowData.bulkloadStatus === 'FINISHED' ||
			rowData.bulkloadStatus === 'FAILED' ||
			rowData.bulkloadStatus === 'STOPPED'
		) {
			if (
				fileWithinSchemaRange(rowData.bulkLoadFile.linkMLSchemaVersion, bulkload.backendBulkLoadType) ||
				exemptTypes(bulkload.backendBulkLoadType)
			) {
				ret.push(
					<Button
						key="run"
						icon="pi pi-play"
						className="p-button-rounded p-button-success mr-2"
						onClick={() => runHistoryLoad(rowData)}
					/>
				);
			}
		}
		if (
			!rowData.bulkloadStatus ||
			rowData.bulkloadStatus === 'FINISHED' ||
			rowData.bulkloadStatus === 'FAILED' ||
			rowData.bulkloadStatus === 'STOPPED'
		) {
			ret.push(
				<Button
					key="delete"
					icon="pi pi-trash"
					className="p-button-rounded p-button-danger mr-2"
					onClick={() => deleteLoadFileHistory(rowData)}
				/>
			);
		}

		return ret;
	};

	const loadActionBodyTemplate = (rowData) => {
		let ret = [];

		ret.push(
			<Button
				key="edit"
				icon="pi pi-pencil"
				className="p-button-rounded p-button-warning mr-2"
				onClick={() => editLoad(rowData)}
			/>
		);

		if (rowData.type !== 'BulkManualLoad') {
			if (
				!rowData.bulkloadStatus ||
				rowData.bulkloadStatus === 'FINISHED' ||
				rowData.bulkloadStatus === 'FAILED' ||
				rowData.bulkloadStatus === 'STOPPED'
			) {
				ret.push(
					<Button
						key="run"
						icon="pi pi-play"
						className="p-button-rounded p-button-success mr-2"
						onClick={() => runLoad(rowData)}
					/>
				);
			}
		} else {
			ret.push(
				<Button
					key="fileUpload"
					icon="pi pi-upload"
					label="Upload"
					className="p-button-rounded p-button-info mr-2"
					onClick={() => showUploadConfirmDialog(rowData)}
				/>
			);
		}

		if (!rowData.history || rowData.history.length === 0) {
			ret.push(
				<Button
					key="delete"
					icon="pi pi-trash"
					className="p-button-rounded p-button-danger mr-2"
					onClick={() => deleteLoad(rowData)}
				/>
			);
		}

		return <div style={{ display: 'inline-flex' }}> {ret} </div>;
	};

	const groupActionBodyTemplate = (rowData) => {
		if (!rowData.loads || rowData.loads.length === 0) {
			return (
				<Button
					icon="pi pi-trash"
					className="p-button-rounded p-button-danger mr-2"
					onClick={() => deleteGroup(rowData)}
				/>
			);
		}
	};

	const nameBodyTemplate = (rowData) => {
		return <React.Fragment>{rowData.name}</React.Fragment>;
	};

	const bulkloadUrlBodyTemplate = (rowData) => {
		if (rowData.bulkloadUrl) {
			return <a href={rowData.bulkloadUrl}>Source Download URL</a>;
		}
	};

	const backendBulkLoadTypeTemplate = (rowData) => {
		if (rowData.backendBulkLoadType === 'ONTOLOGY') {
			return rowData.backendBulkLoadType + '(' + rowData.ontologyType + ')';
		} else {
			return rowData.backendBulkLoadType;
		}
	};

	const scheduleActiveTemplate = (rowData) => {
		return <div>{rowData.scheduleActive ? 'true' : 'false'}</div>;
	};

	const showModRelease = (load) => {
		if (load.backendBulkLoadType === 'RESOURCE_DESCRIPTOR' || load.backendBulkLoadType === 'ONTOLOGY') {
			return null;
		}
		return <Column field="allianceMemberReleaseVersion" header="MOD Release" />;
	};

	const dynamicColumns = (loads) => {
		let showFMSLoad = false;
		let showURLLoad = false;
		let showManualLoad = false;

		let ret = [];

		if (loads) {
			for (const load of loads) {
				if (load.type === 'BulkFMSLoad') showFMSLoad = true;
				if (load.type === 'BulkURLLoad') showURLLoad = true;
				if (load.type === 'BulkManualLoad') showManualLoad = true;
			}
		}

		if (showFMSLoad || showURLLoad) {
			ret.push(
				<Column key="scheduleActive" field="scheduleActive" header="Schedule Active" body={scheduleActiveTemplate} />
			);
			ret.push(<Column key="cronSchedule" field="cronSchedule" header="Cron Schedule" />);
			ret.push(<Column key="nextRun" field="nextRun" header="Next Run" />);
			if (showFMSLoad) {
				ret.push(<Column key="fmsDataType" field="fmsDataType" header="FMS Data Type" />);
				ret.push(<Column key="fmsDataSubType" field="fmsDataSubType" header="FMS Data Sub Type" />);
			}
			if (showURLLoad) {
				ret.push(<Column key="bulkloadUrl" body={bulkloadUrlBodyTemplate} header="Data URL" />);
			}
		}
		if (showManualLoad) {
			ret.push(<Column key="fmsDataType2" field="dataProvider" header="Load Data Provider" />);
		}

		return ret;
	};

	const bulkloadFileStatusTemplate = (rowData) => {
		let styleClass = 'p-button-text p-button-plain';
		if (rowData.bulkloadStatus === 'FAILED') {
			styleClass = 'p-button-danger';
		}

		if (
			rowData.bulkloadStatus &&
			(rowData.bulkloadStatus.endsWith('STARTED') ||
				rowData.bulkloadStatus.endsWith('RUNNING') ||
				rowData.bulkloadStatus.endsWith('PENDING'))
		) {
			styleClass = 'p-button-success';
		}

		return (
			<Button
				label={rowData.bulkloadStatus}
				tooltip={rowData.errorMessage}
				className={`p-button-rounded ${styleClass}`}
			/>
		);
	};

	const bulkloadStatusTemplate = (rowData) => {
		let sortedFiles = [];
		if (rowData.history) {
			sortedFiles = sortFilesByDate(rowData.history);
		}
		let latestStatus = null;
		let latestError = null;
		if (rowData.history) {
			latestStatus = sortedFiles[0].bulkloadStatus;
			latestError = sortedFiles[0].errorMessage;
		}
		let styleClass = 'p-button-text p-button-plain';
		if (latestStatus === 'FAILED') {
			styleClass = 'p-button-danger';
		}
		if (
			latestStatus &&
			(latestStatus.endsWith('STARTED') || latestStatus.endsWith('RUNNING') || latestStatus.endsWith('PENDING'))
		) {
			styleClass = 'p-button-success';
		}

		return <Button label={latestStatus} tooltip={latestError} className={`p-button-rounded ${styleClass}`} />;
	};

	const durationTemplate = (rowData) => {
		moment.tz.setDefault('GMT');

		let started = moment(rowData.loadStarted);
		let finished = moment(Date.now());

		if (rowData.loadFinished) {
			finished = moment(rowData.loadFinished);
		}

		return (
			<>
				Start: <Moment format="YYYY-MM-DD HH:mm:ss" date={started} />
				<br />
				{rowData.loadFinished && (
					<>
						End: <Moment format="YYYY-MM-DD HH:mm:ss" date={finished} />
						<br />
						Duration: <Moment format="HH:mm:ss" duration={started} date={finished} />
					</>
				)}
				{!rowData.loadFinished && rowData.bulkloadStatus !== 'FAILED' && rowData.bulkloadStatus !== 'STOPPED' && (
					<>
						Running Time: <Moment tz="GMT" interval={1000} format="HH:mm:ss" duration={started} date={finished} />
					</>
				)}
			</>
		);
	};

	const countsTemplate = (rowData) => {
		let countsArray = [];
		for (let count in rowData.counts) {
			countsArray.push({ ...rowData.counts[count], name: count });
		}

		return (
			<DataTable key="countsTable" value={countsArray}>
				<Column field="name" />
				<Column
					field="completed"
					header="Completed"
					body={(rowData) => <NumberTemplate number={rowData.completed} />}
				/>
				<Column field="failed" header="Failed" body={(rowData) => <NumberTemplate number={rowData.failed} />} />
				<Column field="skipped" header="Skipped" body={(rowData) => <NumberTemplate number={rowData.skipped} />} />
				<Column field="total" header="Total" body={(rowData) => <NumberTemplate number={rowData.total} />} />
			</DataTable>
		);
	};

	const historyTable = (bulkload) => {
		let sortedHistory = [];
		if (bulkload.history != null) {
			sortedHistory = bulkload.history.sort(function (a, b) {
				const start1 = new Date(a.loadStarted);
				const start2 = new Date(b.loadStarted);
				return start2 - start1;
			});
		}

		return (
			<div className="card">
				<DataTable key="historyTable" value={sortedHistory} responsiveLayout="scroll">
					<Column field="duration" header="Time" body={durationTemplate} />
					<Column field="bulkloadStatus" body={bulkloadFileStatusTemplate} header="Status" />
					<Column field="counts" body={countsTemplate} header="Counts" />
					<Column field="bulkLoadFile.md5Sum" header="MD5 Sum" />
					<Column
						field="bulkLoadFile.fileSize"
						header="Compressed File Size"
						body={(rowData) => <NumberTemplate number={rowData.bulkLoadFile.fileSize} />}
					/>
					<Column field="bulkLoadFile.s3Url" header="S3 Url (Download)" body={urlTemplate} />
					<Column field="bulkLoadFile.linkMLSchemaVersion" header="LinkML Schema Version" />
					{showModRelease(bulkload)}

					<Column body={historyActionBodyTemplate} exportable={false} style={{ minWidth: '8rem' }}></Column>
					<Column
						body={(rowData) => loadFileActionBodyTemplate(rowData, bulkload)}
						exportable={false}
						style={{ minWidth: '8rem' }}
					></Column>
				</DataTable>
			</div>
		);
	};

	const sortFilesByDate = (files) => {
		let sortedFiles = [];
		let lastLoadedDates = new Map();
		let filesWithoutDates = [];
		files.forEach((file) => {
			if (file.bulkloadStatus === 'FINISHED' || file.bulkloadStatus === 'STOPPED' || file.bulkloadStatus === 'FAILED') {
				if (file.dateLastLoaded) {
					lastLoadedDates.set(file.dateLastLoaded, file);
				} else {
					filesWithoutDates.push(file);
				}
			} else {
				lastLoadedDates.set(new Date().toISOString(), file);
			}
		});
		Array.from(lastLoadedDates.keys())
			.sort(function (a, b) {
				const start1 = new Date(a);
				const start2 = new Date(b);
				return start2 - start1;
			})
			.forEach((date) => sortedFiles.push(lastLoadedDates.get(date)));

		if (filesWithoutDates.length > 0) {
			filesWithoutDates.forEach((fwd) => {
				sortedFiles.push(fwd);
			});
		}

		return sortedFiles;
	};

	/*
	const fileTable = (load) => {
		let sortedFiles = [];
		if (load.loadFiles) {
			sortedFiles = sortFilesByDate(load.loadFiles);
		}
		sortedFiles.forEach((file) => {
			file.loadType = load.backendBulkLoadType;
		});
		return (
			<div className="card">
				<DataTable
					key="fileTable"
					value={sortedFiles}
					responsiveLayout="scroll"
					expandedRows={expandedFileRows}
					onRowToggle={(e) => setExpandedFileRows(e.data)}
					rowExpansionTemplate={historyTable}
					dataKey="id"
				>
					<Column expander style={{ width: '3em' }} />
					<Column field="md5Sum" header="MD5 Sum" />
					<Column field="fileSize" header="Compressed File Size" />
					<Column field="recordCount" header="Record Count" />
					<Column field="s3Url" header="S3 Url (Download)" body={urlTemplate} />
					<Column field="linkMLSchemaVersion" header="LinkML Schema Version" />
					{showModRelease(load)}
					<Column field="dateLastLoaded" header="Last Loaded" />
					<Column field="bulkloadStatus" body={bulkloadFileStatusTemplate} header="Status" />
					<Column body={loadFileActionBodyTemplate} exportable={false} style={{ minWidth: '8rem' }}></Column>
				</DataTable>
			</div>
		);
	};
*/

	const loadTable = (group) => {
		let sortedLoads = [];
		if (group.loads) {
			sortedLoads = group.loads.sort((a, b) => (a.name > b.name ? 1 : -1));
		}

		return (
			<div className="card">
				<DataTable
					key="loadTable"
					value={sortedLoads}
					responsiveLayout="scroll"
					expandedRows={expandedLoadRows}
					onRowToggle={(e) => setExpandedLoadRows(e.data)}
					rowExpansionTemplate={historyTable}
					dataKey="id"
				>
					<Column expander style={{ width: '3em' }} />
					<Column body={nameBodyTemplate} header="Load Name" />
					<Column field="type" header="Bulk Load Type" />
					<Column field="backendBulkLoadType" body={backendBulkLoadTypeTemplate} header="Backend Bulk Load Type" />
					{dynamicColumns(sortedLoads)}
					<Column field="status" body={bulkloadStatusTemplate} header="Status" />
					<Column
						key="loadAction"
						body={loadActionBodyTemplate}
						exportable={false}
						style={{ minWidth: '8rem' }}
					></Column>
				</DataTable>
			</div>
		);
	};

	const getSchemaVersionArray = (map) => {
		if (map) {
			const array = [];
			for (let item in map) {
				array.push({ className: item, schemaVersion: map[item] });
			}
			return array;
		} else {
			return [];
		}
	};

	const exemptTypes = (loadType) => {
		return loadType === 'GFF_EXON' || loadType === 'GFF_TRANSCRIPT' || loadType === 'GFF_CDS';
	};

	const fileWithinSchemaRange = (fileVersion, loadType) => {
		if (!fileVersion) return false;
		const classVersions = apiVersion?.agrCurationSchemaVersions;
		if (!classVersions) return false;

		const fileVersionParts = parseVersionString(fileVersion);

		let loadedClasses = [];
		if (loadTypeClasses.has(loadType)) {
			loadedClasses = loadTypeClasses.get(loadType);
		} else {
			console.error('Unrecognized load type ' + loadType);
		}

		for (const loadedClass of loadedClasses) {
			const classVersionRange = classVersions[loadedClass];
			if (!classVersionRange) return false;

			let minMaxVersions = classVersionRange.includes('-')
				? classVersionRange.split(' - ')
				: [classVersionRange, classVersionRange];
			if (minMaxVersions.length === 0 || minMaxVersions.length > 2) return false;
			let minMaxVersionParts = [];
			minMaxVersions.forEach((version, ix) => {
				minMaxVersionParts[ix] = parseVersionString(version);
			});

			const minVersionParts = minMaxVersionParts[0];
			if (minMaxVersions.length === 1) {
				if (
					minVersionParts[0] !== fileVersionParts[0] ||
					minVersionParts[1] !== fileVersionParts[1] ||
					minVersionParts[2] !== fileVersionParts[2]
				) {
					return false;
				}
			}
			const maxVersionParts = minMaxVersionParts[1];
			// check not lower than min version
			if (fileVersionParts[0] < minVersionParts[0]) return false;
			if (fileVersionParts[0] === minVersionParts[0]) {
				if (fileVersionParts[1] < minVersionParts[1]) return false;
				if (fileVersionParts[1] === minVersionParts[1]) {
					if (fileVersionParts[2] < minVersionParts[2]) return false;
				}
			}
			// check not higher than max version
			if (fileVersionParts[0] > maxVersionParts[0]) return false;
			if (fileVersionParts[0] === maxVersionParts[0]) {
				if (fileVersionParts[1] > maxVersionParts[1]) return false;
				if (fileVersionParts[1] === maxVersionParts[1]) {
					if (fileVersionParts[2] > maxVersionParts[2]) return false;
				}
			}
		}
		return true;
	};

	const parseVersionString = (version) => {
		const regexp = /(?<major>\d+)\.(?<minor>\d+)\.?(?<patch>\d*)/;
		const versionParts = version.match(regexp).groups;
		const majorVersion = versionParts.major ? parseInt(versionParts.major) : 0;
		const minorVersion = versionParts.minor ? parseInt(versionParts.minor) : 0;
		const patchVersion = versionParts.patch ? parseInt(versionParts.patch) : 0;

		return [majorVersion, minorVersion, patchVersion];
	};

	const uploadConfirmDialogFooter = () => {
		return (
			<React.Fragment>
				<div className="col-12">
					<div className="grid">
						<div className="col-6">
							<FileUpload
								key="uploadConfirm"
								mode="basic"
								auto
								chooseOptions={{ icon: 'pi pi-check', label: 'Confirm', className: 'p-button-text' }}
								accept="*"
								customUpload
								uploadHandler={(e) => uploadLoadFile(e)}
								maxFileSize={1000000000000000}
							/>
						</div>
						<div className="col-3">
							<Button label="Cancel" icon="pi pi-times" className="p-button-text" onClick={hideUploadConfirmDialog} />
						</div>
					</div>
				</div>
			</React.Fragment>
		);
	};

	const ProgressIndicator = ({ load }) => {
		if (load.currentCount && load.totalSize) {
			return <ProgressBar value={parseInt((load.currentCount / load.totalSize) * 10000) / 100} />;
		} else if (load.currentCount && load.lastCount && load.lastTime && load.nowTime) {
			let rate = Math.ceil(((load.currentCount - load.lastCount) / (load.nowTime - load.lastTime)) * 1000);
			return (
				<ProgressBar
					value={rate}
					displayValueTemplate={(value) => {
						return (
							<>
								{value}r/s -- {load.currentCount}
							</>
						);
					}}
				/>
			);
		} else {
			return <ProgressBar value={0} />;
		}
	};

	const processingLoadsComponents = () => {
		let ret = [];
		//console.log(runningLoads);
		for (let key in runningLoads) {
			//console.log(key);
			if (runningLoads[key]) {
				ret.push(
					<div className="col-2" key={key}>
						<div className="card">
							<h3>{key}</h3>
							<ProgressIndicator load={runningLoads[key]} />
						</div>
					</div>
				);
			}
		}
		return ret;
	};

	return (
		<>
			<Toast ref={toast}></Toast>
			<LoadingOverlay isLoading={isLoading} />
			<div className="card">
				<Button
					label="New Group"
					icon="pi pi-plus"
					className="p-button-success mr-2"
					onClick={handleNewBulkLoadGroupOpen}
				/>
				<Button
					label="New Bulk Load"
					icon="pi pi-plus"
					className="p-button-success mr-2"
					onClick={handleNewBulkLoadOpen}
				/>
				<Button label="Refresh Data" icon="pi pi-plus" className="p-button-success mr-2" onClick={refresh} />
				<Messages ref={errorMessage} />
				{errorLoads.length > 0 && (
					<div>
						<br />
						<h3>Failed Loads Table</h3>
						<DataTable
							key="errorTable"
							value={errorLoads}
							responsiveLayout="scroll"
							expandedRows={expandedErrorLoadRows}
							onRowToggle={(e) => setExpandedErrorLoadRows(e.data)}
							rowExpansionTemplate={historyTable}
							dataKey="id"
						>
							<Column expander style={{ width: '3em' }} />
							<Column body={nameBodyTemplate} header="Load Name" />
							<Column field="type" header="Bulk Load Type" />
							<Column field="backendBulkLoadType" body={backendBulkLoadTypeTemplate} header="Backend Bulk Load Type" />
							{dynamicColumns(errorLoads)}
							<Column field="status" body={bulkloadStatusTemplate} header="Status" />
							<Column
								key="loadAction"
								body={loadActionBodyTemplate}
								exportable={false}
								style={{ minWidth: '8rem' }}
							></Column>
						</DataTable>
					</div>
				)}
				<h3>Data Loads Table</h3>
				<DataTable
					key="groupTable"
					value={groups}
					className="p-datatable-sm"
					expandedRows={expandedGroupRows}
					onRowToggle={(e) => setExpandedGroupRows(e.data)}
					rowExpansionTemplate={loadTable}
					dataKey="id"
				>
					<Column expander style={{ width: '3em' }} />
					<Column body={nameBodyTemplate} header="Group Name" />
					<Column body={groupActionBodyTemplate} exportable={false} style={{ minWidth: '8rem' }}></Column>
				</DataTable>
				<NewBulkLoadForm
					bulkLoadDialog={bulkLoadDialog}
					setBulkLoadDialog={setBulkLoadDialog}
					newBulkLoad={newBulkLoad}
					bulkLoadDispatch={bulkLoadDispatch}
					groups={groups}
					disableFormFields={disableFormFields}
					setDisableFormFields={setDisableFormFields}
					dataLoadService={dataLoadService}
				/>
				<NewBulkLoadGroupForm
					bulkLoadGroupDialog={bulkLoadGroupDialog}
					setBulkLoadGroupDialog={setBulkLoadGroupDialog}
				/>
				<HistoryDialog
					historyDialog={historyDialog}
					setHistoryDialog={setHistoryDialog}
					dataLoadService={getService()}
					history={history}
				/>
				<Dialog
					visible={uploadConfirmDialog}
					style={{ width: '450px' }}
					header="Confirm Upload"
					modal
					footer={uploadConfirmDialogFooter}
					onHide={hideUploadConfirmDialog}
				>
					<div className="upload-confirmation-content">
						<i className="pi pi-exclamation-triangle mr-3" style={{ fontSize: '2rem' }} />
						{
							<span>
								Please confirm that you are submitting a file with LoadType “{uploadLoadType}” and SubType “
								{uploadSubType}”.
							</span>
						}
					</div>
				</Dialog>
			</div>
			<div className="card">
				<h3>Schema Version Table</h3>
				<DataTable
					key="schemaTable"
					value={getSchemaVersionArray(apiVersion?.submittedClassSchemaVersions)}
					className="p-datatable-sm"
					dataKey="id"
				>
					<Column header="Class Name" field="className" />
					<Column header="Curation Schema (LinkML) Version" field="schemaVersion"></Column>
				</DataTable>
			</div>
			<div className="card">
				<h3>Data Processing Info Table</h3>
				<div className="grid">{processingLoadsComponents()}</div>
			</div>
		</>
	);
};
