package com.shen.shengeunion.base;

import android.app.Application;
import android.content.Context;

/**
 * application对象只有一个，
 */
public class BaseApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getBaseContext();
    }

    public static Context getContext() {
        return context;
    }
}
