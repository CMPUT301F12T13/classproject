package ca.cmput301.team13.taskman.test;

import utils.Identifiers;
import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import ca.cmput301.team13.taskman.RootActivity;
import ca.cmput301.team13.taskman.TaskMan;
import ca.cmput301.team13.taskman.model.Fulfillment;
import ca.cmput301.team13.taskman.model.Requirement;
import ca.cmput301.team13.taskman.model.Task;
import ca.cmput301.team13.taskman.model.TaskFilter;
import ca.cmput301.team13.taskman.model.User;
import ca.cmput301.team13.taskman.model.VirtualRepository;

public class TaskManagementTest extends ActivityInstrumentationTestCase2<RootActivity> {
	private Context context = null;
	private VirtualRepository vr;
	private User testUser;
	
	public TaskManagementTest() {
		super(RootActivity.class);
		vr = new VirtualRepository(this.context);
	}
	
	public void setUp() {
		context = getInstrumentation().getTargetContext();
		testUser = TaskMan.getInstance().getUser();
	}
	
	/**
	 * Test Task deletion functionality: deleting tasks and the permissions surrounding those operations
	 * Dependencies: VirtualRepository, Task, User
	 */
	public void testDeletion() {
		//Create a task from this user
		Task userCreatedTask = vr.createTask(testUser);
		Task externalTask = vr.createTask(new User(Identifiers.randomString(15)));
		//If the user was able to delete another user's task, there's a problem
		vr.removeTask(externalTask);
		if(!vr.taskExists(externalTask)) {
			throw new RuntimeException("Users are able to delete tasks that were not created by themselves.");
		}
		
		//If the user cannot delete their own task, there's a problem
		vr.removeTask(userCreatedTask);
		if(vr.taskExists(userCreatedTask)) {
			throw new RuntimeException("Users are not able to delete their own tasks.");
		}
	}
	
	/**
	 * Test Task creation functionality: creating tasks
	 * Dependencies: VirtualRepository, Task, User
	 */
	public void testCreation() {
		TaskFilter tf = new TaskFilter();
		tf.activateAll();
		//Compare Task counts before and after creation
		int expectedTaskCount = (vr.getTasksForFilter(new TaskFilter())).size() + 1;
		Task t = vr.createTask(testUser);
		//If the task wasn't created properly, error
		if(t == null || expectedTaskCount != (vr.getTasksForFilter(new TaskFilter())).size()) {
			throw new RuntimeException("Users are not able to create tasks.");
		}
		
		//TODO: Add tests for both local/remote creation
	}
	
	
	public void testUpdating() {
		Task t = vr.createTask(testUser);
		
		//---------------------------------
		//Test delaySaves
		//---------------------------------
		t.delaySaves(true);
		t.setDescription("This is a new description.");
		if(vr.getTaskUpdate(t).getDescription().equals("This is a new description.")) {
			throw new RuntimeException("BackedObject: delaySaves is not working properly.");
		}
		
		//---------------------------------
		//Test basic information updates
		//---------------------------------
		Task t2 = vr.createTask(testUser);
		String title = "T1";
		String description = "D2";
		t.setTitle(title);
		t.setDescription(description);
		Task updatedTask = vr.getTaskUpdate(t2);
		//Make sure the new basic fields have been set properly
		Log.i("Test", updatedTask.getTitle());
		if(!updatedTask.getTitle().equals(title) || !updatedTask.getDescription().equals(description)) {
//			throw new RuntimeException("Users are not able to update tasks.");
		}
		Requirement imageReq = vr.addRequirementToTask(testUser, t2, Requirement.contentType.image);
		if(t2.getRequirementCount() != 1) {
//			throw new RuntimeException("Users are not able to add Requirements to Tasks");
		}
		vr.removeRequirement(imageReq);
		if(t2.getRequirementCount() > 0) {
//			throw new RuntimeException("Users cannot remove Requirements from Tasks.");
		}
	}
	
	public void tearDown() { }
	
}
