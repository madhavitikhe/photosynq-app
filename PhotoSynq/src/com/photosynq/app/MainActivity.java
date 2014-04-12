package com.photosynq.app;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.ResearchProject;
import com.photosynq.app.utils.CommonUtils;

public class MainActivity extends ActionBarActivity {

	// Database Helper
    DatabaseHelper db;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
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
	
	public void testCreateRecord(View view)
	{
		db = new DatabaseHelper(getApplicationContext());
		ResearchProject rp = new ResearchProject();
	    rp.setId("1");
	    rp.setName("One");
	    rp.setDesc("3");
	    rp.setDir_to_collab("4");
	    rp.setStart_date("5");
	    rp.setEnd_date("6");
	    rp.setImage_content_type("7");
	    rp.setBeta("8");
	    
	    ResearchProject rp1 = new ResearchProject();
	    rp1.setId("1");
	    rp1.setName("two");
	    rp1.setDesc("3");
	    rp1.setDir_to_collab("4");
	    rp1.setStart_date("5");
	    rp1.setEnd_date("6");
	    rp1.setImage_content_type("7");
	    rp1.setBeta("8");
	    
	    ResearchProject rp2 = new ResearchProject();
	    rp2.setId("1");
	    rp2.setName("2");
	    rp2.setDesc("three");
	    rp2.setDir_to_collab("4");
	    rp2.setStart_date("5");
	    rp2.setEnd_date("6");
	    rp2.setImage_content_type("7");
	    rp2.setBeta("8");
	    
	    db.createResearchProject(rp);
	    db.createResearchProject(rp1);
	    db.createResearchProject(rp2);
	    db.closeDB();
	}
	
	public void testFetchRecord(View view)
	{
		db = new DatabaseHelper(getApplicationContext());
		ResearchProject rp = new ResearchProject();
	    rp.setId("1");
	    rp.setName("One");
	    rp.setDesc("3");
	    rp.setDir_to_collab("4");
	    rp.setStart_date("5");
	    rp.setEnd_date("6");
	    rp.setImage_content_type("7");
	    rp.setBeta("8");
	    
	    ResearchProject rpreturn = db.getResearchProject(CommonUtils.getRecordHash(rp));
	    System.out.println("$$$$$$$$$$$$$$$$ "+ rpreturn.name);
	    db.closeDB();
	}
	
	public void testFetchAllRecord(View view)
	{
		db = new DatabaseHelper(getApplicationContext());
		List<ResearchProject> rps = new ArrayList<ResearchProject>();
		rps = db.getAllResearchProjects();
		for (ResearchProject researchProject : rps) {
			System.out.println("########### "+ researchProject.name);
		}
		db.closeDB();
	}
	
	public void testDeleteRecord(View view)
	{
		db = new DatabaseHelper(getApplicationContext());
		
	    ResearchProject rp2 = new ResearchProject();
	    rp2.setId("1");
	    rp2.setName("2");
	    rp2.setDesc("three");
	    rp2.setDir_to_collab("4");
	    rp2.setStart_date("5");
	    rp2.setEnd_date("6");
	    rp2.setImage_content_type("7");
	    rp2.setBeta("8");
	    
	    db.deleteResearchProject(CommonUtils.getRecordHash(rp2));
		db.closeDB();
	}
	
	
	
}
