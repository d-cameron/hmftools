package com.hartwig.hmftools.purple.baf;

import static com.hartwig.hmftools.purple.CommandLineUtil.defaultValue;

import java.io.File;
import java.io.IOException;
import java.util.function.Supplier;

import com.google.common.collect.Multimap;
import com.hartwig.hmftools.common.exception.HartwigException;
import com.hartwig.hmftools.common.purple.baf.TumorBAF;
import com.hartwig.hmftools.common.purple.baf.TumorBAFFile;
import com.hartwig.hmftools.purple.config.CommonConfig;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class BAFSupplier implements Supplier<Multimap<String, TumorBAF>> {

    private static final Logger LOGGER = LogManager.getLogger(BAFSupplier.class);

    private static final String BAF_FILE = "baf";
    private static final String VCF_EXTENSION = "vcf_extension";
    private static final String VCF_EXTENSION_DEFAULT = ".annotated_sliced.vcf";

    public static void addOptions(@NotNull final Options options) {
        options.addOption(VCF_EXTENSION, true, "VCF file extension. Defaults to " + VCF_EXTENSION_DEFAULT);
        options.addOption(BAF_FILE, true, "BAF file location");
    }

    @NotNull
    private final Supplier<Multimap<String, TumorBAF>> supplier;

    public BAFSupplier(@NotNull final CommonConfig config, @NotNull final CommandLine cmd)
            throws ParseException, IOException, HartwigException {
        final String bafFile = cmd.hasOption(BAF_FILE)
                ? cmd.getOptionValue(BAF_FILE)
                : TumorBAFFile.generateFilename(config.outputDirectory(), config.tumorSample());
        if (new File(bafFile).exists()) {
            LOGGER.info("Reading BAFs from {}", bafFile);
            supplier = new FileSupplier(bafFile);
        } else {
            LOGGER.info("Generating BAFs from germline VCF");
            final String vcfExtension = defaultValue(cmd, VCF_EXTENSION, VCF_EXTENSION_DEFAULT);
            supplier = new VCFSupplier(config, vcfExtension);

            LOGGER.info("Persisting BAFs to {}", bafFile);
            TumorBAFFile.write(bafFile, supplier.get());
        }
    }

    @Override
    @NotNull
    public Multimap<String, TumorBAF> get() {
        return supplier.get();
    }
}
