package org.firstinspires.ftc.teamcode.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CommandTimedTest {

    @Test
    public void testGetTimeToRun() {
        CommandTimed command = new CommandTimed(2.5);
        assertEquals(2.5, command.getTimeToRun(), 0.0001);
        assertFalse(command.failed());
    }

    @Test
    public void testTimerExpiration() throws InterruptedException {
        CommandTimed command = new CommandTimed(0.05);
        command.start();
        assertFalse(command.isTimeUp());
        assertFalse(command.isFinished());

        Thread.sleep(70);

        assertTrue(command.isTimeUp());
        assertTrue(command.isFinished());
        assertFalse(command.failed());
        assertTrue(command.getElapsedTime() >= 0.05);
    }

    @Test
    public void testUpdateAndStopDoNotThrow() {
        CommandTimed command = new CommandTimed(1.0);
        command.start();
        command.update();
        command.stop();
        assertFalse(command.failed());
    }

    @Test
    public void testToString() {
        CommandTimed command = new CommandTimed(1.5);
        assertTrue(command.toString().contains("CommandTimed(1.5"));
    }
}
