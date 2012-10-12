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
