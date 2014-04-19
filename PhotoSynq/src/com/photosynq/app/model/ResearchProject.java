package com.photosynq.app.model;

import com.photosynq.app.utils.CommonUtils;

public class ResearchProject {

	public String recordHash;
    public String id;
    public String name;
    public String description;
    public String dirToCollab;
    public String startDate;
    public String endDate;
    public String imageUrl;
    public String beta;
    
    public ResearchProject(String id, String name, String description, String dirToCollab,
    		String startDate, String endDate, String imageUrl, String beta)
    {
    	this.id = id;
    	this.name = name;
    	this.description= description;
    	this.dirToCollab = dirToCollab;
    	this.startDate =startDate;
    	this.endDate = endDate;
    	this.imageUrl = imageUrl;
    	this.beta = beta;
    	this.recordHash = getProjectRecordHash();
    }
    public ResearchProject()
    {
    	
    }
    
    public String getProjectRecordHash() {
		String recordString = (null != getId() ? getId() : "") 
				+ (null != getName() ? getName() : "" )
				+ (null != getDescription() ? getDescription() : "" )
				+ (null != getDirToCollab() ? getDirToCollab() : "")
				+ (null != getStartDate() ? getStartDate() : "") 
				+ (null != getEndDate() ? getEndDate() : "") 
				+ (null != getBeta() ? getBeta() : "")
				+ (null != getImageUrl() ? getImageUrl() : "");
		System.out.println("$$$$$$ Project record string : "+recordString);
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDirToCollab() {
		return dirToCollab;
	}
	public void setDirToCollab(String dirToCollab) {
		this.dirToCollab = dirToCollab;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getBeta() {
		return beta;
	}
	public void setBeta(String beta) {
		this.beta = beta;
	}
}
