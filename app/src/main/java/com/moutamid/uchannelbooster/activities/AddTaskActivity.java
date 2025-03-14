package com.moutamid.uchannelbooster.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.uchannelbooster.R;
import com.moutamid.uchannelbooster.databinding.ActivityAddTaskBinding;
import com.moutamid.uchannelbooster.models.ArrayModel;
import com.moutamid.uchannelbooster.models.LikeTaskModel;
import com.moutamid.uchannelbooster.models.UserDetails;
import com.moutamid.uchannelbooster.models.ViewTaskModel;
import com.moutamid.uchannelbooster.ui.subscribe.utilis.Stash;
import com.moutamid.uchannelbooster.ui.subscribe.utilis.SubscribeTaskModel;
import com.moutamid.uchannelbooster.utils.Constants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddTaskActivity extends AppCompatActivity {

    private static final String TAG = "AddTaskActivity";

    int totalCostInt = 600;
    int currentCoinsValue = 0;

    int viewTimeInteger = 60;
    int taskCutOfAmount = 60;
    //IF ACTIVITY IS LIKE SUBSCRIBE THEN RETRIEVE ABOVE VALUE FROM DATABASE
    private Button viewQuantityButton, viewTimeButton, vipDiscountButton,
            totalCostButton, doneButton;

    private TextView coinsTextView;

    private boolean isVipDiscount = false;

    private YouTubePlayerView youTubePlayerView;
    private String videoUrl;
    int viewQuantityInteger = 10;
    //    private Utils utils = new Utils();
    String videoType = Constants.TYPE_VIEW;

    Integer[] viewQuantityArrayInt;
    String[] viewQuantityArray;
    Integer[] viewTimeArrayInt;
    String[] viewTimeArray;

    List<Integer> viewQuantityListInt = new ArrayList<>();
    List<Integer> viewTimeListInt = new ArrayList<>();
    List<String> viewQuantityListString = new ArrayList<>();
    List<String> viewTimeListString = new ArrayList<>();
    List<ArrayModel> viewQuantityListModel = new ArrayList<>();
    List<ArrayModel> viewTimeListModel = new ArrayList<>();
     ActivityAddTaskBinding binding;
    int CAMPAIGN_SELECTION = Stash.getInt(Constants.CAMPAIGN_SELECTION);
    int CurrentCoins = 0;
    boolean VIP_STATUS = false;
    String url;
    double totalCost = 0;
    double subDefault = 2100;
    double likeDefault = 1700;
    int pickedSub = 0;
    int pickedView = 1;
    int pickedLike = 0;
    int pickedSubTime = 1;
    double holderSub = 0;
    int pickedViewTime = 1;
    int pickedLikeTime = 1;
    String thumbnailUrl;
    double discount = 0.10; // 10% discount
    GetVideoTitle getVideoTitle = new GetVideoTitle();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        url = Stash.getString(Constants.RECENT_LINK);

        showHideLayoutsBasedOnTaskType();
        binding.back.setOnClickListener(v -> onBackPressed());

        Constants.databaseReference().child("userinfo").child(Constants.auth().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            UserDetails userDetails = snapshot.getValue(UserDetails.class);
                            CurrentCoins = userDetails.getCoins();
                            VIP_STATUS = userDetails.isVipStatus();
//                            if (!userDetails.isVipStatus()){
//                                Constants.loadIntersAD(AddTaskActivity.this, AddTaskActivity.this);
//                                Constants.showBannerAd(binding.adView);
//                            } else {
//                                binding.adView.setVisibility(View.GONE);
//                            }
                            binding.coin.setText(userDetails.getCoins()+"");
                            Stash.put(Constants.CURRENT_COINS, userDetails.getCoins());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AddTaskActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        Constants.databaseReference().child(Constants.ADD_TASK_VARIABLES).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    viewQuantityListInt.clear();
                    viewTimeListInt.clear();
                    viewQuantityListString.clear();
                    viewTimeListString.clear();
                    viewQuantityListModel.clear();
                    viewTimeListModel.clear();

                    if (snapshot.child(Constants.TIME_ARRAY).exists()) {

                        for (DataSnapshot dataSnapshot : snapshot.child(Constants.TIME_ARRAY).getChildren()) {
                            ArrayModel model = new ArrayModel();

                            // Check if the snapshot contains a HashMap
                            Object value = dataSnapshot.getValue();
                            if (value instanceof Map) {
                                Map<String, Object> map = (Map<String, Object>) value;
                                if (map.containsKey("value")) {
                                    model.setValue(((Long) map.get("value")).intValue()); // Convert Long to int
                                }
                            } else if (value instanceof Long) {
                                model.setValue(((Long) value).intValue()); // Direct Long to int conversion
                            }
                            model.setKey(dataSnapshot.getKey());
                            viewTimeListModel.add(model);
                            viewTimeListInt.add(model.getValue());
                            viewTimeListString.add(String.valueOf(model.getValue()));
                        }


                        viewTimeArrayInt = viewTimeListInt.toArray(new Integer[viewTimeListInt.size()]);
                        viewTimeArray = viewTimeListString.toArray(new String[viewTimeListString.size()]);

                    } else {
                        viewTimeArrayInt = new Integer[]{60, 90, 120, 150, 180, 210, 240,
                                270, 300, 330, 360, 390, 420, 450, 480, 510, 540,
                                570, 600, 660, 720, 780, 840, 900
                        };
                        viewTimeArray = new String[]{"60", "90", "120", "150", "180", "210", "240",
                                "270", "300", "330", "360", "390", "420", "450", "480", "510", "540",
                                "570", "600", "660", "720", "780", "840", "900"
                        };
                    }

                    if (snapshot.child(Constants.QUANTITY_ARRAY).exists()) {
                        for (DataSnapshot dataSnapshot : snapshot.child(Constants.QUANTITY_ARRAY).getChildren()) {
                            ArrayModel model = new ArrayModel();

                            // Check the type of the value before converting
                            Object value = dataSnapshot.getValue();

                            if (value instanceof Map) {
                                Map<String, Object> map = (Map<String, Object>) value;
                                if (map.containsKey("value")) {
                                    model.setValue(((Long) map.get("value")).intValue()); // Convert Long to int
                                }
                            } else if (value instanceof Long) {
                                model.setValue(((Long) value).intValue()); // Direct Long to int conversion
                            }

                            model.setKey(dataSnapshot.getKey());
                            viewQuantityListModel.add(model);

                            viewQuantityListInt.add(model.getValue());

                            viewQuantityListString.add(String.valueOf(model.getValue()));
                        }


                        viewQuantityArrayInt = viewQuantityListInt.toArray(new Integer[viewQuantityListInt.size()]);
                        viewQuantityArray = viewQuantityListString.toArray(new String[viewQuantityListString.size()]);

                    } else {
                        viewQuantityArrayInt = new Integer[]{10, 20, 30, 40, 50,
                                100, 150, 200, 250, 300, 350, 400, 450, 500, 550,
                                600, 700, 800, 900, 1000, 1500, 2000, 2500,
                                3000, 3500, 4000, 4500, 5000
                        };
                        viewQuantityArray = new String[]{"10", "20", "30", "40", "50",
                                "100", "150", "200", "250", "300", "350", "400", "450", "500", "550",
                                "600", "700", "800", "900", "1000", "1500", "2000", "2500",
                                "3000", "3500", "4000", "4500", "5000"
                        };

                    }

                } else {
                    viewQuantityArrayInt = new Integer[]{10, 20, 30, 40, 50,
                            100, 150, 200, 250, 300, 350, 400, 450, 500, 550,
                            600, 700, 800, 900, 1000, 1500, 2000, 2500,
                            3000, 3500, 4000, 4500, 5000
                    };
                    viewQuantityArray = new String[]{"10", "20", "30", "40", "50",
                            "100", "150", "200", "250", "300", "350", "400", "450", "500", "550",
                            "600", "700", "800", "900", "1000", "1500", "2000", "2500",
                            "3000", "3500", "4000", "4500", "5000"
                    };
                    viewTimeArrayInt = new Integer[]{60, 90, 120, 150, 180, 210, 240,
                            270, 300, 330, 360, 390, 420, 450, 480, 510, 540,
                            570, 600, 660, 720, 780, 840, 900
                    };
                    viewTimeArray = new String[]{"60", "90", "120", "150", "180", "210", "240",
                            "270", "300", "330", "360", "390", "420", "450", "480", "510", "540",
                            "570", "600", "660", "720", "780", "840", "900"
                    };
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.coin.setText(CurrentCoins+"");

        binding.coin.setOnClickListener(v -> {
            startActivity(new Intent(this, BuyPointsActivity.class));
        });

        initYoutubePlayer();

        if (CAMPAIGN_SELECTION == 0){
//            binding.allLayout.setVisibility(View.VISIBLE);
//            binding.likesLayout.setVisibility(View.GONE);
//            binding.viewsLayout.setVisibility(View.GONE);
//
//            binding.pickerSubAll.setText(viewQuantityArray[pickedSub]);
//            binding.likeAll.setText(viewQuantityArray[pickedSub]);
//            binding.viewsAll.setText(viewQuantityArray[pickedSub]);

            totalCost = ((300 * pickedSubTime) + subDefault );
            holderSub = totalCost;

            if (VIP_STATUS) {
                totalCost = totalCost - (totalCost * discount);
            }

            binding.totalCoins.setText(""+totalCost);

        } else if (CAMPAIGN_SELECTION == 1){
//            binding.allLayout.setVisibility(View.GONE);
//            binding.likesLayout.setVisibility(View.GONE);
//            binding.viewsLayout.setVisibility(View.VISIBLE);

            if (viewQuantityArray != null && viewTimeArray != null
                    && pickedView >= 0 && pickedView < viewQuantityArray.length
                    && pickedViewTime >= 0 && pickedViewTime < viewTimeArray.length) {

                totalCost = Integer.parseInt(viewQuantityArray[pickedView])
                        * Integer.parseInt(viewTimeArray[pickedViewTime]);
            } else {
                Log.e("Error", "viewQuantityArray or viewTimeArray is null, or index out of bounds!");
                totalCost = 0;
            }

            if (VIP_STATUS){
                totalCost = totalCost - (totalCost * discount);
            }

            binding.totalCoins.setText(""+totalCost);

            binding.pickerViews.setText("0");
        } else if (CAMPAIGN_SELECTION == 2){
//            binding.allLayout.setVisibility(View.GONE);
//            binding.likesLayout.setVisibility(View.VISIBLE);
//            binding.viewsLayout.setVisibility(View.GONE);

            totalCost = ((300 * pickedLikeTime) + likeDefault );
            holderSub = totalCost;

            if (VIP_STATUS){
                totalCost = totalCost - (totalCost * discount);
            }

            binding.totalCoins.setText(""+totalCost);
        }

        binding.pickerSubAll.setOnClickListener(v -> {
            pickerAllDialog();
        });

        binding.pickerLikes.setOnClickListener(v -> {
            pickerLikesDialog();
        });

        binding.pickerTime.setOnClickListener(v -> {
            pickerTimeDialog();
        });

        binding.pickerViews.setOnClickListener(v -> {
            pickerViewDialog();
        });

        binding.done.setOnClickListener(v -> {
            if (totalCost <= CurrentCoins){
                if (CAMPAIGN_SELECTION == 0){
                    uploadSubscribeCampaign();
                }
                if (CAMPAIGN_SELECTION == 1){
                    uploadViewTask();
                }
                if (CAMPAIGN_SELECTION == 2){
                    uploadLikeTask();
                }
            } else {
                Toast.makeText(this, "Not Enough Coin", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, BuyPointsActivity.class));
                finish();
            }
        });

    }
    private void showHideLayoutsBasedOnTaskType() {
        if (CAMPAIGN_SELECTION==1) {
            findViewById(R.id.viewLayoutAddTask).setVisibility(View.VISIBLE);
        }
        if (CAMPAIGN_SELECTION==2) {
            findViewById(R.id.likesLayoutAddTask).setVisibility(View.VISIBLE);
        }
        if (CAMPAIGN_SELECTION==0) {
            findViewById(R.id.subscribersLayoutAddTask).setVisibility(View.VISIBLE);
        }
    }
    private void uploadViewTask() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Creating Your Campaign..");
        progressDialog.show();

        Constants.databaseReference().child("userinfo").child(Constants.auth().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            UserDetails userDetails = snapshot.getValue(UserDetails.class);
                            Constants.databaseReference().child("userinfo").child(Constants.auth().getCurrentUser().getUid())
                                    .child("coins")
                                    .setValue(userDetails.getCoins() - totalCost)
                                    .addOnCompleteListener(task -> {

                                        if (task.isSuccessful()){
                                            String key = Constants.databaseReference().child(Constants.VIEW_TASKS).push().getKey();

                                            ViewTaskModel task1 = new ViewTaskModel();

                                            task1.setVideoUrl(url);
                                            task1.setThumbnailUrl(thumbnailUrl);
                                            task1.setTotalViewsQuantity(viewQuantityArray[pickedView]);
                                            task1.setTotalViewTimeQuantity(viewTimeArray[pickedViewTime]);
                                            task1.setCompletedDate("error");
                                            task1.setCurrentViewsQuantity(0);
                                            task1.setPosterUid(Constants.auth().getCurrentUser().getUid());
                                            task1.setTaskKey(key);
                                            task1.setCreatedTime(Constants.getDate());

                                            Constants.databaseReference().child(Constants.VIEW_TASKS).child(key).setValue(task1)
                                                    .addOnCompleteListener(task2 -> {

                                                        if (task2.isSuccessful()) {
                                                            progressDialog.dismiss();
                                                            Toast.makeText(AddTaskActivity.this, "Campaign Started Succesfully", Toast.LENGTH_SHORT).show();
                                                            finish();

                                                        } else {
                                                            progressDialog.dismiss();
                                                            Toast.makeText(AddTaskActivity.this, task2.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                        }

                                                    });

                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(AddTaskActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(AddTaskActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.dismiss();
                        Toast.makeText(AddTaskActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }
    private void uploadLikeTask() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Creating Your Campaign..");
        progressDialog.show();

        Constants.databaseReference().child("userinfo").child(Constants.auth().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            UserDetails userDetails = snapshot.getValue(UserDetails.class);
                            Constants.databaseReference().child("userinfo").child(Constants.auth().getCurrentUser().getUid())
                                    .child("coins")
                                    .setValue(userDetails.getCoins() - totalCost)
                                    .addOnCompleteListener(task -> {

                                        if (task.isSuccessful()){
                                            String key = Constants.databaseReference().child(Constants.LIKE_TASKS).push().getKey();

                                            LikeTaskModel task1 = new LikeTaskModel();
                                            task1.setVideoUrl(url);
                                            task1.setThumbnailUrl(thumbnailUrl);
                                            task1.setTotalLikesQuantity(viewQuantityArray[pickedLike]);
                                            task1.setCompletedDate("error");
                                            task1.setTotalViewTimeQuantity(viewTimeArray[pickedLikeTime]);
                                            task1.setCurrentLikesQuantity(0);
                                            task1.setPosterUid(Constants.auth().getCurrentUser().getUid());
                                            task1.setTaskKey(key);
                                            task1.setCreatedTime(Constants.getDate());

                                            Constants.databaseReference().child(Constants.LIKE_TASKS).child(key).setValue(task1)
                                                    .addOnCompleteListener(task2 -> {

                                                        if (task2.isSuccessful()) {
                                                            progressDialog.dismiss();
                                                            Toast.makeText(AddTaskActivity.this, "Campaign Started Succesfully", Toast.LENGTH_SHORT).show();
                                                            finish();

                                                        } else {
                                                            progressDialog.dismiss();
                                                            Toast.makeText(AddTaskActivity.this, task2.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                        }

                                                    });

                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(AddTaskActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(AddTaskActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.dismiss();
                        Toast.makeText(AddTaskActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }
    private void uploadSubscribeCampaign() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Creating Your Campaign..");
        progressDialog.show();

        Constants.databaseReference().child("userinfo").child(Constants.auth().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            UserDetails userDetails = snapshot.getValue(UserDetails.class);
                            Constants.databaseReference().child("userinfo").child(Constants.auth().getCurrentUser().getUid())
                                    .child("coins")
                                    .setValue(userDetails.getCoins() - totalCost)
                                    .addOnCompleteListener(task -> {

                                        if (task.isSuccessful()){
                                            String key = Constants.databaseReference().child(Constants.SUBSCRIBE_TASKS).push().getKey();

                                            SubscribeTaskModel task1 = new SubscribeTaskModel();
                                            task1.setVideoUrl(url);
                                            task1.setThumbnailUrl(thumbnailUrl);
                                            task1.setTotalSubscribesQuantity(viewQuantityArray[pickedSub]);
                                            task1.setCompletedDate("error");
                                            task1.setTotalViewTimeQuantity(viewTimeArray[pickedSubTime]);
                                            task1.setCurrentSubscribesQuantity(0);
                                            task1.setPosterUid(Constants.auth().getCurrentUser().getUid());
                                            task1.setTaskKey(key);
                                            task1.setCreatedTime(Constants.getDate());

                                            Constants.databaseReference().child(Constants.SUBSCRIBE_TASKS).child(key).setValue(task1)
                                                    .addOnCompleteListener(task2 -> {

                                                        if (task2.isSuccessful()) {
                                                            progressDialog.dismiss();
                                                            Toast.makeText(AddTaskActivity.this, "Campaign Started Succesfully", Toast.LENGTH_SHORT).show();
                                                            finish();

                                                        } else {
                                                            progressDialog.dismiss();
                                                            Toast.makeText(AddTaskActivity.this, task2.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                        }

                                                    });

                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(AddTaskActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(AddTaskActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.dismiss();
                        Toast.makeText(AddTaskActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void pickerViewDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.picker_dialog);

        TextView title = dialog.findViewById(R.id.title);
        TextView subTitle = dialog.findViewById(R.id.subTitle);
        Button cancel = dialog.findViewById(R.id.cancel);
        Button select = dialog.findViewById(R.id.select);
        NumberPicker picker = dialog.findViewById(R.id.picker);

        picker.setMinValue(0);
        picker.setMaxValue(viewQuantityArray.length-1);
        picker.setDisplayedValues(viewQuantityArray);
        picker.setValue(pickedView);

        title.setText("EXPECTED VIEWS");
        subTitle.setText("Select number of views that you want to have");
        cancel.setOnClickListener(v -> dialog.dismiss());

        select.setOnClickListener(v -> {
            pickedView = picker.getValue();
            String picked = picker.getDisplayedValues()[picker.getValue()];
            dialog.dismiss();
            binding.pickerViews.setText(picked);

            totalCost = Integer.parseInt(picked) * Integer.parseInt(viewTimeArray[pickedViewTime]);
            if (VIP_STATUS){
                totalCost = totalCost - (totalCost * discount);
            }
            binding.totalCoins.setText(""+totalCost);
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);
    }
    private void pickerTimeDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.picker_dialog);

        TextView title = dialog.findViewById(R.id.title);
        TextView subTitle = dialog.findViewById(R.id.subTitle);
        Button cancel = dialog.findViewById(R.id.cancel);
        Button select = dialog.findViewById(R.id.select);
        NumberPicker picker = dialog.findViewById(R.id.picker);

        if (CAMPAIGN_SELECTION == 0){
            picker.setMinValue(0);
            picker.setMaxValue(viewTimeArray.length-1);
            picker.setDisplayedValues(viewTimeArray);
            picker.setValue(pickedSubTime);
        } if (CAMPAIGN_SELECTION == 1) {
            picker.setMinValue(0);
            picker.setMaxValue(viewTimeArray.length-1);
            picker.setDisplayedValues(viewTimeArray);
            picker.setValue(pickedViewTime);
        } if (CAMPAIGN_SELECTION == 2) {
            picker.setMinValue(0);
            picker.setMaxValue(viewTimeArray.length-1);
            picker.setDisplayedValues(viewTimeArray);
            picker.setValue(pickedLikeTime);
        }



        title.setText("TIME REQUIRED (SECONDS):");
        subTitle.setText("");
        cancel.setOnClickListener(v -> dialog.dismiss());

        select.setOnClickListener(v -> {
            String picked = picker.getDisplayedValues()[picker.getValue()];

            if (CAMPAIGN_SELECTION == 0){
                pickedSubTime = picker.getValue();
                if (pickedSub == 0){
                    totalCost = ((300 * pickedSubTime) + subDefault);
                } else {
                    totalCost = ((300 * pickedSubTime) + subDefault) * (pickedSub+1);
                }
                holderSub = totalCost;
            } if (CAMPAIGN_SELECTION == 1) {
                pickedViewTime = picker.getValue();
                totalCost = Integer.valueOf(picked) * Integer.valueOf(viewQuantityArray[pickedView]);
            } if (CAMPAIGN_SELECTION == 2) {
                pickedLikeTime = picker.getValue();
                if (pickedLike == 0){
                    totalCost = ((300 * pickedLikeTime) + likeDefault);
                } else {
                    totalCost = ((300 * pickedLikeTime) + likeDefault) * (pickedLike+1);
                }
                holderSub = totalCost;
            }

            dialog.dismiss();
            binding.pickerTime.setText(picked+"");

            if (VIP_STATUS){
                totalCost = totalCost - (totalCost * discount);
            }

            binding.totalCoins.setText(""+totalCost);
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);
    }
    private void pickerLikesDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.picker_dialog);

        TextView title = dialog.findViewById(R.id.title);
        TextView subTitle = dialog.findViewById(R.id.subTitle);
        Button cancel = dialog.findViewById(R.id.cancel);
        Button select = dialog.findViewById(R.id.select);
        NumberPicker picker = dialog.findViewById(R.id.picker);

        picker.setMinValue(0);
        picker.setMaxValue(viewQuantityArray.length-1);
        picker.setDisplayedValues(viewQuantityArray);
        picker.setValue(pickedLike);

        title.setText("EXPECTED LIKES");
        subTitle.setText("Select number of likes that you want to have");
        cancel.setOnClickListener(v -> dialog.dismiss());

        select.setOnClickListener(v -> {
            pickedLike = picker.getValue();
            String picked = picker.getDisplayedValues()[picker.getValue()];
            dialog.dismiss();
            binding.pickerViews.setText(picked+"");
            binding.pickerLikes.setText(picked+"");
            totalCost = holderSub * (pickedLike+1);
            if (VIP_STATUS){
                totalCost = totalCost - (totalCost * discount);
            }
            binding.totalCoins.setText(""+totalCost);
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);
    }
    private void pickerAllDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.picker_dialog);

        TextView title = dialog.findViewById(R.id.title);
        TextView subTitle = dialog.findViewById(R.id.subTitle);
        Button cancel = dialog.findViewById(R.id.cancel);
        Button select = dialog.findViewById(R.id.select);
        NumberPicker picker = dialog.findViewById(R.id.picker);

        picker.setMinValue(0);
        picker.setMaxValue(viewQuantityArray.length-1);
        picker.setDisplayedValues(viewQuantityArray);
        picker.setValue(pickedSub);

        title.setText("EXPECTED SUBSCRIBER");
        subTitle.setText("Select number of subscriber that you want to have");
        cancel.setOnClickListener(v -> dialog.dismiss());

        select.setOnClickListener(v -> {
            pickedSub = picker.getValue();
            String picked = picker.getDisplayedValues()[picker.getValue()];
            dialog.dismiss();
//            binding.likeAll.setText(picked+"");
//            binding.viewsAll.setText(picked+"");
//            binding.pickerSubAll.setText(picked+"");

            totalCost = holderSub * (pickedSub+1);

            if (VIP_STATUS){
                totalCost = totalCost - (totalCost * discount);
            }
            binding.totalCoins.setText(""+totalCost);
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);
    }
    private void initYoutubePlayer() {

        YouTubePlayerView youTubePlayerView = binding.youtubePlayerViewFragmentView;
        getLifecycle().addObserver(youTubePlayerView);
        String videoId = Constants.getVideoId(url);
        Log.e("ResponseURL", "ID " + videoId);
        Log.e("ResponseURL", "url " + url);
        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                youTubePlayer.cueVideo(videoId, 0);
            }

            @Override
            public void onError(@NonNull YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlayerError error) {
                super.onError(youTubePlayer, error);
                Log.e("ResponseURL", "Error " + error.toString());
            }
        });

        // INIT PLAYER VIEW
        getVideoTitle.setId(url);
        Log.e("ResponseURL", getVideoTitle.id);
        getVideoTitle.execute();
        Log.e("ResponseURL", "execute");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
//        binding.youtubePlayerViewFragmentView.release();
//        getVideoTitle.cancel(true);
    }
    @Override
    protected void onStop() {
        super.onStop();
//        getVideoTitle.cancel(true);
    }
    private class GetVideoTitle extends AsyncTask<String, Void, String> {

        private String id;

        public void setId(String id) {
            this.id = id;
        }

        @Override
        protected String doInBackground(String... strings) {
            Constants.HttpHandler sh = new Constants.HttpHandler();

            String url = "https://www.youtube.com/oembed?format=json&url=" + id;//https://www.youtube.com/watch?v=" + id;

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            String videoTitle;

            Log.e("ResponseURL", "Response from url: " + jsonStr);

            //https://youtu.be/026vZJ4Gnjc

            if (jsonStr != null) {
                try {
                    JSONObject o = new JSONObject(jsonStr);

                    videoTitle = o.getString("title");

                    thumbnailUrl = o.getString("thumbnail_url");
                    Stash.put(Constants.RECENT_IMAGE, thumbnailUrl);
                    Log.e("ResponseURL", "url: " + thumbnailUrl);
                    return videoTitle;

                } catch (final JSONException e) {
                    Log.e("ResponseURL", "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                            "Json parsing error: " + e.getMessage(),
                                            Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                    e.printStackTrace();
                }
            } else {
                Log.e("ResponseURL", "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                        "Couldn't get json from server. Check LogCat for possible errors!",
                                        Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (!isCancelled()) {
                //binding.youtubePlayerViewFragmentView.getPlayerUiController().setVideoTitle(s);
            }

        }
    }

}