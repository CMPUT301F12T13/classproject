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

public class Requirement extends BackedObject{
	private String description;
	private ArrayList<Fulfillment> fulfillments;

	/**
	 * Construct a Requirement with backing in the persistent store
	 * @param id - the id of the requirement
	 * @param description - the description of the requirement
	 * @param fulfillments - the list of fulfillments of the requirement
	 * @param repo - the repository backing this object
	 */
	Requirement(int id, String description, ArrayList<Fulfillment> fulfillments, VirtualRepository repo) {
		super(id, repo);
		this.description = description;
		this.fulfillments = fulfillments;
	}
	
	/**
	 * 
	 * @return the description of the Requirement
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Changes the description, and saves the changes
	 * @param description the new description of the requirement
	 * @return success of save
	 */
	public boolean setDescription(String description) {
		this.description = description;
		return saveChanges();
	}
	
	/**
	 * Adds a fulfillment, and saves the changes
	 * @param ful the fulfillment to add to the requirement
	 * @return success of save
	 */
	public boolean addFulfillment(Fulfillment ful) {
		fulfillments.add(ful);
		return saveChanges();
	}
	
	/**
	 * Removes a fulfillment, and saves the changes
	 * @param ful the fulfillment to remove from the requirement
	 * @return success of both the remove, and the save
	 */
	public boolean removeFulfillment(Fulfillment ful) {
		boolean success = fulfillments.remove(ful);
		//TODO: ful should probably be destroyed here
		
		if(success)
			return saveChanges();
		return false;
	}
	
	/**
	 * 
	 * @return the number of fulfillments associated with this requirement
	 */
	public int getFullfillmentCount() {
		return fulfillments.size();
	}
	
	/**
	 * 
	 * @param index the index of the desired Fulfillment
	 * @return the associated Fulfillment
	 */
	public Fulfillment getFulfillment(int index) {
		return fulfillments.get(index);
	}

}
