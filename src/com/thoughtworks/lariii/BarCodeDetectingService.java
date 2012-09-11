package com.thoughtworks.lariii;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.client.androidtest.RGBLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

public class BarCodeDetectingService {
	public String decode(File uri, Map<DecodeHintType, ?> hints)
			throws IOException {
		Bitmap image = BitmapFactory.decodeFile(uri.getAbsolutePath());
		RGBLuminanceSource source = new RGBLuminanceSource(image);

		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
		try {
			Result result = new MultiFormatReader().decode(bitmap, hints);
			return result.getText();
		} catch (com.google.zxing.NotFoundException e) {
			e.printStackTrace();
			return "ERROR";
		}
	}

	public String getQRCodeValue(String filename) throws IOException {
		BarCodeDetectingService barCodeDetectingService = new BarCodeDetectingService();
		Map<DecodeHintType, Object> hints = new EnumMap<DecodeHintType, Object>(
				DecodeHintType.class);
		Collection<BarcodeFormat> vector = new ArrayList<BarcodeFormat>(8);
		vector.add(BarcodeFormat.QR_CODE);
		hints.put(DecodeHintType.POSSIBLE_FORMATS, vector);
		return barCodeDetectingService.decode(new File(filename), hints);
	}
}