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

package ca.cmput301.team13.taskman.ui;

import java.io.File;
import java.io.IOException;

import ca.cmput301.team13.taskman.R;
import ca.cmput301.team13.taskman.R.id;
import ca.cmput301.team13.taskman.R.layout;
import ca.cmput301.team13.taskman.R.menu;


import utils.AudioVideoConversion;
import utils.Notifications;
import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * AudioCaptureActivity is the activity that allows the user
 * to record a sound sample to fulfill an audio requirement.
 * This activity should be launched with an intent created
 * by {@link FulfillmentIntentFactory}.
 */
public class AudioCaptureActivity extends FulfillmentActivity implements OnClickListener {

    private Uri audioFileUri;
    private static final int COLLECTION_ACTIVITY_REQUEST_CODE = 300;
    private MediaRecorder recorder;
    private boolean recording;
    private String fileName;

    /**
     * Handles initialization of the activity.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_capture);
        
        //setup our listeners
        ((Button)findViewById(R.id.record_button)).setOnClickListener(this);
        ((Button)findViewById(R.id.collection_button)).setOnClickListener(this);
        ((Button)findViewById(R.id.save_button)).setOnClickListener(this);
        ((Button)findViewById(R.id.cancel_button)).setOnClickListener(this);        
    }

    /**
     * Handles resume event.
     */
    @Override
    public void onResume() {
        super.onResume();
        //make sure the recorder is null and booleans are false
        recorder = null;
        recording = false;
        ((TextView) findViewById(R.id.audio_view)).setText("Choose audio from your collection or record one now.");
    }

    /**
     * Handles pause event.
     */
    @Override
    public void onPause() {
        super.onPause();
        //release the recorder if needed
        if(recorder != null){
            recorder.release();
            recorder = null;
            ((Button)findViewById(R.id.record_button)).setText("Start Recording");
        }
    }
    /**
     * Constructs menu options.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_audio_capture, menu);
        return true;
    }

    /**
     * Delegates action based on which listener has been clicked.
     * @param source 
     */
    public void onClick(View source) {
        if(source.equals(findViewById(R.id.record_button))) {
            if(recording == false){
                recording = true;
                startRecording();
                ((Button)findViewById(R.id.record_button)).setText("Stop Recording");
            } else {
                recording = false;
                recorder.stop();
                recorder.release();
                
                ((TextView) findViewById(R.id.audio_view)).setText("Recording stopped.");
                ((Button)findViewById(R.id.record_button)).setText("Record Again");
            }
        }
        else if(source.equals(findViewById(R.id.collection_button))) {
            audioFromCollection();
        }
        else if (source.equals(findViewById(R.id.save_button))) {
            if(audioFileUri != null || fileName != null ) {
                save();
            } else {
                Notifications.showToast(getApplicationContext(), "No Audio selected");
            }   
        }
        else if (source.equals(findViewById(R.id.cancel_button))) {
            cancel();
        }
    }
    
    /**
     * Send the taken/selected audio to our parent and exit the Activity.
     */
    public void save() {
    	short[] audioShorts = AudioVideoConversion.audioShorts(audioFileUri, fileName, this.getBaseContext());
        //Return to the Task Viewer
        if(audioShorts != null) {
            super.save();
            fulfillment.setAudio(audioShorts);
            finish();
        }
    }

    /**
     * Takes the user to the built-in audio selector where an
     * existing audio file can be selected for use.
     */
    private void audioFromCollection() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        startActivityForResult(Intent.createChooser(intent,"Select Audio "), COLLECTION_ACTIVITY_REQUEST_CODE);
    }

    /**
     * Sets the file path for the new audio file,
     * initializes and starts the MediaRecorder.
     * @see android.media.MediaRecorder
     */
    private void startRecording() {
        //set a file path for the new audio
        String folder = Environment.getExternalStorageDirectory().getAbsolutePath() + "/tmp";
        File folderF = new File(folder);
        if (!folderF.exists()) {
            folderF.mkdir();
        }
        fileName = folder + "/" + String.valueOf(System.currentTimeMillis()) + ".3gp";

        //instantiate the MediaRecorder
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //start the recorder
        recorder.start();
        ((TextView) findViewById(R.id.audio_view)).setText("Recording...");
    }
    
    /**
     * Handles the result condition and/or returned data from the Android built-in 
     * Audio Content selection. 
     * 
     * @param requestCode specifies which type of activity we are returning from
     * @param resultCode signifies the success or fail of the intent
     * @param data The data returned from the intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == COLLECTION_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Notifications.showToast(getApplicationContext(), "Audio Selection Successful");
                audioFileUri = data.getData();             
            } else if (resultCode == RESULT_CANCELED) {
                //Audio selection was cancelled
                Notifications.showToast(getApplicationContext(), "Audio Selection Cancelled");
            } else {
                //Audio selection had an error
                Notifications.showToast(getApplicationContext(), "Audio Selection Error" + resultCode);
            }
        }
    }
}
