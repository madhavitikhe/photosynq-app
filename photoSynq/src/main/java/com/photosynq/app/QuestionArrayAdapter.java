package com.photosynq.app;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.photosynq.app.model.Question;

import java.util.List;

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
	public View getView(final int position, View convertView, final ViewGroup parent) {
		if (convertView == null)
			convertView = activity.getLayoutInflater().inflate(R.layout.measurement_item_card, null);

		TextView tvQuestion = (TextView) convertView.findViewById(R.id.question);
		final Spinner spinOptions = (Spinner) convertView.findViewById(R.id.optionSpinner);

		final Question question = getItem(position);
		if (null != question) {
			try {
					tvQuestion.setText(question.getQuestionText());
				//List<String> list = new ArrayList<String>();
			        ArrayAdapter<String> adapter= new ArrayAdapter<String>(parent.getContext(), android.R.layout.simple_spinner_item,  question.getOptions());   
			        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			        spinOptions.setAdapter(adapter);
			        spinOptions.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> adapter,
								View view, int pos, long id) {
							// TODO Auto-generated method stub
							//System.out.println("$$$$$$$$$$$$$$$$ Pos="+pos+" id="+id + " data="+spinOptions.getSelectedItem().toString());
//							if(position==0)
//							{
//								((NewMeasurmentActivity)parent.getContext()).option1= spinOptions.getSelectedItem().toString();
//								((NewMeasurmentActivity)parent.getContext()).question1= question.getQuestionText();
//								//System.out.println("$$$$$$setting 1: "+spinOptions.getSelectedItem().toString());
//							}else if(position==1)
//							{
//								((NewMeasurmentActivity)parent.getContext()).option2= spinOptions.getSelectedItem().toString();
//								((NewMeasurmentActivity)parent.getContext()).question2= question.getQuestionText();
//								//System.out.println("$$$$$$setting 2: "+spinOptions.getSelectedItem().toString());
//							}else if(position==2)
//							{
//								((NewMeasurmentActivity)parent.getContext()).option3= spinOptions.getSelectedItem().toString();
//								((NewMeasurmentActivity)parent.getContext()).question3= question.getQuestionText();
//								//System.out.println("$$$$$$setting 3: "+spinOptions.getSelectedItem().toString());
//							}
						}

						@Override
						public void onNothingSelected(AdapterView<?> arg0) {
							// TODO Auto-generated method stub
							
						}
					});
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return convertView;
	}
}
