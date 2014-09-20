package com.photosynq.app.navigationDrawer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.photosynq.app.LoginActivity;
import com.photosynq.app.R;
import com.photosynq.app.utils.PrefUtils;
import com.squareup.picasso.Picasso;

public class FragmentUser extends Fragment{

	private TextView userTextTV,userText,userBioText;
    private ImageView userThumb;
	private String userEmail,userName,userBio,thumbUrl;
    private View rootView;
	int id;
    public static FragmentUser newInstance() {
        Bundle bundle = new Bundle();
        
        FragmentUser fragment = new FragmentUser();
        fragment.setArguments(bundle);

        return fragment;
    }	
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
			
	    rootView = inflater.inflate(R.layout.fragment_user, container, false);
        initValues();

		
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

    private void initValues() {
        userEmail = PrefUtils.getFromPrefs(getActivity(), PrefUtils.PREFS_LOGIN_USERNAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);
        userTextTV = (TextView) rootView.findViewById(R.id.userEmailText);
        userTextTV.setText(userEmail);
        userName = PrefUtils.getFromPrefs(getActivity() , PrefUtils.PREFS_NAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);
        userText = (TextView) rootView.findViewById(R.id.userNameText);
        userText.setText(userName);
        userBio = PrefUtils.getFromPrefs(getActivity() , PrefUtils.PREFS_BIO_KEY, PrefUtils.PREFS_DEFAULT_VAL);
        userBioText = (TextView) rootView.findViewById(R.id.userBioText);
        userBioText.setText(userBio);
        thumbUrl = PrefUtils.getFromPrefs(getActivity() , PrefUtils.PREFS_THUMB_URL_KEY, PrefUtils.PREFS_DEFAULT_VAL);
        userThumb = (ImageView) rootView.findViewById(R.id.userThumb);
        Picasso.with(getActivity()).load(thumbUrl).into(userThumb);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent)
	{
		if (requestCode == 999)
		{
			initValues();
		}
	}
}


