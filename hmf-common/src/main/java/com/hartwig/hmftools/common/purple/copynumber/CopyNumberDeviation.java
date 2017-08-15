package com.hartwig.hmftools.common.purple.copynumber;

import com.google.common.annotations.VisibleForTesting;
import com.hartwig.hmftools.common.numeric.Doubles;
import com.hartwig.hmftools.common.purple.PurityAdjuster;
import com.hartwig.hmftools.common.purple.region.FittedRegion;
import com.hartwig.hmftools.common.purple.segment.StructuralVariantSupport;

import org.jetbrains.annotations.NotNull;

final class CopyNumberDeviation {

    private static final double MIN_COPY_NUMBER_TOLERANCE = 0.3;
    @VisibleForTesting
    static final double LC_MAX_COPY_NUMBER_TOLERANCE = 1.3;
    @VisibleForTesting
    static final double HC_MAX_COPY_NUMBER_TOLERANCE = 0.7;

    @NotNull
    private final PurityAdjuster purityAdjuster;

    CopyNumberDeviation(@NotNull final PurityAdjuster purityAdjuster) {
        this.purityAdjuster = purityAdjuster;
    }

    boolean withinCopyNumberTolerance(@NotNull final FittedRegion first, @NotNull final FittedRegion second) {
        double tumorCopyNumberDeviation = Math.abs(first.tumorCopyNumber() - second.tumorCopyNumber());
        double refNormalisedCopyNumberDeviation = Math.abs(first.refNormalisedCopyNumber() - second.refNormalisedCopyNumber());
        double copyNumberDeviation = Math.min(tumorCopyNumberDeviation, refNormalisedCopyNumberDeviation);
        double rawMaxDeviation = maxCopyNumberDeviation(first, second);
        double adjustedMaxDeviation = purityAdjuster.purityAdjustedMaxCopyNumberDeviation(rawMaxDeviation);

        return Doubles.lessOrEqual(copyNumberDeviation, adjustedMaxDeviation);
    }

    static double maxCopyNumberDeviation(@NotNull final FittedRegion first, @NotNull final FittedRegion second) {
        boolean structuralBreakTransition = second.start() < first.start()
                ? !first.structuralVariantSupport().equals(StructuralVariantSupport.NONE)
                : !second.structuralVariantSupport().equals(StructuralVariantSupport.NONE);

        return maxCopyNumberDeviation(second.bafCount(), second.observedTumorRatioCount(), structuralBreakTransition);
    }

    private static double maxCopyNumberDeviation(int bafCount, int observedTumorRatioCount, boolean structuralBreakTransition) {
        if (bafCount >= 10) {
            return MIN_COPY_NUMBER_TOLERANCE;
        }

        final double maxDeviation;
        if (structuralBreakTransition || observedTumorRatioCount > 5) {
            maxDeviation = HC_MAX_COPY_NUMBER_TOLERANCE;
        } else {
            maxDeviation = LC_MAX_COPY_NUMBER_TOLERANCE;
        }

        return (MIN_COPY_NUMBER_TOLERANCE - maxDeviation) / 10 * bafCount + maxDeviation;
    }

}