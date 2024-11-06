import { Tooltip } from 'primereact/tooltip';
import { getGenomicEntityText, getIdentifier } from '../../../utils/utils';

export const GenomicEntityTemplate = ({ genomicEntity }) => {
	if (!genomicEntity) return null;

	const targetClass = `a${global.crypto.randomUUID()}`;
	const subjectText = getGenomicEntityText(genomicEntity);
	const indentifier = getIdentifier(genomicEntity);

	if (!subjectText) return <div className="overflow-hidden text-overflow-ellipsis">{indentifier}</div>;

	return (
		<>
			<div
				className={`overflow-hidden text-overflow-ellipsis ${targetClass}`}
				dangerouslySetInnerHTML={{
					__html: `${subjectText} (${indentifier})`,
				}}
			/>
			<Tooltip target={`.${targetClass}`} mouseTrack position="bottom">
				<div
					dangerouslySetInnerHTML={{
						__html: `${subjectText} (${indentifier})`,
					}}
				/>
			</Tooltip>
		</>
	);
};
