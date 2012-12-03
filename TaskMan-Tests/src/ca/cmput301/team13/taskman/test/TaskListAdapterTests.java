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

import ca.cmput301.team13.taskman.TaskListAdapter;

public class TaskListAdapterTests extends BaseSetup {
    private TaskListAdapter adapter;
    
    public TaskListAdapterTests() {
        super();
    }
    
    public void setUp() {
        super.setUp();
        clearRepo();
        
        for(int i = 0; i < 20; i++) {
	        vr.createTask(testUser);
        }
        
        adapter = new TaskListAdapter(vr, context);
    }
    
    public void test_all_tasks_added() {
    	if(adapter.getCount() != 20) {
    		fail();
    	}
    }

    public void tearDown() {
    	adapter = null;
    	clearRepo();
    }

    protected void clearRepo() {
        // TODO: implement
    }
}
