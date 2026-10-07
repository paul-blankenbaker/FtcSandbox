package org.firstinspires.ftc.teamcode.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name = "Leave park simple", group = "Robot")
public class LeaveParkSimple extends OpMode {
    /* Declare OpMode members. */
    public DcMotor backLeftDrive = null;
    public DcMotor backRightDrive = null;

    public DcMotor frontRightDrive = null;

    public DcMotor frontLeftDrive = null;

    private final ElapsedTime runTime = new ElapsedTime();


    @Override
    public void init() {
        // Define and Initialize Motors
        // Control hub 0 & 1
        backLeftDrive = hardwareMap.get(DcMotor.class, "bl");

        frontLeftDrive = hardwareMap.get(DcMotor.class, "fl");

        // Expansion hub 0 & 1
        backRightDrive = hardwareMap.get(DcMotor.class, "br");

        frontRightDrive = hardwareMap.get(DcMotor.class, "fr");

        backRightDrive.setDirection(DcMotor.Direction.REVERSE);

        backLeftDrive.setDirection(DcMotor.Direction.FORWARD);

        frontRightDrive.setDirection(DcMotor.Direction.REVERSE);

        frontLeftDrive.setDirection(DcMotor.Direction.FORWARD);


        telemetry.addData(">", "Robot Ready.  Press START.");



    }

    public void start(){
        runTime.reset();
        telemetry.addData(">", "Robot Ready.  Robot Starting.");
    }
// ma

    @Override
    public void loop() {
        double leftPower = 0.4;
        if (runTime.milliseconds() > 500) {
            leftPower = 0;
        }

        double rightPower = leftPower;


        backLeftDrive.setPower(leftPower);

        backRightDrive.setPower(rightPower);

        frontLeftDrive.setPower(leftPower);

        frontRightDrive.setPower(rightPower);

        telemetry.addData("leftPower", leftPower);
        telemetry.addData("rightPower", rightPower);
        telemetry.addData(">", "Robot looping.");

    }
}
