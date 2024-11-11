import { render } from '@testing-library/react';
import { StringTemplate } from '../StringTemplate';
import '../../../tools/jest/setupTests';

describe('StringTemplate', () => {
	it('should return null when value is null', () => {
		const { container } = render(<StringTemplate string={null} />);
		expect(container.firstChild).toBeNull();
	});

	it('should render the name inside the div', () => {
		const result = render(<StringTemplate string="test" />);
		expect(result.getByText('test')).toBeInTheDocument();
	});
});
