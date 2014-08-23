package com.photosynq.app.navigationDrawer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.photosynq.app.R;
import com.photosynq.app.db.DatabaseHelper;

public class FragmentData extends Fragment{

	ViewPager viewPager;
	FragmentViewPagerAdapter fragmentViewPagerAdapter;
	private String recordid = ""; 
	private boolean quick_measure;
	DatabaseHelper db;
	
	public static FragmentData newInstance() {
		FragmentData fragment = new FragmentData();
        return fragment;
    }	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
			
		final View rootView = inflater.inflate(R.layout.fragment_data, container, false);
		viewPager = (ViewPager) rootView.findViewById(R.id.viewPager);
	    fragmentViewPagerAdapter = new FragmentViewPagerAdapter(getActivity().getSupportFragmentManager());
	    viewPager.setAdapter(fragmentViewPagerAdapter);
	    
	    db = DatabaseHelper.getHelper(getActivity());
		Bundle extras = getArguments();
		if (extras != null) {
			recordid = extras.getString(DatabaseHelper.C_ID);
		}
	    
		rootView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT ));		
		return rootView;
	}
	
	private class FragmentViewPagerAdapter extends FragmentPagerAdapter{
		 
		 int numberOfPages = 3;
		 public FragmentViewPagerAdapter(FragmentManager fm)   {
		        super(fm);
		    }

		    @Override
		    public android.support.v4.app.Fragment getItem(int index) {

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
			
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu, inflater);		
		inflater.inflate(R.menu.menu, menu);
	}	
}
