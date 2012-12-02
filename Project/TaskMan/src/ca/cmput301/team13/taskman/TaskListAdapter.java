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

package ca.cmput301.team13.taskman;

import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;
import ca.cmput301.team13.taskman.model.BackedObjectCreatedComparator;
import ca.cmput301.team13.taskman.model.Requirement.contentType;
import ca.cmput301.team13.taskman.model.Task;
import ca.cmput301.team13.taskman.model.TaskFilter;
import ca.cmput301.team13.taskman.model.User;
import ca.cmput301.team13.taskman.model.VirtualRepository;

/**
 * Provides a list of tasks from the {@link VirtualRepository}
 * to list views.
 */
public class TaskListAdapter implements ListAdapter {

    private VirtualRepository repo;
    private ArrayList<Task> tasks;
    private ArrayList<DataSetObserver> observers;
    private LayoutInflater inflater;
    private User userFilter;
    private String[] searchTerms;

    // Task Filters
    private TaskFilter taskFilter;

    //View types
    enum viewType {
        Header,
        Task
    };

    /**
     * Construct a TaskListAdapter.
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
     * Refresh task list from the {@link LocalRepository}.
     */
    public void update() {
        tasks = repo.getTasksForFilter(taskFilter);
        filterTasks();
        sortByCreatedDate();
        notifyObservers();
    }

    /**
     * Returns the view type of an item at a given index.
     * @param viewIndex index of the item whose view type to return
     */
    public int getItemViewType(int viewIndex) {
        //TODO: Differentiate headers from Tasks
        return viewType.Task.ordinal();
    }

    /**
     * Returns the {@link View} for an item in the list.
     * @param viewIndex the index of the item
     * @param convertView the old view
     * @param parent the parent view 
     */
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
        boolean video = false;
        Log.w("TaskListAdapter", task + " has " + task.getRequirementCount() + " Requirements");
        for(int i=0;i<task.getRequirementCount();i++) {
            contentType ct = task.getRequirement(i).getContentType();
            Log.w("TaskListAdapter", "Requirement content type: " + ct.toString());
            if(ct == contentType.text) text = true;
            else if(ct == contentType.image) image = true;
            else if(ct == contentType.audio) audio = true;
            else if(ct == contentType.video) video = true;
        }

        //Set all the pertinent values
        ((TextView)newView.findViewById(R.id.title_lbl)).setText(task.getTitle());
        newView.findViewById(R.id.txtImg).setVisibility((text ? View.VISIBLE : View.INVISIBLE));
        newView.findViewById(R.id.imgImg).setVisibility((image ? View.VISIBLE : View.INVISIBLE));
        newView.findViewById(R.id.audImg).setVisibility((audio ? View.VISIBLE : View.INVISIBLE));
        newView.findViewById(R.id.vidImg).setVisibility((video ? View.VISIBLE : View.INVISIBLE));

        return newView;
    }

    /**
     * Returns the number of view types.
     */
    public int getViewTypeCount() {
        return viewType.values().length;
    }

    /**
     * Indicates stable ids.
     */
    public boolean hasStableIds() {
        //Our ids are dependant on array index, which changes on sort.
        return false;
    }

    /**
     * Registers a data set observer.
     * Warning: implementation is not Thread-safe.
     */
    public void registerDataSetObserver(DataSetObserver dso) {
        // TODO: This is used to tell the UI that a reload would be a good idea
        observers.add(dso);
    }

    /**
     * Unregisters a data set observer.
     * Warning: implementation is not Thread-safe.
     */
    public void unregisterDataSetObserver(DataSetObserver dso) {
        // TODO: This is used to tell the UI that a reload would be a good idea
        observers.remove(dso);
    }

    /**
     * Causes items to be sorted by creation date.
     */
    private void sortByCreatedDate() {
        //This as its own method may be unnecessary. THoughts?
        Collections.sort(tasks, new BackedObjectCreatedComparator());
    }

    /**
     * Notify registered observers of changes.
     */
    private void notifyObservers() {
        for(DataSetObserver dso : observers) {
            dso.onChanged();
        }
    }

    /**
     * Returns the number of items in the list.
     */
    public int getCount()        { return tasks.size(); }
    /**
     * Returns the item at a given index.
     * @param i index of the item to retrieve
     */
    public Object getItem(int i) { return tasks.get(i); }
    /**
     * Returns the id of an item at a given index.
     * @param i the index of the item whose index to return
     */
    public long getItemId(int i) { return i; }
    /**
     * Indicates whether or not the list is empty.
     */
    public boolean isEmpty()     { return tasks.isEmpty(); }

    /**
     * Indicates all items enabled.
     */
    public boolean areAllItemsEnabled() {
        return false;
    }

    /**
     * Indicates whether enabled.
     */
    public boolean isEnabled(int index) {
        // TODO mark headers unlabelled
        return true;
    }
    
    private void filterTasks() {
    	//Perform user filtering, if present
    	if(userFilter != null) {
    		for(int i=0;i<tasks.size();i++) {
    			if(!tasks.get(i).getCreator().equals(userFilter)) {
    				tasks.remove(i);
    				i--;
    			}
    		}
    	}
    	//Perform keyword searching, if present
    	if(searchTerms != null) {
    		ArrayList<Task> tasks = new ArrayList<Task>();
    		for(Task t : this.tasks) {
    			boolean match = true;
    			for(String term : searchTerms)
    				if(!t.getTitle().contains(term)){
    					match = false;
    					break;
    				}
    			if(match) {
    				tasks.add(t);
    			}
    		}
    		this.tasks = tasks;
    	}
    		
    }
    public void setSearchTerms(String s) {
    	if(s == null) {
    		searchTerms = null;
    	} else {
    		searchTerms = s.split(" ");
    	}
    	update();
    }

	public void showAll() {
		userFilter = null;
		update();
	}

	public void showUserTasks(User user) {
		userFilter = user;
		update();
	}
}
