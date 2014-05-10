package com.photosynq.app;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.Protocol;
import com.photosynq.app.utils.BluetoothService;
import com.photosynq.app.utils.CommonUtils;

public class SelectProtocolActivity extends ActionBarActivity {


	ListView protocolList;
	DatabaseHelper db;
	private String deviceAddress; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_protocol);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			deviceAddress = extras.getString(BluetoothService.DEVICE_ADDRESS);
		}
		
		
		// Initialize ListView
		protocolList = (ListView) findViewById(R.id.protocol_list_view);
		
		
		db = new DatabaseHelper(getApplicationContext());
		List<Protocol> protocols = db.getAllProtocolsList();
		ProtocolArrayAdapter arrayadapter = new ProtocolArrayAdapter(this, protocols); 
		protocolList.setAdapter(arrayadapter);
		System.out.println("DBCLosing");
		db.closeDB();
		
		protocolList.setOnItemClickListener(new OnItemClickListener() {
		    @Override
		    public void onItemClick(AdapterView<?> adapter, View view, int position, long id){
		    	Protocol protocol = (Protocol) protocolList.getItemAtPosition(position);
				Log.d("GEtting protocol id : ", protocol.getId());
				Intent intent = new Intent(getApplicationContext(),ResultActivity.class);
				intent.putExtra(DatabaseHelper.C_PROTOCOL_JSON, protocol.getProtocol_json());
				intent.putExtra(BluetoothService.DEVICE_ADDRESS, deviceAddress );
				try {
					StringBuffer dataString = new StringBuffer();
					JSONArray protocolJsonArray = new JSONArray();
					JSONObject protocolObject = new JSONObject();
					protocolObject.put("protocolid", protocol.getId());
					protocolObject.put("macroid", protocol.getMacroId());
					protocolJsonArray.put(protocolObject);
					dataString.append("var protocols=" + protocolJsonArray.toString());
					CommonUtils.writeStringToFile(getApplicationContext(), "macros_variable.js",dataString.toString());

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				startActivity(intent);
		    }
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.select_protocol, menu);
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
	
	public void takeMeasurement(View view)
	{
		finish();
		startActivity(getIntent());
	}
	
	public void selectProtocol(View view)
	{
		
	}

}
