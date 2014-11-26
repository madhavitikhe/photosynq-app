package com.photosynq.app.navigationDrawer;

import android.app.ActionBar;
import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.photosynq.app.R;
import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.AppSettings;
import com.photosynq.app.utils.BluetoothService;
import com.photosynq.app.utils.PrefUtils;

public class FragmentMode extends Fragment{

	RadioGroup radioGroup;
	int position;
	int pos1;
	private DatabaseHelper db;
	private String userId;
    String deviceAddress;
	
    public static FragmentMode newInstance() {
        FragmentMode fragment = new FragmentMode();
        return fragment;
    }	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

        ActionBar actionBar = getActivity().getActionBar();
        if(actionBar!=null) {
            actionBar.show();
            actionBar.setTitle(getResources().getString(R.string.title_activity_mode));
        }

        final View rootView = inflater.inflate(R.layout.fragment_mode, container, false);
		db = DatabaseHelper.getHelper(getActivity());
		userId = PrefUtils.getFromPrefs(getActivity() , PrefUtils.PREFS_LOGIN_USERNAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);
		AppSettings appSettings = db.getSettings(userId);

        Bundle extras = getArguments();
        if(extras != null) {
            deviceAddress = extras.getString(BluetoothService.DEVICE_ADDRESS);
        }

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
            FragmentManager fragmentManager = getActivity().getFragmentManager();

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
                    String first_install_cycle = PrefUtils.getFromPrefs(getActivity(), PrefUtils.PREFS_FIRST_INSTALL_CYCLE, "YES");
                    if( first_install_cycle.equals("YES")) {
                        Bundle bundle = new Bundle();
                        bundle.putString(BluetoothService.DEVICE_ADDRESS, deviceAddress);
                        FragmentSelectProtocol fragment=new FragmentSelectProtocol();
                        fragment.setArguments(bundle);

                        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

                        PrefUtils.saveToPrefs(getActivity(), PrefUtils.PREFS_FIRST_INSTALL_CYCLE,"NO");
                    }
                    break;

		    	case 1 :
		    		appSettings.setModeType(Utils.APP_MODE_STREAMLINE);
		    		db.updateSettings(appSettings);
                    String first_install = PrefUtils.getFromPrefs(getActivity(), PrefUtils.PREFS_FIRST_INSTALL_CYCLE, "YES");
                    if( first_install.equals("YES")) {
                        fragmentManager.beginTransaction().replace(R.id.content_frame, new FragmentProjectList()).commit();
                    }
			    	break;
		    	}
		}
	});

        RadioButton quickMeasureRadio = (RadioButton) rootView.findViewById(R.id.quick_measure_mode_radio);
        String getQuickMeasureText = quickMeasureRadio.getText().toString();
        Spannable Passspan1 = new SpannableString(getQuickMeasureText);
        Passspan1.setSpan(new RelativeSizeSpan(1.85f), 0, 17, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        quickMeasureRadio.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        quickMeasureRadio.setText(Passspan1);

        RadioButton ProjectSelectedRadio = (RadioButton) rootView.findViewById(R.id.streamline_mode_radio);
        String getProSelectedText = ProjectSelectedRadio.getText().toString();
        Spannable Passspan2 = new SpannableString(getProSelectedText);
        Passspan2.setSpan(new RelativeSizeSpan(1.85f), 0, 21, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ProjectSelectedRadio.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        ProjectSelectedRadio.setText(Passspan2);

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


