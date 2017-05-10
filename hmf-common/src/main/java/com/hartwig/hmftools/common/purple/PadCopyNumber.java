package com.hartwig.hmftools.common.purple;

import com.google.common.collect.Lists;
import com.hartwig.hmftools.common.chromosome.Chromosomes;
import com.hartwig.hmftools.common.freec.FreecCopyNumber;
import com.hartwig.hmftools.common.freec.ImmutableFreecCopyNumber;

import java.util.List;
import java.util.function.Consumer;

public enum PadCopyNumber {
    ;

    public static List<FreecCopyNumber> pad(List<FreecCopyNumber> copyNumbers) {
        PadCopyNumberForChromosome padder = null;
        List<FreecCopyNumber> result = Lists.newArrayList();
        for (FreecCopyNumber copyNumber : copyNumbers) {
            if (padder == null) {
                padder = new PadCopyNumberForChromosome(copyNumber.chromosome(), result::add);
            } else if (!padder.chromosome.equals(copyNumber.chromosome())) {
                padder.complete();
                padder = new PadCopyNumberForChromosome(copyNumber.chromosome(), result::add);
            }
            padder.addCopyNumber(copyNumber);
        }

        return result;
    }

    static class PadCopyNumberForChromosome {

        private final String chromosome;
        private final Consumer<FreecCopyNumber> handler;
        private long currentPosition = 1;

        PadCopyNumberForChromosome(String chromosome, Consumer<FreecCopyNumber> handler) {
            this.chromosome = chromosome;
            this.handler = handler;
        }

        String getChromosome() {
            return chromosome;
        }

        void addCopyNumber(FreecCopyNumber number) {
            if (number.start() > currentPosition) {
                handler.accept(createDefaultCopyNumber(currentPosition, number.start() - 1));
            }

            handler.accept(number);
            currentPosition = number.end() + 1;
        }

        void complete() {
            long chromosomeLength = Chromosomes.length(chromosome);
            if (currentPosition < chromosomeLength) {
                handler.accept(createDefaultCopyNumber(currentPosition, chromosomeLength));
            }
        }

        private FreecCopyNumber createDefaultCopyNumber(long aStart, long aEnd) {
            return ImmutableFreecCopyNumber.builder().chromosome(chromosome).start(aStart).end(aEnd).value(2).build();
        }
    }
}
