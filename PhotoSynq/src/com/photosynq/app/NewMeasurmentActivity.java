package com.photosynq.app;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.Question;
import com.photosynq.app.utils.BluetoothService;

public class NewMeasurmentActivity extends ActionBarActivity {

	private DatabaseHelper db;
	private String projectId;
	private String deviceAddress;
	public String option1="";
	public String option3="";
	public String option2="";
	
	public String question1="";
	public String question2="";
	public String question3="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_measurment);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			projectId = extras.getString(DatabaseHelper.C_PROJECT_ID);
			deviceAddress = extras.getString(BluetoothService.DEVICE_ADDRESS);
		}
			db = new DatabaseHelper(getApplicationContext());
			List<Question> questions = db.getAllQuestionForProject(projectId);
			ListView lst = (ListView) findViewById(R.id.measurement_list_view);

			QuestionArrayAdapter questionAdapter = new QuestionArrayAdapter(
					this, questions);

			lst.setAdapter(questionAdapter);
			db.closeDB();
			
			
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
//		if (id == R.id.action_settings) {
//			return true;
//		}
		return super.onOptionsItemSelected(item);
	}
	
	public void takeMeasurement(View view) throws JSONException
	{
		
		
		String options = new String ("\"user_questions\": [\""+question1+"\","+"\""+question2+"\","+"\""+question3+"\" ],\"user_answers\": [\""+option1+"\","+"\""+option2+"\","+"\""+option3+"\" ],");
		Intent intent = new Intent(getApplicationContext(),ResultActivity.class);
		intent.putExtra(DatabaseHelper.C_PROJECT_ID, projectId);
		intent.putExtra(BluetoothService.DEVICE_ADDRESS, deviceAddress);
		intent.putExtra(DatabaseHelper.C_OPTION_TEXT, options);
		startActivity(intent);
		
	}
//	public void displayresult(View view)
//	{
//		Intent intent = new Intent(getApplicationContext(),DisplayResultsActivity.class);
//		startActivity(intent);
//	}

}
