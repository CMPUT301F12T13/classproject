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
import java.util.Arrays;

import utils.Identifiers;
import android.content.Context;
import android.graphics.Bitmap;
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
import ca.cmput301.team13.taskman.model.Requirement.contentType;

public class FulfillmentTests extends BaseSetup {
    private Task task;
    private Requirement req_text;
    private Requirement req_audio;
    private Requirement req_image;
    private Requirement req_video;
	short[] data = {0, 1, 2, 100, 7816};
    
    public FulfillmentTests() {
        super();
    }
    
    public void setUp() {
        super.setUp();
        task = vr.createTask(testUser);
        req_text  = vr.addRequirementToTask(testUser, task, contentType.text);
        req_audio = vr.addRequirementToTask(testUser, task, contentType.audio);
        req_image = vr.addRequirementToTask(testUser, task, contentType.image);
        req_video = vr.addRequirementToTask(testUser, task, contentType.video);
    }
    
    public void test_creation () {
        vr.addFulfillmentToRequirement(testUser, req_text);
        if (req_text.getFullfillmentCount() != 1) {
            fail();
        }
    }
    
    public void test_deletion() {
    	Requirement r = vr.addRequirementToTask(testUser, task, contentType.text);
        Fulfillment f = vr.addFulfillmentToRequirement(testUser, r);
        
        String id = f.getId();
        r.removeFulfillment(f);
        
        if (r.getFullfillmentCount() != 0) {
            fail();
        }
        
        if (vr.getFulfillment(id) != null) {
            fail();
        }
        
        task.removeRequirement(r);
    }
    
    public void _test_modification_text() {
    	vr.addFulfillmentToRequirement(testUser, req_text);
    	Fulfillment f = req_text.getFulfillment(0);
    	
    	f.setText("my string");
    	if(f.getText() != "my string") {
    	}
    	
    	f.setText("");
    	if(f.getText() != "") {
    	}
    	
    	req_text.removeFulfillment(f);
    }
    public void _test_modification_image() {
    	Fulfillment f = vr.addFulfillmentToRequirement(testUser, req_image);
    	
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
    	req_image.removeFulfillment(f);
    }
    public void _test_modification_audio() {
    	short[] data_back;
    	
    	Fulfillment f = vr.addFulfillmentToRequirement(testUser, req_audio);
    	
    	f.setAudio(data);
    	data_back = f.getAudio();
    	if(!Arrays.asList(data).equals(Arrays.asList(data_back))) {
    		fail();
    	}
    	req_audio.removeFulfillment(f);
    }
    public void _test_modification_video() {
    	short[] data_back;
    	
    	Fulfillment f = vr.addFulfillmentToRequirement(testUser, req_video);
    	
    	f.setVideo(data);
    	data_back = f.getVideo();
    	if(!Arrays.asList(data).equals(Arrays.asList(data_back))) {
    		fail();
    	}
    	req_video.removeFulfillment(f);
    }
    
    public void test_date_creation() {
    }
    public void test_date_modification() {
    }
}
