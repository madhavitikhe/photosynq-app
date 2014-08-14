package com.photosynq.app;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

import com.photosynq.app.utils.DataUtils;

public class AlarmService extends Service 
{
    
	   
	 
	    @Override
	    public IBinder onBind(Intent arg0)
	    {
	       // TODO Auto-generated method stub
	        return null;
	    }
	 
	    @Override
	    public void onCreate() 
	    {
	       // TODO Auto-generated method stub 
	    	//DataUtils.downloadData(getApplicationContext());
	    	System.out.println("**********Display Notification******");
	       super.onCreate();
	    }
	 
	   @SuppressWarnings("deprecation")
	@Override
	   public void onStart(Intent intent, int startId)
	   {
	       super.onStart(intent, startId);
	       new SyncDataAsyncTask().execute();
	    }
	 
	    @Override
	    public void onDestroy() 
	    {
	        // TODO Auto-generated method stub
	    	stopService(new Intent(AlarmService.this, AlarmService.class));
	        super.onDestroy();
	    }
	  
	    
		private class SyncDataAsyncTask extends AsyncTask<Void, Void, Void> 
	    {   
	        protected void onPreExecute() {
	            super.onPreExecute();
	            
	        }
	 
	        protected Void doInBackground(Void... arg0) {
	            DataUtils.downloadData(getApplicationContext());
	        	return null;
	        }
	 
	        protected void onPostExecute(Void result) {
	            super.onPostExecute(result);
	            
	        }
	    }
}
