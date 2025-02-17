// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import org.photonvision.PhotonCamera;
import org.photonvision.common.hardware.VisionLEDMode;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.HttpCamera;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.CameraConstants;

public class Limelight extends SubsystemBase {
  // Entries on camera server
  private final NetworkTableEntry pipeline = CameraConstants.pipeline;
  private final NetworkTableEntry targetPixelsX = CameraConstants.targetPixelsX;
  private final NetworkTable table = CameraConstants.table;
  private final HttpCamera feed;
  private final PhotonCamera m_camera;

  private PhotonPipelineResult result;

  private final ShuffleboardLayout cameraValues = CameraConstants.CAMERA_VALUES;

  public Limelight() {
    feed = new HttpCamera("photonvision", "http://10.8.10.11:5800/");
    CameraServer.startAutomaticCapture(feed);

    m_camera = new PhotonCamera("photonvision");

    setMode("AprilTag");
    this.table.getEntry("ledMode").setInteger(1);
    m_camera.setLED(VisionLEDMode.kOn);
    // m_camera.setPipelineIndex(0);

    shuffleInit();
  }

  public double getTargetPixelsX() {
    return this.targetPixelsX.getDouble(-1);
  }

  public PhotonTrackedTarget getBestTarget() {
    return m_camera.getLatestResult().getBestTarget();
  }

  public void setMode(String pipeline) {
    switch (pipeline) {
      case "AprilTag":
        // long range 0, short 1
        this.pipeline.setInteger(0);
        this.table.getEntry("ledMode").setInteger(0);
        // m_camera.setLED(VisionLEDMode.kOff);
        break;
      case "Reflective Tape":
        this.pipeline.setInteger(3);
        this.table.getEntry("ledMode").setInteger(1);
        // m_camera.setLED(VisionLEDMode.kOn);
        break;
    }
  }

  public void shuffleInit() {
    this.cameraValues.addBoolean("Is Valid?", () -> result.hasTargets());
    this.cameraValues.addCamera("Live View", "photonvision", "http://10.8.10.11:5800");
  }

  @Override
  public void periodic() {
    result = m_camera.getLatestResult();
  }
}
