package org.alliancegenome.curation_api.dao.associations.alleleAssociations;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.associations.alleleAssociations.AlleleVariantAssociation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AlleleVariantAssociationDAO extends BaseSQLDAO<AlleleVariantAssociation> {

	protected AlleleVariantAssociationDAO() {
		super(AlleleVariantAssociation.class);
	}

}
