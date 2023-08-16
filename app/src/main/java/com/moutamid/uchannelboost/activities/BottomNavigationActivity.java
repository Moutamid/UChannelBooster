package com.moutamid.uchannelboost.activities;

import static com.moutamid.uchannelboost.utils.Constants.databaseReference;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.moutamid.uchannelboost.ContextWrapper;
import com.moutamid.uchannelboost.R;
import com.moutamid.uchannelboost.notifications.FirebaseMessagingService;
import com.moutamid.uchannelboost.utils.Constants;
import com.moutamid.uchannelboost.utils.Utils;

import java.util.Locale;

import np.com.susanthapa.curved_bottom_navigation.CbnMenuItem;
import np.com.susanthapa.curved_bottom_navigation.CurvedBottomNavigationView;

public class BottomNavigationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "BottomNavigationActivit";

    private DrawerLayout drawerLayout;
    //    private FrameLayout frameLayout;
    private NavigationView navigationView;

    private FirebaseAuth mAuth;
    private TextView coinsTextView;
    private static final String USER_INFO = "userinfo";

    private RelativeLayout topHeaderLayout;
    private BottomNavigationView navView;

    public void hideNavBar() {

        if (navView == null) {
            navView = findViewById(R.id.nav_view);
        }
        navView.setVisibility(View.GONE);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        Locale newLocale = new Locale(Utils.getString(Constants.CURRENT_LANGUAGE_CODE, "en"));

        Context context = ContextWrapper.wrap(newBase, newLocale);
        super.attachBaseContext(context);
    }

    String Texttoken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Utils.changeLanguage(Utils.getString(Constants.CURRENT_LANGUAGE_CODE, "en"));
        Constants.adjustFontScale(this);
        setContentView(R.layout.activity_bottom_navigation);

        FirebaseMessaging.getInstance().subscribeToTopic("all");
        this.Texttoken = getSharedPreferences(FirebaseMessagingService.MY_PREFS_NAME, 0)
                .getString("name", "No name defined");

        coinsTextView = findViewById(R.id.coins_text_view_bottom_navigation);
        topHeaderLayout = findViewById(R.id.top_header_layout_bottom_navigation);
        navView = findViewById(R.id.nav_view);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {

            finish();
            startActivity(new Intent(BottomNavigationActivity.this, MainActivity.class));
            return;
        }
        initNavigationMenu();

        initializeViews();

        toggleDrawer();
        initializeDefaultFragment(savedInstanceState, 0);

        getCoinsAmount();

        try {
            ImageView imageView = navigationView.findViewById(R.id.signOutBtnNavigation);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    closeDrawer();
                    clearAppData();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "onCreate: " + e.getMessage());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Utils.toast(e.getMessage());
                }
            });
        }

    }

    private void initializeDefaultFragment(Bundle savedInstanceState, int itemIndex) {
        /*if (savedInstanceState == null) {
            MenuItem menuItem = navigationView.getMenu().getItem(itemIndex).setChecked(true);
            onNavigationItemSelected(menuItem);
        }
        navView.setSelectedItemId(R.id.navigation_subscribe);*/
    }

    private void toggleDrawer() {
//        new ActionBarDrawerToggle()

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        //Checks if the navigation drawer is open -- If so, close it
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        // If drawer is already close -- Do not override original functionality
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.buy_points_nav_option) {
            navView.getMenu().getItem(0).setChecked(true);
            navView.setSelectedItemId(R.id.navigation_points);
            closeDrawer();
        } else if (itemId == R.id.subscribe_nav_option) {
            navView.getMenu().getItem(1).setChecked(true);
            navView.setSelectedItemId(R.id.navigation_subscribe);
            closeDrawer();
        } else if (itemId == R.id.like_nav_option) {
            navView.getMenu().getItem(2).setChecked(true);
            navView.setSelectedItemId(R.id.navigation_like);
            closeDrawer();
        } else if (itemId == R.id.view_nav_option) {
            navView.getMenu().getItem(3).setChecked(true);
            navView.setSelectedItemId(R.id.navigation_view);
            closeDrawer();
        } else if (itemId == R.id.campaign_nav_option) {
            navView.getMenu().getItem(4).setChecked(true);
            navView.setSelectedItemId(R.id.navigation_campaign);
            closeDrawer();
        } else if (itemId == R.id.privacy_policy_nav_option) {
            closeDrawer();
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://palconbooster.blogspot.com/p/privacy-policy.html")));
        } else if (itemId == R.id.change_language_nav_option) {
            closeDrawer();
            showLanguageDialog();
        } else if (itemId == R.id.exit_nav_option) {
            closeDrawer();
            clearAppData();
        }
        return true;
    }

    private void showLanguageDialog() {
        AlertDialog dialog;

        AlertDialog.Builder builder = new AlertDialog.Builder(BottomNavigationActivity.this);
        final CharSequence[] items = {
                "English",
                "Korean",
                "Japanese",
                "Spanish",
                "Hindi (India)",
                "French",
                "Arabic",
                "Indonesian",
                "Vietnamese",
                "Urdu (Pakistan)"};
        final CharSequence[] code = {
                Constants.LANGUAGE_CODE_ENGLISH,
                Constants.LANGUAGE_CODE_KOREAN,
                Constants.LANGUAGE_CODE_JAPANESE,
                Constants.LANGUAGE_CODE_SPANISH,
                Constants.LANGUAGE_CODE_HINDI,
                Constants.LANGUAGE_CODE_FRENCH,
                Constants.LANGUAGE_CODE_ARABIC,
                Constants.LANGUAGE_CODE_INDONESIAN,
                Constants.LANGUAGE_CODE_VIETNAMESE,
                Constants.LANGUAGE_CODE_URDU};
        builder.setTitle("Change language");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                dialog.dismiss();
                Utils.store(Constants.CURRENT_LANGUAGE_CODE, String.valueOf(code[position]));
                try {
                    Utils.changeLanguage(String.valueOf(code[position]));
//                attachBaseContext(BottomNavigationActivity.this);
                    recreate();
                } catch (Exception e) {
                    Log.e(TAG, "onClick: ERROR: " + e.getMessage());
                }
            }
        });

        dialog = builder.create();
        dialog.show();
    }

    private void clearAppData() {

        new AlertDialog.Builder(BottomNavigationActivity.this)
                .setTitle(getString(R.string.are_you_sure))
                .setMessage(getString(R.string.do_you_really_wanna_log_out))
                .setNegativeButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            // clearing app data
                            if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {
                                ((ActivityManager) getSystemService(ACTIVITY_SERVICE))
                                        .clearApplicationUserData(); // note: it has a return value!
                            } else {
                                String packageName = getApplicationContext().getPackageName();
                                Runtime runtime = Runtime.getRuntime();
                                runtime.exec("pm clear " + packageName);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setPositiveButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setCancelable(true)
                .show();
    }

    private void closeDrawer() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }


    private void initializeViews() {
        drawerLayout = findViewById(R.id.container);
//        frameLayout = findViewById(R.id.framelayout_id);
        navigationView = findViewById(R.id.navigationview_id);
        navigationView.setNavigationItemSelectedListener(this);

        TextView emailTextView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_header_user_name_id);
        try {
            emailTextView.setText(mAuth.getCurrentUser().getEmail());
        } catch (Exception e) {
            e.printStackTrace();
        }

        pointsTextView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_header_points);

        findViewById(R.id.menu_option_bottomm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    TextView pointsTextView;

    private void getCoinsAmount() {

        databaseReference().child(USER_INFO).child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String value = String.valueOf(snapshot.child("coins").getValue(Integer.class));
                    coinsTextView.setText(value);
                    pointsTextView.setText(getString(R.string.points) + value);
                } else {
                    coinsTextView.setText("0");
                    pointsTextView.setText(getString(R.string.points) + "0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: " + error.getMessage());
            }
        });
    }

    private void initNavigationMenu() {
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_campaign, R.id.navigation_view, R.id.navigation_points)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        //ic_baseline_subscriptions_24
        //ic_baseline_thumb_up_alt_24
        //ic_baseline_play_arrow_24
        //ic_baseline_campaign_24
        //ic_money_points_24

        CurvedBottomNavigationView navigationView = findViewById(R.id.curveNavView);

        CbnMenuItem[] items = {
                new CbnMenuItem(R.drawable.ic_baseline_subscriptions_24, R.drawable.avd_subscription, R.id.navigation_subscribe),
                new CbnMenuItem(R.drawable.ic_baseline_thumb_up_alt_24, R.drawable.avd_like, R.id.navigation_like),
                new CbnMenuItem(R.drawable.ic_baseline_play_arrow_24, R.drawable.avd_view, R.id.navigation_view),
                new CbnMenuItem(R.drawable.ic_baseline_campaign_24, R.drawable.avd_campaign, R.id.navigation_campaign),
                new CbnMenuItem(R.drawable.ic_money_points_24, R.drawable.avd_points, R.id.navigation_points)
        };

        navigationView.setMenuItems(items, 0);
        navigationView.setupWithNavController(navController);

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.

    }

    public void showNavBar() {
        if (navView == null) {
            navView = findViewById(R.id.nav_view);
        }
        navView.setVisibility(View.VISIBLE);
    }

    public void hideTopHeader() {
        if (topHeaderLayout == null) {
            topHeaderLayout = findViewById(R.id.top_header_layout_bottom_navigation);
        }

        topHeaderLayout.setVisibility(View.GONE);
    }

    public void showTopHeader() {
        if (topHeaderLayout == null) {
            topHeaderLayout = findViewById(R.id.top_header_layout_bottom_navigation);
        }

        topHeaderLayout.setVisibility(View.VISIBLE);
    }
}