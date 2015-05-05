package com.photosynq.app;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;

import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.ProjectResult;
import com.photosynq.app.utils.PrefUtils;

import java.util.List;


public class DisplayCachedDataPoints extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_cached_data_points);

        DatabaseHelper db = DatabaseHelper.getHelper(getApplicationContext());
        final List<ProjectResult> listRecords = db.getAllUnUploadedResults();

        PrefUtils.saveToPrefs(getApplicationContext(), PrefUtils.PREFS_TOTAL_CACHED_DATA_POINTS, ""+listRecords.size());

        TextView txt = (TextView) findViewById(R.id.text1);
        for(int i = 0; i < listRecords.size(); i++){
            txt.append(Html.fromHtml("<br/><b>PROJECT_ID - </b>"+listRecords.get(i).getProjectId()+ "\n\n" + "<br/><b>IS_UPLOADED - </b>" + listRecords.get(i).getUploaded() + "\n\n" + "<br/><b>READINGS - </b>" + listRecords.get(i).getReading() + "\n\n<br/><br/>-------------------------------------------"));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_cached_data_points, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
