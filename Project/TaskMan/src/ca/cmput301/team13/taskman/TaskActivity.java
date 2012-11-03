package ca.cmput301.team13.taskman;

import ca.cmput301.team13.taskman.model.Task;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class TaskActivity extends Activity implements OnClickListener {
	
	private Task task;
	private String mode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        setMode(extras.getString("mode"));
        
        task = (Task) extras.getParcelable("task");
        
        if (getMode().equals("edit") || getMode().equals("create")) {
        	setContentView(R.layout.activity_edit_task);
        	setEditingFields();
        	((Button)findViewById(R.id.save_button)).setOnClickListener(this);
        } else {
        	setContentView(R.layout.activity_view_task);
        }
        ((Button)findViewById(R.id.cancel_button)).setOnClickListener(this);
    }

    private void setEditingFields() {
    	//Set the Title
		((EditText)findViewById(R.id.entry_title)).setText(task.getTitle());
		//Set the Description
		((EditText)findViewById(R.id.entry_description)).setText(task.getDescription());
		
		//TODO: Set the requirements
	}
    
    private void setViewingFields() {
    	//TODO: implement Task Viewing
    }

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_task, menu);
        return true;
    }

	private String getMode() {
		return mode;
	}

	private void setMode(String mode) {
		this.mode = mode;
	}
	
	public void onPause() {
		super.onPause();
		if(getMode().equals("create")) {
			//Destroy the associated Task before returning
			TaskMan.getInstance().getRepository().removeTask(task);
			//This means that pressing Save should switch the mode to "edit", before leaving the Activity
		}
	}
	
	/**
	 * Saves the current Task
	 */
	private void saveTask() {
		String taskTitle = ((EditText)findViewById(R.id.entry_title)).getText().toString();
		String taskDescription = ((EditText)findViewById(R.id.entry_description)).getText().toString();
		//TODO: Validation? Ensure each Task has a title and a single requirement, at least?
		//Update the parceled Task
		if(getMode().equals("edit") || getMode().equals("create")) {
			task.delaySaves(true);
			task.setTitle(taskTitle);
			task.setDescription(taskDescription);
			task.delaySaves(false);
		}
		setMode("edit");
		super.finish();
	}
	
	private void cancelTask() {
		TaskMan.getInstance().getRepository().removeTask(task);
		super.finish();
	}
	
	@Override
	public void onBackPressed() {
		cancelTask();
	}
	
	public void onClick(View source) {
		if(source.equals(findViewById(R.id.save_button))) {
			System.out.println("saving");
			saveTask();
		}else if(source.equals(findViewById(R.id.cancel_button))) {
			System.out.println("cancelling");
			cancelTask();
		}
	}
	
}
