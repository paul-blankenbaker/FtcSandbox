package org.firstinspires.ftc.teamcode.utils;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.VoltageSensor;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * Calculator to help determine power values to produce specific voltage outputs.
 *
 * <ul>
 *     <li>Construct a single instance</li>
 *     <li>Call update() method at start of loop to read current battery voltage</li>
 *     <li>Use the voltsToPower() method to calculate speed controller power
 *     values to produce desired voltage to motor</li>
 * </ul>
 */
public class PowerVoltageCalculator {
    // Sensor(s) used to determine power supply (battery) voltage
    private final HardwareMap.DeviceMapping<VoltageSensor> voltageSensor;

    // Assumed value if we are unable to read supply voltage
    private static final double VOLTS_ASSUMED_IF_SENSOR_FAILS = 12.0;

    // Last supply voltage read
    private double batteryVoltage = VOLTS_ASSUMED_IF_SENSOR_FAILS;

    // Will be true until we start getting good readings of available volts
    private boolean batterySensorBroken = true;

    // Incremented whenever someone requests more volts than the power supply has
    private int brownOuts;

    public PowerVoltageCalculator(HardwareMap.DeviceMapping<VoltageSensor> voltageSensor) {
        this.voltageSensor = voltageSensor;
        update();
    }

    /**
     * Computes the ratio of a desired output voltage over the current battery voltage.
     * Useful for compensating for battery drop in motor power calculations.
     *
     * @param desiredVoltage The target voltage (e.g. 5.0)
     * @param updateBrownouts If this is true and you request more volts than available
     *                        we will increment the diagnostic brownout counter. For
     *                        telop driving you probably don't care. For shooter wheels and
     *                        aton you probably do.
     * @return Ratio of desired voltage to current battery voltage clipped to range of [-1.0, +1.0]
     */
    public double voltageToPower(double desiredVoltage, boolean updateBrownouts) {
        double power = desiredVoltage / batteryVoltage;
        if (power > 1.0) {
            power = 1.0;
            if (updateBrownouts) {
                brownOuts++;
            }
        } else if (power < -1.0) {
            power = -1.0;
            if (updateBrownouts) {
                brownOuts++;
            }
        }
        return power;
    }

    /**
     * Computes the ratio of a desired output voltage over the current battery voltage (no brownout tracking).
     *
     * @param desiredVoltage The target voltage (e.g. 5.0)
     * @return Ratio of desired voltage to current battery voltage clipped to range of [-1.0, +1.0]
     */
    public double voltageToPower(double desiredVoltage) {
        return voltageToPower(desiredVoltage, false);
    }

    /**
     * Computes the ratio of a desired output voltage over the current battery voltage.
     * Useful for compensating for battery drop in motor power calculations.
     *
     * @param power The power value.
     * @return Expected voltage out if power applied to speed controller
     */
    public double powerToVoltage(double power) {
        return power * batteryVoltage;
    }

    /**
     * Returns battery voltage from last update.
     *
     * @return Last reading of battery voltage in volts (like 12.9).
     */
    private double getVoltage() {
        return batteryVoltage;
    }

    /**
     * Checks all devices capable of measuring battery supply power and selects
     * value from first device reporting non-zero value.
     */
    public void update() {
        for (VoltageSensor sensor : voltageSensor) {
            double v = sensor.getVoltage();
            if (v > 0) {
                batteryVoltage = v;
                batterySensorBroken = false;
                return;
            }
        }

        // We failed to determine the battery power, indicate sensor is broken
        // and we'll continue using the last value
        batterySensorBroken = true;
    }

    /**
     * Add diagnostic data about battery output.
     *
     * @param telemetry Where to add values.
     */
    public void addData(Telemetry telemetry) {
        telemetry.addData("Volts Available", batterySensorBroken ? 0.0 : getVoltage());
        telemetry.addData("Brownouts", brownOuts);
    }
}
