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

import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import ca.cmput301.team13.taskman.TaskMan;
import ca.cmput301.team13.taskman.model.Requirement.contentType;
import ca.cmput301.team13.taskman.model.WebRepository.WebActionCallback;

/**
 * A facade combining {@link LocalRepository} and a remote
 * database into one interface.
 */
public class VirtualRepository {
    private LocalRepository local;
    private WebRepository web;

    /**
     * Instantiates a new {@link VirtualRepository} object.
     * @param context the context of the Android application
     */
    public VirtualRepository(Context context) {
    	if(local == null) {
    		local = new LocalRepository(context, this);
        	local.open();
        	web = new WebRepository(this);
    	}
    }

    /**
     * Creates a new Task, with no title, description, or requirements.
     * @param creator The User that has created the Task
     * @return the Task, with no non-housekeeping values yet set
     */
    public Task createTask(User creator) {
        return local.createTask(creator);
    }
    
    public Task createTask(Task t) {
    	return local.createTask(t);
    }

    /**
     * Creates a new Requirement, with no description or fulfillments.
     * @param creator The User that has created the Requirement
     * @param task The Task to add the Requirement to
     * @param contentType The desired content type of the requirement
     * @return the Requirement, with no non-housekeeping values yet set
     */
    public Requirement createRequirement(User creator, Task task, Requirement.contentType contentType) {
        return local.createRequirement(creator, task, contentType);
    }

    /**
     * Creates a new Fulfillment, with no content yet set.
     * @param creator The User that has created the Fulfillment
     * @param req The Requirement to add the FUlfillment to
     * @return the Fulfillment, with no content yet attached
     */
    public Fulfillment createFulfillment(User creator, Requirement req) {
        return local.createFulfillment(creator, req);
    }

    /**
     * Obtains a list of Tasks for display.
     * @param tf The TaskFilter with which to obtain Tasks
     * @return an ArrayList of compatible Tasks
     */
    public ArrayList<Task> getTasksForFilter(TaskFilter tf) {
        return local.loadTasks(tf);
    }
    
    /**
     * Creates a Requirement and links it to a Task. Initializes it with a default ID.
     * @see addRequirementsToTask(User, Task, contentType, int) for full implementation
     */
    public Requirement addRequirementToTask(User creator, Task t, contentType content) {
    	return addRequirementToTask(creator, t, content, -1);
    }

    /**
     * Creates a Requirement and links it to a Task.
     * @param creator The User creating the requirement (null to use Task's creator)
     * @param t The Task to add the Requirement to
     * @param content The content type specified by the Requirement
     * @param id	The desired ID for the Requirement. -1 if a default ID should be generated.
     * @return
     */
    public Requirement addRequirementToTask(User creator, Task t, contentType content, int id) {
        if(creator == null) {
            creator = t.getCreator();
        }
        Requirement r = local.createRequirement(creator, t, content, id);
        return r;
    }
    
    /**
     * Creates a Fulfillment and links it to a Requirement. Adds a default ID
     * @see addFulfillmentToRequirement(User, Requirement, int) for full implementation
     */
    public Fulfillment addFulfillmentToRequirement(User creator, Requirement r) {
    	return addFulfillmentToRequirement(creator, r, -1);
    }

    /**
     * Creates a Fulfillment and links it to a Requirement.
     * @param creator The User creating the fulfillment (null to use Requirement's creator)
     * @param r The Requirement to add the Fulfillment to
     * @return
     */
    public Fulfillment addFulfillmentToRequirement(User creator, Requirement r, int id) {
        if(creator == null) {
            creator = r.getCreator();
        }
        Fulfillment f = local.createFulfillment(creator, r);
        return f;
    }


    /**
     * Saves any changes to the notifying object to the permanent store.
     * @param backedObject - the object with changes
     */
    boolean saveUpdate(final BackedObject backedObject, boolean push) {
    	boolean updated = false;
    	final VirtualRepository vr = this;
    	//Push to web if necessary
    	if(!backedObject.getIsLocal() && push) {
    		System.out.println("going to save to web!");
    		System.out.println("webID: " + backedObject.getWebID());
    		web.pushObject(backedObject, true, new WebActionCallback() {
    			public void run() {
    				if(success) {
    					System.out.println("pushed!");
    					if(backedObject instanceof Task) {
    						vr.getTaskUpdate((Task)backedObject);
    					} else if(backedObject instanceof Requirement) {
    						vr.getRequirementUpdate((Requirement)backedObject);
    					} else if(backedObject instanceof Fulfillment) {
    						vr.getFulfillmentUpdate((Fulfillment)backedObject);
    					}
    				}
    			}
    		}, null);
    	}
    	
    	//Update locally
        if(backedObject instanceof Task) {
            local.updateTask((Task)backedObject);
            updated = true;
        } else if(backedObject instanceof Requirement) {
            local.updateRequirement((Requirement)backedObject);
            updated = true;
        } else if(backedObject instanceof Fulfillment) {
            local.updateFulfillment((Fulfillment)backedObject);
            updated = true;
        }
        
        //Push to web if necessary
        if(!backedObject.getIsLocal()) {
        	System.out.println("going to save to web!");
        	/*web.pushObject(backedObject, true, new WebActionCallback() {
        		public void run(boolean success, String message) {
        			//TODO: If this fails, pop up a Toast or something? With an option to retry?
        		}
        	});*/
        }
        
        if(!updated)
        	//If we're here, then we didn't detect a type. Perhaps a new feature isn't fully implemented?
        	Log.w("VirtualRepository", "Attempted to save changes to unknown object: "+backedObject.getClass().toString());
        return false;
    }

    /**
     * Get the list of Requirements for a given Task.
     * @param t The task for which to return Requirements
     * @return
     */
    ArrayList<Requirement> getRequirementsForTask(Task t) {
        return local.loadRequirementsForTask(t);
    }

    /**
     * Get the list of Fulfillments for a given Requirement.
     * @param r The requirement for which to return Fulfillments
     * @return
     */
    ArrayList<Fulfillment> getFulfillmentsForRequirement(Requirement r) {
        return local.loadFulfillmentsForRequirement(r);
    }

    /**
     * Get the Task corresponding to the given Task Id.
     * @param taskId the ID
     * @return the Task
     */
    public Task getTask(int taskId) {
        return local.getTask(taskId);
    }
    
    /**
     * Get updated data for the requested Task.
     * @param t		The task to get updated data for
     * @return		The updated Task
     */
    public Task getTaskUpdate(Task t) {
    	return local.getTaskUpdate(t);
    }
    
    /**
     * Get updated data for the requested Requirement
     * @param t		The Requirement to get updated data for
     * @return		The updated Requirement
     */
    Requirement getRequirementUpdate(Requirement r) {
    	return local.getRequirementUpdate(r);
    }
    
    /**
     * Get updated data for the requested Fulfillment
     * @param t		The Fulfillment to get updated data for
     * @return		The updated Fulfillment
     */
    Fulfillment getFulfillmentUpdate(Fulfillment f) {
    	return local.getFulfillmentUpdate(f);
    }

    /**
     * Get the Requirement corresponding to the given Requirement Id.
     * @param requirementId the ID
     * @return the Requirement
     */
    public Requirement getRequirement(int requirementId) {
        return local.getRequirement(requirementId);
    }

    /**
     * Get the Fulfillment corresponding to the given Fulfillment Id.
     * @param fulfillmentId the ID
     * @return the Fulfillment
     */
    public Fulfillment getFulfillment(int fulfillmentId) {
        return local.getFulfillment(fulfillmentId);
    }

    /**
     * Remove a specified Task from the backing store. All
     * references to the Task object should be discarded.
     * @param t The Task
     */
    public void removeTask(Task t) {
    	//Only the creator can delete the task
    	if(t.getCreator().equals(TaskMan.getInstance().getUser())) {
    		local.removeTask(t);
    	}
    }

    /**
     * Remove a specified Requirement from the backing
     * store. All references to the Requirement object
     * should be discarded.
     * @param r The Requirement
     */
    public void removeRequirement(Requirement r) {
    	//Only the creator can delete the Requirement
    	if(r.getCreator().equals(TaskMan.getInstance().getUser())) {
    		local.removeRequirement(r);
    	}
    }

    /**
     * Remove a Specified Fulfillment from the backing store.
     * All references to the Fulfillment object should be discarded.
     * @param f The Fulfillment
     */
    public void removeFulfillment(Fulfillment f) {
    	//Only the creator can delete the Fulfillment
    	if(f.getCreator().equals(TaskMan.getInstance().getUser())) {
    		local.removeFulfillment(f);
    	}
    }
    
    public boolean taskExists(Task t) {
    	if(getTask(t.getId()) == null) {
    		return false;
    	} else {
    		return true;
    	}
    }
    
    /**
     * Get the newest modification that has been done in the LocalRepository
     * @return The newest modification date
     */
    public Date getNewestLocalModification() {
    	return local.getNewestModification();
    }

    public void synchronize(WebActionCallback callback, Activity context) {
    	web.pullChanges(callback, context);
    }

}
