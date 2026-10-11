package org.firstinspires.ftc.teamcode.auto;

import static android.os.SystemClock.sleep;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name = "Leave park simple", group = "Robot")
public class LeaveParkSimple extends OpMode {
    /* Declare OpMode members. */
    public DcMotor backLeftDrive = null;
    public DcMotor backRightDrive = null;

    public DcMotor frontRightDrive = null;

    public DcMotor frontLeftDrive = null;

    private final ElapsedTime runTime = new ElapsedTime();

    final static double POS_TO_IN = 0.02193928;


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

    public void start() {
        runTime.reset();
        telemetry.addData(">", "Robot Ready.  Robot Starting.");
    }
// The robot runs about 15 inches a 0.4 power and the robot is about 15 inches long.
    // 17 inches at 0.5 power

    @Override
    public void loop() {
        double leftPower = 0.5;
        if (runTime.milliseconds() < 10000) {
            leftPower = 0;
        } else if (getDistance*POS_TO_IN = 90) {
            leftPower = 0;
        }

        double distanceLeft = getDistance(backLeftDrive, frontLeftDrive);
        int blPos = backLeftDrive.getCurrentPosition();

        double rightPower = leftPower;


        backLeftDrive.setPower(leftPower);

        backRightDrive.setPower(rightPower);

        frontLeftDrive.setPower(leftPower);

        frontRightDrive.setPower(rightPower);

        telemetry.addData("leftPower", leftPower);
        telemetry.addData("rightPower", rightPower);
        telemetry.addData(">", "Robot looping.");

    }

    /**
     * Returns the average distance reported by two motors in inches
     * @param backLeftDrive
     * @param frontLeftDrive
     * @return
     */
    private double getDistance(DcMotor backLeftDrive, DcMotor frontLeftDrive) {

    }
}
