import { getRefString } from '../../../utils/utils';
import { Tooltip } from 'primereact/tooltip';

export const SingleReferenceTemplate = ({ singleReference }) => {
	if (!singleReference) return null;

	const targetClass = `a${global.crypto.randomUUID()}`;

	let refString = getRefString(singleReference);

	return (
		<>
			<div
				className={`overflow-hidden text-overflow-ellipsis ${targetClass}`}
				dangerouslySetInnerHTML={{
					__html: refString,
				}}
			/>
			<Tooltip target={`.${targetClass}`} mouseTrack position="bottom">
				<div
					dangerouslySetInnerHTML={{
						__html: refString,
					}}
				/>
			</Tooltip>
		</>
	);
};
