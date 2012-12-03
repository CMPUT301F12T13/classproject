/*
 * This file is part of TaskMan
 *
 * Copyright (C) 2012 Jed Barlow, Mark Galloway, Taylor Lloyd, Braeden Petruk
 *
 * TaskMan is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TaskMan is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with TaskMan.  If not, see <http://www.gnu.org/licenses/>.
 */

package ca.cmput301.team13.taskman.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;


public class WebRepository {
	
	private enum requestType {
		GET,
		POST
	};
	
	private Queue<Request> requestQueue = new ConcurrentLinkedQueue<Request>();
	public static final String REPO_URL = "http://crowdsourcer.softwareprocess.es/F12/CMPUT301F12T13/";
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd kk:mm:ss:SSS Z");
	private VirtualRepository vr;
	
	public WebRepository(VirtualRepository vr) {
        this.vr = vr;
    }
	
	/**
	 * Invokes pushObject(BackedObject, boolean) with the second parameter as true
	 * @see pushObject(BackedObject, boolean)
	 */
	public void pushObject(BackedObject o, final WebActionCallback callback, final Activity context) {
		pushObject(o, true, callback, context);
	}
	
	/**
	 * Create or update an object in the CrowdSourcer database
	 * 		*NOTE: This method runs asynchronously
	 * @param o			The BackedObject to transfer to CrowdSourcer
	 * @param update	Whether existing objects should be updated
	 */
	public void pushObject(BackedObject o, boolean update, final WebActionCallback callback, final Activity context) {
		if(o.getIsLocal()) return; //Don't upload local objects
		
		CrowdSourcerObject co = new CrowdSourcerObject(o);
		RequestArgument[] action;
		final VirtualRepository vr = this.vr;
		final boolean doUpdate = update;
		
		//If this object doesn't have a webID, then it is not in CrowdSourcer and needs to be posted
		if(o.getWebID() == null || o.getWebID().length() == 0) {
			action = new RequestArgument[] {
				new RequestArgument("action", "post")
			};
		//Otherwise the object *is* in CrowdSourcer and needs to be updated
		} else {
			//If we need to update the object, update it
			if(doUpdate) {
				action = new RequestArgument[] {
						new RequestArgument("action", "update"),
						new RequestArgument("id", o.getWebID())
				}; 
			//Otherwise just get the data that needs to be propagated to its children
			} else {
				action = new RequestArgument[] {
						new RequestArgument("action", "get"),
						new RequestArgument("id", o.getWebID())
				};
			}
		}
		
		//Create a new Request and add it to the queue
		requestQueue.add(new Request(
			REPO_URL,
			action,
			new RequestArgument[]{
				new RequestArgument("content", co.toJSON().toString()),
				new RequestArgument("summary", dateFormat.format(co.getContent(BackedObject.class).getLastModifiedDate())),
				new RequestArgument("id", o.getWebID())
			},
			new RequestCallback() {
				public void run(CrowdSourcerObject co) {
					BackedObject bo = co.getContent(BackedObject.class);
					//Update the local object with the webID returned from CrowdSourcer
					if(bo instanceof Task) {
						Task t = vr.getTask(bo.getId());
						if(t != null) {
							t.setWebID(bo.getWebID());
							t.saveChanges(false);
							/*//Once the Task has been uploaded and updated, add its Requirements
							for(int i=0; i<t.getRequirementCount(); i++) {
								Requirement r = t.getRequirement(i);
								r.setParentId(t.getId());
								r.setParentWebID(t.getWebID());
								pushObject(r, doUpdate, null, context);
							}*/
						}
					} else if(bo instanceof Fulfillment) {
						Fulfillment f = vr.getFulfillment(bo.getId());
						if(f != null) {
							f.setWebID(bo.getWebID());
							f.saveChanges(false);
						}
					} else if(bo instanceof Requirement) {
						Requirement r = vr.getRequirement(bo.getId());
						if(r != null) {
							r.setWebID(bo.getWebID());
							r.saveChanges(false);
							//Once the Requirement has been uploaded and updated, add its Fulfillments
							for(int i=0; i<r.getFullfillmentCount(); i++) {
								Fulfillment f = r.getFulfillment(i);
								f.setParentId(r.getId());
								f.setParentWebID(r.getWebID());
								f.saveChanges(false);
								pushObject(f, doUpdate, null, context);
							}
						}
					}
					invokeActionCallback(callback, true, "The object was successfully added to CrowdSourcer.", context);
				}
			}
		));
		
		new Thread(requestHandler).start();
	}
	
	/**
	 * Loads a Task from CrowdSourcer into the local repository
	 * 		*NOTE: This method runs asynchronously
	 * @param taskId	The webID of the task to load
	 */
	public void loadTask(String taskId) {
		
		requestQueue.add(new Request(
			REPO_URL,
			new RequestArgument[]{
				new RequestArgument("action", "get"),
				new RequestArgument("id", taskId)
			},
			new RequestCallback() {
				public void run(CrowdSourcerObject co) {
					BackedObject bo = co.getContent(BackedObject.class);
					//If a Task was returned and doesn't already exist, add it
					if(bo != null && bo instanceof Task) {
						//If it isn't already there, add the task into the LocalRepository
						if(vr.getTask(bo.getId()) == null) {
							vr.createTask((Task)bo);
						}						
					} else if(bo != null) {
						throw new RuntimeException("Treating " + bo.getClass().getName() + " as Task.");
					} else {
						throw new RuntimeException("The task could not be fetched from CrowdSourcer.");
					}
				}
			}
		));
		
		new Thread(requestHandler).start();
		
	}
	
	/**
	 * Pull all objects from CrowdSourcer that have been modified more recently than local versions
	 * 		*NOTE: This method runs asynchronously
	 */
	public void pullChanges(final WebActionCallback callback, final Activity context) {
			//Objects need to be updated in the order { Task -> Requirement -> Fulfillment } to ensure that
			//parent objects exist before their children are updated
			final List<BackedObject> updatedObjects = new ArrayList<BackedObject>();
			
			//Go through the list of all objects in CrowdSourcer and change things that are newer than local versions
			requestQueue.add(new Request(
				REPO_URL,
				new RequestArgument[]{
					new RequestArgument("action", "list"),
				},
				new RequestCallback() {
					public void run(JSONArray ja) {
						try {
							//Traverse the returned list and pull changes as necessary
							for(int i=0; i<ja.length(); i++) {
								JSONObject currentObject = ja.getJSONObject(i);
								if(currentObject.has("summary")) {
									Date lastModifiedDate = dateFormat.parse(currentObject.getString("summary"));
									//If the CrowdSourcer version is newer than the local version, it needs to be pulled
									if(lastModifiedDate.after(vr.getNewestLocalModification())) {
										//Add this object to the update queue
										updatedObjects.add(getObject(currentObject.getString("id"), BackedObject.class));
									}
								}
							}
							
							//Do all of the updates
							Collections.sort(updatedObjects);
							Iterator<BackedObject> itr = updatedObjects.iterator();
							while (itr.hasNext()) {
								pullObject(itr.next());
							}
							invokeActionCallback(callback, true, "Changes were successfully pulled from CrowdSourcer.", context);
						} catch (JSONException e) {
							invokeActionCallback(callback, false, "An invalid object list was returned from pullChanges.", context);
						} catch (ParseException e) {
							invokeActionCallback(callback, false, "An invalid date string was stored with the list object.", context);
						} /*catch (Exception e) {
							//PullObject failed
							System.out.println("EXCEPTION!: " + e.getMessage());
							invokeActionCallback(callback, false, e.getMessage());
						}*/
					}
				}
			));
				
			new Thread(requestHandler).start();
	}
	
	/**
	 * Pull the object into the local repository by adding it if it doesn't
	 * exist, or updating it if it does exist
	 * 		*NOTE: 	If this BackedObject is a Requirement or Fulfillment, its parent
	 * 				MUST exist locally before invoking this method.
	 * @param bo
	 * @throws Exception 
	 */
	public void pullObject(BackedObject bo) {
		//Update or add objects as necessary
		if(bo instanceof Task) {
			//Add the task if necessary
			if(vr.getTask(bo.getId()) == null) {
				vr.createTask((Task)bo);
			//or update the Task if it exists
			} else {
				vr.getTask(bo.getId()).loadFromTask((Task)bo);
			}
		} else if(bo instanceof Requirement) {
			//Add the Requirement if necessary:
				//Get the Requirement's Task with parentId
				//Add the Requirement
			if(vr.getRequirement(bo.getId()) == null) {
				Task parentTask = vr.getTask(bo.getParentId());
				parentTask.delaySaves(true);
				Requirement newRequirement = vr.addRequirementToTask(bo.getCreator(), parentTask, ((Requirement)bo).getContentType(), bo.getId());
				newRequirement.loadFromRequirement((Requirement)bo);
				parentTask.delaySaves(false, false);
			//or update the Requirement if it exists
			} else {
				vr.getRequirement(bo.getId()).loadFromRequirement((Requirement)bo);
			}
		} else if(bo instanceof Fulfillment) {
			//Add the Fulfillment if necessary:
				//Get the Fulfillment's Requirement with parentId
				//Add the Fulfillment
			if(vr.getFulfillment(bo.getId()) == null) {
				Requirement parentRequirement = vr.getRequirement(bo.getParentId());
				parentRequirement.delaySaves(true);
				Fulfillment newFulfillment = vr.addFulfillmentToRequirement(bo.getCreator(), parentRequirement, bo.getId());
				newFulfillment.loadFromFulfillment((Fulfillment)bo);
				parentRequirement.delaySaves(false, false);
			//or update the Fulfillment if it exists
			} else {
				vr.getFulfillment(bo.getId()).loadFromFulfillment((Fulfillment)bo);
			}
		}
	}
	
	/**
	 * Get a CrowdSourcerObject from CrowdSourcer
	 * @param id				The ID of the CrowdSourcerObject to fetch
	 * @return		The requested BackedObject (null if none is found)
	 */
	public CrowdSourcerObject getObject(String id) {
		JSONObject taskJSON = getJSON(REPO_URL, new RequestArgument[]{
			new RequestArgument("action", "get"),
			new RequestArgument("id", id)
		});
		try {
			CrowdSourcerObject co = new CrowdSourcerObject(taskJSON);
			return co;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Get a BackedObject from CrowdSourcer
	 * @param id				The ID of the BackedObject to fetch
	 * @param objectClass		The type of BackedObject being fetched
	 * @return		The requested BackedObject (null if none is found)
	 */
	public <T extends BackedObject> T getObject(String id, Class<T> objectClass) {
		return getObject(id).getContent(objectClass);
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
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
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
	
	/**
	 * Invokes the supplied ActionCallback if one was supplied
	 * @param callback		The callback to invoke
	 * @param success		The success parameter to pass into the callback
	 * @param message		The message parameter to pass into the callback
	 */
	private void invokeActionCallback(WebActionCallback callback, boolean success, String message, Activity context) {
		if(callback != null && context != null) {
			callback.success = success;
			callback.message = message;
			context.runOnUiThread(callback);
		} else if(callback != null && context == null){
			
		}
	}
	
	/***********************************
	 * Asynchronous Request Mechanisms *
	 **********************************/
	
	/**
	 * Runs queued Requests from requestQueue on a separate thread
	 */
	private Runnable requestHandler = new Runnable() {

		public void run() {
			//If there are Requests waiting to be served, execute them
			while(!requestQueue.isEmpty()) {
				String result = null;
				Request r = requestQueue.remove();
				CrowdSourcerObject co;
				
				if(r.callback != null) {
					//Get the Request result
					switch(r.type) {
						case GET:
							result = getContent(r.uri);
						break;
						case POST:
							result = postContent(r.uri, r.postArguments);
						break;
					}
					//If a result was fetched, turn it into a CrowdSourcerObject and run its Callback
					if(result != null) {
						try {
							//Treat it like a CrowdSourcerObject; this will succeed if it is
							co = new CrowdSourcerObject(new JSONObject(result));
							r.callback.run(co);
						} catch (JSONException e) {
							try {
								//Treat it like it's an object listing
								JSONArray jsonArray = new JSONArray(result);
								r.callback.run(jsonArray);
							} catch (JSONException e2) {
								//We didn't get anything valid, so invoke an "error" callback
								r.callback.run(false);
							}
							co = null;
						}
					}
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
		public void run(CrowdSourcerObject co) { } //This is invoked when a CrowdSourcerObject is returned from the API
		public void run(JSONArray ja) { } //This is invoked when an object listing is returned from the API
		public void run(boolean success) { } //This is invoked when a call errors
	}
	
	/**
	 * A callback that is passed into asynchronous WebRepository methods to allow the caller
	 * to be notified when the method has completed, and to get the status of the resulting
	 * request
	 */
	public static class WebActionCallback implements Runnable{
		public boolean success;
		public String message;
		public void run() { }
	}

	/**
	 * A web request consisiting of JSON api endpoint, parameters, and a callback for invocation
	 * once the request is complete. 
	 * 
	 * The Request should be initiated by putting it into the requestQueue
	 * and invoking requestHandler.run()
	 */
	private class Request {
		
		private requestType type;
		private String uri;
		private RequestArgument[] postArguments;
		private RequestCallback callback = null;
		
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
