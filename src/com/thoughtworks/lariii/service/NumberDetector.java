package com.thoughtworks.lariii.service;

import java.util.HashMap;
import java.util.Map;

import com.thoughtworks.lariii.model.ImageSource;
import com.thoughtworks.lariii.model.Point;

public class NumberDetector {
	private static final int BLACK_RGBA = 0;
	private int basicSegmentWidth;
	private int basicSegmentLength;
	private double threshold;

	private ColorDensityProcessor colorDensityProcessor;

	private Point[][] segmentRanges;
	private Map<Point[], Integer> segmentBitmapMap;
	private Map<Integer, Integer> bitmapNumberMap;

	public NumberDetector(ColorDensityProcessor imageDensityProcessor, int basicLineLength,
			int basicLineWidth, double threshold) {
		this.colorDensityProcessor = imageDensityProcessor;
		this.basicSegmentLength = basicLineLength;
		this.basicSegmentWidth = basicLineWidth;
		this.segmentRanges = new Point[7][];
		this.threshold = threshold;
		this.segmentBitmapMap = new HashMap<Point[], Integer>();
		this.bitmapNumberMap = new HashMap<Integer, Integer>();
		initializeSegmentRanges();
		initializeBitmapNumberMap();
	}

	private void initializeBitmapNumberMap() {
		bitmapNumberMap.put(0x3F, 0);
		bitmapNumberMap.put(0x06, 1);
		bitmapNumberMap.put(0x30, 1);
		bitmapNumberMap.put(0x5B, 2);
		bitmapNumberMap.put(0x4F, 3);
		bitmapNumberMap.put(0x65, 4);
		bitmapNumberMap.put(0x6D, 5);
		bitmapNumberMap.put(0x7C, 6);
		bitmapNumberMap.put(0x07, 7);
		bitmapNumberMap.put(0x7F, 8);
		bitmapNumberMap.put(0x6F, 9);
		bitmapNumberMap.put(0x40, -1);
	}

	private void initializeSegmentRanges() {
		int l = basicSegmentLength;
		int w = basicSegmentWidth;
		segmentRanges[0] = new Point[]{new Point(0, 0), new Point(l, w)};
		segmentBitmapMap.put(segmentRanges[0], 0x00);

		segmentRanges[1] = new Point[]{new Point(l, w), new Point(l + w, l + w)};
		segmentBitmapMap.put(segmentRanges[1], 0x02);

		segmentRanges[2] = new Point[]{new Point(l, l + 2 * w), new Point(l + w, 2 * (l + w))};
		segmentBitmapMap.put(segmentRanges[2], 0x04);

		segmentRanges[3] = new Point[]{new Point(0, 2 * (l + w)), new Point(l, 2 * l + 3 * w)};
		segmentBitmapMap.put(segmentRanges[3], 0x08);

		segmentRanges[4] = new Point[]{new Point(-w, l + 2 * w), new Point(0, 2 * (l + w))};
		segmentBitmapMap.put(segmentRanges[4], 0x10);

		segmentRanges[5] = new Point[]{new Point(-w, w), new Point(0, l + w)};
		segmentBitmapMap.put(segmentRanges[5], 0x20);

		segmentRanges[6] = new Point[]{new Point(0, l + w), new Point(l, l + 2 * w)};
		segmentBitmapMap.put(segmentRanges[6], 0x40);
	}

	public int detectNumber(ImageSource imgSource, Point baseOffset) {
		int bitmap = 0;
		Point topLeft = new Point(0, 0);
		Point bottomRight = new Point(0, 0);
		double colorDensity;
		for (Point[] range : segmentRanges) {
			topLeft.setX(baseOffset.x() + range[0].x());
			topLeft.setY(baseOffset.y() + range[0].y());
			bottomRight.setX(baseOffset.x() + range[1].x());
			bottomRight.setY(baseOffset.y() + range[1].y());

			colorDensity = colorDensityProcessor.getColorDensity(imgSource, topLeft, bottomRight,
					BLACK_RGBA);

			if (colorDensity >= threshold) {
				bitmap |= segmentBitmapMap.get(range);
			}

		}
		if (bitmapNumberMap.containsKey(bitmap))
			return bitmapNumberMap.get(new Integer(bitmap));
		return -1;
	}
}
