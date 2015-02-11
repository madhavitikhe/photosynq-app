package com.photosynq.app.model;

import com.photosynq.app.utils.CommonUtils;

public class ProjectLead {

	public String recordHash;
    public String id;
    public String name;
    public String data_count;
    public String imageUrl;

    public ProjectLead(String id, String name, String data_count, String imageUrl)
    {
    	this.id = id;
    	this.name = name;
    	this.data_count = data_count;
    	this.imageUrl = imageUrl;
    	this.recordHash = getProjectRecordHash();

    }
    public ProjectLead()
    {
    	
    }
    
    public String getProjectRecordHash() {
		String recordString = (null != getId() ? getId() : "") 
				+ (null != getName() ? getName() : "" )
				+ (null != getDataCount() ? getDataCount() : "" )
				+ (null != getImageUrl() ? getImageUrl() : "");
		return CommonUtils.getMD5EncryptedString(recordString);
	}
	public String getRecordHash() {
		return recordHash;
	}
	public void setRecordHash(String recordHash) {
		this.recordHash = recordHash;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDataCount() {
		return data_count;
	}
	public void setDataCount(String data_count) {
		this.data_count = data_count;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
}
