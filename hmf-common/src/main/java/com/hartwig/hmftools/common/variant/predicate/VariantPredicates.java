package com.hartwig.hmftools.common.variant.predicate;

import java.util.function.Predicate;

import com.hartwig.hmftools.common.variant.SomaticVariant;
import com.hartwig.hmftools.common.variant.VariantType;

import org.jetbrains.annotations.NotNull;

public final class VariantPredicates {

    private VariantPredicates() {
    }

    @SafeVarargs
    @NotNull
    public static Predicate<SomaticVariant> and(@NotNull final Predicate<SomaticVariant>... predicates) {
        return variant -> {
            for (final Predicate<SomaticVariant> predicate : predicates) {
                if (!predicate.test(variant)) {
                    return false;
                }
            }
            return true;
        };
    }

    @SafeVarargs
    @NotNull
    public static Predicate<SomaticVariant> or(@NotNull final Predicate<SomaticVariant>... predicates) {
        return variant -> {
            for (final Predicate<SomaticVariant> predicate : predicates) {
                if (predicate.test(variant)) {
                    return true;
                }
            }
            return false;
        };
    }

    @NotNull
    public static Predicate<SomaticVariant> not(@NotNull final Predicate<SomaticVariant> predicate) {
        return variant -> !predicate.test(variant);
    }

    @NotNull
    public static Predicate<SomaticVariant> withType(@NotNull final VariantType type) {
        return variant -> variant.type().equals(type);
    }

    @NotNull
    public static Predicate<SomaticVariant> inCOSMIC() {
        return SomaticVariant::isCOSMIC;
    }

    @NotNull
    public static Predicate<SomaticVariant> inDBSNP() {
        return SomaticVariant::isDBSNP;
    }

    @NotNull
    public static Predicate<SomaticVariant> inDBSNPAndNotInCOSMIC() {
        return and(inDBSNP(), not(inCOSMIC()));
    }
}
