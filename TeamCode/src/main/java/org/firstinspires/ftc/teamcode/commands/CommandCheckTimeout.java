package org.firstinspires.ftc.teamcode.commands;

/**
 * A wrapper class when you want to create a command that fails if it doesn't complete in the given time.
 *
 * <p>If your command doesn't indicate that it's finished before the given time, this command
 * stops the command and fails it.</p>
 */
public class CommandCheckTimeout extends CommandTimed {
    public CommandCheckTimeout(double secondsUntilWeGiveUpAndFail) {
        super(secondsUntilWeGiveUpAndFail);
    }

    @Override
    public boolean failed() {
        return isTimeUp();
    }

    @Override
    public String toString() {
        return "CommandCheckTimeout(" + getTimeToRun() + ", " + getClass().getSimpleName() + ")";
    }
}
