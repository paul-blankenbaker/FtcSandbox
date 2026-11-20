package org.firstinspires.ftc.teamcode.auto.mrb;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.commands.CommandTree;

@Autonomous(name = "Mr B: Buzzy Leave/Park", group = "Robot")
//@Disabled
public class BuzzyLeavePark extends BuzzyAutonBase {

    @Override
    protected CommandTree createAutonStateTree() {
        CommandTree driveAwayFromWall = driveStraightTimed(BuzzyAutonConfig.driveAwayFromWallSecondsLeavePark);
        CommandTree spinToLoadingZone = driveRotateTimed(BuzzyAutonConfig.turnTowardsLoadingZoneSecondsLeavePark);

        // After driving away from wall, spin to face loading zone
        driveAwayFromWall.setNextState(spinToLoadingZone);
        return driveAwayFromWall;
    }

}
