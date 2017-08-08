package com.hartwig.hmftools.patientreporter.purple;

import java.util.List;
import java.util.Optional;

import com.google.common.collect.Lists;
import com.hartwig.hmftools.common.gene.GeneCopyNumber;
import com.hartwig.hmftools.common.purple.PurityAdjuster;
import com.hartwig.hmftools.common.purple.copynumber.PurpleCopyNumber;
import com.hartwig.hmftools.common.purple.gender.Gender;
import com.hartwig.hmftools.common.purple.purity.FittedPurity;
import com.hartwig.hmftools.common.purple.purity.FittedPurityScore;
import com.hartwig.hmftools.common.region.GenomeRegionSelector;
import com.hartwig.hmftools.common.region.GenomeRegionSelectorFactory;
import com.hartwig.hmftools.patientreporter.copynumber.CopyNumberAnalysis;
import com.hartwig.hmftools.patientreporter.copynumber.CopyNumberReport;
import com.hartwig.hmftools.patientreporter.variants.ImmutableVariantReport;
import com.hartwig.hmftools.patientreporter.variants.VariantReport;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(allParameters = true,
             passAnnotations = { NotNull.class, Nullable.class })
public abstract class PurpleAnalysis {

    @NotNull
    public abstract FittedPurity fittedPurity();

    @NotNull
    public abstract FittedPurityScore fittedScorePurity();

    @NotNull
    public abstract List<PurpleCopyNumber> copyNumbers();

    @NotNull
    public abstract List<GeneCopyNumber> geneCopyNumbers();

    @NotNull
    public CopyNumberAnalysis copyNumberAnalysis() {
        return new CopyNumberAnalysis(geneCopyNumbers().size(), copyNumberReport());
    }

    @NotNull
    public List<VariantReport> enrich(@NotNull final List<VariantReport> variants) {
        final List<VariantReport> result = Lists.newArrayList();
        final PurityAdjuster purityAdjuster = new PurityAdjuster(Gender.MALE, fittedPurity());
        final GenomeRegionSelector<PurpleCopyNumber> copyNumberSelector = GenomeRegionSelectorFactory.create(copyNumbers());

        for (final VariantReport variant : variants) {
            final Optional<PurpleCopyNumber> optionalCopyNumber = copyNumberSelector.select(variant);
            if (optionalCopyNumber.isPresent()) {
                final PurpleCopyNumber copyNumber = optionalCopyNumber.get();
                double adjustedVAF =
                        Math.min(1, purityAdjuster.purityAdjustedVAF(copyNumber.averageTumorCopyNumber(), variant.alleleFrequency()));
                result.add(ImmutableVariantReport.builder().from(variant).baf(copyNumber.descriptiveBAF()).impliedVAF(adjustedVAF).build());
            } else {
                result.add(variant);
            }
        }

        return result;
    }

    public double purityUncertainty() {
        return fittedScorePurity().maxPurity() - fittedScorePurity().minPurity();
    }

    @NotNull
    private List<CopyNumberReport> copyNumberReport() {
        return PurpleCopyNumberReportFactory.createReport(fittedPurity().ploidy(), geneCopyNumbers());
    }
}
