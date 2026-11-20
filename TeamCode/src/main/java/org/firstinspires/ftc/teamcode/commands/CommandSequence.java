package org.firstinspires.ftc.teamcode.commands;

import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Used to run a sequence of commands in order.
 */
public class CommandSequence implements Command {
    private final List<Command> states;
    private final double timeoutSeconds;
    private final ElapsedTime timer;

    private int stateIndex;
    private boolean sequenceFailed;
    private boolean timedOut;

    public CommandSequence(List<Command> states, double timeoutSeconds) {
        if (states == null || states.isEmpty()) {
            throw new IllegalArgumentException("States cannot be null or empty");
        }
        this.states = new ArrayList<>(states);
        this.timeoutSeconds = timeoutSeconds;
        this.timer = timeoutSeconds > 0 ? new ElapsedTime() : null;
    }

    public CommandSequence(List<Command> states) {
        this(states, 0.0);
    }

    @Override
    public void start() {
        sequenceFailed = false;
        timedOut = false;
        stateIndex = 0;
        if (timer != null) {
            timer.reset();
        }
        if (!states.isEmpty()) {
            states.get(0).start();
        }
    }

    @Override
    public boolean isFinished() {
        checkTimeout();
        return sequenceFailed || timedOut || (stateIndex >= states.size());
    }

    @Override
    public boolean failed() {
        return sequenceFailed || timedOut;
    }

    private void checkTimeout() {
        if (timer != null && !timedOut && timer.seconds() >= timeoutSeconds) {
            timedOut = true;
        }
    }

    @Override
    public void update() {
        checkTimeout();

        while (!isFinished() && stateIndex < states.size()) {
            Command state = states.get(stateIndex);
            if (!state.isFinished()) {
                state.update();
            }

            if (state.isFinished()) {
                state.stop();
                if (state.failed()) {
                    sequenceFailed = true;
                    return;
                }
                stateIndex++;
                checkTimeout();
                if (!isFinished() && stateIndex < states.size()) {
                    states.get(stateIndex).start();
                }
            } else {
                return;
            }
        }
    }

    @Override
    public void stop() {
        if (stateIndex < states.size()) {
            states.get(stateIndex).stop();
        }
    }

    @Override
    public String toString() {
        String text = "CommandSequence(index: " + stateIndex + ", states: " + states.size();
        if (stateIndex < states.size()) {
            text += ", " + states.get(stateIndex);
        }
        return text + ")";
    }
}
