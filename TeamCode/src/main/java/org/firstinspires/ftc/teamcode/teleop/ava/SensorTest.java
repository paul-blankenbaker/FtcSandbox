package org.firstinspires.ftc.teamcode.teleop.ava;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.stats.ItemTypeSensor;

@TeleOp(name="SensorTest", group="Sensor")

public class SensorTest extends OpMode {


    private ItemTypeSensor colorSensor;

    @Override
    public void init() {
        colorSensor = new ItemTypeSensor(hardwareMap, "colorSensor");
    }

    @Override
    public void loop() {

        telemetry.addData("itemColor", colorSensor.itemColor());
        telemetry.addData("hue", colorSensor.readHue());

































































































    }
}
