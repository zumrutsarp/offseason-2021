/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.drivetrain.commands;

import static frc.robot.drivetrain.Drivetrain.CommandUnits.PERCENT_FULLSPEED;

import com.team2363.commands.HelixDrive;
import com.team2363.utilities.RollingAverager;

import frc.robot.drivetrain.Drivetrain;
import frc.robot.oi.OI;

public class CarsonDrive extends HelixDrive {

  double deadZone = 0.05;

  private final RollingAverager throttle = new RollingAverager(7);

  public CarsonDrive() {
    requires(Drivetrain.getDrivetrain());
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    super.initialize();
    for (int i = 0; i < 7; i++) {
        throttle.getNewAverage(0);
    }
  }

  @Override
  protected double getThrottle() {
      return -handleDeadzone(deadZone, OI.getOI().getThrottle());
  }

  @Override
  protected double getTurn() {
      double rawTurn = OI.getOI().getTurn();
      // return handleDeadzone(deadZone, OI.getOI().getThrottle()) == 0 ? rawTurn * 0.5 : rawTurn * -handleDeadzone(deadZone, OI.getOI().getThrottle());
      return handleDeadzone(deadZone, rawTurn);
  }

  @Override
  protected void useOutputs(final double left, final double right) {
      Drivetrain.getDrivetrain().setSetpoint(PERCENT_FULLSPEED, left, right);
  }

  private double handleDeadzone(double deadZone, double val) {
    return Math.abs(val) > Math.abs(deadZone) ? val / (1 - deadZone) - Math.signum(val) * deadZone : 0;
  }
}
