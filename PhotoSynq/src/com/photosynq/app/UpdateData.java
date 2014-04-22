package com.photosynq.app;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.widget.Toast;

import com.photosynq.app.HTTP.PhotosynqResponse;
import com.photosynq.app.db.DatabaseHelper;

public class UpdateData implements PhotosynqResponse{

	private Context context;
	private String rowid;
	DatabaseHelper db;
	public UpdateData(Context context, String rowid)
	{
		this.context = context;
		this.rowid = rowid;
	}
	@Override
	public void onResponseReceived(String result) {
		System.out.println("data update result :"+result);
		try {
			JSONObject jo = new JSONObject(result);
			String status = jo.getString("status");
			Toast.makeText(context, jo.getString("notice"), Toast.LENGTH_SHORT).show();
			if (status.toUpperCase().equals("SUCCESS"))
			{
			
				if(!rowid.equals("NONE"))
				{
					db = new DatabaseHelper(context);
					System.out.println("Deleting row id:"+rowid);
					db.deleteResult(rowid);
					db.closeDB();
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
