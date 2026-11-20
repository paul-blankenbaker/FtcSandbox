package org.firstinspires.ftc.teamcode.commands;

import java.util.ArrayList;
import java.util.List;

/**
 * Used to run a sequence of commands in order.
 */
public class CommandSequence extends CommandCheckTimeout {
    private final List<Command> states;
    private int stateIndex;
    private boolean sequenceFailed;

    public CommandSequence(List<Command> states, double timeoutSeconds) {
        super(timeoutSeconds);
        if (states == null || states.isEmpty()) {
            throw new IllegalArgumentException("States cannot be null or empty");
        }
        this.states = new ArrayList<>(states);
    }

    public CommandSequence(List<Command> states) {
        this(states, 30.0);
    }

    @Override
    public void start() {
        super.start();
        sequenceFailed = false;
        stateIndex = 0;
        states.get(0).start();
        update();
    }

    @Override
    public boolean isFinished() {
        return sequenceFailed || (stateIndex >= states.size()) || super.isFinished();
    }

    public boolean failed() {
        return sequenceFailed || super.failed();
    }

    @Override
    public void update() {
        boolean needStart = false;

        while (!sequenceFailed && (stateIndex < states.size())) {
            Command state = states.get(stateIndex);
            if (needStart) {
                state.start();
                needStart = false; // Not sure why IDE shows this as unneeded?
            }
            if (!state.isFinished()) {
                state.update();
            }
            if (state.isFinished()) {
                state.stop();
                sequenceFailed = state.failed();
                needStart = true; // Next time through loop, need to call start
                stateIndex++;
            } else {
                return;
            }
        }
    }

    @Override
    public void stop() {
        super.stop();
        if (stateIndex < states.size()) {
            states.get(stateIndex).stop();
        }
        stateIndex = states.size();
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
