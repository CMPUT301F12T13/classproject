package ca.cmput301.team13.taskman;

import ca.cmput301.team13.taskman.model.Task;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class TaskActivity extends Activity {
	
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
        } else {
        	setContentView(R.layout.activity_view_task);
        }
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
		if(getMode().equals("create")) {
			//Destroy the associated Task before returning
			TaskMan.getInstance().getRepository().removeTask(task);
			//This means that pressing Save should switch the mode to "edit", before leaving the Activity
		}
	}
	
	public void OnClick(View source) {
		
	}
	
}
