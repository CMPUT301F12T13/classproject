package ca.cmput301.team13.taskman;

import ca.cmput301.team13.taskman.model.Task;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class TaskActivity extends Activity {
	
	private Task task;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        
        task = (Task) extras.getParcelable("task");
        
        if (extras.getString("mode").equals("edit")) {
        	setContentView(R.layout.activity_edit_task);
        } else {
        	setContentView(R.layout.activity_view_task);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_task, menu);
        return true;
    }
}
