package com.thoughtworks.lariii;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

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
		Button cameraButton = (Button) findViewById(R.id.cameraButton);
		cameraButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				imageHolder.setImageURI(null);
				imageHolder.refreshDrawableState();
				loadCameraIntent();
				startActivityForResult(cameraIntent, 0);
			}
		});
		Button recognizeQRButton = (Button) findViewById(R.id.qrRecognize);
		recognizeQRButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				displayQR();
			}
		});

	}

	private void displayQR() {
		String qrOutput;
		try {
			qrOutput = new BarCodeDetectingService().getQRCodeValue(outputImage
					.getAbsolutePath());
		} catch (IOException e) {
			qrOutput = e.getLocalizedMessage();
		}
		Toast.makeText(getApplicationContext(), qrOutput, Toast.LENGTH_LONG)
				.show();
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
		imageHolder.refreshDrawableState();
	}

}
