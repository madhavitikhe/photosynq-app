package com.photosynq.app.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.photosynq.app.HTTP.HTTPConnection;
import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.Data;
import com.photosynq.app.model.Question;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@SuppressLint("SimpleDateFormat")
public class CommonUtils {

	//Generate a MD5 hash from given string
	public static String getMD5EncryptedString(String encTarget){
        MessageDigest mdEnc = null;
        try {
            mdEnc = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Exception while encrypting to md5");
            e.printStackTrace();
        } // Encryption algorithm
        mdEnc.update(encTarget.getBytes(), 0, encTarget.length());
        String md5 = new BigInteger(1, mdEnc.digest()).toString(16);
        while ( md5.length() < 32 ) {
            md5 = "0"+md5;
        }
        return md5;
    }

    // Invoke this method only on Async task. Do not invoke on UI thread. it will throw exceptions anyway ;)
    public static boolean isConnected(Context context) {
        if (isNetworkAvailable(context)) {
            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL(Constants.SERVER_URL).openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                return (urlc.getResponseCode() == 200);
            } catch (IOException e) {
                Log.e("Connectivity", "Error checking internet connection", e);
            }
        } else {
            Log.d("Connectivity", "No network available!");
        }
        return false;
    }


    private static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }
	
//	public static boolean isConnected(Context context)
//	{
//		ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
//		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
//		System.out.println("$$$$$$$$$$$ Internet connection ? "+ (activeNetwork != null && activeNetwork.isConnected()));
//		return activeNetwork != null && activeNetwork.isConnected();
//	}
	
	public static Date convertToDate(String rawdate)
	{
	    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");//new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa");
	    Date convertedDate = new Date();
	    try {
	        convertedDate = dateFormat.parse(rawdate);
	        System.out.println(convertedDate);
			return convertedDate;
	    } catch (ParseException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
		return null;
	 
	}
	
	public static void writeStringToFile(Context context,String fileName, String dataString)
	{
        try {
            File myFile = new File(context.getExternalFilesDir(null), fileName);
            if (myFile.exists()){
            	myFile.delete();
            }

                myFile.createNewFile();

            FileOutputStream fos;
            //dataString = dataString.replaceAll("\\{", "{\"time\":\""+time+"\",");
            byte[] data = dataString.getBytes();
            try {
                fos = new FileOutputStream(myFile);
                fos.write(data);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }catch (Exception e) {
        	e.printStackTrace();
		}

	}

    /**
     * This function is call when user selects 'Auto Increment' option, this
     * function get input text(from,to and repeat) from user and calculates
     * auto increment values and stored it into populatesValues variable,
     * cycle performs (to*repeat=total) times.
     * Ex.
     * From    1
     * To      2
     * Repeat  3
     *
     * PopulatesValues are --
     * 		1	2
     * 		1	2
     * 		1	2
     */
    public static String getAutoIncrementedValue(Context ctx,String question_id, String index) {
        if(Integer.parseInt(index) == -1) {
            return "-2";
        }

        String userId = PrefUtils.getFromPrefs(ctx , PrefUtils.PREFS_LOGIN_USERNAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);
        DatabaseHelper db = DatabaseHelper.getHelper(ctx);
        String projectId = db.getSettings(userId).getProjectId();
        Question question = db.getQuestionForProject(projectId, question_id);
        Data data = db.getData(userId, projectId, question.getQuestionId());
        String[] items = data.getValue().split(",");
        int from = Integer.parseInt(items[0]);
        int to = Integer.parseInt(items[1]);
        int repeat = Integer.parseInt(items[2]);
        ArrayList<Integer> populatedValues = new ArrayList<Integer>();
        for(int i=from;i<=to;i++){
            for(int j=0;j<repeat;j++){
                populatedValues.add(i);

            }
        }

        if(Integer.parseInt(index) > populatedValues.size()-1)
            return "-1";

        return populatedValues.get(Integer.parseInt(index)).toString();
    }

}
