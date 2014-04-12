package com.photosynq.app;

import java.util.ArrayList;
import java.util.List;

import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.ResearchProject;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ProjectListActivity extends ActionBarActivity  {

	ListView lstTest;
	DatabaseHelper db;
	
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_project_list);
		
		// Initialize ListView
		lstTest = (ListView) findViewById(R.id.list_view);
		
		
		db = new DatabaseHelper(getApplicationContext());
		List<ResearchProject> researchProjectList = db.getAllResearchProjects();
		ResearchProjectArrayAdapter arrayadapter = new ResearchProjectArrayAdapter(this, researchProjectList); 
		lstTest.setAdapter(arrayadapter);
		
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

//	@Override
//	public void onResponseReceived(String result) {
//		JSONArray jArray;
//		if(null!= result)
//			{
//			try {
//				Log.d("PHOTOSYNQ-ProjectListActivity", result);
//				jArray = new JSONArray(result);
//				jSONAdapter = new JsonArrayAdapter(ProjectListActivity.this, jArray);
//				// Set the above adapter as the adapter of choice for our list
//				lstTest.addHeaderView(new View(getApplicationContext()));
//				lstTest.addFooterView(new View(getApplicationContext()));
//				lstTest.setAdapter(jSONAdapter);
//			} catch (JSONException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//	}

}
