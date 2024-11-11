package org.alliancegenome.curation_api.services.validation.dto.fms;

import org.alliancegenome.curation_api.dao.GenePhenotypeAnnotationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.GenePhenotypeAnnotation;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.ingest.dto.fms.PhenotypeFmsDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.GenomicEntityService;
import org.alliancegenome.curation_api.services.PhenotypeAnnotationService;
import org.alliancegenome.curation_api.services.helpers.annotations.AnnotationUniqueIdHelper;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class GenePhenotypeAnnotationFmsDTOValidator extends PhenotypeAnnotationFmsDTOValidator {

	@Inject GenePhenotypeAnnotationDAO genePhenotypeAnnotationDAO;
	@Inject GenomicEntityService genomicEntityService;
	@Inject PhenotypeAnnotationService phenotypeAnnotationService;

	public GenePhenotypeAnnotation validatePrimaryAnnotation(Gene subject, PhenotypeFmsDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {

		ObjectResponse<GenePhenotypeAnnotation> apaResponse = new ObjectResponse<GenePhenotypeAnnotation>();
		GenePhenotypeAnnotation annotation = new GenePhenotypeAnnotation();

		ObjectResponse<Reference> refResponse = validateReference(dto);
		apaResponse.addErrorMessages(refResponse.getErrorMessages());

		Reference reference = refResponse.getEntity();
		String refString = reference == null ? null : reference.getCurie();

		String uniqueId = AnnotationUniqueIdHelper.getPhenotypeAnnotationUniqueId(dto, subject.getIdentifier(), refString);
		SearchResponse<GenePhenotypeAnnotation> annotationSearch = genePhenotypeAnnotationDAO.findByField("uniqueId", uniqueId);
		if (annotationSearch != null && annotationSearch.getSingleResult() != null) {
			annotation = annotationSearch.getSingleResult();
		}

		annotation.setUniqueId(uniqueId);
		annotation.setSingleReference(reference);
		annotation.setPhenotypeAnnotationSubject(subject);

		ObjectResponse<GenePhenotypeAnnotation> paResponse = validatePhenotypeAnnotation(annotation, dto, dataProvider);
		apaResponse.addErrorMessages(paResponse.getErrorMessages());
		annotation = paResponse.getEntity();

		if (apaResponse.hasErrors()) {
			throw new ObjectValidationException(dto, apaResponse.errorMessagesString());
		}

		return annotation;

	}
}
