export const NumberTemplate = ({ number }) => {
	//still want to pass through falsy 0 values
	if (number === null || number === undefined) return;
	return new Intl.NumberFormat().format(number);
};
