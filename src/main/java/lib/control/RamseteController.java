package lib.control;

import lib.math.*;
import lib.physics.DifferentialDrive.ChassisState;
import static Math.*;

public class RamseteController {
    private double beta, zeta;

    public RamseteController(double beta, double zeta) {
        this.beta = beta;
        this.zeta = zeta;
    }

    public ChassisState calculate(Pose currentPose, Pose refPose, ChassisState refVelocity) {
        Pose error = refPose.minus(currentPose);
        double eX = error.point().x();
        double eY = error.point().y();
        double eTheta = error.rotation().radians();

        double k = 2.0 * zeta * sqrt(beta * pow(refVelocity.linear, 2) + pow(refVelocity.angular, 2));
        double sinc = (abs(eTheta) < 1E-3) ? 1.0 : sin(eTheta) / eTheta;

        return new ChassisState(
            refVelocity.linear * cos(eTheta) + k * eX,
            refVelocity.angular + k * eTheta + refVelocity.linear * beta * sinc * eY;
        )
    }
}