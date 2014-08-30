package com.photosynq.app.navigationDrawer;

import java.text.SimpleDateFormat;
import java.util.Locale;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.photosynq.app.R;
import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.AppSettings;
import com.photosynq.app.model.ResearchProject;
import com.photosynq.app.utils.CommonUtils;
import com.photosynq.app.utils.PrefUtils;
import com.squareup.picasso.Picasso;

public class FragmentSelectProject extends Fragment{
	
	private String recordid = ""; 
	DatabaseHelper db;
    public static FragmentSelectProject newInstance() {
        Bundle bundle = new Bundle();

        FragmentSelectProject fragment = new FragmentSelectProject();
        fragment.setArguments(bundle);

        return fragment;
    }	
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
			
		View rootView = inflater.inflate(R.layout.activity_project_description, container, false);
		
		
		db = DatabaseHelper.getHelper(getActivity());
		Bundle extras = getArguments();
		if (extras != null) {
			recordid = extras.getString(DatabaseHelper.C_ID);
			ResearchProject rp = db.getResearchProject(recordid);

			SimpleDateFormat outputDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
			
			DisplayMetrics displaymetrics = new DisplayMetrics();
			getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
			int screenWidth = displaymetrics.widthPixels;
			//int screenHeight = displaymetrics.heightPixels;
			
			TextView tvProjetTitle = (TextView) rootView.findViewById(R.id.project_name);
			TextView tvProjetDesc = (TextView) rootView.findViewById(R.id.project_desc);
			TextView tvStartDate = (TextView) rootView.findViewById(R.id.start_date);
			TextView tvEndDate = (TextView) rootView.findViewById(R.id.end_date);
			TextView tvBeta = (TextView) rootView.findViewById(R.id.beta);
			Button selectprojectBtn = (Button) rootView.findViewById(R.id.participate_btn);
			selectprojectBtn.setText("Select Project");
			selectprojectBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					FragmentManager fm = getActivity().getSupportFragmentManager();
					
					String userId = PrefUtils.getFromPrefs(getActivity() , PrefUtils.PREFS_LOGIN_USERNAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);
		    		AppSettings appSettings = db.getSettings(userId);

					appSettings.setProjectId(recordid);
					db.updateSettings(appSettings);
					fm.beginTransaction().replace(R.id.content_frame, new FragmentProjectList()).commit();	
				}
			});
			
			tvProjetTitle.setText(rp.getName());
			tvProjetTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX,(int)(screenWidth*0.06));
			if(!"null".equals(rp.getDescription()))
			{
				tvProjetDesc.setText(rp.getDescription());
			}else{tvProjetDesc.setText(getResources().getString(R.string.no_data_found));}
			
			if(!"null".equals(rp.getStartDate()))
			{
				tvStartDate.setText(outputDate.format(CommonUtils.convertToDate(rp.getStartDate())));
			}else{tvStartDate.setText(getResources().getString(R.string.no_data_found));}
			
			if(!"null".equals(rp.getEndDate()))
			{
				tvEndDate.setText(outputDate.format(CommonUtils.convertToDate(rp.getEndDate())));
			}else{tvEndDate.setText(getResources().getString(R.string.no_data_found));}
			
			if(!	"null".equals(rp.getBeta()))
			{
				tvBeta.setText(rp.getBeta());
			}else{tvBeta.setText(getResources().getString(R.string.no_data_found));}
			ImageView imageview = (ImageView) rootView.findViewById(R.id.projectImage); 
			Picasso.with(getActivity()).load(rp.getImageUrl()).into(imageview);
			
		}
		rootView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT ));		
		return rootView;
	}
}
