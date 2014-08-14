package com.photosynq.app.navigationDrawer;

import java.util.List;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.photosynq.app.R;

public class NavigationDrawerBluetoothArrayAdapter extends BaseAdapter implements ListAdapter {
	
	private final Activity activity;
	private final List<BluetoothDevice> bluetoothDeviceList;
	int selectedPosition=0;

	public NavigationDrawerBluetoothArrayAdapter(Activity activity, List<BluetoothDevice> bluetoothDeviceList) {
		assert activity != null;
		assert bluetoothDeviceList != null;

		this.bluetoothDeviceList = bluetoothDeviceList;
		this.activity = activity;
	}

	@Override
	public int getCount() {
		if (null == bluetoothDeviceList)
		{
			return 0;
		}
		else
			
		{
			return bluetoothDeviceList.size();
		}
	}

	@Override
	public BluetoothDevice getItem(int position) {
		if (null == bluetoothDeviceList)
			return null;
		else
			return bluetoothDeviceList.get(position);
	}

	@Override
	public long getItemId(int position) {
//		BluetoothDevice bluetoothDevice = getItem(position);

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null)
			convertView = activity.getLayoutInflater().inflate(R.layout.nav_drawer_bluetooth_item_card, null);

		TextView tvDeviceName = (TextView) convertView.findViewById(R.id.deviceName);
		TextView tvDeviceAddress = (TextView) convertView.findViewById(R.id.deviceAddress);
		TextView tvDevicePaired = (TextView) convertView.findViewById(R.id.devicePaired);
		TextView pairDeviceBtn = (TextView) convertView.findViewById(R.id.pairBluetoothDevice);
		RadioButton radiobtn = (RadioButton)convertView.findViewById(R.id.radiobtn);

		BluetoothDevice bluetoothDevice = getItem(position);
		if (null != bluetoothDevice) {
			try {
				tvDeviceName.setText(bluetoothDevice.getName());
				tvDeviceAddress.setText(bluetoothDevice.getAddress());
				tvDevicePaired.setText((bluetoothDevice.getBondState()==10)?"Not Paired":(bluetoothDevice.getBondState()==12)?"Paired":"Pairing");
				radiobtn.setChecked(position == selectedPosition);
				radiobtn.setTag(position);
				radiobtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectedPosition = (Integer)view.getTag();
                        notifyDataSetInvalidated();
                    }
                });
				if(bluetoothDevice.getBondState() == 10){
					pairDeviceBtn.setVisibility(View.VISIBLE);
					radiobtn.setVisibility(View.INVISIBLE);
				}else 
				{
					pairDeviceBtn.setVisibility(View.INVISIBLE);
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return convertView;
	}

}
