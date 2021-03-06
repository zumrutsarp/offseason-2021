package frc.robot.drivetrain.commands;

import frc.paths.Path;
import com.team2363.controller.PIDController;

import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public abstract class HelixFollower extends Command {
  private Notifier pathNotifier = new Notifier(this::moveToNextSegment);
  private Notifier pidNotifier = new Notifier(this::calculateOutputs);

  // The trajectories to follow for each side
  private double[][] trajectory;
  private boolean mirror;
  private boolean reverse;

  private int currentSegment;
  private boolean isFinished;

  /**
   * This will import the path class based on the name of the path provided
   * 
   * @param path the name of the path to run
   */
  public HelixFollower(Path path) {
    trajectory = path.getPath();
  }

  /**
   * A decorator to flip the left and right direction of the path
   * @return the current PathFollower instance
   */
  public HelixFollower mirror() {
    mirror = true;
    return this;
  }

  /**
   * A decorator to run the path with the robot facing backwards
   * @return the current PathFollower instance
   */
  public HelixFollower reverse() {
    reverse = true;
    return this;
  }

  /**
   * Set the current encoder distance to 0
   */
  public abstract void resetDistance();

  /**
   * Get the PID Controller that will be used to correct the heading error during the path
   * 
   * @return the heading PID Controller
   */
  public abstract PIDController getHeadingController();

  /**
   * Get the PID Controller that will be used to correct the heading error during the path
   * 
   * @return the heading PID Controller
   */
  public abstract PIDController getDistanceController();

  /**
   * Get the current robot distance in the units that your path is stored in. 
   * This will typically be an average of both sides of the drivetrain.
   * 
   * @return the current robot distance
   */
  public abstract double getCurrentDistance();

  /**
   * Get the current robot heading in the units that your path is stored in. 
   * This will typically be in radians.
   * 
   * @return the current robot distance
   */
  public abstract double getCurrentHeading();

  /**
   * Use the velocity outputs generated by the path follower.
   * These values will be in the velocity units stored in your path files.
   * @param left the velocity of the left side
   * @param right the velocity of the left side
   */
  public abstract void useOutputs(double left, double right);

  @Override
  protected void initialize() {
    resetDistance();
    //Make sure we're starting at the beginning of the path
    getDistanceController().reset();
    getHeadingController().reset();
    currentSegment = 0;
    isFinished = false;

    // Start running the path
    pathNotifier.startPeriodic(0.02); // trajectory.getValue(0, SegmentValue.TIME_STAMP));
    pidNotifier.startPeriodic(getDistanceController().getPeriod());
  }

  @Override
  protected void execute() {
    SmartDashboard.putNumber("Distance Path Error", getDistanceController().getError());
    SmartDashboard.putNumber("Heading Path Error", getHeadingController().getError());
  }

  @Override
  protected boolean isFinished() {
    return isFinished;
  }

  @Override
  protected void end() {
    pathNotifier.stop();
    pidNotifier.stop();
  }

  @Override
  protected void interrupted() {
    end();
  }


  private void moveToNextSegment() {
    // Move to the next segment in the path
    currentSegment++;

    // Was that the last segment in our path?
    if (currentSegment >= trajectory.length) {
      isFinished = true;
    }
  }

  private void calculateOutputs() {
    // We need to get the current segment right away so it doesn't change in the middle
    // of the calculations
    int segment = currentSegment;
    // If we're finished there are no more segments to read from and we should return
    if (segment >= trajectory.length) {
      return;
    }

    // Get our expected velocities based on the paths
    double leftVelocity = trajectory[segment][mirror ^ reverse ? Path.RIGHT_VELOCITY : Path.LEFT_VELOCITY];
    double rightVelocity = trajectory[segment][mirror ^ reverse ? Path.LEFT_VELOCITY : Path.RIGHT_VELOCITY];

    if (reverse) {
      leftVelocity = -leftVelocity;
      rightVelocity = -rightVelocity;
    }

    // Set our expected position to be the setpoint of our distance controller
    // The position will be an average of both the left and right to give us the overall distance
    double expectedPosition = trajectory[segment][Path.CENTER_POSITION];
    getDistanceController().setReference(reverse ? -expectedPosition : expectedPosition);
    double currentPosition = getCurrentDistance();

    // Set our expected heading to be the setpoint of our direction controller
    double expectedHeading = trajectory[segment][Path.HEADING];
    // If the path is flipped, invert the sign of the heading
    getHeadingController().setReference(mirror ? -expectedHeading : expectedHeading);
    double currentHeading = getCurrentHeading();

    // The final velocity is going to be a combination of our expected velocity corrected by our distance error and our heading error
    // velocity = expected + distanceError +/- headingError
    double correctedLeftVelocity = leftVelocity + getDistanceController().calculate(currentPosition) - getHeadingController().calculate(currentHeading);
    double correctedRightVelocity = rightVelocity + getDistanceController().calculate(currentPosition) + getHeadingController().calculate(currentHeading);

    useOutputs(correctedLeftVelocity, correctedRightVelocity);
  }
}