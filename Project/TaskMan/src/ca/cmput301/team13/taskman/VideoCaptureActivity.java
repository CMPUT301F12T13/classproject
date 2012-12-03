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

import java.io.File;


import utils.AudioVideoConversion;
import utils.Notifications;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

/**
 * VideoCaptureActivity is the activity that allows the user
 * take a video or select one from the device's library to 
 * fulfill a video requirement.
 * This activity should be launched with an intent created
 * by {@link FulfillmentIntentFactory}.
 */
public class VideoCaptureActivity extends FulfillmentActivity implements OnClickListener {

    private Uri videoFileUri;
    private static final int VIDEO_CAPTURE_ACTIVITY_REQUEST_CODE = 400;
    private static final int VIDEO_GALLERY_ACTIVITY_REQUEST_CODE = 500;

    /**
     * Handles initialization of the activity.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_capture);
        
        //setup our listeners
        ((Button)findViewById(R.id.take_button)).setOnClickListener(this);
        ((Button)findViewById(R.id.gallery_button)).setOnClickListener(this);
        ((Button)findViewById(R.id.save_button)).setOnClickListener(this);
        ((Button)findViewById(R.id.cancel_button)).setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_video_capture, menu);
        return true;
    }
    

    /**
     * Delegates action based on which listener has been clicked.
     * @param source 
     */
    public void onClick(View source) {
        if(source.equals(findViewById(R.id.take_button))) {
            takeVideo();
        }
        else if (source.equals(findViewById(R.id.gallery_button))) {
            selectVideo();
        }
        else if (source.equals(findViewById(R.id.save_button))) {
            if(videoFileUri != null) {
                save();
            }
            else {
                Notifications.showToast(getApplicationContext(), "No Video Selected");
            }
            
        }
        else if (source.equals(findViewById(R.id.cancel_button))) {
            cancel();
        }
    }
    
    /**
     * Send the taken/selected video to our parent and exit the Activity.
     */
    public void save() {
        super.save();
        short[] videoShorts = AudioVideoConversion.getShort(AudioVideoConversion.resolvePath(getBaseContext(), videoFileUri));
        //Return to the Task Viewer
        if(videoShorts != null) {
            fulfillment.setVideo(videoShorts);
            finish();
        }
    }
    
    /**
     * Sets up the filepath for a new video and launches the
     * built-in camera application to get the new video.
     */
    public void takeVideo() {
        //set a file path for the new video
        String folder = Environment.getExternalStorageDirectory().getAbsolutePath() + "/tmp";
        File folderF = new File(folder);
        if (!folderF.exists()) {
            folderF.mkdir();
        }
        String imageFilePath = folder + "/" + String.valueOf(System.currentTimeMillis()) + ".mp4";
        File imageFile = new File(imageFilePath);
        videoFileUri = Uri.fromFile(imageFile);
        
        //Start the built-in camera application to get our video
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, videoFileUri);
        intent.putExtra("android.intent.extra.durationLimit", 10000);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0); //low quality
        startActivityForResult(intent, VIDEO_CAPTURE_ACTIVITY_REQUEST_CODE);
    }

    /**
     * Takes the user to the gallery where a previously taken photo can
     * be selected for use.
     */
    public void selectVideo(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        startActivityForResult(Intent.createChooser(intent,"Select Video "), VIDEO_GALLERY_ACTIVITY_REQUEST_CODE);
    }
    
    /**
     * Handles the result condition and/or returned data from the Android built-in 
     * camera and also from the Photo Gallery selection. Converts returned photo's
     * into bitmaps and sets the screen to show a preview of the selected image.
     * 
     * @param requestCode specifies which type of activity we are returning from
     * @param resultCode signifies the success or fail of the intent
     * @param data The data returned from the intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ImageView preview = (ImageView)findViewById(R.id.image_view);

        if (requestCode == VIDEO_CAPTURE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //Video Taking was a success
                Notifications.showToast(getApplicationContext(), "Video Taken");
                
                //create a thumbnail
                Bitmap bm = ThumbnailUtils.createVideoThumbnail(videoFileUri.getPath(), MediaStore.Video.Thumbnails.MICRO_KIND);

                //set the preview to show the image
                preview.setImageBitmap(bm);
                                
            } else if (resultCode == RESULT_CANCELED) {
                //Video Taking was Cancelled
                Notifications.showToast(getApplicationContext(), "Video Cancelled");
            } else {
                //Video Taking had an error
                Notifications.showToast(getApplicationContext(), "Error taking Video" + resultCode);
            }
        } else if (requestCode == VIDEO_GALLERY_ACTIVITY_REQUEST_CODE){
            if (resultCode == RESULT_OK) {
                //Video was successfully chosen from Gallery
                Notifications.showToast(getApplicationContext(), "Video Selected");
                
                //get the returned image from the Intent
                videoFileUri = data.getData();
                
                //create a thumbnail
                Bitmap bm = ThumbnailUtils.createVideoThumbnail(AudioVideoConversion.resolvePath(getBaseContext(), videoFileUri), 
                        MediaStore.Video.Thumbnails.MICRO_KIND);

                //set the preview to show the image
                preview.setImageBitmap(bm);
                                
            } else if (resultCode == RESULT_CANCELED) {
                //Photo selection was cancelled
                Notifications.showToast(getApplicationContext(), "Video Selection Cancelled");
            } else {
                //Photo selection had an error
                Notifications.showToast(getApplicationContext(), "Error choosing Video" + resultCode);
            }
        }
    }

}
