package org.alliancegenome.curation_api.services.helpers.notes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.ingest.dto.NoteDTO;
import org.alliancegenome.curation_api.model.ingest.dto.fms.PublicationRefFmsDTO;
import org.alliancegenome.curation_api.model.ingest.dto.fms.VariantNoteFmsDTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

public class NoteIdentityHelper {
	private NoteIdentityHelper() {
		// Hidden from view, as it is a utility class
	}

	public static String noteIdentity(Note note) {
		String identity = StringUtils.isBlank(note.getFreeText()) ? "" : note.getFreeText();
		if (CollectionUtils.isNotEmpty(note.getReferences())) {
			List<String> evidenceCuries = note.getReferences().stream().map(Reference::getCurie).collect(Collectors.toList());
			Collections.sort(evidenceCuries);
			identity = identity + "|" + StringUtils.join(evidenceCuries, ":");
		}
		if (note.getNoteType() != null) {
			identity = identity + "|" + note.getNoteType().getName();
		}
		if (note.getInternal() != null) {
			identity = identity + "|" + note.getInternal().toString();
		}
		if (note.getObsolete() != null) {
			identity = identity + "|" + note.getObsolete().toString();
		}

		return identity;
	}

	public static String noteDtoIdentity(NoteDTO note) {
		String identity = StringUtils.isBlank(note.getFreeText()) ? "" : note.getFreeText();
		List<String> evidenceCuries = note.getEvidenceCuries();
		if (CollectionUtils.isNotEmpty(evidenceCuries)) {
			Collections.sort(evidenceCuries);
			identity = identity + "|" + StringUtils.join(evidenceCuries, ":");
		}
		if (note.getNoteTypeName() != null) {
			identity = identity + "|" + note.getNoteTypeName();
		}
		if (note.getInternal() != null) {
			identity = identity + "|" + note.getInternal().toString();
		}
		if (note.getObsolete() != null) {
			identity = identity + "|" + note.getObsolete().toString();
		}

		return identity;
	}

	public static String variantNoteFmsDtoIdentity(VariantNoteFmsDTO note) {
		String identity = StringUtils.isBlank(note.getNote()) ? "" : note.getNote();
		List<String> referenceCuries = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(note.getReferences())) {
			for (PublicationRefFmsDTO publicationDto : note.getReferences()) {
				if (StringUtils.isNotBlank(publicationDto.getPublicationId())) {
					referenceCuries.add(publicationDto.getPublicationId());
				}
			}
			Collections.sort(referenceCuries);
			identity = identity + "|" + StringUtils.join(referenceCuries, ":");
		}

		return identity;
	}
}