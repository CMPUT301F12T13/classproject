package ca.cmput301.team13.taskman;

import ca.cmput301.team13.taskman.model.Task;
import ca.cmput301.team13.taskman.model.Requirement.contentType;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

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
		} else if(getMode().equals("edit")){
			//Propagate all Changes to the Task object
			task.delaySaves(true);
			task.setTitle(((EditText)findViewById(R.id.entry_title)).getText().toString());
			task.setDescription(((EditText)findViewById(R.id.entry_description)).getText().toString());
			task.delaySaves(false);
			
			
		}
	}
	
	public void onClick(View source) {
		if(source.getId() == R.id.save_button) {
			//So that the Task isn't deleted when we leave on create
			setMode("edit");
			finish();
		} else if (source.getId() == R.id.cancel_button) {
			finish();
		} else if  (source.getId() == R.id.req_addTxt_btn) {
			TaskMan.getInstance().getRepository().createRequirement(TaskMan.getInstance().getUser(), task, contentType.text);
			adapter.update();
		} else if  (source.getId() == R.id.req_addImg_btn) {
			TaskMan.getInstance().getRepository().createRequirement(TaskMan.getInstance().getUser(), task, contentType.image);
			adapter.update();
		} else if  (source.getId() == R.id.req_addAud_btn) {
			TaskMan.getInstance().getRepository().createRequirement(TaskMan.getInstance().getUser(), task, contentType.audio);
			adapter.update();
		}
	}
	
}
