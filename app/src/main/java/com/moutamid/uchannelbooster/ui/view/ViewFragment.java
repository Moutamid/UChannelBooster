package com.moutamid.uchannelbooster.ui.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.uchannelbooster.R;
import com.moutamid.uchannelbooster.databinding.FragmentViewBinding;
import com.moutamid.uchannelbooster.models.Taskk;
import com.moutamid.uchannelbooster.models.UserDetails;
import com.moutamid.uchannelbooster.ui.subscribe.services.ForegroundService;
import com.moutamid.uchannelbooster.ui.subscribe.utilis.Stash;
import com.moutamid.uchannelbooster.utils.Constants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViewFragment extends Fragment {
    private ArrayList<Taskk> taskArrayList = new ArrayList<>();
    String videoUrl;
    boolean isAutoPlayEnabled = false;
    FragmentViewBinding binding;
    int currentPoints = 30;
    int currentPosition = 0;
    int CurrentCoins = 0;
    int currentVideoLength = 0;
    boolean VIP_STATUS = false;
    boolean run = true;
    int nmbr = 0;
    ProgressDialog progressDialog;
    YouTubePlayerView youTubePlayerView;
    View customPlayerUi;
    ArrayList<String> linkList = new ArrayList<>();

    public ViewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentViewBinding.inflate(getLayoutInflater(), container, false);

        youTubePlayerView = binding.youtubePlayerViewFragmentView;
        customPlayerUi = youTubePlayerView.inflateCustomPlayerUi(R.layout.custom_player_ui);
        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Constants.databaseReference().child("Banners").child("view").addValueEventListener(new ValueEventListener() {
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

                binding.imageSlider.setImageList(imageList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Constants.databaseReference().child("userinfo").child(Constants.auth().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            UserDetails userDetails = snapshot.getValue(UserDetails.class);
                            CurrentCoins = userDetails.getCoins();
                            VIP_STATUS = Stash.getBoolean(Constants.VIP_STATUS);
                            // Toast.makeText(requireContext(), ""+VIP_STATUS, Toast.LENGTH_SHORT).show();
                            Stash.put(Constants.CURRENT_COINS, userDetails.getCoins());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        binding.switchAuto.setChecked(Stash.getBoolean(Constants.isAutoPlayEnabled, false));

        binding.switchAuto.setOnClickListener(v -> {
            if (!VIP_STATUS) {
                binding.switchAuto.setChecked(false);
                new AlertDialog.Builder(requireContext())
                        .setTitle("VIP account")
                        .setMessage("You need to upgrade to a vip account to use this function.")
                        .setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss())
                        .setPositiveButton("UPGRADE", (dialog, which) -> {
//                            startActivity(new Intent(requireContext(), VIPActivity.class));
//                            requireActivity().finish();
                        })
                        .show();
            } else {
                //binding.switchAuto.setChecked(true);
            }
        });


//        binding.seeOther.setOnClickListener(v -> {
//            if (taskArrayList.size()>0){
//                String url = getNextUrl();
//                int rloww = Integer.parseInt(taskArrayList.get(currentPosition).getTotalViewTimeQuantity());
//                currentPoints = rloww - (rloww / 10);
//                binding.currentPoint.setText(currentPoints+"");
//                Stash.put(Constants.COIN, currentPoints);
//                //binding.youtubePlayerViewFragmentView.release();
//                youTubePlayerView.getYouTubePlayerWhenReady(new YouTubePlayerCallback() {
//                    @Override
//                    public void onYouTubePlayer(@NonNull YouTubePlayer youTubePlayer) {
//                        CustomPlayerUiController customPlayerUiController = new CustomPlayerUiController(requireContext(), customPlayerUi, youTubePlayer, youTubePlayerView);
//                        youTubePlayer.addListener(customPlayerUiController);
//
//                        if (Stash.getBoolean(Constants.isAutoPlayEnabled, false)) {
//                            youTubePlayer.loadVideo( Constants.getVideoId(url), 0f);
//                        } else {
//                            youTubePlayer.cueVideo( Constants.getVideoId(url), 0f);
//                        }
//                    }
//                });
//            } else {
//                Toast.makeText(requireContext(), "No Video Found", Toast.LENGTH_SHORT).show();
//            }
//            //initYoutubePlayer(url);
//        });

        binding.switchAuto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (VIP_STATUS) {
                    isAutoPlayEnabled = isChecked;
                    Stash.put(Constants.isAutoPlayEnabled, isChecked);
                }
            }
        });

        Constants.databaseReference().child(Constants.VIEW_TASKS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    taskArrayList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Taskk tasks = dataSnapshot.getValue(Taskk.class);
                        if (!Constants.auth().getCurrentUser().getUid().equals(tasks.getPosterUid())){
                            if (!snapshot.child(tasks.getTaskKey()).child(Constants.VIEWER_PATH).child(Constants.auth().getCurrentUser().getUid()).exists()) {
                                if (tasks.getCompletedDate() != null){
                                    if (tasks.getCompletedDate().equals("error")){
                                        taskArrayList.add(tasks);
                                    }
                                }
                            }
                        }
                    }
                    progressDialog.dismiss();
                    if (taskArrayList.size() > 0) {
                        videoUrl = taskArrayList.get(0).getVideoUrl();
                        initYoutubePlayer(videoUrl);
                        int rlow = Integer.parseInt(taskArrayList.get(currentPosition).getTotalViewTimeQuantity());
                        currentPoints = rlow - (rlow / 10);
                        binding.currentPoint.setText(currentPoints+"");
                        Stash.put(Constants.COIN, currentPoints);
                    } else {
                        Toast.makeText(requireContext(), "No video Found", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    progressDialog.dismiss();
                    Toast.makeText(requireContext(), "No video Found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return binding.getRoot();
    }

    private void initYoutubePlayer(String url) {
        Log.d("firebaseAuthWithGoogle", url);
        getLifecycle().addObserver(youTubePlayerView);
        YouTubePlayerListener youTubePlayerListener = new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                super.onReady(youTubePlayer);
                CustomPlayerUiController customPlayerUiController = new CustomPlayerUiController(requireContext(), customPlayerUi, youTubePlayer, youTubePlayerView);
                youTubePlayer.addListener(customPlayerUiController);

                if (Stash.getBoolean(Constants.isAutoPlayEnabled, false)) {
                    youTubePlayer.loadVideo( Constants.getVideoId(url), 0f);
                } else {
                    youTubePlayer.cueVideo( Constants.getVideoId(url), 0f);
                }
            }
        };

//        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
//            @Override
//            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
//                String videoId = Constants.getVideoId(url);
//                youTubePlayer.loadVideo(videoId, 0);
//                youTubePlayer.addListener(listner);
//            }
//        });

        IFramePlayerOptions options = new IFramePlayerOptions.Builder().controls(0).build();

        youTubePlayerView.initialize(youTubePlayerListener, options);

        int rlow = Integer.parseInt(taskArrayList.get(currentPosition).getTotalViewTimeQuantity());
        currentPoints = rlow - (rlow / 10);
        Stash.put(Constants.COIN, currentPoints);
        binding.currentPoint.setText(currentPoints+"");
        binding.currentSec.setText(taskArrayList.get(currentPosition).getTotalViewTimeQuantity());
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.youtubePlayerViewFragmentView.matchParent();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding.youtubePlayerViewFragmentView.wrapContent();
        }
    }

    private void setNewVideoPlayerDetails() {
        String url = getNextUrl();

        if (url != null){
            //binding.youtubePlayerViewFragmentView.release();

            YouTubePlayerView youTubePlayerView = binding.youtubePlayerViewFragmentView;
            getLifecycle().addObserver(youTubePlayerView);

            youTubePlayerView.getYouTubePlayerWhenReady(new YouTubePlayerCallback() {
                @Override
                public void onYouTubePlayer(@NonNull YouTubePlayer youTubePlayer) {
                    CustomPlayerUiController customPlayerUiController = new CustomPlayerUiController(requireContext(), customPlayerUi, youTubePlayer, youTubePlayerView);
                    youTubePlayer.addListener(customPlayerUiController);
                    if (Stash.getBoolean(Constants.isAutoPlayEnabled, false)) {
                        youTubePlayer.loadVideo( Constants.getVideoId(url), 0f);
                    } else {
                        youTubePlayer.cueVideo( Constants.getVideoId(url), 0f);
                    }
                }
            });


            int rlow = Integer.parseInt(taskArrayList.get(currentPosition).getTotalViewTimeQuantity());
            currentPoints = rlow - (rlow / 10);
            binding.currentPoint.setText(currentPoints+"");
            Stash.put(Constants.COIN, currentPoints);
            binding.currentSec.setText(taskArrayList.get(currentPosition).getTotalViewTimeQuantity());

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                    run = true;
                }
            }, 1000);

        } else {
            //Toast.makeText(requireContext(), "No Video Found", Toast.LENGTH_SHORT).show();
            return;
        }


    }

    private String getNextUrl() {
        if (currentPosition < taskArrayList.size()-1) {
            currentPosition = currentPosition + 1;
            return taskArrayList.get(currentPosition).getVideoUrl();
        } else {
            currentPosition = 0;
            if (taskArrayList.size() > 0) {
                return taskArrayList.get(currentPosition).getVideoUrl();
            } else {
                Toast.makeText(requireContext(), "No video found to watch!", Toast.LENGTH_SHORT).show();
                return null;
            }

        }

    }

    private void showToastOnDifferentSec(int sec) {
        if (!run)
            return;
        if (sec != nmbr) {
            if (sec == Integer.parseInt(taskArrayList.get(currentPosition).getTotalViewTimeQuantity()) + 1
                    || sec == currentVideoLength
            ) {
                run = false;

                Toast.makeText(getActivity(), "Completed!", Toast.LENGTH_SHORT).show();

                uploadAddedVideoViews();

            } else {

                int currentValue = Integer.parseInt(binding.currentSec.getText().toString());
                String newValue = String.valueOf(currentValue - 1);

//                if (currentValue - 1 >= 0) {
//                    binding.progressIndicator.setProgress((currentValue - 1));
//                } else {
//                    binding.progressIndicator.setProgress(0);
//                }

                if ((currentValue - 1) < 0)
                    return;

                binding.currentSec.setText(newValue);

                nmbr = sec;
            }
        }
    }

    private void uploadAddedVideoViews() {
        if (taskArrayList.size()>0) {
            Log.d("ResponseURL", "if");
            progressDialog.show();
            Constants.databaseReference().child(Constants.VIEW_TASKS)
                    .child(taskArrayList.get(currentPosition).getTaskKey())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            Taskk taskk = snapshot.getValue(Taskk.class);

                            String currentViews = String.valueOf(taskk.getCurrentViewsQuantity());

                            if (currentViews.equals(taskk.getTotalViewsQuantity())) {

                                Constants.databaseReference().child(Constants.VIEW_TASKS)
                                        .child(taskArrayList.get(currentPosition).getTaskKey())
                                        .child("completedDate")
                                        .setValue(Constants.getDate())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                uploadAddedCoins();
                                            }
                                        });

                            } else {

                                Constants.databaseReference().child(Constants.VIEW_TASKS)
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
                            Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
        }
    }

    private void uploadAddedCoins() {
        Constants.databaseReference().child(Constants.USER).child(Constants.auth().getCurrentUser().getUid())
                .child("coins").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        int value = snapshot.getValue(Integer.class);

                        Constants.databaseReference().child(Constants.USER).child(Constants.auth().getCurrentUser().getUid())
                                .child("coins")
                                .setValue(value + currentPoints)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        Map<String, Object> map = new HashMap<>();
                                        map.put("userinfo", Constants.auth().getCurrentUser().getUid());
                                        map.put("date", Constants.getDate());

                                        Constants.databaseReference().child(Constants.VIEW_TASKS)
                                                .child(taskArrayList.get(currentPosition).getTaskKey())
                                                .child(Constants.VIEWER_PATH)
                                                .push()
                                                .setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        Stash.put(Constants.CHECK, false);
                                                        progressDialog.dismiss();
                                                        Log.d("ResponseURL", "added");
                                                        if (Stash.getBoolean(Constants.isAutoPlayEnabled, false)) {
                                                            Log.d("ResponseURL", "auto");
                                                            setNewVideoPlayerDetails();
                                                        }
                                                    }
                                                });

                                    }
                                });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // binding.youtubePlayerViewFragmentView.release();
    }

    public class CustomPlayerUiController extends AbstractYouTubePlayerListener {
        private final YouTubePlayerTracker playerTracker;
        private final Context context;
        private final YouTubePlayer youTubePlayer;
        private final YouTubePlayerView youTubePlayerView;
        // panel is used to intercept clicks on the WebView, I don't want the user to be able to click the WebView directly.
        private View panel;
        TextView videoDurationTextView;
        ImageView playBtn;
        private LinearProgressIndicator progressbar;
        private boolean fullscreen = false, isPlaying = false;

        public CustomPlayerUiController(Context context, View customPlayerUi, YouTubePlayer youTubePlayer, YouTubePlayerView youTubePlayerView) {
            this.context = context;
            this.youTubePlayer = youTubePlayer;
            this.youTubePlayerView = youTubePlayerView;

            playerTracker = new YouTubePlayerTracker();

            initViews(customPlayerUi);
        }

        private void initViews(View playerUi) {
            panel = playerUi.findViewById(R.id.panel);
            progressbar = playerUi.findViewById(R.id.progressbar);
            playBtn = playerUi.findViewById(R.id.playBtn);
            videoDurationTextView = playerUi.findViewById(R.id.videoDurationTextView);

            playBtn.setOnClickListener(v -> {
                youTubePlayer.addListener(playerTracker);
                if (isPlaying) {
                    youTubePlayer.pause();
                    isPlaying = false;
                    playBtn.setImageResource(R.drawable.round_play_arrow_24);
                } else {
                    isPlaying = true;
                    playBtn.setImageResource(R.drawable.round_pause_circle_24);
                    youTubePlayer.play();
                }

            });

        }

        @Override
        public void onReady(@NonNull YouTubePlayer youTubePlayer) {
            //progressbar.setVisibility(View.GONE);
        }

        @Override
        public void onStateChange(@NonNull YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlayerState state) {
            if (state == PlayerConstants.PlayerState.PLAYING || state == PlayerConstants.PlayerState.PAUSED || state == PlayerConstants.PlayerState.VIDEO_CUED)
                panel.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
            else if (state == PlayerConstants.PlayerState.BUFFERING)
                panel.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
        }
        int dur = 00;

        @Override
        public void onError(@NonNull YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlayerError playerError) {
            setNewVideoPlayerDetails();
        }
        @SuppressLint("SetTextI18n")
        @Override
        public void onCurrentSecond(@NonNull YouTubePlayer youTubePlayer, float v) {
            progressbar.setProgress(Math.round(v));
            videoDurationTextView.setText(Math.round(v)+":"+dur);
            showToastOnDifferentSec(Math.round(v));

            if (Math.round(v) == 5) {
                boolean check = Stash.getBoolean(Constants.CHECK, false);
                if (check) {
                    check = false;
                    if (Stash.getBoolean(Constants.isAutoPlayEnabled, false)) {
                        String url = getNextUrl();
                        youTubePlayerView.getYouTubePlayerWhenReady(youTubePlayer1 -> {
                            CustomPlayerUiController customPlayerUiController = new CustomPlayerUiController(requireContext(), customPlayerUi, youTubePlayer1, youTubePlayerView);
                            youTubePlayer1.addListener(customPlayerUiController);

                            if (Stash.getBoolean(Constants.isAutoPlayEnabled, false)) {
                                youTubePlayer.loadVideo( Constants.getVideoId(url), 0f);
                            } else {
                                youTubePlayer.cueVideo( Constants.getVideoId(url), 0f);
                            }

//                            YouTubePlayerUtils.loadOrCueVideo(
//                                    youTubePlayer1, getLifecycle(),
//                                    Constants.getVideoId(url), 0f
//                            );

                        });
                    } else {
                        setNewVideoPlayerDetails();
                    }
                } else {
                    if (checkOverlayPermission()){
                        startService();
                        String url = taskArrayList.get(currentPosition).getVideoUrl();
                        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + Constants.getVideoId(url)));
                        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://www.youtube.com/watch?v=" + Constants.getVideoId(url)));
                        try {
                            context.startActivity(appIntent);
                        } catch (ActivityNotFoundException ex) {
                            context.startActivity(webIntent);
                        }
                    }
                }
            }

        }

        @Override
        public void onVideoDuration(@NonNull YouTubePlayer youTubePlayer, float v) {
            dur = Math.round(v);
            videoDurationTextView.setText("00:"+dur);
            currentVideoLength = Math.round(v);
            int s = 0;
            try {
                s = Integer.parseInt(binding.currentSec.getText().toString());
            } catch (Exception e) {e.printStackTrace();}
//            binding.progressIndicator.setMax(s);
            Stash.put(Constants.TIME, s);


        }

    }

    public boolean checkOverlayPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(requireContext())) {
                // send user to the device settings
                Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                startActivity(myIntent);
                return false;
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS);
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.POST_NOTIFICATIONS}, 1);
                return false;
            }
        }

        return true;
    }

    public void startService(){
        if(Settings.canDrawOverlays(requireContext())) {
            // start the service based on the android version
            Intent i = new Intent(requireContext(), ForegroundService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                requireContext().startForegroundService(i);
            } else {
                requireContext().startService(i);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        boolean check = Stash.getBoolean(Constants.CHECK, false);
        Log.d("ResponseURL", "Check " + check);
        if (check) {
            uploadAddedVideoViews();
            Log.d("ResponseURL", "Check true");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // binding.youtubePlayerViewFragmentView.release();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding.youtubePlayerViewFragmentView.release();
    }
}