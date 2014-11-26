package com.photosynq.app.navigationDrawer;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.photosynq.app.R;
import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.AppSettings;
import com.photosynq.app.model.ResearchProject;
import com.photosynq.app.utils.DataUtils;
import com.photosynq.app.utils.PrefUtils;

import java.util.List;

public class FragmentProjectList extends Fragment{
	
	ListView projectList;
	TextView selectedProjectText;
	List<ResearchProject> researchProjectList;
	DatabaseHelper db;
	NavigationDrawerResearchProjectArrayAdapter arrayadapter;
	View view = null;
	private ProgressDialog pDialog;
	
    public static FragmentProjectList newInstance() {
        Bundle bundle = new Bundle();
        FragmentProjectList fragment = new FragmentProjectList();
        fragment.setArguments(bundle);
        return fragment;
    }	
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

        ActionBar actionBar = getActivity().getActionBar();
        if(actionBar!=null) {
            actionBar.show();
            actionBar.setTitle(getResources().getString(R.string.title_activity_select_project));
        }

        View rootView = inflater.inflate(R.layout.fragment_project_list, container, false);
		
		projectList = (ListView) rootView.findViewById(R.id.project_list);
		selectedProjectText = (TextView) rootView.findViewById(R.id.selectedProjectText);
		db = DatabaseHelper.getHelper(getActivity());
		researchProjectList = db.getAllResearchProjects();
		arrayadapter = new NavigationDrawerResearchProjectArrayAdapter(getActivity(), researchProjectList); 
		projectList.setAdapter(arrayadapter);
		checkDataOnProjectList();
		
		String userId = PrefUtils.getFromPrefs(getActivity() , PrefUtils.PREFS_LOGIN_USERNAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);
		AppSettings appSettings = db.getSettings(userId);
		if(null != appSettings.getProjectId())
		{
			String projectName = db.getResearchProject(appSettings.getProjectId()).getName();
			if(projectName.length()<50)
			{
				selectedProjectText.setText(projectName.substring(0,(projectName.length()<50?projectName.length():50)));
			}
			else
			{
				selectedProjectText.setText(projectName.substring(0,(projectName.length()<50?projectName.length():50))+"...");
			}
		}
		else
		{
            selectedProjectText.setText(R.string.project_not_selected);
		}
		//Radio button is checked after clicked on Listview item.
		projectList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long itemId) {
				ResearchProject rp = (ResearchProject) projectList.getItemAtPosition(position);
				FragmentManager fragmentManager = getActivity().getFragmentManager();
				Bundle bundle = new Bundle();
				bundle.putString(DatabaseHelper.C_ID, rp.getId());
			    FragmentSelectProject f = new FragmentSelectProject(); 
			    f.setArguments(bundle);
				fragmentManager.beginTransaction().replace(R.id.content_frame,f).commit();

			}
		});
		
		rootView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT ));	
		return rootView;
	}

    /**
     * When project list is empty it's calls async task (GetDataAsync()) and download project list.
     */
	public void checkDataOnProjectList()
	{
		if(arrayadapter.isEmpty())
		{
			new GetDataAsync().execute();
		}
	}

    /**
     * Download list of research project and set to listview.
     */
	private void refreshProjectList() {
		db = DatabaseHelper.getHelper(getActivity());
		researchProjectList = db.getAllResearchProjects();
		arrayadapter = new NavigationDrawerResearchProjectArrayAdapter(getActivity(), researchProjectList); 
		projectList.setAdapter(arrayadapter);
	}
	
	private class GetDataAsync extends AsyncTask<Void, Void, Void> {
		 
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            DataUtils.downloadData(getActivity());
            pDialog = new ProgressDialog(getActivity());
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
         //Toast.makeText(getActivity(), "List is up to date", Toast.LENGTH_SHORT).show();
        }
    }
}
