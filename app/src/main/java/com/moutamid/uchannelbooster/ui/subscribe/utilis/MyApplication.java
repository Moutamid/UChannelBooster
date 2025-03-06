package com.moutamid.uchannelbooster.ui.subscribe.utilis;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.moutamid.uchannelbooster.ui.subscribe.utilis.Stash;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Stash.init(this);
        FirebaseApp.initializeApp(this);
       // Utils.init(this);
    }
}
