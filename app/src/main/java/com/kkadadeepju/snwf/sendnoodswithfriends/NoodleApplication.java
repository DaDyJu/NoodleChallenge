package com.kkadadeepju.snwf.sendnoodswithfriends;

import android.app.Application;

import timber.log.Timber;

/**
 * Created by Junyu on 2017-04-22.
 */

public class NoodleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}
