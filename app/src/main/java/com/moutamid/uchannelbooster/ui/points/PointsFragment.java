package com.moutamid.uchannelbooster.ui.points;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.moutamid.uchannelbooster.R;
import com.moutamid.uchannelbooster.activities.BuyPointsActivity;
import com.moutamid.uchannelbooster.activities.VipActivity;
import com.moutamid.uchannelbooster.databinding.FragmentPointsBinding;

public class PointsFragment extends Fragment {
    private static final String TAG = "PointsFragment";

    private FragmentPointsBinding b;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        b = FragmentPointsBinding.inflate(inflater, container, false);

        b.feedbackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFeedBackDialog();
            }
        });

        b.buyPointsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(requireContext(), BuyPointsActivity.class));

            }
        });

        b.vipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(requireContext(), VipActivity.class));
            }
        });

        return b.getRoot();
    }

    /*private void uploadViewVideos(String videoUrl, String thumbnailUrl) {
        
        FirebaseAuth auth = FirebaseAuth.getInstance();

        String key = databaseReference().child("tasks").push().getKey();

        PointsFragment.Task task1 = new PointsFragment.Task();
        task1.setVideoUrl(videoUrl);
        task1.setThumbnailUrl(thumbnailUrl);
        task1.setTotalViewsQuantity("" + new Random().nextInt(100) + 1500);
        task1.setTotalViewTimeQuantity("" + 60);
        task1.setCompletedDate("error");
        task1.setCurrentViewsQuantity(0);
        task1.setPosterUid(auth.getCurrentUser().getUid());
        task1.setTaskKey(key);

        databaseReference().child("tasks").child(key).setValue(task1);

    }

    private void uploadLikeVideos(String videoUrl, String thumbnailUrl) {
        
        FirebaseAuth auth = FirebaseAuth.getInstance();

        String key = databaseReference().child(Constants.LIKE_TASKS).push().getKey();

        LikeTaskModel task1 = new LikeTaskModel();
        task1.setVideoUrl(videoUrl);
        task1.setThumbnailUrl(thumbnailUrl);
        task1.setTotalLikesQuantity("" + new Random().nextInt(100) + 1500);
        task1.setCompletedDate("error");
        task1.setCurrentLikesQuantity(0);
        task1.setPosterUid(auth.getCurrentUser().getUid());
        task1.setTaskKey(key);

        databaseReference().child(Constants.LIKE_TASKS).child(key).setValue(task1);

    }

    private void uploadSubscribeVideos(String videoUrl, String thumbnailUrl) {
        
        FirebaseAuth auth = FirebaseAuth.getInstance();

        String key = databaseReference().child(Constants.SUBSCRIBE_TASKS).push().getKey();

        SubscribeTaskModel task1 = new SubscribeTaskModel();
        task1.setVideoUrl(videoUrl);
        task1.setThumbnailUrl(thumbnailUrl);
        task1.setTotalSubscribesQuantity("" + new Random().nextInt(100) + 1500);
        task1.setCompletedDate("error");
        task1.setCurrentSubscribesQuantity(0);
        task1.setPosterUid(auth.getCurrentUser().getUid());
        task1.setTaskKey(key);

        databaseReference().child(Constants.SUBSCRIBE_TASKS).child(key).setValue(task1);

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
    */
    private void showFeedBackDialog() {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_feedback);
        dialog.setCancelable(true);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.findViewById(R.id.laterBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // CODE HERE
                dialog.dismiss();
            }
        });

        RatingBar ratingBar = dialog.findViewById(R.id.ratingBar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
//                final String appPackageName = requireContext().getPackageName();

                dialog.dismiss();

                final String appPackageName = "com.whatsapp";
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + requireActivity().getPackageName())));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(layoutParams);
    }

}