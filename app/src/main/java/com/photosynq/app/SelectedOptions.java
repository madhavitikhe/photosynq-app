package com.photosynq.app;

/**
 * Created by shekhar on 8/25/15.
 */
public class SelectedOptions {

    private String selectedValue;
    private String urlValue;

    private String questionId;
    private int questionType;
    private String projectId;

    public boolean isReset() {
        return reset;
    }

    public void setReset(boolean reset) {
        this.reset = reset;
    }

    private boolean reset;

    public String getSelectedValue() {
        return selectedValue;
    }

    public void setSelectedValue(String selectedValue) {
        this.selectedValue = selectedValue;
    }

    public String getUrlValue() {
        return urlValue;
    }

    public void setUrlValue(String urlValue) {
        this.urlValue = urlValue;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public int getQuestionType() {
        return questionType;
    }

    public void setQuestionType(int questionType) {
        this.questionType = questionType;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public boolean isRemember() {
        return remember;
    }

    public void setRemember(boolean remember) {
        this.remember = remember;
    }

    public int getStartRange() {
        return startRange;
    }

    public void setStartRange(int startRange) {
        this.startRange = startRange;
    }

    public int getRangeStep() {
        return rangeStep;
    }

    public void setRangeStep(int rangeStep) {
        this.rangeStep = rangeStep;
    }

    public int getCurrentRangeValue() {
        return currentRangeValue;
    }

    public void setCurrentRangeValue(int currentRangeValue) {
        this.currentRangeValue = currentRangeValue;
    }

    private boolean remember;

    private int startRange;
    private int rangeStep;
    private int currentRangeValue;

}
