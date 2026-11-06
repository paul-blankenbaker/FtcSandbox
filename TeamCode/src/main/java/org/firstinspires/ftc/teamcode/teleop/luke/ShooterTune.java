
package org.firstinspires.ftc.teamcode.teleop.luke;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.utils.PowerVoltageCalculator;

@TeleOp(name = "Robot: BuzzyShooter", group = "Robot")
public class ShooterTune extends OpMode {

    public DcMotor shooter = null;
    CRServo feeder = null;
    double voltOut = 0;
    private PowerVoltageCalculator powerCalc;

    public static double COUNTS_PER_REV = 28.0;

    private int lastPosition = 0;
    private double lastTime = 0.0;
    private double currentRPM = 0.0;

    private boolean rbWasPressed = false;
    private boolean lbWasPressed = false;

    @Override
    public void init() {
        shooter = hardwareMap.get(DcMotor.class, "shooter");
        // Set direction of shooter motor and enable encoder so we can display RPM
        shooter.setDirection(DcMotor.Direction.REVERSE);
        shooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        feeder = hardwareMap.get(CRServo.class, "shooterFeed");
        powerCalc = new PowerVoltageCalculator(hardwareMap.voltageSensor);
    }

    @Override
    public void loop() {

        // Use left trigger to spin feeder so ball goes into shooter
        if (gamepad1.left_trigger > 0) {
            feeder.setPower(gamepad1.left_trigger);
        } else {
            feeder.setPower(0);
        }

        if (gamepad1.left_bumper && !lbWasPressed) {
            voltOut = voltOut + .5;
        } else if (gamepad1.right_bumper && !rbWasPressed) {
            voltOut = voltOut - .5;
        }
        if (gamepad1.b) {
            voltOut = 0;
        }

        lbWasPressed = gamepad1.left_bumper;
        rbWasPressed = gamepad1.right_bumper;

        double power = powerCalc.voltageToPower(voltOut);
        shooter.setPower(power);
        telemetry.addData("Power", power);
        telemetry.addData("Voltage", -voltOut);
        powerCalc.addData(telemetry);
        telemetry.addData("RPM", "%.1f", getRPM());

    }

    // Compute RPM of shooter given position from previous reading 0.5 seconds ago
    public double getRPM() {
        if (shooter == null) {
            return 0.0;
        }

        double currentTime = getRuntime();
        double deltaTime = currentTime - lastTime;

        if (lastTime == 0.0 || deltaTime < 0) {
            lastTime = currentTime;
            lastPosition = shooter.getCurrentPosition();
            return 0.0;
        }

        if (deltaTime >= 0.5) {
            int currentPosition = shooter.getCurrentPosition();
            int deltaPosition = currentPosition - lastPosition;

            double ticksPerRev = shooter.getMotorType().getTicksPerRev();
            if (ticksPerRev <= 0) {
                ticksPerRev = COUNTS_PER_REV;
            }

            currentRPM = (deltaPosition / ticksPerRev) * (60.0 / deltaTime);

            lastPosition = currentPosition;
            lastTime = currentTime;
        }

        return currentRPM;
    }
}
