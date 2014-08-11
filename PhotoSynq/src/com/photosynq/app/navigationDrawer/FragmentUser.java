package com.photosynq.app.navigationDrawer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

import com.photosynq.app.LoginActivity;
import com.photosynq.app.R;

public class FragmentUser extends Fragment{

	private TextView txtFragmenttwo;
	
    public static FragmentUser newInstance() {
        Bundle bundle = new Bundle();

        FragmentUser fragment = new FragmentUser();
        fragment.setArguments(bundle);

        return fragment;
    }	
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
			
		View rootView = inflater.inflate(R.layout.fragment_user, container, false);
		
		txtFragmenttwo = (TextView) rootView.findViewById(R.id.txtFragmentTwo);
		txtFragmenttwo.setText(R.string.fragment_tab_two);
		
		rootView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT ));	
		 Button button = (Button) rootView.findViewById(R.id.button1);
			   button.setOnClickListener(new OnClickListener()
			   {
			             @Override
			             public void onClick(View v)
			             {
			            	 SharedPreferences settings =  PreferenceManager.getDefaultSharedPreferences(getActivity());                          
			    		     SharedPreferences.Editor editor = settings.edit();
			    		     editor.clear();
			    		     editor.commit();
			            	 Intent intent = new Intent(getActivity(),LoginActivity.class);
			            	 startActivity(intent);
			             } 
			   }); 
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


