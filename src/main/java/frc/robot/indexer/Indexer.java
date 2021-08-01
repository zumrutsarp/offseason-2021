/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.indexer;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.team2363.logger.HelixLogger;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.command_groups.IndexerOscillateCG;

/**
 * Add your docs here.
 */
public class Indexer extends Subsystem {

  private static Indexer INSTANCE = null;

  private static final int INDEXER_ID = 18;
  public double speed = 0.0;

  private TalonSRX indexer = new TalonSRX(INDEXER_ID);

  public Indexer() {
    super();

    // initialize motor
    indexer.configFactoryDefault();

    setupLogs();
  }

  /**
   * @return the singleton instance of the spacer subsystem
   */
  public static Indexer getIndexer() {
    if (INSTANCE == null) {
      INSTANCE = new Indexer();
    }
    return INSTANCE;
  }

  public void setPower(double power) {
    speed = power;
    indexer.set(ControlMode.PercentOutput, power);
  }

  public void Stop() {
    setPower(0.0);
  }

  private void setupLogs() {
    HelixLogger.getInstance().addSource("INDEXER CURRENT", indexer::getSupplyCurrent);
    HelixLogger.getInstance().addSource("INDEXER VOLTAGE", indexer::getBusVoltage);
  }

  @Override
  public void periodic() {
    super.periodic();
    SmartDashboard.putNumber("INDEXER SPEED", speed);
  }

  @Override
  public void initDefaultCommand() {
    // Set the default command for a subsystem here.
    setDefaultCommand(new IndexerOscillateCG());
  }
}
