package com.photosynq.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.photosynq.app.utils.SyncHandler;

public class AlarmReceiver extends BroadcastReceiver
{
      
    @Override
    public void onReceive(Context context, Intent intent)
    {
//      	DataUtils.download(context);
       //Intent service = new Intent(context, AlarmService.class);
       //context.startService(service);
       SyncHandler syncHandler = new SyncHandler(context);
       syncHandler.DoSync(SyncHandler.ALL_SYNC_MODE);

       System.out.println("----------------------sync data onReceive complete-----------------");
        
    }   
}