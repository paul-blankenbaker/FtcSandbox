package org.firstinspires.ftc.teamcode.teleop.mrb;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.robots.Buzzy;
import org.firstinspires.ftc.teamcode.robots.BuzzyConfig;

@TeleOp(name = "Mr B: Buzzy Teleop", group = "Robot")
public class BuzzyTeleop extends OpMode {
    private Buzzy robot;

    @Override
    public void init() {
        if (BuzzyConfig.ENABLE_DASHBOARD_TELEMETRY) {
            telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        }
        robot = new Buzzy(hardwareMap);
    }

    @Override
    public void loop() {
        robot.update();
        robot.drive(gamepad1, telemetry);

        updateShooterControl();
        updateFeederControl();
        updateIntakeControl();

        robot.addData(telemetry);
    }

    private void updateShooterControl() {
        if (gamepad1.rightBumperWasPressed()) {
            if (robot.isShooterRunning()) {
                robot.stopShooter();
            } else {
                robot.startShooterPid();
            }
        }
    }

    private void updateFeederControl() {
        if (gamepad1.rightTriggerWasPressed()) {
            robot.setFeederVolts(BuzzyConfig.FEEDER_VOLTS, BuzzyConfig.FEEDER_DURATION_MILLIS);
        }
    }

    private void updateIntakeControl() {
        // Currently, you can stop it with either button, but this means to reverse the direction
        // requires 2 clicks - need to decide if that's a good approach or not
        if (gamepad1.leftBumperWasPressed()) {
            if (robot.isIntakeRunning()) {
                robot.intakeStop();
            } else {
                robot.intakePullIn();
            }
        } else if (gamepad1.leftTriggerWasPressed()) {
            if (robot.isIntakeRunning()) {
                robot.intakeStop();
            } else {
                robot.intakePushOut();
            }
        }
    }
}
