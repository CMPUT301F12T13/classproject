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

package ca.cmput301.team13.taskman.model.storage;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Holds the information associated with a requirement for a {@link Task};
 * aggregated by {@link Task}.
 */
public class Requirement extends BackedObject {

	/**
	 * An enum of desired contentType for requirements.
	 */
    //Content Mask Stuff
    public static enum contentType {
        text,
        image,
        audio,
        video
    };

    //private state variables
    private String description;
    private ArrayList<Fulfillment> fulfillments;
    private contentType desiredContent;

    //lazy-loading variables
    private int fulfillmentCount;
    private boolean loaded = false;

    /**
     * Construct a Requirement with backing in the persistent store.
     * @param id - the id of the requirement
     * @param description - the description of the requirement
     * @param fulfillments - the list of fulfillments of the requirement
     * @param repo - the repository backing this object
     */
    Requirement(String id, Date created, Date lastModified, User creator, String description, contentType desiredContent, ArrayList<Fulfillment> fulfillments, VirtualRepository repo) {
        super(id, created, lastModified, creator, repo);
        this.description = description;
        this.fulfillments = fulfillments;
        this.desiredContent = desiredContent;
        this.loaded = true;
    }

    /**
     * Construct a Requirement with backing in the persistent store.
     * @param id - the id of the requirement
     * @param description - the description of the requirement
     * @param fulfillmentCount - the quantity of fulfillments
     * @param repo - the repository backing this object
     */
    public Requirement(String id, Date created, Date lastModified, User creator, String description, contentType desiredContent, int fulfillmentCount, VirtualRepository repo) {
        super(id, created, lastModified, creator, repo);
        this.description = description;
        this.fulfillmentCount = fulfillmentCount;
        this.desiredContent = desiredContent;
        this.loaded = false;
    }
    
    public JSONObject toJSON() {
    	JSONObject json = new JSONObject();
    	JSONArray fulfillmentArray = new JSONArray(); //Only stores the IDs
    	//Ensure the Fulfillments are loaded before trying to get their IDs
    	if(!loaded) loadFulfillments(); 
    	//Get Fulfillment IDs of this Requiremenr
    	for(int i=0; i<fulfillments.size(); i++) {
    		try {
				fulfillmentArray.put(i, fulfillments.get(i).getId());
			} catch (JSONException e) {
				e.printStackTrace();
			}
    	}
    	try {
    		json.put("id", getId());
    		json.put("parentId", getParentId());
    		json.put("parentWebID", getParentWebID());
    		json.put("description", getDescription());
			json.put("fulfillmentCount", getFullfillmentCount());
			json.put("contentType", this.desiredContent.ordinal());
    		json.put("created", dateFormat.format(getCreatedDate()));
    		json.put("lastModified", dateFormat.format(getLastModifiedDate()));
			json.put("creator", getCreator().toString());
			json.put("fulfillments", fulfillmentArray);
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	return json;
    }
    
    /**
     * Update this Requirement's mutable properties from
     * the provided Requirement
     * @param r
     */
    public boolean loadFromRequirement(Requirement r) {
    	delaySaves(true);
    	setIsLocal(r.getIsLocal());
    	setWebID(r.getWebID());
    	setDescription(r.getDescription());
    	setLastModifiedDate(r.getLastModifiedDate());
    	fulfillmentCount = r.getFullfillmentCount();
    	return delaySaves(false, false);
    }

    /**
     * Returns the description of the Requirement.
     * @return the description of the Requirement
     */
    public String getDescription() {
        return description;
    }

    /**
     * Changes the description, and saves the changes.
     * @param description the new description of the requirement
     * @return success of save
     */
    public boolean setDescription(String description) {
        this.description = description;
        return saveChanges();
    }

    /**
     * Adds a fulfillment, and saves the changes.
     * @param ful the fulfillment to add to the requirement
     * @return success of save
     */
    public boolean addFulfillment(Fulfillment ful) {
        if(!loaded) {
            loadFulfillments();
        }
        fulfillments.add(ful);
        return saveChanges();
    }

    /**
     * Removes a fulfillment, and saves the changes.
     * @param ful the fulfillment to remove from the requirement
     * @return success of both the remove, and the save
     */
    public boolean removeFulfillment(Fulfillment ful) {
        if(!loaded) {
            loadFulfillments();
        }

        boolean success = fulfillments.remove(ful);
        //TODO: ful should probably be destroyed here

        if(success)
            return saveChanges();
        return false;
    }

    /**
     * Returns the number of fulfillments associated with this requirement.
     * @return the number of fulfillments associated with this requirement
     */
    public int getFullfillmentCount() {
        if(!loaded) {
            return fulfillmentCount;
        }
        return fulfillments.size();
    }

    /**
     * Retrieves a {@link Fulfillment} from an index.
     * @param index the index of the desired Fulfillment
     * @return the associated Fulfillment
     */
    public Fulfillment getFulfillment(int index) {
        if(!loaded) {
            loadFulfillments();
        }
        return fulfillments.get(index);
    }

    /**
     * Returns the content type expected by the Requirement.
     * @return the Content Type expected by this Requirement
     */
    public contentType getContentType() {
        return desiredContent;
    }

    private void loadFulfillments() {
        //TODO: Actually ask the repo for our fulfillments
        if(!loaded) {
            fulfillments = repo.getFulfillmentsForRequirement(this);
            loaded = true;
        }
    }
    
    public int getOrderValue() {
    	return 2;
    }

    /**
     * Returns a string representation of the Requirement.
     * @return a string representation of the Requirement
     */
    @Override
    public String toString() {
        return "Req(ID:"+getId()+")";
    }

}
