package org.firstinspires.ftc.teamcode.commands;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CommandCheckTimeoutTest {

    @Test
    public void testFailedWhenTimeIsUp() throws InterruptedException {
        CommandCheckTimeout command = new CommandCheckTimeout(0.05);
        command.start();
        assertFalse(command.failed());
        assertFalse(command.isFinished());

        Thread.sleep(70);

        assertTrue(command.isFinished());
        assertTrue(command.failed());
    }

    @Test
    public void testToString() {
        CommandCheckTimeout command = new CommandCheckTimeout(2.0);
        assertTrue(command.toString().contains("CommandCheckTimeout(2.0"));
    }
}
