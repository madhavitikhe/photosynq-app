package com.photosynq.app.response;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Toast;

import com.photosynq.app.MainActivity;
import com.photosynq.app.ProjectModeFragment;
import com.photosynq.app.R;
import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.http.HTTPConnection;
import com.photosynq.app.http.PhotosynqResponse;
import com.photosynq.app.model.Option;
import com.photosynq.app.model.ProjectCreator;
import com.photosynq.app.model.Question;
import com.photosynq.app.model.ResearchProject;
import com.photosynq.app.utils.CommonUtils;
import com.photosynq.app.utils.Constants;
import com.photosynq.app.utils.PrefUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by shekhar on 9/19/14.
 */
public class UpdateProject implements PhotosynqResponse {
    private MainActivity navigationDrawer;
    private ProgressDialog mProgressDialog;

    public UpdateProject(MainActivity navigationDrawer, ProgressDialog progressDialog) {
        this.navigationDrawer = navigationDrawer;
        this.mProgressDialog = progressDialog;
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

        int currentPage = 1;
        int totalPages = 1;
        if (null != navigationDrawer) {
            navigationDrawer.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    navigationDrawer.setProgressBarVisibility(View.VISIBLE);
                }
            });
        }

        Date date = new Date();
        System.out.println("UpdateProject Start onResponseReceived: " + date.getTime());

        DatabaseHelper db = DatabaseHelper.getHelper(navigationDrawer);
//        db.openWriteDatabase();
//        db.openReadDatabase();
        JSONArray jArray;

        if (null != result) {
            if (result.equals(Constants.SERVER_NOT_ACCESSIBLE)) {
                Toast.makeText(navigationDrawer, R.string.server_not_reachable, Toast.LENGTH_LONG).show();
//                db.closeWriteDatabase();
//                db.closeReadDatabase();
                return;
            }

            try {
                JSONObject resultJsonObject = new JSONObject(result);

                if (resultJsonObject.has("projects")) {
                    currentPage = Integer.parseInt(resultJsonObject.getString("page"));
                    totalPages = Integer.parseInt(resultJsonObject.getString("total_pages"));


                    String authToken = PrefUtils.getFromPrefs(navigationDrawer, PrefUtils.PREFS_AUTH_TOKEN_KEY, PrefUtils.PREFS_DEFAULT_VAL);
                    String email = PrefUtils.getFromPrefs(navigationDrawer, PrefUtils.PREFS_LOGIN_USERNAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);

                    if (currentPage < totalPages) {
                        String strProjectListURI = Constants.PHOTOSYNQ_PROJECTS_LIST_URL
                                + "all=%d&page=%d&user_email=%s&user_token=%s";
                        //UpdateProject updateProject = new UpdateProject((MainActivity) this);
                        HTTPConnection httpConnection = new HTTPConnection();
                        httpConnection.delegate = this;
                        httpConnection.execute(navigationDrawer, String.format(strProjectListURI, 1, currentPage + 1, email, authToken), "GET");
                    }
                }


                if (resultJsonObject.has("projects")) {
                    jArray = resultJsonObject.getJSONArray("projects");

                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject jsonProject = jArray.getJSONObject(i);
                        String protocol_ids = jsonProject.getJSONArray("protocols_ids").toString().trim();

                        JSONObject projectImageUrl = jsonProject.getJSONObject("project_photo");//get project image url.
                        JSONObject creatorJsonObj = jsonProject.getJSONObject("creator");//get project creator infos.
                        JSONObject creatorAvatar = creatorJsonObj.getJSONObject("avatar");//
                        ProjectCreator pCreator = new ProjectCreator();
                        pCreator.setId(creatorJsonObj.getString("id"));
                        pCreator.setName(creatorJsonObj.getString("name"));
                        pCreator.setImageUrl(creatorAvatar.getString("thumb"));

                        ResearchProject rp = new ResearchProject(
                                jsonProject.getString("id"),
                                jsonProject.getString("name"),
                                jsonProject.getString("description"),
                                jsonProject.getString("directions_to_collaborators"),
                                creatorJsonObj.getString("id"),
                                jsonProject.getString("start_date"),
                                jsonProject.getString("end_date"),
                                projectImageUrl.getString("original"),
                                jsonProject.getString("beta"),
                                jsonProject.getString("is_contributed"),
                                protocol_ids.substring(1, protocol_ids.length() - 1)); // remove first and last square bracket and store as a comma separated string

                        try {
                            //get project creator information like id, name, profile_image.
                            ProjectCreator projectCreator = new ProjectCreator(
                                    creatorJsonObj.getString("id"),
                                    creatorJsonObj.getString("name"),
                                    creatorAvatar.getString("thumb"));

                            db.updateProjectLead(projectCreator);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        JSONArray customFields = jsonProject.getJSONArray("filters");
                        for (int j = 0; j < customFields.length(); j++) {
                            JSONObject jsonQuestion = customFields.getJSONObject(j);
                            int questionType = Integer.parseInt(jsonQuestion.getString("value_type"));
                            JSONArray optionValuesJArray = jsonQuestion.getJSONArray("value");
                            //Sometime option value is empty i.e we need to set "" parameter.
                            if (optionValuesJArray.length() == 0) {
                                Option option = new Option(jsonQuestion.getString("id"), "", jsonProject.getString("id"));
                                db.updateOption(option);
                            }
                            for (int k = 0; k < optionValuesJArray.length(); k++) {
                                if (Question.PROJECT_DEFINED == questionType) { //If question type is project_defined then save options.
                                    String getSingleOption = optionValuesJArray.getString(k);
                                    Option option = new Option(jsonQuestion.getString("id"), getSingleOption, jsonProject.getString("id"));
                                    db.updateOption(option);
                                } else if (Question.PHOTO_TYPE_DEFINED == questionType) { //If question type is photo_type then save options and option image.
                                    JSONObject options = optionValuesJArray.getJSONObject(k);
                                    String optionString = options.getString("answer");
                                    String optionImage = options.getString("medium");//get option image if question type is Photo_Type
                                    Option option = new Option(jsonQuestion.getString("id"), optionString + "," + optionImage, jsonProject.getString("id"));
                                    db.updateOption(option);
                                }
                            }

                            Question question = new Question(
                                    jsonQuestion.getString("id"),
                                    jsonProject.getString("id"),
                                    jsonQuestion.getString("label"),
                                    questionType);
                            db.updateQuestion(question);
                        }
                        db.updateResearchProject(rp);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

//        db.closeWriteDatabase();
//        db.closeReadDatabase();
        Date date1 = new Date();

        if (null != navigationDrawer) {
            navigationDrawer.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    try {
                        FragmentManager fragmentManager = navigationDrawer.getSupportFragmentManager();

                        ProjectModeFragment fragmentProjectList = (ProjectModeFragment) fragmentManager.findFragmentByTag(ProjectModeFragment.class.getName());
                        if (fragmentProjectList != null) {
                            fragmentProjectList.onResponseReceived(Constants.SUCCESS);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    navigationDrawer.setProgressBarVisibility(View.INVISIBLE);
                }

            });
        }

        System.out.println("UpdateProject End onResponseReceived: " + date1.getTime());
        //show progress dialog process on sync screen after sync button click
        int progress = (60 / totalPages) + 1;//60 means 60%, for projects. 60 projects + 20 protocols + 20 macros = 100
        CommonUtils.setProgress(navigationDrawer, mProgressDialog, progress);
    }
}
