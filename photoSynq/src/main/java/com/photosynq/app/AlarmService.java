package com.photosynq.app;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

import com.photosynq.app.utils.SyncHandler;

public class AlarmService extends Service {
    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        //DataUtils.downloadData(getApplicationContext());
        System.out.println("**********Display Notification******");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flag, int startId) {
        new SyncDataAsyncTask().execute(this);

        return super.onStartCommand(intent, flag, startId);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        stopService(new Intent(AlarmService.this, AlarmService.class));
        super.onDestroy();
    }


    private class SyncDataAsyncTask extends AsyncTask<Object, Void, Void> {
        protected void onPreExecute() {
            super.onPreExecute();

        }

        protected Void doInBackground(Object... arg) {
            //DataUtils.downloadData((Context)arg0[0], null);
            //?? SyncHandler syncHandler = new SyncHandler((Context)arg[0]);
            //?? syncHandler.DoSync();
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

        }
    }
}
