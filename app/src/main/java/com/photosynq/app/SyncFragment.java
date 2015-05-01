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
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
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


public class SyncFragment extends Fragment implements PhotosynqResponse{
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static int mSectionNumber;

    private DatabaseHelper dbHelper;
    private ProgressDialog pDialog;
    long set_interval_time;
    private CheckBox cbAutoSyncWifiOnly;
    private int syncBtnClickCount = 0;
    long seconds = 0;
    Timer timer;
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
        if(isCheckedWifiSync.equals("1")){
            cbAutoSyncWifiOnly.setChecked(true);
        }else{
            cbAutoSyncWifiOnly.setChecked(false);
        }
        TextView tvAutoSyncCachedDataPtValue = (TextView) rootView.findViewById(R.id.tv_data_points_value);
        tvAutoSyncCachedDataPtValue.setTypeface(CommonUtils.getInstance(getActivity()).getFontRobotoRegular());
        DatabaseHelper db = DatabaseHelper.getHelper(getActivity());
        final List<ProjectResult> listRecords = db.getAllUnUploadedResults();
        //set total of cached points.
        if(listRecords.size() > 0) {
            tvAutoSyncCachedDataPtValue.setText(listRecords.size() + "");
        }else{
            tvAutoSyncCachedDataPtValue.setText("0");
        }

        tvAutoSyncCachedDataPtValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(listRecords.size() == 0) {
                    Toast.makeText(getActivity(), "No cached data point", Toast.LENGTH_SHORT).show();
                }else {
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


        String get_interval_time = PrefUtils.getFromPrefs(getActivity(), PrefUtils.PREFS_SAVE_SYNC_INTERVAL,"2");
//        <item>5 mins</item> 0
//        <item>30 mins</item> 1
//        <item>1 hr</item> 2
//        <item>12 hrs</item> 3
//        <item>1 day</item> 4
//        <item>Manual</item> 5
        intervalSpinner.setTag(get_interval_time);
        intervalSpinner.setSelection(Integer.parseInt(get_interval_time));
        intervalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(!adapterView.getTag().equals(Integer.toString(i))) {
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

                seconds = 2000;
                timer.schedule(new TimerTask() {
                    public void run() {
                        syncBtnClickCount++;
                    }
                }, seconds);


                //syncBtnClickCount = syncBtnClickCount + 1;
                if (syncBtnClickCount == 3) {
                    new AlertDialog.Builder(getActivity())
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Clear Cache")
                            .setMessage("Do you want to really clear cache ?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dbHelper.deleteAllData();
                                    MainActivity mainActivity = (MainActivity) getActivity();
                                    SyncHandler syncHandler = new SyncHandler(mainActivity);
                                    syncHandler.DoSync(SyncHandler.ALL_SYNC_MODE);

                                }

                            })
                            .setNegativeButton("No", null)
                            .show();
                    syncBtnClickCount = 0;

                } else if (syncBtnClickCount == 1){
                    MainActivity mainActivity = (MainActivity) getActivity();
                    SyncHandler syncHandler = new SyncHandler(mainActivity);
                    syncHandler.DoSync(SyncHandler.ALL_SYNC_MODE);
                    syncBtnClickCount = 0;
                }
            }
        });

//        Button clearBtn = (Button) rootView.findViewById(R.id.btn_clear_cache);
//        clearBtn.setTypeface(CommonUtils.getInstance(getActivity()).getFontRobotoMedium());
//        clearBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                dbHelper.deleteAllData();
//                MainActivity mainActivity = (MainActivity)getActivity();
//                SyncHandler syncHandler = new SyncHandler(mainActivity);
//                syncHandler.DoSync(SyncHandler.ALL_SYNC_MODE);
//            }
//        });

        return rootView;
    }

    public void startSyncService(long set_interval_time) {

        Intent intent = new Intent(getActivity(), AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, 0);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, 10);
        AlarmManager alarmMgr = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        if (set_interval_time == 0) {
            alarmMgr.cancel(alarmIntent);
        } else {
            alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), set_interval_time * 60000, alarmIntent);//3600000*2 means 2 Hours and 60000 = 1 min
            System.out.println("-----------Sync alarm is set-------");
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
        Toast.makeText(getActivity(), "sync fragment is exited", Toast.LENGTH_LONG).show();
        if (cbAutoSyncWifiOnly.isChecked()) {
            PrefUtils.saveToPrefs(getActivity(), PrefUtils.PREFS_SYNC_WIFI_ON, "1");
            ConnectivityManager connManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            if (mWifi.isConnected()) {
                startSyncService(set_interval_time);
                Toast.makeText(getActivity(), "Sync data if Wifi!", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getActivity(), "Wifi is not connected", Toast.LENGTH_SHORT).show();
            }
        }else{
            PrefUtils.saveToPrefs(getActivity(), PrefUtils.PREFS_SYNC_WIFI_ON, "0");
            startSyncService(set_interval_time);
            Toast.makeText(getActivity(), "Sync data if Mobile Data !", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResponseReceived(String result) {
        if(result.equals(Constants.SERVER_NOT_ACCESSIBLE)){
            Toast.makeText(getActivity(), R.string.server_not_reachable, Toast.LENGTH_LONG).show();
        }else {
            //??
        }
    }
}