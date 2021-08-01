/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.magazine.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.magazine.Magazine;

public class ShootOneMagazine extends Command {
  private boolean beamState;
  private boolean lastBeamState;
  private boolean isFinished;

  public ShootOneMagazine() {
    requires(Magazine.getMagazine());
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    lastBeamState = Magazine.getMagazine().ballAtShooter();
    Magazine.getMagazine().setVelocity(600);
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    beamState = Magazine.getMagazine().ballAtShooter();
    isFinished = beamState && !lastBeamState;
    lastBeamState = beamState;
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    return isFinished;
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
  }
}
