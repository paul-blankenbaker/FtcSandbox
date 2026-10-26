package org.firstinspires.ftc.teamcode.drive;

import com.acmerobotics.dashboard.config.Config;

@Config
public class DriveConfig {
    public enum DriveMode {
        MECANUM,
        ARCADE,
        TANK
    }

    public static DriveMode driveMode = DriveMode.MECANUM;
}
