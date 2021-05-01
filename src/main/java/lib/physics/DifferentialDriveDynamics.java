package lib.physics;

public class DifferentialDriveDynamics {
    private DCMotorDynamics leftMotor;
    private DCMotorDynamics rightMotor;
    private double wheelbaseRadius; // Half the distance between the wheels on each side of the robot (m)
    private double wheelRadius; // Radius of the wheels (m)
    private double mass; // Mass of the robot (kg)
    private double moi; // Moment of inertia of the robot (kg * m^2)

    public DifferentialDriveDynamics() {
        
    }

    public double solveMaxVelocity(double voltage, double curvature) {
        double leftFreeSpeed = leftMotor.solveFreeSpeed(voltage);
        double rightFreeSpeed = rightMotor.solveFreeSpeed(voltage);
        return Math.min(leftFreeSpeed / (1 - curvature), rightFreeSpeed / (1 + curvature)) / 2;
    }

    public double solveMaxAcceleration(double absVoltage, double velocity, double curvature, double dcurvature) {
        return solveAcceleration(absVoltage, velocity, curvature, dcurvature, 1.0);
    }

    public double solveMinAcceleration(double absVoltage, double velocity, double curvature, double dcurvature) {
        return solveAcceleration(absVoltage, velocity, curvature, dcurvature, -1.0);
    }

    private double solveAcceleration(double absVoltage, double velocity, double curvature, double dcurvature, double sign) {
        // Add derivation here, explanation of these equations are in the software channel on slack
        double leftMaxTorque = leftMotor.solveTorque(absVoltage * sign, velocity);
        double rightMaxTorque = rightMotor.solveTorque(absVoltage * sign, velocity);
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