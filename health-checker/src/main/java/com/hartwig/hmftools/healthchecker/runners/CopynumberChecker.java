package com.hartwig.hmftools.healthchecker.runners;

import com.google.common.collect.Lists;
import com.hartwig.hmftools.common.context.RunContext;
import com.hartwig.hmftools.common.copynumber.CopyNumber;
import com.hartwig.hmftools.common.exception.HartwigException;
import com.hartwig.hmftools.common.copynumber.freec.FreecCopyNumberFactory;
import com.hartwig.hmftools.common.copynumber.freec.FreecFileLoader;
import com.hartwig.hmftools.healthchecker.resource.ResourceWrapper;
import com.hartwig.hmftools.healthchecker.result.BaseResult;
import com.hartwig.hmftools.healthchecker.result.MultiValueResult;
import com.hartwig.hmftools.healthchecker.runners.checks.CopynumberCheck;
import com.hartwig.hmftools.healthchecker.runners.checks.HealthCheck;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

@SuppressWarnings("WeakerAccess")
@ResourceWrapper(type = CheckType.COPYNUMBER)
public class CopynumberChecker extends ErrorHandlingChecker implements HealthChecker {

    private static final Logger LOGGER = LogManager.getLogger(CopynumberChecker.class);

    public CopynumberChecker() {
    }

    @NotNull
    @Override
    public CheckType checkType() {
        return CheckType.COPYNUMBER;
    }

    @NotNull
    @Override
    public BaseResult tryRun(@NotNull final RunContext runContext) throws IOException, HartwigException {
        long totalGain = 0;
        long totalLoss = 0;
        for (final CopyNumber copyNumber : copynumberLines(runContext)) {
            if (copyNumber.isGain()) {
                totalGain += copyNumber.bases();
            } else if (copyNumber.isLoss()) {
                totalLoss += copyNumber.bases();
            }
        }

        return toMultiValueResult(runContext, String.valueOf(totalGain), String.valueOf(totalLoss));
    }

    @NotNull
    private static List<CopyNumber> copynumberLines(@NotNull final RunContext runContext)
            throws IOException, HartwigException {
        final String basePath = FreecFileLoader.getFreecBasePath(runContext.runDirectory(), runContext.refSample(),
                runContext.isSomaticRun() ? runContext.tumorSample() : null);
        final String relevantSample = relevantSample(runContext);
        return FreecCopyNumberFactory.loadCNV(basePath, relevantSample);
    }

    @NotNull
    @Override
    public BaseResult errorRun(@NotNull final RunContext runContext) {
        return toMultiValueResult(runContext, HealthCheckConstants.ERROR_VALUE, HealthCheckConstants.ERROR_VALUE);
    }

    @NotNull
    private BaseResult toMultiValueResult(@NotNull final RunContext runContext, @NotNull final String totalGain,
                                          @NotNull final String totalLoss) {
        final HealthCheck gainCheck = new HealthCheck(relevantSample(runContext),
                CopynumberCheck.COPYNUMBER_GENOME_GAIN.toString(), totalGain);
        final HealthCheck lossCheck = new HealthCheck(relevantSample(runContext),
                CopynumberCheck.COPYNUMBER_GENOME_LOSS.toString(), totalLoss);
        final List<HealthCheck> checks = Lists.newArrayList(gainCheck, lossCheck);
        HealthCheck.log(LOGGER, checks);
        return new MultiValueResult(checkType(), checks);
    }

    @NotNull
    private static String relevantSample(@NotNull final RunContext runContext) {
        return runContext.isSomaticRun() ? runContext.tumorSample() : runContext.refSample();
    }
}