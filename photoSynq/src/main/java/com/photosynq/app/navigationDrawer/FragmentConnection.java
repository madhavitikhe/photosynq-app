package com.photosynq.app.navigationDrawer;

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
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.photosynq.app.R;
import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.AppSettings;
import com.photosynq.app.utils.BluetoothService;
import com.photosynq.app.utils.PrefUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

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
    private AppSettings appSettings;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
			
		View rootView = inflater.inflate(R.layout.fragment_connection, container, false);
		
		db = DatabaseHelper.getHelper(getActivity());
		userId = PrefUtils.getFromPrefs(getActivity() , PrefUtils.PREFS_LOGIN_USERNAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);
		pairedDeviceList = (ListView) rootView.findViewById(R.id.pairedDevices);
		selectedConnectionText = (TextView) rootView.findViewById(R.id.selectedConnectionText);
		bluetoothStatus = rootView.findViewById(R.id.btooth_status);
		bluetoothStatusMsg = (TextView) rootView.findViewById(R.id.bluetooth_status_msg);
		searchNewBtn = (Button) rootView.findViewById(R.id.searchNewButton);

		searchNewBtn.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
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
        selectedConnectionText.setText(R.string.no_hardware_selection);
		for (BluetoothDevice device : btDevices) {
			btDeviceList.add(device);
            if(null != appSettings.getConnectionId() && appSettings.getConnectionId().equals(device.getAddress()))
            {
                selectedConnectionText.setText(device.getName());
            }
		}
		
		NavigationDrawerBluetoothArrayAdapter btArrayAdapter = new NavigationDrawerBluetoothArrayAdapter(getActivity(), btDeviceList);
		pairedDeviceList.setAdapter(btArrayAdapter);
		
		
		
		pairedDeviceList.setOnItemClickListener(new OnItemClickListener() {
		    @Override
		    public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                BluetoothDevice btDevice = (BluetoothDevice) pairedDeviceList.getItemAtPosition(position);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                Log.d("Pairing device : ", btDevice.getName());
                try {
                    createBond(btDevice);
                    bluetoothID = btDevice.getAddress();
                    appSettings.setConnectionId(bluetoothID);
                    db.updateSettings(appSettings);
                    String first_run = PrefUtils.getFromPrefs(getActivity(), PrefUtils.PREFS_FIRST_INSTALL_CYCLE, "YES");
                    if( first_run.equals("YES")) {
                        Bundle bundle = new Bundle();
                        bundle.putString(BluetoothService.DEVICE_ADDRESS, ""+btDevice);
                        bundle.putString(Utils.APP_MODE, Utils.APP_MODE_QUICK_MEASURE);
                        FragmentMode fragment=new FragmentMode();
                        fragment.setArguments(bundle);

                        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                    }

                    if (null != appSettings.getConnectionId()) {
                        selectedConnectionText.setText(btDevice.getName());
                    }

                    pairedDeviceList.setItemsCanFocus(true);
                    RadioButton radiolistitem = (RadioButton) view.findViewById(R.id.blue_conn_radio);
                    radiolistitem.performClick();
                } catch (Exception e) {

                }
            }
		});
		
		rootView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT ));		
		return rootView;
	}

    public boolean createBond(BluetoothDevice btDevice)
            throws Exception
    {
        @SuppressWarnings("rawtypes")
        Class class1 = Class.forName("android.bluetooth.BluetoothDevice");
        Method createBondMethod = class1.getMethod("createBond");
        Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
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
                        if(!btDeviceList.contains(device))
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

                selectedConnectionText.setVisibility(View.VISIBLE);
                selectedConnectionText.animate().setDuration(shortAnimTime)
                        .alpha(show ? 0 : 1)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                selectedConnectionText.setVisibility(show ? View.GONE
                                        : View.VISIBLE);
                            }
                        });
			} else {
				// The ViewPropertyAnimator APIs are not available, so simply show
				// and hide the relevant UI components.
				bluetoothStatus.setVisibility(show ? View.VISIBLE : View.GONE);
				pairedDeviceList.setVisibility(show ? View.GONE : View.VISIBLE);
                selectedConnectionText.setVisibility(show ? View.GONE : View.VISIBLE);
			}
		}

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bluetoothAdapter != null) {
            bluetoothAdapter.cancelDiscovery();
        }
        getActivity().unregisterReceiver(ActionFoundReceiver);
    }
}


