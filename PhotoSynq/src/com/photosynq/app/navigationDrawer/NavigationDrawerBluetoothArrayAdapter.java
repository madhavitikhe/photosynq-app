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
import android.widget.Toast;

import com.photosynq.app.R;
import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.AppSettings;
import com.photosynq.app.utils.PrefUtils;

public class NavigationDrawerBluetoothArrayAdapter extends BaseAdapter implements ListAdapter {
	
	private final Activity activity;
	private final List<BluetoothDevice> bluetoothDeviceList;
	int selectedPosition=0;
	private String userId;
	private DatabaseHelper db;

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
		db = DatabaseHelper.getHelper(convertView.getContext());
		TextView tvDeviceName = (TextView) convertView.findViewById(R.id.deviceName);
		TextView tvDeviceAddress = (TextView) convertView.findViewById(R.id.deviceAddress);
		TextView tvDevicePaired = (TextView) convertView.findViewById(R.id.devicePaired);
		TextView pairDeviceBtn = (TextView) convertView.findViewById(R.id.pairBluetoothDevice);
		RadioButton radiobtn = (RadioButton)convertView.findViewById(R.id.bluetooth_conn_radiobtn);

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
		
		//it retrieve selected radio button value from database and set selected button as previous.
		radiobtn.setChecked(false);
		userId = PrefUtils.getFromPrefs(convertView.getContext() , PrefUtils.PREFS_LOGIN_USERNAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);
		AppSettings appSettings = db.getSettings(userId);
		
		if (null != appSettings.getConnectionID() && appSettings.getConnectionID().equals(bluetoothDevice.getName()))
			{
				RadioButton rb = (RadioButton)convertView.findViewById(R.id.bluetooth_conn_radiobtn);
				rb.setChecked(true);
			}
		return convertView;
	}

}
