package ca.cmput301.team13.taskman;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ImageCaptureActivity extends Activity implements OnClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_fulfillment);
        
        ((Button)findViewById(R.id.take_button)).setOnClickListener(this);
        ((Button)findViewById(R.id.gallery_button)).setOnClickListener(this);
    }

    @Override
    public void onResume() {
    	super.onResume();
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.image_fulfillment, menu);
        return true;
    }
    
    
    public void onClick(View source) {
		if(source.equals(findViewById(R.id.take_button))) {
			//TODO: implement photo taking
		}
		else if (source.equals(findViewById(R.id.gallery_button))){
			//TODO: implement gallery selection
		}
	}
}
