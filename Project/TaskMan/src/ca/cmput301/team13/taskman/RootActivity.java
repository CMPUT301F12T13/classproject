package ca.cmput301.team13.taskman;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class RootActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_root, menu);
        return true;
    }
}
