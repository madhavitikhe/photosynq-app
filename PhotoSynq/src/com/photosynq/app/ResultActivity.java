package com.photosynq.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.utils.BluetoothService;

public class ResultActivity extends ActionBarActivity {
	
	 private static final String TAG = "BluetoothChat";
	    private static final boolean D = true;
	    
	    // Name of the connected device
	    private String mConnectedDeviceName = null;
	    
	  // Message types sent from the BluetoothService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_STOP = 6;
    
    // Key names received from the BluetoothService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    
    
	private static final int REQUEST_CONNECT_DEVICE = 1;
	private static final int REQUEST_ENABLE_BT = 2;
	private BluetoothService mBluetoothService = null;
	private BluetoothAdapter mBluetoothAdapter = null;
	private String projectId;
	private String deviceAddress;
	private TextView mStatusLine;
	private String protocolNameInArduino="";
	
	DatabaseHelper db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);
		
		mStatusLine = (TextView) findViewById(R.id.statusMessage);
 		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			System.out.println("Project ID 1 "+projectId);
			projectId = extras.getString(DatabaseHelper.C_PROJECT_ID);
			deviceAddress = extras.getString(BluetoothService.DEVICE_ADDRESS);
			protocolNameInArduino = extras.getString(DatabaseHelper.C_PROTOCOL_NAME_IN_ARDUINO_CODE);
			System.out.println(" protocol name in arduion :"+protocolNameInArduino);
		}		
		

		
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
		}
		if (mBluetoothService == null) {
			mBluetoothService = new BluetoothService(getApplicationContext(), mHandler);
		}
		 // Get the BLuetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(deviceAddress);
        mBluetoothService.connect(device);
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
//		if (id == R.id.action_settings) {
//			return true;
//		}
		return super.onOptionsItemSelected(item);
	}
	
    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_STATE_CHANGE:
                if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                switch (msg.arg1) {
                case BluetoothService.STATE_CONNECTED:
                	mStatusLine.setText(R.string.title_connected_to);
                	mStatusLine.append(mConnectedDeviceName);
                	if(protocolNameInArduino.length() == 0)
                	{
                		System.out.println("Setting default protocol ");
                		String obj = "[{\"measurements\":2,\"protocol_name\":\"baseline_sample\",\"averages\":1,\"wait\":0,\"cal_true\":2,\"analog_averages\":1,\"pulsesize\":10,\"pulsedistance\":3000,\"actintensity1\":1,\"actintensity2\":1,\"measintensity\":255,\"calintensity\":255,\"pulses\":[400],\"detectors\":[[34]],\"measlights\":[[14]]},{\"measurements\":2,\"protocol_name\":\"fluorescence\",\"baselines\":[1,1,1,1],\"environmental\":[[\"relative_humidity\",1],[\"temperature\",1]],\"averages\":2,\"wait\":0,\"cal_true\":0,\"analog_averages\":1,\"act_light\":20,\"pulsesize\":10,\"pulsedistance\":10000,\"actintensity1\":100,\"actintensity2\":100,\"measintensity\":3,\"calintensity\":255,\"pulses\":[50,50,50,50],\"detectors\":[[34],[34],[34],[34]],\"measlights\":[[15],[15],[15],[15]],\"act\":[2,1,2,2]}]";
                		//	String protocol= "[{\"protocol_name\":\"fluorescence\",\"baselines\":[1,1,1,1],\"averages\":1,\"wait\":0,\"cal_true\":0,\"analog_averages\":12,\"act_light\":20,\"pulsesize\":50,\"pulsedistance\":3000,\"actintensity1\":100,\"actintensity2\":100,\"measintensity\":3,\"calintensity\":255,\"pulses\":[50,50,50,50],\"detectors\":[[34],[34],[34],[34]],\"measlights\":[[15],[15],[15],[15]],\"act\":[2,1,2,2]}]";
                		sendData(obj);
                	}else
                	{
                		//change this once you get actual protocol
                		System.out.println("Setting protocol :"+protocolNameInArduino);
                		//String obj = "[{\"measurements\":2,\"protocol_name\":\"baseline_sample\",\"averages\":1,\"wait\":0,\"cal_true\":2,\"analog_averages\":1,\"pulsesize\":10,\"pulsedistance\":3000,\"actintensity1\":1,\"actintensity2\":1,\"measintensity\":255,\"calintensity\":255,\"pulses\":[400],\"detectors\":[[34]],\"measlights\":[[14]]},{\"measurements\":2,\"protocol_name\":\"fluorescence\",\"baselines\":[1,1,1,1],\"environmental\":[[\"relative_humidity\",1],[\"temperature\",1]],\"averages\":2,\"wait\":0,\"cal_true\":0,\"analog_averages\":1,\"act_light\":20,\"pulsesize\":10,\"pulsedistance\":10000,\"actintensity1\":100,\"actintensity2\":100,\"measintensity\":3,\"calintensity\":255,\"pulses\":[50,50,50,50],\"detectors\":[[34],[34],[34],[34]],\"measlights\":[[15],[15],[15],[15]],\"act\":[2,1,2,2]}]";
                		sendData(protocolNameInArduino);
                	}
                    
                    mStatusLine.setText("Initializing measurement . .");

                	
                    break;
                case BluetoothService.STATE_CONNECTING:
                	mStatusLine.setText(R.string.title_connecting);
                    break;
                case BluetoothService.STATE_LISTEN:
                case BluetoothService.STATE_NONE:
                	mStatusLine.setText(R.string.title_not_connected);
                    break;
                }
                break;
            case MESSAGE_WRITE:
                byte[] writeBuf = (byte[]) msg.obj;
                // construct a string from the buffer
                //String writeMessage = new String(writeBuf);
                break;
            case MESSAGE_READ:
               // byte[] readBuf = (byte[]) msg.obj;
            	StringBuffer measurement = (StringBuffer)msg.obj;
                // construct a string from the valid bytes in the buffer
               // String readMessage = new String(readBuf, 0, msg.arg1);
                mStatusLine.setText("Receiving data from device");
                try {
                    String filename = "data.js";
                    System.out.println("@@@@@@@@@ writing file data.js");
                    File myFile = new File(getExternalFilesDir(null), filename);
                    if (myFile.exists()){
                    	System.out.println("@@@@@@@@@ deleting file data.js");
                    	myFile.delete();
                    	System.out.println("@@@@@@@@@ creating file data.js");
                    	myFile.createNewFile();
                    }else
                    {
                        myFile.createNewFile();
                    }
                    System.out.println("@@@@@@@@@ path"+myFile.getAbsolutePath());
                    FileOutputStream fos;
                    String dataString = "var data = [\n"+measurement.toString().replaceAll("\\r\\n", "")+"\n];";
                    long time= System.currentTimeMillis();
                    dataString = dataString.replaceAll("\\{", "{\"time\":\""+time+"\",");
                    System.out.println("@@@@@@@@@ writing data to data.js");
                    byte[] data = dataString.getBytes();
                    System.out.println("@@@@@@@@@ "+data.length);
                    try {
                        fos = new FileOutputStream(myFile);
                        fos.write(data);
                        fos.flush();
                        fos.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }catch (Exception e) {
                	e.printStackTrace();
				}
                mBluetoothService.stop();
                Intent intent = new Intent(getApplicationContext(),DisplayResultsActivity.class);
        		intent.putExtra(DatabaseHelper.C_PROJECT_ID, projectId);
        		intent.putExtra(DatabaseHelper.C_PROTOCOL_NAME_IN_ARDUINO_CODE, protocolNameInArduino);
        		intent.putExtra(DatabaseHelper.C_READING, measurement.toString().replaceAll("\\r\\n", ""));
        		startActivity(intent);

                
//                /// Write records to DB.
//                System.out.println("Project ID 2"+projectId);
//                ProjectResult result = new ProjectResult(projectId, measurement.toString(), "N");
//                db = new DatabaseHelper(getApplicationContext());
//                db.createResult(result);
//                db.closeDB();
                


                break;
            case MESSAGE_DEVICE_NAME:
                // save the connected device's name
                mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                Toast.makeText(getApplicationContext(), "Connected to "
                               + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                
                break;
            case MESSAGE_TOAST:
                Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                               Toast.LENGTH_SHORT).show();
                break;
            case MESSAGE_STOP:
                Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                               Toast.LENGTH_SHORT).show();
                mBluetoothService.stop();
                // Create a data.js file and navigate to result display.
                //System.out.println("Project ID3 "+projectId);
//                Intent intent = new Intent(getApplicationContext(),DisplayResultsActivity.class);
//        		intent.putExtra(DatabaseHelper.C_PROJECT_ID, projectId);
//        		startActivity(intent);
                break;
            }
        }
    };
}
