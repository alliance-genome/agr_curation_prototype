import { render, fireEvent } from '@testing-library/react';
import { IdTemplate } from '../IdTemplate';
import '../../../tools/jest/setupTests';

describe('IdTemplate', () => {
	it('should return null when id is falsy', () => {
		const { container } = render(<IdTemplate id={null} />);
		expect(container.firstChild).toBeNull();
	});

	it('should handle id as number', () => {
		const { getByText } = render(<IdTemplate id={123} />);
		expect(getByText('123')).toBeInTheDocument();
	});

	it('should handle id as string', () => {
		const { getByText } = render(<IdTemplate id="abc" />);
		expect(getByText('abc')).toBeInTheDocument();
	});
});
