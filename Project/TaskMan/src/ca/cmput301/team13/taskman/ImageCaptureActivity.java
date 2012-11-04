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
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class ImageCaptureActivity extends FulfillmentActivity implements OnClickListener {
    
    Uri imageFileUri;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int GALLERY_IMAGE_ACTIVITY_REQUEST_CODE = 200;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_fulfillment);
        
        //setup our listeners
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
            takeAPhoto();
        }
        else if (source.equals(findViewById(R.id.gallery_button))){
            selectAPhoto();
        }
    }
    
    public void selectAPhoto(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_IMAGE_ACTIVITY_REQUEST_CODE);
    }
    
    public void takeAPhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        String folder = Environment.getExternalStorageDirectory().getAbsolutePath() + "/tmp";
        File folderF = new File(folder);   	
        if (!folderF.exists()) {
            folderF.mkdir();
        }
        
        String imageFilePath = folder + "/" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        File imageFile = new File(imageFilePath);
        imageFileUri = Uri.fromFile(imageFile);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ImageView preview = (ImageView)findViewById(R.id.image_view);
        
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Drawable img = Drawable.createFromPath(imageFileUri.getPath());
                //preview.setImageDrawable(img);
                
                // Convert the image to a bitmap
                // TODO: check if img is always a BitmapDrawable
                Bitmap b = Bitmap.createBitmap(
                        img.getIntrinsicWidth(),
                        img.getIntrinsicHeight(),
                        Config.ARGB_8888);
                Canvas c = new Canvas(b);
                img.setBounds(0, 0, img.getIntrinsicWidth(), img.getIntrinsicHeight());
                img.draw(c);
                
                preview.setImageBitmap(b);
                
                fulfillment.setImage(b);
                successful = true;
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(ImageCaptureActivity.this,
                        "Photo Cancelled", Toast.LENGTH_SHORT)
                        .show();
            } else {
                Toast.makeText(ImageCaptureActivity.this,
                        "Some sort of error" + resultCode, Toast.LENGTH_SHORT)
                        .show();
            }
        } else if (requestCode == GALLERY_IMAGE_ACTIVITY_REQUEST_CODE){
            if (resultCode == RESULT_OK) {
                imageFileUri = data.getData();
        
                InputStream imageStream = null;
                try {
                    imageStream = getContentResolver().openInputStream(imageFileUri);
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                Bitmap bm = BitmapFactory.decodeStream(imageStream);
                preview.setImageBitmap(bm);
                
                fulfillment.setImage(bm);
                successful = true;
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(ImageCaptureActivity.this,
                        "Photo Selection Cancelled", Toast.LENGTH_SHORT)
                        .show();
            } else {
                Toast.makeText(ImageCaptureActivity.this,
                        "Error choosing Photo" + resultCode, Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
}
