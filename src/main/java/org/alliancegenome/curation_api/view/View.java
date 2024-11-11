package org.alliancegenome.curation_api.view;

public class View {

	// Curation Views

	public static class FieldsOnly {
	}

	public static class FieldsAndLists extends FieldsOnly {
	}

	public static class ConditionRelationView extends FieldsOnly {
	}

	public static class ConditionRelationCreateView extends ConditionRelationView {
	}

	public static class ConditionRelationUpdateView extends ConditionRelationView {
	}

	public static class VocabularyTermView extends FieldsOnly {
	}

	public static class VocabularyView extends FieldsOnly {
	}

	public static class VocabularyTermUpdate extends FieldsOnly {
	}

	public static class VocabularyTermSetView extends FieldsOnly {
	}

	public static class ResourceDescriptorView extends FieldsOnly {
	}

	public static class ResourceDescriptorPageView extends FieldsOnly {
	}

	public static class NoteView extends FieldsAndLists {
	}

	public static class ReportHistory extends FieldsOnly {
	}

	public static class ConstructView extends FieldsOnly {
	}

	public static class DiseaseAnnotation extends FieldsOnly {
	}

	public static class DiseaseAnnotationUpdate extends DiseaseAnnotation {
	}

	public static class DiseaseAnnotationCreate extends DiseaseAnnotation {
	}

	public static class PhenotypeAnnotationView extends FieldsOnly {
	}

	public static class PhenotypeAnnotationUpdate extends PhenotypeAnnotationView {
	}

	public static class PhenotypeAnnotationCreate extends PhenotypeAnnotationView {
	}

	public static class AffectedGenomicModelView extends FieldsOnly {
	}

	public static class AlleleView extends FieldsOnly {
	}

	public static class AlleleDetailView extends AlleleView {
	}

	public static class SequenceTargetingReagentView extends FieldsOnly {
	}

	public static class SequenceTargetingReagentDetailView extends SequenceTargetingReagentView {
	}

	public static class AlleleUpdate extends AlleleView {
	}

	public static class AlleleCreate extends AlleleView {
	}

	public static class GeneView extends FieldsOnly {
	}

	public static class GeneDetailView extends GeneView {
	}

	public static class GeneUpdate extends GeneView {
	}

	public static class GeneCreate extends GeneView {
	}

	public static class VariantView extends FieldsOnly {
	}

	public static class VariantUpdate extends GeneView {
	}

	public static class VariantCreate extends GeneView {
	}
	
	public static class VariantDetailView extends VariantView {
	}

	public static class GeneInteractionView extends FieldsOnly {
	}

	public static class PersonSettingView {
	}

	public static class PrivateOnlyView {
	}

	// Public only views

	public static class ForPublic {
	}

	public static class DiseaseAnnotationForPublic extends ForPublic {
	}

	public static class BulkLoadFileHistoryView extends FieldsOnly {
	}
}
