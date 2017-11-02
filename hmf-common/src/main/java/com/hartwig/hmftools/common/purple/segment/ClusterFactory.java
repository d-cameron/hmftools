package com.hartwig.hmftools.common.purple.segment;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.hartwig.hmftools.common.cobalt.CobaltRatio;
import com.hartwig.hmftools.common.numeric.Doubles;
import com.hartwig.hmftools.common.position.GenomePosition;
import com.hartwig.hmftools.common.variant.structural.StructuralVariant;

import org.jetbrains.annotations.NotNull;

public class ClusterFactory {

    public final long windowSize;

    public ClusterFactory(final long windowSize) {
        this.windowSize = windowSize;
    }

    @NotNull
    public ListMultimap<String, Cluster> cluster(@NotNull final List<StructuralVariant> variants, @NotNull final Multimap<String, GenomePosition> ratioPositions,
            ListMultimap<String, CobaltRatio> ratios) {
        final Multimap<String, StructuralVariantPosition> positions = asMap(StructuralVariantPositionFactory.create(variants));
        return cluster(positions, ratioPositions, ratios);
    }

    @NotNull
    public ListMultimap<String, Cluster> cluster(@NotNull final Multimap<String, StructuralVariantPosition> variantPositions,
            @NotNull final Multimap<String, GenomePosition> ratioPositions, @NotNull final ListMultimap<String, CobaltRatio> ratios) {

        ListMultimap<String, Cluster> clusters = ArrayListMultimap.create();
        for (String chromosome : ratioPositions.keySet()) {
            final Collection<GenomePosition> chromosomeRatioPositions = ratioPositions.get(chromosome);
            final Collection<StructuralVariantPosition> chromosomeVariants =
                    variantPositions.containsKey(chromosome) ? variantPositions.get(chromosome) : Collections.EMPTY_LIST;
            final List<CobaltRatio> chromosomeRatios = ratios.containsKey(chromosome) ? ratios.get(chromosome) : Collections.EMPTY_LIST;
            clusters.putAll(chromosome, cluster(chromosomeVariants, chromosomeRatioPositions, chromosomeRatios));
        }

        return clusters;
    }

    @NotNull
    List<Cluster> cluster(@NotNull final Collection<StructuralVariantPosition> variantPositions,
            @NotNull final Collection<GenomePosition> ratioPositions, @NotNull final List<CobaltRatio> ratios) {

        final List<GenomePosition> allPositions = Lists.newArrayList();
        allPositions.addAll(variantPositions);
        allPositions.addAll(ratioPositions);
        Collections.sort(allPositions);

        final List<Cluster> result = Lists.newArrayList();

        int ratioIndex = 0;
        ModifiableCluster segment = null;
        for (GenomePosition position : allPositions) {
            while (ratioIndex < ratios.size() - 1 && ratios.get(ratioIndex).position() < position.position()) {
                ratioIndex++;
            }

            final long start = start(position.position(), ratioIndex, ratios);
            final long end = end(position.position(), ratioIndex, ratios);

            if (segment == null || start > segment.end()) {
                if (segment != null) {
                    result.add(segment);
                }

                segment = ModifiableCluster.create().setChromosome(position.chromosome()).setStart(start).setEnd(end);

            } else {
                segment.setEnd(end);
            }

            if (position instanceof StructuralVariantPosition) {
                segment.addVariants((StructuralVariantPosition) position);
            } else {
                segment.addRatios(position);
            }

        }
        if (segment != null) {
            result.add(segment);
        }

        return result;
    }

    @VisibleForTesting
    long start(long position, int index, @NotNull final List<CobaltRatio> ratios) {
        assert (index <= ratios.size());
        final long min = position / windowSize * windowSize + 1 - windowSize;
        if (!ratios.isEmpty()) {
            for (int i = index; i >= 0; i--) {
                final CobaltRatio ratio = ratios.get(i);
                if (ratio.position() <= min && Doubles.greaterThan(ratio.tumorGCRatio(), -1)) {
                    return ratio.position();
                }
            }
        }

        return min;
    }

    @VisibleForTesting
    long end(long position, int index, @NotNull final List<CobaltRatio> ratios) {
        for (int i = index; i < ratios.size(); i++) {
            final CobaltRatio ratio = ratios.get(i);
            if (ratio.position() > position && Doubles.greaterThan(ratio.tumorGCRatio(), -1)) {
                return ratio.position() + windowSize - 1;
            }
        }

        return position / windowSize * windowSize + 2 * windowSize;
    }

    private static Multimap<String, StructuralVariantPosition> asMap(@NotNull final List<StructuralVariantPosition> variants) {
        final Multimap<String, StructuralVariantPosition> result = ArrayListMultimap.create();

        for (StructuralVariantPosition variant : variants) {
            result.put(variant.chromosome(), variant);
        }

        return result;
    }
}