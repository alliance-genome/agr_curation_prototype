package org.alliancegenome.curation_api.jobs.executors;

import java.io.FileInputStream;
import java.util.zip.GZIPInputStream;

import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.Molecule;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.ingest.dto.fms.MoleculeIngestFmsDTO;
import org.alliancegenome.curation_api.services.MoleculeService;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class MoleculeExecutor extends LoadFileExecutor {

	@Inject MoleculeService moleculeService;

	public void execLoad(BulkLoadFileHistory bulkLoadFileHistory) {
		try {
			MoleculeIngestFmsDTO moleculeData = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFileHistory.getBulkLoadFile().getLocalFilePath())), MoleculeIngestFmsDTO.class);
			bulkLoadFileHistory.getBulkLoadFile().setRecordCount(moleculeData.getData().size());
			if (bulkLoadFileHistory.getBulkLoadFile().getLinkMLSchemaVersion() == null) {
				AGRCurationSchemaVersion version = Molecule.class.getAnnotation(AGRCurationSchemaVersion.class);
				bulkLoadFileHistory.getBulkLoadFile().setLinkMLSchemaVersion(version.max());
			}
			if (moleculeData.getMetaData() != null && StringUtils.isNotBlank(moleculeData.getMetaData().getRelease())) {
				bulkLoadFileHistory.getBulkLoadFile().setAllianceMemberReleaseVersion(moleculeData.getMetaData().getRelease());
			}
			bulkLoadFileDAO.merge(bulkLoadFileHistory.getBulkLoadFile());

			bulkLoadFileHistory.setCount(moleculeData.getData().size());
			updateHistory(bulkLoadFileHistory);

			runLoad(moleculeService, bulkLoadFileHistory, null, moleculeData.getData(), null);
			bulkLoadFileHistory.finishLoad();
			updateHistory(bulkLoadFileHistory);
			updateExceptions(bulkLoadFileHistory);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
