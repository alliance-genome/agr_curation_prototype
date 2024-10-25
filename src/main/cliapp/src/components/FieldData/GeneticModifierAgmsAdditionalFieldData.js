import React from 'react';
import { getIdentifier } from '../../utils/utils';

export function GeneticModifierAgmsAdditionalFieldData({ fieldData }) {
	let ret = [];
	if (fieldData && fieldData.length > 0) {
		for (let i = 0; i < fieldData.length; i++) {
			if (getIdentifier(fieldData[i]) !== '') {
				ret.push(<div key={i} className="p-info" dangerouslySetInnerHTML={{ __html: fieldData[i]['name'] }}></div>);
			}
		}
	}
	return ret;
}
