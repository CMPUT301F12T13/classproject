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
