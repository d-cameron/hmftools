package com.hartwig.hmftools.breakpointinspector;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMSequenceDictionary;

class Util {

    enum ReadCategory {
        UNSET,
        NORMAL,
        SPAN,
        MATE_UNMAPPED,
        UNMAPPED,
        SECONDARY,
        CHIMERIC
    }

    enum Overlap {
        FILTERED,
        PROXIMITY,
        INTERSECT,
        STRADDLE,
        CLIP_EXACT,
        CLIP_OTHER
    }

    enum Region {
        OTHER,
        BP1,
        BP2
    }

    enum HMFVariantType {
        DEL,
        INV3,
        INV5,
        DUP
    }

    static class HMFVariantContext {
        Location Breakpoint1;
        Range Uncertainty1;
        Location Breakpoint2;
        Range Uncertainty2;
        HMFVariantType Type;

        HMFVariantContext(final Location bp1, final Location bp2, final HMFVariantType type) {
            Breakpoint1 = bp1;
            Breakpoint2 = bp2;
            Type = type;
        }
    }

    static List<String> prefixList(final List<String> list, final String prefix) {
        return list.stream().map(s -> prefix + s).collect(Collectors.toList());
    }

    static List<String> toStrings(final List<Integer> list) {
        return list.stream().map(i -> Integer.toString(i)).collect(Collectors.toList());
    }

    static class ClipInfo {
        Location Alignment;
        int Length = 0;
        boolean HardClipped = false;
        String Sequence = "";
    }

    static class ReadInfo {
        SAMRecord Read = null;
        Region Breakpoint = Region.OTHER;
        ReadCategory Category = ReadCategory.UNSET;
        Overlap Location = Overlap.FILTERED;
    }

    static class NamedReadCollection {
        ArrayList<ReadInfo> Reads = new ArrayList<>();
    }

    static class ClassifiedReadResults {
        Map<String, NamedReadCollection> ReadMap = new Hashtable<>();
    }

    static class Location implements Comparable<Location> {
        String ReferenceName;
        int ReferenceIndex = -1;
        int Position = -1;

        private Location() {
        }

        static Location parseLocationString(final String location, final SAMSequenceDictionary dictionary)
                throws RuntimeException {
            final Location result = new Location();

            final String[] split = location.split(":");
            if (split.length != 2) {
                throw new RuntimeException(location + " is not a valid location string");
            }

            final String chromosome = split[0];
            result.ReferenceName = chromosome;
            try {
                result.Position = Integer.parseInt(split[1]);
            } catch (NumberFormatException e) {
                throw new RuntimeException(location + " is not a valid location string");
            }

            // query the position
            result.ReferenceIndex = dictionary.getSequenceIndex(chromosome);
            if (result.ReferenceIndex < 0) {
                if (!chromosome.startsWith("chr"))
                    result.ReferenceIndex = dictionary.getSequenceIndex("chr" + chromosome);
                else
                    result.ReferenceIndex = dictionary.getSequenceIndex(chromosome.substring(3));
            }
            if (result.ReferenceIndex < 0) {
                throw new RuntimeException(chromosome + " is not in the BAM");
            }

            return result;
        }

        static Location fromSAMRecord(final SAMRecord record) {
            return fromSAMRecord(record, true);
        }

        static Location fromSAMRecord(final SAMRecord record, boolean alignmentStart) {
            final Location result = new Location();
            result.ReferenceName = record.getReferenceName();
            result.ReferenceIndex = record.getReferenceIndex();
            result.Position = alignmentStart ? record.getAlignmentStart() : record.getAlignmentEnd();
            return result;
        }

        Location add(int delta) {
            final Location result = new Location();
            result.ReferenceName = this.ReferenceName;
            result.ReferenceIndex = this.ReferenceIndex;
            result.Position = this.Position + delta;
            return result;
        }

        boolean closeTo(final Location other) {
            return ReferenceIndex == other.ReferenceIndex && Math.abs(other.Position - Position) <= 1;
        }

        @Override
        public int hashCode() {
            return Objects.hash(ReferenceIndex, Position);
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj == null)
                return false;
            if (!(obj instanceof Location))
                return false;

            Location other = (Location) obj;
            return ReferenceIndex == other.ReferenceIndex && Position == other.Position;
        }

        @Override
        public int compareTo(@NotNull final Location o) {
            final int comp1 = Integer.compare(ReferenceIndex, o.ReferenceIndex);
            if (comp1 == 0)
                return Integer.compare(Position, o.Position);
            return comp1;
        }

        @Override
        public String toString() {
            return ReferenceName + ":" + Position;
        }
    }

    static class Range {
        int Min;
        int Max;

        Range(int min, int max) {
            Min = min;
            Max = max;
        }
    }
}