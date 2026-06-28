package org.firstinspires.ftc.teamcode.bench;

import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.SwitchableLight;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@TeleOp(name="Color Sensor Tweak Tool", group="Sensors")
// @Disabled
public class ColorSensorInspector extends OpMode {
    // A gain setting for a sensor can be thought of as a multiplier to increase the sensitivity
    // If a sensor is showing smaller than expected changes when you present it with different
    // environments, then try increasing the gain
    float gain = 1.0f;

    // Amounts to adjust gain setting by
    float[] adjustAmounts = { 10.0f, 1.0f, 0.1f, 0.01f, 0.001f };
    // Selector for magnitude of adjustment (keep in range of [0, adjustAmounts.length - 1]
    int adjustIndex = 1;

    /** The colorSensor field will contain a reference to our color sensor hardware object */
    NormalizedColorSensor colorSensor;

    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        // Get a reference to our sensor object. It's recommended to use NormalizedColorSensor over
        // ColorSensor, because NormalizedColorSensor consistently gives values between 0 and 1, while
        // the values you get from ColorSensor are dependent on the specific sensor you're using.
        colorSensor = hardwareMap.get(NormalizedColorSensor.class, "colorSensor");
        // If possible, turn the light on in the beginning (it might already be on anyway,
        // we just make sure it is if we can).
        if (colorSensor instanceof SwitchableLight) {
            ((SwitchableLight)colorSensor).enableLight(true);
        }

        // Send telemetry message to signify robot waiting;
        telemetry.addData("Model",  colorSensor.getDeviceName() + " v" + colorSensor.getVersion());
        telemetry.addData("Manufacturer", colorSensor.getManufacturer().name());
        telemetry.addData("Connection", colorSensor.getConnectionInfo());
    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit START
     */
    @Override
    public void init_loop() {
        showColorSensorValues();
    }

    /*
     * Code to run ONCE when the driver hits START
     */
    @Override
    public void start() {
    }

    /*
     * Code to run REPEATEDLY after the driver hits START but before they hit STOP
     */
    @Override
    public void loop() {
        adjustSensorConfiguration(gamepad1);

        showColorSensorValues();
    }

    private void adjustSensorConfiguration(Gamepad gamepad1) {
        if (gamepad1.aWasPressed()) {
            colorSensor.setGain(colorSensor.getGain() + adjustAmounts[adjustIndex]);
        }

        if (gamepad1.bWasPressed()) {
            colorSensor.setGain(colorSensor.getGain() + adjustAmounts[adjustIndex]);
        }

        if (gamepad1.yWasPressed()) {
            adjustIndex = (adjustIndex + 1) % adjustAmounts.length;
        }

        // Toggle light on/off with x button
        if (gamepad1.xWasPressed()) {
            if (colorSensor instanceof SwitchableLight) {
                SwitchableLight light = (SwitchableLight)colorSensor;
                light.enableLight(!light.isLightOn());
                telemetry.addData("Light", light.isLightOn());
            }
        }

        // Display current gain setting and selected adjustment amount
        telemetry.addData("Gain", gain);
        telemetry.addData("Adjust", adjustAmounts[adjustIndex]);
    }


    private void showColorSensorValues() {
        /* Use telemetry to display feedback on the driver station. We show the red, green, and blue
         * normalized values from the sensor (in the range of 0 to 1), as well as the equivalent
         * HSV (hue, saturation and value) values. See http://web.archive.org/web/20190311170843/https://infohost.nmt.edu/tcc/help/pubs/colortheory/web/hsv.html
         * for an explanation of HSV color. */
        // Get the normalized colors from the sensor
        NormalizedRGBA colors = colorSensor.getNormalizedColors();

        // Convert RGB colors to HSV colors so we can show both

        // The first element (0) will contain the
        // hue, the second element (1) will contain the saturation, and the third element (2) will
        // contain the value. See http://web.archive.org/web/20190311170843/https://infohost.nmt.edu/tcc/help/pubs/colortheory/web/hsv.html
        // for an explanation of HSV color.
        final float[] hsvValues = new float[3];
        Color.colorToHSV(colors.toColor(), hsvValues);

        telemetry.addLine()
                .addData("Red", "%.3f", colors.red)
                .addData("Green", "%.3f", colors.green)
                .addData("Blue", "%.3f", colors.blue);
        telemetry.addLine()
                .addData("Hue", "%.3f", hsvValues[0])
                .addData("Saturation", "%.3f", hsvValues[1])
                .addData("Value", "%.3f", hsvValues[2]);
        telemetry.addData("Alpha", "%.3f", colors.alpha);

        /* If this color sensor also has a distance sensor, display the measured distance.
         * Note that the reported distance is only useful at very close range, and is impacted by
         * ambient light and surface reflectivity. */
        if (colorSensor instanceof DistanceSensor) {
            telemetry.addData("Distance (cm)", "%.3f", ((DistanceSensor) colorSensor).getDistance(DistanceUnit.CM));
        }
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
    }
}
