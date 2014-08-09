package com.photosynq.app;

import java.io.File;
import java.io.UnsupportedEncodingException;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.photosynq.app.HTTP.HTTPConnection;
import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.ProjectResult;
import com.photosynq.app.utils.CommonUtils;
import com.photosynq.app.utils.PrefUtils;

public class DisplayResultsActivity extends ActionBarActivity {

	WebView webview;
	private String projectId;
	private String reading;
	private DatabaseHelper db;
	private HTTPConnection mDataTask = null;
	private String protocolJson="";
	private boolean quick_measure;
	Button keep;
	Button discard;
	//AlertDialog.Builder alertDialogBuilder;
	final Context context = this;
	//AlertDialog alert;
	@SuppressLint({ "SetJavaScriptEnabled", "NewApi" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_results);
		getActionBar().hide();
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			projectId = extras.getString(DatabaseHelper.C_PROJECT_ID);
			reading = extras.getString(DatabaseHelper.C_READING);
			protocolJson = extras.getString(DatabaseHelper.C_PROTOCOL_JSON);
			quick_measure = extras.getBoolean(MainActivity.QUICK_MEASURE);
			System.out.println(this.getClass().getName()+"############quickmeasure="+quick_measure);
		}	
		 keep = (Button)findViewById(R.id.keep_btn);
		 discard = (Button)findViewById(R.id.discard_btn);
		
		if(quick_measure)
		{
			keep.setText("Return to Quick Measurement");
			keep.setVisibility(View.VISIBLE);
			discard.setVisibility(View.INVISIBLE);
		}
		reloadWebview();
	}

	@Override
	protected void onResume() {
		reloadWebview();
		super.onResume();
	}
	@Override
	protected void onRestart() {
		reloadWebview();
		super.onRestart();
	}
	@Override
	protected void onStart() {
		reloadWebview();
		super.onStart();
	}
	
	private void  reloadWebview()
	{
		webview = (WebView) findViewById(R.id.webView1);
		webview.clearCache(true); // Clear cache. This Mandates to load webview fresh. This is required because we are dynamically writing javascript files.
		String url = "file:///" + this.getExternalFilesDir(null)+ File.separator+"cellphone.html";
		webview.loadUrl(url);
		//webview.loadUrl("file:///android_asset/cellphone.html");
		webview.getSettings().setJavaScriptEnabled(true);

	}
	public void keep_click(View view) throws UnsupportedEncodingException, JSONException {
		
		if(quick_measure)
		{
			finish();
		}
		if (CommonUtils.isConnected(getApplicationContext()))
		{
			String authToken = PrefUtils.getFromPrefs(getApplicationContext(), PrefUtils.PREFS_AUTH_TOKEN_KEY, PrefUtils.PREFS_DEFAULT_VAL);
			String email = PrefUtils.getFromPrefs(getApplicationContext(), PrefUtils.PREFS_LOGIN_USERNAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);
			StringEntity input = null;
			//System.out.println(reading);
			
			JSONObject request_data = new JSONObject();
			JSONObject jo = new JSONObject(reading);
			try {
					request_data.put("user_email", email);
					request_data.put("user_token", authToken);
					request_data.put("data", jo);
					 input = new StringEntity(request_data.toString());
					input.setContentType("application/json");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			UpdateData updateData = new UpdateData(getApplicationContext(),this, "NONE");
			mDataTask = new HTTPConnection(input);
			mDataTask.delegate = updateData;
			mDataTask.execute(context,HTTPConnection.PHOTOSYNQ_DATA_URL+projectId+"/data.json", "POST");
			view.setVisibility(View.INVISIBLE);
			discard.setVisibility(View.INVISIBLE);
		}else
		{
		
		
		//db = new DatabaseHelper(getApplicationContext());
			db = DatabaseHelper.getHelper(getApplicationContext());
		ProjectResult result = new ProjectResult(projectId, reading, "N");
		db.createResult(result);
		//db.closeDB();
		
//		alertDialogBuilder = new AlertDialog.Builder(context);
//		alertDialogBuilder.setTitle(R.string.no_internet_connection);
//		alertDialogBuilder.setMessage(R.string.error_sending_data);
//		alertDialogBuilder.setPositiveButton(R.string.ok,  new DialogInterface.OnClickListener() {
//		 public void onClick(DialogInterface dialog, int which) {
//			        	finish();
//			 }
//	      });
//		alert = alertDialogBuilder.create();
//        alert.show();
		Toast.makeText(context, R.string.no_internet_connection, Toast.LENGTH_LONG).show();
		Toast.makeText(context, R.string.error_sending_data, Toast.LENGTH_LONG).show();
		discard.setVisibility(View.INVISIBLE);
		view.setVisibility(View.INVISIBLE); 
		finish();
		}
	}
	
	public void discard_click(View view) {
//		alertDialogBuilder = new AlertDialog.Builder(context);
//		alertDialogBuilder.setTitle(R.string.discard);
//		alertDialogBuilder.setMessage(R.string.result_discarded);
//		alertDialogBuilder.setPositiveButton(R.string.ok,  new DialogInterface.OnClickListener() {
//	        public void onClick(DialogInterface dialog, int which) {
//	        	finish();
//	          }
//	      });
//		alert = alertDialogBuilder.create();
//		alert.show();
		Toast.makeText(context, R.string.result_discarded, Toast.LENGTH_LONG).show();
		view.setVisibility(View.INVISIBLE); 
		keep.setVisibility(View.INVISIBLE);
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		if ( !(protocolJson.length() > 0))
		getMenuInflater().inflate(R.menu.display_results, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_different_device) {
			 Intent openMainActivity= new Intent(this, BluetoothActivity.class);
		     openMainActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		     startActivity(openMainActivity);
			return true;
		}
		if (id == R.id.action_criteria) {
			 Intent openMainActivity= new Intent(this, DirectionsActivity.class);
		     openMainActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		     startActivity(openMainActivity);
			
			return true;
		}
		if (id == R.id.action_protocol_desc) {
			return true;
		}
		if (id == R.id.action_back_to_projects) {
			Intent intent = new Intent(this, ProjectListActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(intent);
			
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
