/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.oi;

import static com.team2363.utilities.ControllerMap.*;
import static com.team2363.utilities.ControllerPatroller.getPatroller;

import com.team2363.utilities.ControllerMap;

import edu.wpi.first.wpilibj.Joystick;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */

public class OI {
    private static OI INSTANCE;

    /*** 
     * @return retrieves the singleton instance of the Operator Interface
     * */
    public static OI getOI() {
        if (INSTANCE == null) {
            INSTANCE = new OI();
        }
        return INSTANCE;
    }

    private final String DRIVER = "Xbox";
    private final int DRIVER_PORT = 0;   
    private final String OPERATOR = "P";
    private final int OPERATOR_PORT = 1;

    private Joystick driver = getPatroller().get(DRIVER, DRIVER_PORT);
    private Joystick operator = getPatroller().get(OPERATOR, OPERATOR_PORT);

    private OI() {
        // new JoystickButton(operator, ControllerMap.PS4_R1);
    }

    /**
    * @return the raw controller throttle
    */
    public double getThrottle() {
    return driver.getRawAxis(X_BOX_LEFT_STICK_Y);
    }

    /**
    * @return the raw controller turn
    */
    public double getTurn() {
        return driver.getRawAxis(X_BOX_RIGHT_STICK_X);
    }
}