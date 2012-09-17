import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
 
public class Circle{
 public static void main(String[] args){
  IplImage src = cvLoadImage("images/black.tif");
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
      cvCircle(src, center, radius, CvScalar.GREEN, 6, CV_AA, 0);   
     }
   
  cvShowImage("Result",src); 
  cvWaitKey(0);
   
 }
}