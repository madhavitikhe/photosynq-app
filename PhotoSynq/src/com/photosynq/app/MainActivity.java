package com.photosynq.app;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.photosynq.app.HTTP.HTTPConnection;
import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.ProjectResult;
import com.photosynq.app.utils.CommonUtils;
import com.photosynq.app.utils.PrefUtils;

public class MainActivity extends ActionBarActivity {

	protected String latitude,longitude; 
	protected boolean gps_enabled,network_enabled;
	// Database Helper
    public static final String QUICK_MEASURE ="quick";
    HTTPConnection mProjListTask = null;
    HTTPConnection mProtocolListTask = null;
    HTTPConnection mMacroListTask = null;
    HTTPConnection mUpdateDataTask = null;
    DatabaseHelper db;
    String authToken;
    String email;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//reset location.
		PrefUtils.saveToPrefs(getApplicationContext(), PrefUtils.PREFS_CURRENT_LOCATION, null);
		 authToken = PrefUtils.getFromPrefs(getApplicationContext(), PrefUtils.PREFS_AUTH_TOKEN_KEY, PrefUtils.PREFS_DEFAULT_VAL);
		 email = PrefUtils.getFromPrefs(getApplicationContext(), PrefUtils.PREFS_LOGIN_USERNAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);
		
	}
	
	public void download()
	{
		if(CommonUtils.isConnected(getApplicationContext()))
		{
			UpdateResearchProjects updateProjects = new UpdateResearchProjects(getApplicationContext());
			mProjListTask = new HTTPConnection();
			mProjListTask.delegate = updateProjects;
			mProjListTask.execute(HTTPConnection.PHOTOSYNQ_PROJECTS_LIST_URL+ "user_email="+email+"&user_token="+authToken, "GET");
			
			UpdateProtocol upprotoProtocol = new UpdateProtocol(getApplicationContext());
			mProtocolListTask = new HTTPConnection();
			mProtocolListTask.delegate = upprotoProtocol;
			mProtocolListTask.execute(HTTPConnection.PHOTOSYNQ_PROTOCOLS_LIST_URL+ "user_email="+email+"&user_token="+authToken, "GET");
			
	
			UpdateMacro updateMacro = new UpdateMacro(getApplicationContext());
			mMacroListTask = new HTTPConnection();
			mMacroListTask.delegate = updateMacro;
			mMacroListTask.execute(HTTPConnection.PHOTOSYNQ_MACROS_LIST_URL+ "user_email="+email+"&user_token="+authToken, "GET");
			
			db = new DatabaseHelper(getApplicationContext());
			List<ProjectResult> listRecords =  db.getAllUnUploadedResults();
			db.closeDB();
			for (ProjectResult projectResult : listRecords) {
				StringEntity input = null;
				JSONObject request_data = new JSONObject();	
				
				try {
						JSONObject jo = new JSONObject(projectResult.getReading());
						request_data.put("user_email", email);
						request_data.put("user_token", authToken);
						request_data.put("data", jo);
						 input = new StringEntity(request_data.toString());
						input.setContentType("application/json");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				UpdateData updateData = new UpdateData(getApplicationContext(),this,projectResult.getId());
				mUpdateDataTask = new HTTPConnection(input);
				mUpdateDataTask.delegate = updateData;
				mUpdateDataTask.execute(HTTPConnection.PHOTOSYNQ_DATA_URL+projectResult.getProjectId()+"/data.json", "POST");

		}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
	
	public void listResearchProjects(View view)
	{
		Intent intent = new Intent(getApplicationContext(),ProjectListActivity.class);
		intent.putExtra(QUICK_MEASURE, false);
		startActivity(intent);
	}
	public void recentResearchCollab(View view)
	{
//		Intent intent = new Intent(getApplicationContext(),BluetoothActivity.class);
//		startActivity(intent);

		
	}
	public void researchNearMe(View view)
	{
		Intent intent = new Intent(getApplicationContext(),DisplayResultsActivity.class);
		intent.putExtra(DatabaseHelper.C_PROJECT_ID, "3");
		startActivity(intent);
	}
	public void calibrateIntrument(View view)
	{
		Intent intent = new Intent(getApplicationContext(),SelectProtocolActivity.class);
		startActivity(intent);
	}
	
	public void quickMeasurement(View view)
	{
		Intent intent = new Intent(getApplicationContext(),BluetoothActivity.class);
		intent.putExtra(QUICK_MEASURE, true);
		startActivity(intent);
	}
}
