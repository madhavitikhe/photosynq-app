package com.photosynq.app.navigationDrawer;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.photosynq.app.R;

public class ViewPagerFragment extends Fragment{
	private List<SamplePagerItem> mTabs = new ArrayList<SamplePagerItem>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTabs.add(new SamplePagerItem(0, getString(R.string.tab_one), getResources().getColor(Utils.colors[0]),  Color.GRAY));
        mTabs.add(new SamplePagerItem(1, getString(R.string.tab_two), getResources().getColor(Utils.colors[2]), Color.GRAY));
        mTabs.add(new SamplePagerItem(2, getString(R.string.tab_three), getResources().getColor(Utils.colors[4]), Color.GRAY));
//        mTabs.add(new SamplePagerItem(2, getString(R.string.tab_four), getResources().getColor(Utils.colors[4]), Color.GRAY));
//        mTabs.add(new SamplePagerItem(1, getString(R.string.tab_five), getResources().getColor(Utils.colors[4]), Color.GRAY));
//        mTabs.add(new SamplePagerItem(0, getString(R.string.tab_six), getResources().getColor(Utils.colors[4]), Color.GRAY));
        
        for(int i=0;i<mTabs.size();i++)
        {
        	System.out.println(i+" Tab is" + mTabs.get(i).toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	System.out.println("----------ViewPagerFragment");
        return inflater.inflate(R.layout.viewpager_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    	ViewPager mViewPager = (ViewPager) view.findViewById(R.id.mPager);
    	
    	mViewPager.setOffscreenPageLimit(3); 
        mViewPager.setAdapter(new ViewPagerAdapter(getChildFragmentManager(), mTabs));

        SlidingTabLayout mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.mTabs);
        mSlidingTabLayout.setBackgroundResource(R.color.white);
        mSlidingTabLayout.setViewPager(mViewPager);

        mSlidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {

            @Override
            public int getIndicatorColor(int position) {
                return mTabs.get(position).getIndicatorColor();
            }

            @Override
            public int getDividerColor(int position) {
                return mTabs.get(position).getDividerColor();
            }
        });
    }
}