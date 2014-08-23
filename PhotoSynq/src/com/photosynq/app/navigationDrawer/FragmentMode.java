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
import com.photosynq.app.utils.PrefUtils;

public class FragmentMode extends Fragment{

	RadioGroup radioGroup;
	int position;
	int pos1;
	RadioButton rb;
    public static FragmentMode newInstance() {
        FragmentMode fragment = new FragmentMode();
        return fragment;
    }	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
			
		final View rootView = inflater.inflate(R.layout.fragment_mode, container, false);
		
		radioGroup = (RadioGroup) rootView.findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			
			position = radioGroup.indexOfChild(rootView.findViewById(checkedId));
			PrefUtils.saveToPrefs(getActivity(), PrefUtils.PREFS_MODE_TYPE,""+position);
		    	switch (position)
		    	{
		    	case 0 :
		    		Toast.makeText(getActivity(), "Normal Mode"+position,Toast.LENGTH_SHORT).show();	
		    		//send arguments to FragmentReview screen.
//		    		FragmentReview fragment = new FragmentReview();
//		    		Bundle bundle = new Bundle();
//		    		bundle.putInt("MODE", 1);
//		    		fragment.setArguments(bundle);
		    		break;
		    	case 1 :
			    	Toast.makeText(getActivity(), "Streamlined Mode"+position,Toast.LENGTH_SHORT).show(); 
			    	break;
		    	}
		}
	});

//		radioGroup = (RadioGroup) rootView.findViewById(R.id.radioGroup);
//		position = radioGroup.getCheckedRadioButtonId();
//        rb = (RadioButton) rootView.findViewById(position);
//        String selectedValue = rb.getText().toString();
//        Toast.makeText(getActivity(), rb.getText(), Toast.LENGTH_SHORT).show();



		
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


