import { AutocompleteEditor } from '../Autocomplete/AutocompleteEditor';
import { SearchService } from '../../service/SearchService';
import { autocompleteSearch, buildAutocompleteFilter } from '../../utils/utils';
import { LiteratureAutocompleteTemplate } from '../Autocomplete/LiteratureAutocompleteTemplate';
import { DialogErrorMessageComponent } from "../Error/DialogErrorMessageComponent";

const geneSearch = (event, setFiltered, setInputValue) => {
	const searchService = new SearchService();
	const autocompleteFields = [
		"curie", "crossReferences.referencedCurie", "geneFullName.formatText", "geneFullName.displayText", 
		"geneSymbol.formatText", "geneSymbol.displayText", "geneSynonyms.formatText", "geneSynonyms.displayText", 
		"geneSystematicName.formatText", "geneSystematicName.displayText", "geneSecondaryIds.secondaryId"
	];
	const endpoint = "gene";
	const filterName = "objectFilter";
	const filter = buildAutocompleteFilter(event, autocompleteFields);

	setInputValue(event.query);
	autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
}

export const GeneEditor = ({ props, errorMessages, onChange }) => {
	return (
		<>
			<AutocompleteEditor
				search={geneSearch}
				initialValue={props?.rowData?.objectGene?.curie}
				rowProps={props}
				fieldName='objectGene'
				valueDisplay={(item, setAutocompleteHoverItem, op, query) =>
					<LiteratureAutocompleteTemplate item={item} setAutocompleteHoverItem={setAutocompleteHoverItem} op={op} query={query}/>}
				onValueChangeHandler={onChange}
			/>
			<DialogErrorMessageComponent
				errorMessages={errorMessages[props?.rowIndex]}
				errorField={"objectGene"}
			/>
		</>
	);
};