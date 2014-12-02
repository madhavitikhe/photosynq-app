package com.photosynq.app.model;

import com.photosynq.app.utils.CommonUtils;

public class Protocol {

    public static final String NAME = "PROTOCOL_NAME";
    public static final String DESCRIPTION = "PROTOCOL_DESCRIPTION";

	private String recordHash;
	private String id;
	private String name;
	private String protocol_json;
	private String description;
	private String macroId;
	private String slug;
	
	public Protocol()
	{
		
	}
	public Protocol(String id, String name, String protocol_json,String description, String macroId, String slug)
	{
		this.id = id;
		this.name = name;
		this.setProtocol_json(protocol_json);
		this.description = description;
		this.macroId = macroId;
		this.slug = slug;
		this.recordHash = getProtocolRecordHash();
	}
	private String getProtocolRecordHash() {
		String recordString = (null != getId() ? getId(): "") 
				+ (null != getName()? getName() : "" )
				+ (null != getProtocol_json()? getProtocol_json() : "" )
				+ (null != getDescription()? getDescription() : "" )
				+ (null != getMacroId()? getMacroId() : "" )
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getMacroId() {
		return macroId;
	}
	public void setMacroId(String macroId) {
		this.macroId = macroId;
	}
	public String getSlug() {
		return slug;
	}
	public void setSlug(String slug) {
		this.slug = slug;
	}
	public String getProtocol_json() {
		return protocol_json;
	}
	public void setProtocol_json(String protocol_json) {
		this.protocol_json = protocol_json;
	}
	
	
}
