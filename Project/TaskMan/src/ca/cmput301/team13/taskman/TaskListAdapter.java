package ca.cmput301.team13.taskman;

import java.util.ArrayList;

import ca.cmput301.team13.taskman.model.LocalRepository;
import ca.cmput301.team13.taskman.model.TaskFilter;
import ca.cmput301.team13.taskman.model.Task;
import ca.cmput301.team13.taskman.model.User;
import ca.cmput301.team13.taskman.model.VirtualRepository;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;

/**
 * Provides a list of tasks from the virtual repo to list views
 */
public class TaskListAdapter implements Adapter {

	private VirtualRepository repo;
	private ArrayList<Task> tasks;

	// Task Filters
	private TaskFilter taskFilter;

	/**
	 * Construct a TaskListAdapter
	 * @param vr The virtual repository instance
	 */
	public TaskListAdapter(VirtualRepository vr) {
		repo = vr;
		taskFilter = new TaskFilter();

		// update();
	}

	/**
	 * Refresh task list from the local repo.
	 */
	public void update() {
		tasks = repo.getTasksForFilter(taskFilter);

		// TODO: order tasks based on date
		// TODO: order tasks based on fulfillment
	}

	public int getItemViewType(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	public void registerDataSetObserver(DataSetObserver arg0) {
		// TODO: Will we actually use this?
	}

	public void unregisterDataSetObserver(DataSetObserver arg0) {
		// TODO: Will we actually use this?
	}

	public int getCount()        { return tasks.size(); }
	public Object getItem(int i) { return tasks.get(i); }
	public long getItemId(int i) { return i; }
	public boolean isEmpty()     { return tasks.isEmpty(); }
}
