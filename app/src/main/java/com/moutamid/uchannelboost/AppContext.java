package com.moutamid.uchannelboost;

import android.app.Application;

import com.moutamid.uchannelboost.utils.Utils;

public class AppContext extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
    }
}
