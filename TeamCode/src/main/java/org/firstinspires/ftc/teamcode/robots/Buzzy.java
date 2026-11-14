package org.firstinspires.ftc.teamcode.robots;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.drive.DriveSubsystem;
import org.firstinspires.ftc.teamcode.utils.PowerVoltageCalculator;
import org.firstinspires.ftc.teamcode.utils.RpmCalculator;

/**
 * The 2026-2027 starter bot with intake on opposite side of shooter.
 */
public class Buzzy {

    private final DcMotor shooterMotor;
    private final DcMotor intakeMotor;

    private final CRServo feederServo;
    private final DriveSubsystem driveSubsystem;
    private final PowerVoltageCalculator voltageCalculator;

    private final RpmCalculator shooterRpmCalculator;
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
    private double shooterError;


    public Buzzy(HardwareMap hardwareMap) {
        shooterMotor = hardwareMap.get(DcMotor.class, "shooter");
        shooterMotor.setDirection(DcMotor.Direction.FORWARD);
        feederServo = hardwareMap.get(CRServo.class, "shooterFeed");
        sideIntakeRight = hardwareMap.get(CRServo.class, "rightSideIntake");
        sideIntakeLeft = hardwareMap.get(CRServo.class, "leftSideIntake");
        intakeMotor = hardwareMap.get(DcMotor.class, "intake");
        driveSubsystem = DriveSubsystem.autoDetectDriveSubsystem(hardwareMap);
        voltageCalculator = new PowerVoltageCalculator(hardwareMap.voltageSensor);
        shooterRpmCalculator = new RpmCalculator(BuzzyConfig.SHOOTER_COUNTS_PER_REV, BuzzyConfig.SHOOTER_RPM_CALCULATION_SAMPLE_MILLIS);
    }

    public void update() {
        voltageCalculator.update();
        shooterRpmCalculator.addSample(getShooterCounts());
        previousRpm = currentRpm;
        currentRpm = shooterRpmCalculator.computeRpm(0.050);
        updateShooterPid();
        if (feederRunTimeMillis > 0 && feederRunTime.milliseconds() >= feederRunTimeMillis) {
            stopFeeder();
        }
    }

    public void drive(Gamepad gamepad) {
        driveSubsystem.driveArcade(gamepad);
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
        telemetry.addData("Shooter RPM", "%.1f", shooterRpmCalculator.computeRpm(0.050));
        boolean locked = (shooterInRangeCount > 3);
        telemetry.addData("Shooter Locked", locked);

        if (BuzzyConfig.ENABLE_DASHBOARD_TELEMETRY) {
            telemetry.addData("Shooter RPM Error", "%.0f", shooterError, "%.3f");
            telemetry.addData("Shooter Counts", getShooterCounts());
            double shooterPower = shooterMotor.getPower();
            telemetry.addData("Shooter Voltage", "%.3f", voltageCalculator.powerToVoltage(shooterPower));
            telemetry.addData("Shooter Power", "%.3f", shooterPower);
            telemetry.addData("Samples/Sec", "%.1f", shooterRpmCalculator.getSamplesPerSecond());
            telemetry.addData("Shooter In Range Count", shooterInRangeCount);
            double lockTime = locked ? ((shooterGotLockTime - shooterLostLockTime) * 1e-9) : 0.0;
            telemetry.addData("Shooter Time to Lock", "%.3f", lockTime);
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
        shooterError = 0;
        updateShooterPid();
    }

    public void updateShooterPid() {
        if (!shooterPidEnabled) {
            return;
        }
        shooterError = BuzzyConfig.SHOOTER_TARGET_RPM - currentRpm;
        double f = BuzzyConfig.SHOOTER_KF_VOLTS; // Determine experimentally
        double p = shooterError * BuzzyConfig.SHOOTER_KP;
        double d = shooterError * (currentRpm - previousRpm) / (shooterRpmCalculator.getSampleTime(0) - shooterRpmCalculator.getSampleTime(-1));
        // No i yet
        double volts = f + p + d;
        if (volts < 0.0) {
            // Don't allow negative rotation
            volts = 0.0;
        }
        setShooterVolts(volts);

        if (Math.abs(shooterError) < BuzzyConfig.SHOOTER_RPM_TOLERANCE) {
            if (shooterInRangeCount == 0) {
                shooterGotLockTime = System.nanoTime();
            }
            shooterLostLockCount = 0;
            shooterInRangeCount++;
        } else {
            if (shooterLostLockCount == 0) {
                shooterLostLockTime = System.nanoTime();
            }
            shooterInRangeCount = 0;
            shooterLostLockCount++;
        }
    }
}
