package com.hartwig.hmftools.common.purple.copynumber;

import java.util.List;

import com.google.common.collect.Lists;
import com.hartwig.hmftools.common.copynumber.freec.FreecStatus;
import com.hartwig.hmftools.common.numeric.Doubles;
import com.hartwig.hmftools.common.purple.PurityAdjuster;
import com.hartwig.hmftools.common.purple.region.FittedRegion;

import org.jetbrains.annotations.NotNull;

class LowConfidenceSmoothedRegions {

    @NotNull
    private final PurityAdjuster purityAdjuster;
    @NotNull
    private final List<FittedRegion> fittedRegions;
    @NotNull
    private final List<PurpleCopyNumber> smoothedRegions = Lists.newArrayList();

    LowConfidenceSmoothedRegions(@NotNull final PurityAdjuster purityAdjuster, @NotNull final List<FittedRegion> fittedRegions) {
        this.purityAdjuster = purityAdjuster;
        this.fittedRegions = fittedRegions;
        run();
    }

    List<PurpleCopyNumber> smoothedRegions() {
        return smoothedRegions;
    }

    private void run() {

        LowConfidenceCopyNumberBuilder builder = null;
        for (FittedRegion fittedRegion : fittedRegions) {

            if (builder == null) {
                builder = new LowConfidenceCopyNumberBuilder(purityAdjuster, fittedRegion);
            } else {
                if (isSimilar(fittedRegion, builder)) {
                    builder.extendRegion(fittedRegion);
                } else {
                    smoothedRegions.add(builder.build());
                    builder = new LowConfidenceCopyNumberBuilder(purityAdjuster, fittedRegion);
                }
            }
        }

        if (builder != null) {
            smoothedRegions.add(builder.build());
        }
    }

    private static boolean isSimilar(@NotNull final FittedRegion region, @NotNull final LowConfidenceCopyNumberBuilder builder) {
        return region.status().equals(FreecStatus.GERMLINE) || builder.withinCopyNumberTolerance(region)
                || Doubles.isZero(region.tumorCopyNumber())
                || Doubles.isZero(builder.averageTumorCopyNumber());
    }

}
