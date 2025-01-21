package org.alliancegenome.curation_api.services.helpers.crossReferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	ResourceDescriptorPage ncbiGeneOtherExpressionPage;
	ResourceDescriptorPage ncbiGeneBiogridPage;
	Map<String, ResourceDescriptorPage> expressionAtlasPageMap = new HashMap<>();
	
	@Transactional
	public Gene addGeoCrossReference(Gene gene, String entrezCurie) {
	
		CrossReference xref = new CrossReference();
		
		if (ncbiGeneOtherExpressionPage == null) {
			ncbiGeneOtherExpressionPage = rdpService.getPageForResourceDescriptor("NCBI_Gene", "gene/other_expression");
			if (ncbiGeneOtherExpressionPage == null) {
				return null;
			}
		}
		xref.setDisplayName("GEO");
		xref.setReferencedCurie(entrezCurie);
		xref.setResourceDescriptorPage(ncbiGeneOtherExpressionPage);
		
		List<CrossReference> updatedXrefs = xrefService.getUpdatedXrefList(List.of(xref), gene.getCrossReferences(), true);
		
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

	@Transactional
	public Gene addBiogridCrossReference(Gene gene, String entrezCurie) {
	
		CrossReference xref = new CrossReference();
		
		if (ncbiGeneBiogridPage == null) {
			ncbiGeneBiogridPage = rdpService.getPageForResourceDescriptor("NCBI_Gene", "biogrid/orcs");
			if (ncbiGeneBiogridPage == null) {
				return null;
			}
		}
		xref.setDisplayName("BioGRID CRISPR Screen Cell Line Phenotypes");
		xref.setReferencedCurie(entrezCurie);
		xref.setResourceDescriptorPage(ncbiGeneBiogridPage);
		
		List<CrossReference> updatedXrefs = xrefService.getUpdatedXrefList(List.of(xref), gene.getCrossReferences(), true);
		
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
	
	@Transactional
	public Gene addExpressionAtlasXref(Gene gene, String resourceDescriptorPrefix, String referencedCurie) {
	
		CrossReference xref = new CrossReference();
		
		if (!expressionAtlasPageMap.containsKey(resourceDescriptorPrefix)) {
			ResourceDescriptorPage rdp = rdpService.getPageForResourceDescriptor(resourceDescriptorPrefix, "expression_atlas");
			if (rdp == null) {
				return null;
			}
			expressionAtlasPageMap.put(resourceDescriptorPrefix, rdp);
		}
		xref.setDisplayName("Expression Atlas");
		xref.setReferencedCurie(referencedCurie);
		xref.setResourceDescriptorPage(expressionAtlasPageMap.get(resourceDescriptorPrefix));
		
		List<CrossReference> updatedXrefs = xrefService.getUpdatedXrefList(List.of(xref), gene.getCrossReferences(), true);
		
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
