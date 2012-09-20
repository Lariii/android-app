import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class MyCanvas extends Frame {
  
  //public void paint(Graphics g) {
	 Shape circle; 
	  Shape square;

 // }
public static void circleDraw(float x, float y, float radius1, float radius2, Graphics2D ga){
	Shape circle; 
	circle = new Ellipse2D.Float(x, y, radius1, radius2);
	ga.draw(circle);
}
  public static void main(String args[]) throws IOException {
//	  
//  Frame frame = new MyCanvas();
//  frame.addWindowListener(new WindowAdapter(){
//  public void windowClosing(WindowEvent we){
//  System.exit(0);
//  }
//  });
//  frame.setSize(2480, 3508);
//  frame.setVisible(true);
	  Shape square;
	  BufferedImage bi = new BufferedImage(2480, 3508, BufferedImage.TYPE_INT_ARGB);
      Graphics2D ga = bi.createGraphics();

	  for(int j=80; j<610; j+=100){
	  for(int i=70; i<360; i+=30){
		  circleDraw(i, j, 18.0f, 18.0f, ga);
		  circleDraw(i, j+40, 18.0f, 18.0f, ga);
	  }
	  for(int i=460; i<750; i+=30){
		  circleDraw(i, j, 18.0f, 18.0f, ga);
		  circleDraw(i, j+40, 18.0f, 18.0f, ga);
	  }
	  }

	  for(int i=60;i<600;i+=100){
		  square = new Rectangle2D.Double(10, i, 380, 90);
		  ga.draw(square);
		  square = new Rectangle2D.Double(420, i, 380, 90);
		  ga.draw(square);
	  }
      ImageIO.write(bi, "JPEG", new File("images/canvas.jpeg"));

  }
}