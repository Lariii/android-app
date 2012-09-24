package com.thoughtworks.lariii.model;

import java.awt.image.BufferedImage;

public class BasicImageSource implements ImageSource {

	private BufferedImage imageSource;

	public BasicImageSource(BufferedImage bufferedImage) {
		this.imageSource = bufferedImage;
	}

	@Override
	public int getPixel(int x, int y) {
		return imageSource.getRGB(x, y);
	}

}
