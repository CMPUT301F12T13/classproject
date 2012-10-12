package ca.cmput301.team13.taskman.model;

import java.util.ArrayList;

public class Task extends BackedObject{
	private String title;
	private String description;
	private ArrayList<Requirement> requirements;
	
	/**
	 * Construct a Task with backing in a persistent store
	 * @param id - the ID of the Task
	 * @param title - the title of the task
	 * @param description - the description of the task
	 * @param requirements - A List of requirements dependent on the task
	 * @param repo - the repository in which we are stored
	 */
	Task(int id, String title, String description, ArrayList<Requirement> requirements, VirtualRepository repo) {
		super(id, repo);
		this.title = title;
		this.description = description;
		this.requirements = requirements;
	}
	
	/**
	 * 
	 * @return the title of the Task
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * Changes the title, and saves the changes
	 * @param title the new title of the task
	 * @return success of save
	 */
	public boolean setTitle(String title) {
		this.title = title;
		return saveChanges();
	}
	
	/**
	 * 
	 * @return the description of the Task
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Changes the description, and saves the changes
	 * @param description the new description of the task
	 * @return success of save
	 */
	public boolean setDescription(String description) {
		this.description = description;
		return saveChanges();
	}
	
	/**
	 * Adds a requirement, and saves the changes
	 * @param req the requirement to add to the Task
	 * @return success of save
	 */
	public boolean addRequirement(Requirement req) {
		requirements.add(req);
		return saveChanges();
	}
	
	/**
	 * Removes a requirement, and saves the changes
	 * @param req the requirement to remove from the task
	 * @return success of both the remove, and the save
	 */
	public boolean removeRequirement(Requirement req) {
		boolean success = requirements.remove(req);
		//TODO: Req should probably be destroyed here
		
		if(success)
			return saveChanges();
		return false;
	}
	
	/**
	 * 
	 * @return the number of requirements associated with this Task
	 */
	public int getRequirementCount() {
		return requirements.size();
	}
	
	/**
	 * 
	 * @param index the index of the desired Requirement
	 * @return the associated Requirement
	 */
	public Requirement getRequirement(int index) {
		return requirements.get(index);
	}
}
