package com.photosynq.app;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.AppSettings;
import com.photosynq.app.utils.CommonUtils;
import com.photosynq.app.utils.PrefUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by kalpesh on 10/02/15.
 */
public class SelectDeviceDialog extends DialogFragment {

    SelectDeviceDialogDelegate mSelectDeviceDialogDelegate;
    BluetoothAdapter bluetoothAdapter;
    ListView pairedDeviceList;
    View bluetoothStatus;
    TextView bluetoothStatusMsg;
    private ArrayList<BluetoothDevice> btDeviceList = new ArrayList<BluetoothDevice>();
    private Button searchNewBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.dialog_fragment_select_device, container, false);

        Dialog dialog = getDialog();
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        TextView tvTitle = (TextView) dialog.findViewById(R.id.tv_dialog_title);
        if(tvTitle != null){
            tvTitle.setTypeface(CommonUtils.getInstance(getActivity()).getFontRobotoRegular());
            //tvTitle.setTextSize(16);
        }

        final DatabaseHelper databaseHelper = DatabaseHelper.getHelper(getActivity());
        String userId = PrefUtils.getFromPrefs(getActivity(), PrefUtils.PREFS_LOGIN_USERNAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);
        pairedDeviceList = (ListView) rootView.findViewById(R.id.pairedDevices);
        bluetoothStatus = rootView.findViewById(R.id.btooth_status);
        bluetoothStatusMsg = (TextView) rootView.findViewById(R.id.bluetooth_status_msg);
        searchNewBtn = (Button) rootView.findViewById(R.id.btn_Search_New_Device);
        searchNewBtn.setTypeface(CommonUtils.getInstance(getActivity()).getFontRobotoMedium());
        searchNewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btDeviceList.clear();

                    searchNewBtn.setEnabled(false);
                    searchNewBtn.setBackgroundColor(R.drawable.btn_layout_gray_light);
                    searchNewBTDevice();
            }
        });

        Button closeBtn = (Button) rootView.findViewById(R.id.btn_close);
        closeBtn.setTypeface(CommonUtils.getInstance(getActivity()).getFontRobotoMedium());
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String deviceAddress = CommonUtils.getDeviceAddress(getActivity());
                if (mSelectDeviceDialogDelegate != null){
                    mSelectDeviceDialogDelegate.onDeviceSelected( deviceAddress);
                }

                dismiss();
            }
        });

        final AppSettings appSettings = databaseHelper.getSettings(userId);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        getActivity().registerReceiver(ActionFoundReceiver, filter);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(null == bluetoothAdapter)
            return rootView;

        Set<BluetoothDevice> btDevices =  bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : btDevices) {
            btDeviceList.add(device);
            if(null != appSettings.getConnectionId() && appSettings.getConnectionId().equals(device.getAddress()))
            {
                ((MainActivity) getActivity()).setDeviceConnected(device.getName(), appSettings.getConnectionId());
            }
        }

        final NavigationDrawerBluetoothArrayAdapter btArrayAdapter = new NavigationDrawerBluetoothArrayAdapter(getActivity(), btDeviceList);
        pairedDeviceList.setAdapter(btArrayAdapter);

        pairedDeviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                BluetoothDevice btDevice = (BluetoothDevice) pairedDeviceList.getItemAtPosition(position);
                FragmentManager fragmentManager = getActivity().getFragmentManager();
                Log.d("Pairing device : ", btDevice.getName());
                try {
                    createBond(btDevice);
                    String bluetoothID = btDevice.getAddress();
                    appSettings.setConnectionId(bluetoothID);
                    databaseHelper.updateSettings(appSettings);

//                    String first_run = PrefUtils.getFromPrefs(getActivity(), PrefUtils.PREFS_FIRST_INSTALL_CYCLE, "YES");
//                    if( first_run.equals("YES")) {
//                        Bundle bundle = new Bundle();
//                        bundle.putString(BluetoothService.DEVICE_ADDRESS, ""+btDevice);
//                        bundle.putString(Utils.APP_MODE, Utils.APP_MODE_QUICK_MEASURE);
//                        FragmentMode fragment=new FragmentMode();
//                        fragment.setArguments(bundle);
//
//                        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, fragment.getClass().getName()).commit();
//                    }


                    pairedDeviceList.setItemsCanFocus(true);

                    RadioButton radiolistitem = (RadioButton) view.findViewById(R.id.blue_conn_radio);
                    radiolistitem.setChecked(true);

                    btArrayAdapter.notifyDataSetInvalidated();

                    if (null != appSettings.getConnectionId()) {
                        ((MainActivity) getActivity()).setDeviceConnected(btDevice.getName(), appSettings.getConnectionId());

                    }


                } catch (Exception e) {

                    System.out.println(e.getMessage());
                }
            }
        });

        //If bluetooth is off then it ask to allow turn on bluetooth device.
        if(bluetoothAdapter.getState()==BluetoothAdapter.STATE_OFF) {
            searchNewBTDevice();
        }

        //set height and width of dialog by getting screen height and width parameters.
        Display display = (getActivity()).getWindowManager().getDefaultDisplay();
        int width = display.getWidth() - 20;
        int height = display.getHeight() - 40;
        getDialog().getWindow().setLayout(width, height);

        return rootView;
    }

    public void show(android.support.v4.app.FragmentManager manager, String tag, SelectDeviceDialogDelegate selectDeviceDialogDelegate) {
        super.show(manager, tag);

        mSelectDeviceDialogDelegate = selectDeviceDialogDelegate;

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
            Toast.makeText(getActivity(), "Bluetooth NOT supported, Aborting!", Toast.LENGTH_LONG).show();
            return;
        } else {
            if (bluetoothAdapter.isEnabled()) {

                System.out.println("\nBluetooth is enabled...");

                new AlertDialog.Builder(getActivity())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setMessage("Please make sure the device is turned on, and press ok to begin search")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {

                                        // Starting the device discovery
                                        bluetoothAdapter.startDiscovery();

                                    }

                                }

                        )
                        .show();

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
                        searchNewBtn.setEnabled(true);
                        searchNewBtn.setBackgroundResource(R.drawable.btn_layout_orange);
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

//            selectedConnectionText.setVisibility(View.VISIBLE);
//            selectedConnectionText.animate().setDuration(shortAnimTime)
//                    .alpha(show ? 0 : 1)
//                    .setListener(new AnimatorListenerAdapter() {
//                        @Override
//                        public void onAnimationEnd(Animator animation) {
//                            selectedConnectionText.setVisibility(show ? View.GONE
//                                    : View.VISIBLE);
//                        }
//                    });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            bluetoothStatus.setVisibility(show ? View.VISIBLE : View.GONE);
            pairedDeviceList.setVisibility(show ? View.GONE : View.VISIBLE);
//            selectedConnectionText.setVisibility(show ? View.GONE : View.VISIBLE);
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


    private class NavigationDrawerBluetoothArrayAdapter extends BaseAdapter implements ListAdapter {

        private final Activity activity;
        private final List<BluetoothDevice> bluetoothDeviceList;
        int selectedPosition=0;
        private String userId;
        private DatabaseHelper db;

        public NavigationDrawerBluetoothArrayAdapter(Activity activity, List<BluetoothDevice> bluetoothDeviceList) {
            assert activity != null;
            assert bluetoothDeviceList != null;

            this.bluetoothDeviceList = bluetoothDeviceList;
            this.activity = activity;
        }

        @Override
        public int getCount() {
            if (null == bluetoothDeviceList)
            {
                return 0;
            }
            else
            {
                return bluetoothDeviceList.size();
            }
        }

        @Override
        public BluetoothDevice getItem(int position) {
            if (null == bluetoothDeviceList)
                return null;
            else
                return bluetoothDeviceList.get(position);
        }

        @Override
        public long getItemId(int position) {
//		BluetoothDevice bluetoothDevice = getItem(position);

            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = activity.getLayoutInflater().inflate(R.layout.bluetooth_device_list_item, null);
            db = DatabaseHelper.getHelper(convertView.getContext());
            TextView tvDeviceName = (TextView) convertView.findViewById(R.id.device_name);
            tvDeviceName.setTypeface(CommonUtils.getInstance(getActivity()).getFontRobotoMedium());
            TextView tvDeviceAddress = (TextView) convertView.findViewById(R.id.device_address);
            tvDeviceAddress.setTypeface(CommonUtils.getInstance(getActivity()).getFontRobotoRegular());
            TextView tvDevicePaired = (TextView) convertView.findViewById(R.id.device_paired);
            tvDevicePaired.setTypeface(CommonUtils.getInstance(getActivity()).getFontRobotoMedium());
            TextView pairDeviceBtn = (TextView) convertView.findViewById(R.id.pair_bluetooth_device);
            RadioButton radiobtn = (RadioButton)convertView.findViewById(R.id.blue_conn_radio);
            radiobtn.setVisibility(View.VISIBLE);

            BluetoothDevice bluetoothDevice = getItem(position);
            if (null != bluetoothDevice) {
                try {
                    tvDeviceName.setText(bluetoothDevice.getName());
                    tvDeviceAddress.setText(bluetoothDevice.getAddress());
                    tvDevicePaired.setText((bluetoothDevice.getBondState()==10)?"Not Paired":(bluetoothDevice.getBondState()==12)?"Paired":"Pairing");

                    radiobtn.setChecked(position == selectedPosition);
                    radiobtn.setTag(position);
				/*radiobtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectedPosition = (Integer)view.getTag();
                        notifyDataSetInvalidated();
                    }
                });*/
                    if(bluetoothDevice.getBondState() == 10){
                        pairDeviceBtn.setVisibility(View.VISIBLE);
                        radiobtn.setVisibility(View.INVISIBLE);
                    }else
                    {
                        pairDeviceBtn.setVisibility(View.INVISIBLE);
                    }

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            //it retrieve selected radio button value from database and set selected button as previous.
            radiobtn.setChecked(false);
            userId = PrefUtils.getFromPrefs(convertView.getContext() , PrefUtils.PREFS_LOGIN_USERNAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);
            AppSettings appSettings = db.getSettings(userId);

            if (null != appSettings.getConnectionId() && appSettings.getConnectionId().equals(bluetoothDevice.getAddress())) {
                RadioButton rb = (RadioButton) convertView.findViewById(R.id.blue_conn_radio);
                rb.setChecked(true);

                if (null != bluetoothDevice) {
                    try {
                        tvDevicePaired.setText((bluetoothDevice.getBondState() == 10) ? "Not Paired" : (bluetoothDevice.getBondState() == 12) ? "Paired" : "Pairing");

                    }catch(Exception e){
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                notifyDataSetChanged();
            }
            return convertView;
        }

    }
}
