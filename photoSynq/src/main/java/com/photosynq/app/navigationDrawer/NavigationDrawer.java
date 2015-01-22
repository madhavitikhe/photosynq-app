package com.photosynq.app.navigationDrawer;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Fragment;
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
import android.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
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
import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.AppSettings;
import com.photosynq.app.utils.BluetoothService;
import com.photosynq.app.utils.PrefUtils;

import java.util.Calendar;

public class NavigationDrawer extends Activity implements FragmentProgress.OnFragmentInteractionListener{

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

        setContentView(R.layout.navigation_drawer);
        boolean finish = getIntent().getBooleanExtra("finish", false);
        if (finish) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
            return;
        }

        FragmentManager fragmentManager = getFragmentManager();
        db = DatabaseHelper.getHelper(getApplicationContext());
        userId = PrefUtils.getFromPrefs(getApplicationContext() , PrefUtils.PREFS_LOGIN_USERNAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);
        appSettings = db.getSettings(userId);

        //When user install app first time following thing are set default.
        String first_run = PrefUtils.getFromPrefs(getApplicationContext(), PrefUtils.PREFS_FIRST_RUN, "YES");
        if (first_run.equals("YES"))
        {
            System.out.println("First time running? = YES");
            setAlarm(getApplicationContext());
            String userId = PrefUtils.getFromPrefs(getApplicationContext() , PrefUtils.PREFS_LOGIN_USERNAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);

            //fragmentManager.beginTransaction().replace(R.id.content_frame, new FragmentProgress(), FragmentProgress.class.getName()).commit();

            PrefUtils.saveToPrefs(getApplicationContext(), PrefUtils.PREFS_FIRST_RUN,"NO");
            PrefUtils.saveToPrefs(getApplicationContext(), PrefUtils.PREFS_FIRST_INSTALL_CYCLE,"YES");
            PrefUtils.saveToPrefs(getApplicationContext(), PrefUtils.PREFS_SAVE_SYNC_INTERVAL,"2");
        }

        if(null != appSettings.getModeType()) {
            if (appSettings.getModeType().equals(Utils.APP_MODE_QUICK_MEASURE)) {
                fragmentManager.beginTransaction().replace(R.id.content_frame, new FragmentSelectProtocol(), FragmentSelectProtocol.class.getName()).commit();
            } else if (appSettings.getModeType().equals(Utils.APP_MODE_STREAMLINE)) {
                fragmentManager.beginTransaction().replace(R.id.content_frame, new FragmentStreamlinedMode(), FragmentStreamlinedMode.class.getName()).commit();
            }
        }
        setTitleActionBar(getResources().getString(R.string.app_name));

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
                Intent intent = new Intent(getApplicationContext(),UserProfile.class);
                startActivity(intent);
//                SharedPreferences settings =  PreferenceManager.getDefaultSharedPreferences(getBaseContext());
//                SharedPreferences.Editor editor = settings.edit();
//                editor.clear();
//                editor.commit();
//                Intent intent = new Intent(getApplicationContext(),NavigationDrawer.class);
//                intent.putExtra("finish", true);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
//                finish();
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

            boolean retVal = true;
            if(lastPosition == 5) {
                FragmentManager fragmentManager = getFragmentManager();
                Fragment fragment = fragmentManager.findFragmentByTag(FragmentData.class.getName());
                if (fragment != null) {

                    retVal = ((FragmentData) fragment).saveData();
                }
            }

            if(retVal) {
                setLastPosition(posicao);
                setFragmentList(lastPosition);
            }
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
        ActionBar actionBar = getActionBar();
        if(actionBar != null)
            actionBar.setTitle(title);
    }

    public void setSubtitleActionBar(CharSequence subTitle) {
        ActionBar actionBar = getActionBar();
        if(actionBar != null)
            actionBar.setSubtitle(subTitle);
    }

    public void setIconActionBar(int icon) {
        ActionBar actionBar = getActionBar();
        if(actionBar != null)
            actionBar.setIcon(icon);
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
            //?? supportInvalidateOptionsMenu();
        }

        @Override
        public void onDrawerOpened(View drawerView) {
            navigationAdapter.notifyDataSetChanged();
            mEmail = PrefUtils.getFromPrefs(getApplicationContext() , PrefUtils.PREFS_LOGIN_USERNAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);
            user_email.setText(mEmail);
            //?? supportInvalidateOptionsMenu();
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
            layoutDrawer.closeDrawer(linearDrawer);
        }
    };

    /**
     * This method sets the title of selected navigation drawer item.
     * @param position  selected item position.
     */
    public void setFragmentList(int position){

        FragmentManager fragmentManager = getFragmentManager();
        switch (position) {

            case 0:
                String btDevice = appSettings.getConnectionId();
                AppSettings appSettings = db.getSettings(userId);
                String modeType = appSettings.getModeType();
                if(modeType == null)
                {
                    fragmentManager.beginTransaction().replace(R.id.content_frame, new FragmentConnection(), FragmentConnection.class.getName()).commit();
                }
                else if(modeType.equals(Utils.APP_MODE_QUICK_MEASURE))
                {
                    Bundle bundle = new Bundle();
                    bundle.putString(BluetoothService.DEVICE_ADDRESS, btDevice);
                    bundle.putString(Utils.APP_MODE, Utils.APP_MODE_QUICK_MEASURE);
                    FragmentSelectProtocol fragment=new FragmentSelectProtocol();
                    fragment.setArguments(bundle);

                    fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, fragment.getClass().getName()).commit();
                }
                else if(modeType.equals(Utils.APP_MODE_STREAMLINE))
                {
                    fragmentManager.beginTransaction().replace(R.id.content_frame, new FragmentStreamlinedMode(), FragmentStreamlinedMode.class.getName()).commit();
                }
                break;
            case 1:
                fragmentManager.beginTransaction().replace(R.id.content_frame, new FragmentMode(), FragmentMode.class.getName()).commit();
                break;
            case 2:
                fragmentManager.beginTransaction().replace(R.id.content_frame, new FragmentUser(), FragmentUser.class.getName()).commit();
                break;
            case 3:
                fragmentManager.beginTransaction().replace(R.id.content_frame, new FragmentConnection(), FragmentConnection.class.getName()).commit();
                break;
            case 4:
                fragmentManager.beginTransaction().replace(R.id.content_frame, new FragmentProjectList(), FragmentProjectList.class.getName()).commit();
                break;
            case 5:
                fragmentManager.beginTransaction().replace(R.id.content_frame, new FragmentData(), FragmentData.class.getName()).commit();
                break;
            case 6:
                fragmentManager.beginTransaction().replace(R.id.content_frame, new FragmentReview(), FragmentReview.class.getName()).commit();
                break;
            case 7:
                fragmentManager.beginTransaction().replace(R.id.content_frame, new FragmentSync(), FragmentSync.class.getName()).commit();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);
        mEmail = PrefUtils.getFromPrefs(getApplicationContext() , PrefUtils.PREFS_LOGIN_USERNAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);
        user_email.setText(mEmail);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //Handle the back button
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            layoutDrawer.openDrawer(linearDrawer);
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

    public void quickMeasurement(View view){
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