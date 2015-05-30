package com.photosynq.app.model;

/**
 * Created by tushar on 4/28/15.
 */

//Store all user answer, because we need to show it on autocomplete textview.
public class UserAnswer {

    public String optionText;

    public UserAnswer(String optionText)
    {
        this.optionText = optionText;
    }
    public UserAnswer()
    {

    }

    public String getOptionText() {
        return optionText;
    }

    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }



}
