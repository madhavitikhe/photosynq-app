package com.photosynq.app.model;

public class AppSettings {
	//private variables
		public static final String defaultValue = "NOT SET";
		public String userID;
		public String modeType;
		public String connectionID;
		public String projectID;
		
		// Empty constructor
		public AppSettings( ){
			
		}
		public AppSettings(String userID){
			this.userID = userID;
			this.connectionID = this.modeType = this.projectID = defaultValue;
		}
		// constructor
		public AppSettings(String userID, String modeType, String connectionID, String projectID){
			this.userID = userID;
			this.modeType = modeType;
			this.connectionID = connectionID;
			this.projectID = projectID;
			
		}

		
		public String getModeType() {
			return modeType;
		}
		public void setModeType(String modeType) {
			this.modeType = modeType;
		}
		public String getConnectionID() {
			return connectionID;
		}
		public void setConnectionID(String connectionID) {
			this.connectionID = connectionID;
		}
		public String getProjectID() {
			return projectID;
		}
		public void setProjectID(String projectID) {
			this.projectID = projectID;
		}
		public String getUserID() {
			return userID;
		}
		public void setUserID(String userID) {
			this.userID = userID;
		}
}
