/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.spacer.commands;

import edu.wpi.first.wpilibj.command.Command;
import static frc.robot.spacer.Spacer.getSpacer;

public class ShootSpacer extends Command {
  public ShootSpacer() {
    // Use requires() here to declare subsystem dependencies
    // eg. requires(chassis);
    requires(getSpacer());
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    getSpacer().setVelocity(300);
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    return true;
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
