package ca.cmput301.team13.taskman;

import java.util.ArrayList;
import java.util.Collections;

import ca.cmput301.team13.taskman.model.LocalRepository;
import ca.cmput301.team13.taskman.model.Requirement.contentType;
import ca.cmput301.team13.taskman.model.TaskCreatedComparator;
import ca.cmput301.team13.taskman.model.TaskFilter;
import ca.cmput301.team13.taskman.model.Task;
import ca.cmput301.team13.taskman.model.User;
import ca.cmput301.team13.taskman.model.VirtualRepository;
import android.content.Context;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

/**
 * Provides a list of tasks from the virtual repo to list views
 */
public class TaskListAdapter implements ListAdapter {

	private VirtualRepository repo;
	private ArrayList<Task> tasks;
	private ArrayList<DataSetObserver> observers;
	private LayoutInflater inflater;

	// Task Filters
	private TaskFilter taskFilter;
	
	//View types
	enum viewType {
		Header,
		Task
	};

	/**
	 * Construct a TaskListAdapter
	 * @param vr The virtual repository instance
	 */
	public TaskListAdapter(VirtualRepository vr, Context context) {
		repo = vr;
		taskFilter = new TaskFilter();
		observers = new ArrayList<DataSetObserver>();
		inflater = LayoutInflater.from(context);

		//Get our initial data
		update();
	}

	/**
	 * Refresh task list from the local repo.
	 */
	public void update() {
		tasks = repo.getTasksForFilter(taskFilter);
		sortByCreatedDate();
		notifyObservers();
	}

	public int getItemViewType(int viewIndex) {
		//TODO: Differentiate headers from Tasks
		return viewType.Task.ordinal();
	}

	public View getView(int viewIndex, View convertView, ViewGroup parent) {
		View newView;
		if(convertView != null) {
			//Re-use the given view
			newView = convertView;
		} else {
			//Instantiate a new view
			newView = inflater.inflate(R.layout.task_row, null);
		}
		Task task = (Task)getItem(viewIndex);
		//Figure out what we support
		boolean text = false;
		boolean image = false;
		boolean audio = false;
		Log.w("TaskListAdapter", task + " has " + task.getRequirementCount() + " Requirements");
		for(int i=0;i<task.getRequirementCount();i++) {
			contentType ct = task.getRequirement(i).getContentType();
			Log.w("TaskListAdapter", "Requirement content type: " + ct.toString());
			if(ct == contentType.text) text = true;
			else if(ct == contentType.image) image = true;
			else audio = true; 
		}
		
		//TODO: Set all the pertinent values
		((TextView)newView.findViewById(R.id.title_lbl)).setText(task.getTitle());
		newView.findViewById(R.id.txtImg).setVisibility((text ? View.VISIBLE : View.INVISIBLE));
		newView.findViewById(R.id.imgImg).setVisibility((image ? View.VISIBLE : View.INVISIBLE));
		newView.findViewById(R.id.audImg).setVisibility((audio ? View.VISIBLE : View.INVISIBLE));
		return newView;
	}

	public int getViewTypeCount() {
		return viewType.values().length;
	}

	public boolean hasStableIds() {
		//Our ids are dependant on array index, which changes on sort.
		return false;
	}

	/**
	 * Implementation is not Thread-safe.
	 */
	public void registerDataSetObserver(DataSetObserver dso) {
		// TODO: This is used to tell the UI that a reload would be a good idea
		observers.add(dso);
	}

	/**
	 * Implementation is not Thread-safe
	 */
	public void unregisterDataSetObserver(DataSetObserver dso) {
		// TODO: This is used to tell the UI that a reload would be a good idea
		observers.remove(dso);
	}
	
	private void sortByCreatedDate() {
		//This as its own method may be unnecessary. THoughts?
		Collections.sort(tasks, new TaskCreatedComparator());
	}

	private void notifyObservers() {
		for(DataSetObserver dso : observers) {
			dso.onChanged();
		}
	}
	
	public int getCount()        { return tasks.size(); }
	public Object getItem(int i) { return tasks.get(i); }
	public long getItemId(int i) { return i; }
	public boolean isEmpty()     { return tasks.isEmpty(); }

	public boolean areAllItemsEnabled() {
		return false;
	}

	public boolean isEnabled(int index) {
		// TODO mark headers unlabelled
		return true;
	}
}
