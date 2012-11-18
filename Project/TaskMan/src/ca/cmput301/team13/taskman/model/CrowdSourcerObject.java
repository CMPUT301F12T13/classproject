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
		TASK,
		FULFILLMENT,
		REQUIREMENT
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
		JSONObject serializedData = new JSONObject();
		int id = -1;
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
				if(fulfillment != null)
					serializedData = fulfillment.toJSON();
					id = fulfillment.getId();
					lastModifiedDate = fulfillment.getLastModifiedDate().getTime();
					typeOrdinal = entityType.FULFILLMENT.ordinal();
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
	public void fromJSON(JSONObject json) {
		try {
			JSONObject content = json.getJSONObject("content");
			try {
				//Get the entityType
				type = entityType.values()[content.getInt("type")];
				JSONObject data = content.getJSONObject("data");
				//Populate type-specific fields
				switch(type) {
					case TASK:
						Task t = new Task(
							data.getInt("id"), 
							new Date(data.getLong("created")),
							new Date(data.getLong("lastModified")), 
							new User(data.getString("creator")), 
							data.getString("title"), 
							data.getString("description"), 
							data.getInt("reqCount"), 
							TaskMan.getInstance().getRepository()
						);
						setContent(t);
					break;
					case FULFILLMENT:
						Requirement.contentType contentType = Requirement.contentType.values()[data.getInt("contentType")];
						int id = data.getInt("id");
						Date created = new Date(data.getLong("created"));
						Date lastModified = new Date(data.getLong("lastModified"));
						User user = TaskMan.getInstance().getUser();
						VirtualRepository vr = TaskMan.getInstance().getRepository();
						Fulfillment f = null;
						//Get the Fulfillment data
						switch(contentType) {
							case text:
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
								JSONArray audioByteArray = data.getJSONArray("data");
								short[] audioBytes = new short[audioByteArray.length()];
								//Re-generate the short[] from its JSONArray representation
								for(int i=0; i<audioByteArray.length(); i++) {
									audioBytes[i] = (short)audioByteArray.getInt(i);
								}
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
								JSONArray imageByteArray = data.getJSONArray("data");
								byte[] imageBytes = new byte[imageByteArray.length()];
								//Re-generate the short[] from its JSONArray representation
								for(int i=0; i<imageByteArray.length(); i++) {
									imageBytes[i] = (byte)imageByteArray.getInt(i);
								}
								Bitmap image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
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
						setContent(f);
					break;
				}
			} catch (JSONException e) {
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
			this.id = fulfillment.getId();
		}
	}
	
}
