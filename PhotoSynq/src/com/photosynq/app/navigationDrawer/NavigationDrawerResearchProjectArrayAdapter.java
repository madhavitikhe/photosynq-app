package com.photosynq.app.navigationDrawer;

import java.util.List;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.photosynq.app.R;
import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.AppSettings;
import com.photosynq.app.model.ResearchProject;
import com.photosynq.app.utils.PrefUtils;

public class NavigationDrawerResearchProjectArrayAdapter extends BaseAdapter implements ListAdapter {
	
	private final Activity activity;
	private final List<ResearchProject> researchProjectList;
	int selectedPosition = 0;

	public NavigationDrawerResearchProjectArrayAdapter(Activity activity, List<ResearchProject> researchProjectList) {
		assert activity != null;
		assert researchProjectList != null;

		this.researchProjectList = researchProjectList;
		this.activity = activity;
	}

	@Override
	public int getCount() {
		if (null == researchProjectList)
			return 0;
		else
			return researchProjectList.size();
	}

	@Override
	public ResearchProject getItem(int position) {
		if (null == researchProjectList)
			return null;
		else
			return researchProjectList.get(position);
	}

	@Override
	public long getItemId(int position) {
		ResearchProject researchProject = getItem(position);
		return Long.parseLong(researchProject.getId());
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null)
			convertView = activity.getLayoutInflater().inflate(R.layout.nav_drawer_project_list_item_card, null);

		LinearLayout ll = (LinearLayout) convertView.findViewById(R.id.linLayout);
		String userId = PrefUtils.getFromPrefs(convertView.getContext() , PrefUtils.PREFS_LOGIN_USERNAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);
		DatabaseHelper db = DatabaseHelper.getHelper(convertView.getContext());
		AppSettings appSettings = db.getSettings(userId);
		
		
		TextView tvProjectTitle = (TextView) convertView.findViewById(R.id.nav_project_name);
		ResearchProject researchProject = getItem(position);
		//change background color of item card.
		if(researchProject.getId().equals(appSettings.getProjectID()))
		{
			ll.setBackgroundColor(convertView.getContext().getResources().getColor(R.color.green_google_play));
			tvProjectTitle.setTextColor(convertView.getContext().getResources().getColor(R.color.black));
		}
		else
		{
			ll.setBackgroundColor(convertView.getContext().getResources().getColor(R.color.white));
		}
		if (null != researchProject) {
			String projectName;
			try {
				projectName = researchProject.getName();
				if(projectName.length()<35)
				{
					tvProjectTitle.setText(projectName.substring(0,(projectName.length()<35?projectName.length():35)));
				}
				else
				{
					tvProjectTitle.setText(projectName.substring(0,(projectName.length()<35?projectName.length():35))+"...");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return convertView;
	}

}
