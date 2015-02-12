package com.photosynq.app;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.ProjectResult;
import com.photosynq.app.utils.Constants;
import com.photosynq.app.utils.PrefUtils;
import com.photosynq.app.utils.SyncHandler;

import org.json.JSONException;

import java.io.File;
import java.io.UnsupportedEncodingException;


public class DisplayResultsActivity extends ActionBarActivity {

    String projectId;
    String reading;
    String protocolJson;
    String appMode;

    Button keep;
    Button discard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_results);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Result");

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            projectId = extras.getString(DatabaseHelper.C_PROJECT_ID);
            reading = extras.getString(DatabaseHelper.C_READING);
            protocolJson = extras.getString(DatabaseHelper.C_PROTOCOL_JSON);
            appMode = extras.getString(Constants.APP_MODE);
            System.out.println(this.getClass().getName()+"############app mode="+appMode);
        }
        keep = (Button)findViewById(R.id.keep_btn);
        discard = (Button)findViewById(R.id.discard_btn);

        if(appMode.equals(Constants.APP_MODE_QUICK_MEASURE))
        {
            keep.setText("Return");
            keep.setVisibility(View.VISIBLE);
            discard.setVisibility(View.INVISIBLE);
        }
        reloadWebview();
    }

    private void  reloadWebview()
    {
        WebView webview = (WebView) findViewById(R.id.webView1);
        webview.clearCache(true); // Clear cache. This Mandates to load webview fresh. This is required because we are dynamically writing javascript files.
        String url = "file:///" + this.getExternalFilesDir(null)+ File.separator+"cellphone.html";
        webview.loadUrl(url);
        webview.getSettings().setJavaScriptEnabled(true);

    }
    public void keep_click(View view) throws UnsupportedEncodingException, JSONException {

        if(appMode.equals(Constants.APP_MODE_QUICK_MEASURE))
        {
            finish();
        }
        else
        {
            int index = Integer.parseInt(PrefUtils.getFromPrefs(this, PrefUtils.PREFS_QUESTION_INDEX, "1"));
            PrefUtils.saveToPrefs(this, PrefUtils.PREFS_QUESTION_INDEX, ""+ (index+1));

            DatabaseHelper databaseHelper = DatabaseHelper.getHelper(getApplicationContext());
            ProjectResult result = new ProjectResult(projectId, reading, "N");
            databaseHelper.createResult(result);

            SyncHandler syncHandler = new SyncHandler(getApplicationContext());
            syncHandler.DoSync(SyncHandler.UPLOAD_RESULTS_MODE);

            view.setVisibility(View.INVISIBLE);
            discard.setVisibility(View.INVISIBLE);

            finish();
        }
    }

    public void discard_click(View view) {
        Toast.makeText(this, R.string.result_discarded, Toast.LENGTH_LONG).show();
        view.setVisibility(View.INVISIBLE);
        keep.setVisibility(View.INVISIBLE);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_display_results, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
