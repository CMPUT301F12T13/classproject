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

import ca.cmput301.team13.taskman.model.Task;
import android.os.Bundle;
import android.os.Parcelable;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

public class RootActivity extends Activity implements OnClickListener, OnItemClickListener {
	ListView taskList;
	TaskListAdapter taskAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root);
        
        //Setup our list...
        taskList = (ListView)findViewById(R.id.task_list);
        taskAdapter = new TaskListAdapter(TaskMan.getInstance().getRepository(), this);
        taskList.setAdapter(taskAdapter);
        taskList.setOnItemClickListener(this);
        
        ((Button)findViewById(R.id.addTask_btn)).setOnClickListener(this);
        
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	taskAdapter.update();
    	
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_root, menu);
        return true;
    }

	public void onClick(View source) {
		if(source.equals(findViewById(R.id.addTask_btn))) {
			Bundle b = new Bundle();
			//Tuck in pertinent information for the TaskActivity
			b.putParcelable("task", TaskMan.getInstance().getRepository().createTask(TaskMan.getInstance().getUser()));
			b.putString("mode", "create");
			//Create the intent and execute it
			Intent i = new Intent(this, TaskActivity.class);
			i.putExtras(b);
			startActivity(i);
			
		}
	}

	public void onItemClick(AdapterView<?> list, View source, int position,
			long id) {
		Log.w("RootActivity", "Element "+position+" clicked.");
		Bundle b = new Bundle();
		b.putParcelable("task", (Task) taskAdapter.getItem(position));
		b.putString("mode", "view");
		
		Intent i = new Intent(this, TaskActivity.class);
		i.putExtras(b);
		startActivity(i);
	}

}
