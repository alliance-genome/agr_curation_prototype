import { ListTableCell } from '../../ListTableCell';
import { getGenomicEntityText, getIdentifier } from '../../../utils/utils';
import { Tooltip } from 'primereact/tooltip';

export const GenomicEntityListTemplate = ({ genomicEntities }) => {
	if (!genomicEntities || genomicEntities.length === 0) return null;

	const targetClass = `a${global.crypto.randomUUID()}`;

	const genomicEntityStrings = genomicEntities.map((genomicEntity) => {
		const text = getGenomicEntityText(genomicEntity);
		const indentifier = getIdentifier(genomicEntity);

		if (!text) return indentifier;
		if (!indentifier) return text;
		return `${text} (${indentifier})`;
	});

	const sortedGenomicEntityStrings = genomicEntityStrings.sort();

	const listTemplate = (genomicEntityString) => {
		return (
			<div
				className="overflow-hidden text-overflow-ellipsis"
				dangerouslySetInnerHTML={{ __html: genomicEntityString }}
			/>
		);
	};

	return (
		<>
			<div className={`-my-4 p-1 ${targetClass}`}>
				<ListTableCell template={listTemplate} listData={sortedGenomicEntityStrings} />
			</div>
			<Tooltip target={`.${targetClass}`} mouseTrack position="bottom">
				<ListTableCell template={listTemplate} listData={sortedGenomicEntityStrings} />
			</Tooltip>
		</>
	);
};
