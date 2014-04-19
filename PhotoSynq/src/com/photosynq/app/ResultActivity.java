package com.photosynq.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import com.photosynq.app.model.ResearchProject;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

public class ResultActivity extends ActionBarActivity {

	ConnectThread mConnectThread;
	ConnectedThread mConnectedThread;

	ResearchProject researchProject;
	BluetoothAdapter btAdapter;
//	BTDeviceItem btDeviceItem;
	ProgressBar results_progressBar;
	TextView waiting_tv;
	
	boolean is_quick_measurement=false;
	private static final String MAKE_INVISIBLE = "make_invisible";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);
		
		researchProject=((MyApplication) this.getApplication()).getResearchProject();
		btAdapter=((MyApplication) this.getApplication()).getBtAdapter();
		//btDeviceItem=((MyApplication) this.getApplication()).getBtDeviceItem();
		
		
		results_progressBar=(ProgressBar) findViewById(R.id.results_progressBar);
		waiting_tv=(TextView)findViewById(R.id.waiting_tv);
		
		is_quick_measurement=((MyApplication) this.getApplication()).isQuickMeasurement();
		
		//connect(btDeviceItem.getDevice());


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.result, menu);
		return true;
	}
	
	private void connect(BluetoothDevice device) {
		ConnectThread connectThread=new ConnectThread(device,this);
		connectThread.start();
	}
	
private class ConnectThread extends Thread {
		

		private final UUID MY_UUID=UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	   
		
		private final BluetoothDevice mmDevice;
		private final BluetoothSocket mmSocket;
		Context context;
		
				
			public ConnectThread(BluetoothDevice device, Context context)
			{
				this.context=context;
				Log.d("Test Thread", "in constructor");
				Log.d("Test Thread", device.getName());
				BluetoothSocket tmp=null;
				mmDevice=device;
				
				BluetoothDevice actual = btAdapter.getRemoteDevice(mmDevice.getAddress());
				if(actual.getBondState()==device.BOND_BONDED){
					
					Toast.makeText(getApplicationContext(),"is bonded",0).show();
				}
				
				try{
					tmp = actual.createRfcommSocketToServiceRecord(MY_UUID);
				}catch(Exception e){
					Log.d("Test Thread", e.toString());
				}
				mmSocket=tmp;
				
			}

			@SuppressLint("NewApi")
			@Override
			public void run() {
		
				btAdapter.cancelDiscovery();
				try {
					if(mmSocket.isConnected())
					{
					//handle error
					
					}
					mmSocket.connect();
				} catch (IOException e) {
					
					
					try {
						mmSocket.close();
					} catch (IOException closeException) {
						// TODO Auto-generated catch block
						Log.d("Test Thread", "io exception");
						closeException.printStackTrace();
					}//catch
				}
				
				// Do work to manage the connection (in a separate thread)
		        	manageConnectedSocket(mmSocket);

				}//run
			
			
			
			
			private void manageConnectedSocket(BluetoothSocket socket) {
		      
		       ConnectedThread connectedThread=new ConnectedThread(socket);
		       connectedThread.start();
		       try {
				connectedThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		       String outputFromDevice=connectedThread.getString();
		       connectedThread.cancel();
		       
	       
		       Intent intent=new Intent(context,DisplayResultsActivity.class);
		       intent.putExtra("output", outputFromDevice);
		       context.startActivity(intent);
		       intent.addCategory(ACTIVITY_SERVICE);
			}
			
			public String buildResultsString(JSONObject results)
			{
				StringBuilder sb=new StringBuilder();
				
				 Iterator<?> keys = results.keys();
				 while( keys.hasNext() ){
			            
			            try {
			            	String key = (String)keys.next();
							String value=(String) results.get(key);
							sb.append(key+":"+value+"/n");
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			           
			        }
				 return sb.toString();
			}
		

			@SuppressLint("NewApi")
			public void cancel()
			{
				try {
					
					mmSocket.close();
					if(!mmSocket.isConnected())
					{
						//handle error
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}//cancel
		}
	
	
	
	private class ConnectedThread extends Thread {
	    private final BluetoothSocket mmSocket;
	    private final BufferedReader mmInStream;
	    private final OutputStream mmOutStream;
		private String totalResultString;
	 
	    @SuppressLint("NewApi")
		public ConnectedThread(BluetoothSocket socket) {
	        mmSocket = socket;
	        InputStream tmpIn = null;
	        OutputStream tmpOut = null;
	        
	        if(mmSocket.isConnected())
	        {
	        	Log.d("Test Thread", "is connected");
	        }

	        try {
	            tmpIn = socket.getInputStream();
	            tmpOut = socket.getOutputStream();
	        } catch (IOException e) 
	        {
	        	Log.d("Test Thread",e.toString());
	        }
	 
	        mmInStream = new BufferedReader(new InputStreamReader(tmpIn,Charset.forName("US_ASCII")));
	        mmOutStream = tmpOut;
	        
	    }
	    
	    public String getString()
	    {
	    	return this.totalResultString;
	    }
	 
	    public void run() {
	    	
	        byte[] buffer = new byte[1024];  // buffer store for the stream
	        int bytes; // bytes returned from read()

	        StringBuffer textBuffer=new StringBuffer();
	       
         	String protocol= "[{\"protocol_name\":\"fluorescence\",\"baselines\":[1,1,1,1],\"averages\":1,\"wait\":0,\"cal_true\":0,\"analog_averages\":12,\"act_light\":20,\"pulsesize\":50,\"pulsedistance\":3000,\"actintensity1\":100,\"actintensity2\":100,\"measintensity\":3,\"calintensity\":255,\"pulses\":[50,50,50,50],\"detectors\":[[34],[34],[34],[34]],\"measlights\":[[15],[15],[15],[15]],\"act\":[2,1,2,2]}]";
	        byte[] protocolbyte;
			try {
				protocolbyte = protocol.getBytes("US-ASCII");
				write(protocolbyte);
			} catch (UnsupportedEncodingException e1) {

			}
         	
	        while (true) {
	            try {        
	            	
	            	// Read from the InputStream
	                String line  = mmInStream.readLine();
	                Log.d("Test Thread", "output from device"+" "+line);
	                if(line.endsWith("!")){
	                
	                	textBuffer.append(line);
	                	this.totalResultString=textBuffer.toString();
	                	break;
	                }
	                textBuffer.append(line);
	            
	            } catch (IOException e) {
	               Log.d("Test Thread", e.toString());
	            }
	          
	        }
	        
	        
	    }
	 
	    public void write(byte[] bytes) {
	        try {
	            mmOutStream.write(bytes);
	        } catch (IOException e) { }
	    }
	    
	  

	    public void cancel() {
	        try {
	            mmSocket.close();
	        } catch (IOException e) { }
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

}
