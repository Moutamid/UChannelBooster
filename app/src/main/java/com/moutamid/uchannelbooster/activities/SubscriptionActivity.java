package com.moutamid.uchannelbooster.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.uchannelbooster.R;
import com.moutamid.uchannelbooster.databinding.ActivitySubscriptionBinding;
import com.moutamid.uchannelbooster.models.UserDetails;
import com.moutamid.uchannelbooster.ui.subscribe.utilis.Stash;
import com.moutamid.uchannelbooster.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SubscriptionActivity extends AppCompatActivity {
    private static final int PICK_FROM_GALLERY = 1001;
    String selectedPrice = "€130";
    ActivitySubscriptionBinding binding;
    ImageView imageView, icon;
    Uri image = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySubscriptionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showManualPaymentInfo();

            }
        });
        binding.back.setOnClickListener(v -> {
            onBackPressed();
        });

        showManualPaymentInfo();


    }

    @Override
    protected void onResume() {
        super.onResume();
        Constants.initDialog(this);
    }


    private void showManualPaymentInfo() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.manual_pay_info);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.BottomSheetAnim;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();

        MaterialCardView manual = dialog.findViewById(R.id.manual);
        MaterialCardView cancel = dialog.findViewById(R.id.back);
        MaterialCardView upload = dialog.findViewById(R.id.upload);
        CardView imageCard = dialog.findViewById(R.id.imageCard);
        imageView = dialog.findViewById(R.id.imageView);
        icon = dialog.findViewById(R.id.icon);

        cancel.setOnClickListener(v -> dialog.cancel());
        upload.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(Intent.createChooser(intent, ""), PICK_FROM_GALLERY);
        });

        imageCard.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(Intent.createChooser(intent, ""), PICK_FROM_GALLERY);
        });

        manual.setOnClickListener(v -> {
            if (image == null) {
                Toast.makeText(this, "Please upload a proof image", Toast.LENGTH_SHORT).show();
                return;
            }


            uploadProof();
            dialog.cancel();
        });
    }

    private void uploadProof() {
        Constants.showDialog();
        String date = new SimpleDateFormat("ddMMyyyyhhmmss", Locale.getDefault()).format(new Date());
        Constants.storageReference(Constants.auth().getCurrentUser().getUid()).child("proof").child(date).putFile(image)
                .addOnSuccessListener(taskSnapshot -> {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                        upload(uri.toString());
                    });
                })
                .addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void upload(String imageLink) {
        if (imageLink == null || imageLink.isEmpty()) {
            Log.e("ImageUpload", "Image link is null or empty");
            return;
        }

        Constants.databaseReference().child("userinfo")
                .child(Constants.auth().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            UserDetails userModel = snapshot.getValue(UserDetails.class);
                            if (userModel == null) {
                                Log.e("FirebaseError", "UserDetails model is null");
                                return;
                            }

                            String paymentId = Constants.databaseReference().child(Constants.PAYMENTS).push().getKey();
                            Map<String, Object> paymentData = new HashMap<>();
                            paymentData.put("userId", Constants.auth().getCurrentUser().getUid());
                            paymentData.put("email", userModel.getEmail());
                            paymentData.put("amount", Stash.getString("amount"));
                            paymentData.put("imageLink", imageLink);
                            paymentData.put("approve", false);
                            paymentData.put("key", paymentId);
                            paymentData.put("current_coins", String.valueOf(userModel.getCoins()));
                            paymentData.put("need_coins", Stash.getString("coins"));
                            Constants.databaseReference().child(Constants.PAYMENTS)
                                    .child(paymentId)
                                    .updateChildren(paymentData)
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(SubscriptionActivity.this, "Thank you for being our valuable user", Toast.LENGTH_SHORT).show();
                                        onBackPressed();
                                    }).addOnFailureListener(e -> {
                                        Log.e("FirebaseError", e.getLocalizedMessage());
                                        Constants.dismissDialog();
                                        Toast.makeText(SubscriptionActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Log.e("FirebaseError", "User snapshot does not exist.");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("FirebaseError", error.getMessage());
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FROM_GALLERY && resultCode == RESULT_OK && data != null && data.getData() != null) {
            image = data.getData();
            icon.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            Glide.with(this).load(image).placeholder(R.drawable.profile_icon).into(imageView);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


}