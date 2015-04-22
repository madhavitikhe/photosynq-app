package com.photosynq.app.http;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.photosynq.app.MainActivity;
import com.photosynq.app.http.PhotosynqResponse;
import com.photosynq.app.response.UpdateProject;
import com.photosynq.app.utils.CommonUtils;
import com.photosynq.app.utils.Constants;
import com.photosynq.app.utils.PrefUtils;

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

public class HTTPConnection extends AsyncTask<Object, Object, String>{
    private String username;
	private String password;
	private StringEntity input = null;
    public PhotosynqResponse delegate = null;

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
        String email;
        String authToken;
        if(!CommonUtils.isConnected(context))
        {
            return Constants.SERVER_NOT_ACCESSIBLE;
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
            }else if ("GET".equals((String) uri[2]) ) {
                getRequest = new HttpGet((String) uri[1]);
                Log.d("PHOTOSYNQ-HTTPConnection", "$$$$ Executing GET request");
                response = httpclient.execute(getRequest);
            }
            Log.d("PHOTOSYNQ-HTTPConnection", "in async task");

            if (null != response) {
                try {
                    StatusLine statusLine = response.getStatusLine();
                    if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        response.getEntity().writeTo(out);
                        out.close();
                        responseString = out.toString();
                        JSONObject resultJsonObject = new JSONObject(responseString);
                        if (resultJsonObject.has("projects")) {
                            int currentPage = Integer.parseInt(resultJsonObject.getString("page"));
                            int totalPages = Integer.parseInt(resultJsonObject.getString("total_pages"));

                            authToken = PrefUtils.getFromPrefs(context, PrefUtils.PREFS_AUTH_TOKEN_KEY, PrefUtils.PREFS_DEFAULT_VAL);
                            email = PrefUtils.getFromPrefs(context, PrefUtils.PREFS_LOGIN_USERNAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);

                            if (currentPage <= totalPages) {
                                String strProjectListURI = Constants.PHOTOSYNQ_PROJECTS_LIST_URL
                                        + "all=%d&page=%d&user_email=%s&user_token=%s";
                                UpdateProject updateProject = new UpdateProject((MainActivity) context);
                                HTTPConnection httpConnection = new HTTPConnection();
                                httpConnection.delegate = updateProject;
                                httpConnection.execute(context, String.format(strProjectListURI, 1, currentPage + 1, email, authToken), "GET");
                            }
                        }
                    } else {
                        //Closes the connection.
                        response.getEntity().getContent().close();
                        throw new IOException(statusLine.getReasonPhrase());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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
