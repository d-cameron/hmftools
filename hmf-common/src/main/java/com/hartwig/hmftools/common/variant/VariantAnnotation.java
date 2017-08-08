package com.hartwig.hmftools.common.variant;

import java.util.List;
import java.util.StringJoiner;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class VariantAnnotation {

    @NotNull
    public abstract String allele();

    @NotNull
    public abstract List<VariantConsequence> consequences();

    @NotNull
    public String consequenceString() {
        final StringJoiner consequenceString = new StringJoiner("; ");
        for (final VariantConsequence consequence : consequences()) {
            if (!consequence.readableSequenceOntologyTerm().isEmpty()) {
                consequenceString.add(consequence.readableSequenceOntologyTerm());
            }
        }
        return consequenceString.toString();
    }

    @NotNull
    public abstract String severity();

    @NotNull
    public abstract String gene();

    @NotNull
    public abstract String geneID();

    @NotNull
    public abstract String featureType();

    @NotNull
    public abstract String featureID();

    @NotNull
    abstract String transcriptBioType();

    @NotNull
    abstract String rank();

    @NotNull
    public abstract String hgvsCoding();

    @NotNull
    public abstract String hgvsProtein();

    @NotNull
    abstract String cDNAPosAndLength();

    @NotNull
    abstract String cdsPosAndLength();

    @NotNull
    abstract String aaPosAndLength();

    @NotNull
    abstract String distance();

    @NotNull
    abstract String addition();

}
