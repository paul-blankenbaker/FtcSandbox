package org.firstinspires.ftc.teamcode.robots;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.drive.DriveSubsystem;
import org.firstinspires.ftc.teamcode.utils.PowerVoltageCalculator;

/**
 * The 2026-2027 starter bot with intake on opposite side of shooter.
 */
public class Buzzy {

    private final DcMotorEx shooterMotor;
    private final DcMotor intakeMotor;

    private final CRServo feederServo;
    private final DriveSubsystem driveSubsystem;
    private final PowerVoltageCalculator voltageCalculator;

    private final CRServo sideIntakeRight;
    private final CRServo sideIntakeLeft;
    private double currentRpm;
    private double previousRpm;
    private boolean shooterPidEnabled;

    private final ElapsedTime feederRunTime = new ElapsedTime();
    private int feederRunTimeMillis;
    private int shooterInRangeCount;
    private int shooterLostLockCount;
    private long shooterLostLockTime;
    private long shooterGotLockTime;
    private double currentShooterError;

    private long updateTime;
    private long previousUpdateTime;

    private long updateDeltaTime;


    public Buzzy(HardwareMap hardwareMap) {
        shooterMotor = hardwareMap.get(DcMotorEx.class, "shooter");
        shooterMotor.setDirection(DcMotor.Direction.FORWARD);
        feederServo = hardwareMap.get(CRServo.class, "shooterFeed");
        sideIntakeRight = hardwareMap.get(CRServo.class, "rightSideIntake");
        sideIntakeLeft = hardwareMap.get(CRServo.class, "leftSideIntake");
        intakeMotor = hardwareMap.get(DcMotor.class, "intake");
        driveSubsystem = DriveSubsystem.autoDetectDriveSubsystem(hardwareMap);
        voltageCalculator = new PowerVoltageCalculator(hardwareMap.voltageSensor);
    }

    /**
     * Reads internal values and updates any automated controls (like shooter power).
     */
    public void update() {
        updateTime = System.nanoTime();
        updateDeltaTime = updateTime - previousUpdateTime;
        voltageCalculator.update();
        previousRpm = currentRpm;
        currentRpm = shooterMotor.getVelocity() / BuzzyConfig.SHOOTER_COUNTS_PER_REV * 60.0;
        updateShooterPid();
        previousUpdateTime = updateTime;
        if (feederRunTimeMillis > 0 && feederRunTime.milliseconds() >= feederRunTimeMillis) {
            stopFeeder();
        }
    }

    public void drive(Gamepad gamepad, Telemetry telemetry) {
        double throttleVolts = gamePadStickToVolts(-gamepad.left_stick_y, BuzzyConfig.DRIVE_THROTTLE_MIN_VOLTS, BuzzyConfig.DRIVE_THROTTLE_SCALE);
        double rotationVolts = gamePadStickToVolts(gamepad.right_stick_x, BuzzyConfig.DRIVE_ROTATION_MIN_VOLTS, BuzzyConfig.DRIVE_ROTATION_SCALE);

        double throttlePower = voltageCalculator.voltageToPower(throttleVolts);
        double rotationPower = voltageCalculator.voltageToPower(rotationVolts);
        if (BuzzyConfig.ENABLE_DASHBOARD_TELEMETRY) {
            telemetry.addData("Throttle Volts", "%.3f", throttleVolts);
            telemetry.addData("Rotation Volts", "%.3f", rotationVolts);
        }
        driveSubsystem.driveArcade(throttlePower, rotationPower);
    }

    private double gamePadStickToVolts(double axisValue, double minVolts, double scale) {
        double axisValueAbs = Math.abs(axisValue);
        if (axisValueAbs < BuzzyConfig.DRIVE_STICK_DEAD_ZONE) {
            return 0.0;
        }
        // We will square the input value (parabolic effect may be more control at lower speeds)
        axisValue = axisValue * (axisValueAbs - BuzzyConfig.DRIVE_STICK_DEAD_ZONE) / (1.0 - BuzzyConfig.DRIVE_STICK_DEAD_ZONE);
        double maxVolts = BuzzyConfig.DRIVE_MAX_VOLTS * scale;
        // Convert axisValue (range of -1.0 to 1.0) to volts (range of minVolts to maxVolts)
        double outputVolts = axisValue * (maxVolts - minVolts);
        // We need to shift the remapped value the minimum volts away from 0.0
        outputVolts += (outputVolts > 0) ? minVolts : -minVolts;
        return outputVolts;
    }

    public void setShooterPower(double power) {
        shooterMotor.setPower(power);
    }

    public void setShooterVolts(double volts) {
        double power = voltageCalculator.voltageToPower(volts);
        setShooterPower(power);
    }

    public void setIntakeVolts(double volts) {
        intakeMotor.setPower(voltageCalculator.voltageToPower(volts));
        double sidePower = (volts != 0.0) ? 1.0 : 0.0;
        sideIntakeRight.setPower(sidePower);
        sideIntakeLeft.setPower(sidePower);
    }

    public boolean isIntakeRunning() {
        return intakeMotor.getPower() != 0.0;
    }

    public void intakeStop() {
        intakeMotor.setPower(0);
        sideIntakeLeft.setPower(0);
        sideIntakeRight.setPower(0);
    }

    public void intakePullIn() {
        setIntakeVolts(BuzzyConfig.INTAKE_VOLTS);
    }

    public void intakePushOut() {
        setIntakeVolts(-BuzzyConfig.INTAKE_VOLTS);
    }

    public void setFeederVolts(double volts, int durationMillis) {
        feederRunTime.reset();
        feederRunTimeMillis = durationMillis;
        feederServo.setPower(voltageCalculator.voltageToPower(volts));
    }

    public void stopFeeder() {
        feederRunTimeMillis = 0;
        feederServo.setPower(0);
    }

    public int getShooterCounts() {
        return shooterMotor.getCurrentPosition();
    }

    public void addData(Telemetry telemetry) {
        telemetry.addData("Shooter RPM", "%.1f", currentRpm);
        boolean locked = (shooterInRangeCount > 3);
        telemetry.addData("Shooter Locked", locked);

        if (BuzzyConfig.ENABLE_DASHBOARD_TELEMETRY) {
            telemetry.addData("Shooter RPM Error", "%.0f", currentShooterError, "%.3f");
            telemetry.addData("Shooter Counts", getShooterCounts());
            double shooterPower = shooterMotor.getPower();
            telemetry.addData("Shooter Voltage", "%.3f", voltageCalculator.powerToVoltage(shooterPower));
            telemetry.addData("Shooter Power", "%.3f", shooterPower);
            double sps = (updateDeltaTime > 0) ? (1.0e9 / updateDeltaTime) : 0.0;
            telemetry.addData("Samples/Sec", "%.1f", sps);
            telemetry.addData("Shooter In Range Count", shooterInRangeCount);
            double lockTime = locked ? ((shooterGotLockTime - shooterLostLockTime) * 1e-9) : 0.0;
            telemetry.addData("Shooter Time to Lock", "%.3f", lockTime);
            voltageCalculator.addData(telemetry);
        }
    }

    public boolean isShooterRunning() {
        return Math.abs(shooterMotor.getPower()) > 0.05;
    }

    public void stopShooter() {
        shooterPidEnabled = false;
        setShooterPower(0.0);
    }

    public void startShooterPid() {
        shooterPidEnabled = true;
        shooterLostLockTime = shooterGotLockTime = System.nanoTime();
        shooterInRangeCount = shooterLostLockCount = 0;
        currentShooterError = 0;
        updateShooterPid();
    }

    public void updateShooterPid() {
        if (!shooterPidEnabled) {
            // PID control not enabled, don't touch shooter voltage
            return;
        }

        // Compute RPM error (how far off target we are)
        currentShooterError = BuzzyConfig.SHOOTER_TARGET_RPM - currentRpm;

        boolean shooterSpeedOk = Math.abs(currentShooterError) < BuzzyConfig.SHOOTER_RPM_TOLERANCE;
        if (shooterSpeedOk) {
            // To help handle momentary "blips",
            if (shooterLostLockCount >= 3) {
                shooterInRangeCount = 0;
            }
            if (shooterInRangeCount == 0) {
                shooterGotLockTime = updateTime;
            }
            shooterLostLockCount = 0;
            shooterInRangeCount++;

            // Need to think about this. This effectively says that once you've locked onto the
            // target speed, then turn off PID and only put out F (pre-determined volts at target)
//            if (shooterInRangeCount >= 5) {
//                // Once ramped up to speed, just hold steady voltage output
//                setShooterVolts(BuzzyConfig.SHOOTER_KF_VOLTS);
//                return;
//            }
        } else {
            // Shooter no longer locked onto good speed, let PID quickly adjust
            if (shooterLostLockCount == 0) {
                shooterLostLockTime = updateTime;
            }
            shooterLostLockCount++;
            // To help handle momentary "blips", don't touch the in range counts until we've
            // had a few lost locks in a row
            if (shooterLostLockCount >= 3) {
                shooterInRangeCount = 0;
            }
        }

        // Also compute previous error to compute derivative
        double previousShooterError = BuzzyConfig.SHOOTER_TARGET_RPM - previousRpm;

        double f = BuzzyConfig.SHOOTER_KF_VOLTS; // Determine experimentally
        double p = currentShooterError * BuzzyConfig.SHOOTER_KP;

        // Compute derivative in terms of change of error per second (maybe we should do it in
        // terms of milliseconds - you need very very small Kd values)
        double dt = (updateDeltaTime * 1e-9);
        double d = BuzzyConfig.SHOOTER_KD * (currentShooterError - previousShooterError) / dt;

        // We don't really want to wait on I in PIDF to accumulate, so we don't use it and leave it at 0
        double i = 0;

        // Join the PIDF terms together to form the final output voltage and range check
        // (When tuning, start with Kp, Kd, and Ki set to 0, get Kf so until the output RPM
        // matches what you want, then introduce some Kp to get there more quickly. You may
        // not need Kd unless you have oscillation issues in order to keep it quick. You
        // shouldn't need I at all as it takes time to accumulate and may be too slow to help
        double volts = p + i + d + f;
        if (volts < 0.0) {
            // Don't allow negative rotation
            volts = 0.0;
        }
        setShooterVolts(volts);

    }
}
