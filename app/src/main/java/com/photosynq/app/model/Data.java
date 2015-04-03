package com.photosynq.app.model;

public class Data {
	
	public static final String NO_VALUE = "NO_VALUE";
    public static final String USER_SELECTED = "USER_SELECTED";
    public static final String AUTO_INCREMENT = "AUTO_INCREMENT";
    public static final String SCAN_CODE = "SCAN_CODE";
    public static final String PREV = "PREV";
    public static final String NEXT = "NEXT";
    public String user_id;
	public String project_id;
	public String question_id;
	public String type;
	public String value;
	
	public Data(String user_id,String project_id,String question_id,String type,String value)
	{
		this.user_id = user_id;
		this.project_id = project_id;
		this.question_id = question_id;
		this.type = type;
		this.value = value;
	}
	public Data()
	{
		
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getProject_id() {
		return project_id;
	}
	public void setProject_id(String project_id) {
		this.project_id = project_id;
	}
	public String getQuestion_id() {
		return question_id;
	}
	public void setQuestion_id(String question_id) {
		this.question_id = question_id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
