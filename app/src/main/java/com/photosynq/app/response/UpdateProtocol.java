package com.photosynq.app.response;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Toast;

import com.photosynq.app.MainActivity;
import com.photosynq.app.QuickModeFragment;
import com.photosynq.app.http.PhotosynqResponse;
import com.photosynq.app.R;
import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.Protocol;
import com.photosynq.app.utils.CommonUtils;
import com.photosynq.app.utils.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by shekhar on 9/19/14.
 */
public class UpdateProtocol implements PhotosynqResponse {
    private MainActivity navigationDrawer;
    private ProgressDialog mProgressDialog;

    public UpdateProtocol(MainActivity navigationDrawer, ProgressDialog progressDialog)
    {
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

        if(null != navigationDrawer) {
            navigationDrawer.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    navigationDrawer.setProgressBarVisibility(View.VISIBLE);
                }
            });
        }

        Date date = new Date();
        System.out.println("UpdateProtocol Start onResponseReceived: " + date.getTime());

        JSONArray jArray;
        DatabaseHelper db = DatabaseHelper.getHelper(navigationDrawer);
//        db.openWriteDatabase();
//        db.openReadDatabase();
        if (null != result) {
            if(result.equals(Constants.SERVER_NOT_ACCESSIBLE))
            {
                Toast.makeText(navigationDrawer, R.string.server_not_reachable, Toast.LENGTH_LONG).show();
//                db.closeWriteDatabase();
//                db.closeReadDatabase();
                return;
            }

            try {
                JSONObject resultJsonObject = new JSONObject(result);

                if (resultJsonObject.has("protocols")) {
                    String newobj = resultJsonObject.getString("protocols");
                    jArray = new JSONArray(newobj);
                    for (int i = 0; i < jArray.length(); i++) {

                        JSONObject obj = jArray.getJSONObject(i);
                        String id = obj.getString("id");
                        Protocol protocol = new Protocol(id,
                                obj.getString("name"),
                                obj.getString("protocol_json"),
                                obj.getString("description"),
                                obj.getString("macro_id"), "slug",
                                obj.getString("pre_selected"));
                        db.updateProtocol(protocol);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

//        db.closeWriteDatabase();
//        db.closeReadDatabase();
        Date date1 = new Date();

        if(null != navigationDrawer) {
            navigationDrawer.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {

                        FragmentManager fragmentManager = navigationDrawer.getSupportFragmentManager();

                        QuickModeFragment fragmentSelectProtocol = (QuickModeFragment) fragmentManager.findFragmentByTag(QuickModeFragment.class.getName());
                        if (fragmentSelectProtocol != null) {
                            fragmentSelectProtocol.onResponseReceived(Constants.SUCCESS);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    navigationDrawer.setProgressBarVisibility(View.INVISIBLE);
                }

            });
        }

        System.out.println("UpdateProtocol End onResponseReceived: " + date1.getTime());
        //show progress dialog process on sync screen after sync button click
        CommonUtils.setProgress(navigationDrawer, mProgressDialog, 20);
    }
}
