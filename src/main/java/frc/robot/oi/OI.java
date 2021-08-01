
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
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.buttons.Trigger;
import frc.robot.command_groups.LoadMagazineCG;
import frc.robot.command_groups.ShootCG;
import frc.robot.command_groups.StartIntakeCG;
import frc.robot.command_groups.StopIntakeCG;
import frc.robot.flashlight.commands.flashlightOff;
import frc.robot.flashlight.commands.flashlightOn;
import frc.robot.shooter.commands.SpinUpBlueZone;
import frc.robot.shooter.commands.SpinUpGreenZone;
import frc.robot.shooter.commands.SpinUpRedZone;
import frc.robot.shooter.commands.SpinUpYellowZone;
import frc.robot.shooter.commands.StopShooter;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */

public class OI {

  private static OI INSTANCE;

  /**
   * @return retrieves the singleton instance of the Operator Interface
   */
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

    // Intake buttons - Right trigger activates intake, left trigger retractes and
    // disables
    new JoystickButton(operator, ControllerMap.PS4_R1).whenPressed(new StartIntakeCG(true));
    new JoystickButton(operator, ControllerMap.PS4_L1).whenReleased(new StopIntakeCG());

    // Set shooter speeds - Triangle farthest zone from goal, circle third farthest,
    // x second closest, square closest
    new JoystickButton(operator, ControllerMap.PS4_TRIANGLE).whenPressed(new SpinUpRedZone());
    new JoystickButton(operator, ControllerMap.PS4_TRIANGLE).whenReleased(new StopShooter());

    new JoystickButton(operator, ControllerMap.PS4_CIRCLE).whenPressed(new SpinUpYellowZone());
    new JoystickButton(operator, ControllerMap.PS4_CIRCLE).whenReleased(new StopShooter());

    new JoystickButton(operator, ControllerMap.PS4_X).whenPressed(new SpinUpBlueZone());
    new JoystickButton(operator, ControllerMap.PS4_X).whenReleased(new StopShooter());

    new JoystickButton(operator, ControllerMap.PS4_SQUARE).whenPressed(new SpinUpGreenZone());
    new JoystickButton(operator, ControllerMap.PS4_SQUARE).whenReleased(new StopShooter());

    // Shooting is on a whenPressed / whenReleased right button
    new JoystickButton(driver, ControllerMap.X_BOX_RB).whenPressed(new ShootCG());
    new JoystickButton(driver, ControllerMap.X_BOX_RB).whenReleased(new LoadMagazineCG());

    new JoystickButton(driver, ControllerMap.X_BOX_LB).whenPressed(new SpinUpYellowZone());
    new JoystickButton(driver, ControllerMap.X_BOX_LB).whenReleased(new StopShooter());

    // new JoystickButton(operator, ControllerMap.PS4_CIRCLE).whenPressed(new StartIntakeCG(true));

    new JoystickButton(driver, ControllerMap.X_BOX_A).whenPressed(new flashlightOff());
    new JoystickButton(driver, ControllerMap.X_BOX_A).whenReleased(new flashlightOn());
}

  /**
   * @return the raw controller throttle
   */
  public double getThrottle() {
    return driver.getRawAxis(X_BOX_LEFT_STICK_Y);
  }

  public boolean getRightTrigger() {
    return driver.getRawAxis(ControllerMap.X_BOX_RIGHT_TRIGGER) > 0.5;
  }

  /**
   * @return the raw controller turn
   */
  public double getTurn() {
    return driver.getRawAxis(X_BOX_RIGHT_STICK_X);
  }

  /**
   * Turns on and off the rumble function on the driver and operator controllers
   * 
   * @param set true to turn on rumble
   */
  public void setControllerRumble(boolean rumble) {
    if (rumble) {
      setRumble(driver, 1);
      setRumble(operator, 1);
    } else {
      setRumble(driver, 0);
      setRumble(operator, 0);
    }
  }

  private void setRumble(Joystick controller, int state) {
    controller.setRumble(RumbleType.kLeftRumble, state);
    controller.setRumble(RumbleType.kRightRumble, state);
  }

  class RightTriggerButton extends Trigger {
    @Override
    public boolean get(){
      return (driver.getRawAxis(ControllerMap.X_BOX_RIGHT_TRIGGER) > 0.8);    
    }
  }
}