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

package ca.cmput301.team13.taskman.ui;

import utils.Notifications;
import ca.cmput301.team13.taskman.R;
import ca.cmput301.team13.taskman.TaskMan;
import ca.cmput301.team13.taskman.R.id;
import ca.cmput301.team13.taskman.R.layout;
import ca.cmput301.team13.taskman.R.menu;
import ca.cmput301.team13.taskman.model.storage.Task;
import ca.cmput301.team13.taskman.model.storage.WebRepository.WebActionCallback;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

/**
 * The main activity for the app, provides a list of tasks and allows
 * users to navigate to task creation, viewing, and editing activities.
 */
public class RootActivity extends Activity implements OnClickListener, OnItemClickListener, TextWatcher, OnFocusChangeListener {
    ListView taskList;
    TaskListAdapter taskAdapter;
    Intent intent;

    /**
     * Handles initialization of the activity.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root);

        //Setup our list...
        taskList = (ListView)findViewById(R.id.task_list);
        taskAdapter = new TaskListAdapter(TaskMan.getInstance().getRepository(), this);
        taskAdapter.showUserTasks(TaskMan.getInstance().getUser());
        taskList.setAdapter(taskAdapter);
        taskList.setOnItemClickListener(this);

        ((Button)findViewById(R.id.addTask_btn)).setOnClickListener(this);
        ((Button)findViewById(R.id.mytasks_btn)).setOnClickListener(this);
        ((Button)findViewById(R.id.alltasks_btn)).setOnClickListener(this);
        
        ((EditText)findViewById(R.id.search_txt)).setOnFocusChangeListener(this);
        ((EditText)findViewById(R.id.search_txt)).addTextChangedListener(this);
        ((Button)findViewById(R.id.mytasks_btn)).setEnabled(false);
        ((Button)findViewById(R.id.alltasks_btn)).setEnabled(true);
    }

    /**
     * Handles pause event.
     */
    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * Handles resume event.
     */
    @Override
    public void onResume() {
        super.onResume();
        
        TaskMan.getInstance().getRepository().synchronize(new WebActionCallback() {
			
			public void run() {
				if(success) {
					taskAdapter.update();
					System.out.println("success!");
				} else {
					System.out.println("Failure :(");
				}
			}
			
		}, this);
        
        taskAdapter.update();
    }

    /**
     * Constructs menu options.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_root, menu);
        return true;
    }

    /**
     * Handles click events.
     */
    public void onClick(View source) {
        if(source.equals(findViewById(R.id.addTask_btn))) {
            Bundle b = new Bundle();
            //Tuck in pertinent information for the TaskActivity
            b.putParcelable("task", TaskMan.getInstance().getRepository().createTask(TaskMan.getInstance().getUser()));
            b.putString("mode", "create");
            //Create the intent and execute it
            intent = new Intent(this, TaskActivity.class);
            intent.putExtras(b);
            startActivity(intent);
            //Handle not-implemented feature notification
        }else if(source.equals(findViewById(R.id.alltasks_btn))) {
            taskAdapter.showAll();
            ((Button)findViewById(R.id.mytasks_btn)).setEnabled(true);
            ((Button)findViewById(R.id.alltasks_btn)).setEnabled(false);
            
        }else if(source.equals(findViewById(R.id.mytasks_btn))) {
            taskAdapter.showUserTasks(TaskMan.getInstance().getUser());
            ((Button)findViewById(R.id.mytasks_btn)).setEnabled(false);
            ((Button)findViewById(R.id.alltasks_btn)).setEnabled(true);
        }
    }

    /**
     * Handles click events of list items.
     */
    public void onItemClick(AdapterView<?> list, View source, int position,
            long id) {
        Log.w("RootActivity", "Element "+position+" clicked.");
        Bundle b = new Bundle();
        Task task = (Task) taskAdapter.getItem(position);
        b.putParcelable("task", task);

        b.putString("mode", "view");

        intent = new Intent(this, TaskActivity.class);
        intent.putExtras(b);
        startActivity(intent);
    }

	public void onFocusChange(View source, boolean focus) {
		if(focus)
			((EditText)source).setText("");
		else {
			if(((EditText)source).getEditableText().toString().equals("")) {
				((EditText)source).setText("Search");
				taskAdapter.setSearchTerms(null);
			}
		}
	}

	public void afterTextChanged(Editable arg0) {
		if(((EditText)findViewById(R.id.search_txt)).getEditableText().toString().equals("")) {
			taskAdapter.setSearchTerms(null);
		} else {
			taskAdapter.setSearchTerms(((EditText)findViewById(R.id.search_txt)).getEditableText().toString());
		}
	}

	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		
	}

	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}

}
