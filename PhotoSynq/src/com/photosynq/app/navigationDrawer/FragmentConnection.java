package com.photosynq.app.navigationDrawer;

import java.util.ArrayList;
import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.photosynq.app.BluetoothArrayAdapter;
import com.photosynq.app.R;

public class FragmentConnection extends Fragment{
	
	private ListView pairedDeviceList;
	private ArrayList<BluetoothDevice> btDeviceList = new ArrayList<BluetoothDevice>();
	private BluetoothAdapter bluetoothAdapter;
	private Button searchNewBtn;
	
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


