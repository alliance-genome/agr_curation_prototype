import { render, screen } from '@testing-library/react';
import { StringListTemplate } from '../StringListTemplate';
import '../../../tools/jest/setupTests';
import { BrowserRouter } from 'react-router-dom/cjs/react-router-dom.min';

describe('StringListTemplate', () => {
	it('should render correctly when provided with a non-empty list', () => {
		const list = ['item1', 'item2', 'item3'];
		const { container } = render(<StringListTemplate list={list} />);

		expect(container.firstChild).not.toBeNull();

		const renderedListItems = screen.getAllByRole('listitem');

		expect(renderedListItems.length).toBe(list.length);
	});

	it('should return null when provided with an empty list', () => {
		const list = [];
		const { container } = render(<StringListTemplate list={list} />);

		expect(container.firstChild).toBeNull();
	});

	it('should return null when list is null', () => {
		const list = null;

		const result = StringListTemplate({ list });

		expect(result).toBeNull();
	});

	it('should handle lists with falsy elements', () => {
		const list = [null, undefined, 0, '', false, 'Apple', null, 'Banana'];

		render(<StringListTemplate list={list} />);

		const renderedListItems = screen.getAllByRole('listitem');

		expect(renderedListItems).toHaveLength(2);
		expect(renderedListItems[0].textContent).toBe('Apple');
		expect(renderedListItems[1].textContent).toBe('Banana');
	});
});
