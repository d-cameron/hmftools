package com.hartwig.hmftools.common.purple.segment;

import com.hartwig.hmftools.common.region.GenomeRegion;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(allParameters = true,
             passAnnotations = { NotNull.class, Nullable.class })
public abstract class PurpleSegment implements GenomeRegion {

    public abstract boolean ratioSupport();

    public abstract StructuralVariantSupport structuralVariantSupport();

}