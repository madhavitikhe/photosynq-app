package com.photosynq.app.navigationDrawer;

import java.util.ArrayList;
import java.util.Set;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
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
import android.widget.TextView;
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
	private String userId;
	private DatabaseHelper db;
	private TextView bluetoothStatusMsg;
	private TextView selectedConnectionText;
	private View bluetoothStatus;
    private String bluetoothID;
	private String bluetoothName;
    private AppSettings appSettings;

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
		userId = PrefUtils.getFromPrefs(getActivity() , PrefUtils.PREFS_LOGIN_USERNAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);
		//String loggedInUserName = PrefUtils.getFromPrefs(getActivity(), PrefUtils.PREFS_USER,null);
		pairedDeviceList = (ListView) rootView.findViewById(R.id.pairedDevices);
		selectedConnectionText = (TextView) rootView.findViewById(R.id.selectedConnectionText);
		bluetoothStatus = rootView.findViewById(R.id.btooth_status);
		bluetoothStatusMsg = (TextView) rootView.findViewById(R.id.bluetooth_status_msg);
		searchNewBtn = (Button) rootView.findViewById(R.id.searchNewButton);
		
		searchNewBtn.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		        Toast.makeText(getActivity(), "Clicked on Search New Devices", Toast.LENGTH_LONG).show();
		        btDeviceList.clear();
		        searchNewBTDevice();
		    }
		});

        appSettings = db.getSettings(userId);

		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
	    filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
	    filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
	    getActivity().registerReceiver(ActionFoundReceiver, filter); 
		
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		Set<BluetoothDevice> btDevices =  bluetoothAdapter.getBondedDevices();
        String deviceName = getResources().getString(R.string.no_data_found);
		for (BluetoothDevice device : btDevices) {
			btDeviceList.add(device);
            if(appSettings.getConnectionId().equals(device.getAddress()))
            {
                selectedConnectionText.setText(device.getName());
            }
		}
		
		NavigationDrawerBluetoothArrayAdapter btArrayAdapter = new NavigationDrawerBluetoothArrayAdapter(getActivity(), btDeviceList);
		pairedDeviceList.setAdapter(btArrayAdapter);
		
		
		
		pairedDeviceList.setOnItemClickListener(new OnItemClickListener() {
		    @Override
		    public void onItemClick(AdapterView<?> adapter, View view, int position, long id){
		    	BluetoothDevice btDevice = (BluetoothDevice) pairedDeviceList.getItemAtPosition(position);
				Log.d("Pairing device : ", btDevice.getName());
				bluetoothID = btDevice.getAddress();
				appSettings.setConnectionId(bluetoothID);
				db.updateSettings(appSettings);

				if(null != appSettings.getConnectionId())
				{
					selectedConnectionText.setText(btDevice.getName());
				}

				PrefUtils.saveToPrefs(getActivity(), PrefUtils.PREFS_CONNECTION_ID,bluetoothName);
				pairedDeviceList.setItemsCanFocus(true);
				RadioButton radiolistitem=(RadioButton) view.findViewById(R.id.bluetooth_conn_radiobtn);
				radiolistitem.performClick();
		    }
		});
		
		rootView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT ));		
		return rootView;
	}
	
	public void searchNewConnection(View view)
	{
		btDeviceList.clear();
		searchNewBTDevice();
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
				Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, 1);
			}
		}
	}
	
	private final BroadcastReceiver ActionFoundReceiver = new BroadcastReceiver(){
	    
	    @Override
	    public void onReceive(Context context, Intent intent) {
	     String action = intent.getAction();
	     if(BluetoothDevice.ACTION_FOUND.equals(action)) {
	       BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	       btDeviceList.add(device);
	     } else {
	         if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
	        	 bluetoothStatusMsg.setText(R.string.searching_devices);
	        	 showProgress(true);
	         }
	         else {
	           if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
	        	   showProgress(false);
					Set<BluetoothDevice> btDevices =  bluetoothAdapter.getBondedDevices();
					for (BluetoothDevice device : btDevices) {
						btDeviceList.add(device);
					}
	         }
	       }
	      }
	    }
	  };
	  
	  @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
		private void showProgress(final boolean show) {
			// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
			// for very easy animations. If available, use these APIs to fade-in
			// the progress spinner.
			
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
				int shortAnimTime = getResources().getInteger(
						android.R.integer.config_shortAnimTime);

				bluetoothStatus.setVisibility(View.VISIBLE);
				bluetoothStatus.animate().setDuration(shortAnimTime)
						.alpha(show ? 1 : 0)
						.setListener(new AnimatorListenerAdapter() {
							@Override
							public void onAnimationEnd(Animator animation) {
								bluetoothStatus.setVisibility(show ? View.VISIBLE
										: View.GONE);
							}
						});

				pairedDeviceList.setVisibility(View.VISIBLE);
				pairedDeviceList.animate().setDuration(shortAnimTime)
						.alpha(show ? 0 : 1)
						.setListener(new AnimatorListenerAdapter() {
							@Override
							public void onAnimationEnd(Animator animation) {
								pairedDeviceList.setVisibility(show ? View.GONE
										: View.VISIBLE);
							}
						});
			} else {
				// The ViewPropertyAnimator APIs are not available, so simply show
				// and hide the relevant UI components.
				bluetoothStatus.setVisibility(show ? View.VISIBLE : View.GONE);
				pairedDeviceList.setVisibility(show ? View.GONE : View.VISIBLE);
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


