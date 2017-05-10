package com.hartwig.hmftools.common.purple;

import com.google.common.collect.Lists;
import com.hartwig.hmftools.common.freec.FreecCopyNumber;
import com.hartwig.hmftools.common.freec.FreecRatio;
import com.hartwig.hmftools.common.zipper.GenomeZipper;
import com.hartwig.hmftools.common.zipper.GenomeZipperRegionHandler;

import java.util.Collections;
import java.util.List;

import static com.hartwig.hmftools.common.numeric.Doubles.replaceNaNWithZero;

public class EnrichedCopyNumberFactory implements GenomeZipperRegionHandler<FreecCopyNumber> {

    public static List<EnrichedCopyNumber> convoyCopyNumbers(List<FreecCopyNumber> copyNumbers, List<BetaAlleleFrequency> bafs, List<FreecRatio> tumorRatios, List<FreecRatio> normalRatios) {
        return new EnrichedCopyNumberFactory(copyNumbers, bafs, tumorRatios, normalRatios).result();
    }

    private final List<EnrichedCopyNumber> result = Lists.newArrayList();
    private final RatioAccumulator tumorRatio = new RatioAccumulator();
    private final RatioAccumulator normalRatio = new RatioAccumulator();
    private final BetaAlleleFrequencyAccumulator baf = new BetaAlleleFrequencyAccumulator();

    private EnrichedCopyNumberFactory(List<FreecCopyNumber> copyNumbers, List<BetaAlleleFrequency> bafs, List<FreecRatio> tumorRatios, List<FreecRatio> normalRatios) {
        GenomeZipper<FreecCopyNumber> zipper = new GenomeZipper<>(copyNumbers, this);
        zipper.addPositions(bafs, baf::accumulate);
        zipper.addPositions(tumorRatios, tumorRatio::accumulate);
        zipper.addPositions(normalRatios, normalRatio::accumulate);
        zipper.run();
    }

    private List<EnrichedCopyNumber> result() {
        return result;
    }

    @Override
    public void enter(FreecCopyNumber region) {
        baf.reset();
        tumorRatio.reset();
        normalRatio.reset();
    }

    @Override
    public void exit(FreecCopyNumber region) {
        double myTumorRatio = tumorRatio.meanRatio();
        double myNormalRatio = normalRatio.meanRatio();
        EnrichedCopyNumber copyNumber = ImmutableEnrichedCopyNumber.builder().from(region)
                .status(region.status())
                .genotype(region.genotype())
                .mBAFCount(baf.count())
                .mBAF(baf.medianBaf())
                .tumorRatio(myTumorRatio)
                .normalRatio(myNormalRatio)
                .ratioOfRatio(replaceNaNWithZero(myTumorRatio / myNormalRatio))
                .build();

        result.add(copyNumber);
    }

    private class BetaAlleleFrequencyAccumulator {
        private int count;
        final private List<Double> bafs = Lists.newArrayList();

        private void reset() {
            count = 0;
            bafs.clear();
        }

        private void accumulate(BetaAlleleFrequency baf) {
            count++;
            bafs.add(baf.mBaf());
        }

        private int count() {
            return count;
        }

        private double medianBaf() {
            if (count > 0) {
                Collections.sort(bafs);
                return bafs.size() % 2 == 0 ? (bafs.get(count / 2) + bafs.get(count / 2 - 1)) / 2 : bafs.get(count / 2);
            }
            return 0;
        }
    }

    private class RatioAccumulator {
        private double sumRatio;
        private int count;

        private void accumulate(FreecRatio ratio) {
            if (ratio.ratio() > -1) {
                count++;
                sumRatio += ratio.ratio();
            }
        }

        private void reset() {
            count = 0;
            sumRatio = 0;
        }

        private double meanRatio() {
            return count > 0 ? sumRatio / count : 0;
        }
    }
}
