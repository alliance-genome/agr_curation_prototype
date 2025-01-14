import React, { useRef } from 'react';
import { Toast } from 'primereact/toast';
import { Splitter, SplitterPanel } from 'primereact/splitter';
import { Divider } from 'primereact/divider';
import { Button } from 'primereact/button';
import { ProgressSpinner } from 'primereact/progressspinner';
import { useParams } from 'react-router-dom';
import { useMutation, useQuery } from '@tanstack/react-query';
import { AlleleService } from '../../service/AlleleService';
import ErrorBoundary from '../../components/Error/ErrorBoundary';
import { TaxonFormEditor } from '../../components/Editors/taxon/TaxonFormEditor';
import { useAlleleReducer } from './useAlleleReducer';
import { InCollectionFormEditor } from '../../components/Editors/inCollection/InCollectionFormEditor';
import { BooleanFormEditor } from '../../components/Editors/boolean/BooleanFormEditor';
import { IdentifierFormTemplate } from '../../components/Templates/IdentifierFormTemplate';
import { DataProviderFormTemplate } from '../../components/Templates/DataProviderFormTemplate';
import { DateFormTemplate } from '../../components/Templates/DateFormTemplate';
import { UserFormTemplate } from '../../components/Templates/UserFormTemplate';
import { SynonymsForm } from './synonyms/SynonymsForm';
import { FullNameForm } from './fullName/FullNameForm';
import { MutationTypesForm } from './mutationTypes/MutationTypesForm';
import { InheritanceModesForm } from './inheritanceModes/InheritanceModesForm';
import { SecondaryIdsForm } from './secondaryIds/SecondaryIdsForm';
import { FunctionalImpactsForm } from './functionalImpacts/FunctionalImpactsForm';
import { DatabaseStatusForm } from './databaseStatus/DatabaseStatusForm';
import { RelatedNotesForm } from './relatedNotes/RelatedNotesForm';
import { SymbolForm } from './symbol/SymbolForm';
import { GermilineTransmissionStatusForm } from './germlineTransmissionStatus/GermlineTransmissionStatusForm';
import { ReferencesForm } from './referencesTable/ReferencesForm';
import { NomenclatureEventsForm } from './nomenclatureEvents/NomenclatureEventsForm';
import { StickyHeader } from '../../components/StickyHeader';
import { LoadingOverlay } from '../../components/LoadingOverlay';
import { AlleleGeneAssociationsForm } from './alleleGeneAssociations/AlleleGeneAssociationsForm';
import { validateRequiredAutosuggestField, processErrors } from './utils';

export default function AlleleDetailPage() {
	const { identifier } = useParams();
	const { alleleState, alleleDispatch } = useAlleleReducer();
	const alleleService = new AlleleService();
	const toastSuccess = useRef(null);
	const toastError = useRef(null);

	const labelColumnSize = 'col-3';
	const widgetColumnSize = 'col-4';
	const fieldDetailsColumnSize = 'col-5';

	const { isLoading: getRequestIsLoading } = useQuery([identifier], () => alleleService.getAllele(identifier), {
		onSuccess: (result) => {
			alleleDispatch({ type: 'SET', value: result?.data?.entity });
		},
		onError: (error) => {
			console.warn(error);
		},
		keepPreviousData: true,
		refetchOnWindowFocus: false,
	});

	const { isLoading: allelePutRequestIsLoading, mutate: alleleMutate } = useMutation((allele) => {
		return alleleService.saveAlleleDetail(allele);
	});

	const handleSubmit = async (event) => {
		event.preventDefault();
		alleleDispatch({
			type: 'SUBMIT',
		});

		const areUiErrors = validateRequiredAutosuggestField(
			alleleState.allele.alleleGeneAssociations,
			alleleState.entityStates.alleleGeneAssociations.errorMessages,
			alleleDispatch,
			'alleleGeneAssociations',
			'alleleGeneAssociationObject'
		);

		if (areUiErrors) return;

		alleleMutate(alleleState.allele, {
			onSuccess: (result) => {
				toastSuccess.current.show({ severity: 'success', summary: 'Successful', detail: 'Allele Saved' });
				alleleDispatch({ type: 'SET', value: result?.data?.entity });
			},
			onError: (error) => {
				let message;
				const data = error?.response?.data;

				if (data.errorMessage) {
					message = error.response.data.errorMessage;
				} else {
					//toast will still display even if 500 error and no errorMessages
					message = `${error.response.status} ${error.response.statusText}`;
				}
				toastError.current.show([
					{ life: 7000, severity: 'error', summary: 'Page error: ', detail: message, sticky: false },
				]);

				try {
					processErrors(data, alleleDispatch, alleleState.allele);
				} catch (e) {
					console.error(e);
				}
			},
		});
	};

	const onTaxonValueChange = (event) => {
		let value = {};
		if (typeof event.value === 'object') {
			value = event.value;
		} else {
			value.curie = event.value;
		}
		alleleDispatch({
			type: 'EDIT',
			field: 'taxon',
			value,
		});
	};

	const onInCollectionValueChange = (event) => {
		let value = {};
		if (typeof event.value === 'object') {
			value = event.value;
		} else if (event.value === '') {
			value = undefined;
		} else {
			value.name = event.value;
		}
		alleleDispatch({
			type: 'EDIT',
			field: 'inCollection',
			value,
		});
	};

	const onIsExtinctValueChange = (event) => {
		alleleDispatch({
			type: 'EDIT',
			field: 'isExtinct',
			value: event.value,
		});
	};

	const onInternalValueChange = (event) => {
		alleleDispatch({
			type: 'EDIT',
			field: 'internal',
			value: event.value,
		});
	};

	const onObsoleteValueChange = (event) => {
		alleleDispatch({
			type: 'EDIT',
			field: 'obsolete',
			value: event.value,
		});
	};

	if (getRequestIsLoading)
		return (
			<div className="flex align-items-center justify-content-center h-screen">
				<ProgressSpinner />
			</div>
		);

	const headerText = () => {
		let prefix = 'Allele: ';
		if (alleleState.allele?.alleleSymbol?.displayText && alleleState.allele?.primaryExternalId) {
			return `${prefix} ${alleleState.allele.alleleSymbol.displayText} (${alleleState.allele.primaryExternalId})`;
		}
		if (alleleState.allele?.primaryExternalId) {
			return `${prefix} ${alleleState.allele.primaryExternalId}`;
		}
		return 'Allele Detail Page';
	};

	return (
		<>
			<Toast ref={toastError} position="top-left" />
			<Toast ref={toastSuccess} position="top-right" />
			<LoadingOverlay isLoading={!!allelePutRequestIsLoading} />
			<ErrorBoundary>
				<StickyHeader>
					<Splitter className="bg-primary-reverse border-none lg:h-5rem" gutterSize={0}>
						<SplitterPanel size={70} className="flex justify-content-start ml-5 py-3 ">
							<h1 dangerouslySetInnerHTML={{ __html: headerText() }} />
						</SplitterPanel>
						<SplitterPanel size={30} className="flex justify-content-start py-3">
							<Button label="Save" icon="pi pi-check" className="p-button-text" size="large" onClick={handleSubmit} />
						</SplitterPanel>
					</Splitter>
				</StickyHeader>
				<form className="mt-8">
					<IdentifierFormTemplate
						identifier={alleleState.allele?.curie}
						label="Curie"
						widgetColumnSize={widgetColumnSize}
						labelColumnSize={labelColumnSize}
						fieldDetailsColumnSize={fieldDetailsColumnSize}
					/>

					<Divider />

					<IdentifierFormTemplate
						identifier={alleleState.allele?.primaryExternalId}
						label="Primary External ID"
						widgetColumnSize={widgetColumnSize}
						labelColumnSize={labelColumnSize}
						fieldDetailsColumnSize={fieldDetailsColumnSize}
					/>

					<Divider />

					<IdentifierFormTemplate
						identifier={alleleState.allele?.modInternalId}
						label="MOD Internal ID"
						widgetColumnSize={widgetColumnSize}
						labelColumnSize={labelColumnSize}
						fieldDetailsColumnSize={fieldDetailsColumnSize}
					/>

					<Divider />

					<FullNameForm state={alleleState} dispatch={alleleDispatch} />

					<Divider />

					<SymbolForm state={alleleState} dispatch={alleleDispatch} labelColumnSize={labelColumnSize} />

					<Divider />

					<SynonymsForm state={alleleState} dispatch={alleleDispatch} />

					<Divider />

					<SecondaryIdsForm state={alleleState} dispatch={alleleDispatch} />

					<Divider />

					<NomenclatureEventsForm state={alleleState} dispatch={alleleDispatch} />

					<Divider />

					<TaxonFormEditor
						taxon={alleleState.allele?.taxon}
						onTaxonValueChange={onTaxonValueChange}
						widgetColumnSize={widgetColumnSize}
						labelColumnSize={labelColumnSize}
						fieldDetailsColumnSize={fieldDetailsColumnSize}
						errorMessages={alleleState.errorMessages}
					/>

					<Divider />

					<MutationTypesForm state={alleleState} dispatch={alleleDispatch} />

					<Divider />

					<FunctionalImpactsForm state={alleleState} dispatch={alleleDispatch} />

					<Divider />

					<GermilineTransmissionStatusForm state={alleleState} dispatch={alleleDispatch} />

					<Divider />

					<DatabaseStatusForm state={alleleState} dispatch={alleleDispatch} />

					<Divider />

					<InheritanceModesForm state={alleleState} dispatch={alleleDispatch} />

					<Divider />

					<ReferencesForm state={alleleState} dispatch={alleleDispatch} />

					<Divider />

					<InCollectionFormEditor
						inCollection={alleleState.allele?.inCollection}
						onInCollectionValueChange={onInCollectionValueChange}
						widgetColumnSize={widgetColumnSize}
						labelColumnSize={labelColumnSize}
						fieldDetailsColumnSize={fieldDetailsColumnSize}
						errorMessages={alleleState.errorMessages}
					/>

					<Divider />

					<BooleanFormEditor
						value={alleleState.allele?.isExtinct}
						name={'isExtinct'}
						label={'Is Extinct'}
						onValueChange={onIsExtinctValueChange}
						widgetColumnSize={widgetColumnSize}
						labelColumnSize={labelColumnSize}
						fieldDetailsColumnSize={fieldDetailsColumnSize}
						errorMessages={alleleState.errorMessages}
						showClear={true}
					/>

					<Divider />

					<RelatedNotesForm state={alleleState} dispatch={alleleDispatch} />

					<Divider />

					<AlleleGeneAssociationsForm state={alleleState} dispatch={alleleDispatch} />

					<Divider />

					<DataProviderFormTemplate
						dataProvider={alleleState.allele?.dataProvider?.abbreviation}
						widgetColumnSize={widgetColumnSize}
						labelColumnSize={labelColumnSize}
						fieldDetailsColumnSize={fieldDetailsColumnSize}
					/>

					<Divider />

					<UserFormTemplate
						user={alleleState.allele?.updatedBy?.uniqueId}
						fieldName="Updated By"
						widgetColumnSize={widgetColumnSize}
						labelColumnSize={labelColumnSize}
						fieldDetailsColumnSize={fieldDetailsColumnSize}
					/>

					<Divider />

					<DateFormTemplate
						date={alleleState.allele?.dateUpdated}
						fieldName="Date Updated"
						widgetColumnSize={widgetColumnSize}
						labelColumnSize={labelColumnSize}
						fieldDetailsColumnSize={fieldDetailsColumnSize}
					/>

					<Divider />

					<UserFormTemplate
						user={alleleState.allele?.createdBy?.uniqueId}
						fieldName="Created By"
						widgetColumnSize={widgetColumnSize}
						labelColumnSize={labelColumnSize}
						fieldDetailsColumnSize={fieldDetailsColumnSize}
					/>

					<Divider />

					<DateFormTemplate
						date={alleleState.allele?.dateCreated}
						fieldName="Date Created"
						widgetColumnSize={widgetColumnSize}
						labelColumnSize={labelColumnSize}
						fieldDetailsColumnSize={fieldDetailsColumnSize}
					/>

					<Divider />

					<BooleanFormEditor
						value={alleleState.allele?.internal}
						name={'internal'}
						label={'Internal'}
						onValueChange={onInternalValueChange}
						widgetColumnSize={widgetColumnSize}
						labelColumnSize={labelColumnSize}
						fieldDetailsColumnSize={fieldDetailsColumnSize}
						errorMessages={alleleState.errorMessages}
					/>

					<Divider />

					<BooleanFormEditor
						value={alleleState.allele?.obsolete}
						name={'obsolete'}
						label={'Obsolete'}
						onValueChange={onObsoleteValueChange}
						widgetColumnSize={widgetColumnSize}
						labelColumnSize={labelColumnSize}
						fieldDetailsColumnSize={fieldDetailsColumnSize}
						errorMessages={alleleState.errorMessages}
					/>
					<Divider />
				</form>
			</ErrorBoundary>
		</>
	);
}
