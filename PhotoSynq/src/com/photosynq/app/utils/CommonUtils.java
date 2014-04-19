package com.photosynq.app.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

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
	
	
	public static boolean isConnected(Context context)
	{
		ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		System.out.println("$$$$$$$$$$$ Internet connection ? "+ (activeNetwork != null && activeNetwork.isConnected()));
		return activeNetwork != null && activeNetwork.isConnected();
	}
	
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

}
