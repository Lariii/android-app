package com.thoughtworks.lariii;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class ImageCapture extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_capture);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_image_capture, menu);
        return true;
    }
}
