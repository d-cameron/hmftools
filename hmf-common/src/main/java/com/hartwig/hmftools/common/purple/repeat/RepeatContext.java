package com.hartwig.hmftools.common.purple.repeat;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class RepeatContext {

    public abstract int count();

    public abstract String sequence();
}
