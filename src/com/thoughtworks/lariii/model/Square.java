package com.thoughtworks.lariii.model;

import java.util.List;

import com.googlecode.javacv.cpp.opencv_core.CvSeq;

public class Square {
	private List<Point> points;
	private CvSeq imageData;

	public Square(List<Point> points, CvSeq imageData) {
		this.points = points;
		this.imageData = imageData;
	}

	public CvSeq getImageData() {
		return this.imageData;
	}

	public Point getTopLeft() {
		return this.points.get(0);
	}

	public Point getTopRight() {
		return this.points.get(1);
	}

	public Point getBottomRight() {
		return this.points.get(2);
	}

	public Point getBottomLeft() {
		return this.points.get(3);
	}

	public List<Point> getPoints() {
		return this.points;
	}

	@Override
	public String toString() {
		return points.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((imageData == null) ? 0 : imageData.hashCode());
		result = prime * result + ((points == null) ? 0 : points.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Square other = (Square) obj;
		if (imageData == null) {
			if (other.imageData != null)
				return false;
		} else if (!imageData.equals(other.imageData))
			return false;
		if (points == null) {
			if (other.points != null)
				return false;
		} else if (!points.equals(other.points))
			return false;
		return true;
	}

}
