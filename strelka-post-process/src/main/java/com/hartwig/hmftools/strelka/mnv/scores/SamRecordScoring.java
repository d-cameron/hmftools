package com.hartwig.hmftools.strelka.mnv.scores;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.annotations.VisibleForTesting;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import htsjdk.samtools.SAMRecord;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;

public final class SamRecordScoring {
    private SamRecordScoring() {
    }

    @NotNull
    private static ReadType getReadType(@NotNull final SAMRecord record, @NotNull final VariantContext variant) {
        final Allele alt = variant.getAlternateAllele(0);
        final int recordIdxOfVariantStart = record.getReadPositionAtReferencePosition(variant.getStart());
        if (recordIdxOfVariantStart == 0) {
            //MIVO: variant position was deleted
            return ReadType.MISSING;

        }
        if (variant.isSNP()) {
            if (record.getReadString().charAt(recordIdxOfVariantStart - 1) == alt.getBaseString().charAt(0)) {
                return ReadType.ALT;
            } else {
                return ReadType.REF;
            }
        }
        if (variant.isSimpleInsertion()) {
            for (int index = 0; index < alt.length(); index++) {
                final int recordIndex = recordIdxOfVariantStart + index;
                if (recordIndex - 1 < record.getReadLength() && record.getReadString().charAt(recordIndex - 1) == alt.getBaseString()
                        .charAt(index)) {
                    if (index != 0 && record.getReferencePositionAtReadPosition(recordIndex) != 0) {
                        return ReadType.REF;
                    }
                } else {
                    return ReadType.REF;
                }
            }
            // MIVO: check that this insertion is not part of a longer insertion
            if (record.getReadLength() >= recordIdxOfVariantStart + alt.length()
                    && record.getReferencePositionAtReadPosition(recordIdxOfVariantStart + alt.length()) == 0) {
                return ReadType.REF;
            }
            return ReadType.ALT;
        }
        if (variant.isSimpleDeletion()) {
            final Allele ref = variant.getReference();
            for (int index = 0; index < ref.length(); index++) {
                if (index != 0 && record.getReadPositionAtReferencePosition(variant.getStart() + index) != 0) {
                    return ReadType.REF;
                }
            }
            // MIVO: check that this deletion is not part of a longer deletion
            if (record.getAlignmentEnd() >= variant.getStart() + ref.length()
                    && record.getReadPositionAtReferencePosition(variant.getStart() + ref.length()) == 0) {
                return ReadType.REF;
            }
            return ReadType.ALT;
        }
        return ReadType.OTHER;
    }

    // MIVO: assumes single alt allele
    @NotNull
    @VisibleForTesting
    static VariantScore getVariantScore(@NotNull final SAMRecord record, @NotNull final VariantContext variant) {
        assert variant.getAlternateAlleles().size() == 1;
        final ReadType readType = getReadType(record, variant);
        final Allele alt = variant.getAlternateAllele(0);
        final int recordIdxOfVariantStart = record.getReadPositionAtReferencePosition(variant.getStart());
        switch (readType) {
            case REF: {
                if (variant.isSNP() || variant.isSimpleInsertion()) {
                    return VariantScore.of(readType, record.getBaseQualityString().charAt(recordIdxOfVariantStart - 1));
                } else if (variant.isSimpleDeletion()) {
                    final int endIndex = Math.min(recordIdxOfVariantStart - 1 + variant.getReference().length(), record.getReadLength());
                    return VariantScore.of(readType, record.getBaseQualityString().substring(recordIdxOfVariantStart - 1, endIndex));
                }
                break;
            }
            case ALT: {
                if (variant.isSNP()) {
                    return VariantScore.of(readType, record.getBaseQualityString().charAt(recordIdxOfVariantStart - 1));
                } else if (variant.isSimpleInsertion()) {
                    return VariantScore.of(readType, record.getBaseQualityString()
                            .substring(recordIdxOfVariantStart - 1, recordIdxOfVariantStart - 1 + alt.length()));
                } else if (variant.isSimpleDeletion()) {
                    //MIVO: read score of next base after deletion if present, otherwise read score of base before deletion
                    if (record.getReadLength() > recordIdxOfVariantStart) {
                        return VariantScore.of(readType, record.getBaseQualityString().charAt(recordIdxOfVariantStart));
                    } else {
                        return VariantScore.of(readType, record.getBaseQualityString().charAt(recordIdxOfVariantStart - 1));
                    }
                }
                break;
            }
        }
        return ImmutableVariantScore.of(readType, 0);
    }

    @NotNull
    public static Map<VariantContext, VariantScore> scoresPerVariant(@NotNull final SAMRecord record,
            @NotNull final Collection<VariantContext> variants) {
        return variants.stream()
                .map(variant -> ImmutablePair.of(variant, getVariantScore(record, variant)))
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
    }
}
