package com.photosynq.app.model;

import com.photosynq.app.utils.CommonUtils;

public class ProjectResult {

	private String projectId;
	private String id;
	private String reading;
	private String uploaded;
	private String recordHash;
	
	public ProjectResult(String projectId, String reading, String uploaded)
	{
		this.projectId = projectId;
		this.reading = reading;
		this.uploaded = uploaded;
		this.recordHash = getResultRecordHash();
	}
	public ProjectResult()
	{
		
	}
	
	private  String getResultRecordHash() {
		String recordString =  (null != getProjectId()? getProjectId() : "" )
				+(null != getUploaded()? getUploaded() : "" )
				+ (null != getReading()? getReading() : "" );
		return CommonUtils.getMD5EncryptedString(recordString);
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getReading() {
		return reading;
	}

	public void setReading(String reading) {
		this.reading = reading;
	}

	public String getRecordHash() {
		return recordHash;
	}

	public void setRecordHash(String recordHash) {
		this.recordHash = recordHash;
	}

	public String getUploaded() {
		return uploaded;
	}

	public void setUploaded(String uploaded) {
		this.uploaded = uploaded;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
}
