package com.hartwig.hmftools.patientreporter.report.data;

import static net.sf.dynamicreports.report.builder.DynamicReports.field;

import java.util.List;
import java.util.stream.Collectors;

import com.hartwig.hmftools.common.actionability.EvidenceItem;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

import net.sf.dynamicreports.report.base.expression.AbstractSimpleExpression;
import net.sf.dynamicreports.report.builder.FieldBuilder;
import net.sf.dynamicreports.report.datasource.DRDataSource;
import net.sf.dynamicreports.report.definition.ReportParameters;
import net.sf.jasperreports.engine.JRDataSource;

public class ClinicalTrialDataSource {

    public static final FieldBuilder<?> EVENT_FIELD = field("event", String.class);
    public static final FieldBuilder<?> TRIAL_FIELD = field("trial", String.class);
    public static final FieldBuilder<?> SOURCE_FIELD = field("source", String.class);
    public static final FieldBuilder<?> CCMO_FIELD = field("ccmo", String.class);
    public static final FieldBuilder<?> ON_LABEL_FIELD = field("on_label", String.class);

    private ClinicalTrialDataSource() {
    }

    @NotNull
    public static FieldBuilder<?>[] clinicalTrialFields() {
        return new FieldBuilder<?>[] { EVENT_FIELD, TRIAL_FIELD, SOURCE_FIELD, CCMO_FIELD, ON_LABEL_FIELD };
    }

    @NotNull
    public static JRDataSource fromClinicalTrials(@NotNull List<EvidenceItem> trials) {
        final DRDataSource evidenceItemDataSource = new DRDataSource(EVENT_FIELD.getName(),
                TRIAL_FIELD.getName(),
                SOURCE_FIELD.getName(),
                CCMO_FIELD.getName(),
                ON_LABEL_FIELD.getName());

        for (EvidenceItem evidenceItem : sort(trials)) {
            assert evidenceItem.source().isTrialSource();

            evidenceItemDataSource.add(evidenceItem.event(),
                    evidenceItem.drug(),
                    evidenceItem.source().sourceName(),
                    CCMOID(evidenceItem.reference()),
                    evidenceItem.isOnLabel() ? "Yes" : "No");
        }

        return evidenceItemDataSource;
    }

    @NotNull
    private static String CCMOID(@NotNull String CCMO_ids) {
        String CCMO_ids_new = CCMO_ids.replace(")", "");
        String[] CCMO_ids_def = CCMO_ids_new.split("\\(");
        return CCMO_ids_def[1];
    }

    @NotNull
    private static List<EvidenceItem> sort(@NotNull List<EvidenceItem> evidenceItems) {
        return evidenceItems.stream().sorted((item1, item2) -> {
            return item1.event().compareTo(item2.event());
        }).collect(Collectors.toList());
    }

    @NotNull
    public static AbstractSimpleExpression<String> sourceHyperlink() {
        return new AbstractSimpleExpression<String>() {
            @Override
            public String evaluate(@NotNull final ReportParameters data) {
                String source = data.getValue(SOURCE_FIELD.getName()).toString();
                switch (source.toLowerCase()) {
                    case "iclusion":
                        return "https://www.iclusion.org";
                    default:
                        return Strings.EMPTY;
                }
            }
        };
    }
}