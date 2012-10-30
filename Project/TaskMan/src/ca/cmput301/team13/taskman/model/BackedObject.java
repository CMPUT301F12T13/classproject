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

import java.util.Date;

abstract class BackedObject {
	
	
	private int id;
	private Date created;
	private Date lastModified;
	private User creator;
	VirtualRepository repo;
	boolean delaySave = false;
	
	BackedObject(int id, Date created, Date lastModified, User creator, VirtualRepository repo) {
		this.id = id;
		this.repo = repo;
		this.creator = creator;
		this.created = created;
		this.lastModified = lastModified;
	}
	
	/**
	 * Save any changes that have occurred to this object
	 * @return Whether or not the save was successful
	 */
	boolean saveChanges() {
		lastModified = new Date();
		if(delaySave)
			return true; //If we're delaying, no errors occurred here
		
		return repo.saveUpdate(this);
	}
	
	/**
	 * Optionally temporarily turn off immediate updates to the persistent store.
	 * This is a good idea, for example, when about to make a large quantity of changes.
	 * Immediately forces a save when re-enabled.
	 * @param delay - whether or not to push updates, default: true
	 * @return whether or not the operation was successful
	 */
	public boolean delaySaves(boolean delay) {
		delaySave = delay;
		if(!delay) 
			return saveChanges();
		return true;
	}
	
	/**
	 * Access the ID of this object
	 * @return the id
	 */
	int getId() {
		return id;
	}
	
	/**
	 * Access the date/time when this object was first created
	 * @return the date
	 */
	public Date getCreatedDate() {
		return created;
	}
	
	/**
	 * Access the date/time when this object was last modified
	 * @return the date
	 */
	public Date getLastModifiedDate() {
		return lastModified;
	}
	
	/**
	 * 
	 * @return the User that created this Object
	 */
	public User getCreator() {
		return creator;
	}
	
}
