package org.firstinspires.ftc.teamcode.teleop.mrb;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.drive.DriveSubsystem;

@TeleOp(name="Mr B: Drive by Power", group="Robot")
//@Disabled
public class DriveByPower extends OpMode {
    private DriveSubsystem drive;

    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        drive = DriveSubsystem.autoDetectDriveSubsystem(hardwareMap);
    }

    /**
     * Called repeatedly until teleop is over.
     */
    @Override
    public void loop() {
        // Let user drive in configured drive mode
        drive.drive(gamepad1);

        // Optionally let user reset encoders by pressing/releasing the "a" button
        if (gamepad1.aWasReleased()) {
            drive.resetEncoders();
        }

        // Optionally, display some diagnostics (handy when troubleshooting drive issues)
        drive.telemetryAddData(telemetry);
        telemetry.update();
    }

}
