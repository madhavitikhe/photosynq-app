package com.photosynq.app.response;

import android.content.Context;
import android.widget.Toast;

import com.photosynq.app.HTTP.HTTPConnection;
import com.photosynq.app.HTTP.PhotosynqResponse;
import com.photosynq.app.R;
import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.utils.PrefUtils;

import org.json.JSONException;
import org.json.JSONObject;

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
            if(result.equals(HTTPConnection.SERVER_NOT_ACCESSIBLE))
            {
                //Toast.makeText(context, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                String sync_interval = PrefUtils.getFromPrefs(context,PrefUtils.PREFS_SAVE_SYNC_INTERVAL,PrefUtils.PREFS_DEFAULT_VAL);
                Toast.makeText(context, context.getResources().getString(R.string.error_sending_data,sync_interval), Toast.LENGTH_LONG).show();
                return;
            }

			JSONObject jo = new JSONObject(result);
			String status = jo.getString("status");
			Toast.makeText(context, jo.getString("notice"), Toast.LENGTH_SHORT).show();

			if (status.toUpperCase().equals("SUCCESS"))
			{
                Toast.makeText(context, R.string.data_uploaded_to_server, Toast.LENGTH_LONG).show();
				db = DatabaseHelper.getHelper(context);
                System.out.println("Deleting row id:"+rowid);
				db.deleteResult(rowid);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		
	}

}
