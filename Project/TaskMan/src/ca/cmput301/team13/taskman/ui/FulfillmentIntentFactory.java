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

package ca.cmput301.team13.taskman.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import ca.cmput301.team13.taskman.model.storage.Fulfillment;
import ca.cmput301.team13.taskman.model.storage.Requirement;

/**
 * FulfillmentIntentFactory creates intents for launching fulfillment
 * activities (subclasses of FulfillmentActivity).
 */
public class FulfillmentIntentFactory {

    private Context context;

    /**
     * Creates an instance of FulfillmentIntentFactory.
     * @param src The intent that owns the factory; is
     * used to create the intents.
     */
    FulfillmentIntentFactory(Context src) {
        context = src;
    }

    /**
     * Creates an intent for launching an appropriate
     * fulfillment activity for a given requirement.
     * @param req The requirement to use to select an activity
     * @return An intent to launch the fulfillment activity
     */
    public Intent createIntent(Requirement req) {
        Intent intent = null;

        switch(req.getContentType()) {
        case image:
            intent = new Intent(context, ImageCaptureActivity.class); break;
        case audio:
            intent = new Intent(context, AudioCaptureActivity.class); break;
        case text:
            intent = new Intent(context, TextCaptureActivity.class); break;
        case video:
            intent = new Intent(context, VideoCaptureActivity.class);
        }

        return intent.putExtra("requirement", req);
    }

}
