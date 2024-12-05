package org.alliancegenome.curation_api.services.helpers.annotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptorPage;
import org.alliancegenome.curation_api.services.CrossReferenceService;
import org.alliancegenome.curation_api.services.ResourceDescriptorPageService;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class GenePhenotypeAnnotationXrefHelper {

	@Inject ResourceDescriptorPageService rdpService;
	@Inject CrossReferenceService xrefService;
	@Inject GeneDAO geneDAO;
	
	@Transactional
	public Gene addGenePhenotypeCrossReference(BackendBulkDataProvider dataProvider, Gene gene) {
	
		if (Objects.equals("HUMAN", dataProvider.name()) || gene.getIdentifier().startsWith("HGNC:")) {
			return gene;
		}

		CrossReference xref = new CrossReference();
		
		String[] geneCurieParts = gene.getIdentifier().split(":");
		String prefix = geneCurieParts[0];
		String pageName = Objects.equals("MGI", prefix) ? "gene_phenotypes_impc" : "gene/phenotypes";
		
		ResourceDescriptorPage rdp = rdpService.getPageForResourceDescriptor(prefix, pageName);
		xref.setDisplayName(prefix);
		xref.setReferencedCurie(gene.getIdentifier());
		xref.setResourceDescriptorPage(rdp);
		
		List<CrossReference> updatedXrefs = xrefService.getUpdatedXrefList(List.of(xref), gene.getCrossReferences());
		
		if (gene.getCrossReferences() != null) {
			gene.getCrossReferences().clear();
		}
		if (updatedXrefs != null) {
			if (gene.getCrossReferences() == null) {
				gene.setCrossReferences(new ArrayList<>());
			}
			gene.getCrossReferences().addAll(updatedXrefs);
		}
		
		return geneDAO.persist(gene);
	}

}
