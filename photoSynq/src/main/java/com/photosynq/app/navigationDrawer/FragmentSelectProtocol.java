package com.photosynq.app.navigationDrawer;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.photosynq.app.HTTP.PhotosynqResponse;
import com.photosynq.app.NewMeasurmentActivity;
import com.photosynq.app.ProtocolArrayAdapter;
import com.photosynq.app.R;
import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.AppSettings;
import com.photosynq.app.model.Protocol;
import com.photosynq.app.utils.BluetoothService;
import com.photosynq.app.utils.CommonUtils;
import com.photosynq.app.utils.Constants;
import com.photosynq.app.utils.PrefUtils;
import com.photosynq.app.utils.SyncHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class FragmentSelectProtocol extends Fragment implements PhotosynqResponse{

    ListView protocolList;
    private String deviceAddress;
    List<Protocol> protocols;
    ProtocolArrayAdapter arrayadapter;
    private DatabaseHelper db;
    private String userId;
    AppSettings appSettings;
	
	public static FragmentSelectProtocol newInstance() {
		FragmentSelectProtocol fragment = new FragmentSelectProtocol();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ActionBar actionBar = getActivity().getActionBar();
        if(actionBar!=null) {
            actionBar.show();
            actionBar.setTitle(getResources().getString(R.string.title_activity_select_protocol));
        }

        View rootView = inflater.inflate(R.layout.activity_select_protocol, container, false);
        db = DatabaseHelper.getHelper(getActivity());
        userId = PrefUtils.getFromPrefs(getActivity(), PrefUtils.PREFS_LOGIN_USERNAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);
        appSettings = db.getSettings(userId);

        Bundle extras = getArguments();
        if (extras != null) {
            deviceAddress = extras.getString(BluetoothService.DEVICE_ADDRESS);
        }

        if(deviceAddress == null){
            appSettings = db.getSettings(userId);
            String btDevice = appSettings.getConnectionId();
            deviceAddress = btDevice;
        }
        // Initialize ListView
        protocolList = (ListView) rootView.findViewById(R.id.protocol_list_view);
        showFewProtocolList();

        if(arrayadapter.isEmpty())
        {
            SyncHandler syncHandler = new SyncHandler((NavigationDrawer)getActivity());
            syncHandler.DoSync(SyncHandler.PROTOCOL_LIST_MODE);
        }

        final Button showAllProtocolsBtn = new Button(getActivity());
        showAllProtocolsBtn.setText(R.string.show_all_protocols);
        protocolList.addFooterView(showAllProtocolsBtn);

        showAllProtocolsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(showAllProtocolsBtn.getText().equals("Show All Protocols")){
                    showAllProtocolList();
                    showAllProtocolsBtn.setText("Less Protocol List");
                }else if (showAllProtocolsBtn.getText().equals("Less Protocol List")){
                    showFewProtocolList();
                    showAllProtocolsBtn.setText("Show All Protocols");
                }


            }
        });

        protocolList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                Protocol protocol = (Protocol) protocolList.getItemAtPosition(position);
                Log.d("GEtting protocol id : ", protocol.getId());
                Intent intent = new Intent(getActivity(),NewMeasurmentActivity.class);
                intent.putExtra(Utils.APP_MODE, Utils.APP_MODE_QUICK_MEASURE);
                intent.putExtra(DatabaseHelper.C_PROTOCOL_JSON, protocol.getProtocol_json());
                intent.putExtra(BluetoothService.DEVICE_ADDRESS, deviceAddress);
                intent.putExtra(Protocol.NAME, protocol.getName());
                intent.putExtra(Protocol.DESCRIPTION, protocol.getDescription());
                try {
                    StringBuffer dataString = new StringBuffer();
                    dataString.append("var protocols={");
                    //JSONArray protocolJsonArray = new JSONArray();
                    JSONObject detailProtocolObject = new JSONObject();
                    detailProtocolObject.put("protocolid", protocol.getId());
                    detailProtocolObject.put("protocol_name", protocol.getName());
                    detailProtocolObject.put("macro_id", protocol.getMacroId());
                    //protocolJsonArray.put(deatilProtocolObject);
                    dataString.append("\"" + protocol.getId() + "\"" + ":" + detailProtocolObject.toString());
                    dataString.append("}");

                    //	System.out.println("###### writing macros_variable.js :"+dataString);
                    System.out.println("###### writing macros_variable.js :......");
                    CommonUtils.writeStringToFile(getActivity(), "macros_variable.js", dataString.toString());

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                startActivity(intent);
            }
        });

        rootView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT ));
        return rootView;
    }

    private void showFewProtocolList() {
        db = DatabaseHelper.getHelper(getActivity());
        protocols = db.getFewProtocolList();
        arrayadapter = new ProtocolArrayAdapter(getActivity(), protocols);
        protocolList.setAdapter(arrayadapter);
    }

    private void showAllProtocolList() {
        db = DatabaseHelper.getHelper(getActivity());
        protocols = db.getAllProtocolsList();
        arrayadapter = new ProtocolArrayAdapter(getActivity(), protocols);
        protocolList.setAdapter(arrayadapter);
    }

    @Override
    public void onResponseReceived(String result) {

        if(result.equals(Constants.SERVER_NOT_ACCESSIBLE)){
            Toast.makeText(getActivity(), R.string.server_not_reachable, Toast.LENGTH_LONG).show();
        }else {
            showFewProtocolList();
            Toast.makeText(getActivity(), "Protocol list up to date", Toast.LENGTH_SHORT).show();
        }
    }

//    public void takeMeasurement(View view) {
////        finish();
////        startActivity(getIntent());
//    }
}
