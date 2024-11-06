package org.alliancegenome.curation_api.services.helpers;

import org.alliancegenome.curation_api.model.entities.Annotation;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.model.entities.base.SubmittedObject;
import org.alliancegenome.curation_api.model.ingest.dto.AnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.DiseaseAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.base.SubmittedObjectDTO;
import org.apache.commons.lang3.StringUtils;

public class UniqueIdentifierHelper {

	private UniqueIdentifierHelper() {
	}

	public static <E extends AnnotationDTO> String getIdentifyingField(E annotationDTO) {
		if (StringUtils.isNotBlank(annotationDTO.getModEntityId())) {
			return "modEntityId";
		} else if (StringUtils.isNotBlank(annotationDTO.getModInternalId())) {
			return "modInternalId";
		} else {
			return "uniqueId";
		}
	}

	public static void setObsoleteAndInternal(DiseaseAnnotationDTO dto, AuditedObject annotation) {
		// default obsolete value: false
		annotation.setObsolete(dto.getObsolete() != null && dto.getObsolete());
		// default internal value: false
		annotation.setInternal(dto.getInternal() != null && dto.getInternal());
	}

	public static <E extends AnnotationDTO, F extends Annotation> String setAnnotationID(E annotationDTO, F annotation, String uniqueId) {
		if (StringUtils.isNotBlank(annotationDTO.getModEntityId())) {
			annotation.setModEntityId(annotationDTO.getModEntityId());
			return annotationDTO.getModEntityId();
		} else if (StringUtils.isNotBlank(annotationDTO.getModInternalId())) {
			annotation.setModInternalId(annotationDTO.getModInternalId());
			return annotationDTO.getModInternalId();
		} else {
			return uniqueId;
		}
	}


	public static <E extends SubmittedObjectDTO> String getIdentifyingField(E submittedObjectDto) {
		if (StringUtils.isNotBlank(submittedObjectDto.getModEntityId())) {
			return "modEntityId";
		} else if (StringUtils.isNotBlank(submittedObjectDto.getModInternalId())) {
			return "modInternalId";
		} else {
			return "uniqueId";
		}
	}

	public static void setObsoleteAndInternal(SubmittedObjectDTO dto, SubmittedObject submittedObject) {
		// default obsolete value: false
		submittedObject.setObsolete(dto.getObsolete() != null && dto.getObsolete());
		// default internal value: false
		submittedObject.setInternal(dto.getInternal() != null && dto.getInternal());
	}

	public static <E extends SubmittedObjectDTO, F extends SubmittedObject> String setAnnotationID(E submittedObjectDTO, F submittedObject, String uniqueId) {
		if (StringUtils.isNotBlank(submittedObjectDTO.getModEntityId())) {
			submittedObject.setModEntityId(submittedObjectDTO.getModEntityId());
			return submittedObjectDTO.getModEntityId();
		} else if (StringUtils.isNotBlank(submittedObjectDTO.getModInternalId())) {
			submittedObject.setModInternalId(submittedObjectDTO.getModInternalId());
			return submittedObjectDTO.getModInternalId();
		} else {
			return uniqueId;
		}
	}

}
