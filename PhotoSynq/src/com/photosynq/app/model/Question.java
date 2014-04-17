package com.photosynq.app.model;

public class Question {
	public String project_hash;
    public String question_id;
    public String question_text;
   
    
    public Question(String project_hash,String question_text)
    {
    	this.question_text=question_text;
    	this.project_hash=project_hash;
    }
    public Question()
    {
    	
    }
	public String getProject_hash() {
		return project_hash;
	}
	public void setProject_hash(String project_hash) {
		this.project_hash = project_hash;
	}
	public String getQuestion_id() {
		return question_id;
	}
	public void setQuestion_id(String question_id) {
		this.question_id = question_id;
	}
	public String getQuestion_text() {
		return question_text;
	}
	public void setQuestion_text(String question_text) {
		this.question_text = question_text;
	}
	
	

}
