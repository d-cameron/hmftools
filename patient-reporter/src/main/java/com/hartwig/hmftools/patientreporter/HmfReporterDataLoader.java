package com.hartwig.hmftools.patientreporter;

import java.io.IOException;

import com.hartwig.hmftools.common.cosmic.Cosmic;
import com.hartwig.hmftools.common.cosmic.CosmicModel;
import com.hartwig.hmftools.common.exception.HartwigException;
import com.hartwig.hmftools.common.slicing.HmfSlicer;
import com.hartwig.hmftools.common.slicing.SlicerFactory;
import com.hartwig.hmftools.patientreporter.filters.DrupFilter;

import org.jetbrains.annotations.NotNull;

public final class HmfReporterDataLoader {
    private HmfReporterDataLoader() {
    }

    @NotNull
    public static HmfReporterData buildFromFiles(@NotNull final String hmfGenePanelFile, @NotNull final String drupFilterFile,
            @NotNull final String cosmicFile) throws IOException, HartwigException {
        final HmfSlicer hmfSlicer = SlicerFactory.fromHmfGenePanelFile(hmfGenePanelFile);
        final DrupFilter drupFilter = new DrupFilter(drupFilterFile);
        final CosmicModel cosmicModel = Cosmic.buildModelFromCsv(cosmicFile);
        return new ImmutableHmfReporterData(hmfSlicer, cosmicModel, drupFilter);
    }
}
