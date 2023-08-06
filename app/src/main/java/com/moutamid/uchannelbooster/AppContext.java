package com.moutamid.uchannelbooster;

import android.app.Application;

import com.moutamid.uchannelbooster.utils.Utils;

public class AppContext extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
    }
}
