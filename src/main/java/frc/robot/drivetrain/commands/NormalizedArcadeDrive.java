package frc.robot.drivetrain.commands;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;

import edu.wpi.first.wpilibj2.command.CommandBase;

public abstract class NormalizedArcadeDrive extends CommandBase {
    protected abstract double getThrottle();
    protected abstract double getTurn();
    protected abstract void useOutputs(final double left, final double right);

    @Override
    public void execute() {
        //read in joystick values from OI
    	//range [-1, 1]
    	double throttleInput = getThrottle();
		double turnInput = getTurn();
		
    	//find the maximum possible value of (throttle + turn)
    	//along the vector that the arcade joystick is pointing
    	double saturatedInput;
    	double greaterInput = max(abs(throttleInput), abs(turnInput));
    		//range [0, 1]
    	double lesserInput = min(abs(throttleInput), abs(turnInput));
    		//range [0, 1]
    	if (greaterInput > 0.0) {
    		saturatedInput = (lesserInput / greaterInput) + 1.0;
       		//range [1, 2]
    	} else {
    		saturatedInput = 1.0;
    	}
     	
    	//scale down the joystick input values
		//such that (throttle + turn) always has a range [-1, 1]
    	throttleInput = throttleInput / saturatedInput;
		turnInput = turnInput / saturatedInput;
		
		// Now we can calculate the arcade outputs like we normally would
		double left = throttleInput + turnInput;
		double right = throttleInput - turnInput;

	    useOutputs(left, right);
    }
}
