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
 * This base class handles receiving of the parcelled requirement
 * used to launch the activity, the creation of the fulfillment
 * object, and removal of the fulfillment if the activity is
 * cancelled.
 */
public abstract class FulfillmentActivity extends Activity {

    protected Requirement requirement;
    protected Fulfillment fulfillment;
    protected boolean successful;

    /**
     * Receives the parcelled requirement object
     * and adds a fulfillment to the requirement.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.successful = false;
        this.requirement = (Requirement) this.getIntent().getParcelableExtra("requirement");

        fulfillment = TaskMan.getInstance().getRepository().addFulfillmentToRequirement(
                TaskMan.getInstance().getUser(),
                requirement);
    }

    /**
     * Removes the fulfillment from the requirement if the
     * <code> successful </code> member variable is false.
     */
    @Override
    public void onStop() {
        super.onStop();
        if (!successful) {
            requirement.removeFulfillment(fulfillment);
        }
    }
}
