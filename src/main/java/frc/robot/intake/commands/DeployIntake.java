
package frc.robot.intake.commands;

import com.team2363.logger.HelixEvents;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.intake.Intake;

public class DeployIntake extends Command {

  private Intake myIntake = null;
  private boolean runRoller = false;

  public DeployIntake(boolean runRoller) {
    // Use requires() here to declare subsystem dependencies
    // eg. requires(chassis);
    myIntake = Intake.getIntake();
    requires(Intake.getIntake());
    this.runRoller = runRoller;
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    HelixEvents.getInstance().addEvent("INTAKE", "Starting DeployIntake");

    myIntake.extend();
    if (runRoller) {
      myIntake.rollerIn();
    } else {
      myIntake.rollerOff();
    }
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {

  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    return false; // Never finish
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
    HelixEvents.getInstance().addEvent("INTAKE", "Ending DeployIntake");
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
    // Default Command will retract and stop Intake
  }
}