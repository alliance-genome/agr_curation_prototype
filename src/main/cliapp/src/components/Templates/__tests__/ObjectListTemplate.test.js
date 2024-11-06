import { render, screen } from '@testing-library/react';
import { ObjectListTemplate } from '../ObjectListTemplate';
import '../../../tools/jest/setupTests';
import { BrowserRouter } from 'react-router-dom/cjs/react-router-dom.min';

describe('ObjectListTemplate', () => {
	it('should render a list of items using the provided stringTemplate with object list', () => {
		const list = [
			{ id: 1, name: 'item1' },
			{ id: 2, name: 'item2' },
		];
		const stringTemplate = (item) => `Template: ${item.name}`;
		const sortMethod = (list) => list.sort((a, b) => a.name - b.name);

		render(<ObjectListTemplate list={list} sortMethod={sortMethod} stringTemplate={stringTemplate} />);

		const renderedListItems = screen.getAllByRole('listitem');
		const firstItemText = renderedListItems[0].textContent;
		const secondItemText = renderedListItems[1].textContent;

		expect(renderedListItems.length).toBe(2);
		expect(firstItemText).toBe('Template: item1');
		expect(secondItemText).toBe('Template: item2');
	});

	it('should return null when list is empty or null', () => {
		const { container: container1 } = render(<ObjectListTemplate list={null} />);
		expect(container1.firstChild).toBeNull();

		const { container: container2 } = render(<ObjectListTemplate list={[]} />);
		expect(container2.firstChild).toBeNull();
	});

	it('should apply custom sort method to the list when provided', () => {
		const list = [
			{ id: 2, name: 'Alice' },
			{ id: 1, name: 'Bob' },
		];
		const sortMethod = (list) => list.sort((a, b) => a.id - b.id);
		const stringTemplate = (item) => item.name;

		render(
			<ObjectListTemplate list={list} sortMethod={sortMethod} stringTemplate={stringTemplate} showBullets={true} />
		);

		// Assert the order of items in the rendered list after custom sorting
		const renderedListItems = screen.getAllByRole('listitem');
		expect(renderedListItems[0]).toHaveTextContent('Bob');
		expect(renderedListItems[1]).toHaveTextContent('Alice');
	});

	it('should render in original order when no sort method is provided', () => {
		const list = [
			{ id: 2, name: 'B' },
			{ id: 1, name: 'A' },
		];
		const stringTemplate = (item) => item.name;

		const result = render(<ObjectListTemplate list={list} stringTemplate={stringTemplate} />);

		const renderedListItems = screen.getAllByRole('listitem');
		const firstItemText = renderedListItems[0].textContent;
		const secondItemText = renderedListItems[1].textContent;

		expect(firstItemText).toBe('B');
		expect(secondItemText).toBe('A');
	});

	it('should handle null fields that are accessed by string template', () => {
		const list = [{ id: 2 }, { id: 1, name: 'A' }];
		const stringTemplate = (item) => item.name;

		render(<ObjectListTemplate list={list} stringTemplate={stringTemplate} />);

		const renderedListItems = screen.getAllByRole('listitem');
		const firstItemText = renderedListItems[0].textContent;
		const secondItemText = renderedListItems[1].textContent;

		expect(firstItemText).toBe('');
		expect(secondItemText).toBe('A');
	});
});
