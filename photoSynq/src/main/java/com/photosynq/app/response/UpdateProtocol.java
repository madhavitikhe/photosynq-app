package com.photosynq.app.response;

import android.content.Context;
import android.widget.Toast;

import com.photosynq.app.HTTP.HTTPConnection;
import com.photosynq.app.HTTP.PhotosynqResponse;
import com.photosynq.app.R;
import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.Protocol;

import org.json.JSONArray;
import org.json.JSONObject;

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
    public void onResponseReceived(String result) {

        JSONArray jArray;
        DatabaseHelper db = DatabaseHelper.getHelper(context);
        if (null != result) {
            if(result.equals(HTTPConnection.SERVER_NOT_ACCESSIBLE))
            {
                Toast.makeText(context, R.string.server_not_reachable, Toast.LENGTH_LONG).show();
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
                            obj.getString("macro_id"), "slug");
                    db.updateProtocol(protocol);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
