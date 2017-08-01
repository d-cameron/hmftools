package com.hartwig.hmftools.common.chromosome;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

public class ChromosomeLengthFile {

    private static final String DELIMITER = "\t";

    @NotNull
    public static List<ChromosomeLength> read(@NotNull final String filename) throws IOException {
        return fromLines(Files.readAllLines(new File(filename).toPath()));
    }

    @NotNull
    private static List<ChromosomeLength> fromLines(@NotNull List<String> lines) {
        return lines.stream().map(ChromosomeLengthFile::fromString).collect(Collectors.toList());
    }

    @NotNull
    private static ChromosomeLength fromString(@NotNull final String line) {
        String[] values = line.split(DELIMITER);
        return ImmutableChromosomeLength.builder().chromosome(values[0]).position(Long.valueOf(values[2])).build();
    }

}