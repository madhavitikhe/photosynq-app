
package com.photosynq.app.navigationDrawer;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.photosynq.app.AlarmReceiver;
import com.photosynq.app.BluetoothActivity;
import com.photosynq.app.LoginActivity;
import com.photosynq.app.R;
import com.photosynq.app.SelectProtocolActivity;
import com.photosynq.app.StreamlinedModeActivity;
import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.AppSettings;
import com.photosynq.app.utils.BluetoothService;
import com.photosynq.app.utils.PrefUtils;

import java.util.Calendar;

public class NavigationDrawer extends ActionBarActivity implements FragmentHome.OnFragmentInteractionListener{
	
	public static final String LAST_POSITION = "LAST_POSITION";
    private int lastPosition = 0;
	private ListView listDrawer;    
	private int counterItemDownloads;
	protected DrawerLayout layoutDrawer;		
	private LinearLayout linearDrawer;
	private RelativeLayout userDrawer;
	private NavigationAdapter navigationAdapter;
	private ActionBarDrawerToggleCompat drawerToggle;
	private String mEmail;
	private TextView user_email;
    private boolean desktopflag = false;
    private DatabaseHelper db;
    private String userId;
    AppSettings appSettings;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean finish = getIntent().getBooleanExtra("finish", false);
        if (finish) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
            return;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        db = DatabaseHelper.getHelper(getApplicationContext());
        userId = PrefUtils.getFromPrefs(getApplicationContext() , PrefUtils.PREFS_LOGIN_USERNAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);
        appSettings = db.getSettings(userId);
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentManager.beginTransaction().replace(R.id.content_frame, new FragmentHome()).commit();

        //When user install app first time following thing are set default.
        String first_run = PrefUtils.getFromPrefs(getApplicationContext(), PrefUtils.PREFS_FIRST_RUN, "YES");
        if (first_run.equals("YES"))
        {
            System.out.println("First time running? = YES");
            setAlarm(getApplicationContext());
            String userId = PrefUtils.getFromPrefs(getApplicationContext() , PrefUtils.PREFS_LOGIN_USERNAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);
//            appSettings.setModeType(Utils.APP_MODE_QUICK_MEASURE);
//            db.updateSettings(appSettings);
            PrefUtils.saveToPrefs(getApplicationContext(), PrefUtils.PREFS_FIRST_RUN,"NO");
            PrefUtils.saveToPrefs(getApplicationContext(),PrefUtils.PREFS_SAVE_SYNC_INTERVAL,"2");
            fragmentManager.beginTransaction().replace(R.id.content_frame, new FragmentConnection()).commit();
            setTitleActionBar("Select Measurement Device (bluetooth)");
        }

        if(null != appSettings.getModeType()) {
            if (appSettings.getModeType().equals(Utils.APP_MODE_QUICK_MEASURE)) {
                Intent intent = new Intent(getApplicationContext(), SelectProtocolActivity.class);
                startActivity(intent);
            } else if (appSettings.getModeType().equals(Utils.APP_MODE_STREAMLINE)) {
                Intent intent = new Intent(getApplicationContext(), StreamlinedModeActivity.class);
                startActivity(intent);
            }
        }
        setTitleActionBar(getResources().getString(R.string.app_name));

        getSupportActionBar().setIcon(R.drawable.ic_launcher);
		setContentView(R.layout.navigation_drawer);		
		
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);		
        
        user_email = (TextView) findViewById(R.id.userEmail);
        user_email.setText(userId);

		listDrawer = (ListView) findViewById(R.id.listDrawer);        
		linearDrawer = (LinearLayout) findViewById(R.id.linearDrawer);		
		layoutDrawer = (DrawerLayout) findViewById(R.id.layoutDrawer);	
		
		userDrawer = (RelativeLayout) findViewById(R.id.userDrawer);
		userDrawer.setOnClickListener(userOnClick);
		
		if (listDrawer != null) {
			navigationAdapter = NavigationAdapter.getNavigationAdapter(this);
		}

		listDrawer.setAdapter(navigationAdapter);
		listDrawer.setOnItemClickListener(new DrawerItemClickListener());

		drawerToggle = new ActionBarDrawerToggleCompat(this, layoutDrawer);		
		layoutDrawer.setDrawerListener(drawerToggle);
       		
		//User sign out after click on signout option from navigation drawer.
        TextView sign_out=(TextView) findViewById(R.id.signOut);
        sign_out.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                	//Delete user preferences(Credentials).
                	SharedPreferences settings =  PreferenceManager.getDefaultSharedPreferences(getBaseContext());                          
    		        SharedPreferences.Editor editor = settings.edit();
    		        editor.clear();
    		        editor.commit();
    		        Intent intent = new Intent(getApplicationContext(),NavigationDrawer.class);
    		        intent.putExtra("finish", true);
	            	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	                startActivity(intent);
	                finish();	
                }
            });

        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {

                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                boolean isFirstStart = sp.getBoolean("LearnedDrawer", true);

                // we will not get a value  at first start, so true will be returned

                // if it was the first app start
                if(isFirstStart) {
                    layoutDrawer.openDrawer(linearDrawer);
                    SharedPreferences.Editor e = sp.edit();
                    // we save the value "false", indicating that it is no longer the first appstart
                    e.putBoolean("LearnedDrawer", false);
                    e.commit();
                }
            }
        });

        t.start();
	}

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
	        @Override
	        public void onItemClick(AdapterView<?> parent, View view, int posicao, long id) {          	        	
		    	setLastPosition(posicao);        	
		    	setFragmentList(lastPosition);	  
		    	layoutDrawer.closeDrawer(linearDrawer);	    	
	        }
	    }	
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub		
		super.onSaveInstanceState(outState);		
		outState.putInt(LAST_POSITION, lastPosition);					
	}


	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);	     
	    drawerToggle.syncState();	
	 }	
	
	public void setTitleActionBar(CharSequence title) {
    	getSupportActionBar().setTitle(title);
    }	
	
	public void setSubtitleActionBar(CharSequence subTitle) {
    	getSupportActionBar().setSubtitle(subTitle);
    }	

	public void setIconActionBar(int icon) {    	
    	getSupportActionBar().setIcon(icon);
    }	
	
	public void setLastPosition(int position){
		this.lastPosition = position;
	}	
		
	private class ActionBarDrawerToggleCompat extends ActionBarDrawerToggle {

		public ActionBarDrawerToggleCompat(Activity mActivity, DrawerLayout mDrawerLayout){
			super(
			    mActivity,
			    mDrawerLayout, 
  			    R.drawable.ic_drawer,
  			    0,0);
		}
		
		@Override
		public void onDrawerClosed(View view) {			
			supportInvalidateOptionsMenu();				
		}

		@Override
		public void onDrawerOpened(View drawerView) {	
			navigationAdapter.notifyDataSetChanged();			
			supportInvalidateOptionsMenu();			
		}		
	}
		  
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		drawerToggle.onConfigurationChanged(newConfig);		
	}
	
   
    
	private OnClickListener userOnClick = new OnClickListener() {		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			layoutDrawer.closeDrawer(linearDrawer);
		}
	};

    /**
     * This method sets the title of selected navigation drawer item.
     * @param position  selected item position.
     */
	private void setFragmentList(int position){
		
		FragmentManager fragmentManager = getSupportFragmentManager();
		switch (position) {

            case 0:

//                fragmentManager.beginTransaction().replace(R.id.content_frame, new FragmentHome()).commit();
//                setTitleActionBar(getResources().getString(R.string.app_name));
                String btDevice = appSettings.getConnectionId();
                AppSettings appSettings = db.getSettings(userId);
                    if(appSettings.getModeType().equals(Utils.APP_MODE_QUICK_MEASURE))
                    {
                        Intent intent = new Intent(getApplicationContext(),SelectProtocolActivity.class);
                        intent.putExtra(BluetoothService.DEVICE_ADDRESS, btDevice);
                        intent.putExtra(Utils.APP_MODE, Utils.APP_MODE_QUICK_MEASURE);
                        startActivity(intent);
                    }
                    else if(appSettings.getModeType().equals(Utils.APP_MODE_STREAMLINE))
                    {
                        Intent intent = new Intent(getApplicationContext(),StreamlinedModeActivity.class);
                        startActivity(intent);
                    }
                break;
            case 1:
                fragmentManager.beginTransaction().replace(R.id.content_frame, new FragmentMode()).commit();
                setTitleActionBar("Select Measurement Mode");
                break;
            case 2:
                fragmentManager.beginTransaction().replace(R.id.content_frame, new FragmentUser()).commit();
                setTitleActionBar("Select User");
                break;
            case 3:
                fragmentManager.beginTransaction().replace(R.id.content_frame, new FragmentConnection()).commit();
                setTitleActionBar("Select Measurement Device (bluetooth)");
                break;
            case 4:
                fragmentManager.beginTransaction().replace(R.id.content_frame, new FragmentProjectList()).commit();
                setTitleActionBar("Select Project");
                break;
            case 5:
                fragmentManager.beginTransaction().replace(R.id.content_frame, new FragmentData()).commit();
                setTitleActionBar("Select User-defined Data");
                break;
            case 6:
                fragmentManager.beginTransaction().replace(R.id.content_frame, new FragmentReview()).commit();
                setTitleActionBar("Review All Settings");
                break;
            case 7:
                fragmentManager.beginTransaction().replace(R.id.content_frame, new FragmentSync()).commit();
                setTitleActionBar("Data Sync Options");
                break;
            case 8:
                exitApp();
                break;
		}			
		//show selection of navigation drawer item.(set selected item color is dark).
		//our navigation contain 7 elements i.e we check here with 7
		if (position < 8){
			navigationAdapter.resetarCheck();			
			navigationAdapter.setChecked(position, true);
		}
	}

	public void setTitleFragments(int position){	
		setIconActionBar(Utils.iconNavigation[position]);
		setSubtitleActionBar(Utils.getTitleItem(NavigationDrawer.this, position));				
	}

	public int getCounterItemDownloads() {
		return counterItemDownloads;
	}

	public void setCounterItemDownloads(int value) {
		this.counterItemDownloads = value;
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) 
	{
		super.onActivityResult(requestCode, resultCode, intent);
		mEmail = PrefUtils.getFromPrefs(getApplicationContext() , PrefUtils.PREFS_LOGIN_USERNAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);
		user_email.setText(mEmail);

	}

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //Handle the back button
        if(keyCode == KeyEvent.KEYCODE_BACK) {
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            fragmentManager.beginTransaction().replace(R.id.content_frame, new FragmentHome()).commit();
            setTitleActionBar(getResources().getString(R.string.app_name));
            return true;
        }
        else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return drawerToggle.onOptionsItemSelected(item);
        }

//    public void listResearchProjects(View view)
//    {
//        DatabaseHelper db = DatabaseHelper.getHelper(getApplicationContext());
//        String userId = PrefUtils.getFromPrefs(getApplicationContext() , PrefUtils.PREFS_LOGIN_USERNAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);
//        AppSettings appSettings = db.getSettings(userId);
//        if(appSettings.getModeType().equals(Utils.APP_MODE_NORMAL))
//        {
//            Intent intent = new Intent(getApplicationContext(),ProjectListActivity.class);
//            intent.putExtra(Utils.APP_MODE, Utils.APP_MODE_NORMAL);
//            startActivity(intent);
//        }
//        else if(appSettings.getModeType().equals(Utils.APP_MODE_STREAMLINE))
//        {
//            Intent intent = new Intent(getApplicationContext(),StreamlinedModeActivity.class);
//            startActivity(intent);
//        }
//        else
//        {
//            Toast.makeText(getApplicationContext(), "Select mode type first", Toast.LENGTH_LONG).show();
//        }
//    }
    public void quickMeasurement(View view)
    {
        Intent intent = new Intent(getApplicationContext(),BluetoothActivity.class);
        intent.putExtra(Utils.APP_MODE, Utils.APP_MODE_QUICK_MEASURE);
        startActivity(intent);
    }

    public void exitApp() {
        new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Quit")
                    .setMessage("Do You Want to Close the Application")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Stop the activity
                            NavigationDrawer.this.finish();
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();
   }

    public void setAlarm(Context context) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, 10);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),3600000*2, alarmIntent);//3600000*2 means 2 Hours
        System.out.println("-----------Alarm is set-------");
    }

}
