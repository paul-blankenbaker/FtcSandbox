package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import java.util.List;

@Autonomous(name = "AutoRun", group = "Autonomous")
public class AutoRun extends LinearOpMode {

    // Drive motors
    private DcMotor backLeftDrive = null;
    private DcMotor frontLeftDrive = null;
    private DcMotor backRightDrive = null;
    private DcMotor frontRightDrive = null;

    // Shooter & Intake motors
    private DcMotor shooterMotor = null;
    private DcMotor intakeMotor = null;

    // Vision sensor
    private Limelight3A limelight;

    @Override
    public void runOpMode() throws InterruptedException {
        // Initialize drive motors ("bl", "fl", "br", "fr")
        try {
            backLeftDrive = hardwareMap.get(DcMotor.class, "bl");
            frontLeftDrive = hardwareMap.get(DcMotor.class, "fl");
            backRightDrive = hardwareMap.get(DcMotor.class, "br");
            frontRightDrive = hardwareMap.get(DcMotor.class, "fr");

            backLeftDrive.setDirection(DcMotor.Direction.FORWARD);
            frontLeftDrive.setDirection(DcMotor.Direction.FORWARD);
            backRightDrive.setDirection(DcMotor.Direction.REVERSE);
            frontRightDrive.setDirection(DcMotor.Direction.REVERSE);
        } catch (Exception e) {
            telemetry.addData("Warning", "Drive motors not configured properly in HardwareMap");
        }

        // Initialize Shooter and Intake motors
        try {
            shooterMotor = hardwareMap.get(DcMotor.class, "shooter");
        } catch (Exception e) {
            try {
                shooterMotor = hardwareMap.get(DcMotor.class, "Intake");
            } catch (Exception ignored) {}
        }
        try {
            intakeMotor = hardwareMap.get(DcMotor.class, "Intake");
        } catch (Exception ignored) {}

        // Initialize Limelight camera 
        try {
            limelight = hardwareMap.get(Limelight3A.class, "limelight");
        } catch (Exception e) {
            try {
                List<Limelight3A> limelights = hardwareMap.getAll(Limelight3A.class);
                if (!limelights.isEmpty()) {
                    limelight = limelights.get(0);
                }
            } catch (Exception ignored) {}
        }

        if (limelight != null) {
            try {
                limelight.pipelineSwitch(8);
                limelight.start();
                telemetry.addData("Limelight", "Limelight connected successfully!");
            } catch (Exception e) {
                telemetry.addData("Warning", "Limelight start error: " + e.getMessage());
            }
        } else {
            telemetry.addData("Warning", "Limelight not found in Robot Configuration! (Check Driver Station Config)");
        }

        telemetry.addData("Status", "Initialized. Waiting for start...");
        telemetry.update();

        waitForStart();

        // =========================================================================
        // STEP 1: CONTINUOUSLY SCAN FOR APRILTAG (IDs 32, 35, 44, 39)
        // =========================================================================
        telemetry.addData("Step 1", "Scanning for AprilTags...");
        telemetry.update();

        boolean tagFound = false;

        // Keep scanning while OpMode is active until an AprilTag is identified
        while (opModeIsActive() && !tagFound) {
            if (limelight != null) {
                LLResult result = limelight.getLatestResult();
                if (result != null && result.isValid()) {
                    List<LLResultTypes.FiducialResult> fiducialResults = result.getFiducialResults();

                    if (!fiducialResults.isEmpty()) {
                        for (LLResultTypes.FiducialResult fr : fiducialResults) {
                            int id = fr.getFiducialId();
                            if (id == 32 || id == 35 || id == 44 || id == 39) {
                                tagFound = true;
                                handleAprilTagDetection(fr, 3.56, -5.4, 5);
                                telemetry.addData("Status", "AprilTag Identified (ID: %d)!", id);
                                break;
                            }
                        }
                    } else {
                        telemetry.addData("Limelight", "Searching for AprilTag... (tx: %.2f)", result.getTx());
                    }
                } else {
                    telemetry.addData("Limelight", "Waiting for camera data...");
                }
            } else {
                telemetry.addData("Warning", "Limelight is null");
            }
            telemetry.update();
            sleep(50);
        }

        if (!opModeIsActive()) return;

        // AprilTag identified: Shoot constantly for 10 seconds
        telemetry.addData("Status", "AprilTag Identified! Shooting for 10 seconds...");
        telemetry.update();

        if (shooterMotor != null) shooterMotor.setPower(1.0);
        if (intakeMotor != null && intakeMotor != shooterMotor) intakeMotor.setPower(1.0);

        sleep(5000);

        if (shooterMotor != null) shooterMotor.setPower(0.0);
        if (intakeMotor != null && intakeMotor != shooterMotor) intakeMotor.setPower(0.0);

// =========================================================================
        // STEP 2: MOVE FORWARD 2 FEET (Toward ID)
        // =========================================================================
        telemetry.addData("Step 2", "Moving 2 feet forward...");
        telemetry.update();
        turnRight(0.5, 300);    // driveForTime(0.5, 0.5, 1600);
        sleep(300);
        // =========================================================================
        // STEP 3: TURN RIGHT 90 DEGREES
        // =========================================================================
        telemetry.addData("Step 3", "Turning right 90 degrees...");
        telemetry.update();
        driveForTime(0.5, 0.5, 450);
        sleep(300);


        // =========================================================================
        // STEP 4: MOVE FORWARD 2.3 FEET
        // =========================================================================
        telemetry.addData("Step 4", "Moving 2.3 feet forward...");
        telemetry.update();
        turnRight(0.5, 950);
        sleep(300);


        // Finish OpMode
        if (limelight != null) {
            limelight.stop();
        }
        setDrivePower(0, 0);
        telemetry.addData("Status", "Autonomous Complete!");
        telemetry.update();
    }

    /**
     * Sets individual power to each drive motor
     */
    private void setMotorPowers(double flPower, double blPower, double frPower, double brPower) {
        if (frontLeftDrive != null) frontLeftDrive.setPower(flPower);
        if (backLeftDrive != null) backLeftDrive.setPower(blPower);
        if (frontRightDrive != null) frontRightDrive.setPower(frPower);
        if (backRightDrive != null) backRightDrive.setPower(brPower);
    }

    /**
     * Helper to set power to left and right drive sides
     */
    private void setDrivePower(double leftPower, double rightPower) {
        setMotorPowers(leftPower, leftPower, rightPower, rightPower);
    }

    /**
     * Simple timed driving helper function
     */
    private void driveForTime(double leftPower, double rightPower, long durationMs) {
        if (!opModeIsActive()) return;
        setDrivePower(leftPower, rightPower);
        sleep(durationMs);
        setDrivePower(0, 0);
    }

    /**
     * Turns right for a specified duration in milliseconds
     */
    private void turnRight(double power, long durationMs) {
        if (!opModeIsActive()) return;
        setDrivePower(power, -power);
        sleep(durationMs);
        setDrivePower(0, 0);
    }

    /**
     * Helper method to process AprilTags.
     */
    private int handleAprilTagDetection(LLResultTypes.FiducialResult fr, double targetX, double targetY, double maxDistance) {
        int id = fr.getFiducialId();
        int onTargetCount = 0;
        if (id == 32) {
            if (isOnTarget("RL", id, fr, targetX, targetY, maxDistance)) onTargetCount++;
        }
        if (id == 35) {
            if (isOnTarget("RR", id, fr, targetX, targetY, maxDistance)) onTargetCount++;
        }
        if (id == 44) {
            if (isOnTarget("BL", id, fr, targetX, targetY, maxDistance)) onTargetCount++;
        }
        if (id == 39) {
            if (isOnTarget("BR", id, fr, targetX, targetY, maxDistance)) onTargetCount++;
        }
        return onTargetCount;
    }

    private boolean isOnTarget(String name, int id, LLResultTypes.FiducialResult fr,
                               double targetX, double targetY, double maxDistance) {
        double x = fr.getTargetXDegrees();
        double y = fr.getTargetYDegrees();

        telemetry.addData("Tag ID", name + ":" + id);
        telemetry.addData("Tag Position", "X: %.2f, Y: %.2f", x, y);

        double Distance2 = (x - targetX) * (x - targetX) + (y - targetY) * (y - targetY);
        double Distance = Math.sqrt(Distance2);
        telemetry.addData("Distance", Distance);
        if (maxDistance > Distance) {
            telemetry.addLine("SHOOT");
            return true;
        } else {
            telemetry.addLine("NO SHOT");
            return false;
        }

    }
}
