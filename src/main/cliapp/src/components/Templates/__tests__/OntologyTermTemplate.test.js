import { render, fireEvent } from '@testing-library/react';
import { OntologyTermTemplate } from '../OntologyTermTemplate';
import '../../../tools/jest/setupTests';

describe('OntologyTermTemplate', () => {
	it('should render the name and curie of the disease object in an EllipsisTableCell component', () => {
		const object = {
			name: 'Disease Name',
			curie: 'Disease Curie',
		};

		const result = render(<OntologyTermTemplate term={object} />);

		const content = result.getByText('Disease Name (Disease Curie)');
		expect(content).toBeInTheDocument();
	});

	it('should return null if the object is falsy', () => {
		const result = render(<OntologyTermTemplate term={null} />);

		expect(result.container.firstChild).toBeNull();
	});

	it('should render an empty EllipsisTableCell component if the object has no name or curie', () => {
		const object = {};

		const result = render(<OntologyTermTemplate term={object} />);

		const ellipsisTableCell = result.container.firstChild;
		expect(ellipsisTableCell).toHaveTextContent('');
	});

	it('should render an EllipsisTableCell component with only the name if the object has no curie', () => {
		const object = {
			name: 'Disease Name',
		};

		const result = render(<OntologyTermTemplate term={object} />);

		const ellipsisTableCell = result.getByText('Disease Name');
		expect(ellipsisTableCell).toBeInTheDocument();
	});

	it('should render an EllipsisTableCell component with only the curie if the object has no name', () => {
		const object = {
			curie: 'Disease Curie',
		};

		const result = render(<OntologyTermTemplate term={object} />);

		const ellipsisTableCell = result.getByText('Disease Curie');
		expect(ellipsisTableCell).toBeInTheDocument();
	});
});
