package org.alliancegenome.curation_api.services.validation.dto;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Organization;
import org.alliancegenome.curation_api.model.entities.Reagent;
import org.alliancegenome.curation_api.model.ingest.dto.ReagentDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.apache.commons.lang3.tuple.ImmutablePair;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class ReagentDTOValidator extends AnnotationDTOValidator {

	@Inject NcbiTaxonTermService ncbiTaxonTermService;

	public <E extends Reagent, D extends ReagentDTO> ObjectResponse<E> validateReagentDTO(E reagent, D dto) {
		ObjectResponse<E> reagentResponse = validateAuditedObjectDTO(reagent, dto);
		reagent = reagentResponse.getEntity();

		reagent.setPrimaryExternalId(handleStringField(dto.getPrimaryExternalId()));
		reagent.setModInternalId(handleStringField(dto.getModInternalId()));

		reagent.setSecondaryIdentifiers(handleStringListField(dto.getSecondaryIdentifiers()));

		if (dto.getDataProviderDto() == null) {
			reagentResponse.addErrorMessage("data_provider_dto", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			ObjectResponse<ImmutablePair<Organization, CrossReference>> dpResponse = validateDataProviderDTO(dto.getDataProviderDto(), reagent.getDataProviderCrossReference());
			if (dpResponse.hasErrors()) {
				reagentResponse.addErrorMessage("data_provider_dto", dpResponse.errorMessagesString());
			} else {
				reagent.setDataProvider(dpResponse.getEntity().getLeft());
				if (dpResponse.getEntity().getRight() != null) {
					reagent.setDataProviderCrossReference(crossReferenceDAO.persist(dpResponse.getEntity().getRight()));
				} else {
					reagent.setDataProviderCrossReference(null);
				}
			}
		}

		reagentResponse.setEntity(reagent);

		return reagentResponse;
	}
}
