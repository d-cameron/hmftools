package com.hartwig.hmftools.bachelor;

import static com.hartwig.hmftools.bachelor.EligibilityReport.ReportType.GERMLINE_DELETION;
import static com.hartwig.hmftools.bachelor.EligibilityReport.ReportType.SOMATIC_DELETION;
import static com.hartwig.hmftools.bachelor.EligibilityReport.ReportType.SOMATIC_DISRUPTION;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.SortedSetMultimap;
import com.hartwig.hmftools.bachelor.predicates.BlacklistPredicate;
import com.hartwig.hmftools.bachelor.predicates.WhitelistPredicate;
import com.hartwig.hmftools.common.genepanel.HmfGenePanelSupplier;
import com.hartwig.hmftools.common.position.GenomePosition;
import com.hartwig.hmftools.common.position.GenomePositions;
import com.hartwig.hmftools.common.purple.gene.GeneCopyNumber;
import com.hartwig.hmftools.common.region.HmfExonRegion;
import com.hartwig.hmftools.common.region.HmfTranscriptRegion;
import com.hartwig.hmftools.common.variant.snpeff.SnpEffAnnotation;
import com.hartwig.hmftools.common.variant.structural.StructuralVariant;
import com.hartwig.hmftools.common.variant.structural.StructuralVariantType;

import nl.hartwigmedicalfoundation.bachelor.GeneIdentifier;
import nl.hartwigmedicalfoundation.bachelor.OtherEffect;
import nl.hartwigmedicalfoundation.bachelor.Program;
import nl.hartwigmedicalfoundation.bachelor.ProgramPanel;
import nl.hartwigmedicalfoundation.bachelor.SnpEffect;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import htsjdk.samtools.util.CloseableIterator;
import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;

class BachelorEligibility {

    private static final double MAX_COPY_NUMBER_FOR_LOSS = 0.5;

    private static final Logger LOGGER = LogManager.getLogger(BachelorEligibility.class);
    private static final SortedSetMultimap<String, HmfTranscriptRegion> ALL_GENES_BY_CHROMOSOME =
            HmfGenePanelSupplier.allGenesPerChromosomeMap();
    private static final Map<String, HmfTranscriptRegion> ALL_GENES = makeGeneNameMap();
    private static final Map<String, HmfTranscriptRegion> ALL_TRANSCRIPT_IDS = makeTranscriptMap();

    private final List<BachelorProgram> programs = Lists.newArrayList();
    private final Set<HmfTranscriptRegion> variantLocationsToQuery = Sets.newHashSet();

    @NotNull
    private static Map<String, HmfTranscriptRegion> makeGeneNameMap() {
        final Map<String, HmfTranscriptRegion> result = Maps.newHashMap();
        for (final HmfTranscriptRegion region : ALL_GENES_BY_CHROMOSOME.values()) {
            result.put(region.gene(), region);
        }
        return result;
    }

    @NotNull
    private static Map<String, HmfTranscriptRegion> makeTranscriptMap() {
        final Map<String, HmfTranscriptRegion> result = Maps.newHashMap();
        for (final HmfTranscriptRegion region : ALL_GENES_BY_CHROMOSOME.values()) {
            result.put(region.transcriptID(), region);
        }
        return result;
    }

    private BachelorEligibility() {
    }

    @NotNull
    static BachelorEligibility fromMap(@NotNull Map<String, Program> input) {
        final BachelorEligibility result = new BachelorEligibility();

        for (final Program program : input.values()) {

            final Multimap<String, String> geneToEnsemblMap = HashMultimap.create();
            program.getPanel()
                    .stream()
                    .map(ProgramPanel::getGene)
                    .flatMap(Collection::stream)
                    .forEach(g -> geneToEnsemblMap.put(g.getName(), g.getEnsembl()));

            // NOTE: copy number and SVs are untested/unverified for now, but leave in support for them

            // process copy number sections
            final List<Predicate<GeneCopyNumber>> cnvPredicates = Lists.newArrayList();
            for (final ProgramPanel panel : program.getPanel()) {

                final List<GeneIdentifier> genes = panel.getGene();

                if (panel.getEffect().contains(OtherEffect.HOMOZYGOUS_DELETION)) {
                    final Predicate<GeneCopyNumber> geneCopyNumberPredicate =
                            cnv -> genes.stream().anyMatch(g -> g.getEnsembl().equals(cnv.transcriptID()));
                    // TODO: we are matching on transcript ID here but we only have canonical transcripts in our panel file
                    cnvPredicates.add(geneCopyNumberPredicate);
                }
            }

            // process structural variant disruptions
            final List<Predicate<HmfTranscriptRegion>> disruptionPredicates = Lists.newArrayList();
            for (final ProgramPanel panel : program.getPanel()) {

                final List<GeneIdentifier> genes = panel.getGene();

                if (panel.getEffect().contains(OtherEffect.GENE_DISRUPTION)) {
                    final Predicate<HmfTranscriptRegion> disruptionPredicate =
                            sv -> genes.stream().anyMatch(g -> g.getEnsembl().equals(sv.transcriptID()));
                    // TODO: we are matching on transcript ID here but we only have canonical transcripts in our panel file
                    disruptionPredicates.add(disruptionPredicate);
                }
            }

            // process variants from vcf
            final List<Predicate<VariantModel>> panelPredicates = Lists.newArrayList();

            List<String> requiredEffects = Lists.newArrayList();
            List<String> panelTranscripts = Lists.newArrayList();

            for (final ProgramPanel panel : program.getPanel()) {

                final List<GeneIdentifier> genes = panel.getGene();

                // take up a collection of the effects to search for
                requiredEffects = panel.getSnpEffect().stream().map(SnpEffect::value).collect(Collectors.toList());
                panelTranscripts = genes.stream().map(GeneIdentifier::getEnsembl).collect(Collectors.toList());

                final List<String> effects = requiredEffects;

                final Predicate<VariantModel> panelPredicate = v -> genes.stream()
                        .anyMatch(p -> v.sampleAnnotations()
                                .stream()
                                .anyMatch(a -> a.transcript().equals(p.getEnsembl()) && effects.stream()
                                        .anyMatch(x -> a.effects().contains(x))));

                panelPredicates.add(panelPredicate);

                // update query targets
                for (final GeneIdentifier g : genes) {
                    final HmfTranscriptRegion region = ALL_TRANSCRIPT_IDS.get(g.getEnsembl());
                    if (region == null) {
                        final HmfTranscriptRegion namedRegion = ALL_GENES.get(g.getName());
                        if (namedRegion == null) {

                            LOGGER.warn("Program {} gene {} non-canonical transcript {} couldn't find region, transcript will be skipped",
                                    program.getName(),
                                    g.getName(),
                                    g.getEnsembl());

                            // just skip this gene for now
                        } else {
                            result.variantLocationsToQuery.add(namedRegion);
                        }
                    } else {
                        result.variantLocationsToQuery.add(region);
                    }
                }
            }

            final Predicate<VariantModel> inPanel = v -> panelPredicates.stream().anyMatch(p -> p.test(v));

            final Predicate<VariantModel> inBlacklist = new BlacklistPredicate(geneToEnsemblMap.values(), program.getBlacklist());
            final Predicate<VariantModel> inWhitelist = new WhitelistPredicate(geneToEnsemblMap, program.getWhitelist());
            final Predicate<VariantModel> snvPredicate = v -> inPanel.test(v) ? !inBlacklist.test(v) : inWhitelist.test(v);

            final Predicate<GeneCopyNumber> copyNumberPredicate =
                    cnv -> cnvPredicates.stream().anyMatch(p -> p.test(cnv)) && cnv.minCopyNumber() < MAX_COPY_NUMBER_FOR_LOSS;
            final Predicate<HmfTranscriptRegion> disruptionPredicate =
                    disruption -> disruptionPredicates.stream().anyMatch(p -> p.test(disruption));

            BachelorProgram bachelorProgram = new BachelorProgram(program.getName(),
                    snvPredicate,
                    inWhitelist,
                    copyNumberPredicate,
                    disruptionPredicate,
                    requiredEffects,
                    panelTranscripts);

            result.programs.add(bachelorProgram);
        }

        return result;
    }

    @NotNull
    private Collection<EligibilityReport> processVariant(final VariantContext variant, final String patient, final String sample,
            final EligibilityReport.ReportType type) {
        if (variant.isFiltered()) {
            return Collections.emptyList();
        }

        // we will skip when an ALT is not present in the sample
        final Genotype genotype = variant.getGenotype(sample);

        if (genotype == null || !(genotype.isHomVar() || genotype.isHet())) {
            return Collections.emptyList();
        }

        // gather up the relevant alleles
        VariantModel sampleVariant = new VariantModel(sample, variant);

        // apply the all relevant tests to see if this program has been matched
        final List<String> matchingPrograms = programs.stream()
                .filter(program -> program.vcfProcessor().test(sampleVariant))
                .map(BachelorProgram::name)
                .collect(Collectors.toList());

        List<EligibilityReport> reportList = Lists.newArrayList();

        if (matchingPrograms.size() > 0) {
            // found a match, not collect up the details and write them to the output file
            LOGGER.debug("program match found, first entry({}) ", matchingPrograms.get(0));
        }

        // search the list of annotations for the correct allele and transcript ID to write to the result file
        // this effectively reapplies the predicate conditions, so a refactor would be to drop the predicates and
        // just apply the search criteria once, and create a report for any full match
        for (BachelorProgram program : programs) {

            if (!program.vcfProcessor().test(sampleVariant)) {
                continue;
            }

            String programName = program.name();

            // found a match, not collect up the details and write them to the output file
            LOGGER.debug("match found: program({}) ", programName);

            for (int index = 0; index < sampleVariant.sampleAnnotations().size(); ++index) {

                SnpEffAnnotation snpEff = sampleVariant.sampleAnnotations().get(index);

                if(!snpEff.isTranscriptFeature())
                    continue;

                // re-check that this variant is one that is relevant
                if (!program.panelTranscripts().contains(snpEff.transcript())) {
                    // LOGGER.debug("uninteresting transcript({})", snpEff.transcript());
                    continue;
                }

                boolean found = false;
                for (String requiredEffect : program.requiredEffects()) {
                    if (snpEff.effects().contains(requiredEffect)) {
                        found = true;
                        break;
                    }
                }

                if (!found && !program.whitelist().test(sampleVariant)) {

                    if (program.whitelist().test(sampleVariant)) {
                        // allow this whitelist through
                        LOGGER.debug("unlisted effecta({}) but whitelisted variant", snpEff.effects());
                    } else {
                        LOGGER.debug("uninteresting effects({})", snpEff.effects());
                        continue;
                    }
                }

                // now we have the correct allele and transcript ID as required by the XML
                // so write a complete record to the output file
                LOGGER.debug("matched allele({}) transcriptId({}) effect({})", snpEff.allele(), snpEff.transcript(), snpEff.effects());

                final String annotationsStr = sampleVariant.rawAnnotations().get(index);

                boolean isHomozygous = variant.getGenotype(0).isHom();
                int phredScore = variant.getGenotype(0).getPL().length >= 1 ? variant.getGenotype(0).getPL()[0] : 0;

                EligibilityReport report = ImmutableEligibilityReport.builder()
                        .patient(patient)
                        .source(type)
                        .program(programName)
                        .id(variant.getID())
                        .genes(snpEff.gene())
                        .transcriptId(snpEff.transcript())
                        .chrom(variant.getContig())
                        .pos(variant.getStart())
                        .ref(variant.getReference().toString())
                        .alts(snpEff.allele())
                        .effects(snpEff.effects())
                        .annotations(annotationsStr)
                        .hgvsProtein(snpEff.hgvsProtein())
                        .hgvsCoding(snpEff.hgvsCoding())
                        .isHomozygous(isHomozygous)
                        .phredScore(phredScore)
                        .build();

                reportList.add(report);
            }
        }

        if (!reportList.isEmpty()) {
            LOGGER.debug("writing {} matched reports", reportList.size());
        }

        return reportList;
    }

    @NotNull
    Collection<EligibilityReport> processVCF(final String patient, final String sample, final EligibilityReport.ReportType type,
            final VCFFileReader reader) {

        final List<EligibilityReport> results = Lists.newArrayList();

        for (final HmfTranscriptRegion region : variantLocationsToQuery) {

            // LOGGER.debug("chromosome({} start={} end={})", region.chromosome(), (int) region.geneStart(), (int) region.geneEnd());

            final CloseableIterator<VariantContext> query =
                    reader.query(region.chromosome(), (int) region.geneStart(), (int) region.geneEnd());

            while (query.hasNext()) {
                final VariantContext variant = query.next();
                // LOGGER.debug("patient({}) sample({}) processing variant({})", patient, sample, variant.getID());
                results.addAll(processVariant(variant, patient, sample, type));
            }
            query.close();
        }

        return results;
    }

    @NotNull
    Collection<EligibilityReport> processCopyNumbers(final String patient, final List<GeneCopyNumber> copyNumbers) {
        final List<EligibilityReport> results = Lists.newArrayList();
        for (final GeneCopyNumber copyNumber : copyNumbers) {
            // TODO: verify the germline check
            final boolean isGermline = copyNumber.germlineHet2HomRegions() + copyNumber.germlineHomRegions() > 0;
            final List<String> matchingPrograms = programs.stream()
                    .filter(program -> program.copyNumberProcessor().test(copyNumber))
                    .map(BachelorProgram::name)
                    .collect(Collectors.toList());

            final List<EligibilityReport> interimResults = matchingPrograms.stream()
                    .map(p -> ImmutableEligibilityReport.builder()
                            .patient(patient)
                            .source(isGermline ? GERMLINE_DELETION : SOMATIC_DELETION)
                            .program(p)
                            .id("")
                            .genes(copyNumber.gene())
                            .chrom(copyNumber.chromosome())
                            .pos(copyNumber.start())
                            .ref("")
                            .alts("")
                            .effects("")
                            .hgvsProtein("")
                            .build())
                    .collect(Collectors.toList());

            results.addAll(interimResults);
        }

        return results;
    }

    private static int intron(final List<HmfExonRegion> exome, final GenomePosition position) {
        for (int i = 0; i < exome.size() - 1; i++) {
            if (position.position() > exome.get(i).end() && position.position() < exome.get(i + 1).start()) {
                return i;
            }
        }
        return -1;
    }

    @NotNull
    Collection<EligibilityReport> processStructuralVariants(final String patient, final List<StructuralVariant> structuralVariants) {
        return structuralVariants.stream().flatMap(sv -> processStructuralVariant(patient, sv)).collect(Collectors.toList());
    }

    @NotNull
    private Stream<EligibilityReport> processStructuralVariant(final String patient, final StructuralVariant structuralVariant) {
        if (structuralVariant.end() != null) {
            final GenomePosition start = GenomePositions.create(structuralVariant.chromosome(true), structuralVariant.position(true));
            final GenomePosition end = GenomePositions.create(structuralVariant.chromosome(false), structuralVariant.position(false));

            final List<EligibilityReport> results = Lists.newArrayList();
            results.addAll(processStructuralVariant(patient, start, end, structuralVariant.type()));
            results.addAll(processStructuralVariant(patient, end, start, structuralVariant.type()));
            return results.stream();
        } else {
            return Stream.empty();
        }
    }

    @NotNull
    private Collection<EligibilityReport> processStructuralVariant(final String patient, final GenomePosition position,
            final GenomePosition other, final StructuralVariantType svType) {
        final List<EligibilityReport> results = Lists.newArrayList();

        // TODO: can we do better than this performance wise? new map?
        for (final HmfTranscriptRegion region : ALL_GENES_BY_CHROMOSOME.get(position.chromosome())) {

            if (!region.contains(position)) {
                continue;
            }

            // skip non-inversion intronic variants
            if (region.contains(other) && svType != StructuralVariantType.INV) {
                final int intronStart = intron(region.exome(), position);
                final int intronEnd = intron(region.exome(), other);

                // the variant is intronic in a gene -- we will filter it
                if (intronStart >= 0 && intronStart == intronEnd) {
                    continue;
                }
            }

            programs.stream()
                    .filter(p -> p.disruptionProcessor().test(region))
                    .map(p -> ImmutableEligibilityReport.builder()
                            .patient(patient)
                            .source(SOMATIC_DISRUPTION)
                            .program(p.name())
                            .id("")
                            .genes(region.gene())
                            .chrom(region.chromosome())
                            .pos(position.position())
                            .ref("")
                            .alts("")
                            .effects("")
                            .hgvsProtein("")
                            .build())
                    .forEach(results::add);

        }

        return results;
    }
}
