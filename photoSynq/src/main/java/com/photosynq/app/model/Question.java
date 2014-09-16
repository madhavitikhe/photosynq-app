package com.photosynq.app.model;

import java.util.List;

import com.photosynq.app.utils.CommonUtils;

public class Question {
	
	private String projectId;
	private String questionId;
    private String recordHash;
    private String questionText;
    
    private List<String> options;
   
    
    public Question(String questionId,String project_id,String question_text)
    {
    	this.questionId = questionId;
    	this.questionText=question_text;
    	this.projectId=project_id;
    	this.recordHash = getQuestionRecordHash();
    }
   
	public Question()
	{
		
	}
	private  String getQuestionRecordHash() {
		String recordString =  (null != getProjectId()? getProjectId() : "" )
				+ (null != getQuestionId()? getQuestionId() : "" )
				+ (null != getQuestionText()? getQuestionText() : "" );
		return CommonUtils.getMD5EncryptedString(recordString);
	}


	public String getProjectId() {
		return projectId;
	}


	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}


	public String getQuestionText() {
		return questionText;
	}


	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}


	public void setRecordHash(String recordHash) {
		this.recordHash = recordHash;
	}
	public String getRecordHash() {
		return this.recordHash;
	}

	public String getQuestionId() {
		return questionId;
	}

	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}

	public List<String> getOptions() {
		return options;
	}

	public void setOptions(List<String> options) {
		this.options = options;
	}
	
	
}
