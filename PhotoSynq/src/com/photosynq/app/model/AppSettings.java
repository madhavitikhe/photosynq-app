package com.photosynq.app.model;

public class AppSettings {
	//private variables
		public static final String defaultValue = "NOT SET";
		public String userId;
		public String modeType;
		public String connectionId;
		public String projectId;
		
		// Empty constructor
		public AppSettings( ){
			
		}
		public AppSettings(String userID){
			this.userId = userID;
			this.connectionId = this.modeType = this.projectId = defaultValue;
		}
		// constructor
		public AppSettings(String userId, String modeType, String connectionId, String projectId){
			this.userId = userId;
			this.modeType = modeType;
			this.connectionId = connectionId;
			this.projectId = projectId;
			
		}
		public String getUserId() {
			return userId;
		}
		public void setUserId(String userId) {
			this.userId = userId;
		}
		public String getModeType() {
			return modeType;
		}
		public void setModeType(String modeType) {
			this.modeType = modeType;
		}
		public String getConnectionId() {
			return connectionId;
		}
		public void setConnectionId(String connectionId) {
			this.connectionId = connectionId;
		}
		public String getProjectId() {
			return projectId;
		}
		public void setProjectId(String projectId) {
			this.projectId = projectId;
		}
		public static String getDefaultvalue() {
			return defaultValue;
		}
		
}
