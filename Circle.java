import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import java.awt.Color;

import com.googlecode.javacpp.Pointer;
import com.googlecode.javacv.cpp.opencv_core.CvPoint;
public class Circle{
 public static void main(String[] args){
	 for(int k=1; k<13; k++){
  IplImage src = cvLoadImage("images/p"+k+".png");
  IplImage gray = cvCreateImage(cvGetSize(src), 8, 1);
  
  IplImage edge = cvCreateImage(cvGetSize(src), 8, 1);
  cvCvtColor(src, gray, CV_BGR2GRAY); 
//  cvThreshold(gray, gray, 100, 255, CV_THRESH_BINARY);
  cvSmooth(gray, gray, CV_GAUSSIAN, 11);
  CvMemStorage mem = CvMemStorage.create();
   
  CvSeq circles = cvHoughCircles(
    gray, //Input image
    mem, //Memory Storage
    CV_HOUGH_GRADIENT, //Detection method
    1, 7, 80, 20, 7, 35//max radius
    );
 
  for(int i = 0; i < circles.total(); i++){
      CvPoint3D32f circle = new CvPoint3D32f(cvGetSeqElem(circles, i));
      CvPoint center = cvPointFrom32f(new CvPoint2D32f(circle.x(), circle.y()));
      int radius = Math.round(circle.z());     
//      System.out.println("**********************");
//      System.out.println("Point"+center+""+"Radius"+radius);
//      System.out.println("**********************");
//      int x = center.x() - radius;
//      int y = center.y() + radius;
//      for(int j=x;j<=(x+2*radius);j++)
//      {
//    	  for(int k=y;k>=(y-2*radius);k--){
//    		  center
//    	  }
//      }
      Pointer ptr = cvPtr2D(gray, center.y(), center.x(), null);
System.out.println(ptr);
      CvScalar c = cvGet2D(gray,center.y(), center.x() ); //color of the center
      System.out.println("Point"+center+""+"Radius"+radius);
      System.out.println(c);
      cvCircle(src, center, radius, CvScalar.GREEN, 6, CV_AA, 0);   
     }
  
//  cvInRangeS(orgImg, min, max, imgThreshold);

   System.out.println(circles.total()+"Total");
  cvShowImage("Result",src); 
  cvWaitKey(0);
	 }
 }
}