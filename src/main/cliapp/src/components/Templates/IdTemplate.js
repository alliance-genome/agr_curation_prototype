import { EllipsisTableCell } from '../EllipsisTableCell';
import { Tooltip } from 'primereact/tooltip';
import './styles.scss';

export const IdTemplate = ({ id }) => {
	if (!id) return null;

	//the 'a' at the start is a hack since css selectors can't start with a number
	const targetClass = `a${global.crypto.randomUUID()}`;

	return (
		<>
			<EllipsisTableCell otherClasses={targetClass}>{id}</EllipsisTableCell>
			<Tooltip target={`.${targetClass}`} content={id} className="tooltip" mouseTrack position="right" />
		</>
	);
};
