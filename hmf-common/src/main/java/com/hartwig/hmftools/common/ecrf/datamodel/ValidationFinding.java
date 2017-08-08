package com.hartwig.hmftools.common.ecrf.datamodel;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(allParameters = true,
             passAnnotations = { NotNull.class, Nullable.class })
public abstract class ValidationFinding {
    @NotNull
    public abstract String level();

    @NotNull
    public abstract String patientId();

    @NotNull
    public abstract String ecrfItem();

    @NotNull
    public abstract String message();

    @NotNull
    public abstract String formStatus();

    @NotNull
    public abstract String formLocked();

    @NotNull
    public abstract String details();

    @NotNull
    public static ValidationFinding of(String level, String patientId, String ecrfItem, String message, String formStatus,
            String formLocked, String details) {
        return ImmutableValidationFinding.of(level, patientId, ecrfItem, message, formStatus, formLocked, details);
    }

    @NotNull
    public static ValidationFinding of(String level, String patientId, String ecrfItem, String message, String formStatus,
            String formLocked) {
        return ImmutableValidationFinding.of(level, patientId, ecrfItem, message, formStatus, formLocked, "");
    }
}
