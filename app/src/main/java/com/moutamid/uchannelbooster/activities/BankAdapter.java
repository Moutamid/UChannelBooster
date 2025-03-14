package com.moutamid.uchannelbooster.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.moutamid.uchannelbooster.R;
import com.moutamid.uchannelbooster.models.BankModel;
import com.moutamid.uchannelbooster.ui.subscribe.utilis.Stash;
import com.moutamid.uchannelbooster.utils.Constants;
import com.moutamid.uchannelbooster.utils.OnImageSelectedListener;
import com.moutamid.uchannelbooster.utils.UserModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class BankAdapter extends RecyclerView.Adapter<BankAdapter.BankViewHolder> {
    private Context context;
    private List<BankModel> bankList;
    private DatabaseReference databaseReference;
    String selectedPlan = Constants.VIP_YEAR;
    MaterialCardView selectedCard;
    String selectedPrice = "€130";
    public static Uri image = null;
    public static int pos = 0;
    OnImageSelectedListener listener;
    static TextView bankName;
    static TextView accountHolder;
    static TextView accountNumber;
    static TextView extraInfo;

    private static final int PICK_FROM_GALLERY = 1001;

    public BankAdapter(Context context, List<BankModel> bankList, OnImageSelectedListener listener) {
        this.context = context;
        this.bankList = bankList;
        this.listener = listener;

        databaseReference = Constants.databaseReference().child("Banks");
    }

    @NonNull
    @Override
    public BankViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.manual_pay_info, parent, false);
        return new BankViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BankViewHolder holder, int position) {
        BankModel bank = bankList.get(position);
        bankName.setText("Bank Name: " + bank.getBankName());
        accountHolder.setText("Account Holder Name: " + bank.getAccountHolder());
        accountNumber.setText("Account Number: " + bank.getAccountNumber());
        extraInfo.setText("Additional Info: " + bank.getExtraInfo());

        if (!bank.getLogoUrl().isEmpty()) {
            Glide.with(context).load(bank.getLogoUrl()).into(holder.bankLogo);
        }

        if (bank.getImage() != null) {
            holder.imageView.setVisibility(View.VISIBLE);
            Glide.with(context).load(bank.getImage()).into(holder.imageView);
        } else {
            holder.imageView.setVisibility(View.GONE);
        }

        holder.upload.setOnClickListener(v -> pickImage(position));
        holder.imageCard.setOnClickListener(v -> pickImage(position));


        holder.manual.setOnClickListener(v -> {
            if (image == null) {
                Toast.makeText(context, "Please upload a proof image", Toast.LENGTH_SHORT).show();
                return;
            }
            String selectedType = Stash.getString(Constants.TYPE);
            if (bank.getImage() != null) {
                uploadProof(selectedType);
            }
            else
            {
                Toast.makeText(context, "Please upload image", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return bankList.size();
    }

    public static class BankViewHolder extends RecyclerView.ViewHolder {
        ImageView bankLogo;
        MaterialCardView manual;
        MaterialCardView upload;
        CardView imageCard;
        public static ImageView imageView, icon;

        public BankViewHolder(@NonNull View itemView) {
            super(itemView);
            bankName = itemView.findViewById(R.id.bankName);
            accountHolder = itemView.findViewById(R.id.accountHolder);
            accountNumber = itemView.findViewById(R.id.accountNumber);
            extraInfo = itemView.findViewById(R.id.extraInfo);
            bankLogo = itemView.findViewById(R.id.bankLogo);
            manual = itemView.findViewById(R.id.manual);
            upload = itemView.findViewById(R.id.upload);
            imageCard = itemView.findViewById(R.id.imageCard);
            imageView = itemView.findViewById(R.id.imageView);
            icon = itemView.findViewById(R.id.icon);
//
        }
    }

    private void uploadProof(String selectedType) {
        Constants.showDialog();
        String date = new SimpleDateFormat("ddMMyyyyhhmmss", Locale.getDefault()).format(new Date());
        Constants.storageReference(Constants.auth().getCurrentUser().getUid()).child("proof").child(date).putFile(image)
                .addOnSuccessListener(taskSnapshot -> {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                        upload(uri.toString(), selectedType);
                    });
                })
                .addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void upload(String imageLink, String selectedType) {
        String duration;
        if (selectedPlan.equals(Constants.VIP_YEAR)) {
            duration = "YEAR";
        } else if (selectedPlan.equals(Constants.VIP_6_MONTH)) {
            duration = "6 MONTH";
        } else {
            duration = "3 MONTH";
        }

        UserModel userModel = (UserModel) Stash.getObject(Constants.STASH_USER, UserModel.class);
        PaymentModel paymentModel = new PaymentModel(
                UUID.randomUUID().toString(),
                userModel.getKey(),
                userModel.getName(),
                userModel.getEmail(),
                selectedPrice,
                duration,
                imageLink,
                Stash.getString("type", "user"),
                new Date().getTime(),
                false,
                bankName.getText().toString(),
                accountHolder.getText().toString(),
                accountNumber.getText().toString());

        Constants.databaseReference().child(Constants.COURSE_PAYMENTS).child(FirebaseAuth.getInstance().getUid())
                .setValue(paymentModel)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(context, "Thank you for being our valuable user", Toast.LENGTH_SHORT).show();
                    context.startActivity(new Intent(context, BuyPointsActivity.class));
//                    context.finishAffinity();
                })
                .addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                });

//        Constants.databaseReference().child(Constants.PAYMENTS).child(userModel.getID()).child(paymentModel.getID())
//                .setValue(paymentModel)
//                .addOnSuccessListener(unused -> {
//
//                })
//                .addOnFailureListener(e -> {
//                    Constants.dismissDialog();
//                    Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//                });
    }

    private void pickImage(int position) {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pos = position;
        ((SubscriptionActivity) context).startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_FROM_GALLERY);
    }


}
