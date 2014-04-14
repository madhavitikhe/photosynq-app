package com.photosynq.app;

import java.util.List;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.photosynq.app.model.ResearchProject;

class ResearchProjectArrayAdapter extends BaseAdapter implements ListAdapter {

	private final Activity activity;
	private final List<ResearchProject> researchProjectList;

	ResearchProjectArrayAdapter(Activity activity, List<ResearchProject> researchProjectList) {
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
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null)
			convertView = activity.getLayoutInflater().inflate(R.layout.list_item_card, null);

		TextView tvProjectTitle = (TextView) convertView.findViewById(R.id.project_name);
		TextView tvProjectDesc = (TextView) convertView.findViewById(R.id.project_desc);

		ResearchProject researchProject = getItem(position);
		if (null != researchProject) {
			String projectDesc;
			try {
				projectDesc = researchProject.getDesc();
				tvProjectTitle.setText(researchProject.getName());
				// only show 100 chars of descr.
				tvProjectDesc.setText(projectDesc.substring(0, (projectDesc.length()<100?projectDesc.length():100))+"...");
				//tvProjectDesc.setText(projectDesc);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return convertView;
	}
}
