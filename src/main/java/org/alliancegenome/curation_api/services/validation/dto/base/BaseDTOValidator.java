package org.alliancegenome.curation_api.services.validation.dto.base;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.CrossReferenceDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.model.entities.BiologicalEntity;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.GenomicEntity;
import org.alliancegenome.curation_api.model.entities.Organization;
import org.alliancegenome.curation_api.model.entities.Person;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.model.entities.ontology.MITerm;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.ingest.dto.BiologicalEntityDTO;
import org.alliancegenome.curation_api.model.ingest.dto.CrossReferenceDTO;
import org.alliancegenome.curation_api.model.ingest.dto.DataProviderDTO;
import org.alliancegenome.curation_api.model.ingest.dto.GenomicEntityDTO;
import org.alliancegenome.curation_api.model.ingest.dto.NoteDTO;
import org.alliancegenome.curation_api.model.ingest.dto.base.AuditedObjectDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.CrossReferenceService;
import org.alliancegenome.curation_api.services.OrganizationService;
import org.alliancegenome.curation_api.services.PersonService;
import org.alliancegenome.curation_api.services.helpers.interactions.InteractionStringHelper;
import org.alliancegenome.curation_api.services.ontology.MiTermService;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.alliancegenome.curation_api.services.validation.dto.CrossReferenceDTOValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class BaseDTOValidator {

	@Inject PersonService personService;
	@Inject NcbiTaxonTermService ncbiTaxonTermService;
	@Inject OrganizationService organizationService;
	@Inject CrossReferenceDTOValidator crossReferenceDtoValidator;
	@Inject CrossReferenceDAO crossReferenceDAO;
	@Inject CrossReferenceService crossReferenceService;
	@Inject MiTermService miTermService;

	protected HashMap<String, String> miCurieCache = new HashMap<>();
	protected HashMap<String, MITerm> miTermCache = new HashMap<>();

	protected String getCurieFromCache(String psiMiFormat) {
		if (miCurieCache.containsKey(psiMiFormat)) {
			return miCurieCache.get(psiMiFormat);
		} else {
			String curie = InteractionStringHelper.extractCurieFromPsiMiFormat(psiMiFormat);
			if (curie != null) {
				miCurieCache.put(psiMiFormat, curie);
				return curie;
			}
		}
		return null;
	}

	protected MITerm getTermFromCache(String curie) {
		if (miTermCache.containsKey(curie)) {
			return miTermCache.get(curie);
		} else {
			if (curie != null) {
				MITerm miTerm = miTermService.findByCurie(curie);
				if (miTerm != null) {
					miTermCache.put(curie, miTerm);
					return miTerm;
				}
			}
		}
		return null;
	}

	public <E extends AuditedObject, D extends AuditedObjectDTO> ObjectResponse<E> validateAuditedObjectDTO(E entity, D dto) {

		ObjectResponse<E> response = new ObjectResponse<E>();

		Person createdBy = null;
		if (StringUtils.isNotBlank(dto.getCreatedByCurie())) {
			createdBy = personService.fetchByUniqueIdOrCreate(dto.getCreatedByCurie());
		}
		entity.setCreatedBy(createdBy);

		Person updatedBy = null;
		if (StringUtils.isNotBlank(dto.getUpdatedByCurie())) {
			updatedBy = personService.fetchByUniqueIdOrCreate(dto.getUpdatedByCurie());
		}
		entity.setUpdatedBy(updatedBy);

		Boolean internal = dto instanceof NoteDTO;
		if (dto.getInternal() != null) {
			internal = dto.getInternal();
		}
		entity.setInternal(internal);

		Boolean obsolete = false;
		if (dto.getObsolete() != null) {
			obsolete = dto.getObsolete();
		}
		entity.setObsolete(obsolete);

		OffsetDateTime dateUpdated = null;
		if (StringUtils.isNotBlank(dto.getDateUpdated())) {
			try {
				dateUpdated = OffsetDateTime.parse(dto.getDateUpdated());
			} catch (DateTimeParseException e) {
				response.addErrorMessage("date_updated", ValidationConstants.INVALID_MESSAGE + " (" + dto.getDateUpdated() + ")");
			}
		}
		entity.setDateUpdated(dateUpdated);

		OffsetDateTime creationDate = null;
		if (StringUtils.isNotBlank(dto.getDateCreated())) {
			try {
				creationDate = OffsetDateTime.parse(dto.getDateCreated());
			} catch (DateTimeParseException e) {
				response.addErrorMessage("date_created", ValidationConstants.INVALID_MESSAGE + " (" + dto.getDateCreated() + ")");
			}
		}
		entity.setDateCreated(creationDate);

		response.setEntity(entity);

		return response;
	}

	public <E extends BiologicalEntity, D extends BiologicalEntityDTO> ObjectResponse<E> validateBiologicalEntityDTO(E entity, D dto, BackendBulkDataProvider beDataProvider) {

		ObjectResponse<E> beResponse = new ObjectResponse<E>();

		ObjectResponse<E> aoResponse = validateAuditedObjectDTO(entity, dto);
		beResponse.addErrorMessages(aoResponse.getErrorMessages());
		entity = aoResponse.getEntity();

		if (StringUtils.isBlank(dto.getTaxonCurie())) {
			beResponse.addErrorMessage("taxon_curie", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			ObjectResponse<NCBITaxonTerm> taxonResponse = ncbiTaxonTermService.getByCurie(dto.getTaxonCurie());
			if (taxonResponse.getEntity() == null) {
				beResponse.addErrorMessage("taxon_curie", ValidationConstants.INVALID_MESSAGE + " (" + dto.getTaxonCurie() + ")");
			}
			if (beDataProvider != null && (beDataProvider.name().equals("RGD") || beDataProvider.name().equals("HUMAN")) && !taxonResponse.getEntity().getCurie().equals(beDataProvider.canonicalTaxonCurie)) {
				beResponse.addErrorMessage("taxon_curie", ValidationConstants.INVALID_MESSAGE + " (" + dto.getTaxonCurie() + ") for " + beDataProvider.name() + " load");
			}
			entity.setTaxon(taxonResponse.getEntity());
		}

		if (dto.getDataProviderDto() == null) {
			beResponse.addErrorMessage("data_provider_dto", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			ObjectResponse<ImmutablePair<Organization, CrossReference>> dpResponse = validateDataProviderDTO(dto.getDataProviderDto(), entity.getDataProviderCrossReference());
			if (dpResponse.hasErrors()) {
				beResponse.addErrorMessage("data_provider_dto", dpResponse.errorMessagesString());
			} else {
				entity.setDataProvider(dpResponse.getEntity().getLeft());
				if (dpResponse.getEntity().getRight() != null) {
					entity.setDataProviderCrossReference(crossReferenceDAO.persist(dpResponse.getEntity().getRight()));
				} else {
					entity.setDataProviderCrossReference(null);
				}
			}
		}

		beResponse.setEntity(entity);

		return beResponse;
	}

	public <E extends GenomicEntity, D extends GenomicEntityDTO> ObjectResponse<E> validateGenomicEntityDTO(E entity, D dto, BackendBulkDataProvider dataProvider) {

		ObjectResponse<E> geResponse = new ObjectResponse<>();

		ObjectResponse<E> beResponse = validateBiologicalEntityDTO(entity, dto, dataProvider);
		geResponse.addErrorMessages(beResponse.getErrorMessages());
		entity = beResponse.getEntity();

		List<CrossReference> validatedXrefs = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(dto.getCrossReferenceDtos())) {
			for (CrossReferenceDTO xrefDto : dto.getCrossReferenceDtos()) {
				ObjectResponse<CrossReference> xrefResponse = crossReferenceDtoValidator.validateCrossReferenceDTO(xrefDto, null);
				if (xrefResponse.hasErrors()) {
					geResponse.addErrorMessage("cross_reference_dtos", xrefResponse.errorMessagesString());
					break;
				} else {
					validatedXrefs.add(xrefResponse.getEntity());
				}
			}
		}

		List<CrossReference> xrefs = crossReferenceService.getUpdatedXrefList(validatedXrefs, entity.getCrossReferences());

		if (entity.getCrossReferences() != null) {
			entity.getCrossReferences().clear();
		}
		if (xrefs != null) {
			if (entity.getCrossReferences() == null) {
				entity.setCrossReferences(new ArrayList<>());
			}
			entity.getCrossReferences().addAll(xrefs);
		}

		geResponse.setEntity(entity);

		return geResponse;
	}

	public String handleStringField(String string) {
		if (StringUtils.isNotBlank(string)) {
			return string;
		}

		return null;
	}

	public List<String> handleStringListField(List<String> list) {
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}

		return list;
	}
	
	public ObjectResponse<ImmutablePair<Organization, CrossReference>> validateDataProviderDTO(DataProviderDTO dto, CrossReference dbDataProviderXref) {
		ObjectResponse<ImmutablePair<Organization, CrossReference>> dpResponse = new ObjectResponse<ImmutablePair<Organization, CrossReference>>();

		Organization dataProvider = null;
		CrossReference dataProviderXref = null;
		if (StringUtils.isBlank(dto.getSourceOrganizationAbbreviation())) {
			dpResponse.addErrorMessage("source_organization_abbreviation", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			ObjectResponse<Organization> soResponse = organizationService.getByAbbr(dto.getSourceOrganizationAbbreviation());
			if (soResponse == null || soResponse.getEntity() == null) {
				dpResponse.addErrorMessage("source_organization_abbreviation", ValidationConstants.INVALID_MESSAGE + " (" + dto.getSourceOrganizationAbbreviation() + ")");
			} else {
				dataProvider = soResponse.getEntity();
			}
		}

		if (dto.getCrossReferenceDto() != null) {
			ObjectResponse<CrossReference> crResponse = crossReferenceDtoValidator.validateCrossReferenceDTO(dto.getCrossReferenceDto(), dbDataProviderXref);
			if (crResponse.hasErrors()) {
				dpResponse.addErrorMessage("cross_reference_dto", crResponse.errorMessagesString());
			} else {
				dataProviderXref = crResponse.getEntity();
			}
		}
		dpResponse.setEntity(new ImmutablePair<Organization, CrossReference>(dataProvider, dataProviderXref));

		return dpResponse;
	}

}
