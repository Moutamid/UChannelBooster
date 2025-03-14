package com.moutamid.uchannelbooster.activities;

import static com.moutamid.uchannelbooster.utils.Constants.databaseReference;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.PurchaseInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.uchannelbooster.ContextWrapper;
import com.moutamid.uchannelbooster.R;
import com.moutamid.uchannelbooster.databinding.ActivityBuyPointsBinding;
import com.moutamid.uchannelbooster.models.CoinPack;
import com.moutamid.uchannelbooster.utils.Constants;
import com.moutamid.uchannelbooster.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class BuyPointsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CoinPackAdapter adapter;
    private List<CoinPack> coinPackList = new ArrayList<>();
    private DatabaseReference reference;
    private ActivityBuyPointsBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Constants.adjustFontScale(this);
        b = ActivityBuyPointsBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Adapter
        adapter = new CoinPackAdapter(coinPackList, this);
        recyclerView.setAdapter(adapter);

        // Firebase Reference
        reference = Constants.databaseReference().child(Constants.COIN_PACK);

        // Load Data
        loadCoinPacks();
    }

    private void loadCoinPacks() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                coinPackList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    CoinPack coinPack = data.getValue(CoinPack.class);
                    coinPackList.add(coinPack);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(BuyPointsActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
