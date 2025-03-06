package com.moutamid.uchannelbooster.utils;

import static android.content.Context.WINDOW_SERVICE;

import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Constants {

    public static final String PARAMS = "params";
    public static final String TYPE_VIEW = "type_view";
    public static final String TYPE_LIKE = "type_like";
    public static final String TYPE_SUBSCRIBE = "type_subscribe";
    public static final String LIKE_TASKS = "like_tasks";
    public static final String SUBSCRIBE_TASKS = "subscribe_tasks";
    public static final String SUBSCRIBER_PATH = "subscribers";
    public static final String LIKERS_PATH = "subscribers";
    public static final String DAILY_LIMIT = "daily_limit";
    public static final String VIEWER_PATH = "viewers";

    public static final String LICENSE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgRtbp8cxRVrS6mHkTurccNhFptQhzbgywVeTd4m6lXiRYFFXACgI1Q7iDEtlHGevIIOSCoJ7DuXfAbNCfrDooFiwCqwFmDV0NiDr/KbLQ4BwWyHtN0ufIyA3TCpL8CmnOgPV02DJuIPVP/Tptk81nRZs1XNWoOhQz/h8Bn/nZRiYTduwY77fCR8SfyJM4znXAZdEidXRhQjhB/EqcIjf2CStL37umK7MiNILRKg7qDmCET30h6rDpNpzIAcSli81nWUuntkQsQWoKYj5rzyZDuyW0tvcijUb7UAevtt8GsyB5B2noLa4oSrkoUqeNPmsy5bxZQB6jnzq/Yj8h8zpTwIDAQAB";
    public static final String USER_ID = "user_id";
    public static final String PRODUCT_TYPE = "product_type";
    public static final String PATH_PRODUCTS = "purchased_products";
    public static final String COINS = "coins";
    public static final String USER_INFO = "userinfo";
    public static final String PURCHASE_DATE = "purchase_date";
    public static final String VIP_STATUS = "vip_status";

    public static final String ONE_DOLLAR_PRODUCT = "one_dollar_com.moutamid.uchannelboost";
    public static final String TWO_DOLLAR_PRODUCT = "two_dollar_com.moutamid.uchannelboost";
    public static final String FIVE_DOLLAR_PRODUCT = "five_dollar_com.moutamid.uchannelboost";
    public static final String TEN_DOLLAR_PRODUCT = "ten_dollar_com.moutamid.uchannelboost";
    public static final String TWENTY_FIVE_DOLLAR_PRODUCT = "twenty_five_dollar_com.moutamid.uchannelboost";

    public static final String ONE_WEEK_SUBSCRIPTION = "one_week_com.moutamid.uchannelboost";
    public static final String ONE_MONTH_SUBSCRIPTION = "one_month_com.moutamid.uchannelboost";
    public static final String THREE_MONTHS_SUBSCRIPTION = "three_months_com.moutamid.uchannelboost";

    public static final int FIFTEEN_K = 15000;
    public static final int FORTY_FIVE_K = 45000;
    public static final int ONE_HUNDRED_N_TWENTY_FIVE_K = 125000;
    public static final int THREE_HUNDRED_K = 300000;
    public static final int SEVEN_HUNDRED_K = 700000;
    public static final String PATH_SUBSCRIPTIONS = "purchased_subscriptions";
    public static final String COINS_PATH = "coins_path";
    public static final String VIEW_COINS = "view_coins";
    public static final String LIKE_COINS = "like_coins";
    public static final String SUBSCRIBE_COINS = "subscribe_coins";
    public static final String ADD_TASK_VARIABLES = "add_tasks_variable";
    public static final String TIME_ARRAY = "time_array";
    public static final String QUANTITY_ARRAY = "quantity_array";
    public static final String CUT_OFF_AMOUNT_OF_TASKS = "cut_of_amount_of_tasks";
    public static final String CUT_OFF_AMOUNT_OF_VIEWS = "cut_of_amount_of_views";
    public static final String CUT_OFF_AMOUNT_OF_LIKE = "cut_of_amount_of_like";
    public static final String CUT_OFF_AMOUNT_OF_SUBSCRIBE = "cut_of_amount_of_subscribe";

    public static final String CURRENT_LANGUAGE_CODE = "current_language_code";
    public static final String LANGUAGE_CODE_ENGLISH = "en";
    public static final String LANGUAGE_CODE_ARABIC = "ar";
    public static final String LANGUAGE_CODE_SPANISH = "es";
    public static final String LANGUAGE_CODE_FRENCH = "fr";
    public static final String LANGUAGE_CODE_HINDI = "hi";
    public static final String LANGUAGE_CODE_INDONESIAN = "in";
    public static final String LANGUAGE_CODE_JAPANESE = "ja";
    public static final String LANGUAGE_CODE_KOREAN = "ko";
    public static final String LANGUAGE_CODE_VIETNAMESE = "vi";
    public static final String LANGUAGE_CODE_URDU = "ur";

    public static final int RC_SIGN_IN = 9001;
    public static final String USER = "userinfo";
    public static final String TIME = "TIME";
    public static final String MODEL = "MODEL";
    public static final String vipStatus = "vipStatus";
    public static final String coins = "coins";
    public static final String COIN = "COIN";
    public static final String CHECK = "CHECK";
      public static final String CURRENT_COINS = "CURRENT_COINS";
    public static final String RECENT_IMAGE = "RECENT_IMAGE";
    public static final String RECENT_LINK = "RECENT_LINK";
    public static final String CAMPAIGN_SELECTION = "CAMPAIGN_SELECTION";
    public static final String VIEW_TASKS = "view_tasks";
    public static final String isAutoPlayEnabled = "isAutoPlayEnabled";

    public static final String[] subsQuantityArray = new String[]{
            "10", "20", "30", "40", "50", "60", "70", "80", "90", "100",
            "200", "300", "400", "500", "600", "700", "800", "900", "1000"
    };

    public static final String[] subTimeArray = new String[]{
            "45", "60", "90", "120", "150", "180", "210", "240", "270", "300",
            "330", "360", "390", "420", "450", "480", "510", "540", "570"
    };

    public static final String[] viewQuantityArray = new String[]{
            "10", "50", "100", "150", "200", "250", "300", "350", "400", "450",
            "500", "550", "600", "650", "700", "750", "800", "850", "900", "950", "1000"
    };

    public static final String[] viewTimeArray = new String[] {
            "45", "60", "90", "120", "150", "180", "210", "240", "270", "300",
            "330", "360", "390", "420", "450", "480", "510", "540", "570", "600"
    };
    public static final String[] likeTimeArray = new String[] {
            "30", "60", "90", "120", "150", "180", "210", "240", "270", "300",
            "330", "360", "390", "420", "450", "480", "510", "540", "570"
    };

    public static FirebaseAuth auth() {
        return FirebaseAuth.getInstance();
    }

    public static DatabaseReference databaseReference() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("UChannelBooster");
        db.keepSynced(true);
        return db;
    }

    public static String getVideoId(@NonNull String videoUrl) {
        String videoId = "";
        String regex = "http(?:s)?:\\/\\/(?:m.)?(?:www\\.)?youtu(?:\\.be\\/|be\\.com\\/(?:watch\\?(?:feature=youtu.be\\&)?v=|v\\/|embed\\/|user\\/(?:[\\w#]+\\/)+))([^&#?\\n]+)";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(videoUrl);
        if (matcher.find()) {
            videoId = matcher.group(1);
        }
        return videoId;
    }

    public static class HttpHandler {
        private String TAG = "HttpHandler";

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

    public static String getDate() {

        try {
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm aa dd/MM/yyyy");
            return sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Error";

    }


    public static void adjustFontScale(Context context) {
        Configuration configuration = context.getResources().getConfiguration();
        if (configuration.fontScale > 1.00) {
            Log.d("TAG1", "fontScale=" + configuration.fontScale);
            configuration.fontScale = 1.00f;
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            WindowManager wm = (WindowManager) context.getSystemService(WINDOW_SERVICE);
            wm.getDefaultDisplay().getMetrics(metrics);
            metrics.scaledDensity = configuration.fontScale * metrics.density;
            context.getResources().updateConfiguration(configuration, metrics);
        }
    }
}