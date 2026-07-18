package org.firstinspires.ftc.teamcode.time;

public class StopWatch {
    private double totalTime;
    private long lastStart;


    public StopWatch() {
reset();
    }

    public void start() {
        if (lastStart == -1) {
            lastStart = System.currentTimeMillis();
        }
    }

    double check() {
        if (isRunning()) {
            long elapsedTime = System.currentTimeMillis() - lastStart;
            return (totalTime + elapsedTime) /1000.0;
        }
        return totalTime / 1000.0;
    }

    double stop() {
        if (isRunning()) {

            long elapsedTime = System.currentTimeMillis() - lastStart;
            totalTime = totalTime + elapsedTime;
        }
        lastStart = -1;
        return totalTime / 1000.0;

    }

    void reset() {
        totalTime = 0.0;
        lastStart = -1;

    }

    public boolean isRunning() {
        return lastStart != -1;
    }

}
