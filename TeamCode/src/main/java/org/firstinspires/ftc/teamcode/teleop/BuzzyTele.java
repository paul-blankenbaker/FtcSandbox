package org.firstinspires.ftc.teamcode.teleop;/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.utils.PowerVoltageCalculator;

import java.util.Calendar;

/*
 * This OpMode executes a Tank Drive control TeleOp a direct drive robot
 * The code is structured as an Iterative OpMode
 *
 * In this mode, the left and right joysticks control the left and right motors respectively.
 * Pushing a joystick forward will make the attached motor drive forward.
 * It raises and lowers the claw using the Gamepad Y and A buttons respectively.
 * It also opens and closes the claws slowly using the left and right Bumper buttons.
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this OpMode to the Driver Station OpMode list
 */

@TeleOp(name = "BuzzyTele", group = "Robot")
//@Disabled
public class BuzzyTele extends OpMode {

    /* Declare OpMode members. */
    private DcMotor shooterMotor = null;
    public DcMotor leftBackDrive = null;
    public DcMotor rightBackDrive = null;
    private CRServo Feeder = null;
    private DcMotor intakeMotor;
    private CRServo RSI;
    private CRServo LSI;
    private ElapsedTime shooterDelay = new ElapsedTime();
    private PowerVoltageCalculator powerCalculator;

    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        // Define and Initialize Motors
        powerCalculator = new PowerVoltageCalculator(hardwareMap.voltageSensor);

        leftBackDrive = hardwareMap.get(DcMotor.class, "bl");
        rightBackDrive = hardwareMap.get(DcMotor.class, "br");
        shooterMotor = hardwareMap.get(DcMotor.class, "shooter");
        intakeMotor = hardwareMap.get(DcMotor.class, "intake");
        Feeder = hardwareMap.get(CRServo.class, "shooterFeed");
        RSI = hardwareMap.get(CRServo.class, "rightSideIntake");
        LSI = hardwareMap.get(CRServo.class, "leftSideIntake");


        // To drive forward, most robots need the motor on one side to be reversed, because the axles point in opposite directions.
        // Pushing the left and right sticks forward MUST make robot go forward. So adjust these two lines based on your first test drive.
        // Note: The settings here assume direct drive on left and right wheels.  Gear Reduction or 90 Deg drives may require direction flips
        leftBackDrive.setDirection(DcMotor.Direction.FORWARD);
        rightBackDrive.setDirection(DcMotor.Direction.REVERSE);

        // If there are encoders connected, switch to RUN_USING_ENCODER mode for greater accuracy
        // leftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        // rightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Define and initialize ALL installed servos.

        // Send telemetry message to signify robot waiting;
        telemetry.addData(">", "Robot Ready.  Press START.");    //t

    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit START
     */
    @Override
    public void init_loop() {
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

    public void driveArcade(double forwardPower, double rotateRightPower) {
        double leftPower = forwardPower + rotateRightPower;
        double rightPower = forwardPower - rotateRightPower;
        double scale = Math.max(Math.abs(leftPower), Math.abs(rightPower));
        if (scale > 1) {
            leftPower /= scale;
            rightPower /= scale;
        }
        leftBackDrive.setPower(leftPower);
        rightBackDrive.setPower(rightPower);
    }

    public void driveArcade(Gamepad gamepad) {
        driveArcade(-gamepad.left_stick_y, gamepad.right_stick_x);
    }

    @Override
    public void loop() {
        powerCalculator.update();
        driveArcade(gamepad1);

//        if (gamepad1.leftTriggerWasPressed() && intakeMotor != null) {
//            intakeMotor.setPower(1.0);
//            RSI.setPower(1.0);
//            LSI.setPower(-1.0);
//
//        }
//        if (gamepad1.leftTriggerWasReleased() && intakeMotor != null) {
//            intakeMotor.setPower(0.0);
//            RSI.setPower(0.0);
//            LSI.setPower(0.0);

//                                                                                  `           }

        if (gamepad1.rightTriggerWasPressed()) {
            if (shooterMotor.getPower() == 0) {
                shooterMotor.setPower(powerCalculator.voltageToPower(6.65, true));
            }
            else {
                shooterMotor.setPower(powerCalculator.voltageToPower(0, true));
            }
        }

        if (gamepad1.leftTriggerWasPressed()) {
            if (intakeMotor.getPower() == 0) {
                intakeMotor.setPower(1.0);
                RSI.setPower(1.0);
                LSI.setPower(-1.0);
            }
            else {
                intakeMotor.setPower(0);
                RSI.setPower(0);
                LSI.setPower(0);
            }
        }


        if (gamepad1.leftBumperWasPressed() && shooterMotor != null) {
            Feeder.setPower(powerCalculator.voltageToPower(6.75, true));

            if (shooterDelay.milliseconds() > 3000) {

            }
        }
        if (gamepad1.leftBumperWasReleased() && shooterMotor != null) {
            Feeder.setPower(0.0);

            ;
        }

    // when button y is pressed on the gamepad, the feeder servo should reverse and spin backward

    }


    @Override
    public void stop() {
    }
}

