package com.photosynq.app;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

import com.photosynq.app.db.DatabaseHelper;

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
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class BluetoothActivity extends ActionBarActivity {
	private BluetoothAdapter bluetoothAdapter;
	private ListView lst;
	private View bluetoothStatus;
	private TextView bluetoothStatusMsg;
	private ArrayList<BluetoothDevice> btDeviceList = new ArrayList<BluetoothDevice>();
	private String projectId;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bluetooth);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			projectId = extras.getString(DatabaseHelper.C_PROJECT_ID);
		}
	    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
	    filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
	    filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
	    registerReceiver(ActionFoundReceiver, filter); 

	    // Getting the Bluetooth adapter
	    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	    System.out.println("\nAdapter: " + bluetoothAdapter);
	    
	    CheckBTState();

		lst = (ListView) findViewById(R.id.bluetooth_list_view);
		bluetoothStatus = findViewById(R.id.bluetooth_status);
		bluetoothStatusMsg = (TextView) findViewById(R.id.bluetooth_status_message);
		
		BluetoothArrayAdapter btArrayAdapter = new BluetoothArrayAdapter(this, btDeviceList);
		
		lst.setAdapter(btArrayAdapter);
		
		lst.setOnItemClickListener(new OnItemClickListener() {
		    @Override
		    public void onItemClick(AdapterView<?> adapter, View view, int position, long id){
		    	BluetoothDevice btDevice = (BluetoothDevice) lst.getItemAtPosition(position);
				Log.d("Pairing device : ", btDevice.getName());
				try {
					createBond(btDevice);
					Intent intent = new Intent(getApplicationContext(),NewMeasurmentActivity.class);
					intent.putExtra(DatabaseHelper.C_PROJECT_ID, projectId);
					startActivity(intent);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.bluetooth, menu);
		return true;
	}
	
	 /* This routine is called when an activity completes.*/
	  @Override
	  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    if (requestCode == 1) {
	      CheckBTState();
	    }
	  }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
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
						bluetoothAdapter.ACTION_REQUEST_ENABLE);
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
        Class btClass = Class.forName("android.bluetooth.BluetoothDevice");
        Method removeBondMethod = btClass.getMethod("removeBond");  
        Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice);  
        return returnValue.booleanValue();  
    }


    public boolean createBond(BluetoothDevice btDevice)  
    throws Exception  
    { 
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
	       System.out.println("\n  Device: " + device.getName() + ", " + device);
	       btDeviceList.add(device);
	     } else {
	         if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
	        	 bluetoothStatusMsg.setText(R.string.searching_devices);
	        	 showProgress(true);
	           System.out.println("\nDiscovery Started...");
	         } else {
	           if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
	        	   showProgress(false);
					Set<BluetoothDevice> btDevices =  bluetoothAdapter.getBondedDevices();
					for (BluetoothDevice device : btDevices) {
						btDeviceList.add(device);
					}

	        	   System.out.println("\nDiscovery Finished");
	         }
	       }
	      }
	    }
	  };
}
