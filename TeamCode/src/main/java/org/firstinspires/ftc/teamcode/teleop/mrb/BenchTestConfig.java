package org.firstinspires.ftc.teamcode.teleop.mrb;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;

@Config
public class BenchTestConfig {
    public static String IMU_NAME = "imu";
    public static RevHubOrientationOnRobot.LogoFacingDirection LOGO_FACING_DIR = RevHubOrientationOnRobot.LogoFacingDirection.RIGHT;
    public static RevHubOrientationOnRobot.UsbFacingDirection USB_FACING_DIR = RevHubOrientationOnRobot.UsbFacingDirection.UP;

    public static double DPAD_UP_MOTOR_VOLTS = 6.0;
    public static double DPAD_DOWN_MOTOR_VOLTS = -6.0;
    public static double DPAD_UP_SERVO_POSITION = 0.75;
    public static double DPAD_DOWN_SERVO_POSITION = 0.25;

    public static double COUNTS_PER_ROTATION = 1425.7;
}
