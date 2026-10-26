package org.firstinspires.ftc.teamcode.auto.mrb;
import com.acmerobotics.dashboard.config.Config;

/**
 * Configuration for the DriveByVolts OpMode - you can adjust values using web interface on control hub.
 *
 * <ul>
 *     <li>Used a laptop and connect to robot control hub WIFI (phone will work - but tiny screen/keyboard isn't what I'd call fun)</li>
 *     <li>Open http://192.168.43.1:8080/dash</li>
 * </ul>
 */
@Config
public class DriveByVoltsConfig {
    // Set to true when shooting into hive, then driving to loading zone, set to false when simple drive
    public static boolean shootThenPark = true;
    public static double initialDelaySeconds = 1.0;
    public static double driveStraightVolts = 6.0;
    public static double driveSpinVolts = 7.0;
    public static double driveAwayFromWallSeconds = 0.75;
    public static double turnTowardsLoadingZoneSeconds = 0.75;
    public static double driveToLoadingZoneSeconds = 2.0;
    public static double driveSimpleSeconds = 0.75;

    public static boolean showTiming = false;
    public static boolean enableGraphing = false;

    public static int forceLoopTimingMillis = 0;
}
