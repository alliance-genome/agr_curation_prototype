import { AutocompleteEditor } from '../Autocomplete/AutocompleteEditor';
import { SearchService } from '../../service/SearchService';
import { autocompleteSearch, buildAutocompleteFilter } from '../../utils/utils';
import { SubjectAutocompleteTemplate } from '../Autocomplete/SubjectAutocompleteTemplate';
import { DialogErrorMessageComponent } from '../Error/DialogErrorMessageComponent';
import { getIdentifier } from '../../utils/utils';

const geneSearch = (event, setFiltered, setInputValue) => {
	const searchService = new SearchService();
	const autocompleteFields = [
		'curie',
		'primaryExternalId',
		'crossReferences.referencedCurie',
		'geneFullName.formatText',
		'geneFullName.displayText',
		'geneSymbol.formatText',
		'geneSymbol.displayText',
		'geneSynonyms.formatText',
		'geneSynonyms.displayText',
		'geneSystematicName.formatText',
		'geneSystematicName.displayText',
		'geneSecondaryIds.secondaryId',
	];
	const endpoint = 'gene';
	const filterName = 'objectFilter';
	const filter = buildAutocompleteFilter(event, autocompleteFields);

	setInputValue(event.query);
	autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
};

export const GeneEditor = ({ props, errorMessages, onChange, dataKey, fieldName }) => {
	return (
		<>
			<AutocompleteEditor
				search={geneSearch}
				initialValue={getIdentifier(props?.rowData?.[fieldName])}
				rowProps={props}
				fieldName={fieldName}
				subField="primaryExternalId"
				valueDisplay={(item, setAutocompleteHoverItem, op, query) => (
					<SubjectAutocompleteTemplate
						item={item}
						setAutocompleteHoverItem={setAutocompleteHoverItem}
						op={op}
						query={query}
					/>
				)}
				onValueChangeHandler={onChange}
			/>
			<DialogErrorMessageComponent errorMessages={errorMessages[dataKey]} errorField={fieldName} />
		</>
	);
};
