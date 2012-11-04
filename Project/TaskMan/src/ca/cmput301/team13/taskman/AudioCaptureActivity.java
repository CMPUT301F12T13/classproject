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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

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
import utils.Notifications;

public class AudioCaptureActivity extends FulfillmentActivity implements OnClickListener {

    private static final int COLLECTION_ACTIVITY_REQUEST_CODE = 300;
    private MediaRecorder recorder;
    private boolean recording;
    private String fileName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audio_fulfillment);
        
        //setup our listeners
        ((Button)findViewById(R.id.record_button)).setOnClickListener(this);
        ((Button)findViewById(R.id.collection_button)).setOnClickListener(this);
        ((Button)findViewById(R.id.save_button)).setOnClickListener(this);
        ((Button)findViewById(R.id.cancel_button)).setOnClickListener(this);        
    }

    @Override
    public void onResume() {
        super.onResume();
        recorder = null;
        recording = false;
        ((TextView) findViewById(R.id.audio_view)).setText("Choose audio from your collection or record one now.");
    }

    @Override
    public void onPause() {
        super.onPause();
        if(recorder != null){
            recorder.release();
            recorder = null;
            ((Button)findViewById(R.id.record_button)).setText("Start Recording");
        }
    }
    
    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.audio_fulfillment, menu);
        return true;
    }

    /**
     * Delegates action based on which listener has been clicked
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
                
                //TODO: need to figure out what to do with audio file here
                
                ((TextView) findViewById(R.id.audio_view)).setText("Recording stopped.");
                ((Button)findViewById(R.id.record_button)).setText("Record Again");
            }
        }
        else if(source.equals(findViewById(R.id.collection_button))) {
            audioFromCollection();
        }
    }
    
    /**
     * Takes the user to the built-in audio selector where an existing audio file can
     * be selected for use
     */
    private void audioFromCollection() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        startActivityForResult(Intent.createChooser(intent,"Select Audio "), COLLECTION_ACTIVITY_REQUEST_CODE);
    }

    private void startRecording() {
        String folder = Environment.getExternalStorageDirectory().getAbsolutePath() + "/tmp";
        File folderF = new File(folder);
        if (!folderF.exists()) {
            folderF.mkdir();
        }
        fileName = folder + "/" + String.valueOf(System.currentTimeMillis()) + ".3gp";

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

        recorder.start();
        ((TextView) findViewById(R.id.audio_view)).setText("Recording...");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == COLLECTION_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Notifications.showToast(getApplicationContext(), "Audio Selection Successful");

                Uri audioFileUri = data.getData();
                InputStream audioStream = null;
                try {
                    audioStream = getContentResolver().openInputStream(audioFileUri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                //TODO: need to figure out what to do with audio file here
                
            } else if (resultCode == RESULT_CANCELED) {
                Notifications.showToast(getApplicationContext(), "Audio Selection Cancelled");
            } else {
                Notifications.showToast(getApplicationContext(), "Audio Selection Error" + resultCode);
            }
        }
    }
}