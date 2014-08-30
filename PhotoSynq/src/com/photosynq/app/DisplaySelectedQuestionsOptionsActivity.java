package com.photosynq.app;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.navigationDrawer.NavigationDrawer;
import com.photosynq.app.utils.BluetoothService;

public class DisplaySelectedQuestionsOptionsActivity extends NavigationDrawer {

	ArrayList<CharSequence> getAllSelectedOptions = new ArrayList<CharSequence>();
	ArrayList<CharSequence> getAllSelectedQuestions = new ArrayList<CharSequence>();
	TextView questionText,optionText;
	int size;
	String question_text;
	LinearLayout liLayout;
	private String projectId;
	private String deviceAddress;
	private String protocolJson="";
	private DatabaseHelper db;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_display_selected_questions_options);
		LayoutInflater inflater = (LayoutInflater) this
	            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View contentView = inflater.inflate(R.layout.activity_display_selected_questions_options, null, false);
	    layoutDrawer.addView(contentView, 0); 
	    db = DatabaseHelper.getHelper(getApplicationContext());
	    Bundle extras = getIntent().getExtras();
		if (extras != null) {
			projectId = extras.getString(DatabaseHelper.C_PROJECT_ID);
			deviceAddress = extras.getString(BluetoothService.DEVICE_ADDRESS);
			protocolJson = extras.getString(DatabaseHelper.C_PROTOCOL_JSON);
			if (null == protocolJson) protocolJson="";
		}
		liLayout = (LinearLayout) findViewById(R.id.linearlayoutoptions);
		getAllSelectedOptions = extras.getCharSequenceArrayList("All_Options");
		getAllSelectedQuestions = extras.getCharSequenceArrayList("All_Questions");
		System.out.println("--getsize-------"+ getAllSelectedOptions.size());

		for (int i = 0; i < getAllSelectedQuestions.size(); i++) {
			System.out.println("--getQuestion-------"+ getAllSelectedQuestions.get(i));
			System.out.println("--getOption-------"+ getAllSelectedOptions.get(i));
		    final TextView que = new TextView(this);
		    que.setText("Question -  " + getAllSelectedQuestions.get(i));
		    liLayout.addView(que);
		    final TextView opt = new TextView(this);
		    opt.setText("Option -  " + getAllSelectedOptions.get(i));
		    liLayout.addView(opt);
		}
	}
	
	public void measure(View view)
	{
		Intent intent = new Intent(getApplicationContext(),DisplayResultsActivity.class);
 		intent.putExtra(DatabaseHelper.C_PROJECT_ID, projectId);
 		intent.putExtra(DatabaseHelper.C_PROTOCOL_JSON, protocolJson);
 		System.out.println("---------DatabaseHelper.C_PROJECT_ID-------------      "+DatabaseHelper.C_PROJECT_ID);
 		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_selected_questions_options,
				menu);
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
