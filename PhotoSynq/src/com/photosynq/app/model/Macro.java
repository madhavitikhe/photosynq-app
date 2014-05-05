package com.photosynq.app.model;

import com.photosynq.app.utils.CommonUtils;

public class Macro {
	
	private String recordHash;
	private String id;
	private String name;
	private String defaultXAxis;
	private String defaultYAxis;
	private String javascriptCode;
	private String description;
	private String slug;
	
	public Macro()
	{
		
	}
	public Macro(String id, String name, String description, String defaultXAxis,String defaultYAxis, String javascriptCode, String slug)
	{
		this.id = id;
		this.name = name;
		this.description = description;
		this.defaultXAxis = defaultXAxis;
		this.defaultYAxis = defaultYAxis;
		this.javascriptCode = javascriptCode;
		this.slug = slug;
		this.recordHash = getMacroRecordHash();
	}
	private String getMacroRecordHash() {
		String recordString = (null != getId() ? getId(): "") 
				+ (null != getName()? getName() : "" )
				+ (null != getDescription()? getDescription() : "" )
				+ (null != getDefaultXAxis()? getDefaultXAxis() : "" )
				+ (null != getDefaultYAxis()? getDefaultYAxis() : "" )
				+ (null != getJavascriptCode()? getJavascriptCode() : "" )
				+ (null != getSlug()? getSlug() : "" );
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
	public String getDefaultXAxis() {
		return defaultXAxis;
	}
	public void setDefaultXAxis(String defaultXAxis) {
		this.defaultXAxis = defaultXAxis;
	}
	public String getDefaultYAxis() {
		return defaultYAxis;
	}
	public void setDefaultYAxis(String defaultYAxis) {
		this.defaultYAxis = defaultYAxis;
	}
	public String getJavascriptCode() {
		return javascriptCode;
	}
	public void setJavascriptCode(String javascriptCode) {
		this.javascriptCode = javascriptCode;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getSlug() {
		return slug;
	}
	public void setSlug(String slug) {
		this.slug = slug;
	}

}
