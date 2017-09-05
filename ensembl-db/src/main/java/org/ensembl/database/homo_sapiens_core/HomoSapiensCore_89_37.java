/*
 * This file is generated by jOOQ.
*/
package org.ensembl.database.homo_sapiens_core;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.ensembl.database.homo_sapiens_core.tables.AltAllele;
import org.ensembl.database.homo_sapiens_core.tables.AltAlleleAttrib;
import org.ensembl.database.homo_sapiens_core.tables.AltAlleleGroup;
import org.ensembl.database.homo_sapiens_core.tables.Analysis;
import org.ensembl.database.homo_sapiens_core.tables.AnalysisDescription;
import org.ensembl.database.homo_sapiens_core.tables.Assembly;
import org.ensembl.database.homo_sapiens_core.tables.AssemblyException;
import org.ensembl.database.homo_sapiens_core.tables.AssociatedGroup;
import org.ensembl.database.homo_sapiens_core.tables.AssociatedXref;
import org.ensembl.database.homo_sapiens_core.tables.AttribType;
import org.ensembl.database.homo_sapiens_core.tables.CoordSystem;
import org.ensembl.database.homo_sapiens_core.tables.DataFile;
import org.ensembl.database.homo_sapiens_core.tables.DensityFeature;
import org.ensembl.database.homo_sapiens_core.tables.DensityType;
import org.ensembl.database.homo_sapiens_core.tables.DependentXref;
import org.ensembl.database.homo_sapiens_core.tables.Ditag;
import org.ensembl.database.homo_sapiens_core.tables.DitagFeature;
import org.ensembl.database.homo_sapiens_core.tables.Dna;
import org.ensembl.database.homo_sapiens_core.tables.DnaAlignFeature;
import org.ensembl.database.homo_sapiens_core.tables.DnaAlignFeatureAttrib;
import org.ensembl.database.homo_sapiens_core.tables.Exon;
import org.ensembl.database.homo_sapiens_core.tables.ExonTranscript;
import org.ensembl.database.homo_sapiens_core.tables.ExternalDb;
import org.ensembl.database.homo_sapiens_core.tables.ExternalSynonym;
import org.ensembl.database.homo_sapiens_core.tables.Gene;
import org.ensembl.database.homo_sapiens_core.tables.GeneArchive;
import org.ensembl.database.homo_sapiens_core.tables.GeneAttrib;
import org.ensembl.database.homo_sapiens_core.tables.GenomeStatistics;
import org.ensembl.database.homo_sapiens_core.tables.IdentityXref;
import org.ensembl.database.homo_sapiens_core.tables.Interpro;
import org.ensembl.database.homo_sapiens_core.tables.IntronSupportingEvidence;
import org.ensembl.database.homo_sapiens_core.tables.Karyotype;
import org.ensembl.database.homo_sapiens_core.tables.Map;
import org.ensembl.database.homo_sapiens_core.tables.MappingSession;
import org.ensembl.database.homo_sapiens_core.tables.MappingSet;
import org.ensembl.database.homo_sapiens_core.tables.Marker;
import org.ensembl.database.homo_sapiens_core.tables.MarkerFeature;
import org.ensembl.database.homo_sapiens_core.tables.MarkerMapLocation;
import org.ensembl.database.homo_sapiens_core.tables.MarkerSynonym;
import org.ensembl.database.homo_sapiens_core.tables.Meta;
import org.ensembl.database.homo_sapiens_core.tables.MetaCoord;
import org.ensembl.database.homo_sapiens_core.tables.MiscAttrib;
import org.ensembl.database.homo_sapiens_core.tables.MiscFeature;
import org.ensembl.database.homo_sapiens_core.tables.MiscFeatureMiscSet;
import org.ensembl.database.homo_sapiens_core.tables.MiscSet;
import org.ensembl.database.homo_sapiens_core.tables.ObjectXref;
import org.ensembl.database.homo_sapiens_core.tables.OntologyXref;
import org.ensembl.database.homo_sapiens_core.tables.Operon;
import org.ensembl.database.homo_sapiens_core.tables.OperonTranscript;
import org.ensembl.database.homo_sapiens_core.tables.OperonTranscriptGene;
import org.ensembl.database.homo_sapiens_core.tables.PeptideArchive;
import org.ensembl.database.homo_sapiens_core.tables.PredictionExon;
import org.ensembl.database.homo_sapiens_core.tables.PredictionTranscript;
import org.ensembl.database.homo_sapiens_core.tables.ProteinAlignFeature;
import org.ensembl.database.homo_sapiens_core.tables.ProteinFeature;
import org.ensembl.database.homo_sapiens_core.tables.RepeatConsensus;
import org.ensembl.database.homo_sapiens_core.tables.RepeatFeature;
import org.ensembl.database.homo_sapiens_core.tables.SeqRegion;
import org.ensembl.database.homo_sapiens_core.tables.SeqRegionAttrib;
import org.ensembl.database.homo_sapiens_core.tables.SeqRegionMapping;
import org.ensembl.database.homo_sapiens_core.tables.SeqRegionSynonym;
import org.ensembl.database.homo_sapiens_core.tables.SimpleFeature;
import org.ensembl.database.homo_sapiens_core.tables.StableIdEvent;
import org.ensembl.database.homo_sapiens_core.tables.SupportingFeature;
import org.ensembl.database.homo_sapiens_core.tables.Transcript;
import org.ensembl.database.homo_sapiens_core.tables.TranscriptAttrib;
import org.ensembl.database.homo_sapiens_core.tables.TranscriptIntronSupportingEvidence;
import org.ensembl.database.homo_sapiens_core.tables.TranscriptSupportingFeature;
import org.ensembl.database.homo_sapiens_core.tables.Translation;
import org.ensembl.database.homo_sapiens_core.tables.TranslationAttrib;
import org.ensembl.database.homo_sapiens_core.tables.UnmappedObject;
import org.ensembl.database.homo_sapiens_core.tables.UnmappedReason;
import org.ensembl.database.homo_sapiens_core.tables.Xref;
import org.jooq.Catalog;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.9.5"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class HomoSapiensCore_89_37 extends SchemaImpl {

    private static final long serialVersionUID = -400491167;

    /**
     * The reference instance of <code>homo_sapiens_core_89_37</code>
     */
    public static final HomoSapiensCore_89_37 HOMO_SAPIENS_CORE_89_37 = new HomoSapiensCore_89_37();

    /**
     * The table <code>homo_sapiens_core_89_37.alt_allele</code>.
     */
    public final AltAllele ALT_ALLELE = org.ensembl.database.homo_sapiens_core.tables.AltAllele.ALT_ALLELE;

    /**
     * The table <code>homo_sapiens_core_89_37.alt_allele_attrib</code>.
     */
    public final AltAlleleAttrib ALT_ALLELE_ATTRIB = org.ensembl.database.homo_sapiens_core.tables.AltAlleleAttrib.ALT_ALLELE_ATTRIB;

    /**
     * The table <code>homo_sapiens_core_89_37.alt_allele_group</code>.
     */
    public final AltAlleleGroup ALT_ALLELE_GROUP = org.ensembl.database.homo_sapiens_core.tables.AltAlleleGroup.ALT_ALLELE_GROUP;

    /**
     * The table <code>homo_sapiens_core_89_37.analysis</code>.
     */
    public final Analysis ANALYSIS = org.ensembl.database.homo_sapiens_core.tables.Analysis.ANALYSIS;

    /**
     * The table <code>homo_sapiens_core_89_37.analysis_description</code>.
     */
    public final AnalysisDescription ANALYSIS_DESCRIPTION = org.ensembl.database.homo_sapiens_core.tables.AnalysisDescription.ANALYSIS_DESCRIPTION;

    /**
     * The table <code>homo_sapiens_core_89_37.assembly</code>.
     */
    public final Assembly ASSEMBLY = org.ensembl.database.homo_sapiens_core.tables.Assembly.ASSEMBLY;

    /**
     * The table <code>homo_sapiens_core_89_37.assembly_exception</code>.
     */
    public final AssemblyException ASSEMBLY_EXCEPTION = org.ensembl.database.homo_sapiens_core.tables.AssemblyException.ASSEMBLY_EXCEPTION;

    /**
     * The table <code>homo_sapiens_core_89_37.associated_group</code>.
     */
    public final AssociatedGroup ASSOCIATED_GROUP = org.ensembl.database.homo_sapiens_core.tables.AssociatedGroup.ASSOCIATED_GROUP;

    /**
     * The table <code>homo_sapiens_core_89_37.associated_xref</code>.
     */
    public final AssociatedXref ASSOCIATED_XREF = org.ensembl.database.homo_sapiens_core.tables.AssociatedXref.ASSOCIATED_XREF;

    /**
     * The table <code>homo_sapiens_core_89_37.attrib_type</code>.
     */
    public final AttribType ATTRIB_TYPE = org.ensembl.database.homo_sapiens_core.tables.AttribType.ATTRIB_TYPE;

    /**
     * The table <code>homo_sapiens_core_89_37.coord_system</code>.
     */
    public final CoordSystem COORD_SYSTEM = org.ensembl.database.homo_sapiens_core.tables.CoordSystem.COORD_SYSTEM;

    /**
     * The table <code>homo_sapiens_core_89_37.data_file</code>.
     */
    public final DataFile DATA_FILE = org.ensembl.database.homo_sapiens_core.tables.DataFile.DATA_FILE;

    /**
     * The table <code>homo_sapiens_core_89_37.density_feature</code>.
     */
    public final DensityFeature DENSITY_FEATURE = org.ensembl.database.homo_sapiens_core.tables.DensityFeature.DENSITY_FEATURE;

    /**
     * The table <code>homo_sapiens_core_89_37.density_type</code>.
     */
    public final DensityType DENSITY_TYPE = org.ensembl.database.homo_sapiens_core.tables.DensityType.DENSITY_TYPE;

    /**
     * The table <code>homo_sapiens_core_89_37.dependent_xref</code>.
     */
    public final DependentXref DEPENDENT_XREF = org.ensembl.database.homo_sapiens_core.tables.DependentXref.DEPENDENT_XREF;

    /**
     * The table <code>homo_sapiens_core_89_37.ditag</code>.
     */
    public final Ditag DITAG = org.ensembl.database.homo_sapiens_core.tables.Ditag.DITAG;

    /**
     * The table <code>homo_sapiens_core_89_37.ditag_feature</code>.
     */
    public final DitagFeature DITAG_FEATURE = org.ensembl.database.homo_sapiens_core.tables.DitagFeature.DITAG_FEATURE;

    /**
     * The table <code>homo_sapiens_core_89_37.dna</code>.
     */
    public final Dna DNA = org.ensembl.database.homo_sapiens_core.tables.Dna.DNA;

    /**
     * The table <code>homo_sapiens_core_89_37.dna_align_feature</code>.
     */
    public final DnaAlignFeature DNA_ALIGN_FEATURE = org.ensembl.database.homo_sapiens_core.tables.DnaAlignFeature.DNA_ALIGN_FEATURE;

    /**
     * The table <code>homo_sapiens_core_89_37.dna_align_feature_attrib</code>.
     */
    public final DnaAlignFeatureAttrib DNA_ALIGN_FEATURE_ATTRIB = org.ensembl.database.homo_sapiens_core.tables.DnaAlignFeatureAttrib.DNA_ALIGN_FEATURE_ATTRIB;

    /**
     * The table <code>homo_sapiens_core_89_37.exon</code>.
     */
    public final Exon EXON = org.ensembl.database.homo_sapiens_core.tables.Exon.EXON;

    /**
     * The table <code>homo_sapiens_core_89_37.exon_transcript</code>.
     */
    public final ExonTranscript EXON_TRANSCRIPT = org.ensembl.database.homo_sapiens_core.tables.ExonTranscript.EXON_TRANSCRIPT;

    /**
     * The table <code>homo_sapiens_core_89_37.external_db</code>.
     */
    public final ExternalDb EXTERNAL_DB = org.ensembl.database.homo_sapiens_core.tables.ExternalDb.EXTERNAL_DB;

    /**
     * The table <code>homo_sapiens_core_89_37.external_synonym</code>.
     */
    public final ExternalSynonym EXTERNAL_SYNONYM = org.ensembl.database.homo_sapiens_core.tables.ExternalSynonym.EXTERNAL_SYNONYM;

    /**
     * The table <code>homo_sapiens_core_89_37.gene</code>.
     */
    public final Gene GENE = org.ensembl.database.homo_sapiens_core.tables.Gene.GENE;

    /**
     * The table <code>homo_sapiens_core_89_37.gene_archive</code>.
     */
    public final GeneArchive GENE_ARCHIVE = org.ensembl.database.homo_sapiens_core.tables.GeneArchive.GENE_ARCHIVE;

    /**
     * The table <code>homo_sapiens_core_89_37.gene_attrib</code>.
     */
    public final GeneAttrib GENE_ATTRIB = org.ensembl.database.homo_sapiens_core.tables.GeneAttrib.GENE_ATTRIB;

    /**
     * The table <code>homo_sapiens_core_89_37.genome_statistics</code>.
     */
    public final GenomeStatistics GENOME_STATISTICS = org.ensembl.database.homo_sapiens_core.tables.GenomeStatistics.GENOME_STATISTICS;

    /**
     * The table <code>homo_sapiens_core_89_37.identity_xref</code>.
     */
    public final IdentityXref IDENTITY_XREF = org.ensembl.database.homo_sapiens_core.tables.IdentityXref.IDENTITY_XREF;

    /**
     * The table <code>homo_sapiens_core_89_37.interpro</code>.
     */
    public final Interpro INTERPRO = org.ensembl.database.homo_sapiens_core.tables.Interpro.INTERPRO;

    /**
     * The table <code>homo_sapiens_core_89_37.intron_supporting_evidence</code>.
     */
    public final IntronSupportingEvidence INTRON_SUPPORTING_EVIDENCE = org.ensembl.database.homo_sapiens_core.tables.IntronSupportingEvidence.INTRON_SUPPORTING_EVIDENCE;

    /**
     * The table <code>homo_sapiens_core_89_37.karyotype</code>.
     */
    public final Karyotype KARYOTYPE = org.ensembl.database.homo_sapiens_core.tables.Karyotype.KARYOTYPE;

    /**
     * The table <code>homo_sapiens_core_89_37.map</code>.
     */
    public final Map MAP = org.ensembl.database.homo_sapiens_core.tables.Map.MAP;

    /**
     * The table <code>homo_sapiens_core_89_37.mapping_session</code>.
     */
    public final MappingSession MAPPING_SESSION = org.ensembl.database.homo_sapiens_core.tables.MappingSession.MAPPING_SESSION;

    /**
     * The table <code>homo_sapiens_core_89_37.mapping_set</code>.
     */
    public final MappingSet MAPPING_SET = org.ensembl.database.homo_sapiens_core.tables.MappingSet.MAPPING_SET;

    /**
     * The table <code>homo_sapiens_core_89_37.marker</code>.
     */
    public final Marker MARKER = org.ensembl.database.homo_sapiens_core.tables.Marker.MARKER;

    /**
     * The table <code>homo_sapiens_core_89_37.marker_feature</code>.
     */
    public final MarkerFeature MARKER_FEATURE = org.ensembl.database.homo_sapiens_core.tables.MarkerFeature.MARKER_FEATURE;

    /**
     * The table <code>homo_sapiens_core_89_37.marker_map_location</code>.
     */
    public final MarkerMapLocation MARKER_MAP_LOCATION = org.ensembl.database.homo_sapiens_core.tables.MarkerMapLocation.MARKER_MAP_LOCATION;

    /**
     * The table <code>homo_sapiens_core_89_37.marker_synonym</code>.
     */
    public final MarkerSynonym MARKER_SYNONYM = org.ensembl.database.homo_sapiens_core.tables.MarkerSynonym.MARKER_SYNONYM;

    /**
     * The table <code>homo_sapiens_core_89_37.meta</code>.
     */
    public final Meta META = org.ensembl.database.homo_sapiens_core.tables.Meta.META;

    /**
     * The table <code>homo_sapiens_core_89_37.meta_coord</code>.
     */
    public final MetaCoord META_COORD = org.ensembl.database.homo_sapiens_core.tables.MetaCoord.META_COORD;

    /**
     * The table <code>homo_sapiens_core_89_37.misc_attrib</code>.
     */
    public final MiscAttrib MISC_ATTRIB = org.ensembl.database.homo_sapiens_core.tables.MiscAttrib.MISC_ATTRIB;

    /**
     * The table <code>homo_sapiens_core_89_37.misc_feature</code>.
     */
    public final MiscFeature MISC_FEATURE = org.ensembl.database.homo_sapiens_core.tables.MiscFeature.MISC_FEATURE;

    /**
     * The table <code>homo_sapiens_core_89_37.misc_feature_misc_set</code>.
     */
    public final MiscFeatureMiscSet MISC_FEATURE_MISC_SET = org.ensembl.database.homo_sapiens_core.tables.MiscFeatureMiscSet.MISC_FEATURE_MISC_SET;

    /**
     * The table <code>homo_sapiens_core_89_37.misc_set</code>.
     */
    public final MiscSet MISC_SET = org.ensembl.database.homo_sapiens_core.tables.MiscSet.MISC_SET;

    /**
     * The table <code>homo_sapiens_core_89_37.object_xref</code>.
     */
    public final ObjectXref OBJECT_XREF = org.ensembl.database.homo_sapiens_core.tables.ObjectXref.OBJECT_XREF;

    /**
     * The table <code>homo_sapiens_core_89_37.ontology_xref</code>.
     */
    public final OntologyXref ONTOLOGY_XREF = org.ensembl.database.homo_sapiens_core.tables.OntologyXref.ONTOLOGY_XREF;

    /**
     * The table <code>homo_sapiens_core_89_37.operon</code>.
     */
    public final Operon OPERON = org.ensembl.database.homo_sapiens_core.tables.Operon.OPERON;

    /**
     * The table <code>homo_sapiens_core_89_37.operon_transcript</code>.
     */
    public final OperonTranscript OPERON_TRANSCRIPT = org.ensembl.database.homo_sapiens_core.tables.OperonTranscript.OPERON_TRANSCRIPT;

    /**
     * The table <code>homo_sapiens_core_89_37.operon_transcript_gene</code>.
     */
    public final OperonTranscriptGene OPERON_TRANSCRIPT_GENE = org.ensembl.database.homo_sapiens_core.tables.OperonTranscriptGene.OPERON_TRANSCRIPT_GENE;

    /**
     * The table <code>homo_sapiens_core_89_37.peptide_archive</code>.
     */
    public final PeptideArchive PEPTIDE_ARCHIVE = org.ensembl.database.homo_sapiens_core.tables.PeptideArchive.PEPTIDE_ARCHIVE;

    /**
     * The table <code>homo_sapiens_core_89_37.prediction_exon</code>.
     */
    public final PredictionExon PREDICTION_EXON = org.ensembl.database.homo_sapiens_core.tables.PredictionExon.PREDICTION_EXON;

    /**
     * The table <code>homo_sapiens_core_89_37.prediction_transcript</code>.
     */
    public final PredictionTranscript PREDICTION_TRANSCRIPT = org.ensembl.database.homo_sapiens_core.tables.PredictionTranscript.PREDICTION_TRANSCRIPT;

    /**
     * The table <code>homo_sapiens_core_89_37.protein_align_feature</code>.
     */
    public final ProteinAlignFeature PROTEIN_ALIGN_FEATURE = org.ensembl.database.homo_sapiens_core.tables.ProteinAlignFeature.PROTEIN_ALIGN_FEATURE;

    /**
     * The table <code>homo_sapiens_core_89_37.protein_feature</code>.
     */
    public final ProteinFeature PROTEIN_FEATURE = org.ensembl.database.homo_sapiens_core.tables.ProteinFeature.PROTEIN_FEATURE;

    /**
     * The table <code>homo_sapiens_core_89_37.repeat_consensus</code>.
     */
    public final RepeatConsensus REPEAT_CONSENSUS = org.ensembl.database.homo_sapiens_core.tables.RepeatConsensus.REPEAT_CONSENSUS;

    /**
     * The table <code>homo_sapiens_core_89_37.repeat_feature</code>.
     */
    public final RepeatFeature REPEAT_FEATURE = org.ensembl.database.homo_sapiens_core.tables.RepeatFeature.REPEAT_FEATURE;

    /**
     * The table <code>homo_sapiens_core_89_37.seq_region</code>.
     */
    public final SeqRegion SEQ_REGION = org.ensembl.database.homo_sapiens_core.tables.SeqRegion.SEQ_REGION;

    /**
     * The table <code>homo_sapiens_core_89_37.seq_region_attrib</code>.
     */
    public final SeqRegionAttrib SEQ_REGION_ATTRIB = org.ensembl.database.homo_sapiens_core.tables.SeqRegionAttrib.SEQ_REGION_ATTRIB;

    /**
     * The table <code>homo_sapiens_core_89_37.seq_region_mapping</code>.
     */
    public final SeqRegionMapping SEQ_REGION_MAPPING = org.ensembl.database.homo_sapiens_core.tables.SeqRegionMapping.SEQ_REGION_MAPPING;

    /**
     * The table <code>homo_sapiens_core_89_37.seq_region_synonym</code>.
     */
    public final SeqRegionSynonym SEQ_REGION_SYNONYM = org.ensembl.database.homo_sapiens_core.tables.SeqRegionSynonym.SEQ_REGION_SYNONYM;

    /**
     * The table <code>homo_sapiens_core_89_37.simple_feature</code>.
     */
    public final SimpleFeature SIMPLE_FEATURE = org.ensembl.database.homo_sapiens_core.tables.SimpleFeature.SIMPLE_FEATURE;

    /**
     * The table <code>homo_sapiens_core_89_37.stable_id_event</code>.
     */
    public final StableIdEvent STABLE_ID_EVENT = org.ensembl.database.homo_sapiens_core.tables.StableIdEvent.STABLE_ID_EVENT;

    /**
     * The table <code>homo_sapiens_core_89_37.supporting_feature</code>.
     */
    public final SupportingFeature SUPPORTING_FEATURE = org.ensembl.database.homo_sapiens_core.tables.SupportingFeature.SUPPORTING_FEATURE;

    /**
     * The table <code>homo_sapiens_core_89_37.transcript</code>.
     */
    public final Transcript TRANSCRIPT = org.ensembl.database.homo_sapiens_core.tables.Transcript.TRANSCRIPT;

    /**
     * The table <code>homo_sapiens_core_89_37.transcript_attrib</code>.
     */
    public final TranscriptAttrib TRANSCRIPT_ATTRIB = org.ensembl.database.homo_sapiens_core.tables.TranscriptAttrib.TRANSCRIPT_ATTRIB;

    /**
     * The table <code>homo_sapiens_core_89_37.transcript_intron_supporting_evidence</code>.
     */
    public final TranscriptIntronSupportingEvidence TRANSCRIPT_INTRON_SUPPORTING_EVIDENCE = org.ensembl.database.homo_sapiens_core.tables.TranscriptIntronSupportingEvidence.TRANSCRIPT_INTRON_SUPPORTING_EVIDENCE;

    /**
     * The table <code>homo_sapiens_core_89_37.transcript_supporting_feature</code>.
     */
    public final TranscriptSupportingFeature TRANSCRIPT_SUPPORTING_FEATURE = org.ensembl.database.homo_sapiens_core.tables.TranscriptSupportingFeature.TRANSCRIPT_SUPPORTING_FEATURE;

    /**
     * The table <code>homo_sapiens_core_89_37.translation</code>.
     */
    public final Translation TRANSLATION = org.ensembl.database.homo_sapiens_core.tables.Translation.TRANSLATION;

    /**
     * The table <code>homo_sapiens_core_89_37.translation_attrib</code>.
     */
    public final TranslationAttrib TRANSLATION_ATTRIB = org.ensembl.database.homo_sapiens_core.tables.TranslationAttrib.TRANSLATION_ATTRIB;

    /**
     * The table <code>homo_sapiens_core_89_37.unmapped_object</code>.
     */
    public final UnmappedObject UNMAPPED_OBJECT = org.ensembl.database.homo_sapiens_core.tables.UnmappedObject.UNMAPPED_OBJECT;

    /**
     * The table <code>homo_sapiens_core_89_37.unmapped_reason</code>.
     */
    public final UnmappedReason UNMAPPED_REASON = org.ensembl.database.homo_sapiens_core.tables.UnmappedReason.UNMAPPED_REASON;

    /**
     * The table <code>homo_sapiens_core_89_37.xref</code>.
     */
    public final Xref XREF = org.ensembl.database.homo_sapiens_core.tables.Xref.XREF;

    /**
     * No further instances allowed
     */
    private HomoSapiensCore_89_37() {
        super("homo_sapiens_core_89_37", null);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Table<?>> getTables() {
        List result = new ArrayList();
        result.addAll(getTables0());
        return result;
    }

    private final List<Table<?>> getTables0() {
        return Arrays.<Table<?>>asList(
            AltAllele.ALT_ALLELE,
            AltAlleleAttrib.ALT_ALLELE_ATTRIB,
            AltAlleleGroup.ALT_ALLELE_GROUP,
            Analysis.ANALYSIS,
            AnalysisDescription.ANALYSIS_DESCRIPTION,
            Assembly.ASSEMBLY,
            AssemblyException.ASSEMBLY_EXCEPTION,
            AssociatedGroup.ASSOCIATED_GROUP,
            AssociatedXref.ASSOCIATED_XREF,
            AttribType.ATTRIB_TYPE,
            CoordSystem.COORD_SYSTEM,
            DataFile.DATA_FILE,
            DensityFeature.DENSITY_FEATURE,
            DensityType.DENSITY_TYPE,
            DependentXref.DEPENDENT_XREF,
            Ditag.DITAG,
            DitagFeature.DITAG_FEATURE,
            Dna.DNA,
            DnaAlignFeature.DNA_ALIGN_FEATURE,
            DnaAlignFeatureAttrib.DNA_ALIGN_FEATURE_ATTRIB,
            Exon.EXON,
            ExonTranscript.EXON_TRANSCRIPT,
            ExternalDb.EXTERNAL_DB,
            ExternalSynonym.EXTERNAL_SYNONYM,
            Gene.GENE,
            GeneArchive.GENE_ARCHIVE,
            GeneAttrib.GENE_ATTRIB,
            GenomeStatistics.GENOME_STATISTICS,
            IdentityXref.IDENTITY_XREF,
            Interpro.INTERPRO,
            IntronSupportingEvidence.INTRON_SUPPORTING_EVIDENCE,
            Karyotype.KARYOTYPE,
            Map.MAP,
            MappingSession.MAPPING_SESSION,
            MappingSet.MAPPING_SET,
            Marker.MARKER,
            MarkerFeature.MARKER_FEATURE,
            MarkerMapLocation.MARKER_MAP_LOCATION,
            MarkerSynonym.MARKER_SYNONYM,
            Meta.META,
            MetaCoord.META_COORD,
            MiscAttrib.MISC_ATTRIB,
            MiscFeature.MISC_FEATURE,
            MiscFeatureMiscSet.MISC_FEATURE_MISC_SET,
            MiscSet.MISC_SET,
            ObjectXref.OBJECT_XREF,
            OntologyXref.ONTOLOGY_XREF,
            Operon.OPERON,
            OperonTranscript.OPERON_TRANSCRIPT,
            OperonTranscriptGene.OPERON_TRANSCRIPT_GENE,
            PeptideArchive.PEPTIDE_ARCHIVE,
            PredictionExon.PREDICTION_EXON,
            PredictionTranscript.PREDICTION_TRANSCRIPT,
            ProteinAlignFeature.PROTEIN_ALIGN_FEATURE,
            ProteinFeature.PROTEIN_FEATURE,
            RepeatConsensus.REPEAT_CONSENSUS,
            RepeatFeature.REPEAT_FEATURE,
            SeqRegion.SEQ_REGION,
            SeqRegionAttrib.SEQ_REGION_ATTRIB,
            SeqRegionMapping.SEQ_REGION_MAPPING,
            SeqRegionSynonym.SEQ_REGION_SYNONYM,
            SimpleFeature.SIMPLE_FEATURE,
            StableIdEvent.STABLE_ID_EVENT,
            SupportingFeature.SUPPORTING_FEATURE,
            Transcript.TRANSCRIPT,
            TranscriptAttrib.TRANSCRIPT_ATTRIB,
            TranscriptIntronSupportingEvidence.TRANSCRIPT_INTRON_SUPPORTING_EVIDENCE,
            TranscriptSupportingFeature.TRANSCRIPT_SUPPORTING_FEATURE,
            Translation.TRANSLATION,
            TranslationAttrib.TRANSLATION_ATTRIB,
            UnmappedObject.UNMAPPED_OBJECT,
            UnmappedReason.UNMAPPED_REASON,
            Xref.XREF);
    }
}