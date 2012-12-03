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


import java.util.ArrayList;
import java.util.Set;

import ca.cmput301.team13.taskman.model.Requirement;
import ca.cmput301.team13.taskman.model.Task;
import ca.cmput301.team13.taskman.model.Requirement.contentType;

public class SearchTests extends BaseSetup {
    Set<String> expected_ids;
    
    public SearchTests() {
        super();
    }
    
    public void setUp() {
        super.setUp();

        Task t;

        t = vr.createTask(testUser);
        t.setDescription("this contains the keyword");
        expected_ids.add(t.getId());

        t = vr.createTask(testUser);
        t.setDescription("this does not.");

        t = vr.createTask(testUser);
        t.setDescription("the keyword is here too.");
        expected_ids.add(t.getId());

        t = vr.createTask(testUser);
        t.setDescription("");

        t = vr.createTask(testUser);
        t.setDescription("invisible");
    }

    public void test_search() {
        expected_ids = null;
        // TODO: Try several searches and confirm proper findings.
    }

    public void tearDown() {
    }
}
