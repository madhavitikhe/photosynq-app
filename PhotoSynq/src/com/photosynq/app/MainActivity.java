package com.photosynq.app;

import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.AppSettings;
import com.photosynq.app.navigationDrawer.NavigationDrawer;
import com.photosynq.app.navigationDrawer.Utils;
import com.photosynq.app.utils.PrefUtils;

public class MainActivity extends NavigationDrawer {

	protected String latitude,longitude; 
	protected boolean gps_enabled,network_enabled;
	private DatabaseHelper db;
	private String userId;
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		boolean finish = getIntent().getBooleanExtra("finish", false);
        if (finish) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
            return;
        }
        
		//setContentView(R.layout.activity_main);
		LayoutInflater inflater = (LayoutInflater) this
	            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View contentView = inflater.inflate(R.layout.activity_main, null, false);
	    layoutDrawer.addView(contentView, 0); 
	    
		db = DatabaseHelper.getHelper(getApplicationContext());
		
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
		    WebView.setWebContentsDebuggingEnabled(true);
		}
		
		//When user install app first time following thing are set default.
		String first_run = PrefUtils.getFromPrefs(getApplicationContext(), PrefUtils.PREFS_FIRST_RUN,"YES");
		if (first_run.equals("YES"))
		{
			System.out.println("First time running? = YES");
			setAlarm(this);
			userId = PrefUtils.getFromPrefs(getApplicationContext() , PrefUtils.PREFS_LOGIN_USERNAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);
    		AppSettings appSettings = db.getSettings(userId);
			appSettings.setModeType(Utils.APP_MODE_NORMAL);
			db.updateSettings(appSettings);
			PrefUtils.saveToPrefs(getApplicationContext(), PrefUtils.PREFS_FIRST_RUN,"NO");
		}
	}
	
	//setAlarm method sets interval time to executing sync in background and show notification to user.
	public void setAlarm(Context context) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, 10);
	    Intent intent = new Intent(context, AlarmReceiver.class);
	    alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
	    alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
	    alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),3600000*2, alarmIntent);//3600000*2 means 2 Hours
	    System.out.println("-----------Alarm is set-------");
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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    //Handle the back button
	    if(keyCode == KeyEvent.KEYCODE_BACK) {
	        //Ask the user if they want to quit
	        new AlertDialog.Builder(this)
	        .setIcon(android.R.drawable.ic_dialog_alert)
	        .setTitle("Quit")
	        .setMessage("Do You Want to Close the Application")
	        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

	            @Override
	            public void onClick(DialogInterface dialog, int which) {
	                //Stop the activity
	                MainActivity.this.finish();    
	            }

	        })
	        .setNegativeButton("No", null)
	        .show();

	        return true;
	    }
	    else {
	        return super.onKeyDown(keyCode, event);
	    }
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		  case R.id.sign_out:
			Toast.makeText(getApplicationContext(), "Sign Out Successfully", Toast.LENGTH_LONG).show();
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
			SharedPreferences.Editor editor = settings.edit();
			editor.clear();
			editor.commit();
			finish();
			return true;
		  case R.id.close:
			startActivity(this.getIntent());
			finish();
	        return true;
		  default:
			  return super.onOptionsItemSelected(item);
		}		
	}
	
	public void listResearchProjects(View view)
	{
		db = DatabaseHelper.getHelper(getApplicationContext());
		userId = PrefUtils.getFromPrefs(getApplicationContext() , PrefUtils.PREFS_LOGIN_USERNAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);
		AppSettings appSettings = db.getSettings(userId);
		if(appSettings.getModeType().equals(Utils.APP_MODE_NORMAL))
		{
			Intent intent = new Intent(getApplicationContext(),ProjectListActivity.class);
			intent.putExtra(Utils.APP_MODE, Utils.APP_MODE_NORMAL);
			startActivity(intent);
		}
		else if(appSettings.getModeType().equals(Utils.APP_MODE_STREAMLINE))
		{
			Intent intent = new Intent(getApplicationContext(),StreamlinedModeActivity.class);
			startActivity(intent);
		}
		else
		{
			Toast.makeText(getApplicationContext(), "Select mode type first", Toast.LENGTH_LONG).show();
		}
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
	
	public void quickMeasurement(View view)
	{
		Intent intent = new Intent(getApplicationContext(),BluetoothActivity.class);
		intent.putExtra(Utils.APP_MODE, Utils.APP_MODE_QUICK_MEASURE);
		startActivity(intent);
	}
	
	public void test(View view)
	{
		Intent intent = new Intent(getApplicationContext(),StreamlinedModeActivity.class);
		startActivity(intent);
	}
	
}
