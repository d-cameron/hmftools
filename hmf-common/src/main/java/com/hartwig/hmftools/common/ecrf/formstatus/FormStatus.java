package com.hartwig.hmftools.common.ecrf.formstatus;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Maps;
import com.hartwig.hmftools.common.exception.EmptyFileException;
import com.hartwig.hmftools.common.io.reader.FileReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public final class FormStatus {

    private static final Logger LOGGER = LogManager.getLogger(FormStatus.class);

    private static final int PATIENT_KEY_COLUMN = 0;
    private static final int PATIENT_ID_COLUMN = 1;
    private static final int DEPT_PERS_ID_COLUMN = 2;
    private static final int EMAIL_COLUMN = 3;
    private static final int INSTITUTE_ID_COLUMN = 4;
    private static final int INSTITUTE_NAME_COLUMN = 5;
    private static final int COMPLETED_FORM_ID_COLUMN = 6;
    private static final int DATA_STATUS_COLUMN = 7;
    private static final int FORM_ID_COLUMN = 8;
    private static final int FORM_NAME_COLUMN = 9;
    private static final int FORM_SEQ_NUM_COLUMN = 10;
    private static final int DATA_STATUS_STRING_COLUMN = 11;
    private static final int STUDY_EVENT_ID_COLUMN = 12;
    private static final int STUDY_EVENT_SEQ_NUM_COLUMN = 13;
    private static final int STUDY_EVENT_NAME_COLUMN = 14;
    private static final int LAST_SAVED_COLUMN = 15;
    private static final int LOCKED_COLUMN = 16;

    private static final int FIELD_COUNT = 17;

    private static final String FIELD_SEPARATOR = ",";
    private static final char QUOTE = '"';

    private FormStatus() {
    }

    @NotNull
    public static FormStatusModel buildModelFromCsv(@NotNull final String pathToCsv) throws IOException, EmptyFileException {
        final Map<FormStatusKey, FormStatusData> formStatuses = Maps.newHashMap();
        final List<String> lines = FileReader.build().readLines(new File(pathToCsv).toPath());
        for (final String line : lines) {
            final String[] parts = splitCsvLine(line, FIELD_SEPARATOR, FIELD_COUNT);
            if (parts.length == 17) {
                final FormStatusKey formKey = new ImmutableFormStatusKey(removeQuotes(parts[PATIENT_ID_COLUMN].replaceAll("-", "")),
                        removeParentheses(removeQuotes(parts[FORM_NAME_COLUMN])), removeQuotes(parts[FORM_SEQ_NUM_COLUMN]),
                        removeParentheses(removeQuotes(parts[STUDY_EVENT_NAME_COLUMN])), removeQuotes(parts[STUDY_EVENT_SEQ_NUM_COLUMN]));
                final FormStatusData formStatus = new ImmutableFormStatusData(removeQuotes(parts[DATA_STATUS_COLUMN]), removeQuotes(parts[LOCKED_COLUMN]));
                formStatuses.put(formKey, formStatus);
            } else if (parts.length > 0) {
                LOGGER.warn("Could not properly parse line in form status csv: " + line);
            }
        }
        return new ImmutableFormStatusModel(formStatuses);
    }

    //MIVO: split a line in a csv/tsv file by the separator, ignoring occurrences of separator in quoted groups
    @SuppressWarnings("SameParameterValue")
    @NotNull
    private static String[] splitCsvLine(@NotNull final String line, @NotNull final String separator, final int limit) {
        return line.split(separator + "(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", limit);
    }

    @NotNull
    private static String removeQuotes(@NotNull final String text) {
        final String trimmedText = text.trim();
        if (trimmedText.length() > 0 && trimmedText.charAt(0) == QUOTE && trimmedText.charAt(trimmedText.length() - 1) == QUOTE) {
            return trimmedText.substring(1, trimmedText.length() - 1);
        }
        return trimmedText;
    }

    @NotNull
    private static String removeParentheses(@NotNull final String text) {
        final Pattern pattern = Pattern.compile(".*(?=(?:\\([0-9]+\\))$)");
        final Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(0).trim();
        } else {
            return text;
        }
    }
}
