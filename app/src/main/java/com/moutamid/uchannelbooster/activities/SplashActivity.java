package com.moutamid.uchannelbooster.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.moutamid.uchannelbooster.ContextWrapper;
import com.moutamid.uchannelbooster.R;
import com.moutamid.uchannelbooster.utils.Constants;
import com.moutamid.uchannelbooster.utils.Utils;

import java.util.Locale;
public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authListener;
    @Override
    protected void attachBaseContext(Context newBase) {
        Locale newLocale = new Locale(Utils.getString(Constants.CURRENT_LANGUAGE_CODE, "en"));

        Context context = ContextWrapper.wrap(newBase, newLocale);
        super.attachBaseContext(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();

        authListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                startActivity(new Intent(SplashActivity.this, BottomNavigationActivity.class));
            } else {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }
            finish();
        };


        new Handler().postDelayed(() -> mAuth.addAuthStateListener(authListener), 1500);
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        if (mAuth != null && authListener != null) {
            mAuth.removeAuthStateListener(authListener);
        }
    }
}


