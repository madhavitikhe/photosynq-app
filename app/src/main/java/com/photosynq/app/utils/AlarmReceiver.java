package com.photosynq.app.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.photosynq.app.MainActivity;
import com.photosynq.app.utils.SyncHandler;

public class AlarmReceiver extends BroadcastReceiver
{
      
    @Override
    public void onReceive(Context context, Intent intent)
    {
        String isCheckedWifiSync = PrefUtils.getFromPrefs(context, PrefUtils.PREFS_SYNC_WIFI_ON, "1");
        if(isCheckedWifiSync.equals("1")) {

            ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            if (mWifi != null && mWifi.isConnected()) {//if Wifi is connected

                SyncHandler syncHandler = new SyncHandler(context);
                syncHandler.DoSync(SyncHandler.ALL_SYNC_MODE);
            }
        }else{

            SyncHandler syncHandler = new SyncHandler(context);
            syncHandler.DoSync(SyncHandler.ALL_SYNC_MODE);
        }

       System.out.println("----------------------sync data onReceive complete-----------------");
        
    }   
}