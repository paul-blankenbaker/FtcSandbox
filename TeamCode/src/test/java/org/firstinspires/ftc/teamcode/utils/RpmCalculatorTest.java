package org.firstinspires.ftc.teamcode.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

public class RpmCalculatorTest {

    private static final double EPSILON = 1e-4;

    @Test
    public void testDefaultConstructor() {
        RpmCalculator calc = new RpmCalculator();
        assertEquals(28.0, calc.getCountsPerRevolution(), EPSILON);
        assertEquals(100, calc.getSampleSize());
        assertEquals(0, calc.getSampleCount());
    }

    @Test
    public void testCustomConstructor() {
        RpmCalculator calc = new RpmCalculator(537.7, 50);
        assertEquals(537.7, calc.getCountsPerRevolution(), EPSILON);
        assertEquals(50, calc.getSampleSize());
        assertEquals(0, calc.getSampleCount());
    }

    @Test
    public void testInvalidConstructorArguments() {
        try {
            new RpmCalculator(0, 50);
            fail("Should throw IllegalArgumentException for cpr <= 0");
        } catch (IllegalArgumentException e) {
            // expected
        }

        try {
            new RpmCalculator(28.0, 1);
            fail("Should throw IllegalArgumentException for sampleSize <= 1");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testInsufficientSamples() {
        RpmCalculator calc = new RpmCalculator(28.0, 10);

        // 0 samples
        assertEquals(0.0, calc.computeRpm(1.0, 1_000_000_000L), EPSILON);

        // 1 sample
        calc.addSample(0, 0L);
        assertEquals(1, calc.getSampleCount());
        assertEquals(0.0, calc.computeRpm(1.0, 1_000_000_000L), EPSILON);
    }

    @Test
    public void testZeroOrNegativeSecondsBack() {
        RpmCalculator calc = new RpmCalculator(28.0, 10);
        calc.addSample(0, 0L);
        calc.addSample(28, 1_000_000_000L); // 1 sec later, 28 counts = 60 RPM

        assertEquals(0.0, calc.computeRpm(0.0, 1_000_000_000L), EPSILON);
        assertEquals(0.0, calc.computeRpm(-0.5, 1_000_000_000L), EPSILON);
    }

    @Test
    public void testConstantPositiveRpm() {
        // 28 counts per rev motor running at 60 RPM = 1 rev/sec = 28 counts/sec
        // +7 counts per 0.25s
        RpmCalculator calc = new RpmCalculator(28.0, 10);

        long timeNs = 0L;
        int pos = 0;
        calc.addSample(pos, timeNs);

        for (int i = 1; i <= 4; i++) {
            timeNs += 250_000_000L; // +0.25s
            pos += 7;               // +7 counts per 0.25s => 28 counts/sec
            calc.addSample(pos, timeNs);
        }

        // Compute RPM looking back 0.5s from 1.0s timestamp
        double rpm = calc.computeRpm(0.5, 1_000_000_000L);
        assertEquals(60.0, rpm, EPSILON);
    }

    @Test
    public void testConstantNegativeRpm() {
        // 28 counts per rev motor running at -120 RPM = -2 rev/sec = -56 counts/sec
        // -14 counts per 0.25s
        RpmCalculator calc = new RpmCalculator(28.0, 10);

        long timeNs = 0L;
        int pos = 1000;
        calc.addSample(pos, timeNs);

        for (int i = 1; i <= 4; i++) {
            timeNs += 250_000_000L; // +0.25s
            pos -= 14;              // -14 counts per 0.25s => -56 counts/sec
            calc.addSample(pos, timeNs);
        }

        double rpm = calc.computeRpm(0.5, 1_000_000_000L);
        assertEquals(-120.0, rpm, EPSILON);
    }

    @Test
    public void testWindowedLookback() {
        // Motor is stationary for 1s, then speeds up to 300 RPM (5 rev/s = 140 counts/s) for 0.5s
        RpmCalculator calc = new RpmCalculator(28.0, 20);

        // Stationary phase: 0.0s to 1.0s (11 samples, 0.1s apart, position = 0)
        long timeNs = 0L;
        int pos = 0;
        for (int i = 0; i <= 10; i++) {
            calc.addSample(pos, timeNs);
            timeNs += 100_000_000L;
        }

        // Running phase: 1.1s to 1.5s (5 samples, position increases by 14 counts/0.1s)
        for (int i = 1; i <= 5; i++) {
            pos += 14; // 14 counts/0.1s = 140 counts/s = 5 rev/s = 300 RPM
            calc.addSample(pos, timeNs);
            timeNs += 100_000_000L;
        }

        // If we look back 0.4 seconds from t=1.5s (window: 1.1s to 1.5s), RPM should be 300.0
        long currentTime = 1_500_000_000L;
        double shortWindowRpm = calc.computeRpm(0.4, currentTime);
        assertEquals(300.0, shortWindowRpm, EPSILON);

        // If we look back 1.5 seconds from t=1.5s (window: 0.0s to 1.5s), average RPM across whole history
        // 70 counts in 1.5 sec = 2.5 revs in 1.5 sec = 2.5 / (1.5/60) = 100 RPM
        double longWindowRpm = calc.computeRpm(1.5, currentTime);
        assertEquals(100.0, longWindowRpm, EPSILON);
    }

    @Test
    public void testCircularBufferRollover() {
        // Buffer size 5, motor running at 120 RPM (2 rev/s = 56 counts/s)
        RpmCalculator calc = new RpmCalculator(28.0, 5);

        // Add 12 samples (causing buffer to roll over twice)
        // 14 counts per 0.25s = 56 counts/sec
        for (int i = 0; i < 12; i++) {
            long timeNs = i * 250_000_000L;
            int pos = i * 14;
            calc.addSample(pos, timeNs);
        }

        assertEquals(5, calc.getSampleCount()); // Max capacity

        // Last sample added was i=11 at t=2.75s (2,750,000,000 ns)
        // Look back 0.5s (target 2.25s)
        double rpm = calc.computeRpm(0.5, 2_750_000_000L);
        assertEquals(120.0, rpm, EPSILON);
    }

    @Test
    public void testZeroDurationSameTimestamp() {
        RpmCalculator calc = new RpmCalculator(28.0, 10);
        calc.addSample(0, 1_000_000_000L);
        calc.addSample(10, 1_000_000_000L); // Duplicate timestamp

        assertEquals(0.0, calc.computeRpm(0.5, 1_000_000_000L), EPSILON);
    }

    @Test
    public void testGetSamplesPerSecond() {
        RpmCalculator calc = new RpmCalculator(28.0, 5);

        // Insufficient samples
        assertEquals(0.0, calc.getSamplesPerSecond(), EPSILON);
        calc.addSample(0, 0L);
        assertEquals(0.0, calc.getSamplesPerSecond(), EPSILON);

        // 5 samples spaced 100 ms (0.1 s) apart -> 4 intervals over 0.4 s -> 10 samples/sec
        for (int i = 1; i <= 4; i++) {
            calc.addSample(i * 10, i * 100_000_000L);
        }
        assertEquals(5, calc.getSampleCount());
        assertEquals(10.0, calc.getSamplesPerSecond(), EPSILON);

        // Circular buffer wrap around: add 5 more samples spaced 200 ms (0.2 s) apart
        // Timestamps added:
        // i=5: 600 ms
        // i=6: 800 ms
        // i=7: 1000 ms
        // i=8: 1200 ms
        // i=9: 1400 ms
        // History buffer holds 5 samples from t=600 ms to t=1400 ms (duration 800 ms = 0.8 s)
        // 4 intervals over 0.8 s = 5.0 samples/sec
        for (int i = 5; i < 10; i++) {
            calc.addSample(i * 10, (400L + (i - 4) * 200L) * 1_000_000L);
        }
        assertEquals(5, calc.getSampleCount());
        assertEquals(5.0, calc.getSamplesPerSecond(), EPSILON);
    }

    @Test
    public void testGetSampleTime() {
        RpmCalculator calc = new RpmCalculator(28.0, 5);

        // 1. No samples added -> return 0 for any index
        assertEquals(0L, calc.getSampleTime(0));
        assertEquals(0L, calc.getSampleTime(-1));
        assertEquals(0L, calc.getSampleTime(1));

        // 2. Add 3 samples
        calc.addSample(10, 1000L);
        calc.addSample(20, 2000L);
        calc.addSample(30, 3000L);

        assertEquals(3000L, calc.getSampleTime(0));   // Newest
        assertEquals(2000L, calc.getSampleTime(-1));  // Previous
        assertEquals(1000L, calc.getSampleTime(-2));  // Oldest available

        // Invalid / out-of-bounds queries
        assertEquals(0L, calc.getSampleTime(1));     // Future sample index
        assertEquals(0L, calc.getSampleTime(-3));    // Before available samples
        assertEquals(0L, calc.getSampleTime(-100));  // Out of range negative index

        // 3. Add more samples to force circular buffer wrap around
        // Capacity is 5, adding samples at 4000L, 5000L, 6000L, 7000L, 8000L (total 8 samples added)
        for (long t = 4000L; t <= 8000L; t += 1000L) {
            calc.addSample(0, t);
        }
        assertEquals(5, calc.getSampleCount());

        assertEquals(8000L, calc.getSampleTime(0));   // Newest
        assertEquals(7000L, calc.getSampleTime(-1));
        assertEquals(6000L, calc.getSampleTime(-2));
        assertEquals(5000L, calc.getSampleTime(-3));
        assertEquals(4000L, calc.getSampleTime(-4));  // Oldest stored sample

        // Out of bounds queries after rollover
        assertEquals(0L, calc.getSampleTime(1));
        assertEquals(0L, calc.getSampleTime(-5));
        assertEquals(0L, calc.getSampleTime(-100));
    }
}
