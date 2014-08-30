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
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.photosynq.app.R;
import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.Data;
import com.photosynq.app.model.Question;
import com.photosynq.app.utils.PrefUtils;

public class DataFirstFragment extends Fragment {
	
	public Button save_btn;
	public DatabaseHelper db;
	private String userId;
	private String questionId;
	private String projectId;
	private RadioGroup radiogroup;
	private RadioButton seletedRadioBtn;
	private String selectedValue;
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
		}
		radiogroup = (RadioGroup) rootView.findViewById(R.id.radioGroup1);
		int selectedId = radiogroup.getCheckedRadioButtonId();
		seletedRadioBtn = (RadioButton) rootView.findViewById(selectedId);
		
		fixed_value_edit_text = (EditText) rootView.findViewById(R.id.fixed_value_editText);
		from_edit_text = (EditText) rootView.findViewById(R.id.from_editText);
		to_edit_text = (EditText) rootView.findViewById(R.id.to_editText);
		repeat_edit_text = (EditText) rootView.findViewById(R.id.repeat_editText);
		
		 radiogroup.setOnCheckedChangeListener(new OnCheckedChangeListener() 
	        {
	            public void onCheckedChanged(RadioGroup group, int checkedId) {
	                switch(checkedId){
	                    case R.id.user_select_radiobtn:
	                    	Toast.makeText(getActivity(), "User Selected", Toast.LENGTH_SHORT).show();
	                    break;

	                    case R.id.fixedvalueradio:
	                    	Toast.makeText(getActivity(), "Fixed Value", Toast.LENGTH_SHORT).show();
	                    	selectedValue = fixed_value_edit_text.getText().toString();
	                    break;

	                    case R.id.autoincrementradio:
	                    	Toast.makeText(getActivity(), "Auto Increment", Toast.LENGTH_SHORT).show();
	                    	selectedValue = from_edit_text.getText().toString();
	                    	selectedValue = to_edit_text.getText().toString();
	                    	selectedValue = repeat_edit_text.getText().toString();
	                    break;
	                    
	                    case R.id.scanCode:
	                    	Toast.makeText(getActivity(), "Scan Code", Toast.LENGTH_SHORT).show();
	                    break;

	                }


	            }
	        });

		 
		 
		
		
		TextView questionText = (TextView) rootView.findViewById(R.id.question_txt);
		Question question = db.getQuestionForProject(projectId,questionId);
		questionText.setText(question.getQuestionText());
		System.out.println("--------------question Text-----"+question.getQuestionText());
		save_btn = (Button) rootView.findViewById(R.id.data_fragment_save_btn);

		save_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Data data = new Data();
				data.setUser_id(userId);
				data.setProject_id(projectId);
				data.setQuestion_id(questionId);
				data.setType(Data.USER_SELECTED);
				data.setValue(selectedValue);
	    		db.updateData(data);
	    		Toast.makeText(getActivity(), "---Data Saved---"+selectedValue,Toast.LENGTH_SHORT).show();	
			}
		});
		
	rootView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT ));	
	return rootView;
 }
}
