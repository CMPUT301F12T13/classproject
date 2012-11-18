package ca.cmput301.team13.taskman.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;


public class WebRepository {
	
	public static final String REPO_URL = "http://crowdsourcer.softwareprocess.es/F12/CMPUT301F12T13/";
	private VirtualRepository vr;
	
	public WebRepository(VirtualRepository vr) {
        this.vr = vr;
    }
	
	public void createTask(Task task) {
		CrowdSourcerObject co = new CrowdSourcerObject(task);
		
		//Things stored in the "content" field in CrowdSourcer
		//TODO: Perhaps store a text version of DateModified here for synchronization purposes?
		//TODO: Actually send the data to CrowdSourcer
		postContent(
			REPO_URL,
			//GET params
			new RequestArgument[]{
					new RequestArgument("action", "post")
			},
			//POST params
			new RequestArgument[]{
					new RequestArgument("summary", CrowdSourcerObject.entityType.TASK.toString()),
					new RequestArgument("content", co.toJSON().toString())
			}
		);
	}
	
	public void createFulfillment(Fulfillment f) {
		CrowdSourcerObject co = new CrowdSourcerObject(f);
		
		postContent(
				REPO_URL,
				new RequestArgument[]{
					new RequestArgument("action", "post")
				},
				new RequestArgument[]{
					new RequestArgument("summary", CrowdSourcerObject.entityType.FULFILLMENT.toString()),
					new RequestArgument("content", co.toJSON().toString())
				}
		);
	}
	
	public Task getTask(String id) {
		JSONObject taskJSON = getJSON(REPO_URL, new RequestArgument[]{new RequestArgument("id", id)});
		try {
			CrowdSourcerObject taskCS = (CrowdSourcerObject)taskJSON.get("data");
			return taskCS.getContent(Task.class);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public JSONObject getJSON(String uri) {
		try {
			return new JSONObject(getContent(uri));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return new JSONObject();
	}
	
	public JSONObject getJSON(String uri, RequestArgument[] arguments) {
		try {
			return new JSONObject(getContent(uri, arguments));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return new JSONObject();
	}
	
	/**
	 * Executes a POST request to the provided URI using the specified arguments
	 * 		NOTE: All params (GET and POST) must be of type String
	 * @param uri			The URI to send the request to
	 * @param getParams		The GET params
	 * @param postParams	The data to send through the POST body
	 * @return				Whether the transmission was successful
	 */
	private boolean postContent(String uri, RequestArgument[] getParams, RequestArgument[] postParams) {
		System.out.println("URI!!!:" + uri);
		HttpClient webSender = new DefaultHttpClient();
		HttpPost postRequest = new HttpPost(uri + "?" + getArgumentString(getParams));
		List<NameValuePair> dataList = new ArrayList<NameValuePair>();
		for(int i=0; i<postParams.length; i++) {
			dataList.add(new BasicNameValuePair(postParams[i].getName(), (String)postParams[i].getData()));
		}
		try {
			postRequest.setEntity(new UrlEncodedFormEntity(dataList));
			webSender.execute(postRequest);
			//TODO: Check whether it was successful
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Builds a JSON string from the response provided by the specified endpoint
	 * @param uri
	 * @return
	 */
	private String getContent(String uri) {
		String line;
		StringBuilder jsonString = new StringBuilder();
		HttpClient webReader = new DefaultHttpClient();
		HttpGet getRequest = new HttpGet(uri);
		
		try {
			HttpResponse response = webReader.execute(getRequest);
			int statusCode = response.getStatusLine().getStatusCode();
			//If the service was ready and we got a response, collect it
			if(statusCode == 200) {
				InputStream responseContent = response.getEntity().getContent();
				BufferedReader contentReader = new BufferedReader(new InputStreamReader(responseContent));
				//Build the JSON string from the response stream
				while((line = contentReader.readLine()) != null) {
					jsonString.append(line);
				}
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return jsonString.toString();
	}
	
	/**
	 * Builds a JSON string from the response provided by the specified endpoint and argument list
	 * 		- Each Object in the arguments list should have a toString() method that prints out the
	 *        desired identifier that should be present in the request URL.
	 * @param uri			The endpoint to query
	 * @param arguments		The argument list
	 * @return				The JSON response from the server, if any. Empty string otherwise.
	 */
	private String getContent(String uri, RequestArgument[] arguments) {
		return getContent(getArgumentString(arguments));
	}
	
	private String getArgumentString(RequestArgument[] arguments) {
		StringBuilder getArguments = new StringBuilder();
		int numArguments = arguments.length;
		//Generate a string of arguments
		for(int i=0; i<numArguments; i++) {
			getArguments.append(arguments[i]);
			//If there are more arguments, concatenate them with "&"
			if(i < arguments.length - 1) getArguments.append("&");
		}
		return getArguments.toString();
	}

}
