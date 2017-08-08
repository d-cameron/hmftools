package com.hartwig.hmftools.patientreporter.variants;

import static com.hartwig.hmftools.common.variant.VariantAnnotationTest.createVariantAnnotationBuilder;

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.google.common.collect.Lists;
import com.hartwig.hmftools.common.region.GenomeRegion;
import com.hartwig.hmftools.common.region.bed.ImmutableBEDGenomeRegion;
import com.hartwig.hmftools.common.region.hmfslicer.HmfGenomeRegion;
import com.hartwig.hmftools.common.region.hmfslicer.ImmutableHmfGenomeRegion;
import com.hartwig.hmftools.common.slicing.HmfSlicer;
import com.hartwig.hmftools.common.slicing.Slicer;
import com.hartwig.hmftools.common.slicing.SlicerFactory;
import com.hartwig.hmftools.common.variant.SomaticVariant;
import com.hartwig.hmftools.common.variant.VariantAnnotation;
import com.hartwig.hmftools.common.variant.VariantConsequence;
import com.hartwig.hmftools.common.variant.VariantType;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class VariantAnalyzerTest {

    private static final String CHROMOSOME = "X";
    private static final String PASS_FILTER = "PASS";

    private static final String RIGHT_FEATURE_TYPE = "transcript";
    private static final String WRONG_FEATURE_TYPE = "sequence_feature";
    private static final String RIGHT_TRANSCRIPT = "TR";
    private static final String WRONG_TRANSCRIPT = "RT";
    private static final int TRANSCRIPT_VERSION = 1;
    private static final String GENE = "(KODU)";
    private static final String GENE_ID = "ENSG0000";
    private static final long GENE_START = 1;
    private static final long GENE_END = 42;
    private static final String CHROMOSOME_BAND = "p1";
    private static final String ENTREZ_ID = "11";

    @Test
    public void realCaseWorks() {
        final HmfSlicer hmfSlicingRegion = SlicerFactory.hmfSlicerFromSingleGenomeRegion(hmfRegion());
        final Slicer giabHighConfidenceRegion = SlicerFactory.fromSingleGenomeRegion(region(100, 1000));
        final Slicer cpctSlicingRegion = SlicerFactory.fromSingleGenomeRegion(region(400, 500));

        final VariantAnalyzer analyzer = VariantAnalyzer.fromSlicingRegions(hmfSlicingRegion, giabHighConfidenceRegion, cpctSlicingRegion);

        final VariantAnnotation rightAnnotation =
                createVariantAnnotationBuilder(VariantConsequence.MISSENSE_VARIANT).featureType(RIGHT_FEATURE_TYPE).
                        featureID(RIGHT_TRANSCRIPT).build();

        final VariantAnnotation wrongTranscript =
                createVariantAnnotationBuilder(VariantConsequence.MISSENSE_VARIANT).featureType(RIGHT_FEATURE_TYPE)
                        .featureID(WRONG_TRANSCRIPT)
                        .build();

        final VariantAnnotation wrongFeatureType =
                createVariantAnnotationBuilder(VariantConsequence.MISSENSE_VARIANT).featureType(WRONG_FEATURE_TYPE).
                        featureID(RIGHT_TRANSCRIPT).build();

        final VariantAnnotation wrongConsequence = createVariantAnnotationBuilder(VariantConsequence.OTHER).featureType(RIGHT_FEATURE_TYPE).
                featureID(RIGHT_TRANSCRIPT).build();

        final List<SomaticVariant> variants =
                Lists.newArrayList(builder().position(420).annotations(Lists.newArrayList(rightAnnotation, wrongTranscript)).build(),
                        builder().position(430).annotations(Lists.newArrayList(wrongConsequence)).build(),
                        builder().position(440).annotations(Lists.newArrayList(wrongFeatureType)).build(),
                        builder().position(460).annotations(Lists.newArrayList(rightAnnotation)).build());

        final VariantAnalysis analysis = analyzer.run(variants);

        assertEquals(4, analysis.allVariants().size());
        assertEquals(4, analysis.passedVariants().size());
        assertEquals(4, analysis.consensusPassedVariants().size());
        assertEquals(3, analysis.mutationalLoad());
        assertEquals(1, analysis.findings().size());
    }

    @NotNull
    private static SomaticVariant.Builder builder() {
        return new SomaticVariant.Builder().type(VariantType.SNP).chromosome(CHROMOSOME).filter(PASS_FILTER);
    }

    @NotNull
    private static GenomeRegion region(final long start, final long end) {
        return ImmutableBEDGenomeRegion.of(CHROMOSOME, start, end);
    }

    @NotNull
    private static HmfGenomeRegion hmfRegion() {
        return new ImmutableHmfGenomeRegion(CHROMOSOME, 350, 450, RIGHT_TRANSCRIPT, TRANSCRIPT_VERSION, GENE, GENE_ID, GENE_START, GENE_END,
                CHROMOSOME_BAND, ENTREZ_ID);
    }
}
