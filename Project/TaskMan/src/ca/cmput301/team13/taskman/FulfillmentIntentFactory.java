/*
 * This file is part of TaskMan
 * 
 * This file contains a factory for intents for fulfillment activities.
 * Note that the factory pattern has been employed in a slightly unusual
 * way: instead of directly producing instances of classes that extend
 * FulfillmentActivities, the factory creates intents to launch these
 * activities.
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

import android.app.Activity;
import android.content.Intent;
import ca.cmput301.team13.taskman.model.Requirement;

/**
 * FulfillmentIntentFactory creates intents for launching fulfillment
 * activities (subclasses of FulfillmentActivity).
 */
public class FulfillmentIntentFactory {

    private Activity source;

    /**
     * Creates an instance of FulfillmentIntentFactory.
     * @param src The intent that owns the factory; is
     * used to create the intents.
     */
    FulfillmentIntentFactory(Activity src) {
        source = src;
    }

    /**
     * Creates an intent for launching an appropriate
     * fulfillment activity for a given requirement.
     * @param req The requirement to use to select an activity
     * @return An intent to launch the fulfillment activity
     */
    public Intent createIntent(Requirement req) {
        Intent i = null;

        if (req.getContentType() == Requirement.contentType.image) {
            i = new Intent(source, ImageCaptureActivity.class);
        }
        else if(req.getContentType() == Requirement.contentType.audio) {
            i = new Intent(source, AudioCaptureActivity.class);
        }
        else if(req.getContentType() == Requirement.contentType.text) {
            i = new Intent(source, TextCaptureActivity.class);
        }

        i.putExtra("requirement", req);
        return i;
    }
}
