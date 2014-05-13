package com.photosynq.app;


import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.Protocol;
import com.photosynq.app.model.ResearchProject;
import com.photosynq.app.utils.CommonUtils;
import com.squareup.picasso.Picasso;

public class ProjectDescriptionActivity extends ActionBarActivity {

	private String recordid = ""; 
	private boolean quick_measure;
	DatabaseHelper db;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_project_description);
		db = new DatabaseHelper(getApplicationContext());
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			recordid = extras.getString(DatabaseHelper.C_ID);
			quick_measure = extras.getBoolean(MainActivity.QUICK_MEASURE);
			System.out.println(this.getClass().getName()+"############quickmeasure="+quick_measure);
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
			try {
				StringBuffer dataString = new StringBuffer();
				
				String[] projectProtocols = rp.getProtocols_ids().split(",");
				if(rp.getProtocols_ids().length() >=1)
				{
					JSONArray protocolJsonArray = new JSONArray();
					for (String protocolId : projectProtocols) {
						Protocol protocol = db.getProtocol(protocolId);
						JSONObject protocolObject = new JSONObject();
						protocolObject.put("protocolid", protocol.getId());
						protocolObject.put("macroid", protocol.getMacroId());
						protocolJsonArray.put(protocolObject);
					}
					
					dataString.append("var protocols=" + protocolJsonArray.toString());
					CommonUtils.writeStringToFile(getApplicationContext(), "macros_variable.js",dataString.toString());
				}
				else
				{
					Toast.makeText(getApplicationContext(), "No protocols assigned to this project, cannot continue.", Toast.LENGTH_SHORT).show();
					finish();
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		// Writing macros_variable.js file with protocol and macro relations
		
		System.out.println("DBCLosing");
		db.closeDB();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.project_description, menu);
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
	
	public void onParticipateClicked(View view)
	{
		Intent intent = new Intent(getApplicationContext(),DirectionsActivity.class);
		intent.putExtra(MainActivity.QUICK_MEASURE, quick_measure);
		intent.putExtra(DatabaseHelper.C_PROJECT_ID, recordid);
		startActivity(intent);
	}

}
