package com.thoughtworks.lariii;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.widget.ImageView;

public class ImageCapture extends Activity {

	private static final String OUTPUT_FILE_NAME = "outputImage.png";

	ImageView imageHolder;

	Intent cameraIntent;

	File outputImage;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_capture);
		imageHolder = (ImageView) findViewById(R.id.imageHolder);
		outputImage = new File(Environment.getExternalStorageDirectory(),
				OUTPUT_FILE_NAME);
		loadCameraIntent();
		startActivityForResult(cameraIntent, 0);
	}

	private void loadCameraIntent() {
		cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
				Uri.fromFile(outputImage));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_image_capture, menu);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		imageHolder.setImageURI(Uri.fromFile(outputImage));
	}

}
