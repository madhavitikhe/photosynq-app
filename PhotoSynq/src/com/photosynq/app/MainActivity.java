package com.photosynq.app;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.photosynq.app.HTTP.HTTPConnection;
import com.photosynq.app.HTTP.PhotosynqResponse;
import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.ResearchProject;
import com.photosynq.app.utils.PrefUtils;

public class MainActivity extends ActionBarActivity implements PhotosynqResponse{

	// Database Helper
    DatabaseHelper db;
    
    HTTPConnection mProjListTask = null;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		String authToken = PrefUtils.getFromPrefs(getApplicationContext(), PrefUtils.PREFS_AUTH_TOKEN_KEY, PrefUtils.PREFS_DEFAULT_VAL);
		mProjListTask = new HTTPConnection();
		mProjListTask.delegate = this;
		mProjListTask.execute(HTTPConnection.PHOTOSYNQ_PROJECTS_LIST_URL+authToken, "GET");

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
		
	}
	public void researchNearMe(View view)
	{
		
	}
	public void calibrateIntrument(View view)
	{
		
	}
	@Override
	public void onResponseReceived(String result) {
		JSONArray jArray;
		if(null!= result)
			{
			try {
				Log.d("PHOTOSYNQ-MainActivity", result);
				jArray = new JSONArray(result);
				for (int i = 0; i < jArray.length(); i++) {
					ResearchProject rp = new ResearchProject("DUMMYHASH");
					
					JSONObject obj = jArray.getJSONObject(i);
					rp.setId(obj.getString("id"));
					rp.setName(obj.getString("name"));
					rp.setStart_date(obj.getString("start_date"));
					rp.setEnd_date(obj.getString("end_date"));
					rp.setDir_to_collab(obj.getString("directions_to_collaborators"));
					rp.setDesc(obj.getString("description"));
					rp.setBeta(obj.getString("beta"));
					rp.setImage_content_type(obj.getString("image_content_type"));
					
					db = new DatabaseHelper(getApplicationContext());
					
					db.updateResearchProject(rp);
					db.closeDB();
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
}
