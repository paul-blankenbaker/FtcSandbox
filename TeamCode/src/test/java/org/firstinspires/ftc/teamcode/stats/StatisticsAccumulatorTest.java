package org.firstinspires.ftc.teamcode.stats;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class StatisticsAccumulatorTest {

    @Test
    public void testAccumulator() {
        StatisticsAccumulator stats = new StatisticsAccumulator();
        assertEquals(0, stats.count());
        assertEquals(Double.NaN, stats.average(), 0.001);
        assertEquals(Double.MAX_VALUE, stats.min(), 0.001);
        assertEquals(-Double.MAX_VALUE, stats.max(), 0.001);
        assertEquals(0.0, stats.variance(), 0.001);
        assertEquals(0.0, stats.standardDeviation(), 0.001);

        stats.addValue(1.0);
        assertEquals(1, stats.count());
        assertEquals(1.0, stats.average(), 0.001);
        assertEquals(1.0, stats.min(), 0.001);
        assertEquals(1.0, stats.max(), 0.001);
        assertEquals(0.0, stats.variance(), 0.001);
        assertEquals(0.0, stats.standardDeviation(), 0.001);

        stats.addValue(2.0);
        assertEquals(2, stats.count());
        assertEquals(1.5, stats.average(), 0.001);
        assertEquals(1.0, stats.min(), 0.001);
        assertEquals(2.0, stats.max(), 0.001);
        assertEquals(0.5, stats.variance(), 0.001);
        assertEquals(Math.sqrt(0.5), stats.standardDeviation(), 0.001);

        stats.addValue(3.0);
        assertEquals(3, stats.count());
        assertEquals(2.0, stats.average(), 0.001);
        assertEquals(1.0, stats.min(), 0.001);
        assertEquals(3.0, stats.max(), 0.001);
        
        // Variance for 1, 2, 3:
        // average = 2
        // sum of squares of diffs = (1-2)^2 + (2-2)^2 + (3-2)^2 = 1 + 0 + 1 = 2
        // variance = 2 / (3-1) = 1
        assertEquals(1.0, stats.variance(), 0.001);
        assertEquals(1.0, stats.standardDeviation(), 0.001);
    }

    @Test
    public void testEmpty() {
        StatisticsAccumulator stats = new StatisticsAccumulator();
        assertEquals(0, stats.count());
        assertEquals(0.0, stats.variance(), 0.001);
    }

}
