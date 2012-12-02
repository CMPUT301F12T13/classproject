package ca.cmput301.team13.taskman.model;

import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import ca.cmput301.team13.taskman.TaskMan;

public class CrowdSourcerObject {
	
	public static enum entityType {
		TASK("TSK"),
		FULFILLMENT("FUL"),
		REQUIREMENT("REQ");
		
		private final String value;
		private entityType(final String value) {
			this.value = value;
		}
		public String toString() {
			return value;
		}
	}
	
	//The ID of the entity
	private String id;
	//The entity type (TASK or FULFILLMENT)
	//This is stored in the "summary" field
	private entityType type; 
	//The actual Task or Fulfillment object, serialized when output
	private Task task;
	private Fulfillment fulfillment;
	private Requirement requirement;
	
	//NOTE: The "description" field here is not used; that is to be stored 
	//within the Fulfillments and Tasks
	
	/**
	 * Initializes a CrowdSourcerObject from a BackedObject; used for SENDING CrowdSourcerObjects
	 * @param object
	 */
	public CrowdSourcerObject(BackedObject object) {
		setContent(object);
	}
	
	/**
	 * Initializes a CrowdSourcerObject from a JSON object; used for RECEIVING CrowdSourcerObjects
	 * 		NOTE: This is compatible with responses from ONLY the following CrowdSourcer actions:
	 * 				- post
	 * 				- update
	 * 				- get
	 * @param json		The JSONObject containing the CrowdSourcer response data
	 * @throws JSONException 
	 */
	public CrowdSourcerObject(JSONObject json) throws JSONException {
		fromJSON(json);
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
		}else if(objectClass.equals(Requirement.class)) {
			if(type == entityType.REQUIREMENT)
				return (T)requirement;
			else
				throw new RuntimeException("Getting a Requirement from a non-requirement CrowdSourcerObject");
		}else if(objectClass.equals(BackedObject.class)) {
			switch(type) {
				case TASK:
					return (T)task;
				case FULFILLMENT:
					return (T)fulfillment;
				case REQUIREMENT:
					return (T)requirement;
			}
			return null;
		}else { 
			return null;
		}
	}
	
	/**
	 * Converts this object to JSON with the following format:
	 * 		{
	 * 			summary: [%% TASK | FULFILLMENT %%],
	 * 			content: {
	 * 				data: [%% serialized BackedObject %%],
	 * 				id: [%% id of the BackedObject from the LocalRepository; -1 if none %%]
	 * 				type: [%% an ordinal CrowdSourcer.entityType type %%]
	 * 			}
	 * 		}
	 * @return
	 */
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		JSONObject serializedData = new JSONObject();
		String id = null;
		long lastModifiedDate = -1;
		int typeOrdinal = -1;
		switch(type) {
			case TASK:
				if(task != null) {
					serializedData = task.toJSON();
					id = task.getId();
					lastModifiedDate = task.getLastModifiedDate().getTime();
					typeOrdinal = entityType.TASK.ordinal();
				}
			break;
			case FULFILLMENT:
				if(fulfillment != null) {
					serializedData = fulfillment.toJSON();
					id = fulfillment.getId();
					lastModifiedDate = fulfillment.getLastModifiedDate().getTime();
					typeOrdinal = entityType.FULFILLMENT.ordinal();
				}
			break;
			case REQUIREMENT:
				if(requirement != null) {
					serializedData = requirement.toJSON();
					id = requirement.getId();
					lastModifiedDate = requirement.getLastModifiedDate().getTime();
					typeOrdinal = entityType.REQUIREMENT.ordinal();
				}
			break;
		}
		try {
			json.put("type", typeOrdinal);
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
	private void fromJSON(JSONObject json) throws JSONException{
		JSONObject content = json.getJSONObject("content");
		//Get the entityType
		type = entityType.values()[content.getInt("type")];
		JSONObject data = content.getJSONObject("data");
		//Populate type-specific fields
		switch(type) {
			case TASK:
				Task t = new Task(
					data.getString("id"), 
					new Date(data.getLong("created")),
					new Date(data.getLong("lastModified")), 
					new User(data.getString("creator")), 
					data.getString("title"), 
					data.getString("description"), 
					data.getInt("reqCount"), 
					TaskMan.getInstance().getRepository()
				);
				t.setIsLocal(false);
				t.setWebID(json.getString("id"));
				setContent(t);
			break;
			case FULFILLMENT:
				//Get generic fields
				Requirement.contentType contentType = Requirement.contentType.values()[data.getInt("contentType")];
				String id = data.getString("id");
				Date created = new Date(data.getLong("created"));
				Date lastModified = new Date(data.getLong("lastModified"));
				User user = new User(data.getString("creator"));
				VirtualRepository vr = TaskMan.getInstance().getRepository();
				Fulfillment f = null;
				//Get the Fulfillment data
				switch(contentType) {
					case text:
						//Create the Fulfillment
						f = new Fulfillment(
							id, 
							created, 
							lastModified, 
							data.getString("data"), 
							user,
							vr
						);
					break;
					case audio:
						//Convert the JSON int[] into short[]
						JSONArray audioByteArray = data.getJSONArray("data");
						short[] audioBytes = new short[audioByteArray.length()];
						for(int i=0; i<audioByteArray.length(); i++) {
							audioBytes[i] = (short)audioByteArray.getInt(i);
						}
						//Create the Fulfillment
						f = new Fulfillment(
							id,
							created,
							lastModified,
							audioBytes,
							user,
							vr
						);
					break;
					case image:
						//Create the JSON int[] into Bitmap
						JSONArray imageByteArray = data.getJSONArray("data");
						byte[] imageBytes = new byte[imageByteArray.length()];
						for(int i=0; i<imageByteArray.length(); i++) {
							imageBytes[i] = (byte)imageByteArray.getInt(i);
						}
						Bitmap image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
						//Create the Fulfillment
						f = new Fulfillment(
							id,
							created,
							lastModified,
							image,
							user,
							vr
						);
				}
				//Actually set this object's fulfillment
				f.setIsLocal(false);
				f.setParentId(data.getString("parentId"));
				f.setParentWebID(data.getString("parentWebID"));
				f.setWebID(json.getString("id"));
				setContent(f);
			break;
			case REQUIREMENT:
				//Create the Requirement
				Requirement r = new Requirement(
					data.getString("id"), 
					new Date(data.getInt("created")), 
					new Date(data.getInt("lastModified")), 
					new User(data.getString("creator")), 
					data.getString("description"), 
					Requirement.contentType.values()[data.getInt("contentType")], 
					data.getInt("fulfillmentCount"), 
					TaskMan.getInstance().getRepository()
				);
				r.setIsLocal(false);
				r.setParentId(data.getString("parentId"));
				r.setParentWebID(data.getString("parentWebID"));
				r.setWebID(json.getString("id"));
				setContent(r);
			break;
		}
	}
	
	public entityType getType() {
		return type;
	}
	
	public String getId() {
		return id;
	}
	
	/**
	 * Given a Task or Fulfillment, populates all CrowdSourcerObject fields properly
	 * @param content		The Task or Fulfillment object to wrap by CrowdSourcerObject
	 */
	public void setContent(BackedObject content) {
		if(content instanceof Task) {
			this.type = entityType.TASK;
			this.task = (Task)content;
		}else if(content instanceof Fulfillment) {
			this.type = entityType.FULFILLMENT;
			this.fulfillment = (Fulfillment)content;
		}else if(content instanceof Requirement) {
			this.type = entityType.REQUIREMENT;
			this.requirement = (Requirement)content;
		}
		this.id = content.getId();
	}
	
}
