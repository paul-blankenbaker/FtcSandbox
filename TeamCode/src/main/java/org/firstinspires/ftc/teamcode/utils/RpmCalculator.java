package org.firstinspires.ftc.teamcode.utils;

/**
 * Keeps a rolling window of time and position values and calculates RPM based on sample size.
 */
public class RpmCalculator {
    private final long[] nanoTimestampValues;
    private final int[] positionValues;

    private int currentSampleIndex;
    private int sampleCount;
    private final double countsPerRevolution;

    public RpmCalculator(double countsPerRevolution, int sampleSize) {
        if (countsPerRevolution <= 0) {
            throw new IllegalArgumentException("countsPerRevolution must be positive");
        }
        if (sampleSize <= 1) {
            throw new IllegalArgumentException("sampleSize must be at least 2");
        }
        this.countsPerRevolution = countsPerRevolution;
        this.nanoTimestampValues = new long[sampleSize];
        this.positionValues = new int[sampleSize];
        this.currentSampleIndex = -1;
        this.sampleCount = 0;
    }

    public RpmCalculator() {
        this(28.0, 100);
    }

    public void addSample(int position) {
        addSample(position, System.nanoTime());
    }

    public void addSample(int position, long nanoTimestamp) {
        currentSampleIndex = (currentSampleIndex + 1) % nanoTimestampValues.length;
        nanoTimestampValues[currentSampleIndex] = nanoTimestamp;
        positionValues[currentSampleIndex] = position;
        if (sampleCount < nanoTimestampValues.length) {
            sampleCount++;
        }
    }

    /**
     * Looks back in history buffer from current sample until secondsBack of time has elapsed
     * @param secondsBack seconds of history to look back
     * @return calculated RPM, or 0.0 if insufficient data
     */
    public double computeRpm(double secondsBack) {
        return computeRpm(secondsBack, System.nanoTime());
    }

    /**
     * Looks back in history buffer from current sample until secondsBack of time has elapsed
     * relative to the provided current timestamp in nanoseconds.
     * @param secondsBack seconds of history to look back
     * @param currentNanoTime reference timestamp in nanoseconds
     * @return calculated RPM, or 0.0 if insufficient data
     */
    public double computeRpm(double secondsBack, long currentNanoTime) {
        if (sampleCount < 2 || secondsBack <= 0) {
            return 0.0;
        }

        long seekTime = currentNanoTime - (long) (secondsBack * 1e9);

        int oldestIndexInWindow = currentSampleIndex;
        for (int i = 1; i < sampleCount; i++) {
            int index = (currentSampleIndex - i + nanoTimestampValues.length) % nanoTimestampValues.length;
            oldestIndexInWindow = index;
            if (nanoTimestampValues[index] <= seekTime) {
                break;
            }
        }

        long duration = nanoTimestampValues[currentSampleIndex] - nanoTimestampValues[oldestIndexInWindow];
        if (duration <= 0) {
            return 0.0;
        }

        double deltaPosition = positionValues[currentSampleIndex] - positionValues[oldestIndexInWindow];
        double durationSeconds = duration * 1e-9;

        return (deltaPosition * 60.0) / (durationSeconds * countsPerRevolution);
    }

    public double getCountsPerRevolution() {
        return countsPerRevolution;
    }

    public int getSampleSize() {
        return nanoTimestampValues.length;
    }

    public int getSampleCount() {
        return sampleCount;
    }

    /**
     * Computes the samples per second from the newest to oldest sample.
     * @return samples per second, or 0.0 if insufficient data
     */
    public double getSamplesPerSecond() {
        if (sampleCount < 2) {
            return 0.0;
        }

        int oldestIndex = (currentSampleIndex - (sampleCount - 1) + nanoTimestampValues.length) % nanoTimestampValues.length;
        long duration = nanoTimestampValues[currentSampleIndex] - nanoTimestampValues[oldestIndex];
        if (duration <= 0) {
            return 0.0;
        }

        double durationSeconds = duration * 1e-9;
        return (sampleCount - 1) / durationSeconds;
    }
}
