package com.photosynq.app.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.photosynq.app.model.ResearchProject;

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
	
	public static String getRecordHash(ResearchProject rp) {
		String recordString = (null != rp.getId() ? rp.getId() : "") 
				+ (null != rp.getName() ? rp.getName() : "" )
				+ (null != rp.getDesc() ? rp.getDesc() : "" )
				+ (null != rp.getDir_to_collab() ? rp.getDir_to_collab() : "")
				+ (null != rp.getStart_date() ? rp.getStart_date() : "") 
				+ (null != rp.getEnd_date() ? rp.getEnd_date() : "") 
				+ (null != rp.getBeta() ? rp.getBeta() : "")
				+ (null != rp.getImage_content_type() ? rp.getImage_content_type() : "");
		System.out.println("$$$$$$ record string : "+recordString);
		return getMD5EncryptedString(recordString);
	}
	
	public static boolean isConnected(Context context)
	{
		ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		System.out.println("$$$$$$$$$$$ Internet connection ? "+ (activeNetwork != null && activeNetwork.isConnected()));
		return activeNetwork != null && activeNetwork.isConnected();
	}

}
