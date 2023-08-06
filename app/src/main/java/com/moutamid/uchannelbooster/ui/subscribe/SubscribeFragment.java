package com.moutamid.uchannelbooster.ui.subscribe;

import static android.app.Activity.RESULT_OK;
import static com.bumptech.glide.Glide.with;
import static com.bumptech.glide.load.engine.DiskCacheStrategy.DATA;
import static com.moutamid.uchannelbooster.R.color.lighterGrey;
import static com.moutamid.uchannelbooster.utils.Constants.databaseReference;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
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
import com.denzcoskun.imageslider.interfaces.ItemChangeListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.Subscription;
import com.google.api.services.youtube.model.SubscriptionSnippet;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.uchannelbooster.R;
import com.moutamid.uchannelbooster.databinding.FragmentSubscribeBinding;
import com.moutamid.uchannelbooster.models.SubscribeTaskModel;
import com.moutamid.uchannelbooster.utils.Constants;
import com.moutamid.uchannelbooster.utils.Helper;
import com.moutamid.uchannelbooster.utils.Utils;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class SubscribeFragment extends Fragment implements EasyPermissions.PermissionCallbacks {
    private static final String TAG = "SubscribeFragment";

    int remainingDailyLimitInt = Utils.getInt(Utils.getDate(), 0);

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    private static final String BUTTON_TEXT = "Call YouTube Data API";
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {YouTubeScopes.YOUTUBE_READONLY, YouTubeScopes.YOUTUBE_FORCE_SSL};
    private static final String CLIENT_SECRETS = "client_secret.json";
    private static final Collection<String> SCOPE =
            Arrays.asList("https://www.googleapis.com/auth/youtube.force-ssl", "https://www.googleapis.com/auth/youtube");

    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    Video video;

    GoogleAccountCredential mCredential;
    String currentVideoLink;
    ProgressDialog mProgress;
    String currentVideoId = "";
//    private TextView mOutputText;
//    private Button mCallApiButton;

    //-------------------------------------------------------------------------------------------------------
    public SubscribeFragment() {
        // Required empty public constructor
    }

    private FragmentSubscribeBinding b;

    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    ArrayList<SubscribeTaskModel> subscribeTaskModelArrayList = new ArrayList<>();
    private ProgressDialog progressDialog;
    int currentCounter = 0;
    int currentPoints = 120;

    String totall = "30";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        b = FragmentSubscribeBinding.inflate(inflater, container, false);
        try {
            progressDialog = new ProgressDialog(requireContext());
            progressDialog.setCancelable(false);
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        vipStatus = Utils.getBoolean(Constants.VIP_STATUS, false);

        if (vipStatus)
            totall = "80";

        databaseReference().child(Constants.COINS_PATH).child(Constants.SUBSCRIBE_COINS)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            currentPoints = snapshot.getValue(Integer.class);
                            b.subscribeBtnSubscribeActivity.setText(getString(R.string.subscribebracket) + currentPoints + ")");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        databaseReference().child(Constants.SUBSCRIBE_TASKS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    progressDialog.dismiss();
                    Utils.toast(getString(R.string.nodataexist));
                    return;
                }

                subscribeTaskModelArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    SubscribeTaskModel model = dataSnapshot.getValue(SubscribeTaskModel.class);

                    if (snapshot.child(model.getTaskKey()).child(Constants.SUBSCRIBER_PATH).child(mAuth.getUid()).exists()) {
//                        model.setSubscribed(true);
                    } else {

                        subscribeTaskModelArrayList.add(model);
                    }


                }
                progressDialog.dismiss();
                setDataOnViews(0, false);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        b.seeNextBtnSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentCounter++;

                if (currentCounter >= subscribeTaskModelArrayList.size()) {
                    Utils.toast(getString(R.string.endoftasks));

                } else setDataOnViews(currentCounter, false);

            }
        });

        //--------------------------------------------------------------------------------------------
        b.subscribeBtnSubscribeActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Utils.toast("Coming soon!");

                // SUBSCRIBE USER TO CHANNEL
                subscribeUserToChannel();

            }
        });

        try {
            mProgress = new ProgressDialog(requireContext());
            mProgress.setMessage(getString(R.string.callyoutubedataapi));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            // Initialize credentials and service object.
            mCredential = GoogleAccountCredential.usingOAuth2(//TODO: requireContext() is added
                            requireContext().getApplicationContext(), Arrays.asList(SCOPES))
                    .setBackOff(new ExponentialBackOff());
        } catch (Exception e) {
            e.printStackTrace();
        }
        b.autoPlaySwitchSubscribe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bi) {
                if (isTimerRunning) {
                    b.autoPlaySwitchSubscribe.setChecked(false);
                    b.autoPlaySwitchSubscribe.setText(getString(R.string.autoplay));
                    isAutoPlay = false;
                    return;
                }

                if (bi) {
                    b.autoPlaySwitchSubscribe.setText(
                            getString(R.string.autoplaydailylimit)
                                    + remainingDailyLimitInt + "/)" + totall
                    );
                    isAutoPlay = true;
                    subscribeUserToChannel();
                } else {
                    b.autoPlaySwitchSubscribe.setText(getString(R.string.autoplay));
                    isAutoPlay = false;
                }
            }
        });

        databaseReference().child("Banners").child("subscribe").addValueEventListener(new ValueEventListener() {
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

                b.imageSlider.setImageList(imageList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        b.sliderLayoutSubscribe.setOnClickListener(view -> {
            if (linkList.size() > 0) {
                String url = linkList.get(currentClick);

                if (!url.startsWith("https://") && !url.startsWith("http://")) {
                    url = "http://" + url;
                }
                Intent openUrlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(openUrlIntent);
            }
        });

        b.imageSlider.setItemChangeListener(new ItemChangeListener() {
            @Override
            public void onItemChanged(int i) {
                currentClick = i;
            }
        });

        return b.getRoot();
    }

    boolean vipStatus;
    ArrayList<String> linkList = new ArrayList<>();
    int currentClick = 0;

    private void subscribeUserToChannel() {

        if (vipStatus) {
            if (remainingDailyLimitInt == 80) {
                Utils.toast(getString(R.string.yourdailylimitreached));
                return;
            }
        } else {
            if (remainingDailyLimitInt == 30) {
                Utils.toast(getString(R.string.yourdailylimitreached));
                return;
            }
        }

        if (subscribeTaskModelArrayList.size() == 0)
            return;

//        if (currentVideoLink.isEmpty()) {
//            currentVideoLink.setError("Required");
//            return;
//        } else {
        currentVideoId = Helper.getVideoId(currentVideoLink);
//        }

//        if (currentVideoId.isEmpty()) {
//            currentVideoLink.setError("Invalid Video Url");
//            return;
//        }

//        mCallApiButton.setEnabled(false);
//        mOutputText.setText("");
        getResultsFromApi();
//        mCallApiButton.setEnabled(true);


    }

    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    private void getResultsFromApi() {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (!isDeviceOnline()) {
//            mOutputText.setText("No network connection available.");
            b.outputTextView.setText("No network connection available.");
            Utils.toast("No network connection");
        } else {
            new MakeRequestTask(mCredential).execute();
        }
    }

    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        try {
            if (EasyPermissions.hasPermissions(
                    requireContext(), Manifest.permission.GET_ACCOUNTS)) {
                String accountName = requireContext().getSharedPreferences("dev.moutamid.uchannelbooster", Context.MODE_PRIVATE)
                        .getString(PREF_ACCOUNT_NAME, null);
                if (accountName != null) {
                    mCredential.setSelectedAccountName(accountName);
                    getResultsFromApi();
                } else {
                    // Start a dialog from which the user can choose an account
                    startActivityForResult(
                            mCredential.newChooseAccountIntent(),
                            REQUEST_ACCOUNT_PICKER);
                }
            } else {
                // Request the GET_ACCOUNTS permission via a user dialog
                EasyPermissions.requestPermissions(
                        this,
                        getString(R.string.thisappneedstoaccessgooglecontacts),
                        REQUEST_PERMISSION_GET_ACCOUNTS,
                        Manifest.permission.GET_ACCOUNTS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     *
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode  code indicating the result of the incoming
     *                    activity result.
     * @param data        Intent (containing result data) returned by incoming
     *                    activity result.
     */
    @Override
    public void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
//                    mOutputText.setText(
//                            "This app requires Google Play Services. Please install " +
//                                    "Google Play Services on your device and relaunch this app.");
                    Utils.toast("This app requires Google Play Services. Please install " +
                            "Google Play Services on your device and relaunch this app.");
                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        try {
                            SharedPreferences settings =
                                    requireContext().getSharedPreferences("dev.moutamid.uchannelbooster", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString(PREF_ACCOUNT_NAME, accountName);
                            editor.apply();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     *
     * @param requestCode  The request code passed in
     *                     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    /**
     * Callback for when a permission is granted using the EasyPermissions
     * library.
     *
     * @param requestCode The request code associated with the requested
     *                    permission
     * @param list        The requested permission list. Never null.
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Callback for when a permission is denied using the EasyPermissions
     * library.
     *
     * @param requestCode The request code associated with the requested
     *                    permission
     * @param list        The requested permission list. Never null.
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Checks whether the device currently has a network connection.
     *
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        try {
            ConnectivityManager connMgr =
                    (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            return (networkInfo != null && networkInfo.isConnected());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

//    public YouTube getService() throws GeneralSecurityException, IOException {
//        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
////        Credential credential = authorize(httpTransport);
//        return new YouTube.Builder(httpTransport, JSON_FACTORY, mCredential)
//                .setApplicationName(getString(R.string.app_name))
//                .build();
//    }

    /**
     * Check that Google Play services APK is installed and up to date.
     *
     * @return true if Google Play Services is available and up to
     * date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        try {
            GoogleApiAvailability apiAvailability =
                    GoogleApiAvailability.getInstance();
            final int connectionStatusCode =
                    apiAvailability.isGooglePlayServicesAvailable(requireContext());
            return connectionStatusCode == ConnectionResult.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        try {
            GoogleApiAvailability apiAvailability =
                    GoogleApiAvailability.getInstance();
            final int connectionStatusCode =
                    apiAvailability.isGooglePlayServicesAvailable(requireContext());
            if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
                showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     *
     * @param connectionStatusCode code describing the presence (or lack of)
     *                             Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                requireActivity(),
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    private void subscribeToYoutubeChannel() throws GeneralSecurityException, IOException {
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        YouTube youtubeService = new YouTube.Builder(
                transport, jsonFactory, mCredential)
                .setApplicationName("YouTube Data API Android Quickstart")
                .build();

        Subscription subscription = new Subscription();

        // Add the snippet object property to the Subscription object.
        SubscriptionSnippet snippet = new SubscriptionSnippet();
        ResourceId resourceId = new ResourceId();
        resourceId.setChannelId(video.getSnippet().getChannelId());
        resourceId.setKind("youtube#channel");
        snippet.setResourceId(resourceId);
        subscription.setSnippet(snippet);

        // Define and execute the API request
        YouTube.Subscriptions.Insert request = youtubeService.subscriptions()
                .insert("snippet", subscription);


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Subscription response = request.execute();
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            b.outputTextView.setText(response.toString());
                            uploadAddedSubscribers();
//                            mOutputText.setText(mOutputText + response.toString());
//                            Toast.makeText(requireContext(), "" + response.toString(), Toast.LENGTH_SHORT).show();
                            //Toast.makeText(MainActivity.this, "Subscribed", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void uploadAddedSubscribers() {
//        mProgress.hide();
//        progressDialog.show();

        /*databaseReference().child("tasks")
                .child(taskArrayList.get(currentPosition).getTaskKey())*/
        //.child("currentViewsQuantity")
        databaseReference()
                .child(Constants.SUBSCRIBE_TASKS)
                .child(subscribeTaskModelArrayList.get(currentCounter).getTaskKey())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        SubscribeTaskModel taskk = snapshot.getValue(SubscribeTaskModel.class);

                        String currentViews = String.valueOf(taskk.getCurrentSubscribesQuantity());

                        if (currentViews.equals(taskk.getTotalSubscribesQuantity())) {

                            databaseReference()
                                    .child(Constants.SUBSCRIBE_TASKS)
                                    .child(subscribeTaskModelArrayList.get(currentCounter).getTaskKey())
                                    .child("completedDate")
                                    .setValue(Utils.getDate())
//                                    .setValue(new Utils().getDate())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

//                                            subscribeTaskModelArrayList.remove(currentCounter);

                                            uploadAddedCoins();
                                            // UPLOAD COINS AND THEN RESTART VIDEO PLAYER

                                        }
                                    });

                        } else {

                            databaseReference()
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
                        Log.d(TAG, "onCancelled: " + error.getMessage());
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        mProgress.hide();
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

                                        databaseReference()
                                                .child(Constants.SUBSCRIBE_TASKS)
                                                .child(subscribeTaskModelArrayList.get(currentCounter).getTaskKey())
                                                .child(Constants.SUBSCRIBER_PATH)
                                                .child(mAuth.getUid())
                                                .setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        mProgress.hide();
                                                        Utils.toast(getString(R.string.subscribedmark));


                                                        // INCREMENT DAILY LIMIT
                                                        remainingDailyLimitInt++;
                                                        Utils.store(Utils.getDate(),
                                                                remainingDailyLimitInt);
                                                        b.autoPlaySwitchSubscribe.setText(
                                                                getString(R.string.autoplaydailylimit)
                                                                        + remainingDailyLimitInt + "/)" + totall
                                                        );

                                                        currentCounter++;

                                                        if (currentCounter >= subscribeTaskModelArrayList.size()) {
                                                            Utils.toast(getString(R.string.endoftasks));
                                                            b.videoImageSubscribe.setBackgroundResource(0);
                                                            b.videoIdSubscribe.setText("Empty");
                                                        } else setDataOnViews(currentCounter, true);

                                                    }
                                                });

                                    }
                                });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d(TAG, "onCancelled: " + error.getMessage());
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        mProgress.hide();
                    }
                });
    }

    /*private void uploadSubscribeDoneToDB() {



    }*/

    /*private void likeVideo() throws GeneralSecurityException, IOException {
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        YouTube youtubeService = new com.google.api.services.youtube.YouTube.Builder(
                transport, jsonFactory, mCredential)
                .setApplicationName("YouTube Data API Android Quickstart")
                .build();

        YouTube.Videos.Rate request = youtubeService.videos()
                .rate(videoId, "like");


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    request.execute();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Toast.makeText(MainActivity.this, "Liked", Toast.LENGTH_SHORT).show();

                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }*/


    /**
     * An asynchronous task that handles the YouTube Data API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
        private YouTube mService = null;
        private Exception mLastError = null;

        MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new YouTube.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("YouTube Data API Android Quickstart")
                    .build();
        }

        /**
         * Background task to call YouTube Data API.
         *
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        /**
         * Fetch information about the "GoogleDevelopers" YouTube channel.
         *
         * @return List of Strings containing information about the channel.
         * @throws IOException
         */
        private List<String> getDataFromApi() throws IOException {
            // Get a list of up to 10 files.
            List<String> channelInfo = new ArrayList<String>();

            YouTube.Videos.List request = mService.videos()
                    .list("snippet,contentDetails,statistics,status");
            VideoListResponse response = request.setId(currentVideoId).execute();

            Log.d("clima", response.getItems().get(0).getSnippet().getChannelId());


            video = response.getItems().get(0);

            channelInfo.add("This video's title is " + video.getSnippet().getTitle() + ". " +
                    "Channel title is '" + video.getSnippet().getChannelTitle() + ", " +
                    "and it has " + video.getStatistics().getLikeCount() + " likes."

            );

            status = video.getSnippet().getTitle();

            return channelInfo;
        }

        String status = "nothing";

        @Override
        protected void onPreExecute() {
//            mOutputText.setText("");
            mProgress.show();
        }

        @Override
        protected void onPostExecute(List<String> output) {

            Utils.toast(status);

            if (output == null || output.size() == 0) {
//                mOutputText.setText("No results returned.");
                b.outputTextView.setText("No results returned.");
                Utils.toast("No results returned.");
            } else {
                output.add(0, "Data retrieved using the YouTube Data API:");
//                mOutputText.setText(TextUtils.join("\n", output));
                String text = TextUtils.join("\n", output);
                b.outputTextView.setText(text);
//                Utils.toast(text);
            }

            try {
                subscribeToYoutubeChannel();
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onCancelled() {
            mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            SubscribeFragment.REQUEST_AUTHORIZATION);//TODO: MainActivity is removed
                } else {
//                    mOutputText.setText("The following error occurred:\n"
//                            + mLastError.getMessage());
                    b.outputTextView.setText("The following error occurred:\n"
                            + mLastError.getMessage());
                    Utils.toast("The following error occurred:\n"
                            + mLastError.getMessage());
                    Log.d("TAGSUB", "onCancelled: " + "The following error occurred:\n"
                            + mLastError.getMessage());
                }
            } else {
//                mOutputText.setText("Request cancelled.");
                b.outputTextView.setText("Request cancelled.");
                Utils.toast("Request cancelled.");
            }
        }
    }

    boolean isAutoPlay = false;

    //-------------------------------------------------------------------------------------------------
    boolean isTimerRunning = false;

    int isError = 0;
    Handler handler = new Handler();

    private void setDataOnViews(int counter, boolean isTaskCompleted) {

        if (subscribeTaskModelArrayList.size() == 0)
            return;

        // IF FIRST TIME
        if (counter == 0 || !isTaskCompleted) {
            progressDialog.show();

            b.videoImageSubscribe.setScaleType(ImageView.ScaleType.CENTER_CROP);
            with(requireActivity())
                    .asBitmap()
                    .load(subscribeTaskModelArrayList.get(counter).getThumbnailUrl())
                    .apply(new RequestOptions()
                            .placeholder(lighterGrey)
                            .error(lighterGrey)
                    )
                    .diskCacheStrategy(DATA)
                    .addListener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {

                                    isError++;

                                    databaseReference().child(Constants.SUBSCRIBE_TASKS)
                                            .child(subscribeTaskModelArrayList.get(counter)
                                                    .getTaskKey()).removeValue();

                                    b.videoImageSubscribe.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                    b.videoImageSubscribe.setImageResource(R.drawable.ic_baseline_access_time_filled_24);

                                    currentCounter++;

                                    if (currentCounter >= subscribeTaskModelArrayList.size()) {
                                        Utils.toast(getString(R.string.endoftasks));
                                        b.videoImageSubscribe.setBackgroundResource(0);
                                        b.videoIdSubscribe.setText("Empty");
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

            b.videoIdSubscribe.setText(
                    getString(R.string.videoidmark) + Helper.getVideoId(subscribeTaskModelArrayList.get(counter).getVideoUrl())
            );

            currentVideoLink = subscribeTaskModelArrayList.get(counter).getVideoUrl();

            progressDialog.dismiss();

            if (isError > 0) {
                return;
            }

            if (isAutoPlay)
                subscribeUserToChannel();
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
                b.videoIdSubscribe.setText("" + millisUntilFinished / 1000);
//                b.autoPlaySwitchSubscribe.setEnabled(false);
                isTimerRunning = true;
                b.subscribeBtnSubscribeActivity.setEnabled(false);
                b.seeNextBtnSubscribe.setEnabled(false);
            }

            public void onFinish() {
                isTimerRunning = false;
//                b.autoPlaySwitchSubscribe.setEnabled(true);
                b.subscribeBtnSubscribeActivity.setEnabled(true);
                b.seeNextBtnSubscribe.setEnabled(true);

                b.videoImageSubscribe.setRotation(0);

                progressDialog.show();

                b.videoImageSubscribe.setScaleType(ImageView.ScaleType.CENTER_CROP);
                try {
                    with(requireContext())
                            .asBitmap()
                            .load(subscribeTaskModelArrayList.get(counter).getThumbnailUrl())
                            .apply(new RequestOptions()
                                    .placeholder(lighterGrey)
                                    .error(lighterGrey)
                            )
                            .addListener(new RequestListener<Bitmap>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {


                                            isError++;

                                            databaseReference().child(Constants.SUBSCRIBE_TASKS)
                                                    .child(subscribeTaskModelArrayList.get(counter)
                                                            .getTaskKey()).removeValue();

                                            b.videoImageSubscribe.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                            b.videoImageSubscribe.setImageResource(R.drawable.ic_baseline_access_time_filled_24);

                                            currentCounter++;

                                            if (currentCounter >= subscribeTaskModelArrayList.size()) {
                                                Utils.toast(getString(R.string.endoftasks));
                                                b.videoImageSubscribe.setBackgroundResource(0);
                                                b.videoIdSubscribe.setText("Empty");
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
                } catch (Exception e) {
                    e.printStackTrace();
                }

                b.videoIdSubscribe.setText(
                        getString(R.string.videoidmark) + Helper.getVideoId(subscribeTaskModelArrayList.get(counter).getVideoUrl())
                );

                currentVideoLink = subscribeTaskModelArrayList.get(counter).getVideoUrl();

                progressDialog.dismiss();

                if (isError > 0) {
                    return;
                }

                if (isAutoPlay)
                    subscribeUserToChannel();
            }
        }.start();


/*
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        })*/
    }
}