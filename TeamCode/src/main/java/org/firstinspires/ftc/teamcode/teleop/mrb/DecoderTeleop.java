package org.firstinspires.ftc.teamcode.teleop.mrb;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.robots.Decoder;

@TeleOp(name = "Mr B: Decoder Teleop", group = "Robot")
public class DecoderTeleop extends OpMode {
    double shooterVolts = 5.0;
    private Decoder robot;

    @Override
    public void init() {
        robot = new Decoder(hardwareMap);
    }

    @Override
    public void loop() {
        robot.update();
        robot.drive(gamepad1);

        if (gamepad1.dpadDownWasPressed()) {
            shooterVolts -= 0.25;
        } else if (gamepad1.dpadUpWasPressed()) {
            shooterVolts += 0.25;
        }

        if (gamepad1.rightBumperWasPressed()) {
            if (robot.isShooterRunning()) {
                robot.stopShooter();
            } else {
                robot.setShooterVolts(shooterVolts);
            }
        }

        if (gamepad1.left_bumper) {
            robot.intakePullIn();
        } else if (gamepad1.left_trigger_pressed) {
            robot.intakePushOut();
        } else {
            robot.intakeStop();
        }


        robot.addData(telemetry);
    }
}
