package com.photosynq.app;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.http.PhotosynqResponse;
import com.photosynq.app.model.ProjectResult;
import com.photosynq.app.utils.AlarmReceiver;
import com.photosynq.app.utils.CommonUtils;
import com.photosynq.app.utils.Constants;
import com.photosynq.app.utils.PrefUtils;
import com.photosynq.app.utils.SyncHandler;
import com.squareup.picasso.Picasso;

import java.util.Timer;
import java.util.TimerTask;

import java.util.Calendar;
import java.util.List;


public class SyncFragment extends Fragment implements PhotosynqResponse {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */

    private static int clickCounter = 0;

    private static int mSectionNumber;

    private DatabaseHelper dbHelper;
    private ProgressDialog pDialog;
    long set_interval_time;
    private CheckBox cbAutoSyncWifiOnly;
    private int syncBtnClickCount = 0;
    long seconds = 0;
    Timer timer;

    private TextView tvAutoSyncCachedDataPtValue;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static SyncFragment newInstance(int sectionNumber) {
        SyncFragment fragment = new SyncFragment();
        mSectionNumber = sectionNumber;
        return fragment;
    }

    public SyncFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sync, container, false);

        dbHelper = DatabaseHelper.getHelper(getActivity());
        timer = new Timer();

        TextView tvAutoSyncInterval = (TextView) rootView.findViewById(R.id.tv_auto_sync_interval);
        tvAutoSyncInterval.setTypeface(CommonUtils.getInstance(getActivity()).getFontRobotoMedium());
        TextView tvAutoSyncWifiRange = (TextView) rootView.findViewById(R.id.tv_auto_sync_wifi);
        tvAutoSyncWifiRange.setTypeface(CommonUtils.getInstance(getActivity()).getFontRobotoMedium());
        TextView tvAutoSyncCachedData = (TextView) rootView.findViewById(R.id.tv_cached_data_points);
        tvAutoSyncCachedData.setTypeface(CommonUtils.getInstance(getActivity()).getFontRobotoMedium());

        TextView tvAutoSyncDesc = (TextView) rootView.findViewById(R.id.tv_auto_sync_interval_desc);
        tvAutoSyncDesc.setTypeface(CommonUtils.getInstance(getActivity()).getFontRobotoRegular());
        TextView tvAutoSyncWifiDesc = (TextView) rootView.findViewById(R.id.tv_auto_sync_wifi_desc);
        tvAutoSyncWifiDesc.setTypeface(CommonUtils.getInstance(getActivity()).getFontRobotoRegular());
        TextView tvAutoSyncCachedDataDesc = (TextView) rootView.findViewById(R.id.tv_cached_data_points_desc);
        tvAutoSyncCachedDataDesc.setTypeface(CommonUtils.getInstance(getActivity()).getFontRobotoRegular());
        TextView tvSyncBtnMsg = (TextView) rootView.findViewById(R.id.tv_sync_btn_message);
        tvSyncBtnMsg.setTypeface(CommonUtils.getInstance(getActivity()).getFontRobotoRegular());

        cbAutoSyncWifiOnly = (CheckBox) rootView.findViewById(R.id.auto_sync_wifi_checkbox);
        String isCheckedWifiSync = PrefUtils.getFromPrefs(getActivity(), PrefUtils.PREFS_SYNC_WIFI_ON, PrefUtils.PREFS_DEFAULT_VAL);
        if (isCheckedWifiSync.equals("1")) {
            cbAutoSyncWifiOnly.setChecked(true);
        } else {
            cbAutoSyncWifiOnly.setChecked(false);
        }
        tvAutoSyncCachedDataPtValue = (TextView) rootView.findViewById(R.id.tv_data_points_value);
        tvAutoSyncCachedDataPtValue.setTypeface(CommonUtils.getInstance(getActivity()).getFontRobotoRegular());
        final DatabaseHelper db = DatabaseHelper.getHelper(getActivity());
        //List<ProjectResult> listRecords = db.getAllUnUploadedResults();
        int recordCount = db.getAllUnuploadedResultsCount(null);
        PrefUtils.saveToPrefs(getActivity(), PrefUtils.PREFS_TOTAL_CACHED_DATA_POINTS, "" + recordCount);

        //set total of cached points.
        tvAutoSyncCachedDataPtValue.setText(recordCount + "");

        tvAutoSyncCachedDataPtValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int recordCount = db.getAllUnuploadedResultsCount(null);
                if (recordCount == 0) {
                    Toast.makeText(getActivity(), "No cached data point", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(getActivity(), DisplayCachedDataPoints.class);
                    startActivity(intent);
                }

            }
        });

        Spinner intervalSpinner = (Spinner) rootView.findViewById(R.id.interval_time_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.sync_intervals, R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        intervalSpinner.setAdapter(adapter);


        String get_interval_time = PrefUtils.getFromPrefs(getActivity(), PrefUtils.PREFS_SAVE_SYNC_INTERVAL, "2");
        PrefUtils.saveToPrefs(getActivity(), "PrevSyncIntervalTime", get_interval_time);
        int sync_iterval = 2;
        try {
            sync_iterval = Integer.parseInt(get_interval_time);
        } catch (NumberFormatException nfe) {
            System.out.println("Could not parse " + nfe);
        }
        switch (sync_iterval) {
            case 0:
                set_interval_time = 5;
                break;
            case 1:
                set_interval_time = 30;
                break;
            case 2:
                set_interval_time = 60;
                break;
            case 3:
                set_interval_time = 720;
                break;
            case 4:
                set_interval_time = 1440;
                break;
            default:
                set_interval_time = 0;
                break;
        }

//        <item>5 mins</item> 0
//        <item>30 mins</item> 1
//        <item>1 hr</item> 2
//        <item>12 hrs</item> 3
//        <item>1 day</item> 4
        intervalSpinner.setTag(get_interval_time);
        intervalSpinner.setSelection(Integer.parseInt(get_interval_time));
        intervalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!adapterView.getTag().equals(Integer.toString(i))) {
                    //adapterView.getItemAtPosition(i);
                    PrefUtils.saveToPrefs(getActivity(), PrefUtils.PREFS_SAVE_SYNC_INTERVAL, i + "");

                    switch (i) {
                        case 0:
                            set_interval_time = 5;
                            break;
                        case 1:
                            set_interval_time = 30;
                            break;
                        case 2:
                            set_interval_time = 60;
                            break;
                        case 3:
                            set_interval_time = 720;
                            break;
                        case 4:
                            set_interval_time = 1440;
                            break;
                        default:
                            set_interval_time = 0;
                            break;

                    }
                }
                adapterView.setTag(Integer.toString(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Button syncBtn = (Button) rootView.findViewById(R.id.btn_sync_data);
        syncBtn.setTypeface(CommonUtils.getInstance(getActivity()).getFontRobotoMedium());

        syncBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Context context = getActivity();

                if (clickCounter == 0) {

                    String isSyncInProgress = PrefUtils.getFromPrefs(getActivity(), PrefUtils.PREFS_IS_SYNC_IN_PROGRESS, "false");
                    if (isSyncInProgress.equals("true")) {

                        Toast.makeText(getActivity(), "Sync already in progress!", Toast.LENGTH_LONG).show();
                        return;

                    } else {
                        Toast.makeText(getActivity(), "Checking internet connection...", Toast.LENGTH_SHORT).show();
                    }

                }
                clickCounter++;

                if (clickCounter == 1) {
                    new CountDownTimer(1000, 1000) {
                        public void onTick(long millisUntilFinished) {

                            System.out.print("@@@@@@@@@@@@@@ test tick");

                        }

                        public void onFinish() {

                            if (clickCounter >= 3) {
                                // Clear cache

                                if (cbAutoSyncWifiOnly.isChecked()) {
                                    PrefUtils.saveToPrefs(getActivity(), PrefUtils.PREFS_SYNC_WIFI_ON, "1");//set 1 if wifi is connected
                                    ConnectivityManager connManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                                    NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

                                    if (mWifi != null && mWifi.isConnected()) {//if Wifi is connected

                                        MainActivity mainActivity = (MainActivity) getActivity();
                                        SyncHandler syncHandler = new SyncHandler(mainActivity);
                                        syncHandler.DoSync(SyncHandler.ALL_SYNC_UI_MODE_CLEAR_CACHE);

                                    } else {//if Wifi is not connected

                                        Toast.makeText(context, "You are not connect to a network.\n" +
                                                "\n" +
                                                "Check if wifi is turned on \n" +
                                                "and if networks are available in your system settings screen. ", Toast.LENGTH_LONG).show();
                                    }

                                } else {//Mobile Data

                                    MainActivity mainActivity = (MainActivity) getActivity();
                                    SyncHandler syncHandler = new SyncHandler(mainActivity);
                                    syncHandler.DoSync(SyncHandler.ALL_SYNC_UI_MODE_CLEAR_CACHE);
                                }
                            } else if (clickCounter == 1) {
                                if (cbAutoSyncWifiOnly.isChecked()) {
                                    PrefUtils.saveToPrefs(getActivity(), PrefUtils.PREFS_SYNC_WIFI_ON, "1");//set 1 if wifi is connected
                                    ConnectivityManager connManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                                    NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

                                    if (mWifi != null && mWifi.isConnected()) {//if Wifi is connected

                                        MainActivity mainActivity = (MainActivity) getActivity();
                                        SyncHandler syncHandler = new SyncHandler(mainActivity);
                                        syncHandler.DoSync(SyncHandler.ALL_SYNC_UI_MODE);

                                    } else {//if Wifi is not connected

                                        Toast.makeText(context, "You are not connect to a network.\n" +
                                                "\n" +
                                                "Check if wifi is turned on \n" +
                                                "and if networks are available in your system settings screen. ", Toast.LENGTH_LONG).show();
                                    }
                                } else {//Mobile Data

                                    MainActivity mainActivity = (MainActivity) getActivity();
                                    SyncHandler syncHandler = new SyncHandler(mainActivity);
                                    syncHandler.DoSync(SyncHandler.ALL_SYNC_UI_MODE);
                                }

                            }
                            clickCounter = 0;
                        }
                    }.start();
                }
            }
        });


        return rootView;
    }

    public void refresh() {

        DatabaseHelper db = DatabaseHelper.getHelper(getActivity());
        int recordCount = db.getAllUnuploadedResultsCount(null);
        PrefUtils.saveToPrefs(getActivity(), PrefUtils.PREFS_TOTAL_CACHED_DATA_POINTS, "" + recordCount);

        //set total of cached points.
        tvAutoSyncCachedDataPtValue.setText(recordCount + "");
    }

    public void startSyncService(long set_interval_time) {

        String get_interval_time = PrefUtils.getFromPrefs(getActivity(), "PrevSyncIntervalTime", "2");
        int prev_sync_iterval = 2;
        try {
            prev_sync_iterval = Integer.parseInt(get_interval_time);
        } catch (NumberFormatException nfe) {
            System.out.println("Could not parse " + nfe);
        }

        Intent intent = new Intent(getActivity(), AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, 0);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, 10);
        AlarmManager alarmMgr = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        if (set_interval_time == 0) {
            alarmMgr.cancel(alarmIntent);
        } else {

            switch (prev_sync_iterval) {
                case 0:
                    prev_sync_iterval = 5;
                    break;
                case 1:
                    prev_sync_iterval = 30;
                    break;
                case 2:
                    prev_sync_iterval = 60;
                    break;
                case 3:
                    prev_sync_iterval = 720;
                    break;
                case 4:
                    prev_sync_iterval = 1440;
                    break;
                default:
                    prev_sync_iterval = 0;
                    break;
            }
            if (prev_sync_iterval != set_interval_time) {
                alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), set_interval_time * 60000, alarmIntent);//3600000*2 means 2 Hours and 60000 = 1 min
                System.out.println("-----------Sync alarm is set-------");
            }
        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(mSectionNumber);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (cbAutoSyncWifiOnly.isChecked()) {
            PrefUtils.saveToPrefs(getActivity(), PrefUtils.PREFS_SYNC_WIFI_ON, "1");//set 1 if wifi is connected
//            ConnectivityManager connManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
//            NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//
//            if (mWifi != null && mWifi.isConnected()) {//if Wifi is connected
//                startSyncService(set_interval_time);
//            }else{//if Wifi is not connected
//
//                Toast.makeText(getActivity(), "You are not connect to a network.\n" +
//                        "\n" +
//                        "Check if wifi is turned on \n" +
//                        "and if networks are available in your system settings screen. ", Toast.LENGTH_LONG).show();
//            }
        } else {//Mobile Data
            PrefUtils.saveToPrefs(getActivity(), PrefUtils.PREFS_SYNC_WIFI_ON, "0");//set 0 if wifi is not connected
//            startSyncService(set_interval_time);
        }

        startSyncService(set_interval_time);
    }

    @Override
    public void onResponseReceived(String result) {
        if (result.equals(Constants.SERVER_NOT_ACCESSIBLE)) {
            Toast.makeText(getActivity(), R.string.server_not_reachable, Toast.LENGTH_LONG).show();
        } else {
            //??
        }
    }

    public void onResume() {
        super.onResume();
        DatabaseHelper db = DatabaseHelper.getHelper(getActivity());
        int recordCount = db.getAllUnuploadedResultsCount(null);

        tvAutoSyncCachedDataPtValue.setText(recordCount + "");


//        Thread t = new Thread() {
//
//            @Override
//            public void run() {
//                try {
//                    while (!isInterrupted()) {
//                        Thread.sleep(1000);
//                        if (getActivity() != null) {
//                            getActivity().runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    try {
//                                        DatabaseHelper db = DatabaseHelper.getHelper(getActivity());
//                                        int recordCount = db.getAllUnuploadedResultsCount(null);
//                                        PrefUtils.saveToPrefs(getActivity(), PrefUtils.PREFS_TOTAL_CACHED_DATA_POINTS, "" + recordCount);
//
//                                        //set total of cached points.
//                                        if (recordCount > 0) {
//                                            tvAutoSyncCachedDataPtValue.setText(recordCount + "");
//                                        } else {
//                                            tvAutoSyncCachedDataPtValue.setText("0");
//                                        }
//                                    }catch (Exception e){}
//                                }
//                            });
//                        }
//                    }
//                } catch (InterruptedException e) {
//                }
//            }
//        };
//
//        t.start();
    }
}
