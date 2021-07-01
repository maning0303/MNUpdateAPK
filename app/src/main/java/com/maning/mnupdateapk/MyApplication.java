package com.maning.mnupdateapk;

import android.app.Application;

import com.hjq.permissions.XXPermissions;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        XXPermissions.setScopedStorage(true);
    }
}
