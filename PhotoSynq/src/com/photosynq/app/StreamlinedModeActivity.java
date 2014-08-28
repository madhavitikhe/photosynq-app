package com.photosynq.app;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.Question;
import com.photosynq.app.navigationDrawer.NavigationDrawer;
import com.squareup.picasso.Picasso;

public class StreamlinedModeActivity extends NavigationDrawer {

	ViewFlipper viewFlipper;
	private DatabaseHelper db;
	private String projectId;
	private Context ctx;
	ArrayList<String> allSelectedOptions;
	ArrayList<String> allSelectedQuestions ;
	//String[n] array;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_streamlined_mode);
		LayoutInflater inflater = (LayoutInflater) this
	            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View contentView = inflater.inflate(R.layout.activity_streamlined_mode, null, false);
	    layoutDrawer.addView(contentView, 0); 
	    allSelectedOptions= new ArrayList<String>();
		allSelectedQuestions = new ArrayList<String>();
		ctx = getApplicationContext();
		// it returns questions and options of projectId-7 only, and show
		// question and option on viewflipper.
		//db = new DatabaseHelper(ctx);
		db = DatabaseHelper.getHelper(ctx);
		projectId = "7";
		final List<Question> questions = db.getAllQuestionForProject(projectId);
		//db.closeDB();
		viewFlipper = (ViewFlipper) findViewById(R.id.ViewFlipper01);
		for (final Question question : questions) {
			System.out.println("question size"+ question.getOptions());
			System.out.println("questions"+question.getQuestionText());
			/*
			 * Creating following layout programmatically to display each 
			 * question as a single view in ViewFlipper
			 * 
			 * LinearLayout ->  (mainLinearLayout)		
			 * 		ScrollView -> 
			 * 			LinearLayout -> (subLinearLayout)
			 * 				TextView(for question)
			 * 				</TV>
			 * 				LinearLayout -> (detailsLinearLayout)
			 * 					RelativeLayout -> 
			 * 						TextView , ImageView(for option)
			 * 					</RL>
			 * 					RelativeLayout -> 
			 * 						TextView , ImageView(for option)....
			 * 					</RL>
			 * 				</LL>
			 * 			</LL>
			 * 		</SV>
			 * <//LL>					
			 */
			LinearLayout mainLinearLayout = new LinearLayout(ctx);
			mainLinearLayout.setOrientation(LinearLayout.VERTICAL);
		    mainLinearLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));

			ScrollView scrollView = new ScrollView(ctx);
			scrollView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
			
			LinearLayout subLinearLayout = new LinearLayout(ctx);
			subLinearLayout.setOrientation(LinearLayout.VERTICAL);
		    subLinearLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
		    
		    
			TextView questionTextView = new TextView(ctx);
			questionTextView.setTextColor(Color.WHITE);
			questionTextView.setTextSize(25); 
			questionTextView.setBackgroundColor(Color.GRAY);
			questionTextView.setText(question.getQuestionText());
			questionTextView.setLayoutParams( new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
			
			subLinearLayout.addView(questionTextView);
			
			
		    
			for (int i=1; i<= question.getOptions().size(); i++) 
			{
				if(i%2==0)
				{
					
				}
				else
				{
					LinearLayout detailsLinearLayout = new LinearLayout(ctx);
					detailsLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
					detailsLinearLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
				
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
					imageView.setId(i-1);
					RelativeLayout.LayoutParams imageVParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
					imageVParams.addRule(RelativeLayout.BELOW, optionTextView.getId());
					imageView.setOnClickListener(new View.OnClickListener() {
				        @Override
				        public void onClick(View v) {
				        	//Toast.makeText(ctx, "You clicked on option", 5).show();
				        	//System.out.println(question.getOptions().get(v.getId()));
				        	int displayedChild = viewFlipper.getDisplayedChild();
				            int childCount = viewFlipper.getChildCount();
				        	allSelectedQuestions.add(question.getQuestionText());
				        	allSelectedOptions.add(question.getOptions().get(v.getId()));
				        	for(int i=0;i<allSelectedOptions.size();i++)
				        	{
				        		System.out.println(allSelectedOptions.get(i));
				        	}
				            if (displayedChild == childCount - 1) {
				                viewFlipper.stopFlipping();
				                Intent intent = new Intent(ctx,DisplaySelectedQuestionsOptionsActivity.class);
				                intent.putExtra("All_Questions", allSelectedQuestions);
				                intent.putExtra("All_Options", allSelectedOptions);
				                startActivity(intent);
				            }
				            
				            
					        	viewFlipper.showNext();
				        }
				    });
					
	
					Picasso.with(getApplicationContext())
					.load("http://static.dezeen.com/uploads/2013/09/dezeen_Google-logo_1sq-300x300.jpg")
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
						RelativeLayout.LayoutParams imageVParamseven = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
						imageVParamseven.addRule(RelativeLayout.BELOW, optionTextVieweven.getId());
						
						imageVieweven.setOnClickListener(new View.OnClickListener() {
					        @Override
					        public void onClick(View v) {
					        	
					        	//Toast.makeText(ctx, "You clicked on option", 5).show();
					        	//System.out.println(""+ question.getOptions().get(v.getId()));
					        	int displayedChild = viewFlipper.getDisplayedChild();
					            int childCount = viewFlipper.getChildCount();
					            allSelectedQuestions.add(question.getQuestionText());
					        	allSelectedOptions.add(question.getOptions().get(v.getId()));
					        	for(int i=0;i<allSelectedOptions.size();i++)
					        	{
					        		System.out.println(allSelectedOptions.get(i));
					        	}
					        	if (displayedChild == childCount - 1) {
					                viewFlipper.stopFlipping();
					                Intent intent = new Intent(ctx,DisplaySelectedQuestionsOptionsActivity.class);
					                intent.putExtra("All_Questions", allSelectedQuestions);
					                intent.putExtra("All_Options", allSelectedOptions);
					               // intent.putExtra("Question_Size", questions.size());
					              //  intent.putExtra("Question_Text", question.getQuestionText());
					                startActivity(intent);
					            }
					        	
					        		
						        	viewFlipper.showNext();
					        }
					    });
						
		
						Picasso.with(getApplicationContext())
						.load("http://static.dezeen.com/uploads/2013/09/dezeen_Google-logo_1sq-300x300.jpg")
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
			viewFlipper.addView(mainLinearLayout);
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		allSelectedOptions= new ArrayList<String>();
		 allSelectedQuestions = new ArrayList<String>();
	}
}
