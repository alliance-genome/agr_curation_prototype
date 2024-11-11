package org.alliancegenome.curation_api.services.helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.model.entities.base.SubmittedObject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

public class UniqueIdGeneratorHelper extends ArrayList<String> {

	public String getUniqueId() {
		return StringUtils.join(this, "|");
	}

	public String getSummary() {
		return StringUtils.join(this, ":");
	}

	@Override
	public boolean add(String s) {
		if (StringUtils.isNotBlank(s)) {
			return super.add(s);
		}
		return false;
	}
	
	public void add(Integer i) {
		if (i != null) {
			add(Integer.toString(i));
		}
	}

	public void addList(List<String> list) {
		if (CollectionUtils.isNotEmpty(list)) {
			Collections.sort(list);
			for (String s : list) {
				add(s);
			}
		}
	}

	public <E extends SubmittedObject> void addSubmittedObjectList(List<E> list) {
		if (CollectionUtils.isNotEmpty(list)) {
			List<String> submittedIdentifiers = list.stream().map(SubmittedObject::getSubmittedIdentifier).collect(Collectors.toList());
			addList(submittedIdentifiers);
		}
	}

}
