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

abstract class BackedObject {
	private int id;
	private VirtualRepository repo;
	boolean delaySave = false;
	
	BackedObject(int id, VirtualRepository repo) {
		this.id = id;
		this.repo = repo;
	}
	
	/**
	 * Save any changes that have occurred to this object
	 * @return Whether or not the save was successful
	 */
	boolean saveChanges() {
		if(delaySave)
			return true; //If we're delaying, no errors occurred here
		
		return repo.saveUpdate(this);
	}
	
	public boolean delaySaves(boolean delay) {
		delaySave = delay;
		if(!delay) 
			return saveChanges();
		return true;
	}
	
	
	int getId() {
		return id;
	}
	
}
