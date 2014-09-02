package com.photosynq.app.navigationDrawer;

import android.content.Intent;
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
import android.widget.TextView;

import com.photosynq.app.LoginActivity;
import com.photosynq.app.R;
import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.utils.PrefUtils;

public class FragmentUser extends Fragment{

	private TextView userText;
	private String mEmail;
	int id;
	String modeType, userName, connectionID, projectID;
	private DatabaseHelper db;
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
		db = DatabaseHelper.getHelper(getActivity());
		mEmail = PrefUtils.getFromPrefs(getActivity() , PrefUtils.PREFS_LOGIN_USERNAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);
		PrefUtils.saveToPrefs(getActivity(), PrefUtils.PREFS_USER,mEmail);
		
		userText = (TextView) rootView.findViewById(R.id.userNameText);
		userText.setText(mEmail);
		
		rootView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT ));	
		Button changeUserButton = (Button) rootView.findViewById(R.id.changeUser);
			   changeUserButton.setOnClickListener(new OnClickListener()
			   {
			             @Override
			             public void onClick(View v)
			             {
			            	 Intent intent = new Intent(getActivity(),LoginActivity.class);
			            	 intent.putExtra("change_user", true);
			            	 startActivityForResult(intent, 999);
			             } 
			   }); 
		return rootView;
	}
				
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) 
	{
		if (requestCode == 999)
		{
			mEmail = PrefUtils.getFromPrefs(getActivity() , PrefUtils.PREFS_LOGIN_USERNAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);
			userText.setText(mEmail);
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


