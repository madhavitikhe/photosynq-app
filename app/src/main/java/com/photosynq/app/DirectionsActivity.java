package com.photosynq.app;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.ResearchProject;
import com.photosynq.app.utils.CommonUtils;
import com.photosynq.app.utils.PrefUtils;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class DirectionsActivity extends Activity {

	private String projectId = ""; 
	DatabaseHelper dbHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_directions);

        CheckBox showDirCheck = (CheckBox)findViewById(R.id.cb_show_directions);
        if(showDirCheck != null) {
            String showDirections = PrefUtils.getFromPrefs(this, PrefUtils.PREFS_SHOW_DIRECTIONS, "YES");
            if (showDirections.equals("YES"))
                showDirCheck.setChecked(false);
            else
                showDirCheck.setChecked(true);
        }

		dbHelper = DatabaseHelper.getHelper(getApplicationContext());
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			projectId = extras.getString(DatabaseHelper.C_PROJECT_ID);
			ResearchProject researchProject = dbHelper.getResearchProject(projectId);
			SimpleDateFormat outputDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
			
			DisplayMetrics displaymetrics = new DisplayMetrics();
			this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
			int screenWidth = displaymetrics.widthPixels;

			TextView tvProjetTitle = (TextView) findViewById(R.id.tv_project_name);
			TextView tvProjetDirections = (TextView) findViewById(R.id.tv_project_directions);
			TextView tvStartDate = (TextView) findViewById(R.id.tv_start_date);
			TextView tvEndDate = (TextView) findViewById(R.id.tv_end_date);
			TextView tvBeta = (TextView) findViewById(R.id.tv_beta);
			tvProjetTitle.setText(researchProject.getName());
			tvProjetTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX,(int)(screenWidth*0.06));
			if(!"null".equals(researchProject.getDirToCollab()))
			{
				tvProjetDirections.setText(researchProject.getDirToCollab());
			}else{
                tvProjetDirections.setText("");
            }
			
			if(!"null".equals(researchProject.getStartDate()))
			{
				tvStartDate.setText(outputDate.format(CommonUtils.convertToDate(researchProject.getStartDate())));
			}else{
                tvStartDate.setText("");
            }
			
			if(!"null".equals(researchProject.getEndDate()))
			{
				tvEndDate.setText(outputDate.format(CommonUtils.convertToDate(researchProject.getEndDate())));
			}else{
                tvEndDate.setText("");
            }
			
			if(!	"null".equals(researchProject.getBeta()))
			{
				tvBeta.setText(researchProject.getBeta());
			}else{
                tvBeta.setText("");
            }

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
