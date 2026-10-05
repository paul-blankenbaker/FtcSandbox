


















package org.firstinspires.ftc.teamcode.teleop.muhammad;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.VoltageSensor;

    public class Intake {



    public enum State {
        STOP,
        UP
    }
public State target_state, last_state;

    public DcMotorEx Intake;

public Intake(HardwareMap hardwareMap){
    Intake = hardwareMap.get(DcMotorEx.class, "Intake");
    Intake.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);

    target_state = State.STOP;
    last_state = State.STOP;
}

public void stop(){target_state = State.STOP;}
public void up(){target_state = State.UP;}
public void update(){
    switch(target_state){
        case STOP:
        Intake.setPower(0);
        break;

case UP:
    Intake.setPower(1);
    break;
    }

    last_state = target_state;
}

}
