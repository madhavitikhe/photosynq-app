package com.photosynq.app.navigationDrawer;

import android.content.Context;

import com.photosynq.app.R;

public class Utils {
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
	public static int[] iconNavigation = new int[] { 
		0, 0, 0, 0, 0, 0, 0};	
	
	//get title of the item navigation
	public static String getTitleItem(Context context, int posicao){		
		String[] titulos = context.getResources().getStringArray(R.array.nav_menu_items);  
		return titulos[posicao];
	} 
	
	public static int[] colors = new int[] { 
		R.color.blue_dark, R.color.blue_dark, R.color.red_dark, R.color.red_light,
		R.color.green_dark, R.color.green_light, R.color.orange_dark, R.color.orange_light,
		R.color.purple_dark, R.color.purple_light};	
}
