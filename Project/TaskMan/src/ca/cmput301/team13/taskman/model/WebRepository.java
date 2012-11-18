package ca.cmput301.team13.taskman.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

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
	
	private enum requestType {
		GET,
		POST
	};
	
	private Queue<Request> requestQueue = new ConcurrentLinkedQueue<Request>();
	public static final String REPO_URL = "http://crowdsourcer.softwareprocess.es/F12/CMPUT301F12T13/";
	private VirtualRepository vr;
	
	public WebRepository(VirtualRepository vr) {
        this.vr = vr;
    }
	
	/**
	 * Create an object in the CrowdSourcer database
	 * @param o		The BackedObject to transfer to CrowdSourcer
	 */
	public void createObject(BackedObject o) {
		CrowdSourcerObject co = new CrowdSourcerObject(o);
		
		//Create a new Request and add it to the queue
		requestQueue.add(new Request(
			REPO_URL,
			new RequestArgument[]{
				new RequestArgument("action", "post")
			},
			new RequestArgument[]{
				new RequestArgument("summary", co.getType().toString()),
				new RequestArgument("content", co.toJSON().toString())
			}
			//TODO: add callback?
		));
		
		requestHandler.run();
	}
	
	/**
	 * Get a Task from CrowdSourcer
	 * @param id	The ID of the Task to fetch
	 * @return		The requested Task (null if none is found)
	 */
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
	
	/**
	 * Gets a String response from a JSON web service endpoint using GET
	 * @param uri	The URI to request from
	 * @return		The response JSONObject
	 */
	public JSONObject getJSON(String uri) {
		try {
			return new JSONObject(getContent(uri));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return new JSONObject();
	}
	
	/**
	 * Gets a String response from a JSON web service endpoint using GET
	 * @param uri			The URI to request from
	 * @param arguments		A list of GET arguments to pass along to the service
	 * 							*Note: Assumes no GET parameters are included in the provided URI
	 * @return				The response JSONObject
	 */
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
	private String postContent(String uri, RequestArgument[] postParams) {
		HttpClient webSender = new DefaultHttpClient();
		HttpPost postRequest = new HttpPost(uri);
		List<NameValuePair> dataList = new ArrayList<NameValuePair>();
		String line;
		StringBuilder jsonString = new StringBuilder();
		for(int i=0; i<postParams.length; i++) {
			dataList.add(new BasicNameValuePair(postParams[i].getName(), (String)postParams[i].getData()));
		}
		try {
			postRequest.setEntity(new UrlEncodedFormEntity(dataList));
			//Execute the request and return whether it was successful
			HttpResponse response = webSender.execute(postRequest);
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
			
			return jsonString.toString();
			//TODO: Check whether it was successful
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private String postContent(String uri, RequestArgument[] getParams, RequestArgument[] postParams) {
		return postContent(uri + getArgumentString(getParams), postParams);
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
		return getContent(uri + getArgumentString(arguments));
	}
	
	/**
	 * Creates a formatted String from an array of RequestArguments of the form:
	 * 		?argument=value&anotherArgument=anotherValue
	 * @param arguments		The array of RequestArguments
	 * @return
	 */
	private String getArgumentString(RequestArgument[] arguments) {
		StringBuilder getArguments = new StringBuilder();
		int numArguments = arguments.length;
		//Generate a string of arguments
		for(int i=0; i<numArguments; i++) {
			getArguments.append(arguments[i]);
			//If there are more arguments, concatenate them with "&"
			if(i < arguments.length - 1) getArguments.append("&");
		}
		return "?" + getArguments.toString();
	}
	
	/***********************************
	 * Asynchronous Request Mechanisms *
	 **********************************/
	
	private Runnable requestHandler = new Runnable() {

		public void run() {
			//If there are Requests waiting to be served, execute them
			if(!requestQueue.isEmpty()) {
				Request r = requestQueue.remove();
				switch(r.type) {
					case GET:
						CrowdSourcerObject getResult;
						try {
							getResult = new CrowdSourcerObject(getJSON(r.uri));
						} catch(JSONException e) {
							getResult = null;
						}
						if(r.callback != null && getResult != null)
							r.callback.run(getResult);
					break;
					case POST:
						String postResult = postContent(r.uri, r.postArguments);
						if(r.callback != null && postResult != null) {
							CrowdSourcerObject co;
							try {
								co = new CrowdSourcerObject(new JSONObject(postResult));
							//If the JSON is invalid or doesn't comply with CrowdSourcerObject, just pass in null
							} catch (JSONException e) {
								co = null;
							}
							r.callback.run(co);
						}
					break;
				}
			}
		}
		
	};
	
	/**
	 * Default RequestCallback
	 * 		NOTE: Designed to override one or more of the run() methods:
	 * 					- if a GET method is being executed, override the
	 * 					  the CrowdSourcerObject version (passes retrieved data)
	 * 					- if a POST method is being executed, override the
	 * 					  boolean version (passes transmission success) 
	 */
	private class RequestCallback {
		public void run(boolean success) {	}
		public void run(CrowdSourcerObject co) { }
	}
	
	private class Request {
		
		private requestType type;
		private String uri;
		private RequestArgument[] postArguments;
		private RequestCallback callback = null;
		
		public Request(String uri) {
			type = requestType.GET;
			this.uri = uri;
		}
		
		public Request(String uri, RequestCallback callback) {
			this(uri);
			this.callback = callback;
		}
		
		public Request(String uri, RequestArgument[] arguments) {
			type = requestType.GET;
			this.uri = uri + getArgumentString(arguments);
		}
		
		public Request(String uri, RequestArgument[] arguments, RequestCallback callback) {
			this(uri, arguments);
			this.callback = callback;
		}
		
		public Request(String uri, RequestArgument[] getArguments, RequestArgument[] postArguments) {
			type = requestType.POST;
			this.uri = uri + getArgumentString(getArguments);
			this.postArguments = postArguments;
		}
		
		public Request(String uri, RequestArgument[] getArguments, RequestArgument[] postArguments, RequestCallback callback) {
			this(uri, getArguments, postArguments);
			this.callback = callback;
		}
		
	}

}
