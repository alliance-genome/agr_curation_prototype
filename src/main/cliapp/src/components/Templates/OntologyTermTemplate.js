import { Tooltip } from 'primereact/tooltip';
import { EllipsisTableCell } from '../EllipsisTableCell';

export const OntologyTermTemplate = ({ term }) => {
	if (!term) return null;

	const targetClass = `a${global.crypto.randomUUID()}`;
	const textString = getTextString(term);

	return (
		<>
			<EllipsisTableCell otherClasses={targetClass}>{textString}</EllipsisTableCell>
			<Tooltip target={`.${targetClass}`} content={textString} mouseTrack position="bottom" />
		</>
	);
};

const getTextString = (term) => {
	if (!term.name) return term.curie;
	if (!term.curie) return term.name;
	if (!term.curie && !term.name) return '';
	return `${term.name} (${term.curie})`;
};
