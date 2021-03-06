package com.hartwig.hmftools.common.actionability.somaticvariant;

import com.google.common.collect.Lists;
import com.hartwig.hmftools.common.actionability.cancertype.CancerTypeAnalyzer;
import com.hartwig.hmftools.common.actionability.cancertype.CancerTypeReading;
import com.hartwig.hmftools.common.actionability.cancertype.ImmutableCancerTypeReading;
import com.hartwig.hmftools.common.variant.CodingEffect;
import com.hartwig.hmftools.common.variant.Hotspot;
import com.hartwig.hmftools.common.variant.ImmutableSomaticVariantImpl;
import com.hartwig.hmftools.common.variant.SomaticVariant;
import com.hartwig.hmftools.common.variant.VariantType;

import org.apache.logging.log4j.util.Strings;
import org.junit.Test;

public class SomaticVariantEvidenceAnalyzerTest {

    @Test
    public void actionabilityWorksVariants() {
        ActionableSomaticVariant actionableSomaticVariant = ImmutableActionableSomaticVariant.builder()
                .gene("BRAF")
                .chromosome("X")
                .position(1234)
                .ref("C")
                .alt("T")
                .source("civic")
                .reference("BRAF 600E")
                .drug("Dabrafenib")
                .drugsType("BRAF inhibitor")
                .cancerType("Skin Melanoma")
                .level("A")
                .response("Responsive")
                .build();

        ActionableRange actionableRange = ImmutableActionableRange.builder()
                .gene("BRAF")
                .chromosome("7")
                .start(10)
                .end(1500)
                .source("oncoKB")
                .reference("NRAS Oncogenic Mutations")
                .drug("Cetuximab")
                .drugsType("EGFR mAb inhibitor")
                .cancerType("Skin Melanoma")
                .level("A")
                .response("Resistant")
                .build();

        CancerTypeReading reading = ImmutableCancerTypeReading.builder().doidSet("4159").cancerType("Skin").build();

        SomaticVariantEvidenceAnalyzer analyzer =
                new SomaticVariantEvidenceAnalyzer(Lists.newArrayList(actionableSomaticVariant), Lists.newArrayList(actionableRange));

        CancerTypeAnalyzer cancerType = new CancerTypeAnalyzer(Lists.newArrayList(reading));

        SomaticVariant variant = ImmutableSomaticVariantImpl.builder()
                .chromosome("7")
                .position(100)
                .ref("C")
                .alt("T")
                .type(VariantType.UNDEFINED)
                .filter(Strings.EMPTY)
                .gene("BRAF")
                .genesEffected(0)
                .worstEffect(Strings.EMPTY)
                .worstEffectTranscript(Strings.EMPTY)
                .worstCodingEffect(CodingEffect.NONE)
                .totalReadCount(0)
                .alleleReadCount(0)
                .canonicalEffect(Strings.EMPTY)
                .canonicalCodingEffect(CodingEffect.UNDEFINED)
                .canonicalHgvsCodingImpact(Strings.EMPTY)
                .canonicalHgvsProteinImpact(Strings.EMPTY)
                .hotspot(Hotspot.NON_HOTSPOT)
                .mappability(0D)
                .build();

        //   assertEquals(false, analyzer.actionableVariants(variant, cancerType, "4159", "Skin"));
        //   assertEquals(false, analyzer.actionableVariants(variant, cancerType, "4159", "Breast"));
        //     assertEquals(true, analyzer.actionableRange(variant, cancerType, "4159", "Skin"));
        //    assertEquals(false, analyzer.actionableRange(variant, cancerType, "4159", "Kidney"));
    }
}