package com.photosynq.app.model;

import com.photosynq.app.utils.CommonUtils;

public class ResearchProject {

	public String recordHash;
    public String id;
    public String name;
    public String description;
    public String dirToCollab;
	public String creatorId;
    public String startDate;
    public String endDate;
    public String imageUrl;
    public String beta;
	public String is_contributed;
    public String protocols_ids;
    
    public ResearchProject(String id, String name, String description, String dirToCollab, String creatorId,
    		String startDate, String endDate, String imageUrl, String beta, String is_contributed, String protocols_ids)
    {
    	this.id = id;
    	this.name = name;
    	this.description= description;
    	this.dirToCollab = dirToCollab;
		this.creatorId = creatorId;
    	this.startDate =startDate;
    	this.endDate = endDate;
    	this.imageUrl = imageUrl;
    	this.beta = beta;
		this.is_contributed = is_contributed;
    	this.protocols_ids = protocols_ids;
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
				+ (null != getCreatorId() ? getCreatorId() : "")
				+ (null != getStartDate() ? getStartDate() : "")
				+ (null != getEndDate() ? getEndDate() : "") 
				+ (null != getBeta() ? getBeta() : "")
				+ (null != getIs_contributed() ? getIs_contributed() : "")
				+ (null != getProtocols_ids() ? getProtocols_ids() : "")
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
	public String getCreatorId() {
		return creatorId;
	}
	public void setCreatorId(String creator) {
		this.creatorId = creator;
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
	}	public String getIs_contributed() {
		return is_contributed;
	}

	public void setIs_contributed(String is_contributed) {
		this.is_contributed = is_contributed;
	}

	public String getProtocols_ids() {
		return protocols_ids;
	}
	public void setProtocols_ids(String protocols_ids) {
		this.protocols_ids = protocols_ids;
	}
}
