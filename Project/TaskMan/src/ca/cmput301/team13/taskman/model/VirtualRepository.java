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

import ca.cmput301.team13.taskman.TaskMan;
import ca.cmput301.team13.taskman.model.Requirement.contentType;

import android.content.Context;
import android.util.Log;

public class VirtualRepository {
    private LocalRepository local;

    public VirtualRepository(Context context) {
        local = new LocalRepository(context, this);
        local.open();
    }

    /**
     * Creates a new Task, with no title, description, or requirements
     * @param creator The User that has created the Task
     * @return the Task, with no non-housekeeping values yet set
     */
    public Task createTask(User creator) {
        return local.createTask(creator);
    }

    /**
     * Creates a new Requirement, with no description or fulfillments
     * @param creator The User that has created the Requirement
     * @param task The Task to add the Requirement to
     * @param contentType The desired content type of the requirement
     * @return the Requirement, with no non-housekeeping values yet set
     */
    public Requirement createRequirement(User creator, Task task, Requirement.contentType contentType) {
        return local.createRequirement(creator, task, contentType);
    }

    /**
     * Creates a new Fulfillment, with no content yet set
     * @param creator The User that has created the Fulfillment
     * @param req The Requirement to add the FUlfillment to
     * @return the Fulfillment, with no content yet attached
     */
    public Fulfillment createFulfillment(User creator, Requirement req) {
        return local.createFulfillment(creator, req);
    }

    /**
     * Obtains a list of Tasks for display
     * 
     * @param tf The TaskFilter with which to obtain Tasks
     * @return an ArrayList of compatible Tasks
     */
    public ArrayList<Task> getTasksForFilter(TaskFilter tf) {
        return local.loadTasks(tf);
    }

    /**
     * Creates a Requirement and links it to a Task
     * @param creator The User creating the requirement (null to use Task's creator)
     * @param t The Task to add the Requirement to
     * @param content The content type specified by the Requirement
     * @return
     */
    public Requirement addRequirementToTask(User creator, Task t, contentType content) {
        if(creator == null) {
            creator = t.getCreator();
        }
        Requirement r = local.createRequirement(creator, t, content);
        t.addRequirement(r);
        return r;
    }

    /**
     * Creates a Fulfillment and links it to a Requirement
     * @param creator The User creating the fulfillment (null to use Requirement's creator)
     * @param r The Requirement to add the Fulfillment to
     * @return
     */
    public Fulfillment addFulfillmentToRequirement(User creator, Requirement r) {
        if(creator == null) {
            creator = r.getCreator();
        }
        Fulfillment f = local.createFulfillment(creator, r);
        r.addFulfillment(f);
        return f;
    }


    /**
     * Saves any changes to the notifying object to the permanent store
     * @param backedObject - the object with changes
     */
    boolean saveUpdate(BackedObject backedObject) {
        if(backedObject instanceof Task) {
            local.updateTask((Task)backedObject);
            return true;
        } else if(backedObject instanceof Requirement) {
            local.updateRequirement((Requirement)backedObject);
            return true;
        } else if(backedObject instanceof Fulfillment) {
            local.updateFulfillment((Fulfillment)backedObject);
            return true;
        }
        //If we're here, then we didn't detect a type. Perhaps a new feature isn't fully implemented?
        Log.w("VirtualRepository", "Attempted to save changes to unknown object: "+backedObject.getClass().toString());
        return false;
    }

    /**
     * Get the list of Requirements for a given Task
     * @param t The task for which to return Requirements
     * @return
     */
    ArrayList<Requirement> getRequirementsForTask(Task t) {
        return local.loadRequirementsForTask(t);
    }

    /**
     * Get the list of Fulfillments for a given Requirement
     * @param r The requirement for which to return Fulfillments
     * @return
     */
    ArrayList<Fulfillment> getFulfillmentsForRequirement(Requirement r) {
        return local.loadFulfillmentsForRequirement(r);
    }

    /**
     * Get the Task corresponding to the given Task Id
     * @param taskId the ID
     * @return the Task
     */
    public Task getTask(int taskId) {
        return local.getTask(taskId);
    }
    
    /**
     * Get updated data for the requested Task
     * @param t		The task to get updated data for
     * @return		The updated Task
     */
    public Task getTaskUpdate(Task t) {
    	return local.getTaskUpdate(t);
    }

    /**
     * Get the Requirement corresponding to the given Requirement Id
     * @param requirementId the ID
     * @return the Requirement
     */
    public Requirement getRequirement(int requirementId) {
        return local.getRequirement(requirementId);
    }

    /**
     * Get the Fulfillment corresponding to the given Fulfillment Id
     * @param fulfillmentId the ID
     * @return the Fulfillment
     */
    public Fulfillment getFulfillment(int fulfillmentId) {
        return local.getFulfillment(fulfillmentId);
    }

    /**
     * Remove a Specified Task from the backing store. All references to the Task object should be discarded
     * @param t The Task
     */
    public void removeTask(Task t) {
    	//Only the creator can delete the task
    	if(t.getCreator().equals(TaskMan.getInstance().getUser())) {
    		local.removeTask(t);
    	}
    }

    /**
     * Remove a Specified Requirement from the backing store. All references to the Requirement object should be discarded
     * @param r The Requirement
     */
    public void removeRequirement(Requirement r) {
    	//Only the creator can delete the Requirement
    	if(r.getCreator().equals(TaskMan.getInstance().getUser())) {
    		local.removeRequirement(r);
    	}
    }

    /**
     * Remove a Specified Fulfillment from the backing store. All references to the Fulfillment object should be discarded
     * @param f The Fulfillment
     */
    public void removeFulfillment(Fulfillment f) {
    	//Only the creator can delete the Fulfillment
    	if(f.getCreator().equals(TaskMan.getInstance().getUser())) {
    		local.removeFulfillment(f);
    	}
    }


}
