import React from 'react';

export const ListTableCell = ({ template, listData, showBullets }) => {
	let suppress = 'list-none';
	if (showBullets) suppress = '';
	return (
		<ul className={`m-1 pl-0 ${suppress}`}>
			{listData?.map((item, index) => (
				<li key={index}>{template(item)}</li>
			))}
		</ul>
	);
};
