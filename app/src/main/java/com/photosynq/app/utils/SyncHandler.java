package com.photosynq.app.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.photosynq.app.MainActivity;
import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.http.HTTPConnection;
import com.photosynq.app.http.PhotosynqResponse;
import com.photosynq.app.model.ProjectResult;
import com.photosynq.app.response.UpdateMacro;
import com.photosynq.app.response.UpdateProject;
import com.photosynq.app.response.UpdateProtocol;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by kalpesh on 30/11/14.
 *
 *  Download data from photosynq website, it return projects, protocols and macros list.
 *
 */
public class SyncHandler {

    private Context context = null;
    private MainActivity navigationDrawer;
    private ProgressBar progressBar;

    public static int ALL_SYNC_MODE = 0;
    public static int PROJECT_LIST_MODE = 1;
    public static int PROTOCOL_LIST_MODE = 2;
    public static int UPLOAD_RESULTS_MODE = 3;


//    public SyncHandler(Context context) {
//        this.context = context;
//    }
//
//    public SyncHandler(MainActivity navigationDrawer){
//        this.navigationDrawer = navigationDrawer;
//        this.context = navigationDrawer;
//    }

    public SyncHandler(Context context, ProgressBar progressBar) {
        this.context = context;
        this.progressBar = progressBar;
    }

    public SyncHandler(MainActivity navigationDrawer) {
        this.context = navigationDrawer;
        this.navigationDrawer = navigationDrawer;
    }

    public int DoSync(int sync_mode) {

        if(sync_mode == PROJECT_LIST_MODE){
            DatabaseHelper db = DatabaseHelper.getHelper(context);
            if(db.getAllProtocolsList().size() == 0){
                sync_mode = ALL_SYNC_MODE;
            }
        }

        if(sync_mode == PROTOCOL_LIST_MODE) {
            DatabaseHelper db = DatabaseHelper.getHelper(context);
            if(db.getAllResearchProjects().size() == 0){
                sync_mode = ALL_SYNC_MODE;
            }
        }

        new SyncTask().execute(sync_mode);
        return 0;
    }

    private class SyncTask extends AsyncTask<Integer, Object, String> {

        @Override
        protected void onPreExecute() {
            if(null != progressBar){
                progressBar.setVisibility(View.VISIBLE);
            }

            if(null != navigationDrawer) {
                navigationDrawer.setProgressBarVisibility(View.VISIBLE);
            }

            super.onPreExecute();
        }

        protected synchronized String doInBackground(Integer... SyncMode) {
            try {

                int syncMode = SyncMode[0];
                PrefUtils.saveToPrefs(context, PrefUtils.PREFS_CURRENT_LOCATION, null);
                String authToken = PrefUtils.getFromPrefs(context, PrefUtils.PREFS_AUTH_TOKEN_KEY, PrefUtils.PREFS_DEFAULT_VAL);
                String email = PrefUtils.getFromPrefs(context, PrefUtils.PREFS_LOGIN_USERNAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);

                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response = null;
                HttpGet getRequest = null;
                String responseString = null;
                HTTPConnection mProtocolListTask = null;
                HTTPConnection mMacroListTask = null;
                if (!CommonUtils.isConnected(context)) {
                    return Constants.SERVER_NOT_ACCESSIBLE;
                }
                Log.d("PHOTOSYNQ-HTTPConnection", "in async task");
                    // Download ProjectList
                    if(syncMode == ALL_SYNC_MODE || syncMode == PROJECT_LIST_MODE || syncMode == PROTOCOL_LIST_MODE) {
                        UpdateProject updateProject = new UpdateProject((MainActivity) context);
                        HTTPConnection mProjListTask = new HTTPConnection();
                        mProjListTask.delegate = updateProject;
                        mProjListTask
                                .execute(context,Constants.PHOTOSYNQ_PROJECTS_LIST_URL
                                        + "all=1"+"&page=1"
                                        + "&user_email=" + email + "&user_token="
                                        + authToken, "GET");

                        UpdateProtocol updateProtocol = new UpdateProtocol((MainActivity) context);
                        mProtocolListTask = new HTTPConnection();
                        mProtocolListTask.delegate = updateProtocol;
                        mProtocolListTask.execute(context,
                                Constants.PHOTOSYNQ_PROTOCOLS_LIST_URL + "user_email="
                                        + email + "&user_token=" + authToken, "GET");


                        UpdateMacro updateMacro = new UpdateMacro((MainActivity) context);
                        mMacroListTask = new HTTPConnection();
                        mMacroListTask.delegate = updateMacro;
                        mMacroListTask
                                .execute(context, Constants.PHOTOSYNQ_MACROS_LIST_URL
                                        + "user_email=" + email + "&user_token="
                                        + authToken, "GET");

                    }

                    // Upload all unuploaded results
                    if(syncMode == ALL_SYNC_MODE || syncMode == UPLOAD_RESULTS_MODE) {
                        DatabaseHelper db = DatabaseHelper.getHelper(context);
                        List<ProjectResult> listRecords = db.getAllUnUploadedResults();
                        for (ProjectResult projectResult : listRecords) {
                            CommonUtils.uploadResults(context, projectResult.getProjectId(), projectResult.getId(), projectResult.getReading());
                        }
                    }

                return Constants.SUCCESS;

            } catch (Exception e) {
                e.printStackTrace();
                return Constants.SERVER_NOT_ACCESSIBLE;
            }

        }

        // This is called each time you call publishProgress()
        @Override
        protected void onProgressUpdate(Object... result) {
            //Do anything with response..
            PhotosynqResponse delegate = (PhotosynqResponse)result[0];
            if(null!=delegate)
            {
                delegate.onResponseReceived((String) result[1]);
            }
            if (null == result)
            {
                Log.d("PHOTOSYNQ-HTTPConnection", "No results returned");
            }
            super.onProgressUpdate(result);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

//            if(navigationDrawer != null) {
//                try {
//
//                    FragmentManager fragmentManager = navigationDrawer.getSupportFragmentManager();
//
//                    ProjectModeFragment fragmentProjectList = (ProjectModeFragment) fragmentManager.findFragmentByTag(ProjectModeFragment.class.getName());
//                    if (fragmentProjectList != null) {
//                        fragmentProjectList.onResponseReceived(result);
//                    }
//                    QuickModeFragment fragmentSelectProtocol = (QuickModeFragment) fragmentManager.findFragmentByTag(QuickModeFragment.class.getName());
//                    if (fragmentSelectProtocol != null) {
//                        fragmentSelectProtocol.onResponseReceived(result);
//                    }
//                    SyncFragment fragmentSync = (SyncFragment) fragmentManager.findFragmentByTag(SyncFragment.class.getName());
//                    if (fragmentSync != null) {
//                        fragmentSync.onResponseReceived(result);
//                    }
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//            }

            if(null != progressBar){
                progressBar.setVisibility(View.INVISIBLE);
            }

            if(null != navigationDrawer){
                navigationDrawer.setProgressBarVisibility(View.INVISIBLE);
            }
        }

    }

    private void getResponse(HttpResponse response){
        HttpClient httpclient = new DefaultHttpClient();
        String responseString = null;
        String authToken = PrefUtils.getFromPrefs(context, PrefUtils.PREFS_AUTH_TOKEN_KEY, PrefUtils.PREFS_DEFAULT_VAL);
        String email = PrefUtils.getFromPrefs(context, PrefUtils.PREFS_LOGIN_USERNAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);
        String strProjectListURI = Constants.PHOTOSYNQ_PROJECTS_LIST_URL
                + "all=%d&page=%d&user_email=%s&user_token=%s";

        if (null != response) {
            try {
                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    out.close();
                    responseString = out.toString();
                    JSONObject resultJsonObject = new JSONObject(responseString);
                    handleProgress(new UpdateProject(navigationDrawer), responseString);
                    String status = resultJsonObject.getString("status");
                    if (status.equals("success")) {
                        int currentPage = Integer.parseInt(resultJsonObject.getString("page"));
                        int totalPages = Integer.parseInt(resultJsonObject.getString("total_pages"));
                            HttpResponse secondResponse = httpclient.execute(new HttpGet(String.format(strProjectListURI, 1, currentPage + 1, email, authToken)));
                            getResponse(secondResponse);
                    }
                } else {
                    //Closes the connection.
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        }

    //publishProgress method is not accesible here, then need to write custom method.
    private void handleProgress(PhotosynqResponse delegate, String responseString){
        //Do anything with response..
        if(null!=delegate)
        {
            delegate.onResponseReceived(responseString);
        }
        if (null == delegate)
        {
            Log.d("PHOTOSYNQ-HTTPConnection", "No results returned");
        }
    }
}
