package com.parker.adsdk;

import android.app.Application;

/**
 * Created by thinkpad on 2016/8/30.
 */
public class AdApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        WorkHelper.sdkInitialize(this);
    }
}
