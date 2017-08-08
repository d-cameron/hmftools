package com.hartwig.hmftools.patientreporter.copynumber;

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class CopyNumberReportTest {

    @Test
    public void canResolveType() {
        final CopyNumberReport gain = create("18", "p2", 5);
        assertEquals(CopyNumberReportType.GAIN.description(), gain.description());

        final CopyNumberReport loss = create("18", "p2", 0);
        assertEquals(CopyNumberReportType.LOSS.description(), loss.description());

        final CopyNumberReport neutral = create("18", "p2", 2);
        assertEquals(CopyNumberReportType.NEUTRAL.description(), neutral.description());
    }

    @Test
    public void canSort() {
        final CopyNumberReport first = create("18", "p2", 2);
        final CopyNumberReport second = create("Y", "q1", 2);
        final CopyNumberReport third = create("X", "q1", 2);
        final CopyNumberReport fourth = create("12", "q1", 2);
        final CopyNumberReport fifth = create("18", "p1", 2);
        final CopyNumberReport sixth = create("X", "p1", 2);

        final List<CopyNumberReport> reports = Lists.newArrayList(first, second, third, fourth, fifth, sixth);
        final List<CopyNumberReport> sortedReports = Lists.newArrayList(Sets.newTreeSet(reports));

        assertEquals(fourth, sortedReports.get(0));
        assertEquals(fifth, sortedReports.get(1));
        assertEquals(first, sortedReports.get(2));
        assertEquals(sixth, sortedReports.get(3));
        assertEquals(third, sortedReports.get(4));
        assertEquals(second, sortedReports.get(5));
    }

    @NotNull
    private static CopyNumberReport create(@NotNull final String chromosome, @NotNull final String chromosomeBand, final int copyNumber) {
        return ImmutableCopyNumberReport.builder()
                .chromosome(chromosome)
                .chromosomeBand(chromosomeBand)
                .gene("gene")
                .copyNumber(copyNumber)
                .type(CopyNumberReportType.resolveType(copyNumber))
                .build();
    }
}