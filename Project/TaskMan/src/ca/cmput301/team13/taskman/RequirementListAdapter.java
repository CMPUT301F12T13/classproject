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

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import ca.cmput301.team13.taskman.model.Requirement;
import ca.cmput301.team13.taskman.model.Requirement.contentType;
import ca.cmput301.team13.taskman.model.Task;
import ca.cmput301.team13.taskman.model.TaskFilter;

/**
 * Provides a list of requirements from the virtual repo to list views
 */
public class RequirementListAdapter implements ListAdapter {

	private Task task;
	private ArrayList<DataSetObserver> observers;
	private LayoutInflater inflater;
	private Activity activity;

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
	
	public void setActivity(Activity activity) {
		this.activity = activity;
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
		
		/*((EditText)newView.findViewById(R.id.reqDescriptionEdit)).setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				EditText descriptionEditor = (EditText)v;
				if(!hasFocus) {
					req.setDescription(descriptionEditor.getText().toString());
				}
			}
		});*/
		
		//Enable the fields to be edited
		((EditText)newView.findViewById(R.id.reqDescriptionEdit)).addTextChangedListener(new TextWatcher(){
	        
			//TODO: Perhaps increase efficiency by saving when the field loses focus?
	        public void afterTextChanged(Editable editable) {
	        	String t = editable.toString();
	            if(req.setDescription(editable.toString())){
	            	System.out.println("saved");
	            }
	        }

			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				//Do nothing
			}

			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				//Do Nothing
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
