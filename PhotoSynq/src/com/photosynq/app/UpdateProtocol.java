package com.photosynq.app;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.photosynq.app.HTTP.PhotosynqResponse;
import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.Protocol;
import com.photosynq.app.utils.CommonUtils;

public class UpdateProtocol implements PhotosynqResponse {
	DatabaseHelper db;
	Context context;

	public UpdateProtocol(Context context) {
		this.context = context;
	}

	@Override
	public void onResponseReceived(String result) {

		JSONArray jArray;

		if (null != result) {
			try {
				jArray = new JSONArray(result);
				for (int i = 0; i < jArray.length(); i++) {

					JSONObject obj = jArray.getJSONObject(i);
					Protocol protocol = new Protocol(obj.getString("id"),
							obj.getString("name"),
							obj.getString("protocol_json"),
							obj.getString("description"),
							obj.getString("macro_id"), obj.getString("slug"));
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

//		// Writing macros_variable.js file with protocol and macro relations
//		try {
//			StringBuffer dataString = new StringBuffer(); 
//			db = new DatabaseHelper(context);
//			List<Protocol> protocols = db.getAllProtocolsList();
//			JSONArray protocolJsonArray = new JSONArray();
//			for (Protocol protocol : protocols) {
//				JSONObject protocolObject = new JSONObject();
//				protocolObject.put("protocolid", protocol.getId());
//				protocolObject.put("macroid", protocol.getMacroId());
//				protocolJsonArray.put(protocolObject);
//			}
//			db.closeDB();
//			dataString.append("var protocols="+protocolJsonArray.toString());
//			CommonUtils.writeStringToFile(context, "macros_variable.js",dataString.toString());
//			
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		
		

	}
}
