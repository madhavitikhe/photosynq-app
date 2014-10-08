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
	
	
	//Set all the navigation icons and always to set "zero 0" for the item is a category
	public static int[] iconNavigation = new int[] { 0,
		0, 0, 0, 0, 0, 0, 0, 0};
	
	//get title of the item navigation
	public static String getTitleItem(Context context, int posicao){		
		String[] titulos = context.getResources().getStringArray(R.array.nav_menu_items);  
		return titulos[posicao];
	} 
}
