package org.firstinspires.ftc.teamcode.teleop.mrb;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.teamcode.utils.PowerVoltageCalculator;

import java.util.ArrayList;
import java.util.List;

@TeleOp(name = "Mr B: Bench Test", group = "Robot")
//@Disabled
public class BenchTest extends OpMode {
    // Motors
    String[] motorNames;
    int motorIndex = 0;
    ElapsedTime velocityTime = new ElapsedTime();
    int lastPosition = Integer.MAX_VALUE;
    double velocity = 0.0;

    PowerVoltageCalculator powerVoltageCalculator;

    // Servos with fixed positions
    private String[] servoNames;
    private int servoIndex = 0;
    private double servoPosition = 0;

    // Continuous rotation servos
    private String[] crServoNames;
    private int crServoIndex = 0;

    enum Mode {DcMotor, Servo, CRServo, Battery, Imu}

    ;
    private Mode mode = Mode.DcMotor;

    private IMU imu;

    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        powerVoltageCalculator = new PowerVoltageCalculator(hardwareMap.voltageSensor);
        motorNames = hardwareMap.getAllNames(DcMotor.class).toArray(new String[0]);
        servoNames = hardwareMap.getAllNames(Servo.class).toArray(new String[0]);
        crServoNames = hardwareMap.getAllNames(CRServo.class).toArray(new String[0]);

        imu = hardwareMap.get(IMU.class, BenchTestConfig.IMU_NAME);
        RevHubOrientationOnRobot orientationOnRobot = new RevHubOrientationOnRobot(
                BenchTestConfig.LOGO_FACING_DIR,
                BenchTestConfig.USB_FACING_DIR
        );
        imu.initialize(new IMU.Parameters(orientationOnRobot));

        List<String> deviceClasses = new ArrayList<>();
        for (HardwareMap.DeviceMapping<? extends HardwareDevice> deviceMapping : hardwareMap.allDeviceMappings) {
            deviceClasses.add(deviceMapping.getDeviceTypeClass().getSimpleName() + " " + deviceMapping.size());
        }

        telemetry.addData("X", "Select Device Type");
        telemetry.addData("B", "Select Device");
        telemetry.addData("A", "Enable left stick Y");
        telemetry.addData("Left Y-stick", "Adjust power when A held down");
        telemetry.addData("D-Pad Up", "+Fixed positive power when held");
        telemetry.addData("D-Pad Down", "-Fixed power when held");
        telemetry.addData("Device Classes", deviceClasses.toString());
    }

    @Override
    public void start() {
        motorIndex = 0;
        lastPosition = Integer.MAX_VALUE;
    }

    /**
     * Called repeatedly until teleop is over.
     */
    @Override
    public void loop() {
        powerVoltageCalculator.update();

        if (gamepad1.xWasPressed()) {
            mode = Mode.values()[(mode.ordinal() + 1) % Mode.values().length];
        }
        telemetry.addData("Mode", mode);

        if (mode == Mode.DcMotor && motorNames.length > 0) {
            updateMotor();
        } else if (mode == Mode.Servo && servoNames.length > 0) {
            updateServo();
        } else if (mode == Mode.CRServo && crServoNames.length > 0) {
            updateCRServo();
        } else if (mode == Mode.Battery) {
            powerVoltageCalculator.addData(telemetry);
        } else if (mode == Mode.Imu) {
            showImuInfo();
        }

        telemetry.update();
    }

    private void showImuInfo() {
        telemetry.addData("Model", imu.getDeviceName());
        telemetry.addData("Manufacturer", imu.getManufacturer());
        YawPitchRollAngles angles = imu.getRobotYawPitchRollAngles();
        telemetry.addData("Yaw", angles.getYaw(AngleUnit.DEGREES));
        telemetry.addData("Pitch", angles.getPitch(AngleUnit.DEGREES));
        telemetry.addData("Roll", angles.getRoll(AngleUnit.DEGREES));
    }

    private void updateServo() {
        if (gamepad1.bWasPressed()) {
            servoIndex = (servoIndex + 1) % servoNames.length;
            Servo servo = hardwareMap.get(Servo.class, servoNames[servoIndex]);
            // Save initial position as point to restore to
            servoPosition = servo.getPosition();
        }
        Servo servo = hardwareMap.get(Servo.class, servoNames[servoIndex]);
        double seekPosition = gamepad1.a ? ((gamepad1.left_stick_x + 2) / 2.0) : servoPosition;
        seekPosition = gamepad1.dpad_down ? BenchTestConfig.DPAD_DOWN_SERVO_POSITION : seekPosition;
        seekPosition = gamepad1.dpad_up ? BenchTestConfig.DPAD_UP_SERVO_POSITION : seekPosition;
        servo.setPosition(seekPosition);

        telemetry.addData("Servo", servoNames[servoIndex]);
        telemetry.addData("Seek Position", seekPosition);
        telemetry.addData("Position", servo.getPosition());
    }

    private void updateCRServo() {
        if (gamepad1.bWasPressed()) {
            crServoIndex = (crServoIndex + 1) % crServoNames.length;
        }
        CRServo servo = hardwareMap.get(CRServo.class, crServoNames[crServoIndex]);

        // While holding B down, use Y axis to adjust power
        double volts = gamepad1.a ? (-gamepad1.left_stick_y * 13.0) : 0.0;
        // Or, dpad to set fixed positive or negative speed
        volts = gamepad1.dpad_up ? BenchTestConfig.DPAD_UP_MOTOR_VOLTS : volts;
        volts = gamepad1.dpad_down ? BenchTestConfig.DPAD_DOWN_MOTOR_VOLTS : volts;

        servo.setPower(powerVoltageCalculator.voltageToPower(volts, true));

        telemetry.addData("Servo", crServoNames[crServoIndex]);
        telemetry.addData("Connection", servo.getConnectionInfo());
        telemetry.addData("Volts", volts);
        telemetry.addData("Power", "%.3f", servo.getPower());
    }

    private void updateMotor() {
        boolean resetEncoder = false;

        if (gamepad1.bWasPressed()) {
            setVolts(0.0, false);
            motorIndex = (motorIndex + 1) % motorNames.length;
            resetEncoder = true;
        }

        // While holding B down, use Y axis to adjust power
        double volts = gamepad1.a ? (-gamepad1.left_stick_y * 13.0) : 0.0;
        // Or, dpad to set fixed positive or negative speed
        volts = gamepad1.dpad_up ? BenchTestConfig.DPAD_UP_MOTOR_VOLTS : volts;
        volts = gamepad1.dpad_down ? BenchTestConfig.DPAD_DOWN_MOTOR_VOLTS : volts;

        DcMotor motor = setVolts(volts, resetEncoder);

        int currentPosition = motor.getCurrentPosition();
        double secondsElapsed = velocityTime.seconds();
        // Update 10 times a second
        if (secondsElapsed > 0.1) {
            velocity = ((double) (currentPosition - lastPosition) / secondsElapsed);
            velocityTime.reset();
            lastPosition = currentPosition;
        }

        telemetry.addData("Motor", motorNames[motorIndex]);
        telemetry.addData("Connection", motor.getConnectionInfo());
        telemetry.addData("Volts", volts);
        telemetry.addData("Power", "%.3f", motor.getPower());
        telemetry.addData("Position", currentPosition);
        telemetry.addData("Pos/Sec", "%.3f", velocity);
        telemetry.addData("Pos/Rot", "%.3f", BenchTestConfig.COUNTS_PER_ROTATION);
        telemetry.addData("RPM", "%.3f", velocity * 60.0 / BenchTestConfig.COUNTS_PER_ROTATION);
    }

    private DcMotor setVolts(double volts, boolean resetEncoder) {
        DcMotor motor = hardwareMap.get(DcMotor.class, motorNames[motorIndex]);
        if (resetEncoder) {
            motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            lastPosition = motor.getCurrentPosition();
            velocityTime.reset();
            velocity = 0;
        }
        motor.setPower(powerVoltageCalculator.voltageToPower(volts, true));
        return motor;
    }

}
