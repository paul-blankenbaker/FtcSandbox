package org.firstinspires.ftc.teamcode.robots;

import com.acmerobotics.dashboard.config.Config;

@Config
public class BuzzyConfig {

    public static boolean ENABLE_DASHBOARD_TELEMETRY = false;

    public static double SHOOTER_TARGET_RPM = 2200.0;
    public static double SHOOTER_KF_VOLTS = 5.0;

    public static double SHOOTER_KP = 0.007;

    public static double FEEDER_VOLTS = 5.0;
    public static int FEEDER_DURATION_MILLIS = 125;

    public static double INTAKE_VOLTS = 9.0;

    public static double SHOOTER_COUNTS_PER_REV = 28.0;
    public static int SHOOTER_RPM_CALCULATION_SAMPLE_MILLIS = 150;

    // How close actual RPM must be within target RPM to be considered good
    public static double SHOOTER_RPM_TOLERANCE = 250;
}
