package com.hartwig.hmftools.patientreporter.report;

import static com.hartwig.hmftools.patientreporter.PatientReporterTestFactory.createTestCopyNumberBuilder;
import static com.hartwig.hmftools.patientreporter.PatientReporterTestUtil.testBaseReportData;
import static com.hartwig.hmftools.patientreporter.PatientReporterTestUtil.testSampleReport;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import com.hartwig.hmftools.common.actionability.ActionabilitySource;
import com.hartwig.hmftools.common.actionability.EvidenceItem;
import com.hartwig.hmftools.common.actionability.ImmutableEvidenceItem;
import com.hartwig.hmftools.common.drivercatalog.DriverCategory;
import com.hartwig.hmftools.common.fusions.KnownFusionsModel;
import com.hartwig.hmftools.common.purple.PurityAdjuster;
import com.hartwig.hmftools.common.purple.gender.Gender;
import com.hartwig.hmftools.common.purple.gene.GeneCopyNumber;
import com.hartwig.hmftools.common.purple.purity.FittedPurity;
import com.hartwig.hmftools.common.purple.purity.ImmutableFittedPurity;
import com.hartwig.hmftools.common.variant.Clonality;
import com.hartwig.hmftools.common.variant.Hotspot;
import com.hartwig.hmftools.common.variant.structural.StructuralVariantType;
import com.hartwig.hmftools.patientreporter.AnalysedPatientReport;
import com.hartwig.hmftools.patientreporter.BaseReportData;
import com.hartwig.hmftools.patientreporter.ImmutableAnalysedPatientReport;
import com.hartwig.hmftools.patientreporter.ImmutableNotAnalysedPatientReport;
import com.hartwig.hmftools.patientreporter.NotAnalysedPatientReport;
import com.hartwig.hmftools.patientreporter.SampleReport;
import com.hartwig.hmftools.patientreporter.actionability.ClinicalTrial;
import com.hartwig.hmftools.patientreporter.actionability.ImmutableClinicalTrial;
import com.hartwig.hmftools.patientreporter.chord.ChordAnalysis;
import com.hartwig.hmftools.patientreporter.chord.ImmutableChordAnalysis;
import com.hartwig.hmftools.patientreporter.germline.GermlineVariant;
import com.hartwig.hmftools.patientreporter.germline.ImmutableGermlineVariant;
import com.hartwig.hmftools.patientreporter.qcfail.NotAnalysableReason;
import com.hartwig.hmftools.patientreporter.qcfail.NotAnalysableStudy;
import com.hartwig.hmftools.patientreporter.structural.ImmutableReportableGeneDisruption;
import com.hartwig.hmftools.patientreporter.structural.ImmutableReportableGeneFusion;
import com.hartwig.hmftools.patientreporter.structural.ReportableGeneDisruption;
import com.hartwig.hmftools.patientreporter.structural.ReportableGeneFusion;
import com.hartwig.hmftools.patientreporter.variants.ImmutableReportableSomaticVariant;
import com.hartwig.hmftools.patientreporter.variants.ReportableSomaticVariant;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.exception.DRException;

public class PDFWriterTest {

    private static final boolean WRITE_TO_PDF = false;

    private static final String REPORT_BASE_DIR = System.getProperty("user.home") + File.separator + "hmf" + File.separator + "tmp";

    @Test
    public void canGenerateSequenceReport() throws DRException, IOException {
        final double pathologyTumorPercentage = 0.6;
        final double impliedTumorPurity = 0.58;
        final double averageTumorPloidy = 2.53;
        final int tumorMutationalLoad = 361;
        final double tumorMutationalBurden = 10.1;
        final double microsatelliteIndelsPerMb = 2.1;

        final BaseReportData baseReportData = testBaseReportData();
        final FittedPurity fittedPurity = createFittedPurity(impliedTumorPurity, averageTumorPloidy);

        final PurityAdjuster purityAdjuster = new PurityAdjuster(Gender.MALE, fittedPurity);
        final List<EvidenceItem> clinicalEvidence = createTestClinicalEvidence();
        final List<ClinicalTrial> clinicalTrials = createTestClinicalTrials();
        final List<ReportableSomaticVariant> somaticVariants = createTestSomaticVariants(purityAdjuster);
        final List<GermlineVariant> germlineVariants = createTestGermlineVariants(purityAdjuster);
        final ChordAnalysis chordAnalysis = createTestChordAnalysis();
        final List<GeneCopyNumber> copyNumbers = createTestCopyNumbers();
        final List<ReportableGeneFusion> fusions = createTestFusions();
        final List<ReportableGeneDisruption> disruptions = createTestDisruptions();

        final SampleReport sampleReport = testSampleReport(pathologyTumorPercentage);

        final AnalysedPatientReport patientReport = ImmutableAnalysedPatientReport.of(sampleReport,
                fittedPurity.purity(),
                true,
                fittedPurity.ploidy(),
                clinicalEvidence,
                clinicalTrials,
                somaticVariants,
                microsatelliteIndelsPerMb,
                tumorMutationalLoad,
                tumorMutationalBurden,
                chordAnalysis,
                germlineVariants.size() > 0,
                germlineVariants,
                copyNumbers,
                fusions,
                disruptions,
                Resources.getResource("circos/circos_example.png").getPath(),
                Optional.of("this is a test report and does not relate to any real CPCT patient"),
                baseReportData.signaturePath(),
                baseReportData.logoRVAPath());

        final JasperReportBuilder mainReport = PDFWriter.generatePatientReport(patientReport);
        assertNotNull(mainReport);

        if (WRITE_TO_PDF) {
            mainReport.toPdf(new FileOutputStream(REPORT_BASE_DIR + File.separator + "hmf_test_sequence_report.pdf"));
        }
    }

    @NotNull
    private static List<EvidenceItem> createTestClinicalEvidence() {
        List<EvidenceItem> evidenceItems = Lists.newArrayList();
        evidenceItems.add(ImmutableEvidenceItem.builder()
                .event("TP53 p.Pro177_Cys182del")
                .drug("Docetaxel")
                .drugsType("Chemotherapy")
                .level("D")
                .response("Resistant")
                .reference("variant:222")
                .source(ActionabilitySource.CIVIC)
                .isOnLabel(false)
                .build());

        evidenceItems.add(ImmutableEvidenceItem.builder()
                .event("CDK4 Amplification")
                .drug("LEE011")
                .drugsType("CDK4/6 inhibitor")
                .level("C")
                .response("Responsive")
                .reference("")
                .source(ActionabilitySource.CGI)
                .isOnLabel(true)
                .build());

        return evidenceItems;
    }

    @NotNull
    private static List<ClinicalTrial> createTestClinicalTrials() {
        List<ClinicalTrial> trials = Lists.newArrayList();
        trials.add(ImmutableClinicalTrial.builder()
                .event("BRAF p.Val600Glu")
                .acronym("IMPemBra")
                .reference("EXT8846 (NL54421.031.15)")
                .source(ActionabilitySource.ICLUSION)
                .isOnLabel(true)
                .level("C")
                .build());
        return trials;
    }

    @NotNull
    private static List<GermlineVariant> createTestGermlineVariants(@NotNull PurityAdjuster purityAdjuster) {
        List<GermlineVariant> germlineVariants = Lists.newArrayList();

        int totalReads = 112;
        int altReads = 67;
        double adjustedCopyNumber = 3D;

        double adjustedVAF =
                purityAdjuster.purityAdjustedVAFWithHeterozygousNormal("13", adjustedCopyNumber, (double) altReads / (double) totalReads);

        germlineVariants.add(ImmutableGermlineVariant.builder()
                .passFilter(true)
                .gene("BRCA2")
                .hgvsCodingImpact("c.5946delT")
                .hgvsProteinImpact("p.Ser1982fs")
                .totalReadCount(totalReads)
                .alleleReadCount(altReads)
                .germlineStatus("HET")
                .adjustedCopyNumber(adjustedCopyNumber)
                .adjustedVAF(adjustedVAF)
                .minorAllelePloidy(1D)
                .biallelic(false)
                .build());

        return germlineVariants;
    }

    @NotNull
    private static ChordAnalysis createTestChordAnalysis() {
        double brca1Value = 0.10;
        double brca2Value = 0.86;

        return ImmutableChordAnalysis.builder()
                .noneValue(1 - (brca1Value + brca2Value))
                .BRCA1Value(brca1Value)
                .BRCA2Value(brca2Value)
                .hrdValue(brca1Value + brca2Value)
                .predictedResponseValue(brca1Value + brca2Value > 0.5 ? 1 : 0)
                .build();
    }

    @NotNull
    private static FittedPurity createFittedPurity(double impliedPurity, double averageTumorPloidy) {
        return ImmutableFittedPurity.builder()
                .purity(impliedPurity)
                .diploidProportion(0)
                .normFactor(0)
                .score(0)
                .ploidy(averageTumorPloidy)
                .somaticDeviation(0)
                .build();
    }

    @NotNull
    private static List<ReportableSomaticVariant> createTestSomaticVariants(@NotNull final PurityAdjuster purityAdjuster) {
        final ReportableSomaticVariant variant1 = ImmutableReportableSomaticVariant.builder()
                .gene("BRAF")
                .isDrupActionable(true)
                .hgvsCodingImpact("c.1799T>A")
                .hgvsProteinImpact("p.Val600Glu")
                .hotspot(Hotspot.HOTSPOT)
                .clonality(Clonality.CLONAL)
                .alleleReadCount(18)
                .totalReadCount(99)
                .adjustedCopyNumber(4)
                .minorAllelePloidy(1)
                .biallelic(false)
                .adjustedVAF(purityAdjuster.purityAdjustedVAF("7", 4, 0.18 / 0.99))
                .driverCategory(DriverCategory.ONCO)
                .driverLikelihood(1D)
                .build();

        final ReportableSomaticVariant variant2 = ImmutableReportableSomaticVariant.builder()
                .gene("TP53")
                .isDrupActionable(false)
                .hgvsCodingImpact("c.821_826delTTTGTG")
                .hgvsProteinImpact("p.Val274_Cus275del")
                .hotspot(Hotspot.NON_HOTSPOT)
                .clonality(Clonality.CLONAL)
                .alleleReadCount(20)
                .totalReadCount(87)
                .adjustedCopyNumber(3)
                .minorAllelePloidy(0)
                .biallelic(false)
                .adjustedVAF(purityAdjuster.purityAdjustedVAF("17", 3, 0.20 / 0.87))
                .driverCategory(DriverCategory.TSG)
                .driverLikelihood(0.5)
                .build();

        final ReportableSomaticVariant variant3 = ImmutableReportableSomaticVariant.builder()
                .gene("MYC")
                .isDrupActionable(false)
                .hgvsCodingImpact("c.15_16delinsCA")
                .hgvsProteinImpact("p.Val6Ile")
                .hotspot(Hotspot.NON_HOTSPOT)
                .clonality(Clonality.CLONAL)
                .alleleReadCount(20)
                .totalReadCount(88)
                .adjustedCopyNumber(2)
                .minorAllelePloidy(1)
                .biallelic(false)
                .adjustedVAF(purityAdjuster.purityAdjustedVAF("8", 2, 0.2 / 0.88))
                .driverCategory(DriverCategory.ONCO)
                .driverLikelihood(0.1)
                .build();

        return Lists.newArrayList(variant1, variant2, variant3);
    }

    @NotNull
    private static List<GeneCopyNumber> createTestCopyNumbers() {
        final GeneCopyNumber copyNumber1 = createTestCopyNumberBuilder().chromosome("17")
                .chromosomeBand("p13.1")
                .gene("TP53")
                .minCopyNumber(0)
                .maxCopyNumber(2)
                .build();

        final GeneCopyNumber copyNumber2 = createTestCopyNumberBuilder().chromosome("9")
                .chromosomeBand("p21.3")
                .gene("CDKN2A")
                .minCopyNumber(0)
                .maxCopyNumber(0)
                .build();

        final GeneCopyNumber copyNumber3 = createTestCopyNumberBuilder().chromosome("17")
                .chromosomeBand("q12")
                .gene("ERBB2")
                .minCopyNumber(11)
                .maxCopyNumber(11)
                .build();
        return Lists.newArrayList(copyNumber1, copyNumber2, copyNumber3);
    }

    @NotNull
    private static List<ReportableGeneFusion> createTestFusions() {
        ReportableGeneFusion fusion1 = ImmutableReportableGeneFusion.builder()
                .geneStart("TMPRSS2")
                .geneStartTranscript("ENST00000398585")
                .geneContextStart("Intron 5")
                .geneEnd("PNPLA7")
                .geneEndTranscript("ENST00000406427")
                .geneContextEnd("Intron 3")
                .ploidy(0.4)
                .source(KnownFusionsModel.CIVIC)
                .build();

        ReportableGeneFusion fusion2 = ImmutableReportableGeneFusion.builder()
                .geneStart("CLCN6")
                .geneStartTranscript("ENST00000346436")
                .geneContextStart("Intron 1")
                .geneEnd("BRAF")
                .geneEndTranscript("ENST00000288602")
                .geneContextEnd("Intron 8")
                .ploidy(1D)
                .source(KnownFusionsModel.ONCOKB)
                .build();

        return Lists.newArrayList(fusion1, fusion2);
    }

    @NotNull
    private static List<ReportableGeneDisruption> createTestDisruptions() {
        ReportableGeneDisruption disruption1 = createDisruptionBuilder().location("2q34")
                .gene("ERBB4")
                .range("Intron 4 -> Intron 9")
                .type(StructuralVariantType.INV)
                .ploidy(1D)
                .geneMinCopies(1)
                .geneMaxCopies(1)
                .build();

        ReportableGeneDisruption disruption2 = createDisruptionBuilder().location("17q12")
                .gene("CDK12")
                .range("Intron 12 Downstream")
                .type(StructuralVariantType.BND)
                .ploidy(2.3)
                .geneMinCopies(2)
                .geneMaxCopies(4)
                .build();

        ReportableGeneDisruption disruption3 = createDisruptionBuilder().location("21q22.12")
                .gene("RUNX1")
                .range("Promoter Region Upstream")
                .type(StructuralVariantType.INS)
                .ploidy(0.8)
                .geneMinCopies(1)
                .geneMaxCopies(1)
                .build();

        ReportableGeneDisruption disruption4 = createDisruptionBuilder().location("1p13.1")
                .gene("CD58")
                .range("Intron 2 Upstream")
                .type(StructuralVariantType.DUP)
                .ploidy(0.2)
                .geneMinCopies(4)
                .geneMaxCopies(4)
                .build();

        return Lists.newArrayList(disruption1, disruption2, disruption3, disruption4);
    }

    @Test
    public void canGenerateLowTumorPercentageReport() throws DRException, IOException {
        final JasperReportBuilder report = generateNotAnalysableCPCTReport(0.1, NotAnalysableReason.LOW_TUMOR_PERCENTAGE);
        assertNotNull(report);

        if (WRITE_TO_PDF) {
            report.toPdf(new FileOutputStream(REPORT_BASE_DIR + File.separator + "hmf_low_tumor_percentage_report.pdf"));
        }
    }

    @Test
    public void canGenerateLowDNAYieldReport() throws DRException, IOException {
        final JasperReportBuilder report = generateNotAnalysableCPCTReport(0.6, NotAnalysableReason.LOW_DNA_YIELD);
        assertNotNull(report);

        if (WRITE_TO_PDF) {
            report.toPdf(new FileOutputStream(REPORT_BASE_DIR + File.separator + "hmf_low_dna_yield_report.pdf"));
        }
    }

    @Test
    public void canGeneratePostDNAIsolationFailReport() throws DRException, IOException {
        final JasperReportBuilder report = generateNotAnalysableCPCTReport(0.6, NotAnalysableReason.POST_ANALYSIS_FAIL);
        assertNotNull(report);

        if (WRITE_TO_PDF) {
            report.toPdf(new FileOutputStream(REPORT_BASE_DIR + File.separator + "hmf_post_dna_isolation_fail_report.pdf"));
        }
    }

    @NotNull
    private static JasperReportBuilder generateNotAnalysableCPCTReport(final double pathologyTumorEstimate,
            @NotNull final NotAnalysableReason reason) throws IOException {
        final NotAnalysedPatientReport patientReport = ImmutableNotAnalysedPatientReport.of(testSampleReport(pathologyTumorEstimate),
                reason,
                NotAnalysableStudy.CPCT,
                Optional.empty(),
                testBaseReportData().signaturePath(),
                testBaseReportData().logoRVAPath());

        return PDFWriter.generateNotAnalysableReport(patientReport);
    }

    @NotNull
    private static ImmutableReportableGeneDisruption.Builder createDisruptionBuilder() {
        return ImmutableReportableGeneDisruption.builder().firstAffectedExon(1);
    }
}