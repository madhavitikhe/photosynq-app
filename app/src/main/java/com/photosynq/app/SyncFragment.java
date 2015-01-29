package com.photosynq.app;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.utils.AlarmReceiver;
import com.photosynq.app.utils.CommonUtils;
import com.photosynq.app.utils.PrefUtils;
import com.photosynq.app.utils.SyncHandler;
import com.squareup.picasso.Picasso;

import java.util.Calendar;


public class SyncFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static int mSectionNumber;

    private DatabaseHelper dbHelper;
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

        TextView tvAutoSync = (TextView) rootView.findViewById(R.id.tv_auto_sync);
        tvAutoSync.setTypeface(CommonUtils.getInstance(getActivity()).getFontRobotoMedium());

        TextView tvAutoSyncDesc = (TextView) rootView.findViewById(R.id.tv_auto_sync_desc);
        tvAutoSyncDesc.setTypeface(CommonUtils.getInstance(getActivity()).getFontRobotoRegular());


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
                    long set_interval_time;
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
                    Toast.makeText(getActivity(), "Sync interval saved successfully!", Toast.LENGTH_SHORT).show();
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

                SyncHandler syncHandler = new SyncHandler((MainActivity)getActivity());
                syncHandler.DoSync(SyncHandler.ALL_SYNC_MODE);
                //??flag = 0;
            }
        });

        Button clearBtn = (Button) rootView.findViewById(R.id.btn_clear_cache);
        clearBtn.setTypeface(CommonUtils.getInstance(getActivity()).getFontRobotoMedium());
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dbHelper.deleteAllData();
                SyncHandler syncHandler = new SyncHandler((MainActivity)getActivity());
                syncHandler.DoSync(SyncHandler.ALL_SYNC_MODE);
                //??flag = 1;
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(mSectionNumber);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}