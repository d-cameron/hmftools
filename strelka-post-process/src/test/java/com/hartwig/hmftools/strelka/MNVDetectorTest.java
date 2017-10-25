package com.hartwig.hmftools.strelka;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Streams;
import com.google.common.io.Resources;
import com.hartwig.hmftools.strelka.scores.ImmutableReadScore;
import com.hartwig.hmftools.strelka.scores.ReadScore;
import com.hartwig.hmftools.strelka.scores.ReadType;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import htsjdk.samtools.SAMRecord;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;

public class MNVDetectorTest {
    private static final File VCF_FILE = new File(Resources.getResource("mnvs.vcf").getPath());
    private static final VCFFileReader VCF_FILE_READER = new VCFFileReader(VCF_FILE, false);
    private static final List<VariantContext> VARIANTS = Streams.stream(VCF_FILE_READER).collect(Collectors.toList());

    @Test
    public void correctlyComputes100PercentFrequency() {
        final List<Pair<ReadScore, ReadScore>> mnvScores = Lists.newArrayList();
        mnvScores.add(ImmutablePair.of(ImmutableReadScore.of(ReadType.REF, 10), ImmutableReadScore.of(ReadType.REF, 20)));
        mnvScores.add(ImmutablePair.of(ImmutableReadScore.of(ReadType.ALT, 15), ImmutableReadScore.of(ReadType.ALT, 15)));
        final MNVScore scores = build2VariantScores(mnvScores);
        assertEquals(1.0, scores.frequency(), 0.000001);
    }

    @Test
    public void correctlyComputes50PercentFrequency() {
        final List<Pair<ReadScore, ReadScore>> mnvScores = Lists.newArrayList();
        mnvScores.add(ImmutablePair.of(ImmutableReadScore.of(ReadType.ALT, 10), ImmutableReadScore.of(ReadType.REF, 20)));
        mnvScores.add(ImmutablePair.of(ImmutableReadScore.of(ReadType.ALT, 15), ImmutableReadScore.of(ReadType.ALT, 15)));
        final MNVScore scores = build2VariantScores(mnvScores);
        assertEquals(0.5, scores.frequency(), 0.000001);
    }

    @Test
    public void correctlyComputes50PercentFrequencyForMissingRead() {
        final List<Pair<ReadScore, ReadScore>> mnvScores = Lists.newArrayList();
        mnvScores.add(ImmutablePair.of(ImmutableReadScore.of(ReadType.ALT, 15), ImmutableReadScore.of(ReadType.MISSING, 0)));
        mnvScores.add(ImmutablePair.of(ImmutableReadScore.of(ReadType.ALT, 15), ImmutableReadScore.of(ReadType.ALT, 15)));
        final MNVScore scores = build2VariantScores(mnvScores);
        assertEquals(0.5, scores.frequency(), 0.000001);
    }

    @Test
    public void correctlyComputes33PercentFrequency() {
        final List<Pair<ReadScore, ReadScore>> mnvScores = Lists.newArrayList();
        mnvScores.add(ImmutablePair.of(ImmutableReadScore.of(ReadType.ALT, 5), ImmutableReadScore.of(ReadType.REF, 20)));
        mnvScores.add(ImmutablePair.of(ImmutableReadScore.of(ReadType.ALT, 15), ImmutableReadScore.of(ReadType.ALT, 15)));
        mnvScores.add(ImmutablePair.of(ImmutableReadScore.of(ReadType.REF, 25), ImmutableReadScore.of(ReadType.ALT, 10)));
        final MNVScore scores = build2VariantScores(mnvScores);
        assertEquals(0.33, scores.frequency(), 0.01);
    }

    @Test
    public void correctlyComputes80PercentFrequency() {
        final List<Pair<ReadScore, ReadScore>> mnvScores = Lists.newArrayList();
        mnvScores.add(ImmutablePair.of(ImmutableReadScore.of(ReadType.ALT, 5), ImmutableReadScore.of(ReadType.REF, 25)));
        mnvScores.add(ImmutablePair.of(ImmutableReadScore.of(ReadType.ALT, 10), ImmutableReadScore.of(ReadType.ALT, 20)));
        mnvScores.add(ImmutablePair.of(ImmutableReadScore.of(ReadType.ALT, 15), ImmutableReadScore.of(ReadType.ALT, 15)));
        mnvScores.add(ImmutablePair.of(ImmutableReadScore.of(ReadType.ALT, 13), ImmutableReadScore.of(ReadType.ALT, 17)));
        mnvScores.add(ImmutablePair.of(ImmutableReadScore.of(ReadType.ALT, 27), ImmutableReadScore.of(ReadType.ALT, 3)));
        final MNVScore scores = build2VariantScores(mnvScores);
        assertEquals(0.8, scores.frequency(), 0.000001);
    }

    @NotNull
    static SAMRecord buildSamRecord(final int alignmentStart, @NotNull final String cigar, @NotNull final String readString,
            final boolean negativeStrand) {
        final StringBuilder qualityString = new StringBuilder();
        for (int i = 0; i < readString.length(); i++) {
            qualityString.append("A");
        }
        return buildSamRecord(alignmentStart, cigar, readString, qualityString.toString(), negativeStrand);
    }

    @NotNull
    static SAMRecord buildSamRecord(final int alignmentStart, @NotNull final String cigar, @NotNull final String readString,
            @NotNull final String qualities, final boolean negativeStrand) {
        final SAMRecord record = new SAMRecord(null);
        record.setAlignmentStart(alignmentStart);
        record.setCigarString(cigar);
        record.setReadString(readString);
        record.setReadNegativeStrandFlag(negativeStrand);
        record.setBaseQualityString(qualities);
        return record;
    }

    @NotNull
    private static MNVScore build2VariantScores(@NotNull final List<Pair<ReadScore, ReadScore>> readScores) {
        MNVScore score = MNVScore.of(Lists.newArrayList(VARIANTS.get(0), VARIANTS.get(1)));
        for (final Pair<ReadScore, ReadScore> readScore : readScores) {
            final Map<VariantContext, ReadScore> recordScores = Maps.newHashMap();
            recordScores.put(VARIANTS.get(0), readScore.getLeft());
            recordScores.put(VARIANTS.get(1), readScore.getRight());
            score = MNVScore.addReadScore(score, recordScores);
        }
        return score;
    }
}