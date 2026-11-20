package org.firstinspires.ftc.teamcode.commands;

import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Used to run a command for a given amount of time (finishes successfully after timeout).
 */
public class CommandTimed implements Command {
    private final double seconds;
    private final ElapsedTime timer = new ElapsedTime();

    public CommandTimed(double seconds) {
        this.seconds = seconds;
    }

    public double getTimeToRun() {
        return seconds;
    }

    public void start() {
        timer.reset();
    }

    public double getElapsedTime() {
        return timer.seconds();
    }

    public boolean isTimeUp() {
        return timer.seconds() >= seconds;
    }

    public boolean isFinished() {
        return isTimeUp();
    }

    @Override
    public boolean failed() {
        return false;
    }

    @Override
    public void update() {

    }

    @Override
    public void stop() {

    }

    @Override
    public String toString() {
        return "CommandTimed(" + seconds + ", " + getClass().getSimpleName() + ")";
    }
}
