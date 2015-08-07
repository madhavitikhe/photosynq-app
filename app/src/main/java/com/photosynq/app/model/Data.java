package com.photosynq.app.model;

public class Data {
	
	public static final String NO_VALUE = "NO_VALUE";
    public static final String USER_SELECTED = "USER_SELECTED";
    public static final String AUTO_INCREMENT = "AUTO_INCREMENT";
    public static final String SCAN_CODE = "SCAN_CODE";
    public static final String PREV = "PREV";
    public static final String NEXT = "NEXT";
    private String user_id;
	private String project_id;
	private String question_id;
	private String type;
	private String value;
	private String selected_option;
    private String is_remembered;

	
	public Data(String user_id,String project_id,String question_id,String type,String value, String selected_option, String is_remembered)
	{
		this.user_id = user_id;
		this.project_id = project_id;
		this.question_id = question_id;
		this.type = type;
		this.value = value;
		this.selected_option = selected_option;
		this.is_remembered = is_remembered;
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
    public String getIs_remembered() {
        return is_remembered;
    }

    public void setIs_remembered(String is_remembered) {
        this.is_remembered = is_remembered;
    }

    public String getSelected_option() {
        return selected_option;
    }

    public void setSelected_option(String selected_option) {
        this.selected_option = selected_option;
    }


}
