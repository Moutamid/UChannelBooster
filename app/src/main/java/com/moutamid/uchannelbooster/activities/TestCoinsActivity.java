package com.moutamid.uchannelbooster.activities;

import static com.moutamid.uchannelbooster.utils.Constants.databaseReference;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.uchannelbooster.R;

public class TestCoinsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tse_coins);

        getCoinsAmount();
    }

    private void getCoinsAmount() {

        FirebaseAuth mAuth;
        TextView coinsTextView;
        String USER_INFO = "userinfo";
        coinsTextView = findViewById(R.id.coins_text_view_buy_coins);
        mAuth = FirebaseAuth.getInstance();


        databaseReference().child(USER_INFO).child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String value = String.valueOf(snapshot.child("coins").getValue(Integer.class));
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
}