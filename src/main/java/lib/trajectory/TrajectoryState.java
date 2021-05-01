package lib.trajectory;

import lib.geometry.Pose2d;

public class TrajectoryState {
    public double t;
    public double d;
    public double v;
    public double a;
    public Pose2d pose;
    public double curvature;
}