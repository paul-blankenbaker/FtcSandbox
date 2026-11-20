package org.firstinspires.ftc.teamcode.robots;

import com.acmerobotics.dashboard.config.Config;

@Config
public class DecoderConfig {
    public static double SHOOTER_COUNTS_PER_REV = 28.0;
    public static double SHOOTER_KF_VOLTS = 5.0;

    public static double SHOOTER_TARGET_RPM = 360.0;

    public static double SHOOTER_KF = SHOOTER_KF_VOLTS / SHOOTER_TARGET_RPM;
    public static double SHOOTER_KP = 0.0;
    public static double SHOOTER_KD = 0.0;
}
