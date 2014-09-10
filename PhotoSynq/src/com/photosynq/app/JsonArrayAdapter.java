package com.photosynq.app;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

class JsonArrayAdapter extends BaseAdapter implements ListAdapter {

	private final Activity activity;
	private final JSONArray jsonArray;

	JsonArrayAdapter(Activity activity, JSONArray jsonArray) {
		assert activity != null;
		assert jsonArray != null;

		this.jsonArray = jsonArray;
		this.activity = activity;
	}

	@Override
	public int getCount() {
		if (null == jsonArray)
			return 0;
		else
			return jsonArray.length();
	}

	@Override
	public JSONObject getItem(int position) {
		if (null == jsonArray)
			return null;
		else
			return jsonArray.optJSONObject(position);
	}

	@Override
	public long getItemId(int position) {
		JSONObject jsonObject = getItem(position);

		return jsonObject.optLong("id");
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null)
			convertView = activity.getLayoutInflater().inflate(R.layout.project_list_item_card, null);

		TextView tvProjectTitle = (TextView) convertView.findViewById(R.id.project_name);
		TextView tvProjectDesc = (TextView) convertView.findViewById(R.id.project_desc);

		JSONObject json_data = getItem(position);
		if (null != json_data) {
			String projectTitle;
			String projectDesc;
			try {
				projectTitle = json_data.getString("name");
				projectDesc = json_data.getString("description");
				tvProjectTitle.setText(projectTitle);
				//tvProjectDesc.setText(projectDesc.substring(0, (projectDesc.length()<40?projectDesc.length():40)));
				tvProjectDesc.setText(projectDesc);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return convertView;
	}
}
