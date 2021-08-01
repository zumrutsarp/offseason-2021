/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.drivetrain.commands;

import frc.paths.Path;

import static frc.robot.drivetrain.Drivetrain.CommandUnits.FPS;

import com.team2363.controller.PIDController;

import frc.robot.drivetrain.Drivetrain;

public class PathFollower extends HelixFollower {

    private final Drivetrain drivetrain = Drivetrain.getDrivetrain();

    private final PIDController headingController = new PIDController(15, 0, 0, 0.01);
    private final PIDController distanceController = new PIDController(0.1, 0, 0, 0.01);

    public PathFollower(final Path input) {
        super(input);
        requires(drivetrain);
    }

    @Override
    public void resetDistance() {
        drivetrain.resetEncoders();
    }

    @Override
    public PIDController getHeadingController() {
        return headingController;
    }

    @Override
    public PIDController getDistanceController() {
        return distanceController;
    }

    @Override
    public double getCurrentDistance() {
        return (drivetrain.getLeftPosition() + drivetrain.getRightPosition()) / 2.0;
    }

    @Override
    public double getCurrentHeading() {
        return Math.toRadians(drivetrain.getHeading());
    }

    @Override
    public void useOutputs(final double left, final double right) {
        drivetrain.setSetpoint(FPS, left, right);
	}
}