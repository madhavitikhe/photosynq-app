package com.photosynq.app.model;

public class Option {
	 public String option_id;
	 public String option_text;
	 public String question_id;  
	    
	    public Option(String question_id,String option_text)
	    {
	    	this.option_text = option_text;
	    	this.question_id =question_id;
	    }
	    public Option()
	    {
	    	
	    }
		public String getOption_id() {
			return option_id;
		}
		public void setOption_id(String option_id) {
			this.option_id = option_id;
		}
		public String getOption_text() {
			return option_text;
		}
		public void setOption_text(String option_text) {
			this.option_text = option_text;
		}
		public String getQuestion_id() {
			return question_id;
		}
		public void setQuestion_id(String question_id) {
			this.question_id = question_id;
		}
	    
		

}
