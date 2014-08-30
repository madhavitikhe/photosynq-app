package com.photosynq.app.navigationDrawer;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.photosynq.app.R;
import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.Question;
import com.photosynq.app.utils.PrefUtils;

public class FragmentData extends Fragment {
	MyPageAdapter pageAdapter;
	ViewPager viewPager;
	DatabaseHelper db;
	private String userID;

	public static FragmentData newInstance() {
		FragmentData fragment = new FragmentData();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		final View rootView = inflater.inflate(R.layout.fragment_data,container, false);
		viewPager = (ViewPager) rootView.findViewById(R.id.viewPager);
		db = DatabaseHelper.getHelper(getActivity());
		userID = PrefUtils.getFromPrefs(getActivity(),
						PrefUtils.PREFS_LOGIN_USERNAME_KEY,
						PrefUtils.PREFS_DEFAULT_VAL);
		List<Fragment> fragments = getFragments();
		pageAdapter = new MyPageAdapter(getActivity()
				.getSupportFragmentManager(), fragments);
		
		viewPager.setAdapter(pageAdapter);
		
		rootView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		return rootView;
	}

	private List<Fragment> getFragments() {
		List<Fragment> fList = new ArrayList<Fragment>();
		List<Question> questions = db.getAllQuestionForProject(db.getSettings(
				userID).getProjectId());

		for (Question question : questions) {
			Bundle bundle = new Bundle();
			bundle.putString(DatabaseHelper.C_QUESTION_ID,
					question.getQuestionId());
			DataFirstFragment f = new DataFirstFragment();
			f.setArguments(bundle);
			fList.add(f);
		}
		return fList;
	}

	private class MyPageAdapter extends FragmentPagerAdapter {
		private List<Fragment> fragments;
		private FragmentManager fm;

		public MyPageAdapter(FragmentManager fm, List<Fragment> fragments) {
			super(fm);
			this.fragments = fragments;
			this.fm = fm;
		}

		@Override
		public Fragment getItem(int position) {
			fm.beginTransaction().attach(fragments.get(position)).commit();
			return this.fragments.get(position);
		}

		@Override
		public int getCount() {
			return this.fragments.size();
		}
	}
}
