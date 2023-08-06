package com.moutamid.uchannelbooster.activities;

import static com.moutamid.uchannelbooster.utils.Constants.databaseReference;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.PurchaseData;
import com.anjlab.android.iab.v3.PurchaseInfo;
import com.anjlab.android.iab.v3.PurchaseState;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.uchannelbooster.ContextWrapper;
import com.moutamid.uchannelbooster.R;
import com.moutamid.uchannelbooster.utils.Constants;
import com.moutamid.uchannelbooster.utils.Utils;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler {
    private ProgressDialog progressDialog;


    private static final String TAG = "GoogleActivity";
    private static final String USER_INFO = "userinfo";
    private static final int RC_SIGN_IN = 9001;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private GoogleSignInClient mGoogleSignInClient;
    BillingProcessor bp;

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
//        Utils.changeLanguage(Utils.getString(Constants.CURRENT_LANGUAGE_CODE, "en"));
        setContentView(R.layout.activity_main);
        bp = BillingProcessor.newBillingProcessor(this, Constants.LICENSE_KEY, this);
        bp.initialize();

        bp.loadOwnedPurchasesFromGoogleAsync(new BillingProcessor.IPurchasesResponseListener() {
            @Override
            public void onPurchasesSuccess() {
                String currentPurchasedSubscription = Constants.ONE_WEEK_SUBSCRIPTION;

                if (Utils.getBoolean(Constants.ONE_WEEK_SUBSCRIPTION))
                    currentPurchasedSubscription = Constants.ONE_WEEK_SUBSCRIPTION;
                if (Utils.getBoolean(Constants.ONE_MONTH_SUBSCRIPTION))
                    currentPurchasedSubscription = Constants.ONE_MONTH_SUBSCRIPTION;
                if (Utils.getBoolean(Constants.THREE_MONTHS_SUBSCRIPTION))
                    currentPurchasedSubscription = Constants.THREE_MONTHS_SUBSCRIPTION;

                try {
                    PurchaseInfo info = bp.getPurchaseInfo(currentPurchasedSubscription);
                    Log.i(TAG, "onCreate12345: ResponseData: " + info.responseData);
                    PurchaseData data = info.purchaseData;
                    Log.i(TAG, "onCreate12345: Auto-renewing: " + data.autoRenewing);
                    PurchaseState purchaseState = data.purchaseState;

                    // IF PURCHASE IS NOT ACTIVE WHATSOEVER
                    if (purchaseState == PurchaseState.Canceled
                            || purchaseState == PurchaseState.Refunded
                            || purchaseState == PurchaseState.SubscriptionExpired
                            || !data.autoRenewing
                    ) {

                        Log.i(TAG, "onCreate12345: PurchaseState: NOT ACTIVE");
                        Utils.store(Constants.VIP_STATUS, false);

                        Utils.store(currentPurchasedSubscription, false);
                    }

                } catch (Exception e) {
                    Log.i(TAG, "onCreate12345: " + e.getMessage());
                    Log.i(TAG, "onCreate12345: PurchaseState: NOT ACTIVE");
                    Utils.store(Constants.VIP_STATUS, false);

                    Utils.store(currentPurchasedSubscription, false);
                }
                initViewsAndData();
            }

            @Override
            public void onPurchasesError() {
                Log.i(TAG, "onPurchasesError: 12345");
                initViewsAndData();

            }
        });

    }

    private void initViewsAndData() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            finish();
            startActivity(new Intent(MainActivity.this, BottomNavigationActivity.class));
            return;
        }

        findViewById(R.id.main_activity_layout).setVisibility(View.VISIBLE);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading));

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("890062472575-05ue90j9694eg6eorj1lhjoknkj3oqja.apps.googleusercontent.com")
//                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // [END config_signin]

        // [START initialize_auth]
        // Initialize Firebase Auth

        findViewById(R.id.signInButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                signIn();

            }
        });

    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.

    }
    // [END on_start_check_user]

    // [START onactivityresult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                progressDialog.dismiss();
                Toast.makeText(this, "Google sign in failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Google sign in failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }
                    }
                });
    }
    // [END auth_with_google]

    // [START signin]
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signin]

    private void updateUI(FirebaseUser user) {
        if (user != null) {

            uploadUserInfoToDatabase(user);
        }
    }

    private void uploadUserInfoToDatabase(FirebaseUser user) {


        databaseReference().child(USER_INFO).child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {

                    UserDetails userDetails = new UserDetails(user.getEmail(), 600, false);

                    databaseReference().child(USER_INFO).child(user.getUid())
                            .setValue(userDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        progressDialog.dismiss();
                                        finish();
                                        startActivity(new Intent(MainActivity.this, BottomNavigationActivity.class));
                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                } else {
                    progressDialog.dismiss();
                    finish();
                    startActivity(new Intent(MainActivity.this, BottomNavigationActivity.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Database failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onCancelled: " + error.getMessage());
            }
        });

    }

    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable PurchaseInfo details) {

    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {

    }

    @Override
    public void onBillingInitialized() {

    }

    private static class UserDetails {

        private String email;
        private int coins;
        private boolean vipStatus;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public int getCoins() {
            return coins;
        }

        public void setCoins(int coins) {
            this.coins = coins;
        }

        public boolean isVipStatus() {
            return vipStatus;
        }

        public void setVipStatus(boolean vipStatus) {
            this.vipStatus = vipStatus;
        }

        public UserDetails(String email, int coins, boolean vipStatus) {
            this.email = email;
            this.coins = coins;
            this.vipStatus = vipStatus;
        }

        UserDetails() {
        }
    }

}