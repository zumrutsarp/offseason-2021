/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.magazine;

import com.revrobotics.CANDigitalInput;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;
import com.revrobotics.CANDigitalInput.LimitSwitch;
import com.revrobotics.CANDigitalInput.LimitSwitchPolarity;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.team2363.logger.HelixLogger;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Preferences;
import frc.robot.spacer.Spacer;

public class Magazine extends Subsystem {

  private static Magazine INSTANCE = null;

  private static final int MAGAZINE_ID = 15;

  private final CANSparkMax motor;
  private final CANDigitalInput limit;

  // Number of balls currently in the system.
  public int ball_count = 0;
  public int balls_processed = 0;

  private final CANPIDController controller;

  // The various states of the Ball Handling subsystems, which include the magazine,
  // the spacer and the intake. Each subsytem, based on the state given to
  // it via the SetBallHandlingTo() command.  The state is passed to each 
  // subsystem from controller action command groups
  // like ShootCG, IntakeCG, etc.
  public enum BallHandlingState {
    SHOOT, INTAKE, SHOOT_NO_LOGIC, INTAKE_NO_LOGIC, SHOOT_ONE, ADVANCE, STOP
  };

  private Magazine() {
    super();

    // initialize motor
    motor = new CANSparkMax(MAGAZINE_ID, MotorType.kBrushless);
    motor.restoreFactoryDefaults();
    motor.setIdleMode(IdleMode.kBrake);
    motor.setSmartCurrentLimit(30);

    //  Disable Limit Switches
    limit = new CANDigitalInput(motor,LimitSwitch.kForward,LimitSwitchPolarity.kNormallyOpen);
    limit.enableLimitSwitch(false);

    ball_count = 0;
    balls_processed = Preferences.getPreferences().getBallsProcessed();

    motor.getEncoder().setVelocityConversionFactor(1.0/16.0);

    controller = motor.getPIDController();

    controller.setP(0.0014);
    controller.setI(0);
    controller.setD(0.0);
    controller.setFF(0.00125);

    setupLogs();
  }

  /**
   * @return the singleton instance of the magazine subsystem
   */
  public static Magazine getMagazine() {
    if (INSTANCE == null) {
      INSTANCE = new Magazine();
    }
    return INSTANCE;
  }

  public void setVelocity(double velocity) {
    controller.setReference(velocity, ControlType.kVelocity);
  }

  public double getVelocity() {
    return motor.getEncoder().getVelocity();
  }

  public void setPower(double power) {
    // set motors to power;
    motor.set(power);
  }

  //  ball_count - total number of balls currently in the magazine.
  //  balls_processed - total number of balls brought into the robot EVER.
  public void IncreaseBallCount() {
    ball_count++;
    balls_processed++;
    Preferences.getPreferences().setBallsProcessed(balls_processed);
  }

  public void DecreaseBallCount() {
    ball_count--;
  }

  public int getBallCount() {
    return ball_count;
  }
  
  public void setBallCount(int count) {
    ball_count = count;
  }
  
  public void ResetBallCount() {
    ball_count = 0;
  }

  public Boolean ballAtShooter() {
    return motor.getForwardLimitSwitch(LimitSwitchPolarity.kNormallyOpen).get();
    // return(false);
  }

  public Boolean ballAtSpacer() {
    return Spacer.getSpacer().isBallPresent();
  }

  private void setupLogs() {
    HelixLogger.getInstance().addSource("MAGAZINE CURRENT", motor::getOutputCurrent);
    HelixLogger.getInstance().addSource("MAGAZINE VOLTAGE", motor::getBusVoltage);
  }

  @Override
  public void periodic() {

    super.periodic();

    SmartDashboard.putNumber("Ball Count", ball_count);
    SmartDashboard.putBoolean("Ball At Spacer", ballAtSpacer());
    SmartDashboard.putBoolean("Ball At Shooter", ballAtShooter());

  }

  @Override
  public void initDefaultCommand() {
    // Set the default command for a subsystem here.
    // setDefaultCommand(new MySpecialCommand());
  }
}
