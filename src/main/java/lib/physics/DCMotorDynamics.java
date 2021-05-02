package lib.physics;

public class DCMotorDynamics {
    private double ks; // voltage needed to break stiction (V)
    private double kt; // 
    private double kv; //

    public DCMotorDynamics(double ks, double kt, double kv) {
        this.ks = ks;
        this.kt = kt;
        this.kv = kv;
    }

    public double solveFreeSpeed(double voltage) {
        if (Math.abs(voltage) <= ks) {
            return 0.0;
        }
        return (voltage - Math.signum(voltage) * ks) / kv;
    }

    public double solveTorque(double voltage, double velocity) {
        // sign of ks is incorrect here because it varies based on the sign of velocity
        return (voltage - ks - kv * velocity) / kt;
    }
}
