package com.photosynq.app;

import java.util.List;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

class BluetoothArrayAdapter extends BaseAdapter implements ListAdapter {

	private final Activity activity;
	private final List<BluetoothDevice> bluetoothDeviceList;

	BluetoothArrayAdapter(Activity activity, List<BluetoothDevice> bluetoothDeviceList) {
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
			convertView = activity.getLayoutInflater().inflate(R.layout.bluetooth_item_card, null);

		TextView tvDeviceName = (TextView) convertView.findViewById(R.id.device_name);
		TextView tvDeviceAddress = (TextView) convertView.findViewById(R.id.device_address);
		TextView tvDevicePaired = (TextView) convertView.findViewById(R.id.device_paired);
		TextView pairDeviceBtn = (TextView) convertView.findViewById(R.id.pair_bluetooth_device);

		BluetoothDevice bluetoothDevice = getItem(position);
		if (null != bluetoothDevice) {
			try {
				tvDeviceName.setText(bluetoothDevice.getName());
				tvDeviceAddress.setText(bluetoothDevice.getAddress());
				tvDevicePaired.setText((bluetoothDevice.getBondState()==10)?"Not Paired":(bluetoothDevice.getBondState()==12)?"Paired":"Pairing");
				if(bluetoothDevice.getBondState() == 10){
					System.out.println("making button visible");
					pairDeviceBtn.setVisibility(View.VISIBLE);
				}else 
				{
					System.out.println("making button invisible");
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
