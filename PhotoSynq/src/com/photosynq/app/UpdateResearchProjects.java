package com.photosynq.app;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.photosynq.app.HTTP.PhotosynqResponse;
import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.Option;
import com.photosynq.app.model.Question;
import com.photosynq.app.model.ResearchProject;

public class UpdateResearchProjects implements PhotosynqResponse {

	DatabaseHelper db;
	Context context;
	public UpdateResearchProjects(Context context)
	{
		this.context = context;
	}
	
	@Override
	public void onResponseReceived(String result) {
		JSONArray jArray;
		
		if(null!= result)
			{
			try {
				jArray = new JSONArray(result);
				for (int i = 0; i < jArray.length(); i++) {
					JSONObject jsonProject = jArray.getJSONObject(i);
					String protocol_ids = jsonProject.getJSONArray("protocols_ids").toString().trim();

					ResearchProject rp = new ResearchProject(
					jsonProject.getString("id"),
					jsonProject.getString("name"),
					jsonProject.getString("description"),
					jsonProject.getString("directions_to_collaborators"),
					jsonProject.getString("start_date"),
					jsonProject.getString("end_date"),
					jsonProject.getString("medium_image_url"),
					jsonProject.getString("beta"),
					protocol_ids.substring(1, protocol_ids.length()-1)); // remove first and last square bracket and store as a comma separated string
					
					JSONArray customFields = jsonProject.getJSONArray("custom_fields");
					db = new DatabaseHelper(context);
					
					for (int j = 0; j < customFields.length(); j++) {
						JSONObject jsonQuestion = customFields.getJSONObject(j);
						Question question = new Question(jsonQuestion.getString("id"),
								jsonProject.getString("id"), 
								jsonQuestion.getString("label"));
						db.updateQuestion(question);
						
						//String options = jsonQuestion.getString("value");
						String[] options = jsonQuestion.getString("value").split(",");
						for (String opt : options) {
							Option option = new Option(jsonQuestion.getString("id"), opt, jsonProject.getString("id"));
							db.updateOption(option);
						}
						
					}
					
					
					
					
					db.updateResearchProject(rp);
					System.out.println("DBCLosing");
					db.closeDB();
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
