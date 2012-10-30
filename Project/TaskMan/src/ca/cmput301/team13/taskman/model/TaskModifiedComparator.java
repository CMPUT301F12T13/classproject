package ca.cmput301.team13.taskman.model;

import java.util.Comparator;

public class TaskModifiedComparator implements Comparator<Task> {

	public int compare(Task t1, Task t2) {
		return t1.getLastModifiedDate().compareTo(t2.getLastModifiedDate());
	}

}
