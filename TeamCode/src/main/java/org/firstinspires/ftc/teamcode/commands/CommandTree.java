package org.firstinspires.ftc.teamcode.commands;

/**
 * Composite command representing a node in a decision tree or state machine.
 * When executed, it runs its assigned state command, and upon completion
 * transitions to either nextState (on success) or failedState (on failure).
 */
public class CommandTree implements Command {
    private final Command state;
    private CommandTree nextState;
    private CommandTree failedState;

    private CommandTree currentNode;
    private boolean currentNodeStarted;
    private boolean treeFailed;

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

    @Override
    public void start() {
        currentNode = this;
        currentNodeStarted = false;
        treeFailed = false;
    }

    @Override
    public void update() {
        while (currentNode != null && currentNode.state != null) {
            if (!currentNodeStarted) {
                currentNodeStarted = true;
                currentNode.state.start();
            }

            if (!currentNode.state.isFinished()) {
                currentNode.state.update();
            }

            if (currentNode.state.isFinished()) {
                currentNode.state.stop();
                boolean nodeFailed = currentNode.state.failed();
                if (nodeFailed) {
                    treeFailed = true;
                }
                currentNodeStarted = false;
                currentNode = nodeFailed ? currentNode.failedState : currentNode.nextState;
            } else {
                // Current node is still running
                return;
            }
        }
    }

    @Override
    public boolean isFinished() {
        return currentNode == null || currentNode.state == null;
    }

    @Override
    public boolean failed() {
        return treeFailed;
    }

    @Override
    public void stop() {
        if (currentNode != null && currentNode.state != null && currentNodeStarted) {
            currentNode.state.stop();
            currentNodeStarted = false;
        }
    }

    public void setNextState(CommandTree nextState) {
        this.nextState = nextState;
    }

    public void setFailState(CommandTree failState) {
        this.failedState = failState;
    }

    @Override
    public String toString() {
        if (currentNode != null && currentNode.state != null) {
            return "CommandTree(current: " + currentNode.state + ")";
        }
        return "CommandTree(finished)";
    }
}
