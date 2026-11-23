package org.firstinspires.ftc.teamcode.auto.buzzy;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.utils.PowerVoltageCalculator;

import java.util.List;

@Autonomous(name = "closeTwo", group = "Autonomous")

public class closeTwo extends LinearOpMode {

    // Drive motors
    private DcMotor backLeftDrive = null;

    private PowerVoltageCalculator powerCalc;

    private DcMotor backRightDrive = null;


    private DcMotor shooterMotor = null;
    private DcMotor intakeMotor = null;

    // Vision sensor
    private Limelight3A limelight;

    private PowerVoltageCalculator powerCalculator;
    private CRServo Feeder;
    private CRServo RSI;
    private CRServo LSI;



    @Override
    public void runOpMode() throws InterruptedException {
        // Initialize drive motors ("bl", "fl", "br", "fr")
        powerCalculator = new PowerVoltageCalculator(hardwareMap.voltageSensor);
        try {
            backLeftDrive = hardwareMap.get(DcMotor.class, "bl");

            backRightDrive = hardwareMap.get(DcMotor.class, "br");


            backLeftDrive.setDirection(DcMotor.Direction.FORWARD);

            backRightDrive.setDirection(DcMotor.Direction.REVERSE);

            // MOTORS = shooter = shooter intake= intake
            // SERVOS = shooterFeed rightSideIntake leftSideIntake
            // Initialize Shooter and Intake motors


            shooterMotor = hardwareMap.get(DcMotor.class, "shooter");

            intakeMotor = hardwareMap.get(DcMotor.class, "intake");

            Feeder = hardwareMap.get(CRServo.class, "shooterFeed");

            RSI = hardwareMap.get(CRServo.class, "rightSideIntake");

            LSI = hardwareMap.get(CRServo.class, "leftSideIntake");

            powerCalc = new PowerVoltageCalculator(hardwareMap.voltageSensor);


        } catch (Exception e) {
            telemetry.addData("Warning", "Drive motors not configured properly in HardwareMap");
        }

        // Initialize Limelight camera
        //try {
        limelight = hardwareMap.get(Limelight3A.class, "limelight");


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
        // STEP 1: CONTINUOUSLY SCAN FOR APRILTAG (IDs 32, 35)
        // =========================================================================
        telemetry.addData("Step 1", "Scanning for AprilTags...");
        telemetry.update();

        boolean tagFound = false;

        // Keep scanning while OpMode is active until an AprilTag is identified
        long maxWaitMillis = 18_000;
        ElapsedTime timeWaited = new ElapsedTime();

        while (opModeIsActive() && !tagFound && (timeWaited.milliseconds() < maxWaitMillis)) {
            if (limelight != null) {
                LLResult result = limelight.getLatestResult();
                if (result != null && result.isValid()) {
                    List<LLResultTypes.FiducialResult> fiducialResults = result.getFiducialResults();

                    if (!fiducialResults.isEmpty()) {
                        for (LLResultTypes.FiducialResult fr : fiducialResults) {
                            int id = fr.getFiducialId();
                            if (id == 35 || id == 44) {
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
        telemetry.addData("Status", "Found tag: " + tagFound);
        telemetry.update();


        if(tagFound  && timeWaited.milliseconds() < maxWaitMillis) {
            if (shooterMotor != null) shooterMotor.setPower(1.0);

            sleep(1000);


            //
//6.65
            Feeder.setPower(1);
            setShooterVolts(6.65, 900);
            setShooterVolts(7.5, 900);
            setShooterVolts(11, 900);

            if (Feeder != null) Feeder.setPower(.95);
            setShooterVolts(0, 0);
            Feeder.setPower(0.0);


            if (Feeder != null) Feeder.setPower(0);
            if (Feeder != null) Feeder.setPower(0);

            telemetry.addData("Status", "Shooting complete! Moving forward...");
            telemetry.update();

            driveStraight(-5, 350);
            sleep(10);
            rightSpin(6, 6, 600);
            sleep(10);
            driveStraight(-10, 1000);
            driveStraight(10, 80);
        }
        else {
            driveStraight(-5, 350);
            sleep(10);
            rightSpin(6, 6, 600);
            sleep(10);
            driveStraight(-10, 1000);
            driveStraight(10, 80);
        }


        if (limelight != null) {
            limelight.stop();
        }
        setDriveVolts(0, 0);
        telemetry.addData("Status", "Autonomous Complete!");
        telemetry.update();
    }



    private void driveForTime(double rightPow, double leftPow, long time) {

        if (!opModeIsActive()) return;

        telemetry.addData("Step 2", "Moving forward...");
        telemetry.update();

        setDriveVolts(leftPow, rightPow);

        sleep(time);

        setDriveVolts(0, 0);
    }

    private void setShooterVolts(double volts, int millis) {
        powerCalc.update();
        if (shooterMotor != null) shooterMotor.setPower(powerCalc.voltageToPower(volts));
        sleep(millis);
    }



    /**
     * Sets individual power to each drive motor
     */
    private void setMotorVolts(double flVolts, double blVolts, double frVolts, double brVolts) {
        powerCalculator.update();
        double flPower = powerCalculator.voltageToPower(flVolts);
        double blPower = powerCalculator.voltageToPower(blVolts);
        double frPower = powerCalculator.voltageToPower(frVolts);
        double brPower = powerCalculator.voltageToPower(brVolts);
        if (backLeftDrive != null) backLeftDrive.setPower(blPower);
        if (backRightDrive != null) backRightDrive.setPower(brPower);
    }

    /**
     * Helper to set power to left and right drive sides
     */
    private void setDriveVolts(double leftPower, double rightPower) {
        setMotorVolts(leftPower, leftPower, rightPower, rightPower);
    }

    /**
     * Simple timed driving helper function
     */
    private void rightSpin(double leftPower, double rightPower, long durationMs) {
        if (!opModeIsActive()) return;
        setDriveVolts(leftPower, -rightPower);
        sleep(durationMs);
        setDriveVolts(0, 0);
    }

    /**
     * Turns right for a specified duration in milliseconds
     */
    private void driveStraight(double power, long durationMs) {
        if (!opModeIsActive()) return;
        setDriveVolts(power, power);
        sleep(durationMs);
        setDriveVolts(0, 0);
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

