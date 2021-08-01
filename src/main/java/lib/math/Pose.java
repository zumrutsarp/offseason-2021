package lib.math;

public class Pose {
    // Based on wpilib/254 geometry classes
    private Point point;
    private Rotation rotation;
    
    public Pose(Point point, Rotation rotation) {
        this.point = point;
        this.rotation = rotation;
    }

    public Pose plus(Pose pose) {
        return new Pose(point.plus(pose.point().rotate(rotation)), rotation.plus(pose.rotation()));
    }

    public Pose minus(Pose pose) {
        return new Pose(point.minus(pose.point()).rotate(pose.rotation().inverse()), rotation.minus(pose.rotation));
    }

    public Pose exp(Twist twist) {
        double s, c;
        if (Math.abs(twist.dtheta()) < 1E-6) {
          s = 1.0 - 1.0 / 6.0 * twist.dtheta() * twist.dtheta();
          c = 0.5 * twist.dtheta();
        } else {
          s = Math.sin(twist.dtheta()) / twist.dtheta();
          c = (1 - Math.cos(twist.dtheta())) / twist.dtheta();
        }
        Pose delta = 
            new Pose(
                new Point(twist.dx() * s - twist.dy() * c, twist.dx() * c + twist.dy() * s),
                new Rotation(twist.dtheta()));
    
        return this.plus(delta);
    }

    public Pose log(Pose end) {
        return new Twist(
            translationPart.getX(), 
            translationPart.getY(), 
            dtheta);
    }

    public Point point() {
        return point;
    }

    public Rotation rotation() {
        return point;
    }
}