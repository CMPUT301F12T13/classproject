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


import ca.cmput301.team13.taskman.RequirementListAdapter;
import ca.cmput301.team13.taskman.RootActivity;
import ca.cmput301.team13.taskman.model.Requirement;
import ca.cmput301.team13.taskman.model.Task;
import ca.cmput301.team13.taskman.model.Requirement.contentType;

public class RequirementListAdapterTests extends BaseSetup {
	private RootActivity ra;
    private RequirementListAdapter adapter;
	private Task task;
    
    public RequirementListAdapterTests() {
        super();
        ra = new RootActivity();
    }
    
    public void setUp() {
        super.setUp();
        
        task = vr.createTask(testUser);
        
        for(int i = 0; i < 5; i++) {
	        vr.addRequirementToTask(testUser, task, contentType.text);
	        vr.addRequirementToTask(testUser, task, contentType.image);
	        vr.addRequirementToTask(testUser, task, contentType.audio);
	        vr.addRequirementToTask(testUser, task, contentType.video);
        }
        
        adapter = new RequirementListAdapter(task, "view", ra);
    }
    
    public void test_all_requirements_added() {
    	if(adapter.getCount() != 20) {
    		fail();
    	}
    }
    
    public void tearDown() {
    	adapter = null;
    }
}
