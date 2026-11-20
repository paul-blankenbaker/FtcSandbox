package org.firstinspires.ftc.teamcode.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CommandTreeTest {

    @Test
    public void testSuccessfulBranchTransition() {
        TestCommand cmd1 = new TestCommand();
        TestCommand cmd2 = new TestCommand();

        CommandTree node2 = new CommandTree(cmd2);
        CommandTree node1 = new CommandTree(cmd1, node2);

        node1.start();
        assertFalse(node1.isFinished());

        // First update starts and updates cmd1
        node1.update();
        assertEquals(1, cmd1.startCount);
        assertEquals(1, cmd1.updateCount);

        // Complete cmd1 successfully
        cmd1.finished = true;
        cmd1.failed = false;
        node1.update();

        assertEquals(1, cmd1.stopCount);
        // cmd2 should now be started and updated
        assertEquals(1, cmd2.startCount);
        assertEquals(1, cmd2.updateCount);
        assertFalse(node1.isFinished());

        // Complete cmd2
        cmd2.finished = true;
        node1.update();

        assertEquals(1, cmd2.stopCount);
        assertTrue(node1.isFinished());
        assertFalse(node1.failed());
    }

    @Test
    public void testFailedBranchTransition() {
        TestCommand rootCmd = new TestCommand();
        TestCommand successCmd = new TestCommand();
        TestCommand failCmd = new TestCommand();

        CommandTree successNode = new CommandTree(successCmd);
        CommandTree failNode = new CommandTree(failCmd);
        CommandTree tree = new CommandTree(rootCmd, successNode, failNode);

        tree.start();

        // Complete rootCmd with failure
        rootCmd.finished = true;
        rootCmd.failed = true;
        tree.update();

        assertEquals(1, rootCmd.stopCount);
        assertEquals(0, successCmd.startCount);
        // failCmd should be started and updated
        assertEquals(1, failCmd.startCount);
        assertEquals(1, failCmd.updateCount);

        // Complete failCmd
        failCmd.finished = true;
        tree.update();

        assertEquals(1, failCmd.stopCount);
        assertTrue(tree.isFinished());
        assertTrue(tree.failed());
    }

    @Test
    public void testSetters() {
        TestCommand cmd1 = new TestCommand();
        TestCommand cmd2 = new TestCommand();
        TestCommand cmd3 = new TestCommand();

        CommandTree tree = new CommandTree(cmd1);
        CommandTree nextNode = new CommandTree(cmd2);
        CommandTree failNode = new CommandTree(cmd3);

        tree.setNextState(nextNode);
        tree.setFailState(failNode);

        tree.start();
        cmd1.finished = true;
        cmd1.failed = false;
        tree.update();

        assertEquals(1, cmd2.startCount);
    }

    @Test
    public void testStopForwardsToCurrentNode() {
        TestCommand cmd1 = new TestCommand();
        CommandTree tree = new CommandTree(cmd1);

        tree.start();
        tree.update(); // starts cmd1

        tree.stop();
        assertEquals(1, cmd1.stopCount);
    }

    @Test
    public void testToString() {
        TestCommand cmd1 = new TestCommand();
        CommandTree tree = new CommandTree(cmd1);

        tree.start();
        tree.update();
        assertTrue(tree.toString().contains("CommandTree(current:"));

        cmd1.finished = true;
        tree.update();
        assertEquals("CommandTree(finished)", tree.toString());
    }
}
