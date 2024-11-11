import { within, fireEvent, render } from '@testing-library/react';
import { GenomicEntityListTemplate } from '../genomicEntity/GenomicEntityListTemplate';
import '../../../tools/jest/setupTests';

describe('GenomicEntityListTemplate', () => {
	it('should render a list of genomic entities with their corresponding curie', () => {
		const genomicEntities = [
			{ alleleFullName: { displayText: 'Allele Full Name 1' }, curie: 'CURIE1' },
			{ alleleFullName: { displayText: 'Allele Full Name 2' }, curie: 'CURIE2' },
			{ alleleFullName: { displayText: 'Allele Full Name 3' }, curie: 'CURIE3' },
		];

		const result = render(<GenomicEntityListTemplate genomicEntities={genomicEntities} />);

		const genomicEntity1 = result.getByText('Allele Full Name 1 (CURIE1)');
		const genomicEntity2 = result.getByText('Allele Full Name 2 (CURIE2)');
		const genomicEntity3 = result.getByText('Allele Full Name 3 (CURIE3)');

		expect(genomicEntity1).toBeInTheDocument();
		expect(genomicEntity2).toBeInTheDocument();
		expect(genomicEntity3).toBeInTheDocument();
	});

	it('should sort the genomic entities alphabetically by their display text', () => {
		const genomicEntities = [
			{ alleleSymbol: { displayText: 'Allele Symbol C' }, curie: 'CURIE1' },
			{ alleleSymbol: { displayText: 'Allele Symbol A' }, curie: 'CURIE2' },
			{ alleleSymbol: { displayText: 'Allele Symbol B' }, curie: 'CURIE3' },
		];

		const result = render(<GenomicEntityListTemplate genomicEntities={genomicEntities} />);

		const genomicEntityList = result.getByRole('list');
		const genomicEntityItems = within(genomicEntityList).getAllByRole('listitem');

		expect(genomicEntityItems[0]).toHaveTextContent('Allele Symbol A (CURIE2)');
		expect(genomicEntityItems[1]).toHaveTextContent('Allele Symbol B (CURIE3)');
		expect(genomicEntityItems[2]).toHaveTextContent('Allele Symbol C (CURIE1)');
	});

	it('should return null if genomic entities array is empty', () => {
		const { container } = render(<GenomicEntityListTemplate genomicEntities={[]} />);
		expect(container.firstChild).toBeNull();
	});

	it('should return null if genomic entities array is undefined', () => {
		const { container } = render(<GenomicEntityListTemplate genomicEntities={undefined} />);
		expect(container.firstChild).toBeNull();
	});

	it('should return the genomic entity curie if no display text is available', () => {
		const genomicEntities = [{ curie: 'CURIE1' }, { curie: 'CURIE2' }, { curie: 'CURIE3' }];

		const result = render(<GenomicEntityListTemplate genomicEntities={genomicEntities} />);

		const genomicEntity1 = result.getByText('CURIE1');
		const genomicEntity2 = result.getByText('CURIE2');
		const genomicEntity3 = result.getByText('CURIE3');

		expect(genomicEntity1).toBeInTheDocument();
		expect(genomicEntity2).toBeInTheDocument();
		expect(genomicEntity3).toBeInTheDocument();
	});

	it('should handle genomic entities with missing or null properties', () => {
		const genomicEntities = [
			{ geneSymbol: null, curie: 'CURIE1' },
			{ geneSymbol: { displayText: 'Gene Symbol 2' }, curie: 'CURIE2' },
			{ geneSymbol: { displayText: 'Gene Symbol 3' }, curie: null },
		];

		const result = render(<GenomicEntityListTemplate genomicEntities={genomicEntities} />);

		const genomicEntity1 = result.getByText('CURIE1');
		const genomicEntity2 = result.getByText('Gene Symbol 2 (CURIE2)');
		const genomicEntity3 = result.getByText('Gene Symbol 3');

		expect(genomicEntity1).toBeInTheDocument();
		expect(genomicEntity2).toBeInTheDocument();
		expect(genomicEntity3).toBeInTheDocument();
	});

	it('should handle genomic entities with missing or null properties', () => {
		const genomicEntities = [
			{ alleleFullName: null, curie: 'CURIE1' },
			{ alleleFullName: { displayText: 'Allele Full Name 2' }, curie: null },
			{ alleleFullName: null, curie: null },
		];

		const result = render(<GenomicEntityListTemplate genomicEntities={genomicEntities} />);

		const genomicEntity1 = result.getByText('CURIE1');
		const genomicEntity2 = result.getByText('Allele Full Name 2');
		const genomicEntity3 = result.queryByText('Allele Full Name 3');

		expect(genomicEntity1).toBeInTheDocument();
		expect(genomicEntity2).toBeInTheDocument();
		expect(genomicEntity3).toBeNull();
	});

	it('should handle genomic entities with missing or null display text', () => {
		const genomicEntities = [
			{ alleleFullName: { displayText: null }, curie: 'CURIE1' },
			{ alleleFullName: { displayText: null }, curie: 'CURIE2' },
			{ alleleFullName: { displayText: null }, curie: 'CURIE3' },
		];

		const result = render(<GenomicEntityListTemplate genomicEntities={genomicEntities} />);

		const genomicEntity1 = result.getByText('CURIE1');
		const genomicEntity2 = result.getByText('CURIE2');
		const genomicEntity3 = result.getByText('CURIE3');

		expect(genomicEntity1).toBeInTheDocument();
		expect(genomicEntity2).toBeInTheDocument();
		expect(genomicEntity3).toBeInTheDocument();
	});

	it('should handle genomic entities with missing or null alleleFullName', () => {
		const genomicEntities = [
			{ alleleFullName: null, curie: 'CURIE1' },
			{ alleleFullName: null, curie: 'CURIE2' },
			{ alleleFullName: null, curie: 'CURIE3' },
		];

		const result = render(<GenomicEntityListTemplate genomicEntities={genomicEntities} />);

		const genomicEntity1 = result.getByText('CURIE1');
		const genomicEntity2 = result.getByText('CURIE2');
		const genomicEntity3 = result.getByText('CURIE3');

		expect(genomicEntity1).toBeInTheDocument();
		expect(genomicEntity2).toBeInTheDocument();
		expect(genomicEntity3).toBeInTheDocument();
	});

	it('should display symbol before full name', () => {
		const genomicEntities = [
			{
				geneSymbol: {
					displayText: 'Gene Symbol 1',
				},
				geneFullName: {
					displayText: 'Gene Full Name 1',
				},
				curie: 'ID1',
			},
			{
				geneSymbol: null,
				geneFullName: {
					displayText: 'Gene Full Name 2',
				},
				modEntityId: 'ID2',
			},
			{
				geneSymbol: {
					displayText: 'Gene Symbol 3',
				},
				geneFullName: null,
				modInternalId: 'ID3',
			},
		];

		const result = render(<GenomicEntityListTemplate genomicEntities={genomicEntities} />);

		const genomicEntity1 = result.getByText('Gene Symbol 1 (ID1)');
		const genomicEntity2 = result.getByText('Gene Full Name 2 (ID2)');
		const genomicEntity3 = result.getByText('Gene Symbol 3 (ID3)');

		expect(genomicEntity1).toBeInTheDocument();
		expect(genomicEntity2).toBeInTheDocument();
		expect(genomicEntity3).toBeInTheDocument();
	});
});
