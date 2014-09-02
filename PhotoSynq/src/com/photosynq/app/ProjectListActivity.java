package com.photosynq.app;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.photosynq.app.HTTP.HTTPConnection;
import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.ResearchProject;
import com.photosynq.app.navigationDrawer.NavigationDrawer;
import com.photosynq.app.navigationDrawer.Utils;
import com.photosynq.app.utils.DataUtils;
import com.photosynq.app.utils.PrefUtils;

public class ProjectListActivity extends NavigationDrawer  {

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
	private ProgressDialog pDialog;
    String image_url;
    
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_project_list);
		LayoutInflater inflater = (LayoutInflater) this
	            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View contentView = inflater.inflate(R.layout.activity_project_list, null, false);
	    layoutDrawer.addView(contentView, 0); 
		
		// Initialize ListView
		projectList = (ListView) findViewById(R.id.list_view);
		PrefUtils.saveToPrefs(getApplicationContext(), PrefUtils.PREFS_CURRENT_LOCATION, null);
		authToken = PrefUtils.getFromPrefs(getApplicationContext(), PrefUtils.PREFS_AUTH_TOKEN_KEY, PrefUtils.PREFS_DEFAULT_VAL);
		email = PrefUtils.getFromPrefs(getApplicationContext(), PrefUtils.PREFS_LOGIN_USERNAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);
		
		refreshProjectList();
		
		projectList.setOnItemClickListener(new OnItemClickListener() {
		    @Override
		    public void onItemClick(AdapterView<?> adapter, View view, int position, long id){
		    	ResearchProject rp = (ResearchProject) projectList.getItemAtPosition(position);
				Log.d("Getting record id : ", rp.getId());
				Intent intent = new Intent(getApplicationContext(),ProjectDescriptionActivity.class);
				intent.putExtra(Utils.APP_MODE, Utils.APP_MODE_NORMAL);
				intent.putExtra(DatabaseHelper.C_ID, rp.getId());
				startActivity(intent);
		    }
		});
		getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#AA0000")));//it change option bar color.
		checkDataOnList();
	}
	
	public void checkDataOnList()
	{
		if(arrayadapter.isEmpty())
		{
			System.out.println("-------------------arrayadapter.isEmpty()--------------");
			new GetDataAsync().execute();
		}
		else
		{
			System.out.println("-------------------arrayadapter.isNotEmpty()--------------");
		}
	}

	private void refreshProjectList() {
		//db = new DatabaseHelper(getApplicationContext());
		db = DatabaseHelper.getHelper(getApplicationContext());
		researchProjectList = db.getAllResearchProjects();
		arrayadapter = new ResearchProjectArrayAdapter(this, researchProjectList); 
		projectList.setAdapter(arrayadapter);
		//System.out.println("DBCLosing");
		//db.closeDB();
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
			  new GetDataAsync().execute();
			  return true;
		  default:
			  return super.onOptionsItemSelected(item);
		}		
	}
	
	
	private class GetDataAsync extends AsyncTask<Void, Void, Void> {
		 
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            //downloadData();
            DataUtils.downloadData(getApplicationContext());
            pDialog = new ProgressDialog(ProjectListActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }
 
        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
             return null;
        }
 
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            refreshProjectList();
         Toast.makeText(getApplicationContext(), "List is up to date", Toast.LENGTH_SHORT).show();
        }
    }
}
