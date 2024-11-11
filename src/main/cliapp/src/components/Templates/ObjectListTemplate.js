import { EllipsisTableCell } from '../EllipsisTableCell';
import { ListTableCell } from '../ListTableCell';
import { Tooltip } from 'primereact/tooltip';

export const ObjectListTemplate = ({ list, sortMethod, stringTemplate, showBullets = false }) => {
	if (!list || list.length === 0) return null;

	const targetClass = `a${global.crypto.randomUUID()}`;

	const sortedList = sortMethod ? sortMethod(list) : list;

	const listTemplate = (item) => {
		const template = stringTemplate(item);
		return template ? <EllipsisTableCell>{template}</EllipsisTableCell> : null;
	};

	return (
		<>
			<div className={`-my-2 p-1 ${targetClass}`}>
				<ListTableCell template={listTemplate} listData={sortedList} showBullets={showBullets} />
			</div>
			<Tooltip target={`.${targetClass}`} className="tooltip" mouseTrack position="bottom">
				<ListTableCell template={listTemplate} listData={sortedList} />
			</Tooltip>
		</>
	);
};
