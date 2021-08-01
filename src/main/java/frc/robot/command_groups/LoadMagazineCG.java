/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.command_groups;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.magazine.commands.IntakeMagazine;
import frc.robot.spacer.commands.IntakeSpacer;

public class LoadMagazineCG extends CommandGroup {
  public LoadMagazineCG() {
    addParallel(new IntakeSpacer());
    addParallel(new IntakeMagazine());
  }
}
