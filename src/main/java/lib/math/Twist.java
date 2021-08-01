package lib.math;

public class Twist {
    // Based on wpilib/254 geometry classes
    private double dx, dy, dtheta;

    public Twist(double dx, double dy, double dtheta) {
        this.dx = dx;
        this.dy = dy;
        this.dtheta = dtheta;
    }

    public Twist scale(double scalar) {
        return new Twist(dx * scalar, dy * scalar, dtheta * scalar);
    }

    public double dx() {
        return dx;
    }

    public double dy() {
        return dy;
    }

    public double dtheta() {
        return dtheta;
    }
}