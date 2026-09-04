
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;


@TeleOp(name = "Car Drive", group = "Robot")
public class RobotTeleopTankDrive extends OpMode {
    /* Declare OpMode members. */
    public DcMotor backLeftDrive = null;
    public DcMotor backRightDrive = null;

    int backLeftStartPosition;
    int backRightStartPosition;

    /*
     * Code to run ONCE when the driver hits INIT


    private Servo left_arm, right_arm;

    private double rarm_pos, larm_pos;

    private static double larm_home_pos = 0.725hbv
    private static double larm_hover_pos = 0.54;


    private static double rarm_home_pos = 0.497;
    private static double rarm_hover_pos = 0.682;
*/
    @Override
    public void init() {
        // Define and Initialize Motors
        // Control hub 0 & 1
        backLeftDrive = hardwareMap.get(DcMotor.class, "backLeft");

        // Expansion hub 0 & 1
        backRightDrive = hardwareMap.get(DcMotor.class, "backRight");

        backRightDrive.setDirection(DcMotor.Direction.REVERSE);

        backLeftDrive.setDirection(DcMotor.Direction.FORWARD);

        backLeftStartPosition = backLeftDrive.getCurrentPosition();
        backRightStartPosition = backRightDrive.getCurrentPosition();



        telemetry.addData(">", "Robot Ready.  Press START.");

        /*left_arm = hardwareMap.servo.get("leftArm");
        right_arm = hardwareMap.servo.get("rightArm");

        rarm_pos = rarm_home_pos;
        larm_pos = larm_home_pos;*/


    }
    // This is inches over the current position of the robot
    final static double POS_TO_IN = 0.02193927522;

   // double DistanceTrackerOpmMode;
  //  double DistanceTracker = 0;


    @Override
    public void loop() {
               double rotation;
        double forwardBackward;

        boolean b;

        if (gamepad1.bWasPressed() == true) {
            backLeftStartPosition = backLeftDrive.getCurrentPosition();
            backRightStartPosition = backRightDrive.getCurrentPosition();
        }


        //read_sensors();

        // Run wheels in tank mode (note: The joystick goes negative when pushed forward, so negate it)
        forwardBackward = -gamepad1.left_stick_y;
        rotation = gamepad1.right_stick_x;

        telemetry.addData("forwardBackward", forwardBackward);
        telemetry.addData("spinTurn", rotation);

        double leftPower = forwardBackward + rotation;
        double rightPower = forwardBackward - rotation;

        double mag = Math.max(Math.abs(leftPower), Math.abs(rightPower));
        if (mag > 1.0) {
            leftPower /= mag;
            rightPower /= mag;
        }


        setTankDrivePower(leftPower * 1, rightPower*1);
       // updateArm();
    }
    private void setTankDrivePower(double leftPower, double rightPower) {
        leftPower = Math.min(1.0, Math.max(-1.0, leftPower));
        backLeftDrive.setPower(leftPower);


        rightPower = Math.min(1.0, Math.max(-1.0, rightPower));
        backRightDrive.setPower(rightPower);


        backLeftDrive.getCurrentPosition();
        backRightDrive.getCurrentPosition();

        double leftPosition = backLeftDrive.getCurrentPosition() - backLeftStartPosition;
        double rightPosition = backRightDrive.getCurrentPosition() - backRightStartPosition;




        telemetry.addData("leftPower", leftPower);
        telemetry.addData("rightPower", rightPower);
        telemetry.addData("left position", leftPosition);
        telemetry.addData("right position", rightPosition);

        telemetry.addData("right pos (IN)", rightPosition * POS_TO_IN);
        telemetry.addData("left pos (IN)", leftPosition * POS_TO_IN);

        /*
        telemetry.addData("Total right", Math.abs(rightPosition * POS_TO_IN));
        telemetry.addData("Total left", Math.abs(leftPosition * POS_TO_IN));
        */
    }

    private void read_sensors() {
        // Place holder for when we start reading back sensors
    }
}
