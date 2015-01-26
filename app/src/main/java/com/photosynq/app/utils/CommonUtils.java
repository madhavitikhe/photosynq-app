package com.photosynq.app.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by kalpesh on 24/01/15.
 */
public class CommonUtils {

    // App context
    private Context mContext;

    // Singletone class
    private static CommonUtils instance = null;

    // Holds fonts instance
    Typeface uifontFace;
    Typeface openSansLightFace;
    Typeface robotoLightFace;
    Typeface robotoMediumFace;
    Typeface robotoRegularFace;

    private CommonUtils(Context context) {
        mContext = context;
    }

    public static CommonUtils getInstance(Context context){
        if (instance == null)
            instance = new CommonUtils(context);

        return instance;
    }

    public Typeface getFontUiFontSolid() {

        if(uifontFace == null)
            uifontFace = Typeface.createFromAsset(mContext.getAssets(), "uifont-solid.otf");

        return uifontFace;
    }
    public Typeface getFontOpenSansLight() {
        if(openSansLightFace == null)
            openSansLightFace = Typeface.createFromAsset(mContext.getAssets(), "opensans-light.ttf");

        return openSansLightFace;
    }
    public Typeface getFontRobotoLight() {
        if(robotoLightFace == null)
            robotoLightFace = Typeface.createFromAsset(mContext.getAssets(), "roboto-light.ttf");

        return robotoLightFace;
    }
    public Typeface getFontRobotoMedium() {
        if(robotoMediumFace == null)
            robotoMediumFace = Typeface.createFromAsset(mContext.getAssets(), "roboto-medium.ttf");

        return robotoMediumFace;
    }
    public Typeface getFontRobotoRegular() {
        if(robotoRegularFace == null)
            robotoRegularFace = Typeface.createFromAsset(mContext.getAssets(), "roboto-regular.ttf");

        return robotoRegularFace;
    }

    // Invoke this method only on Async task. Do not invoke on UI thread. it will throw exceptions anyway ;)
    public static boolean isConnected(Context context) {
        if (isNetworkAvailable(context)) {
            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL(Constants.SERVER_URL).openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                return (urlc.getResponseCode() == 200);
            } catch (IOException e) {
                Log.e("Connectivity", "Error checking internet connection", e);
            }
        } else {
            Log.d("Connectivity", "No network available!");
        }
        return false;
    }


    private static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }
}
