package frc.robot.drivetrain.commands;

import frc.paths.Path;
import frc.robot.drivetrain.*;
import lib.math.*;

import static frc.robot.drivetrain.Drivetrain.CommandUnits.FPS;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.Notifier;

public class TrajectoryFollower extends Command {
    private Drivetrain drive;
    private StateEstimator odometry;
    private Path path;
    private double wheelbase, beta, zeta;
    private int index;
    private boolean isFinished;

    private Notifier controller = new Notifier(this::controller);

    public TrajectoryFollower(Path path) {
        this.path = path;
        beta = 2.0;
        zeta = 0.7;
        wheelbase = 2.435;
    }

    @Override
    protected void initialize() {
        drive = Drivetrain.getDrivetrain();
        double[] state = path.getPath()[0];
        odometry = new StateEstimator(drive.getLeftPosition(), 
                                    drive.getRightPosition(), 
                                    -drive.getHeading(), 
                                    state[4], 
                                    state[5], 
                                    state[6]);
        index = 0;
        isFinished = false;
        controller.startPeriodic(0.01);
    }

    @Override
    protected boolean isFinished() {
        return isFinished;
    }
  
    @Override
    protected void end() {
        controller.stop();
    }
  
    @Override
    protected void interrupted() {
        end();
    }

    private void controller() {
        if (index >= path.getPath().length) {
            isFinished = true;
            return;
        }

        Pose pose = odometry.update(drive.getLeftPosition(), drive.getRightPosition(), -drive.getHeading());
        double[] state = path.getPath()[index];

        Pose error = state.minus(pose);
        double linearRef = (state[Path.LEFT_VELOCITY] + state[Path.RIGHT_VELOCITY]) * 0.5;
        double angularRef = (state[Path.RIGHT_VELOCITY] - state[Path.LEFT_VELOCITY]) / wheelbase;
        double k = 2.0 * zeta * Math.sqrt(beta * linearRef * linearRef + angularRef * angularRef);
        // There is a discontinuity at x = 0, we can remove it by substituting y = 1 which can be derived by L'Hopitals rule
        double sinc = (Math.abs(eTheta) < 1E-3) ? 1.0 : Math.sin(eTheta) / eTheta;
        double linearVelocity = linearRef * Math.cos(eTheta) + k * error.x();
        double angularVelocity = angularRef + k * error.rotation().radians() + linearRef * beta * sinc * error.point().y();

        drivetrain.setSetpoint(FPS, 
            linearVelocity - angularVelocity * wheelbase * 0.5, 
            linearVelocity + angularVelocity * wheelbase * 0.5);

        index++;
    }
}