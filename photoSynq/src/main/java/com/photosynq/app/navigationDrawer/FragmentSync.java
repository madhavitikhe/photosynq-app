package com.photosynq.app.navigationDrawer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.photosynq.app.AlarmReceiver;
import com.photosynq.app.R;
import com.photosynq.app.utils.DataUtils;
import com.photosynq.app.utils.PrefUtils;

import java.util.Calendar;

public class FragmentSync extends Fragment{

	public Button syncbtn;
	public Spinner intervalSpinner;
	private AlarmManager alarmMgr;
	private PendingIntent alarmIntent;
	private ProgressDialog pDialog;
	
    public static FragmentSync newInstance() {
        FragmentSync fragment = new FragmentSync();
        return fragment;
    }	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_sync, container, false);
		intervalSpinner = (Spinner) rootView.findViewById(R.id.interval_time_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.sync_intervals, android.R.layout.simple_spinner_item);
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
                    alarmIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, 0);
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.SECOND, 10);
                    alarmMgr = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                    if (set_interval_time == 0) {
                        alarmMgr.cancel(alarmIntent);
                        Toast.makeText(getActivity(), "Sync interval saved successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), set_interval_time * 60000, alarmIntent);//3600000*2 means 2 Hours and 60000 = 1 min
                        System.out.println("-----------Sync alarm is set-------");
                        Toast.makeText(getActivity(), "Sync interval saved successfully!", Toast.LENGTH_SHORT).show();
                    }
                }
                adapterView.setTag(Integer.toString(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
		//savebtn = (Button) rootView.findViewById(R.id.save_btn);
		//savebtn.setOnClickListener(new OnClickListener() {
			
//			@Override
//			public void onClick(View v) {
//
//				String interval_time = getTimeInterval.getText().toString();
//				if(interval_time.isEmpty())
//				{
//					Toast.makeText(getActivity(), "Please set interval time in minutes", Toast.LENGTH_SHORT).show();
//				}
//				else{
//					PrefUtils.saveToPrefs(getActivity(), PrefUtils.PREFS_SAVE_SYNC_INTERVAL,interval_time);
//					String get_interval_time = PrefUtils.getFromPrefs(getActivity(), PrefUtils.PREFS_SAVE_SYNC_INTERVAL,null);
//					long set_interval_time = Long.parseLong(get_interval_time);
//			        Calendar calendar = Calendar.getInstance();
//			        calendar.add(Calendar.SECOND, 10);
//				    Intent intent = new Intent(getActivity(), AlarmReceiver.class);
//				    alarmIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, 0);
//				    alarmMgr = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
//				    alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),set_interval_time * 60000, alarmIntent);//3600000*2 means 2 Hours and 60000 = 1 min
//				    System.out.println("-----------Sync alarm is set-------");
//					Toast.makeText(getActivity(), "Sync interval saved successfully!", Toast.LENGTH_SHORT).show();
//
//				}
//
//			}
//		});
		
		syncbtn = (Button) rootView.findViewById(R.id.sync_btn);
		syncbtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//DataUtils.downloadData(getActivity());
				//if(CommonUtils.isConnected(getActivity()))
				//{
					new downloadDataAsyncTask().execute();
				//}
				//else
				//{
				//	Toast.makeText(getActivity(), R.string.server_not_reachable, Toast.LENGTH_SHORT).show();
				//}
				
			}
		});
		rootView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT ));		
		return rootView;
	}
	
	private class downloadDataAsyncTask extends AsyncTask<Void, Void, Void> {
		 
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            //downloadData();
            DataUtils.downloadData(getActivity());
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }
 
        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
             return null;
        }
 
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
         //Toast.makeText(getActivity(), R.string.sync_successful, Toast.LENGTH_SHORT).show();
        }
    }

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu, inflater);		
		inflater.inflate(R.menu.menu, menu);
	}	
}


