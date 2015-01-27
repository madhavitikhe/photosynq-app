package com.photosynq.app.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.photosynq.app.utils.SyncHandler;

public class AlarmReceiver extends BroadcastReceiver
{
      
    @Override
    public void onReceive(Context context, Intent intent)
    {
       SyncHandler syncHandler = new SyncHandler(context);
       syncHandler.DoSync(SyncHandler.ALL_SYNC_MODE);

       System.out.println("----------------------sync data onReceive complete-----------------");
        
    }   
}