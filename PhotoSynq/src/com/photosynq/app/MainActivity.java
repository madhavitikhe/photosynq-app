package com.photosynq.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.photosynq.app.HTTP.HTTPConnection;
import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.utils.PrefUtils;

public class MainActivity extends ActionBarActivity {

	// Database Helper
    DatabaseHelper db;
    
    HTTPConnection mProjListTask = null;
    HTTPConnection mProtocolListTask = null;
    HTTPConnection mMacroListTask = null;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		String authToken = PrefUtils.getFromPrefs(getApplicationContext(), PrefUtils.PREFS_AUTH_TOKEN_KEY, PrefUtils.PREFS_DEFAULT_VAL);
		String email = PrefUtils.getFromPrefs(getApplicationContext(), PrefUtils.PREFS_LOGIN_USERNAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);
		UpdateResearchProjects updateProjects = new UpdateResearchProjects(getApplicationContext());
		mProjListTask = new HTTPConnection();
		mProjListTask.delegate = updateProjects;
		mProjListTask.execute(HTTPConnection.PHOTOSYNQ_PROJECTS_LIST_URL+ "user_email="+email+"&user_token="+authToken, "GET");
		
//		UpdateProtocol upprotoProtocol = new UpdateProtocol(getApplicationContext());
//		mProtocolListTask = new HTTPConnection();
//		mProtocolListTask.delegate = upprotoProtocol;
//		mProtocolListTask.execute(HTTPConnection.PHOTOSYNQ_PROTOCOLS_LIST_URL+ "user_email="+email+"&user_token="+authToken, "GET");
//		
//
//		UpdateMacro updateMacro = new UpdateMacro(getApplicationContext());
//		mMacroListTask = new HTTPConnection();
//		mMacroListTask.delegate = updateMacro;
//		mMacroListTask.execute(HTTPConnection.PHOTOSYNQ_MACROS_LIST_URL+ "user_email="+email+"&user_token="+authToken, "GET");

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
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void listResearchProjects(View view)
	{
		Intent intent = new Intent(getApplicationContext(),ProjectListActivity.class);
		startActivity(intent);
	}
	public void recentResearchCollab(View view)
	{
//		Intent intent = new Intent(getApplicationContext(),BluetoothActivity.class);
//		startActivity(intent);

		
	}
	public void researchNearMe(View view)
	{
		
	}
	public void calibrateIntrument(View view)
	{
		
	}
	
	
	
}
