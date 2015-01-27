package com.photosynq.app.model;

import com.photosynq.app.utils.CommonUtils;

public class Option {
	 public String recordHash;
	 public String optionText;
	 public String questionId;
	 public String projectId;
	    
	    public Option(String questionId,String optionText, String projectId)
	    {
	    	this.optionText = optionText;
	    	this.questionId = questionId;
	    	this.projectId = projectId;
	    	this.recordHash = getOptionRecordHash();
	    }
	    public Option()
	    {
	    	
	    }

		private  String getOptionRecordHash() {
			String recordString = (null != getQuestionId()? getQuestionId() : "" )
					+ (null != getProjectId()? getProjectId() : "" )
					+ (null != getOptionText()? getOptionText() : "" );
			return CommonUtils.getMD5EncryptedString(recordString);
		}

		public String getRecordHash() {
			return recordHash;
		}

		public void setRecordHash(String recordHash) {
			this.recordHash = recordHash;
		}

		public String getOptionText() {
			return optionText;
		}

		public void setOptionText(String optionText) {
			this.optionText = optionText;
		}

		public String getQuestionId() {
			return questionId;
		}

		public void setQuestionId(String questionId) {
			this.questionId = questionId;
		}
		public String getProjectId() {
			return projectId;
		}
		public void setProjectId(String projectId) {
			this.projectId = projectId;
		}
		

}
