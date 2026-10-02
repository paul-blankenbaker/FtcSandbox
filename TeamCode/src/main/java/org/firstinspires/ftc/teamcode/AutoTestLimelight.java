package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import java.util.List;

@Autonomous(name = "Autonomous Limelight AprilTag", group = "Autonomous")
public class AutoTestLimelight extends LinearOpMode {

    private Limelight3A limelight;

    @Override
    public void runOpMode() throws InterruptedException {
        // Initialize the Limelight sensor
        limelight = hardwareMap.get(Limelight3A.class, "limelight");

        // Set the pipeline to the one configured for AprilTags (usually 0 or 1 depending on your Limelight settings)
        limelight.pipelineSwitch(8);

        // Start polling for data
        limelight.start();

        telemetry.addData("Status", "Initialized. Waiting for start...");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            // Get Limelight status
            com.qualcomm.hardware.limelightvision.LLStatus status = limelight.getStatus();
            if (status != null) {
                telemetry.addData("Limelight Status", "Temp: %.1fC, CPU: %.1f%%, FPS: %d",
                        status.getTemp(), status.getCpu(), (int) status.getFps());
                telemetry.addData("Pipeline", "Index: %d, Type: %s",
                        status.getPipelineIndex(), status.getPipelineType());
            }

            // Get the latest result from the Limelight
            LLResult result = limelight.getLatestResult();

            if (result != null && result.isValid()) {
                // Access fiducial (AprilTag) results
                List<LLResultTypes.FiducialResult> fiducialResults = result.getFiducialResults();

                if (!fiducialResults.isEmpty()) {
                    //telemetry.addData("AprilTag", "Detected %d tag(s)", fiducialResults.size());

                    for (LLResultTypes.FiducialResult fr : fiducialResults) {

                        // --- NOTIFY THE CODE ---
                        int onTargetCount = handleAprilTagDetection(fr, 3.56, -9.39, 5);
                       // telemetry.addData("On Target Cnt", onTargetCount);`
                    }

                    // Also get the robot's position on the field if AprilTags are configured in the Limelight web UI
                    //telemetry.addData("Botpose", result.getBotpose().toString());
                } else {
                 //   telemetry.addData("AprilTag", "No tags detected (tx: %.2f)", result.getTx());
                }
            }
//            } else {
//                if (result == null) {
//                    telemetry.addData("Limelight", "Result is null (Check if start() was called and LL is connected)");
//                } else {
//                    telemetry.addData("Limelight", "Result invalid (v=0). Check if Limelight has a target.");
//                    telemetry.addData("Staleness", result.getStaleness() + " ms");
//                    telemetry.addData("Raw Target Info", "tx: %.2f, ty: %.2f, ta: %.2f",
//                            result.getTx(), result.getTy(), result.getTa());
//                    telemetry.addData("Pipeline Index", result.getPipelineIndex());
//                }
//            }

            telemetry.update();

            // Small sleep to avoid hogging CPU
            sleep(20);
        }

        // Stop the Limelight when the OpMode is finished
        limelight.stop();
    }

    /**
     * Placeholder method to handle detected AprilTags.
     * This is where you would "notify" the rest of your robot logic.
     */
    private int handleAprilTagDetection(LLResultTypes.FiducialResult fr, double targetX, double targetY, double maxDistance) {
        int id = fr.getFiducialId();
        int onTargetCount = 0;
        if (id == 32) {
            if (isOnTarget("RL", id, fr, targetX, targetY, maxDistance)) {
                onTargetCount++;
            }
        }
        if (id == 35) {
            if (isOnTarget("RR", id, fr, targetX, targetY, maxDistance)) {
                onTargetCount++;
            }
        }
        if (id == 44) {
            if (isOnTarget("BL", id, fr, targetX, targetY, maxDistance)) {
                onTargetCount++;
            }
        }
        if (id == 39) {
            if (isOnTarget("BR", id, fr, targetX, targetY, maxDistance)) {
                onTargetCount++;
            }
        }

        return onTargetCount;

    }

    private boolean isOnTarget(String name, int id, LLResultTypes.FiducialResult fr,
                               double targetX, double targetY, double maxDistance) {
        double x = fr.getTargetXDegrees();
        double y = fr.getTargetYDegrees();

        telemetry.addData("Tag ID", name + ":" + id);
        telemetry.addData("Tag Position", "X: %.2f, Y: %.2f", x, y);

        double Distance2 = (x - targetX) * (x - targetX) + (y - targetY) * (y - targetY);
        double Distance = Math.sqrt(Distance2);
        telemetry.addData("Distance", Distance);
        if (maxDistance > Distance) {
            telemetry.addLine("SHOOT");
            return true;
        } else {
            telemetry.addLine("NO SHOT");
            return false;
        }
    }
}

