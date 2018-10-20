package com.ezzat.spofi.Control;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by pkharche on 10/04/18.
 */
public class SharedValues {

    private static final String SHARED_PREFS = "shared_values";

    public static String getValueS(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        String value = sharedPreferences.getString(key, null);
        return value;
    }

    public static boolean getValueB(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        boolean value = sharedPreferences.getBoolean(key, true);
        return value;
    }

    public static int getValueI(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        int value = sharedPreferences.getInt(key, 0);
        return value;
    }

    public static void saveValue(Context context, String key, Object value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (value.getClass().equals(String.class)) {
            editor.putString(key, (String) value);
        } else if (value.getClass().equals(Integer.class)) {
            editor.putInt(key, (Integer) value);
        } else if (value.getClass().equals(Boolean.class)) {
            editor.putBoolean(key, (Boolean) value);
        }
        editor.apply();
    }

    public static void resetTripValues(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        /*editor.putString(Constants.TRIP_ID, null);
        editor.putString(Constants.TRIP_IS_ACCEPTED, null);
        editor.putString(Constants.PICKUP_PLACE, null);
        editor.putString(Constants.DROPOFF_PLACE, null);*/

        editor.apply();
    }

    public static void resetAllValues(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        /*editor.putString(Constants.USER_ID, null);
        editor.putString(Constants.TRIP_ID, null);
        editor.putString(Constants.TRIP_IS_ACCEPTED, null);
        editor.putString(Constants.PICKUP_PLACE, null);
        editor.putString(Constants.DROPOFF_PLACE, null);*/

        editor.apply();
    }
}
