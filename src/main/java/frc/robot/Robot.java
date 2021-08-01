/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import static frc.robot.Preferences.getPreferences;
import static frc.robot.drivetrain.Drivetrain.getDrivetrain;
import static frc.robot.shooter.Shooter.getShooter;
import static frc.robot.intake.Intake.getIntake;
import static frc.robot.magazine.Magazine.getMagazine;
import static frc.robot.spacer.Spacer.getSpacer;
import static frc.robot.indexer.Indexer.getIndexer;
import static frc.robot.oi.OI.getOI;
import static frc.robot.status.Status.getStatus;

import com.team2363.logger.HelixEvents;
import com.team2363.logger.HelixLogger;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.drivetrain.Drivetrain;
// import frc.robot.command_groups.AutoRoutines;
// import frc.robot.command_groups.AutoRoutines.AutoMode;
import frc.robot.drivetrain.StateEstimator;
import frc.robot.drivetrain.commands.PathFollower;
// import frc.robot.drivetrain.commands.AutoVisionDriving;
// import frc.robot.drivetrain.commands.ManualVisionDriving;
// import frc.robot.drivetrain.commands.SetFrontCameraAlignment;
import frc.robot.limelight.Limelight;
import frc.robot.magazine.Magazine;
import frc.robot.oi.OI;

import frc.paths.*;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  Command autonomousCommand;
  private final Compressor compressor = new Compressor();
  private StateEstimator estimator;

  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    SmartDashboard.putString("Robot Name", getPreferences().getRobotName());
    
    initializeSubsystems();
    getDrivetrain().resetHeading();
    // SmartDashboard.putData(new SetFrontCameraAlignment());
  }

  private void initializeSubsystems() {
    getStatus();
    getOI();
    getDrivetrain();
    getIntake();
    getSpacer();
    getMagazine();
    getShooter();
    getIndexer();

    getStatus().resetBoot();
  }

  /** 
   *  This function is called every robot packet, no matter the mode. Use
   * this for items like diagnostics that you want ran during disabled,
   * autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
  }

  /**
   * This function is called once each time the robot enters Disabled mode.
   * You can use it to reset any subsystem information you want to clear when
   * the robot is disabled.
   */
  @Override
  public void disabledInit() {
    OI.getOI().setControllerRumble(false);
  }

  @Override
  public void disabledPeriodic() {
    Scheduler.getInstance().run();
    SmartDashboard.putNumber("Current Heading", getDrivetrain().getHeading());

    SmartDashboard.putBoolean("Ball At Spacer", Magazine.getMagazine().ballAtSpacer());
    SmartDashboard.putBoolean("Ball At Shooter", Magazine.getMagazine().ballAtShooter());

    // SmartDashboard.putString("AUTO SWITCH:", AutoRoutines.getSelectedAutoMode().toString());

  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable
   * chooser code works with the Java SmartDashboard. If you prefer the
   * LabVIEW Dashboard, remove all of the chooser code and uncomment the
   * getString code to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional commands to the
   * chooser code above (like the commented example) or additional comparisons
   * to the switch structure below with additional strings & commands.
   */
  @Override
  public void autonomousInit() {
    getStatus().resetAuto();

    getDrivetrain().resetHeading();

    // AutoMode mode;
    
    // GET THE AUTO MODE FROM THE HARDWARE SWITCH
    // mode = AutoRoutines.getSelectedAutoMode(); 

    // HARDCODE THE AUTO MODE FOR TESTING PURPOSES, BY-PASSING THE SWITCH
    // mode = AutoMode.TEST_RIGHT_TURN;
    // mode = AutoMode.TEST_3FEET_FORWARD;
    // mode = AutoMode.BASELINE_AUTO;
    // mode = AutoMode.COLLECT_REND_BALLS;
    // mode = AutoMode.TRENCH_AUTO;     
    // mode = AutoMode.LAYUP;
    // mode = AutoMode.NONE;

    // autonomousCommand = new PathFollower("SandableLacquers");
    autonomousCommand = new PathFollower(new Traj());

    if (autonomousCommand != null) {
      autonomousCommand.start();
    }
    Limelight.getLimelight().setDriverMode();
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    Scheduler.getInstance().run();
    HelixLogger.getInstance().saveLogs();
  }

  @Override
  public void teleopInit() {
    getStatus().resetTeleOp();
    
    // This makes sure that the autonomous stops running when
    // teleop starts running. If you want the autonomous to
    // continue until interrupted by another command, remove
    // this line or comment it out.
    if (autonomousCommand != null) {
      autonomousCommand.cancel();
    }

    Limelight.getLimelight().setAimingMode();
    estimator = new StateEstimator(Drivetrain.getDrivetrain().getLeftPosition(),Drivetrain.getDrivetrain().getLeftPosition(),Math.toRadians(Drivetrain.getDrivetrain().getHeading()),0,0,0);
  }

  /**
   * This function is called periodically during operator control.
   */
  boolean firstTime = true;
  @Override
  public void teleopPeriodic() {
    Scheduler.getInstance().run();
    // if (firstTime) {
    //   Scheduler.getInstance().add(new SetBallHandlingCG(BallHandlingState.ADVANCE));
    //   firstTime = false;
    // }

    HelixLogger.getInstance().saveLogs();
    // SmartDashboard.putNumber("Throttle", OI.getOI().getThrottle());
    // SmartDashboard.putNumber("rpm", getDrivetrain().getFrontCamera().calculateRPM());
    // SmartDashboard.putNumber("Distance", getDrivetrain().getFrontCamera().calculateDistanceToTarget());
    double[] pose = estimator.update(Drivetrain.getDrivetrain().getLeftPosition(),Drivetrain.getDrivetrain().getLeftPosition(),Math.toRadians(Drivetrain.getDrivetrain().getHeading()));
    SmartDashboard.putNumber("x", pose[0]);
    SmartDashboard.putNumber("y", pose[1]);
    SmartDashboard.putNumber("theta", pose[2]);
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
    
  }
}