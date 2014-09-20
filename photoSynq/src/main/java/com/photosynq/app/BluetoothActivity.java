package com.photosynq.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.navigationDrawer.Utils;
import com.photosynq.app.utils.BluetoothService;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

public class BluetoothActivity extends Activity {
	private BluetoothAdapter bluetoothAdapter;
	private ListView lst;
	private View bluetoothStatus;
	private TextView bluetoothStatusMsg;
	private ArrayList<BluetoothDevice> btDeviceList = new ArrayList<BluetoothDevice>();
	private String projectId;
	private String appmode = "";
	private Button searchBTdevice;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bluetooth);
//		LayoutInflater inflater = (LayoutInflater) this
//	            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//	    View contentView = inflater.inflate(R.layout.activity_bluetooth, null, false);
//	    layoutDrawer.addView(contentView);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			projectId = extras.getString(DatabaseHelper.C_PROJECT_ID);
			appmode = extras.getString(Utils.APP_MODE);
			System.out.println(this.getClass().getName()+"############app mode="+appmode);
		}
	    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
	    filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
	    filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
	    registerReceiver(ActionFoundReceiver, filter); 

	    // Getting the Bluetooth adapter
	    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	    System.out.println("\nAdapter: " + bluetoothAdapter);

	    //enable bluetooth and show list of paired devices
		if (!bluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, 1);
		}
			Set<BluetoothDevice> btDevices =  bluetoothAdapter.getBondedDevices();
		for (BluetoothDevice device : btDevices) {
			btDeviceList.add(device);
		}
	    
	    //CheckBTState();

		lst = (ListView) findViewById(R.id.bluetooth_list_view);
		bluetoothStatus = findViewById(R.id.bluetooth_status);
		bluetoothStatusMsg = (TextView) findViewById(R.id.bluetooth_status_message);
		searchBTdevice = (Button) findViewById(R.id.searchNewBluetooth);
		
		BluetoothArrayAdapter btArrayAdapter = new BluetoothArrayAdapter(this, btDeviceList);
		
		lst.setAdapter(btArrayAdapter);
		
		lst.setOnItemClickListener(new OnItemClickListener() {
		    @Override
		    public void onItemClick(AdapterView<?> adapter, View view, int position, long id){
		    	BluetoothDevice btDevice = (BluetoothDevice) lst.getItemAtPosition(position);
				Log.d("Pairing device : ", btDevice.getName());
				try {
					createBond(btDevice);
					if(appmode.equals(Utils.APP_MODE_NORMAL))
					{
						Intent intent = new Intent(getApplicationContext(),NewMeasurmentActivity.class);
						intent.putExtra(DatabaseHelper.C_PROJECT_ID, projectId);
						intent.putExtra(Utils.APP_MODE, Utils.APP_MODE_NORMAL);
						intent.putExtra(BluetoothService.DEVICE_ADDRESS, btDevice.getAddress());
						startActivity(intent);
					}else if (appmode.equals(Utils.APP_MODE_QUICK_MEASURE) )
					{
						Intent intent = new Intent(getApplicationContext(),SelectProtocolActivity.class);
						intent.putExtra(BluetoothService.DEVICE_ADDRESS, btDevice.getAddress());
						intent.putExtra(Utils.APP_MODE, Utils.APP_MODE_QUICK_MEASURE);
						startActivity(intent);
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		});
	}



	 /* This routine is called when an activity completes.*/
	@Override 
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	 // TODO Auto-generated method stub
	  super.onActivityResult(requestCode, resultCode, data);
	  if(resultCode == RESULT_OK){
		if (requestCode == 1) {
		      CheckBTState();
		 }
		else if(resultCode == RESULT_CANCELED){
			// Toast.makeText(getApplicationContext(), "Error occured while enabling.Leaving the application..", Toast.LENGTH_LONG).show();
			// bluetoothAdapter.disable();
	     finish();
	     }
	    }
	  }
	
	public void searchNewBluetooth(View view)
	{
		searchBTdevice.setVisibility(View.GONE);
		 btDeviceList.clear();
    	 CheckBTState();
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
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

			lst.setVisibility(View.VISIBLE);
			lst.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							lst.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			bluetoothStatus.setVisibility(show ? View.VISIBLE : View.GONE);
			lst.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	private void CheckBTState() {
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
	protected void onDestroy() {
		super.onDestroy();
		if (bluetoothAdapter != null) {
			bluetoothAdapter.cancelDiscovery();
		}
		unregisterReceiver(ActionFoundReceiver);
	}

	
    public boolean removeBond(BluetoothDevice btDevice)  
    throws Exception  
    {  
        @SuppressWarnings("rawtypes")
		Class btClass = Class.forName("android.bluetooth.BluetoothDevice");
        @SuppressWarnings("unchecked")
		Method removeBondMethod = btClass.getMethod("removeBond");  
        Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice);  
        return returnValue.booleanValue();  
    }


    @SuppressWarnings("unchecked")
	public boolean createBond(BluetoothDevice btDevice)  
    throws Exception  
    { 
        @SuppressWarnings("rawtypes")
		Class class1 = Class.forName("android.bluetooth.BluetoothDevice");
        Method createBondMethod = class1.getMethod("createBond");  
        Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);  
        return returnValue.booleanValue();  
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
	        	   searchBTdevice.setVisibility(View.VISIBLE);
					Set<BluetoothDevice> btDevices =  bluetoothAdapter.getBondedDevices();
					for (BluetoothDevice device : btDevices) {
                        //This is strage but its adding devices twice
                        if(!btDeviceList.contains(device))
						btDeviceList.add(device);
					}

	         }
	       }
	      }
	    }
	  };
}
