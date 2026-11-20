package org.firstinspires.ftc.teamcode.commands;

/**
 * Definition of a generic command that can be run.
 */
public interface Command {

    /**
     * Invoked once when command activates.
     */
    void start();

    /**
     * Invoked repeatedly while command is active to check if done.
     *
     * @return true if done.
     */
    boolean isFinished();

    /**
     * Indicates if command failed, ignored until command indicates it is finished.
     *
     * @return true if command failed.
     */
    boolean failed();

    /**
     * Invoked repeatedly while command is active.
     */
    void update();

    /**
     * Invoked once after command finishes (even if failure).
     */
    void stop();
}
