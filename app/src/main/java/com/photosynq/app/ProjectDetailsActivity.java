package com.photosynq.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.AppSettings;
import com.photosynq.app.model.Data;
import com.photosynq.app.model.ProjectCreator;
import com.photosynq.app.model.Question;
import com.photosynq.app.model.ResearchProject;
import com.photosynq.app.utils.CommonUtils;
import com.photosynq.app.utils.Constants;
import com.photosynq.app.utils.PrefUtils;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;


public class ProjectDetailsActivity extends ActionBarActivity {

    String projectID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);//or add in style.xml
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_project_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        ColorDrawable newColor = new ColorDrawable(getResources().getColor(R.color.green_light));//your color from res
        newColor.setAlpha(0);//from 0(0%) to 256(100%)
        actionBar.setBackgroundDrawable(newColor);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");

//        String isShowed = PrefUtils.getFromPrefs(this, "IsFirstProjectDetailsActivity", "FALSE");
//        if (isShowed.equals("FALSE")) {
//            CommonUtils.showShowCaseView(this, R.id.btn_take_measurement, "To collect data, choose a project, follow directions, answer questions, and take sensor measurement", "");
//            PrefUtils.saveToPrefs(this, "IsFirstProjectDetailsActivity", "TRUE");
//        }

        DatabaseHelper databaseHelper = DatabaseHelper.getHelper(this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            projectID = extras.getString(DatabaseHelper.C_PROJECT_ID);
            ResearchProject project = databaseHelper.getResearchProject(projectID);

            SimpleDateFormat outputDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);

            ImageView projectImage = (ImageView) findViewById(R.id.im_projectImage);
            Picasso.with(this)
                    .load(project.getImageUrl())
                    .error(R.drawable.ic_launcher1)
                    .into(projectImage);

            ImageView profileImage = (ImageView) findViewById(R.id.user_profile_image);
            ProjectCreator projectLead = databaseHelper.getProjectLead(project.getCreatorId());
            String imageUrl = projectLead.getImageUrl();
            Picasso.with(this)
                    .load(imageUrl)
                    .error(R.drawable.ic_launcher1)
                    .into(profileImage);

            Typeface tfRobotoRegular = CommonUtils.getInstance(this).getFontRobotoRegular();
            Typeface tfRobotoMedium = CommonUtils.getInstance(this).getFontRobotoMedium();

            TextView tvProjetTitle = (TextView) findViewById(R.id.tv_project_name);
            tvProjetTitle.setTypeface(tfRobotoRegular);
            tvProjetTitle.setText(project.getName());

//            TextView tvEndsIn = (TextView) findViewById(R.id.tv_ends_in);
//            tvEndsIn.setTypeface(tfRobotoRegular);

            TextView tvBeta = (TextView) findViewById(R.id.tv_beta);
            tvBeta.setTypeface(tfRobotoMedium);
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

            TextView tvOverview = (TextView) findViewById(R.id.tv_overview);
            tvOverview.setTypeface(tfRobotoRegular);

            final TextView tvOverviewText = (TextView) findViewById(R.id.tv_overview_text);
            tvOverviewText.setTypeface(tfRobotoRegular);
            tvOverviewText.setText(Html.fromHtml(project.getDescription()));


            final TextView tvShowHideOverview = (TextView) findViewById(R.id.show_hide_overview);
            tvShowHideOverview.setTypeface(tfRobotoRegular);
            tvShowHideOverview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if("Read More".equals(tvShowHideOverview.getText())) {
                        tvShowHideOverview.setText("Less");
                        tvOverviewText.setMaxLines(Integer.MAX_VALUE);
                    }else{
                        tvShowHideOverview.setText("Read More");
                        tvOverviewText.setLines(2);
                    }
                }
            });

            TextView tvInstructions = (TextView) findViewById(R.id.tv_instructions);
            tvInstructions.setTypeface(tfRobotoRegular);

            final TextView tvInstructionsText = (TextView) findViewById(R.id.tv_instructions_text);
            tvInstructionsText.setTypeface(tfRobotoRegular);
            tvInstructionsText.setText(Html.fromHtml(project.getDirToCollab()));

            final TextView tvShowHideInstructions = (TextView) findViewById(R.id.show_hide_instructions);
            tvShowHideInstructions.setTypeface(tfRobotoRegular);
            tvShowHideInstructions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if("Read More".equals(tvShowHideInstructions.getText())) {
                        tvShowHideInstructions.setText("Less");
                        tvInstructionsText.setMaxLines(Integer.MAX_VALUE);
                    }else{
                        tvShowHideInstructions.setText("Read More");
                        tvInstructionsText.setMaxLines(5);
                    }
                }
            });



//            if(!"null".equals(rp.getEndDate()))
//            {
//                tvEndDate.setText(outputDate.format(CommonUtils.convertToDate(rp.getEndDate())));
//            }else{tvEndDate.setText(getResources().getString(R.string.no_data_found));}


        }
    }


    public void take_measurement_click(View view){
        String userId = PrefUtils.getFromPrefs(this, PrefUtils.PREFS_LOGIN_USERNAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);
        DatabaseHelper databaseHelper = DatabaseHelper.getHelper(this);
        AppSettings appSettings = databaseHelper.getSettings(userId);
        appSettings.setProjectId(projectID);
        databaseHelper.updateSettings(appSettings);

        List<Question> questions = databaseHelper.getAllQuestionForProject(projectID);
        for (int i = 0; i< questions.size(); i++) {
            Question question = questions.get(i);
            int queType = question.getQuestionType();
            if(queType == Question.USER_DEFINED) { //question type is user selected.
                Data data = databaseHelper.getData(userId, projectID, question.getQuestionId());
                if(null == data.getValue() || data.getValue().isEmpty()) {
                    data.setUser_id(userId);
                    data.setProject_id(projectID);
                    data.setQuestion_id(question.getQuestionId());
                    data.setValue(Data.NO_VALUE);
                    data.setType(Constants.QuestionType.USER_SELECTED.getStatusCode());
                    databaseHelper.updateData(data);
                }
            }
        }

        Intent intent = new Intent(this, ProjectMeasurmentActivity.class);
        intent.putExtra(DatabaseHelper.C_PROJECT_ID, projectID);
        startActivityForResult(intent, 555);
    }

    public void join_team_click(View view){

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_project_details, menu);
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

            DatabaseHelper databaseHelper = DatabaseHelper.getHelper(this);
            List<Question> questions = databaseHelper.getAllQuestionForProject(projectID);
            if(questions.size() <= 0)
            {
                Toast.makeText(this, "No Questions for project selected", Toast.LENGTH_LONG).show();

            }else {

                Intent intent = new Intent(this, ProjectDataActivity.class);
                intent.putExtra(DatabaseHelper.C_PROJECT_ID, projectID);
                startActivity(intent);
            }

            return true;
        }

        if (id == android.R.id.home){

            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == 555) {

            setResult(555);
            finish();
        }
    }
}
