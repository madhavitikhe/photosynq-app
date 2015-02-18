package com.photosynq.app;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.ResearchProject;
import com.photosynq.app.utils.CommonUtils;
import com.photosynq.app.utils.PrefUtils;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class DirectionsActivity extends ActionBarActivity {

	private String projectId = ""; 
	DatabaseHelper dbHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_directions);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Directions");

        Typeface typefaceRobotoMedium = CommonUtils.getInstance(this).getFontRobotoMedium();
        Typeface typefaceRobotoRegular = CommonUtils.getInstance(this).getFontRobotoRegular();

        CheckBox showDirCheck = (CheckBox)findViewById(R.id.cb_show_directions);
        showDirCheck.setTypeface(typefaceRobotoMedium);
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
            tvProjetTitle.setTypeface(typefaceRobotoMedium);
			TextView tvProjetDirections = (TextView) findViewById(R.id.tv_project_directions);
            tvProjetDirections.setTypeface(typefaceRobotoRegular);
            TextView tvProjetDirectionsLbl = (TextView) findViewById(R.id.tv_directions_to_collab);
            tvProjetDirectionsLbl.setTypeface(typefaceRobotoMedium);
			TextView tvStartDate = (TextView) findViewById(R.id.tv_start_date);
            tvStartDate.setTypeface(typefaceRobotoRegular);
            TextView tvStartDateLbl = (TextView) findViewById(R.id.start_date_lbl);
            tvStartDateLbl.setTypeface(typefaceRobotoMedium);
			TextView tvEndDate = (TextView) findViewById(R.id.tv_end_date);
            tvEndDate.setTypeface(typefaceRobotoRegular);
            TextView tvEndDateLbl = (TextView) findViewById(R.id.end_date_lbl);
            tvEndDateLbl.setTypeface(typefaceRobotoMedium);
			TextView tvBeta = (TextView) findViewById(R.id.tv_beta);
            tvBeta.setTypeface(typefaceRobotoRegular);
            TextView tvBetaLbl = (TextView) findViewById(R.id.beta_lbl);
            tvBetaLbl.setTypeface(typefaceRobotoMedium);

			tvProjetTitle.setText(researchProject.getName());
			tvProjetTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX,(int)(screenWidth*0.06));
			if(!"null".equals(researchProject.getDirToCollab()))
			{
				tvProjetDirections.setText(Html.fromHtml(researchProject.getDirToCollab()));
			}else{
                tvProjetDirections.setText("");
            }
			
			if(!"null".equals(researchProject.getStartDate()))
			{
				tvStartDate.setText(outputDate.format(CommonUtils.convertToDate(researchProject.getStartDate())));
			}else{
                tvStartDate.setText("null");
            }
			
			if(!"null".equals(researchProject.getEndDate()))
			{
				tvEndDate.setText(outputDate.format(CommonUtils.convertToDate(researchProject.getEndDate())));
			}else{
                tvEndDate.setText("null");
            }
			
			if(!"null".equals(researchProject.getBeta()))
			{
				tvBeta.setText(researchProject.getBeta());
			}else{
                tvBeta.setText("null");
            }

		}
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_display_results, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
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
