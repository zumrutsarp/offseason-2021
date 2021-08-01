package lib.math;

public class Point {
    // Based on wpilib/254 geometry classes
    private double x, y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // Applies a linear transformation of a rotation matrix
    public Point rotate(Rotation theta) {
        return new Point(x * theta.cos() - y * theta.sin(), x * theta.sin() + y * theta.cos());
    }

    // Vector sum
    public Point plus(Point point) {
        return new Point(x + point.x(), y + point.y());
    }

    // Vector subtraction
    public Point minus(Point point) {
        return new Point(x - point.x(), y - point.y());
    }

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }
}