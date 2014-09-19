package com.photosynq.app.utils;

import android.content.Context;

import com.photosynq.app.HTTP.HTTPConnection;
import com.photosynq.app.response.UpdateData;
import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.Data;
import com.photosynq.app.model.ProjectResult;
import com.photosynq.app.model.Question;
import com.photosynq.app.response.UpdateMacro;
import com.photosynq.app.response.UpdateProject;
import com.photosynq.app.response.UpdateProtocol;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class DataUtils {
/**
 * Download data from photosynq website, it return projects, protocols and macros list. 
 */
	public static void downloadData(Context context) 
	{
		System.out.println("Downloading data..............");
		//if (CommonUtils.isConnected(context))
		//{
			DatabaseHelper db;
			String authToken;
			String email;
			HTTPConnection mProtocolListTask = null;
			HTTPConnection mMacroListTask = null;
			HTTPConnection mUpdateDataTask = null;

			PrefUtils.saveToPrefs(context, PrefUtils.PREFS_CURRENT_LOCATION,
					null);
			authToken = PrefUtils
					.getFromPrefs(context, PrefUtils.PREFS_AUTH_TOKEN_KEY,
							PrefUtils.PREFS_DEFAULT_VAL);
			email = PrefUtils.getFromPrefs(context,
					PrefUtils.PREFS_LOGIN_USERNAME_KEY,
					PrefUtils.PREFS_DEFAULT_VAL);

            UpdateProject updateProject = new UpdateProject(context);
			HTTPConnection mProjListTask = new HTTPConnection();
            mProjListTask.delegate = updateProject;
			mProjListTask
					.execute(context,HTTPConnection.PHOTOSYNQ_PROJECTS_LIST_URL
							+ "user_email=" + email + "&user_token="
							+ authToken, "GET");


            UpdateProtocol updateProtocol = new UpdateProtocol(context);
			mProtocolListTask = new HTTPConnection();
            mProtocolListTask.delegate = updateProtocol;
			mProtocolListTask.execute(context,
					HTTPConnection.PHOTOSYNQ_PROTOCOLS_LIST_URL + "user_email="
							+ email + "&user_token=" + authToken, "GET");


            UpdateMacro updateMacro = new UpdateMacro(context);
			mMacroListTask = new HTTPConnection();
            mMacroListTask.delegate = updateMacro;
			mMacroListTask
					.execute(context,HTTPConnection.PHOTOSYNQ_MACROS_LIST_URL
							+ "user_email=" + email + "&user_token="
							+ authToken, "GET");

			//db = new DatabaseHelper(context);
			db = DatabaseHelper.getHelper(context);
			List<ProjectResult> listRecords = db.getAllUnUploadedResults();
			//db.closeDB();
			for (ProjectResult projectResult : listRecords) {
				StringEntity input = null;
				JSONObject request_data = new JSONObject();

				try {
					JSONObject jo = new JSONObject(projectResult.getReading());
					request_data.put("user_email", email);
					request_data.put("user_token", authToken);
					request_data.put("data", jo);
					input = new StringEntity(request_data.toString());
					input.setContentType("application/json");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				UpdateData updateData = new UpdateData(context,projectResult.getId());
				mUpdateDataTask = new HTTPConnection(input);
                mUpdateDataTask.delegate = updateData;
				mUpdateDataTask.execute(context,HTTPConnection.PHOTOSYNQ_DATA_URL
						+ projectResult.getProjectId() + "/data.json", "POST");
			}
		//}
	}
	
	/**
	 * This function is call when user selects 'Auto Increment' option, this
	 * function get input text(from,to and repeat) from user and calculates 
	 * auto increment values and stored it into populatesValues variable,
	 * cycle performs (to*repeat=total) times.
	 * Ex. 
	 * From    1
	 * To      2
	 * Repeat  3
	 * 
	 * PopulatesValues are --
	 * 		1	2
	 * 		1	2
	 * 		1	2
	 */
	public static String getAutoIncrementedValue(Context ctx,String question_id, String index) {
        if(Integer.parseInt(index) == -1)
            return "-2";
		
		String userId = PrefUtils.getFromPrefs(ctx , PrefUtils.PREFS_LOGIN_USERNAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);
		DatabaseHelper db = DatabaseHelper.getHelper(ctx);
		String projectId = db.getSettings(userId).getProjectId();
		Question question = db.getQuestionForProject(projectId, question_id);
		Data data = db.getData(userId, projectId, question.getQuestionId());
		String[] items = data.getValue().split(",");
		 int from = Integer.parseInt(items[0]);
		 int to = Integer.parseInt(items[1]);
		 int repeat = Integer.parseInt(items[2]);
		 ArrayList<Integer> populatedValues = new ArrayList<Integer>();
 		 for(int i=from;i<=to;i++){
			 for(int j=0;j<repeat;j++){
				 populatedValues.add(i);
				
			 }
		 }

        if(Integer.parseInt(index) > populatedValues.size()-1)
        return "-1";

        return populatedValues.get(Integer.parseInt(index)).toString();
	}
}
