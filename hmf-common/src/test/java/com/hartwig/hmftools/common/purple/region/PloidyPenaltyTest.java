package com.hartwig.hmftools.common.purple.region;

import static org.junit.Assert.assertEquals;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class PloidyPenaltyTest {

    private static final double EPSILON = 1e-10;

    @Test
    public void testOld() {
        assertOld(1.0, "A");
        assertOld(1.0, "AB");
        assertOld(1.0, "AA");
        assertOld(1.5, "AAB");
        assertOld(2.0, "AABB");
        assertOld(1.5, "AAA");
        assertOld(2.0, "AAAB");
        assertOld(2.5, "AAABB");
        assertOld(3.0, "AAABBB");
    }

    @Test
    public void testNew() {
        assertNew(2.0, "A");
        assertNew(1.0, "AB");
        assertNew(3.0, "AA");
        assertNew(2.0, "AAB");
        assertNew(2.0, "AABB");
        assertNew(4.0, "AAA");
        assertNew(3.0, "AAAB");
        assertNew(3.0, "AAABB");
        assertNew(4.0, "AAABBB");
    }

    private void assertOld(double expectedResult, @NotNull final String descriptiveBAF) {
        int ploidy = descriptiveBAF.length();
        int major = (int) descriptiveBAF.chars().filter(x -> x == 'A').count();
        assertEquals(expectedResult, PloidyPenalty.penalty(ploidy), EPSILON);
    }

    private void assertNew(double expectedResult, @NotNull final String descriptiveBAF) {
        int ploidy = descriptiveBAF.length();
        int major = (int) descriptiveBAF.chars().filter(x -> x == 'A').count();
        assertEquals(expectedResult, PloidyPenalty.newPenalty(ploidy, major), EPSILON);
    }

}