package com.photosynq.app.navigationDrawer;

import java.util.List;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.photosynq.app.R;
import com.photosynq.app.model.ResearchProject;

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

		TextView tvProjectTitle = (TextView) convertView.findViewById(R.id.nav_project_name);
		//TextView tvProjectDesc = (TextView) convertView.findViewById(R.id.nav_project_desc);
		//final RadioButton projectListRadio = (RadioButton) convertView.findViewById(R.id.nav_radiobtn);

//		tvProjectTitle.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//            	//projectListRadio.setChecked(position == selectedPosition);
//				//projectListRadio.setTag(position);
//				//selectedPosition = (Integer) view.getTag();
//              //  notifyDataSetInvalidated();
//            	projectListRadio.setChecked(true);
//            }
//        });
		
		ResearchProject researchProject = getItem(position);
		if (null != researchProject) {
			@SuppressWarnings("unused")
			String projectDesc,projectName;
			try {
				projectDesc = researchProject.getDescription();
				projectName = researchProject.getName();
				if(projectName.length()<35)
				{
					tvProjectTitle.setText(projectName.substring(0,(projectName.length()<35?projectName.length():35)));
				}
				else
				{
					tvProjectTitle.setText(projectName.substring(0,(projectName.length()<35?projectName.length():35))+"...");
					//tvProjectTitle.setText(projectName);
				}
				
//				projectListRadio.setChecked(position == selectedPosition);
//				projectListRadio.setTag(position);
//				
//				//Radio button is checked after clicked on radio button, single choice mode.
//				projectListRadio.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        selectedPosition = (Integer)view.getTag();
//                        notifyDataSetInvalidated();
//                    }
//                });
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return convertView;
	}

}
