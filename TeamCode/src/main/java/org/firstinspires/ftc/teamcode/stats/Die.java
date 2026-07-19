package org.firstinspires.ftc.teamcode.stats;

import java.util.Random;

public class Die {
    private final int numSides;
    private final Random random;
    private int currentRoll; // Variable that stores the die roll

    public Die(int numSides) {
        // Enforce that the number of sides must be greater than 3
        if (numSides <= 3) {
            throw new IllegalArgumentException("A die must have more than 3 sides.");
        }
        this.numSides = numSides;
        this.random = new Random();
        this.currentRoll = 0; // Starts at 0 to show it hasn't been rolled yet
    }

    public int roll() {
        // Rolls the die, stores the result in our variable, and returns it
        this.currentRoll = random.nextInt(numSides) + 1;
        return this.currentRoll;
    }

    public int sides() {
        return this.numSides;
    }

    @Override
    public String toString() {
        if (currentRoll == 0) {
            return "The die has " + numSides + " sides and has not been rolled yet.";
        }
        return "The die rolled a " + currentRoll + ".";
    }
}
