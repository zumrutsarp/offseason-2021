package lib.math;

public class Rotation {
    // Based on wpilib/254 geometry classes
    private double theta;

    public Rotation(double theta) {
        this.theta = theta;
    }

    public Rotation plus(Rotation rotation) {
        return new Rotation(theta + rotation.radians());
    }

    public Rotation minus(Rotation rotation) {
        return new Rotation(theta - rotation.radians());
    }

    public Rotation inverse() {
        return new Rotation(-theta);
    }

    public double radians() {
        return theta;
    }

    public double degrees() {
        return Math.toDegrees(theta);
    }

    public double sin() {
        return Math.sin(theta);
    }

    public double cos() {
        return Math.cos(theta);
    }

    public double tan() {
        return Math.tan(theta);
    }
}