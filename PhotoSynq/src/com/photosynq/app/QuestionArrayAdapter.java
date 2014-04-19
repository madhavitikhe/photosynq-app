package com.photosynq.app;

import java.util.List;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.photosynq.app.model.Question;

public class QuestionArrayAdapter extends BaseAdapter implements ListAdapter  {

	private final Activity activity;
	private final List<Question> questionList;
	

	QuestionArrayAdapter(Activity activity, List<Question> questionList) {
		assert activity != null;
		assert questionList != null;

		this.questionList = questionList;
		this.activity = activity;
	}

	@Override
	public int getCount() {
		if (null == questionList)
		{
			return 0;
		}
		else
			
		{
			return questionList.size();
		}
	}

	@Override
	public Question getItem(int position) {
		if (null == questionList)
			return null;
		else
			return questionList.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null)
			convertView = activity.getLayoutInflater().inflate(R.layout.measurement_item_card, null);

		TextView tvQuestion = (TextView) convertView.findViewById(R.id.question);
		Spinner spinOptions = (Spinner) convertView.findViewById(R.id.optionSpinner);

		Question question = getItem(position);
		if (null != question) {
			try {
					tvQuestion.setText(question.getQuestionText());
				//List<String> list = new ArrayList<String>();
			        ArrayAdapter<String> adapter= new ArrayAdapter<String>(parent.getContext(), android.R.layout.simple_spinner_item,  question.getOptions());   
			        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			        spinOptions.setAdapter(adapter);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return convertView;
	}
}
