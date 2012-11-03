package ca.cmput301.team13.taskman;

import java.util.ArrayList;
import java.util.Collections;

import ca.cmput301.team13.taskman.model.LocalRepository;
import ca.cmput301.team13.taskman.model.Requirement;
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

/**
 * Provides a list of requirements from the virtual repo to list views
 */
public class RequirementListAdapter implements ListAdapter {

	private Task task;
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
	 * Construct a RequirementListAdapter
	 * @param task The task for which to list Requirements
	 * @param mode The mode ("edit"/"view")
	 * @param context The context for which to inflate layouts
	 */
	public RequirementListAdapter(Task task, String mode, Context context) {
		this.task = task;
		observers = new ArrayList<DataSetObserver>();
		inflater = LayoutInflater.from(context);

		//Get our initial data
		update();
	}
	
	public void update() {
		notifyObservers();
	}

	public int getItemViewType(int viewIndex) {
		return 0;
	}

	public View getView(int viewIndex, View convertView, ViewGroup parent) {
		View newView;
		if(convertView != null) {
			//Re-use the given view
			newView = convertView;
		} else {
			//Instantiate a new view
			newView = inflater.inflate(R.layout.req_edit_elem, null);
		}
		final Requirement req = (Requirement)getItem(viewIndex);
		
		((EditText)newView.findViewById(R.id.reqDescriptionEdit)).setText(req.getDescription());
		//Figure out what Image resource to set
		int resource;
		if(req.getContentType() == contentType.text) {
			resource = R.drawable.txticon;
		}
		else if (req.getContentType() == contentType.image) {
			resource = R.drawable.imgicon;
		}
		else if (req.getContentType() == contentType.audio)  {
			resource = R.drawable.audicon;
		}
		else {
			Log.w("RequirementListAdapter", "Unknown Content Type: "+req.getContentType());
			resource = R.drawable.txticon;
		}
		//Set the image
		((ImageView)newView.findViewById(R.id.reqContentImg)).setImageResource(resource);
		
		//Enable the delete button
		((Button)newView.findViewById(R.id.reqDeleteBtn)).setOnClickListener(new OnClickListener() {

			public void onClick(View source) {
				task.removeRequirement(req);
				update();
			}
			
		});
		return newView;
	}

	public int getViewTypeCount() {
		return 1;
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

	private void notifyObservers() {
		for(DataSetObserver dso : observers) {
			dso.onChanged();
		}
	}
	
	public int getCount()        { return task.getRequirementCount(); }
	public Object getItem(int i) { return task.getRequirement(i); }
	public long getItemId(int i) { return i; }
	public boolean isEmpty()     { return task.getRequirementCount() == 0; }

	public boolean areAllItemsEnabled() {
		return false;
	}

	public boolean isEnabled(int index) {
		return true;
	}
}
