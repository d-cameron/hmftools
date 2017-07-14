package com.hartwig.hmftools.common.purple;

import static com.hartwig.hmftools.common.purple.BAFUtils.NORMAL_BAF;
import static com.hartwig.hmftools.common.purple.BAFUtils.minAlleleCount;
import static com.hartwig.hmftools.common.purple.BAFUtils.modelBAF;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BAFUtilsTest {

    private static final double EPSILON = 1e-10;

    @Test
    public void testMinBetaAllele() {
        assertEquals(0, minAlleleCount(-28));

        assertEquals(1, minAlleleCount(1));
        assertEquals(1, minAlleleCount(2));
        assertEquals(2, minAlleleCount(3));
        assertEquals(2, minAlleleCount(4));
        assertEquals(3, minAlleleCount(5));
        assertEquals(3, minAlleleCount(6));
        assertEquals(4, minAlleleCount(7));
    }

    @Test
    public void testDiploidModelBaf() {
        assertEquals(NORMAL_BAF, modelBAF(1, 2, 1), EPSILON);
        assertEquals(NORMAL_BAF, modelBAF(0.5, 4, 2), EPSILON);
        assertEquals(NORMAL_BAF, modelBAF(0, 6, 3), EPSILON);
    }

    @Test
    public void testZeroPloidyModelBaf() {
        assertEquals(NORMAL_BAF, modelBAF(0, 0, 0), EPSILON);
        assertEquals(NORMAL_BAF, modelBAF(0.5, 0, 0), EPSILON);
        assertEquals(NORMAL_BAF, modelBAF(1, 0, 0), EPSILON);
    }

}
