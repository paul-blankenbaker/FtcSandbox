package org.firstinspires.ftc.teamcode.auto.mrb;

import com.acmerobotics.dashboard.config.Config;

/**
 * Configuration for the Buzzy auton parameters - you can adjust values using web interface on control hub.
 *
 * <ul>
 *     <li>Used a laptop and connect to robot control hub WIFI (phone will work - but tiny screen/keyboard isn't what I'd call fun)</li>
 *     <li>Open http://192.168.43.1:8080/dash</li>
 * </ul>
 */
@Config
public class BuzzyAutonConfig {
    public static double driveStraightVolts = 6.0;
    public static double driveSpinVolts = 7.0;
    public static double driveAwayFromWallSecondsLeavePark = 0.75;
    public static double turnTowardsLoadingZoneSecondsLeavePark = 0.75;

    public static double shotAttempts = 6;

    public static double shootParkDriveAwayFromWallSeconds = 2.75;

    public static double shootParkSpinToLoadingZoneSeconds = 1.5;
    public static double shootParkDriveToLoadingZoneSeconds = 1.5;
    public static boolean showTiming = false;
    public static boolean enableGraphing = false;

    public static int forceLoopTimingMillis = 0;
}
