package lib.physics;

public class DifferentialDrive {
    // Based on 254 kinematics
    private double wheelbase;

    public DifferentialDrive(double wheelbase) {
        this.wheelbase = wheelbase;
    }

    public WheelState forwardKinematics(ChassisState state) {
        return new WheelState(
            state.linear - state.angular * wheelbase * 0.5, 
            state.linear + state.angular * wheelbase * 0.5);
    }

    public ChassisState inverseKinematics(WheelState state) {
        return new ChassisState((state.right + state.left) * 0.5, (state.right - state.left) / wheelbase);
    }

    public static class ChassisState {
        public double linear;
        public double angular;

        public ChassisState(double linear, double angular) {
            this.linear = linear;
            this.angular = angular;
        }
    }

    public static class WheelState {
        public double left;
        public double right;

        public WheelState(double left, double right) {
            this.left = left;
            this.right = right;
        }
    }
}