import React from 'react';
import { AutocompleteFormMultiEditor } from '../../Autocomplete/AutocompleteFormMultiEditor';
import { LiteratureAutocompleteTemplate } from '../../Autocomplete/LiteratureAutocompleteTemplate';
import { FormErrorMessageComponent } from '../../Error/FormErrorMessageComponent';
import { FormFieldWrapper } from '../../FormFieldWrapper';
import { referenceSearch } from './utils';

export const ReferencesFormEditor = ({
	references,
	onReferencesValueChange,
	widgetColumnSize,
	labelColumnSize,
	fieldDetailsColumnSize,
	errorMessages,
}) => {
	return (
		<>
			<FormFieldWrapper
				labelColumnSize={labelColumnSize}
				fieldDetailsColumnSize={fieldDetailsColumnSize}
				widgetColumnSize={widgetColumnSize}
				fieldName="References"
				formField={
					<AutocompleteFormMultiEditor
						search={referenceSearch}
						initialValue={references}
						fieldName="references"
						valueDisplay={(item, setAutocompleteHoverItem, op, query) => (
							<LiteratureAutocompleteTemplate
								item={item}
								setAutocompleteHoverItem={setAutocompleteHoverItem}
								op={op}
								query={query}
							/>
						)}
						onValueChangeHandler={onReferencesValueChange}
					/>
				}
				errorField={<FormErrorMessageComponent errorMessages={errorMessages} errorField={'references'} />}
				additionalDataField={null}
			/>
		</>
	);
};
