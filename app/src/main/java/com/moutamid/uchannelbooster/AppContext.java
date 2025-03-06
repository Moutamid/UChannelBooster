package com.moutamid.uchannelbooster;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.moutamid.uchannelbooster.ui.subscribe.utilis.Stash;
import com.moutamid.uchannelbooster.utils.Utils;

public class AppContext extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
        Stash.init(this);
        FirebaseApp.initializeApp(this);

    }
}
