package com.hartwig.hmftools.common.copynumber.freec;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.hartwig.hmftools.common.exception.HartwigException;
import com.hartwig.hmftools.common.io.reader.LineReader;
import com.hartwig.hmftools.common.purple.ratio.ImmutableReadCount;
import com.hartwig.hmftools.common.purple.ratio.ReadCount;

import org.jetbrains.annotations.NotNull;

public enum FreecCPNFileLoader {
    ;

    private static final String RATIO_COLUMN_SEPARATOR = "\t";
    private static final int CHROMOSOME_COLUMN = 0;
    private static final int START_FIELD_COLUMN = 1;
    private static final int READ_COLUMN = 2;

    @NotNull
    public static Multimap<String, ReadCount> loadCPNContent(@NotNull final String filePath) throws IOException, HartwigException {
        final Path path = new File(filePath).toPath();
        return loadCPNContent(LineReader.build().readLines(path, x -> true));
    }

    @NotNull
    private static Multimap<String, ReadCount> loadCPNContent(@NotNull final List<String> lines) {

        final Multimap<String, ReadCount> result = ArrayListMultimap.create();
        for (String line : lines) {
            final ReadCount gcContent = fromLine(line);
            result.put(gcContent.chromosome(), gcContent);
        }

        return result;
    }

    @NotNull
    @VisibleForTesting
    static ReadCount fromLine(@NotNull final String ratioLine) {
        final String[] values = ratioLine.split(RATIO_COLUMN_SEPARATOR);

        final String chromosome = values[CHROMOSOME_COLUMN].trim();
        final long position = Long.valueOf(values[START_FIELD_COLUMN].trim());
        final int readCount = Integer.valueOf(values[READ_COLUMN].trim());

        return ImmutableReadCount.builder().chromosome(chromosome).position(position).readCount(readCount).build();
    }
}
