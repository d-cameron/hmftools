package com.hartwig.hmftools.common.purple.copynumber;

import static com.hartwig.hmftools.common.purple.copynumber.ExtendLongArmTest.assertCombinedRegion;
import static com.hartwig.hmftools.common.purple.copynumber.ExtendLongArmTest.createCombinedRegion;

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.google.common.collect.Lists;
import com.hartwig.hmftools.common.purple.segment.SegmentSupport;

import org.junit.Test;

public class ExtendStructuralVariantTest {

    @Test
    public void testSingleSVExtendLeft() {
        final CombinedRegion leftMost = createCombinedRegion(2001, 3000, 3, 0.5, SegmentSupport.BND);
        final CombinedRegion middle1 = createCombinedRegion(3001, 4000, 4, 0.6, SegmentSupport.NONE);
        final CombinedRegion middle2 = createCombinedRegion(4001, 5000, 4, 0.6, SegmentSupport.NONE);
        final CombinedRegion middle3 = createSVImplied(5001, 6000, 6, 0.9, SegmentSupport.NONE);
        final CombinedRegion rightMost = createCombinedRegion(6001, 7000, 7, 1, SegmentSupport.BND);

        final List<CombinedRegion> regions = Lists.newArrayList(leftMost, middle1, middle2, middle3, rightMost);
        final List<CombinedRegion> result = ExtendStructuralVariant.extendStructuralVariants(regions);

        assertEquals(2, result.size());
        assertCombinedRegion(2001, 6000, 6, 0.9, CombinedRegionMethod.STRUCTURAL_VARIANT, result.get(0));
        assertCombinedRegion(6001, 7000, 7, 1, CombinedRegionMethod.NONE, result.get(1));
    }

    @Test
    public void testSingleSVExtendRight() {
        final CombinedRegion leftMost = createCombinedRegion(2001, 3000, 3, 0.5, SegmentSupport.BND);
        final CombinedRegion middle1 = createSVImplied(3001, 4000, 4, 0.6, SegmentSupport.NONE);
        final CombinedRegion middle2 = createCombinedRegion(4001, 5000, 4, 0.6, SegmentSupport.NONE);
        final CombinedRegion middle3 = createCombinedRegion(5001, 6000, 6, 0.9, SegmentSupport.NONE);
        final CombinedRegion rightMost = createCombinedRegion(6001, 7000, 7, 1, SegmentSupport.BND);

        final List<CombinedRegion> regions = Lists.newArrayList(leftMost, middle1, middle2, middle3, rightMost);
        final List<CombinedRegion> result = ExtendStructuralVariant.extendStructuralVariants(regions);

        assertEquals(2, result.size());
        assertCombinedRegion(2001, 6000, 4, 0.6, CombinedRegionMethod.STRUCTURAL_VARIANT, result.get(0));
        assertCombinedRegion(6001, 7000, 7, 1, CombinedRegionMethod.NONE, result.get(1));
    }

    @Test
    public void testTwoSVsExtendBetween() {
        final CombinedRegion leftMost = createSVImplied(2001, 3000, 3, 0.5, SegmentSupport.BND);
        final CombinedRegion middle1 = createCombinedRegion(3001, 4000, 4, 0.6, SegmentSupport.NONE);
        final CombinedRegion middle2 = createCombinedRegion(4001, 5000, 4, 0.6, SegmentSupport.NONE);
        final CombinedRegion middle3 = createSVImplied(5001, 6000, 6, 0.9, SegmentSupport.NONE);
        final CombinedRegion rightMost = createCombinedRegion(6001, 7000, 7, 1, SegmentSupport.BND);

        final List<CombinedRegion> regions = Lists.newArrayList(leftMost, middle1, middle2, middle3, rightMost);
        final List<CombinedRegion> result = ExtendStructuralVariant.extendStructuralVariants(regions);

        assertEquals(2, result.size());
        assertCombinedRegion(2001, 6000, 4.5, 0.7, CombinedRegionMethod.STRUCTURAL_VARIANT, result.get(0));
        assertCombinedRegion(6001, 7000, 7, 1, CombinedRegionMethod.NONE, result.get(1));
    }

    private static CombinedRegion createSVImplied(long start, long end, double copyNumber, double baf, SegmentSupport support) {
        final CombinedRegion region = createCombinedRegion(start, end, copyNumber, baf, support);
        region.setMethod(CombinedRegionMethod.STRUCTURAL_VARIANT);
        return region;
    }

}