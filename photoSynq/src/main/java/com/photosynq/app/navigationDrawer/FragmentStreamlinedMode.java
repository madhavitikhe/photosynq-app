package com.photosynq.app.navigationDrawer;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.DialogFragment;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.gson.Gson;
import com.google.zxing.client.android.CaptureActivity;
import com.photosynq.app.DirectionsActivity;
import com.photosynq.app.DisplayResultsActivity;
import com.photosynq.app.R;
import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.AppSettings;
import com.photosynq.app.model.Data;
import com.photosynq.app.model.Protocol;
import com.photosynq.app.model.Question;
import com.photosynq.app.model.ResearchProject;
import com.photosynq.app.utils.BluetoothService;
import com.photosynq.app.utils.CommonUtils;
import com.photosynq.app.utils.LocationUtils;
import com.photosynq.app.utils.PrefUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class FragmentStreamlinedMode extends Fragment implements LocationListener,
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    private DatabaseHelper db;
    private String projectId;
    private String deviceAddress;
    private Context ctx;
    ArrayList<String> allSelectedOptions;
    ArrayList<String> allSelectedQuestions;
    private Handler handler = new Handler();
    private boolean scanMode = false;
    private int autoIncProjecSize = 0;
    private String protocolJson = "";
    private Data data;
    ViewFlipper viewFlipper;
    private String userId;
    AppSettings appSettings;

    // A request to connect to Location Services
    private LocationRequest mLocationRequest;

    // Stores the current instantiation of the location client in this object
    private LocationClient mLocationClient;
    //String[n] array;

    // Message types sent from the BluetoothService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_STOP = 6;
    private static final boolean D = true;

    // Key names received from the BluetoothService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";


    //private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private BluetoothService mBluetoothService = null;
    private BluetoothAdapter mBluetoothAdapter = null;
    private TextView mStatusLine;
    private String mConnectedDeviceName = null;
//    private String option1 = "";
//    private String option3 = "";
//    private String option2 = "";
      ArrayList<String> allOptions;

    private boolean clearflag = false;
    private boolean reviewFlag = false;
    private int fixedValueCount = 0;
    private boolean mIsMeasureBtnClicked = false;
    private boolean mIsCancelMeasureBtnClicked = false;
    private Button measureButton;
    //private ProgressDialog pDialog;

    public static FragmentStreamlinedMode newInstance() {
        Bundle bundle = new Bundle();
        FragmentStreamlinedMode fragment = new FragmentStreamlinedMode();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_streamlined_mode, container, false);

        fixedValueCount = 0;

        ActionBar actionBar = getActivity().getActionBar();
        if(actionBar != null)
            actionBar.hide();

        //Location related

        // Create a new global location parameters object
        mLocationRequest = LocationRequest.create();
        //  Set the update interval
        mLocationRequest.setInterval(LocationUtils.UPDATE_INTERVAL_IN_MILLISECONDS);
        // Use high accuracy
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // Set the interval ceiling to one minute
        mLocationRequest.setFastestInterval(LocationUtils.FAST_INTERVAL_CEILING_IN_MILLISECONDS);
        /*
         * Create a new location client, using the enclosing class to
         * handle callbacks.
         */
        ctx = getActivity();
        mLocationClient = new LocationClient(ctx, this, this);
//        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View contentView = inflater.inflate(R.layout.activity_streamlined_mode, null, false);
//        layoutDrawer.addView(contentView,1);
        allSelectedOptions= new ArrayList<String>();
        allSelectedQuestions = new ArrayList<String>();


        //Show question and option on viewflipper.
        //db = new DatabaseHelper(ctx);
        userId = PrefUtils.getFromPrefs(ctx, PrefUtils.PREFS_LOGIN_USERNAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);
        db = DatabaseHelper.getHelper(ctx);
        projectId = db.getSettings(userId).getProjectId();
        deviceAddress = db.getSettings(userId).getConnectionId();
        final List<Question> questions = db.getAllQuestionForProject(projectId);
        //db.closeDB();
        viewFlipper = (ViewFlipper) rootView.findViewById(R.id.ViewFlipper01);
        int questionLoop =0;

        if(null == projectId)
        {
            Toast.makeText(ctx,"Project not selected, Please select the project.",Toast.LENGTH_SHORT).show();
        }
        if(null == deviceAddress)
        {
            Toast.makeText(ctx,"Measurement device not configured, Please configure measurement device (bluetooth).",Toast.LENGTH_SHORT).show();
        }

        boolean isAnsIncomplete = false;

        for (final Question question : questions) {
            scanMode = false;
            allSelectedOptions.add(questionLoop,"");
            LinearLayout mainLinearLayout = new LinearLayout(ctx);
            mainLinearLayout.setOrientation(LinearLayout.VERTICAL);
            mainLinearLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

            ScrollView scrollView = new ScrollView(ctx);
            scrollView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

            LinearLayout subLinearLayout = new LinearLayout(ctx);
            subLinearLayout.setOrientation(LinearLayout.VERTICAL);
            subLinearLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));


            TextView questionTextView = new TextView(ctx);
            questionTextView.setTextColor(Color.WHITE);
            questionTextView.setTextSize(25);
            questionTextView.setBackgroundColor(Color.GRAY);
            questionTextView.setText(question.getQuestionText());
            questionTextView.setLayoutParams( new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

            subLinearLayout.addView(questionTextView);

            Data data ;
            data = db.getData(userId, projectId, question.getQuestionId());
            //if( !data.getType().isEmpty() && !data.getValue().isEmpty())
            if(question.getQuestionType()!= Question.PROJECT_DEFINED)
            {
                if( null == data.getType() || null == data.getValue())
                {
                    isAnsIncomplete = true;
                }

                if(null != data.getType() && data.getType().equals(Data.USER_SELECTED))
                {
                    LayoutInflater infltr = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View viewUserSelected = infltr.inflate(R.layout.user_selected, null, false);
                    viewUserSelected.setTag(question.getQuestionId());
                    TextView questionText = (TextView)viewUserSelected.findViewById(R.id.question_layout);
                    questionText.setText(question.getQuestionText());


                    Button showNext = (Button) viewUserSelected.findViewById(R.id.next);
                    showNext.setTag(questionLoop);
                    showNext.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        EditText userEnteredAnswer = (EditText) ((View)v.getParent()).findViewById(R.id.userAnswer);
                            int displayedChild = viewFlipper.getDisplayedChild();
                            int childCount = viewFlipper.getChildCount();
                            allSelectedQuestions.add(new Gson().toJson(question));
                            //allSelectedOptions.add(Integer.parseInt(v.getTag().toString()),userEnteredAnswer.getText().toString());
                            String str = userEnteredAnswer.getText().toString().trim();
                            if(true == str.matches(".*['{}!].*") ||
                                    true == str.contains("\\n") ||
                                    true == str.contains("[")||
                                    true == str.contains("]")){
                               Toast.makeText(ctx,"Re-Enter Answer... special character is not allowed",Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                if (displayedChild == childCount - 2 ) {
                                    viewFlipper.stopFlipping();
                                    if(reviewFlag)
                                    {
                                        allSelectedOptions.set(Integer.parseInt(v.getTag().toString()),userEnteredAnswer.getText().toString());
                                        viewFlipper.setDisplayedChild(viewFlipper.getChildCount()-1);
                                        reviewFlag = false;
                                    }
                                    else {
                                        allSelectedOptions.set(Integer.parseInt(v.getTag().toString()),userEnteredAnswer.getText().toString());
                                        viewFlipper.showNext();
                                    }
                                    setMeasurementScreen();
                                }
                                else
                                {
                                    if(reviewFlag)
                                    {
                                        allSelectedOptions.set(Integer.parseInt(v.getTag().toString()),userEnteredAnswer.getText().toString());
                                        setMeasurementScreen();
                                        viewFlipper.setDisplayedChild(viewFlipper.getChildCount()-1);
                                        reviewFlag = false;
                                    }
                                    else {
                                        allSelectedOptions.set(Integer.parseInt(v.getTag().toString()),userEnteredAnswer.getText().toString());
                                        viewFlipper.showNext();
                                    }
                                }
                                userEnteredAnswer.setText("");
                            }
                        }
                    });

                    viewFlipper.addView(viewUserSelected);
                }
                else if(null != data.getType() && data.getType().equals(Data.FIXED_VALUE))
                {
                    //if(questionLoop<= allSelectedOptions.size())
                    allSelectedOptions.set(questionLoop,data.getValue());
                    fixedValueCount++;
                }
                else if(null != data.getType() && data.getType().equals(Data.AUTO_INCREMENT))
                {
                    autoIncProjecSize = autoIncProjecSize + 1;
                    if((data.getValue()).equals(Data.NO_VALUE))
                    {
                        Toast.makeText(ctx,"Incomplete information, please define answer types in data tab.",Toast.LENGTH_SHORT).show();
//                        return;
                    }
                    if(questions.size() == autoIncProjecSize)
                    {
                    allSelectedQuestions.add(new Gson().toJson(question));
                    if(reviewFlag)
                    {
                        viewFlipper.setDisplayedChild(viewFlipper.getChildCount()-1);
                        reviewFlag = false;
                    }
                    else {
                        viewFlipper.showNext();
                    }
//                        setMeasurementScreen();
                }

                    PrefUtils.saveToPrefs(ctx, PrefUtils.PREFS_QUESTION_INDEX, "0");
                }
                else if(null != data.getType() && data.getType().equals(Data.SCAN_CODE))
                {
                    LayoutInflater infltr = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View viewScanCode = infltr.inflate(R.layout.barcode_reader, null,true);
                    viewScanCode.setId(Integer.parseInt(question.getQuestionId()));
                    viewScanCode.setTag(question.getQuestionId());

                    //final TextView txtScanResult = (TextView) viewScanCode.findViewById(R.id.scan_result);
                    TextView questionText = (TextView) viewScanCode.findViewById(R.id.question_layout);
                    questionText.setText(question.getQuestionText());
                     View btnScan = viewScanCode.findViewById(R.id.scan_button);
                        btnScan.setTag(questionLoop);
                        btnScan.setOnClickListener(new OnClickListener() {
                            public void onClick(View v) {
                                scanMode = true;
                                allSelectedQuestions.add(new Gson().toJson(question));
                                Intent intent = new Intent(ctx,CaptureActivity.class);
                                intent.setAction("com.google.zxing.client.android.SCAN");
                                // this stops saving ur barcode in barcode scanner app's history
                                intent.putExtra("SAVE_HISTORY", false);
                                //int viewId = ((View)v.getParent().getParent()).getId();
                                startActivityForResult(intent, Integer.parseInt(v.getTag().toString()));
                            }
                        });

                    viewFlipper.addView(viewScanCode);
                }
            }
            else
            {
             for (int i=1; i<= question.getOptions().size(); i++)
             {
                if(i%2==0)
                {

                }
                else
                {
                    LinearLayout detailsLinearLayout = new LinearLayout(ctx);
                    detailsLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
                    detailsLinearLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

                    RelativeLayout optionsRelativeLayout = new RelativeLayout(ctx);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
                    optionsRelativeLayout.setLayoutParams(params);

                    TextView optionTextView = new TextView(ctx);
                    optionTextView.setId(1);
                    optionTextView.setTextColor(Color.BLACK);
                    optionTextView.setTextSize(20);
                    RelativeLayout.LayoutParams optionTVParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

                    optionTextView.setText(question.getOptions().get(i-1));
                    ImageView imageView = new ImageView(ctx);
                    imageView.setId(i - 1);
                    RelativeLayout.LayoutParams imageVParams = new RelativeLayout.LayoutParams(300,300);
                    imageVParams.setMargins(10,10,10,10);
                    imageVParams.addRule(RelativeLayout.BELOW, optionTextView.getId());
                    imageView.setTag(questionLoop);
                    imageView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            int displayedChild = viewFlipper.getDisplayedChild();
                            int childCount = viewFlipper.getChildCount();
                            allSelectedQuestions.add(new Gson().toJson(question));
                            //allSelectedOptions.add(question.getOptions().get(v.getId()));

                            //allSelectedOptions.add(Integer.parseInt(v.getTag().toString()),question.getOptions().get(v.getId()));
                            for (int i = 0; i < allSelectedOptions.size(); i++)
                            {
                                System.out.println(allSelectedOptions.get(i));
                            }
                            if (displayedChild == childCount - 2) {
                                viewFlipper.stopFlipping();
                                if(reviewFlag)
                                {
                                    allSelectedOptions.set(Integer.parseInt(v.getTag().toString()),question.getOptions().get(v.getId()));
                                    viewFlipper.setDisplayedChild(viewFlipper.getChildCount()-1);
                                    reviewFlag = false;
                                }
                                else {
                                    allSelectedOptions.set(Integer.parseInt(v.getTag().toString()),question.getOptions().get(v.getId()));
                                    viewFlipper.showNext();
                                }
                                setMeasurementScreen();
                            }
                            else{
                                if(reviewFlag)
                                {
                                    allSelectedOptions.set(Integer.parseInt(v.getTag().toString()),question.getOptions().get(v.getId()));
                                    setMeasurementScreen();
                                    viewFlipper.setDisplayedChild(viewFlipper.getChildCount()-1);
                                    reviewFlag = false;
                                }
                                else {
                                    allSelectedOptions.set(Integer.parseInt(v.getTag().toString()),question.getOptions().get(v.getId()));
                                    viewFlipper.showNext();
                                }
                            }
                        }
                    });

                    Picasso.with(ctx)
                    .load(R.drawable.ic_launcher)
                    .error(R.drawable.ic_launcher)
                    .into(imageView);
                    optionsRelativeLayout.addView(optionTextView, optionTVParams);
                    optionsRelativeLayout.addView(imageView, imageVParams);
                    LinearLayout.LayoutParams relativelayoutweight = new LinearLayout.LayoutParams(	LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1);
                    detailsLinearLayout.addView(optionsRelativeLayout,relativelayoutweight);

                    if(i <question.getOptions().size())
                    {
                        RelativeLayout optionsRelativeLayouteven = new RelativeLayout(ctx);
                        RelativeLayout.LayoutParams paramseven = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
                        optionsRelativeLayouteven.setLayoutParams(paramseven);

                        TextView optionTextVieweven = new TextView(ctx);
                        optionTextVieweven.setId(1555555);
                        optionTextVieweven.setTextColor(Color.BLACK);
                        optionTextVieweven.setTextSize(20);
                        RelativeLayout.LayoutParams optionTVParamseven = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

                        optionTextVieweven.setText(question.getOptions().get(i));
                        ImageView imageVieweven = new ImageView(ctx);
                        imageVieweven.setId(i);
                        RelativeLayout.LayoutParams imageVParamseven = new RelativeLayout.LayoutParams(300, 300);
                        imageVParamseven.setMargins(10,10,10,10);
                        imageVParamseven.addRule(RelativeLayout.BELOW, optionTextVieweven.getId());
                        imageVieweven.setTag(questionLoop);
                        imageVieweven.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int displayedChild = viewFlipper.getDisplayedChild();
                                int childCount = viewFlipper.getChildCount();
                                allSelectedQuestions.add(new Gson().toJson(question));

                                for(int i=0;i<allSelectedOptions.size();i++)
                                {
                                    System.out.println(allSelectedOptions.get(i));
                                }
                                if (displayedChild == childCount - 2) {
                                    viewFlipper.stopFlipping();

                                    if(reviewFlag)
                                    {
                                        allSelectedOptions.set(Integer.parseInt(v.getTag().toString()),question.getOptions().get(v.getId()));
                                        viewFlipper.setDisplayedChild(viewFlipper.getChildCount()-1);
                                        reviewFlag = false;
                                    }
                                    else {
                                        allSelectedOptions.set(Integer.parseInt(v.getTag().toString()),question.getOptions().get(v.getId()));
                                        viewFlipper.showNext();
                                    }
                                    setMeasurementScreen();
                                }
                                else{
                                    if(reviewFlag)
                                    {
                                        allSelectedOptions.set(Integer.parseInt(v.getTag().toString()),question.getOptions().get(v.getId()));
                                        setMeasurementScreen();
                                        viewFlipper.setDisplayedChild(viewFlipper.getChildCount()-1);
                                        reviewFlag = false;
                                    }
                                    else {
                                        allSelectedOptions.set(Integer.parseInt(v.getTag().toString()),question.getOptions().get(v.getId()));
                                        viewFlipper.showNext();
                                    }
                                }
                            }
                        });

                        Picasso.with(ctx)
                        .load(R.drawable.ic_launcher)
                        .error(R.drawable.ic_launcher)
                        .into(imageVieweven);
                        optionsRelativeLayouteven.addView(optionTextVieweven, optionTVParamseven);
                        optionsRelativeLayouteven.addView(imageVieweven, imageVParamseven);
                        //LinearLayout.LayoutParams relativelayoutweight = new LinearLayout.LayoutParams(	LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1);
                        detailsLinearLayout.addView(optionsRelativeLayouteven,relativelayoutweight);
                    }
                    subLinearLayout.addView(detailsLinearLayout);
                }
            }
            scrollView.addView(subLinearLayout);
            mainLinearLayout.addView(scrollView);
            mainLinearLayout.setTag(question.getQuestionId());
            viewFlipper.addView(mainLinearLayout);
        }
            questionLoop++;
    }

        if(isAnsIncomplete){
            Toast.makeText(ctx,"Incomplete information, please define answer types in data tab.",Toast.LENGTH_SHORT).show();
        }else{
            String showDirections = PrefUtils.getFromPrefs(ctx, PrefUtils.PREFS_SHOW_DIRECTIONS, "YES");
            if(showDirections.equals("YES")){
                if(null != projectId && null != deviceAddress) {
                    Intent openMainActivity = new Intent(ctx, DirectionsActivity.class);
                    openMainActivity.putExtra(DatabaseHelper.C_PROJECT_ID, projectId);
                    openMainActivity.putExtra(Utils.APP_MODE, Utils.APP_MODE_STREAMLINE);
                    startActivity(openMainActivity);
                }
            }
        }

        LayoutInflater infltr = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View measurementScreen = infltr.inflate(R.layout.activity_display_selected_questions_options, null,false);
        mStatusLine = (TextView) measurementScreen.findViewById(R.id.statusMessage);
        measurementScreen.setId(9595);
        viewFlipper.addView(measurementScreen);
//        if(fixedValueCount == questions.size()) {
//            setMeasurementScreen();
//        }
        // add on click listener
        final Button measureButton = (Button)measurementScreen.findViewById(R.id.measure_btn);
        measureButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mIsMeasureBtnClicked = true;
                if (mBluetoothService == null) {
                    mBluetoothService = new BluetoothService(ctx, mHandler);
                }
                if (measureButton.getText().equals("MEASURE")) {
                    if (mBluetoothService.getState() != BluetoothService.STATE_CONNECTED) {
                        // Get the BLuetoothDevice object
                        if (mBluetoothAdapter == null)
                            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                        if (null == deviceAddress) {
                            Toast.makeText(ctx, "Measurement device not configured, Please configure measurement device (bluetooth).", Toast.LENGTH_SHORT).show();
                        } else {
                            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(deviceAddress);
                            mBluetoothService.connect(device);
                        }
                    } else {
                        mHandler.obtainMessage(MESSAGE_STATE_CHANGE, BluetoothService.STATE_CONNECTED, -1).sendToTarget();
                    }
                    measureButton.setText("CANCEL");
                    measureButton.setBackgroundColor(Color.RED);
                }else if(measureButton.getText().equals("CANCEL"))
                {
                    mIsCancelMeasureBtnClicked = true;
                    mHandler.obtainMessage(MESSAGE_STATE_CHANGE, BluetoothService.STATE_CONNECTED, 0).sendToTarget();
                    measureButton.setText("MEASURE");
                    measureButton.setBackgroundColor(Color.GRAY);
                }
            }
        });
        Button directionsButton = (Button)measurementScreen.findViewById(R.id.directions_btn);
        directionsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openMainActivity= new Intent(ctx, DirectionsActivity.class);
                openMainActivity.putExtra(DatabaseHelper.C_PROJECT_ID, projectId);
                openMainActivity.putExtra(Utils.APP_MODE, Utils.APP_MODE_STREAMLINE);
                startActivity(openMainActivity);
            }
        });
        rootView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT ));
        return rootView;
    }


    @Override
	public void onResume() {
		super.onResume();
		if (!scanMode )
		{
            if(mIsMeasureBtnClicked)
                viewFlipper.setDisplayedChild(0);

            List<Question> questions = db.getAllQuestionForProject(projectId);

            int viewCount = viewFlipper.getChildCount();
            refreshMeasrementScreen(viewFlipper.getChildAt(viewCount - 1));

            //allSelectedOptions = new ArrayList<String>();
            //allSelectedQuestions = new ArrayList<String>();

            //fixedValueCount = getActivity().getIntent().getExtras().getInt("fixedValueCount");
            //autoIncProjecSize = getActivity().getIntent().getExtras().getInt("autoIncProjecSize");
            if(fixedValueCount == questions.size() || autoIncProjecSize == questions.size()|| fixedValueCount+autoIncProjecSize == questions.size()) {
                setMeasurementScreen();
            }
            clearflag = true;
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

            if (displayedChild == childCount - 2) {
                viewFlipper.stopFlipping();

                if(reviewFlag)
                {
                    allSelectedOptions.set(requestCode,contents);
                    viewFlipper.setDisplayedChild(viewFlipper.getChildCount()-1);
                    reviewFlag = false;
                }
                else {
                    allSelectedOptions.set(requestCode,contents);
                    viewFlipper.showNext();
                }
                setMeasurementScreen();
            }
            else
            {
                if(reviewFlag)
                {
                    allSelectedOptions.set(requestCode,contents);
                    setMeasurementScreen();
                    viewFlipper.setDisplayedChild(viewFlipper.getChildCount()-1);
                    reviewFlag = false;
                }
                else {
                    allSelectedOptions.set(requestCode,contents);
                    viewFlipper.showNext();
                }
            }


            Toast.makeText(ctx, contents, Toast.LENGTH_SHORT).show();
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // Handle cancel
            Toast.makeText(ctx, "Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    private void refreshMeasrementScreen(View measurementScreen){
        mStatusLine = (TextView) measurementScreen.findViewById(R.id.statusMessage);
        measureButton = (Button)measurementScreen.findViewById(R.id.measure_btn);
        if (mBluetoothService == null) {
            mBluetoothService = new BluetoothService(ctx, mHandler);
        }
        if (mBluetoothService.getState() == BluetoothService.STATE_CONNECTED)
        {
            TextView status = (TextView)measurementScreen.findViewById(R.id.statusMessage);

            status.setText(R.string.connected); //getResources().getString(R.string.connected);
        }
        measureButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mIsMeasureBtnClicked = true;
                if(measureButton.getText().equals("MEASURE")){
                    if (mBluetoothService == null) {
                        mBluetoothService = new BluetoothService(ctx, mHandler);
                    }
                    if (mBluetoothService.getState() != BluetoothService.STATE_CONNECTED)
                    {
                        // Get the BLuetoothDevice object
                        if(mBluetoothAdapter == null)
                            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                        if(null == deviceAddress)
                        {
                            Toast.makeText(ctx,"Measurement device not configured, Please configure measurement device (bluetooth).",Toast.LENGTH_SHORT).show();
                        }else {
                            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(deviceAddress);
                            mBluetoothService.connect(device);
                        }
                    }else {
                        mHandler.obtainMessage(MESSAGE_STATE_CHANGE, BluetoothService.STATE_CONNECTED, -1).sendToTarget();
                    }
                    measureButton.setText("CANCEL");
                    measureButton.setBackgroundColor(Color.RED);
                }else if(measureButton.getText().equals("CANCEL"))
                {
                    mIsCancelMeasureBtnClicked = true;
                    mHandler.obtainMessage(MESSAGE_STATE_CHANGE, BluetoothService.STATE_CONNECTED, 0).sendToTarget();
                    measureButton.setText("MEASURE");
                    measureButton.setBackgroundColor(Color.GRAY);
                }
            }
        });
        Button directionsButton = (Button)measurementScreen.findViewById(R.id.directions_btn);
        directionsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openMainActivity= new Intent(ctx, DirectionsActivity.class);
                openMainActivity.putExtra(DatabaseHelper.C_PROJECT_ID, projectId);
                openMainActivity.putExtra(Utils.APP_MODE, Utils.APP_MODE_STREAMLINE);
                startActivity(openMainActivity);
            }
        });
    }

    private void openQuestionScreen() {
        viewFlipper.removeViewAt(viewFlipper.getChildCount()-1); //0 based index
        LayoutInflater infltr = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View measurementScreen = infltr.inflate(R.layout.activity_display_selected_questions_options, null,false);
        measurementScreen.setId(9595);
        viewFlipper.addView(measurementScreen);
        refreshMeasrementScreen(measurementScreen);
    }


    /// Ported code from here

//    public void takeMeasurement(View view) throws JSONException
//    {
//        if (mBluetoothService.getState() != BluetoothService.STATE_CONNECTED)
//        {
//            // Get the BLuetoothDevice object
//            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(deviceAddress);
//            mBluetoothService.connect(device);
//        }else {
//            mHandler.obtainMessage(MESSAGE_STATE_CHANGE, BluetoothService.STATE_CONNECTED, -1).sendToTarget();
//        }
//
//    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    if(D) Log.i("PHOTOSYNC", "MESSAGE_STATE_CHANGE: " + msg.arg1);

                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            if(msg.arg2 == 0){//Sending cancel request to the device
                                sendData("-1+-1+");
                                if (mBluetoothService != null) {
                                    if(mBluetoothService.getState() == BluetoothService.STATE_CONNECTED){
                                        mBluetoothService.stop();
                                    }
                                }
                                mStatusLine.setText("Measurement cancel");
                            }else {
                                mStatusLine.setText(R.string.title_connected_to);
                                mStatusLine.append(mConnectedDeviceName);
                                if (protocolJson.length() == 0) {
                                    //db = new DatabaseHelper(getApplicationContext());
                                    db = DatabaseHelper.getHelper(ctx);
                                    ResearchProject rp = db.getResearchProject(projectId);
                                    if (null == rp) {
                                        Toast.makeText(ctx, "Project not selected, Please select the project.", Toast.LENGTH_LONG).show();
                                        break;
                                    }
                                    String[] protocol_ids = rp.getProtocols_ids().trim().split(",");
                                    System.out.println("***************Sequence of protocol id is***********" + rp.getProtocols_ids());

                                    try {
                                        StringBuffer dataString = new StringBuffer();
                                        String[] projectProtocols = rp.getProtocols_ids().split(",");
                                        if (rp.getProtocols_ids().length() >= 1) {
                                            //JSONArray protocolJsonArray = new JSONArray();
                                            for (String protocolId : projectProtocols) {
                                                Protocol protocol = db.getProtocol(protocolId);
                                                JSONObject detailProtocolObject = new JSONObject();
                                                detailProtocolObject.put("protocolid", protocol.getId());
                                                detailProtocolObject.put("protocol_name", protocol.getId());
                                                detailProtocolObject.put("macro_id", protocol.getMacroId());
                                                //protocolJsonArray.put(detailProtocolObject);
                                                dataString.append("\"" + protocol.getId() + "\"" + ":" + detailProtocolObject.toString() + ",");

                                                if (protocol.getProtocol_json().trim().length() > 1) {
                                                    protocolJson += "{" + protocol.getProtocol_json().trim().substring(1, protocol.getProtocol_json().trim().length() - 1) + "},";
                                                }

                                            }


                                            String data = "var protocols={" + dataString.substring(0, dataString.length() - 1) + "}";

                                            // Writing macros_variable.js file with protocol and macro relations
                                            System.out.println("######Writing macros_variable.js file:" + data);
                                            CommonUtils.writeStringToFile(ctx, "macros_variable.js", data);

                                            protocolJson = "[" + protocolJson.substring(0, protocolJson.length() - 1) + "]"; // remove last comma and add suqare brackets and start and end.

                                            System.out.println("$$$$$$$$$$$$$$ protocol json sending to device :" + protocolJson + "length:" + protocolJson.length());
                                            //db.closeDB();
                                            //String obj = "[{\"environmental\":[[\"light_intensity\",0]],\"tcs_to_act\":100,\"protocol_name\":\"baseline_sample\",\"protocols_delay\":5,\"act_background_light\":20,\"actintensity1\":5,\"actintensity2\":5,\"averages\":1,\"wait\":0,\"cal_true\":2,\"analog_averages\":1,\"pulsesize\":10,\"pulsedistance\":3000,\"calintensity\":255,\"pulses\":[400],\"detectors\":[[34]],\"measlights\":[[14]]},{\"tcs_to_act\":100,\"environmental\":[[\"relative_humidity\",0],[\"temperature\",0],[\"light_intensity\",0]],\"protocols_delay\":5,\"act_background_light\":20,\"protocol_name\":\"fluorescence\",\"baselines\":[1,1,1,1],\"averages\":1,\"wait\":0,\"cal_true\":0,\"analog_averages\":1,\"act_light\":20,\"pulsesize\":10,\"pulsedistance\":10000,\"actintensity1\":5,\"actintensity2\":50,\"measintensity\":7,\"calintensity\":255,\"pulses\":[50,50,50,50],\"detectors\":[[34],[34],[34],[34]],\"measlights\":[[15],[15],[15],[15]],\"act\":[0,1,0,0]},{\"protocol_name\":\"chlorophyll_spad_ndvi\",\"baselines\":[0,0,0,0],\"environmental\":[[\"relative_humidity\",1],[\"temperature\",1],[\"light_intensity\",1]],\"measurements\":1,\"measurements_delay\":1,\"averages\":1,\"wait\":0,\"cal_true\":0,\"analog_averages\":1,\"pulsesize\":20,\"pulsedistance\":3000,\"actintensity1\":8,\"actintensity2\":8,\"measintensity\":80,\"calintensity\":255,\"pulses\":[100],\"detectors\":[[34,35,35,34]],\"measlights\":[[12,20,12,20]]}]";
                                            //	String protocol= "[{\"protocol_name\":\"fluorescence\",\"baselines\":[1,1,1,1],\"averages\":1,\"wait\":0,\"cal_true\":0,\"analog_averages\":12,\"act_light\":20,\"pulsesize\":50,\"pulsedistance\":3000,\"actintensity1\":100,\"actintensity2\":100,\"measintensity\":3,\"calintensity\":255,\"pulses\":[50,50,50,50],\"detectors\":[[34],[34],[34],[34]],\"measlights\":[[15],[15],[15],[15]],\"act\":[2,1,2,2]}]";
//    	                		for (String chunk : protocolJson.split("(?<=,)")) {
//    								sendData(chunk);
//    							}
                                            sendData(protocolJson);
                                        } else {
                                            mStatusLine.setText("No protocol defined for this project.");
                                            Toast.makeText(ctx, "No protocol defined for this project.", Toast.LENGTH_LONG).show();
                                            break;
                                        }

                                    } catch (JSONException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                } else {
                                    protocolJson = "[" + protocolJson + "]";
                                    System.out.println("sending protocol to device using quick measure : " + protocolJson + "length:" + protocolJson.length());
                                    sendData(protocolJson);
                                }

                                mStatusLine.setText("Initializing measurement please wait ...");
                            }

                            break;
                        case BluetoothService.STATE_CONNECTING:
                            mStatusLine.setText(R.string.title_connecting);
                            break;
                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:
                            if(null!=mStatusLine)
                            mStatusLine.setText(R.string.title_not_connected);
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    //byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    //String writeMessage = new String(writeBuf);
                    break;
                case MESSAGE_READ:
                    if(mIsCancelMeasureBtnClicked == false) {
                        // byte[] readBuf = (byte[]) msg.obj;
                        StringBuffer measurement = (StringBuffer) msg.obj;
                        // construct a string from the valid bytes in the buffer
                        // String readMessage = new String(readBuf, 0, msg.arg1);
                        mStatusLine.setText(R.string.connected);
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
//                    String options = new String ("\"user_answers\": [\""+option1+"\","+"\""+option2+"\","+"\""+option3+"\" ],");
                        long time = System.currentTimeMillis();
                        if (options.equals("")) {
                            dataString = "var data = [\n" + measurement.toString().replaceAll("\\r\\n", "").replaceAll("\\{", "{\"time\":\"" + time + "\",") + "\n];";
                            System.out.println("All Options" + dataString);
                        } else {
                            String currentLocation = PrefUtils.getFromPrefs(ctx, PrefUtils.PREFS_CURRENT_LOCATION, "NONE");
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
                        CommonUtils.writeStringToFile(ctx, "data.js", dataString);
                        //mBluetoothService.stop();
                        Intent intent = new Intent(ctx, DisplayResultsActivity.class);
                        intent.putExtra(DatabaseHelper.C_PROJECT_ID, projectId);
                        intent.putExtra(DatabaseHelper.C_PROTOCOL_JSON, protocolJson);
                        intent.putExtra(Utils.APP_MODE, Utils.APP_MODE_STREAMLINE);
                        String reading = measurement.toString().replaceAll("\\r\\n", "").replaceFirst("\\{", "{" + options).replaceAll("\\{", "{\"time\":\"" + time + "\",");
                        //reading = reading.replaceFirst("\\{", "{"+options);
                        intent.putExtra(DatabaseHelper.C_READING, reading);
                        startActivity(intent);
                    }
                    mIsCancelMeasureBtnClicked = false;
                    if(measureButton != null) {
                        if (measureButton.getText().equals("CANCEL")) {
                            measureButton.setText("MEASURE");
                            measureButton.setBackgroundColor(Color.GRAY);
                        }
                    }
                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(ctx, "Connected to "
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();

                    break;
                case MESSAGE_TOAST:
                    if(mIsCancelMeasureBtnClicked == false){
                        Toast.makeText(ctx, msg.getData().getString(TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    mIsCancelMeasureBtnClicked = false;
                    if(measureButton != null) {
                        if (measureButton.getText().equals("CANCEL")) {
                            measureButton.setText("MEASURE");
                            measureButton.setBackgroundColor(Color.GRAY);
                        }
                    }
                    break;
                case MESSAGE_STOP:
                    Toast.makeText(ctx, msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();
                    if (mBluetoothService != null)
                        mBluetoothService.stop();
                    break;
            }
        }
    };

    private void sendData(String data) {
        // Check that we're actually connected before trying anything
        if (mBluetoothService == null) {
            mBluetoothService = new BluetoothService(ctx, mHandler);
        }
        if (mBluetoothService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(ctx,"Not Connected", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (data.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send;
            send = data.getBytes();
            mBluetoothService.write(send);
            //byte[] bytes = ByteBuffer.allocate(4).putInt(9).array();
        }
    }

    private void setMeasurementScreen()
    {
        View measurementView = getActivity().findViewById(9595);
        LinearLayout liLayout = (LinearLayout) measurementView.findViewById(R.id.linearlayoutoptions);
        ArrayList<Question> selectedQuestions = new ArrayList<Question>();
        if(null != allSelectedQuestions)
        {
            for (String questionObject : allSelectedQuestions) {
                Question question = new Gson().fromJson(questionObject, Question.class);
                selectedQuestions.add(question);
            }
        }

        //int optionLoop = 0;
        List<Question> allQuestions = db.getAllQuestionForProject(projectId);
        allOptions = new ArrayList<String>();
        for (int i = 0; i < allQuestions.size(); i++) {

            Activity activity = getActivity();
            View reviewItem = activity.getLayoutInflater().inflate(R.layout.protocol_list_item, null);
            liLayout.addView(reviewItem);

            TextView tvQuestion = (TextView) reviewItem.findViewById(R.id.protocol_name);
            TextView tvOption = (TextView) reviewItem.findViewById(R.id.protocol_desc);
            tvQuestion.setTextSize(20);
            tvOption.setTextSize(20);
            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            llp.setMargins(0, 15, 0, 0);
            tvOption.setLayoutParams(llp);
            reviewItem.setTag(allQuestions.get(i).getQuestionId());



            String data_value= new String("");
            //que = new TextView(this);
            //que.setTextSize(18);
            //que.setTag(i);

            //opt = new TextView(this);
            //LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            //llp.setMargins(0, 10, 0, 10);
            //opt.setTextSize(16);
            //opt.setLayoutParams(llp);
            String userId = PrefUtils.getFromPrefs(activity , PrefUtils.PREFS_LOGIN_USERNAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);
            data = db.getData(userId, projectId, allQuestions.get(i).getQuestionId());
            //if selected option type is User_Selected, Fixed_Value, Auto_Increment, Scan_Code
            if(null != data.getUser_id() && null != data.getProject_id() &&  null != data.getQuestion_id())
            {
                //Question and Option shown only if selected option type is 'Auto_Increment'
                if(data.getType().equals(Data.AUTO_INCREMENT))
                {
                    int index = Integer.parseInt(PrefUtils.getFromPrefs(activity, PrefUtils.PREFS_QUESTION_INDEX, "-1"));
                    int optionvalue = Integer.parseInt(CommonUtils.getAutoIncrementedValue(activity, allQuestions.get(i).getQuestionId(), "" + index));
                    //que.setText("Question -  " + allQuestions.get(i).getQuestionText());
                    tvQuestion.setText(allQuestions.get(i).getQuestionText());
                    //liLayout.addView(que);
                    if(optionvalue != -1) {
                      //  opt.setText("Option -  " + optionvalue);
                        tvOption.setText(optionvalue+"");
                        data_value = ""+optionvalue;
                    }


                }
                else if(data.getType().equals(Data.FIXED_VALUE))
                {
                    //que.setText("Question -  " + allQuestions.get(i).getQuestionText());
                    tvQuestion.setText(allQuestions.get(i).getQuestionText());
                    tvOption.setText(data.getValue());
                    //liLayout.addView(que);
                    //opt.setText("Option -  " + data.getValue());
                    data_value = data.getValue();
                }
                else  //Question and Option shown except 'Auto_Increment' option type.(for User_Selected, Scan_Code)
                {
                    //que.setText("Question -  " + selectedQuestions.get(optionLoop).getQuestionText());
                    //liLayout.addView(que);
                    tvQuestion.setText(allQuestions.get(i).getQuestionText());
                    tvOption.setText(allSelectedOptions.get(i));
                    //opt.setText("Option -  " + allSelectedOptions.get(optionLoop));
                    data_value = allSelectedOptions.get(i);
                    reviewItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            reviewFlag = true;
                            View child = viewFlipper.findViewWithTag(view.getTag());
                            viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(child));
                            openQuestionScreen();
                        }
                    });
//                    que.setOnClickListener(new OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            viewFlipper.setDisplayedChild(Integer.parseInt(view.getTag().toString()));
//                            refreshMeasrementScreen();
//                        }
//                    });
                }
                //liLayout.addView(opt);
            }
            else  //Streamline mode Question and Option is display.
            {
                tvQuestion.setText(allQuestions.get(i).getQuestionText());
                tvOption.setText(allSelectedOptions.get(i));
                //que.setText("Question -  " + allQuestions.get(i).getQuestionText());
                //liLayout.addView(que);
                //opt.setText("Option -  " + allSelectedOptions.get(i));
                data_value = allSelectedOptions.get(i);
                //liLayout.addView(opt);
                reviewItem.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        reviewFlag = true;
                        View child = viewFlipper.findViewWithTag(view.getTag());
                        viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(child));
                        openQuestionScreen();
                    }
                });
//                que.setOnClickListener(new OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        viewFlipper.setDisplayedChild(Integer.parseInt(view.getTag().toString()));
//                        refreshMeasrementScreen();
//                    }
//                });
            }
            //for(int j=0;j<allSelectedOptions.size();j++){
                allOptions.add(data_value);
           // }
//            try{
//                if(i==0)
//                {
//                    option1= data_value;
//                }else if(i == 1)
//                {
//                    option2 = data_value;
//                }else if(i==2)
//                {
//                    option3 = data_value;
//                }
//
////                    option1 = (String) getAllSelectedOptions.get(0);
////                    option2 = (String) getAllSelectedOptions.get(1);
////                    option3 = (String) getAllSelectedOptions.get(2);
//            }catch ( IndexOutOfBoundsException ex)
//            {
//                //eat the exceptions !!!! Basically ignore questions less or more than 3
//            }

            mStatusLine = (TextView) activity.findViewById(R.id.statusMessage);

            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            }
            if (mBluetoothService == null) {
                mBluetoothService = new BluetoothService(activity, mHandler);
            }

        }
    }


    //location related


    /*
* Called when the Activity is no longer visible at all.
* Stop updates and disconnect.
*/
    @Override
    public void onStop() {

        // If the client is connected
        if (mLocationClient.isConnected()) {
            stopPeriodicUpdates();
        }

        // After disconnect() is called, the client is considered "dead".
        mLocationClient.disconnect();

        super.onStop();
    }

    /*
     * Called when the Activity is restarted, even before it becomes visible.
     */
    @Override
    public void onStart() {

        super.onStart();

        /*
         * Connect the client. Don't re-start any requests here;
         * instead, wait for onResume()
         */
        mLocationClient.connect();

    }

    /**
     * Verify that Google Play services is available before making a request.
     *
     * @return true if Google Play services is available, otherwise false
     */
    private boolean servicesConnected() {

        // Check that Google Play services is available
        Activity activity = getActivity();
        int resultCode =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("PHOTOSYNQ-RESULTACTIVITY", getString(R.string.play_services_available));

            // Continue
            return true;
            // Google Play services was not available for some reason
        } else {
            // Display an error dialog
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, activity, 0);
            if (dialog != null) {
                dialog.show();
//                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
//                errorFragment.setDialog(dialog);
//                errorFragment.show(getSupportFragmentManager(), "PHOTOSYNQ-RESULTACTIVITY");
            }
            return false;
        }
    }

    public String getLocation() {

        // If Google Play Services is available
        if (servicesConnected()) {

            // Get the current location
            Location currentLocation = mLocationClient.getLastLocation();

            return LocationUtils.getLatLng(getActivity(), currentLocation);
        }
        return "";
    }

    public void startUpdates() {

        if (servicesConnected()) {
            startPeriodicUpdates();
        }
    }

    public void stopUpdates() {

        if (servicesConnected()) {
            stopPeriodicUpdates();
        }
    }

    /*
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    @Override
    public void onConnected(Bundle bundle) {
        startPeriodicUpdates();
    }

    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onDisconnected() {
        Log.d("PHOTOSYNQ-RESULTACTIVITY", "Disconnected");
    }

    /*
     * Called by Location Services if the attempt to
     * Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {

                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        getActivity(),
                        LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

                /*
                * Thrown if Google Play services canceled the original
                * PendingIntent
                */

            } catch (IntentSender.SendIntentException e) {

                // Log the error
                e.printStackTrace();
            }
        } else {

            // If no resolution is available, display a dialog to the user with the error.
            showErrorDialog(connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Activity activity = getActivity();
        Log.d("PHOTOSYNQ", "Location changed:"+LocationUtils.getLatLng(activity, location));
        PrefUtils.saveToPrefs(activity, PrefUtils.PREFS_CURRENT_LOCATION, LocationUtils.getLatLng(activity, location));
    }

    /**
     * In response to a request to start updates, send a request
     * to Location Services
     */
    private void startPeriodicUpdates() {

        mLocationClient.requestLocationUpdates(mLocationRequest, this);
    }

    /**
     * In response to a request to stop updates, send a request to
     * Location Services
     */
    private void stopPeriodicUpdates() {
        mLocationClient.removeLocationUpdates(this);
    }

    private void showErrorDialog(int errorCode) {

        // Get the error dialog from Google Play services
        Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
                errorCode,
                getActivity(),
                LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

        // If Google Play services can provide an error dialog
        if (errorDialog != null) {

            errorDialog.show();
//            // Create a new DialogFragment in which to show the error dialog
//            ErrorDialogFragment errorFragment = new ErrorDialogFragment();
//
//            // Set the dialog in the DialogFragment
//            errorFragment.setDialog(errorDialog);
//
//            // Show the error dialog in the DialogFragment
//            errorFragment.show(getSupportFragmentManager(), "PHOTOSYNQ-RESULTACTIVITY");
        }
    }

    /**
     * Define a DialogFragment to display the error dialog generated in
     * showErrorDialog.
     */
    public static class ErrorDialogFragment extends DialogFragment {

        // Global field to contain the error dialog
        private Dialog mDialog;

        /**
         * Default constructor. Sets the dialog field to null
         */
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        /**
         * Set the dialog to display
         *
         * @param dialog An error dialog
         */
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        /*
         * This method must return a Dialog to the DialogFragment.
         */
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth  services
        if (mBluetoothService != null) mBluetoothService.stop();
    }
}
