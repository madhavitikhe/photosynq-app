package com.photosynq.app;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.photosynq.app.model.Protocol;

import java.util.List;

public class ProtocolArrayAdapter extends BaseAdapter implements ListAdapter {

	//private final Activity activity;
    public final Context context;
	public final List<Protocol> protocolList;
    LayoutInflater mInflater;
    private Typeface robotoRegular;

	public ProtocolArrayAdapter(Context context, List<Protocol> protocolList) {
		//assert activity != null;
        assert context != null;
		assert protocolList != null;

		this.protocolList = protocolList;
        this.context = context;
		//this.activity = activity;
        mInflater = LayoutInflater.from(context);
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
			//convertView = getLayoutInflater().inflate(R.layout.protocol_list_item, null);
        convertView = mInflater.inflate(R.layout.protocol_list_item, null);

        robotoRegular = Typeface.createFromAsset(parent.getContext().getAssets(), "Roboto-Regular.ttf");

		TextView tvProtocolName = (TextView) convertView.findViewById(R.id.protocol_name);
		TextView tvProtocolDesc = (TextView) convertView.findViewById(R.id.protocol_desc);
        tvProtocolName.setTypeface(robotoRegular);
		Protocol protocol = getItem(position);
		if (null != protocol) {
			String protocolDesc;
			try {
				protocolDesc = protocol.getDescription();
				tvProtocolName.setText(protocol.getName());
				// only show 100 chars of descr.
//				tvProtocolDesc.setText(protocolDesc);//.substring(0, (projectDesc.length()<100?projectDesc.length():100))+"...");
				//tvProjectDesc.setText(projectDesc);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return convertView;
	}
}
