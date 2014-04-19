package com.photosynq.app;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.photosynq.app.HTTP.PhotosynqResponse;
import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.Macro;

public class UpdateMacro implements PhotosynqResponse{

	DatabaseHelper db;
	Context context;
	public UpdateMacro(Context context)
	{
		this.context = context;
	}

	@Override
	public void onResponseReceived(String result) {
	
		System.out.println("$$$$$$$ Macro : "+result);
		JSONArray jArray;
		
		if(null!= result)
			{
			try {
				Log.d("PHOTOSYNQ-MainActivity", result);
				jArray = new JSONArray(result);
				for (int i = 0; i < jArray.length(); i++) {
					
					
					JSONObject obj = jArray.getJSONObject(i);
					Macro macro = new Macro(
					obj.getString("id"),
					obj.getString("name"),
					obj.getString("description"),
					obj.getString("default_x_axis"),
					obj.getString("default_y_axis"),
					obj.getString("javascript_code"),
					obj.getString("json_data"),
					obj.getString("slug"));
					
					
					db = new DatabaseHelper(context);
					
					db.createMacro(macro);
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
