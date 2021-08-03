package lib.trajectory;

import lib.math.*;
import lib.physics.DifferentialDrive.*;

public class TrajectoryState {
    private Pose pose;
    private ChassisState velocity;
    private ChassisState acceleration;

    public TrajectoryState(Pose pose, ChassisState velocity, ChassisState acceleration) {
        this.pose = pose;
        this.velocity = velocity;
        this.acceleration = acceleration;
    }
}