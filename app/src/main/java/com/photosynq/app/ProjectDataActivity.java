package com.photosynq.app;

import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.Data;
import com.photosynq.app.model.Question;

import java.util.ArrayList;
import java.util.List;


public class ProjectDataActivity extends ActionBarActivity {

    DatabaseHelper dbHelper;
    String projectId;
    private ViewPager viewPager;
    private List<Fragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_data);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        ColorDrawable newColor = new ColorDrawable(getResources().getColor(R.color.green_light));//your color from res
        newColor.setAlpha(0);//from 0(0%) to 256(100%)
        //actionBar.setBackgroundDrawable(newColor);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;

        dbHelper = DatabaseHelper.getHelper(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            projectId = extras.getString(DatabaseHelper.C_PROJECT_ID);
        }

        viewPager = (ViewPager) findViewById(R.id.viewPager);

        List<Question> questions = dbHelper.getAllQuestionForProject(projectId);
        fragments = getFragments(questions);
        MyPageAdapter pageAdapter = new MyPageAdapter(getSupportFragmentManager(), fragments);

        viewPager.setAdapter(pageAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if(state == ViewPager.SCROLL_STATE_DRAGGING) {
                    int currentFragmentIdx = viewPager.getCurrentItem();
                    DataFragment dataFragment = (DataFragment) fragments.get(currentFragmentIdx);
                    dataFragment.saveData(true);
                }

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_project_data, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            if(saveData()) {
                finish();
            }else{
                Toast.makeText(this, "Incomplete information, please define answer types in data tab.", Toast.LENGTH_LONG).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean saveData(){
        boolean retVal = false;

        int fragCnt = fragments.size();
        for(int idx = 0; idx < fragCnt; idx++){
            retVal = ((DataFragment)fragments.get(idx)).saveData(false);
            if(retVal == false)
                break;
        }

        return  retVal;
    }

    /**
     * It's calculate fragment size and send boolean values for prev and next to DataFirstFragment.
     */
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
            bundle.putString(DatabaseHelper.C_PROJECT_ID, projectId);
            bundle.putString(DatabaseHelper.C_QUESTION_ID, questions.get(i).getQuestionId());
            bundle.putInt(DatabaseHelper.C_QUESTION_TYPE, questions.get(i).getQuestionType());
            DataFragment dataFragment = new DataFragment();
            dataFragment.setArguments(bundle);
            fList.add(dataFragment);
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
