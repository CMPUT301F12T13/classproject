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

package ca.cmput301.team13.taskman.test;

import utils.Identifiers;
import android.util.Log;
import ca.cmput301.team13.taskman.TaskMan;
import ca.cmput301.team13.taskman.model.Requirement;
import ca.cmput301.team13.taskman.model.Task;
import ca.cmput301.team13.taskman.model.TaskFilter;
import ca.cmput301.team13.taskman.model.User;
import ca.cmput301.team13.taskman.model.VirtualRepository;

public class TaskTests extends BaseSetup {
    public TaskTests() {
        super();
    }
    
    /**
     * Test Task creation functionality: creating tasks
     * Dependencies: VirtualRepository, Task, User
     */
    public void _testCreation() {
        TaskFilter tf = new TaskFilter();
        tf.activateAll();
        //Compare Task counts before and after creation
        int expectedTaskCount = (vr.getTasksForFilter(new TaskFilter())).size() + 1;
        Task t = vr.createTask(testUser);
        //If the task wasn't created properly, error
        if(t == null || expectedTaskCount != (vr.getTasksForFilter(new TaskFilter())).size()) {
            Log.i("TestCaseError", "Users are not able to create tasks.");
            fail();
        }
        
        //TODO: Add tests for both local/remote creation
    }
    
    
    /**
     * Test Task deletion functionality: deleting tasks and the permissions surrounding those operations
     * Dependencies: VirtualRepository, Task, User
     */
    public void _testDeletion() {
        //Create a task from this user
        Task userCreatedTask     = vr.createTask(testUser);
        Task externalTask         = vr.createTask(new User(Identifiers.randomString(15)));
        
        //5.1: Attempt to delete someone else's Task. Should fail
        vr.removeTask(externalTask);
        if(!vr.taskExists(externalTask)) {
            Log.i("TestCaseError", "Users are able to delete tasks that were not created by themselves.");
            fail();
        }
        
        //5.2 Attempt to delete a Task with no fulfillments
        vr.removeTask(userCreatedTask);
        if(vr.taskExists(userCreatedTask)) {
            Log.i("TestCaseError", "Users are not able to delete their own tasks.");
            fail();
        }
    }
    
    public void _testUpdating() {
        Task t = vr.createTask(testUser);
        
        //---------------------------------
        //Test delaySaves
        //---------------------------------
        t.delaySaves(true);
        t.setDescription("This is a new description.");
        if(vr.getTaskUpdate(t).getDescription().equals("This is a new description.")) {
            Log.i("TestCaseError", "BackedObject: delaySaves is not working properly.");
            fail();
        }
        
        //---------------------------------
        //Test basic information updates
        //---------------------------------
        //4.2 Change the title of a Task you've created
        //4.3 Change the description of a Task you've created
        Task t2             = vr.createTask(testUser);
        String title         = "T1";
        String description     = "D2";
        t2.setTitle(title);
        t2.setDescription(description);
        Task updatedTask = vr.getTaskUpdate(t2);
        //Make sure the new basic fields have been set properly
        if(!updatedTask.getTitle().equals(title) || !updatedTask.getDescription().equals(description)) {
            Log.i("TestCaseError", "Users are not able to update tasks.");
            fail();
        }
        
        //---------------------------------
        //Test basic information updates
        //---------------------------------
        //4.6 Add a requirement to a Task you've created
        Requirement imageReq = vr.addRequirementToTask(testUser, t2, Requirement.contentType.image);
        if(t2.getRequirementCount() != 1) {
            Log.i("TestCaseError", "Users are not able to add Requirements to Tasks");
            fail();
        }
        //4.5 Delete a Requirement on a Task you've created, leaving 1 remaining
        vr.removeRequirement(imageReq);
        //TODO: Implement Requirement removal *without* requiring a manual update?
        t2 = vr.getTaskUpdate(t2);
        if(t2.getRequirementCount() > 0) {
            Log.i("TestCaseError", "Users cannot remove Requirements from Tasks.");
            fail();
        }
        
        //---------------------------------
        //Test Permissions
        //---------------------------------
        //4.1 Attempt to edit someone else's Task. Should fail
        Task t3     = vr.createTask(new User(Identifiers.randomString(15)));
        title         = "T2";
        
        try {
            t3.setTitle(title);
            t3 = vr.getTaskUpdate(t3);
        } catch(RuntimeException e) { }
        if(t3.getTitle().equals(title)) {
            Log.i("TestCaseError", "Users can update the Tasks of foreign Users.");
            fail();
        }
        
    }
    
    public void _testFulfillments() {
        
        //Prepare a personal Task with all three types of Requirements
        String ownError = "Users cannot add fulfillments to their own Requirements";
        Task t1 = vr.createTask(testUser);
        Requirement imageRequirement = vr.addRequirementToTask(testUser, t1, Requirement.contentType.image);
        Requirement audioRequirement = vr.addRequirementToTask(testUser, t1, Requirement.contentType.audio);
        Requirement textRequirement  = vr.addRequirementToTask(testUser, t1, Requirement.contentType.text);
        
        //Prepare a foreign Task with an image Requirement
        User foreignUser = new User(Identifiers.randomString(15));
        //Task t2 = vr.createTask(foreignUser);
        //Can't do this, because it violates permissions. How can we test these?
        //vr.addRequirementToTask(foreignUser, t2, Requirement.contentType.image);
        
        //3.1 Submit text to a Task you've created yourself
        //3.2 Submit a photo to a Task you've created yourself
        //3.3 Submit audio to a Task you've created yourself
        vr.addFulfillmentToRequirement(testUser, textRequirement);
        vr.addFulfillmentToRequirement(testUser, imageRequirement);
        vr.addFulfillmentToRequirement(testUser, audioRequirement);
        
        if(imageRequirement.getFullfillmentCount() != 1) {
            Logging.logError(ownError);
            fail();
        }
        if(audioRequirement.getFullfillmentCount() != 1) {
            Logging.logError(ownError);
            fail();
        }
        if(textRequirement.getFullfillmentCount() != 1) {
            Logging.logError(ownError);
            fail();
        }
        
        //Can't test 3.4 or 3.5 until we can override permissions to create a fake foreign task (or get one from the cloud)
    }
    
    public void tearDown() { }
    
}
