package org.firstinspires.ftc.teamcode.time;

import junit.framework.TestCase;

public class StopWatchTest extends TestCase {

    public void testStart() throws InterruptedException {
        StopWatch instance = new StopWatch();
        instance.start();
        Thread.sleep(2);
        assertTrue("Measured at least 2 milliseconds", instance.check() >= 0.002);
        assertTrue(instance.check() + " seconds is less than 3 milliseconds", instance.check() <= 0.010);
    }

    public void testStop() throws InterruptedException {
        StopWatch instance = new StopWatch();
        instance.start();
        double firstStopTime = instance.stop();
        assertTrue(firstStopTime >= 0);
        assertTrue(firstStopTime < 1);

        // Sleeping when stopped should be ignored
        Thread.sleep(2);
        double secondStopTime = instance.stop();
        assertEquals(secondStopTime, firstStopTime, 0.0);

        // When we start again and wait at least 2 milliseconds
        instance.start();
        Thread.sleep(2);

        // When we stop, the new accumulated time should be 2 to 3 milliseconds
        // more than the first time
        double thirdStopTime = instance.stop();
        assertTrue(thirdStopTime + " seconds should be greater than or equal to" + (firstStopTime + 2), thirdStopTime >= (firstStopTime + .002));
        assertTrue(thirdStopTime < (firstStopTime + 3));
        Thread.sleep(2);
        double fourthStopTime = instance.stop();
        assertEquals(fourthStopTime, thirdStopTime);
    }

    public void testReset() throws InterruptedException {
        StopWatch instance = new StopWatch();
        instance.start();
        Thread.sleep(2);
        assertTrue("Measured at least 2 milliseconds", instance.check() >= 0.002);
        instance.reset();
        assertEquals(0.0, instance.check(), Double.MIN_VALUE);
        instance.reset();
        assertEquals(0.0, instance.check(), Double.MIN_VALUE);
    }
}