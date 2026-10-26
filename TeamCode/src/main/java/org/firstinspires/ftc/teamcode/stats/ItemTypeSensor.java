package org.firstinspires.ftc.teamcode.stats;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

import android.graphics.Color;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

public class ItemTypeSensor {
    public enum ItemColor {
        NONE, // There is no ball
        POLLEN, // Ball is pollen
        RED_NECTAR, // Ball is red nectar
        BLUE_NECTAR // Ball is blue nectar
    }

    private NormalizedColorSensor colorSensor;

    public ItemTypeSensor(HardwareMap hardwareMap, String colorSensorName) {
        colorSensor = hardwareMap.get(NormalizedColorSensor.class, "colorSensor");
        colorSensor.setGain(0.5F);
    }

    public ItemColor itemColor() {
        double hue = readHue(colorSensor);
        if (70 < hue && hue < 120) {
            return ItemColor.POLLEN;
        } else if (200 < hue && hue < 250) {
            return ItemColor.BLUE_NECTAR;
        } else if (10 < hue && hue < 50) {
            return ItemColor.RED_NECTAR;
        } else {
            return ItemColor.NONE;
        }
    }

    public static double readHue(NormalizedColorSensor colorSensor) {
        NormalizedRGBA colors = colorSensor.getNormalizedColors();
        final float[] hsvValues = new float[3];
        Color.colorToHSV(colors.toColor(), hsvValues);
        return hsvValues[0];
    }
    public double readHue () {
        return readHue(colorSensor);
    }
}

