package com.hartwig.hmftools.patientreporter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import com.hartwig.hmftools.common.ecrf.projections.PatientTumorLocation;
import com.hartwig.hmftools.common.fusions.KnownFusionsModel;
import com.hartwig.hmftools.common.region.BEDFileLoader;
import com.hartwig.hmftools.common.variant.enrich.HotspotEnrichment;
import com.hartwig.hmftools.patientreporter.algo.DrupActionabilityModel;
import com.hartwig.hmftools.patientreporter.algo.DrupActionabilityModelFactory;
import com.hartwig.hmftools.patientreporter.algo.GeneModel;
import com.hartwig.hmftools.patientreporter.algo.GeneModelFactory;

import org.jetbrains.annotations.NotNull;

import htsjdk.samtools.reference.IndexedFastaSequenceFile;

final class SequencedReportDataLoader {

    private SequencedReportDataLoader() {
    }

    @NotNull
    static SequencedReportData buildFromFiles(@NotNull String fusionPairsLocation, @NotNull String promiscuousFiveLocation,
            @NotNull String promiscuousThreeLocation, @NotNull String drupGeneCsv, @NotNull String hotspotTsv,
            @NotNull String fastaFileLocation, @NotNull String highConfidenceBed, @NotNull String tumorLocationsCsv) throws IOException {
        final DrupActionabilityModel drupActionabilityModel = DrupActionabilityModelFactory.buildFromCsv(drupGeneCsv);
        final GeneModel panelGeneModel = GeneModelFactory.create(drupActionabilityModel);
        final List<PatientTumorLocation> patientTumorLocations = PatientTumorLocation.readRecords(tumorLocationsCsv);

        final KnownFusionsModel knownFusionsModel = KnownFusionsModel.fromInputStreams(new FileInputStream(fusionPairsLocation),
                new FileInputStream(promiscuousFiveLocation),
                new FileInputStream(promiscuousThreeLocation));

        return ImmutableSequencedReportData.of(panelGeneModel,
                HotspotEnrichment.fromHotspotsFile(hotspotTsv),
                knownFusionsModel,
                new IndexedFastaSequenceFile(new File(fastaFileLocation)),
                BEDFileLoader.fromBedFile(highConfidenceBed),
                patientTumorLocations);
    }
}