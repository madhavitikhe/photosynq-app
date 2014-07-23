package com.photosynq.app;

import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

import com.photosynq.app.db.DatabaseHelper;

public class MainActivity extends ActionBarActivity {

	protected String latitude,longitude; 
	protected boolean gps_enabled,network_enabled;
	// Database Helper
    public static final String QUICK_MEASURE ="quick";
   
    
    //private PendingIntent pendingIntent;
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
		    WebView.setWebContentsDebuggingEnabled(true);
		}
		//setAlarm method sets interval time to executing sync in background and show notification to user.
		setAlarm(this);
	}
	
	public void setAlarm(Context context) {   
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, 10);
	    Intent intent = new Intent(context, MyReceiver.class);
	    alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
	    alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
	    alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),7200000, alarmIntent);
	    System.out.println("-----------setalarm");
		}
	
	public void cancelAlarm(Context context) {
	        // If the alarm has been set, cancel it.
	        if (alarmMgr!= null) {
	            alarmMgr.cancel(alarmIntent);
	        }
	    } 
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.

//		switch (item.getItemId()) {
//		  case R.id.start_action:
//			   setAlarm(this);
//	           Toast.makeText(getApplicationContext(), "start", 5).show();
//	           return true;
//		  case R.id.cancel_action:
//			 cancelAlarm(this);
//			  Toast.makeText(getApplicationContext(), "cancel.......", 5).show();
//			  return true;  
//		  default:
//			  return super.onOptionsItemSelected(item);
//		}		
		return super.onOptionsItemSelected(item);
	}
	
	public void listResearchProjects(View view)
	{
		Intent intent = new Intent(getApplicationContext(),ProjectListActivity.class);
		intent.putExtra(QUICK_MEASURE, false);
		startActivity(intent);
	}
	public void recentResearchCollab(View view)
	{
//		Intent intent = new Intent(getApplicationContext(),BluetoothActivity.class);
//		startActivity(intent);

		
	}
	public void researchNearMe(View view)
	{
		Intent intent = new Intent(getApplicationContext(),DisplayResultsActivity.class);
		intent.putExtra(DatabaseHelper.C_PROJECT_ID, "3");
		startActivity(intent);
	}
	public void calibrateIntrument(View view)
	{
		Intent intent = new Intent(getApplicationContext(),SelectProtocolActivity.class);
		startActivity(intent);
	}
	
	public void quickMeasurement(View view)
	{
		Intent intent = new Intent(getApplicationContext(),BluetoothActivity.class);
		intent.putExtra(QUICK_MEASURE, true);
		startActivity(intent);
		
	}
}
