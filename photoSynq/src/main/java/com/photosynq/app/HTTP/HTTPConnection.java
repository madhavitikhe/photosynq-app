package com.photosynq.app.HTTP;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.photosynq.app.utils.CommonUtils;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class HTTPConnection extends AsyncTask<Object, String, String>{
    public static final String SERVER_NOT_ACCESSIBLE = "SERVER_NOT_ACCESSIBLE";
    public static final String SERVER_URL = "http://photosynq.venturit.net/";
    public static final String API_VER = "api/v1/";

	public static final String PHOTOSYNQ_LOGIN_URL = SERVER_URL+API_VER+"sign_in.json";
	public static final String PHOTOSYNQ_PROJECTS_LIST_URL = SERVER_URL+API_VER+"projects.json?";
	public static final String PHOTOSYNQ_PROTOCOLS_LIST_URL = SERVER_URL+API_VER+"protocols.json?";
	public static final String PHOTOSYNQ_MACROS_LIST_URL =SERVER_URL+API_VER+ "macros.json?";
	public static final String PHOTOSYNQ_DATA_URL = SERVER_URL+API_VER+"projects/";
//	public static final String PHOTOSYNQ_LOGIN_URL = SERVER_URL+API_VER+"sign_in.json";
//	public static final String PHOTOSYNQ_PROJECTS_LIST_URL = SERVER_URL+API_VER+"projects.json?";
//	public static final String PHOTOSYNQ_PROTOCOLS_LIST_URL = SERVER_URL+API_VER+"protocols.json?";
//	public static final String PHOTOSYNQ_MACROS_LIST_URL = SERVER_URL+API_VER+"macros.json?";
//	public static final String PHOTOSYNQ_DATA_URL = SERVER_URL+API_VER+"projects/";

	public PhotosynqResponse delegate = null;
	private String username;
	private String password;
	private StringEntity input = null;
	
	public HTTPConnection(String username, String password)
	{
		this.username=username;
		this.password=password;
	}
	
	public HTTPConnection(StringEntity input)
	{
		this.input = input;
	}
	
	public HTTPConnection()
	{

	}
	@Override
	protected void onPreExecute() {
		if(null != username && null != password)
		{
			JSONObject credentials = new JSONObject();
			JSONObject user = new JSONObject();	
			try {
					credentials.put("email", username);
					credentials.put("password", password);
					user.put("user", credentials);
					input = new StringEntity(user.toString());
					input.setContentType("application/json");
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			
		}
		super.onPreExecute();	
	}

	@Override
    protected String doInBackground(Object... uri) {
        HttpClient httpclient = new DefaultHttpClient();
        Context context = (Context)uri[0];
        HttpResponse response = null;
        HttpPost postRequest = null;
        HttpGet getRequest = null;
        String responseString = null;
        if(!CommonUtils.isConnected(context))
        {
            return SERVER_NOT_ACCESSIBLE;
        }
        Log.d("PHOTOSYNQ-HTTPConnection", "in async task");
        try {
        	Log.d("PHOTOSYNQ-HTTPConnection", "$$$$ URI"+uri[1]);
        	if("POST".equals((String) uri[2]) )
        	{
        		postRequest = new HttpPost((String)uri[1]);
        		if(null!=input)
            	{
            		postRequest.setEntity(input);
            	}
        		Log.d("PHOTOSYNQ-HTTPConnection", "$$$$ Executing POST request");
        		response = httpclient.execute(postRequest);
        	}else if ("GET".equals((String) uri[2]) )
        	{
        		getRequest = new HttpGet((String)uri[1]);
        		Log.d("PHOTOSYNQ-HTTPConnection", "$$$$ Executing GET request");
        		response = httpclient.execute(getRequest);
        	}

        	if(null!=response)
        	{
	            StatusLine statusLine = response.getStatusLine();
	            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
	                ByteArrayOutputStream out = new ByteArrayOutputStream();
	                response.getEntity().writeTo(out);
	                out.close();
	                responseString = out.toString();
	            } else{
	                //Closes the connection.
	                response.getEntity().getContent().close();
	                throw new IOException(statusLine.getReasonPhrase());
	            }
        	}
        	
        } catch (ClientProtocolException e) {
            //TODO Handle problems..
        } catch (IOException e) {
            //TODO Handle problems..
        }
        return responseString;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        //Do anything with response..
        if(null!=delegate)
        {
        	delegate.onResponseReceived(result);
        }
        if (null == result)
        {
        	Log.d("PHOTOSYNQ-HTTPConnection","No results returned");
        }
    }
}
