package org.alliancegenome.curation_api.services;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.exceptions.KnownIssueValidationException;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.ingest.dto.GeneDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.associations.alleleAssociations.AlleleGeneAssociationService;
import org.alliancegenome.curation_api.services.associations.constructAssociations.ConstructGenomicEntityAssociationService;
import org.alliancegenome.curation_api.services.base.SubmittedObjectCrudService;
import org.alliancegenome.curation_api.services.helpers.crossReferences.GeneXrefHelper;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.alliancegenome.curation_api.services.orthology.GeneToGeneOrthologyService;
import org.alliancegenome.curation_api.services.validation.GeneValidator;
import org.alliancegenome.curation_api.services.validation.dto.GeneDTOValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import io.quarkus.logging.Log;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class GeneService extends SubmittedObjectCrudService<Gene, GeneDTO, GeneDAO> {

	@Inject GeneDAO geneDAO;
	@Inject GeneValidator geneValidator;
	@Inject GeneDTOValidator geneDtoValidator;
	@Inject DiseaseAnnotationService diseaseAnnotationService;
	@Inject PersonService personService;
	@Inject GeneToGeneOrthologyService orthologyService;
	@Inject AlleleGeneAssociationService alleleGeneAssociationService;
	@Inject ConstructGenomicEntityAssociationService constructGenomicEntityAssociationService;
	@Inject GeneInteractionService geneInteractionService;
	@Inject PhenotypeAnnotationService phenotypeAnnotationService;
	@Inject NcbiTaxonTermService ncbiTaxonTermService;
	@Inject GeneXrefHelper geneXrefHelper;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(geneDAO);
	}

	@Override
	@Transactional
	public ObjectResponse<Gene> update(Gene uiEntity) {
		Gene dbEntity = geneValidator.validateGeneUpdate(uiEntity);
		return new ObjectResponse<Gene>(dbEntity);
	}

	@Override
	@Transactional
	public ObjectResponse<Gene> create(Gene uiEntity) {
		Gene dbEntity = geneValidator.validateGeneCreate(uiEntity);
		return new ObjectResponse<Gene>(dbEntity);
	}

	@Override
	public Gene upsert(GeneDTO dto) throws ValidationException {
		return upsert(dto, null);
	}

	@Override
	public Gene upsert(GeneDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {
		return geneDtoValidator.validateGeneDTO(dto, dataProvider);
	}

	@Override
	@Transactional
	public ObjectResponse<Gene> deleteById(Long id) {
		deprecateOrDelete(id, true, "Gene DELETE API call", false);
		ObjectResponse<Gene> ret = new ObjectResponse<>();
		return ret;
	}

	@Override
	@Transactional
	public Gene deprecateOrDelete(Long id, Boolean throwApiError, String requestSource, Boolean forceDeprecate) {
		Gene gene = geneDAO.find(id);
		if (gene != null) {
			if (forceDeprecate || geneDAO.hasReferencingDiseaseAnnotations(id) || geneDAO.hasReferencingPhenotypeAnnotations(id)
					|| geneDAO.hasReferencingOrthologyPairs(id) || geneDAO.hasReferencingInteractions(id)
					|| CollectionUtils.isNotEmpty(gene.getAlleleGeneAssociations())
					|| CollectionUtils.isNotEmpty(gene.getConstructGenomicEntityAssociations())) {
				if (!gene.getObsolete()) {
					gene.setUpdatedBy(personService.fetchByUniqueIdOrCreate(requestSource));
					gene.setDateUpdated(OffsetDateTime.now());
					gene.setObsolete(true);
					return geneDAO.persist(gene);
				} else {
					return gene;
				}
			} else {
				geneDAO.remove(id);
			}
		} else {
			String errorMessage = "Could not find Gene with id: " + id;
			if (throwApiError) {
				ObjectResponse<Gene> response = new ObjectResponse<>();
				response.addErrorMessage("id", errorMessage);
				throw new ApiErrorException(response);
			}
			Log.error(errorMessage);
		}
		return null;
	}

	public List<Long> getIdsByDataProvider(BackendBulkDataProvider dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.DATA_PROVIDER, dataProvider.sourceOrganization);
		if (StringUtils.equals(dataProvider.sourceOrganization, "RGD")) {
			params.put(EntityFieldConstants.TAXON, dataProvider.canonicalTaxonCurie);
		}
		List<Long> ids = geneDAO.findIdsByParams(params);
		ids.removeIf(Objects::isNull);

		return ids;
	}

	@Transactional
	public void addBiogridXref(String entrezId, BackendBulkDataProvider dataProvider) throws ValidationException {
		NCBITaxonTerm taxon = ncbiTaxonTermService.getByCurie(dataProvider.canonicalTaxonCurie).getEntity();
		if (taxon == null) {
			throw new ObjectValidationException(entrezId, "dataProvider - canonical taxon: " + ValidationConstants.INVALID_MESSAGE + " (" + dataProvider.canonicalTaxonCurie + " not found)");
		}

		Gene allianceGene = null;
		SearchResponse<Gene> searchResponse = findByField("crossReferences.referencedCurie", "NCBI_Gene:" + entrezId);
		if (searchResponse != null) {
			// Need to check that returned gene belongs to MOD corresponding to taxon
			for (Gene searchResult : searchResponse.getResults()) {
				String resultDataProviderCoreGenus = BackendBulkDataProvider.getCoreGenus(searchResult.getDataProvider().getAbbreviation());
				if (taxon.getName().startsWith(resultDataProviderCoreGenus + " ")) {
					allianceGene = searchResult;
					break;
				}
				if (StringUtils.equals(taxon.getCurie(), "NCBITaxon:9606") && StringUtils.equals(searchResult.getDataProvider().getAbbreviation(), "RGD")) {
					allianceGene = searchResult;
					break;
				}
			}
		}
		
		if (allianceGene == null) {
			throw new KnownIssueValidationException("crossReferences - referencedCurie: " + ValidationConstants.INVALID_MESSAGE + " (NCBI_Gene:" + entrezId + ")");
		}
		
		allianceGene = geneXrefHelper.addBiogridCrossReference(allianceGene, "NCBI_Gene:" + entrezId);
		
		if (allianceGene == null) {
			throw new ObjectValidationException(entrezId, "resourceDescriptorPage: " + ValidationConstants.INVALID_MESSAGE + " (NCBI_Gene:biogrid/orcs)");
		}
	}

	@Transactional
	public void addGeoXref(String entrezId, BackendBulkDataProvider dataProvider) throws ValidationException {
		NCBITaxonTerm taxon = ncbiTaxonTermService.getByCurie(dataProvider.canonicalTaxonCurie).getEntity();
		if (taxon == null) {
			throw new ObjectValidationException(entrezId, "dataProvider - canonical taxon: " + ValidationConstants.INVALID_MESSAGE + " (" + dataProvider.canonicalTaxonCurie + " not found)");
		}

		Gene allianceGene = null;
		SearchResponse<Gene> searchResponse = findByField("crossReferences.referencedCurie", "NCBI_Gene:" + entrezId);
		if (searchResponse != null) {
			// Need to check that returned gene belongs to MOD corresponding to taxon
			for (Gene searchResult : searchResponse.getResults()) {
				String resultDataProviderCoreGenus = BackendBulkDataProvider.getCoreGenus(searchResult.getDataProvider().getAbbreviation());
				if (taxon.getName().startsWith(resultDataProviderCoreGenus + " ")) {
					allianceGene = searchResult;
					break;
				}
				if (StringUtils.equals(taxon.getCurie(), "NCBITaxon:9606") && StringUtils.equals(searchResult.getDataProvider().getAbbreviation(), "RGD")) {
					allianceGene = searchResult;
					break;
				}
			}
		}
		
		if (allianceGene == null) {
			throw new KnownIssueValidationException("crossReferences - referencedCurie: " + ValidationConstants.INVALID_MESSAGE + " (NCBI_Gene:" + entrezId + ")");
		}
		
		allianceGene = geneXrefHelper.addGeoCrossReference(allianceGene, "NCBI_Gene:" + entrezId);
		
		if (allianceGene == null) {
			throw new ObjectValidationException(entrezId, "resourceDescriptorPage: " + ValidationConstants.INVALID_MESSAGE + " (NCBI_Gene:gene/other_expression)");
		}
	}

	@Transactional
	public void addExpressionAtlasXref(String identifier, BackendBulkDataProvider dataProvider) throws ValidationException {
		NCBITaxonTerm taxon = ncbiTaxonTermService.getByCurie(dataProvider.canonicalTaxonCurie).getEntity();
		if (taxon == null) {
			throw new ObjectValidationException(identifier, "dataProvider - canonical taxon: " + ValidationConstants.INVALID_MESSAGE + " (" + dataProvider.canonicalTaxonCurie + " not found)");
		}

		
		String searchField;
		String searchValue;
		String referencedCurie;
		String resourceDescriptorPrefix;
		switch (dataProvider) {
			case FB -> {
				searchField = "primaryExternalId";
				searchValue = "FB:" + identifier;
				referencedCurie = searchValue;
				resourceDescriptorPrefix = "FB";
			}
			case SGD -> {
				searchField = "geneSymbol.displayText";
				searchValue = identifier;
				referencedCurie = "SGD:" + identifier;
				resourceDescriptorPrefix = "SGD";
			}
			default -> {
				searchField = "crossReferences.referencedCurie";
				searchValue = "ENSEMBL:" + identifier;
				referencedCurie = searchValue;
				resourceDescriptorPrefix = "ENSEMBL";
			}
		}
		
		Gene allianceGene = null;
		SearchResponse<Gene> searchResponse = findByField(searchField, searchValue);
		if (searchResponse != null) {
			// Need to check that returned gene belongs to MOD corresponding to taxon
			for (Gene searchResult : searchResponse.getResults()) {
				String resultDataProviderCoreGenus = BackendBulkDataProvider.getCoreGenus(searchResult.getDataProvider().getAbbreviation());
				if (taxon.getName().startsWith(resultDataProviderCoreGenus + " ")) {
					allianceGene = searchResult;
					break;
				}
				if (StringUtils.equals(taxon.getCurie(), "NCBITaxon:9606") && StringUtils.equals(searchResult.getDataProvider().getAbbreviation(), "RGD")) {
					allianceGene = searchResult;
					break;
				}
			}
		}
		
		if (allianceGene == null) {
			throw new KnownIssueValidationException(searchField + ": " + ValidationConstants.INVALID_MESSAGE + " (" + searchValue + ")");
		}
		
		allianceGene = geneXrefHelper.addExpressionAtlasXref(allianceGene, resourceDescriptorPrefix, referencedCurie);
		
		if (allianceGene == null) {
			throw new ObjectValidationException(identifier, "resourceDescriptorPage: " + ValidationConstants.INVALID_MESSAGE + " (" + resourceDescriptorPrefix + ":expression_atlas)");
		}
	}

}
