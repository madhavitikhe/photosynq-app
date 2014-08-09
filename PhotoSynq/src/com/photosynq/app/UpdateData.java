package com.photosynq.app;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
	private NotificationManager mManager;
	
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
			if(null != activity)
			{
//			alertDialogBuilder = new AlertDialog.Builder(activity);
//			alertDialogBuilder.setTitle(R.string.keep_data);
//			alertDialogBuilder.setMessage(R.string.data_uploaded_to_server);
//			alertDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//				 public void onClick(DialogInterface dialog, int which) {
//					 activity.finish();
//			 }
//	      });
//			alert = alertDialogBuilder.create();
//	        alert.show();
				Toast.makeText(context, R.string.data_uploaded_to_server, Toast.LENGTH_LONG).show();
			}
			if (status.toUpperCase().equals("SUCCESS"))
			{
			
				if(!rowid.equals("NONE"))
				{
					//db = new DatabaseHelper(context);
					db = DatabaseHelper.getHelper(context);
							
					System.out.println("Deleting row id:"+rowid);
					db.deleteResult(rowid);
					//db.closeDB();
				}
				
				//Generate sync notification.
				mManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
			       Intent intent1 = new Intent(context,MainActivity.class);
			     
			       Notification notification = new Notification(R.drawable.ic_launcher, context.getString(R.string.sync_successful), System.currentTimeMillis());
			       intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_CLEAR_TOP);
			 
			       PendingIntent pendingNotificationIntent = PendingIntent.getActivity( context,0, intent1,PendingIntent.FLAG_UPDATE_CURRENT);
			       notification.flags |= Notification.FLAG_AUTO_CANCEL;
			       notification.setLatestEventInfo(context,context.getString(R.string.sync_successful), "List is up to date!", pendingNotificationIntent);
			       System.out.println("-------------------MyAlarmService--------------------");
			       mManager.notify(0, notification);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
