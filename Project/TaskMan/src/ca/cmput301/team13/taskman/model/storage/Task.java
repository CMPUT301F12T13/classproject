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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcelable;
import android.util.Log;

/**
 * Holds the information associated with a task;
 * aggregates {@link Requirement}.
 */
public class Task extends BackedObject implements Parcelable, Serializable {

	private static final long serialVersionUID = -4003796765447519712L;
	private String title;
    private String description;
    transient private ArrayList<Requirement> requirements;
    //Implementation of requirements handoff
    boolean reqsLoaded = false;
    int reqCount = 0;

    /**
     * Construct a Task with backing in a persistent store.
     * @param id - the ID of the Task
     * @param title - the title of the task
     * @param description - the description of the task
     * @param requirements - A List of requirements dependent on the task
     * @param repo - the repository in which we are stored
     */
    Task(String id, Date created, Date lastModified, User creator, String title, String description, ArrayList<Requirement> requirements, VirtualRepository repo) {
        super(id, created, lastModified, creator, repo);
        this.title = title;
        this.description = description;
        reqsLoaded = true;
        this.requirements = requirements;
    }

    /**
     * Construct a Task with backing in a persistent store,
     * without preloaded requirements.
     * @param id - the ID of the Task
     * @param title - the title of the task
     * @param description - the description of the task
     * @param reqCount - The number of requirements this task has
     * @param repo - the repository in which we are stored
     */
    Task(String id, Date created, Date lastModified, User creator, String title, String description, int reqCount, VirtualRepository repo) {
        super(id, created, lastModified, creator, repo);
        this.title = title;
        this.description = description;
        reqsLoaded = false;
        this.reqCount = reqCount;
    }
    
    /**
     * Required to make Tasks serializable. The created Task object will not be used as an independent object; it will instead be used to store intermediate data
     * until a proper object can be created in, or fetched from, the LocalRepository
     */
    public Task() { }
    
    public JSONObject toJSON() {
    	JSONObject json = new JSONObject();
    	try {
			json.put("id", getId());
			json.put("title", getTitle());
			json.put("description", getDescription());
    		json.put("created", dateFormat.format(getCreatedDate()));
    		json.put("lastModified", dateFormat.format(getLastModifiedDate()));
			json.put("creator", getCreator().toString());
			json.put("reqCount", getRequirementCount());
			return json;
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	return null;
    }
    
    /**
     * Update this Task's mutable properties from the provided
     * Task object
     * @param t
     */
    public void loadFromTask(Task t) {
    	delaySaves(true);
    	setWebID(t.getWebID());
    	setLastModifiedDate(t.getLastModifiedDate());
    	setTitle(t.getTitle());
    	setDescription(t.getDescription());
    	reqCount = t.reqCount;
    	setIsLocal(t.getIsLocal());
    	delaySaves(false, false);
    }

    private void loadRequirements() {
        Log.w(toString(),"Performing lazy-load");
        if(!reqsLoaded) {
            requirements = repo.getRequirementsForTask(this);
            reqsLoaded = true;
        }
    }

    /**
     * Returns the title of the task.
     * @return the title of the task
     */
    public String getTitle() {
        return title;
    }

    /**
     * Changes the title, and saves the changes.
     * @param title the new title of the task
     * @return success of save
     */
    public boolean setTitle(String title) {
        this.title = title;
        return saveChanges();
    }

    /**
     * Returns the description of the task.
     * @return the description of the task
     */
    public String getDescription() {
        return description;
    }

    /**
     * Changes the description, and saves the changes.
     * @param description the new description of the task
     * @return success of save
     */
    public boolean setDescription(String description) {
        this.description = description;
        return saveChanges();
    }

    /**
     * Adds a requirement, and saves the changes.
     * @param req the requirement to add to the task
     * @return success of save
     */
    public boolean addRequirement(Requirement req) {
        if(!reqsLoaded)
            loadRequirements();

        requirements.add(req);
        Log.w(toString(),"Added Requirement: "+req);
        return saveChanges();
    }

    /**
     * Removes a requirement, and saves the changes.
     * @param req the requirement to remove from the task
     * @return success of both the remove, and the save
     */
    public boolean removeRequirement(Requirement req) {
        if(!reqsLoaded)
            loadRequirements();

        boolean success = requirements.remove(req);
        //TODO: Req should probably be destroyed here

        if(success)
            return saveChanges();
        return false;
    }

    /**
     * Returns the number of requirements associated with this task.
     * @return the number of requirements associated with this task
     */
    public int getRequirementCount() {
        if(!reqsLoaded)
            loadRequirements();
        return requirements.size();
    }

    /**
     * Retrieves a requirement from an index.
     * @param index the index of the desired Requirement
     * @return the associated Requirement
     */
    public Requirement getRequirement(int index) {
        if(!reqsLoaded)
            loadRequirements();

        return requirements.get(index);
    }
    
    public int getOrderValue() {
    	return 1;
    }

    /**
     * Returns a string representation of the task.
     * @return a string representation of the task
     */
    @Override
    public String toString() {
        return "Task(ID:"+getId()+")";
    }
}
