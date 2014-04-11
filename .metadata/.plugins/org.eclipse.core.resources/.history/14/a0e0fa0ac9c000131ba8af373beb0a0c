package com.example.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JSONBuilder {
	
	public String buildJSONString(InputStream is ){
	    InputStreamReader inputStreamReader = new InputStreamReader(is);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			StringBuilder inputStringBuilder = new StringBuilder();
			String line;
			
			try {
				line = bufferedReader.readLine();
				
				while(line != null){
		            inputStringBuilder.append(line);inputStringBuilder.append('\n');
		            line = bufferedReader.readLine();
				}
		 
			} catch (IOException e) {
				Log.d("Exception",e.toString());
			}
			
	       String JSONString=inputStringBuilder.toString();
	       return JSONString;
	    	
	    }
	 
	//////////////////////////////////////////////////////////////////////////////////////////
	    
	    public JSONObject buildJSONObject(String JSONString){
	    	JSONObject js = null;
	    	try {
				 js=new JSONObject(JSONString);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				Log.d("Exception",e.toString());
			}
	    	return js;
	    	
	    }

}
