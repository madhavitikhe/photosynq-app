package com.photosynq.app.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.photosynq.app.MainActivity;
import com.photosynq.app.R;
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
 * <p/>
 * Download data from photosynq website, it return projects, protocols and macros list.
 */
public class SyncHandler {

    private Context context = null;
    private Activity activity = null;
    private MainActivity navigationDrawer;
    private ProgressBar progressBar;

    public static int ALL_SYNC_MODE = 0;
    public static int PROJECT_LIST_MODE = 1;
    public static int PROTOCOL_LIST_MODE = 2;
    public static int UPLOAD_RESULTS_MODE = 3;
    public static int ALL_SYNC_UI_MODE = 4;
    public static int ALL_SYNC_UI_MODE_CLEAR_CACHE = 5;


//    public SyncHandler(Context context) {
//        this.context = context;
//    }
//
//    public SyncHandler(MainActivity navigationDrawer){
//        this.navigationDrawer = navigationDrawer;
//        this.context = navigationDrawer;
//    }

    public SyncHandler(Context context) {
        this.context = context;
    }

    public SyncHandler(Activity activity, ProgressBar progressBar) {
        this.context = activity;
        this.activity = activity;
        this.progressBar = progressBar;
    }

    public SyncHandler(MainActivity navigationDrawer) {
        this.context = navigationDrawer;
        this.activity = navigationDrawer;
        this.navigationDrawer = navigationDrawer;
    }

    public int DoSync(int sync_mode) {

        if (sync_mode == PROJECT_LIST_MODE) {
            DatabaseHelper db = DatabaseHelper.getHelper(context);
            if (db.getAllProtocolsList().size() == 0) {
                sync_mode = ALL_SYNC_MODE;
            }
        }

        if (sync_mode == PROTOCOL_LIST_MODE) {
            DatabaseHelper db = DatabaseHelper.getHelper(context);
            if (db.getAllResearchProjects().size() == 0) {
                sync_mode = ALL_SYNC_MODE;
            }
        }

        new SyncTask().execute(sync_mode, -1);
        return 0;
    }

    public int DoSync(String projectId) {

        new SyncTask().execute(UPLOAD_RESULTS_MODE, Integer.parseInt(projectId));
        return 0;
    }

    private class SyncTask extends AsyncTask<Integer, Object, String> {
        @Override
        protected void onPreExecute() {
            if (null != progressBar) {
                progressBar.setVisibility(View.VISIBLE);
            }

            if (null != navigationDrawer) {
                navigationDrawer.setProgressBarVisibility(View.VISIBLE);
            }

            super.onPreExecute();
        }

        protected synchronized String doInBackground(Integer... SyncMode) {
            try {

                String isSyncInProgress = PrefUtils.getFromPrefs(context, PrefUtils.PREFS_IS_SYNC_IN_PROGRESS, "false");
                if (isSyncInProgress.equals("true")) {
                    System.out.println("sync already in progress");
                    return Constants.SUCCESS;
                }

                String isCheckedWifiSync = PrefUtils.getFromPrefs(context, PrefUtils.PREFS_SYNC_WIFI_ON, "0");
                if(isCheckedWifiSync.equals("1")) {

                    ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

                    if (mWifi != null && mWifi.isConnected() == false) {//if Wifi is connected

                        return Constants.SUCCESS;
                    }
                }

                PrefUtils.saveToPrefs(context, PrefUtils.PREFS_IS_SYNC_IN_PROGRESS, "true");

                final int syncMode = SyncMode[0];

                if (!CommonUtils.isConnected(context)) {

                    PrefUtils.saveToPrefs(context, PrefUtils.PREFS_IS_SYNC_IN_PROGRESS, "false");
                    return Constants.SERVER_NOT_ACCESSIBLE;
                }

                Log.d("sync_handler", "in async task");

                // Sync with clear cache
                if(syncMode == ALL_SYNC_UI_MODE_CLEAR_CACHE) {

                    final Activity mainActivity = (Activity)activity;

                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            new AlertDialog.Builder(mainActivity)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setTitle("Clear Cache")
                                    .setMessage("Do you want to really clear cache ?")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            ProgressDialog mProgressDialog = new ProgressDialog(context);
                                            mProgressDialog.setTitle("Syncing ...");
                                            mProgressDialog.setMessage("Download in progress ...");
                                            mProgressDialog.setProgressStyle(mProgressDialog.STYLE_HORIZONTAL);
                                            mProgressDialog.setProgress(0);
                                            mProgressDialog.setMax(100);
                                            mProgressDialog.setProgressNumberFormat(null);
                                            mProgressDialog.show();

                                            DatabaseHelper dbHelper = DatabaseHelper.getHelper(context);
                                            dbHelper.deleteAllData();

                                            syncData(ALL_SYNC_MODE, mProgressDialog);

                                        }

                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                            PrefUtils.saveToPrefs(context, PrefUtils.PREFS_IS_SYNC_IN_PROGRESS, "false");
                                        }
                                    })
                                    .show();
                        }
                    });

                }else if (syncMode == ALL_SYNC_UI_MODE){

                    final Activity mainActivity = (Activity)activity;
                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            ProgressDialog mProgressDialog = new ProgressDialog(context);
                            mProgressDialog.setTitle("Syncing ...");
                            mProgressDialog.setMessage("Download in progress ...");
                            mProgressDialog.setProgressStyle(mProgressDialog.STYLE_HORIZONTAL);
                            mProgressDialog.setProgress(0);
                            mProgressDialog.setMax(100);
                            mProgressDialog.setProgressNumberFormat(null);
                            mProgressDialog.show();

                            syncData(ALL_SYNC_MODE, mProgressDialog);
                        }
                    });

                }else if (syncMode == UPLOAD_RESULTS_MODE) {

                    int projectId = SyncMode[1];
                    syncData(syncMode, projectId);
                }else{

                    // Sync as per mode
                    syncData(syncMode, null);
                }

                return Constants.SUCCESS;

            } catch (Exception e) {
                e.printStackTrace();
                PrefUtils.saveToPrefs(context, PrefUtils.PREFS_IS_SYNC_IN_PROGRESS, "false");
                return Constants.SERVER_NOT_ACCESSIBLE;
            }

        }

        private void syncData(int syncMode, ProgressDialog mProgressDialog) {

            PrefUtils.saveToPrefs(context, PrefUtils.PREFS_CURRENT_LOCATION, null);
            String authToken = PrefUtils.getFromPrefs(context, PrefUtils.PREFS_AUTH_TOKEN_KEY, PrefUtils.PREFS_DEFAULT_VAL);
            String email = PrefUtils.getFromPrefs(context, PrefUtils.PREFS_LOGIN_USERNAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);

            HTTPConnection mProtocolListTask = null;
            HTTPConnection mMacroListTask = null;

            // Upload all unuploaded results
            if(syncMode == ALL_SYNC_MODE || syncMode == UPLOAD_RESULTS_MODE) {

                CommonUtils.uploadResults(context, -1);

//                DatabaseHelper db = DatabaseHelper.getHelper(context);
//                List<ProjectResult> listRecords = db.getAllUnUploadedResults();
//                for (ProjectResult projectResult : listRecords) {
//                    CommonUtils.uploadResults(context, projectResult.getProjectId(), projectResult.getId(), projectResult.getReading());
//                }

                if (syncMode == UPLOAD_RESULTS_MODE) {
                    PrefUtils.saveToPrefs(context, PrefUtils.PREFS_IS_SYNC_IN_PROGRESS, "false");
                }
            }

            // Download ProjectList
            if(syncMode == ALL_SYNC_MODE || syncMode == PROJECT_LIST_MODE || syncMode == PROTOCOL_LIST_MODE) {
                UpdateProject updateProject = new UpdateProject(activity, navigationDrawer, mProgressDialog);
                HTTPConnection mProjListTask = new HTTPConnection();
                mProjListTask.delegate = updateProject;
                mProjListTask
                        .execute(context, Constants.PHOTOSYNQ_PROJECTS_LIST_URL
                                + "all=1" + "&page=1"
                                + "&user_email=" + email + "&user_token="
                                + authToken, "GET");

                UpdateProtocol updateProtocol = new UpdateProtocol(activity, navigationDrawer, mProgressDialog);
                mProtocolListTask = new HTTPConnection();
                mProtocolListTask.delegate = updateProtocol;
                mProtocolListTask.execute(context,
                        Constants.PHOTOSYNQ_PROTOCOLS_LIST_URL + "user_email="
                                + email + "&user_token=" + authToken, "GET");


                UpdateMacro updateMacro = new UpdateMacro(activity, navigationDrawer, mProgressDialog);
                mMacroListTask = new HTTPConnection();
                mMacroListTask.delegate = updateMacro;
                mMacroListTask
                        .execute(context, Constants.PHOTOSYNQ_MACROS_LIST_URL
                                + "user_email=" + email + "&user_token="
                                + authToken, "GET");

            }
        }

        private void syncData(int syncMode, int projectId) {

            Spanned tt = Html.fromHtml("sdfsdf");

            PrefUtils.saveToPrefs(context, PrefUtils.PREFS_CURRENT_LOCATION, null);
            String authToken = PrefUtils.getFromPrefs(context, PrefUtils.PREFS_AUTH_TOKEN_KEY, PrefUtils.PREFS_DEFAULT_VAL);
            String email = PrefUtils.getFromPrefs(context, PrefUtils.PREFS_LOGIN_USERNAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);

            HTTPConnection mProtocolListTask = null;
            HTTPConnection mMacroListTask = null;

            // Upload all unuploaded results
            if(syncMode == UPLOAD_RESULTS_MODE) {

                CommonUtils.uploadResults(context, projectId);

//                DatabaseHelper db = DatabaseHelper.getHelper(context);
//                List<ProjectResult> listRecords = db.getAllUnUploadedResults();
//                for (ProjectResult projectResult : listRecords) {
//                    CommonUtils.uploadResults(context, projectResult.getProjectId(), projectResult.getId(), projectResult.getReading());
//                }

                if (syncMode == UPLOAD_RESULTS_MODE) {
                    PrefUtils.saveToPrefs(context, PrefUtils.PREFS_IS_SYNC_IN_PROGRESS, "false");
                }
            }

        }

        // This is called each time you call publishProgress()
        @Override
        protected void onProgressUpdate(Object... result) {
            //Do anything with response..
            PhotosynqResponse delegate = (PhotosynqResponse) result[0];
            if (null != delegate) {
                delegate.onResponseReceived((String) result[1]);
            }
            if (null == result) {
                Log.d("sync_handler", "No results returned");
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

            if (null != progressBar) {
                progressBar.setVisibility(View.INVISIBLE);
            }

            if (null != navigationDrawer) {
                navigationDrawer.setProgressBarVisibility(View.INVISIBLE);
            }

            if (result.equals(Constants.SERVER_NOT_ACCESSIBLE)){

//                if (null != mProgressDialog) {
//                    mProgressDialog.dismiss();
//                }

                //Toast.makeText(context, R.string.server_not_reachable, Toast.LENGTH_LONG).show();
            }
        }

    }
}
