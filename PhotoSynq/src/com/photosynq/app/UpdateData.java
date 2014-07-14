package com.photosynq.app;

import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import com.photosynq.app.HTTP.PhotosynqResponse;
import com.photosynq.app.db.DatabaseHelper;

public class UpdateData implements PhotosynqResponse{

	private Activity activity;
	private Context context;
	private String rowid;
	DatabaseHelper db;
	AlertDialog.Builder alertDialogBuilder;
	AlertDialog alert;
	public UpdateData(Context context,Activity activity, String rowid)
	{
		this.context = context;
		this.activity = activity;
		this.rowid = rowid;
	}
	@Override
	public void onResponseReceived(String result) {
		System.out.println("data update result :"+result);
		try {
			JSONObject jo = new JSONObject(result);
			String status = jo.getString("status");
			Toast.makeText(context, jo.getString("notice"), Toast.LENGTH_SHORT).show();
			alertDialogBuilder = new AlertDialog.Builder(activity);
			alertDialogBuilder.setTitle(R.string.keep_data);
			alertDialogBuilder.setMessage(R.string.data_uploaded_to_server);
			alertDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				 public void onClick(DialogInterface dialog, int which) {
					 
			 }
	      });
			alert = alertDialogBuilder.create();
	        alert.show();
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
