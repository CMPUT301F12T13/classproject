package ca.cmput301.team13.taskman.model;

import java.util.HashSet;
import java.util.Iterator;

public class TaskFilter {
	
	HashSet<Requirement.contentType> activeFilters;
	
	/**
	 * Initializes a new, empty TaskFilter
	 * 		(the default filter will not allow any tasks to be displayed)
	 */
	public TaskFilter() {
		this.activeFilters = new HashSet<Requirement.contentType>();
	}
	
	/**
	 * Initializes a new TaskFilter, given a set of active filters
	 * @param activeFilters
	 */
	public TaskFilter(HashSet<Requirement.contentType> activeFilters) {
		this.activeFilters = activeFilters;
	}

	/**
	 * Activates the specified filter
	 * @param requirementType		Requirement.contentType		The filter to activate
	 */
	public void activateFilter(Requirement.contentType requirementType) {
		this.activeFilters.add(requirementType);
	}
	
	/**
	 * Activates all possible filter types (according to Requirement.contentType
	 */
	public void activateAll() {
		for (Requirement.contentType requirementType : Requirement.contentType.values()) {
			this.activeFilters.add(requirementType);
		}
	}
	
	/**
	 * Deactivates the specified filter
	 * @param requirementType		Requirement.contentType		The filter to deactivate
	 */
	public void deactivateFilter(Requirement.contentType requirementType) {
		this.activeFilters.remove(requirementType);
	}
	
	/**
	 * Checks the task against the filter and determines whether it should be displayed
	 * @param	task	Task		The task to evaluate
	 * @return			boolean		Whether the task should be displayed under the filter
	 */
	public boolean isVisible(Task task) {
		int numRequirements = task.getRequirementCount();
		for (int i=0; i<numRequirements; i++) {
			if (!this.activeFilters.contains(task.getRequirement(i).getDesiredContent())) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Creates a SQL "WHERE" representation of this filter
	 * 		ex. "text OR image"
	 * @return	String		The SQL string
	 */
	public String toString() {
		String filterString = "";
		Iterator<Requirement.contentType> itr = this.activeFilters.iterator();
		while (itr.hasNext()) {
			filterString.concat(itr.next().toString());
			if (itr.hasNext()) filterString.concat(" OR ");
		}
		return filterString;
	}

}
