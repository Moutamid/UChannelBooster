package com.moutamid.uchannelbooster.ui.subscribe;

import static com.bumptech.glide.Glide.with;
import static com.bumptech.glide.load.engine.DiskCacheStrategy.DATA;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.Subscription;
import com.google.api.services.youtube.model.SubscriptionSnippet;
import com.google.api.services.youtube.model.Video;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.uchannelbooster.R;
import com.moutamid.uchannelbooster.databinding.FragmentSubscribeBinding;
import com.moutamid.uchannelbooster.ui.subscribe.services.ForegroundService;
import com.moutamid.uchannelbooster.ui.subscribe.utilis.Stash;
import com.moutamid.uchannelbooster.ui.subscribe.utilis.SubscribeTaskModel;
import com.moutamid.uchannelbooster.utils.Constants;
import com.moutamid.uchannelbooster.utils.Helper;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SubscribeFragment extends Fragment {
    FragmentSubscribeBinding b;
    private static final String TAG = "SubscribeFragment";
    private static final String[] SCOPES = {YouTubeScopes.YOUTUBE_READONLY, YouTubeScopes.YOUTUBE_FORCE_SSL};
    Video video;
    GoogleAccountCredential mCredential;
    String currentVideoLink;
    ProgressDialog mProgress;
    Handler handler = new Handler();
    boolean isTimerRunning = false;
    ArrayList<SubscribeTaskModel> subscribeTaskModelArrayList = new ArrayList<>();
    private ProgressDialog progressDialog;
    int currentCounter = 0;
    int currentPoints = 30;
    boolean vipStatus;
    ArrayList<String> linkList = new ArrayList<>();
    boolean default_image = false;

    public SubscribeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        b = FragmentSubscribeBinding.inflate(getLayoutInflater(), container, false);
        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        mProgress = new ProgressDialog(requireContext());
        mProgress.setMessage("Please Wait");

        mCredential = GoogleAccountCredential.usingOAuth2(requireContext().getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

        vipStatus = Stash.getBoolean(Constants.VIP_STATUS, false);
        Constants.databaseReference().child(Constants.SUBSCRIBE_TASKS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("ataaaaaa", snapshot + "-------");
                if (snapshot.exists()) {
                    subscribeTaskModelArrayList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        SubscribeTaskModel model = dataSnapshot.getValue(SubscribeTaskModel.class);

                        if (snapshot.child(model.getTaskKey()).child(Constants.SUBSCRIBER_PATH).child(Constants.auth().getCurrentUser().getUid()).exists()) {
//                        model.setSubscribed(true);
                        } else {
                            subscribeTaskModelArrayList.add(model);
                            Stash.put(Constants.subscribeTaskModelArrayList, subscribeTaskModelArrayList);
                        }
                    }

                    if (subscribeTaskModelArrayList.size() > 0) {
                        int rlow = Integer.parseInt(subscribeTaskModelArrayList.get(currentCounter).getTotalViewTimeQuantity());
                        currentPoints = rlow - (rlow / 10);
                        Stash.put(Constants.COIN, currentPoints);
                    }

                    progressDialog.dismiss();
                    setDataOnViews(0, false);
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(requireContext(), "No data found", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(requireContext(), "No data found", Toast.LENGTH_SHORT).show();
            }
        });
        b.autoPlaySwitchSubscribe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Stash.put(Constants.subscribeSwitchState, b);
                Stash.put(Constants.likeSwitchState, false);

            }
        });
        b.seeOther.setOnClickListener(view -> {
            ArrayList<SubscribeTaskModel> subscribeTaskModelArrayList = Stash.getArrayList(Constants.subscribeTaskModelArrayList, SubscribeTaskModel.class);
            int currentCountervalue = Stash.getInt(Constants.COUNTER_VALUE, 0);
            currentCountervalue++;
            if (currentCountervalue >= subscribeTaskModelArrayList.size()) {
                Toast.makeText(requireContext(), "End of task", Toast.LENGTH_SHORT).show();

            } else {
                int rloww = Integer.parseInt(subscribeTaskModelArrayList.get(currentCountervalue).getTotalViewTimeQuantity());
                currentPoints = rloww - (rloww / 10);
                Stash.put(Constants.COIN, currentPoints);
                setDataOnViews(currentCountervalue, false);
                Stash.put(Constants.COUNTER_VALUE, currentCountervalue);

            }

        });
        b.subscribeBtn.setOnClickListener(view -> {
            try {
//                Stash.put(Constants.COUNTER_VALUE, currentCounter);
                subscribeToYoutubeChannel();
            } catch (GeneralSecurityException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        Constants.databaseReference().child("Banners").child("subscribe").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<SlideModel> imageList = new ArrayList<>();

                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                        imageList.add(new SlideModel(dataSnapshot.child("link").getValue(String.class), "", ScaleTypes.CENTER_INSIDE));
                        default_image = false;

                        if (dataSnapshot.child("click").exists())
                            linkList.add(dataSnapshot.child("click").getValue(String.class));
                        else linkList.add("google.com");
                    }

                } else {
                    default_image = true;

                    imageList.add(new SlideModel(R.drawable.mask_group, "", ScaleTypes.CENTER_INSIDE));
                }
                if (!default_image) {
                    imageList.add(new SlideModel(R.drawable.mask_group, "", ScaleTypes.CENTER_INSIDE));
                }
                b.imageSlider.setImageList(imageList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return b.getRoot();
    }

    private void subscribeToYoutubeChannel() throws GeneralSecurityException, IOException {
        Subscription subscription = new Subscription();
        SubscriptionSnippet snippet = new SubscriptionSnippet();
        ResourceId resourceId = new ResourceId();
//        resourceId.setChannelId(video.getSnippet().getChannelId());
        resourceId.setKind("youtube#channel");
        snippet.setResourceId(resourceId);
        subscription.setSnippet(snippet);
        new Thread(() -> requireActivity().runOnUiThread(() -> {
            if (checkOverlayPermission()) {
                startService();

                String url = currentVideoLink;
                Log.d("YouTubeIntent", "Opening video: " + url);

                // Open YouTube Home first to refresh
                Intent youtubeHomeIntent = new Intent(Intent.ACTION_MAIN);
                youtubeHomeIntent.setComponent(new ComponentName("com.google.android.youtube", "com.google.android.youtube.HomeActivity"));
                youtubeHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                requireContext().startActivity(youtubeHomeIntent);

                // Wait before launching the new video
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    Intent appIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://www.youtube.com/watch?v=" + Constants.getVideoId(url) + "&t=0s&autoplay=1"));
                    appIntent.setPackage("com.google.android.youtube");
                    appIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    requireContext().startActivity(appIntent);
                }, 2000);  // 2-second delay

                // Send Play command
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    Intent playIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
                    playIntent.setPackage("com.google.android.youtube");
                    playIntent.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY));
                    requireContext().sendBroadcast(playIntent);
                }, 1000); // Play after 4 seconds
            }
        })).start();

    }

    private void uploadAddedSubscribers() {
        if (subscribeTaskModelArrayList.size() > 0) {
            Constants.databaseReference()
                    .child(Constants.SUBSCRIBE_TASKS)
                    .child(subscribeTaskModelArrayList.get(currentCounter).getTaskKey())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            SubscribeTaskModel taskk = snapshot.getValue(SubscribeTaskModel.class);

                            String currentViews = String.valueOf(taskk.getCurrentSubscribesQuantity());

                            if (currentViews.equals(taskk.getTotalSubscribesQuantity())) {

                                Constants.databaseReference()
                                        .child(Constants.SUBSCRIBE_TASKS)
                                        .child(subscribeTaskModelArrayList.get(currentCounter).getTaskKey())
                                        .child("completedDate")
                                        .setValue(Constants.getDate())
//                                    .setValue(new Utils().getDate())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                uploadAddedCoins();
                                            }
                                        });

                            } else {

                                Constants.databaseReference()
                                        .child(Constants.SUBSCRIBE_TASKS)
                                        .child(subscribeTaskModelArrayList.get(currentCounter).getTaskKey())
                                        .child("currentSubscribesQuantity")
                                        .setValue(taskk.getCurrentSubscribesQuantity() + 1)
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
                            progressDialog.dismiss();
                            Log.d(TAG, "onCancelled: " + error.getMessage());
                            Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                            mProgress.hide();
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
                                        Constants.databaseReference()
                                                .child(Constants.SUBSCRIBE_TASKS)
                                                .child(subscribeTaskModelArrayList.get(currentCounter).getTaskKey())
                                                .child(Constants.SUBSCRIBER_PATH)
                                                .push()
                                                .setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        progressDialog.dismiss();
                                                        mProgress.hide();
//                                                        Toast.makeText(requireContext(), "Subscribed Done", Toast.LENGTH_SHORT).show();
                                                        Stash.put(Constants.CHECK, false);
                                                        boolean auto = Stash.getBoolean("auto", false);
//                                                        if (auto) {
//                                                            b.seeOther.performClick();
//                                                            b.subscribeBtn.performClick();
//
//                                                        } else {
                                                        currentCounter++;
                                                        if (currentCounter >= subscribeTaskModelArrayList.size()) {
                                                            Toast.makeText(requireContext(), "End of task", Toast.LENGTH_SHORT).show();
                                                            b.videoImageSubscribe.setBackgroundResource(0);
                                                        } else {
                                                            int rlow = Integer.parseInt(subscribeTaskModelArrayList.get(currentCounter).getTotalViewTimeQuantity());
                                                            currentPoints = rlow - (rlow / 10);
                                                            Stash.put(Constants.COIN, currentPoints);
                                                            setDataOnViews(currentCounter, true);
//                                                            }
                                                        }
                                                        ;

                                                    }
                                                });

                                    }
                                });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d(TAG, "onCancelled: " + error.getMessage());
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        mProgress.hide();
                    }
                });
    }

    public boolean checkOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(requireContext())) {
                // send user to the device settings
                Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                startActivity(myIntent);
                return false;
            }
        }
        return true;
    }

    public void startService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(requireContext())) {
                // start the service based on the android version
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Intent i = new Intent(requireContext(), ForegroundService.class);
                    requireContext().startForegroundService(i);
                } else {
                    Intent i = new Intent(requireContext(), ForegroundService.class);
                    requireContext().startService(i);
                }
            }
        } else {
            Intent i = new Intent(requireContext(), ForegroundService.class);
            requireContext().startService(i);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        boolean check = Stash.getBoolean(Constants.CHECK, false);
        boolean auto = Stash.getBoolean(Constants.subscribeSwitchState, false);
        b.autoPlaySwitchSubscribe.setChecked(auto);
//

        if (auto) {
            showCountdownDialog();

//            b.seeOther.performClick()

        }
//       else
//       if (check) {
//        uploadAddedSubscribers();
//        }


    }

    private void setDataOnViews(int counter, boolean isTaskCompleted) {

        if (subscribeTaskModelArrayList.size() == 0)
            return;

        // IF FIRST TIME
        if (counter == 0 || !isTaskCompleted) {
            progressDialog.show();
            b.videoIdSubscribe.setText(
                    getString(R.string.videoidmark) + Helper.getVideoId(subscribeTaskModelArrayList.get(counter).getVideoUrl())
            );
            b.videoImageSubscribe.setScaleType(ImageView.ScaleType.CENTER_CROP);
            with(requireActivity())
                    .asBitmap()
                    .load(subscribeTaskModelArrayList.get(counter).getThumbnailUrl())
                    .apply(new RequestOptions()
                            .placeholder(R.color.light_grey)
                            .error(R.color.light_grey)
                    )
                    .diskCacheStrategy(DATA)
                    .addListener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    b.videoImageSubscribe.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                    b.videoImageSubscribe.setImageResource(R.drawable.ic_baseline_access_time_filled_24);

                                    currentCounter++;

                                    if (currentCounter >= subscribeTaskModelArrayList.size()) {
                                        Toast.makeText(requireContext(), "End of task", Toast.LENGTH_SHORT).show();
                                        b.videoImageSubscribe.setBackgroundResource(0);
                                    } else setDataOnViews(currentCounter, false);

                                }
                            });

                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(b.videoImageSubscribe);

            currentVideoLink = subscribeTaskModelArrayList.get(counter).getVideoUrl();
            int i = Integer.parseInt(subscribeTaskModelArrayList.get(counter).getTotalViewTimeQuantity());
            Stash.put(Constants.TIME, i);
            Stash.put(Constants.COIN, currentPoints);
            progressDialog.dismiss();

            return;
        }

        // IF SECOND OR THIRD TIME
        b.videoImageSubscribe.setScaleType(ImageView.ScaleType.FIT_CENTER);
        b.videoImageSubscribe.setImageResource(R.drawable.ic_baseline_access_time_filled_24);
        new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                b.videoImageSubscribe.setScaleType(ImageView.ScaleType.FIT_CENTER);
                b.videoImageSubscribe.setImageResource(R.drawable.ic_baseline_access_time_filled_24);
                b.videoImageSubscribe.animate().rotation(b.videoImageSubscribe.getRotation() + 20)
                        .setDuration(100).start();

                isTimerRunning = true;
                b.subscribeBtn.setEnabled(false);
                b.seeOther.setEnabled(false);
            }

            public void onFinish() {
                isTimerRunning = false;
//                b.autoPlaySwitchSubscribe.setEnabled(true);
                b.subscribeBtn.setEnabled(true);
                b.seeOther.setEnabled(true);

                b.videoImageSubscribe.setRotation(0);

                progressDialog.show();

                b.videoImageSubscribe.setScaleType(ImageView.ScaleType.CENTER_CROP);
                with(requireContext())
                        .asBitmap()
                        .load(subscribeTaskModelArrayList.get(counter).getThumbnailUrl())
                        .apply(new RequestOptions()
                                .placeholder(R.color.light_grey)
                                .error(R.color.light_grey)
                        )
                        .addListener(new RequestListener<Bitmap>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        b.videoImageSubscribe.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                        b.videoImageSubscribe.setImageResource(R.drawable.ic_baseline_access_time_filled_24);
                                        currentCounter++;
                                        int rlow = Integer.parseInt(subscribeTaskModelArrayList.get(currentCounter).getTotalViewTimeQuantity());
                                        currentPoints = rlow - (rlow / 10);
                                        Stash.put(Constants.COIN, currentPoints);
                                        if (currentCounter >= subscribeTaskModelArrayList.size()) {
                                            Toast.makeText(requireContext(), "End of task", Toast.LENGTH_SHORT).show();
                                            b.videoImageSubscribe.setBackgroundResource(0);
                                        } else setDataOnViews(currentCounter, false);
                                    }
                                });
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                return false;
                            }
                        })
                        .diskCacheStrategy(DATA)
                        .into(b.videoImageSubscribe);

                currentVideoLink = subscribeTaskModelArrayList.get(counter).getVideoUrl();

                progressDialog.dismiss();

            }
        }.start();

    }

    public void showCountdownDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View view = inflater.inflate(R.layout.countdown_dialog, null);
        builder.setView(view);

        TextView countdownText = view.findViewById(R.id.countdownText);
        Switch autoplaySwitch = view.findViewById(R.id.autoplaySwitch);
        autoplaySwitch.setChecked(Stash.getBoolean(Constants.subscribeSwitchState));
        AlertDialog dialog = builder.create();
        dialog.show();

        // Countdown Logic
        Handler handler = new Handler(Looper.getMainLooper());
        int[] counter = {5}; // Start countdown from 3

        Runnable countdownRunnable = new Runnable() {
            @Override
            public void run() {
                ArrayList<SubscribeTaskModel> list = Stash.getArrayList(Constants.subscribeTaskModelArrayList, SubscribeTaskModel.class);
                int currentCountervalue = Stash.getInt(Constants.COUNTER_VALUE, 0);
                currentCountervalue++;
                Log.d("dataaaa", currentCountervalue + "====" + list.size() + "====" + "=====" + currentVideoLink);

                if (currentCountervalue < list.size()) {
                    setDataOnViews(currentCountervalue, false);
                }
                if (counter[0] > 0) {
                    countdownText.setText(counter[0] + "");
                    counter[0]--;
                    handler.postDelayed(this, 1000); // 1-second interval
                } else {
                    dialog.dismiss(); // Close the dialog
                    boolean isAutoplayOn = autoplaySwitch.isChecked();
                    Stash.put(Constants.subscribeSwitchState, isAutoplayOn);
                    if (isAutoplayOn) {
                        Log.d("dataaaa", currentCountervalue + "====" + list.size() + "====" + "=====" + currentVideoLink);
                        if (currentCountervalue >= list.size()) {
                            Toast.makeText(requireContext(), "End of task", Toast.LENGTH_SHORT).show();
                        } else {
                            currentVideoLink = list.get(currentCountervalue).getVideoUrl();
                            Stash.put(Constants.COUNTER_VALUE, currentCountervalue);
                            b.subscribeBtn.performClick();
                        }
                    }
                }
            }
        };

        handler.post(countdownRunnable);
    }


}