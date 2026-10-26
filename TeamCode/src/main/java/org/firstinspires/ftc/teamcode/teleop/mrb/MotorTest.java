package org.firstinspires.ftc.teamcode.teleop.mrb;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name="Mr B: Motor Test", group="Robot")
//@Disabled
public class MotorTest extends OpMode {
    String[] motorNames;
    int motorIndex = 0;
    ElapsedTime velocityTime = new ElapsedTime();
    int lastPosition = Integer.MAX_VALUE;

    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        motorNames = hardwareMap.getAllNames(DcMotor.class).toArray(new String[0]);
    }

    @Override
    public void start() {
        motorIndex = 0;
        lastPosition = Integer.MAX_VALUE;
    }

    /**
     * Called repeatedly until teleop is over.
     */
    @Override
    public void loop() {
        if (motorNames.length == 0) {
            telemetry.addData("Message", "ERROR no motors found");
            telemetry.update();
            return;
        }

        if (gamepad1.aWasPressed()) {
            setPower(0);
            motorIndex = (motorIndex + 1) % motorNames.length;
        }

        // While holding B down, use Y axis to adjust power
        double power = gamepad1.b ? -gamepad1.left_stick_y : 0.0;
        // Or, dpad to set fixed positive or negative speed
        power = gamepad1.dpad_up ? 0.5 : power;
        power = gamepad1.dpad_down ? -0.5 : power;

        DcMotor motor = setPower(power);

        int currentPosition = motor.getCurrentPosition();
        double velocity = 0.0;
        if (currentPosition > lastPosition) {
            double secondsElapsed = velocityTime.seconds();
            // Update 10 times a second
            if (secondsElapsed > 0.1) {
                velocity = ((double) (currentPosition - lastPosition) / secondsElapsed);
                velocityTime.reset();
                lastPosition = currentPosition;
            }
        }

        telemetry.addData("Motor", motorNames[motorIndex]);
        telemetry.addData("Connection", motor.getConnectionInfo());
        telemetry.addData("Power", "%.3f", motor.getPower());
        telemetry.addData("Position", currentPosition);
        telemetry.addData("Last Velocity (pos/sec)", "%.3f", velocity);
        telemetry.update();
    }

    private DcMotor setPower(double power) {
        DcMotor motor = hardwareMap.get(DcMotor.class, motorNames[motorIndex]);
        motor.setPower(power);
        return motor;
    }

}
