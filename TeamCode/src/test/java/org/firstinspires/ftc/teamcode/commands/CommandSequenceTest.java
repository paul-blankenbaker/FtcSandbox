package org.firstinspires.ftc.teamcode.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandSequenceTest {

    @Test(expected = IllegalArgumentException.class)
    public void testNullCommandsThrows() {
        List<Command> nullList = null;
        new CommandSequence(nullList);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyCommandsThrows() {
        new CommandSequence(Collections.emptyList());
    }

    @Test
    public void testSequentialExecution() {
        TestCommand cmd1 = new TestCommand();
        TestCommand cmd2 = new TestCommand();
        TestCommand cmd3 = new TestCommand();

        CommandSequence sequence = new CommandSequence(Arrays.asList(cmd1, cmd2, cmd3));
        assertFalse(sequence.isFinished());

        sequence.start();
        assertEquals(1, cmd1.startCount);
        assertEquals(0, cmd2.startCount);

        // Update cmd1 while running
        sequence.update();
        assertEquals(1, cmd1.updateCount);

        // Complete cmd1
        cmd1.finished = true;
        sequence.update();
        assertEquals(1, cmd1.stopCount);
        assertEquals(1, cmd2.startCount);

        // Complete cmd2
        cmd2.finished = true;
        sequence.update();
        assertEquals(1, cmd2.stopCount);
        assertEquals(1, cmd3.startCount);

        // Complete cmd3
        cmd3.finished = true;
        sequence.update();
        assertEquals(1, cmd3.stopCount);

        assertTrue(sequence.isFinished());
        assertFalse(sequence.failed());
    }

    @Test
    public void testSequenceFailure() {
        TestCommand cmd1 = new TestCommand();
        TestCommand cmd2 = new TestCommand();

        CommandSequence sequence = new CommandSequence(Arrays.asList(cmd1, cmd2));
        sequence.start();

        // Finish cmd1 with failure
        cmd1.finished = true;
        cmd1.failed = true;
        sequence.update();

        assertEquals(1, cmd1.stopCount);
        assertTrue(sequence.isFinished());
        assertTrue(sequence.failed());
        // cmd2 should never have started
        assertEquals(0, cmd2.startCount);
    }

    @Test
    public void testStopForwarding() {
        TestCommand cmd1 = new TestCommand();
        TestCommand cmd2 = new TestCommand();

        CommandSequence sequence = new CommandSequence(Arrays.asList(cmd1, cmd2));
        sequence.start();
        sequence.update();

        sequence.stop();
        assertEquals(1, cmd1.stopCount);
        assertEquals(0, cmd2.stopCount);
    }

    @Test
    public void testSequenceTimeout() throws InterruptedException {
        TestCommand cmd1 = new TestCommand();
        CommandSequence sequence = new CommandSequence(Collections.singletonList(cmd1), 0.05);

        sequence.start();
        assertFalse(sequence.isFinished());

        Thread.sleep(70);

        assertTrue(sequence.isFinished());
        assertTrue(sequence.failed());
    }

    @Test
    public void testToString() {
        TestCommand cmd1 = new TestCommand();
        CommandSequence sequence = new CommandSequence(Collections.singletonList(cmd1));
        sequence.start();

        String str = sequence.toString();
        assertTrue(str.contains("CommandSequence(index: 0, states: 1"));
    }
}
