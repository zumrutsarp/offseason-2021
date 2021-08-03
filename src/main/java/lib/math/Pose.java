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

    public Twist log(final Pose transform) {
        final double dtheta = transform.rotation().radians();
        final double half_dtheta = 0.5 * dtheta;
        final double cos_minus_one = transform.rotation().cos() - 1.0;
        double halftheta_by_tan_of_halfdtheta;
        if (Math.abs(cos_minus_one) < 1E-6) {
            halftheta_by_tan_of_halfdtheta = 1.0 - 1.0 / 12.0 * dtheta * dtheta;
        } else {
            halftheta_by_tan_of_halfdtheta = -(half_dtheta * transform.rotation().sin()) / cos_minus_one;
        }
        final Point translation_part = transform.point()
                .rotate(new Rotation(halftheta_by_tan_of_halfdtheta, -half_dtheta));
        return new Twist(translation_part.x(), translation_part.y(), dtheta);
    }

    public Point point() {
        return point;
    }

    public Rotation rotation() {
        return rotation;
    }
}