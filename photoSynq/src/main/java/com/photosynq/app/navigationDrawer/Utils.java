package com.photosynq.app.navigationDrawer;

import android.content.Context;

import com.photosynq.app.R;

public class Utils {
	
	public static final String APP_MODE = "APP_MODE";
	public static final String APP_MODE_QUICK_MEASURE = "APP_MODE_QUICK_MEASURE";
	public static final String APP_MODE_STREAMLINE = "APP_MODE_STREAMLINE";
	public static final String APP_MODE_NORMAL = "APP_MODE_NORMAL";

    public enum SettingKey {
		MODE("MODE"), USER("USER"), CONNECTION("CONNECTION"), PROJECT("PROJECT");
	 
		private String statusCode;
	 
		private SettingKey(String s) {
			statusCode = s;
		}
	 
		public String getStatusCode() {
			return statusCode;
		}
	 
	}
	
	
	public enum QuestionType {
		SCAN_CODE("SCAN_CODE"), 
		USER_SELECTED("USER_SELECTED"), 
		PROJECT_SELECTED("PROJECT_SELECTED"), 
		FIXED_VALUE("FIXED_VALUE"), 
		AUTO_INCREMENT("AUTO_INCREMENT");
	 
		private String statusCode;
		private QuestionType(String s) {
			statusCode = s;
		}
		public String getStatusCode() {
			return statusCode;
		}
	}
	
	
	//  Add '0' to this array add navigation drawer 'O' for navigation drawer items.
	public static int[] iconNavigation = new int[] { 0,
		0, 0, 0, 0, 0, 0, 0, 0};
	
	//It returns navigation drawer items title.
	public static String getTitleItem(Context context, int position){
		String[] titles = context.getResources().getStringArray(R.array.nav_menu_items);
		return titles[position];
	} 
}
