package com.photosynq.app.navigationDrawer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.photosynq.app.R;
import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.AppSettings;
import com.photosynq.app.model.Data;
import com.photosynq.app.utils.PrefUtils;

public class FragmentReview extends Fragment {
	
	private DatabaseHelper db;
	private String userId;
	public static String newInstance() {
		Bundle bundle = new Bundle();
		String mail = bundle.getString("USER_EMAIL");
		System.out.println("------------------- Mail------"+mail);
		return mail;
    }	
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_review, container, false);
		
		db = DatabaseHelper.getHelper(getActivity());
		
		TextView tvMode = (TextView) rootView.findViewById(R.id.tvMode);
		TextView tvUser = (TextView) rootView.findViewById(R.id.tvUserName);
		TextView tvBluetoothId = (TextView) rootView.findViewById(R.id.tvConnection);
		TextView tvProjectId = (TextView) rootView.findViewById(R.id.tvProjectName);
		TextView tvQuestions = (TextView) rootView.findViewById(R.id.tvQuestions);
		userId = PrefUtils.getFromPrefs(getActivity() , PrefUtils.PREFS_LOGIN_USERNAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);
		AppSettings appSettings = db.getSettings(userId);
			
		//Set cuurent settings 
		if(null != appSettings.getModeType())
		{
			if (appSettings.getModeType().equals("Normal Mode"))
			{
				tvMode.setText("Normal Mode");
			}
			else if(appSettings.getModeType().equals("Streamlined Mode"))
			{
				tvMode.setText("Streamlined Mode");
			}
		}
		String loggedInUserName = PrefUtils.getFromPrefs(getActivity(), PrefUtils.PREFS_USER,null);
		tvUser.setText(loggedInUserName);
		
		String bluetoothMacId = PrefUtils.getFromPrefs(getActivity(), PrefUtils.PREFS_CONNECTION_ID,null);
		tvBluetoothId.setText(bluetoothMacId);
		
		String projectId = PrefUtils.getFromPrefs(getActivity(), PrefUtils.PREFS_PROJECT_ID,null);
		tvProjectId.setText(projectId);
		
		rootView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT ));	
		return rootView;
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
