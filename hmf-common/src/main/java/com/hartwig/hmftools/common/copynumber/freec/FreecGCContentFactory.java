package com.hartwig.hmftools.common.copynumber.freec;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.hartwig.hmftools.common.exception.HartwigException;
import com.hartwig.hmftools.common.io.reader.LineReader;
import com.hartwig.hmftools.common.purple.ratio.GCContent;
import com.hartwig.hmftools.common.purple.ratio.ImmutableGCContent;

import org.jetbrains.annotations.NotNull;

public enum FreecGCContentFactory {
    ;

    private static final String RATIO_COLUMN_SEPARATOR = "\t";
    private static final int CHROMOSOME_COLUMN = 0;
    private static final int START_FIELD_COLUMN = 1;
    private static final int GC_CONTENT_COLUMN = 2;
    private static final int NON_N_PERCENTAGE_COLUMN = 3;
    private static final int MAPPABLE_PERCENTAGE_COLUMN = 4;

    @NotNull
    public static Multimap<String, GCContent> loadGCContent(@NotNull final String fileName) throws IOException, HartwigException {
        return loadGCContent(LineReader.build().readLines(new File(fileName).toPath(), x -> true));
    }

    @NotNull
    private static Multimap<String, GCContent> loadGCContent(@NotNull final List<String> lines) {

        final Multimap<String, GCContent> result = ArrayListMultimap.create();
        for (String line : lines) {
            final GCContent gcContent = fromLine(line);
            result.put(gcContent.chromosome(), gcContent);
        }

        return result;
    }

    @NotNull
    @VisibleForTesting
    static GCContent fromLine(@NotNull final String ratioLine) {
        final String[] values = ratioLine.split(RATIO_COLUMN_SEPARATOR);

        final String chromosome = values[CHROMOSOME_COLUMN].trim();
        final long position = 1 + Long.valueOf(values[START_FIELD_COLUMN].trim());
        final double gcContent = Double.valueOf(values[GC_CONTENT_COLUMN].trim());
        final double nonNPercentage = Double.valueOf(values[NON_N_PERCENTAGE_COLUMN].trim());
        final double mappablePercentage = Double.valueOf(values[MAPPABLE_PERCENTAGE_COLUMN].trim());

        return ImmutableGCContent.builder()
                .chromosome(chromosome)
                .position(position)
                .gcContent(gcContent)
                .nonNPercentage(nonNPercentage)
                .mappablePercentage(mappablePercentage)
                .build();
    }
}
