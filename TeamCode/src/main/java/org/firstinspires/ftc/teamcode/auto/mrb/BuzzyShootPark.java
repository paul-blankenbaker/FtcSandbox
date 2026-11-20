package org.firstinspires.ftc.teamcode.auto.mrb;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.commands.CommandTree;

@Autonomous(name = "Mr B: Buzzy Shoot/Park", group = "Robot")
//@Disabled
public class BuzzyShootPark extends BuzzyAutonBase {

    @Override
    protected CommandTree createAutonStateTree() {
        CommandTree shoot = new CommandTree(shootBallSequence());
        CommandTree driveUnderHive = driveStraightTimed(BuzzyAutonConfig.shootParkDriveAwayFromWallSeconds);
        CommandTree spinToLoadingZone = driveRotateTimed(BuzzyAutonConfig.shootParkSpinToLoadingZoneSeconds);
        CommandTree driveToLoadingZone = driveRotateTimed(BuzzyAutonConfig.shootParkDriveToLoadingZoneSeconds);

        // After shooting, drive under hive, turn towards loading zone and drive to loading zone
        shoot.setNextState(driveUnderHive);
        shoot.setFailState(driveToLoadingZone);
        driveUnderHive.setNextState(spinToLoadingZone);
        spinToLoadingZone.setNextState(driveToLoadingZone);

        return shoot;
    }
}
