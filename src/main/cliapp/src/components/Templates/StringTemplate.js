import React from 'react';
import { Tooltip } from 'primereact/tooltip';
import './styles.scss';

export const StringTemplate = ({ string }) => {
	const targetClass = `a${global.crypto.randomUUID()}`;
	if (!string) return null;
	return (
		<>
			<div
				className={`overflow-hidden text-overflow-ellipsis ${targetClass}`}
				dangerouslySetInnerHTML={{ __html: string }}
			/>
			<Tooltip target={`.${targetClass}`} className="tooltip" mouseTrack position="bottom">
				<div dangerouslySetInnerHTML={{ __html: string }} />
			</Tooltip>
		</>
	);
};
