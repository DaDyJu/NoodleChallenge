package com.kkadadeepju.snwf.sendnoodswithfriends;

import android.content.Context;
import android.content.SharedPreferences;

import static com.kkadadeepju.snwf.sendnoodswithfriends.Constants.PREFERENCE_NAME;
import static com.kkadadeepju.snwf.sendnoodswithfriends.Constants.USER_GAME_NAME;

/**
 * Created by Junyu on 2017-04-22.
 */

public class NCUserPreference {

    public static String getUserGameName(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, context.MODE_PRIVATE);
        return sharedPreferences.getString(USER_GAME_NAME, "DaDyJu");
    }

    public static void setUserGameName(Context context, String name) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, context.MODE_PRIVATE);
        sharedPreferences.edit().putString(USER_GAME_NAME, name).apply();
    }

    public static Boolean isUserNameSet(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, context.MODE_PRIVATE);
        return sharedPreferences.contains(USER_GAME_NAME);
    }

}
