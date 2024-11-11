import { render } from '@testing-library/react';
import '../../../tools/jest/setupTests';
import { NumberTemplate } from '../NumberTemplate';

describe('NumberTemplate', () => {
	it('should format positive integers correctly', () => {
		const number = 12345;
		const result = NumberTemplate({ number });
		expect(result).toBe('12,345');
	});

	it('should format negative integers correctly', () => {
		const number = -1000;
		const formattedNumber = NumberTemplate({ number });

		expect(formattedNumber).toBe('-1,000');
	});

	it('should format positive floating-point numbers correctly', () => {
		const number = 1234.56;
		const formattedNumber = NumberTemplate({ number });

		expect(formattedNumber).toBe('1,234.56');
	});

	it('should format negative floating-point numbers correctly', () => {
		const number = -1234.5678;
		const formattedNumber = NumberTemplate({ number });

		expect(formattedNumber).toBe('-1,234.568');
	});

	it('should return formatted number when input is zero', () => {
		const number = 0;

		const result = NumberTemplate({ number });

		expect(result).toBe('0');
	});

	it('should handle very large numbers correctly', () => {
		const number = 1000000000000000000000000000000;

		const result = NumberTemplate({ number });

		expect(result).toBe('1,000,000,000,000,000,000,000,000,000,000');
	});

	it('should handle string representations of numbers correctly', () => {
		const number = '1000';

		const result = NumberTemplate({ number });

		expect(result).toBe('1,000');
	});

	it('should return undefined when number is null', () => {
		const number = null;
		const result = NumberTemplate({ number });
		expect(result).toBeUndefined();
	});
});
