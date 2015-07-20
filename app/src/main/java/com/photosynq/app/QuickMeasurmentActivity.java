package com.photosynq.app;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.BluetoothMessage;
import com.photosynq.app.model.Macro;
import com.photosynq.app.model.Protocol;
import com.photosynq.app.model.ResearchProject;
import com.photosynq.app.utils.BluetoothService;
import com.photosynq.app.utils.CommonUtils;
import com.photosynq.app.utils.Constants;
import com.photosynq.app.utils.PrefUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class QuickMeasurmentActivity extends ActionBarActivity implements SelectDeviceDialogDelegate {

    private String deviceAddress;
    private String mConnectedDeviceName;
    private String protocolId;
    private String protocolJson;
    private String mProtocolName = "";
    private String mProtocolDescription = "";

    private TextView mtvStatusMessage;

    //private BluetoothService mBluetoothService = null;
    private BluetoothAdapter mBluetoothAdapter = null;
    private static final int REQUEST_ENABLE_BT = 2;

    private DatabaseHelper dbHelper;

    private Button btnTakeMeasurement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_measurment);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));
        actionBar.setDisplayHomeAsUpEnabled(true);

        dbHelper = DatabaseHelper.getHelper(this);

        deviceAddress = CommonUtils.getDeviceAddress(this);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (null != mBluetoothAdapter && !mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }

        View reviewPage = findViewById(R.id.ll_btn);

        BluetoothMessage bluetoothMessage = new BluetoothMessage();
        bluetoothMessage.view = reviewPage;

        final BluetoothService mBluetoothService = BluetoothService.getInstance(bluetoothMessage, mHandler);

        btnTakeMeasurement = (Button) findViewById(R.id.btn_take_measurement);
        btnTakeMeasurement.setTypeface(CommonUtils.getInstance(this).getFontRobotoMedium());

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            protocolId = extras.getString(Protocol.ID);
            protocolJson = extras.getString(DatabaseHelper.C_PROTOCOL_JSON);
            mConnectedDeviceName = extras.getString(Constants.DEVICE_NAME);
            mProtocolName = extras.getString(Protocol.NAME);
            mProtocolDescription = extras.getString(Protocol.DESCRIPTION);

            String isCalledFromResults = extras.getString(Constants.START_MEASURE);
            if (isCalledFromResults != null && isCalledFromResults.equals("TRUE")) {
                if (mBluetoothService.getState() != BluetoothService.STATE_CONNECTED) {
                    // Get the BLuetoothDevice object
                    deviceAddress = CommonUtils.getDeviceAddress(QuickMeasurmentActivity.this);
                    if(null == deviceAddress)
                    {
                        Toast.makeText(QuickMeasurmentActivity.this, "Measurement device not configured, Please configure measurement device (bluetooth).", Toast.LENGTH_SHORT).show();
                        selectDevice();
                        return;
                    }else {
                        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(deviceAddress);
                        mBluetoothService.connect(device);
                    }
                } else {
                    mHandler.obtainMessage(Constants.MESSAGE_STATE_CHANGE, BluetoothService.STATE_CONNECTED, 1).sendToTarget();
                }
                btnTakeMeasurement.setText("Cancel Measure");
                btnTakeMeasurement.setBackgroundResource(R.drawable.btn_layout_red);

            }
        }

        TextView tvProtocolName = (TextView) findViewById(R.id.tv_protocol_name);
        tvProtocolName.setTypeface(CommonUtils.getInstance(this).getFontRobotoRegular());
        tvProtocolName.setText(mProtocolName);

        TextView tvProtocolDesc = (TextView) findViewById(R.id.tv_protocol_desc);
        tvProtocolDesc.setTypeface(CommonUtils.getInstance(this).getFontRobotoRegular());
        tvProtocolDesc.setText(mProtocolDescription);

        mtvStatusMessage = (TextView) findViewById(R.id.tv_status_message);
        mtvStatusMessage.setTypeface(CommonUtils.getInstance(this).getFontRobotoRegular());

        btnTakeMeasurement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(btnTakeMeasurement.getText().equals("+ Take Measurement")) {
                    if (mBluetoothService.getState() != BluetoothService.STATE_CONNECTED) {
                        // Get the BLuetoothDevice object
                        if(null == deviceAddress)
                        {
                            Toast.makeText(QuickMeasurmentActivity.this, "Measurement device not configured, Please configure measurement device (bluetooth).", Toast.LENGTH_SHORT).show();
                            selectDevice();
                            return;

                        }else {
                            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(deviceAddress);
                            mBluetoothService.connect(device);
                        }
                    } else {
                        mHandler.obtainMessage(Constants.MESSAGE_STATE_CHANGE, BluetoothService.STATE_CONNECTED, 1).sendToTarget();
                    }
                    btnTakeMeasurement.setText("Cancel Measure");
                    btnTakeMeasurement.setBackgroundResource(R.drawable.btn_layout_red);
                }
                else if(btnTakeMeasurement.getText().equals("Cancel Measure"))
                {
                    sendData("-1+-1+");
                    finish();
                }
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_display_results, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void selectDevice() {

        deviceAddress = CommonUtils.getDeviceAddress(this);
        if (null == deviceAddress) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            SelectDeviceDialog selectDeviceDialog = new SelectDeviceDialog();
            selectDeviceDialog.show(fragmentManager, "Select Measurement Device", this);
        }
    }

    @Override
    public void onDeviceSelected(String result) {

        deviceAddress = result;
        if (null == deviceAddress) {
            Toast.makeText(this, "Measurement device not configured, Please configure measurement device (bluetooth).", Toast.LENGTH_SHORT).show();

            selectDevice();
        }

    }


    private void sendData(String data) {
        // Check that we're actually connected before trying anything

        View reviewPage = findViewById(R.id.ll_btn);
        BluetoothMessage bluetoothMessage = new BluetoothMessage();
        bluetoothMessage.view = reviewPage;

        BluetoothService mBluetoothService = BluetoothService.getInstance(bluetoothMessage, mHandler);
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
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth  services
        //??if (mBluetoothService != null) mBluetoothService.stop();
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            BluetoothMessage bluetoothMessage = (BluetoothMessage) msg.obj;

            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    if(Constants.D) Log.i("PHOTOSYNC", "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            //if(msg.arg2 == 1) { //Send measurement request
                            mtvStatusMessage.setText(R.string.title_connected_to);
                            if (mConnectedDeviceName != null) {
                                mtvStatusMessage.append(mConnectedDeviceName);
                            }
                                if (protocolJson.length() > 0) {
                                    //change this once you get actual protocol
                                    //change this once you get actual protocol

                                    //??protocolJson = "[" + protocolJson + "]";
                                    System.out.println("sending protocol to device using quick measure : " + protocolJson + "length:" + protocolJson.length());

                                    sendData("[" + protocolJson + "]");

                                    Protocol protocol = dbHelper.getProtocol(protocolId);
                                    if (null != protocol) {

                                        StringBuffer dataStringMacro = new StringBuffer();

                                        // Writing macros.js file with all macro functions
                                        List<Macro> macros = dbHelper.getAllMacros();
                                        for (Macro macro : macros) {
                                            if (macro.getId().equals(protocol.getMacroId())) {
                                                dataStringMacro.append("function macro_" + macro.getId() + "(json){");
                                                dataStringMacro.append(System.getProperty("line.separator"));
                                                dataStringMacro.append("try{");
                                                dataStringMacro.append(System.getProperty("line.separator"));
//                                                        String evalStr = macro.getJavascriptCode().replaceAll("\\r\\n", " ");
//                                                        evalStr = evalStr.replaceAll("\"", "\\\\\"");
//                                                        dataStringMacro.append("eval(\"" + evalStr + "\");");
//                                                        dataStringMacro.append(System.getProperty("line.separator"));
                                                dataStringMacro.append(macro.getJavascriptCode().replaceAll("\\r\\n", System.getProperty("line.separator"))); //replacing ctrl+m characters
                                                dataStringMacro.append(System.getProperty("line.separator"));
                                                dataStringMacro.append("}"); //try closing
                                                dataStringMacro.append(System.getProperty("line.separator"));
                                                dataStringMacro.append("catch(err) {}");
                                                dataStringMacro.append(System.getProperty("line.separator") + " }");
                                                dataStringMacro.append(System.getProperty("line.separator"));
                                                dataStringMacro.append(System.getProperty("line.separator"));

                                                break;
                                            }
                                        }
                                        CommonUtils.writeStringToFile(getApplicationContext(), "macros.js", dataStringMacro.toString());
                                    }
                                }


                                mtvStatusMessage.setText("Initializing measurement please wait ...");

                            //}
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            mtvStatusMessage.setText(R.string.title_connecting);
                            break;
                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:
                            mtvStatusMessage.setText(R.string.title_not_connected);
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    break;
                case Constants.MESSAGE_READ:
                    String measurement = bluetoothMessage.message;
                    // Do not process the message if contain pwr_off from device
                    if (!measurement.contains("pwr_off")) {
                        // construct a string from the valid bytes in the buffer
                        // String readMessage = new String(readBuf, 0, msg.arg1);
                        mtvStatusMessage.setText(R.string.start_measurement);
                        String dataString;
                        StringBuffer options = new StringBuffer();

                        String currentLocation = PrefUtils.getFromPrefs(getApplicationContext(), PrefUtils.PREFS_CURRENT_LOCATION, "NONE");
                        if (!currentLocation.equals("NONE")) {
                            options.append("\"location\":[" + currentLocation + "],");
                            dataString = "var data = [\n" + measurement.replaceAll("\\r\\n", "").replaceFirst("\\{", "{" + options) + "\n];";
                        } else {
                            dataString = "var data = [\n" + measurement.replaceAll("\\r\\n", "").replaceFirst("\\{", "{" + options) + "\n];";
                        }

                        System.out.println("###### writing data.js :" + dataString);
                        CommonUtils.writeStringToFile(getApplicationContext(), "data.js", dataString);

                        Intent intent = new Intent(getApplicationContext(), DisplayResultsActivity.class);
                        intent.putExtra(Constants.APP_MODE, Constants.APP_MODE_QUICK_MEASURE);
                        intent.putExtra(Protocol.ID, protocolId);
                        intent.putExtra(DatabaseHelper.C_PROTOCOL_JSON, protocolJson);
                        intent.putExtra(Constants.DEVICE_NAME, mConnectedDeviceName);
                        intent.putExtra(Protocol.NAME, mProtocolName);
                        intent.putExtra(Protocol.DESCRIPTION, mProtocolDescription);
                        startActivity(intent);

                        finish();
                    }

                    if(btnTakeMeasurement != null) {
                        if (btnTakeMeasurement.getText().equals("Cancel Measure")) {
                            btnTakeMeasurement.setText("+ Take Measurement");
                            btnTakeMeasurement.setBackgroundResource(R.drawable.btn_layout_orange);
                        }
                    }

                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "Connected to "
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();

                    break;
                case Constants.MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(Constants.TOAST),
                            Toast.LENGTH_SHORT).show();

                    if(btnTakeMeasurement != null) {
                        if (btnTakeMeasurement.getText().equals("Cancel Measure")) {
                            btnTakeMeasurement.setText("+ Take Measurement");
                            btnTakeMeasurement.setBackgroundResource(R.drawable.btn_layout_orange);
                        }
                    }
                    break;
                case Constants.MESSAGE_STOP:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(Constants.TOAST),
                            Toast.LENGTH_SHORT).show();
                    //??mBluetoothService.stop();
                    break;
            }
        }
    };

}
