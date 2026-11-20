package org.firstinspires.ftc.teamcode.auto.mrb;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.commands.Command;
import org.firstinspires.ftc.teamcode.commands.CommandCheckTimeout;
import org.firstinspires.ftc.teamcode.commands.CommandSequence;
import org.firstinspires.ftc.teamcode.commands.CommandTimed;
import org.firstinspires.ftc.teamcode.commands.CommandTree;
import org.firstinspires.ftc.teamcode.robots.Buzzy;
import org.firstinspires.ftc.teamcode.robots.BuzzyConfig;
import org.firstinspires.ftc.teamcode.stats.StatisticsAccumulator;

import java.util.ArrayList;
import java.util.List;

/**
 * Demonstration of a simple timed autonomous auton that drives the robot away from a wall
 * but specifies output levels to motors by volts instead of power (percentage of available volts)
 */
public abstract class BuzzyAutonBase extends OpMode {
    protected Buzzy robot;
    private StatisticsAccumulator loopTimeStats;
    private long lastLoopTime;
    private double loopTime;
    private long forceLoopTimingNanos;
    private CommandTree commandTree;

    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        // If we want to be able to plot telemetry values on the dashboard
        if (BuzzyAutonConfig.enableGraphing) {
            telemetry = FtcDashboard.getInstance().getTelemetry();
        }
        loopTimeStats = new StatisticsAccumulator();
        robot = new Buzzy(hardwareMap);
        updateTelemetry();
    }

    @Override
    public void start() {
        forceLoopTimingNanos = BuzzyAutonConfig.forceLoopTimingMillis * 1000000L;

        commandTree = createAutonStateTree();
        if (commandTree != null) {
            commandTree.start();
        }

        loopTimeStats = new StatisticsAccumulator();
        lastLoopTime = Long.MAX_VALUE;
    }

    protected abstract CommandTree createAutonStateTree();

    @Override
    public void stop() {
        if (commandTree != null) {
            commandTree.stop();
        }
        robot.stop();
    }

    /**
     * Called repeatedly until teleop is over.
     */
    @Override
    public void loop() {
        // Read sensors, current battery voltage, update state, ...
        robot.update();

        // Track how long the loop takes to run
        updateLoopTimeStats();

        // See if there are more auton states to work on
        if (commandTree != null && !commandTree.isFinished()) {
            commandTree.update();
        } else {
            robot.stop();
        }

        // Update diagnostic values
        updateTelemetry();
    }

    protected void setDriveVolts(double left, double right) {
        robot.setDriveVolts(left, right);
    }

    protected CommandTree driveVoltsTime(double voltsLeft, double voltsRight, double driveTimeSeconds) {
        return new CommandTree(new CommandTimed(driveTimeSeconds) {
            @Override
            public void update() {
                setDriveVolts(voltsLeft, voltsRight);
            }

            @Override
            public void stop() {
                setDriveVolts(0, 0);
            }
        });
    }

    protected CommandTree driveStraightTimed(double driveTimeSeconds) {
        return driveVoltsTime(BuzzyAutonConfig.driveStraightVolts, BuzzyAutonConfig.driveStraightVolts, driveTimeSeconds);
    }

    protected CommandTree driveRotateTimed(double driveTimeSeconds) {
        return driveVoltsTime(BuzzyAutonConfig.driveSpinVolts, -BuzzyAutonConfig.driveSpinVolts, driveTimeSeconds);
    }

    protected Command waitForShooterSpinUp() {
        return new CommandCheckTimeout(3.0) {
            @Override
            public void start() {
                super.start();
                robot.startShooterPid();
            }

            @Override
            public boolean isFinished() {
                return robot.isShooterReady() || super.isFinished();
            }
        };
    }

    protected Command shootBallSequence() {
        List<Command> states = new ArrayList<>();
        for (int i = 0; i < BuzzyAutonConfig.shotAttempts; i++) {
            states.add(waitForShooterSpinUp());
            states.add(feedBallIntoShooter());
        }
        states.add(new CommandTimed(0.0) {
            @Override
            public void update() {
                stop();
            }
        });
        return new CommandSequence(states, 15.0);
    }

    public Command feedBallIntoShooter() {
        return new CommandTimed(0.5) {
            @Override
            public void start() {
                super.start();
                robot.setFeederVolts(BuzzyConfig.FEEDER_VOLTS, BuzzyConfig.FEEDER_DURATION_MILLIS);
            }
        };
    }

    private void updateLoopTimeStats() {
        // Just for kicks lets, track how often the loop runs
        long currentNanos = System.nanoTime();
        long deltaNanos = (currentNanos - lastLoopTime);
        if (forceLoopTimingNanos > 0) {
            if (deltaNanos < forceLoopTimingNanos) {
                try {
                    long sleepNanos = forceLoopTimingNanos - deltaNanos;
                    Thread.sleep(sleepNanos / 1000000, (int) (sleepNanos % 1000000));
                } catch (InterruptedException e) {
                    // Will try again next time
                }
                currentNanos = System.nanoTime();
            }
        }

        if (currentNanos > lastLoopTime) {
            // Track amount of time between each loop run in milliseconds
            loopTime = (currentNanos - lastLoopTime) / 1000000.0;
            loopTimeStats.addValue(loopTime);
        }
        lastLoopTime = currentNanos;
    }

    //
    // Diagnostic output to driver station useful when debugging
    //
    private void updateTelemetry() {
        // Optionally, display some diagnostics (handy when troubleshooting drive issues)
        telemetry.addData("Auton State", String.valueOf(commandTree));
        robot.addData(telemetry);

        if (BuzzyAutonConfig.showTiming) {
            boolean hasStats = loopTimeStats.count() > 0;
            telemetry.addData("Loop Delay Min", "%.3f", hasStats ? loopTimeStats.min() : 0.0);
            telemetry.addData("Loop Delay Now", "%.3f", hasStats ? loopTime : 0.0);
            telemetry.addData("Loop Delay Avg", "%.3f", hasStats ? loopTimeStats.average() : 0.0);
            telemetry.addData("Loop Delay Max", "%.3f", hasStats ? loopTimeStats.max() : 0.0);
        }
    }

}
