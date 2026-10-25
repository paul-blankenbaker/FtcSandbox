package org.firstinspires.ftc.teamcode.auto.mrb;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.drive.DriveSubsystem;
import org.firstinspires.ftc.teamcode.stats.StatisticsAccumulator;
import org.firstinspires.ftc.teamcode.utils.PowerVoltageCalculator;

/**
 * Demonstration of a simple timed autonomous auton that drives the robot away from a wall
 * but specifies output levels to motors by volts instead of power (percentage of available volts)
 */
@Autonomous(name = "Mr B: Drive by Volts", group = "Robot")
// @Disabled
public class DriveByVolts extends OpMode {
    private DriveSubsystem drive;
    private PowerVoltageCalculator powerVoltageCalculator;

    private double voltsLeft = 0.0;
    private double voltsRight = 0.0;

    private final ElapsedTime stateTimer = new ElapsedTime();
    private double loopTime;
    private long forceLoopTimingNanos;

    private enum DriveState {
        // At very start of auton, make sure timers, volts, etc. are reset and do any initial delay
        RESET_FOR_MATCH_START,
        // First thing is to drive away from the wall
        DRIVE_AWAY_FROM_WALL,
        // Next, spin the robot to face the loading zone (not an elegant turn)
        SPIN_TOWARDS_LOADING_ZONE,
        // Next, drive to the loading zone
        DRIVE_TO_LOADING_ZONE,
        // Finally make sure shut off the motors
        STOPPED
    }

    private DriveState state = DriveState.STOPPED;

    private StatisticsAccumulator loopTimeStats = new StatisticsAccumulator();
    private long lastLoopTime;

    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        // If we want to be able to plot telemetry values on the dashboard
        if (DriveByVoltsConfig.enableGraphing) {
            telemetry = FtcDashboard.getInstance().getTelemetry();
        }
        powerVoltageCalculator = new PowerVoltageCalculator(hardwareMap.voltageSensor);
        drive = DriveSubsystem.autoDetectDriveSubsystem(hardwareMap);

        updateTelemetry();
    }

    @Override
    public void start() {
        forceLoopTimingNanos = DriveByVoltsConfig.forceLoopTimingMillis * 1000000;
        // Create a new PowerVoltageCalculator instance (to reset the tracked statistics each time we start)
        powerVoltageCalculator = new PowerVoltageCalculator(hardwareMap.voltageSensor);
        // When match starts, make sure we're back to the initial state
        changeState(DriveState.RESET_FOR_MATCH_START);

        loopTimeStats = new StatisticsAccumulator();
        lastLoopTime = Long.MAX_VALUE;
    }

    /**
     * Called repeatedly until teleop is over.
     */
    @Override
    public void loop() {
        // Track how long the loop takes to run
        updateLoopTimeStats();

        // Make sure to read the current battery voltage so we can calculate power outputs for
        // desired voltages
        powerVoltageCalculator.update();

        // Check to see where we are in auton and figure out if we need to move
        updateDriveState();

        // Update diagnostic values
        updateTelemetry();
    }

    private void updateDriveState() {
        // Based on state and elapsed time, decide what voltage level we want on the drive motors
        switch (state) {
            case RESET_FOR_MATCH_START:
                // Do any initial delay and update the
                resetForMatchStart();
                break;

            case DRIVE_AWAY_FROM_WALL:
                if (DriveByVoltsConfig.shootThenPark) {
                    // Drive away from wall enough to spin towards loading zone
                    driveStraight(DriveByVoltsConfig.driveAwayFromWallSeconds, DriveState.SPIN_TOWARDS_LOADING_ZONE);
                } else {
                    // Drive away from wall enough to drive to loading zone
                    driveStraight(DriveByVoltsConfig.driveSimpleSeconds, DriveState.STOPPED);
                }
                break;

            case SPIN_TOWARDS_LOADING_ZONE:
                // Rotate to face loading zone
                driveSpin(DriveByVoltsConfig.turnTowardsLoadingZoneSeconds, DriveState.DRIVE_TO_LOADING_ZONE);
                break;

            case DRIVE_TO_LOADING_ZONE:
                // Long run to loading zone
                driveStraight(DriveByVoltsConfig.driveToLoadingZoneSeconds, DriveState.STOPPED);
                break;

            case STOPPED:
            default:
                // Make sure to shut off the motors when we're done (or unknown drive state)
                voltsLeft = voltsRight = 0.0;
                break;
        }

        // Apply output voltage to motors as determined by states above
        updateDriveVolts(voltsLeft, voltsRight);
    }

    private void driveStraight(double driveTimeSeconds, DriveState nextState) {
        updateDriveVolts(DriveByVoltsConfig.driveStraightVolts, DriveByVoltsConfig.driveStraightVolts);
        if (stateTimer.seconds() >= driveTimeSeconds) {
            changeState(nextState);
        }
    }

    private void driveSpin(double spinSeconds, DriveState nextState) {
        updateDriveVolts(DriveByVoltsConfig.driveSpinVolts, -DriveByVoltsConfig.driveSpinVolts);
        if (stateTimer.seconds() >= spinSeconds) {
            changeState(nextState);
        }
    }

    private void resetForMatchStart() {
        voltsLeft = voltsRight = 0.0;  // Make sure no power is applied
        if (stateTimer.seconds() >= DriveByVoltsConfig.initialDelaySeconds) {
            changeState(DriveState.DRIVE_AWAY_FROM_WALL);
        }
    }

    private void changeState(DriveState state) {
        this.state = state;
        stateTimer.reset();
    }

    private void updateDriveVolts(double left, double right) {
        voltsLeft = left;
        voltsRight = right;

        powerVoltageCalculator.update();
        double powerLeft = powerVoltageCalculator.voltageToPower(voltsLeft, true);
        double powerRight = powerVoltageCalculator.voltageToPower(voltsRight, true);

        drive.driveTank(powerLeft, powerRight);
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
        telemetry.addData("Drive State", state);
        telemetry.addData("Volts Left", "%.3f", voltsLeft);
        telemetry.addData("Volts Right", "%.3f", voltsRight);
        drive.telemetryAddData(telemetry);

        // If power voltage calculator is available, display its diagnostics
        if (powerVoltageCalculator != null) {
            powerVoltageCalculator.addData(telemetry);
        }
        telemetry.update();

        if (DriveByVoltsConfig.showTiming) {
            boolean hasStats = loopTimeStats.count() > 0;
            telemetry.addData("Loop Delay Min", "%.3f", hasStats ? loopTimeStats.min() : 0.0);
            telemetry.addData("Loop Delay Now", "%.3f", hasStats ? loopTime : 0.0);
            telemetry.addData("Loop Delay Avg", "%.3f", hasStats ? loopTimeStats.average() : 0.0);
            telemetry.addData("Loop Delay Max", "%.3f", hasStats ? loopTimeStats.max() : 0.0);
        }
    }

}
