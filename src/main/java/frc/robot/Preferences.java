/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

public class Preferences {

    // singleton instance
    private static Preferences INSTANCE = null;

    // This is a value that represents an unset preference.
    // No value should ever be set to this as it's used to detect
    // an unset value and then initialize it with something else.
    private static String RIO_PREF_UNSET_VALUE = "prefunset";

    // List of keys used with the wpilibj Preferences class.
    // These will be exposed in Shuffleboard NetworkTables Preferences list
    // so they should make sense to users of Shuffleboard.
    private static String RIO_PREF_KEY_ROBOT_NAME = "robotName";
    private static String RIO_STATS_TOTAL_BALLS = "Total Balls: "; 
    //private static String RIO_PREF_KEY.... = "...";

    // The wpilibj Preference instance used to fetch preferences from.
    edu.wpi.first.wpilibj.Preferences rioPrefs = null;

    /**
     * Get the singleton instance.
     * 
     * @return
     */
    public static Preferences getPreferences() {
        if (INSTANCE == null) {
            INSTANCE = new Preferences();
        }
        return INSTANCE;
    }

    private Preferences() {
        rioPrefs = edu.wpi.first.wpilibj.Preferences.getInstance();
    }

    /**
     * Code should if,else if on an expected name and error out if "unknown" but
     * should not expect "unknown" (instead, use a final else).
     * 
     * @return Name of the robot (eg, bot1, bot2, joe, etc...)
     */
    public String getRobotName() {
        return this.getInitializedValue(RIO_PREF_KEY_ROBOT_NAME, "unkown");
    }

    public int getBallsProcessed() {
        String key = RIO_STATS_TOTAL_BALLS;
        
        String value = this.getInitializedValue(key, Integer.toString(0));
        int balls = 0;
        try {
            balls = Integer.parseInt(value);
        } catch (Exception ex) {
            balls = 0;
        }

        return balls;
    }

    public void setBallsProcessed(int balls) {
        String key = RIO_STATS_TOTAL_BALLS;
        this.rioPrefs.putString(key, Integer.toString(balls));
    }
    /**
     * This method will fetch the preference with the specified key and if
     * not currently set will set it to the initializedValue.  Use this to
     * not only get a value but insure one is set in the wpilibj Preference's
     * storage (NetworkTables).
     * 
     * Values should NEVER be the RIO_PREF_UNSET_VALUE value.
     * 
     * TODO: This is for strings but could be adapted to work with multiple types through reflection.
     * Ex: Change initializedValue to an object, and check it's type to determine String, Double, Int, etc.
     * 
     * @param key
     * @param initializeValue
     * @return
     */
    private String getInitializedValue(String key, String initializeValue) {
        String value = this.rioPrefs.getString(key, RIO_PREF_UNSET_VALUE);

        if (value.equals(RIO_PREF_UNSET_VALUE) == true) {
            value = initializeValue;
            this.rioPrefs.putString(key, value);
        }

        return value;
    }
}