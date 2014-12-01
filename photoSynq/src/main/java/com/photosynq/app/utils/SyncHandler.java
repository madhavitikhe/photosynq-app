package com.photosynq.app.utils;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.photosynq.app.HTTP.PhotosynqResponse;
import com.photosynq.app.R;
import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.ProjectResult;
import com.photosynq.app.navigationDrawer.FragmentProgress;
import com.photosynq.app.navigationDrawer.FragmentProjectList;
import com.photosynq.app.navigationDrawer.FragmentSelectProtocol;
import com.photosynq.app.navigationDrawer.FragmentSync;
import com.photosynq.app.navigationDrawer.NavigationDrawer;
import com.photosynq.app.response.UpdateData;
import com.photosynq.app.response.UpdateMacro;
import com.photosynq.app.response.UpdateProject;
import com.photosynq.app.response.UpdateProtocol;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by kalpesh on 30/11/14.
 *
 *  Download data from photosynq website, it return projects, protocols and macros list.
 *
 */
public class SyncHandler {

    Context context = null;
    NavigationDrawer navigationDrawer = null;

    public SyncHandler(Context context) {
        this.context = context;
    }

    public SyncHandler(NavigationDrawer navigationDrawer){
        this.navigationDrawer = navigationDrawer;
        this.context = navigationDrawer;
    }

    public int DoSync() {
        new SyncTask().execute();
        return 0;
    }

    private class SyncTask extends AsyncTask<String, Object, String> {

        @Override
        protected void onPreExecute() {

            if(navigationDrawer != null) {
                FragmentManager fragmentManager = navigationDrawer.getFragmentManager();
                fragmentManager.beginTransaction().add(R.id.content_frame, new FragmentProgress(), FragmentProgress.class.getName()).commit();
            }

            super.onPreExecute();
        }

        protected synchronized String doInBackground(String... ServerInfo) {
            try {

                PrefUtils.saveToPrefs(context, PrefUtils.PREFS_CURRENT_LOCATION,
                        null);
                String authToken = PrefUtils
                        .getFromPrefs(context, PrefUtils.PREFS_AUTH_TOKEN_KEY,
                                PrefUtils.PREFS_DEFAULT_VAL);
                String email = PrefUtils.getFromPrefs(context,
                        PrefUtils.PREFS_LOGIN_USERNAME_KEY,
                        PrefUtils.PREFS_DEFAULT_VAL);

                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response = null;
                HttpPost postRequest = null;
                HttpGet getRequest = null;
                String responseString = null;
                if (!CommonUtils.isConnected(context)) {
                    return Constants.SERVER_NOT_ACCESSIBLE;
                }
                Log.d("PHOTOSYNQ-HTTPConnection", "in async task");
                try {
                    // Download ProjectList
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
                    publishProgress(new Object[]{new UpdateProject(context),responseString});

                    // Download Protocols
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

                    // Upload all unuploaded results
                    DatabaseHelper db = DatabaseHelper.getHelper(context);
                    List<ProjectResult> listRecords = db.getAllUnUploadedResults();
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
                            e.printStackTrace();
                            return Constants.SERVER_NOT_ACCESSIBLE;
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                            return Constants.SERVER_NOT_ACCESSIBLE;
                        }

                        String strDataURI = Constants.PHOTOSYNQ_DATA_URL
                                            + projectResult.getProjectId() + "/data.json";

                        Log.d("PHOTOSYNQ-HTTPConnection", "$$$$ URI" + strDataURI);

                        postRequest = new HttpPost(strDataURI);
                        if (null != input) {
                            postRequest.setEntity(input);
                        }
                        Log.d("PHOTOSYNQ-HTTPConnection", "$$$$ Executing POST request");
                        response = httpclient.execute(postRequest);

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

                        publishProgress(new Object[]{new UpdateData(context, projectResult.getId()),responseString});
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

            if(navigationDrawer != null) {
                try {
                    FragmentManager fragmentManager = navigationDrawer.getFragmentManager();
                    Fragment fragment = fragmentManager.findFragmentByTag(FragmentProgress.class.getName());
                    if (fragment != null) {
                        fragmentManager.beginTransaction().remove(fragment).commit();
                    }
                    FragmentProjectList fragmentProjectList = (FragmentProjectList) fragmentManager.findFragmentByTag(FragmentProjectList.class.getName());
                    if (fragmentProjectList != null) {
                        fragmentProjectList.onResponseReceived(result);
                    }
                    FragmentSelectProtocol fragmentSelectProtocol = (FragmentSelectProtocol) fragmentManager.findFragmentByTag(FragmentSelectProtocol.class.getName());
                    if (fragmentSelectProtocol != null) {
                        fragmentSelectProtocol.onResponseReceived(result);
                    }
                    FragmentSync fragmentSync = (FragmentSync) fragmentManager.findFragmentByTag(FragmentSync.class.getName());
                    if (fragmentSync != null) {
                        fragmentSync.onResponseReceived(result);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
