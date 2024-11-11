package org.alliancegenome.curation_api.model.bridges;

import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.AssemblyComponent;
import org.alliancegenome.curation_api.model.entities.BiologicalEntity;
import org.alliancegenome.curation_api.model.entities.CodingSequence;
import org.alliancegenome.curation_api.model.entities.Exon;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.SequenceTargetingReagent;
import org.alliancegenome.curation_api.model.entities.Transcript;
import org.hibernate.search.engine.backend.document.DocumentElement;
import org.hibernate.search.engine.backend.document.IndexFieldReference;
import org.hibernate.search.engine.backend.document.model.dsl.IndexSchemaElement;
import org.hibernate.search.engine.backend.types.IndexFieldType;
import org.hibernate.search.engine.backend.types.Projectable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.bridge.TypeBridge;
import org.hibernate.search.mapper.pojo.bridge.binding.TypeBindingContext;
import org.hibernate.search.mapper.pojo.bridge.mapping.programmatic.TypeBinder;
import org.hibernate.search.mapper.pojo.bridge.runtime.TypeBridgeWriteContext;

public class BiologicalEntityTypeBridge implements TypeBinder {

	@Override
	public void bind(TypeBindingContext context) {
		
		context.dependencies().useRootOnly();
		
		IndexSchemaElement schemaElement = context.indexSchemaElement();
		
		IndexFieldType<String> type = context.typeFactory().asString().analyzer("autocompleteAnalyzer").searchAnalyzer("autocompleteSearchAnalyzer").toIndexFieldType();
		IndexFieldType<String> keywordType = context.typeFactory().asString().searchable(Searchable.YES).sortable(Sortable.YES).projectable(Projectable.YES).normalizer("sortNormalizer").toIndexFieldType();
		
		context.bridge(BiologicalEntity.class, new Bridge(
				schemaElement.field("name", type).toReference(),
				schemaElement.field("symbol", type).toReference(),
				schemaElement.field("name_keyword", keywordType).toReference(),
				schemaElement.field("symbol_keyword", keywordType).toReference()
				));
	}

	private static class Bridge implements TypeBridge<BiologicalEntity> {

		private final IndexFieldReference<String> nameField;
		private final IndexFieldReference<String> symbolField;
		private final IndexFieldReference<String> nameKeywordField;
		private final IndexFieldReference<String> symbolKeywordField;
		
		private Bridge(IndexFieldReference<String> nameField, IndexFieldReference<String> symbolField,
			IndexFieldReference<String> nameKeywordField, IndexFieldReference<String> symbolKeywordField) {
			this.nameField = nameField;
			this.symbolField = symbolField;
			this.nameKeywordField = nameKeywordField;
			this.symbolKeywordField = symbolKeywordField;
		}

		@Override
		public void write(DocumentElement target, BiologicalEntity bridgedElement, TypeBridgeWriteContext context) {
			String symbol;
			String name;

			if (bridgedElement == null) {
				symbol = null;
				name = null;
			} else {

				if (bridgedElement instanceof Gene) {
					Gene gene = (Gene) bridgedElement;
					symbol = gene.getGeneSymbol() == null ? null : gene.getGeneSymbol().getDisplayText();
					name = gene.getGeneFullName() == null ? null : gene.getGeneFullName().getDisplayText();
				} else if (bridgedElement instanceof Allele) {
					Allele allele = (Allele) bridgedElement;
					symbol = allele.getAlleleSymbol() == null ? null : allele.getAlleleSymbol().getDisplayText();
					name = allele.getAlleleFullName() == null ? null : allele.getAlleleFullName().getDisplayText();
				} else if (bridgedElement instanceof AffectedGenomicModel) {
					AffectedGenomicModel agm = (AffectedGenomicModel) bridgedElement;
					name = agm.getName();
					symbol = null;
				} else if (bridgedElement instanceof SequenceTargetingReagent) {
					SequenceTargetingReagent sqtr = (SequenceTargetingReagent) bridgedElement;
					name = sqtr.getName();
					symbol = null;
				} else if (bridgedElement instanceof Transcript) {
					Transcript transcript = (Transcript) bridgedElement;
					name = transcript.getName();
					symbol = null;
				} else if (bridgedElement instanceof Exon) {
					Exon exon = (Exon) bridgedElement;
					name = exon.getName();
					symbol = null;
				} else if (bridgedElement instanceof CodingSequence) {
					CodingSequence cds = (CodingSequence) bridgedElement;
					name = cds.getName();
					symbol = null;
				} else if (bridgedElement instanceof AssemblyComponent) {
					AssemblyComponent ac = (AssemblyComponent) bridgedElement;
					name = ac.getName();
					symbol = null;
				} else {
					name = null;
					symbol = null;
				}
			}
			
			target.addValue(this.symbolField, symbol);
			target.addValue(this.nameField, name);
			target.addValue(this.symbolKeywordField, symbol);
			target.addValue(this.nameKeywordField, name);
		}

	}
}
