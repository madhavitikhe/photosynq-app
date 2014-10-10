package com.photosynq.app.navigationDrawer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.photosynq.app.R;
import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.Data;
import com.photosynq.app.model.Question;
import com.photosynq.app.utils.PrefUtils;

import java.util.ArrayList;
import java.util.List;

public class FragmentData extends Fragment {
	MyPageAdapter pageAdapter;
    ViewPager viewPager;
	DatabaseHelper db;
	private String userID;

//	public static FragmentData newInstance() {
//		FragmentData fragment = new FragmentData();
//		return fragment;
//	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        db = DatabaseHelper.getHelper(getActivity());
        userID = PrefUtils.getFromPrefs(getActivity(),PrefUtils.PREFS_LOGIN_USERNAME_KEY,
                PrefUtils.PREFS_DEFAULT_VAL);
        List<Question> questions = db.getAllQuestionForProject(db.getSettings(userID).getProjectId());
        //if project is not selected then it shows blank layout with message on data tab click.
        if(questions.size() <=0 )
        {
                View rootView = inflater.inflate(R.layout.blank_layout, container, false);
                TextView tv = (TextView)rootView.findViewById(R.id.messagetv);
                tv.setText(R.string.project_not_selected);
                return rootView;
        }
		View rootView = inflater.inflate(R.layout.fragment_data,container, false);
		viewPager = (ViewPager) rootView.findViewById(R.id.viewPager);

		List<Fragment> fragments = getFragments(questions);
		pageAdapter = new MyPageAdapter(getActivity().getSupportFragmentManager(), fragments);

		viewPager.setAdapter(pageAdapter);
		
		rootView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		return rootView;
	}

	private List<Fragment> getFragments(List<Question> questions) {
		List<Fragment> fList = new ArrayList<Fragment>();
            for (int i = 0; i< questions.size(); i++) {
                Bundle bundle = new Bundle();
                if(i==0 && questions.size() > 1)
                {
                    bundle.putBoolean(Data.PREV,false);
                    bundle.putBoolean(Data.NEXT,true);
                }
                else if (i == 0 && questions.size() ==1)
                {
                    bundle.putBoolean(Data.PREV,false);
                    bundle.putBoolean(Data.NEXT,false);
                }
                else if (i > 0 && questions.size() == i+1 )
                {
                    bundle.putBoolean(Data.PREV,true);
                    bundle.putBoolean(Data.NEXT,false);
                }
                else
                {
                    bundle.putBoolean(Data.PREV,true);
                    bundle.putBoolean(Data.NEXT,true);
                }
                bundle.putString(DatabaseHelper.C_QUESTION_ID,
                        questions.get(i).getQuestionId());
                bundle.putInt(DatabaseHelper.C_QUESTION_TYPE, questions.get(i).getQuestionType());
                DataFirstFragment f = new DataFirstFragment();
                f.setArguments(bundle);
                fList.add(f);
            }
		return fList;
	}

	private class MyPageAdapter extends FragmentStatePagerAdapter {
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
