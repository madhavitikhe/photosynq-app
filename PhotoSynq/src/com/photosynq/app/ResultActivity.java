package com.photosynq.app;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.utils.BluetoothService;

public class ResultActivity extends ActionBarActivity {
	private static final int REQUEST_CONNECT_DEVICE = 1;
	private static final int REQUEST_ENABLE_BT = 2;
	private BluetoothService mBluetoothService = null;
	private BluetoothAdapter mBluetoothAdapter = null;
	private String projectId;
	private String deviceAddress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			projectId = extras.getString(DatabaseHelper.C_PROJECT_ID);
			deviceAddress = extras.getString(BluetoothService.DEVICE_ADDRESS);
		}		
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
		}
		if (mBluetoothService == null) {
			mBluetoothService = new BluetoothService(getApplicationContext());
		}
		 // Get the BLuetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(deviceAddress);
        mBluetoothService.connect(device);
        try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}        
        // Attempt to connect to the device
        
        String obj = "[{\"measurements\":2,\"protocol_name\":\"baseline_sample\",\"averages\":1,\"wait\":0,\"cal_true\":2,\"analog_averages\":1,\"pulsesize\":10,\"pulsedistance\":3000,\"actintensity1\":1,\"actintensity2\":1,\"measintensity\":255,\"calintensity\":255,\"pulses\":[400],\"detectors\":[[34]],\"measlights\":[[14]]},{\"measurements\":2,\"protocol_name\":\"fluorescence\",\"baselines\":[1,1,1,1],\"environmental\":[[\"relative_humidity\",1],[\"temperature\",1]],\"averages\":2,\"wait\":0,\"cal_true\":0,\"analog_averages\":1,\"act_light\":20,\"pulsesize\":10,\"pulsedistance\":10000,\"actintensity1\":100,\"actintensity2\":100,\"measintensity\":3,\"calintensity\":255,\"pulses\":[50,50,50,50],\"detectors\":[[34],[34],[34],[34]],\"measlights\":[[15],[15],[15],[15]],\"act\":[2,1,2,2]}]";
        //String protocol= "[{\"protocol_name\":\"fluorescence\",\"baselines\":[1,1,1,1],\"averages\":1,\"wait\":0,\"cal_true\":0,\"analog_averages\":12,\"act_light\":20,\"pulsesize\":50,\"pulsedistance\":3000,\"actintensity1\":100,\"actintensity2\":100,\"measintensity\":3,\"calintensity\":255,\"pulses\":[50,50,50,50],\"detectors\":[[34],[34],[34],[34]],\"measlights\":[[15],[15],[15],[15]],\"act\":[2,1,2,2]}]";
        sendData(obj);
	}


    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth  services
        if (mBluetoothService != null) mBluetoothService.stop();
    }
    
    private void sendData(String data) {
        // Check that we're actually connected before trying anything
        if (mBluetoothService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(getApplicationContext(),"Not Connected", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (data.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send;
            System.out.println("sending data to device");
				send = data.getBytes();
				 mBluetoothService.write(send);
            //byte[] bytes = ByteBuffer.allocate(4).putInt(9).array();
            
           
        }
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.result, menu);
		return true;
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
}
