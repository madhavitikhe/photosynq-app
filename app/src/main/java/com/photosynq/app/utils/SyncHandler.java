package com.photosynq.app.utils;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.photosynq.app.http.PhotosynqResponse;
import com.photosynq.app.MainActivity;
import com.photosynq.app.R;
import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.ProjectResult;
//??
//import com.photosynq.app.navigationDrawer.FragmentProgress;
//import com.photosynq.app.navigationDrawer.FragmentProjectList;
//import com.photosynq.app.navigationDrawer.FragmentSelectProtocol;
//import com.photosynq.app.navigationDrawer.FragmentSync;
//import com.photosynq.app.navigationDrawer.NavigationDrawer;
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

    Context context = null;
    MainActivity navigationDrawer = null;

    public static int ALL_SYNC_MODE = 0;
    public static int PROJECT_LIST_MODE = 1;
    public static int PROTOCOL_LIST_MODE = 2;
    public static int UPLOAD_RESULTS_MODE = 3;

    public SyncHandler(Context context) {
        this.context = context;
    }

    public SyncHandler(MainActivity navigationDrawer){
        this.navigationDrawer = navigationDrawer;
        this.context = navigationDrawer;
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
//??
//            if(navigationDrawer != null) {
//                FragmentManager fragmentManager = navigationDrawer.getFragmentManager();
//                fragmentManager.beginTransaction().add(R.id.content_frame, new FragmentProgress(), FragmentProgress.class.getName()).commit();
//            }

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
                if (!CommonUtils.isConnected(context)) {
                    return Constants.SERVER_NOT_ACCESSIBLE;
                }
                Log.d("PHOTOSYNQ-HTTPConnection", "in async task");
                try {
                    // Download ProjectList
                    if(syncMode == ALL_SYNC_MODE || syncMode == PROJECT_LIST_MODE) {
                        String strProjectListURI = Constants.PHOTOSYNQ_PROJECTS_LIST_URL
                                + "user_email=" + email + "&user_token=" + authToken;

                        Log.d("PHOTOSYNQ-HTTPConnection", "$$$$ URI" + strProjectListURI);
                        getRequest = new HttpGet(strProjectListURI);
                        Log.d("PHOTOSYNQ-HTTPConnection", "$$$$ Executing GET request");
                        response = httpclient.execute(getRequest);

                        if (null != response) {
                            StatusLine statusLine = response.getStatusLine();
                            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                                ByteArrayOutputStream out = new ByteArrayOutputStream();
                                response.getEntity().writeTo(out);
                                out.close();
                                responseString = out.toString();
                            } else {
                                //Closes the connection.
                                response.getEntity().getContent().close();
                                throw new IOException(statusLine.getReasonPhrase());
                            }
                        }
                        publishProgress(new Object[]{new UpdateProject(context), responseString});
                    }
                    // Download Protocols
                    if(syncMode == ALL_SYNC_MODE || syncMode == PROTOCOL_LIST_MODE) {
                        String strProtocolURI = Constants.PHOTOSYNQ_PROTOCOLS_LIST_URL
                                + "user_email=" + email + "&user_token=" + authToken;

                        Log.d("PHOTOSYNQ-HTTPConnection", "$$$$ URI" + strProtocolURI);
                        getRequest = new HttpGet(strProtocolURI);
                        Log.d("PHOTOSYNQ-HTTPConnection", "$$$$ Executing GET request");
                        response = httpclient.execute(getRequest);

                        if (null != response) {
                            StatusLine statusLine = response.getStatusLine();
                            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                                ByteArrayOutputStream out = new ByteArrayOutputStream();
                                response.getEntity().writeTo(out);
                                out.close();
                                responseString = out.toString();
                            } else {
                                //Closes the connection.
                                response.getEntity().getContent().close();
                                throw new IOException(statusLine.getReasonPhrase());
                            }
                        }
                        publishProgress(new Object[]{new UpdateProtocol(context), responseString});

                        // Download Macros
                        String strMacroURI = Constants.PHOTOSYNQ_MACROS_LIST_URL
                                + "user_email=" + email + "&user_token=" + authToken;

                        Log.d("PHOTOSYNQ-HTTPConnection", "$$$$ URI" + strMacroURI);
                        getRequest = new HttpGet(strMacroURI);
                        Log.d("PHOTOSYNQ-HTTPConnection", "$$$$ Executing GET request");
                        response = httpclient.execute(getRequest);

                        if (null != response) {
                            StatusLine statusLine = response.getStatusLine();
                            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                                ByteArrayOutputStream out = new ByteArrayOutputStream();
                                response.getEntity().writeTo(out);
                                out.close();
                                responseString = out.toString();
                            } else {
                                //Closes the connection.
                                response.getEntity().getContent().close();
                                throw new IOException(statusLine.getReasonPhrase());
                            }
                        }
                        publishProgress(new Object[]{new UpdateMacro(context), responseString});
                    }

                    // Upload all unuploaded results
                    if(syncMode == ALL_SYNC_MODE || syncMode == UPLOAD_RESULTS_MODE) {
                        DatabaseHelper db = DatabaseHelper.getHelper(context);
                        List<ProjectResult> listRecords = db.getAllUnUploadedResults();
                        for (ProjectResult projectResult : listRecords) {
                            CommonUtils.uploadResults(context, projectResult.getProjectId(), projectResult.getId(), projectResult.getReading());
                        }
                    }


                } catch (ClientProtocolException e) {
                    return Constants.SERVER_NOT_ACCESSIBLE;
                } catch (IOException e) {
                    return Constants.SERVER_NOT_ACCESSIBLE;
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
                delegate.onResponseReceived((String)result[1]);
            }
            if (null == result)
            {
                Log.d("PHOTOSYNQ-HTTPConnection","No results returned");
            }
            super.onProgressUpdate(result);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
//??
//            if(navigationDrawer != null) {
//                try {
//                    FragmentManager fragmentManager = navigationDrawer.getFragmentManager();
//                    Fragment fragment = fragmentManager.findFragmentByTag(FragmentProgress.class.getName());
//                    if (fragment != null) {
//                        fragmentManager.beginTransaction().remove(fragment).commit();
//                    }
//                    FragmentProjectList fragmentProjectList = (FragmentProjectList) fragmentManager.findFragmentByTag(FragmentProjectList.class.getName());
//                    if (fragmentProjectList != null) {
//                        fragmentProjectList.onResponseReceived(result);
//                    }
//                    FragmentSelectProtocol fragmentSelectProtocol = (FragmentSelectProtocol) fragmentManager.findFragmentByTag(FragmentSelectProtocol.class.getName());
//                    if (fragmentSelectProtocol != null) {
//                        fragmentSelectProtocol.onResponseReceived(result);
//                    }
//                    FragmentSync fragmentSync = (FragmentSync) fragmentManager.findFragmentByTag(FragmentSync.class.getName());
//                    if (fragmentSync != null) {
//                        fragmentSync.onResponseReceived(result);
//                    }
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//            }
        }
    }
}
