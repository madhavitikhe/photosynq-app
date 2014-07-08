package com.photosynq.app;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.photosynq.app.HTTP.HTTPConnection;
import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.ProjectResult;
import com.photosynq.app.model.ResearchProject;
import com.photosynq.app.utils.CommonUtils;
import com.photosynq.app.utils.PrefUtils;

public class ProjectListActivity extends ActionBarActivity  {

	ListView projectList;
	DatabaseHelper db;
	HTTPConnection mProjListTask = null;
    HTTPConnection mProtocolListTask = null;
    HTTPConnection mMacroListTask = null;
    HTTPConnection mUpdateDataTask = null;
    String email;
    String authToken;
    List<ResearchProject> researchProjectList;
    ResearchProjectArrayAdapter arrayadapter;
    private View mProListStatusView;
	private TextView mProListStatusMessageView;
    
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_project_list);
		
		// Initialize ListView
		projectList = (ListView) findViewById(R.id.list_view);
		mProListStatusView = findViewById(R.id.prolist_status);
		mProListStatusMessageView = (TextView) findViewById(R.id.prolist_status_message);
		PrefUtils.saveToPrefs(getApplicationContext(), PrefUtils.PREFS_CURRENT_LOCATION, null);
		authToken = PrefUtils.getFromPrefs(getApplicationContext(), PrefUtils.PREFS_AUTH_TOKEN_KEY, PrefUtils.PREFS_DEFAULT_VAL);
		email = PrefUtils.getFromPrefs(getApplicationContext(), PrefUtils.PREFS_LOGIN_USERNAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);
		
		db = new DatabaseHelper(getApplicationContext());
		researchProjectList = db.getAllResearchProjects();
		arrayadapter = new ResearchProjectArrayAdapter(this, researchProjectList); 
		projectList.setAdapter(arrayadapter);
		System.out.println("DBCLosing");
		db.closeDB();
		
		projectList.setOnItemClickListener(new OnItemClickListener() {
		    @Override
		    public void onItemClick(AdapterView<?> adapter, View view, int position, long id){
		    	ResearchProject rp = (ResearchProject) projectList.getItemAtPosition(position);
				Log.d("Getting record id : ", rp.getId());
				Intent intent = new Intent(getApplicationContext(),ProjectDescriptionActivity.class);
				intent.putExtra(MainActivity.QUICK_MEASURE, false);
				intent.putExtra(DatabaseHelper.C_ID, rp.getId());
				startActivity(intent);
		    }
		});
		getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#AA0000")));//it change option bar color.
		//checkDataOnList();
	}

	public void checkDataOnList()
	{
		if(researchProjectList.isEmpty())
		{
			showProgress(true);
			downloadData();
		}
		else
		{
			Toast.makeText(getApplicationContext(), "List is Up to Date", 5).show();
			//showProgress(false);
			//downloadData();
			//projectList.setVisibility(View.VISIBLE);
		}
	}
	@SuppressLint("NewApi")
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mProListStatusView.setVisibility(View.VISIBLE);
			mProListStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mProListStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mProListStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
		}
	}
	
	public void downloadData()
	{
		if(CommonUtils.isConnected(getApplicationContext()))
		{
			UpdateResearchProjects updateProjects = new UpdateResearchProjects(getApplicationContext());
			mProjListTask = new HTTPConnection();
			mProjListTask.delegate = updateProjects;
			mProjListTask.execute(HTTPConnection.PHOTOSYNQ_PROJECTS_LIST_URL+ "user_email="+email+"&user_token="+authToken, "GET");
			
			UpdateProtocol upprotoProtocol = new UpdateProtocol(getApplicationContext());
			mProtocolListTask = new HTTPConnection();
			mProtocolListTask.delegate = upprotoProtocol;
			mProtocolListTask.execute(HTTPConnection.PHOTOSYNQ_PROTOCOLS_LIST_URL+ "user_email="+email+"&user_token="+authToken, "GET");
			
	
			UpdateMacro updateMacro = new UpdateMacro(getApplicationContext());
			mMacroListTask = new HTTPConnection();
			mMacroListTask.delegate = updateMacro;
			mMacroListTask.execute(HTTPConnection.PHOTOSYNQ_MACROS_LIST_URL+ "user_email="+email+"&user_token="+authToken, "GET");
			
			db = new DatabaseHelper(getApplicationContext());
			List<ProjectResult> listRecords =  db.getAllUnUploadedResults();
			db.closeDB();
			for (ProjectResult projectResult : listRecords) {
				StringEntity input = null;
				JSONObject request_data = new JSONObject();	
				
				try {
						JSONObject jo = new JSONObject(projectResult.getReading());
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
				
				UpdateData updateData = new UpdateData(getApplicationContext(),projectResult.getId());
				mUpdateDataTask = new HTTPConnection(input);
				mUpdateDataTask.delegate = updateData;
				mUpdateDataTask.execute(HTTPConnection.PHOTOSYNQ_DATA_URL+projectResult.getProjectId()+"/data.json", "POST");
		   }
		}
		db = new DatabaseHelper(getApplicationContext());
		researchProjectList = db.getAllResearchProjects();
		arrayadapter = new ResearchProjectArrayAdapter(this, researchProjectList); 
		projectList.setAdapter(arrayadapter);
		System.out.println("DBCLosing");
		db.closeDB();
		projectList.setVisibility(View.VISIBLE);
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
//		int id = item.getItemId();
//		if (id == R.id.action_settings) {
//			return true;
//		}
		/**
		 * After Clicking Refresh Option Menu Button, Research Project List Get Refresh, And You Will Get Updated list. 
		 */
		switch (item.getItemId()) {
		  case R.id.refresh:
			  if(researchProjectList.isEmpty())
				{
		           Toast.makeText(getApplicationContext(), "Loading.......", 5).show();
		           //downloadData();
			       showProgress(false);
			       projectList.setVisibility(View.VISIBLE);
				}
			  else
			  {
				  Toast.makeText(getApplicationContext(), "Refreshing......", 5).show();
				  projectList.setVisibility(View.GONE);
				  showProgress(true);
				//  downloadData();
			  }
		          
        	return true;
		default:
			return super.onOptionsItemSelected(item);
  }
}
}
