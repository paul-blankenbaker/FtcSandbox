package org.firstinspires.ftc.teamcode.stats;

import junit.framework.TestCase;

public class DieTest extends TestCase {

    public void testRoll() {
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