package com.photosynq.app;

import org.json.JSONArray;
import org.json.JSONException;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.photosynq.app.HTTP.HTTPConnection;
import com.photosynq.app.HTTP.PhotosynqResponse;
import com.photosynq.app.utils.PrefUtils;

public class ProjectListActivity extends ActionBarActivity implements PhotosynqResponse {

	ListView lstTest;
    JsonArrayAdapter jSONAdapter ;
    HTTPConnection mProjTask = null;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_project_list);

		String authToken = PrefUtils.getFromPrefs(getApplicationContext(), PrefUtils.PREFS_AUTH_TOKEN_KEY, PrefUtils.PREFS_DEFAULT_VAL);
		mProjTask = new HTTPConnection();
		mProjTask.delegate = this;
		mProjTask.execute(HTTPConnection.PHOTOSYNQ_PROJECTS_LIST_URL+authToken, "GET");
		
		// Initialize ListView
		lstTest = (ListView) findViewById(R.id.list_view);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.project_list, menu);
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

	@Override
	public void onResponseReceived(String result) {
		JSONArray jArray;
		if(null!= result)
			{
			try {
				Log.d("PHOTOSYNQ-ProjectListActivity", result);
				jArray = new JSONArray(result);
				jSONAdapter = new JsonArrayAdapter(ProjectListActivity.this, jArray);
				// Set the above adapter as the adapter of choice for our list
				lstTest.addHeaderView(new View(getApplicationContext()));
				lstTest.addFooterView(new View(getApplicationContext()));
				lstTest.setAdapter(jSONAdapter);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
