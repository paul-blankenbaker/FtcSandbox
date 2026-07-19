package org.firstinspires.ftc.teamcode.time;

/**
 * A simple stopwatch class to measure elapsed time in seconds.
 */
public class StopWatch {
    private double totalTime;
    private long lastStart;

    /**
     * Constructs a new StopWatch and resets it.
     */
    public StopWatch() {
        reset();
    }

    /**
     * Starts the stopwatch. If it is already running, this method does nothing.
     */
    public void start() {
        if (lastStart == -1) {
            lastStart = System.currentTimeMillis();
        }
    }

    /**
     * Returns the total elapsed time in seconds.
     * If the stopwatch is running, it includes the time since the last start.
     *
     * @return the elapsed time in seconds.
     */
    public double check() {
        if (isRunning()) {
            long elapsedTime = System.currentTimeMillis() - lastStart;
            return (totalTime + elapsedTime) / 1000.0;
        }
        return totalTime / 1000.0;
    }

    /**
     * Stops the stopwatch and returns the total elapsed time in seconds.
     *
     * @return the total elapsed time in seconds.
     */
    public double stop() {
        if (isRunning()) {
            long elapsedTime = System.currentTimeMillis() - lastStart;
            totalTime = totalTime + elapsedTime;
        }
        lastStart = -1;
        return totalTime / 1000.0;
    }

    /**
     * Resets the stopwatch, clearing all recorded time and stopping it if it was running.
     */
    public void reset() {
        totalTime = 0.0;
        lastStart = -1;
    }

    /**
     * Checks whether the stopwatch is currently running.
     *
     * @return true if running, false otherwise.
     */
    public boolean isRunning() {
        return lastStart != -1;
    }

    @Override
    public String toString() {
        return String.format("%.3f", check());
    }


}
