package com.photosynq.app;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.photosynq.app.HTTP.PhotosynqResponse;
import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.Protocol;

public class UpdateProtocol implements PhotosynqResponse {
	DatabaseHelper db;
	Context context;
	public UpdateProtocol(Context context)
	{
		this.context = context;
	}

	@Override
	public void onResponseReceived(String result) {
	
		System.out.println("$$$$$$$ Protocol : "+result);
		JSONArray jArray;
		
		if(null!= result)
			{
			try {
				jArray = new JSONArray(result);
				for (int i = 0; i < jArray.length(); i++) {
					
					
					JSONObject obj = jArray.getJSONObject(i);
					Protocol protocol = new Protocol(
					obj.getString("id"),
					obj.getString("name"),
					obj.getString("quick_description"),
					obj.getString("protocol_name_in_arduino_code"),
					obj.getString("description"),
					obj.getString("macro_id"),
					obj.getString("slug"));
					
					
					db = new DatabaseHelper(context);
					
					db.updateProtocol(protocol);
					System.out.println("DBCLosing");
					db.closeDB();
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	}
}
