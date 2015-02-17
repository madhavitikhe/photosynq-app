package com.photosynq.app;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import com.google.zxing.client.android.CaptureActivity;

import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.Data;
import com.photosynq.app.model.Protocol;
import com.photosynq.app.model.Question;
import com.photosynq.app.model.ResearchProject;
import com.photosynq.app.utils.BluetoothService;
import com.photosynq.app.utils.CommonUtils;
import com.photosynq.app.utils.Constants;
import com.photosynq.app.utils.PrefUtils;
import com.squareup.picasso.Picasso;

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
    private ViewFlipper viewFlipper;
    private ArrayList<String> allSelectedOptions;
    private ArrayList<String> allOptions;
    private boolean reviewFlag = false;
    private TextView mtvStatusMessage;

    Button btnTakeMeasurement;
    private boolean mIsCancelMeasureBtnClicked = false;
    private boolean mIsMeasureBtnClicked = false;
    private boolean scanMode = false;
    private int autoIncQueCount = 0;
    private int fixedValueQueCount = 0;

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
        int screenWidth = size.x;

        dbHelper = DatabaseHelper.getHelper(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            projectId = extras.getString(DatabaseHelper.C_PROJECT_ID);
        }

        deviceAddress = CommonUtils.getDeviceAddress(this);
        if(null == deviceAddress)
        {
            Toast.makeText(this,"Measurement device not configured, Please configure measurement device (bluetooth).",Toast.LENGTH_SHORT).show();
            finish();
        }

        String showDirections = PrefUtils.getFromPrefs(this, PrefUtils.PREFS_SHOW_DIRECTIONS, "YES");
        if(showDirections.equals("YES")){
            if(null != projectId && null != deviceAddress) {
                Intent directionIntent = new Intent(this, DirectionsActivity.class);
                directionIntent.putExtra(DatabaseHelper.C_PROJECT_ID, projectId);
                startActivity(directionIntent);
            }
        }

        viewFlipper = (ViewFlipper) findViewById(R.id.viewflipper);

        allSelectedOptions = new ArrayList<String>();

        String userId = PrefUtils.getFromPrefs(this, PrefUtils.PREFS_LOGIN_USERNAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);
        List<Question> questions = dbHelper.getAllQuestionForProject(projectId);
        for(int queIndex = 0; queIndex < questions.size(); queIndex++){
            scanMode = false;
            final Question question = questions.get(queIndex);
            allSelectedOptions.add(queIndex, "");

            int queType = question.getQuestionType();
            String queId = question.getQuestionId();
            if(Question.USER_DEFINED == queType){ //If question is user defined, then show the screen as per data type
                Data data = dbHelper.getData(userId, projectId, queId);
                String dataType = data.getType();
                String dataValue = data.getValue();
                if( null == dataType || null == dataValue)
                {
                    //if datatype or datavalue is not set in data
                    continue;
                }

                if( dataType.equals(Data.USER_SELECTED)) { //If data is user selected

                    LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View viewUserSelected = layoutInflater.inflate(R.layout.page_que_type_user_selected, null, false);
                    viewUserSelected.setTag(question.getQuestionId());
                    TextView questionText = (TextView) viewUserSelected.findViewById(R.id.tv_question_text);
                    questionText.setText(question.getQuestionText());
                    questionText.setTypeface(CommonUtils.getInstance(this).getFontRobotoMedium());

                    Button showNext = (Button) viewUserSelected.findViewById(R.id.btn_next);
                    showNext.setTag(queIndex);
                    showNext.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EditText userEnteredAnswer = (EditText) ((View)v.getParent()).findViewById(R.id.et_user_answer);
                            int displayedChild = viewFlipper.getDisplayedChild();
                            int childCount = viewFlipper.getChildCount();
                            String str = userEnteredAnswer.getText().toString().trim();
                            if(true == str.matches(".*['{}!].*") ||
                                    true == str.contains("\\n") ||
                                    true == str.contains("[")||
                                    true == str.contains("]")){
                                Toast.makeText(ProjectMeasurmentActivity.this,"Re-Enter Answer... special character is not allowed",Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                allSelectedOptions.set(Integer.parseInt(v.getTag().toString()),userEnteredAnswer.getText().toString());
                                if(reviewFlag)
                                {
                                    viewFlipper.setDisplayedChild(viewFlipper.getChildCount()-1);
                                    reviewFlag = false;

                                    initReviewPage();
                                }
                                else {

                                    viewFlipper.showNext();
                                    if (displayedChild == childCount - 2 ) {
                                        viewFlipper.stopFlipping();

                                        initReviewPage();
                                    }
                                }

                                userEnteredAnswer.setText("");
                            }
                        }
                    });

                    viewFlipper.addView(viewUserSelected);

                }else if( dataType.equals(Data.FIXED_VALUE)) { //If data has set fixed value

                    allSelectedOptions.set(queIndex,dataValue);
                    fixedValueQueCount++;

                }else if( dataType.equals(Data.AUTO_INCREMENT)) { //If data is auto increment

                    autoIncQueCount = autoIncQueCount + 1;
                    if(dataValue.equals(Data.NO_VALUE))
                    {
                        Toast.makeText(this,"Incomplete information, please define answer types in data tab.",Toast.LENGTH_SHORT).show();
                    }
                    if(questions.size() == autoIncQueCount) {
                        if (reviewFlag) {
                            viewFlipper.setDisplayedChild(viewFlipper.getChildCount() - 1);
                            reviewFlag = false;
                        } else {
                            viewFlipper.showNext();
                        }
                    }
                    PrefUtils.saveToPrefs(this, PrefUtils.PREFS_QUESTION_INDEX, "0");

                }else if( dataType.equals(Data.SCAN_CODE)) { //If data is scan code
                    LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View viewScanCode = layoutInflater.inflate(R.layout.page_que_type_barcode_reader, null,true);
                    viewScanCode.setId(Integer.parseInt(queId));
                    viewScanCode.setTag(queId);

                    TextView questionText = (TextView) viewScanCode.findViewById(R.id.tv_question_text);
                    questionText.setText(question.getQuestionText());
                    questionText.setTypeface(CommonUtils.getInstance(this).getFontRobotoMedium());
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

            }else if(Question.PROJECT_DEFINED == queType){

                LinearLayout mainLinearLayout = new LinearLayout(this);
                mainLinearLayout.setBackgroundColor(Color.WHITE);
                mainLinearLayout.setOrientation(LinearLayout.VERTICAL);
                mainLinearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                ScrollView scrollView = new ScrollView(this);
                scrollView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                LinearLayout subLinearLayout = new LinearLayout(this);
                subLinearLayout.setOrientation(LinearLayout.VERTICAL);
                subLinearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                TextView questionTextView = new TextView(this);
                questionTextView.setTextColor(Color.WHITE);
                questionTextView.setTextSize(18);
                questionTextView.setTypeface(CommonUtils.getInstance(this).getFontRobotoMedium());
                questionTextView.setBackgroundColor(Color.GRAY);
                questionTextView.setText(question.getQuestionText());
                questionTextView.setLayoutParams( new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                subLinearLayout.addView(questionTextView);

                RelativeLayout.LayoutParams optionTVParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                int optionIvWidth = (screenWidth / 2) - 20;
                RelativeLayout.LayoutParams imageVParams = new RelativeLayout.LayoutParams(optionIvWidth, optionIvWidth);

                LinearLayout.LayoutParams linearlayoutweight = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1);

                LinearLayout rowLinearLayout = new LinearLayout(this);

                for (int i = 0; i < question.getOptions().size(); i++)
                {
                    String optionText = question.getOptions().get(i);
                    TextView optionTextView = new TextView(this);
                    optionTextView.setTextColor(Color.BLACK);
                    optionTextView.setTextSize(16);
                    optionTextView.setText(optionText);
                    optionTextView.setTypeface(CommonUtils.getInstance(this).getFontRobotoRegular());

                    ImageView imageView = new ImageView(this);
                    imageView.setId(i);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    imageView.setTag(queIndex);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        int displayedChild = viewFlipper.getDisplayedChild();
                        int childCount = viewFlipper.getChildCount();

                        allSelectedOptions.set(Integer.parseInt(v.getTag().toString()),question.getOptions().get(v.getId()));
                        if(reviewFlag)
                        {
                            viewFlipper.setDisplayedChild(viewFlipper.getChildCount()-1);
                            reviewFlag = false;

                            initReviewPage();
                        }
                        else {
                            viewFlipper.showNext();
                            if (displayedChild == childCount - 2 ) {
                                viewFlipper.stopFlipping();

                                initReviewPage();
                            }
                        }

                        }
                    });

                    Picasso.with(this)
                            .load(R.drawable.ic_launcher)
                            .error(R.drawable.ic_launcher)
                            .into(imageView);

                    RelativeLayout cellRelativeLayout = new RelativeLayout(this);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    cellRelativeLayout.setLayoutParams(params);

                    if(i%2 == 0)
                    {
                        rowLinearLayout = new LinearLayout(this);
                        rowLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
                        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        params1.setMargins(10,10,10,10);
                        rowLinearLayout.setLayoutParams(params1);


                        optionTextView.setId(1);
                        imageVParams.addRule(RelativeLayout.BELOW, optionTextView.getId());
                        imageVParams.setMargins(0, 10, 10, 10);

                        cellRelativeLayout.addView(optionTextView, optionTVParams);
                        cellRelativeLayout.addView(imageView, imageVParams);

                        rowLinearLayout.addView(cellRelativeLayout, linearlayoutweight);
                        if(i == question.getOptions().size() - 1){

                            subLinearLayout.addView(rowLinearLayout);
                        }

                    }
                    else
                    {
                        optionTextView.setId(1555555);
                        imageVParams.addRule(RelativeLayout.BELOW, optionTextView.getId());
                        imageVParams.setMargins(10, 10, 10, 0);

                        cellRelativeLayout.addView(optionTextView, optionTVParams);
                        cellRelativeLayout.addView(imageView, imageVParams);

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

        addReviewPage();

    }

    private void initReviewPage() {
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

            TextView tvQuestion = (TextView) reviewItem.findViewById(R.id.tv_question_text);
            TextView tvOption = (TextView) reviewItem.findViewById(R.id.tv_option_text);
            tvQuestion.setTextSize(18);
            tvQuestion.setTypeface(CommonUtils.getInstance(this).getFontRobotoMedium());
            tvOption.setTextSize(16);
            tvOption.setTypeface(CommonUtils.getInstance(this).getFontRobotoRegular());

            reviewItem.setTag(question.getQuestionId());

            String data_value = new String("");

            tvQuestion.setText(question.getQuestionText());

            //if selected option type is User_Selected, Fixed_Value, Auto_Increment, Scan_Code
            if(Question.USER_DEFINED == question.getQuestionType()){ //If question is user defined, then show the screen as per data type

                String userId = PrefUtils.getFromPrefs(this, PrefUtils.PREFS_LOGIN_USERNAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);
                Data data = dbHelper.getData(userId, projectId, question.getQuestionId());

                //Question and Option shown only if selected option type is 'Auto_Increment'
                if(data.getType().equals(Data.AUTO_INCREMENT))
                {
                    int index = Integer.parseInt(PrefUtils.getFromPrefs(this, PrefUtils.PREFS_QUESTION_INDEX, "-1"));
                    int optionValue = Integer.parseInt(CommonUtils.getAutoIncrementedValue(this, question.getQuestionId(), "" + index));

                    if(optionValue != -1) {
                        data_value = "" + optionValue;
                    }else{
                        data_value = "";
                    }
                    tvOption.setText(data_value);
                }
                else if(data.getType().equals(Data.FIXED_VALUE))
                {
                    data_value = data.getValue();
                    tvOption.setText(data_value);

                }
                else  //Question and Option shown except 'Auto_Increment' option type.(for User_Selected, Scan_Code)
                {
                    data_value = allSelectedOptions.get(queIndex);
                    tvOption.setText(data_value);

                    reviewItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            reviewFlag = true;
                            View child = viewFlipper.findViewWithTag(view.getTag());
                            viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(child));

                            viewFlipper.removeViewAt(viewFlipper.getChildCount()-1); //0 based index
                            addReviewPage();
                        }
                    });
//
                }

            }
            else  //Project mode Question and Option is display.
            {
                data_value = allSelectedOptions.get(queIndex);
                tvOption.setText(data_value);

                reviewItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        reviewFlag = true;
                        View child = viewFlipper.findViewWithTag(view.getTag());
                        viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(child));

                        viewFlipper.removeViewAt(viewFlipper.getChildCount()-1); //0 based index
                        addReviewPage();
                    }
                });
            }

            allOptions.add(data_value);

        }

        int viewCount = viewFlipper.getChildCount();
        refreshReviewPage(viewFlipper.getChildAt(viewCount - 1));

    }

    private void addReviewPage() {

        LayoutInflater infltr = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View reviewPage = infltr.inflate(R.layout.page_question_answer_review, null,false);
        reviewPage.setId(9595);
        viewFlipper.addView(reviewPage);

        List<Question> questions = dbHelper.getAllQuestionForProject(projectId);
        int queCount = questions.size();
        if(fixedValueQueCount == queCount || autoIncQueCount == queCount || fixedValueQueCount+autoIncQueCount == queCount){
            initReviewPage();
        }else {
            refreshReviewPage(reviewPage);
        }
    }

    private void refreshReviewPage(View reviewPage){

        int displayedChild = viewFlipper.getDisplayedChild();
        int childCount = viewFlipper.getChildCount();
        if(displayedChild == childCount - 1){
            getSupportActionBar().setTitle("Review");
        }else{
            getSupportActionBar().setTitle("");
        }

        mtvStatusMessage = (TextView) reviewPage.findViewById(R.id.tv_status_message);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (null != mBluetoothAdapter && !mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
        if (null == mBluetoothService) {
            mBluetoothService = new BluetoothService(this, mHandler);
        }

        if (mBluetoothService.getState() != BluetoothService.STATE_CONNECTED) {
            // Get the BLuetoothDevice object
            if(null == deviceAddress)
            {
                Toast.makeText(ProjectMeasurmentActivity.this, "Measurement device not configured, Please configure measurement device (bluetooth).", Toast.LENGTH_SHORT).show();
            }else {
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(deviceAddress);
                mBluetoothService.connect(device);
            }
        }

        btnTakeMeasurement = (Button) reviewPage.findViewById(R.id.btn_take_measurement);

        btnTakeMeasurement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btnTakeMeasurement.getText().equals("+ Take Measurement")){
                    mIsMeasureBtnClicked = true;
                    if (mBluetoothService == null) {
                        mBluetoothService = new BluetoothService(ProjectMeasurmentActivity.this, mHandler);
                    }
                    if (mBluetoothService.getState() != BluetoothService.STATE_CONNECTED)
                    {
                        // Get the BLuetoothDevice object
                        if(mBluetoothAdapter == null)
                            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                        if(null == deviceAddress)
                        {
                            Toast.makeText(ProjectMeasurmentActivity.this,"Measurement device not configured, Please configure measurement device (bluetooth).",Toast.LENGTH_SHORT).show();
                        }else {
                            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(deviceAddress);
                            mBluetoothService.connect(device);
                        }
                    }else {
                        mHandler.obtainMessage(Constants.MESSAGE_STATE_CHANGE, BluetoothService.STATE_CONNECTED, 1).sendToTarget();
                    }
                    btnTakeMeasurement.setText("Cancel");
                    btnTakeMeasurement.setBackgroundResource(R.drawable.btn_layout_red);
                }else if(btnTakeMeasurement.getText().equals("Cancel"))
                {
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
                Intent openMainActivity= new Intent(ProjectMeasurmentActivity.this, DirectionsActivity.class);
                openMainActivity.putExtra(DatabaseHelper.C_PROJECT_ID, projectId);
                startActivity(openMainActivity);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!scanMode )
        {
//            if(mIsMeasureBtnClicked)
//                viewFlipper.setDisplayedChild(0);

            List<Question> questions = dbHelper.getAllQuestionForProject(projectId);

            if(fixedValueQueCount == questions.size() || autoIncQueCount == questions.size() || fixedValueQueCount+autoIncQueCount == questions.size()) {
                initReviewPage();

            }else{
                int viewCount = viewFlipper.getChildCount();
                if(viewCount > 1)
                    viewFlipper.setDisplayedChild(0);
                refreshReviewPage(viewFlipper.getChildAt(viewCount - 1));
            }
            //clearflag = true;
        }
        scanMode = false;
        mIsMeasureBtnClicked = false;
        mIsCancelMeasureBtnClicked = false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == Activity.RESULT_OK) {
            String contents = intent.getStringExtra("SCAN_RESULT");

            int displayedChild = viewFlipper.getDisplayedChild();
            int childCount = viewFlipper.getChildCount();

            allSelectedOptions.set(requestCode,contents);
            if(reviewFlag)
            {
                viewFlipper.setDisplayedChild(viewFlipper.getChildCount()-1);
                reviewFlag = false;

                initReviewPage();
            }
            else {
                viewFlipper.showNext();
                if (displayedChild == childCount - 2 ) {
                    viewFlipper.stopFlipping();

                    initReviewPage();
                }
            }

            Toast.makeText(this, contents, Toast.LENGTH_SHORT).show();
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // Handle cancel
            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
        }
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

    private void sendData(String data) {
        // Check that we're actually connected before trying anything
        if (mBluetoothService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(getApplicationContext(),"Not Connected", Toast.LENGTH_SHORT).show();
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
                    if(Constants.D) Log.i("PHOTOSYNC", "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            if(msg.arg2 == 0){//Sending cancel request to the device
                                sendData("-1+-1+");
                                if (mBluetoothService != null) {
                                    if(mBluetoothService.getState() == BluetoothService.STATE_CONNECTED){
                                        mBluetoothService.stop();
                                    }
                                }
                                mtvStatusMessage.setText("Measurement cancel");
                            }else if(msg.arg2 == 1){ //Send measurement request
                                mtvStatusMessage.setText(R.string.title_connected_to);
                                mtvStatusMessage.append(mConnectedDeviceName);

                                ResearchProject researchProject = dbHelper.getResearchProject(projectId);
                                if (null == researchProject) {
                                    Toast.makeText(ProjectMeasurmentActivity.this, "Project not selected, Please select the project.", Toast.LENGTH_LONG).show();
                                    break;
                                }

                                try {
                                    String protocolJson= "";
                                    StringBuffer dataString = new StringBuffer();
                                    String[] projectProtocols = researchProject.getProtocols_ids().split(",");
                                    if (projectProtocols.length >= 1) {
                                        for (String protocolId : projectProtocols) {
                                            if(protocolId.equals(""))
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

                                        if(dataString.length() > 0) {
                                            String data = "var protocols={" + dataString.substring(0, dataString.length() - 1) + "}";

                                            // Writing macros_variable.js file with protocol and macro relations
                                            System.out.println("######Writing macros_variable.js file:" + data);
                                            CommonUtils.writeStringToFile(ProjectMeasurmentActivity.this, "macros_variable.js", data);

                                            protocolJson = "[" + protocolJson.substring(0, protocolJson.length() - 1) + "]"; // remove last comma and add suqare brackets and start and end.

                                            System.out.println("$$$$$$$$$$$$$$ protocol json sending to device :" + protocolJson + "length:" + protocolJson.length());

                                            sendData(protocolJson);

                                            mtvStatusMessage.setText("Initializing measurement please wait ...");

                                        }else{

                                            mtvStatusMessage.setText("No protocol defined for this project.");
                                            Toast.makeText(ProjectMeasurmentActivity.this, "No protocol defined for this project.", Toast.LENGTH_LONG).show();

                                            if(btnTakeMeasurement != null) {
                                                if (btnTakeMeasurement.getText().equals("Cancel")) {
                                                    btnTakeMeasurement.setText("+ Take Measurement");
                                                    btnTakeMeasurement.setBackgroundResource(R.drawable.btn_layout_orange);
                                                }
                                            }
                                        }
                                    } else {

                                        mtvStatusMessage.setText("No protocol defined for this project.");
                                        Toast.makeText(ProjectMeasurmentActivity.this, "No protocol defined for this project.", Toast.LENGTH_LONG).show();
                                        if(btnTakeMeasurement != null) {
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

                            }else{
                                if(mIsMeasureBtnClicked) {
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
                    if(mIsCancelMeasureBtnClicked == false) {
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
                            for (int i = 0; i < allOptions.size(); i++) {
                                options.append("\"" + allOptions.get(i) + "\"");
                                if (i < allOptions.size() - 1)
                                    options.append(",");
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
                    mIsMeasureBtnClicked = false;
                    mIsCancelMeasureBtnClicked = false;
                    if(btnTakeMeasurement != null) {
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
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();

                    break;
                case Constants.MESSAGE_TOAST:
                    if(mIsCancelMeasureBtnClicked == false){
                        Toast.makeText(ProjectMeasurmentActivity.this, msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    mIsMeasureBtnClicked = false;
                    mIsCancelMeasureBtnClicked = false;
                    if(btnTakeMeasurement != null) {
                        if (btnTakeMeasurement.getText().equals("Cancel")) {
                            btnTakeMeasurement.setText("+ Take Measurement");
                            btnTakeMeasurement.setBackgroundResource(R.drawable.btn_layout_orange);
                        }
                    }
                    break;
                case Constants.MESSAGE_STOP:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(Constants.TOAST),
                            Toast.LENGTH_SHORT).show();
                    mBluetoothService.stop();
                    break;
            }
        }
    };

}
