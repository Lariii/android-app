package com.thoughtworks.lariii.service;

import static com.googlecode.javacpp.Loader.sizeof;
import static com.googlecode.javacv.cpp.opencv_core.CV_WHOLE_SEQ;
import static com.googlecode.javacv.cpp.opencv_core.cvCloneImage;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvCvtSeqToArray;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSeqElem;
import static com.googlecode.javacv.cpp.opencv_core.cvSize;
import static com.googlecode.javacv.cpp.opencv_core.cvSplit;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_CHAIN_APPROX_SIMPLE;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_GAUSSIAN_5x5;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_POLY_APPROX_DP;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_RETR_EXTERNAL;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_THRESH_BINARY;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvApproxPoly;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCanny;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCheckContourConvexity;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvContourArea;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvContourPerimeter;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvDilate;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvFindContours;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvPyrDown;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvPyrUp;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvThreshold;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.cpp.opencv_core.CvContour;
import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import com.googlecode.javacv.cpp.opencv_core.CvSize;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.thoughtworks.lariii.model.Point;
import com.thoughtworks.lariii.model.Square;

public class SquareDetector {
	private int endPointResolutionFactor;
	private final static int THRESHOLD = 50;
	private final static int N = 11;

	public SquareDetector(int endPointResolutionFactor) {
		this.endPointResolutionFactor = endPointResolutionFactor;
	}

	public List<Square> findSquares(final IplImage src, CvMemStorage storage) {
		List<Square> mySquares = new ArrayList<Square>();
		List<Point> points = new ArrayList<Point>();

		IplImage pyr = null, timg = null, gray = null, tgray;
		timg = cvCloneImage(src);

		CvSize sz = cvSize(src.width(), src.height());
		tgray = cvCreateImage(sz, src.depth(), 1);
		gray = cvCreateImage(sz, src.depth(), 1);
		pyr = cvCreateImage(cvSize(sz.width() / 2, sz.height() / 2), src.depth(), src.nChannels());

		cvPyrDown(timg, pyr, CV_GAUSSIAN_5x5);
		cvPyrUp(pyr, timg, CV_GAUSSIAN_5x5);
		CvSeq contours = new CvContour();
		for (int c = 0; c < 3; c++) {
			IplImage channels[] = {cvCreateImage(sz, 8, 1), cvCreateImage(sz, 8, 1),
					cvCreateImage(sz, 8, 1)};
			if (src.nChannels() > 1) {
				cvSplit(timg, channels[0], channels[1], channels[2], null);
			} else {
				tgray = cvCloneImage(timg);
			}
			tgray = channels[c];
			// // try several threshold levels
			for (int l = 0; l < N; l++) {
				if (l == 0) {
					cvCanny(tgray, gray, 0, THRESHOLD, 5);
					cvDilate(gray, gray, null, 1);
				} else {
					cvThreshold(tgray, gray, (l + 1) * 255 / N, 255, CV_THRESH_BINARY);
				}
				cvFindContours(gray, storage, contours, sizeof(CvContour.class), CV_RETR_EXTERNAL,
						CV_CHAIN_APPROX_SIMPLE);

				CvSeq approx;
				while (contours != null && !contours.isNull()) {
					if (contours.elem_size() > 0) {
						approx = cvApproxPoly(contours, Loader.sizeof(CvContour.class), storage,
								CV_POLY_APPROX_DP, cvContourPerimeter(contours) * 0.02, 0);
						if (approx.total() == 4 && Math.abs(cvContourArea(approx, CV_WHOLE_SEQ, 0)) > 1000
								&& cvCheckContourConvexity(approx) != 0) {
							double maxCosine = 0;
							for (int j = 2; j < 5; j++) {
								double cosine = Math.abs(angle(new CvPoint(cvGetSeqElem(approx, j % 4)),
										new CvPoint(cvGetSeqElem(approx, j - 2)),
										new CvPoint(cvGetSeqElem(approx, j - 1))));
								maxCosine = Math.max(maxCosine, cosine);
							}
							if (maxCosine < 0.2) {
								points.clear();
								CvPoint pts = new CvPoint(4);
								cvCvtSeqToArray(approx.position(0), pts, CV_WHOLE_SEQ);

								for (int k = 0; k < 4; k++) {
									points.add(new Point(pts.position(k).x(), pts.position(k).y()));
								}
								Square temp = new Square(reorderPoints(points), approx);
								mySquares.add(temp);
							}
						}
					}
					contours = contours.h_next();
				}
				contours = new CvContour();
			}
		}
		return removeDuplicates(mySquares);
	}

	private void printPoints(List<Point> points) {

		for (Point p : points) {
			System.out.println("Point : " + p);
		}

	}

	private List<Point> reorderPoints(List<Point> points) {
		int minx = Integer.MAX_VALUE, miny = Integer.MAX_VALUE, maxx = 0, maxy = 0;
		for (Point point : points) {
			if (point.x() > maxx)
				maxx = point.x();
			if (minx > point.x())
				minx = point.x();
			if (point.y() > maxy)
				maxy = point.y();
			if (miny > point.y())
				miny = point.y();
		}
		List<Point> ordered = new ArrayList<Point>(points);
		for (Point point : points) {
			if (Math.abs(point.x() - minx) < endPointResolutionFactor
					&& Math.abs(point.y() - miny) < endPointResolutionFactor) {
				ordered.set(0, point);
			}
			if (Math.abs(point.x() - maxx) < endPointResolutionFactor
					&& Math.abs(point.y() - miny) < endPointResolutionFactor) {
				ordered.set(1, point);
			}
			if (Math.abs(point.x() - maxx) < endPointResolutionFactor
					&& Math.abs(point.y() - maxy) < endPointResolutionFactor) {
				ordered.set(2, point);
			}
			if (Math.abs(point.x() - minx) < endPointResolutionFactor
					&& Math.abs(point.y() - maxy) < endPointResolutionFactor) {
				ordered.set(3, point);
			}

		}

		return ordered;
	}

	private List<Square> removeDuplicates(List<Square> mySquares) {
		List<Square> tempSquares = new ArrayList<Square>(mySquares);
		int main_x, main_y, temp_x, temp_y;
		for (int i = 0; i < mySquares.size(); i++) {
			main_x = mySquares.get(i).getPoints().get(0).x();
			main_y = mySquares.get(i).getPoints().get(0).y();
			for (int j = i + 1; j < mySquares.size(); j++) {
				temp_x = mySquares.get(j).getPoints().get(0).x();
				temp_y = mySquares.get(j).getPoints().get(0).y();
				if (Math.abs(main_x - temp_x) < endPointResolutionFactor
						&& Math.abs(main_y - temp_y) < endPointResolutionFactor) {
					tempSquares.remove(mySquares.get(j));
				}
			}
		}
		return tempSquares;
	}

	private double angle(CvPoint pt1, CvPoint pt2, CvPoint pt0) {
		double dx1 = pt1.x() - pt0.x();
		double dy1 = pt1.y() - pt0.y();
		double dx2 = pt2.x() - pt0.x();
		double dy2 = pt2.y() - pt0.y();
		return (dx1 * dx2 + dy1 * dy2)
				/ Math.sqrt((dx1 * dx1 + dy1 * dy1) * (dx2 * dx2 + dy2 * dy2) + 1e-10);
	}

}