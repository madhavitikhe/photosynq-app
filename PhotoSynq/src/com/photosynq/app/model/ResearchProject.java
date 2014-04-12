package com.photosynq.app.model;

public class ResearchProject {

	public String record_hash;
    public String id;
    public String name;
    public String desc;
    public String dir_to_collab;
    public String start_date;
    public String end_date;
    public String image_content_type;
    public String beta;
    
	public String getRecord_hash() {
		return record_hash;
	}
	public void setRecord_hash(String record_hash) {
		this.record_hash = record_hash;
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
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getDir_to_collab() {
		return dir_to_collab;
	}
	public void setDir_to_collab(String dir_to_collab) {
		this.dir_to_collab = dir_to_collab;
	}
	public String getStart_date() {
		return start_date;
	}
	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}
	public String getEnd_date() {
		return end_date;
	}
	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}
	public String getImage_content_type() {
		return image_content_type;
	}
	public void setImage_content_type(String image_content_type) {
		this.image_content_type = image_content_type;
	}
	public String getBeta() {
		return beta;
	}
	public void setBeta(String beta) {
		this.beta = beta;
	}
}
