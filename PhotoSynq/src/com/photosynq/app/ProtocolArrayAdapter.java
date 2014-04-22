package com.photosynq.app;

import java.util.List;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.photosynq.app.model.Protocol;

public class ProtocolArrayAdapter extends BaseAdapter implements ListAdapter {

	private final Activity activity;
	private final List<Protocol> protocolList;

	ProtocolArrayAdapter(Activity activity, List<Protocol> protocolList) {
		assert activity != null;
		assert protocolList != null;

		this.protocolList = protocolList;
		this.activity = activity;
	}

	@Override
	public int getCount() {
		if (null == protocolList)
			return 0;
		else
			return protocolList.size();
	}

	@Override
	public Protocol getItem(int position) {
		if (null == protocolList)
			return null;
		else
			return protocolList.get(position);
	}

	@Override
	public long getItemId(int position) {
		//Protocol protocol = getItem(position);

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null)
			convertView = activity.getLayoutInflater().inflate(R.layout.protocol_list_item, null);

		TextView tvProtocolName = (TextView) convertView.findViewById(R.id.protocol_name);
		TextView tvProtocolDesc = (TextView) convertView.findViewById(R.id.protocol_desc);

		Protocol protocol = getItem(position);
		if (null != protocol) {
			String protocolDesc;
			try {
				protocolDesc = protocol.getDescription();
				tvProtocolName.setText(protocol.getName());
				// only show 100 chars of descr.
				tvProtocolDesc.setText(protocolDesc);//.substring(0, (projectDesc.length()<100?projectDesc.length():100))+"...");
				//tvProjectDesc.setText(projectDesc);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return convertView;
	}
}
