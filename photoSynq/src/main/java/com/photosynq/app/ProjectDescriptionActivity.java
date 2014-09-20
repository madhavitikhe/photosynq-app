package com.photosynq.app;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.ResearchProject;
import com.photosynq.app.navigationDrawer.Utils;
import com.photosynq.app.utils.CommonUtils;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Locale;

@SuppressLint("NewApi")
public class ProjectDescriptionActivity extends Activity {

	private String recordid = ""; 
	private String appMode;
	DatabaseHelper db;
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_project_description);

	    //getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#AA0000")));//it change option bar color.
	    
		db = DatabaseHelper.getHelper(getApplicationContext());
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			recordid = extras.getString(DatabaseHelper.C_ID);
			appMode = extras.getString(Utils.APP_MODE);
			System.out.println(this.getClass().getName()+"############app mode="+appMode);
			ResearchProject rp = db.getResearchProject(recordid);
			
			
			SimpleDateFormat outputDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
			
			DisplayMetrics displaymetrics = new DisplayMetrics();
			this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
			int screenWidth = displaymetrics.widthPixels;
			//int screenHeight = displaymetrics.heightPixels;
			
			TextView tvProjetTitle = (TextView) findViewById(R.id.project_name);
			TextView tvProjetDesc = (TextView) findViewById(R.id.project_desc);
			TextView tvStartDate = (TextView) findViewById(R.id.start_date);
			TextView tvEndDate = (TextView) findViewById(R.id.end_date);
			TextView tvBeta = (TextView) findViewById(R.id.beta);
			
			Button participate = (Button) findViewById(R.id.participate_btn);
			participate.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(getApplicationContext(),DirectionsActivity.class);
					intent.putExtra(Utils.APP_MODE, appMode);
					intent.putExtra(DatabaseHelper.C_PROJECT_ID, recordid);
					startActivity(intent);
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
			ImageView imageview = (ImageView) findViewById(R.id.projectImage); 
			Picasso.with(getApplicationContext()).load(rp.getImageUrl()).into(imageview);

		}
	}

}
