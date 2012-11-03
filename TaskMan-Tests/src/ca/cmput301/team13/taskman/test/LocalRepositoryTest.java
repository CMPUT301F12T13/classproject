package ca.cmput301.team13.taskman.test;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import ca.cmput301.team13.taskman.RootActivity;
import ca.cmput301.team13.taskman.model.Fulfillment;
import ca.cmput301.team13.taskman.model.Requirement;
import ca.cmput301.team13.taskman.model.Task;
import ca.cmput301.team13.taskman.model.User;
import ca.cmput301.team13.taskman.model.VirtualRepository;

public class LocalRepositoryTest extends ActivityInstrumentationTestCase2<RootActivity> {
	private Context context = null;
	private VirtualRepository virtualRepository;
	private User testUser;
	
	public LocalRepositoryTest() {
		super(RootActivity.class);
	}
	
	public void setUp() {
		this.context = getInstrumentation().getTargetContext();
		this.testUser = new User("Tester");
		this.virtualRepository = new VirtualRepository(this.context);
	}
	
	/**
	 * Test Fulfillment functionality: creating, updating, and consuming Fulfillments
	 * Dependencies: VirtualRepository, Task, Requirement, User
	 */
	public void testFulfillments() {
		//Create a new fulfillment and set some text content
		Task task = this.virtualRepository.createTask(this.testUser);
		Requirement newRequirement = this.virtualRepository.addRequirementToTask(this.testUser, task, Requirement.contentType.text);
		Fulfillment textFulfillment = this.virtualRepository.addFulfillmentToRequirement(this.testUser, newRequirement);
		textFulfillment.setText("Testing this text.");
	}
	
	public void tearDown() { }
	
}
