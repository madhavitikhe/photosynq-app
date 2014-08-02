package com.photosynq.app;

import java.text.SimpleDateFormat;
import java.util.Locale;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.ResearchProject;
import com.photosynq.app.utils.CommonUtils;

public class DirectionsActivity extends ActionBarActivity {

	private String projectId = ""; 
	private boolean quick_measure;
	DatabaseHelper db;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_directions);
		//db = new DatabaseHelper(getApplicationContext());
		db = DatabaseHelper.getHelper(getApplicationContext());
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			projectId = extras.getString(DatabaseHelper.C_PROJECT_ID);
			quick_measure = extras.getBoolean(MainActivity.QUICK_MEASURE);
			System.out.println(this.getClass().getName()+"############quickmeasure="+quick_measure);
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
		//System.out.println("DBCLosing");
		//db.closeDB();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.directions, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
//		int id = item.getItemId();
//		if (id == R.id.action_settings) {
//			return true;
//		}
		return super.onOptionsItemSelected(item);
	}
	
	public void onDirNextClicked(View view)
	{
		Intent intent = new Intent(getApplicationContext(),BluetoothActivity.class);
		intent.putExtra(MainActivity.QUICK_MEASURE, quick_measure);
		intent.putExtra(DatabaseHelper.C_PROJECT_ID, projectId);
		startActivity(intent);
	}
	
}
