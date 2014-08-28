package com.photosynq.app;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.photosynq.app.navigationDrawer.NavigationDrawer;

public class DisplaySelectedQuestionsOptionsActivity extends NavigationDrawer {

	ArrayList<CharSequence> getAllSelectedOptions = new ArrayList<CharSequence>();
	ArrayList<CharSequence> getAllSelectedQuestions = new ArrayList<CharSequence>();
	TextView questionText,optionText;
	int size;
	String question_text;
	LinearLayout liLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_display_selected_questions_options);
		LayoutInflater inflater = (LayoutInflater) this
	            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View contentView = inflater.inflate(R.layout.activity_display_selected_questions_options, null, false);
	    layoutDrawer.addView(contentView, 0); 
		
		liLayout = (LinearLayout) findViewById(R.id.linearlayoutoptions);
		//questionText = (TextView) findViewById(R.id.questionText);
		//optionText = (TextView) findViewById(R.id.optionText);
		
		Bundle extras = getIntent().getExtras();
		getAllSelectedOptions = extras.getCharSequenceArrayList("All_Options");
		getAllSelectedQuestions = extras.getCharSequenceArrayList("All_Questions");
		
		//size = extras.getInt("Question_Size");
		//question_text = extras.getString("Question_Text");
		

		//optionText.setText(""+getAllSelectedOptions.get(0));
		System.out.println("--getsize-------"+ getAllSelectedOptions.size());

		for (int i = 0; i < getAllSelectedQuestions.size(); i++) {
			System.out.println("--getQuestion-------"+ getAllSelectedQuestions.get(i));
			System.out.println("--getOption-------"+ getAllSelectedOptions.get(i));
		    final TextView que = new TextView(this);
		    que.setText("Question -  " + getAllSelectedQuestions.get(i));
		    liLayout.addView(que);
		    final TextView opt = new TextView(this);
		    opt.setText("Option -  " + getAllSelectedOptions.get(i));
		    liLayout.addView(opt);
		}
		
		/*
		 * <LL>
		 * 	<SV>
		 * 		<LL>
		 * 			<RL>
		 * 				<TextView>.....
		 * 			</RL>
		 * 			<RL>
		 * 				<TextView>.....
		 * 			</RL>
		 * 		</LL>
		 * 	</SV>
		 * 	<RL>
		 * 		<Measure_Btn>
		 * 	</RL>
		 * </LL>
		 */
		
//		LinearLayout mainLL = new LinearLayout(ctx);
//		mainLL.setOrientation(LinearLayout.VERTICAL);
//	    mainLL.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
//
//		ScrollView scrollView = new ScrollView(ctx);
//		scrollView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
//		
//		LinearLayout subLLayout = new LinearLayout(ctx);
//		subLLayout.setOrientation(LinearLayout.VERTICAL);
//	    subLLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
//	    	
//		RelativeLayout relativeLayout = new RelativeLayout(ctx);
//		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
//		relativeLayout.setLayoutParams(params);
//		 
//		TextView questionText = new TextView(ctx);
//		questionText.setId(1);
//		questionText.setText("Hello Venturit");
//		questionText.setTextColor(Color.BLACK);
//		questionText.setTextSize(20); 
//		
//		relativeLayout.addView(questionText);
//		subLLayout.addView(relativeLayout);
//		scrollView.addView(subLLayout);
//		mainLL.addView(scrollView);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_selected_questions_options,
				menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
