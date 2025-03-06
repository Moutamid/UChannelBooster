package com.moutamid.uchannelbooster.activities;

import static com.moutamid.uchannelbooster.utils.Constants.databaseReference;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.PurchaseInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.uchannelbooster.ContextWrapper;
import com.moutamid.uchannelbooster.R;
import com.moutamid.uchannelbooster.databinding.ActivityBuyPointsBinding;
import com.moutamid.uchannelbooster.utils.Constants;
import com.moutamid.uchannelbooster.utils.Utils;

import java.util.HashMap;
import java.util.Locale;

public class BuyPointsActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler {
    private static final String TAG = "BuyPointsActivity";
    private final Context context = BuyPointsActivity.this;

    private ActivityBuyPointsBinding b;


    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

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
//        Utils.changeLanguage(Utils.getString(Constants.CURRENT_LANGUAGE_CODE, "en"));
        Constants.adjustFontScale(this);
        b = ActivityBuyPointsBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        getCoinsAmount();

        bp = BillingProcessor.newBillingProcessor(this, Constants.LICENSE_KEY, this);
        bp.initialize();

        b.buyPoints1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                buyProduct(Constants.ONE_DOLLAR_PRODUCT);
                bp.purchase(BuyPointsActivity.this, Constants.ONE_DOLLAR_PRODUCT);
            }
        });

        b.buyPoints2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                buyProduct(Constants.TWO_DOLLAR_PRODUCT);
                bp.purchase(BuyPointsActivity.this, Constants.TWO_DOLLAR_PRODUCT);
            }
        });
        b.buyPoints3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                buyProduct(Constants.FIVE_DOLLAR_PRODUCT);
                bp.purchase(BuyPointsActivity.this, Constants.FIVE_DOLLAR_PRODUCT);
            }
        });

        b.buyPoints4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                buyProduct(Constants.TEN_DOLLAR_PRODUCT);
                bp.purchase(BuyPointsActivity.this, Constants.TEN_DOLLAR_PRODUCT);
            }
        });
        b.buyPoints5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                buyProduct(Constants.TWENTY_FIVE_DOLLAR_PRODUCT);
                bp.purchase(BuyPointsActivity.this, Constants.TWENTY_FIVE_DOLLAR_PRODUCT);
            }
        });

        /*int AMOUNT = getBoughtCoinsAmount(Constants.TEN_DOLLAR_PRODUCT);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(Constants.USER_ID, mAuth.getUid());
        hashMap.put(Constants.PRODUCT_TYPE, Constants.TEN_DOLLAR_PRODUCT);
        hashMap.put(Constants.PURCHASE_DATE, Utils.getDate());

        // ADD VALUES OF NEW PURCHASE TO DATABASE
        databaseReference().child(Constants.PATH_PRODUCTS)
                .push()
                .setValue(hashMap);*/

    }

    private void buyProduct(String productId) {
        bp.consumePurchaseAsync(productId, new BillingProcessor.IPurchasesResponseListener() {
            @Override
            public void onPurchasesSuccess() {
                Utils.toast(getString(R.string.purchase_successfull));
                int AMOUNT = getBoughtCoinsAmount(productId);
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put(Constants.USER_ID, mAuth.getUid());
                hashMap.put(Constants.PRODUCT_TYPE, productId);
                hashMap.put(Constants.PURCHASE_DATE, Utils.getDate());

                // ADD VALUES OF NEW PURCHASE TO DATABASE
                databaseReference().child(Constants.PATH_PRODUCTS)
                        .push()
                        .setValue(hashMap);

                // INCREASE USER AMOUNT
                databaseReference().child(Constants.USER_INFO).child(mAuth.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String value = String.valueOf(snapshot.child(Constants.COINS).getValue(Integer.class));
                            databaseReference().child(Constants.USER_INFO)
                                    .child(mAuth.getUid())
                                    .child(Constants.COINS)
                                    .setValue(value + AMOUNT);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }

            @Override
            public void onPurchasesError() {
                Utils.toast(getString(R.string.purchasenotsuccessfull));
            }
        });
    }

    private int getBoughtCoinsAmount(String productId) {

        switch (productId) {
            case Constants.ONE_DOLLAR_PRODUCT:
                return Constants.FIFTEEN_K;
            case Constants.TWO_DOLLAR_PRODUCT:
                return Constants.FORTY_FIVE_K;
            case Constants.FIVE_DOLLAR_PRODUCT:
                return Constants.ONE_HUNDRED_N_TWENTY_FIVE_K;
            case Constants.TEN_DOLLAR_PRODUCT:
                return Constants.THREE_HUNDRED_K;
            case Constants.TWENTY_FIVE_DOLLAR_PRODUCT:
                return Constants.SEVEN_HUNDRED_K;
            default:
                return Constants.FIFTEEN_K;

        }
    }

    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable PurchaseInfo details) {
        Utils.toast(getString(R.string.purchase_successfull));
        int AMOUNT = getBoughtCoinsAmount(productId);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(Constants.USER_ID, mAuth.getUid());
        hashMap.put(Constants.PRODUCT_TYPE, productId);
        hashMap.put(Constants.PURCHASE_DATE, Utils.getDate());

        // ADD VALUES OF NEW PURCHASE TO DATABASE
        databaseReference().child(Constants.PATH_PRODUCTS)
                .push()
                .setValue(hashMap);

        // INCREASE USER AMOUNT
        databaseReference().child(Constants.USER_INFO).child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String value = String.valueOf(snapshot.child(Constants.COINS).getValue(Integer.class));
                    databaseReference().child(Constants.USER_INFO)
                            .child(mAuth.getUid())
                            .child(Constants.COINS)
                            .setValue(value + AMOUNT);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public void onPurchaseHistoryRestored() {
//        Utils.toast("onPurchaseHistoryRestored");
    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {
        Utils.toast("onBillingError: code: " + errorCode + " \n" + error.getMessage());
    }

    @Override
    public void onBillingInitialized() {
//        Utils.toast("onBillingInitialized");

//        if (bp.isConnected()) {
//            Utils.toast("CONNECTED!");
//        }
    }

    @Override
    public void onDestroy() {
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
    }

    private void getCoinsAmount() {

        TextView coinsTextView;

        coinsTextView = findViewById(R.id.coins_text_view_buy_points);
        databaseReference().child(Constants.USER_INFO).child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String value = String.valueOf(snapshot.child(Constants.COINS).getValue(Integer.class));
                    coinsTextView.setText(value);
                } else {
                    coinsTextView.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                String TAG = "oncancelled";
                Log.d(TAG, "onCancelled: " + error.getMessage());
            }
        });
    }

    /*//------------------------------------------------------------------------------------------
//    private BillingClient billingClient;
//    private List<String> skuList;

    public void inAppPurchases() {
        // To be implemented in a later section.
        PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
            @Override
            public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> purchases) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                    SharedPreferences.Editor edit = prefs.edit();
                    edit.putBoolean(context.getString(R.string.adsubscribed), Boolean.TRUE);
                    edit.apply();
                    Toast.makeText(context, "Thanks for purchase.", Toast.LENGTH_SHORT).show();
                }
            }
        };
        ///create client billing
        billingClient = BillingClient.newBuilder(this)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();
        //make connect to google play store
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {

            }

            @Override
            public void onBillingServiceDisconnected() {

            }
        });
//        skuProductList();
    }

    public void btSubscribe(View view) {
        if (getPurchaseSharedPreference()) {
            Toast.makeText(context, "Already Subscribed", Toast.LENGTH_SHORT).show();
        } else {

            if (billingClient.isReady()) {
                skuQueryOnContinue();
            } else {
                Toast.makeText(context, "Something wrong!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void skuQueryOnContinue() {
        try {
            SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
            if (skuList.size() > 0) {

                params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS);
                billingClient.querySkuDetailsAsync(params.build(), new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> list) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            // crashes due to null list of SkuDetails objects //
                            try {
                                assert list != null;
                                BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                                        .setSkuDetails(list.get(0))
                                        .build();
                                int responseCode = billingClient.launchBillingFlow(BuyPointsActivity.this,
                                        flowParams).getResponseCode();
                                Utils.toast(responseCode + "");
                            } catch (Exception ignored) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Utils.toast("ERROR: App need to be published!");//+ignored.getMessage());
                                    }
                                });
                            }
                        } else {
                            Utils.toast("Billing response is not ok");
                        }
                    }
                });
            } else Utils.toast("Size is 0");
        } catch (Exception ignored) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Utils.toast("ERROR: App need to be published!");//"FIRST CATCH: "+ignored.getMessage());
                }
            });
        }
    }

    public void skuProductList() {
        skuList = new ArrayList<>();
        skuList.add(getResources().getString(R.string.per_6_month_subcription));
    }

    public boolean getPurchaseSharedPreference() {
        SharedPreferences prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        return prefs.getBoolean(this.getString(R.string.adsubscribed), false);
    }*/
}