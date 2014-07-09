package com.photosynq.app;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.photosynq.app.HTTP.PhotosynqResponse;
import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.Macro;
import com.photosynq.app.utils.CommonUtils;

public class UpdateMacro implements PhotosynqResponse {

	DatabaseHelper db;
	Context context;

	public UpdateMacro(Context context) {
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
					Macro macro = new Macro(obj.getString("id"),
							obj.getString("name"),
							obj.getString("description"),
							obj.getString("default_x_axis"),
							obj.getString("default_y_axis"),
							obj.getString("javascript_code"),
							"slug");

					db = new DatabaseHelper(context);

					db.updateMacro(macro);
					System.out.println("DBCLosing");
					db.closeDB();
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// Writing macros.js file with all macro functions
		StringBuffer dataString = new StringBuffer();
		db = new DatabaseHelper(context);
		List<Macro> macros = db.getAllMacros();
		for (Macro macro : macros) {
			dataString.append("function macro_" + macro.getId() + "(json){");
			dataString.append(System.getProperty("line.separator"));
			dataString.append(macro.getJavascriptCode().replaceAll("\\r\\n", System.getProperty("line.separator"))); //replacing ctrl+m characters
			dataString.append(System.getProperty("line.separator") + " }");
			dataString.append(System.getProperty("line.separator"));
			dataString.append(System.getProperty("line.separator"));
		}
		db.closeDB();
		CommonUtils.writeStringToFile(context, "macros.js",dataString.toString());
	}
}
