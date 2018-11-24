package com.hartwig.hmftools.svanalysis.svgraph;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.hartwig.hmftools.common.position.GenomeInterval;
import com.hartwig.hmftools.common.purple.copynumber.CopyNumberMethod;
import com.hartwig.hmftools.common.purple.copynumber.ImmutablePurpleCopyNumber;
import com.hartwig.hmftools.common.purple.copynumber.PurpleCopyNumber;
import com.hartwig.hmftools.common.purple.segment.SegmentSupport;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Segment of DNA
 */
public class BgSegment implements GenomeInterval {
    private PurpleCopyNumber _cn;
    private double cnAdjustment = 0;

    public BgSegment(PurpleCopyNumber _cn) {
        this._cn = _cn;
    }
    public static BgSegment createUnplacedSegment() {
        return new BgSegment(ImmutablePurpleCopyNumber.builder()
                .segmentEndSupport(SegmentSupport.NONE)
                .segmentStartSupport(SegmentSupport.NONE)
                .method(CopyNumberMethod.UNKNOWN)
                .chromosome("unplaced")
                .start(1)
                .end(1)
                .averageTumorCopyNumber(0)
                .bafCount(0)
                .averageActualBAF(0)
                .averageObservedBAF(0)
                .depthWindowCount(0)
                .gcContent(0)
                .minStart(0)
                .maxStart(0)
                .build());
    }
    public double copyNumber() {
        return _cn.averageTumorCopyNumber() + cnAdjustment;
    }
    @Nullable
    @Override
    public Integer startOffset() {
        return 0;
    }

    @Nullable
    @Override
    public Integer endOffset() {
        return (int)(_cn.end() - _cn.start());
    }

    @NotNull
    @Override
    public String chromosome() {
        return _cn.chromosome();
    }

    public long length() { return _cn.length(); }

    @Override
    public long position() {
        return _cn.start();
    }

    public long positionOf(int breakendOrientation) {
        return breakendOrientation < 0 ? _cn.start() : _cn.end();
    }

    private static boolean canMerge(BgSegment left, BgSegment right) {
        return left._cn.end() == right._cn.start() - 1 && left._cn.chromosome().equals(right._cn.chromosome());
    }

    public static BgSegment merge(BgSegment left, BgSegment right) {
        if (!canMerge(left, right)) {
            throw new IllegalArgumentException("Cannot merge segments");
        }
        if (left._cn.segmentEndSupport() == SegmentSupport.CENTROMERE ||  right._cn.segmentStartSupport() == SegmentSupport.CENTROMERE) {
            throw new IllegalArgumentException("Merging across centromere");
        }
        BgSegment merged = new BgSegment(merge(left._cn, right._cn));
        return merged;
    }
    public PurpleCopyNumber cn() {
        return _cn;
    }

    @Override
    public String toString() {
        return String.format("%s:%d-%d CN:%f", chromosome(), startPosition(), endPosition(), copyNumber());
    }

    private static PurpleCopyNumber merge(PurpleCopyNumber left, PurpleCopyNumber right) {
        long length = left.length() + right.length();
        int bafCount = left.bafCount() + right.bafCount();
        ImmutablePurpleCopyNumber merged = ImmutablePurpleCopyNumber.builder()
                .chromosome(left.chromosome())
                .start(left.start())
                .end(right.end())
                .segmentStartSupport(left.segmentStartSupport())
                .segmentEndSupport(right.segmentEndSupport())
                .averageTumorCopyNumber(
                        left.averageTumorCopyNumber() * left.length() / (double) length +
                                right.averageTumorCopyNumber() * right.length() / (double) length)
                .bafCount(bafCount)
                .averageActualBAF(
                        left.averageActualBAF() * left.bafCount() / (double) bafCount +
                                right.averageActualBAF() * right.bafCount() / (double) bafCount)
                .depthWindowCount(left.depthWindowCount() + right.depthWindowCount())
                .method(left.method() == right.method() ? left.method() : CopyNumberMethod.UNKNOWN)
                .averageObservedBAF(left.averageObservedBAF() * left.bafCount() / (double) bafCount +
                        right.averageObservedBAF() * right.bafCount() / (double) bafCount)
                .gcContent(left.gcContent() * left.length() / (double) length +
                        right.gcContent() * right.length() / (double) length)
                .minStart(left.minStart())
                .maxStart(left.maxStart())
                .build();
        return merged;
    }

    /**
     * Adjusts the copyNumber of the given segment by the given amount
     * @param ploidy
     */
    public void adjustCopyNumber(Double ploidy) {
        // TODO: need to adjust BAF range: the added copyNumber could be major or minor and we don't know
        this._cn = ImmutablePurpleCopyNumber.builder().from(_cn)
                .averageTumorCopyNumber(_cn.averageTumorCopyNumber() + ploidy)
                .build();
    }
    public double maxMajorCopyNumber() {
        // TODO use BAF range to calculate
        return copyNumber();
    }
    public void pushCopyNumberChange(double ploidy) {
        cnAdjustment += ploidy;
    }
    public void popCopyNumberChange(double ploidy) {
        cnAdjustment -= ploidy;
    }
    public void resetCopyNumberChange() {
        this.cnAdjustment = 0;
    }
    public static Ordering<BgSegment> ByPloidy = new Ordering<BgSegment>() {
        public int compare(BgSegment o1, BgSegment o2) {
            return ComparisonChain.start()
                    .compare(o1.copyNumber(), o2.copyNumber())
                    .compare(o1.maxMajorCopyNumber(), o2.maxMajorCopyNumber())
                    .result();
        }
    };
}
