package com.photosynq.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver
{
      
    @Override
    public void onReceive(Context context, Intent intent)
    {
//      	DataUtils.download(context);
       Intent service1 = new Intent(context, AlarmService.class);
       context.startService(service1);
       System.out.println("----------------------sync data onReceive complete-----------------");
        
    }   
}