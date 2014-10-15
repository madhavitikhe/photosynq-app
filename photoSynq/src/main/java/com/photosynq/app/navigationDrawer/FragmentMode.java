package com.photosynq.app.navigationDrawer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.photosynq.app.R;
import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.AppSettings;
import com.photosynq.app.utils.PrefUtils;

public class FragmentMode extends Fragment{

	RadioGroup radioGroup;
	int position;
	int pos1;
	RadioButton rb;
	private DatabaseHelper db;
	private String userId;
	
    public static FragmentMode newInstance() {
        FragmentMode fragment = new FragmentMode();
        return fragment;
    }	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
			
		final View rootView = inflater.inflate(R.layout.fragment_mode, container, false);
		db = DatabaseHelper.getHelper(getActivity());
		userId = PrefUtils.getFromPrefs(getActivity() , PrefUtils.PREFS_LOGIN_USERNAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);
		AppSettings appSettings = db.getSettings(userId);
		
		//Set cuurent settings 
		if(null != appSettings.getModeType())
		{
//			if (appSettings.getModeType().equals(Utils.APP_MODE_NORMAL))
//			{
//				RadioButton rb = (RadioButton)rootView.findViewById(R.id.normal_mode_radio);
//				rb.setChecked(true);
//			}
            if (appSettings.getModeType().equals(Utils.APP_MODE_QUICK_MEASURE))
            {
                RadioButton rb = (RadioButton)rootView.findViewById(R.id.quick_measure_mode_radio);
                rb.setChecked(true);
            }
			else if(appSettings.getModeType().equals(Utils.APP_MODE_STREAMLINE))
			{
            RadioButton rb = (RadioButton)rootView.findViewById(R.id.streamline_mode_radio);
            rb.setChecked(true);
        }
		}
		
		radioGroup = (RadioGroup) rootView.findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			
			position = radioGroup.indexOfChild(rootView.findViewById(checkedId));
			
			userId = PrefUtils.getFromPrefs(getActivity() , PrefUtils.PREFS_LOGIN_USERNAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);
    		AppSettings appSettings = db.getSettings(userId);
    		
		    	switch (position)
		    	{
//		    	case 0 :
//		    		appSettings.setModeType(Utils.APP_MODE_NORMAL);
//		    		db.updateSettings(appSettings);
//		    		Toast.makeText(getActivity(), Utils.APP_MODE_NORMAL,Toast.LENGTH_SHORT).show();
//		    		break;
                case 0 :
                    appSettings.setModeType(Utils.APP_MODE_QUICK_MEASURE);
                    db.updateSettings(appSettings);
                    Toast.makeText(getActivity(), Utils.APP_MODE_QUICK_MEASURE,Toast.LENGTH_SHORT).show();
                    break;

		    	case 1 :
		    		appSettings.setModeType(Utils.APP_MODE_STREAMLINE);
		    		db.updateSettings(appSettings);
			    	Toast.makeText(getActivity(), Utils.APP_MODE_STREAMLINE,Toast.LENGTH_SHORT).show(); 
			    	break;
		    	}
		}
	});

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


