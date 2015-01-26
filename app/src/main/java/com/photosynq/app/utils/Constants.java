package com.photosynq.app.utils;

/**
 * Created by kalpesh on 24/01/15.
 */
public class Constants {

    public static final String SUCCESS = "SUCCESS";
    public static final String SERVER_NOT_ACCESSIBLE = "SERVER_NOT_ACCESSIBLE";
    public static final String SERVER_URL = "http://photosynq.org/";
    //public static final String SERVER_URL = "http://www.photosynq.org/";
    public static final String API_VER = "api/v1/";

    public static final String PHOTOSYNQ_LOGIN_URL = SERVER_URL+API_VER+"sign_in.json";
    public static final String PHOTOSYNQ_PROJECTS_LIST_URL = SERVER_URL+API_VER+"projects.json?";
    public static final String PHOTOSYNQ_PROTOCOLS_LIST_URL = SERVER_URL+API_VER+"protocols.json?";
    public static final String PHOTOSYNQ_MACROS_LIST_URL =SERVER_URL+API_VER+ "macros.json?";
    public static final String PHOTOSYNQ_DATA_URL = SERVER_URL+API_VER+"projects/";
}
