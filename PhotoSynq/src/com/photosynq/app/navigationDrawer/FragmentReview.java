package com.photosynq.app.navigationDrawer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.photosynq.app.R;
import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.AppSettings;
import com.photosynq.app.utils.BluetoothService;
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
		userId = PrefUtils.getFromPrefs(getActivity() , PrefUtils.PREFS_LOGIN_USERNAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);
		db = DatabaseHelper.getHelper(getActivity());
		AppSettings appSettings = db.getSettings(userId);
		
		TextView tvMode = (TextView) rootView.findViewById(R.id.mode_text);
		TextView tvUser = (TextView) rootView.findViewById(R.id.user_text);
		TextView tvBluetoothId = (TextView) rootView.findViewById(R.id.connection_text);
		TextView tvProjectId = (TextView) rootView.findViewById(R.id.project_text);
		//TextView tvQuestions = (TextView) rootView.findViewById(R.id.tvQuestions);
			
		//Set cuurent settings 
		if(null != appSettings.getModeType())
		{
			if (appSettings.getModeType().equals(Utils.APP_MODE_NORMAL))
			{
				tvMode.setText(getResources().getString(R.string.normal_mode));
			}
			else if(appSettings.getModeType().equals(Utils.APP_MODE_STREAMLINE))
			{
				tvMode.setText(getResources().getString(R.string.streamlined_mode));
			}
		}
		String loggedInUserName = PrefUtils.getFromPrefs(getActivity(), PrefUtils.PREFS_USER,null);
		tvUser.setText(loggedInUserName);
		
//		String bluetoothMacId = PrefUtils.getFromPrefs(getActivity(), PrefUtils.PREFS_CONNECTION_ID,null);
		
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(appSettings.connectionId);
		tvBluetoothId.setText(device.getName());
		
		String projectId = appSettings.getProjectId();
		tvProjectId.setText(db.getResearchProject(projectId).getName());
		
		
		
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
