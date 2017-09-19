package com.hartwig.hmftools.common.purple.ratio;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.hartwig.hmftools.common.chromosome.Chromosome;
import com.hartwig.hmftools.common.chromosome.HumanChromosome;
import com.hartwig.hmftools.common.cobalt.ReadCount;
import com.hartwig.hmftools.common.gc.GCMedianReadCount;
import com.hartwig.hmftools.common.gc.GCMedianReadCountBuilder;
import com.hartwig.hmftools.common.gc.GCProfile;
import com.hartwig.hmftools.common.numeric.Doubles;
import com.hartwig.hmftools.common.position.GenomePosition;
import com.hartwig.hmftools.common.purple.gender.Gender;

import org.jetbrains.annotations.NotNull;

public class NormalizedRatiosBuilder {

    private static final long ROLLING_MEDIAN_MAX_DISTANCE = 5_000;
    private static final long ROLLING_MEDIAN_MIN_COVERAGE = 1_000;
    private static final double MIN_MAPPABLE_PERCENTAGE = 0.85;

    private final Gender gender;
    private final boolean applyRollingMedianNormalization;
    private final GCMedianReadCountBuilder medianReadCountBuilder = new GCMedianReadCountBuilder();
    private final Multimap<String, ReadCountWithGCContent> entries = ArrayListMultimap.create();

    public NormalizedRatiosBuilder(final boolean applyRollingMedianNormalization, final Gender gender) {
        this.applyRollingMedianNormalization = applyRollingMedianNormalization;
        this.gender = gender;
    }

    public void addPosition(@NotNull final Chromosome chromosome, @NotNull final GCProfile gcProfile, @NotNull final ReadCount readCount) {
        final ReadCountWithGCContent readCountWithGCContent = new ReadCountWithGCContent(readCount, gcProfile);
        entries.put(gcProfile.chromosome(), readCountWithGCContent);

        // TODO: TEST With/without ismappable
        if (chromosome.isAutosome() && readCountWithGCContent.isMappable() && readCount.readCount() > 0) {
            medianReadCountBuilder.add(gcProfile, readCount);
        }
    }

    public NormalizedRatios build() {

        final GCMedianReadCount gcMedianReadCount = medianReadCountBuilder.build();
        final ImmutableNormalizedRatios.Builder builder = ImmutableNormalizedRatios.builder().medianReadCount(gcMedianReadCount);

        for (String chromosome : entries.keySet()) {
            final List<ReadRatio> normalisedRatio =
                    entries.get(chromosome).stream().map(x -> create(gcMedianReadCount, x)).collect(Collectors.toList());

            if (applyRollingMedianNormalization) {
                double expectedRatio = HumanChromosome.fromString(chromosome).isHomologous(gender) ? 1 : 0.5;
                final Supplier<List<ReadRatio>> diploidNormalization = new RollingRatioNormalization(expectedRatio,
                        ROLLING_MEDIAN_MAX_DISTANCE,
                        ROLLING_MEDIAN_MIN_COVERAGE,
                        normalisedRatio);
                builder.putAllNormalisedRatios(chromosome, diploidNormalization.get());
            } else {
                builder.putAllNormalisedRatios(chromosome, normalisedRatio);
            }

        }

        return builder.build();
    }

    private ReadRatio create(GCMedianReadCount medians, ReadCountWithGCContent readCount) {
        int gcMedianCount = medians.medianReadCount(readCount.gcProfile());
        final double ratio;

        double medianNormalisation = 1.0 * medians.medianReadCount() / medians.meanReadCount();

        if (gcMedianCount == -1 || !readCount.isMappable() || gcMedianCount == 0) {
            ratio = -1;
        } else {
            ratio = medianNormalisation * readCount.readCount() / gcMedianCount;
        }

        return ImmutableReadRatio.builder().from(readCount).ratio(ratio).build();

    }

    private class ReadCountWithGCContent implements GenomePosition {

        private final GCProfile gcProfile;
        private final ReadCount readCount;

        private ReadCountWithGCContent(@NotNull final ReadCount readCount, @NotNull final GCProfile gcProfile) {
            this.readCount = readCount;
            this.gcProfile = gcProfile;
        }

        @NotNull
        @Override
        public String chromosome() {
            return readCount.chromosome();
        }

        @Override
        public long position() {
            return readCount.position();
        }

        private int readCount() {
            return readCount.readCount();
        }

        public GCProfile gcProfile() {
            return gcProfile;
        }

        private boolean isMappable() {
            return Doubles.greaterOrEqual(gcProfile.mappablePercentage(), MIN_MAPPABLE_PERCENTAGE);
        }
    }
}
