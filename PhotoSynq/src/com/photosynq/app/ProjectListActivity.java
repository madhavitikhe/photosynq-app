package com.photosynq.app;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.ResearchProject;

public class ProjectListActivity extends ActionBarActivity  {

	ListView projectList;
	DatabaseHelper db;
	
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_project_list);
		
		// Initialize ListView
		projectList = (ListView) findViewById(R.id.list_view);
		
		
		db = new DatabaseHelper(getApplicationContext());
		List<ResearchProject> researchProjectList = db.getAllResearchProjects();
		ResearchProjectArrayAdapter arrayadapter = new ResearchProjectArrayAdapter(this, researchProjectList); 
		projectList.setAdapter(arrayadapter);
		System.out.println("DBCLosing");
		db.closeDB();
		
		projectList.setOnItemClickListener(new OnItemClickListener() {
		    @Override
		    public void onItemClick(AdapterView<?> adapter, View view, int position, long id){
		    	ResearchProject rp = (ResearchProject) projectList.getItemAtPosition(position);
				Log.d("GEtting record id : ", rp.getId());
				Intent intent = new Intent(getApplicationContext(),ProjectDescriptionActivity.class);
				intent.putExtra(DatabaseHelper.C_ID, rp.getId());
				startActivity(intent);
		    }
		});
		
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
//		if (id == R.id.action_settings) {
//			return true;
//		}
		return super.onOptionsItemSelected(item);
	}
}
