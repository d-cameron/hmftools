package com.hartwig.hmftools.common.ecrf.reader;

import java.util.Map;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(of = "new",
             passAnnotations = { NotNull.class, Nullable.class })
public abstract class CodeList implements OIDObject {

    @Override
    @Value.Parameter
    @NotNull
    public abstract String OID();

    @Override
    @Value.Parameter
    @NotNull
    public abstract String name();

    @Value.Parameter
    @NotNull
    public abstract Map<Integer, String> values();

    @Override
    public String toString() {
        return "CodeList{" + "OID='" + OID() + '\'' + ", codeList=" + values() + '}';
    }
}
