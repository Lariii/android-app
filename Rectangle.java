import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.cpp.opencv_core.CvContour;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import com.googlecode.javacv.cpp.opencv_core.CvSize;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

import static com.googlecode.javacpp.Loader.*;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;


public class Rectangle {

static int thresh = 50;
static int N = 11;
final static String wndname = "Square Detection Demo";

// helper function:
// finds a cosine of angle between vectors
// from pt0->pt1 and from pt0->pt2
public static double angle( CvPoint pt1, CvPoint pt2, CvPoint pt0 )
{
    double dx1 = pt1.x() - pt0.x();
    double dy1 = pt1.y() - pt0.y();
    double dx2 = pt2.x() - pt0.x();
    double dy2 = pt2.y() - pt0.y();
    return (dx1*dx2 + dy1*dy2)/Math.sqrt((dx1*dx1 + dy1*dy1)*(dx2*dx2 + dy2*dy2) + 1e-10);
}

// returns sequence of squares detected on the image.
public static CvSeq findSquares( final IplImage src,  CvMemStorage storage)
{   
	
	CvSeq squares = new CvContour();
	squares = cvCreateSeq(0, sizeof(CvContour.class), sizeof(CvSeq.class), storage);
    
    IplImage pyr = null, timg = null, gray = null, tgray;
    timg = cvCloneImage(src);
    
    CvSize sz = cvSize(src.width() & -2, src.height() & -2);
    tgray = cvCreateImage(sz, src.depth(), 1);
    gray = cvCreateImage(sz, src.depth(), 1);
    pyr = cvCreateImage(cvSize(sz.width()/2, sz.height()/2), src.depth(), src.nChannels());
    
    // down-scale and upscale the image to filter out the noise
    cvPyrDown(timg, pyr, CV_GAUSSIAN_5x5);
    cvPyrUp(pyr, timg, CV_GAUSSIAN_5x5);
    cvSaveImage("ha.jpg",	timg);
    CvSeq contours = new CvContour();
    // request closing of the application when the image window is closed
    // show image on window
    // find squares in every color plane of the image
    for( int c = 0; c < 3; c++ )
    {
        IplImage channels[] = {cvCreateImage(sz, 8, 1), cvCreateImage(sz, 8, 1), cvCreateImage(sz, 8, 1)};
        channels[c] = cvCreateImage(sz, 8, 1);
        if(src.nChannels() > 1){
        	cvSplit(timg, channels[0], channels[1], channels[2], null);
        }else{
        	tgray = cvCloneImage(timg);
        }
        tgray = channels[c];
//        // try several threshold levels
        for( int l = 0; l < N; l++ )
        {
//             hack: use Canny instead of zero threshold level.
//             Canny helps to catch squares with gradient shading
            if( l == 0 )
            {
//                apply Canny. Take the upper threshold from slider
//                and set the lower to 0 (which forces edges merging)
                cvCanny(tgray, gray, 0, thresh, 5);
//                 dilate canny output to remove potential
//                // holes between edge segments
                cvDilate(gray, gray, null, 1);
            }
            else
            {
//                apply threshold if l!=0:
                cvThreshold(tgray, gray, (l+1)*255/N, 255, CV_THRESH_BINARY);
            }
//            find contours and store them all as a list
            cvFindContours(gray, storage, contours, sizeof(CvContour.class), CV_RETR_LIST, CV_CHAIN_APPROX_SIMPLE);
            
            CvSeq approx;

//            test each contour
            while (contours != null && !contours.isNull()) {
                    if (contours.elem_size() > 0) {
                        approx = cvApproxPoly(contours, Loader.sizeof(CvContour.class),
                                storage, CV_POLY_APPROX_DP, cvContourPerimeter(contours)*0.02, 0);
                        if( approx.total() == 4
                        		&&
                        		Math.abs(cvContourArea(approx, CV_WHOLE_SEQ, 0)) > 1000 &&
                            cvCheckContourConvexity(approx) != 0 
                            ){
                        	double maxCosine = 0;
                        	//
                        	for( int j = 2; j < 5; j++ )
                        	{
//                        	                        find the maximum cosine of the angle between joint edges
                        	                        double cosine = Math.abs(angle(new CvPoint(cvGetSeqElem(approx, j%4)), new CvPoint(cvGetSeqElem(approx, j-2)), new CvPoint(cvGetSeqElem(approx, j-1))));
                        	                        maxCosine = Math.max(maxCosine, cosine);
                        	 }
                        	 if( maxCosine < 0.2 ){
                        		 cvSeqPush(squares, approx);
                        	 }
                        }
                    }
                    contours = contours.h_next();
                }
            contours = new CvContour();
        }
    }
	return squares;
}


public static void getRectangleclip(int a, int b, int c, int d, int i) throws IOException {
	BufferedImage templateImage1 = ImageIO.read(new File("images/canvas.jpeg"));
//	BufferedImage splitImages = templateImage1.getSubimage(220,690,60,60);
	BufferedImage splitImages = templateImage1.getSubimage(a,b,c,d);
    ImageIO.write(splitImages, "png", new File("images/p"+i+".png"));
}


// the function draws all the squares in the image
public static void drawSquares( IplImage image, final CvSeq squares ) throws IOException
{
	if(!squares.isNull()){
		CvSeq p = new CvSeq(squares.total());
   	 cvCvtSeqToArray(squares, p, CV_WHOLE_SEQ);
	    for(int i = 0; i < squares.total(); i++ )
	    {
	    	 CvPoint pts = new CvPoint(4);
	    	 cvCvtSeqToArray(p.position(i), pts, CV_WHOLE_SEQ);
	        int npt[] = {4, 4};

	        cvDrawLine(image, new CvPoint(pts.position(0).x(),pts.position(0).y()), new CvPoint(pts.position(1).x(),pts.position(1).y()), CvScalar.GREEN, 3, CV_AA, 0);

	        cvDrawLine(image, new CvPoint(pts.position(1).x(),pts.position(1).y()), new CvPoint(pts.position(2).x(),pts.position(2).y()), CvScalar.GREEN, 3, CV_AA, 0);
	        cvDrawLine(image, new CvPoint(pts.position(2).x(),pts.position(2).y()), new CvPoint(pts.position(3).x(),pts.position(3).y()), CvScalar.GREEN, 3, CV_AA, 0);
	        cvDrawLine(image, new CvPoint(pts.position(3).x(),pts.position(3).y()), new CvPoint(pts.position(0).x(),pts.position(0).y()), CvScalar.GREEN, 3, CV_AA, 0);
	        int width = Math.abs(pts.position(1).x() - pts.position(2).x());
	        int height = pts.position(2).y() - pts.position(0).y();
System.out.println("x0 "+pts.toString());
System.out.println("y0 "+ pts.position(3).y());
	        getRectangleclip(pts.position(0).x(),pts.position(0).y(), 380, 90, i);
	    }
	}
    final CanvasFrame canvas = new CanvasFrame(wndname);
    
//     request closing of the application when the image window is closed
    canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
            
    // show image on window
    canvas.showImage(image);
}

public static void main(String[] args) throws IOException {
	String fileName[] = {
			"images/canvas.jpeg"
			};
	for(int i = 0 ; i < fileName.length ; i++){
		IplImage src = cvLoadImage(fileName[i], CV_LOAD_IMAGE_UNCHANGED);
		final CanvasFrame canvas = new CanvasFrame("tests");
	    
	    // request closing of the application when the image window is closed
	    canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
	    // show image on window
	    canvas.showImage(src);
	    drawSquares(src, findSquares(src, cvCreateMemStorage(0)));
	}
	
	
}

}