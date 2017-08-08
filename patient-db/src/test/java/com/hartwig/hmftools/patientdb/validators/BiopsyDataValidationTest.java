package com.hartwig.hmftools.patientdb.validators;

import static com.hartwig.hmftools.patientdb.readers.BiopsyReader.FIELD_BIOPSY_DATE;
import static com.hartwig.hmftools.patientdb.readers.BiopsyReader.FIELD_LOCATION;
import static com.hartwig.hmftools.patientdb.readers.BiopsyReader.FIELD_LOCATION_OTHER;
import static com.hartwig.hmftools.patientdb.readers.BiopsyReader.FORM_BIOPS;
import static com.hartwig.hmftools.patientdb.readers.BiopsyTreatmentReader.FORM_TREATMENT;
import static com.hartwig.hmftools.patientdb.readers.CpctPatientReader.FIELD_REGISTRATION_DATE1;
import static com.hartwig.hmftools.patientdb.readers.CpctPatientReader.FIELD_REGISTRATION_DATE2;
import static com.hartwig.hmftools.patientdb.validators.PatientValidator.fields;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.hartwig.hmftools.common.ecrf.datamodel.ValidationFinding;
import com.hartwig.hmftools.patientdb.data.BiopsyData;
import com.hartwig.hmftools.patientdb.data.BiopsyTreatmentData;
import com.hartwig.hmftools.patientdb.data.ImmutableBiopsyData;
import com.hartwig.hmftools.patientdb.data.ImmutableBiopsyTreatmentData;
import com.hartwig.hmftools.patientdb.data.ImmutablePatientData;

import org.junit.Test;

public class BiopsyDataValidationTest {
    private final String CPCT_ID = "CPCT01020000";
    private final static LocalDate JAN2015 = LocalDate.parse("2015-01-01");
    private final static LocalDate FEB2015 = LocalDate.parse("2015-02-01");
    private final static LocalDate MAR2016 = LocalDate.parse("2016-03-01");

    private final static BiopsyData BIOPSY_NULL = ImmutableBiopsyData.of(null, null, "", "");
    private final static BiopsyData BIOPSY_FEB1 = ImmutableBiopsyData.of(FEB2015, "1", "", "");
    private final static BiopsyData BIOPSY_FEB2 = ImmutableBiopsyData.of(FEB2015, "2", "", "");
    private final static BiopsyTreatmentData TREATMENT_JAN_FEB =
            ImmutableBiopsyTreatmentData.of("Yes", JAN2015, FEB2015, Lists.newArrayList(), "", "");

    @Test
    public void reportsMissingFields() {
        final List<ValidationFinding> findings = PatientValidator.validateBiopsyData(CPCT_ID, BIOPSY_NULL);
        assertEquals(2, findings.size());
        findings.stream().map(ValidationFinding::patientId).forEach(id -> assertEquals(CPCT_ID, id));
        final List<String> findingsFields = findings.stream().map(ValidationFinding::ecrfItem).collect(Collectors.toList());
        assertTrue(findingsFields.contains(FIELD_BIOPSY_DATE));
        assertTrue(findingsFields.contains(fields(FIELD_LOCATION, FIELD_LOCATION_OTHER)));
    }

    @Test
    public void reportsBiopsiesEmpty() {
        final List<ValidationFinding> findings = PatientValidator.validateBiopsies(CPCT_ID, Lists.newArrayList(), Lists.newArrayList());
        assertEquals(1, findings.size());
        findings.stream().map(ValidationFinding::patientId).forEach(id -> assertEquals(CPCT_ID, id));
        final List<String> findingsFields = findings.stream().map(ValidationFinding::ecrfItem).collect(Collectors.toList());
        assertTrue(findingsFields.contains(FORM_BIOPS));
    }

    @Test
    public void reportsAllFindings() {
        final List<ValidationFinding> findings =
                PatientValidator.validateBiopsies(CPCT_ID, Lists.newArrayList(BIOPSY_NULL, BIOPSY_FEB1, BIOPSY_FEB2), Lists.newArrayList());
        assertEquals(2, findings.size());
        findings.stream().map(ValidationFinding::patientId).forEach(id -> assertEquals(CPCT_ID, id));
        final List<String> findingsFields = findings.stream().map(ValidationFinding::ecrfItem).collect(Collectors.toList());
        assertTrue(findingsFields.contains(FIELD_BIOPSY_DATE));
        assertTrue(findingsFields.contains(fields(FIELD_LOCATION, FIELD_LOCATION_OTHER)));
    }

    @Test
    public void reportsBiopsyBeforeRegistration() {
        final List<ValidationFinding> findings =
                PatientValidator.validateRegistrationDate(CPCT_ID, ImmutablePatientData.builder().registrationDate(MAR2016).build(),
                        Lists.newArrayList(BIOPSY_FEB1));
        assertEquals(3, findings.size());
        findings.stream().map(ValidationFinding::patientId).forEach(id -> assertEquals(CPCT_ID, id));
        final List<String> findingsFields = findings.stream().map(ValidationFinding::ecrfItem).collect(Collectors.toList());
        assertTrue(findingsFields.contains(FIELD_REGISTRATION_DATE2));
        assertTrue(findingsFields.contains(FIELD_REGISTRATION_DATE1));
        assertTrue(findingsFields.contains(FIELD_BIOPSY_DATE));
    }

    @Test
    public void reportsTreatmentBeforeBiopsy() {
        final List<ValidationFinding> findings =
                PatientValidator.validateBiopsies(CPCT_ID, Lists.newArrayList(BIOPSY_FEB1), Lists.newArrayList(TREATMENT_JAN_FEB));
        assertEquals(2, findings.size());
        findings.stream().map(ValidationFinding::patientId).forEach(id -> assertEquals(CPCT_ID, id));
        final List<String> findingsFields = findings.stream().map(ValidationFinding::ecrfItem).collect(Collectors.toList());
        assertTrue(findingsFields.contains(FORM_TREATMENT));
        assertTrue(findingsFields.contains(FORM_BIOPS));
    }
}
