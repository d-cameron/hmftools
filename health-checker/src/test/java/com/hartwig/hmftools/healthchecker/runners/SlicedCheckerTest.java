package com.hartwig.hmftools.healthchecker.runners;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import com.hartwig.hmftools.common.context.RunContext;
import com.hartwig.hmftools.common.context.TestRunContextFactory;
import com.hartwig.hmftools.common.exception.HartwigException;
import com.hartwig.hmftools.healthchecker.result.BaseResult;
import com.hartwig.hmftools.healthchecker.result.SingleValueResult;
import com.hartwig.hmftools.healthchecker.runners.checks.HealthCheck;
import com.hartwig.hmftools.healthchecker.runners.checks.SlicedCheck;

import org.junit.Test;

public class SlicedCheckerTest {

    private static final String RUN_DIRECTORY = RunnerTestFunctions.getRunnerResourcePath("sliced");
    private static final String REF_SAMPLE = "sample1";
    private static final String TUMOR_SAMPLE = "sample2";

    private static final int EXPECTED_SLICED_COUNT = 4;

    private final SlicedChecker checker = new SlicedChecker();

    @Test
    public void canAnalyseTypicalSlicedVCF() throws IOException, HartwigException {
        final RunContext runContext = TestRunContextFactory.forSomaticTest(RUN_DIRECTORY, REF_SAMPLE, TUMOR_SAMPLE);

        final BaseResult result = checker.tryRun(runContext);
        assertEquals(CheckType.SLICED, result.getCheckType());

        final HealthCheck check = ((SingleValueResult) result).getCheck();
        assertEquals(SlicedCheck.SLICED_NUMBER_OF_VARIANTS.toString(), check.getCheckName());
        assertEquals(TUMOR_SAMPLE, check.getSampleId());
        assertEquals(Integer.toString(EXPECTED_SLICED_COUNT), check.getValue());
    }

    @Test
    public void alsoWorksForSingleSample() throws IOException, HartwigException {
        final RunContext runContext = TestRunContextFactory.forSingleSampleTest(RUN_DIRECTORY, REF_SAMPLE);

        final BaseResult result = checker.tryRun(runContext);
        assertEquals(CheckType.SLICED, result.getCheckType());

        final HealthCheck check = ((SingleValueResult) result).getCheck();
        assertEquals(SlicedCheck.SLICED_NUMBER_OF_VARIANTS.toString(), check.getCheckName());
        assertEquals(REF_SAMPLE, check.getSampleId());
        assertEquals(Integer.toString(EXPECTED_SLICED_COUNT), check.getValue());
    }

    @Test
    public void errorYieldsCorrectOutputForSomatic() {
        final RunContext runContext = TestRunContextFactory.forSomaticTest(RUN_DIRECTORY, REF_SAMPLE, TUMOR_SAMPLE);
        final SingleValueResult result = (SingleValueResult) checker.errorRun(runContext);
        assertEquals(TUMOR_SAMPLE, result.getCheck().getSampleId());
    }

    @Test
    public void errorYieldsCorrectOutputForSingleSample() {
        final RunContext runContext = TestRunContextFactory.forSingleSampleTest(RUN_DIRECTORY, REF_SAMPLE);
        final SingleValueResult result = (SingleValueResult) checker.errorRun(runContext);
        assertEquals(REF_SAMPLE, result.getCheck().getSampleId());
    }

    @Test(expected = IOException.class)
    public void readingNonExistingFileYieldsIOException() throws IOException, HartwigException {
        final RunContext runContext = TestRunContextFactory.forSomaticTest("DoesNotExist", REF_SAMPLE, TUMOR_SAMPLE);
        checker.tryRun(runContext);
    }
}