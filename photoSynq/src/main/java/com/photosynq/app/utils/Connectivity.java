package com.photosynq.app.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.photosynq.app.HTTP.PhotosynqResponse;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by shekhar on 9/13/14.
 */
public class Connectivity extends AsyncTask<String, String, String>{
    private Context context;
    public PhotosynqResponse delegate;
    public Connectivity(Context context)
    {
        this.context = context;
    }
    @Override
    protected String doInBackground(String... strings) {
        if(hasActiveInternetConnection(context)) {
            return "YES";
        }
        else {
            return "NO";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        delegate.onResponseReceived(s);

    }

    private static boolean hasActiveInternetConnection(Context context) {
        if (isNetworkAvailable(context)) {
            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL("http://photosynq.org").openConnection());
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
