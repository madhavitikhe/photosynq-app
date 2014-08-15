package com.photosynq.app.navigationDrawer;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;

import com.photosynq.app.R;

public class DataActivity extends NavigationDrawer {
	
	protected void onCreate(Bundle savedInstanceState) {

		ViewPager myViewPager;
		MyFragmentPagerAdapter myFragmentPagerAdapter;
		    
		
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.data_activity);
		LayoutInflater inflater = (LayoutInflater) this
	            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View contentView = inflater.inflate(R.layout.data_activity, null, false);
	    layoutDrawer.addView(contentView, 0); 
		
		myViewPager = (ViewPager) findViewById(R.id.viewpager);
	    myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
	    myViewPager.setAdapter(myFragmentPagerAdapter);
	}
	
	 private class MyFragmentPagerAdapter extends FragmentPagerAdapter{
		 
		 int numberOfPages = 3;
		 public MyFragmentPagerAdapter(FragmentManager fm)   {
		        super(fm);
		    }

		    @Override
		    public android.support.v4.app.Fragment getItem(int index) {

		    	//return DataFirstFragment.newInstance();
		    	if(index==0)
		    	{
		    		return new DataFirstFragment();
		    	}
		    	else if(index==1)
		    	{
		    		return new DataSecondFragment();
		    	}
		    	else
		    	{
		    		return new DataThirdFragment();
		    	}
		    }

		    @Override
		    public int getCount() {
		        return numberOfPages;
		    }
	 }
}
