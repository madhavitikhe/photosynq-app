
package com.photosynq.app.navigationDrawer;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.photosynq.app.R;
import com.photosynq.app.utils.PrefUtils;

public class NavigationMain extends ActionBarActivity{
	
	public static final String LAST_POSITION = "LAST_POSITION";
    private int lastPosition = 0;
	private ListView listDrawer;    
	private int counterItemDownloads;
	protected DrawerLayout layoutDrawer;		
	private LinearLayout linearDrawer;
	private RelativeLayout userDrawer;
	private NavigationAdapter navigationAdapter;
	private ActionBarDrawerToggleCompat drawerToggle;
	private String mEmail;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		getSupportActionBar().setIcon(R.drawable.ic_action_play);
		setContentView(R.layout.navigation_main);		
		
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);		
        
        mEmail = PrefUtils.getFromPrefs(getApplicationContext() , PrefUtils.PREFS_LOGIN_USERNAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);
        TextView user_email = (TextView) findViewById(R.id.userEmail);
        user_email.setText(mEmail);
        
        listDrawer = (ListView) findViewById(R.id.listDrawer);        
		linearDrawer = (LinearLayout) findViewById(R.id.linearDrawer);		
		layoutDrawer = (DrawerLayout) findViewById(R.id.layoutDrawer);	
		
		userDrawer = (RelativeLayout) findViewById(R.id.userDrawer);
		userDrawer.setOnClickListener(userOnClick);
		
		if (listDrawer != null) {
			navigationAdapter = NavigationAdapter.getNavigationAdapter(this);
		}
		
		listDrawer.setAdapter(navigationAdapter);
		listDrawer.setOnItemClickListener(new DrawerItemClickListener());

		drawerToggle = new ActionBarDrawerToggleCompat(this, layoutDrawer);		
		layoutDrawer.setDrawerListener(drawerToggle);
       		
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
     
        TextView sign_out=(TextView) findViewById(R.id.signOut);
        sign_out.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                	SharedPreferences settings =  PreferenceManager.getDefaultSharedPreferences(getBaseContext());                          
    		        SharedPreferences.Editor editor = settings.edit();
    		        editor.clear();
    		        editor.commit();
    		        finish();
                }
            });
	
        
	}
	
	 private class DrawerItemClickListener implements ListView.OnItemClickListener {
	        @Override
	        public void onItemClick(AdapterView<?> parent, View view, int posicao, long id) {          	        	
		    	setLastPosition(posicao);        	
		    	setFragmentList(lastPosition);	  
		    	Toast.makeText(getApplicationContext(), "You clicked on "+ posicao, Toast.LENGTH_LONG).show();
		    	layoutDrawer.closeDrawer(linearDrawer);	    	
	        }
	    }	
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub		
		super.onSaveInstanceState(outState);		
		outState.putInt(LAST_POSITION, lastPosition);					
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {  
			return super.onOptionsItemSelected(item);			
    }
		
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);  
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);        		
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);	     
	    drawerToggle.syncState();	
	 }	
	
	public void setTitleActionBar(CharSequence informacao) {    	
    	getSupportActionBar().setTitle(informacao);
    }	
	
	public void setSubtitleActionBar(CharSequence informacao) {    	
    	getSupportActionBar().setSubtitle(informacao);
    }	

	public void setIconActionBar(int icon) {    	
    	getSupportActionBar().setIcon(icon);
    }	
	
	public void setLastPosition(int posicao){		
		this.lastPosition = posicao;
	}	
		
	private class ActionBarDrawerToggleCompat extends ActionBarDrawerToggle {

		public ActionBarDrawerToggleCompat(Activity mActivity, DrawerLayout mDrawerLayout){
			super(
			    mActivity,
			    mDrawerLayout, 
  			    R.drawable.ic_action_navigation_drawer, 
				R.string.drawer_open,
				R.string.drawer_close);
		}
		
		@Override
		public void onDrawerClosed(View view) {			
			supportInvalidateOptionsMenu();				
		}

		@Override
		public void onDrawerOpened(View drawerView) {	
			navigationAdapter.notifyDataSetChanged();			
			supportInvalidateOptionsMenu();			
		}		
	}
		  
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		drawerToggle.onConfigurationChanged(newConfig);		
	}
	
   
    
	private OnClickListener userOnClick = new OnClickListener() {		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			layoutDrawer.closeDrawer(linearDrawer);
		}
	};	
	
	private void setFragmentList(int position){
		
		FragmentManager fragmentManager = getSupportFragmentManager();							
		
		switch (position) {
		case 0:			
			Toast.makeText(getApplicationContext(), "You clicked on Mode Option", Toast.LENGTH_LONG).show();
			fragmentManager.beginTransaction().replace(R.id.content_frame, new ViewPagerFragment()).commit();
			break;					
		case 1:			
			fragmentManager.beginTransaction().replace(R.id.content_frame, new ViewPagerFragment()).commit();
			break;			
		case 2:			
			fragmentManager.beginTransaction().replace(R.id.content_frame, new ViewPagerFragment()).commit();						
			break;				
			
		default:
			Toast.makeText(getApplicationContext(), "implement other fragments here", Toast.LENGTH_SHORT).show();
			break;	
		}			
	
		if (position < 5){
			navigationAdapter.resetarCheck();			
			navigationAdapter.setChecked(position, true);
		}
	}

	public void setTitleFragments(int position){	
		setIconActionBar(Utils.iconNavigation[position]);
		setSubtitleActionBar(Utils.getTitleItem(NavigationMain.this, position));				
	}

	public int getCounterItemDownloads() {
		return counterItemDownloads;
	}

	public void setCounterItemDownloads(int value) {
		this.counterItemDownloads = value;
	}
}
