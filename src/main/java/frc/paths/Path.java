package frc.paths;

public abstract class Path {
    public abstract double[][] getPath();
    public static int X = 0;
    public static int Y = 1;
    public static int HEADING = 2;
    public static int LEFT_VELOCITY = 3;
    public static int RIGHT_VELOCITY = 4;
    public static int CENTER_POSITION = 5;
}