ALTER TABLE ONLY agmdiseaseannotation ADD CONSTRAINT agmdiseaseannotation_assertedallele_id_fk FOREIGN KEY (assertedallele_id) REFERENCES allele(id);
ALTER TABLE ONLY diseaseannotation ADD CONSTRAINT diseaseannotation_singlereference_id_fk FOREIGN KEY (singlereference_id) REFERENCES reference(id);
ALTER TABLE ONLY diseaseannotation ADD CONSTRAINT diseaseannotation_geneticsex_id_fk FOREIGN KEY (geneticsex_id) REFERENCES vocabularyterm(id);
ALTER TABLE ONLY agmdiseaseannotation ADD CONSTRAINT agmdiseaseannotation_dasubject_id_fk FOREIGN KEY (diseaseannotationsubject_id) REFERENCES affectedgenomicmodel(id) ON DELETE CASCADE;
ALTER TABLE ONLY diseaseannotation ADD CONSTRAINT diseaseannotation_dgmrelation_id_fk FOREIGN KEY (diseasegeneticmodifierrelation_id) REFERENCES vocabularyterm(id);
ALTER TABLE ONLY diseaseannotation ADD CONSTRAINT diseaseannotation_relation_id_fk FOREIGN KEY (relation_id) REFERENCES vocabularyterm(id);
ALTER TABLE ONLY diseaseannotation ADD CONSTRAINT diseaseannotation_annotationtype_id_fk FOREIGN KEY (annotationtype_id) REFERENCES vocabularyterm(id);
ALTER TABLE ONLY agmdiseaseannotation ADD CONSTRAINT agmdiseaseannotation_inferredallele_id_fk FOREIGN KEY (inferredallele_id) REFERENCES allele(id);
ALTER TABLE ONLY diseaseannotation ADD CONSTRAINT diseaseannotation_createdby_id_fk FOREIGN KEY (createdby_id) REFERENCES person(id);
ALTER TABLE ONLY diseaseannotation ADD CONSTRAINT diseaseannotation_secondarydataprovider_id_fk FOREIGN KEY (secondarydataprovider_id) REFERENCES dataprovider(id);
ALTER TABLE ONLY diseaseannotation ADD CONSTRAINT diseaseannotation_updatedby_id_fk FOREIGN KEY (updatedby_id) REFERENCES person(id);
ALTER TABLE ONLY agmdiseaseannotation ADD CONSTRAINT agmdiseaseannotation_inferredgene_id_fk FOREIGN KEY (inferredgene_id) REFERENCES gene(id);
ALTER TABLE ONLY diseaseannotation ADD CONSTRAINT diseaseannotation_dataprovider_id_fk FOREIGN KEY (dataprovider_id) REFERENCES dataprovider(id);
ALTER TABLE ONLY diseaseannotation ADD CONSTRAINT diseaseannotation_daobject_id_fk FOREIGN KEY (diseaseannotationobject_id) REFERENCES ontologyterm(id);

ALTER TABLE ONLY diseaseannotation_biologicalentity ADD CONSTRAINT diseaseannotation_biologicalentity_dgm_id_fk FOREIGN KEY (diseasegeneticmodifiers_id) REFERENCES biologicalentity(id);
ALTER TABLE ONLY diseaseannotation_biologicalentity ADD CONSTRAINT diseaseannotation_biologicalentity_da_id_fk FOREIGN KEY (diseaseannotation_id) REFERENCES diseaseannotation(id);

ALTER TABLE ONLY diseaseannotation_conditionrelation ADD CONSTRAINT diseaseannotation_conditionrelation_annotation_id_fk FOREIGN KEY (annotation_id) REFERENCES diseaseannotation(id);
ALTER TABLE ONLY diseaseannotation_conditionrelation ADD CONSTRAINT diseaseannotation_conditionrelation_cr_id_fk FOREIGN KEY (conditionrelations_id) REFERENCES conditionrelation(id);

ALTER TABLE ONLY diseaseannotation_gene ADD CONSTRAINT diseaseannotation_gene_diseaseannotation_id_fk FOREIGN KEY (diseaseannotation_id) REFERENCES diseaseannotation(id);
ALTER TABLE ONLY diseaseannotation_gene ADD CONSTRAINT diseaseannotation_gene_with_id_fk FOREIGN KEY (with_id) REFERENCES gene(id);
ALTER TABLE ONLY agmdiseaseannotation_gene ADD CONSTRAINT agmdiseaseannotation_gene_agmdiseaseannotation_id_fk FOREIGN KEY (agmdiseaseannotation_id) REFERENCES agmdiseaseannotation(id);
ALTER TABLE ONLY agmdiseaseannotation_gene ADD CONSTRAINT agmdiseaseannotation_gene_assertedgenes_id_fk FOREIGN KEY (assertedgenes_id) REFERENCES gene(id);

ALTER TABLE ONLY diseaseannotation_note ADD CONSTRAINT diseaseannotation_note_annotation_id_fk FOREIGN KEY (annotation_id) REFERENCES diseaseannotation(id);
ALTER TABLE ONLY diseaseannotation_note ADD CONSTRAINT diseaseannotation_note_relatednotes_id_fku9eem1iwfkgx FOREIGN KEY (relatednotes_id) REFERENCES note(id);

ALTER TABLE ONLY diseaseannotation_ontologyterm ADD CONSTRAINT diseaseannotation_ontologyterm_evidencecodes_id_fk FOREIGN KEY (evidencecodes_id) REFERENCES ontologyterm(id);
ALTER TABLE ONLY diseaseannotation_ontologyterm ADD CONSTRAINT diseaseannotation_ontologyterm_diseaseannotation_id_fk FOREIGN KEY (diseaseannotation_id) REFERENCES diseaseannotation(id);

ALTER TABLE ONLY diseaseannotation_vocabularyterm ADD CONSTRAINT diseaseannotation_vocabularyterm_diseaseannotation_id_fk FOREIGN KEY (diseaseannotation_id) REFERENCES diseaseannotation(id);
ALTER TABLE ONLY diseaseannotation_vocabularyterm ADD CONSTRAINT diseaseannotation_vocabularyterm_diseasequalifiers_id_fk FOREIGN KEY (diseasequalifiers_id) REFERENCES vocabularyterm(id);

ALTER TABLE ONLY agmphenotypeannotation ADD CONSTRAINT agmphenotypeannotation_inferredallele_id_fk FOREIGN KEY (inferredallele_id) REFERENCES allele(id);
ALTER TABLE ONLY phenotypeannotation ADD CONSTRAINT phenotypeannotation_crossreference_id_fk FOREIGN KEY (crossreference_id) REFERENCES crossreference(id);
ALTER TABLE ONLY agmphenotypeannotation ADD CONSTRAINT agmphenotypeannotation_inferredgene_id_fk FOREIGN KEY (inferredgene_id) REFERENCES gene(id);
ALTER TABLE ONLY phenotypeannotation ADD CONSTRAINT phenotypeannotation_createdby_id_fk FOREIGN KEY (createdby_id) REFERENCES person(id);
ALTER TABLE ONLY phenotypeannotation ADD CONSTRAINT phenotypeannotation_updatedby_id_fk FOREIGN KEY (updatedby_id) REFERENCES person(id);
ALTER TABLE ONLY phenotypeannotation ADD CONSTRAINT phenotypeannotation_dataprovider_id_fk FOREIGN KEY (dataprovider_id) REFERENCES dataprovider(id);
ALTER TABLE ONLY phenotypeannotation ADD CONSTRAINT phenotypeannotation_singlereference_id_fk FOREIGN KEY (singlereference_id) REFERENCES reference(id);
ALTER TABLE ONLY agmphenotypeannotation ADD CONSTRAINT agmphenotypeannotation_assertedallele_id_fk FOREIGN KEY (assertedallele_id) REFERENCES allele(id);
ALTER TABLE ONLY agmphenotypeannotation ADD CONSTRAINT agmphenotypeannotation_pasubject_id_fk FOREIGN KEY (phenotypeannotationsubject_id) REFERENCES affectedgenomicmodel(id) ON DELETE CASCADE;
ALTER TABLE ONLY phenotypeannotation ADD CONSTRAINT phenotypeannotation_relation_id_fk FOREIGN KEY (relation_id) REFERENCES vocabularyterm(id);

ALTER TABLE ONLY phenotypeannotation_conditionrelation ADD CONSTRAINT phenotypeannotation_conditionrelation_cr_id_fk FOREIGN KEY (conditionrelations_id) REFERENCES conditionrelation(id);
ALTER TABLE ONLY phenotypeannotation_conditionrelation ADD CONSTRAINT phenotypeannotation_conditionrelation_annotation_id_fk FOREIGN KEY (annotation_id) REFERENCES phenotypeannotation(id);

ALTER TABLE ONLY agmphenotypeannotation_gene ADD CONSTRAINT agmphenotypeannotation_gene_agmpa_id_fk FOREIGN KEY (agmphenotypeannotation_id) REFERENCES agmphenotypeannotation(id);
ALTER TABLE ONLY agmphenotypeannotation_gene ADD CONSTRAINT agmphenotypeannotation_gene_assertedgenes_id_fk FOREIGN KEY (assertedgenes_id) REFERENCES gene(id);

ALTER TABLE ONLY phenotypeannotation_note ADD CONSTRAINT phenotypeannotation_note_annotation_id_fk FOREIGN KEY (annotation_id) REFERENCES phenotypeannotation(id);
ALTER TABLE ONLY phenotypeannotation_note ADD CONSTRAINT phenotypeannotation_note_relatednotes_id_fk FOREIGN KEY (relatednotes_id) REFERENCES note(id);

ALTER TABLE ONLY phenotypeannotation_ontologyterm ADD CONSTRAINT phenotypeannotation_ontologyterm_pa_id_fk FOREIGN KEY (phenotypeannotation_id) REFERENCES phenotypeannotation(id);
ALTER TABLE ONLY phenotypeannotation_ontologyterm ADD CONSTRAINT phenotypeannotation_ontologyterm_phenotypeterms_id_fk FOREIGN KEY (phenotypeterms_id) REFERENCES ontologyterm(id);

ALTER TABLE ONLY allelediseaseannotation ADD CONSTRAINT allelediseaseannotation_dasubject_id_fk FOREIGN KEY (diseaseannotationsubject_id) REFERENCES allele(id) ON DELETE CASCADE;
ALTER TABLE ONLY allelediseaseannotation ADD CONSTRAINT allelediseaseannotation_inferredgene_id_fk FOREIGN KEY (inferredgene_id) REFERENCES gene(id);

ALTER TABLE ONLY allelediseaseannotation_gene ADD CONSTRAINT allelediseaseannotation_gene_assertedgenes_id FOREIGN KEY (assertedgenes_id) REFERENCES gene(id);
ALTER TABLE ONLY allelediseaseannotation_gene ADD CONSTRAINT allelediseaseannotation_gene_ada_id FOREIGN KEY (allelediseaseannotation_id) REFERENCES allelediseaseannotation(id);

ALTER TABLE ONLY allelegeneassociation ADD CONSTRAINT allelegeneassociation_relation_id_fk FOREIGN KEY (relation_id) REFERENCES vocabularyterm(id);
ALTER TABLE ONLY allelegeneassociation ADD CONSTRAINT allelegeneassociation_agaobject_id_fk FOREIGN KEY (allelegeneassociationobject_id) REFERENCES gene(id);
ALTER TABLE ONLY allelegeneassociation ADD CONSTRAINT allelegeneassociation_updatedby_id_fk FOREIGN KEY (updatedby_id) REFERENCES person(id);
ALTER TABLE ONLY allelegeneassociation ADD CONSTRAINT allelegeneassociation_createdby_id_fk FOREIGN KEY (createdby_id) REFERENCES person(id);
ALTER TABLE ONLY allelegeneassociation ADD CONSTRAINT allelegeneassociation_evidencecode_id_fk FOREIGN KEY (evidencecode_id) REFERENCES ontologyterm(id);
ALTER TABLE ONLY allelegeneassociation ADD CONSTRAINT allelegeneassociation_relatednote_id_fk FOREIGN KEY (relatednote_id) REFERENCES note(id);
ALTER TABLE ONLY allelegeneassociation ADD CONSTRAINT allelegeneassociation_aasubject_id_fk FOREIGN KEY (alleleassociationsubject_id) REFERENCES allele(id);

ALTER TABLE ONLY allelegeneassociation_informationcontententity ADD CONSTRAINT allelegeneassociation_ice_evidence_id_fk FOREIGN KEY (evidence_id) REFERENCES informationcontententity(id);
ALTER TABLE ONLY allelegeneassociation_informationcontententity ADD CONSTRAINT allelegeneassociation_ice_association_id_fk FOREIGN KEY (association_id) REFERENCES allelegeneassociation(id);

ALTER TABLE ONLY allelephenotypeannotation ADD CONSTRAINT allelephenotypeannotation_inferredgene_id_fk FOREIGN KEY (inferredgene_id) REFERENCES gene(id);
ALTER TABLE ONLY allelephenotypeannotation ADD CONSTRAINT allelephenotypeannotation_pasubject_id_fk FOREIGN KEY (phenotypeannotationsubject_id) REFERENCES allele(id) ON DELETE CASCADE;

ALTER TABLE ONLY allelephenotypeannotation_gene ADD CONSTRAINT allelephenotypeannotation_gene_allelephenotypeannotation_id_fk FOREIGN KEY (allelephenotypeannotation_id) REFERENCES allelephenotypeannotation(id);
ALTER TABLE ONLY allelephenotypeannotation_gene ADD CONSTRAINT allelephenotypeannotation_gene_assertedgenes_id_fk FOREIGN KEY (assertedgenes_id) REFERENCES gene(id);

ALTER TABLE ONLY codingsequencegenomiclocationassociation ADD CONSTRAINT cdsglassociation_updatedby_id_fk FOREIGN KEY (updatedby_id) REFERENCES person(id);
ALTER TABLE ONLY codingsequencegenomiclocationassociation ADD CONSTRAINT cdsglassociation_cdsglaobject_id_fk FOREIGN KEY (codingsequencegenomiclocationassociationobject_id) REFERENCES assemblycomponent(id);
ALTER TABLE ONLY codingsequencegenomiclocationassociation ADD CONSTRAINT cdsglassociation_createdby_id_fk FOREIGN KEY (createdby_id) REFERENCES person(id);
ALTER TABLE ONLY codingsequencegenomiclocationassociation ADD CONSTRAINT cdsglassociation_cdsglsubject_id_fk FOREIGN KEY (codingsequenceassociationsubject_id) REFERENCES codingsequence(id);
ALTER TABLE ONLY codingsequencegenomiclocationassociation ADD CONSTRAINT cdsglassociation_relation_id_fk FOREIGN KEY (relation_id) REFERENCES vocabularyterm(id);

ALTER TABLE ONLY CodingSequenceGenomicLocationAssociation_InformationContentEntity ADD CONSTRAINT cdsgla_ice_association_id_fk FOREIGN KEY (association_id) REFERENCES codingsequencegenomiclocationassociation(id);
ALTER TABLE ONLY CodingSequenceGenomicLocationAssociation_InformationContentEntity ADD CONSTRAINT cdsgla_ice_evidence_id_fk FOREIGN KEY (evidence_id) REFERENCES informationcontententity(id);

ALTER TABLE ONLY constructgenomicentityassociation ADD CONSTRAINT constructgenomicentityassociation_updatedby_id_fk FOREIGN KEY (updatedby_id) REFERENCES person(id);
ALTER TABLE ONLY constructgenomicentityassociation ADD CONSTRAINT constructgenomicentityassociation_cgeaobject_id_fk FOREIGN KEY (constructgenomicentityassociationobject_id) REFERENCES genomicentity(id);
ALTER TABLE ONLY constructgenomicentityassociation ADD CONSTRAINT constructgenomicentityassociation_createdby_id_fk FOREIGN KEY (createdby_id) REFERENCES person(id);
ALTER TABLE ONLY constructgenomicentityassociation ADD CONSTRAINT constructgenomicentityassociation_casubject_id_fk FOREIGN KEY (constructassociationsubject_id) REFERENCES construct(id);
ALTER TABLE ONLY constructgenomicentityassociation ADD CONSTRAINT constructgenomicentityassociation_relation_id_fk FOREIGN KEY (relation_id) REFERENCES vocabularyterm(id);

ALTER TABLE ONLY constructgenomicentityassociation_informationcontententity ADD CONSTRAINT cgea_ice_evidence_id_fk FOREIGN KEY (evidence_id) REFERENCES informationcontententity(id);
ALTER TABLE ONLY constructgenomicentityassociation_informationcontententity ADD CONSTRAINT cgea_ice_association_id_fk FOREIGN KEY (association_id) REFERENCES constructgenomicentityassociation(id);

ALTER TABLE ONLY constructgenomicentityassociation_note ADD CONSTRAINT cgea_note_cgeassociation_id_fk FOREIGN KEY (constructgenomicentityassociation_id) REFERENCES constructgenomicentityassociation(id);
ALTER TABLE ONLY constructgenomicentityassociation_note ADD CONSTRAINT cgea_note_relatednotes_id_fk FOREIGN KEY (relatednotes_id) REFERENCES note(id);

ALTER TABLE ONLY exongenomiclocationassociation ADD CONSTRAINT exongenomiclocationassociation_relation_id_fk FOREIGN KEY (relation_id) REFERENCES vocabularyterm(id);
ALTER TABLE ONLY exongenomiclocationassociation ADD CONSTRAINT exongenomiclocationassociation_createdby_id_fk FOREIGN KEY (createdby_id) REFERENCES person(id);
ALTER TABLE ONLY exongenomiclocationassociation ADD CONSTRAINT exongenomiclocationassociation_updatedby_id_fk FOREIGN KEY (updatedby_id) REFERENCES person(id);
ALTER TABLE ONLY exongenomiclocationassociation ADD CONSTRAINT exongenomiclocationassociation_easubject_id_fk FOREIGN KEY (exonassociationsubject_id) REFERENCES exon(id);
ALTER TABLE ONLY exongenomiclocationassociation ADD CONSTRAINT exongenomiclocationassociation_eglaobject_id_fk FOREIGN KEY (exongenomiclocationassociationobject_id) REFERENCES assemblycomponent(id);

ALTER TABLE ONLY exongenomiclocationassociation_informationcontententity ADD CONSTRAINT exongla_ice_association_id_fk FOREIGN KEY (association_id) REFERENCES exongenomiclocationassociation(id);
ALTER TABLE ONLY exongenomiclocationassociation_informationcontententity ADD CONSTRAINT exongla_ice_evidence_id_fk FOREIGN KEY (evidence_id) REFERENCES informationcontententity(id);

ALTER TABLE ONLY genediseaseannotation ADD CONSTRAINT genediseaseannotation_dasubject_id_fk FOREIGN KEY (diseaseannotationsubject_id) REFERENCES gene(id) ON DELETE CASCADE;
ALTER TABLE ONLY genediseaseannotation ADD CONSTRAINT genediseaseannotation_sgdstrainbackground_id_fk FOREIGN KEY (sgdstrainbackground_id) REFERENCES affectedgenomicmodel(id);

ALTER TABLE ONLY geneexpressionannotation ADD CONSTRAINT geneexpressionannotation_singlereference_id_fk FOREIGN KEY (singlereference_id) REFERENCES reference(id);
ALTER TABLE ONLY geneexpressionannotation ADD CONSTRAINT geneexpressionannotation_createdby_id_fk FOREIGN KEY (createdby_id) REFERENCES person(id);
ALTER TABLE ONLY geneexpressionannotation ADD CONSTRAINT geneexpressionannotation_expressionpattern_id_fk FOREIGN KEY (expressionpattern_id) REFERENCES expressionpattern(id);
ALTER TABLE ONLY geneexpressionannotation ADD CONSTRAINT geneexpressionannotation_updatedby_id_fk FOREIGN KEY (updatedby_id) REFERENCES person(id);
ALTER TABLE ONLY geneexpressionannotation ADD CONSTRAINT geneexpressionannotation_relation_id_fk FOREIGN KEY (relation_id) REFERENCES vocabularyterm(id);
ALTER TABLE ONLY geneexpressionannotation ADD CONSTRAINT geneexpressionannotation_expressionassayused_id_fk FOREIGN KEY (expressionassayused_id) REFERENCES ontologyterm(id);
ALTER TABLE ONLY geneexpressionannotation ADD CONSTRAINT geneexpressionannotation_dataprovider_id_fk FOREIGN KEY (dataprovider_id) REFERENCES dataprovider(id);
ALTER TABLE ONLY geneexpressionannotation ADD CONSTRAINT geneexpressionannotation_easubject_id_fk FOREIGN KEY (expressionannotationsubject_id) REFERENCES gene(id);

ALTER TABLE ONLY geneexpressionannotation_conditionrelation ADD CONSTRAINT gea_conditionrelation_annotation_id_fk FOREIGN KEY (annotation_id) REFERENCES geneexpressionannotation(id);
ALTER TABLE ONLY geneexpressionannotation_conditionrelation ADD CONSTRAINT gea_conditionrelation_conditionrelations_id_fk FOREIGN KEY (conditionrelations_id) REFERENCES conditionrelation(id);

ALTER TABLE ONLY geneexpressionannotation_note ADD CONSTRAINT geneexpressionannotation_note_annotation_id_fk FOREIGN KEY (annotation_id) REFERENCES geneexpressionannotation(id);
ALTER TABLE ONLY geneexpressionannotation_note ADD CONSTRAINT geneexpressionannotation_note_relatednotes_id_fk FOREIGN KEY (relatednotes_id) REFERENCES note(id);

ALTER TABLE ONLY genegeneticinteraction ADD CONSTRAINT genegeneticinteraction_updatedby_id_fk FOREIGN KEY (updatedby_id) REFERENCES person(id);
ALTER TABLE ONLY genegeneticinteraction ADD CONSTRAINT genegeneticinteraction_interactiontype_id_fk FOREIGN KEY (interactiontype_id) REFERENCES ontologyterm(id);
ALTER TABLE ONLY genegeneticinteraction ADD CONSTRAINT genegeneticinteraction_interactorbtype_id_fk FOREIGN KEY (interactorbtype_id) REFERENCES ontologyterm(id);
ALTER TABLE ONLY genegeneticinteraction ADD CONSTRAINT genegeneticinteraction_interactorbgeneticperturbation_id_fk FOREIGN KEY (interactorbgeneticperturbation_id) REFERENCES allele(id) ON DELETE CASCADE;
ALTER TABLE ONLY genegeneticinteraction ADD CONSTRAINT genegeneticinteraction_createdby_id_fk FOREIGN KEY (createdby_id) REFERENCES person(id);
ALTER TABLE ONLY genegeneticinteraction ADD CONSTRAINT genegeneticinteraction_interactorbrole_id_fk FOREIGN KEY (interactorbrole_id) REFERENCES ontologyterm(id);
ALTER TABLE ONLY genegeneticinteraction ADD CONSTRAINT genegeneticinteraction_interactorageneticperturbation_id_fk FOREIGN KEY (interactorageneticperturbation_id) REFERENCES allele(id) ON DELETE CASCADE;
ALTER TABLE ONLY genegeneticinteraction ADD CONSTRAINT genegeneticinteraction_interactorarole_id_fk FOREIGN KEY (interactorarole_id) REFERENCES ontologyterm(id);
ALTER TABLE ONLY genegeneticinteraction ADD CONSTRAINT genegeneticinteraction_interactoratype_id_fk FOREIGN KEY (interactoratype_id) REFERENCES ontologyterm(id);
ALTER TABLE ONLY genegeneticinteraction ADD CONSTRAINT genegeneticinteraction_interactionsource_id_fk FOREIGN KEY (interactionsource_id) REFERENCES ontologyterm(id);
ALTER TABLE ONLY genegeneticinteraction ADD CONSTRAINT genegeneticinteraction_relation_id_fk FOREIGN KEY (relation_id) REFERENCES vocabularyterm(id);
ALTER TABLE ONLY genegeneticinteraction ADD CONSTRAINT genegeneticinteraction_gasubject_id_fk FOREIGN KEY (geneassociationsubject_id) REFERENCES gene(id);
ALTER TABLE ONLY genegeneticinteraction ADD CONSTRAINT genegeneticinteraction_ggaobject_id_fk FOREIGN KEY (genegeneassociationobject_id) REFERENCES gene(id);

ALTER TABLE ONLY genegeneticinteraction_crossreference ADD CONSTRAINT genegeneticinteraction_xref_geneinteraction_id_fk FOREIGN KEY (geneinteraction_id) REFERENCES genegeneticinteraction(id);
ALTER TABLE ONLY genegeneticinteraction_crossreference ADD CONSTRAINT genegeneticinteraction_xref_crossreferences_id_fk FOREIGN KEY (crossreferences_id) REFERENCES crossreference(id);

ALTER TABLE ONLY genegeneticinteraction_informationcontententity ADD CONSTRAINT genegeneticinteraction_ice_association_id_fk FOREIGN KEY (association_id) REFERENCES genegeneticinteraction(id);
ALTER TABLE ONLY genegeneticinteraction_informationcontententity ADD CONSTRAINT genegeneticinteraction_ice_evidence_id_fk FOREIGN KEY (evidence_id) REFERENCES informationcontententity(id);

ALTER TABLE ONLY genegeneticinteraction_phenotypesortraits ADD CONSTRAINT genegeneticinteraction_phenotypesortraits_ggi_id_fk FOREIGN KEY (genegeneticinteraction_id) REFERENCES genegeneticinteraction(id);

ALTER TABLE ONLY genemolecularinteraction ADD CONSTRAINT genemolecularinteraction_interactorarole_id FOREIGN KEY (interactorarole_id) REFERENCES ontologyterm(id);
ALTER TABLE ONLY genemolecularinteraction ADD CONSTRAINT genemolecularinteraction_interactorbtype_id FOREIGN KEY (interactorbtype_id) REFERENCES ontologyterm(id);
ALTER TABLE ONLY genemolecularinteraction ADD CONSTRAINT genemolecularinteraction_relation_id FOREIGN KEY (relation_id) REFERENCES vocabularyterm(id);
ALTER TABLE ONLY genemolecularinteraction ADD CONSTRAINT genemolecularinteraction_interactiontype_id FOREIGN KEY (interactiontype_id) REFERENCES ontologyterm(id);
ALTER TABLE ONLY genemolecularinteraction ADD CONSTRAINT genemolecularinteraction_gasubject_id FOREIGN KEY (geneassociationsubject_id) REFERENCES gene(id);
ALTER TABLE ONLY genemolecularinteraction ADD CONSTRAINT genemolecularinteraction_detectionmethod_id FOREIGN KEY (detectionmethod_id) REFERENCES ontologyterm(id);
ALTER TABLE ONLY genemolecularinteraction ADD CONSTRAINT genemolecularinteraction_interactorbrole_id FOREIGN KEY (interactorbrole_id) REFERENCES ontologyterm(id);
ALTER TABLE ONLY genemolecularinteraction ADD CONSTRAINT genemolecularinteraction_interactionsource_id FOREIGN KEY (interactionsource_id) REFERENCES ontologyterm(id);
ALTER TABLE ONLY genemolecularinteraction ADD CONSTRAINT genemolecularinteraction_interactoratype_id FOREIGN KEY (interactoratype_id) REFERENCES ontologyterm(id);
ALTER TABLE ONLY genemolecularinteraction ADD CONSTRAINT genemolecularinteraction_aggregationdatabase_id FOREIGN KEY (aggregationdatabase_id) REFERENCES ontologyterm(id);
ALTER TABLE ONLY genemolecularinteraction ADD CONSTRAINT genemolecularinteraction_createdby_id FOREIGN KEY (createdby_id) REFERENCES person(id);
ALTER TABLE ONLY genemolecularinteraction ADD CONSTRAINT genemolecularinteraction_ggaobject_id FOREIGN KEY (genegeneassociationobject_id) REFERENCES gene(id);
ALTER TABLE ONLY genemolecularinteraction ADD CONSTRAINT genemolecularinteraction_updatedby_id FOREIGN KEY (updatedby_id) REFERENCES person(id);

ALTER TABLE ONLY genemolecularinteraction_crossreference ADD CONSTRAINT genemolecularinteraction_xref_geneinteraction_id_fk FOREIGN KEY (geneinteraction_id) REFERENCES genemolecularinteraction(id);
ALTER TABLE ONLY genemolecularinteraction_crossreference ADD CONSTRAINT genemolecularinteraction_xref_crossreferences_id_fk FOREIGN KEY (crossreferences_id) REFERENCES crossreference(id);

ALTER TABLE ONLY genemolecularinteraction_informationcontententity ADD CONSTRAINT genemolecularinteraction_ice_evidence_id_fk FOREIGN KEY (evidence_id) REFERENCES informationcontententity(id);
ALTER TABLE ONLY genemolecularinteraction_informationcontententity ADD CONSTRAINT genemolecularinteraction_ice_association_id_fk FOREIGN KEY (association_id) REFERENCES genemolecularinteraction(id);

ALTER TABLE ONLY genephenotypeannotation ADD CONSTRAINT genephenotypeannotation_sgdstrainbackground_id_fk FOREIGN KEY (sgdstrainbackground_id) REFERENCES affectedgenomicmodel(id);
ALTER TABLE ONLY genephenotypeannotation ADD CONSTRAINT genephenotypeannotation_pasubject_id_fk FOREIGN KEY (phenotypeannotationsubject_id) REFERENCES gene(id) ON DELETE CASCADE;

ALTER TABLE ONLY sequencetargetingreagentgeneassociation ADD CONSTRAINT sequencetargetingreagentgeneassociation_sqtrasubject_id FOREIGN KEY (sequencetargetingreagentassociationsubject_id) REFERENCES sequencetargetingreagent(id);
ALTER TABLE ONLY sequencetargetingreagentgeneassociation ADD CONSTRAINT sequencetargetingreagentgeneassociation_sqtragobject_id FOREIGN KEY (sequencetargetingreagentgeneassociationobject_id) REFERENCES gene(id);
ALTER TABLE ONLY sequencetargetingreagentgeneassociation ADD CONSTRAINT sequencetargetingreagentgeneassociation_updatedby_id FOREIGN KEY (updatedby_id) REFERENCES person(id);
ALTER TABLE ONLY sequencetargetingreagentgeneassociation ADD CONSTRAINT sequencetargetingreagentgeneassociation_relation_id FOREIGN KEY (relation_id) REFERENCES vocabularyterm(id);
ALTER TABLE ONLY sequencetargetingreagentgeneassociation ADD CONSTRAINT sequencetargetingreagentgeneassociation_createdby_id FOREIGN KEY (createdby_id) REFERENCES person(id);

ALTER TABLE ONLY sequencetargetingreagentgeneassociation_informationcontententit ADD CONSTRAINT sqtrga_ice_association_id_fk FOREIGN KEY (association_id) REFERENCES sequencetargetingreagentgeneassociation(id);
ALTER TABLE ONLY sequencetargetingreagentgeneassociation_informationcontententit ADD CONSTRAINT sqtrga_ice_evidence_id_fk FOREIGN KEY (evidence_id) REFERENCES informationcontententity(id);

ALTER TABLE ONLY transcriptcodingsequenceassociation ADD CONSTRAINT transcriptcodingsequenceassociation_relation_id_fk FOREIGN KEY (relation_id) REFERENCES vocabularyterm(id);
ALTER TABLE ONLY transcriptcodingsequenceassociation ADD CONSTRAINT transcriptcodingsequenceassociation_updatedby_id_fk FOREIGN KEY (updatedby_id) REFERENCES person(id);
ALTER TABLE ONLY transcriptcodingsequenceassociation ADD CONSTRAINT transcriptcodingsequenceassociation_tasubject_id_fk FOREIGN KEY (transcriptassociationsubject_id) REFERENCES transcript(id);
ALTER TABLE ONLY transcriptcodingsequenceassociation ADD CONSTRAINT transcriptcodingsequenceassociation_tcdsaobject_id_fk FOREIGN KEY (transcriptcodingsequenceassociationobject_id) REFERENCES codingsequence(id);
ALTER TABLE ONLY transcriptcodingsequenceassociation ADD CONSTRAINT transcriptcodingsequenceassociation_createdby_id_fk FOREIGN KEY (createdby_id) REFERENCES person(id);

ALTER TABLE ONLY transcriptcodingsequenceassociation_informationcontententity ADD CONSTRAINT transcriptcdsassociation_ice_evidence_id_fk FOREIGN KEY (evidence_id) REFERENCES informationcontententity(id);
ALTER TABLE ONLY transcriptcodingsequenceassociation_informationcontententity ADD CONSTRAINT transcriptcdsassociation_ice_association_id_fk FOREIGN KEY (association_id) REFERENCES transcriptcodingsequenceassociation(id);

ALTER TABLE ONLY transcriptexonassociation ADD CONSTRAINT transcriptexonassociation_createdby_id_fk FOREIGN KEY (createdby_id) REFERENCES person(id);
ALTER TABLE ONLY transcriptexonassociation ADD CONSTRAINT transcriptexonassociation_teaobject_id_fk FOREIGN KEY (transcriptexonassociationobject_id) REFERENCES exon(id);
ALTER TABLE ONLY transcriptexonassociation ADD CONSTRAINT transcriptexonassociation_updatedby_id_fk FOREIGN KEY (updatedby_id) REFERENCES person(id);
ALTER TABLE ONLY transcriptexonassociation ADD CONSTRAINT transcriptexonassociation_tasubject_id_fk FOREIGN KEY (transcriptassociationsubject_id) REFERENCES transcript(id);
ALTER TABLE ONLY transcriptexonassociation ADD CONSTRAINT transcriptexonassociation_relation_id_fk FOREIGN KEY (relation_id) REFERENCES vocabularyterm(id);

ALTER TABLE ONLY transcriptexonassociation_informationcontententity ADD CONSTRAINT transcriptexonassociation_ice_evidence_id_fk FOREIGN KEY (evidence_id) REFERENCES informationcontententity(id);
ALTER TABLE ONLY transcriptexonassociation_informationcontententity ADD CONSTRAINT transcriptexonassociation_ice_association_id_fk FOREIGN KEY (association_id) REFERENCES transcriptexonassociation(id);

ALTER TABLE ONLY transcriptgeneassociation ADD CONSTRAINT transcriptgeneassociation_tgaobject_id_fk FOREIGN KEY (transcriptgeneassociationobject_id) REFERENCES gene(id);
ALTER TABLE ONLY transcriptgeneassociation ADD CONSTRAINT transcriptgeneassociation_tasubject_id_fk FOREIGN KEY (transcriptassociationsubject_id) REFERENCES transcript(id);
ALTER TABLE ONLY transcriptgeneassociation ADD CONSTRAINT transcriptgeneassociation_updatedby_id_fk FOREIGN KEY (updatedby_id) REFERENCES person(id);
ALTER TABLE ONLY transcriptgeneassociation ADD CONSTRAINT transcriptgeneassociation_relation_id_fk FOREIGN KEY (relation_id) REFERENCES vocabularyterm(id);
ALTER TABLE ONLY transcriptgeneassociation ADD CONSTRAINT transcriptgeneassociation_createdby_id_fk FOREIGN KEY (createdby_id) REFERENCES person(id);

ALTER TABLE ONLY transcriptgeneassociation_informationcontententity ADD CONSTRAINT transcriptgeneassoc_ice_association_id_fk FOREIGN KEY (association_id) REFERENCES transcriptgeneassociation(id);
ALTER TABLE ONLY transcriptgeneassociation_informationcontententity ADD CONSTRAINT transcriptgeneassoc_ice_evidence_id_fk FOREIGN KEY (evidence_id) REFERENCES informationcontententity(id);

ALTER TABLE ONLY transcriptgenomiclocationassociation ADD CONSTRAINT transcriptgla_relation_id_fk FOREIGN KEY (relation_id) REFERENCES vocabularyterm(id);
ALTER TABLE ONLY transcriptgenomiclocationassociation ADD CONSTRAINT transcriptgla_updatedby_id_fk FOREIGN KEY (updatedby_id) REFERENCES person(id);
ALTER TABLE ONLY transcriptgenomiclocationassociation ADD CONSTRAINT transcriptgla_tasubject_id_fk FOREIGN KEY (transcriptassociationsubject_id) REFERENCES transcript(id);
ALTER TABLE ONLY transcriptgenomiclocationassociation ADD CONSTRAINT transcriptgla_createdby_id_fk FOREIGN KEY (createdby_id) REFERENCES person(id);
ALTER TABLE ONLY transcriptgenomiclocationassociation ADD CONSTRAINT transcriptgla_tglaobject_id_fk FOREIGN KEY (transcriptgenomiclocationassociationobject_id) REFERENCES assemblycomponent(id);

ALTER TABLE ONLY transcriptgenomiclocationassociation_informationcontententity ADD CONSTRAINT transcriptgla_ice_association_id_fk FOREIGN KEY (association_id) REFERENCES transcriptgenomiclocationassociation(id);
ALTER TABLE ONLY transcriptgenomiclocationassociation_informationcontententity ADD CONSTRAINT transcriptgla_ice_evidence_id_fk FOREIGN KEY (evidence_id) REFERENCES informationcontententity(id);
