package com.photosynq.app;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.Protocol;
import com.photosynq.app.model.ResearchProject;
import com.photosynq.app.utils.BluetoothService;
import com.photosynq.app.utils.CommonUtils;
import com.photosynq.app.utils.LocationUtils;
import com.photosynq.app.utils.PrefUtils;

public class ResultActivity extends ActionBarActivity implements
LocationListener,
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener{
	
	private static final boolean D = true;
	    
	// Name of the connected device
	private String mConnectedDeviceName = null;
	
	// A request to connect to Location Services
    private LocationRequest mLocationRequest;

    // Stores the current instantiation of the location client in this object
    private LocationClient mLocationClient;
	
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
    
    
	//private static final int REQUEST_CONNECT_DEVICE = 1;
	private static final int REQUEST_ENABLE_BT = 2;
	private BluetoothService mBluetoothService = null;
	private BluetoothAdapter mBluetoothAdapter = null;
	private String projectId;
	private String deviceAddress;
	private TextView mStatusLine;
	private String protocolJson="";
	private String options="";
	private boolean quick_measure;
	
	DatabaseHelper db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);
		
		//Location related 
		
        // Create a new global location parameters object
        mLocationRequest = LocationRequest.create();

        /*
         * Set the update interval
         */
        mLocationRequest.setInterval(LocationUtils.UPDATE_INTERVAL_IN_MILLISECONDS);

        // Use high accuracy
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Set the interval ceiling to one minute
        mLocationRequest.setFastestInterval(LocationUtils.FAST_INTERVAL_CEILING_IN_MILLISECONDS);

        /*
         * Create a new location client, using the enclosing class to
         * handle callbacks.
         */
        mLocationClient = new LocationClient(this, this, this);
		
		//
		mStatusLine = (TextView) findViewById(R.id.statusMessage);
 		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			projectId = extras.getString(DatabaseHelper.C_PROJECT_ID)!=null?extras.getString(DatabaseHelper.C_PROJECT_ID):"";
			deviceAddress = extras.getString(BluetoothService.DEVICE_ADDRESS);
			protocolJson = extras.getString(DatabaseHelper.C_PROTOCOL_JSON)!=null?extras.getString(DatabaseHelper.C_PROTOCOL_JSON):"";
			options = extras.getString(DatabaseHelper.C_OPTION_TEXT)!=null?extras.getString(DatabaseHelper.C_OPTION_TEXT):"";
			quick_measure = extras.getBoolean(MainActivity.QUICK_MEASURE);
			System.out.println(this.getClass().getName()+"############quickmeasure="+quick_measure);
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
//		int id = item.getItemId();
//		if (id == R.id.action_settings) {
//			return true;
//		}
		return super.onOptionsItemSelected(item);
	}
	
    /*
     * Called when the Activity is no longer visible at all.
     * Stop updates and disconnect.
     */
    @Override
    public void onStop() {

        // If the client is connected
        if (mLocationClient.isConnected()) {
            stopPeriodicUpdates();
        }

        // After disconnect() is called, the client is considered "dead".
        mLocationClient.disconnect();

        super.onStop();
    }
    
    /*
     * Called when the Activity is restarted, even before it becomes visible.
     */
    @Override
    public void onStart() {

        super.onStart();

        /*
         * Connect the client. Don't re-start any requests here;
         * instead, wait for onResume()
         */
        mLocationClient.connect();

    }
    
    
    /*
     * Handle results returned to this Activity by other Activities started with
     * startActivityForResult(). In particular, the method onConnectionFailed() in
     * LocationUpdateRemover and LocationUpdateRequester may call startResolutionForResult() to
     * start an Activity that handles Google Play services problems. The result of this
     * call returns here, to onActivityResult.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        // Choose what to do based on the request code
        switch (requestCode) {

            // If the request code matches the code sent in onConnectionFailed
            case LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST :

                switch (resultCode) {
                    // If Google Play services resolved the problem
                    case Activity.RESULT_OK:
                        // Log the result
                        Log.d("PHOTOSYNQ-RESULTACTIVITY", getString(R.string.resolved));
//                        // Display the result
//                        mConnectionState.setText(R.string.connected);
//                        mConnectionStatus.setText(R.string.resolved);
                    break;

                    // If any other result was returned by Google Play services
                    default:
                        // Log the result
                        Log.d("PHOTOSYNQ-RESULTACTIVITY", getString(R.string.no_resolution));
//                        // Display the result
//                        mConnectionState.setText(R.string.disconnected);
//                        mConnectionStatus.setText(R.string.no_resolution);

                    break;
                }

            // If any other request code was received
            default:
               // Report that this Activity received an unknown requestCode
               Log.d("PHOTOSYNQ-RESULTACTIVITY",
                       getString(R.string.unknown_activity_request_code, requestCode));

               break;
        }
    }
    
    
    /**
     * Verify that Google Play services is available before making a request.
     *
     * @return true if Google Play services is available, otherwise false
     */
    private boolean servicesConnected() {

        // Check that Google Play services is available
        int resultCode =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("PHOTOSYNQ-RESULTACTIVITY", getString(R.string.play_services_available));

            // Continue
            return true;
        // Google Play services was not available for some reason
        } else {
            // Display an error dialog
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0);
            if (dialog != null) {
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(dialog);
                errorFragment.show(getSupportFragmentManager(), "PHOTOSYNQ-RESULTACTIVITY");
            }
            return false;
        }
    }

    public String getLocation() {

        // If Google Play Services is available
        if (servicesConnected()) {

            // Get the current location
            Location currentLocation = mLocationClient.getLastLocation();

           return LocationUtils.getLatLng(this, currentLocation);
        }
        return "";
    }
	
    public void startUpdates() {

        if (servicesConnected()) {
            startPeriodicUpdates();
        }
    }    
    
    public void stopUpdates() {

        if (servicesConnected()) {
            stopPeriodicUpdates();
        }
    }
    
    /*
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    @Override
    public void onConnected(Bundle bundle) {
            startPeriodicUpdates();
    }

    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onDisconnected() {
    	Log.d("PHOTOSYNQ-RESULTACTIVITY", "Disconnected");
    }

    /*
     * Called by Location Services if the attempt to
     * Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {

                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

                /*
                * Thrown if Google Play services canceled the original
                * PendingIntent
                */

            } catch (IntentSender.SendIntentException e) {

                // Log the error
                e.printStackTrace();
            }
        } else {

            // If no resolution is available, display a dialog to the user with the error.
            showErrorDialog(connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
    	Log.d("PHOTOSYNQ", "Location changed:"+LocationUtils.getLatLng(this, location));
    	PrefUtils.saveToPrefs(getApplicationContext(), PrefUtils.PREFS_CURRENT_LOCATION, LocationUtils.getLatLng(this, location));
    }

    /**
     * In response to a request to start updates, send a request
     * to Location Services
     */
    private void startPeriodicUpdates() {

        mLocationClient.requestLocationUpdates(mLocationRequest, this);
    }

    /**
     * In response to a request to stop updates, send a request to
     * Location Services
     */
    private void stopPeriodicUpdates() {
        mLocationClient.removeLocationUpdates(this);
    }
    
    private void showErrorDialog(int errorCode) {

        // Get the error dialog from Google Play services
        Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
            errorCode,
            this,
            LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

        // If Google Play services can provide an error dialog
        if (errorDialog != null) {

            // Create a new DialogFragment in which to show the error dialog
            ErrorDialogFragment errorFragment = new ErrorDialogFragment();

            // Set the dialog in the DialogFragment
            errorFragment.setDialog(errorDialog);

            // Show the error dialog in the DialogFragment
            errorFragment.show(getSupportFragmentManager(), "PHOTOSYNQ-RESULTACTIVITY");
        }
    }

    /**
     * Define a DialogFragment to display the error dialog generated in
     * showErrorDialog.
     */
    public static class ErrorDialogFragment extends DialogFragment {

        // Global field to contain the error dialog
        private Dialog mDialog;

        /**
         * Default constructor. Sets the dialog field to null
         */
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        /**
         * Set the dialog to display
         *
         * @param dialog An error dialog
         */
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        /*
         * This method must return a Dialog to the DialogFragment.
         */
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }

    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_STATE_CHANGE:
                if(D) Log.i("PHOTOSYNC", "MESSAGE_STATE_CHANGE: " + msg.arg1);
                switch (msg.arg1) {
                case BluetoothService.STATE_CONNECTED:
                	mStatusLine.setText(R.string.title_connected_to);
                	mStatusLine.append(mConnectedDeviceName);
                	if(protocolJson.length() == 0)
                	{
                		db = new DatabaseHelper(getApplicationContext());
                		ResearchProject rp =  db.getResearchProject(projectId);
                		String[] protocol_ids = rp.getProtocols_ids().trim().split(",");
                		if(rp.getProtocols_ids().length() >=1)
                		{
	                		for (String protocol_id : protocol_ids) {
	                			Protocol protocol = db.getProtocol(protocol_id);
	                			System.out.println("######## protocol :"+protocol.getProtocol_json());
	                			if(protocol.getProtocol_json().trim().length() > 1)
	                			{
	                				protocolJson +=  "{"+protocol.getProtocol_json().trim().substring(1, protocol.getProtocol_json().trim().length()-1)+"},";
	                			}
							}
	                		protocolJson = "["+protocolJson.substring(0, protocolJson.length()-1) +"]"; // remove last comma and add suqare brackets and start and end.
	                		
	                		System.out.println("$$$$$$$$$$$$$$ protocol json sending to device :"+protocolJson);
	                		db.closeDB();
	                		//String obj = "[{\"environmental\":[[\"light_intensity\",0]],\"tcs_to_act\":100,\"protocol_name\":\"baseline_sample\",\"protocols_delay\":5,\"act_background_light\":20,\"actintensity1\":5,\"actintensity2\":5,\"averages\":1,\"wait\":0,\"cal_true\":2,\"analog_averages\":1,\"pulsesize\":10,\"pulsedistance\":3000,\"calintensity\":255,\"pulses\":[400],\"detectors\":[[34]],\"measlights\":[[14]]},{\"tcs_to_act\":100,\"environmental\":[[\"relative_humidity\",0],[\"temperature\",0],[\"light_intensity\",0]],\"protocols_delay\":5,\"act_background_light\":20,\"protocol_name\":\"fluorescence\",\"baselines\":[1,1,1,1],\"averages\":1,\"wait\":0,\"cal_true\":0,\"analog_averages\":1,\"act_light\":20,\"pulsesize\":10,\"pulsedistance\":10000,\"actintensity1\":5,\"actintensity2\":50,\"measintensity\":7,\"calintensity\":255,\"pulses\":[50,50,50,50],\"detectors\":[[34],[34],[34],[34]],\"measlights\":[[15],[15],[15],[15]],\"act\":[0,1,0,0]},{\"protocol_name\":\"chlorophyll_spad_ndvi\",\"baselines\":[0,0,0,0],\"environmental\":[[\"relative_humidity\",1],[\"temperature\",1],[\"light_intensity\",1]],\"measurements\":1,\"measurements_delay\":1,\"averages\":1,\"wait\":0,\"cal_true\":0,\"analog_averages\":1,\"pulsesize\":20,\"pulsedistance\":3000,\"actintensity1\":8,\"actintensity2\":8,\"measintensity\":80,\"calintensity\":255,\"pulses\":[100],\"detectors\":[[34,35,35,34]],\"measlights\":[[12,20,12,20]]}]";
	                		//	String protocol= "[{\"protocol_name\":\"fluorescence\",\"baselines\":[1,1,1,1],\"averages\":1,\"wait\":0,\"cal_true\":0,\"analog_averages\":12,\"act_light\":20,\"pulsesize\":50,\"pulsedistance\":3000,\"actintensity1\":100,\"actintensity2\":100,\"measintensity\":3,\"calintensity\":255,\"pulses\":[50,50,50,50],\"detectors\":[[34],[34],[34],[34]],\"measlights\":[[15],[15],[15],[15]],\"act\":[2,1,2,2]}]";
	                		sendData(protocolJson);
                		}
                		else{
                				mStatusLine.setText("No protocol defined for this project.");
                				Toast.makeText(getApplicationContext(), "No protocol defined for this project.", Toast.LENGTH_LONG).show();
                				break;
                			}
                	}else
                	{
                		//change this once you get actual protocol
                		//String obj = "[{\"measurements\":2,\"protocol_name\":\"baseline_sample\",\"averages\":1,\"wait\":0,\"cal_true\":2,\"analog_averages\":1,\"pulsesize\":10,\"pulsedistance\":3000,\"actintensity1\":1,\"actintensity2\":1,\"measintensity\":255,\"calintensity\":255,\"pulses\":[400],\"detectors\":[[34]],\"measlights\":[[14]]},{\"measurements\":2,\"protocol_name\":\"fluorescence\",\"baselines\":[1,1,1,1],\"environmental\":[[\"relative_humidity\",1],[\"temperature\",1]],\"averages\":2,\"wait\":0,\"cal_true\":0,\"analog_averages\":1,\"act_light\":20,\"pulsesize\":10,\"pulsedistance\":10000,\"actintensity1\":100,\"actintensity2\":100,\"measintensity\":3,\"calintensity\":255,\"pulses\":[50,50,50,50],\"detectors\":[[34],[34],[34],[34]],\"measlights\":[[15],[15],[15],[15]],\"act\":[2,1,2,2]}]";
                		sendData(protocolJson);
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
                //byte[] writeBuf = (byte[]) msg.obj;
                // construct a string from the buffer
                //String writeMessage = new String(writeBuf);
                break;
            case MESSAGE_READ:
               // byte[] readBuf = (byte[]) msg.obj;
            	StringBuffer measurement = (StringBuffer)msg.obj;
                // construct a string from the valid bytes in the buffer
               // String readMessage = new String(readBuf, 0, msg.arg1);
                mStatusLine.setText("Receiving data from device");
                String dataString;
                long time= System.currentTimeMillis();
                if (options.equals(""))
                {
                	 dataString = "var data = [\n"+measurement.toString().replaceAll("\\r\\n", "").replaceAll("\\{", "{\"time\":\""+time+"\",")+"\n];";
                }
                else
                {
                	String currentLocation = PrefUtils.getFromPrefs(getApplicationContext(), PrefUtils.PREFS_CURRENT_LOCATION, "NONE");
                	if(!currentLocation.equals("NONE"))
                	{
                		options = options+"\"location\":["+currentLocation+"],";
                		dataString = "var data = [\n"+measurement.toString().replaceAll("\\r\\n", "").replaceFirst("\\{", "{"+options).replaceAll("\\{", "{\"time\":\""+time+"\",")+"\n];";
                	}
                	else
                	{
                		dataString = "var data = [\n"+measurement.toString().replaceAll("\\r\\n", "").replaceFirst("\\{", "{"+options).replaceAll("\\{", "{\"time\":\""+time+"\",")+"\n];";
                	}
                }
                System.out.println("###### writing data.js :"+dataString);
                CommonUtils.writeStringToFile(getApplicationContext(), "data.js", dataString);
                mBluetoothService.stop();
                Intent intent = new Intent(getApplicationContext(),DisplayResultsActivity.class);
        		intent.putExtra(DatabaseHelper.C_PROJECT_ID, projectId);
        		intent.putExtra(DatabaseHelper.C_PROTOCOL_JSON, protocolJson);
        		intent.putExtra(MainActivity.QUICK_MEASURE, quick_measure);
        		String reading = measurement.toString().replaceAll("\\r\\n", "").replaceFirst("\\{", "{"+options).replaceAll("\\{", "{\"time\":\""+time+"\",");
        		//reading = reading.replaceFirst("\\{", "{"+options);
        		intent.putExtra(DatabaseHelper.C_READING, reading);
        		startActivity(intent);
        		finish();
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
                break;
            }
        }
    };
}
