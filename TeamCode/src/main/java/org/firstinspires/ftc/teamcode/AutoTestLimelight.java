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
                        status.getTemp(), status.getCpu(), (int)status.getFps());
                telemetry.addData("Pipeline", "Index: %d, Type: %s",
                        status.getPipelineIndex(), status.getPipelineType());
            }

            // Get the latest result from the Limelight
            LLResult result = limelight.getLatestResult();

            if (result != null && result.isValid()) {
                // Access fiducial (AprilTag) results
                List<LLResultTypes.FiducialResult> fiducialResults = result.getFiducialResults();

                if (!fiducialResults.isEmpty()) {
                    telemetry.addData("AprilTag", "Detected %d tag(s)", fiducialResults.size());

                    for (LLResultTypes.FiducialResult fr : fiducialResults) {
                        int tagId = fr.getFiducialId();
                        double x = fr.getTargetXDegrees();
                        double y = fr.getTargetYDegrees();
                        
                        telemetry.addData("Tag ID", tagId);
                        telemetry.addData("Tag Position", "X: %.2f, Y: %.2f", x, y);

                        // --- NOTIFY THE CODE ---
                        handleAprilTagDetection(tagId, x, y);
                    }

                    // Also get the robot's position on the field if AprilTags are configured in the Limelight web UI
                    telemetry.addData("Botpose", result.getBotpose().toString());
                } else {
                    telemetry.addData("AprilTag", "No tags detected (tx: %.2f)", result.getTx());
                }
            } else {
                if (result == null) {
                    telemetry.addData("Limelight", "Result is null (Check if start() was called and LL is connected)");
                } else {
                    telemetry.addData("Limelight", "Result invalid (v=0). Check if Limelight has a target.");
                    telemetry.addData("Staleness", result.getStaleness() + " ms");
                    telemetry.addData("Raw Target Info", "tx: %.2f, ty: %.2f, ta: %.2f", 
                            result.getTx(), result.getTy(), result.getTa());
                    telemetry.addData("Pipeline Index", result.getPipelineIndex());
                }
            }

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
    private void handleAprilTagDetection(int id, double x, double y) {
        // Example notification: Log to telemetry or adjust robot state
        // telemetry.addData("Detection Log", "Tag %d seen at %.2f, %.2f", id, x, y);
        
        // You could also trigger a callback or update a shared state object here
    }
}
