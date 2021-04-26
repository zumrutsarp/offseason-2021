package frc.robot.drivetrain.commands;

import static frc.robot.drivetrain.Drivetrain.getDrivetrain;

import frc.robot.drivetrain.Drivetrain.CommandUnits;
import frc.robot.oi.OI;

public class TeleopDrive extends NormalizedArcadeDrive {
    public TeleopDrive() {
        addRequirements(getDrivetrain());
    }

    @Override
    protected double getThrottle() {
        return OI.getOI().getThrottle();
    }

    @Override
    protected double getTurn() {
        return OI.getOI().getTurn();
    }

    @Override
    protected void useOutputs(final double left, final double right) {
        getDrivetrain().setSetpoint(CommandUnits.PERCENT_FULLSPEED, left, right);
    }
}