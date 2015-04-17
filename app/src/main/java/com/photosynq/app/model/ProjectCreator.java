package com.photosynq.app.model;

import com.photosynq.app.utils.CommonUtils;

public class ProjectCreator {

	public String recordHash;
    public String id;
    public String name;
	public String imageUrl;


    public ProjectCreator(String id, String name, String imageUrl)
    {
    	this.id = id;
    	this.name = name;
		this.imageUrl = imageUrl;
    	this.recordHash = getProjectRecordHash();

    }
    public ProjectCreator()
    {
    	
    }
    
    public String getProjectRecordHash() {
		String recordString = (null != getId() ? getId() : "") 
				+ (null != getName() ? getName() : "" )
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
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

}
