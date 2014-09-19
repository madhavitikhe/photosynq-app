package com.photosynq.app.response;

import android.content.Context;
import android.widget.Toast;

import com.photosynq.app.HTTP.HTTPConnection;
import com.photosynq.app.HTTP.PhotosynqResponse;
import com.photosynq.app.R;
import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.Option;
import com.photosynq.app.model.Question;
import com.photosynq.app.model.ResearchProject;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by shekhar on 9/19/14.
 */
public class UpdateProject implements PhotosynqResponse {
    private Context context;
    public UpdateProject(Context context)
    {
        this.context = context;
    }
    @Override
    public void onResponseReceived(String result) {
        DatabaseHelper db = DatabaseHelper.getHelper(context);
        JSONArray jArray;

        if(null!= result)
        {
            if(result.equals(HTTPConnection.SERVER_NOT_ACCESSIBLE))
            {
                Toast.makeText(context, R.string.server_not_reachable, Toast.LENGTH_LONG).show();
                return;
            }

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
                    for (int j = 0; j < customFields.length(); j++) {
                        JSONObject jsonQuestion = customFields.getJSONObject(j);
                        Question question = new Question(jsonQuestion.getString("id"),
                                jsonProject.getString("id"),
                                jsonQuestion.getString("label"),Integer.parseInt(jsonQuestion.getString("value_type")));
                        db.updateQuestion(question);

                        String[] options = jsonQuestion.getString("value").split(",");
                        for (String opt : options) {
                            Option option = new Option(jsonQuestion.getString("id"), opt, jsonProject.getString("id"));
                            db.updateOption(option);
                        }

                    }
                    db.updateResearchProject(rp);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
