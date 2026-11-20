package org.firstinspires.ftc.teamcode.commands;

public class CommandTree {
    private final Command state;

    private CommandTree nextState;
    private CommandTree failedState;
    private boolean started;

    public CommandTree(Command state, CommandTree nextState, CommandTree failedState) {
        this.state = state;
        this.nextState = nextState;
        this.failedState = failedState;
    }

    public CommandTree(Command state, CommandTree nextState) {
        this(state, nextState, null);
    }

    public CommandTree(Command state) {
        this(state, null, null);
    }

    public CommandTree update() {
        if (state == null) {
            return null;
        }
        if (!started) {
            started = true;
            state.start();
        }
        if (!state.isFinished()) {
            state.update();
        }
        if (state.isFinished()) {
            started = false;
            state.stop();
            if (state.failed()) {
                return failedState;
            }
            return nextState;
        }
        // Remain in same state
        return this;
    }

    public void setNextState(CommandTree nextState) {
        this.nextState = nextState;
    }

    public void setFailState(CommandTree failState) {
        this.failedState = failState;
    }
}
