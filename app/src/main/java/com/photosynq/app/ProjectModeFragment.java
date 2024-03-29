package com.photosynq.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.http.PhotosynqResponse;
import com.photosynq.app.model.ProjectCreator;
import com.photosynq.app.model.ResearchProject;
import com.photosynq.app.utils.CommonUtils;
import com.photosynq.app.utils.Constants;
import com.photosynq.app.utils.PrefUtils;
import com.photosynq.app.utils.SyncHandler;
import com.squareup.picasso.Picasso;

import java.util.List;


public class ProjectModeFragment extends Fragment implements PhotosynqResponse{

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static int mSectionNumber;

    private DatabaseHelper dbHelper;
    private ProjectArrayAdapter arrayAdapter;
    private ListView projectList;
    private static String mSearchString;
    private String pCreatorId;
    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ProjectModeFragment newInstance(int sectionNumber) {
        ProjectModeFragment fragment = new ProjectModeFragment();
        mSectionNumber = sectionNumber;
        mSearchString = "";
        return fragment;
    }

    public static ProjectModeFragment newInstance(int sectionNumber, String searchString) {
        ProjectModeFragment fragment = new ProjectModeFragment();
        mSectionNumber = sectionNumber;
        mSearchString = searchString;
        return fragment;
    }

    public ProjectModeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView;

        if(mSectionNumber == 0) {
            rootView = inflater.inflate(R.layout.fragment_discover_project_mode, container, false);
        }else{
            rootView = inflater.inflate(R.layout.fragment_my_project_mode, container, false);
        }

        dbHelper = DatabaseHelper.getHelper(getActivity());
        pCreatorId = PrefUtils.getFromPrefs(getActivity(), PrefUtils.PREFS_CREATOR_ID, PrefUtils.PREFS_DEFAULT_VAL);


        // Initialize ListView
        projectList = (ListView) rootView.findViewById(R.id.lv_project);
        showProjectList();

        if(arrayAdapter.isEmpty())
        {
            if(mSearchString.length() == 0) {
                MainActivity mainActivity = (MainActivity) getActivity();
                SyncHandler syncHandler = new SyncHandler(mainActivity);
                syncHandler.DoSync(SyncHandler.PROJECT_LIST_MODE);
            }
        }

        projectList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                ResearchProject project = (ResearchProject) projectList.getItemAtPosition(position);
                Intent intent = new Intent(getActivity(), ProjectDetailsActivity.class);
                intent.putExtra(DatabaseHelper.C_PROJECT_ID, project.getId());
                startActivityForResult(intent, 555);

            }
        });


        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == 555) {

            ((MainActivity) getActivity()).openDrawer();

        }
    }

    private void showProjectList() {
        List<ResearchProject> projects = null;
        if(mSearchString.length() > 0) {
            if(mSectionNumber == 0) {
                projects = dbHelper.getAllResearchProjects(mSearchString);
            }else{
                projects = dbHelper.getUserCreatedContributedProjects(mSearchString, pCreatorId);
            }
            if(projects == null  || projects.isEmpty()){
                Toast.makeText(getActivity(), "No project found", Toast.LENGTH_LONG).show();
            }
        }else{
            if(mSectionNumber == 0) {
                projects = dbHelper.getAllResearchProjects();
            }else{
                projects = dbHelper.getUserCreatedContributedProjects(pCreatorId);
            }
        }

        arrayAdapter = new ProjectArrayAdapter(getActivity(), projects);
        projectList.setAdapter(arrayAdapter);
    }

    /**
     * Download list of research project and set to listview.
     */
    private void refreshProjectList() {
        dbHelper = DatabaseHelper.getHelper(getActivity());
        List<ResearchProject> projects;
        if(mSearchString.length() > 0) {
            if(mSectionNumber == 0) {
                projects = dbHelper.getAllResearchProjects(mSearchString);
            }else{
                projects = dbHelper.getUserCreatedContributedProjects(mSearchString, pCreatorId);
            }
            if(projects.isEmpty()){
                Toast.makeText(getActivity(), "No project found", Toast.LENGTH_LONG).show();
            }
        }else{
            if(mSectionNumber == 0) {
                projects = dbHelper.getAllResearchProjects();
            }else{
                projects = dbHelper.getUserCreatedContributedProjects(pCreatorId);
            }
        }
        arrayAdapter = new ProjectArrayAdapter(getActivity(), projects);
        projectList.setAdapter(arrayAdapter);
    }

    @Override
    public void onResponseReceived(String result) {

        if(result.equals(Constants.SERVER_NOT_ACCESSIBLE)){
            Toast.makeText(getActivity(), R.string.server_not_reachable, Toast.LENGTH_LONG).show();
        }else {
            refreshProjectList();
        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(mSectionNumber);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private class ProjectArrayAdapter extends BaseAdapter implements ListAdapter {

        public final Context context;
        public final List<ResearchProject> projectList;
        LayoutInflater mInflater;

        public ProjectArrayAdapter(Context context, List<ResearchProject> projectList) {
            assert context != null;
            assert projectList != null;

            this.projectList = projectList;
            this.context = context;
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            if (null == projectList)
                return 0;
            else
                return projectList.size();
        }

        @Override
        public ResearchProject getItem(int position) {
            if (null == projectList)
                return null;
            else
                return projectList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = mInflater.inflate(R.layout.project_list_item, null);

            TextView tvProjectName = (TextView) convertView.findViewById(R.id.tv_project_name);
            tvProjectName.setTypeface(CommonUtils.getInstance(getActivity()).getFontRobotoRegular());
            TextView tvProjectBy = (TextView) convertView.findViewById(R.id.tv_project_by);
            tvProjectBy.setTypeface(CommonUtils.getInstance(getActivity()).getFontRobotoRegular());
//            TextView tvLastCont = (TextView) convertView.findViewById(R.id.tv_last_contribution);
//            tvLastCont.setTypeface(CommonUtils.getInstance(getActivity()).getFontRobotoRegular());

            ResearchProject project = getItem(position);
            if (null != project) {
                try {
                    tvProjectName.setText(project.getName());

                    ProjectCreator projectCreator = dbHelper.getProjectLead(project.getCreatorId());
                    if(null != projectCreator)
                        tvProjectBy.setText("by " + projectCreator.getName());

                    ImageView imageview = (ImageView) convertView.findViewById(R.id.im_projectImage);
                    Picasso.with(getActivity()).load(project.getImageUrl()).into(imageview);
                    Picasso.with(getActivity())
                            .load(project.getImageUrl())
                            .error(R.drawable.ic_launcher1)
                            .into(imageview);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return convertView;
        }
    }
}