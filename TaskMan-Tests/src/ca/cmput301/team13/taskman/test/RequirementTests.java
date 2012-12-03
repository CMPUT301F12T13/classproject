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
import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import ca.cmput301.team13.taskman.RootActivity;
import ca.cmput301.team13.taskman.TaskMan;
import ca.cmput301.team13.taskman.model.Requirement;
import ca.cmput301.team13.taskman.model.Task;
import ca.cmput301.team13.taskman.model.TaskFilter;
import ca.cmput301.team13.taskman.model.User;
import ca.cmput301.team13.taskman.model.VirtualRepository;
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
    
    public void testCreation() {
    	//vr.addRequirementToTask(testUser, task, contentType.text);
    	//vr.getRequirement(requirementId)
    }
    public void testDeletion() {
    }
    
    public void date_creation() {
    }
    public void date_modification() {
    }
    
    public void tearDown() { }
}
