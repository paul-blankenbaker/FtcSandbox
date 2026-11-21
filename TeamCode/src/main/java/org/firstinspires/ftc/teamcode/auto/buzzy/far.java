package org.firstinspires.ftc.teamcode.auto.buzzy;
import org.firstinspires.ftc.teamcode.utils.PowerVoltageCalculator;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;

import java.time.Duration;

@Autonomous(name = "far", group = "Autonomous")

public class far extends LinearOpMode {

    // Drive motors
    private DcMotor backLeftDrive = null;
    private DcMotor backRightDrive = null;
    double voltOut = 0;
    private PowerVoltageCalculator powerCalc;
    private DcMotor shooterMotor = null;
    private DcMotor intakeMotor = null;

    // Vision sensor
    private CRServo Feeder;
    private CRServo RSI;
    private CRServo LSI;





    @Override
    public void runOpMode() throws InterruptedException {



        try {
            backLeftDrive = hardwareMap.get(DcMotor.class, "bl");
            backRightDrive = hardwareMap.get(DcMotor.class, "br");

            backLeftDrive.setDirection(DcMotor.Direction.FORWARD);
            backRightDrive.setDirection(DcMotor.Direction.REVERSE);
        } catch (Exception e) {
            telemetry.addData("Warning", "Drive motors not configured properly in HardwareMap");
        }

        // MOTORS = shooter = shooter intake= intake
        // SERVOS = shooterFeed rightSideIntake leftSideIntake
        // Initialize Shooter and Intake motors

        shooterMotor = hardwareMap.get(DcMotor.class, "shooter");


        intakeMotor = hardwareMap.get(DcMotor.class, "intake");

        Feeder = hardwareMap.get(CRServo.class, "shooterFeed");

        RSI = hardwareMap.get(CRServo.class, "rightSideIntake");

        LSI = hardwareMap.get(CRServo.class, "leftSideIntake");

        powerCalc = new PowerVoltageCalculator(hardwareMap.voltageSensor);



        // =========================================================================
        // STEP 1: CONTINUOUSLY SCAN FOR APRILTAG (IDs 32, 35, 44, 39)
        // =========================================================================


        setShooterVolts(6.65,3000);

        //
//6.65
        Feeder.setPower(.6);
        setShooterVolts(6.60, 900);
        setShooterVolts(7.5,900);
        setShooterVolts(10, 900);

        if (Feeder != null) Feeder.setPower(.95);
        setShooterVolts(0, 0);
        Feeder.setPower(0.0);

        //   if (intakeMotor != null && intakeMotor != shooterMotor) intakeMotor.setPower(1.0);


        //     if (intakeMotor != null) intakeMotor.setPower(0);
// =========================================================================
        // STEP 2: MOVE FORWARD 2 FEET (Toward ID)
        // =========================================================================
        telemetry.addData("Step 2", "Moving forward...");
        telemetry.update();
        driveForTime(-.5, -.5, 300);
        sleep(300);
        // =========================================================================
        // STEP 3: TURN RIGHT 90 DEGREES
        // =========================================================================
        telemetry.addData("Step 3", "Turning right 90 degrees...");
        telemetry.update();
        driveForTime(5, -5, 300);
        sleep(300);


        // =========================================================================
        // STEP 4: MOVE FORWARD 2.3 FEET
        // =========================================================================
        telemetry.addData("Step 4", "Moving forward...");
        telemetry.update();
        driveForTime(.5, .5, 700);
        sleep(300);


        telemetry.addData("Step 5", "Moving forward...");
        telemetry.update();
        driveForTime(5, -5, 95);
        sleep(300);


        telemetry.addData("Step 6", "Moving forward...");
        telemetry.update();
        driveForTime(4, 4, 1130);
        sleep(600);

        telemetry.addData("Step 6", "Moving forward...");
        telemetry.update();
        driveForTime(-4, -4, 100);
        sleep(300);


        // Finish OpMode

        setDrivePower(0, 0);
        telemetry.addData("Status", "Autonomous Complete!");
        telemetry.update();
    }

    private void setShooterVolts(double volts, int millis) {
        powerCalc.update();
        if (shooterMotor != null) shooterMotor.setPower(powerCalc.voltageToPower(volts));
        sleep(millis);
    }

    /**
     * Sets individual power to each drive motor
     */
    private void setMotorPowers(double flPower, double blPower, double frPower, double brPower) {
        if (backLeftDrive != null) backLeftDrive.setPower(blPower);
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




