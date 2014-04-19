package com.photosynq.app.model;

import com.photosynq.app.utils.CommonUtils;

public class Protocol {

	private String recordHash;
	private String id;
	private String name;
	private String quickDescription;
	private String protocolNameInArduino_code;
	private String description;
	private String macroId;
	private String slug;
	
	public Protocol()
	{
		
	}
	public Protocol(String id, String name, String quickDescription, String protocolNameInArduino,String description, String macroId, String slug)
	{
		this.id = id;
		this.name = name;
		this.quickDescription = quickDescription;
		this.protocolNameInArduino_code = protocolNameInArduino;
		this.description = description;
		this.macroId = macroId;
		this.slug = slug;
		this.recordHash = getProtocolRecordHash();
	}
	private String getProtocolRecordHash() {
		String recordString = (null != getId() ? getId(): "") 
				+ (null != getName()? getName() : "" )
				+ (null != getQuickDescription()? getQuickDescription() : "" )
				+ (null != getProtocolNameInArduino_code()? getProtocolNameInArduino_code() : "" )
				+ (null != getDescription()? getDescription() : "" )
				+ (null != getMacroId()? getMacroId() : "" )
				+ (null != getSlug()? getSlug() : "" );
		System.out.println("$$$$$$ Protocol record string : "+recordString);
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
	public String getQuickDescription() {
		return quickDescription;
	}
	public void setQuickDescription(String quickDescription) {
		this.quickDescription = quickDescription;
	}
	public String getProtocolNameInArduino_code() {
		return protocolNameInArduino_code;
	}
	public void setProtocolNameInArduino_code(String protocolNameInArduino_code) {
		this.protocolNameInArduino_code = protocolNameInArduino_code;
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
	
	
}
