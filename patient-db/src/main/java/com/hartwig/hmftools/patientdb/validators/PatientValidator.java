package com.hartwig.hmftools.patientdb.validators;

import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsFirst;
import static java.util.Comparator.nullsLast;

import static com.hartwig.hmftools.patientdb.readers.BiopsyReader.FIELD_BIOPSY_DATE;
import static com.hartwig.hmftools.patientdb.readers.BiopsyReader.FIELD_LOCATION;
import static com.hartwig.hmftools.patientdb.readers.BiopsyReader.FIELD_LOCATION_OTHER;
import static com.hartwig.hmftools.patientdb.readers.BiopsyReader.FORM_BIOPS;
import static com.hartwig.hmftools.patientdb.readers.BiopsyTreatmentReader.FIELD_DRUG;
import static com.hartwig.hmftools.patientdb.readers.BiopsyTreatmentReader.FIELD_DRUG_END;
import static com.hartwig.hmftools.patientdb.readers.BiopsyTreatmentReader.FIELD_DRUG_OTHER;
import static com.hartwig.hmftools.patientdb.readers.BiopsyTreatmentReader.FIELD_DRUG_START;
import static com.hartwig.hmftools.patientdb.readers.BiopsyTreatmentReader.FIELD_TREATMENT_GIVEN;
import static com.hartwig.hmftools.patientdb.readers.BiopsyTreatmentReader.FORM_TREATMENT;
import static com.hartwig.hmftools.patientdb.readers.BiopsyTreatmentResponseReader.FIELD_ASSESSMENT_DATE;
import static com.hartwig.hmftools.patientdb.readers.BiopsyTreatmentResponseReader.FIELD_MEASUREMENT_YN;
import static com.hartwig.hmftools.patientdb.readers.BiopsyTreatmentResponseReader.FIELD_RESPONSE;
import static com.hartwig.hmftools.patientdb.readers.BiopsyTreatmentResponseReader.FORM_TUMOR_MEASUREMENT;
import static com.hartwig.hmftools.patientdb.readers.CpctPatientReader.FIELD_BIRTH_YEAR1;
import static com.hartwig.hmftools.patientdb.readers.CpctPatientReader.FIELD_BIRTH_YEAR2;
import static com.hartwig.hmftools.patientdb.readers.CpctPatientReader.FIELD_BIRTH_YEAR3;
import static com.hartwig.hmftools.patientdb.readers.CpctPatientReader.FIELD_DEATH_DATE;
import static com.hartwig.hmftools.patientdb.readers.CpctPatientReader.FIELD_ETHNICITY;
import static com.hartwig.hmftools.patientdb.readers.CpctPatientReader.FIELD_PRIMARY_TUMOR_LOCATION;
import static com.hartwig.hmftools.patientdb.readers.CpctPatientReader.FIELD_PRIMARY_TUMOR_LOCATION_OTHER;
import static com.hartwig.hmftools.patientdb.readers.CpctPatientReader.FIELD_REGISTRATION_DATE1;
import static com.hartwig.hmftools.patientdb.readers.CpctPatientReader.FIELD_REGISTRATION_DATE2;
import static com.hartwig.hmftools.patientdb.readers.CpctPatientReader.FIELD_SEX;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.hartwig.hmftools.common.ecrf.datamodel.ValidationFinding;
import com.hartwig.hmftools.patientdb.Config;
import com.hartwig.hmftools.patientdb.data.BiopsyData;
import com.hartwig.hmftools.patientdb.data.BiopsyTreatmentData;
import com.hartwig.hmftools.patientdb.data.BiopsyTreatmentDrugData;
import com.hartwig.hmftools.patientdb.data.BiopsyTreatmentResponseData;
import com.hartwig.hmftools.patientdb.data.Patient;
import com.hartwig.hmftools.patientdb.data.PatientData;
import com.hartwig.hmftools.patientdb.matchers.TreatmentResponseMatcher;

import org.jetbrains.annotations.NotNull;

public class PatientValidator {
    private static final String FIELD_SEPARATOR = ";";

    public static List<ValidationFinding> validatePatient(@NotNull final Patient patient) {
        final List<ValidationFinding> findings = Lists.newArrayList();
        findings.addAll(validatePatientData(patient.patientData()));
        final String patientId = patient.patientData().cpctId();
        if (patientId != null) {
            findings.addAll(validateBiopsies(patientId, patient.clinicalBiopsies(), patient.treatments()));
            findings.addAll(validateTreatments(patientId, patient.treatments()));
            findings.addAll(validateTreatmentResponses(patientId, patient.treatments(), patient.treatmentResponses()));
            findings.addAll(validateDeathDate(patientId, patient.patientData(), patient.treatments()));
            findings.addAll(validateRegistrationDate(patientId, patient.patientData(), patient.clinicalBiopsies()));
        }
        return findings;
    }

    @NotNull
    @VisibleForTesting
    static List<ValidationFinding> validatePatientData(@NotNull final PatientData patientData) {
        final String cpctId = patientData.cpctId();
        final List<ValidationFinding> findings = Lists.newArrayList();
        if (cpctId != null) {
            if (patientData.primaryTumorLocation() == null) {
                findings.add(ValidationFinding.of("ecrf", cpctId, fields(FIELD_PRIMARY_TUMOR_LOCATION, FIELD_PRIMARY_TUMOR_LOCATION_OTHER),
                        "primary tumor location empty", patientData.primaryTumorStatus(), patientData.primaryTumorLocked()));
            }
            if (patientData.gender() == null) {
                findings.add(ValidationFinding.of("ecrf", cpctId, FIELD_SEX, "gender empty", patientData.demographyStatus(),
                        patientData.demographyLocked()));
            }
            if (patientData.registrationDate() == null) {
                findings.add(ValidationFinding.of("ecrf", cpctId, FIELD_REGISTRATION_DATE2, "registration date empty or in wrong format",
                        patientData.selectionCriteriaStatus(), patientData.selectionCriteriaLocked()));
                findings.add(ValidationFinding.of("ecrf", cpctId, FIELD_REGISTRATION_DATE1, "registration date empty or in wrong format",
                        patientData.eligibilityStatus(), patientData.eligibilityLocked()));
            }
            if (patientData.birthYear() == null) {
                findings.add(ValidationFinding.of("ecrf", cpctId, FIELD_BIRTH_YEAR1, "birth year could not be determined",
                        patientData.selectionCriteriaStatus(), patientData.selectionCriteriaLocked()));
                findings.add(ValidationFinding.of("ecrf", cpctId, fields(FIELD_BIRTH_YEAR2, FIELD_BIRTH_YEAR3),
                        "birth year could not be determined", patientData.eligibilityStatus(), patientData.eligibilityLocked()));
            }
            if (patientData.ethnicity() == null) {
                findings.add(ValidationFinding.of("ecrf", cpctId, FIELD_ETHNICITY, "ethnicity empty", patientData.demographyStatus(),
                        patientData.demographyLocked()));
            }
        }
        return findings;
    }

    @NotNull
    @VisibleForTesting
    static List<ValidationFinding> validateBiopsies(@NotNull final String patientId, @NotNull final List<BiopsyData> biopsies,
            @NotNull final List<BiopsyTreatmentData> treatments) {
        final List<ValidationFinding> findings = Lists.newArrayList();
        biopsies.forEach(biopsy -> findings.addAll(validateBiopsyData(patientId, biopsy)));
        if (biopsies.isEmpty()) {
            findings.add(ValidationFinding.of("ecrf", patientId, FORM_BIOPS, "no biopsies found", "", ""));
        }
        if (biopsies.size() > 0 && treatments.size() > 0) {
            biopsies.sort(comparing(BiopsyData::date, nullsLast(naturalOrder())));
            treatments.sort(comparing(BiopsyTreatmentData::startDate, nullsLast(naturalOrder())));
            final LocalDate firstBiopsyDate = biopsies.get(0).date();
            final LocalDate firstTreatmentStart = treatments.get(0).startDate();
            if (firstBiopsyDate != null && firstTreatmentStart != null && firstTreatmentStart.isBefore(firstBiopsyDate)) {
                findings.add(ValidationFinding.of("ecrf", patientId, FORM_TREATMENT, "first treatment start is before first biopsy date",
                        biopsies.get(0).formStatus(), biopsies.get(0).formLocked()));
                findings.add(ValidationFinding.of("ecrf", patientId, FORM_BIOPS, "first treatment start is before first biopsy date",
                        treatments.get(0).formStatus(), treatments.get(0).formLocked()));
            }
        }
        return findings;
    }

    @NotNull
    @VisibleForTesting
    static List<ValidationFinding> validateBiopsyData(@NotNull final String patientId, @NotNull final BiopsyData biopsyData) {
        final List<ValidationFinding> findings = Lists.newArrayList();
        if (biopsyData.date() == null) {
            findings.add(ValidationFinding.of("ecrf", patientId, FIELD_BIOPSY_DATE, "biopsy date empty or in wrong format",
                    biopsyData.formStatus(), biopsyData.formLocked()));
        }
        if (biopsyData.location() == null) {
            findings.add(ValidationFinding.of("ecrf", patientId, fields(FIELD_LOCATION, FIELD_LOCATION_OTHER), "biopsy location empty",
                    biopsyData.formStatus(), biopsyData.formLocked()));
        }
        return findings;
    }

    @NotNull
    @VisibleForTesting
    static List<ValidationFinding> validateTreatments(@NotNull final String patientId,
            @NotNull final List<BiopsyTreatmentData> treatments) {
        final List<ValidationFinding> findings = Lists.newArrayList();
        treatments.forEach(treatment -> validateTreatmentData(patientId, treatment));
        treatments.sort(comparing(BiopsyTreatmentData::startDate, nullsLast(naturalOrder())).thenComparing(BiopsyTreatmentData::endDate,
                nullsFirst(naturalOrder())));
        if (treatments.size() > 1) {
            for (int index = 1; index < treatments.size(); index++) {
                final BiopsyTreatmentData currentTreatment = treatments.get(index);
                final LocalDate startDate = currentTreatment.startDate();
                final LocalDate previousTreatmentEnd = treatments.get(index - 1).endDate();
                if (startDate != null && (previousTreatmentEnd == null || startDate.isBefore(previousTreatmentEnd))) {
                    findings.add(ValidationFinding.of("ecrf", patientId, FORM_TREATMENT,
                            "subsequent treatment starts before the end of previous treatment", currentTreatment.formStatus(),
                            currentTreatment.formLocked()));
                }
            }
            final List<BiopsyTreatmentData> nonFinishedTreatments =
                    treatments.stream().filter(treatment -> treatment.endDate() == null).collect(Collectors.toList());
            if (nonFinishedTreatments.size() > 1) {
                nonFinishedTreatments.forEach(treatment -> findings.add(
                        ValidationFinding.of("ecrf", patientId, FORM_TREATMENT, "end of at least 1 non-final treatment is missing",
                                treatment.formStatus(), treatment.formLocked())));
            }
        }
        return findings;
    }

    @NotNull
    @VisibleForTesting
    static List<ValidationFinding> validateTreatmentData(@NotNull final String patientId,
            @NotNull final BiopsyTreatmentData treatmentData) {
        final String treatmentGiven = treatmentData.treatmentGiven();
        final List<ValidationFinding> findings = Lists.newArrayList();
        if (treatmentGiven == null) {
            findings.add(ValidationFinding.of("ecrf", patientId, FIELD_TREATMENT_GIVEN, "treatment given field empty",
                    treatmentData.formStatus(), treatmentData.formLocked()));
        } else if (treatmentGiven.trim().toLowerCase().equals("yes")) {
            if (treatmentData.drugs().isEmpty()) {
                findings.add(
                        ValidationFinding.of("ecrf", patientId, FORM_TREATMENT, "treatment given is yes, but no treatment data filled in",
                                treatmentData.formStatus(), treatmentData.formLocked()));
            } else {
                treatmentData.drugs()
                        .forEach(drug -> findings.addAll(
                                validateDrugData(patientId, drug, treatmentData.formStatus(), treatmentData.formLocked())));
            }
        } else if (treatmentGiven.trim().toLowerCase().equals("no")) {
            if (!treatmentData.drugs().isEmpty()) {
                findings.add(ValidationFinding.of("ecrf", patientId, FIELD_TREATMENT_GIVEN,
                        "treatment given is no, but treatment data is filled in", treatmentData.formStatus(), treatmentData.formLocked()));
            }
        } else {
            findings.add(ValidationFinding.of("ecrf", patientId, FIELD_TREATMENT_GIVEN, "treatment given is not yes/no",
                    treatmentData.formStatus(), treatmentData.formLocked()));
        }
        return findings;
    }

    @NotNull
    @VisibleForTesting
    static List<ValidationFinding> validateDrugData(@NotNull final String patientId, @NotNull final BiopsyTreatmentDrugData drugData,
            @NotNull final String formStatus, @NotNull final String formLocked) {
        final LocalDate drugStart = drugData.startDate();
        final List<ValidationFinding> findings = Lists.newArrayList();
        if (drugStart == null) {
            findings.add(ValidationFinding.of("ecrf", patientId, FIELD_DRUG_START, "drug start date empty or in wrong format", formStatus,
                    formLocked));
        } else {
            final LocalDate drugEnd = drugData.endDate();
            if (drugEnd != null) {
                if (drugStart.isAfter(drugEnd)) {
                    findings.add(ValidationFinding.of("ecrf", patientId, fields(FIELD_DRUG_START, FIELD_DRUG_END),
                            "drug startDate is after drug endDate", formStatus, formLocked));
                }
            }
        }
        if (drugData.name() == null) {
            findings.add(ValidationFinding.of("ecrf", patientId, fields(FIELD_DRUG, FIELD_DRUG_OTHER), "drug name empty", formStatus,
                    formLocked));
        }
        return findings;
    }

    @NotNull
    @VisibleForTesting
    static List<ValidationFinding> validateTreatmentResponses(@NotNull final String patientId,
            @NotNull final List<BiopsyTreatmentData> treatments, @NotNull final List<BiopsyTreatmentResponseData> responses) {
        final List<ValidationFinding> findings = Lists.newArrayList();
        responses.forEach(response -> findings.addAll(validateTreatmentResponse(patientId, response)));
        treatments.sort(comparing(BiopsyTreatmentData::startDate, nullsLast(naturalOrder())));
        responses.sort(comparing(BiopsyTreatmentResponseData::assessmentDate, nullsLast(naturalOrder())));
        if (treatments.isEmpty() && !responses.isEmpty()) {
            findings.add(
                    ValidationFinding.of("ecrf", patientId, FORM_TREATMENT, "treatment response filled in, but treatment data missing", "",
                            ""));
        }
        if (!treatments.isEmpty() && !responses.isEmpty()) {
            final LocalDate firstAssessmentDate = responses.get(0).assessmentDate();
            final LocalDate firstTreatmentStart = treatments.get(0).startDate();
            if (firstAssessmentDate != null && firstTreatmentStart != null && firstAssessmentDate.isAfter(firstTreatmentStart)) {
                findings.add(ValidationFinding.of("ecrf", patientId, FORM_TREATMENT,
                        "first (baseline) measurement date is after first treatment start", treatments.get(0).formStatus(),
                        treatments.get(0).formLocked()));
                findings.add(ValidationFinding.of("ecrf", patientId, FORM_TUMOR_MEASUREMENT,
                        "first (baseline) measurement date is after first treatment start", responses.get(0).formStatus(),
                        responses.get(0).formLocked()));
            }
        }
        final List<BiopsyTreatmentData> treatmentsMissingResponse = treatments.stream()
                .filter(treatment -> shouldHaveResponse(treatment) && !hasResponse(treatment, responses))
                .collect(Collectors.toList());
        if (treatmentsMissingResponse.size() > 0) {
            findings.add(
                    ValidationFinding.of("ecrf", patientId, FORM_TUMOR_MEASUREMENT, "no treatment response for at least 1 treatment", "",
                            "", "treatments " + treatmentsMissingResponse.stream()
                                    .map(BiopsyTreatmentData::toString)
                                    .collect(Collectors.toList()) + " should have response since they lasted more than "
                                    + Config.IMMEDIATE_TREATMENT_END_THRESHOLD + " days and started more than "
                                    + Config.START_DATE_RESPONSE_THRESHOLD + " days ago"));
        }
        return findings;
    }

    private static boolean shouldHaveResponse(@NotNull final BiopsyTreatmentData treatment) {
        final LocalDate treatmentStart = treatment.startDate();
        return (treatmentStart != null && Duration.between(treatmentStart.atStartOfDay(), LocalDate.now().atStartOfDay()).toDays()
                > Config.START_DATE_RESPONSE_THRESHOLD) && !endedImmediately(treatment);
    }

    private static boolean endedImmediately(@NotNull final BiopsyTreatmentData treatment) {
        final LocalDate treatmentStart = treatment.startDate();
        final LocalDate treatmentEnd = treatment.endDate();
        return treatmentStart != null && treatmentEnd != null
                && Duration.between(treatmentStart.atStartOfDay(), treatmentEnd.atStartOfDay()).toDays()
                < Config.IMMEDIATE_TREATMENT_END_THRESHOLD;
    }

    private static boolean hasResponse(@NotNull final BiopsyTreatmentData treatment,
            @NotNull final List<BiopsyTreatmentResponseData> responses) {
        return responses.stream().filter(response -> TreatmentResponseMatcher.responseMatchesTreatment(response, treatment)).count() > 0;
    }

    @NotNull
    @VisibleForTesting
    static List<ValidationFinding> validateTreatmentResponse(@NotNull final String patientId,
            @NotNull final BiopsyTreatmentResponseData treatmentResponse) {
        final List<ValidationFinding> findings = Lists.newArrayList();
        final String measurementDone = treatmentResponse.measurementDone();
        final LocalDate assessmentDate = treatmentResponse.assessmentDate();
        if (measurementDone == null) {
            findings.add(ValidationFinding.of("ecrf", patientId, FIELD_MEASUREMENT_YN, "measurement done field empty",
                    treatmentResponse.formStatus(), treatmentResponse.formLocked()));

        } else if (measurementDone.trim().toLowerCase().equals("yes")) {
            if (assessmentDate == null) {
                findings.add(ValidationFinding.of("ecrf", patientId, FIELD_ASSESSMENT_DATE, "assessment date empty or in wrong format",
                        treatmentResponse.formStatus(), treatmentResponse.formLocked()));
            }
            if (treatmentResponse.response() == null) {
                findings.add(ValidationFinding.of("ecrf", patientId, FIELD_RESPONSE, "measurement done is yes, but response is empty",
                        treatmentResponse.formStatus(), treatmentResponse.formLocked()));
            }
        } else if (measurementDone.trim().toLowerCase().equals("no")) {
            if (assessmentDate != null) {
                findings.add(ValidationFinding.of("ecrf", patientId, FIELD_MEASUREMENT_YN,
                        "measurement done is no, but assessment date filled in", treatmentResponse.formStatus(),
                        treatmentResponse.formLocked()));
            }
            if (treatmentResponse.response() != null) {
                findings.add(ValidationFinding.of("ecrf", patientId, FIELD_MEASUREMENT_YN, "measurement done is no, but response filled in",
                        treatmentResponse.formStatus(), treatmentResponse.formLocked()));
            }
        } else {
            findings.add(ValidationFinding.of("ecrf", patientId, FIELD_MEASUREMENT_YN, "measurement done is not yes/no",
                    treatmentResponse.formStatus(), treatmentResponse.formLocked()));
        }
        if (treatmentResponse.response() != null && treatmentResponse.assessmentDate() == null) {
            findings.add(ValidationFinding.of("ecrf", patientId, FIELD_ASSESSMENT_DATE, "response filled in, but no assessment date found",
                    treatmentResponse.formStatus(), treatmentResponse.formLocked()));
        }
        return findings;
    }

    @NotNull
    @VisibleForTesting
    static List<ValidationFinding> validateDeathDate(@NotNull final String patientId, @NotNull final PatientData patient,
            @NotNull final List<BiopsyTreatmentData> treatments) {
        final List<ValidationFinding> findings = Lists.newArrayList();
        final LocalDate deathDate = patient.deathDate();
        if (deathDate != null && !treatments.isEmpty()) {
            treatments.sort(comparing(BiopsyTreatmentData::endDate, nullsLast(naturalOrder())));
            final BiopsyTreatmentData lastTreatment = treatments.get(treatments.size() - 1);
            final LocalDate lastTreatmentEndDate = lastTreatment.endDate();
            if (lastTreatmentEndDate == null || lastTreatmentEndDate.isAfter(deathDate)) {
                findings.add(ValidationFinding.of("ecrf", patientId, FIELD_DEATH_DATE, "death date before end of last treatment",
                        patient.deathStatus(), patient.deathLocked()));
                findings.add(ValidationFinding.of("ecrf", patientId, FORM_TREATMENT, "death date before end of last treatment",
                        lastTreatment.formStatus(), lastTreatment.formLocked()));
            }
        }
        return findings;
    }

    @NotNull
    @VisibleForTesting
    static List<ValidationFinding> validateRegistrationDate(@NotNull final String patientId, @NotNull final PatientData patient,
            @NotNull final List<BiopsyData> biopsies) {
        final List<ValidationFinding> findings = Lists.newArrayList();
        final LocalDate registrationDate = patient.registrationDate();
        if (registrationDate != null && !biopsies.isEmpty()) {
            final List<BiopsyData> biopsiesPriorToRegistration = biopsies.stream().filter(biopsy -> {
                final LocalDate biopsyDate = biopsy.date();
                return biopsyDate != null && biopsyDate.isBefore(registrationDate);
            }).collect(Collectors.toList());
            if (biopsiesPriorToRegistration.size() > 0) {
                final String detailsMessage =
                        "registrationDate: " + registrationDate + ". biopsies: " + biopsiesPriorToRegistration.stream()
                                .map(BiopsyData::toString)
                                .collect(Collectors.toList());
                findings.add(ValidationFinding.of("ecrf", patientId, FIELD_REGISTRATION_DATE2,
                        "at least 1 biopsy date prior to registration date", patient.selectionCriteriaStatus(),
                        patient.selectionCriteriaLocked(), detailsMessage));
                findings.add(ValidationFinding.of("ecrf", patientId, FIELD_REGISTRATION_DATE1,
                        "at least 1 biopsy date prior to registration date", patient.eligibilityStatus(), patient.eligibilityLocked(),
                        detailsMessage));
                biopsiesPriorToRegistration.forEach(biopsy -> findings.add(
                        ValidationFinding.of("ecrf", patientId, FIELD_BIOPSY_DATE, "at least 1 biopsy date prior to registration date",
                                biopsy.formStatus(), biopsy.formLocked(), detailsMessage)));
            }
        }
        return findings;
    }

    @NotNull
    @VisibleForTesting
    static String fields(@NotNull final String... fields) {
        return String.join(FIELD_SEPARATOR, fields);
    }
}
