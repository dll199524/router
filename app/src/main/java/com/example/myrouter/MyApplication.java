package com.example.myrouter;

import android.app.Application;

import com.example.myrouter_api.core.MyRouter;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MyRouter.openDebug();
        MyRouter.getInstance().init(this);
    }
}
