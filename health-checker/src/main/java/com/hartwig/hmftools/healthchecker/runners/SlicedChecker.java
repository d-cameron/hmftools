package com.hartwig.hmftools.healthchecker.runners;

import com.hartwig.hmftools.common.exception.HartwigException;
import com.hartwig.hmftools.common.variant.GermlineVariant;
import com.hartwig.hmftools.common.variant.vcf.VCFFileLoader;
import com.hartwig.hmftools.common.context.RunContext;
import com.hartwig.hmftools.healthchecker.resource.ResourceWrapper;
import com.hartwig.hmftools.healthchecker.result.BaseResult;
import com.hartwig.hmftools.healthchecker.result.SingleValueResult;
import com.hartwig.hmftools.healthchecker.runners.checks.HealthCheck;
import com.hartwig.hmftools.healthchecker.runners.checks.SlicedCheck;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

@SuppressWarnings("WeakerAccess")
@ResourceWrapper(type = CheckType.SLICED)
public class SlicedChecker extends ErrorHandlingChecker implements HealthChecker {

    private static final Logger LOGGER = LogManager.getLogger(SlicedChecker.class);

    private static final String SLICED_VCF_EXTENSION = "_GoNLv5_sliced.vcf";

    public SlicedChecker() {
    }

    @NotNull
    @Override
    public CheckType checkType() {
        return CheckType.SLICED;
    }

    @NotNull
    @Override
    public BaseResult tryRun(@NotNull final RunContext runContext) throws IOException, HartwigException {
        final List<GermlineVariant> variants = VCFFileLoader.loadGermlineVCF(runContext.runDirectory(),
                SLICED_VCF_EXTENSION).variants();
        final long value = variants.size();

        final String sample = runContext.isSomaticRun() ? runContext.tumorSample() : runContext.refSample();
        return toSingleValueResult(
                new HealthCheck(sample, SlicedCheck.SLICED_NUMBER_OF_VARIANTS.toString(), String.valueOf(value)));
    }

    @NotNull
    @Override
    public BaseResult errorRun(@NotNull final RunContext runContext) {
        final String sample = runContext.isSomaticRun() ? runContext.tumorSample() : runContext.refSample();
        return toSingleValueResult(new HealthCheck(sample, SlicedCheck.SLICED_NUMBER_OF_VARIANTS.toString(),
                HealthCheckConstants.ERROR_VALUE));
    }

    @NotNull
    private BaseResult toSingleValueResult(@NotNull final HealthCheck check) {
        check.log(LOGGER);
        return new SingleValueResult(checkType(), check);
    }
}