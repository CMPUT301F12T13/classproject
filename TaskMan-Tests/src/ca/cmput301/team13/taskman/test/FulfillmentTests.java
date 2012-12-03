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

import java.util.Date;
import java.util.Arrays;

import android.graphics.Bitmap;
import ca.cmput301.team13.taskman.model.Fulfillment;
import ca.cmput301.team13.taskman.model.Requirement;
import ca.cmput301.team13.taskman.model.Task;
import ca.cmput301.team13.taskman.model.Requirement.contentType;

public class FulfillmentTests extends BaseSetup {
    private Task task;
	short[] data = {0, 1, 2, 100, 7816};
    
    public FulfillmentTests() {
        super();
    }
    
    public void setUp() {
        super.setUp();
        task = vr.createTask(testUser);
    }
    
    public void test_creation () {
    	Requirement r = vr.addRequirementToTask(testUser, task, contentType.text);
        vr.addFulfillmentToRequirement(testUser, r);
        if (r.getFullfillmentCount() != 1) {
            fail();
        }
    }
    
    public void test_deletion() {
    	Requirement r = vr.addRequirementToTask(testUser, task, contentType.text);
        Fulfillment f = vr.addFulfillmentToRequirement(testUser, r);
        
        String id = f.getId();
        vr.removeFulfillment(f);
        r = vr.getRequirementUpdate(r);
        
        if (r.getFullfillmentCount() != 0) {
            fail();
        }
        
        if (vr.getFulfillment(id) != null) {
            fail();
        }
        
        task.removeRequirement(r);
    }
    
    public void test_modification_text() {
    	Requirement r = vr.addRequirementToTask(testUser, task, contentType.text);
    	Fulfillment f = vr.addFulfillmentToRequirement(testUser, r);
    	
    	f.setText("my string");
    	if(f.getText() != "my string") {
    	}
    	
    	f.setText("");
    	if(f.getText() != "") {
    	}
    	
    	task.removeRequirement(r);
    }
    public void test_modification_image() {
    	Requirement r = vr.addRequirementToTask(testUser, task, contentType.image);
    	Fulfillment f = vr.addFulfillmentToRequirement(testUser, r);
    	
    	Bitmap b = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
    	f.setImage(b);
    	Bitmap b_got_back = f.getImage();
    	if(b_got_back == null) {
    		fail();
    	}
    	
    	if(b_got_back.getWidth() != b.getWidth() ||
    			b_got_back.getHeight() != b.getHeight()) {
    		fail();
    	}
    	task.removeRequirement(r);
    }
    public void test_modification_audio() {
    	short[] data_back;
    	
    	Requirement r = vr.addRequirementToTask(testUser, task, contentType.audio);
    	Fulfillment f = vr.addFulfillmentToRequirement(testUser, r);
    	
    	f.setAudio(data);
    	data_back = f.getAudio();
    	if(!Arrays.asList(data).equals(Arrays.asList(data_back))) {
    		fail();
    	}
    	task.removeRequirement(r);
    }
    public void test_modification_video() {
    	short[] data_back;
    	
    	Requirement r = vr.addRequirementToTask(testUser, task, contentType.video);
    	Fulfillment f = vr.addFulfillmentToRequirement(testUser, r);
    	
    	f.setVideo(data);
    	data_back = f.getVideo();
    	if(!Arrays.asList(data).equals(Arrays.asList(data_back))) {
    		fail();
    	}
    	task.removeRequirement(r);
    }
    
    public void test_date_creation() {
    	Requirement r = vr.addRequirementToTask(testUser, task, contentType.text);
    	Fulfillment f = vr.addFulfillmentToRequirement(testUser, r);
    	Date creationDate = f.getCreatedDate();
    	
    	sleep();
    	
    	f.setText("text data");
    	
    	if(f.getCreatedDate().compareTo(creationDate) != 0) {
    		fail();
    	}
    }
    public void test_date_modification() {
    	Requirement r = vr.addRequirementToTask(testUser, task, contentType.text);
    	Fulfillment f = vr.addFulfillmentToRequirement(testUser, r);
    	Date modificationDate = f.getLastModifiedDate();
    	
    	if(modificationDate.compareTo(f.getCreatedDate()) != 0) {
    		fail();
    	}
    	
    	sleep();
    	
    	f.setText("text data");
    	
    	if(modificationDate.compareTo(f.getLastModifiedDate()) == 0) {
    		fail();
    	}
    }

    public void tearDown() {
    	vr.removeTask(task);
    }
}
