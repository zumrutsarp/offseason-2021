/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.command_groups;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.magazine.commands.ShootOneMagazine;
import frc.robot.spacer.commands.ShootSpacer;

public class ShootOneCG extends CommandGroup {
  public ShootOneCG() {
    addSequential(new ShootSpacer());
    addSequential(new ShootOneMagazine(), 1.5);
    addSequential(new StopMagazineCG());
  }
}
