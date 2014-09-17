package com.photosynq.app.navigationDrawer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.photosynq.app.R;
import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.Data;
import com.photosynq.app.model.Question;
import com.photosynq.app.navigationDrawer.Utils.QuestionType;
import com.photosynq.app.utils.PrefUtils;

public class DataFirstFragment extends Fragment {
	
	public Button save_btn;
	public DatabaseHelper db;
	private String userId;
	private String questionId;
    private int questionValueType;
	private String projectId;
	private RadioGroup radioGroup;
	private RadioButton userSelectedRadio;
	private RadioButton fixedValueRadio;
	private RadioButton autoIncRadio;
	private RadioButton scanCodeRadio;
	public EditText fixed_value_edit_text;
	public EditText from_edit_text;
	public EditText to_edit_text;
	public EditText repeat_edit_text;
	
	public static DataFirstFragment newInstance() {
        Bundle bundle = new Bundle();

        DataFirstFragment fragment = new DataFirstFragment();
        fragment.setArguments(bundle);
        
        return fragment;
    }	
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.data_first_fragment, container, false);
		userId = PrefUtils.getFromPrefs(getActivity() , PrefUtils.PREFS_LOGIN_USERNAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);
		db = DatabaseHelper.getHelper(getActivity());
		projectId = db.getSettings(userId).getProjectId();
		Bundle extras = getArguments();
		if(null != extras)
		{
			questionId = extras.getString(DatabaseHelper.C_QUESTION_ID);
            questionValueType = extras.getInt(DatabaseHelper.C_QUESTION_TYPE);
		}
		radioGroup = (RadioGroup) rootView.findViewById(R.id.radioGroupQuestionType);
		userSelectedRadio = (RadioButton) rootView.findViewById(R.id.user_select_radiobtn);
		fixedValueRadio = (RadioButton) rootView.findViewById(R.id.fixedvalueradio);
		autoIncRadio = (RadioButton) rootView.findViewById(R.id.autoincrementradio);
		scanCodeRadio = (RadioButton) rootView.findViewById(R.id.scanCode);
		
		fixed_value_edit_text = (EditText) rootView.findViewById(R.id.fixed_value_editText);
		from_edit_text = (EditText) rootView.findViewById(R.id.from_editText);
		to_edit_text = (EditText) rootView.findViewById(R.id.to_editText);
		repeat_edit_text = (EditText) rootView.findViewById(R.id.repeat_editText);

        TextView questionTextView = (TextView) rootView.findViewById(R.id.question_txt);
        Question question = db.getQuestionForProject(projectId,questionId);
        questionTextView.setText(question.getQuestionText());
        if(questionValueType == 1 ) {
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
        }else if(questionValueType == 2 ) {
            Data retrieveData = db.getData(userId, projectId, questionId);
            if (null != retrieveData.getType()) {
                switch (QuestionType.valueOf(retrieveData.getType())) {
                    case AUTO_INCREMENT:
                        autoIncRadio.setChecked(true);
                        String[] values = retrieveData.getValue().split(",");
                        from_edit_text.setText(values[0]);
                        to_edit_text.setText(values[1]);
                        repeat_edit_text.setText(values[2]);
                        break;
                    case FIXED_VALUE:
                        fixedValueRadio.setChecked(true);
                        fixed_value_edit_text.setText(retrieveData.getValue());
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


            save_btn = (Button) rootView.findViewById(R.id.data_fragment_save_btn);
            save_btn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    Data data = new Data();
                    data.setUser_id(userId);
                    data.setProject_id(projectId);
                    data.setQuestion_id(questionId);
                    data.setValue(Data.NO_VALUE);

                    int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();

                    if (selectedRadioButtonId == userSelectedRadio.getId()) {
                        data.setType(QuestionType.USER_SELECTED.getStatusCode());
                        Toast.makeText(getActivity(), "Saved Successfully", Toast.LENGTH_LONG).show();
                    } else if (selectedRadioButtonId == fixedValueRadio.getId()) {
                        data.setType(QuestionType.FIXED_VALUE.getStatusCode());
                        data.setValue(fixed_value_edit_text.getText().toString());
                        Toast.makeText(getActivity(), "Saved Successfully", Toast.LENGTH_LONG).show();
                    } else if (selectedRadioButtonId == autoIncRadio.getId()) {
                        data.setType(QuestionType.AUTO_INCREMENT.getStatusCode());
                        if (from_edit_text.getText().toString().isEmpty() || to_edit_text.getText().toString().isEmpty() || repeat_edit_text.getText().toString().isEmpty()) {
                            if (from_edit_text.getText().toString().isEmpty()) {
                                from_edit_text.setError("Please insert field");
                            } else if (to_edit_text.getText().toString().isEmpty()) {
                                to_edit_text.setError("Please insert field");
                            } else if (repeat_edit_text.getText().toString().isEmpty()) {
                                repeat_edit_text.setError("Please insert field");
                            }
                            Toast.makeText(getActivity(), "Please insert all fields", Toast.LENGTH_LONG).show();
                        } else {
                            data.setValue(from_edit_text.getText().toString() + "," + to_edit_text.getText().toString() + "," + repeat_edit_text.getText().toString());
                            Toast.makeText(getActivity(), "Saved Successfully", Toast.LENGTH_LONG).show();
                        }
                    } else if (selectedRadioButtonId == scanCodeRadio.getId()) {
                        data.setType(QuestionType.SCAN_CODE.getStatusCode());
                        Toast.makeText(getActivity(), "Saved Successfully", Toast.LENGTH_LONG).show();
                    }

                    db.updateData(data);
                }
            });
        }

	rootView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT ));	
	return rootView;
 }
}
