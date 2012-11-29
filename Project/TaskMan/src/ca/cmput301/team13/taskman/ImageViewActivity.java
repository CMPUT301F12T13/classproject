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

package ca.cmput301.team13.taskman;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

/**
 * ImageViewActivity is the activity that allows the user
 * view a photo fulfillment.
 * This activity should be launched with an intent created
 * by {@link FulfillmentViewIntentFactory}.
 */
public class ImageViewActivity extends FulfillmentViewActivity implements OnClickListener {

	private ImageView imageview;

    /**
     * Initializes the activity.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        imageview = ((ImageView)findViewById(R.id.ful_img_view));
        imageview.setOnClickListener(this);
        
        imageview.setImageBitmap(fulfillment.getImage());
    }
    
    /**
     * Constructs menu options.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    /**
     * Closes the activity
     * @param source The source view.
     */
    public void onClick(View source) {
        this.finish();
    }
}
