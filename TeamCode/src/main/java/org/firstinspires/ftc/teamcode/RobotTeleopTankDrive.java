
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;


@TeleOp(name="Car Drive", group="Robot")
public class RobotTeleopTankDrive extends OpMode {
    /* Declare OpMode members. */
    public DcMotor  backLeftDrive   = null;
    public DcMotor  backRightDrive   = null;
    public DcMotor  frontLeftDrive   = null;
    public DcMotor  frontRightDrive  = null;

    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        // Define and Initialize Motors
        // Control hub 0 & 1
        backLeftDrive  = hardwareMap.get(DcMotor.class, "bl");
        frontLeftDrive  = hardwareMap.get(DcMotor.class, "fl");
        // Expansion hub 0 & 1
        backRightDrive = hardwareMap.get(DcMotor.class, "br");
        frontRightDrive = hardwareMap.get(DcMotor.class, "fr");

        backRightDrive.setDirection(DcMotor.Direction.REVERSE);
        frontRightDrive.setDirection(DcMotor.Direction.REVERSE);
        backLeftDrive.setDirection(DcMotor.Direction.FORWARD);
        frontLeftDrive.setDirection(DcMotor.Direction.FORWARD);


        telemetry.addData(">", "Robot Ready.  Press START.");    //
    }


    @Override
    public void loop() {
        double rotation;
        double fowardBackward;

        read_sensors();

        // Run wheels in tank mode (note: The joystick goes negative when pushed forward, so negate it)
        fowardBackward = -gamepad1.left_stick_y;
        rotation = gamepad1.right_stick_x;

        double leftPower = fowardBackward + rotation;
        double rightPower = fowardBackward - rotation;

       double mag = Math.max(Math.abs(leftPower), Math.abs(rightPower));
       if (mag > 1.0)
       {
           leftPower /= mag;
           rightPower /= mag;
       }


        setTankDrivePower(leftPower * 0.7, rightPower * 0.7);
    }

    private void setTankDrivePower(double leftPower, double rightPower) {
        leftPower = Math.min(1.0, Math.max(-1.0, leftPower));
        backLeftDrive.setPower(leftPower);
        frontLeftDrive.setPower(leftPower);

        rightPower = Math.min(1.0, Math.max(-1.0, rightPower));
        backRightDrive.setPower(rightPower);
        frontRightDrive.setPower(rightPower);

        telemetry.addData("leftPower", leftPower);
        telemetry.addData("rightPower", rightPower);
    }

    private void read_sensors() {
        // Place holder for when we start reading back sensors
    }
}
