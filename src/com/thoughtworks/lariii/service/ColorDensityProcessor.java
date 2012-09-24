package com.thoughtworks.lariii.service;

import com.thoughtworks.lariii.model.ImageSource;
import com.thoughtworks.lariii.model.Point;

public class ColorDensityProcessor {

	public double getColorDensity(ImageSource imgSource, Point topLeft, Point bottomRight,
			int desiredRGB) {
		int count = 0;
		int width = bottomRight.x() - topLeft.x();
		int height = bottomRight.y() - topLeft.y();
		int numberOfPixels = width * height;
		int baseX = topLeft.x();
		int baseY = topLeft.y();
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				System.out.println(desiredRGB + "  got : " + imgSource.getPixel(i + baseX, j + baseY));
				if (desiredRGB == imgSource.getPixel(i + baseX, j + baseY)) {
					count++;
				}
			}
		}
		return ((double) count) / numberOfPixels;
	}
}
