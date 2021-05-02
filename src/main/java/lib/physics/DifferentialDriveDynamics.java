package lib.physics;

public class DifferentialDriveDynamics {
    private DCMotorDynamics leftMotor;
    private DCMotorDynamics rightMotor;
    private double wheelbaseRadius; // Half the distance between the wheels on each side of the robot (m)
    private double wheelRadius; // Radius of the wheels (m)
    private double mass; // Mass of the robot (kg)
    private double moi; // Moment of inertia of the robot (kg * m^2)
    private double cof; // Coefficient of friction of the wheels (dimensionless)

    public DifferentialDriveDynamics(DCMotorDynamics leftMotor,
            DCMotorDynamics rightMotor,
            double wheelbaseRadius,
            double wheelRadius,
            double mass,
            double moi,
            double cof) {
        this.leftMotor = leftMotor;
        this.rightMotor = rightMotor;
        this.wheelbaseRadius = wheelbaseRadius;
        this.wheelRadius = wheelRadius;
        this.mass = mass;
        this.moi = moi;
        this.cof = cof;
    }

    public double solveMaxVelocity(double voltage, double curvature) {

        double tractionLimitedVelocity = Math.sqrt(cof * mass / curvature);

    }

    public double solveMaxAcceleration(double absVoltage, double velocity, double curvature, double dcurvature) {
        return solveAcceleration(absVoltage, velocity, curvature, dcurvature, 1.0);
    }

    public double solveMinAcceleration(double absVoltage, double velocity, double curvature, double dcurvature) {
        return solveAcceleration(absVoltage, velocity, curvature, dcurvature, -1.0);
    }

    private double solveAcceleration(double absVoltage, double velocity, double curvature, double dcurvature, double sign) {
        // Add derivation here, explanation of these equations are in the software channel on slack
        double leftMaxTorque = leftMotor.solveTorque(absVoltage * sign, velocity * (1 - curvature * wheelbaseRadius));
        double rightMaxTorque = rightMotor.solveTorque(absVoltage * sign, velocity * (1 + curvature * wheelbaseRadius));
        double leftAtRightMax = (rightMaxTorque * (wheelbaseRadius / moi - curvature / mass) - 
                velocity * velocity * dcurvature * wheelRadius) / (wheelbaseRadius / moi + curvature / mass);
        double rightAtLeftMax = (velocity * velocity * dcurvature * wheelRadius + leftMaxTorque *
                (wheelbaseRadius / moi + curvature / mass)) / (wheelbaseRadius / moi - curvature / mass);
        return sign * Math.min(sign * (leftMaxTorque + rightAtLeftMax), sign * (rightMaxTorque + leftAtRightMax)) / (wheelRadius * mass);
    }

    // public double solveTorques(double velocity, double linearAcceleration, double curvature, double dcurvature) {
        // linear acceleration * mass * wheelRadius = Tr + Tl
        // angular acceleration * moi * wheelRadius / wheelbaseRadius = Tr - Tl 
    // }
}
