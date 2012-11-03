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

import java.io.IOException;

import android.app.Activity;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class AudioCaptureActivity extends Activity implements OnClickListener {

    private MediaRecorder recorder;
	private boolean recording;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //TODO: implement bundle grabbing
        
        setContentView(R.layout.audio_fulfillment);
        ((Button)findViewById(R.id.record_button)).setOnClickListener(this);
        ((Button)findViewById(R.id.collection_button)).setOnClickListener(this);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.audio_fulfillment, menu);
        return true;
    }
    
    
	public void onClick(View source) {
		if(source.equals(findViewById(R.id.record_button))) {
			if(recording == false){
				recording = true;
				startRecording();
				((Button)findViewById(R.id.record_button)).setText("Stop Recording");
			} else {
				recording = false;
				recorder.stop();
				((TextView) findViewById(R.id.audio_view)).setText("Recording stopped.");
				((Button)findViewById(R.id.record_button)).setText("Record Again");
			}
		}
		else if(source.equals(findViewById(R.id.collection_button))) {
			//TODO: implement collection selection
		}
	}
	
	private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        //recorder.setOutputFile(mFileName);
        //TODO: send audio to model
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        recorder.start();
        ((TextView) findViewById(R.id.audio_view)).setText("Recording...");
    }
   
}