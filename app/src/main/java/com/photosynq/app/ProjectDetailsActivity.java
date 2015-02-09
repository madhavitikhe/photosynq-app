package com.photosynq.app;

import android.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.AppSettings;
import com.photosynq.app.model.ResearchProject;
import com.photosynq.app.utils.CommonUtils;
import com.photosynq.app.utils.PrefUtils;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Locale;


public class ProjectDetailsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_details);

        DatabaseHelper databaseHelper = DatabaseHelper.getHelper(this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String projectID = extras.getString(DatabaseHelper.C_ID);
            ResearchProject project = databaseHelper.getResearchProject(projectID);

            SimpleDateFormat outputDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);

            ImageView projectImage = (ImageView) findViewById(R.id.im_projectImage);
            Picasso.with(this).load(project.getImageUrl()).into(projectImage);

            ImageView profileImage = (ImageView) findViewById(R.id.user_profile_image);
            String imageUrl = PrefUtils.getFromPrefs(this, PrefUtils.PREFS_THUMB_URL_KEY, PrefUtils.PREFS_DEFAULT_VAL);
            Picasso.with(this).load(imageUrl).into(profileImage);

            TextView tvProjetTitle = (TextView) findViewById(R.id.tv_project_name);
            TextView tvEndsIn = (TextView) findViewById(R.id.tv_ends_in);
            TextView tvBeta = (TextView) findViewById(R.id.tv_beta);

            tvProjetTitle.setText(project.getName());
//            if(!"null".equals(rp.getEndDate()))
//            {
//                tvEndDate.setText(outputDate.format(CommonUtils.convertToDate(rp.getEndDate())));
//            }else{tvEndDate.setText(getResources().getString(R.string.no_data_found));}

            String isBeta = project.getBeta();
            if(!"null".equals(isBeta))
            {
                if("true".equals(isBeta)) {
                    tvBeta.setVisibility(View.VISIBLE);
                    tvBeta.setText("BETA");
                }else{
                    tvBeta.setVisibility(View.INVISIBLE);
                    tvBeta.setText("");
                }
            }else{
                tvBeta.setVisibility(View.INVISIBLE);
                tvBeta.setText("");
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_project_details, menu);
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
