package com.photosynq.app;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.Data;
import com.photosynq.app.model.Question;
import com.photosynq.app.utils.CommonUtils;
import com.photosynq.app.utils.Constants;
import com.photosynq.app.utils.PrefUtils;

/**
 * Created by kalpesh on 17/02/15.
 */
public class DataFragment extends Fragment{

    private DatabaseHelper databaseHelper;
    private NonSwipeableViewPager viewPager;
    private String questionId;
    private String userId;
    private String projectId;

    private RadioGroup radioGroup;
    private RadioButton userSelectedRadio;
    private RadioButton fixedValueRadio;
    private RadioButton autoIncRadio;
    private RadioButton scanCodeRadio;

    private EditText fixed_value_edit_text;
    private EditText from_edit_text;
    private EditText to_edit_text;
    private EditText repeat_edit_text;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        /**
         * Receive sended value from FragmentData.
         */
        Bundle extras = getArguments();
        int questionValueType = 0;
        boolean prev = false;
        boolean next = false;
        if(null != extras)
        {
            projectId = extras.getString(DatabaseHelper.C_PROJECT_ID);
            questionId = extras.getString(DatabaseHelper.C_QUESTION_ID);
            questionValueType = extras.getInt(DatabaseHelper.C_QUESTION_TYPE);
            prev = extras.getBoolean(Data.PREV);
            next = extras.getBoolean(Data.NEXT);
        }

        Typeface typefaceRobotoRegular = CommonUtils.getInstance(getActivity()).getFontRobotoRegular();
        Typeface typefaceRobotoMedium = CommonUtils.getInstance(getActivity()).getFontRobotoMedium();

        databaseHelper = DatabaseHelper.getHelper(getActivity());

        View rootView = inflater.inflate(R.layout.fragment_data, container, false);
        userId = PrefUtils.getFromPrefs(getActivity(), PrefUtils.PREFS_LOGIN_USERNAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);

        Button saveButton = (Button)rootView.findViewById(R.id.save_btn);
        saveButton.setTypeface(typefaceRobotoMedium);
        Button prevButton = (Button)rootView.findViewById(R.id.prev_btn);
        prevButton.setTypeface(typefaceRobotoMedium);
        if(prev)
        {
            prevButton.setVisibility(View.VISIBLE);
            prevButton.setText("<  Prev");
            prevButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(saveData(false)) {
                        viewPager = (NonSwipeableViewPager) getActivity().findViewById(R.id.viewPager);
                        viewPager.setCurrentItem(getItem(-1), true);
                    }
                }
            });
        }
        else
        {
            prevButton.setVisibility(View.GONE);
        }
        if(!next)
        {
            saveButton.setVisibility(View.GONE);
        }
        else
        {
            saveButton.setText("Next  >");
        }

        radioGroup = (RadioGroup) rootView.findViewById(R.id.radioGroupQuestionType);
        userSelectedRadio = (RadioButton) rootView.findViewById(R.id.user_select_radiobtn);
        userSelectedRadio.setTypeface(typefaceRobotoRegular);
        fixedValueRadio = (RadioButton) rootView.findViewById(R.id.fixedvalueradio);
        fixedValueRadio.setTypeface(typefaceRobotoRegular);
        autoIncRadio = (RadioButton) rootView.findViewById(R.id.autoincrementradio);
        autoIncRadio.setTypeface(typefaceRobotoRegular);
        scanCodeRadio = (RadioButton) rootView.findViewById(R.id.scanCode);
        scanCodeRadio.setTypeface(typefaceRobotoRegular);

        fixed_value_edit_text = (EditText) rootView.findViewById(R.id.fixed_value_editText);
        fixed_value_edit_text.setTypeface(typefaceRobotoRegular);
        from_edit_text = (EditText) rootView.findViewById(R.id.from_editText);
        from_edit_text.setTypeface(typefaceRobotoRegular);
        to_edit_text = (EditText) rootView.findViewById(R.id.to_editText);
        to_edit_text.setTypeface(typefaceRobotoRegular);
        repeat_edit_text = (EditText) rootView.findViewById(R.id.repeat_editText);
        repeat_edit_text.setTypeface(typefaceRobotoRegular);

        //??
//        fixed_value_edit_text.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            public void onFocusChange(View v, boolean hasFocus) {
//                if(!hasFocus){
//                    saveData(true);
//                }
//            }
//        });

//        from_edit_text.setOnFocusChangeListener(this);
//        to_edit_text.setOnFocusChangeListener(this);
//        repeat_edit_text.setOnFocusChangeListener(this);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(saveData(false)) {
                    viewPager = (NonSwipeableViewPager) getActivity().findViewById(R.id.viewPager);
                    viewPager.setCurrentItem(getItem(+1), true);
                }
            }
        });

        fixed_value_edit_text.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                fixedValueRadio.setChecked(true);
                return false;
            }
        });
        from_edit_text.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                autoIncRadio.setChecked(true);
                return false;
            }
        });
        to_edit_text.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                autoIncRadio.setChecked(true);
                return false;
            }
        });
        repeat_edit_text.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                autoIncRadio.setChecked(true);
                return false;
            }
        });


        TextView questionTextView = (TextView) rootView.findViewById(R.id.tv_question_text);
        questionTextView.setTypeface(typefaceRobotoMedium);
        Question question = databaseHelper.getQuestionForProject(projectId, questionId);
        questionTextView.setText(question.getQuestionText());
        questionTextView.setTextColor(Color.WHITE);

        /**
         * If QuestionValueType is 1 then question type is project selected,
         * else QuestionValueType is 2 then question type is user selected.
         */
        if(questionValueType == Question.PROJECT_DEFINED ) {
            TextView lbl = (TextView)rootView.findViewById(R.id.select_from_web_lbl);
            lbl.setTypeface(typefaceRobotoMedium);
            lbl.setVisibility(View.VISIBLE);
            RadioGroup radioGroupQuestionType = (RadioGroup)rootView.findViewById(R.id.radioGroupQuestionType);
            for (int i = 0; i < radioGroupQuestionType.getChildCount(); i++) {
                View child = radioGroupQuestionType.getChildAt(i);
                child.setEnabled(false);
            }
            RelativeLayout subRelativeRadio = (RelativeLayout)rootView.findViewById(R.id.subRelativeRadio);
            for (int i = 0; i < subRelativeRadio.getChildCount(); i++) {
                View child = subRelativeRadio.getChildAt(i);
                child.setEnabled(false);
            }
            RelativeLayout subRelativeRadio1 = (RelativeLayout)rootView.findViewById(R.id.subRelativeRadio1);
            for (int i = 0; i < subRelativeRadio1.getChildCount(); i++) {
                View child = subRelativeRadio1.getChildAt(i);
                child.setEnabled(false);
            }
            RelativeLayout subRelativeRadio2 = (RelativeLayout)rootView.findViewById(R.id.subRelativeRadio2);
            for (int i = 0; i < subRelativeRadio2.getChildCount(); i++) {
                View child = subRelativeRadio2.getChildAt(i);
                child.setEnabled(false);
            }
        }else if(questionValueType == Question.USER_DEFINED ) {
            Data retrieveData = databaseHelper.getData(userId, projectId, questionId);
            if (null == retrieveData.getValue() || retrieveData.getValue().isEmpty()) {
                retrieveData.setUser_id(userId);
                retrieveData.setProject_id(projectId);
                retrieveData.setQuestion_id(question.getQuestionId());
                retrieveData.setValue(Data.NO_VALUE);
                retrieveData.setType(Constants.QuestionType.USER_SELECTED.getStatusCode());
                databaseHelper.updateData(retrieveData);
            }
            if (null != retrieveData.getType()) {
                switch (Constants.QuestionType.valueOf(retrieveData.getType())) {
                    case AUTO_INCREMENT:
                        String[] values = retrieveData.getValue().split(",");
                        if(values[0].equals(Data.NO_VALUE))
                        {
                            break;
                        }
                        from_edit_text.setText(values[0]);
                        to_edit_text.setText(values[1]);
                        repeat_edit_text.setText(values[2]);
                        autoIncRadio.setChecked(true);
                        break;
                    case FIXED_VALUE:
                        if(retrieveData.equals(Data.NO_VALUE))
                        {
                            break;
                        }
                        fixed_value_edit_text.setText(retrieveData.getValue());
                        fixedValueRadio.setChecked(true);
                        break;
                    case SCAN_CODE:
                        scanCodeRadio.setChecked(true);
                        break;
                    case USER_SELECTED:
                        userSelectedRadio.setChecked(true);
                        break;

                    default:
                        break;
                }
            }

        }
        rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT ));
        return rootView;
    }

    //Returns current position of viewpager.
    private int getItem(int i) {
        int a = viewPager.getCurrentItem();
        i = i + a;
        return i;
    }

    //Save all user selected values into database like(user selected, fixed value, auto inc, scan code)
    public boolean saveData(boolean showMessage) {
        boolean retVal = true;

        if(radioGroup == null)
            return true;

        Data data = new Data();
        data.setUser_id(userId);
        data.setProject_id(projectId);
        data.setQuestion_id(questionId);
        data.setValue(Data.NO_VALUE);
        data.setType(Constants.QuestionType.PROJECT_SELECTED.getStatusCode());

        int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();

        if (selectedRadioButtonId == userSelectedRadio.getId()) {

            data.setType(Constants.QuestionType.USER_SELECTED.getStatusCode());

        } else if (selectedRadioButtonId == fixedValueRadio.getId()) {

            if (fixed_value_edit_text.getText().toString().isEmpty()) {
                fixed_value_edit_text.setError("Please enter value");
                retVal = false;
            }else {
                data.setType(Constants.QuestionType.FIXED_VALUE.getStatusCode());
                data.setValue(fixed_value_edit_text.getText().toString());
            }

        } else if (selectedRadioButtonId == autoIncRadio.getId()) {

            if (from_edit_text.getText().toString().isEmpty()) {
                from_edit_text.setError("Please enter value");
                retVal = false;
            } else if (to_edit_text.getText().toString().isEmpty()) {
                to_edit_text.setError("Please enter value");
                retVal = false;
            } else if (repeat_edit_text.getText().toString().isEmpty()) {
                repeat_edit_text.setError("Please enter value");
                retVal = false;
            }else
            {
                data.setType(Constants.QuestionType.AUTO_INCREMENT.getStatusCode());
                data.setValue(from_edit_text.getText().toString() + "," + to_edit_text.getText().toString() + "," + repeat_edit_text.getText().toString());
            }

        } else if (selectedRadioButtonId == scanCodeRadio.getId()) {

            data.setType(Constants.QuestionType.SCAN_CODE.getStatusCode());
        }

        databaseHelper.updateData(data);

        return retVal;
    }//end save data function..

    @Override
    public void onPause() {
        saveData(true);
        super.onPause();
    }

    @Override
    public void onResume() {
        //saveData(false);
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        saveData(true);
    }
}
