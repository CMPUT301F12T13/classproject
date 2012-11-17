package ca.cmput301.team13.taskman;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class VideoCaptureActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_capture);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_video_capture, menu);
        return true;
    }

}
