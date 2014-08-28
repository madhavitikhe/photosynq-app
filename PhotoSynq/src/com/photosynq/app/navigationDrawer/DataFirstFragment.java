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
import android.widget.TextView;
import android.widget.Toast;

import com.photosynq.app.R;
import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.Data;
import com.photosynq.app.model.Question;
import com.photosynq.app.utils.PrefUtils;

public class DataFirstFragment extends Fragment {
	
	public EditText fixed_value;
	public Button save_btn;
	public DatabaseHelper db;
	private String userId;
	private String questionID;
	private String projectID;
	
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
		projectID = db.getSettings(userId).getProjectID();
		Bundle extras = getArguments();
		if(null != extras)
		{
			questionID = extras.getString(DatabaseHelper.C_QUESTION_ID);
		}
		fixed_value = (EditText) rootView.findViewById(R.id.fixed_value_editText);
		TextView questionText = (TextView) rootView.findViewById(R.id.question_txt);
		Question question = db.getQuestionForProject(projectID,questionID);
		questionText.setText(question.getQuestionText());
		System.out.println("--------------question Text-----"+question.getQuestionText());
		save_btn = (Button) rootView.findViewById(R.id.data_fragment_save_btn);

		save_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Data data = new Data();
				String s = fixed_value.getText().toString();
				data.setUser_id(userId);
				data.setProject_id(projectID);
				data.setQuestion_id(questionID);
				data.setType(Data.USER_SELECTED);
				data.setValue(s);
	    		db.updateData(data);
	    		Toast.makeText(getActivity(), "---fixed value---"+s,Toast.LENGTH_SHORT).show();	
			}
		});
		
	rootView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT ));	
	return rootView;
 }
}
