import React, { useState, useEffect, useRef } from 'react';
import { useQuery } from '@tanstack/react-query';
import classNames from 'classnames';
import { useLocation } from 'react-router-dom';
import { CSSTransition } from 'react-transition-group';

import { useOktaAuth } from '@okta/okta-react';

import { AppTopbar } from '../../AppTopbar';
import { AppFooter } from '../../AppFooter';
import { AppMenu } from '../../AppMenu';
import { AppConfig } from '../../AppConfig';

import { ApiVersionService } from '../../service/ApiVersionService';

import PrimeReact from 'primereact/api';
import { Tooltip } from 'primereact/tooltip';
import { SiteContext } from './SiteContext';

import 'primereact/resources/primereact.css';
import 'primeicons/primeicons.css';
import 'primeflex/primeflex.css';
import 'prismjs/themes/prism-coy.css';
import '../../assets/demo/flags/flags.css';
import '../../assets/demo/Demos.scss';
import '../../assets/layout/layout.scss';
import '../../App.scss';
import { useGetUserSettings } from '../../service/useGetUserSettings';

const initialThemeState = {
	layoutMode: 'static',
	layoutColorMode: 'dark',
	inputStyle: 'outlined',
	ripple: true,
	scale: 14,
	theme: 'vela-blue',
};

export const SiteLayout = (props) => {
	const { settings: themeState, mutate: setThemeState } = useGetUserSettings('themeSettings', initialThemeState, false);
	const [staticMenuInactive, setStaticMenuInactive] = useState(false);
	const [overlayMenuActive, setOverlayMenuActive] = useState(false);
	const [mobileMenuActive, setMobileMenuActive] = useState(false);
	const [mobileTopbarMenuActive, setMobileTopbarMenuActive] = useState(false);

	const [siteContext, setSiteContext] = useState({});

	const copyTooltipRef = useRef();
	const location = useLocation();

	const { authState, oktaAuth } = useOktaAuth();

	const { children } = props;
	let [apiService, setApiService] = useState();

	const setInputStyle = (value) => {
		let _themeState = {
			...themeState,
			inputStyle: value,
		};

		setThemeState(_themeState);
	};

	const setRipple = (value) => {
		let _themeState = {
			...themeState,
			ripple: value,
		};

		setThemeState(_themeState);
	};

	const setLayoutMode = (value) => {
		let _themeState = {
			...themeState,
			layoutMode: value,
		};

		setThemeState(_themeState);
	};

	useEffect(() => {
		if (authState?.isAuthenticated) {
			setApiService(new ApiVersionService());
		}
	}, [authState]);

	useQuery(['getApiVersion'], () => apiService.getApiVersion(), {
		onSuccess: (data) => {
			//console.log(data);
			//setApiVersion(data);
			setSiteContext({ ...siteContext, apiVersion: data });
		},
		onError: (error) => {
			console.log(error);
		},
		keepPreviousData: true,
		refetchOnWindowFocus: false,
		enabled: !!(authState?.isAuthenticated && apiService),
	});

	const logout = async () => {
		await oktaAuth.signOut();
	};

	useEffect(() => {
		if (!authState || !authState.isAuthenticated) {
			setStaticMenuInactive(true);
		} else {
			setStaticMenuInactive(false);
		}
	}, [authState]);

	PrimeReact.ripple = true;

	let menuClick = false;
	let mobileTopbarMenuClick = false;

	useEffect(() => {
		if (mobileMenuActive) {
			addClass(document.body, 'body-overflow-hidden');
		} else {
			removeClass(document.body, 'body-overflow-hidden');
		}
	}, [mobileMenuActive]);

	useEffect(() => {
		copyTooltipRef && copyTooltipRef.current && copyTooltipRef.current.updateTargetEvents();
	}, [location]);

	const onInputStyleChange = (inputStyle) => {
		setInputStyle(inputStyle);
	};

	const onRipple = (e) => {
		PrimeReact.ripple = e.value;
		setRipple(e.value);
	};

	const onLayoutModeChange = (mode) => {
		setLayoutMode(mode);
	};

	const onWrapperClick = (event) => {
		if (!menuClick) {
			setOverlayMenuActive(false);
			setMobileMenuActive(false);
		}

		if (!mobileTopbarMenuClick) {
			setMobileTopbarMenuActive(false);
		}

		mobileTopbarMenuClick = false;
		menuClick = false;
	};

	const onToggleMenuClick = (event) => {
		menuClick = true;

		if (isDesktop()) {
			if (themeState.layoutMode === 'overlay') {
				if (mobileMenuActive === true) {
					setOverlayMenuActive(true);
				}

				setOverlayMenuActive((prevState) => !prevState);
				setMobileMenuActive(false);
			} else if (themeState.layoutMode === 'static') {
				setStaticMenuInactive((prevState) => !prevState);
			}
		} else {
			setMobileMenuActive((prevState) => !prevState);
		}

		event.preventDefault();
	};

	const onSidebarClick = () => {
		menuClick = true;
	};

	const onMobileTopbarMenuClick = (event) => {
		mobileTopbarMenuClick = true;

		setMobileTopbarMenuActive((prevState) => !prevState);
		event.preventDefault();
	};

	const onMobileSubTopbarMenuClick = (event) => {
		mobileTopbarMenuClick = true;

		event.preventDefault();
	};

	const onMenuItemClick = (event) => {
		if (!event.item.items) {
			setOverlayMenuActive(false);
			setMobileMenuActive(false);
		}
	};
	const isDesktop = () => {
		return window.innerWidth >= 992;
	};

	const menu = [
		{
			label: 'Curation',
			icon: 'pi pi-fw pi-home',
			items: [
				{ label: 'Dashboard', icon: 'pi pi-fw pi-home', to: '/' },
				{
					label: 'Data Tables',
					icon: 'pi pi-fw pi-sitemap',
					//Add and update new items in alphabetical order
					items: [
						{ label: 'Affected Genomic Models', icon: 'pi pi-fw pi-home', to: '/agms' },
						{ label: 'Alleles', icon: 'pi pi-fw pi-home', to: '/alleles' },
						{ label: 'Constructs', icon: 'pi pi-fw pi-home', to: '/constructs' },
						{ label: 'Disease Annotations', icon: 'pi pi-fw pi-home', to: '/diseaseAnnotations' },
						{ label: 'Experimental Conditions', icon: 'pi pi-fw pi-home', to: '/experimentalConditions' },
						{ label: 'Experiments', icon: 'pi pi-fw pi-home', to: '/conditionRelations' },
						{ label: 'Genes', icon: 'pi pi-fw pi-home', to: '/genes' },
						{ label: 'Gene Genetic Interactions', icon: 'pi pi-fw pi-home', to: '/geneGeneticInteractions' },
						{ label: 'Gene Molecular Interactions', icon: 'pi pi-fw pi-home', to: '/geneMolecularInteractions' },
						{ label: 'Literature References', icon: 'pi pi-fw pi-home', to: '/references' },
						{ label: 'Molecules', icon: 'pi pi-fw pi-home', to: '/molecules' },
						{ label: 'Phenotype Annotations', icon: 'pi pi-fw pi-home', to: '/phenotypeAnnotations' },
						{ label: 'Species', icon: 'pi pi-fw pi-home', to: '/species' },
						{ label: 'Variants', icon: 'pi pi-fw pi-home', to: '/variants' },
					],
				},
				{
					label: 'Ontologies',
					icon: 'pi pi-fw pi-sitemap',
					items: [
						{ label: 'Alliance Tags for Papers Ontology (ATP)', icon: 'pi pi-fw pi-home', to: '/ontology/atp' },
						{
							label: 'Anatomical Ontologies',
							icon: 'pi pi-fw pi-sitemap',
							items: [
								{ label: 'C. elegans Gross Anatomy Ontology (WBbt)', icon: 'pi pi-fw pi-home', to: '/ontology/wbbt' },
								{ label: 'Cell Ontology (CL)', icon: 'pi pi-fw pi-home', to: '/ontology/cl' },
								{ label: 'Drosophila Anatomy Ontology (DAO)', icon: 'pi pi-fw pi-home', to: '/ontology/dao' },
								{ label: 'Mouse Adult Gross Anatomy Ontology (MA)', icon: 'pi pi-fw pi-home', to: '/ontology/ma' },
								{
									label: 'Mouse Developmental Anatomy Ontology (EMAPA)',
									icon: 'pi pi-fw pi-home',
									to: '/ontology/emapa',
								},
								{ label: 'Uberon Ontology (UBERON)', icon: 'pi pi-fw pi-home', to: '/ontology/uberon' },
								{ label: 'Xenopus Anatomy Ontology (XBA)', icon: 'pi pi-fw pi-home', to: '/ontology/xba' },
								{ label: 'Zebrafish Anatomy Ontology (ZFA)', icon: 'pi pi-fw pi-home', to: '/ontology/zfa' },
							],
						},
						{ label: 'Biological Spatial Ontology (BSPO)', icon: 'pi pi-fw pi-home', to: '/ontology/bspo' },
						{
							label: 'Chemical Ontologies',
							icon: 'pi pi-fw pi-sitemap',
							items: [
								{
									label: 'Chemical Entities of Biological Interest (ChEBI)',
									icon: 'pi pi-fw pi-home',
									to: '/ontology/chebi',
								},
								{ label: 'Xenopus Small Molecule Ontology (XSMO)', icon: 'pi pi-fw pi-home', to: '/ontology/xsmo' },
							],
						},
						{ label: 'Clinical Measurement Ontology (CMO)', icon: 'pi pi-fw pi-home', to: '/ontology/cmo' },
						{ label: 'Disease Ontology (DO)', icon: 'pi pi-fw pi-home', to: '/ontology/do' },
						{ label: 'Evidence & Conclusion Ontology (ECO)', icon: 'pi pi-fw pi-home', to: '/ontology/eco' },
						{
							label: 'Experimental Condition Ontologies',
							icon: 'pi pi-fw pi-sitemap',
							items: [
								{ label: 'Experimental Condition Ontology (XCO)', icon: 'pi pi-fw pi-home', to: '/ontology/xco' },
								{
									label: 'Zebrafish Experimental Conditions Ontology (ZECO)',
									icon: 'pi pi-fw pi-home',
									to: '/ontology/zeco',
								},
							],
						},
						{ label: 'Flybase Controlled Vocabulary (FBcv)', icon: 'pi pi-fw pi-home', to: '/ontology/fbcv' },
						{ label: 'Gene Ontology (GO)', icon: 'pi pi-fw pi-home', to: '/ontology/go' },
						{ label: 'Genotype Ontology (GENO)', icon: 'pi pi-fw pi-home', to: '/ontology/geno' },
						{ label: 'Measurement Method Ontology (MMO)', icon: 'pi pi-fw pi-home', to: '/ontology/mmo' },
						{ label: 'Molecular Interactions Ontology (MI)', icon: 'pi pi-fw pi-home', to: '/ontology/mi' },
						{ label: 'Mouse Pathology Ontology (MPATH)', icon: 'pi pi-fw pi-home', to: '/ontology/mpath' },
						{
							label: 'NCBI Organismal Classification (NCBITaxon)',
							icon: 'pi pi-fw pi-home',
							to: '/ontology/ncbitaxon',
						},
						{ label: 'Ontology for Biomedical Investigations (OBI)', icon: 'pi pi-fw pi-home', to: '/ontology/obi' },
						{ label: 'Phenotype and Trait Ontology (PATO)', icon: 'pi pi-fw pi-home', to: '/ontology/pato' },
						{
							label: 'Phenotype Ontologies',
							icon: 'pi pi-fw pi-sitemap',
							items: [
								{ label: 'Ascomycete Phenotype Ontology (APO)', icon: 'pi pi-fw pi-home', to: '/ontology/apo' },
								{
									label: 'C. elegans Phenotype Ontology (WBPhenotype)',
									icon: 'pi pi-fw pi-home',
									to: '/ontology/wbpheno',
								},
								{ label: 'Human Phenotype Ontology (HP)', icon: 'pi pi-fw pi-home', to: '/ontology/hp' },
								{ label: 'Mammalian Phenotype Ontology (MP)', icon: 'pi pi-fw pi-home', to: '/ontology/mp' },
								{ label: 'Xenopus Phenotype Ontology (XPO)', icon: 'pi pi-fw pi-home', to: '/ontology/xpo' },
							],
						},
						{ label: 'Pathway Ontology (PW)', icon: 'pi pi-fw pi-home', to: '/ontology/pw' },
						{ label: 'Protein Modification Ontology (MOD)', icon: 'pi pi-fw pi-home', to: '/ontology/mod' },
						{ label: 'Rat Strain Ontology (RS)', icon: 'pi pi-fw pi-home', to: '/ontology/rs' },
						{ label: 'Relation Ontology (RO)', icon: 'pi pi-fw pi-home', to: '/ontology/ro' },
						{ label: 'Sequence Ontology (SO)', icon: 'pi pi-fw pi-home', to: '/ontology/so' },
						{
							label: 'Stage Ontologies',
							icon: 'pi pi-fw pi-sitemap',
							items: [
								{ label: 'C. elegans Development Ontology (WBls)', icon: 'pi pi-fw pi-home', to: '/ontology/wbls' },
								{ label: 'FlyBase Developmental Ontology (FBdv)', icon: 'pi pi-fw pi-home', to: '/ontology/fbdv' },
								{
									label: 'Mouse Developmental Stages Ontology (MmusDv)',
									icon: 'pi pi-fw pi-home',
									to: '/ontology/mmusdv',
								},
								{ label: 'Xenopus Developmental Stage Ontology (XBS)', icon: 'pi pi-fw pi-home', to: '/ontology/xbs' },
								{
									label: 'Zebrafish Developmental Stages Ontology (ZFS)',
									icon: 'pi pi-fw pi-home',
									to: '/ontology/zfs',
								},
							],
						},
						{ label: 'Vertebrate Trait Ontology (VT)', icon: 'pi pi-fw pi-home', to: '/ontology/vt' },
						{ label: 'Xenbase Experimental Data Ontology (XBED)', icon: 'pi pi-fw pi-home', to: '/ontology/xbed' },
					],
				},
				{
					label: 'Controlled Vocabularies',
					icon: 'pi pi-fw pi-sitemap',
					items: [
						{ label: 'Terms', icon: 'pi pi-fw pi-home', to: '/vocabterms' },
						{ label: 'Vocabularies', icon: 'pi pi-fw pi-home', to: '/vocabularies' },
						{ label: 'Vocabulary Term Sets', icon: 'pi pi-fw pi-home', to: '/vocabularytermsets' },
					],
				},
				{
					label: 'Resource Descriptors',
					icon: 'pi pi-fw pi-sitemap',
					items: [
						{ label: 'Resource Descriptors', icon: 'pi pi-fw pi-home', to: '/resourcedescriptors' },
						{ label: 'Resource Descriptor Pages', icon: 'pi pi-fw pi-home', to: '/resourcedescriptorpages' },
					],
				},
				{
					label: 'Data files and reports',
					icon: 'pi pi-fw pi-sitemap',
					items: [
						{ label: 'FMS Data Files', icon: 'pi pi-fw pi-home', to: '/fmsdatafiles' },
						{ label: 'FMS Data Types', icon: 'pi pi-fw pi-home', to: '/fmsdatatypes' },
						{ label: 'Data Loads', icon: 'pi pi-fw pi-home', to: '/dataloads' },
						{ label: 'Reports', icon: 'pi pi-fw pi-home', to: '/reports' },
					],
				},
				{
					label: 'Developer links',
					icon: 'pi pi-fw pi-sitemap',
					items: [
						{ label: 'Site Metrics', icon: 'pi pi-fw pi-home', to: '/metricspage' },
						{ label: 'Entity Counts', icon: 'pi pi-fw pi-home', to: '/entitycounts' },
						{ label: 'API UI (Swagger)', icon: 'pi pi-fw pi-home', url: '/swagger-ui', target: '_blank' },
						{
							label: 'Search index UI (cerebro)',
							icon: 'pi pi-fw pi-home',
							url: `http://cerebro.alliancegenome.org:9000/#!/overview?host=https://${siteContext?.apiVersion?.esHost}`,
							target: '_blank',
						},
						{
							label: 'Logs Server UI',
							icon: 'pi pi-fw pi-home',
							url: `http://logs.alliancegenome.org:5601/app/logtrail#/?q=*&h=agr.curation.${siteContext?.apiVersion?.env}.api.server&t=Now&i=logstash*&_g=()`,
							target: '_blank',
						},
						{ label: 'Site Health', icon: 'pi pi-fw pi-plus', to: '/healthpage' },
					],
				},
			],
		},
	];

	const addClass = (element, className) => {
		if (element.classList) element.classList.add(className);
		else element.className += ' ' + className;
	};

	const removeClass = (element, className) => {
		if (element.classList) element.classList.remove(className);
		else
			element.className = element.className.replace(
				new RegExp('(^|\\b)' + className.split(' ').join('|') + '(\\b|$)', 'gi'),
				' '
			);
	};

	const wrapperClass = classNames('layout-wrapper', {
		'layout-overlay': themeState?.layoutMode === 'overlay',
		'layout-static': themeState?.layoutMode === 'static',
		'layout-static-sidebar-inactive': staticMenuInactive && themeState?.layoutMode === 'static',
		'layout-overlay-sidebar-active': overlayMenuActive && themeState?.layoutMode === 'overlay',
		'layout-mobile-sidebar-active': mobileMenuActive,
		'p-input-filled': themeState?.inputStyle === 'filled',
		'p-ripple-disabled': themeState?.ripple === false,
		'layout-theme-light': themeState?.layoutColorMode === 'light',
	});

	return (
		<SiteContext.Provider value={siteContext}>
			<div className={wrapperClass} onClick={onWrapperClick}>
				<Tooltip
					ref={copyTooltipRef}
					target=".block-action-copy"
					position="bottom"
					content="Copied to clipboard"
					event="focus"
				/>

				<AppTopbar
					onToggleMenuClick={onToggleMenuClick}
					layoutColorMode={themeState?.layoutColorMode}
					mobileTopbarMenuActive={mobileTopbarMenuActive}
					onMobileTopbarMenuClick={onMobileTopbarMenuClick}
					onMobileSubTopbarMenuClick={onMobileSubTopbarMenuClick}
					authState={authState}
					logout={logout}
				/>

				<div className="layout-sidebar" onClick={onSidebarClick}>
					<AppMenu model={menu} onMenuItemClick={onMenuItemClick} layoutColorMode={themeState?.layoutColorMode} />
				</div>

				<div className="layout-main-container">
					<div className="layout-main">{children}</div>

					<AppFooter layoutColorMode={themeState?.layoutColorMode} />
				</div>

				<AppConfig
					rippleEffect={themeState?.ripple}
					onRippleEffect={onRipple}
					inputStyle={themeState?.inputStyle}
					onInputStyleChange={onInputStyleChange}
					layoutMode={themeState?.layoutMode}
					onLayoutModeChange={onLayoutModeChange}
					layoutColorMode={themeState?.layoutColorMode}
					themeState={themeState}
					setThemeState={setThemeState}
				/>

				<CSSTransition classNames="layout-mask" timeout={{ enter: 200, exit: 200 }} in={mobileMenuActive} unmountOnExit>
					<div className="layout-mask p-component-overlay"></div>
				</CSSTransition>
			</div>
		</SiteContext.Provider>
	);
};
