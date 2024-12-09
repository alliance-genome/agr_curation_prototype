package org.alliancegenome.curation_api.services.helpers.crossReferences;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptorPage;
import org.alliancegenome.curation_api.services.CrossReferenceService;
import org.alliancegenome.curation_api.services.ResourceDescriptorPageService;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class GeneXrefHelper {

	@Inject ResourceDescriptorPageService rdpService;
	@Inject CrossReferenceService xrefService;
	@Inject GeneDAO geneDAO;
	
	@Transactional
	public void addGeoCrossReference(Gene gene, String entrezCurie) {
	
		CrossReference xref = new CrossReference();
		
		ResourceDescriptorPage rdp = rdpService.getPageForResourceDescriptor("NCBI_Gene", "gene/other_expression");
		xref.setDisplayName("GEO");
		xref.setReferencedCurie(entrezCurie);
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
		
		geneDAO.persist(gene);
	}

}
