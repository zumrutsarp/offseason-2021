/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.spacer;

import com.revrobotics.CANDigitalInput;
import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;
import com.revrobotics.CANDigitalInput.LimitSwitch;
import com.revrobotics.CANDigitalInput.LimitSwitchPolarity;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.team2363.logger.HelixLogger;

import edu.wpi.first.wpilibj.command.Subsystem;

import com.revrobotics.CANPIDController;

/**
 * Add your docs here.
 */
public class Spacer extends Subsystem {

  private static Spacer INSTANCE = null;

  private static final int SPACER_ID = 16;

  public Boolean spacer_running = false;

  private CANSparkMax motor = new CANSparkMax(SPACER_ID, MotorType.kBrushless);

  private final CANDigitalInput limit;

  private final CANPIDController controller;
  
  public Spacer() {
    super();

    // initialize motor
    motor.restoreFactoryDefaults();
    motor.setIdleMode(IdleMode.kBrake);
    motor.setSmartCurrentLimit(30);
    motor.getEncoder().setVelocityConversionFactor(1.0/16.0);

    //  Disable Limit Switches
    limit = new CANDigitalInput(motor,LimitSwitch.kForward,LimitSwitchPolarity.kNormallyOpen);
    limit.enableLimitSwitch(false);

    setupLogs();

    controller = motor.getPIDController();

    controller.setP(0.0014);
    controller.setI(0);
    controller.setD(0.0);
    controller.setFF(0.00125);
  }

  /**
   * @return the singleton instance of the spacer subsystem
   */
  public static Spacer getSpacer() {
    if (INSTANCE == null) {
      INSTANCE = new Spacer();
    }
    return INSTANCE;
  }

  public void setVelocity(double velocity) {
    spacer_running = true;
    controller.setReference(velocity, ControlType.kVelocity);
  }

  public double getVelocity() {
    return motor.getEncoder().getVelocity();
  }

  public void setPower(double power) {
    spacer_running = false;
    motor.set(power);
  }

  public boolean isBallPresent() {
    return motor.getForwardLimitSwitch(LimitSwitchPolarity.kNormallyOpen).get();
  }

  public boolean isSpacerRunning() {
    return spacer_running;
  }
  
  private void setupLogs() {
    HelixLogger.getInstance().addSource("SPACER CURRENT", motor::getOutputCurrent);
    HelixLogger.getInstance().addSource("SPACER VOLTAGE", motor::getBusVoltage);
  }

  @Override
  public void initDefaultCommand() {
    // Set the default command for a subsystem here.
    // setDefaultCommand(new MySpecialCommand());

  }
}