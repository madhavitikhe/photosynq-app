package com.photosynq.app.navigationDrawer;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import com.photosynq.app.utils.PrefUtils;

public class FragmentProjectList extends Fragment{
	
	ListView projectList;
	TextView selectedProjectText;
	List<ResearchProject> researchProjectList;
	DatabaseHelper db;
	NavigationDrawerResearchProjectArrayAdapter arrayadapter;
	View view = null;
	
    public static FragmentProjectList newInstance() {
        Bundle bundle = new Bundle();
        FragmentProjectList fragment = new FragmentProjectList();
        fragment.setArguments(bundle);
        return fragment;
    }	
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.fragment_project_list, container, false);
		
		projectList = (ListView) rootView.findViewById(R.id.project_list);
		selectedProjectText = (TextView) rootView.findViewById(R.id.selectedProText);
		db = DatabaseHelper.getHelper(getActivity());
		researchProjectList = db.getAllResearchProjects();
		arrayadapter = new NavigationDrawerResearchProjectArrayAdapter(getActivity(), researchProjectList); 
		projectList.setAdapter(arrayadapter);
		String userId = PrefUtils.getFromPrefs(getActivity() , PrefUtils.PREFS_LOGIN_USERNAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);
		AppSettings appSettings = db.getSettings(userId);
		String projectName = db.getResearchProject(appSettings.getProjectId()).getName();
		if(projectName.length()<50)
		{
			selectedProjectText.setText(projectName.substring(0,(projectName.length()<50?projectName.length():50)));
		}
		else
		{
			selectedProjectText.setText(projectName.substring(0,(projectName.length()<50?projectName.length():50))+"...");
		}
		
		//Radio button is checked after clicked on Listview item.
		projectList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long itemId) {
				ResearchProject rp = (ResearchProject) projectList.getItemAtPosition(position);
				FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
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
}
