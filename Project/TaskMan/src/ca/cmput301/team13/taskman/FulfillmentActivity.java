/*
 * This file is part of TaskMan
 * 
 * This file contains the fulfillment activity base class.  The class
 * handles the tasks that are common to all activities that allow users
 * to make fulfillments.  The implementation is designed to interact 
 * with the intent as constructed by the FulfillmentIntentFactory factory.
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

package ca.cmput301.team13.taskman;

import ca.cmput301.team13.taskman.model.Fulfillment;
import ca.cmput301.team13.taskman.model.Requirement;
import android.app.Activity;
import android.os.Bundle;

/**
 * FulfillmentActivity is the base class for activities that
 * allow the user to make a fulfillment for a requirement.
 * This base class handles receiving of the parcelled
 * {@link Requirement} used to launch the activity, the creation
 * of the {@link Fulfillment} object, and removal of the fulfillment
 * if the activity is cancelled.
 */
public abstract class FulfillmentActivity extends Activity {

	/**
	 * The parcelled {@link Requirement}.
	 */
    protected Requirement requirement;
    
    /**
     * The newly created {@link Fulfillment} for the requirement.
     */
    protected Fulfillment fulfillment;

    /**
     * Receives the parcelled requirement object
     * and adds a fulfillment to the requirement.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requirement = (Requirement) this.getIntent().getParcelableExtra("requirement");
        
    }

    /**
     * Create the new fulfillment
     */
    public void save() {
        fulfillment = TaskMan.getInstance().getRepository().addFulfillmentToRequirement(
                TaskMan.getInstance().getUser(),
                requirement);
    }
    
    /**
     * Cancel the Activity.
     */
    public void cancel() {
        finish();
    }
}
