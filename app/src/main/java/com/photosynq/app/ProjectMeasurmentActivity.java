package com.photosynq.app;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.client.android.CaptureActivity;

import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.Data;
import com.photosynq.app.model.Protocol;
import com.photosynq.app.model.Question;
import com.photosynq.app.model.RememberAnswers;
import com.photosynq.app.model.ResearchProject;
import com.photosynq.app.utils.BluetoothService;
import com.photosynq.app.utils.CommonUtils;
import com.photosynq.app.utils.Constants;
import com.photosynq.app.utils.PrefUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;



public class ProjectMeasurmentActivity extends ActionBarActivity {

    private String deviceAddress;
    private String mConnectedDeviceName;
    private String projectId;

    private BluetoothService mBluetoothService = null;
    private BluetoothAdapter mBluetoothAdapter = null;
    private static final int REQUEST_ENABLE_BT = 2;

    private DatabaseHelper dbHelper;
    private QuestionViewFlipper viewFlipper;
    private ArrayList<String> allOptions;
    public boolean reviewFlag = false;
    private TextView mtvStatusMessage;

    Button btnTakeMeasurement;
    private boolean mIsCancelMeasureBtnClicked = false;
    private boolean mIsMeasureBtnClicked = false;
    private boolean scanMode = false;
    private int autoIncQueCount = 0;
    private String userId;
    private int screenWidth;
    private Menu optionsMenu;
    int optionMenuClickFlag = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_measurment);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;

        dbHelper = DatabaseHelper.getHelper(this);
        userId = PrefUtils.getFromPrefs(this, PrefUtils.PREFS_LOGIN_USERNAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            projectId = extras.getString(DatabaseHelper.C_PROJECT_ID);
        }

        deviceAddress = CommonUtils.getDeviceAddress(this);
        if (null == deviceAddress) {
            Toast.makeText(this, "Measurement device not configured, Please configure measurement device (bluetooth).", Toast.LENGTH_SHORT).show();
            finish();
        }

        String showDirections = PrefUtils.getFromPrefs(this, PrefUtils.PREFS_SHOW_DIRECTIONS, "YES");
        if (showDirections.equals("YES")) {
            if (null != projectId && null != deviceAddress) {
                Intent directionIntent = new Intent(this, DirectionsActivity.class);
                directionIntent.putExtra(DatabaseHelper.C_PROJECT_ID, projectId);
                startActivity(directionIntent);
            }
        }

        viewFlipper = (QuestionViewFlipper) findViewById(R.id.viewflipper);
        viewFlipper.setTag(projectId);

        createDynamicViewForQuestions();
        addReviewPage();

    }

    public void createDynamicViewForQuestions() {

        viewFlipper.removeAllViews();
        final String userId = PrefUtils.getFromPrefs(this, PrefUtils.PREFS_LOGIN_USERNAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);
        List<Question> questions = dbHelper.getAllQuestionForProject(projectId);
        for (int queIndex = 0; queIndex < questions.size(); queIndex++) {
            scanMode = false;
            final Question question = questions.get(queIndex);

            int queType = question.getQuestionType();
            String queId = question.getQuestionId();

            if (Question.USER_DEFINED == queType) { //If question is user defined, then show the screen as per data type

                Data data = dbHelper.getData(userId, projectId, queId);
                String dataType = data.getType();
                String dataValue = data.getValue();

                //TODO validate if following block is necessary - Shekhar
                if (null == dataType || null == dataValue) {
                    //if datatype or datavalue is not set in data
                    continue;
                }

                if (dataType.equals(Data.USER_SELECTED)) { //If data is user selected

                    LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View viewUserSelected = layoutInflater.inflate(R.layout.page_que_type_user_selected, null, false);
                    viewUserSelected.setTag(question.getQuestionId());
                    TextView questionText = (TextView) viewUserSelected.findViewById(R.id.tv_question_text);
                    questionText.setText(question.getQuestionText());
                    questionText.setTextColor(Color.WHITE);
                    questionText.setTypeface(CommonUtils.getInstance(this).getFontRobotoMedium());
                    questionText.setBackgroundResource(R.drawable.actionbar_bg);
                    questionText.setPadding(30, 0, 30, 30);

                    EditText userEnteredAnswer = (EditText) viewUserSelected.findViewById(R.id.et_user_answer);
                    final CheckBox userDefinedRememberCB = (CheckBox) viewUserSelected.findViewById(R.id.rememberAnswerCheckBox);

                    RememberAnswers rememberAnswers = dbHelper.getRememberAnswers(userId, projectId, question.getQuestionId());
                    //TODO  Should use boolean check instead of String, wrong convention for method name - Shekhar
                    if (rememberAnswers.getIs_remember() != null && rememberAnswers.getIs_remember().equals(Constants.IS_REMEMBER)) {
                        userDefinedRememberCB.setChecked(true);
                        userEnteredAnswer.setText(rememberAnswers.getSelected_option_text().toString());
                    } else {
                        userDefinedRememberCB.setChecked(false);
                    }

                    //set checked false when user change menu (if not remember).
                    if(optionMenuClickFlag == 1){
                        userDefinedRememberCB.setChecked(false);
                        userEnteredAnswer.setText("");
                    }

                    Button showNext = (Button) viewUserSelected.findViewById(R.id.btn_next);
                    showNext.setTag(queIndex);
                    showNext.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            hideKeyboard();

                            EditText userEnteredAnswer = (EditText) ((View) v.getParent()).findViewById(R.id.et_user_answer);
                            int displayedChild = viewFlipper.getDisplayedChild();
                            int childCount = viewFlipper.getChildCount();

                            String str = userEnteredAnswer.getText().toString().trim();
                            if (true == str.matches(".*['{}!].*") ||
                                    true == str.contains("\\n") ||
                                    true == str.contains("[") ||
                                    true == str.contains("]")) {
                                Toast.makeText(ProjectMeasurmentActivity.this, "Re-Enter Answer... special character is not allowed", Toast.LENGTH_LONG).show();
                            } else {
                                List<Question> questions = dbHelper.getAllQuestionForProject(projectId);
                                final Question question = questions.get(Integer.parseInt(v.getTag().toString()));
                                RememberAnswers rememberAnswers = new RememberAnswers();
                                rememberAnswers.setUser_id(userId);
                                rememberAnswers.setProject_id(projectId);
                                rememberAnswers.setQuestion_id(question.getQuestionId());

                                if (reviewFlag) {
                                    viewFlipper.setDisplayedChild(viewFlipper.getChildCount() - 1);
                                    reviewFlag = false;

                                    if (userDefinedRememberCB.isChecked()) {
                                        rememberAnswers.setSelected_option_text(str);
                                        rememberAnswers.setIs_remember(Constants.IS_REMEMBER);
                                    } else {
                                        userEnteredAnswer.setText("");
                                        rememberAnswers.setSelected_option_text(str);
                                        rememberAnswers.setIs_remember(Constants.IS_NOT_REMEMBER);
                                    }
                                    dbHelper.updateRememberAnswers(rememberAnswers);

                                    initReviewPage();
                                } else {
                                    if (userDefinedRememberCB.isChecked()) {
                                        rememberAnswers.setSelected_option_text(str);
                                        rememberAnswers.setIs_remember(Constants.IS_REMEMBER);
                                    } else {
                                        userEnteredAnswer.setText("");
                                        rememberAnswers.setSelected_option_text(str);
                                        rememberAnswers.setIs_remember(Constants.IS_NOT_REMEMBER);
                                    }
                                    dbHelper.updateRememberAnswers(rememberAnswers);
                                    viewFlipper.showNext();
                                    if (displayedChild == childCount - 2) {
                                        viewFlipper.stopFlipping();
                                        initReviewPage();
                                    }
                                }

                            }

                        }
                    });

                    viewFlipper.addView(viewUserSelected);

                } else if (dataType.equals(Data.AUTO_INCREMENT)) { //If data is auto increment

                    LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View viewAutoIncrement = layoutInflater.inflate(R.layout.page_que_type_auto_increment, null, false);
                    viewAutoIncrement.setTag(question.getQuestionId());
                    TextView questionText = (TextView) viewAutoIncrement.findViewById(R.id.tv_question_text);
                    questionText.setText(question.getQuestionText());
                    questionText.setTextColor(Color.WHITE);
                    questionText.setTypeface(CommonUtils.getInstance(this).getFontRobotoMedium());
                    questionText.setBackgroundResource(R.drawable.actionbar_bg);
                    questionText.setPadding(30, 0, 30, 30);

                    final EditText fromEditText = (EditText) viewAutoIncrement.findViewById(R.id.from_editText);
                    final EditText toEditText = (EditText) viewAutoIncrement.findViewById(R.id.to_editText);
                    final EditText repeatEditText = (EditText) viewAutoIncrement.findViewById(R.id.repeat_editText);
                    final Data retrieveData = dbHelper.getData(userId, projectId, question.getQuestionId());

                    RememberAnswers rememberAnswers = dbHelper.getRememberAnswers(userId, projectId, question.getQuestionId());
                    if (rememberAnswers.getIs_remember() != null && rememberAnswers.getIs_remember().equals(Constants.IS_REMEMBER)) {
                        String[] values = retrieveData.getValue().split(",");
                        fromEditText.setText(values[0]);
                        toEditText.setText(values[1]);
                        repeatEditText.setText(values[2]);
                        fromEditText.setFocusable(false);
                        toEditText.setFocusable(false);
                        repeatEditText.setFocusable(false);
                        fromEditText.setBackgroundColor(getResources().getColor(R.color.gray));
                        toEditText.setBackgroundColor(getResources().getColor(R.color.gray));
                        repeatEditText.setBackgroundColor(getResources().getColor(R.color.gray));
                    }

                    final Button editAutoIncrementValuesBtn = (Button) viewAutoIncrement.findViewById(R.id.autoInc_edit_button);
                    editAutoIncrementValuesBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (editAutoIncrementValuesBtn.getText().equals("Edit")) {
                                fromEditText.setFocusableInTouchMode(true);
                                toEditText.setFocusableInTouchMode(true);
                                repeatEditText.setFocusableInTouchMode(true);
                                fromEditText.setBackgroundColor(getResources().getColor(R.color.white));
                                toEditText.setBackgroundColor(getResources().getColor(R.color.white));
                                repeatEditText.setBackgroundColor(getResources().getColor(R.color.white));
                                editAutoIncrementValuesBtn.setText("Cancel");
                            } else if (editAutoIncrementValuesBtn.getText().equals("Cancel")) {
                                String[] values = retrieveData.getValue().split(",");
                                fromEditText.setText(values[0]);
                                toEditText.setText(values[1]);
                                repeatEditText.setText(values[2]);
                                fromEditText.setFocusable(false);
                                toEditText.setFocusable(false);
                                repeatEditText.setFocusable(false);
                                fromEditText.setBackgroundColor(getResources().getColor(R.color.gray));
                                toEditText.setBackgroundColor(getResources().getColor(R.color.gray));
                                repeatEditText.setBackgroundColor(getResources().getColor(R.color.gray));
                                editAutoIncrementValuesBtn.setText("Edit");
                            }

                        }
                    });

                    Button showNext = (Button) viewAutoIncrement.findViewById(R.id.btn_next);
                    showNext.setTag(queIndex);
                    showNext.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            hideKeyboard();

                            Data saveAutoIncData = dbHelper.getData(userId, projectId, question.getQuestionId());
                            saveAutoIncData.setUser_id(userId);
                            saveAutoIncData.setProject_id(projectId);
                            saveAutoIncData.setQuestion_id(question.getQuestionId());

//                            EditText fromEditText = (EditText) ((View) v.getParent()).findViewById(R.id.from_editText);
//                            EditText toEditText = (EditText) ((View) v.getParent()).findViewById(R.id.to_editText);
//                            EditText repeatEditText = (EditText) ((View) v.getParent()).findViewById(R.id.repeat_editText);

                            if (fromEditText.getText().toString().isEmpty()) {
                                fromEditText.setError("Please enter value");
                            } else if (toEditText.getText().toString().isEmpty()) {
                                toEditText.setError("Please enter value");
                            } else if (repeatEditText.getText().toString().isEmpty()) {
                                repeatEditText.setError("Please enter value");
                            } else {
                                editAutoIncrementValuesBtn.setText("Edit");
                                if (fromEditText.isFocusable() && toEditText.isFocusable() && repeatEditText.isFocusable()) {
                                    fromEditText.setFocusable(false);
                                    toEditText.setFocusable(false);
                                    repeatEditText.setFocusable(false);

                                    saveAutoIncData.setType(Constants.QuestionType.AUTO_INCREMENT.getStatusCode());
                                    saveAutoIncData.setValue(fromEditText.getText().toString() + "," + toEditText.getText().toString() + "," + repeatEditText.getText().toString());
                                    dbHelper.updateData(saveAutoIncData);
                                    PrefUtils.saveToPrefs(getApplicationContext(), PrefUtils.PREFS_QUESTION_INDEX, "" + "0");
                                    List<Question> questions = dbHelper.getAllQuestionForProject(projectId);
                                    final Question question = questions.get(Integer.parseInt(v.getTag().toString()));
                                    RememberAnswers rememberAnswers = new RememberAnswers();
                                    rememberAnswers.setUser_id(userId);
                                    rememberAnswers.setProject_id(projectId);
                                    rememberAnswers.setQuestion_id(question.getQuestionId());
                                    rememberAnswers.setSelected_option_text(fromEditText.getText().toString() + "," + toEditText.getText().toString() + "," + repeatEditText.getText().toString());
                                    rememberAnswers.setIs_remember(Constants.IS_REMEMBER);
                                    dbHelper.updateRememberAnswers(rememberAnswers);

                                    if (reviewFlag) {
                                        initReviewPage();
                                        viewFlipper.setDisplayedChild(viewFlipper.getChildCount() - 1);
                                        reviewFlag = false;
                                    } else {

                                        viewFlipper.showNext();
                                    }
                                }else{

                                    if (reviewFlag) {
                                        initReviewPage();
                                        viewFlipper.setDisplayedChild(viewFlipper.getChildCount() - 1);
                                        reviewFlag = false;
                                    } else {

                                        viewFlipper.showNext();
                                    }
                                }
                            }

                        }
                    });

                    autoIncQueCount = autoIncQueCount + 1;
                    if (dataValue.equals(Data.NO_VALUE)) {
                        Toast.makeText(this, "Incomplete information, please define answer types in data tab.", Toast.LENGTH_SHORT).show();
                    }
                    if (questions.size() == autoIncQueCount) {

                        //TODO need calrification on this flow, looks problematic code to me, it will crash if all questions are auto inc.
                        if (reviewFlag) {
                            viewFlipper.setDisplayedChild(viewFlipper.getChildCount() - 1);
                            reviewFlag = false;
                        } else {

                            viewFlipper.showNext();
                        }
                    }
                    PrefUtils.saveToPrefs(this, PrefUtils.PREFS_QUESTION_INDEX, "0");
                    viewFlipper.addView(viewAutoIncrement);

                } else if (dataType.equals(Data.SCAN_CODE)) { //If data is scan code
                    LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View viewScanCode = layoutInflater.inflate(R.layout.page_que_type_barcode_reader, null, true);
                    viewScanCode.setId(Integer.parseInt(queId));

                    viewScanCode.setTag(queId);

                    TextView questionText = (TextView) viewScanCode.findViewById(R.id.tv_question_text);
                    questionText.setText(question.getQuestionText());
                    questionText.setTextColor(Color.WHITE);
                    questionText.setTypeface(CommonUtils.getInstance(this).getFontRobotoMedium());
                    questionText.setBackgroundResource(R.drawable.actionbar_bg);
                    questionText.setPadding(30, 0, 30, 30);

                    final CheckBox scanCodeRememberCB = (CheckBox) viewScanCode.findViewById(R.id.rememberAnswerCheckBox);

                    RememberAnswers rememberAnswers = dbHelper.getRememberAnswers(userId, projectId, question.getQuestionId());
                    if (rememberAnswers.getIs_remember() != null && rememberAnswers.getIs_remember().equals(Constants.IS_REMEMBER)) {
                        scanCodeRememberCB.setChecked(true);
                    } else {
                        scanCodeRememberCB.setChecked(false);
                    }

                    //set checked false when user change menu (if not remember).
                    if(optionMenuClickFlag == 2){
                        scanCodeRememberCB.setChecked(false);
                    }

                    scanCodeRememberCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (scanCodeRememberCB.isChecked()) {
                                PrefUtils.saveToPrefs(getApplicationContext(), PrefUtils.PREFS_CHECK_IS_REMEMBER, Constants.IS_REMEMBER);
                            } else {
                                PrefUtils.saveToPrefs(getApplicationContext(), PrefUtils.PREFS_CHECK_IS_REMEMBER, Constants.IS_NOT_REMEMBER);
                            }
                        }
                    });

                    View btnScan = viewScanCode.findViewById(R.id.btn_barcode_scan);
                    btnScan.setTag(queIndex);
                    btnScan.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {

                            scanMode = true;
                            Intent intent = new Intent(ProjectMeasurmentActivity.this, CaptureActivity.class);
                            intent.setAction("com.google.zxing.client.android.SCAN");
                            // this stops saving ur barcode in barcode scanner app's history
                            intent.putExtra("SAVE_HISTORY", false);
                            startActivityForResult(intent, Integer.parseInt(v.getTag().toString()));
                        }
                    });

                    viewFlipper.addView(viewScanCode);
                }

            } else if (Question.PROJECT_DEFINED == queType) {

                LinearLayout mainLinearLayout = new LinearLayout(this);
                mainLinearLayout.setBackgroundColor(Color.WHITE);
                mainLinearLayout.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                mainLinearLayout.setLayoutParams(layoutParams);

                ScrollView scrollView = new ScrollView(this);
                scrollView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                LinearLayout subLinearLayout = new LinearLayout(this);
                subLinearLayout.setOrientation(LinearLayout.VERTICAL);
                subLinearLayout.setLayoutParams(layoutParams);

                TextView questionTextView = new TextView(this);
                questionTextView.setTextColor(Color.WHITE);
                questionTextView.setTextSize(18);
                questionTextView.setTypeface(CommonUtils.getInstance(this).getFontRobotoMedium());
                questionTextView.setBackgroundResource(R.drawable.actionbar_bg);
                questionTextView.setText(question.getQuestionText());
                questionTextView.setPadding(10, 0, 10, 10);
                questionTextView.setGravity(Gravity.CENTER);
                questionTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                mainLinearLayout.addView(questionTextView);

                LinearLayout.LayoutParams optionTVParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
                int optionIvWidth = (screenWidth / 2) - 20;
                LinearLayout.LayoutParams imageVParams = new LinearLayout.LayoutParams(optionIvWidth, optionIvWidth);

                LinearLayout.LayoutParams linearlayoutweight = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1);

                LinearLayout rowLinearLayout = new LinearLayout(this);

                RelativeLayout rememberCBLayout = new RelativeLayout(this);
                RelativeLayout.LayoutParams paramsCB = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                paramsCB.gravity = Gravity.BOTTOM;
                paramsCB.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                rememberCBLayout.setLayoutParams(paramsCB);

                final CheckBox rememberAnswersCB = new CheckBox(this);
                rememberAnswersCB.setText("Remember this option");
                rememberCBLayout.addView(rememberAnswersCB);
                mainLinearLayout.addView(rememberCBLayout);

                RememberAnswers rememberAnswers = dbHelper.getRememberAnswers(userId, projectId, question.getQuestionId());
                if (rememberAnswers.getIs_remember() != null && rememberAnswers.getIs_remember().equals(Constants.IS_REMEMBER)) {
                    rememberAnswersCB.setChecked(true);
                } else {
                    rememberAnswersCB.setChecked(false);
                }

                for (int i = 0; i < question.getOptions().size(); i++) {
                    String optionText = question.getOptions().get(i);
                    TextView optionTextView = new TextView(this);
                    optionTextView.setText(optionText);
                    optionTextView.setTextColor(Color.BLACK);
                    optionTextView.setTextSize(30);
                    optionTextView.setSingleLine(false);
                    optionTextView.setMaxLines(Integer.MAX_VALUE);
                    optionTextView.setGravity(Gravity.CENTER);
                    optionTextView.setBackgroundResource(R.drawable.actionbar_bg);
                    optionTextView.setTextColor(getResources().getColor(R.color.white));
                    optionTextView.setTypeface(CommonUtils.getInstance(this).getFontRobotoRegular());

//                    ImageView imageView = new ImageView(this);
//                    imageView.setId(i);
//                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                    imageView.setTag(queIndex);
                    optionTextView.setId(i);
                    optionTextView.setTag(queIndex);
                    optionTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            int displayedChild = viewFlipper.getDisplayedChild();
                            int childCount = viewFlipper.getChildCount();

                            List<Question> questions = dbHelper.getAllQuestionForProject(projectId);
                            final Question question = questions.get(Integer.parseInt(v.getTag().toString()));
                            RememberAnswers rememberAnswers = new RememberAnswers();
                            rememberAnswers.setUser_id(userId);
                            rememberAnswers.setProject_id(projectId);
                            rememberAnswers.setQuestion_id(question.getQuestionId());

                            if (reviewFlag) {
                                viewFlipper.setDisplayedChild(viewFlipper.getChildCount() - 1);
                                reviewFlag = false;

                                if (rememberAnswersCB.isChecked()) {
                                    rememberAnswers.setSelected_option_text(question.getOptions().get(v.getId()));
                                    rememberAnswers.setIs_remember(Constants.IS_REMEMBER);
                                } else {
                                    rememberAnswers.setSelected_option_text(question.getOptions().get(v.getId()));
                                    rememberAnswers.setIs_remember(Constants.IS_NOT_REMEMBER);
                                }
                                dbHelper.updateRememberAnswers(rememberAnswers);

                                initReviewPage();
                            } else {
                                if (rememberAnswersCB.isChecked()) {
                                    rememberAnswers.setSelected_option_text(question.getOptions().get(v.getId()));
                                    rememberAnswers.setIs_remember(Constants.IS_REMEMBER);
                                } else {
                                    rememberAnswers.setSelected_option_text(question.getOptions().get(v.getId()));
                                    rememberAnswers.setIs_remember(Constants.IS_NOT_REMEMBER);
                                }
                                dbHelper.updateRememberAnswers(rememberAnswers);
                                viewFlipper.showNext();
                                if (displayedChild == childCount - 2) {
                                    viewFlipper.stopFlipping();

                                    initReviewPage();
                                }
                            }

                        }
                    });

//                    Picasso.with(this)
//                            .load(R.drawable.ic_launcher)
//                            .error(R.drawable.ic_launcher)
//                            .into(imageView);

                    LinearLayout cellRelativeLayout = new LinearLayout(this);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    cellRelativeLayout.setLayoutParams(params);
                    cellRelativeLayout.setOrientation(LinearLayout.VERTICAL);

                    if (i % 2 == 0) {
                        rowLinearLayout = new LinearLayout(this);
                        rowLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
                        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        params1.setMargins(10, 10, 10, 10);
                        rowLinearLayout.setLayoutParams(params1);


//                        optionTextView.setId(1);
                        //??imageVParams.addRule(RelativeLayout.BELOW, optionTextView.getId());
                        imageVParams.setMargins(0, 10, 10, 10);


                        cellRelativeLayout.addView(optionTextView, imageVParams);
//                        cellRelativeLayout.addView(imageView, imageVParams);

                        rowLinearLayout.addView(cellRelativeLayout, linearlayoutweight);
                        if (i == question.getOptions().size() - 1) {

                            subLinearLayout.addView(rowLinearLayout);
                        }

                    } else {
//                        optionTextView.setId(1555555);
                        //??imageVParams.addRule(RelativeLayout.BELOW, optionTextView.getId());
                        imageVParams.setMargins(10, 10, 10, 0);


                        cellRelativeLayout.addView(optionTextView, imageVParams);
//                        cellRelativeLayout.addView(imageView, imageVParams);

                        rowLinearLayout.addView(cellRelativeLayout, linearlayoutweight);

                        subLinearLayout.addView(rowLinearLayout);

                    }

                }
                scrollView.addView(subLinearLayout);
                mainLinearLayout.addView(scrollView);
                mainLinearLayout.setTag(question.getQuestionId());
                viewFlipper.addView(mainLinearLayout);
            }
        }
    }

    public void initReviewPage() {
        View measurementView = findViewById(9595);
        LinearLayout liLayout = (LinearLayout) measurementView.findViewById(R.id.ll_options);
        liLayout.removeAllViews();

        //int optionLoop = 0;
        List<Question> questions = dbHelper.getAllQuestionForProject(projectId);
        allOptions = new ArrayList<String>();
        for (int queIndex = 0; queIndex < questions.size(); queIndex++) {

            Question question = questions.get(queIndex);

            View reviewItem = getLayoutInflater().inflate(R.layout.review_list_item, null);
            liLayout.addView(reviewItem);

            LinearLayout reviewLL = (LinearLayout) reviewItem.findViewById(R.id.reviewLL);
            TextView tvQuestion = (TextView) reviewItem.findViewById(R.id.tv_question_text);
            TextView tvOption = (TextView) reviewItem.findViewById(R.id.tv_option_text);
            TextView tvRemembered = (TextView) reviewItem.findViewById(R.id.remembered_text);
            tvQuestion.setTextSize(18);
            tvQuestion.setTypeface(CommonUtils.getInstance(this).getFontRobotoMedium());
            tvOption.setTextSize(16);
            tvOption.setTypeface(CommonUtils.getInstance(this).getFontRobotoRegular());

            reviewItem.setTag(question.getQuestionId());

            String data_value = new String("");

            tvQuestion.setText(question.getQuestionText());
            String userId = PrefUtils.getFromPrefs(this, PrefUtils.PREFS_LOGIN_USERNAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);

            Data reviewData = dbHelper.getData(userId, projectId, question.getQuestionId());
            RememberAnswers rememberAnswers = dbHelper.getRememberAnswers(userId, projectId, question.getQuestionId());
            if (null != rememberAnswers.getIs_remember() && rememberAnswers.getIs_remember().equals(Constants.IS_REMEMBER)) {
                if (reviewData.getType().equals(Data.USER_SELECTED)) {
                    tvRemembered.setText("remembered");
                } else if (reviewData.getType().equals(Data.AUTO_INCREMENT)) {
                    tvRemembered.setText("auto increment");
                } else if (reviewData.getType().equals(Data.SCAN_CODE)) {
                    tvRemembered.setText("remembered");
                } else {
                    tvRemembered.setText("remembered");
                }
            } else {
                tvRemembered.setText("");
            }

            //if selected option type is User_Selected, Fixed_Value, Auto_Increment, Scan_Code
            if (Question.USER_DEFINED == question.getQuestionType()) { //If question is user defined, then show the screen as per data type

                Data data = dbHelper.getData(userId, projectId, question.getQuestionId());

                //Question and Option shown only if selected option type is 'Auto_Increment'
                if (data.getType().equals(Data.AUTO_INCREMENT)) {
                    int index = Integer.parseInt(PrefUtils.getFromPrefs(this, PrefUtils.PREFS_QUESTION_INDEX, "-1"));
                    int optionValue = Integer.parseInt(CommonUtils.getAutoIncrementedValue(this, question.getQuestionId(), "" + index));

                    if (optionValue != -1) {
                        data_value = "" + optionValue;
                    } else {
                        data_value = "";
                    }
                    tvOption.setText(data_value);
                    reviewItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            reviewFlag = true;
                            View child = viewFlipper.findViewWithTag(view.getTag());
                            viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(child));

                            viewFlipper.removeViewAt(viewFlipper.getChildCount() - 1); //0 based index
                            addReviewPage();
                        }
                    });
                } else  //Question and Option shown except 'Auto_Increment' option type.(for User_Selected, Scan_Code)
                {
                    data_value = rememberAnswers.getSelected_option_text();
                    tvOption.setText(data_value);

                    reviewItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            reviewFlag = true;
                            View child = viewFlipper.findViewWithTag(view.getTag());
                            viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(child));

                            viewFlipper.removeViewAt(viewFlipper.getChildCount() - 1); //0 based index
                            addReviewPage();
                        }
                    });
                }

            } else  //Project mode Question and Option is display.
            {
                data_value = rememberAnswers.getSelected_option_text();
                tvOption.setText(data_value);

                reviewItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        reviewFlag = true;
                        View child = viewFlipper.findViewWithTag(view.getTag());
                        viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(child));
                        viewFlipper.removeViewAt(viewFlipper.getChildCount() - 1); //0 based index
                        addReviewPage();
                    }
                });
            }

            allOptions.add(data_value);

        }

        int viewCount = viewFlipper.getChildCount();
        refreshReviewPage(viewFlipper.getChildAt(viewCount - 1));

    }

    public void userDefinedOptions() {
        if(null != optionsMenu) {
            if (null != viewFlipper.getTag() && null != viewFlipper.getCurrentView().getTag()) {
                Question question = dbHelper.getQuestionForProject(viewFlipper.getTag().toString(), viewFlipper.getCurrentView().getTag().toString());
                if (question.getQuestionType() == Question.USER_DEFINED) {
                    optionsMenu.getItem(0).setEnabled(true);
                } else {
                    optionsMenu.getItem(0).setEnabled(false);
                }
            } else {
                optionsMenu.getItem(0).setEnabled(false);
            }
        }
    }


    public void hideKeyboard() {

        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getWindow().getDecorView().getRootView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void addReviewPage() {

        LayoutInflater infltr = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View reviewPage = infltr.inflate(R.layout.page_question_answer_review, null, false);
        reviewPage.setId(9595);
        viewFlipper.addView(reviewPage);

        List<Question> questions = dbHelper.getAllQuestionForProject(projectId);
        int queCount = questions.size();
        if (autoIncQueCount == queCount) {
            initReviewPage();
        } else {
            refreshReviewPage(reviewPage);
        }
    }

    private void refreshReviewPage(View reviewPage) {

        int displayedChild = viewFlipper.getDisplayedChild();
        int childCount = viewFlipper.getChildCount();
        if (displayedChild == childCount - 1) {
            getSupportActionBar().setTitle("Review");
        } else {
            getSupportActionBar().setTitle("");
        }

        mtvStatusMessage = (TextView) reviewPage.findViewById(R.id.tv_status_message);

        btnTakeMeasurement = (Button) reviewPage.findViewById(R.id.btn_take_measurement);

        btnTakeMeasurement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnTakeMeasurement.getText().equals("+ Take Measurement")) {
                    mIsMeasureBtnClicked = true;
                    if (mBluetoothService == null) {
                        mBluetoothService = new BluetoothService(ProjectMeasurmentActivity.this, mHandler);
                    }
                    if (mBluetoothService.getState() != BluetoothService.STATE_CONNECTED) {
                        // Get the BLuetoothDevice object
                        if (mBluetoothAdapter == null)
                            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                        if (null == deviceAddress) {
                            Toast.makeText(ProjectMeasurmentActivity.this, "Measurement device not configured, Please configure measurement device (bluetooth).", Toast.LENGTH_SHORT).show();
                        } else {
                            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(deviceAddress);
                            mBluetoothService.connect(device);
                        }
                    } else {
                        mHandler.obtainMessage(Constants.MESSAGE_STATE_CHANGE, BluetoothService.STATE_CONNECTED, 1).sendToTarget();
                    }
                    btnTakeMeasurement.setText("Cancel");
                    btnTakeMeasurement.setBackgroundResource(R.drawable.btn_layout_red);
                } else if (btnTakeMeasurement.getText().equals("Cancel")) {
                    mIsCancelMeasureBtnClicked = true;
                    mHandler.obtainMessage(Constants.MESSAGE_STATE_CHANGE, BluetoothService.STATE_CONNECTED, 0).sendToTarget();
                    btnTakeMeasurement.setText("+ Take Measurement");
                    btnTakeMeasurement.setBackgroundResource(R.drawable.btn_layout_orange);
                }
            }
        });
        Button directionsButton = (Button) reviewPage.findViewById(R.id.btn_directions);
        directionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openMainActivity = new Intent(ProjectMeasurmentActivity.this, DirectionsActivity.class);
                openMainActivity.putExtra(DatabaseHelper.C_PROJECT_ID, projectId);
                startActivity(openMainActivity);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!scanMode) {
//            if(mIsMeasureBtnClicked)
//                viewFlipper.setDisplayedChild(0);

            List<Question> questions = dbHelper.getAllQuestionForProject(projectId);

            if (autoIncQueCount == questions.size()) {
                initReviewPage();

            } else {
                if (mIsMeasureBtnClicked) {
                    int viewCount = viewFlipper.getChildCount();
                    if (viewCount > 1) {
                        viewFlipper.setDisplayedChild(0);
                    }
                    refreshReviewPage(viewFlipper.getChildAt(viewCount - 1));
                }
            }
            //clearflag = true;
        }
        scanMode = false;
        mIsMeasureBtnClicked = false;
        mIsCancelMeasureBtnClicked = false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == Activity.RESULT_OK) {
            if (null != intent) {
                String contents = intent.getStringExtra("SCAN_RESULT");

                int displayedChild = viewFlipper.getDisplayedChild();
                int childCount = viewFlipper.getChildCount();

                String checkboxState = PrefUtils.getFromPrefs(getApplicationContext(), PrefUtils.PREFS_CHECK_IS_REMEMBER, PrefUtils.PREFS_DEFAULT_VAL);
                List<Question> questions = dbHelper.getAllQuestionForProject(projectId);
                final Question question = questions.get(requestCode);
                RememberAnswers rememberAnswers = new RememberAnswers();
                rememberAnswers.setUser_id(userId);
                rememberAnswers.setProject_id(projectId);
                rememberAnswers.setQuestion_id(question.getQuestionId());
                if (reviewFlag) {
                    viewFlipper.setDisplayedChild(viewFlipper.getChildCount() - 1);
                    reviewFlag = false;
                    if (checkboxState.equals(Constants.IS_REMEMBER)) {
                        rememberAnswers.setSelected_option_text(contents);
                        rememberAnswers.setIs_remember(Constants.IS_REMEMBER);
                    } else {
                        rememberAnswers.setSelected_option_text(contents);
                        rememberAnswers.setIs_remember(Constants.IS_NOT_REMEMBER);
                    }
                    dbHelper.updateRememberAnswers(rememberAnswers);

                    initReviewPage();
                } else {

                    if (checkboxState.equals(Constants.IS_REMEMBER)) {
                        rememberAnswers.setSelected_option_text(contents);
                        rememberAnswers.setIs_remember(Constants.IS_REMEMBER);
                    } else {
                        rememberAnswers.setSelected_option_text(contents);
                        rememberAnswers.setIs_remember(Constants.IS_NOT_REMEMBER);
                    }
                    dbHelper.updateRememberAnswers(rememberAnswers);
                    viewFlipper.showNext();
                    if (displayedChild == childCount - 2) {
                        viewFlipper.stopFlipping();

                        initReviewPage();
                    }
                }
                PrefUtils.saveToPrefs(getApplicationContext(), PrefUtils.PREFS_CHECK_IS_REMEMBER, Constants.IS_NOT_REMEMBER);
                Toast.makeText(this, contents, Toast.LENGTH_SHORT).show();
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // Handle cancel
            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_project_measurment, menu);
        optionsMenu = menu;
        userDefinedOptions();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId(); //TODO crashing on first question - Shekhar

        switch (item.getItemId()) {
            case R.id.userSelectedMenuItem:
            case R.id.autoIncMenuItem:
            case R.id.barCodeOptionMenuItem:
                return changeQuestionType(item);
            default:
        }

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private boolean changeQuestionType(MenuItem item) {
        List<Question> questions = dbHelper.getAllQuestionForProject(projectId);
        final Question question = questions.get(viewFlipper.getDisplayedChild());

        int queType = question.getQuestionType();
        String queId = question.getQuestionId();
        userId = PrefUtils.getFromPrefs(getApplicationContext(), PrefUtils.PREFS_LOGIN_USERNAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);
        final Data retrieveData = dbHelper.getData(userId, projectId, queId);
        retrieveData.setUser_id(userId);
        retrieveData.setProject_id(projectId);
        retrieveData.setQuestion_id(question.getQuestionId());
        retrieveData.setValue(Data.NO_VALUE);

        switch (item.getItemId()) {
            case R.id.userSelectedMenuItem:
                optionMenuClickFlag = 1;//set flag 1 if user selected menu is clicked.
                hideKeyboard();
                retrieveData.setType(Constants.QuestionType.USER_SELECTED.getStatusCode());
                dbHelper.updateData(retrieveData);

                int currentViewIndex = viewFlipper.getDisplayedChild();
                createDynamicViewForQuestions();
                addReviewPage();
                viewFlipper.setDisplayedChild(currentViewIndex);
                return true;

            case R.id.autoIncMenuItem:
                hideKeyboard();
                retrieveData.setType(Constants.QuestionType.AUTO_INCREMENT.getStatusCode());
                retrieveData.setValue(0 + "," + 0 + "," + 0);
                dbHelper.updateData(retrieveData);

                int currentAutoIncViewIndex = viewFlipper.getDisplayedChild();
                createDynamicViewForQuestions();
                addReviewPage();
                viewFlipper.setDisplayedChild(currentAutoIncViewIndex);
                return true;

            case R.id.barCodeOptionMenuItem:
                optionMenuClickFlag = 2;//set flag 2 if user selected menu is clicked.
                hideKeyboard();
                retrieveData.setType(Constants.QuestionType.SCAN_CODE.getStatusCode());
                dbHelper.updateData(retrieveData);

                int currentViewIndexBarcode = viewFlipper.getDisplayedChild();
                createDynamicViewForQuestions();
                addReviewPage();
                viewFlipper.setDisplayedChild(currentViewIndexBarcode);
                return true;
            default:
        }
        return false;
    }

    private void sendData(String data) {
        // Check that we're actually connected before trying anything
        if (mBluetoothService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(getApplicationContext(), "Not Connected", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (data.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send;
            send = data.getBytes();
            mBluetoothService.write(send);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth  services
        if (mBluetoothService != null) mBluetoothService.stop();
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    if (Constants.D) Log.i("PHOTOSYNC", "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            if (msg.arg2 == 0) {//Sending cancel request to the device
                                sendData("-1+-1+");
                                if (mBluetoothService != null) {
                                    if (mBluetoothService.getState() == BluetoothService.STATE_CONNECTED) {
                                        mBluetoothService.stop();
                                    }
                                }
                                mtvStatusMessage.setText("Measurement cancel");
                            } else if (msg.arg2 == 1) { //Send measurement request
                                mtvStatusMessage.setText(R.string.title_connected_to);
                                mtvStatusMessage.append(mConnectedDeviceName);

                                ResearchProject researchProject = dbHelper.getResearchProject(projectId);
                                if (null == researchProject) {
                                    Toast.makeText(ProjectMeasurmentActivity.this, "Project not selected, Please select the project.", Toast.LENGTH_LONG).show();
                                    break;
                                }

                                try {
                                    String protocolJson = "";
                                    StringBuffer dataString = new StringBuffer();
                                    String[] projectProtocols = researchProject.getProtocols_ids().split(",");
                                    if (projectProtocols.length >= 1) {
                                        for (String protocolId : projectProtocols) {
                                            if (protocolId.equals(""))
                                                continue;
                                            Protocol protocol = dbHelper.getProtocol(protocolId);
                                            JSONObject detailProtocolObject = new JSONObject();
                                            detailProtocolObject.put("protocolid", protocol.getId());
                                            detailProtocolObject.put("protocol_name", protocol.getName());
                                            detailProtocolObject.put("macro_id", protocol.getMacroId());
                                            dataString.append("\"" + protocol.getId() + "\"" + ":" + detailProtocolObject.toString() + ",");

                                            String tempProtocolJson = protocol.getProtocol_json().trim();
                                            if (tempProtocolJson.length() > 1) {
                                                protocolJson += "{" + tempProtocolJson.substring(1, tempProtocolJson.length() - 1) + "},";
                                            }

                                        }

                                        if (dataString.length() > 0) {
                                            String data = "var protocols={" + dataString.substring(0, dataString.length() - 1) + "}";

                                            // Writing macros_variable.js file with protocol and macro relations
                                            System.out.println("######Writing macros_variable.js file:" + data);
                                            CommonUtils.writeStringToFile(ProjectMeasurmentActivity.this, "macros_variable.js", data);

                                            protocolJson = "[" + protocolJson.substring(0, protocolJson.length() - 1) + "]"; // remove last comma and add suqare brackets and start and end.

                                            System.out.println("$$$$$$$$$$$$$$ protocol json sending to device :" + protocolJson + "length:" + protocolJson.length());

                                            sendData(protocolJson);

                                            mtvStatusMessage.setText("Initializing measurement please wait ...");

                                        } else {

                                            mtvStatusMessage.setText("No protocol defined for this project.");
                                            Toast.makeText(ProjectMeasurmentActivity.this, "No protocol defined for this project.", Toast.LENGTH_LONG).show();

                                            if (btnTakeMeasurement != null) {
                                                if (btnTakeMeasurement.getText().equals("Cancel")) {
                                                    btnTakeMeasurement.setText("+ Take Measurement");
                                                    btnTakeMeasurement.setBackgroundResource(R.drawable.btn_layout_orange);
                                                }
                                            }
                                        }
                                    } else {

                                        mtvStatusMessage.setText("No protocol defined for this project.");
                                        Toast.makeText(ProjectMeasurmentActivity.this, "No protocol defined for this project.", Toast.LENGTH_LONG).show();
                                        if (btnTakeMeasurement != null) {
                                            if (btnTakeMeasurement.getText().equals("Cancel")) {
                                                btnTakeMeasurement.setText("+ Take Measurement");
                                                btnTakeMeasurement.setBackgroundResource(R.drawable.btn_layout_orange);
                                            }
                                        }
                                        break;
                                    }

                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }

                            } else {
                                if (mIsMeasureBtnClicked) {
                                    mHandler.obtainMessage(Constants.MESSAGE_STATE_CHANGE, BluetoothService.STATE_CONNECTED, 1).sendToTarget();
                                }
                            }

                            break;
                        case BluetoothService.STATE_CONNECTING:
                            mtvStatusMessage.setText(R.string.title_connecting);
                            break;
                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:
                            mtvStatusMessage.setText(R.string.title_not_connected);
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    break;
                case Constants.MESSAGE_READ:
                    if (mIsCancelMeasureBtnClicked == false) {
                        StringBuffer measurement = (StringBuffer) msg.obj;
                        // Do not process the message if contain pwr_off from device
                        if (!measurement.toString().contains("pwr_off")) {

                            // construct a string from the valid bytes in the buffer
                            // String readMessage = new String(readBuf, 0, msg.arg1);
                            mtvStatusMessage.setText(R.string.connected);
                            String dataString;
                            StringBuffer options = new StringBuffer();
                            options.append("\"user_answers\": [");
                            //loop
                            if (null != allOptions) {
                                for (int i = 0; i < allOptions.size(); i++) {
                                    options.append("\"" + allOptions.get(i) + "\"");
                                    if (i < allOptions.size() - 1)
                                        options.append(",");
                                }
                            }
                            options.append(" ],");
                            long time = System.currentTimeMillis();
                            if (options.equals("")) {
                                dataString = "var data = [\n" + measurement.toString().replaceAll("\\r\\n", "").replaceAll("\\{", "{\"time\":\"" + time + "\",") + "\n];";
                                System.out.println("All Options" + dataString);
                            } else {
                                String currentLocation = PrefUtils.getFromPrefs(ProjectMeasurmentActivity.this, PrefUtils.PREFS_CURRENT_LOCATION, "NONE");
                                if (!currentLocation.equals("NONE")) {
                                    options = options.append("\"location\":[" + currentLocation + "],");
                                    dataString = "var data = [\n" + measurement.toString().replaceAll("\\r\\n", "").replaceFirst("\\{", "{" + options).replaceAll("\\{", "{\"time\":\"" + time + "\",") + "\n];";
                                    System.out.println("All Options" + dataString);
                                } else {
                                    dataString = "var data = [\n" + measurement.toString().replaceAll("\\r\\n", "").replaceFirst("\\{", "{" + options).replaceAll("\\{", "{\"time\":\"" + time + "\",") + "\n];";
                                    System.out.println("All Options" + dataString);
                                }
                            }
                            System.out.println("###### writing data.js :" + dataString);
                            CommonUtils.writeStringToFile(ProjectMeasurmentActivity.this, "data.js", dataString);

                            Intent intent = new Intent(ProjectMeasurmentActivity.this, DisplayResultsActivity.class);
                            intent.putExtra(DatabaseHelper.C_PROJECT_ID, projectId);
                            intent.putExtra(Constants.APP_MODE, Constants.APP_MODE_PROJECT_MEASURE);
                            String reading = measurement.toString().replaceAll("\\r\\n", "").replaceFirst("\\{", "{" + options).replaceAll("\\{", "{\"time\":\"" + time + "\",");
                            intent.putExtra(DatabaseHelper.C_READING, reading);
                            startActivity(intent);
                        }
                    }
                    //mIsMeasureBtnClicked = false;
                    mIsCancelMeasureBtnClicked = false;
                    if (btnTakeMeasurement != null) {
                        if (btnTakeMeasurement.getText().equals("Cancel")) {
                            btnTakeMeasurement.setText("+ Take Measurement");
                            btnTakeMeasurement.setBackgroundResource(R.drawable.btn_layout_orange);
                        }
                    }
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "Connected to "
                            + mConnectedDeviceName, Toast.LENGTH_LONG).show();

                    break;
                case Constants.MESSAGE_TOAST:
                    if (mIsCancelMeasureBtnClicked == false) {
                        Toast.makeText(ProjectMeasurmentActivity.this, msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_LONG).show();
                    }
                    mIsMeasureBtnClicked = false;
                    mIsCancelMeasureBtnClicked = false;
                    if (btnTakeMeasurement != null) {
                        if (btnTakeMeasurement.getText().equals("Cancel")) {
                            btnTakeMeasurement.setText("+ Take Measurement");
                            btnTakeMeasurement.setBackgroundResource(R.drawable.btn_layout_orange);
                        }
                    }
                    break;
                case Constants.MESSAGE_STOP:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(Constants.TOAST),
                            Toast.LENGTH_LONG).show();
                    mBluetoothService.stop();
                    break;
            }
        }
    };

}
