package com.moutamid.uchannelboost.ui.campaign;

import static com.moutamid.uchannelboost.utils.Constants.databaseReference;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.uchannelboost.R;
import com.moutamid.uchannelboost.activities.AddTaskActivity;
import com.moutamid.uchannelboost.models.LikeTaskModel;
import com.moutamid.uchannelboost.models.SubscribeTaskModel;
import com.moutamid.uchannelboost.models.TasksTypeModel;
import com.moutamid.uchannelboost.models.ViewTaskModel;
import com.moutamid.uchannelboost.utils.Constants;
import com.moutamid.uchannelboost.utils.Helper;
import com.moutamid.uchannelboost.utils.Utils;

import java.util.ArrayList;

public class CampaignFragment extends Fragment {
    private static final String TAG = "CampaignFragment";
//    private Context context = CampaignFragment.this;

    //    private HomeViewModel homeViewModel;
    private View root;

    private final ArrayList<Task> tasksArrayList = new ArrayList<>();

    ArrayList<TasksTypeModel> allTasksArrayList = new ArrayList<>();

    private RecyclerView conversationRecyclerView;
    private RecyclerViewAdapterMessages adapter;

    private FirebaseAuth mAuth;

    private ProgressDialog progressDialog;

    ImageView fabViewBtn, fabLikeBtn, fabSubscribeBtn, addFabBtn;
//    FloatingActionButton fabViewBtn, fabLikeBtn, fabSubscribeBtn;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_campaign, container, false);
        Log.d(TAG, "onCreateView: ");
//        LikeTaskModel model = new LikeTaskModel();
//        model.setCurrentLikesQuantity(76);
//
//        allTasksArrayList.add(new TasksTypeModel(model, ""));

        mAuth = FirebaseAuth.getInstance();

        fabViewBtn = root.findViewById(R.id.viewTaskBtn);
        fabLikeBtn = root.findViewById(R.id.likeTaskBtn);
        fabSubscribeBtn = root.findViewById(R.id.subscribeTaskBtn);
        addFabBtn = root.findViewById(R.id.addBtnShowTasks);

        fabViewBtn.setOnClickListener(fabViewBtnClickListener());
        fabLikeBtn.setOnClickListener(fabLikeBtnClickListener());
        fabSubscribeBtn.setOnClickListener(fabSubscribeBtnClickListener());

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();

//        getViewTasksListFromDB();

        /*databaseReference().child("tasks").orderByChild("posterUid")
                .equalTo(mAuth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (!snapshot.exists()) {
                            Utils.store("canAddNewTask", true);

                            progressDialog.dismiss();
                            return;
                        }

                        tasksArrayList.clear();

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                            CampaignFragment.Task task = dataSnapshot.getValue(CampaignFragment.Task.class);

                            tasksArrayList.add(task);

                        }

                        initRecyclerView();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        Utils.store( "canAddNewTask", true);

                    }
                });*/


//        setAddTaskButton();

        addFabBtn.setOnClickListener(addBtnShowTasksClickListener());

        return root;
    }

    private void getViewTasksListFromDB() {
        databaseReference().child("tasks").orderByChild("posterUid")
                .equalTo(mAuth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d(TAG, "getViewTasksListFromDB onDataChange: ");
                        allTasksArrayList.clear();
                        if (!snapshot.exists()) {
                            Log.d(TAG, "onDataChange: if (!snapshot.exists()) {");
                            getLikeTasksListFromDB();
//                            progressDialog.dismiss();
                            return;
                        }
                        Log.d(TAG, "onDataChange: running");

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Log.d(TAG, "onDataChange: looping");
                            ViewTaskModel task = dataSnapshot.getValue(ViewTaskModel.class);

                            TasksTypeModel tasksTypeModel = new TasksTypeModel();
                            tasksTypeModel.setViewTaskModel(task);
                            tasksTypeModel.setType(Constants.TYPE_VIEW);

                            allTasksArrayList.add(tasksTypeModel);
//                            allTasksArrayList.add(new TasksTypeModel(task, Constants.TYPE_VIEW));

                        }
                        Log.d(TAG, "onDataChange: loop ended");
//                        initRecyclerView();
                        getLikeTasksListFromDB();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d(TAG, "onCancelled: error: " + error.toException().getMessage());
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getLikeTasksListFromDB() {
        databaseReference().child(Constants.LIKE_TASKS).orderByChild("posterUid")
                .equalTo(mAuth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d(TAG, "getLikeTasksListFromDB onDataChange: ");
//                        allTasksArrayList.clear();
                        if (!snapshot.exists()) {
                            Log.d(TAG, "onDataChange: if (!snapshot.exists()) {");
                            getSubscribeTasksListFromDB();
//                            progressDialog.dismiss();
                            return;
                        }

                        Log.d(TAG, "onDataChange: started");
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Log.d(TAG, "onDataChange: looping");
                            LikeTaskModel task = dataSnapshot.getValue(LikeTaskModel.class);

                            TasksTypeModel tasksTypeModel = new TasksTypeModel();
                            tasksTypeModel.setLikeTaskModel(task);
                            tasksTypeModel.setType(Constants.TYPE_LIKE);

                            allTasksArrayList.add(tasksTypeModel);

//                            allTasksArrayList.add(new TasksTypeModel(task, Constants.TYPE_LIKE));

                        }
                        Log.d(TAG, "onDataChange: loop ended");
//                        initRecyclerView();
                        getSubscribeTasksListFromDB();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d(TAG, "onCancelled: " + error.toException().getMessage());
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getSubscribeTasksListFromDB() {
        databaseReference().child(Constants.SUBSCRIBE_TASKS).orderByChild("posterUid")
                .equalTo(mAuth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d(TAG, "onDataChange: getSubscribeTasksListFromDB");
//                        allTasksArrayList.clear();
                        if (!snapshot.exists()) {
                            Log.d(TAG, "onDataChange: if (!snapshot.exists()) {");
                            initRecyclerView();
//                            getSubscribeTasksListFromDB();
                            progressDialog.dismiss();
                            return;
                        }


                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Log.d(TAG, "onDataChange: looping");

                            SubscribeTaskModel task = dataSnapshot.getValue(SubscribeTaskModel.class);

                            TasksTypeModel tasksTypeModel = new TasksTypeModel();
                            tasksTypeModel.setSubscribeTaskModel(task);
                            tasksTypeModel.setType(Constants.TYPE_SUBSCRIBE);

                            allTasksArrayList.add(tasksTypeModel);

//                            allTasksArrayList.add(new TasksTypeModel(task, Constants.TYPE_SUBSCRIBE));

                        }
                        Log.d(TAG, "onDataChange: loop ended");
                        Log.d(TAG, "onDataChange: RecyclerView Initialized");
                        Log.d(TAG, "onDataChange: total list size: " + allTasksArrayList.size());
                        initRecyclerView();
//                        getSubscribeTasksListFromDB();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d(TAG, "onCancelled: " + error.toException().getMessage());
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
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

    private View.OnClickListener fabViewBtnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoType = Constants.TYPE_VIEW;
                showAddTaskDialog(getString(R.string.addvideoforviews));

            }
        };
    }

    private View.OnClickListener fabLikeBtnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoType = Constants.TYPE_LIKE;
                showAddTaskDialog(getString(R.string.addvideoforlikes));

            }
        };
    }

    private View.OnClickListener fabSubscribeBtnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoType = Constants.TYPE_SUBSCRIBE;
                showAddTaskDialog(getString(R.string.advideoforsubscribers));

            }
        };
    }

    private View.OnClickListener addBtnShowTasksClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fabSubscribeBtn.getVisibility() == View.VISIBLE) {
                    root.findViewById(R.id.viewview).setVisibility(View.GONE);

                    fabViewBtn.setVisibility(View.GONE);
                    fabLikeBtn.setVisibility(View.GONE);
                    fabSubscribeBtn.setVisibility(View.GONE);

                    addFabBtn.animate().rotation(addFabBtn.getRotation() + 45)
                            .setDuration(500).start();

                } else {
                    root.findViewById(R.id.viewview).setVisibility(View.VISIBLE);

                    fabViewBtn.setVisibility(View.VISIBLE);
                    fabLikeBtn.setVisibility(View.VISIBLE);
                    fabSubscribeBtn.setVisibility(View.VISIBLE);

                    addFabBtn.animate().rotation(addFabBtn.getRotation() + 45)
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
            intent.putExtra("url", editText.getText().toString().trim());
            intent.putExtra(Constants.PARAMS, videoType);

            dialog.dismiss();
            editText.setText("");

            startActivity(intent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getViewTasksListFromDB();
    }

    private void initRecyclerView() {

        conversationRecyclerView = root.findViewById(R.id.home_recyclerview);
        adapter = new RecyclerViewAdapterMessages();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

        conversationRecyclerView.setLayoutManager(linearLayoutManager);
        conversationRecyclerView.setHasFixedSize(true);
        conversationRecyclerView.setNestedScrollingEnabled(false);

        conversationRecyclerView.setAdapter(adapter);

        /*TODO: COMMENTED
        if (adapter.getItemCount() == 3) {

            Utils.store( "canAddNewTask", false);


            //        noChatsLayout.setVisibility(View.GONE);
            //        chatsRecyclerView.setVisibility(View.VISIBLE);

        } else {
            Utils.store( "canAddNewTask", true);

        }*/

        if (adapter.getItemCount() != 0) {
            root.findViewById(R.id.noChatsLayout).setVisibility(View.GONE);
            conversationRecyclerView.setVisibility(View.VISIBLE);
        }

        progressDialog.dismiss();

    }


    private class RecyclerViewAdapterMessages extends RecyclerView.Adapter
            <RecyclerViewAdapterMessages.ViewHolderRightMessage> {

        @NonNull
        @Override
        public ViewHolderRightMessage onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_task_home, parent, false);
            return new ViewHolderRightMessage(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolderRightMessage holder, int position1) {
            int position = holder.getAdapterPosition();
            Log.d(TAG, "onBindViewHolder: position: " + position);
            Log.d(TAG, "onBindViewHolder: totalSize: " + allTasksArrayList.size());
            if (Constants.TYPE_VIEW.equals(allTasksArrayList.get(position).getType())) {
                Log.d(TAG, "onBindViewHolder: case Constants.TYPE_VIEW:");
                Log.d(TAG, "onBindViewHolder: " +
                        "CURRENT VIEWS: " + allTasksArrayList.get(position).getViewTaskModel().getCurrentViewsQuantity()
                        + "\n"
                        + "TOTAL VIEWS: " + allTasksArrayList.get(position).getViewTaskModel().getTotalViewsQuantity()
                );
                // VIEW TYPE VIDEO ITEM
                dealWithViewItemLayouts(holder, position);
            }
            if (Constants.TYPE_LIKE.equals(allTasksArrayList.get(position).getType())) {
                Log.d(TAG, "onBindViewHolder: case Constants.TYPE_LIKE:");
                Log.d(TAG, "onBindViewHolder: " +
                        "CURRENT LIKES: " + allTasksArrayList.get(position).getLikeTaskModel().getCurrentLikesQuantity()
                        + "\n"
                        + "TOTAL LIKES: " + allTasksArrayList.get(position).getLikeTaskModel().getTotalLikesQuantity()
                );
                // LIKE TYPE VIDEO ITEM
                dealWithLikeItemLayouts(holder, position);
            }
            if (Constants.TYPE_SUBSCRIBE.equals(allTasksArrayList.get(position).getType())) {
                Log.d(TAG, "onBindViewHolder: case Constants.TYPE_SUBSCRIBE:");
                Log.d(TAG, "onBindViewHolder: " +
                        "CURRENT SUBS: " + allTasksArrayList.get(position).getSubscribeTaskModel().getCurrentSubscribesQuantity()
                        + "\n"
                        + "TOTAL SUBS: " + allTasksArrayList.get(position).getSubscribeTaskModel().getTotalSubscribesQuantity()
                );
                // SUBSCRIBE TYPE VIDEO ITEM
                dealWithSubscribeItemLayouts(holder, position);
            }


        }

        private void dealWithSubscribeItemLayouts(ViewHolderRightMessage holder, int position) {
            SubscribeTaskModel task = allTasksArrayList.get(position).getSubscribeTaskModel();
//            CampaignFragment.Task task = tasksArrayList.get(position);

            holder.totalViews.setText(
                    "" + task.getCurrentSubscribesQuantity()
                            + "/" +
                            task.getTotalSubscribesQuantity() + getString(R.string.subscribes));

            holder.watchSecondsDefined.setText(getString(R.string.totasubscribes) + task.getTotalSubscribesQuantity());

            if (!task.getCompletedDate().equals("error")) {
                holder.completedTime.setText(getString(R.string.completesmall) + task.getCompletedDate());
            }

            if (task.getTotalSubscribesQuantity().equals(String.valueOf(
                    task.getCurrentSubscribesQuantity()
            ))) {
                holder.doneTickImageview.setVisibility(View.VISIBLE);
            }

            Glide.with(requireContext())
                    .load(task.getThumbnailUrl())
                    .apply(new RequestOptions()
                            .placeholder(R.color.grey)
                            .error(R.color.grey)
                    )
                    .into(holder.thumbnailImage);

            holder.parentLayout.setOnLongClickListener(subscribeParentLayoutLngCLick(task));

        }

        private void dealWithLikeItemLayouts(ViewHolderRightMessage holder, int position) {
            LikeTaskModel task = allTasksArrayList.get(position).getLikeTaskModel();
//            CampaignFragment.Task task = tasksArrayList.get(position);

            holder.totalViews.setText(
                    "" + task.getCurrentLikesQuantity()
                            + "/" +
                            task.getTotalLikesQuantity() + getString(R.string.likes));

            holder.watchSecondsDefined.setText(getString(R.string.totallikes) + task.getTotalLikesQuantity());

            if (!task.getCompletedDate().equals("error")) {
                holder.completedTime.setText(getString(R.string.completesmall) + task.getCompletedDate());
            }

            if (task.getTotalLikesQuantity().equals(String.valueOf(
                    task.getCurrentLikesQuantity()
            ))) {
                holder.doneTickImageview.setVisibility(View.VISIBLE);
            }

            Glide.with(requireContext())
                    .load(task.getThumbnailUrl())
                    .apply(new RequestOptions()
                            .placeholder(R.color.grey)
                            .error(R.color.grey)
                    )
                    .into(holder.thumbnailImage);

            holder.parentLayout.setOnLongClickListener(likeParentLayoutLngCLick(task));

        }

        private void dealWithViewItemLayouts(ViewHolderRightMessage holder, int position) {
            ViewTaskModel task = allTasksArrayList.get(position).getViewTaskModel();
//            CampaignFragment.Task task = tasksArrayList.get(position);

            holder.totalViews.setText(
                    "" + task.getCurrentViewsQuantity()
                            + "/" +
                            task.getTotalViewsQuantity() + getString(R.string.view));

            holder.watchSecondsDefined.setText(getString(R.string.watchseconds) + task.getTotalViewTimeQuantity());

            if (!task.getCompletedDate().equals("error")) {
                holder.completedTime.setText(getString(R.string.completesmall) + task.getCompletedDate());
            }

            if (task.getTotalViewsQuantity().equals(String.valueOf(
                    task.getCurrentViewsQuantity()
            ))) {
                holder.doneTickImageview.setVisibility(View.VISIBLE);
            }

            Glide.with(requireContext())
                    .load(task.getThumbnailUrl())
                    .apply(new RequestOptions()
                            .placeholder(R.color.grey)
                            .error(R.color.grey)
                    )
                    .into(holder.thumbnailImage);

            holder.parentLayout.setOnLongClickListener(viewParentLayoutLngCLick(task));

        }


        private View.OnLongClickListener viewParentLayoutLngCLick(ViewTaskModel task) {
            return new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    Utils.showDialog(requireContext(),
                            getString(R.string.are_you_sure),
                            getString(R.string.doyoureallywantotodelete),
                            getString(R.string.yes),
                            getString(R.string.no),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    databaseReference().child("tasks")
                                            .child(task.getTaskKey())
                                            .removeValue();

                                    dialogInterface.dismiss();
                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }, true);

                    return false;
                }
            };
        }

        private View.OnLongClickListener likeParentLayoutLngCLick(LikeTaskModel task) {
            return new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    Utils.showDialog(requireContext(),
                            getString(R.string.are_you_sure),
                            getString(R.string.doyoureallywantotodelete),
                            getString(R.string.yes),
                            getString(R.string.no),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    databaseReference().child(Constants.LIKE_TASKS)
                                            .child(task.getTaskKey())
                                            .removeValue();

                                    dialogInterface.dismiss();
                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }, true);

                    return false;
                }
            };
        }

        private View.OnLongClickListener subscribeParentLayoutLngCLick(SubscribeTaskModel task) {
            return new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    Utils.showDialog(requireContext(),
                            getString(R.string.are_you_sure),
                            getString(R.string.doyoureallywantotodelete),
                            getString(R.string.yes),
                            getString(R.string.no),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    databaseReference().child(Constants.SUBSCRIBE_TASKS)
                                            .child(task.getTaskKey())
                                            .removeValue();

                                    dialogInterface.dismiss();
                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }, true);

                    return false;
                }
            };
        }

        @Override
        public int getItemCount() {
            if (allTasksArrayList == null)
                return 0;
            return allTasksArrayList.size();
            /*if (tasksArrayList == null)
                return 0;
            return tasksArrayList.size();*/
        }

        public class ViewHolderRightMessage extends RecyclerView.ViewHolder {
            ImageView thumbnailImage, doneTickImageview;
            TextView completedTime, watchSecondsDefined, totalViews;
            RelativeLayout parentLayout;

            public ViewHolderRightMessage(@NonNull View v) {
                super(v);
                completedTime = v.findViewById(R.id.done_time_textview_youtube);
                thumbnailImage = v.findViewById(R.id.youtube_thumbnail_imageview);
                watchSecondsDefined = v.findViewById(R.id.watch_seconds_defined_textview_youtube);
                doneTickImageview = v.findViewById(R.id.done_tick_imageview_youtube);
                totalViews = v.findViewById(R.id.total_youtube_views_textview);
                parentLayout = v.findViewById(R.id.parent_layout_task_home);

            }
        }

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

}