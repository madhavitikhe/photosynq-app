package com.photosynq.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.ResearchProject;
import com.photosynq.app.navigationDrawer.Utils;
import com.photosynq.app.utils.CommonUtils;
import com.photosynq.app.utils.PrefUtils;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class DirectionsActivity extends Activity {

	private String projectId = ""; 
	private String appMode;
	DatabaseHelper db;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_directions);

        CheckBox showDirCheck = (CheckBox)findViewById(R.id.showDirections);
        if(showDirCheck != null) {
            String showDirections = PrefUtils.getFromPrefs(this, PrefUtils.PREFS_SHOW_DIRECTIONS, "YES");
            if (showDirections.equals("YES"))
                showDirCheck.setChecked(false);
            else
                showDirCheck.setChecked(true);
        }

		db = DatabaseHelper.getHelper(getApplicationContext());
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			projectId = extras.getString(DatabaseHelper.C_PROJECT_ID);
			appMode = extras.getString(Utils.APP_MODE);
			System.out.println(this.getClass().getName()+"############app mode="+appMode);
			ResearchProject rp = db.getResearchProject(projectId);
			SimpleDateFormat outputDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
			
			DisplayMetrics displaymetrics = new DisplayMetrics();
			this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
			int screenWidth = displaymetrics.widthPixels;
			//int screenHeight = displaymetrics.heightPixels;
			
			TextView tvProjetTitle = (TextView) findViewById(R.id.project_name);
			TextView tvProjetDirections = (TextView) findViewById(R.id.project_directions);
			TextView tvStartDate = (TextView) findViewById(R.id.start_date);
			TextView tvEndDate = (TextView) findViewById(R.id.end_date);
			TextView tvBeta = (TextView) findViewById(R.id.beta);
			tvProjetTitle.setText(rp.getName());
			tvProjetTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX,(int)(screenWidth*0.06));
			if(!"null".equals(rp.getDirToCollab()))
			{
				tvProjetDirections.setText(rp.getDirToCollab());
			}else{tvProjetDirections.setText(getResources().getString(R.string.no_data_found));}
			
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

		}
	}

	public void onCheckBoxClicked(View view)
	{
        CheckBox checkBox = (CheckBox)view;
        if(checkBox.isChecked())
            PrefUtils.saveToPrefs(getApplicationContext(), PrefUtils.PREFS_SHOW_DIRECTIONS, "NO");
        else
            PrefUtils.saveToPrefs(getApplicationContext(), PrefUtils.PREFS_SHOW_DIRECTIONS, "YES");
	}

    public void onCloseBtnClicked(View view){
        finish();
    }
	
}
