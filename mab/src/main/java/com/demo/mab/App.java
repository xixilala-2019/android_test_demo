package com.demo.mab;

import android.app.Application;

/**
 * Created by hc on 2019.5.18.
 */
public class App extends Application {
    static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static App getInstance() {
        return instance;
    }
}
