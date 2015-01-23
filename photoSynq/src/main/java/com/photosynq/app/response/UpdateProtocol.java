package com.photosynq.app.response;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.photosynq.app.HTTP.HTTPConnection;
import com.photosynq.app.HTTP.PhotosynqResponse;
import com.photosynq.app.R;
import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.Protocol;
import com.photosynq.app.utils.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Time;
import java.util.Date;

/**
 * Created by shekhar on 9/19/14.
 */
public class UpdateProtocol implements PhotosynqResponse {
    private Context context;

    public UpdateProtocol(Context context)
    {
        this.context = context;
    }
    @Override
    public void onResponseReceived(final String result) {

        Thread t = new Thread(new Runnable() {
            public void run() {
                processResult(result);
            }
        });

        t.start();

    }

    private void processResult(String result) {
        Date date = new Date();
        System.out.println("UpdateProtocol Start onResponseReceived: " + date.getTime());

        JSONArray jArray;
        DatabaseHelper db = DatabaseHelper.getHelper(context);
        db.openWriteDatabase();
        db.openReadDatabase();
        if (null != result) {
            if(result.equals(Constants.SERVER_NOT_ACCESSIBLE))
            {
                Toast.makeText(context, R.string.server_not_reachable, Toast.LENGTH_LONG).show();
                db.closeWriteDatabase();
                db.closeReadDatabase();
                return;
            }

            try {
                jArray = new JSONArray(result);
                for (int i = 0; i < jArray.length(); i++) {

                    JSONObject obj = jArray.getJSONObject(i);
                    String id = obj.getString("id");
                    Protocol protocol = new Protocol(id,
                            obj.getString("name"),
                            obj.getString("protocol_json2"),
                            obj.getString("description"),
                            obj.getString("macro_id"), "slug",
                            obj.getString("pre_selected"));
                    db.updateProtocol(protocol);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        db.closeWriteDatabase();
        db.closeReadDatabase();
        Date date1 = new Date();
        System.out.println("UpdateProtocol End onResponseReceived: " + date1.getTime());
    }
}
