package com.photosynq.app.navigationDrawer;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ListView;

import com.photosynq.app.R;
import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.ResearchProject;

public class FragmentProjectList extends Fragment{
	
	ListView projectList;
	List<ResearchProject> researchProjectList;
	DatabaseHelper db;
	NavigationDrawerResearchProjectArrayAdapter arrayadapter;
	
    public static FragmentUser newInstance() {
        Bundle bundle = new Bundle();

        FragmentUser fragment = new FragmentUser();
        fragment.setArguments(bundle);
        
        return fragment;
    }	
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_project_list, container, false);
		
		projectList = (ListView) rootView.findViewById(R.id.project_list);
		db = DatabaseHelper.getHelper(getActivity());
		researchProjectList = db.getAllResearchProjects();
		arrayadapter = new NavigationDrawerResearchProjectArrayAdapter(getActivity(), researchProjectList); 
		projectList.setAdapter(arrayadapter);
		
		rootView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT ));	
		return rootView;
	}
				
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu, inflater);		
		inflater.inflate(R.menu.menu, menu);
	}

}
