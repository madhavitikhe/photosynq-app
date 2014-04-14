package com.photosynq.app;

import java.util.List;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.ResearchProject;

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
		db.closeDB();
		
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
}
