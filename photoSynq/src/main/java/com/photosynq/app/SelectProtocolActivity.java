package com.photosynq.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.Protocol;
import com.photosynq.app.navigationDrawer.Utils;
import com.photosynq.app.utils.BluetoothService;
import com.photosynq.app.utils.CommonUtils;
import com.photosynq.app.utils.DataUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class SelectProtocolActivity extends Activity {


	ListView protocolList;
	DatabaseHelper db;
	private String deviceAddress;
	List<Protocol> protocols;
	ProtocolArrayAdapter arrayadapter;
	private ProgressDialog pDialog;
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
		
		refreshProtocolList();
		
		if(arrayadapter.isEmpty())
		{
			new ProtocolListAsync().execute();
		}
		
		protocolList.setOnItemClickListener(new OnItemClickListener() {
		    @Override
		    public void onItemClick(AdapterView<?> adapter, View view, int position, long id){
		    	Protocol protocol = (Protocol) protocolList.getItemAtPosition(position);
				Log.d("GEtting protocol id : ", protocol.getId());
				Intent intent = new Intent(getApplicationContext(),NewMeasurmentActivity.class);
				intent.putExtra(Utils.APP_MODE, Utils.APP_MODE_QUICK_MEASURE);
				intent.putExtra(DatabaseHelper.C_PROTOCOL_JSON, protocol.getProtocol_json());
				intent.putExtra(BluetoothService.DEVICE_ADDRESS, deviceAddress );
				try {
					StringBuffer dataString = new StringBuffer();
					dataString.append("var protocols={");
					//JSONArray protocolJsonArray = new JSONArray();					
					JSONObject detailProtocolObject = new JSONObject();
					detailProtocolObject.put("protocolid", protocol.getId());
					detailProtocolObject.put("protocol_name", protocol.getName());
					detailProtocolObject.put("macro_id", protocol.getMacroId());
					//protocolJsonArray.put(deatilProtocolObject);
					dataString.append("\""+protocol.getId()+"\""+":"+detailProtocolObject.toString());
					dataString.append("}");

				//	System.out.println("###### writing macros_variable.js :"+dataString);
					System.out.println("###### writing macros_variable.js :......");
					CommonUtils.writeStringToFile(getApplicationContext(), "macros_variable.js",dataString.toString());

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				startActivity(intent);
		    }
		});
	}
	
	private void refreshProtocolList() {
		db = DatabaseHelper.getHelper(getApplicationContext());
		protocols = db.getAllProtocolsList();
		arrayadapter = new ProtocolArrayAdapter(this, protocols); 
		protocolList.setAdapter(arrayadapter);
	}
	
	private class ProtocolListAsync extends AsyncTask<Void, Void, Void> {
		 
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
           DataUtils.downloadData(getApplicationContext());
           pDialog = new ProgressDialog(SelectProtocolActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }
 
        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
             return null;
        }
 
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            refreshProtocolList();
         Toast.makeText(getApplicationContext(), "Protocol list up to date", Toast.LENGTH_SHORT).show();
        }
    }

	public void takeMeasurement(View view)
	{
		finish();
		startActivity(getIntent());
	}
}
