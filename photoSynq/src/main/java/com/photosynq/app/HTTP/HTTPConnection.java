package com.photosynq.app.HTTP;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.Macro;
import com.photosynq.app.model.Option;
import com.photosynq.app.model.Protocol;
import com.photosynq.app.model.Question;
import com.photosynq.app.model.ResearchProject;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class HTTPConnection extends AsyncTask<Object, String, String>{
//	public static final String PHOTOSYNQ_LOGIN_URL = "http://photosynq.venturit.net/api/v1/sign_in.json";
//	public static final String PHOTOSYNQ_PROJECTS_LIST_URL = "http://photosynq.venturit.net/api/v1/projects.json?";
//	public static final String PHOTOSYNQ_PROTOCOLS_LIST_URL = "http://photosynq.venturit.net/api/v1/protocols.json?";
//	public static final String PHOTOSYNQ_MACROS_LIST_URL = "http://photosynq.venturit.net/api/v1/macros.json?";
//	public static final String PHOTOSYNQ_DATA_URL = "http://photosynq.venturit.net/api/v1/projects/";
	public static final String PHOTOSYNQ_LOGIN_URL = "http://photosynq.venturit.org/api/v1/sign_in.json";
	public static final String PHOTOSYNQ_PROJECTS_LIST_URL = "http://photosynq.venturit.org/api/v1/projects.json?";
	public static final String PHOTOSYNQ_PROTOCOLS_LIST_URL = "http://photosynq.venturit.org/api/v1/protocols.json?";
	public static final String PHOTOSYNQ_MACROS_LIST_URL = "http://photosynq.venturit.org/api/v1/macros.json?";
	public static final String PHOTOSYNQ_DATA_URL = "http://photosynq.venturit.org/api/v1/projects/";
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
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
        	
//        	if(null!=input)
//        	{
//        		postRequest.setEntity(input);
//        	}
//        	if("POST".equals((String) uri[2]) )
//        	{
//        		Log.d("PHOTOSYNQ-HTTPConnection", "$$$$ Executing POST request");
//        		response = httpclient.execute(postRequest);
//        	}else
//        	{
//        		Log.d("PHOTOSYNQ-HTTPConnection", "$$$$ Executing GET request");
//        		response = httpclient.execute(getRequest);
//        	}
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
        
        if(((String)uri[1]).contains("projects.json"))
        {
        	System.out.println("updating projects . ....");
        	updateProjects(context, responseString);
        }
        if(((String)uri[1]).contains("macros.json"))
        {
        	System.out.println("updating macros . ...."); 
        	updateMacros(context, responseString);
        }
        if(((String)uri[1]).contains("protocols.json"))
        {
        	System.out.println("updating protocols . ...."); 
        	updateProtocols(context, responseString);
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
        if (null != result)
        {
        }
        else 
        {
        	Log.d("PHOTOSYNQ-HTTPConnection","No results returned");
        }
    }
    
    
    private void updateMacros(Context context, String result)
    {
    	DatabaseHelper db = DatabaseHelper.getHelper(context);

		JSONArray jArray;

		if (null != result) {
			try {
				jArray = new JSONArray(result);
				for (int i = 0; i < jArray.length(); i++) {

					JSONObject obj = jArray.getJSONObject(i);
					Macro macro = new Macro(obj.getString("id"),
							obj.getString("name"),
							obj.getString("description"),
							obj.getString("default_x_axis"),
							obj.getString("default_y_axis"),
							obj.getString("javascript_code"),
							"slug");

					//db = new DatabaseHelper(context);
					//db = DatabaseHelper.getHelper(context);
					db.updateMacro(macro);
					//System.out.println("DBCLosing");
					//db.closeDB();
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// Writing macros.js file with all macro functions
		StringBuffer dataString = new StringBuffer();
		//db = new DatabaseHelper(context);
		//db = DatabaseHelper.getHelper(context);
		List<Macro> macros = db.getAllMacros();
		for (Macro macro : macros) {
			dataString.append("function macro_" + macro.getId() + "(json){");
			dataString.append(System.getProperty("line.separator"));
			dataString.append(macro.getJavascriptCode().replaceAll("\\r\\n", System.getProperty("line.separator"))); //replacing ctrl+m characters
			dataString.append(System.getProperty("line.separator") + " }");
			dataString.append(System.getProperty("line.separator"));
			dataString.append(System.getProperty("line.separator"));
		}
		//db.closeDB();
		//System.out.println("###### writing macros :"+dataString);
		System.out.println("###### writing macros :......");
		CommonUtils.writeStringToFile(context, "macros.js",dataString.toString());
	
    }
    
    private void updateProtocols(Context context, String result)
    {
		JSONArray jArray;
		DatabaseHelper db = DatabaseHelper.getHelper(context);
		if (null != result) {
			try {
				jArray = new JSONArray(result);
				for (int i = 0; i < jArray.length(); i++) {

					JSONObject obj = jArray.getJSONObject(i);
					String id = obj.getString("id");
					Protocol protocol = new Protocol(id,
							obj.getString("name"),
							obj.getString("protocol_json2"),
							obj.getString("description"),
							obj.getString("macro_id"), "slug");
					//db = new DatabaseHelper(context);
					//db = DatabaseHelper.getHelper(context);
					db.updateProtocol(protocol);
					//System.out.println("DBCLosing");
					//db.closeDB();
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

    }
    
    private void updateProjects(Context context, String result)
    {
    	DatabaseHelper db = DatabaseHelper.getHelper(context);
		JSONArray jArray;
		
		if(null!= result)
			{
			try {
				jArray = new JSONArray(result);
				for (int i = 0; i < jArray.length(); i++) {
					JSONObject jsonProject = jArray.getJSONObject(i);
					String protocol_ids = jsonProject.getJSONArray("protocols_ids").toString().trim();

					ResearchProject rp = new ResearchProject(
					jsonProject.getString("id"),
					jsonProject.getString("name"),
					jsonProject.getString("description"),
					jsonProject.getString("directions_to_collaborators"),
					jsonProject.getString("start_date"),
					jsonProject.getString("end_date"),
					jsonProject.getString("medium_image_url"),
					jsonProject.getString("beta"),
					protocol_ids.substring(1, protocol_ids.length()-1)); // remove first and last square bracket and store as a comma separated string
					
					JSONArray customFields = jsonProject.getJSONArray("custom_fields");
					//db = new DatabaseHelper(context);
					//db = DatabaseHelper.getHelper(context);
					for (int j = 0; j < customFields.length(); j++) {
						JSONObject jsonQuestion = customFields.getJSONObject(j);
						Question question = new Question(jsonQuestion.getString("id"),
								jsonProject.getString("id"), 
								jsonQuestion.getString("label"),Integer.parseInt(jsonQuestion.getString("value_type")));
						db.updateQuestion(question);
						
						//String options = jsonQuestion.getString("value");
						String[] options = jsonQuestion.getString("value").split(",");
						for (String opt : options) {
							Option option = new Option(jsonQuestion.getString("id"), opt, jsonProject.getString("id"));
							db.updateOption(option);
						}
						
					}
					
					
					
					
					db.updateResearchProject(rp);
					//System.out.println("DBCLosing");
					//db.closeDB();
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
    }
}
