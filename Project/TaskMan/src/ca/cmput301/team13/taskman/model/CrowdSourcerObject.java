package ca.cmput301.team13.taskman.model;

import java.io.IOException;
import java.io.StreamCorruptedException;

import org.json.JSONException;
import org.json.JSONObject;

import utils.ObjectWriter;

public class CrowdSourcerObject {
	
	public static enum entityType {
		TASK,
		FULFILLMENT
	}
	
	//The ID of the entity
	private int id;
	//The entity type (TASK or FULFILLMENT)
	//This is stored in the "summary" field
	private entityType type; 
	//The actual Task or Fulfillment object, serialized when output
	private Task task;
	private Fulfillment fulfillment;
	
	//NOTE: The "description" field here is not used; that is to be stored 
	//within the Fulfillments and Tasks
	
	public CrowdSourcerObject() { }
	
	public CrowdSourcerObject(Object object) {
		setContent(object);
	}
	
	/**
	 * Gets the content stored within the CrowdSourcerObject
	 * @param objectClass		The class of the stored object (Task or Fulfillment)
	 * @return					The stored object of the specified type
	 */
	@SuppressWarnings("unchecked")
	public <T extends BackedObject> T getContent(Class<T> objectClass) {
		if(objectClass.equals((Task.class))) {
			if(type == entityType.TASK)
				return (T)task;
			else
				throw new RuntimeException("Getting a Task from a non-task CrowdSourcerObjec");
		}else if(objectClass.equals(Fulfillment.class)) {
			if(type == entityType.FULFILLMENT)
				return (T)fulfillment;
			else
				throw new RuntimeException("Getting a Fulfillment from a non-fulfillment CrowdSourcerObjec");
		}else {
			return null;
		}
	}
	
	/**
	 * Converts this object to JSON with the following format:
	 * 		{
	 * 			summary: [%% TASK | FULFILLMENT %%],
	 * 			content: {
	 * 				data: [%% serialized Task or Fulfillment object %%],
	 * 				id: [%% id of the Task or Fulfillment from the LocalRepository; -1 if none %%]
	 * 				type: [%% an ordinal CrowdSourcer.entityType type %%]
	 * 			}
	 * 		}
	 * @return
	 */
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		String serializedData = "";
		int id = -1;
		long lastModifiedDate = -1;
		switch(type) {
			case TASK:
				if(task != null)
					try {
						serializedData = ObjectWriter.objectToString(task);
						id = task.getId();
						lastModifiedDate = task.getLastModifiedDate().getTime();
					} catch (IOException e) {
						e.printStackTrace();
					} 
			break;
			case FULFILLMENT:
				if(fulfillment != null)
					try {
						serializedData = ObjectWriter.objectToString(fulfillment);
						id = fulfillment.getId();
						lastModifiedDate = fulfillment.getLastModifiedDate().getTime();
					} catch (IOException e) {
						e.printStackTrace();
					} 
				break;
		}
		try {
			json.put("type", entityType.TASK.ordinal());
			json.put("id", id);
			json.put("lastModifiedDate", lastModifiedDate);
			json.put("data", serializedData);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}
	
	/**
	 * Populate this CrowdSourcerObject with data from a JSON object returned from CrowdSourcer
	 * 		NOTE: This works with the data returned from the actions:
	 * 			- post
	 * 			- update
	 * 			- get
	 * @param jsonString	The JSON representation
	 */
	public void fromJSON(JSONObject json) {
		String jsonString = json.toString();
		try {
			JSONObject content = json.getJSONObject("content");
			try {
				//Get the entityType
				type = entityType.values()[content.getInt("type")];
				//Populate type-specific fields
				switch(type) {
					case TASK:
						Object o = ObjectWriter.stringToObject(content.getString("data"));
						setContent((Task) ObjectWriter.stringToObject(content.getString("data")));
					break;
					case FULFILLMENT:
						setContent((Fulfillment) ObjectWriter.stringToObject(content.getString("data")));
					break;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (StreamCorruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
	}
	
	public entityType getType() {
		return type;
	}
	
	public void setType(entityType type) {
		this.type = type;
	}
	
	public void setType(String type) {
		setType(entityType.valueOf(type));
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * Given a Task or Fulfillment, populates all CrowdSourcerObject fields properly
	 * @param content		The Task or Fulfillment object to wrap by CrowdSourcerObject
	 */
	public void setContent(Object content) {
		if(content instanceof Task) {
			this.type = entityType.TASK;
			this.task = (Task)content;
			this.id = task.getId();
		}else if(content instanceof Fulfillment) {
			this.type = entityType.FULFILLMENT;
			this.fulfillment = (Fulfillment)content;
			this.id = task.getId();
		}
	}
	
}
