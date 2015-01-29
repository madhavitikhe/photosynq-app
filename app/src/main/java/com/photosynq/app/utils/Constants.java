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

    // Message types sent from the BluetoothService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_STOP = 6;
    private static final boolean D = true;
    // Key names received from the BluetoothService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
}
