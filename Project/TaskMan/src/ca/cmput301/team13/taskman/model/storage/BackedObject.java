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

import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;
import ca.cmput301.team13.taskman.TaskMan;

/**
 * Base class for objects that reside in the database.
 */
public abstract class BackedObject implements Parcelable, Comparable<BackedObject> {

    private String id;
    private String webID = "";
    private String parentId = null;
    private String parentWebID = "";
	private Date created;
    private Date lastModified;
	private User creator;
    transient VirtualRepository repo;
    boolean delaySave = false;
    private boolean isLocal = false;
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd kk:mm:ss:SSS Z");
    
    /**
     * Creates a BackedObject.
     * @param id			The ID for insertion into the database
     * @param created		The date the item was created
     * @param lastModified	The last modification date 
     * @param creator		The User who created this Object
     * @param repo			The VirtualRepository this Object is wrapped under
     */
    BackedObject(String id, Date created, Date lastModified, User creator, VirtualRepository repo) {
        this.id = id;
        this.repo = repo;
        this.creator = creator;
        this.created = created;
        this.lastModified = lastModified;
    }
    
    public BackedObject() { }
    
    boolean saveChanges(boolean push) {
        if(delaySave)
            return true; //If we're delaying, no errors occurred here

        //lastModified = new Date();
        return repo.saveUpdate(this, push);
    }

    /**
     * Save any changes that have occurred to this object.
     * @return Whether or not the save was successful
     */
    boolean saveChanges() {
        return saveChanges(true);
    }

    /**
     * Optionally temporarily turn off immediate updates to the persistent store.
     * This is a good idea, for example, when about to make a large quantity of changes.
     * Immediately forces a save when re-enabled.
     * @param delay - whether or not to push updates, default: true
     * @return whether or not the operation was successful
     */
    public boolean delaySaves(boolean delay) {
        return delaySaves(delay, true);
    }
    
    public boolean delaySaves(boolean delay, boolean push) {
        delaySave = delay;
        if(!delay)
            return saveChanges(push);
        return true;
    }

    /**
     * Access the ID of this object.
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Access the date/time when this object was first created.
     * @return the date
     */
    public Date getCreatedDate() {
        return created;
    }

    /**
     * Access the date/time when this object was last modified.
     * @return the date
     */
    public Date getLastModifiedDate() {
        return lastModified;
    }
    
    public void setLastModifiedDate(Date lastModified) {
		this.lastModified = lastModified;
	}

    /**
     * Returns the {@link User} who created the object.
     * @return the User that created this Object
     */
    public User getCreator() {
        return creator;
    }
    
    public String getWebID() {
		return webID;
	}

	public void setWebID(String webID) {
		this.webID = webID;
	}
	
    /**
     * Describes the contents of the object (for parcelling).
     */
    //Parcelable Implementation
    public int describeContents() {
        return 0;
    }

    /**
     * Writes the object to a {@link android.os.Parcel}.
     */
    public void writeToParcel(Parcel out, int flags) {
        BackedObjectParcel parcel = new BackedObjectParcel(getId(), getClass().getName());
        out.writeSerializable(parcel);
    }

    public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getParentWebID() {
		return parentWebID;
	}

	public void setParentWebID(String parentWebID) {
		this.parentWebID = parentWebID;
	}
	
	abstract public int getOrderValue();
	
	public int compareTo(BackedObject bo) {
		return this.getOrderValue() - bo.getOrderValue();
	}

	public boolean getIsLocal() {
        return isLocal;
    }

    public void setIsLocal(boolean isLocal) {
        this.isLocal = isLocal;
    }

    /**
     * Returns a BackedObject conforming with the type of BackedObject that was parcelled.
     * 		- Possible types: Task, Requirement, Fulfillment
     */
    public static final Parcelable.Creator<BackedObject> CREATOR
    = new Parcelable.Creator<BackedObject>() {
        public BackedObject createFromParcel(Parcel in) {
            BackedObjectParcel parcel = (BackedObjectParcel)in.readSerializable();
            //Decide which type of BackedObject needs to be returned
            if(parcel.backedObjectType.equals(Task.class.getName())) {
                return TaskMan.getInstance().getRepository().getTask(parcel.id);
            } else if(parcel.backedObjectType.equals(Requirement.class.getName())) {
                return TaskMan.getInstance().getRepository().getRequirement(parcel.id);
            } else if(parcel.backedObjectType.equals(Fulfillment.class.getName())) {
                return TaskMan.getInstance().getRepository().getFulfillment(parcel.id);
                //If none match, perhaps a new BackedObject type has been added and not handled here?
            } else {
                throw new RuntimeException("Parceled BackedObject type that isn't supported.");
            }
        }

        public Task[] newArray(int size) {
            return new Task[size];
        }
    };
}
