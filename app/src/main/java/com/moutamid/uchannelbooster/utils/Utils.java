package com.moutamid.uchannelbooster.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class Utils {

    private static Utils utils;
    private static Context instance;
    private SharedPreferences sp;

    public Utils() {
    }

    public static void changeLanguage(String languageToLoad) {
        Locale newLocale = new Locale(languageToLoad);
        Resources res = instance.getResources();
        Configuration configuration = res.getConfiguration();

        if (Build.VERSION.SDK_INT >= 24) {
//        if (BuildUtils.isAtLeast24Api()) {
            configuration.setLocale(newLocale);

            LocaleList localeList = new LocaleList(newLocale);
            LocaleList.setDefault(localeList);
            configuration.setLocales(localeList);

            instance = instance.createConfigurationContext(configuration);

        } else if (Build.VERSION.SDK_INT >= 17) {
//        } else if (BuildUtils.isAtLeast17Api()) {
            configuration.setLocale(newLocale);
            instance = instance.createConfigurationContext(configuration);

        } else {
            configuration.locale = newLocale;
            res.updateConfiguration(configuration, res.getDisplayMetrics());
        }
    }

    public static void init(Context context) {
        utils = new Utils();
        instance = context;
        if (utils.sp == null) {
            utils.sp = context.getSharedPreferences("com.moutamid.uchannelboost", Context.MODE_PRIVATE);
//            utils.sp = PreferenceManager.getDefaultSharedPreferences(context);
        }

    }

    private static void checkfornull() {
        if (utils == null)
            throw new NullPointerException("Call init() method in application context class for the manifest");
    }

    // toast
    public static void toast(String msg) {
        checkfornull();
        Toast.makeText(instance, msg, Toast.LENGTH_SHORT).show();
    }

    //putString
    public static void store(String key, String value) {
        checkfornull();
        try {
            utils.sp.edit().putString(key, value).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //putInt
    public static void store(String key, int value) {
        checkfornull();
        try {
            utils.sp.edit().putInt(key, value).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //putLong
    public static void store(String key, long value) {
        checkfornull();
        try {
            utils.sp.edit().putLong(key, value).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //putFloat
    public static void store(String key, float value) {
        checkfornull();
        try {
            utils.sp.edit().putFloat(key, value).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //putBoolean
    public static void store(String key, boolean value) {
        checkfornull();
        try {
            utils.sp.edit().putBoolean(key, value).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //putObject or arrayList
//    public static void store(String key, Object value) {
//          implementation 'com.google.code.gson:gson:2.8.7'
//        checkfornull();
//        try {
//            Gson gson = new GsonBuilder().create();
//            utils.sp.edit().putString(key, gson.toJson(value).toString()).apply();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


    //getString
    public static String getString(String key, String defaultvalue) {
        checkfornull();
        try {
            return utils.sp.getString(key, defaultvalue);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultvalue;
        }
    }

    public static String getString(String key) {
        return getString(key, "Error");
    }

    //getInt
    public static int getInt(String key, int defaultvalue) {
        checkfornull();
        try {
            return utils.sp.getInt(key, defaultvalue);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultvalue;
        }
    }

    public static int getInt(String key) {
        return getInt(key, 0);
    }

    //getLong
    public static long getLong(String key, long defaultvalue) {
        checkfornull();
        try {
            return utils.sp.getLong(key, defaultvalue);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultvalue;
        }
    }

    public static long getLong(String key) {
        return getLong(key, (long) 0);
    }

    //getFloat
    public static float getFloat(String key, float defaultvalue) {
        checkfornull();
        try {
            return utils.sp.getFloat(key, defaultvalue);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultvalue;
        }
    }

    public static float getFloat(String key) {
        return getFloat(key, 0.0f);
    }

    //getBoolean
    public static boolean getBoolean(String key, boolean defaultvalue) {
        checkfornull();
        try {
            return utils.sp.getBoolean(key, defaultvalue);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultvalue;
        }
    }

    public static boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    //getObject
//    public static <T> Object getObject(String key, Class<?> tClass) {
//          implementation 'com.google.code.gson:gson:2.8.7'
//        checkfornull();
//        try {
//            Gson gson = new GsonBuilder().create();
//            return gson.fromJson(utils.sp.getString(key, ""), tClass);
//        } catch (Exception e) {
//            Log.e("gson", e.getMessage());
//            return "";
//        }
//    }

    //getArrayList
//    public static <T> ArrayList<T> getArrayList(String key, Class<?> tClass) {
//          implementation 'com.google.code.gson:gson:2.8.7'
//        Log.e("_+_++__+_+", "" + tClass.getName());
//        Gson gson = new Gson();
//        String data = utils.sp.getString(key, "");
//        if (!data.trim().isEmpty()) {
//            Type type = new GenericType(tClass);
//            return (ArrayList<T>) gson.fromJson(data, type);
//        }
//        return new ArrayList<T>();
//    }

    //clear single value
    public static void remove(String key) {
        checkfornull();
        try {
            utils.sp.edit().remove(key).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //clear all preference
    public static void removeSharedPref() {
        checkfornull();
        try {
            utils.sp.edit().clear().apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public static void store(String name, ArrayList<String> arrayList) {
//        Set<String> set = new HashSet<>(arrayList);
//        utils.sp.edit().putStringSet(name, set).apply();
//    }
//
//    public static ArrayList<String> getArrayList(String name) {
//        Set<String> defaultSet = new HashSet<>();
//        defaultSet.add("Error");
//        Set<String> set = utils.sp.getStringSet(name, defaultSet);
//        return new ArrayList<>(set);
//    }

    public static int getRandomNmbr(int length) {
        return new Random().nextInt(length) + 100;
    }

//    public void showOfflineDialog(Context context, String title, String desc) {
//

    /*
    * dialog_background.xml
    *
    * <?xml version="1.0" encoding="utf-8"?>
<selector xmlns:android="http://schemas.android.com/apk/res/android">

    <item>

        <shape android:shape="rectangle">

<!--            <solid android:color="#EA3030" />-->
            <solid android:color="@color/white" />
            <corners android:radius="15dp" />

        </shape>

    </item>

</selector>
    * */

    /*
    * ic_info.xml
    *
    * <vector android:height="24dp" android:tint="@color/red"
    android:viewportHeight="24" android:viewportWidth="24"
    android:width="24dp" xmlns:android="http://schemas.android.com/apk/res/android">
    <path android:fillColor="@color/red" android:pathData="M11,7h2v2h-2zM11,11h2v6h-2zM12,2C6.48,2 2,6.48 2,12s4.48,10 10,10 10,-4.48 10,-10S17.52,2 12,2zM12,20c-4.41,0 -8,-3.59 -8,-8s3.59,-8 8,-8 8,3.59 8,8 -3.59,8 -8,8z"/>
</vector>

    *
    * */

    /*
		<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="300dp"
    android:layout_height="wrap_content"
    android:layout_gravity="center"
    android:background="@drawable/bg_dialog"
    android:orientation="vertical">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:visibility="invisible"
        android:layout_gravity="end"
        android:layout_marginEnd="10dp"
        android:layout_marginRight="10dp"
        android:layout_marginTop="7dp"
        android:text="x"
        android:textColor="@color/white"
        android:textSize="20sp"
        android:textStyle="bold" />

    <ImageView
        android:layout_width="80dp"
        android:layout_height="80dp"
        android:layout_gravity="center"
        android:src="@drawable/ic_info" />

    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_gravity="center"
        android:layout_marginEnd="12dp"
        android:layout_marginLeft="12dp"
        android:layout_marginRight="12dp"
        android:id="@+id/title_offline_dialog"
        android:layout_marginStart="12dp"
        android:gravity="center"
        android:maxLines="1"
        android:text="Awww... Snap!"
        android:textColor="@color/darkBlue"
        android:textSize="18sp"
        android:layout_marginTop="5dp"
        android:textStyle="bold" />

    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_gravity="center"
        android:layout_marginEnd="12dp"
        android:layout_marginLeft="12dp"
        android:layout_marginRight="12dp"
        android:layout_marginStart="12dp"
        android:layout_marginTop="10dp"
        android:gravity="center"
        android:text="You are not connected to Internet. Please make sure you have a working connection"
        android:textColor="@color/greyishblue"
        android:id="@+id/desc_offline_dialog"
        android:textSize="15sp" />

    <Button
        android:id="@+id/okay_btn_offline_dialog"
        android:layout_width="160dp"
        android:layout_height="45dp"
        android:layout_gravity="center"
        android:layout_marginBottom="15dp"
        android:layout_marginTop="20dp"
        android:background="@drawable/bg_dialog_offline_button"
        android:gravity="center"
        android:text="Okay"
        android:textColor="@color/white" />

</LinearLayout>
		*/

//        Button okayBtn;
//
//        final Dialog dialogOffline = new Dialog(context);
//        dialogOffline.setContentView(R.layout.dialog_offline);
//
//        okayBtn = dialogOffline.findViewById(R.id.okay_btn_offline_dialog);
//        TextView titleTv = dialogOffline.findViewById(R.id.title_offline_dialog);
//        TextView descTv = dialogOffline.findViewById(R.id.desc_offline_dialog);
//
//        if (!TextUtils.isEmpty(title))
//            titleTv.setText(title);
//
//        if (!TextUtils.isEmpty(desc))
//            descTv.setText(desc);
//
//        okayBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialogOffline.dismiss();
//            }
//        });
//
//        dialogOffline.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialogOffline.show();
//
//    }
//
//    public void showWorkDoneDialog(Context context, String title, String desc) {
//

    /*
    * <?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="300dp"
    android:layout_height="wrap_content"
    android:layout_gravity="center"
    android:background="@drawable/bg_dialog"
    android:orientation="vertical">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="end"
        android:layout_marginEnd="10dp"
        android:layout_marginRight="10dp"
        android:visibility="invisible"
        android:layout_marginTop="7dp"
        android:text="x"
        android:textColor="@color/black"
        android:textSize="20sp"
        android:textStyle="bold" />

    <ImageView
        android:layout_width="80dp"
        android:layout_height="80dp"
        android:layout_gravity="center"
        android:src="@drawable/ic_done" />

    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_gravity="center"
        android:layout_marginEnd="12dp"
        android:layout_marginLeft="12dp"
        android:layout_marginRight="12dp"
        android:layout_marginStart="12dp"
        android:gravity="center"
        android:maxLines="1"
        android:text="Email sent"
        android:id="@+id/title_work_done_dialog"
        android:textColor="@color/darkBlue"
        android:textSize="18sp"
        android:layout_marginTop="5dp"
        android:textStyle="bold" />

    <TextView
        android:id="@+id/desc_work_done_dialog"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_gravity="center"
        android:layout_marginEnd="12dp"
        android:layout_marginLeft="12dp"
        android:layout_marginRight="12dp"
        android:layout_marginStart="12dp"
        android:layout_marginTop="10dp"
        android:gravity="center"
        android:text="Open your email account and follow the instructions given through the link."
        android:textColor="@color/greyishblue"
        android:textSize="15sp" />

    <Button
        android:id="@+id/okay_btn_work_done_dialog"
        android:layout_width="160dp"
        android:layout_height="45dp"
        android:layout_gravity="center"
        android:layout_marginBottom="15dp"
        android:layout_marginTop="20dp"
        android:background="@drawable/bg_dialog_work_done_button"
        android:gravity="center"
        android:text="Okay"
        android:textColor="@color/white" />

</LinearLayout>
    * */

//        final Dialog dialog = new Dialog(context);
//        dialog.setContentView(R.layout.dialog_work_done);
//
//        Button okayBtn = dialog.findViewById(R.id.okay_btn_work_done_dialog);
//        TextView titleTv = dialog.findViewById(R.id.title_work_done_dialog);
//        TextView descTv = dialog.findViewById(R.id.desc_work_done_dialog);
//
//        if (!TextUtils.isEmpty(title))
//            titleTv.setText(title);
//
//        if (!TextUtils.isEmpty(desc))
//            descTv.setText(desc);
//
//        okayBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//            }
//        });
//
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialog.show();
//
//    }

    public static void showDialog(Context context, String title, String message, String positiveBtnName, String negativeBtnName, DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener negativeListener, boolean cancellable) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveBtnName, positiveListener)
                .setNegativeButton(negativeBtnName, negativeListener)
                .setCancelable(cancellable)
                .show();
    }

//    public void saveArrayList(ArrayList<String> list, String key){
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
    //      SharedPreferences.Editor editor = prefs.edit();
    //    Gson gson = new Gson();
    //  String json = gson.toJson(list);
    // editor.putString(key, json);
    //editor.apply();     // This line is IMPORTANT !!!
    // }

    //public ArrayList<String> getArrayList(String key){
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
    //      Gson gson = new Gson();
    //    String json = prefs.getString(key, null);
    //  Type type = new TypeToken<ArrayList<String>>() {}.getType();
    //return gson.fromJson(json, type);
    // }

    public static String getDate() {

        try {

            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            return sdf.format(date);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Error";

    }


//    public String getDate(Context context) {
//
//        try {
//
//            Date date = SecureTimer.with(context).getCurrentDate();
//            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
//            return sdf.format(date);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return "Error";
//
//    }
//
//    public String getNextDate(Context context) {
//
//        try {
//            Date date = SecureTimer.with(context).getCurrentDate();
//            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
//
//            Calendar c = Calendar.getInstance();
//
//            c.setTime(sdf.parse(sdf.format(date)));
//            c.add(Calendar.DATE, 1);
//            return sdf.format(c.getTime());
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return "Error";
//    }
//
//    public String getPreviousDate(Context context) {
//
//        try {
//            Date date = SecureTimer.with(context).getCurrentDate();
//            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
//
//            Calendar c = Calendar.getInstance();
//
//            c.setTime(sdf.parse(sdf.format(date)));
//            c.add(Calendar.DATE, -1);
//            return sdf.format(c.getTime());
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return "Error";
//    }

//    public void showSnackBar(View view, String msg) {
//        Snackbar.make(view, msg, Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show();
//    }
/*public void changeStatusBarColor(Activity activity, int id) {

    // Changing the color of status bar
    if (Build.VERSION.SDK_INT >= 21) {
        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(activity.getResources().getColor(id));
    }

    // CHANGE STATUS BAR TO TRANSPARENT
    //window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
}

    // PUBLIC METHOD TO GET VIEW FROM ONE ACTIVITY OR FRAGMENT TO ANOTHER
    //-------------------------------------------------------------------
    //public Utils.NonSwipableViewPager getClassRoomViewPager() {
//
    //      // Class to set current item or change any view from any different activity
//
    //      if (null == classRoomViewPager) {
    //        classRoomViewPager = (Utils.NonSwipableViewPager) findViewById(R.id.class_room_viewPager);
    //  }
//
    //      return classRoomViewPager;
    //}

    From fragment to activty:

((YourActivityClassName)getActivity()).yourPublicMethod();
From activity to fragment:

FragmentManager fm = getSupportFragmentManager();

//if you added fragment via layout xml
YourFragmentClass fragment = (YourFragmentClass)fm.findFragmentById(R.id.your_fragment_id);
fragment.yourPublicMethod();
If you added fragment via code and used a tag string when you added your fragment, use findFragmentByTag instead:

YourFragmentClass fragment = (YourFragmentClass)fm.findFragmentByTag("yourTag");

    */

    private static class GenericType implements ParameterizedType {

        private final Type type;

        GenericType(Type type) {
            this.type = type;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return new Type[]{type};
        }

        @Override
        public Type getRawType() {
            return ArrayList.class;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }

        // implement equals method too! (as per javadoc)
    }

}

    /*
    public class Utils {

        private static final String PACKAGE_NAME = "com.moutamid.uchannelboost";

        private SharedPreferences sharedPreferences;

        public void removeSharedPref(Context context) {
            sharedPreferences = context.getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
            sharedPreferences.edit().clear().apply();
        }

        public String getStoredString(Context context, String name) {
            sharedPreferences = context.getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
            return sharedPreferences.getString(name, "Error");
        }

        public void storeString(Context context, String name, String object) {
            sharedPreferences = context.getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
            sharedPreferences.edit().putString(name, object).apply();
        }

        public void storeBoolean(Context context1, String name, boolean value) {
            sharedPreferences = context1.getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
            sharedPreferences.edit().putBoolean(name, value).apply();
        }

        public boolean getStoredBoolean(Context context1, String name) {
            sharedPreferences = context1.getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
            return sharedPreferences.getBoolean(name, false);
        }

        public void storeInteger(Context context1, String name, int value) {
            sharedPreferences = context1.getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
            sharedPreferences.edit().putInt(name, value).apply();
        }

        public int getStoredInteger(Context context1, String name) {
            sharedPreferences = context1.getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
            return sharedPreferences.getInt(name, 0);
        }

        public void storeFloat(Context context1, String name, float value) {
            sharedPreferences = context1.getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
            sharedPreferences.edit().putFloat(name, value).apply();
        }

        public float getStoredFloat(Context context1, String name) {
            sharedPreferences = context1.getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
            return sharedPreferences.getFloat(name, 0);
        }

        //    public void storeArrayList(Context context, String name, ArrayList<String> arrayList) {
    //        sharedPreferences = context.getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
    //        SharedPreferences.Editor edit = sharedPreferences.edit();
    //        Set<String> set = new HashSet<>(arrayList);
    //        edit.putStringSet(name, set);
    //        edit.apply();
    //    }
    //
    //    public ArrayList<String> getStoredArrayList(Context context, String name) {
    //        sharedPreferences = context.getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
    //        Set<String> defaultSet = new HashSet<>();
    //        defaultSet.add("Error");
    //        Set<String> set = sharedPreferences.getStringSet(name, defaultSet);
    //        return new ArrayList<>(set);
    //    }

        public String getRandomNmbr(int length) {
            return String.valueOf(new Random().nextInt(length) + 1);
        }

    //    public void showOfflineDialog(Context context, String title, String desc) {
    //

        */
/*
		<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="300dp"
    android:layout_height="wrap_content"
    android:layout_gravity="center"
    android:background="@drawable/bg_dialog"
    android:orientation="vertical">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:visibility="invisible"
        android:layout_gravity="end"
        android:layout_marginEnd="10dp"
        android:layout_marginRight="10dp"
        android:layout_marginTop="7dp"
        android:text="x"
        android:textColor="@color/white"
        android:textSize="20sp"
        android:textStyle="bold" />

    <ImageView
        android:layout_width="80dp"
        android:layout_height="80dp"
        android:layout_gravity="center"
        android:src="@drawable/ic_info" />

    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_gravity="center"
        android:layout_marginEnd="12dp"
        android:layout_marginLeft="12dp"
        android:layout_marginRight="12dp"
        android:id="@+id/title_offline_dialog"
        android:layout_marginStart="12dp"
        android:gravity="center"
        android:maxLines="1"
        android:text="Awww... Snap!"
        android:textColor="@color/darkBlue"
        android:textSize="18sp"
        android:layout_marginTop="5dp"
        android:textStyle="bold" />

    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_gravity="center"
        android:layout_marginEnd="12dp"
        android:layout_marginLeft="12dp"
        android:layout_marginRight="12dp"
        android:layout_marginStart="12dp"
        android:layout_marginTop="10dp"
        android:gravity="center"
        android:text="You are not connected to Internet. Please make sure you have a working connection"
        android:textColor="@color/greyishblue"
        android:id="@+id/desc_offline_dialog"
        android:textSize="15sp" />

    <Button
        android:id="@+id/okay_btn_offline_dialog"
        android:layout_width="160dp"
        android:layout_height="45dp"
        android:layout_gravity="center"
        android:layout_marginBottom="15dp"
        android:layout_marginTop="20dp"
        android:background="@drawable/bg_dialog_offline_button"
        android:gravity="center"
        android:text="Okay"
        android:textColor="@color/white" />

</LinearLayout>
		*//*


//        Button okayBtn;
//
//        final Dialog dialogOffline = new Dialog(context);
//        dialogOffline.setContentView(R.layout.dialog_offline);
//
//        okayBtn = dialogOffline.findViewById(R.id.okay_btn_offline_dialog);
//        TextView titleTv = dialogOffline.findViewById(R.id.title_offline_dialog);
//        TextView descTv = dialogOffline.findViewById(R.id.desc_offline_dialog);
//
//        if (!TextUtils.isEmpty(title))
//            titleTv.setText(title);
//
//        if (!TextUtils.isEmpty(desc))
//            descTv.setText(desc);
//
//        okayBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialogOffline.dismiss();
//            }
//        });
//
//        dialogOffline.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialogOffline.show();
//
//    }
//
//    public void showWorkDoneDialog(Context context, String title, String desc) {
//

    */
/*
    * <?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="300dp"
    android:layout_height="wrap_content"
    android:layout_gravity="center"
    android:background="@drawable/bg_dialog"
    android:orientation="vertical">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="end"
        android:layout_marginEnd="10dp"
        android:layout_marginRight="10dp"
        android:visibility="invisible"
        android:layout_marginTop="7dp"
        android:text="x"
        android:textColor="@color/black"
        android:textSize="20sp"
        android:textStyle="bold" />

    <ImageView
        android:layout_width="80dp"
        android:layout_height="80dp"
        android:layout_gravity="center"
        android:src="@drawable/ic_done" />

    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_gravity="center"
        android:layout_marginEnd="12dp"
        android:layout_marginLeft="12dp"
        android:layout_marginRight="12dp"
        android:layout_marginStart="12dp"
        android:gravity="center"
        android:maxLines="1"
        android:text="Email sent"
        android:id="@+id/title_work_done_dialog"
        android:textColor="@color/darkBlue"
        android:textSize="18sp"
        android:layout_marginTop="5dp"
        android:textStyle="bold" />

    <TextView
        android:id="@+id/desc_work_done_dialog"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_gravity="center"
        android:layout_marginEnd="12dp"
        android:layout_marginLeft="12dp"
        android:layout_marginRight="12dp"
        android:layout_marginStart="12dp"
        android:layout_marginTop="10dp"
        android:gravity="center"
        android:text="Open your email account and follow the instructions given through the link."
        android:textColor="@color/greyishblue"
        android:textSize="15sp" />

    <Button
        android:id="@+id/okay_btn_work_done_dialog"
        android:layout_width="160dp"
        android:layout_height="45dp"
        android:layout_gravity="center"
        android:layout_marginBottom="15dp"
        android:layout_marginTop="20dp"
        android:background="@drawable/bg_dialog_work_done_button"
        android:gravity="center"
        android:text="Okay"
        android:textColor="@color/white" />

</LinearLayout>
    * *//*


//        final Dialog dialog = new Dialog(context);
//        dialog.setContentView(R.layout.dialog_work_done);
//
//        Button okayBtn = dialog.findViewById(R.id.okay_btn_work_done_dialog);
//        TextView titleTv = dialog.findViewById(R.id.title_work_done_dialog);
//        TextView descTv = dialog.findViewById(R.id.desc_work_done_dialog);
//
//        if (!TextUtils.isEmpty(title))
//            titleTv.setText(title);
//
//        if (!TextUtils.isEmpty(desc))
//            descTv.setText(desc);
//
//        okayBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//            }
//        });
//
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialog.show();
//
//    }

    public void showDialog(Context context, String title, String message, String positiveBtnName, String negativeBtnName, DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener negativeListener, boolean cancellable) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveBtnName, positiveListener)
                .setNegativeButton(negativeBtnName, negativeListener)
                .setCancelable(cancellable)
                .show();
    }

//    public void saveArrayList(ArrayList<String> list, String key){
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
    //      SharedPreferences.Editor editor = prefs.edit();
    //    Gson gson = new Gson();
    //  String json = gson.toJson(list);
    // editor.putString(key, json);
    //editor.apply();     // This line is IMPORTANT !!!
    // }

    //public ArrayList<String> getArrayList(String key){
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
    //      Gson gson = new Gson();
    //    String json = prefs.getString(key, null);
    //  Type type = new TypeToken<ArrayList<String>>() {}.getType();
    //return gson.fromJson(json, type);
    // }

    public String getDate() {

        try {

            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            return sdf.format(date);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Error";

    }


//    public String getDate(Context context) {
//
//        try {
//
//            Date date = SecureTimer.with(context).getCurrentDate();
//            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
//            return sdf.format(date);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return "Error";
//
//    }
//
//    public String getNextDate(Context context) {
//
//        try {
//            Date date = SecureTimer.with(context).getCurrentDate();
//            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
//
//            Calendar c = Calendar.getInstance();
//
//            c.setTime(sdf.parse(sdf.format(date)));
//            c.add(Calendar.DATE, 1);
//            return sdf.format(c.getTime());
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return "Error";
//    }
//
//    public String getPreviousDate(Context context) {
//
//        try {
//            Date date = SecureTimer.with(context).getCurrentDate();
//            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
//
//            Calendar c = Calendar.getInstance();
//
//            c.setTime(sdf.parse(sdf.format(date)));
//            c.add(Calendar.DATE, -1);
//            return sdf.format(c.getTime());
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return "Error";
//    }

//    public void showSnackBar(View view, String msg) {
//        Snackbar.make(view, msg, Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show();
//    }

    // PUBLIC METHOD TO GET VIEW FROM ONE ACTIVITY OR FRAGMENT TO ANOTHER
    //-------------------------------------------------------------------
    //public Utils.NonSwipableViewPager getClassRoomViewPager() {
//
    //      // Class to set current item or change any view from any different activity
//
    //      if (null == classRoomViewPager) {
    //        classRoomViewPager = (Utils.NonSwipableViewPager) findViewById(R.id.class_room_viewPager);
    //  }
//
    //      return classRoomViewPager;
    //}*//*

}

*/
