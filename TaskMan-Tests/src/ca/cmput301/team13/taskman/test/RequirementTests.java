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

import ca.cmput301.team13.taskman.model.Requirement;
import ca.cmput301.team13.taskman.model.Task;
import ca.cmput301.team13.taskman.model.Requirement.contentType;

public class RequirementTests extends BaseSetup {
    private Task task;
    public RequirementTests() {
        super();
    }
    
    public void setUp() {
        super.setUp();
        task = vr.createTask(testUser);
    }
    
    public void test_creation() {
        task = vr.getTaskUpdate(task);
        
        Requirement rt = vr.addRequirementToTask(testUser, task, contentType.text);
        Requirement ri = vr.addRequirementToTask(testUser, task, contentType.image);
        Requirement ra = vr.addRequirementToTask(testUser, task, contentType.audio);
        Requirement rv = vr.addRequirementToTask(testUser, task, contentType.video);
        
        if(task.getRequirementCount() != 4) {
            fail();
        }
        
        if(vr.getRequirement(rt.getId()).getContentType() != contentType.text) {
            fail();
        }
        if(vr.getRequirement(ri.getId()).getContentType() != contentType.image) {
            fail();
        }
        if(vr.getRequirement(ra.getId()).getContentType() != contentType.audio) {
            fail();
        }
        if(vr.getRequirement(rv.getId()).getContentType() != contentType.video) {
            fail();
        }
        
        task.removeRequirement(rt);
        task.removeRequirement(ri);
        task.removeRequirement(ra);
        task.removeRequirement(rv);
    }
    public void test_deletion() {
        task = vr.getTaskUpdate(task);
        
        Requirement r = vr.addRequirementToTask(testUser, task, contentType.text);
        String id = r.getId();
        vr.removeRequirement(r);
        task = vr.getTaskUpdate(task);
        
        if(vr.getRequirement(id) != null) {
            fail();
        }
        
        if(task.getRequirementCount() != 0) {
            fail();
        }
    }

    public void test_modification_add_fulfillment_text() {
        task = vr.getTaskUpdate(task);
        
        Requirement r = vr.addRequirementToTask(testUser, task, contentType.text);
        vr.addFulfillmentToRequirement(testUser, r);
        r = vr.getRequirementUpdate(r);
        
        if(r.getFullfillmentCount() == 0) {
            fail();
        }
        
        vr.removeRequirement(r);
    }

    public void test_modification_remove_fulfillment() {
        task = vr.getTaskUpdate(task);
        
        Requirement r = vr.addRequirementToTask(testUser, task, contentType.text);
        vr.addFulfillmentToRequirement(testUser, r);
        r = vr.getRequirementUpdate(r);
        
        vr.removeRequirement(r);
        task = vr.getTaskUpdate(task);
        
        if(task.getRequirementCount() != 0) {
            fail();
        }
    }
    
    public void test_date_creation() {
        // TODO: implement
    }
    public void test_date_modification() {
        // TODO: implement
    }
    
    public void tearDown() { }
}
