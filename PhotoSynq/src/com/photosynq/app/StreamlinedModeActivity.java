package com.photosynq.app;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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

import com.google.gson.Gson;
import com.google.zxing.client.android.CaptureActivity;
import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.Data;
import com.photosynq.app.model.Question;
import com.photosynq.app.navigationDrawer.NavigationDrawer;
import com.photosynq.app.navigationDrawer.Utils;
import com.photosynq.app.utils.BluetoothService;
import com.photosynq.app.utils.PrefUtils;
import com.squareup.picasso.Picasso;

public class StreamlinedModeActivity extends NavigationDrawer {

	ViewFlipper viewFlipper;
	private DatabaseHelper db;
	private String projectId;
	private String deviceAddress;
	private Context ctx;
	ArrayList<String> allSelectedOptions;
	ArrayList<String> allSelectedQuestions ;
	private Handler  handler = new Handler();
	private TextView txtScanResult;
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
		
		//Show question and option on viewflipper.
		//db = new DatabaseHelper(ctx);
		String userId = PrefUtils.getFromPrefs(ctx , PrefUtils.PREFS_LOGIN_USERNAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);
		db = DatabaseHelper.getHelper(ctx);
		projectId = db.getSettings(userId).getProjectId();
		deviceAddress = db.getSettings(userId).getConnectionId();
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
			
			Data data = db.getData(userId, projectId, question.getQuestionId());
			if(null != data.getProject_id() || null != data.getQuestion_id() || null != data.getType() || null != data.getValue())
			{
				if(data.getType().equals(Utils.USER_SELECTED))
				{
					RelativeLayout optionsRelativeLayout = new RelativeLayout(ctx);
					RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
					optionsRelativeLayout.setLayoutParams(params);
					
					LayoutInflater infltr = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					View view = infltr.inflate(R.layout.user_selected, null);
	
					final EditText userEnteredAnswer = (EditText) view.findViewById(R.id.userAnswer);
					Button showNext = (Button) view.findViewById(R.id.next);
					showNext.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							int displayedChild = viewFlipper.getDisplayedChild();
				            int childCount = viewFlipper.getChildCount();
				            allSelectedQuestions.add(new Gson().toJson(question));
				            allSelectedOptions.add(userEnteredAnswer.getText().toString());
				            if (displayedChild == childCount - 1) {
				                viewFlipper.stopFlipping();
				                Intent intent = new Intent(ctx,NewMeasurmentActivity.class);
				                intent.putExtra("All_Questions", allSelectedQuestions);
				                intent.putExtra("All_Options", allSelectedOptions);
				                intent.putExtra(Utils.APP_MODE, Utils.APP_MODE_STREAMLINE);
				                intent.putExtra(BluetoothService.DEVICE_ADDRESS, deviceAddress);
				                intent.putExtra(DatabaseHelper.C_PROJECT_ID, projectId);
				                startActivity(intent);
				            }
				            else
				            {
				            	viewFlipper.showNext();
				            }
						}
					});
	
					optionsRelativeLayout.addView(view);
	
					subLinearLayout.addView(optionsRelativeLayout);
					scrollView.addView(subLinearLayout);
					mainLinearLayout.addView(scrollView);
					viewFlipper.addView(mainLinearLayout);
					Toast.makeText(ctx, "User Selected", Toast.LENGTH_LONG).show();
				}
				else if(data.getType().equals(Utils.FIXED_VALUE))
				{
					String val = data.getValue();
					Toast.makeText(ctx, "Fixed Value  "+val, Toast.LENGTH_LONG).show();
				}
				else if(data.getType().equals(Utils.AUTO_INCREMENT))
				{
					if(questions.size()<=1)
					{
						viewFlipper.addView(mainLinearLayout);
						Toast.makeText(ctx, "Auto Increment is selected and Question size is <= 1", Toast.LENGTH_LONG).show();
					}
					
					PrefUtils.saveToPrefs(ctx, PrefUtils.PREFS_QUESTION_INDEX+question.getQuestionId(), "0");
					Toast.makeText(ctx, "Auto Increment ", Toast.LENGTH_LONG).show();
				}
				else if(data.getType().equals(Utils.SCAN_CODE))
				{
					RelativeLayout optionsRelativeLayout = new RelativeLayout(ctx);
					RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
					optionsRelativeLayout.setLayoutParams(params);
					
					LayoutInflater infltr = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					View view = infltr.inflate(R.layout.barcode_reader, null);
	
					 txtScanResult = (TextView) view.findViewById(R.id.scan_result);
				     View btnScan = view.findViewById(R.id.scan_button);
	
				        btnScan.setOnClickListener(new OnClickListener() {
				            public void onClick(View v) {
				            	Intent intent = new Intent(StreamlinedModeActivity.this,CaptureActivity.class);
				            	intent.setAction("com.google.zxing.client.android.SCAN");
				            	// this stops saving ur barcode in barcode scanner app's history
				            	intent.putExtra("SAVE_HISTORY", false);
				            	startActivityForResult(intent, 0);
				            }
				        });
				        
				      View btnScanDone = view.findViewById(R.id.done_scan_btn);
				      btnScanDone.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							int displayedChild = viewFlipper.getDisplayedChild();
				            int childCount = viewFlipper.getChildCount();
				            allSelectedQuestions.add(new Gson().toJson(question));
				            allSelectedOptions.add(txtScanResult.getText().toString());
				            if (displayedChild == childCount - 1) {
				                viewFlipper.stopFlipping();
				                Intent intent = new Intent(ctx,NewMeasurmentActivity.class);
				                intent.putExtra("All_Questions", allSelectedQuestions);
				                intent.putExtra("All_Options", allSelectedOptions);
				                intent.putExtra(Utils.APP_MODE, Utils.APP_MODE_STREAMLINE);
				                intent.putExtra(BluetoothService.DEVICE_ADDRESS, deviceAddress);
				                intent.putExtra(DatabaseHelper.C_PROJECT_ID, projectId);
				                startActivity(intent);
				            }
				            else
				            {
				            	viewFlipper.showNext();
				            }
						}
					});
				        
					optionsRelativeLayout.addView(view);
	
					subLinearLayout.addView(optionsRelativeLayout);
					scrollView.addView(subLinearLayout);
					mainLinearLayout.addView(scrollView);
					viewFlipper.addView(mainLinearLayout);
					Toast.makeText(ctx, "Scan Code", Toast.LENGTH_LONG).show();
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
				        	int displayedChild = viewFlipper.getDisplayedChild();
				            int childCount = viewFlipper.getChildCount();
				        	allSelectedQuestions.add(new Gson().toJson(question));
				        	allSelectedOptions.add(question.getOptions().get(v.getId()));
				        	for(int i=0;i<allSelectedOptions.size();i++)
				        	{
				        		System.out.println(allSelectedOptions.get(i));
				        	}
				            if (displayedChild == childCount - 1) {
				                viewFlipper.stopFlipping();
				                Intent intent = new Intent(ctx,NewMeasurmentActivity.class);
				                intent.putExtra("All_Questions", allSelectedQuestions);
				                intent.putExtra("All_Options", allSelectedOptions);
				                intent.putExtra(Utils.APP_MODE, Utils.APP_MODE_STREAMLINE);
				                intent.putExtra(BluetoothService.DEVICE_ADDRESS, deviceAddress);
				                intent.putExtra(DatabaseHelper.C_PROJECT_ID, projectId);
				                startActivity(intent);
				            }
				            else{
					        		viewFlipper.showNext();
				            }
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
					        	int displayedChild = viewFlipper.getDisplayedChild();
					            int childCount = viewFlipper.getChildCount();
					            allSelectedQuestions.add(new Gson().toJson(question));
					        	allSelectedOptions.add(question.getOptions().get(v.getId()));
					        	for(int i=0;i<allSelectedOptions.size();i++)
					        	{
					        		System.out.println(allSelectedOptions.get(i));
					        	}
					        	if (displayedChild == childCount - 1) {
					                viewFlipper.stopFlipping();
					                Intent intent = new Intent(ctx,NewMeasurmentActivity.class);
					                intent.putExtra("All_Questions", allSelectedQuestions);
					                intent.putExtra("All_Options", allSelectedOptions);
					                intent.putExtra(Utils.APP_MODE, Utils.APP_MODE_STREAMLINE);
					                intent.putExtra(BluetoothService.DEVICE_ADDRESS, deviceAddress);
					                intent.putExtra(DatabaseHelper.C_PROJECT_ID, projectId);

					               // intent.putExtra("Question_Size", questions.size());
					              //  intent.putExtra("Question_Text", question.getQuestionText());
					                startActivity(intent);
					            }
					        	else{
					        		viewFlipper.showNext();
					        	}
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
	}


	 @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	        super.onActivityResult(requestCode, resultCode, data);
	        if (requestCode == 0) {
	        					if (resultCode == RESULT_OK) {
	        						String contents = data.getStringExtra("SCAN_RESULT");
	        						txtScanResult.setText(contents);
	        						Toast.makeText(getApplicationContext(), contents, Toast.LENGTH_SHORT).show();
	        					} else if (resultCode == RESULT_CANCELED) {
	        						// Handle cancel
	        						Toast.makeText(getApplicationContext(), "Cancelled", Toast.LENGTH_SHORT).show();
	        					}
	        				}
	 }
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		
		viewFlipper.setDisplayedChild(0);
		viewFlipper.refreshDrawableState();
		super.onResume();
//		allSelectedOptions= new ArrayList<String>();
//		 allSelectedQuestions = new ArrayList<String>();
	}
}
