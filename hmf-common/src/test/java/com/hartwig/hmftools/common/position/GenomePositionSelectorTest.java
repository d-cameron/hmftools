package com.hartwig.hmftools.common.position;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.function.Consumer;

import com.google.common.collect.Lists;
import com.hartwig.hmftools.common.region.GenomeRegion;
import com.hartwig.hmftools.common.region.bed.ImmutableBEDGenomeRegion;

import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;

public class GenomePositionSelectorTest {

    private List<GenomePosition> positions;
    private GenomePositionSelector<GenomePosition> standardSelector;

    private GenomeRegion region1;
    private GenomeRegion region2;
    private GenomeRegion region3;

    private GenomePosition beforeRegion1;
    private GenomePosition inRegion1a;
    private GenomePosition inRegion1b;
    private GenomePosition inRegion1c;
    private GenomePosition inRegion2;
    private GenomePosition afterRegion2;
    private GenomePosition inRegion3;
    private GenomePosition afterRegion3;

    @Before
    public void setup() {

        region1 = createRegion("1", 200, 300);
        region2 = createRegion("1", 400, 500);
        region3 = createRegion("2", 1, 100);

        beforeRegion1 = createPosition("1", 50);
        inRegion1a = createPosition("1", 200);
        inRegion1b = createPosition("1", 250);
        inRegion1c = createPosition("1", 300);
        inRegion2 = createPosition("1", 450);
        afterRegion2 = createPosition("1", 550);
        inRegion3 = createPosition("2", 50);
        afterRegion3 = createPosition("2", 150);
        positions = Lists.newArrayList(beforeRegion1, inRegion1a, inRegion1b, inRegion1c, inRegion2, afterRegion2, inRegion3, afterRegion3);

        standardSelector = GenomePositionSelectorFactory.create(positions);
    }

    @Test
    public void testExpectedBehaviour() {

        final ListConsumer region1Consumer = new ListConsumer();
        standardSelector.select(region1, region1Consumer);
        assertEquals(inRegion1a, region1Consumer.getPositions().get(0));
        assertEquals(inRegion1b, region1Consumer.getPositions().get(1));
        assertEquals(inRegion1c, region1Consumer.getPositions().get(2));

        final ListConsumer region2Consumer = new ListConsumer();
        standardSelector.select(region2, region2Consumer);
        assertEquals(inRegion2, region2Consumer.getPositions().get(0));

        final ListConsumer region3Consumer = new ListConsumer();
        standardSelector.select(region3, region3Consumer);
        assertEquals(inRegion3, region3Consumer.getPositions().get(0));
    }


    private GenomeRegion createRegion(final String chromosome, final long start, final long end) {
        return ImmutableBEDGenomeRegion.builder().chromosome(chromosome).start(start).end(end).build();
    }

    private GenomePosition createPosition(final String chromosome, final long position) {
        return new GenomePosition() {
            @NotNull
            @Override
            public String chromosome() {
                return chromosome;
            }

            @Override
            public long position() {
                return position;
            }
        };
    }

    class ListConsumer implements Consumer<GenomePosition> {

        private final List<GenomePosition> positions = Lists.newArrayList();

        public List<GenomePosition> getPositions() {
            return positions;
        }

        @Override
        public void accept(final GenomePosition genomePosition) {
            positions.add(genomePosition);
        }
    }

}
