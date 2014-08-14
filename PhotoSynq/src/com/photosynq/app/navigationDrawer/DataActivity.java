package com.photosynq.app.navigationDrawer;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.photosynq.app.R;

public class DataActivity extends Activity {
	
	protected void onCreate(Bundle savedInstanceState) {
		
		ViewPager viewPager;
		 MyPagerAdapter myPagerAdapter;
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.data_activity);
		viewPager = (ViewPager)findViewById(R.id.myviewpager);
		  myPagerAdapter = new MyPagerAdapter();
		  viewPager.setAdapter(myPagerAdapter);
}
	
	 private class MyPagerAdapter extends PagerAdapter{
		  
		  int NumberOfPages = 3;
		  
		  int[] res = { 
		   android.R.drawable.ic_dialog_alert,
		   android.R.drawable.ic_menu_camera,
		   android.R.drawable.ic_menu_compass,};
		  int[] backgroundcolor = { 
		   0xFF101010,
		   0xFF404040,
		   0xFF505050};

		  @Override
		  public int getCount() {
		   return NumberOfPages;
		  }

		  @Override
		  public boolean isViewFromObject(View view, Object object) {
		   return view == object;
		  }

		  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
		@SuppressLint("InlinedApi")
		@Override
		  public Object instantiateItem(ViewGroup container, int position) {
		   
		      
		      TextView textView = new TextView(DataActivity.this);
		      textView.setTextColor(Color.WHITE);
		      textView.setTextSize(30);
		      textView.setTypeface(Typeface.DEFAULT_BOLD);
		      textView.setText(String.valueOf(position));
		      
//		      ImageView imageView = new ImageView(FragmentData.this);
//		      imageView.setImageResource(res[position]);
//		      LayoutParams imageParams = new LayoutParams(
//		        LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
//		      imageView.setLayoutParams(imageParams);
		      
		      LinearLayout layout = new LinearLayout(DataActivity.this);
		      layout.setOrientation(LinearLayout.VERTICAL);
		      LayoutParams layoutParams = new LayoutParams(
		      LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		      layout.setBackgroundColor(backgroundcolor[position]);
		      layout.setLayoutParams(layoutParams);
		      layout.addView(textView);
		     // layout.addView(imageView);
		      
		      final int page = position;
		      layout.setOnClickListener(new OnClickListener(){

		    @Override
		    public void onClick(View v) {
		     Toast.makeText(getApplicationContext(),"Page " + page + " clicked",Toast.LENGTH_LONG).show();
		    }});
		      
		      container.addView(layout);
		      return layout;
		  }

		  @Override
		  public void destroyItem(ViewGroup container, int position, Object object) {
		   container.removeView((LinearLayout)object);
		  }

		 }


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.streamlined_mode, menu);
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
