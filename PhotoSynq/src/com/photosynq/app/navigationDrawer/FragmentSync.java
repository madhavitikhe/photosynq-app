package com.photosynq.app.navigationDrawer;

import java.util.Calendar;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.photosynq.app.AlarmReceiver;
import com.photosynq.app.MainActivity;
import com.photosynq.app.ProjectListActivity;
import com.photosynq.app.R;
import com.photosynq.app.utils.CommonUtils;
import com.photosynq.app.utils.DataUtils;
import com.photosynq.app.utils.PrefUtils;

public class FragmentSync extends Fragment{

	public Button syncbtn,savebtn;
	public EditText getTimeInterval;
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
		
		getTimeInterval = (EditText) rootView.findViewById(R.id.interval_time_editText);
		savebtn = (Button) rootView.findViewById(R.id.save_btn);
		savebtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String interval_time = getTimeInterval.getText().toString();
				PrefUtils.saveToPrefs(getActivity(), PrefUtils.PREFS_SAVE_SYNC_INTERVAL,interval_time);
				
				String get_interval_time = PrefUtils.getFromPrefs(getActivity(), PrefUtils.PREFS_SAVE_SYNC_INTERVAL,null);
				long set_interval_time = Long.parseLong(get_interval_time);
		        Calendar calendar = Calendar.getInstance();
		        calendar.add(Calendar.SECOND, 10);
			    Intent intent = new Intent(getActivity(), AlarmReceiver.class);
			    alarmIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, 0);
			    alarmMgr = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
			    alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),set_interval_time * 60000, alarmIntent);//3600000*2 means 2 Hours and 60000 = 1 min
			    System.out.println("-----------Sync alarm is set-------");
			}
		});
		
		syncbtn = (Button) rootView.findViewById(R.id.sync_btn);
		syncbtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//DataUtils.downloadData(getActivity());
				if(CommonUtils.isConnected(getActivity()))
				{
					new downloadDataAsyncTask().execute();
				}
				else
				{
					Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
				}
				
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
         Toast.makeText(getActivity(), R.string.sync_successful, Toast.LENGTH_SHORT).show();
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


