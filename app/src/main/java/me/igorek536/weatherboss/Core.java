package me.igorek536.weatherboss;

import android.content.Context;

/**
 * Core class where application context is defined!
 */

public class Core {
    private static Context appContext;

    public void setAppContext(Context context) {
        appContext = context;
    }

    public Context getAppContext() {
        return appContext;
    }
}
