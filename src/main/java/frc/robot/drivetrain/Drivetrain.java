/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.drivetrain;

import static com.ctre.phoenix.motorcontrol.ControlMode.Velocity;
import static com.ctre.phoenix.motorcontrol.NeutralMode.Brake;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import com.ctre.phoenix.sensors.PigeonIMU;
import com.team2363.logger.HelixLogger;
import com.team2363.utilities.HelixMath;
import com.team319.models.BobTalonSRX;
import com.team319.models.BobVictorSPX;
import com.team319.models.LeaderBobTalonSRX;

import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.drivetrain.commands.CarsonDrive;

public class Drivetrain extends Subsystem {

  private static Drivetrain INSTANCE = null;
  
  /**
   * @return the singleton instance of the Drivetrain subsystem
   */
  public static Drivetrain getDrivetrain() {
    if (INSTANCE == null) {
      INSTANCE = new Drivetrain();
    }
    return INSTANCE;
  }

  public enum CommandUnits {
    PERCENT_FULLPOWER, PERCENT_FULLSPEED, FPS, TICKSPER100MS
  }

  // PIDF for drivetrain.
  public double kP, kI, kD, kF;  

  // Items that could be put into a per-bot config file.
  private double WHEEL_DIAMETER_IN_INCHES = 6;
  // private int ENCODER_TICKS_PER_REVOLUTION = (int) (480 * 28.0/56.0);
  private int ENCODER_TICKS_PER_REVOLUTION = (int) (480 * 56.0/28.0);
  public static double MAX_VELOCITY_IN_FPS = 10.0;
  private static int VELOCITY_CONTROL_SLOT = 0;

  // Constructed in initMotorControllers:
  private LeaderBobTalonSRX left = null;
  private LeaderBobTalonSRX right = null;

  // Constructed in initMotorControllers... because it MAY
  // be connected to one of the slave motor controllers.
  private PigeonIMU pigeon = null;

  // private PowerDistributionPanel pdp = new PowerDistributionPanel();

  PowerDistributionPanel pdp = new PowerDistributionPanel();

  private Drivetrain() {
    initMotorControllers();

    setPIDFValues();
    setBrakeMode(Brake);
    setupSensors();
    setupLogs();
  }

  /**
   * Initialize the motor controllers based on the name of the robot from preferences.
   * Also initializes the pigeon since the pigeon can run off a slave controller (oler
   * bots) or run solo (newer bots).
   */
  private void initMotorControllers() {

    BaseMotorController rightSlave1 = null;
    BaseMotorController rightSlave2 = null;
    BaseMotorController leftSlave1 = null;
    BaseMotorController leftSlave2 = null;

    // Configure the controllers based on the name of the bot.
    final String botName = frc.robot.Preferences.getPreferences().getRobotName();
    if (("Bot1".equalsIgnoreCase(botName) == true) || ("Bot2".equalsIgnoreCase(botName) == true)) {

      WHEEL_DIAMETER_IN_INCHES = 6;
      ENCODER_TICKS_PER_REVOLUTION = (int) (480 * 56.0/28.0);
      MAX_VELOCITY_IN_FPS = 10;
      VELOCITY_CONTROL_SLOT = 0;
      
      // Bot1 uses Victors as slaves.
      rightSlave1 = new BobVictorSPX(23);
      rightSlave2 = new BobVictorSPX(24);
      leftSlave1 = new BobVictorSPX(12);
      leftSlave2 = new BobVictorSPX(11);

      left = new LeaderBobTalonSRX(10, leftSlave1, leftSlave2);
      right = new LeaderBobTalonSRX(25, rightSlave1, rightSlave2);

      // Riding solo on CAN
      pigeon = new PigeonIMU(30);

      // Handles direction of motors and corrisponding encoders.

      left.setSensorPhase(false);
      right.setSensorPhase(false);
      left.setInverted(false);
      right.setInverted(true);

    } else {   // Programming Bot or Unknown Bot
      
      WHEEL_DIAMETER_IN_INCHES = 4;
      ENCODER_TICKS_PER_REVOLUTION = (int) (480 * 42.0/48.0);
      MAX_VELOCITY_IN_FPS = 10;
      VELOCITY_CONTROL_SLOT = 0;

      // Programming bot uses Talons everywhere
      rightSlave1 = new BobTalonSRX(11);
      rightSlave2 = new BobTalonSRX(10);
      leftSlave1 = new BobTalonSRX(24);
      leftSlave2 = new BobTalonSRX(25);

      left = new LeaderBobTalonSRX(23, leftSlave1, leftSlave2);
      right = new LeaderBobTalonSRX(12, rightSlave1, rightSlave2);

      // Riding on the slave controller.
      pigeon = new PigeonIMU((BobTalonSRX)rightSlave2);

      // Handles direction of motors and corrisponding encoders.
      left.setSensorPhase(false);
      right.setSensorPhase(false);
      left.setInverted(true);
      right.setInverted(false);
    }
  }

  @Override
  public void initDefaultCommand() {
    setDefaultCommand(new CarsonDrive());
  }

  private void setVelocityOutput(final double leftVelocity, final double rightVelocity) {
    left.set(Velocity, leftVelocity);
    right.set(Velocity, rightVelocity);
  }

  private void setPowerOutput(final double leftPercent, final double rightPercent) {
    left.set(ControlMode.PercentOutput, leftPercent);
    right.set(ControlMode.PercentOutput, rightPercent);
  }

  public void setSetpoint(final CommandUnits commandUnits, final double left, final double right) {
    switch (commandUnits) {
    case PERCENT_FULLPOWER:
      setPowerOutput(left, right);
      break;
    case PERCENT_FULLSPEED:
      setVelocityOutput(
          HelixMath.convertFromFpsToTicksPer100Ms(left * MAX_VELOCITY_IN_FPS, WHEEL_DIAMETER_IN_INCHES,
              ENCODER_TICKS_PER_REVOLUTION),
          HelixMath.convertFromFpsToTicksPer100Ms(right * MAX_VELOCITY_IN_FPS, WHEEL_DIAMETER_IN_INCHES,
              ENCODER_TICKS_PER_REVOLUTION));
          SmartDashboard.putNumber("Left Velocity SetPoint (FPS)", left * MAX_VELOCITY_IN_FPS);
          SmartDashboard.putNumber("Right Velocity SetPoint (FPS)", right * MAX_VELOCITY_IN_FPS);
          SmartDashboard.putNumber("Left Velocity Error", getLeftVelocity() - (left * MAX_VELOCITY_IN_FPS));
          SmartDashboard.putNumber("Right Velocity Error", getRightVelocity() - (right * MAX_VELOCITY_IN_FPS));
      break;
    case FPS:
      setVelocityOutput(
          HelixMath.convertFromFpsToTicksPer100Ms(left, WHEEL_DIAMETER_IN_INCHES, ENCODER_TICKS_PER_REVOLUTION),
          HelixMath.convertFromFpsToTicksPer100Ms(right, WHEEL_DIAMETER_IN_INCHES, ENCODER_TICKS_PER_REVOLUTION));
      break;
    case TICKSPER100MS:
      setVelocityOutput(left, right);
    }
  }

  public void setPIDFValues() {

    kF = 1.75; //1.25
    kP = 1; //1
    kI = 0.0; //0.01
    kD = 0;

    left.configPIDF(VELOCITY_CONTROL_SLOT, kP, kI, kD, kF);
    right.configPIDF(VELOCITY_CONTROL_SLOT, kP, kI, kD, kF);

    left.config_IntegralZone(VELOCITY_CONTROL_SLOT,
        (int) HelixMath.convertFromFpsToTicksPer100Ms(1, WHEEL_DIAMETER_IN_INCHES, ENCODER_TICKS_PER_REVOLUTION));
    right.config_IntegralZone(VELOCITY_CONTROL_SLOT,
        (int) HelixMath.convertFromFpsToTicksPer100Ms(1, WHEEL_DIAMETER_IN_INCHES, ENCODER_TICKS_PER_REVOLUTION));
    
    // Keep this putPIDFSmartDash call so can tune through Shuffleboard.
    putPIDFSmartDash();

  }

  // Called in the periodic() and other times to display info on the SmartDashboard.
  public void putPIDFSmartDash() {

        SmartDashboard.putNumber("DriveTrain P Gain", kP);
        SmartDashboard.putNumber("DriveTrain I Gain", kI);
        SmartDashboard.putNumber("DriveTrain D Gain", kD);
        SmartDashboard.putNumber("DriveTrain Feed Forward", kF);
  }

  private void setupSensors() {
    left.configPrimaryFeedbackDevice(FeedbackDevice.QuadEncoder);
    right.configPrimaryFeedbackDevice(FeedbackDevice.QuadEncoder);
  }

  private void setBrakeMode(final NeutralMode neutralMode) {
    left.setNeutralMode(neutralMode);
    right.setNeutralMode(neutralMode);
  }

  private void setupLogs() {
    HelixLogger.getInstance().addSource("TOTAL CURRENT", pdp::getTotalCurrent);
    HelixLogger.getInstance().addSource("DRIVETRAIN LEFT Voltage", left::getMotorOutputVoltage);
    HelixLogger.getInstance().addSource("DRIVETRAIN RIGHT Voltage", right::getMotorOutputVoltage);
    HelixLogger.getInstance().addSource("DT LM Current", left::getSupplyCurrent);
    HelixLogger.getInstance().addSource("DT RM Current", right::getSupplyCurrent);
    
    // HelixLogger.getInstance().addDoubleSource("PIGEON HEADING", this::getYaw); 
    // This logging format should work for Talons OR Victor SLAVES.
    // HelixLogger.getInstance().addDoubleSource("DT LS1 Current", () -> pdp.getCurrent(LEFT_SLAVE_1_PDP));
    // HelixLogger.getInstance().addDoubleSource("DT LS2 Current", () -> pdp.getCurrent(LEFT_SLAVE_2_PDP));
    // HelixLogger.getInstance().addDoubleSource("DT RS1 Current", () -> pdp.getCurrent(RIGHT_SLAVE_1_PDP));
    // HelixLogger.getInstance().addDoubleSource("DT RS2 Current", () -> pdp.getCurrent(RIGHT_SLAVE_1_PDP));
    // HelixLogger.getInstance().addDoubleSource("DRIVETRAIN LEFT Velocity", ()->this.getLeftVelocity()); 
    // HelixLogger.getInstance().addDoubleSource("DRIVETRAIN RIGHT Velocity", this::getRightVelocity);
  }

  public void resetEncoders() {
    left.getSensorCollection().setQuadraturePosition(0, 0);
    right.getSensorCollection().setQuadraturePosition(0, 0);
  }

  public void resetHeading() {
    pigeon.setYaw(0.0);
  }

  public double getHeading() {
    final double[] ypr = { 0, 0, 0 };
    pigeon.getYawPitchRoll(ypr);
    return ypr[0];
  }

  public double getLeftVelocity() {
      return HelixMath.convertFromTicksPer100MsToFps(left.getSelectedSensorVelocity(), WHEEL_DIAMETER_IN_INCHES, ENCODER_TICKS_PER_REVOLUTION);
  }

  public double getRightVelocity() {
    return HelixMath.convertFromTicksPer100MsToFps(right.getSelectedSensorVelocity(), WHEEL_DIAMETER_IN_INCHES, ENCODER_TICKS_PER_REVOLUTION);
  }

  public double getLeftPosition() {
    return HelixMath.convertFromTicksToFeet(left.getSelectedSensorPosition(), WHEEL_DIAMETER_IN_INCHES, ENCODER_TICKS_PER_REVOLUTION);
  }

  public double getRightPosition() {
    return  HelixMath.convertFromTicksToFeet(right.getSelectedSensorPosition(), WHEEL_DIAMETER_IN_INCHES, ENCODER_TICKS_PER_REVOLUTION);
  }

  @Override
  public void periodic() {

    // read PID coefficients from SmartDashboard
    final double p = SmartDashboard.getNumber("DriveTrain P Gain", 0);
    final double d = SmartDashboard.getNumber("DriveTrain D Gain", 0);
    final double ff = SmartDashboard.getNumber("DriveTrain Feed Forward", 0);

    // if PID coefficients on SmartDashboard have changed, write new values to
    // controller
      if ((p != kP)) {
          left.config_kP(VELOCITY_CONTROL_SLOT, p);
          right.config_kP(VELOCITY_CONTROL_SLOT, p);
          kP = p;
      }
      if ((d != kD)) {
          left.config_kP(VELOCITY_CONTROL_SLOT, d);
          right.config_kP(VELOCITY_CONTROL_SLOT, d);
          kD = d;
      }
      if ((ff != kF)) {
          left.config_kP(VELOCITY_CONTROL_SLOT, ff);
          right.config_kP(VELOCITY_CONTROL_SLOT, ff);
          kF = ff;
      }

      // Keep this putPIDFSmartDash call so can tune through Shuffleboard.
      putPIDFSmartDash();

      SmartDashboard.putNumber("Pigeon Yaw", getHeading());
      SmartDashboard.putNumber("Left Velocity", getLeftVelocity());
      SmartDashboard.putNumber("Right Velocity", getRightVelocity());
      SmartDashboard.putNumber("Left Distance", getLeftPosition());
      SmartDashboard.putNumber("Right Distance", getRightPosition());
  }
}
