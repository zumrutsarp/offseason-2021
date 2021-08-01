package lib.control;

import lib.math.*;

public class PoseEstimator {
    private double leftDistance, rightDistance;
    private Pose pose;
    private Rotation theta;

    public PoseEstimator(double leftDistance, double rightDistance, Rotation theta, Pose initial) {
        this.leftDistance = leftDistance;
        this.rightDistance = rightDistance;
        this.theta = theta;
        this.pose = initial;
    }

    public Pose update(double leftDistance, double rightDistance, Rotation theta) {
        double dLeft = leftDistance - this.leftDistance;
        double dRight = rightDistance - this.rightDistance;
        Rotation dTheta = theta.minus(this.theta);

        this.leftDistance = leftDistance;
        this.rightDistance = rightDistance;
        this.theta = theta.plus(dTheta);

        return pose = pose.exp(new Twist((dLeft + dRight) * 0.5, 0, dTheta.radians()));
    }
}