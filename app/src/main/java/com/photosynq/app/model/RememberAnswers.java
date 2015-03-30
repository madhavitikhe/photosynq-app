package com.photosynq.app.model;

/**
 * Created by tushar on 3/25/15.
 */
public class RememberAnswers {

    public static final String IS_REMEMBER = "IS_REMEMBER";

    public String user_id;
    public String project_id;
    public String question_id;
    public String selected_option_text;
    public String is_remember;

    public RememberAnswers(String user_id,String project_id,String question_id,String selected_option_text,String is_remember)
    {
        this.user_id = user_id;
        this.project_id = project_id;
        this.question_id = question_id;
        this.selected_option_text = selected_option_text;
        this.is_remember = is_remember;
    }
    public RememberAnswers()
    {

    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getProject_id() {
        return project_id;
    }

    public void setProject_id(String project_id) {
        this.project_id = project_id;
    }

    public String getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(String question_id) {
        this.question_id = question_id;
    }

    public String getSelected_option_text() {
        return selected_option_text;
    }

    public void setSelected_option_text(String selected_option_text) {
        this.selected_option_text = selected_option_text;
    }

    public String getIs_remember() {
        return is_remember;
    }

    public void setIs_remember(String is_remember) {
        this.is_remember = is_remember;
    }
}
