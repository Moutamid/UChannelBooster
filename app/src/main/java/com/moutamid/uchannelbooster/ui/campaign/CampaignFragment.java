package com.moutamid.uchannelbooster.ui.campaign;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.uchannelbooster.R;
import com.moutamid.uchannelbooster.activities.AddTaskActivity;
import com.moutamid.uchannelbooster.databinding.FragmentCampaignBinding;
import com.moutamid.uchannelbooster.models.LikeTaskModel;
import com.moutamid.uchannelbooster.models.TasksTypeModel;
import com.moutamid.uchannelbooster.models.ViewTaskModel;
import com.moutamid.uchannelbooster.ui.subscribe.utilis.Stash;
import com.moutamid.uchannelbooster.ui.subscribe.utilis.SubscribeTaskModel;
import com.moutamid.uchannelbooster.utils.Constants;
import com.moutamid.uchannelbooster.utils.Helper;

import java.util.ArrayList;
import java.util.Collections;

public class CampaignFragment extends Fragment {
    FragmentCampaignBinding binding;
    ArrayList<TasksTypeModel> allTasksArrayList = new ArrayList<>();
    ArrayList<TasksTypeModel> views = new ArrayList<>();
    ArrayList<TasksTypeModel> likes = new ArrayList<>();
    ArrayList<TasksTypeModel> subs = new ArrayList<>();
    CampaignAdapter adapter;

    public CampaignFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentCampaignBinding.inflate(getLayoutInflater(), container, false);


       binding.viewTaskBtn.setOnClickListener(viewTaskBtnClickListener());
        binding.likeTaskBtn.setOnClickListener(likeTaskBtnClickListener());
        binding.subscribeTaskBtn.setOnClickListener(subscribeTaskBtnClickListener());
        binding.addBtn.setOnClickListener(addBtnShowTasksClickListener());

//        binding.addBtn.setOnClickListener(v -> {
//            showDialog();
//        });

        binding.recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recycler.setHasFixedSize(false);

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        getViewTasksList();
    }

    private void getViewTasksList() {
        Constants.databaseReference().child(Constants.VIEW_TASKS).orderByChild("posterUid")
                .equalTo(Constants.auth().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            views.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                ViewTaskModel task = dataSnapshot.getValue(ViewTaskModel.class);
                                TasksTypeModel tasksTypeModel = new TasksTypeModel();
                                tasksTypeModel.setViewTaskModel(task);
                                tasksTypeModel.setType(Constants.TYPE_VIEW);
                                views.add(tasksTypeModel);
                            }
                        }
                        getLikeTasksList();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getLikeTasksList() {
        Constants.databaseReference().child(Constants.LIKE_TASKS).orderByChild("posterUid")
                .equalTo(Constants.auth().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            likes.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                                LikeTaskModel task = dataSnapshot.getValue(LikeTaskModel.class);

                                TasksTypeModel tasksTypeModel = new TasksTypeModel();
                                tasksTypeModel.setLikeTaskModel(task);
                                tasksTypeModel.setType(Constants.TYPE_LIKE);
                                likes.add(tasksTypeModel);
                            }
                        }
                        getSubscribeTasksList();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getSubscribeTasksList() {
        Constants.databaseReference().child(Constants.SUBSCRIBE_TASKS).orderByChild("posterUid")
                .equalTo(Constants.auth().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            subs.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                SubscribeTaskModel task = dataSnapshot.getValue(SubscribeTaskModel.class);
                                TasksTypeModel tasksTypeModel = new TasksTypeModel();
                                tasksTypeModel.setSubscribeTaskModel(task);
                                tasksTypeModel.setType(Constants.TYPE_SUBSCRIBE);
                                subs.add(tasksTypeModel);
                            }
                        }

                        initRecyclerView();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }

    private void initRecyclerView() {
        allTasksArrayList.clear();
        allTasksArrayList.addAll(views);
        allTasksArrayList.addAll(likes);
        allTasksArrayList.addAll(subs);
        Collections.reverse(allTasksArrayList);

        if (allTasksArrayList.size() > 0){
            binding.noCampLayout.setVisibility(View.GONE);
            binding.recycler.setVisibility(View.VISIBLE);

            adapter = new CampaignAdapter(requireContext(), allTasksArrayList);
            binding.recycler.setAdapter(adapter);
        } else {
            binding.noCampLayout.setVisibility(View.VISIBLE);
            binding.recycler.setVisibility(View.GONE);
        }
    }

    private void showDialog() {
//        final Dialog dialog = new Dialog(requireContext());
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.new_campaign_dialog);
//
//        Button all = dialog.findViewById(R.id.all);
//        Button view = dialog.findViewById(R.id.view);
//        Button like = dialog.findViewById(R.id.like);
//        Button cancel = dialog.findViewById(R.id.cancel);
//
//        all.setOnClickListener(v -> {
//            Stash.put(Constants.CAMPAIGN_SELECTION, 0);
//            startActivity(new Intent(requireContext(), AddVideoActivity.class));
//            dialog.dismiss();
//        });
//
//        view.setOnClickListener(v -> {
//            Stash.put(Constants.CAMPAIGN_SELECTION, 1);
//            startActivity(new Intent(requireContext(), AddVideoActivity.class));
//            dialog.dismiss();
//        });
//
//        like.setOnClickListener(v -> {
//            Stash.put(Constants.CAMPAIGN_SELECTION, 2);
//            startActivity(new Intent(requireContext(), AddVideoActivity.class));
//            dialog.dismiss();
//        });

//        cancel.setOnClickListener(v -> {
//            dialog.dismiss();
//        });
//
//        dialog.show();
//        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialog.getWindow().setGravity(Gravity.CENTER);

    }

    String videoType = Constants.TYPE_VIEW;

    private void showAddTaskDialog(String title) {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_video_task);
        dialog.setCancelable(true);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        TextView titleTv = dialog.findViewById(R.id.titleDialog);
        titleTv.setText(title);

        dialog.findViewById(R.id.addTaskButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // CODE HERE
                setAddTaskButton(dialog);
            }
        });
        dialog.show();
        dialog.getWindow().setAttributes(layoutParams);
    }

    private View.OnClickListener viewTaskBtnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoType = Constants.TYPE_VIEW;
                Stash.put(Constants.CAMPAIGN_SELECTION, 1);
                showAddTaskDialog(getString(R.string.addvideoforviews));

            }
        };
    }

    private View.OnClickListener likeTaskBtnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoType = Constants.TYPE_LIKE;
                Stash.put(Constants.CAMPAIGN_SELECTION, 2);

                showAddTaskDialog(getString(R.string.addvideoforlikes));

            }
        };
    }

    private View.OnClickListener subscribeTaskBtnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoType = Constants.TYPE_SUBSCRIBE;
                Stash.put(Constants.CAMPAIGN_SELECTION, 0);
                showAddTaskDialog(getString(R.string.advideoforsubscribers));

            }
        };
    }

    private View.OnClickListener addBtnShowTasksClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.subscribeTaskBtn.getVisibility() == View.VISIBLE) {
                    binding.viewview.setVisibility(View.GONE);

                      binding.viewTaskBtn.setVisibility(View.GONE);
                    binding.likeTaskBtn.setVisibility(View.GONE);
                    binding.subscribeTaskBtn.setVisibility(View.GONE);

                    binding.addBtn.animate().rotation(binding.addBtn.getRotation() + 45)
                            .setDuration(500).start();

                } else {
                    binding.viewview.setVisibility(View.VISIBLE);

                      binding.viewTaskBtn.setVisibility(View.VISIBLE);
                    binding.likeTaskBtn.setVisibility(View.VISIBLE);
                    binding.subscribeTaskBtn.setVisibility(View.VISIBLE);

                    binding.addBtn.animate().rotation(binding.addBtn.getRotation() + 45)
                            .setDuration(500).start();
                }
            }
        };
    }

    private void setAddTaskButton(Dialog dialog) {
        EditText editText = dialog.findViewById(R.id.youtube_video_url_edittext);

        if (TextUtils.isEmpty(editText.getText().toString())) {
            editText.setError(getString(R.string.pleaseenterurl));
            return;
        }

        if (TextUtils.isEmpty(Helper.getVideoId(editText.getText().toString()))) {
            editText.setError(getString(R.string.wrongurl));
        } else {

            Intent intent = new Intent(getActivity(), AddTaskActivity.class);
            Stash.put(Constants.RECENT_LINK, editText.getText().toString().trim());
            intent.putExtra(Constants.RECENT_LINK, editText.getText().toString().trim());
            intent.putExtra(Constants.PARAMS, videoType);

            dialog.dismiss();
            editText.setText("");

            startActivity(intent);
        }
    }
}