package org.firstinspires.ftc.teamcode.commands;

/**
 * Stub implementation of Command used for unit testing lifecycle calls and states.
 */
public class TestCommand implements Command {
    public int startCount = 0;
    public int updateCount = 0;
    public int stopCount = 0;
    public boolean finished = false;
    public boolean failed = false;

    @Override
    public void start() {
        startCount++;
    }

    @Override
    public void update() {
        updateCount++;
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    @Override
    public boolean failed() {
        return failed;
    }

    @Override
    public void stop() {
        stopCount++;
    }
}
