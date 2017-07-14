package com.hartwig.hmftools.purple.plot;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ChartWriterTest {

    @Test
    public void testSubtitle() {
        assertEquals("COLO829 PUR:100% PLE:2.01", ChartWriter.subtitle("COLO829", 1.0, 2.01));
        assertEquals("CPCT012345 PUR:83% PLE:1.10", ChartWriter.subtitle("CPCT012345", 0.83, 1.10));
        assertEquals("CPCT012345 PUR:83% PLE:1.10", ChartWriter.subtitle("CPCT012345", 0.83, 1.10));
    }

}
