package com.photosynq.app;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.photosynq.app.HTTP.PhotosynqResponse;
import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.Protocol;

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
					String id = obj.getString("id");
					String protocoljson = obj.getString("protocol_json2").replaceFirst("\\{", "{\"protocol_id\"="+id+",");
					Protocol protocol = new Protocol(id,
							obj.getString("name"),
							protocoljson,
							obj.getString("description"),
							obj.getString("macro_id"), "slug");
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
