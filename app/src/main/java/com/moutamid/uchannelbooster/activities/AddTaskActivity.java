package com.moutamid.uchannelbooster.activities;

import static com.moutamid.uchannelbooster.utils.Constants.databaseReference;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.moutamid.uchannelbooster.ContextWrapper;
import com.moutamid.uchannelbooster.R;
import com.moutamid.uchannelbooster.models.ArrayModel;
import com.moutamid.uchannelbooster.models.LikeTaskModel;
import com.moutamid.uchannelbooster.models.SubscribeTaskModel;
import com.moutamid.uchannelbooster.utils.Constants;
import com.moutamid.uchannelbooster.utils.Utils;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.shawnlin.numberpicker.NumberPicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private final GetVideoTitle getVideoTitle = new GetVideoTitle();
    private YouTubePlayerView youTubePlayerView;
    private String videoUrl;
    private String thumbnailUrl;
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
        setContentView(R.layout.activity_add_task);

        videoType = getIntent().getStringExtra(Constants.PARAMS);
        videoUrl = getIntent().getStringExtra("url");

        isVipDiscount = Utils.getBoolean(Constants.VIP_STATUS, false);


        databaseReference().child(Constants.ADD_TASK_VARIABLES).addValueEventListener(new ValueEventListener() {
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

                        for (DataSnapshot dataSnapshot : snapshot
                                .child(Constants.TIME_ARRAY).getChildren()) {
                            ArrayModel model = new ArrayModel();
                            model.setValue(dataSnapshot.getValue(Integer.class));
                            model.setKey(dataSnapshot.getKey());
                            viewTimeListModel.add(model);

                            viewTimeListInt.add(dataSnapshot.getValue(Integer.class));

                            viewTimeListString.add(String.valueOf(dataSnapshot.getValue(Integer.class)));

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
                        for (DataSnapshot dataSnapshot : snapshot.child(Constants.QUANTITY_ARRAY)
                                .getChildren()) {
                            ArrayModel model = new ArrayModel();
                            model.setValue(dataSnapshot.getValue(Integer.class));
                            model.setKey(dataSnapshot.getKey());
                            viewQuantityListModel.add(model);

                            viewQuantityListInt.add(dataSnapshot.getValue(Integer.class));

                            viewQuantityListString.add(String.valueOf(dataSnapshot.getValue(Integer.class)));

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

        initViews();

        showHideLayoutsBasedOnTaskType();

        getCoinsAmount();

        initYoutubePlayer();

        databaseReference().child(Constants.ADD_TASK_VARIABLES)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            taskCutOfAmount = snapshot.child(Constants.CUT_OFF_AMOUNT_OF_TASKS)
                                    .getValue(Integer.class);

                            if (videoType.equals(Constants.TYPE_VIEW) && snapshot.child(Constants.CUT_OFF_AMOUNT_OF_VIEWS).exists()) {
                                taskCutOfAmount = snapshot.child(Constants.CUT_OFF_AMOUNT_OF_VIEWS)
                                        .getValue(Integer.class);
                            }

                            if (videoType.equals(Constants.TYPE_LIKE) && snapshot.child(Constants.CUT_OFF_AMOUNT_OF_LIKE).exists()) {
                                taskCutOfAmount = snapshot.child(Constants.CUT_OFF_AMOUNT_OF_LIKE)
                                        .getValue(Integer.class);
                            }

                            if (videoType.equals(Constants.TYPE_SUBSCRIBE) && snapshot.child(Constants.CUT_OFF_AMOUNT_OF_SUBSCRIBE).exists()) {
                                taskCutOfAmount = snapshot.child(Constants.CUT_OFF_AMOUNT_OF_SUBSCRIBE)
                                        .getValue(Integer.class);
                            }

                            totalCostInt = viewQuantityInteger * taskCutOfAmount;

                            if (isVipDiscount) {
                                int percentage = (int) (totalCostInt * (20.0f / 100.0f));
                                vipDiscountButton.setText(String.valueOf(percentage));
                                totalCostInt = totalCostInt - percentage;
                            }

                            totalCostButton.setText(String.valueOf(totalCostInt));

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }

    private void showHideLayoutsBasedOnTaskType() {
        if (videoType.equals(Constants.TYPE_VIEW)) {
            findViewById(R.id.viewLayoutAddTask).setVisibility(View.VISIBLE);
        }
        if (videoType.equals(Constants.TYPE_LIKE)) {
            findViewById(R.id.likesLayoutAddTask).setVisibility(View.VISIBLE);
        }
        if (videoType.equals(Constants.TYPE_SUBSCRIBE)) {
            findViewById(R.id.subscribersLayoutAddTask).setVisibility(View.VISIBLE);
        }
    }

    private void initYoutubePlayer() {
        youTubePlayerView = findViewById(R.id.youtube_player_view_native);

        getLifecycle().addObserver(youTubePlayerView);

        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NotNull YouTubePlayer youTubePlayer) {
                Log.d(TAG, "onReady: public void onReady(YouTubePlayer youTubePlayer) {");

                youTubePlayer.cueVideo(getVideoId(videoUrl), 0);
                youTubePlayer.addListener(youTubePlayerListener());

//                ActivityVideoPlayer.this.addFullScreenListenerToPlayer(youTubePlayer);

//                YouTubePlayerTracker tracker = new YouTubePlayerTracker();
//                youTubePlayer.addListener(tracker);
//
//                Log.d(TAG, "onReady: tracker.getCurrentSecond();"+tracker.getCurrentSecond());
//                Log.d(TAG, "onReady: tracker.getState();"+tracker.getState());
//                Log.d(TAG, "onReady: tracker.getVideoDuration()"+tracker.getVideoDuration());
//                Log.d(TAG, "onReady: tracker.getVideoId();"+tracker.getVideoId());
            }

        });

        youTubePlayerView.enableBackgroundPlayback(false);

        // Showing Video Title
        youTubePlayerView.getPlayerUiController().showVideoTitle(true);
        youTubePlayerView.getPlayerUiController().setVideoTitle(getString(R.string.loading));

        // Showing Custom Forward and Backward Icons
        youTubePlayerView.getPlayerUiController().showCustomAction1(false);
        youTubePlayerView.getPlayerUiController().showCustomAction2(false);

        // Showing Menu Button
        youTubePlayerView.getPlayerUiController().showMenuButton(false);

        // Hiding Full Screen Button
        youTubePlayerView.getPlayerUiController().showFullscreenButton(false);

        // Hiding Seekbar
        youTubePlayerView.getPlayerUiController().showSeekBar(false);

        // INIT PLAYER VIEW
        getVideoTitle.setId(videoUrl);
        getVideoTitle.execute();

    }

    private static String getVideoId(@NonNull String videoUrl) {
        String videoId = "";
        String regex = "http(?:s)?:\\/\\/(?:m.)?(?:www\\.)?youtu(?:\\.be\\/|be\\.com\\/(?:watch\\?(?:feature=youtu.be\\&)?v=|v\\/|embed\\/|user\\/(?:[\\w#]+\\/)+))([^&#?\\n]+)";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(videoUrl);
        if (matcher.find()) {
            videoId = matcher.group(1);
        }
        return videoId;
    }

    private YouTubePlayerListener youTubePlayerListener() {
        return new YouTubePlayerListener() {
            @Override
            public void onReady(@NotNull YouTubePlayer youTubePlayer) {

            }

            @Override
            public void onStateChange(@NotNull YouTubePlayer youTubePlayer, @NotNull PlayerConstants.PlayerState playerState) {

            }

            @Override
            public void onPlaybackQualityChange(@NotNull YouTubePlayer youTubePlayer, @NotNull PlayerConstants.PlaybackQuality playbackQuality) {

            }

            @Override
            public void onPlaybackRateChange(@NotNull YouTubePlayer youTubePlayer, @NotNull PlayerConstants.PlaybackRate playbackRate) {

            }

            @Override
            public void onError(@NotNull YouTubePlayer youTubePlayer, @NotNull PlayerConstants.PlayerError playerError) {

            }

            @Override
            public void onCurrentSecond(@NotNull YouTubePlayer youTubePlayer, float v) {

            }

            @Override
            public void onVideoDuration(@NotNull YouTubePlayer youTubePlayer, float v) {

            }

            @Override
            public void onVideoLoadedFraction(@NotNull YouTubePlayer youTubePlayer, float v) {

            }

            @Override
            public void onVideoId(@NotNull YouTubePlayer youTubePlayer, @NotNull String s) {

            }

            @Override
            public void onApiChange(@NotNull YouTubePlayer youTubePlayer) {

            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        youTubePlayerView.release();
        getVideoTitle.cancel(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        getVideoTitle.cancel(true);
    }

    @Override
    public void onBackPressed() {
        if (youTubePlayerView.isFullScreen()) {

            youTubePlayerView.exitFullScreen();

        } else {

            super.onBackPressed();
        }
    }

    private class GetVideoTitle extends AsyncTask<String, Void, String> {

        private String id;

        public void setId(String id) {
            this.id = id;
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpHandler sh = new HttpHandler();

            String url = "https://www.youtube.com/oembed?format=json&url=" + id;//https://www.youtube.com/watch?v=" + id;

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            String videoTitle;

            Log.e("", "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject o = new JSONObject(jsonStr);

                    videoTitle = o.getString("title");

                    thumbnailUrl = o.getString("thumbnail_url");

                    return videoTitle;

                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
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
                Log.e(TAG, "Couldn't get json from server.");
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
                youTubePlayerView.getPlayerUiController().setVideoTitle(s);
            }

        }
    }

    private void initViews() {
        viewQuantityButton = findViewById(R.id.select_view_quantity_button);
        viewTimeButton = findViewById(R.id.select_view_time_button);
        vipDiscountButton = findViewById(R.id.vip_discount_button);
        totalCostButton = findViewById(R.id.total_cost_button);
        doneButton = findViewById(R.id.done_button_add_task);

        coinsTextView = findViewById(R.id.coins_text_view_add_task);

        viewQuantityButton.setOnClickListener(view -> {

            openPickerDialog(true);

        });

        findViewById(R.id.select_likes_quantity_button).setOnClickListener(view -> {

            openPickerDialog(true);

        });

        findViewById(R.id.select_subscribers_quantity_button).setOnClickListener(view -> {

            openPickerDialog(true);

        });

        viewTimeButton.setOnClickListener(view -> {
            openPickerDialog(false);
        });
        doneButton.setOnClickListener(view -> {

            if (totalCostInt <= currentCoinsValue) {

//                new Utils().storeBoolean(getActivity(), "canAddNewTask", false);

//TODO: COMMENTED                if (Utils.getBoolean("canAddNewTask"))

                if (videoType.equals(Constants.TYPE_VIEW))
                    uploadViewTask();

                if (videoType.equals(Constants.TYPE_LIKE))
                    uploadLikeTask();

                if (videoType.equals(Constants.TYPE_SUBSCRIBE))
                    uploadSubscribeTask();
//TODO: COMMENTED                else Toast.makeText(this, "Only supports upto 3 tasks at the same time. Please delete the tasks.", Toast.LENGTH_LONG).show();

//                Toast.makeText(this, viewQuantityInteger+""+viewTimeInteger+"", Toast.LENGTH_SHORT).show();

//                Toast.makeText(this, "You can buy views " + String.valueOf(totalCostInt), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.low_coins_mcg), Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.backBTnAddTask).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();

            }
        });
    }

    private void uploadSubscribeTask() {
        ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();

        FirebaseAuth auth = FirebaseAuth.getInstance();


        databaseReference().child("userinfo")
                .child(auth.getCurrentUser().getUid())
                .child("coins")
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if (!snapshot.exists()) {
                                    progressDialog.dismiss();
                                    Toast.makeText(AddTaskActivity.this, getString(R.string.no_coins_exist), Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                int coins = snapshot.getValue(Integer.class);

                                databaseReference().child("userinfo")
                                        .child(auth.getCurrentUser().getUid())
                                        .child("coins")
                                        .setValue(coins - totalCostInt)
                                        .addOnCompleteListener(
                                                new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {

                                                        if (task.isSuccessful()) {

                                                            String key = databaseReference().child(Constants.SUBSCRIBE_TASKS).push().getKey();

                                                            SubscribeTaskModel task1 = new SubscribeTaskModel();
                                                            task1.setVideoUrl(videoUrl);
                                                            task1.setThumbnailUrl(thumbnailUrl);
                                                            task1.setTotalSubscribesQuantity("" + viewQuantityInteger);
                                                            task1.setCompletedDate("error");
                                                            task1.setCurrentSubscribesQuantity(0);
                                                            task1.setPosterUid(auth.getCurrentUser().getUid());
                                                            task1.setTaskKey(key);

                                                            databaseReference().child(Constants.SUBSCRIBE_TASKS).child(key).setValue(task1)
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {

                                                                            if (task.isSuccessful()) {

                                                                                progressDialog.dismiss();
                                                                                Toast.makeText(AddTaskActivity.this, getString(R.string.done), Toast.LENGTH_SHORT).show();
                                                                                finish();

                                                                            } else {
                                                                                progressDialog.dismiss();
                                                                                Toast.makeText(AddTaskActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                                            }

                                                                        }
                                                                    });


                                                        } else {
                                                            progressDialog.dismiss();
                                                            Toast.makeText(AddTaskActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                        }

                                                    }
                                                }
                                        );

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                progressDialog.dismiss();
                                Toast.makeText(AddTaskActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
    }

    private void uploadLikeTask() {
        ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();

        FirebaseAuth auth = FirebaseAuth.getInstance();


        databaseReference().child("userinfo")
                .child(auth.getCurrentUser().getUid())
                .child("coins")
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if (!snapshot.exists()) {
                                    progressDialog.dismiss();
                                    Toast.makeText(AddTaskActivity.this, getString(R.string.no_coins_exist), Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                int coins = snapshot.getValue(Integer.class);

                                databaseReference().child("userinfo")
                                        .child(auth.getCurrentUser().getUid())
                                        .child("coins")
                                        .setValue(coins - totalCostInt)
                                        .addOnCompleteListener(
                                                new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {

                                                        if (task.isSuccessful()) {

                                                            String key = databaseReference().child(Constants.LIKE_TASKS).push().getKey();

                                                            LikeTaskModel task1 = new LikeTaskModel();
                                                            task1.setVideoUrl(videoUrl);
                                                            task1.setThumbnailUrl(thumbnailUrl);
                                                            task1.setTotalLikesQuantity("" + viewQuantityInteger);
                                                            task1.setCompletedDate("error");
                                                            task1.setCurrentLikesQuantity(0);
                                                            task1.setPosterUid(auth.getCurrentUser().getUid());
                                                            task1.setTaskKey(key);

                                                            databaseReference().child(Constants.LIKE_TASKS).child(key).setValue(task1)
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {

                                                                            if (task.isSuccessful()) {

                                                                                progressDialog.dismiss();
                                                                                Toast.makeText(AddTaskActivity.this, getString(R.string.done), Toast.LENGTH_SHORT).show();
                                                                                finish();

                                                                            } else {
                                                                                progressDialog.dismiss();
                                                                                Toast.makeText(AddTaskActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                                            }

                                                                        }
                                                                    });


                                                        } else {
                                                            progressDialog.dismiss();
                                                            Toast.makeText(AddTaskActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                        }

                                                    }
                                                }
                                        );

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                progressDialog.dismiss();
                                Toast.makeText(AddTaskActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
    }

    private static class Task {

        private String videoUrl, thumbnailUrl, posterUid, taskKey,
                totalViewsQuantity, totalViewTimeQuantity, completedDate;
        private int currentViewsQuantity;

        public Task(String videoUrl, String thumbnailUrl, String posterUid, String taskKey, String totalViewsQuantity, String totalViewTimeQuantity, String completedDate, int currentViewsQuantity) {
            this.videoUrl = videoUrl;
            this.thumbnailUrl = thumbnailUrl;
            this.posterUid = posterUid;
            this.taskKey = taskKey;
            this.totalViewsQuantity = totalViewsQuantity;
            this.totalViewTimeQuantity = totalViewTimeQuantity;
            this.completedDate = completedDate;
            this.currentViewsQuantity = currentViewsQuantity;
        }

        public String getPosterUid() {
            return posterUid;
        }

        public void setPosterUid(String posterUid) {
            this.posterUid = posterUid;
        }

        public String getTaskKey() {
            return taskKey;
        }

        public void setTaskKey(String taskKey) {
            this.taskKey = taskKey;
        }

        public String getVideoUrl() {
            return videoUrl;
        }

        public void setVideoUrl(String videoUrl) {
            this.videoUrl = videoUrl;
        }

        public String getThumbnailUrl() {
            return thumbnailUrl;
        }

        public void setThumbnailUrl(String thumbnailUrl) {
            this.thumbnailUrl = thumbnailUrl;
        }

        public String getTotalViewsQuantity() {
            return totalViewsQuantity;
        }

        public void setTotalViewsQuantity(String totalViewsQuantity) {
            this.totalViewsQuantity = totalViewsQuantity;
        }

        public String getTotalViewTimeQuantity() {
            return totalViewTimeQuantity;
        }

        public void setTotalViewTimeQuantity(String totalViewTimeQuantity) {
            this.totalViewTimeQuantity = totalViewTimeQuantity;
        }

        public String getCompletedDate() {
            return completedDate;
        }

        public void setCompletedDate(String completedDate) {
            this.completedDate = completedDate;
        }

        public int getCurrentViewsQuantity() {
            return currentViewsQuantity;
        }

        public void setCurrentViewsQuantity(int currentViewsQuantity) {
            this.currentViewsQuantity = currentViewsQuantity;
        }

        Task() {
        }
    }

    private void uploadViewTask() {

        ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();

        FirebaseAuth auth = FirebaseAuth.getInstance();


        databaseReference().child("userinfo")
                .child(auth.getCurrentUser().getUid())
                .child("coins")
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if (!snapshot.exists()) {
                                    progressDialog.dismiss();
                                    Toast.makeText(AddTaskActivity.this, getString(R.string.no_coins_exist), Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                int coins = snapshot.getValue(Integer.class);

                                databaseReference().child("userinfo")
                                        .child(auth.getCurrentUser().getUid())
                                        .child("coins")
                                        .setValue(coins - totalCostInt)
                                        .addOnCompleteListener(
                                                new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {

                                                        if (task.isSuccessful()) {

                                                            String key = databaseReference().child("tasks").push().getKey();

                                                            Task task1 = new Task();
                                                            task1.setVideoUrl(videoUrl);
                                                            task1.setThumbnailUrl(thumbnailUrl);
                                                            task1.setTotalViewsQuantity("" + viewQuantityInteger);
                                                            task1.setTotalViewTimeQuantity("" + viewTimeInteger);
                                                            task1.setCompletedDate("error");
                                                            task1.setCurrentViewsQuantity(0);
                                                            task1.setPosterUid(auth.getCurrentUser().getUid());
                                                            task1.setTaskKey(key);

                                                            databaseReference().child("tasks").child(key).setValue(task1)
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {

                                                                            if (task.isSuccessful()) {

                                                                                progressDialog.dismiss();
                                                                                Toast.makeText(AddTaskActivity.this, getString(R.string.done), Toast.LENGTH_SHORT).show();
                                                                                finish();

                                                                            } else {
                                                                                progressDialog.dismiss();
                                                                                Toast.makeText(AddTaskActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                                            }

                                                                        }
                                                                    });


                                                        } else {
                                                            progressDialog.dismiss();
                                                            Toast.makeText(AddTaskActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                        }

                                                    }
                                                }
                                        );

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                progressDialog.dismiss();
                                Toast.makeText(AddTaskActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
    }

    private void getCoinsAmount() {

        ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(AddTaskActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();

        FirebaseAuth mAuth;
        String USER_INFO = "userinfo";

        mAuth = FirebaseAuth.getInstance();


        databaseReference().child(USER_INFO).child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    UserDetails details = snapshot.getValue(UserDetails.class);

//                    Toast.makeText(AddTaskActivity.this, details.getCoins()+"", Toast.LENGTH_SHORT).show();
//                    Toast.makeText(AddTaskActivity.this, details.isVipStatus()+"", Toast.LENGTH_SHORT).show();

//                    String value = String.valueOf(snapshot.child("coins").getValue(Integer.class));
//                    coinsTextView.setText(value);

                    coinsTextView.setText(details.getCoins() + "");
//TODO: COMMENTED isVipDiscount = details.isVipStatus();

                    currentCoinsValue = details.getCoins();

                    if (details.isVipStatus()) {
                        vipDiscountButton.setText("60");
                        totalCostButton.setText("540");
                        totalCostInt = 540;
                    } else vipDiscountButton.setText("0");


                } else {
                    coinsTextView.setText("0");
                }

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                String TAG = "oncancelled";
                Log.d(TAG, "onCancelled: " + error.getMessage());
                progressDialog.dismiss();
            }
        });
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

    private void openPickerDialog(boolean quantity) {
        Dialog dialog = new Dialog(AddTaskActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_edit_details);
        dialog.setCancelable(true);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        NumberPicker hoursPicker = dialog.findViewById(R.id.hourspicker);

        if (quantity)
            setValuesToNumberPickerForQuantity(hoursPicker);
        else
            setValuesToNumberPickerForTime(hoursPicker);

        hoursPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
//                hoursInt = newVal;
                if (quantity)
//                    Toast.makeText(AddTaskActivity.this,
//                            String.valueOf(
                    viewQuantityInteger = viewQuantityArrayInt[newVal - 1];
//                            ),
//                            Toast.LENGTH_SHORT).show();
                else
//                    Toast.makeText(AddTaskActivity.this,
//                            String.valueOf(
                    viewTimeInteger = viewTimeArrayInt[newVal - 1];
//                            ),
//                            Toast.LENGTH_SHORT).show();
//                Toast.makeText(AddTaskActivity.this, String.valueOf(newVal), Toast.LENGTH_SHORT).show();
            }
        });

        dialog.findViewById(R.id.saveBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // CODE HERE

                if (videoType.equals(Constants.TYPE_VIEW)) {
                    viewQuantityButton.setText(String.valueOf(viewQuantityInteger));
                    viewTimeButton.setText(String.valueOf(viewTimeInteger));
                }

                if (videoType.equals(Constants.TYPE_LIKE)) {
                    ((AppCompatButton) findViewById(R.id.select_likes_quantity_button))
                            .setText(String.valueOf(viewQuantityInteger));
                }

                if (videoType.equals(Constants.TYPE_SUBSCRIBE)) {
                    ((AppCompatButton) findViewById(R.id.select_subscribers_quantity_button))
                            .setText(String.valueOf(viewQuantityInteger));
                }

                totalCostInt = viewQuantityInteger * taskCutOfAmount;

                if (isVipDiscount) {
                    int percentage = (int) (totalCostInt * (20.0f / 100.0f));
                    vipDiscountButton.setText(String.valueOf(percentage));
                    totalCostInt = totalCostInt - percentage;
                }

                totalCostButton.setText(String.valueOf(totalCostInt));

                dialog.dismiss();
            }
        });
        dialog.show();
        dialog.getWindow().setAttributes(layoutParams);
    }

    private void setValuesToNumberPickerForQuantity(NumberPicker picker) {
        picker.setMinValue(1);
        picker.setMaxValue(viewQuantityArray.length);
        picker.setDisplayedValues(viewQuantityArray);
        picker.setValue(1);
    }

    private void setValuesToNumberPickerForTime(NumberPicker picker) {
        picker.setMinValue(1);
        picker.setMaxValue(viewTimeArray.length);
        picker.setDisplayedValues(viewTimeArray);
        picker.setValue(1);
    }

    private static class HttpHandler {

        private final String TAG = "HttpHandler";

        public HttpHandler() {
        }

        public String makeServiceCall(String reqUrl) {
            String response = null;
            try {
                URL url = new URL(reqUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                // read the response
                InputStream in = new BufferedInputStream(conn.getInputStream());

                response = convertStreamToString(in);
            } catch (MalformedURLException e) {
                Log.e(TAG, "MalformedURLException: " + e.getMessage());
            } catch (ProtocolException e) {
                Log.e(TAG, "ProtocolException: " + e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, "IOException: " + e.getMessage());
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
            return response;
        }

        private String convertStreamToString(InputStream is) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();

            String line;
            try {

                while ((line = reader.readLine()) != null) {

                    sb.append(line).append('\n');

                }

            } catch (IOException e) {

                e.printStackTrace();

            } finally {

                try {

                    is.close();

                } catch (IOException e) {

                    e.printStackTrace();
                }
            }

            return sb.toString();
        }
    }
}