package ca.cmput301.team13.taskman;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import ca.cmput301.team13.taskman.model.Requirement.contentType;
import ca.cmput301.team13.taskman.model.Task;

public class TaskActivity extends Activity implements OnClickListener {
	
	private Task task;
	private String mode;
	private RequirementListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        setMode(extras.getString("mode"));
        
        task = (Task) extras.getParcelable("task");
        
        //If we're in an editing mode, populate the editing controls and consider the appropriate layout
        if (getMode().equals("edit") || getMode().equals("create")) {
        	setContentView(R.layout.activity_edit_task);
        	setEditingFields();
        	((Button)findViewById(R.id.save_button)).setOnClickListener(this);
        } else {
        	setContentView(R.layout.activity_view_task);
        }
        //Prevent loss of focus when selecting an EditText field
        ((ListView)findViewById(R.id.requirement_list)).setDescendantFocusability(ListView.FOCUS_AFTER_DESCENDANTS);
        ((LinearLayout)findViewById(R.id.basic_info_entry_panel)).setVisibility(View.GONE);
        ((Button)findViewById(R.id.cancel_button)).setOnClickListener(this);
    }

    private void setEditingFields() {
    	//Set the Title
		((EditText)findViewById(R.id.entry_title)).setText(task.getTitle());
		//Set the Description
		((EditText)findViewById(R.id.entry_description)).setText(task.getDescription());
		
		//Add onClickListeners
		((Button)findViewById(R.id.save_button)).setOnClickListener(this);
		((Button)findViewById(R.id.cancel_button)).setOnClickListener(this);
		
		((ImageButton)findViewById(R.id.req_addTxt_btn)).setOnClickListener(this);
		((ImageButton)findViewById(R.id.req_addImg_btn)).setOnClickListener(this);
		((ImageButton)findViewById(R.id.req_addAud_btn)).setOnClickListener(this);
		//TODO: Set the requirements
		adapter = new RequirementListAdapter(task, mode, this);
		((ListView)findViewById(R.id.requirement_list)).setAdapter(adapter);
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
	
	private void saveRequirementList() {
		int numRequirements = adapter.getCount();
		for(int i=0; i<numRequirements; i++) {
//			adapter.getItem(i)
		}
	}
	
	/**
	 * Return to the previous activity without saving
	 */
	private void cancelTask() {
		super.finish();
	}
	
	public void onClick(View source) {
		if(source.equals(findViewById(R.id.save_button))) {
			saveTask();
		} else if(source.equals(findViewById(R.id.cancel_button))) {
			cancelTask();
		} else if  (source.getId() == R.id.req_addTxt_btn) {
			TaskMan.getInstance().getRepository().createRequirement(TaskMan.getInstance().getUser(), task, contentType.text);
			adapter.update();
		} else if  (source.getId() == R.id.req_addImg_btn) {
			TaskMan.getInstance().getRepository().createRequirement(TaskMan.getInstance().getUser(), task, contentType.image);
			adapter.update();
		} else if  (source.getId() == R.id.req_addAud_btn) {
			TaskMan.getInstance().getRepository().createRequirement(TaskMan.getInstance().getUser(), task, contentType.audio);
			adapter.update();
		} else {
			InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
		    inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
		}
	}
	
}
