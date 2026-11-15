package org.firstinspires.ftc.teamcode.robots;

import com.acmerobotics.dashboard.config.Config;

@Config
public class BuzzyConfig {

    public static boolean ENABLE_DASHBOARD_TELEMETRY = false;

    public static double SHOOTER_TARGET_RPM = 2350.0;
    public static double SHOOTER_KF_VOLTS = 5.25;

    public static double SHOOTER_KP = 0.007;

    public static double SHOOTER_KD = 0.0;

    public static double FEEDER_VOLTS = 5.0;
    public static int FEEDER_DURATION_MILLIS = 125;

    public static double INTAKE_VOLTS = 9.0;

    public static double SHOOTER_COUNTS_PER_REV = 28.0;

    // How close actual RPM must be within target RPM to be considered good
    public static double SHOOTER_RPM_TOLERANCE = 150;

    public static double DRIVE_MAX_VOLTS = 13.0;
    public static double DRIVE_THROTTLE_MIN_VOLTS = 2.0;
    public static double DRIVE_ROTATION_MIN_VOLTS = 2.25;
    public static double DRIVE_STICK_DEAD_ZONE = 0.05;

    public static double DRIVE_THROTTLE_SCALE = 1.0;
    public static double DRIVE_ROTATION_SCALE = 0.4;
}
