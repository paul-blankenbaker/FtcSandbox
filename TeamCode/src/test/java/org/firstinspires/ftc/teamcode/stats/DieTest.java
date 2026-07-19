package org.firstinspires.ftc.teamcode.stats;

import junit.framework.TestCase;

public class DieTest extends TestCase {

    public void testRoll() {
        Die die = new Die(1);
        int value = die.roll();
        assertEquals(1, value);

        die = new Die(10);
        value = die.roll();
        assertTrue(value >= 1);
        assertTrue(value <= 10);
    }

    public void testString() {
        Die die = new Die(1);
        int value = die.roll();
        assertEquals("The die rolled a 1.", die.toString());

        die = new Die(10);
        value = die.roll();
        String firstcheck = die.toString();
        String secondcheck = die.toString();
        assertEquals(firstcheck, secondcheck);
    }


    public void testSides12() {
        Die myDie = new Die(12);
        assertEquals(12, myDie.sides());

    }

    public void testSides3() {
        Die secondDie = new Die(3);
        assertEquals(3, secondDie.sides());
    }
}
