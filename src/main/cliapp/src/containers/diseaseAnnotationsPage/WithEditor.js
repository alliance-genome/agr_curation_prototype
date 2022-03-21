import React, { useState, useRef } from 'react';
import { AutoComplete } from "primereact/autocomplete";
import { trimWhitespace } from '../../utils/utils';
import { WithTooltip } from './WithTooltip';

export const WithEditor = ({ rowProps, searchService, autocompleteFields }) => {
  const [filteredWithGenes, setFilteredWithGenes] = useState([]);
  const [query, setQuery] = useState();
  const [fieldValue, setFieldValue] = useState(rowProps.rowData.with);

  const op = useRef(null);
  const [autocompleteSelectedItem, setAutocompleteSelectedItem] = useState({});

  const searchWithGenes = (event) => {
    setQuery(event.query);
    let withFilter = {};
    autocompleteFields.forEach(field => {
      withFilter[field] = {
        queryString: event.query,
        tokenOperator: "AND"
      }
    });

    searchService.search("gene", 15, 0, null, { "withFilter": withFilter })
      .then((data) => {
        if (data.results) {
          setFilteredWithGenes(data.results.filter((gene) => Boolean(gene.curie.startsWith("HGNC:"))));
        }
        else {
          setFilteredWithGenes([]);
        }
      });
  };

  const onWithEditorValueChange = (event) => {//this should propably be generalized so that all of these editor value changes can use the same method
    let updatedAnnotations = [...rowProps.props.value];
    if (event.value) {
      updatedAnnotations[rowProps.rowIndex].with = event.value;
    }
    setFieldValue(updatedAnnotations[rowProps.rowIndex].with);
  };

  const onSelectionOver = (event, item) => {
    setAutocompleteSelectedItem(item);
    op.current.show(event);
  };

  const withItemTemplate = (item) => {
    let inputValue = trimWhitespace(query.toLowerCase());
    if (autocompleteSelectedItem.synonyms && autocompleteSelectedItem.synonyms.length > 0) {
      for (let i in autocompleteSelectedItem.synonyms) {
        if (autocompleteSelectedItem.synonyms[i].name.toString().toLowerCase().indexOf(inputValue) < 0) {
          delete autocompleteSelectedItem.synonyms[i];
        }
      }
    }
    if (autocompleteSelectedItem.crossReferences && autocompleteSelectedItem.crossReferences.length > 0) {
      for (let i in autocompleteSelectedItem.crossReferences) {
        if (autocompleteSelectedItem.crossReferences[i].curie.toString().toLowerCase().indexOf(inputValue) < 0) {
          delete autocompleteSelectedItem.crossReferences[i];
        }
      }
    }

    if (autocompleteSelectedItem.secondaryIdentifiers && autocompleteSelectedItem.secondaryIdentifiers.length > 0) {
      for (let i in autocompleteSelectedItem.secondaryIdentifiers) {
        if (autocompleteSelectedItem.secondaryIdentifiers[i].toString().toLowerCase().indexOf(inputValue) < 0) {
          delete autocompleteSelectedItem.secondaryIdentifiers[i];
        }
      }
    }

    if (item.symbol) {
      return (
        <div>
          <div onMouseOver={(event) => onSelectionOver(event, item)} dangerouslySetInnerHTML={{ __html: item.symbol + ' (' + item.curie + ') ' }} />
        </div>
      );
    } else if (item.name) {
      return (
        <div>
          <div onMouseOver={(event) => onSelectionOver(event, item)} dangerouslySetInnerHTML={{ __html: item.name + ' (' + item.curie + ') ' }} />
        </div>
      );
    } else {
      return (
        <div>
          <div onMouseOver={(event) => onSelectionOver(event, item)}>{item.curie}</div>
        </div>
      );
    }
  };


  return (
    <div>
      <AutoComplete
        multiple
        value={fieldValue}
        field="curie"
        panelStyle={{ width: '15%', display: 'flex', maxHeight: '350px' }}
        suggestions={filteredWithGenes}
        itemTemplate={withItemTemplate}
        completeMethod={searchWithGenes}
        onHide={(e) => op.current.hide(e)}
        onChange={(e) => onWithEditorValueChange(e)}
      />
      <WithTooltip op={op} autocompleteSelectedItem={autocompleteSelectedItem} inputValue={trimWhitespace(query ? query.toLowerCase() : '')}
      />
    </div>
  )
};
