package org.firstinspires.ftc.teamcode.drive;

import android.annotation.SuppressLint;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class DriveSubsystem {
    // If you use these names when configuring your motors, you can use the auto detect method to construct
    // (NOTE: If you only have two motors only configure one left and one right - and auto detect will handle it)
    private static final String[] BACK_LEFT_MOTOR_NAMES = { "bl", "back_left", "left_back" };
    private static final String[] BACK_RIGHT_MOTOR_NAMES = { "br", "back_right", "right_back" };
    private static final String[] FRONT_LEFT_MOTOR_NAMES = { "fl", "front_left", "left_front" };
    private static final String[] FRONT_RIGHT_MOTOR_NAMES = { "fr", "front_right", "right_front" };

    private final DcMotor leftFrontMotor;
    private final DcMotor leftBackMotor;
    private final DcMotor rightFrontMotor;
    private final DcMotor rightBackMotor;
    private final boolean mecanumWheels;

    public DriveSubsystem(DcMotor leftFrontMotor, DcMotor leftBackMotor, DcMotor rightFrontMotor, DcMotor rightBackMotor, boolean mecanumWheels) {
        this.leftFrontMotor = leftFrontMotor;
        this.leftBackMotor = leftBackMotor;
        this.rightFrontMotor = rightFrontMotor;
        this.rightBackMotor = rightBackMotor;
        this.mecanumWheels = mecanumWheels;
    }

    public DriveSubsystem(DcMotor leftMotor, DcMotor rightMotor) {
        this(leftMotor, leftMotor, rightMotor, rightMotor, false);
    }

    public static DriveSubsystem autoDetectDriveSubsystem(HardwareMap hardwareMap) {
        DcMotor leftFrontMotor = findMotor(hardwareMap, FRONT_LEFT_MOTOR_NAMES);
        DcMotor leftBackMotor = findMotor(hardwareMap, BACK_LEFT_MOTOR_NAMES);

        DcMotor rightFrontMotor = findMotor(hardwareMap, FRONT_RIGHT_MOTOR_NAMES);
        DcMotor rightBackMotor = findMotor(hardwareMap, BACK_RIGHT_MOTOR_NAMES);

        // If all four motors found, assume a mecanum drive
        if (leftFrontMotor != null && leftBackMotor != null && rightFrontMotor != null && rightBackMotor != null) {
            // TODO: Need to check - if we aren't consistent with how they are mounted/wired
            // this won't work!
            leftFrontMotor.setDirection(DcMotor.Direction.REVERSE);
            leftBackMotor.setDirection(DcMotor.Direction.REVERSE);
            rightFrontMotor.setDirection(DcMotor.Direction.FORWARD);
            rightBackMotor.setDirection(DcMotor.Direction.FORWARD);
            return new DriveSubsystem(leftFrontMotor, leftBackMotor, rightFrontMotor, rightBackMotor, true);
        }

        // Assume tank drive if only two motors found
        DcMotor leftMotor = leftFrontMotor != null ? leftFrontMotor : leftBackMotor;
        DcMotor rightMotor = rightFrontMotor != null ? rightFrontMotor : rightBackMotor;
        if (leftMotor != null && rightMotor != null) {
            // TODO: Need to check - if we aren't consistent with how they are mounted/wired
            // this won't work
            leftMotor.setDirection(DcMotor.Direction.REVERSE);
            rightMotor.setDirection(DcMotor.Direction.FORWARD);
            return new DriveSubsystem(leftMotor, rightMotor);
        }

        // Otherwise, robot needs to be reconfigured
        throw new IllegalArgumentException("Failed to find two or four motors configured as \"bl\", \"fl\" and/or \"br\", \"fr\"");
    }

    public void drive(Gamepad gamepad) {
        if (DriveConfig.driveMode == DriveConfig.DriveMode.TANK) {
            driveTank(gamepad);
        } else if (DriveConfig.driveMode == DriveConfig.DriveMode.ARCADE) {
            driveArcade(gamepad);
        } else {
            driveMecanum(gamepad);
        }
    }

    public void stop() {
        driveTank(0, 0);
    }

    public void forward(double forwardPower) {
        driveTank(forwardPower, forwardPower);
    }

    public void spin(double rotateRightPower) {
        driveTank(rotateRightPower, -rotateRightPower);
    }

    /**
     * Tank drive with power scaling.
     *
     * @param leftPower - Range of -1.0 to +1.0 (negative for backwards)
     * @param rightPower - Range of -1.0 to +1.0 (negative for backwards)
     */
    public void driveTank(double leftPower, double rightPower) {
        leftFrontMotor.setPower(leftPower);
        leftBackMotor.setPower(leftPower);
        rightFrontMotor.setPower(rightPower);
        rightBackMotor.setPower(rightPower);
    }

    public void driveTank(Gamepad gamepad) {
        driveTank(-gamepad.left_stick_y, -gamepad.right_stick_y);
    }

    /**
     * Arcade drive with power scaling.
     *
     * @param forwardPower - Range of -1.0 to +1.0 (negative for backwards)
     * @param rotateRightPower - Range of -1.0 to +1.0 (negative for counter-clockwise)
     */
    public void driveArcade(double forwardPower, double rotateRightPower) {
        double leftPower = forwardPower + rotateRightPower;
        double rightPower = forwardPower - rotateRightPower;
        double scale = Math.max(Math.abs(leftPower), Math.abs(rightPower));
        if (scale > 1.0) {
            leftPower /= scale;
            rightPower /= scale;
        }
        driveTank(leftPower, rightPower);
    }

    public void driveArcade(Gamepad gamepad) {
        driveArcade(-gamepad.left_stick_y, gamepad.right_stick_x);
    }

    /**
     * Mecanum drive that allows strafing if mecanumWheels is set to true (same as driveArcade if not).
     *
     * @param forwardPower - Range of -1.0 to +1.0 (negative for backwards)
     * @param rotateRightPower - Range of -1.0 to +1.0 (negative for counter-clockwise)
     * @param strafePower - Range of -1.0 to +1.0 (negative for strafing left)
     */
    public void driveMecanum(double forwardPower, double rotateRightPower, double strafePower) {
        if (!mecanumWheels) {
            driveArcade(forwardPower, rotateRightPower);
        }
        double leftFrontPower = forwardPower + strafePower + rotateRightPower;
        double leftBackPower = forwardPower - strafePower + rotateRightPower;
        double rightFrontPower = forwardPower - strafePower - rotateRightPower;
        double rightBackPower = forwardPower + strafePower - rotateRightPower;
        double scale = Math.max(Math.max(Math.abs(leftFrontPower), Math.abs(leftBackPower)), Math.max(Math.abs(rightFrontPower), Math.abs(rightBackPower)));
        if (scale > 1.0) {
            leftFrontPower /= scale;
            leftBackPower /= scale;
            rightFrontPower /= scale;
            rightBackPower /= scale;
        }
        leftFrontMotor.setPower(leftFrontPower);
        leftBackMotor.setPower(leftBackPower);
        rightFrontMotor.setPower(rightFrontPower);
        rightBackMotor.setPower(rightBackPower);
    }

    public void driveMecanum(Gamepad gamepad) {
        driveMecanum(-gamepad.left_stick_y, gamepad.right_stick_x, gamepad.left_stick_x);
    }

    /**
     * Check to see if this drive subsystem has four motors.
     *
     * @return True if this drive subsystem has four motors, false otherwise.
     */
    public boolean hasFourMotors() {
        return (leftBackMotor != leftFrontMotor) && (rightBackMotor != rightFrontMotor);
    }

    private static DcMotor findMotor(HardwareMap hardwareMap, String[] motorNames) {
        for (String name : motorNames) {
            try {
                DcMotor motor = hardwareMap.get(DcMotor.class, name);
                if (motor != null) {
                    return motor;
                }
            } catch (IllegalArgumentException e) {
                // Will try next name in list
            }
        }
        // Failed to find motor by its possible names
        return null;
    }

    public void resetEncoders() {
        leftFrontMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftBackMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightFrontMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightBackMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    public void telemetryAddData(Telemetry telemetry) {
        if (hasFourMotors()) {
            telemetryAddData(telemetry, "leftFront", leftFrontMotor);
            telemetryAddData(telemetry, "leftBack", leftBackMotor);
            telemetryAddData(telemetry, "rightFront", rightFrontMotor);
            telemetryAddData(telemetry, "rightBack", rightBackMotor);
        } else {
            telemetryAddData(telemetry, "leftSide", leftFrontMotor);
            telemetryAddData(telemetry, "rightSide", rightFrontMotor);
        }
    }

    @SuppressLint("DefaultLocale")
    private void telemetryAddData(Telemetry telemetry, String name, DcMotor motor) {
        telemetry.addData(name, String.format("pow: %.2f, pos: %d", motor.getPower(), motor.getCurrentPosition()));
    }
}
