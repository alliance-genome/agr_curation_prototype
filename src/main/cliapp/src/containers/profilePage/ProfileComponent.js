import React, { useState, useRef } from 'react';
import { Card } from 'primereact/card';
import { DataTable } from 'primereact/datatable';
import { Dropdown } from 'primereact/dropdown';
import { Column } from 'primereact/column';
import { PersonService } from '../../service/PersonService';
import { useOktaAuth } from '@okta/okta-react';
import { Panel } from 'primereact/panel';
import { Ripple } from 'primereact/ripple';
import * as jose from 'jose';

import { ConfirmButton } from '../../components/ConfirmButton';
import { useGetUserSettings } from '../../service/useGetUserSettings';
import JsonView from 'react18-json-view';
import 'react18-json-view/src/style.css';
import 'react18-json-view/src/dark.css';
import { PersonSettingsService } from '../../service/PersonSettingsService';
import { useControlledVocabularyService } from '../../service/useControlledVocabularyService';
import { Toast } from 'primereact/toast';
import { useQueryClient, useQuery } from '@tanstack/react-query';

const initialThemeState = {
	layoutMode: 'static',
	layoutColorMode: 'dark',
	inputStyle: 'outlined',
	ripple: true,
	scale: 14,
	theme: 'vela-blue',
};

const initialSiteState = {
	debug: 'false',
};

export const ProfileComponent = () => {
	const { settings: themeState, mutate: setThemeState } = useGetUserSettings('themeSettings', initialThemeState, false);
	const { settings: siteState, mutate: setSiteState } = useGetUserSettings('siteSettings', initialSiteState, false);
	const queryClient = useQueryClient();

	const [localUserInfo, setLocalUserInfo] = useState({});
	const [oktaToken] = useState(JSON.parse(localStorage.getItem('okta-token-storage')));

	const { authState } = useOktaAuth();
	const toast_topright = useRef(null);

	const personService = new PersonService();
	const personSettingsService = new PersonSettingsService();
	const booleanTerms = useControlledVocabularyService('generic_boolean_terms');

	let deleteLocalStorage = (result, success, key) => {
		if (result.status !== 200) success = false;
		localStorage.removeItem(key);
	};

	const globalResetHandler = () => {
		let success = true;
		if (localUserInfo && localUserInfo.settings) {
			for (let setting of localUserInfo.settings) {
				personSettingsService
					.deleteUserSettings(setting.settingsKey)
					.then((result) => deleteLocalStorage(result, success, setting.settingsKey));
			}
			if (success) {
				toast_topright.current.show([
					{
						life: 7000,
						severity: 'success',
						summary: 'Update success: ',
						detail: 'Application state has been reset',
						sticky: false,
					},
				]);
				queryClient.invalidateQueries(['localUserInfo']);
			} else {
				toast_topright.current.show([
					{
						life: 7000,
						severity: 'error',
						summary: 'Update error: ',
						detail: 'An error has occured while trying to reset your application state',
						sticky: false,
					},
				]);
			}
		}
	};

	const resetTableState = (settingsKey) => {
		let success = true;
		personSettingsService
			.deleteUserSettings(settingsKey)
			.then((result) => deleteLocalStorage(result, success, settingsKey));
		if (success) {
			toast_topright.current.show([
				{
					life: 7000,
					severity: 'success',
					summary: 'Update success: ',
					detail: 'Table state has been reset',
					sticky: false,
				},
			]);
			queryClient.invalidateQueries(['localUserInfo']);
		} else {
			toast_topright.current.show([
				{
					life: 7000,
					severity: 'error',
					summary: 'Update error: ',
					detail: 'An error has occured while trying to reset your table state',
					sticky: false,
				},
			]);
		}
	};

	const themeResetHandler = () => {
		setThemeState(initialThemeState);
		window.location.reload();
	};

	const regenApiToken = () => {
		personService
			.regenApiToken()
			.then((data) => {
				setLocalUserInfo(data);
			})
			.catch((err) => {
				console.log(err);
			});
	};

	useQuery(['localUserInfo'], () => personService.getUserInfo(), {
		onSuccess: (data) => {
			setLocalUserInfo(data);
		},
		onError: (error) => {
			console.log(error);
		},
		keepPreviousData: true,
		refetchOnWindowFocus: false,
		enabled: !!authState?.isAuthenticated,
	});

	const valueTemplate = (props) => {
		return props.template(props);
	};

	const textTemplate = (props) => {
		return <p style={{ wordBreak: 'break-all' }}>{props.value}</p>;
	};

	const headerTemplate = (options, props) => {
		const toggleIcon = options.collapsed ? 'pi pi-chevron-down' : 'pi pi-chevron-up';
		const className = `${options.className} justify-content-start`;
		const titleClassName = `${options.titleClassName} pl-1`;

		return (
			<div className={className}>
				<button className={options.togglerClassName} onClick={options.onTogglerClick}>
					<span className={toggleIcon}></span>
					<Ripple />
				</button>
				<span className={titleClassName}>Expand</span>
			</div>
		);
	};

	const jsonTemplate = (props) => {
		return (
			<Panel headerTemplate={headerTemplate} toggleable collapsed>
				<JsonView src={props.value} theme="default" dark={themeState?.layoutColorMode !== 'light'} />
			</Panel>
		);
	};

	const tableResetTemplate = (props) => {
		return (
			<>
				<Panel headerTemplate={headerTemplate} toggleable collapsed>
					<JsonView src={props.value} theme="default" dark={themeState?.layoutColorMode !== 'light'} />
				</Panel>
				<ConfirmButton
					buttonText="Reset Table"
					headerText={`${props.value.tableKeyName} State Reset`}
					messageText={`Are you sure? This will reset the local state of the ${props.name}.`}
					acceptHandler={() => resetTableState(props.name)}
				/>
			</>
		);
	};

	const debugChange = (event) => {
		let _siteState = {
			...siteState,
			debug: event.value,
		};
		setSiteState(_siteState);
	};

	const siteTemplate = (settings) => {
		return (
			<div>
				<div>
					Debug:{' '}
					<Dropdown
						name={'Debug'}
						value={siteState.debug}
						options={booleanTerms}
						optionLabel="text"
						optionValue="text"
						onChange={debugChange}
					/>
				</div>
			</div>
		);
	};

	const userInfos = [
		{ name: 'Name', value: localUserInfo.firstName + ' ' + localUserInfo.lastName, template: textTemplate },
		{
			name: 'Alliance Member',
			value: localUserInfo?.allianceMember?.fullName + ' (' + localUserInfo?.allianceMember?.abbreviation + ')',
			template: textTemplate,
		},
		{ name: 'Okta Email', value: localUserInfo.oktaEmail, template: textTemplate },
		{ name: 'Okta Access Token', value: oktaToken.accessToken.accessToken, template: textTemplate },
		{ name: 'Okta Id Token', value: oktaToken.idToken.idToken, template: textTemplate },
		{ name: 'Curation API Token', value: localUserInfo.apiToken, template: textTemplate },
		{
			name: 'Okta Access Token Content',
			value: jose.decodeJwt(oktaToken.accessToken.accessToken),
			template: jsonTemplate,
		},
		{ name: 'Okta Id Token Content', value: jose.decodeJwt(oktaToken.idToken.idToken), template: jsonTemplate },
		{ name: 'User Settings', value: localUserInfo.settings, template: jsonTemplate },
		{ name: 'Site Settings', value: localUserInfo.settings, template: siteTemplate },
	];

	if (localUserInfo && localUserInfo.settings) {
		for (let setting of localUserInfo.settings) {
			if (setting.settingsKey === 'themeSettings') continue;
			if (setting.settingsKey === 'siteSettings') continue;
			userInfos.push({ name: setting.settingsKey, value: setting.settingsMap, template: tableResetTemplate });
		}
	}

	return (
		<>
			<Toast ref={toast_topright} position="top-right" />
			<div className="grid">
				<div className="col-12">
					<Card title="User Profile" subTitle="">
						<DataTable value={userInfos} style={{ width: '100%' }}>
							<Column field="name" header="Name" style={{ minWidth: '16rem' }} />
							<Column field="value" header="Value" body={valueTemplate} />
						</DataTable>
					</Card>
				</div>
				<div className="col-6">
					<div className="grid">
						<div className="col-5">
							<ConfirmButton
								buttonText="Regenerate Curation API Token"
								headerText="Regenerate Curation API Token"
								messageText="Are you sure you want to regenerate the API token? This will immediately invalidate the prior token and it can't be used again."
								acceptHandler={regenApiToken}
							/>
						</div>
						<div className="col-4">
							<ConfirmButton
								buttonText="Global State Reset"
								headerText="Global State Reset"
								buttonClassName="p-button-danger mr-3"
								messageText="Are you sure? This will reset the local state of all the data tables and your theme settings."
								acceptHandler={globalResetHandler}
							/>
						</div>
						<div className="col-3">
							<ConfirmButton
								buttonText="Reset Themes"
								headerText="Reset Themes"
								messageText="Are you sure? This will reset your theme settings."
								acceptHandler={themeResetHandler}
							/>
						</div>
					</div>
				</div>
			</div>
		</>
	);
};
