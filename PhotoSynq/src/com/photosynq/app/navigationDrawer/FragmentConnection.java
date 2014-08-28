package com.photosynq.app.navigationDrawer;

import java.util.ArrayList;
import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.photosynq.app.R;
import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.AppSettings;
import com.photosynq.app.utils.PrefUtils;

public class FragmentConnection extends Fragment{
	
	private ListView pairedDeviceList;
	private ArrayList<BluetoothDevice> btDeviceList = new ArrayList<BluetoothDevice>();
	private BluetoothAdapter bluetoothAdapter;
	private Button searchNewBtn;
	String loggedInUserName;
	private String userId;
	private DatabaseHelper db;
	
    public static FragmentConnection newInstance() {
        Bundle bundle = new Bundle();

        FragmentConnection fragment = new FragmentConnection();
        fragment.setArguments(bundle);

        return fragment;
    }	
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
			
		View rootView = inflater.inflate(R.layout.fragment_connection, container, false);
		
		db = DatabaseHelper.getHelper(getActivity());
		//String loggedInUserName = PrefUtils.getFromPrefs(getActivity(), PrefUtils.PREFS_USER,null);
		pairedDeviceList = (ListView) rootView.findViewById(R.id.pairedDevices);
		searchNewBtn = (Button) rootView.findViewById(R.id.searchNewButton);
		searchNewBtn.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		        Toast.makeText(getActivity(), "Clicked on Search New Devices", Toast.LENGTH_LONG).show();
		        btDeviceList.clear();
		        searchNewBTDevice();
		    }
		});
		
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		Set<BluetoothDevice> btDevices =  bluetoothAdapter.getBondedDevices();
		for (BluetoothDevice device : btDevices) {
			btDeviceList.add(device);
		}
		
		NavigationDrawerBluetoothArrayAdapter btArrayAdapter = new NavigationDrawerBluetoothArrayAdapter(getActivity(), btDeviceList);
		pairedDeviceList.setAdapter(btArrayAdapter);
		
		
		
		pairedDeviceList.setOnItemClickListener(new OnItemClickListener() {
		    @Override
		    public void onItemClick(AdapterView<?> adapter, View view, int position, long id){
		    	BluetoothDevice btDevice = (BluetoothDevice) pairedDeviceList.getItemAtPosition(position);
				Log.d("Pairing device : ", btDevice.getName());
				
				userId = PrefUtils.getFromPrefs(getActivity() , PrefUtils.PREFS_LOGIN_USERNAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);
	    		AppSettings appSettings = db.getSettings(userId);
				
				String bluetoothID = btDevice.getName();
				
				appSettings.setConnectionID(bluetoothID);
				db.updateSettings(appSettings);
				
				//String bluetoothAddress = adapter.getItemAtPosition(position).toString();
				PrefUtils.saveToPrefs(getActivity(), PrefUtils.PREFS_CONNECTION_ID,bluetoothID);
				Toast.makeText(getActivity(), "Clicked "+bluetoothID, Toast.LENGTH_SHORT).show();
				pairedDeviceList.setItemsCanFocus(true);
				RadioButton radiolistitem=(RadioButton) view.findViewById(R.id.bluetooth_conn_radiobtn);
				radiolistitem.performClick();
		    }
		});
		
		rootView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT ));		
		return rootView;
	}
	
	private void searchNewBTDevice() {
		// Check for Bluetooth support and then check to make sure it is turned
		// on
		// If it isn't request to turn it on
		// List paired devices
		// Emulator doesn't support Bluetooth and will return null
		if (bluetoothAdapter == null) {
			System.out.println("\nBluetooth NOT supported. Aborting.");
			return;
		} else {
			if (bluetoothAdapter.isEnabled()) {
				
				System.out.println("\nBluetooth is enabled...");

				// Starting the device discovery
				bluetoothAdapter.startDiscovery();
			} else {
				Intent enableBtIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, 1);
			}
		}
	}
				
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu, inflater);		
		inflater.inflate(R.menu.menu, menu);
	}
}


