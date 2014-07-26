package com.photosynq.app;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.Protocol;
import com.photosynq.app.utils.BluetoothService;
import com.photosynq.app.utils.CommonUtils;
import com.photosynq.app.utils.DataUtils;

public class SelectProtocolActivity extends ActionBarActivity {


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
			System.out.println("-------------------Protocol list arrayadapter is Empty()--------------");
			new ProtocolListAsync().execute();
		}
		else
		{
			System.out.println("-------------------Protocol list arrayadapter is not Empty()--------------");
		}
		
		protocolList.setOnItemClickListener(new OnItemClickListener() {
		    @Override
		    public void onItemClick(AdapterView<?> adapter, View view, int position, long id){
		    	Protocol protocol = (Protocol) protocolList.getItemAtPosition(position);
				Log.d("GEtting protocol id : ", protocol.getId());
				Intent intent = new Intent(getApplicationContext(),NewMeasurmentActivity.class);
				intent.putExtra(MainActivity.QUICK_MEASURE, true);
				intent.putExtra(DatabaseHelper.C_PROTOCOL_JSON, protocol.getProtocol_json());
				intent.putExtra(BluetoothService.DEVICE_ADDRESS, deviceAddress );
				try {
					StringBuffer dataString = new StringBuffer();
					dataString.append("var protocols={");
					//JSONArray protocolJsonArray = new JSONArray();					
					JSONObject detailProtocolObject = new JSONObject();
					detailProtocolObject.put("protocolid", protocol.getId());
					detailProtocolObject.put("protocol_name", protocol.getId());
					detailProtocolObject.put("macro_id", protocol.getMacroId());
					//protocolJsonArray.put(deatilProtocolObject);
					dataString.append("\""+protocol.getId()+"\""+":"+detailProtocolObject.toString());
					dataString.append("}");

					System.out.println("###### writing macros_variable.js :"+dataString);
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
		db = new DatabaseHelper(getApplicationContext());
		protocols = db.getAllProtocolsList();
		arrayadapter = new ProtocolArrayAdapter(this, protocols); 
		protocolList.setAdapter(arrayadapter);
		System.out.println("DBCLosing");
		db.closeDB();
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
