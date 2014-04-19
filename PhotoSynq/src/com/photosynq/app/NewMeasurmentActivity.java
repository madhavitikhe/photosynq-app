package com.photosynq.app;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.Question;

public class NewMeasurmentActivity extends ActionBarActivity {

	private DatabaseHelper db;
	private String projectId;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_measurment);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			projectId = extras.getString(DatabaseHelper.C_PROJECT_ID);
		}
		db = new DatabaseHelper(getApplicationContext());
		List<Question> questions = db.getAllQuestionForProject(projectId);
		ListView lst = (ListView) findViewById(R.id.measurement_list_view);
		
		QuestionArrayAdapter questionAdapter = new QuestionArrayAdapter(this, questions );
		
		lst.setAdapter(questionAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_measurment, menu);
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
	
	public void displayresult(View view)
	{
		Intent intent = new Intent(getApplicationContext(),DisplayResultsActivity.class);
		startActivity(intent);
	}

}
