// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import java.util.List;

import javax.management.loading.PrivateClassLoader;

import org.photonvision.PhotonCamera;
import org.photonvision.PhotonUtils;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.HttpCamera;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class Limelight extends SubsystemBase {
  public NetworkTableEntry camMode = Constants.camMode;

  public NetworkTableEntry pipeline = Constants.pipeline;
  public NetworkTableEntry stream = Constants.stream;

  private PhotonPipelineResult result;

  private boolean hasTargets;
  private List<PhotonTrackedTarget> targets;
  private PhotonTrackedTarget target;

  private double yaw;
  private double pitch;
  private double area;
  private double poseAmbiguity;

  private int targetID;

  private Transform3d bestCameraToTarget;
  private Transform3d alternateCameraToTarget;

  private double rangeToTarget; 
  private double distanceToTarget;

  private Translation2d translation;

  private Pose3d robotPose;
  
  HttpCamera feed;
  // AprilTagFieldLayout aprilTagFieldLayout = new
  // AprilTagFieldLayout(AprilTagFieldLayout.loadFromResource
  // (AprilTagFields.k2022RapidReact.m_resourceFile));

  /** Creates a new Limelight. */

  PhotonCamera m_camera;
  DrivetrainSubsystem m_drivetrain;

  public Limelight() {
    feed = new HttpCamera("photonvision", "http://10.8.10.11:5800/");
    CameraServer.startAutomaticCapture(feed);

    m_camera = new PhotonCamera("photonvision");

    camMode.setBoolean(false);

    setMode(2);
  }

  public void shuffleUpdate()
  {
    result = m_camera.getLatestResult();
    target = result.getBestTarget();
  }

  public void aprilTagData() {
    hasTargets = result.hasTargets();

    targets = result.getTargets();

    target = result.getBestTarget();

    yaw = target.getYaw();
    pitch = target.getPitch();
    area = target.getArea();

    targetID = target.getFiducialId();
    poseAmbiguity = target.getPoseAmbiguity();
    bestCameraToTarget = target.getBestCameraToTarget();
    alternateCameraToTarget = target.getAlternateCameraToTarget();

    if(result.hasTargets()){
      PhotonUtils.calculateDistanceToTargetMeters(
              Constants.CAMERA_HEIGHT_METERS,
              Constants.TEST_TARGET_HEIGHT_METERS, 
              Constants.CAMERA_PITCH_RADIANS, 
              Units.degreesToRadians(result.getBestTarget().getPitch()));
    }
     
    translation = PhotonUtils.estimateCameraToTargetTranslation(
                  Constants.TEST_TARGET_HEIGHT_METERS, 
                  Rotation2d.fromDegrees(-target.getYaw()));

    // robotPose = PhotonUtils.estimateFieldToRobotAprilTag(
    //                         null,
    //                         null,
    //                         bestCameraToTarget); 

  }


  private void setMode(int mode)
  {
    switch(mode){
      case 1:
        pipeline.setNumber(1);
        break;
      case 2:
        pipeline.setNumber(2);
        break;
      case 3:
        pipeline.setNumber(3);
      break;
    }
  }
  
  public   void processedPipeline(){
    setMode(1); //button 7
  }

  public void aprilTagPipeline(){
    setMode(2); //button 8
  }

  public void limeLightPipeline(){
    setMode(3); //button 9 
  }


  @Override
  public void periodic() {
    // shuffleUpdate();
  }
}
