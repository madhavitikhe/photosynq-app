package com.photosynq.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.AppSettings;
import com.photosynq.app.utils.BluetoothService;
import com.photosynq.app.utils.Constants;
import com.photosynq.app.utils.PrefUtils;

import org.json.JSONObject;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    private int mCurrentSelectedPosition = 0;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        String prevSelPos = PrefUtils.getFromPrefs(this, PrefUtils.PREFS_PREV_SELECTED_POSITION, "0");
        mCurrentSelectedPosition = Integer.parseInt(prevSelPos);
        onNavigationDrawerItemSelected(mCurrentSelectedPosition);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //Handle the back button
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Quit")
                    .setMessage("Do You Want to Close the Application")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Stop the activity
                            PrefUtils.saveToPrefs(MainActivity.this, PrefUtils.PREFS_PREV_SELECTED_POSITION, "0");

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
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        mCurrentSelectedPosition = position;
        FragmentManager fragmentManager = getSupportFragmentManager();

        switch (position){
            case 0:
                // Open Discover
                fragmentManager.beginTransaction()
                        .replace(R.id.container, ProjectModeFragment.newInstance(position), ProjectModeFragment.class.getName())
                        .commit();
                break;
            case 1:
                // Open MyProjects
                fragmentManager.beginTransaction()
                        .replace(R.id.container, ProjectModeFragment.newInstance(position), ProjectModeFragment.class.getName())
                        .commit();
                break;
            case 2:
                // Open Quick Measurement
                String userId = PrefUtils.getFromPrefs(this, PrefUtils.PREFS_LOGIN_USERNAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);
                AppSettings appSettings = DatabaseHelper.getHelper(this).getSettings(userId);

                Bundle bundle = new Bundle();
                String btDevice = appSettings.getConnectionId();
                bundle.putString(BluetoothService.DEVICE_ADDRESS, btDevice);
                QuickModeFragment quickModeFragment = QuickModeFragment.newInstance(position);
                quickModeFragment.setArguments(bundle);

                fragmentManager.beginTransaction()
                        .replace(R.id.container, quickModeFragment, QuickModeFragment.class.getName())
                        .commit();
                break;
            case 3:
                // Sync Settings
                fragmentManager.beginTransaction()
                        .replace(R.id.container, SyncFragment.newInstance(position), SyncFragment.class.getName())
                        .commit();
                break;
            case 4:
                // Open Profile
                fragmentManager.beginTransaction()
                        .replace(R.id.container, ProfileFragment.newInstance(position), ProfileFragment.class.getName())
                        .commit();
                break;
            case 5:
                // Open select device
                SelectDeviceDialog selectDeviceDialog = new SelectDeviceDialog();
                selectDeviceDialog.show(fragmentManager, "Select Measurement Device");
                break;
        }
//        fragmentManager.beginTransaction()
//                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
//                .commit();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(Constants.STATE_SELECTED_POSITION, mCurrentSelectedPosition);
        super.onSaveInstanceState(outState);

        PrefUtils.saveToPrefs(this, PrefUtils.PREFS_PREV_SELECTED_POSITION, mCurrentSelectedPosition + "");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        PrefUtils.saveToPrefs(this, PrefUtils.PREFS_PREV_SELECTED_POSITION, "0");

    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 0:
                mTitle = getString(R.string.discover_title);
                break;
            case 1:
                mTitle = getString(R.string.my_projects_title);
                break;
            case 2:
                mTitle = "Select Measurement";
                break;
            case 3:
                mTitle = getString(R.string.sync_settings_title);
                break;
            case 4:
                mTitle = getString(R.string.profile_title);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setDeviceConnected(String deviceName, String deviceAddress) {
        mNavigationDrawerFragment.setDeviceConnected(deviceName, deviceAddress);
    }
}
