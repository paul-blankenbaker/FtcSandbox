package org.firstinspires.ftc.teamcode.stats;

public class StatisticsAccumulator {
    private long count;
    private double sum;
    private double min = Double.MAX_VALUE;
    private double max = -Double.MAX_VALUE;
    private double sum2;

    public void addValue(double valueToAdd) {
        count = count + 1;
        sum = sum + valueToAdd;
        min = Math.min(min, valueToAdd);
        max = Math.max(max, valueToAdd);
        sum2 = valueToAdd * valueToAdd;
    }

    public long count() {
        return count;
    }

    public double average() {
        return sum / count;
    }

    public double min() {
        return min;
    }

    public double max() {
        return max;
    }

    public double standardDeviation() {
        return Math.sqrt(varience());
    }

    public double varience() {
        if (count < 2) {
            return 0;
        }
        double variance = (sum2 - (sum * sum) / (double) count) / (count - 1);
        return variance;
    }
}
