package com.moutamid.uchannelbooster.utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    public static final String ONE_DOLLAR_PRODUCT = "one_dollar_com.moutamid.uchannelbooster";
    public static final String TWO_DOLLAR_PRODUCT = "two_dollar_com.moutamid.uchannelbooster";
    public static final String FIVE_DOLLAR_PRODUCT = "five_dollar_com.moutamid.uchannelbooster";
    public static final String TEN_DOLLAR_PRODUCT = "ten_dollar_com.moutamid.uchannelbooster";
    public static final String TWENTY_FIVE_DOLLAR_PRODUCT = "twenty_five_dollar_com.moutamid.uchannelbooster";

    public static final String ONE_WEEK_SUBSCRIPTION = "one_week_com.moutamid.uchannelbooster";
    public static final String ONE_MONTH_SUBSCRIPTION = "one_month_com.moutamid.uchannelbooster";
    public static final String THREE_MONTHS_SUBSCRIPTION = "three_months_com.moutamid.uchannelbooster";

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

    public static DatabaseReference databaseReference() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("UChannelBooster");
        db.keepSynced(true);
        return db;
    }
}
