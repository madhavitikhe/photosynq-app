package com.photosynq.app.response;

import android.content.Context;
import android.widget.Toast;

import com.photosynq.app.http.PhotosynqResponse;
import com.photosynq.app.R;
import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.Option;
import com.photosynq.app.model.ProjectLead;
import com.photosynq.app.model.Question;
import com.photosynq.app.model.ResearchProject;
import com.photosynq.app.utils.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;

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
        System.out.println("UpdateProject Start onResponseReceived: " + date.getTime());

        DatabaseHelper db = DatabaseHelper.getHelper(context);
        db.openWriteDatabase();
        db.openReadDatabase();
        JSONArray jArray;

        if(null!= result)
        {
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
                    JSONObject jsonProject = jArray.getJSONObject(i);
                    String protocol_ids = jsonProject.getJSONArray("protocols_ids").toString().trim();

                    ResearchProject rp = new ResearchProject(
                            jsonProject.getString("id"),
                            jsonProject.getString("name"),
                            jsonProject.getString("description"),
                            jsonProject.getString("directions_to_collaborators"),
                            jsonProject.getString("lead_id"),
                            jsonProject.getString("start_date"),
                            jsonProject.getString("end_date"),
                            jsonProject.getString("medium_image_url"),
                            jsonProject.getString("beta"),
                            protocol_ids.substring(1, protocol_ids.length()-1)); // remove first and last square bracket and store as a comma separated string

                    try {
                        String pleadString = jsonProject.getString("plead");
                        JSONObject pleadJson = new JSONObject(pleadString);

                        ProjectLead projectLead = new ProjectLead(
                                pleadJson.getString("id"),
                                pleadJson.getString("name"),
                                pleadJson.getString("data_count"),
                                pleadJson.getString("thumb_url"));

                        db.updateProjectLead(projectLead);

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    JSONArray customFields = jsonProject.getJSONArray("custom_fields");
                    for (int j = 0; j < customFields.length(); j++) {
                        JSONObject jsonQuestion = customFields.getJSONObject(j);
                        int questionType = Integer.parseInt(jsonQuestion.getString("value_type"));
                        Question question = new Question(
                                jsonQuestion.getString("id"),
                                jsonProject.getString("id"),
                                jsonQuestion.getString("label"),
                                questionType);
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

        db.closeWriteDatabase();
        db.closeReadDatabase();
        Date date1 = new Date();
        System.out.println("UpdateProject End onResponseReceived: " + date1.getTime());
    }
}
