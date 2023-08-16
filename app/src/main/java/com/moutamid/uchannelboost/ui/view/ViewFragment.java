package com.moutamid.uchannelboost.ui.view;

import static com.moutamid.uchannelboost.utils.Constants.databaseReference;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemChangeListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.moutamid.uchannelboost.R;
import com.moutamid.uchannelboost.activities.BottomNavigationActivity;
import com.moutamid.uchannelboost.utils.Constants;
import com.moutamid.uchannelboost.utils.Helper;
import com.moutamid.uchannelboost.utils.Utils;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

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

public class ViewFragment extends Fragment {
    private static final String TAG = "ViewFragment";

    private final FullScreenHelper fullScreenHelper = new FullScreenHelper(getActivity());

    private static class HttpHandler {

//        private String TAG = "HttpHandler";

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

    private final ArrayList<Taskk> taskArrayList = new ArrayList<>();

    //    private GetVideoTitle getVideoTitle = new GetVideoTitle();
    private YouTubePlayerView youTubePlayerView;
    private String videoUrl;

    
    private FirebaseAuth mAuth;

//    private int array = 0;

    //    private String url1 = "https://youtu.be/EWHiqEj52Xs";
//    private String url1 = "https://youtu.be/EWHiqEj52Xs";
//    private String url2 = "https://youtu.be/G393z8s8nFY";
    private final String url3 = "https://youtu.be/27m980F_obg";

    private YouTubePlayer youTubePlayer1;
    private View root;

    private SwitchCompat switchCompat;
    private boolean isAutoPlayEnabled = false;
    private TextView currentPointTextview;

    private int currentPoints = 120;

    private int currentPosition = 0;
    private int currentVideoLength = 0;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        root = inflater.inflate(R.layout.fragment_view, container, false);

        videoUrl = "https://youtu.be/G393z8s8nFY";

        mAuth = FirebaseAuth.getInstance();

        databaseReference().child(Constants.COINS_PATH).child(Constants.VIEW_COINS)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            currentPoints = snapshot.getValue(Integer.class);
                            currentPointTextview.setText(currentPoints + "");

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        progressDialog = new ProgressDialog(getActivity());
//        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading));

        databaseReference().child("tasks").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Log.d(TAG, "onDataChange: snapshot not exist");
                    return;
                }
                /*databaseReference
                        .child("tasks")
                        .child(taskArrayList.get(currentPosition).getTaskKey())
                        .child(Constants.VIEWER_PATH)
                        .child(mAuth.getUid())
                        .setValue(true)*/
                taskArrayList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Taskk tasks = dataSnapshot.getValue(Taskk.class);
                    if (snapshot.child(tasks.getTaskKey()).child(Constants.VIEWER_PATH).child(mAuth.getUid()).exists()) {
//                        model.setSubscribed(true);
                    } else {

                        taskArrayList.add(tasks);
                    }
                }

                for (int i = 0; i <= taskArrayList.size() - 1; i++) {
                    Log.d(TAG, "onDataChange: " + i);
                    Taskk currentTask1 = taskArrayList.get(i);
                    if (currentTask1.getCompletedDate() != null)
                        if (!currentTask1.getCompletedDate().equals("error")) {
                            Log.d(TAG, "onDataChange: removed");
                            taskArrayList.remove(i);
//                            taskArrayList.remove(currentTask1);
                        }
                }

//                for (Taskk task1 : taskArrayList) {
//
//                    Toast.makeText(getActivity(), task1.getTaskKey(), Toast.LENGTH_SHORT).show();
//                }

                if (taskArrayList.size() > 0) {
                    Log.d(TAG, "onDataChange: if (taskArrayList.size() > 0) {");
                    videoUrl = taskArrayList.get(0).getVideoUrl();
/*TODO: REMOVEDD

                    currentPoints = Integer.parseInt(taskArrayList.get(0)
                            .getTotalViewTimeQuantity());

                    currentPointTextview.setText(currentPoints + "");
*/

                    initYoutubePlayer();
                    Log.e(TAG, "onError: 1234: " + videoUrl);
                } else {
                    try {
                        Toast.makeText(requireContext(), "No video found to watch!", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: " + error.getMessage());
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        youTubePlayerView = root.findViewById(R.id.youtube_player_view_fragment_view);

        root.findViewById(R.id.other_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setNewVideoPlayerDetails();

            }
        });

        switchCompat = root.findViewById(R.id.autoplay_switch);
        currentPointTextview = root.findViewById(R.id.current_point);

        final int percentage = (int) (currentPoints * (10.0f / 100.0f));

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b)
                    currentPoints = currentPoints - percentage;
                else
                    currentPoints = currentPoints + percentage;

//                currentPointTextview.setText(currentPoints + "");

                isAutoPlayEnabled = b;

            }
        });

        databaseReference().child("Banners").child("view").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<SlideModel> imageList = new ArrayList<>();

                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        imageList.add(new SlideModel(dataSnapshot.child("link").getValue(String.class), "", ScaleTypes.CENTER_INSIDE));

                        if (dataSnapshot.child("click").exists())
                            linkList.add(dataSnapshot.child("click").getValue(String.class));
                        else linkList.add("google.com");
                    }

                } else {
                    imageList.add(new SlideModel(R.drawable.mask_group, "", ScaleTypes.CENTER_INSIDE));
                    imageList.add(new SlideModel(R.drawable.mask_group, "", ScaleTypes.CENTER_INSIDE));
                    imageList.add(new SlideModel(R.drawable.mask_group, "", ScaleTypes.CENTER_INSIDE));
                }

                ImageSlider imageSlider = root.findViewById(R.id.image_slider);
                imageSlider.setImageList(imageList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ImageSlider imageSlider = root.findViewById(R.id.image_slider);

        root.findViewById(R.id.imageSliderLayoutView).setOnClickListener(view -> {
            if (linkList.size() > 0) {
                String url = linkList.get(currentClick);

                if (!url.startsWith("https://") && !url.startsWith("http://")) {
                    url = "http://" + url;
                }
                Intent openUrlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(openUrlIntent);
            }
        });

        imageSlider.setItemChangeListener(new ItemChangeListener() {
            @Override
            public void onItemChanged(int i) {
                currentClick = i;
            }
        });

        return root;
    }

    ArrayList<String> linkList = new ArrayList<>();
    int currentClick = 0;

    private void initYoutubePlayer() {
        Log.d(TAG, "initYoutubePlayer: ");
        getLifecycle().addObserver(youTubePlayerView);

        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NotNull YouTubePlayer youTubePlayer) {
                Log.d(TAG, "onReady: public void onReady(YouTubePlayer youTubePlayer) {");

                youTubePlayer1 = youTubePlayer;

//                youTubePlayer.cueVideo(getVideoId(videoUrl), 0);
                youTubePlayer.loadVideo(Helper.getVideoId(videoUrl), 0);
                youTubePlayer.addListener(youTubePlayerListener());

                addFullScreenListenerToPlayer(youTubePlayer);

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
//        youTubePlayerView.getPlayerUiController().showFullscreenButton(false);

        // Showing Full Screen Button
        youTubePlayerView.getPlayerUiController().showFullscreenButton(true);
        youTubePlayerView.getPlayerUiController().setFullScreenButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: fullscreen");
                youTubePlayerView.toggleFullScreen();
            }
        });

        // Hiding Seekbar
        youTubePlayerView.getPlayerUiController().showSeekBar(false);

        TextView tv = root.findViewById(R.id.current_sec);

        tv.setText(taskArrayList.get(currentPosition).getTotalViewTimeQuantity());

        // INIT PLAYER VIEW
        if (!isRunning) {
            new GetVideoTitle(videoUrl).execute();
//            getVideoTitle.setId(videoUrl);
//            getVideoTitle.execute();
        }
    }

    private boolean isRunning = false;

    /*private static String getVideoId(@NonNull String videoUrl) {
        String videoId = "";
        String regex = "http(?:s)?:\\/\\/(?:m.)?(?:www\\.)?youtu(?:\\.be\\/|be\\.com\\/(?:watch\\?(?:feature=youtu.be\\&)?v=|v\\/|embed\\/|user\\/(?:[\\w#]+\\/)+))([^&#?\\n]+)";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(videoUrl);
        if (matcher.find()) {
            videoId = matcher.group(1);
        }
        Log.d(TAG, "getVideoId: videoId "+videoId);
        return videoId;
    }*/

    private final int currentTime = 60;

    private YouTubePlayerListener youTubePlayerListener() {
        return new YouTubePlayerListener() {
            @Override
            public void onReady(@NotNull YouTubePlayer youTubePlayer) {

            }

            @Override
            public void onStateChange(@NotNull YouTubePlayer youTubePlayer, @NotNull PlayerConstants.PlayerState playerState) {
//                Toast.makeText(getActivity(), playerState.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPlaybackQualityChange(@NotNull YouTubePlayer youTubePlayer, @NotNull PlayerConstants.PlaybackQuality playbackQuality) {

            }

            @Override
            public void onPlaybackRateChange(@NotNull YouTubePlayer youTubePlayer, @NotNull PlayerConstants.PlaybackRate playbackRate) {

            }

            @Override
            public void onError(@NotNull YouTubePlayer youTubePlayer, @NotNull PlayerConstants.PlayerError playerError) {
                Log.e(TAG, "onError: ");
                Log.e(TAG, "onError: 1234: " + taskArrayList.get(currentPosition).getTaskKey());
//                taskArrayList.remove(currentPosition);
                databaseReference().child("tasks").child(taskArrayList.get(currentPosition).getTaskKey())
                        .removeValue();
                setNewVideoPlayerDetails();

            }

            final int prev = 0;

            @Override
            public void onCurrentSecond(@NotNull YouTubePlayer youTubePlayer, float v) {
                Log.d(TAG, "onCurrentSecond: " + Math.round(v));
                showToastOnDifferentSec(Math.round(v));

//                if (Math.round(v) != prev) {
//                    currentTime = currentTime - Math.round(v);
//                    prev = currentTime;
//
//                    TextView sec = root.findViewById(R.id.current_sec);
//                    sec.setText(currentTime + "");
//                }
//                Toast.makeText(getActivity(), Math.round(v) + "", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVideoDuration(@NotNull YouTubePlayer youTubePlayer, float v) {
                Log.d(TAG, "onVideoDuration: " + Math.round(v));
                currentVideoLength = Math.round(v);

                //                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        youTubePlayer.loadVideo(getVideoId(url3), 0);
//                    }
//                }, 3000);
            }

            @Override
            public void onVideoLoadedFraction(@NotNull YouTubePlayer youTubePlayer, float v) {

            }

            @Override
            public void onVideoId(@NotNull YouTubePlayer youTubePlayer, @NotNull String s) {
//                Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onApiChange(@NotNull YouTubePlayer youTubePlayer) {

            }
        };
    }

    int nmbr = 0;

    boolean run = true;

    private void showToastOnDifferentSec(int sec) {
        Log.d(TAG, "showToastOnDifferentSec: " + sec);
        if (!run)
            return;

        if (sec == nmbr) {
            Log.d(TAG, "showToastOnDifferentSec: if (sec == nmbr) {");

        } else {

            if (sec == Integer.parseInt(taskArrayList.get(currentPosition).getTotalViewTimeQuantity()) + 1
                    || sec == currentVideoLength
            ) {
                run = false;

                Toast.makeText(getActivity(), "Completed!", Toast.LENGTH_SHORT).show();

                uploadAddedVideoViews();

//                setNewVideoPlayerDetails();

            } else {

                TextView tv = root.findViewById(R.id.current_sec);

                int currentValue = Integer.parseInt(tv.getText().toString());
                String newValue = String.valueOf(currentValue - 1);

                if (Integer.parseInt(newValue) < 0)
                    return;

                tv.setText(newValue);

//            Toast.makeText(getActivity(), sec+"", Toast.LENGTH_SHORT).show();
                nmbr = sec;
            }
        }

    }

    private void setNewVideoPlayerDetails() {
        if (youTubePlayer1 == null) {
            try {
                Toast.makeText(requireContext(), "Player is getting ready!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        String url = getNextUrl();

        if (url.equals("null"))
            return;

//        GetVideoTitle getVideoTitle1 = new GetVideoTitle();

//        getVideoTitle1.setId(url);
//        getVideoTitle1.execute();

        if (!isRunning) {
            new GetVideoTitle(url).execute();
//            getVideoTitle.setId(videoUrl);
//            getVideoTitle.execute();
        }


        youTubePlayer1.loadVideo(Helper.getVideoId(url), 0);

        if (!isAutoPlayEnabled)
            youTubePlayer1.pause();

        TextView tv = root.findViewById(R.id.current_sec);

        tv.setText(taskArrayList.get(currentPosition).getTotalViewTimeQuantity());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                progressDialog.dismiss();
                run = true;

            }
        }, 1000);
    }

    private ProgressDialog progressDialog;

    private void uploadAddedVideoViews() {

        progressDialog.show();

        databaseReference().child("tasks")
                .child(taskArrayList.get(currentPosition).getTaskKey())
                //.child("currentViewsQuantity")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        Taskk taskk = snapshot.getValue(Taskk.class);

                        String currentViews = String.valueOf(taskk.getCurrentViewsQuantity());

                        if (currentViews.equals(taskk.getTotalViewsQuantity())) {

                            databaseReference()
                                    .child("tasks")
                                    .child(taskArrayList.get(currentPosition).getTaskKey())
                                    .child("completedDate")
                                    .setValue(Utils.getDate())
//                                    .setValue(new Utils().getDate())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

//                                            taskArrayList.remove(currentPosition);

                                            uploadAddedCoins();
                                            // UPLOAD COINS AND THEN RESTART VIDEO PLAYER

                                        }
                                    });

                        } else {

                            databaseReference()
                                    .child("tasks")
                                    .child(taskArrayList.get(currentPosition).getTaskKey())
                                    .child("currentViewsQuantity")
                                    .setValue(taskk.getCurrentViewsQuantity() + 1)
                                    .addOnSuccessListener(
                                            new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    uploadAddedCoins();
                                                }
                                            }
                                    );

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d(TAG, "onCancelled: " + error.getMessage());
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
    }

    private void uploadAddedCoins() {
        databaseReference().child("userinfo").child(mAuth.getCurrentUser().getUid())
                .child("coins").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int value = snapshot.getValue(Integer.class);

                databaseReference().child("userinfo").child(mAuth.getCurrentUser().getUid())
                        .child("coins")
                        .setValue(value + currentPoints)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.e(TAG, "onError: 1234: position " + currentPosition);
                                Log.e(TAG, "onError: 1234: current " + taskArrayList.get(currentPosition).getVideoUrl());
                                databaseReference()
                                        .child("tasks")
                                        .child(taskArrayList.get(currentPosition).getTaskKey())
                                        .child(Constants.VIEWER_PATH)
                                        .child(mAuth.getUid())
                                        .setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        setNewVideoPlayerDetails();

                                    }
                                });

                            }
                        });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: " + error.getMessage());
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private String getNextUrl() {

        if (currentPosition < taskArrayList.size() - 1) {

            currentPosition = currentPosition + 1;
/*TODO: REMOVEDD
            currentPoints = Integer.parseInt(taskArrayList.get(currentPosition)
                    .getTotalViewTimeQuantity());*/

//            currentPointTextview.setText(currentPoints + "");

            return taskArrayList.get(currentPosition).getVideoUrl();
        } else {
            currentPosition = 0;

            if (taskArrayList.size() > 0) {
/*TODO: REMOVEDD
                currentPoints = Integer.parseInt(taskArrayList.get(0)
                        .getTotalViewTimeQuantity());*/

//                currentPointTextview.setText(currentPoints + "");

                return taskArrayList.get(0).getVideoUrl();

            } else {
                try {
                    Toast.makeText(requireContext(), "No video found to watch!", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return "null";
            }

        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        youTubePlayerView.release();
//        getVideoTitle.cancel(true);
    }

    @Override
    public void onStop() {
        super.onStop();

//        getVideoTitle.cancel(true);

    }

    private class GetVideoTitle extends AsyncTask<String, Void, String> {

        public GetVideoTitle(String id) {
            this.id = id;
        }

        private final String id;

//        public void setId(String id) {
//            this.id = id;
//        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isRunning = true;
            if (!isCancelled())
                youTubePlayerView.getPlayerUiController().setVideoTitle(getString(R.string.loading));
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpHandler sh = new HttpHandler();

            String url = "https://www.youtube.com/oembed?format=json&url=" + id;//https://www.youtube.com/watch?v=" + id;

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            String videoTitle = getString(R.string.loading);

            Log.e("", "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject o = new JSONObject(jsonStr);

                    videoTitle = o.getString("title");

                    return videoTitle;

                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            setNewVideoPlayerDetails();

//                            Toast.makeText(getActivity(),
//                                    "Json parsing error: " + e.getMessage(),
//                                    Toast.LENGTH_LONG)
//                                    .show();
                        }
                    });

                    e.printStackTrace();
                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        setNewVideoPlayerDetails();

//                        Toast.makeText(getActivity(),
//                                "Couldn't get json from server!",
//                                Toast.LENGTH_LONG)
//                                .show();
                    }
                });

            }

            return videoTitle;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (!isCancelled())
                youTubePlayerView.getPlayerUiController().setVideoTitle(s);

            isRunning = false;
        }
    }

    private static class Taskk {

        private String videoUrl, thumbnailUrl, posterUid, taskKey,
                totalViewsQuantity, totalViewTimeQuantity, completedDate;
        private int currentViewsQuantity;

        public Taskk(String videoUrl, String thumbnailUrl, String posterUid, String taskKey, String totalViewsQuantity, String totalViewTimeQuantity, String completedDate, int currentViewsQuantity) {
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

        Taskk() {
        }
    }

    private void addFullScreenListenerToPlayer(final YouTubePlayer youTubePlayer) {

        youTubePlayerView.addFullScreenListener(new YouTubePlayerFullScreenListener() {

            @Override
            public void onYouTubePlayerEnterFullScreen() {

                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

                fullScreenHelper.enterFullScreen();

                //addCustomActionToPlayer(youTubePlayer);

                ViewGroup.LayoutParams viewParams = youTubePlayerView.getLayoutParams();
                viewParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                viewParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                youTubePlayerView.setLayoutParams(viewParams);

                // Adding Another Extra Custom Icon to top
//                imageView1.setImageResource(R.drawable.ic_drop_down_menu_white);
//                imageView1.setBackgroundResource(R.drawable.bg_on_board_btn);
                //imageView1.setOnClickListener(listener);
//                resizeImageView(imageView1);
//                youTubePlayerView.getPlayerUiController().addView(imageView1);

            }


            @Override
            public void onYouTubePlayerExitFullScreen() {

                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                fullScreenHelper.exitFullScreen();

                //removeCustomActionFromPlayer();

                ViewGroup.LayoutParams viewParams = youTubePlayerView.getLayoutParams();
                viewParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                viewParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                youTubePlayerView.setLayoutParams(viewParams);

                // Remove View
//                youTubePlayerView.getPlayerUiController().removeView(imageView1);

            }

        });

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        checkOrientationForYoutubePlayer(newConfig);

    }

    private void checkOrientationForYoutubePlayer(Configuration newConfig) {

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Landscape mode

            if (!youTubePlayerView.isFullScreen())
                youTubePlayerView.enterFullScreen();

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // Portrait mode

            if (youTubePlayerView.isFullScreen())
                youTubePlayerView.exitFullScreen();

        }

    }

    private class FullScreenHelper {

        private final Activity context;

        private final View[] views;

        private FullScreenHelper(Activity context, View... views) {

            this.context = context;

            this.views = views;

        }

        private void enterFullScreen() {

            View decorView = getActivity().getWindow().getDecorView();

            hideSystemUI(decorView);

            ((BottomNavigationActivity) getActivity()).hideNavBar();
            ((BottomNavigationActivity) getActivity()).hideTopHeader();

            for (View view : views) {

                view.setVisibility(View.GONE);

                view.invalidate();

            }

        }

        private void exitFullScreen() {

            View decorView = getActivity().getWindow().getDecorView();

            ((BottomNavigationActivity) getActivity()).showNavBar();
            ((BottomNavigationActivity) getActivity()).showTopHeader();

            showSystemUI(decorView);


            for (View view : views) {

                view.setVisibility(View.VISIBLE);

                view.invalidate();

            }

        }

        private void hideSystemUI(View mDecorView) {

            mDecorView.setSystemUiVisibility(

                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE

                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

                            | View.SYSTEM_UI_FLAG_FULLSCREEN

                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

            );

        }

        private void showSystemUI(View mDecorView) {

            mDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        }

    }

    private void resizeImageView(ImageView imageView3) {

        //LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        imageView3.setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8));
        imageView3.setLayoutParams(layoutParams);
    }

    public int dpToPx(int dp) {
        float density = getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }

//    @Override
//    public void onBackPressed() {
//        if (youTubePlayerView.isFullScreen()) {
//
//            youTubePlayerView.exitFullScreen();
//
//        } else {
//
//            super.onBackPressed();
//        }
//    }

    @Override
    public void onResume() {
        super.onResume();

        if (youTubePlayerView.isFullScreen()) {

            View statusView = getActivity().getWindow().getDecorView();

            fullScreenHelper.hideSystemUI(statusView);
        }
    }
}