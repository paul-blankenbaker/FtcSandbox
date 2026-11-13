package org.firstinspires.ftc.teamcode.robots;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.drive.DriveSubsystem;
import org.firstinspires.ftc.teamcode.utils.PowerVoltageCalculator;
import org.firstinspires.ftc.teamcode.utils.RpmCalculator;

/**
 * The hardware on the 2025-2026 Decode robot.
 */
public class Decoder {

    private final DcMotor shooterLeft;
    private final DcMotor shooterRight;
    private final DcMotor intake;
    private final DriveSubsystem driveSubsystem;
    private final PowerVoltageCalculator voltageCalculator;

    private final RpmCalculator shooterRpmCalculator;


    public Decoder(HardwareMap hardwareMap) {
        shooterLeft = hardwareMap.get(DcMotor.class, "shooterLeft");
        shooterRight = hardwareMap.get(DcMotor.class, "shooterRight");
        shooterLeft.setDirection(DcMotor.Direction.FORWARD);
        shooterRight.setDirection(DcMotor.Direction.FORWARD);
        intake = hardwareMap.get(DcMotor.class, "intake");
        driveSubsystem = DriveSubsystem.autoDetectDriveSubsystem(hardwareMap);
        voltageCalculator = new PowerVoltageCalculator(hardwareMap.voltageSensor);
        shooterRpmCalculator = new RpmCalculator(28.0, 100);
    }

    public void update() {
        voltageCalculator.update();
        shooterRpmCalculator.addSample(getShooterCounts());
    }

    public void drive(Gamepad gamepad) {
        driveSubsystem.driveMecanum(gamepad);
    }

    public void setShooterPower(double power) {
        shooterRight.setPower(power);
        shooterLeft.setPower(power);
    }
    public void setShooterVolts(double volts) {
        double power = voltageCalculator.voltageToPower(volts);
        setShooterPower(power);
    }

    public void setIntakeVolts(double volts) {
        intake.setPower(voltageCalculator.voltageToPower(volts));
    }

    public void intakeStop() {
        intake.setPower(0);
    }
    public void intakePullIn() {
        setIntakeVolts(6.0);
    }

    public void intakePushOut() {
        setIntakeVolts(-6.0);
    }

    public int getShooterCounts() {
        return shooterLeft.getCurrentPosition();
    }

    public void addData(Telemetry telemetry) {
        telemetry.addData("Shooter Counts", getShooterCounts());
        double shooterPower = shooterRight.getPower();
        telemetry.addData("Shooter Voltage", voltageCalculator.powerToVoltage(shooterPower));
        telemetry.addData("Shooter Power", shooterPower);
        telemetry.addData("Shooter RPM", shooterRpmCalculator.computeRpm(0.050));
        telemetry.addData("Samples/Sec", shooterRpmCalculator.getSamplesPerSecond());
    }

    public boolean isShooterRunning() {
        return Math.abs(shooterRight.getPower()) > 0.05;
    }

    public void stopShooter() {
        setShooterPower(0.0);
    }
}
